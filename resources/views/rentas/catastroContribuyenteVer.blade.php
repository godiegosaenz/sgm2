@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css" rel="stylesheet" />
<style>
.checkbox-grande {
    transform: scale(1.5); /* Ajusta el número para el tamaño deseado */
    margin-right: 8px; /* Ajuste opcional */
}
.desabilita_txt{
    display: block;
}
.readonly-checkbox {
    pointer-events: none; /* Evita la interacción */
    opacity: 0.5; /* Visualmente deshabilitado */
}

.select2-container .select2-selection--single {
    height: 36px !important;
}
</style>
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <a href="../list" class="btn btn-sm btn-primary" style="margin-top:10px">Regresar</a>
                      <button type="button" class="btn btn-sm btn-secondary" style="margin-top:10px" onclick="reportePdf()" >Descargar</button>
                    <button type="button" class="btn btn-sm btn-warning" style="margin-top:10px"  onclick="abrirContribuyente()">Actualizar</button>
                    <button type="button" class="btn btn-sm btn-danger" style="margin-top:10px" onclick="abrirLocales()">Locales</button>
                    <button type="button" class="btn btn-sm btn-success" style="margin-top:10px" onclick="abrirActividades()">Actividades</button>
                    <h3 class="text-center">Detalle de contribuyente</h3>
                </div>
            </div>
        </div>
        @if(@session('error'))
                <div class="alert alert-danger">
                    {{session('error')}}
                </div>
        @endif
        @if(@session('success'))
                <div class="alert alert-success">
                    {{session('success')}}
                </div>
        @endif
        <!-- Mensaje general de errores -->
        @if($errors->any())
            <div class="alert alert-danger">
                "Por favor, revise los campos obligatorios y corrija los errores antes de continuar."
            </div>
        @endif

        <div class="row">
            <!-- Propietario -->
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">Propietario</div>
                    <div class="card-body">
                        <input type="hidden" name="id_cont" id="id_cont" value="{{$contribuyente->id}}">
                        <p><strong>Cédula:</strong> {{ $contribuyente->ruc ?? 'N/A' }}</p>
                        <p><strong>Nombres:</strong> {{ $contribuyente->razon_social ?? 'N/A' }}</p>
                       
                    </div>
                </div>
            </div>

            <!-- Representante Legal -->
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">Representante Legal</div>
                    <div class="card-body">
                        <p><strong>Cédula:</strong> {{ $contribuyente->ruc_representante_legal ?? 'N/A' }}</p>
                        <p><strong>Nombres:</strong> {{ $contribuyente->nombre_representante_legal ?? 'N/A' }}</p>
                       
                    </div>
                </div>
            </div>
        </div>

        <!-- Información General y Otros Detalles -->
        <br>
        <div class="card mb-4">
            <div class="card-header">Información General y Otros Detalles</div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        
                        <!-- Estado del Contribuyente con etiqueta de color -->
                        <p><strong>Estado del Contribuyente:</strong>
                            @switch($contribuyente->estado_contribuyente_id)
                                @case(1)
                                    <span class="badge bg-success">Activo</span>
                                    @break
                                @case(2)
                                    <span class="badge bg-danger">Inactivo</span>
                                    @break
                                @case(3)
                                    <span class="badge bg-warning">Suspendido</span>
                                    @break
                                @default
                                    <span class="badge bg-secondary">Desconocido</span>
                            @endswitch
                        </p>
                        <p><strong>Tipo de Contribuyente:</strong>
                            @switch($contribuyente->tipo_contribuyente)
                                @case(1)
                                    Persona Natural
                                    @break
                                @case(2)
                                    Sociedad
                                    @break
                                @default
                                    N/A
                            @endswitch
                        </p>
                        <p><strong>Fecha Nacimiento:</strong> {{ $contribuyente->fecha_nacimiento ?? 'N/A' }}</p>
                        <p><strong>RUC:</strong>  <i class="fa fa-file-pdf" style="color: cornflowerblue;"></i></p>
                         <!-- Clase Contribuyente -->
                       
                    </div>
                    <div class="col-md-6">
                        <p><strong>Obligado a Contabilidad:</strong> {{ $contribuyente->obligado_contabilidad ? 'Sí' : 'No' }}</p>
                        <p><strong>Regimen:</strong> {{ $contribuyente->clase_cont ?? 'N/A' }}</p>
                        <p><strong>Edad:</strong> {{ $contribuyente->edad_contribuyente ?? 'N/A' }}</p>
                        <p><strong>Es Artesano:</strong> </strong>  <i class="fa fa-file-pdf" style="color: cornflowerblue;"></i></p>
                       
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header">Fechas</div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        
                        <!-- Estado del Contribuyente con etiqueta de color -->
                        <p><strong>Fecha Inicio Actividades:</strong>
                            {{ $contribuyente->fecha_inicio_actividades ?? 'N/A' }}</p>
                        </p>
                        <p><strong>Fecha Actualización Actividades:</strong>
                            {{ $contribuyente->fecha_actualizacion_actividades ?? 'N/A' }}</p>
                        </p>
                        
                         <!-- Clase Contribuyente -->
                       
                    </div>
                    <div class="col-md-6">
                        <p><strong>Fecha Reinicio Actividades:</strong> {{ $contribuyente->fecha_reinicio_actividades ?? 'N/A'  }}</p>
                        <p><strong>Fecha Suspensión Definitiva:</strong> {{ $contribuyente->fecha_suspension_definitiva ?? 'N/A' }}</p>
                       
                    </div>
                </div>
            </div>
        </div>


        <div class="card mb-4">
            <div class="card-header">Ubicacion y Contacto</div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        
                        <!-- Estado del Contribuyente con etiqueta de color -->
                        <p><strong>Telefono:</strong>
                            {{ $contribuyente->telefono ?? 'N/A'  }}
                        </p>
                        <p><strong>Direccion:</strong>
                           {{ $contribuyente->direccion ?? 'N/A'  }}
                        </p>
                        <p><strong>Canton:</strong> {{ $contribuyente->nombre_canton ?? 'N/A' }}</p>
                        
                         <!-- Clase Contribuyente -->
                       
                    </div>
                    <div class="col-md-6">
                        <p><strong>Correo:</strong> {{ $contribuyente->correo_1 ?? 'N/A' }}</p>
                        <p><strong>Provincia:</strong> {{ $contribuyente->nombre_provincia ?? 'N/A' }}</p>
                        <p><strong>Parroquia:</strong> {{ $contribuyente->nombre_parroquia ?? 'N/A' }}</p>
                       
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header">Locales</div>
            <div class="card-body">
                @if($contribuyente->actividades)
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Nombre Comercial</th>
                                    <th>Direccion</th>
                                    <th>Arrendado</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($contribuyente->locales as $local)
                                    @php
                                        $direccion=$local->calle_principal;
                                        if(!is_null($local->calle_principal) && !is_null($local->calle_secundaria)){
                                            $direccion=$local->calle_principal." y ".$local->calle_secundaria;
                                        }
                                    @endphp
                                    <tr>
                                        <td>{{ $local->actividad_descripcion }}</td>
                                        <td>{{ $direccion }}</td>
                                        <td>{{ $local->local_propio == 1 ? 'Propio' : 'Arrendado' }}</td>
                                        <td>{{ $local->estado_establecimiento == 1 ? 'Abierto' : 'Cerrado' }}</td>
                                    </tr>
                                @endforeach
                            </tbody>
                        </table>
                    </div>
                @else
                    <p>No se encontraron actividades comerciales para este contribuyente.</p>
                @endif
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header">Actividades Comerciales</div>
            <div class="card-body">
                @if($contribuyente->actividades)
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>CIIU</th>
                                    <th>Descripción de la Actividad Comercial</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($contribuyente->actividades as $actividad)
                                    <tr>
                                        <td>{{ $actividad->ciiu }}</td>
                                        <td>{{ $actividad->actividad }}</td>
                                    </tr>
                                @endforeach
                            </tbody>
                        </table>
                    </div>
                @else
                    <p>No se encontraron actividades comerciales para este contribuyente.</p>
                @endif
            </div>
        </div>

      
    </div>
    @include('rentas.modal_locales')

    <div class="modal fade" id="actividadLocal" tabindex="-1" aria-labelledby="ActividadModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalActividadLabel">Nueva Actividad</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-12 mb-3">
                                
                                <div class="mb-3 row">
                                    <label for="cmb_actividad" class="col-sm-3 col-form-label" style="text-align: end;">Actividad</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <select id="cmb_actividad" name="cmb_actividad" class="col-md-4 col-form-label fw-bold form-control modal_act" style="width: 100%;" data-bs-theme="dark" onchange="cargaInfoActividad()">
                                                <option value=""></option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="codigo_act" class="col-sm-3 col-form-label" style="text-align: end;">Codigo</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="desabilita_txt form-control modal_act" name="codigo_act" id="codigo_act" value="{{old('codigo_act')}}">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="descripcion_act" class="col-sm-3 col-form-label" style="text-align: end;">Descripcion</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <textarea class="desabilita_txt form-control modal_act" name="descripcion_act" id="descripcion_act" value="{{old('descripcion_act')}}" rows="6"></textarea>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top:12px">
                                    <center>
                                        <button type="button" class="btn btn-success btn-sm" onclick="guardaActividad()">
                                            <span id="label_btn_actividad">Guardar</span>
                                        </button>

                                        <button type="button" onclick="cancelarActividad()" class="btn btn-danger btn-sm">
                                            Cancelar
                                        </button>
                                    </center>
                       
                                </div>

                                
                            </div>

                            <div class="col-md-12">
                                <div class="table-responsive">
                                    <table class="table table-bordered" id="tablaActividades" style="width: 100%">
                                        <thead>
                                            <tr>
                                                <th>Ciiu</th>
                                                <th>Actividad</th>
                                                <th></th>
                                            
                                            </tr>
                                        </thead>
                                        <tbody>
                                        </tbody>

                                    </table>
                                </div>
                            </div>
                    
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <!-- <button type="button" class="btn btn-success" onclick="guardaActividad()">
                        <span id="label_btn_actividad"></span>
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button> -->
                </div>
            </div>
        </div>
    </div>

     <div class="modal fade" id="modalContri" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Actualizar Contribuyente</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
               
                    <div class="modal-body">
                        <form method="POST" action="" id="form_new_contribuyente">
                        <div class="col-md-12">
                            <div class="row">

                                <!-- <form method="POST" action="" id="form_new_contribuyente"> -->
                                    @csrf
                                    <!-- Columna izquierda -->
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label for="tipo_persona_new" class="form-label">Tipo Persona</label>
                                            <select class="form-select modal_new_cont" id="tipo_persona_new" name="tipo_persona_new" onchange="cambioTipoPersona()">
                                                <option value="">Seleccione un tipo</option>
                                                <option value="1" {{ old('tipo_contribuyente_id') == '1' ? 'selected' : '' }}>Persona natural</option>
                                                <option value="2" {{ old('tipo_contribuyente_id') == '2' ? 'selected' : '' }}>Sociedades</option>
                                            </select>

                                           
                                        </div>

                                        <div class="mb-3">
                                            <label for="cmb_ruc" class="form-label">RUC-Contribuyente</label>
                                          
                                            <input type="text" name="cmb_ruc_new" class="form-control modal_new_cont" id="cmb_ruc_new">
                                        </div>

                                        <div class="mb-3">
                                            <label for="contribuyente" class="form-label">Contribuyente</label>
                                            <input type="text" class="form-control modal_new_cont" name="contribuyente" id="contribuyente" value="{{old('contribuyente')}}">
                                        </div>

                                        <div class="mb-3">
                                            <label for="fecha_nacimiento" class="form-label">Fecha Nacimiento</label>
                                            <input type="date" class="form-control modal_new_cont" name="fecha_nacimiento" id="fecha_nacimiento" value="{{old('fecha_nacimiento')}}">
                                        </div>

                                        <div class="mb-3">
                                            <label for="cmb_ruc_rep" class="form-label">RUC-Repres. Legal</label>
                                            <!-- <select id="cmb_ruc_rep" name="cmb_ruc_rep" class="form-control modal_new_cont" onchange="cargaRepresentanteLegal()">
                                                <option value=""></option>
                                            </select> -->
                                            <input type="text" name="cmb_ruc_rep" class="form-control modal_new_cont" id="cmb_ruc_rep">
                                        </div>

                                        <div class="mb-3">
                                            <label for="representante" class="form-label">Repres. Legal</label>
                                            <input type="text" class="form-control modal_new_cont" name="representante" id="representante" value="{{old('representante')}}">
                                        </div>

                                        <div class="mb-3">
                                            <label for="provincia" class="form-label">Provincia</label>
                                            <select class="form-select modal_new_cont" id="provincia" name="provincia" onchange="seleccionaProvincia()">
                                            <option value="">Seleccione una provincia</option>
                                                @foreach ($PsqlProvincia as $p)
                                                    <option value="{{$p->id}}" {{ old('provincia_id') == $p->id ? 'selected' : '' }}>{{$p->descripcion}}</option>
                                                @endforeach
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label for="canton" class="form-label">Cantón</label>
                                            <select class="form-select modal_new_cont" id="canton_id" name="canton_id" onchange="seleccionaParroquia()">
                                                <option value="">Seleccione una</option>
                                            
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label for="parroquia" class="form-label">Parroquia</label>
                                            <select class="form-select modal_new_cont" id="parroquia_id_" name="parroquia_id_" required>
                                                <option value="">Seleccione una Parroquia</option>
                                                
                                            </select>
                                        </div>

                                        <div class="mb-3">
                                            <label for="direccion" class="form-label">Dirección</label>
                                            <input type="text"  class="form-control modal_new_cont" name="direccion" id="direccion">{{ old('descripcion_act') }}
                                        </div>

                                        
                                    </div>

                                    <!-- Columna derecha -->
                                    <div class="col-md-6">
                                        

                                        <div class="mb-3">
                                            <label for="correo" class="form-label">Correo Electrónico</label>
                                            <input type="email" class="form-control modal_new_cont" name="correo" id="correo" value="{{old('correo')}}">
                                        </div>

                                        <div class="mb-3">
                                            <label for="telefono" class="form-label">Teléfono</label>
                                            <input type="text" class="form-control modal_new_cont" name="telefono" id="telefono" value="{{old('telefono')}}">
                                        </div>

                                        <div class="mb-3">
                                            <label class="form-label">Otros</label><br>
                                            <div class="form-check form-check-inline">
                                                <input class="form-check-input modal_new_cont" type="checkbox" id="obligado_contabilidad" name="obligado_contabilidad" value="1" {{ old('obligado_contabilidad') ? 'checked' : '' }}>
                                                <label class="form-check-label" for="obligado_contabilidad">Obligado a llevar contabilidad</label>
                                            </div>
                                            <div class="form-check form-check-inline">
                                                <input class="form-check-input modal_new_cont" type="checkbox" id="es_artesano" name="es_artesano" value="1" {{ old('es_artesano') ? 'checked' : '' }}>
                                                <label class="form-check-label" for="es_artesano">¿Es artesano?</label>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_ruc" class="form-label">Cargar Documento RUC</label>
                                            <input type="file" class="form-control modal_new_cont" name="doc_ruc" id="doc_ruc">
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_artesano" class="form-label">Cargar Documento Artesano</label>
                                            <input type="file" class="form-control modal_new_cont" name="doc_artesano" id="doc_artesano">
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_artesano" class="form-label">Fecha de Inicio Actividades</label>
                                            <input type="date" class="form-control modal_new_cont" name="fecha_inicio_act" id="fecha_inicio_act">
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_artesano" class="form-label">Fecha de Reinicio Actividades</label>
                                            <input type="date" class="form-control modal_new_cont" name="fecha_reinicio_act" id="fecha_reinicio_act">
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_artesano" class="form-label">Fecha de Actualizacion Actividades</label>
                                            <input type="date" class="form-control modal_new_cont" name="fecha_actualizacion_act" id="fecha_actualizacion_act">
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_artesano" class="form-label">Fecha de Suspension Actividades</label>
                                            <input type="date" class="form-control modal_new_cont" name="fecha_suspension_act" id="fecha_suspension_act">
                                        </div>

                                        <div class="mb-3">
                                            <label for="doc_artesano" class="form-label">Regimen</label>
                                                <select class="modal_new_cont form-select {{$errors->has('clase_contribuyente_id') ? 'is-invalid' : ''}}" id="clase_contribuyente_id" name="clase_contribuyente_id" required>
                                                    <option value="">Seleccione una clase</option>
                                                    @foreach ($clase as $c)
                                                        <option value="{{$c->id}}" {{ old('clase_contribuyente_id') == $c->id ? 'selected' : '' }}>{{$c->nombre}}</option>
                                                    @endforeach
                                            </select>
                                        </div>

                                        

                                    </div>
                                <!-- </form> -->
                            </div>

                        </div>
                        </form>
                    </div>
                
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="guardaContribuyente()">
                            <span id="label_btn_contribuyente1">Actualizar</span>
                        </button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
            </div>
        </div>
    </div>

@endsection
@push('scripts')
<!-- jQuery -->
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
<!-- DataTables -->

<script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
<script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
<script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>

<script>
     $(document).ready(function(){
        tablePersona = $("#tablaCatastro").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/catastrocontribuyente/datatables2") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'id', name: 'id'},
                {width: '',data: 'ruc', name: 'ruc'},
                {width: '',data: 'razon_social'},
                {width: '',data: 'fecha_inicio_actividades'},
                {width: '',data: 'obligado_contabilidad'},
                {width: '',data: 'estado_contribuyente_id'},
            ],
            "fixedColumns" : true
        });
    })

    function abrirLocales(){
        $('#modal_local').modal('show')
       
        llenar_tabla_locales( $('#id_cont').val())
    }

    $('#modal_local').on('hidden.bs.modal', function () {
        // alert("sdsd")
        location.reload()
    });

    function abrirActividades(){
        $('#modalActividadLabel').html('Nueva Actividad')
        $('#label_btn_actividad').html('Guardar')
        $('#actividadLocal').modal('show')

        llenar_tabla_actividades($('#id_cont').val())
    }

    $('#actividadLocal').on('shown.bs.modal', function () {
        $('#cmb_actividad').select2({
            dropdownParent: $('#actividadLocal'),
            ajax: {
                url: '../buscarActividad',
                dataType: 'json',
                delay: 250,
                data: function (params) {
                    return {
                        q: params.term
                    };
                },
                processResults: function (data) {
                    return {
                        results: data.map(item => ({
                            id: item.id,
                            text: item.ciiu + " - " + item.nombre
                        }))
                    };
                }
            },
            minimumInputLength: 1
        });
    });

    function llenar_tabla_actividades(idcontribuyente){       
        
        var num_col = $("#tablaActividades thead tr th").length; //obtenemos el numero de columnas de la tabla
        $("#tablaActividades tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center><span class="spinner-border" role="status" aria-hidden="true"></span><b> Obteniendo información</b></center></td></tr>`);
            
        $.get("../listado-actividades/"+idcontribuyente, function(data){
            console.log(data)
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                $("#tablaActividades tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
                return;   
            }
            if(data.error==false){
                
                if(data.resultado.length <= 0){
                    $("#tablaActividades tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
                    alertNotificar("No se encontró datos","error");
                    return;  
                }
            
                $('#tablaActividades').DataTable({
                    "destroy":true,
                    pageLength: 10,
                    autoWidth : false,
                    order: [[ 1, "desc" ]],
                    sInfoFiltered:false,
                    searching: false,
                    language: {
                        url: 'json/datatables/spanish.json',
                    },
                    columnDefs: [
                        { "width": "10%", "targets": 0 },
                        { "width": "80%", "targets": 1 },
                        { "width": "10%", "targets": 2 },
                       
                    
                    ],
                    data: data.resultado,
                    columns:[
                            {data: "ciiu"},
                            {data: "descripcion" },
                            {data: "id" },
                           
                          
                    ],    
                    "rowCallback": function( row, data, index ) {
                       
                        // $('td', row).eq(0).html(index+1)
                        // $('td', row).eq(0).html(`${data.provincia.descripcion}/${data.canton.nombre}/${data.parroquia.descripcion}
                        //                         `)
                                         
                        $('td', row).eq(2).html(`
                                    
                                                <button type="button" class="btn btn-danger btn-sm" onclick="eliminarActividad(${data.id})">
                                                    <i class="fa fa-trash"></i>
                                                </button>
                                                                                    
                                               
                                        
                                        
                        `); 
                    }             
                });
            }
        }).fail(function(){
            $("#tablaActividades tbody").html(`<tr><td colspan="${num_col}" style="padding:40px; 0px; font-size:20px;"><center>No se encontraron datos</center></td></tr>`);
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });

        $('.collapse-link').click();
        $('.datatable_wrapper').children('.row').css('overflow','inherit !important');

        $('.table-responsive').css({'padding-top':'12px','padding-bottom':'12px', 'border':'0', 'overflow-x':'inherit'});


    }

    function eliminarActividad(idactividad){
        if(confirm('¿Quiere eliminar el registro?')){
            vistacargando("m","Espere por favor")
            $.get("../eliminar-activida-contr/"+idactividad, function(data){
                vistacargando("")
                if(data.error==true){
                    alertNotificar(data.mensaje,"error");
                    return;   
                }
            
                alertNotificar(data.mensaje,"success");
                llenar_tabla_actividades($('#id_cont').val())
            
            }).fail(function(){
                vistacargando("")
                alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
            });
        }
    }

    function guardaActividad(){
        var cmb_actividad=$('#cmb_actividad').val()
        var cmb_propietario=$('#id_cont').val()

        if(cmb_propietario==null || cmb_propietario==""){
            alertNotificar("Debe seleccionar el contribuyente","error")
            return
        }

        if(cmb_actividad==null || cmb_actividad==""){
            alertNotificar("Debe seleccionar la actividad","error")
            return
        }

        vistacargando("m","Espere por favor")
        $.ajaxSetup({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            }
        });
        var tipo='POST'
        $.ajax({
            
            type: tipo,
            url: "../agregar-actividad",
            method: tipo,             
            data: {
                cmb_actividad: cmb_actividad,
                cmb_propietario:cmb_propietario
            },
            
            success: function(response) {
                vistacargando("")
                if(response.error==true){
                    alertNotificar(response.mensaje,"error")
                    return
                }
                alertNotificar(response.mensaje,"success")
                llenar_tabla_actividades(cmb_propietario)
                
                
            },
            error: function(xhr, status, error) {
                vistacargando("")
                console.error("Error al obtener los datos:", error);
            }
        });
    }

    $('#actividadLocal').on('hidden.bs.modal', function () {
        // alert("sdsd")
        location.reload()
    });

    function cancelarActividad(){
        $('.modal_act').val('')
    }

    function reportePdf(){
        var idcont=$('#id_cont').val()
        vistacargando("m","Espere por favor")
        $.get("../pdf-contribuyente/"+idcont, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            
            alertNotificar("El documento se descargara en unos segundos","success")   
            window.location.href="../../descargar-reporte/"+data.pdf        
        
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    function abrirContribuyente(){
        var idcont=$('#id_cont').val()
        vistacargando("m","Espere por favor")
        $.get("../detalle-contr/"+idcont, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            var fecha_nac=data.resultado.fecha_nacimiento
            fecha_nac=fecha_nac.split(" ")
            $('#tipo_persona_new').val(data.resultado.tipo_contribuyente)
            $('#cmb_ruc_new').val(data.resultado.ruc)
            $('#contribuyente').val(data.resultado.contribuyente)
            $('#fecha_nacimiento').val(fecha_nac[0])

            $('#cmb_ruc_rep').val(data.resultado.ruc_representante_legal)
            $('#cmb_ruc_new').prop('disabled',true)
            $('#representante').val(data.resultado.nombre_representante_legal)
            $('#direccion').val(data.resultado.direccion)
            $('#correo').val(data.resultado.correo_1)
            $('#telefono').val(data.resultado.telefono)

            $('#obligado_contabilidad').prop('checked', false);            
            if(data.resultado.obligado_contabilidad==true){
                $('#obligado_contabilidad').prop('checked', true);
            }
            $('#es_artesano').prop('checked', false);            
            if(data.resultado.es_artesano==true){
                $('#es_artesano').prop('checked', true);
            }
            
            $('#fecha_inicio_act').val(data.resultado.fecha_inicio_actividades)
            $('#fecha_reinicio_act').val(data.resultado.fecha_reinicio_actividades)
            $('#fecha_actualizacion_act').val(data.resultado.fecha_actualizacion_actividades)
            $('#fecha_suspension_act').val(data.resultado.fecha_suspension_definitiva)

            
            $('#clase_contribuyente_id').val(data.resultado.clase_contribuyente_id)
            $('#provincia').val(data.resultado.provincia_id)
            cargarcantones(data.resultado.provincia_id)

            setTimeout(() => {
                $('#canton_id').val(data.resultado.canton_id)
                cargarparroquia(data.resultado.canton_id)   
            }, 500); // 5000 milisegundos = 5 segundos

            setTimeout(() => {      
                   
                 $('#parroquia_id_').val(data.resultado.parroquia_id)  
                vistacargando("")   
                
            }, 1500); // 5000 milisegundos = 5 segundos
          
            $('#modalContri').modal('show')
                        
        
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
       
    }


    function seleccionaProvincia(){
      
        var idprov=$('#provincia').val()
        if(idprov=="" || idprov==null){return}
        cargarcantones(idprov)
    }

    function cargarcantones(idprovincia){
        var canton_id = document.getElementById('canton_id');

        axios.post('../canton', {
            _token: token,
            idprovincia:idprovincia
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando cantones");
                canton_id.innerHTML = res.data;
            }
        }).catch(function(err) {
            if(err.response.status == 500){
                console.log('error al consultar al servidor');
            }

            if(err.response.status == 419){
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }
        }).then(function() {

        });
    }

    function seleccionaParroquia(){
        var idcanton=$('#canton_id').val()
        if(idcanton=="" || idcanton==null){return}
        cargarparroquia(idcanton)
    }

    function cargarparroquia(idcanton){
   
        var parroquia_id = document.getElementById('parroquia_id_');

        axios.post('../parroquia', {
            _token: token,
            idcanton:idcanton
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando parroquia");
                console.log(res)
                parroquia_id.innerHTML = res.data;
            }
        }).catch(function(err) {
            if(err.response.status == 500){
                console.log('error al consultar al servidor');
            }

            if(err.response.status == 419){
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }
        }).then(function() {

        });
    }
        
    function cambioTipoPersona(){
        limpiaTipo()
        var tipo_per=$('#tipo_persona_new').val()
       
        if(tipo_per==1){
            $('#representante').prop('disabled', true);
            $('#cmb_ruc_rep').prop('disabled', true);
        }else{
            $('#obligado_contabilidad').prop('checked', true);
            $('#es_artesano').prop('checked', false);
            $('#doc_artesano').val('');
            $('#representante').prop('disabled', false);
        }
        
    }

    function limpiaTipo(){
        $('#contribuyente').prop('disabled', false);
        $('#cmb_ruc_rep').prop('disabled', false);

        $('#representante').val('');
        $('#cmb_ruc_rep').val('').trigger('change.select2')

        // $('#contribuyente').val('');
        // $('#cmb_ruc').val('').trigger('change.select2')
    }

    globalThis.EsArtesanoNew=0
    $('#es_artesano').change(function () {
        EsArtesanoNew=0
        $('#doc_artesano').val('')
        $('#doc_artesano').prop('disabled', true);
        if ($(this).is(':checked')) {

            var tipo=$('#tipo_persona_new').val()
            if(tipo==2){
            alertNotificar("No se permite como artesano a SOCIEDADES","error")
                $('#es_artesano').prop('checked', false);
            return 
            }

            $('#doc_artesano').prop('disabled', false);
            EsArtesanoNew=1
            
        } 
    });

    globalThis.LlevaContabilidaNew=0
    $('#obligado_contabilidad').change(function () {
        LlevaContabilidaNew=0
        $('#doc_ruc').val('')
    
        if ($(this).is(':checked')) {
            // $('#doc_ruc').prop('disabled', false);
        
            LlevaContabilidaNew=1
        } 
    });


    const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if(isDarkMode){
        applyDarkModeStyles('D')
    }else{
        applyDarkModeStyles('L')
    }


    function applyDarkModeStyles(enable) {
        let styleTag = document.getElementById('dark-mode-styles');

        if (enable=='D' ) {
            // Si el estilo ya existe, no lo agregamos de nuevo
            if (!styleTag) {
                styleTag = document.createElement('style');
                styleTag.id = 'dark-mode-styles';
                styleTag.innerHTML = `
                    .select2-container--default .select2-results__option {
                        background-color: #212529 !important;
                        color: #fff !important;
                    }
                    .select2-container--default .select2-results__option--highlighted {
                        background-color: #343a40 !important;
                        color: #fff !important;
                    }
                    .select2-container--default .select2-results__option[aria-selected="true"] {
                        background-color: #555 !important;
                        color: #fff !important;
                    }
                    .select2-container--default .select2-selection--single {
                        background-color: #212529 !important;
                        color: #fff !important;
                        border: 1px solid #555 !important;
                    }
                    .select2-container--default .select2-selection__arrow b {
                        border-color: #fff transparent transparent transparent !important;
                    }
                    .select2-container--default .select2-search--dropdown .select2-search__field {
                        background-color: #333 !important;
                        color: #fff !important;
                        border: 1px solid #555 !important;
                    }
                    .select2-container--default .select2-results__message {
                        background-color: #212529 !important;
                        color: #bbb !important;
                    }
                    .select2-container--default .select2-selection__rendered {
                        color: white !important;
                    }
                `;
                document.head.appendChild(styleTag);
            }
        } else {           
            // Si el usuario cambia a modo claro, eliminamos la etiqueta de estilos oscuros
            if (styleTag) {
                styleTag.remove();
            }
        }
    }
    // Llamar a la función cuando cambie el tema
    function cambiaTema(isDark) {
        applyDarkModeStyles(isDark);       
    }
    function setTheme(theme) {
        localStorage.setItem('theme', theme);  // Guardar preferencia en localStorage
        applyTheme(theme);
    }

    // Cuando la página carga, recuperamos la preferencia
    window.addEventListener('DOMContentLoaded', (event) => {
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme) {
            applyTheme(savedTheme);  // Aplica el tema guardado
        }
    });

    // Función para aplicar el tema (claro u oscuro)
    function applyTheme(theme) {
        // alert(theme)
        if(theme=='dark'){
            applyDarkModeStyles('D')
        }else if(theme=='light'){
            applyDarkModeStyles('L')
        }
    }
    function getSystemTheme() {
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }


    function guardaContribuyente(){
        var tipo=$('#tipo_persona_new').val()
        var contribuyente=$('#cmb_ruc_new').val()
        var txt_contribuyente=$('#contribuyente').val()
        var fecha_nacimiento=$('#fecha_nacimiento').val()
        var cmb_ruc_rep=$('#cmb_ruc_rep').val()
        var txt_repres=$('#representante').val()
        var id_provincia=$('#provincia').val()
        var id_canton=$('#canton_id').val()
        var id_parroquia=$('#parroquia_id_').val()
        // alert(id_parroquia)
        var direccion=$('#direccion').val()
        var correo=$('#correo').val()
        var telefono=$('#telefono').val()
        var doc_ruc=$('#doc_ruc').val()
        var doc_artesano=$('#doc_artesano').val()
        var clase_contribuyente_id=$('#clase_contribuyente_id').val()
      
        if(tipo=="" || tipo==null){
            alertNotificar("Seleccione un tipo","error")
            return
        }

        if(contribuyente=="" || contribuyente==null){
            alertNotificar("Seleccione un contribuyente","error")
            return
        }

        if(fecha_nacimiento=="" || fecha_nacimiento==null){
            alertNotificar("Ingrese la fecha de nacimiento","error")
            return
        }

        if(tipo==2){
            if(cmb_ruc_rep=="" || cmb_ruc_rep==null){
                alertNotificar("Seleccione un representante legal","error")
                return
            }
        }

        if(tipo==2){
            if(EsArtesanoNew==1){
                alertNotificar("No se permite como artesano a SOCIEDADES","error")
                $('#es_artesano').prop('checked', false);
                return 
            }
        }

        if(id_provincia=="" || id_provincia==null){
            alertNotificar("Seleccione la provincia","error")
            return
        }

        if(id_canton=="" || id_canton==null){
            alertNotificar("Seleccione el canton","error")
            return
        }

        if(id_parroquia=="" || id_parroquia==null){
            alertNotificar("Seleccione la parroquia","error")
            return
        }

        if(direccion=="" || direccion==null){
            alertNotificar("Ingrese la direccion","error")
            return
        }

        if(correo=="" || correo==null){
            alertNotificar("Ingrese el correo","error")
            return
        }

        if(telefono=="" || telefono==null){
            alertNotificar("Ingrese el telefono","error")
            return
        }

        if(EsArtesanoNew==1){
            if(doc_artesano=="" || doc_artesano==null){
                alertNotificar("Seleccione el documento del artesano","error")
                return
            } 
        }

        // if(doc_ruc=="" || doc_ruc==null){
        //     alertNotificar("Seleccione el documento del RUC","error")
        //     return
        // } 

        if(clase_contribuyente_id=="" || clase_contribuyente_id==null){
            alertNotificar("Seleccione el regimen","error")
            return
        } 

        $("#form_new_contribuyente").submit()

    }

    $("#form_new_contribuyente").submit(function(e){
    
        e.preventDefault();

        var ruta="../actualiza-contribuyente"
        
        vistacargando("m","Espere por favor")
        $.ajaxSetup({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            }
        });

        //comprobamos si es registro o edicion
        let AccionForm="R"
        let tipo_=""
        let url_form=""
        if(AccionForm=="R"){
            tipo_="POST"
            url_form=ruta
        }else{
            tipo_="PUT"
            url_form=ruta
        }

        // var FrmData=$("#formPatente").serialize();
        var FrmData = new FormData(this);
          var contribuyente_id=$('#id_cont').val()
      
        FrmData.append("contribuyente_id",contribuyente_id);
        $.ajax({
                
            type: tipo_,
            url: url_form,
            method: tipo_,             
            // data: FrmData,      
            
            // processData:false, 
            data: FrmData,
            contentType:false,
            cache:false,
            processData:false, 

            success: function(data){
                console.log(data)
                vistacargando("");                
                if(data.error==true){
                    alertNotificar(data.mensaje,'error');
                    return;                      
                }
                
                $('#modalContri').modal('hide')
                alertNotificar(data.mensaje,'success');
                location.reload()

            }, error:function (data) {
                console.log(data)

                vistacargando("");     
                alertNotificar('Ocurrió un error','error');
            }
        });

    })
</script>
@endpush