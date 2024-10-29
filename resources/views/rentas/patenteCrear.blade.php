@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<style>
.checkbox-grande {
    transform: scale(1.5); /* Ajusta el número para el tamaño deseado */
    margin-right: 8px; /* Ajuste opcional */
}
</style>

@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Registrar patente</h2>
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

        <form>
                <!-- Información General -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Información contribuyente</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="buscar_contribuyente" class="form-label">Seleccionar Contribuyente</label>
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" placeholder="Buscar Contribuyente" id="buscar_contribuyente">
                            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#modalContribuyente">Buscar</button>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <!-- Columna izquierda -->
                    <div class="col-md-6 mb-3">

                        <div class="mb-3">
                            <label for="representante_legal" class="form-label">Rep. legal (sociedades)</label>
                            <input type="text" class="form-control" id="representante_legal" maxlength="255" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="cedula_representante" class="form-label">Cédula del Rep. Legal</label>
                            <input type="text" class="form-control" id="cedula_representante" maxlength="13" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="num_establecimientos" class="form-label">Numero de establecimientos</label>
                            <input type="text" class="form-control" id="num_establecimientos" required disabled>
                        </div>
                        <div class="mb-3">
                            <label for="ruc_cedula" class="form-label">RUC</label>
                            <input type="text" class="form-control" id="ruc_cedula" maxlength="13" disabled required>
                        </div>
                        <div class="mb-3">
                            <label for="domicilio" class="form-label">Razon Social</label>
                            <input type="text" class="form-control" id="razon_social" maxlength="255" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="domicilio" class="form-label">Domicilio</label>
                            <input type="text" class="form-control" id="domicilio" maxlength="255" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="Telefono_domicilio" class="form-label">Teléfono </label>
                            <input type="text" class="form-control" id="Telefono_domicilio" maxlength="15" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="Telefono_domicilio" class="form-label">Correo electronico</label>
                            <input type="text" class="form-control" id="correo_electronico" maxlength="15" disabled>
                        </div>

                    </div>
                    <!-- Columna derecha -->
                    <div class="col-md-6 mb-3">
                        <div class="mb-3">
                            <label for="representante_legal" class="form-label">Propietario cedula</label>
                            <input type="text" class="form-control" id="propietario_cedula" maxlength="255" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="cedula_representante" class="form-label">Propietario</label>
                            <input type="text" class="form-control" id="propietario_nombre" maxlength="13" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="inicio_actividades" class="form-label">Inicio de Actividades</label>
                            <input type="text" class="form-control" id="inicio_actividades" maxlength="255" disabled>
                        </div>
                        <div class="mb-3">
                            <label for="nombre_contribuyente" class="form-label">Fantasia comercial</label>
                            <input type="text" class="form-control" id="nombre_contribuyente" required disabled>
                        </div>
                        <div class="mb-3">
                            <label for="estado_contribuyente" class="form-label">Estado de contribuyente</label>
                            <input type="text" class="form-control" id="estado_contribuyente" required disabled>
                        </div>
                        <div class="mb-3">
                            <label for="clase_contribuyente" class="form-label">Clase de contribuyente</label>
                            <input type="text" class="form-control" id="clase_contribuyente" required disabled>
                        </div>
                        <div class="mb-3">
                            <label for="tipo_contribuyente" class="form-label">Tipo de contribuyente</label>
                            <input type="text" class="form-control" id="tipo_contribuyente" required disabled>
                        </div>

                        <div class="mb-3">
                            <label for="estado_establecimiento" class="form-label">Estado establecimiento</label>
                            <input type="text" class="form-control" id="estado_establecimiento" required disabled>
                        </div>
                        <div class="mb-3">
                            <label for="local_propio" class="form-label">Establecimiento Propio/Arrendado</label>
                            <input type="text" class="form-control" id="local_propio" required disabled>
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Actividades comerciales</legend>
                <div class="row">
                    <div class="col-md-12 mb-3 mt-3">
                        <table class="table table-striped" id="tablaActividades">
                            <thead>
                                <tr>
                                    <th scope="col">Seleccione</th>
                                    <th scope="col">CIIU</th>
                                    <th scope="col">Actividad</th>
                                </tr>
                            </thead>
                            <tbody>
                                @if(old('actividades'))
                                    @foreach(old('actividades') as $key => $actividad)
                                        <tr>
                                            <td>
                                                <input type="hidden" name="actividades[{{$key}}][ciiu]" value="{{ $actividad['ciiu'] }}">
                                                {{ $actividad['ciiu'] }}
                                            </td>
                                            <td>
                                                <input type="hidden" name="actividades[{{$key}}][nombre]" value="{{ $actividad['nombre'] }}">
                                                {{ $actividad['nombre'] }}
                                            </td>
                                            <td class="text-end">

                                                <button type="button" class="btn btn-sm btn-danger" onclick="eliminarFila(this)"><i class="bi bi-trash"></i></button>
                                            </td>
                                        </tr>
                                    @endforeach
                                @endif
                            </tbody>
                        </table>
                    </div>
                </div>
            </fieldset>

            <!-- Exoneraciones -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Exoneraciones y deducciones</legend>
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <div class="form-check">
                            <input class="form-check-input checkbox-grande" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Calificacion artesanal</label>
                        </div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <div class="form-check">
                            <input class="form-check-input checkbox-grande" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Tercera edad</label>
                        </div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <div class="form-check">
                            <input class="form-check-input checkbox-grande" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Discapacidad</label>
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Datos de declaracion</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="year_declaracion" class="form-label">Año de declaracion</label>
                        <select class="form-select" id="year_declaracion" required>
                            <option value="">Seleccione</option>
                            <option value="1">2024 >> Periodo Fiscal 2023</option>
                            <option value="2">2023 >> Periodo Fiscal 2022</option>
                            <option value="2">2022 >> Periodo Fiscal 2021</option>
                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="fecha_declaracion" class="form-label">Fecha de Declaración</label>
                        <input type="date" class="form-control" id="fecha_declaracion" required>
                    </div>
                    <div class="mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Lleva Contabilidad</label>
                        </div>
                    </div>

                </div>
            </fieldset>

            <!-- Declaracion tributaria -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Declaracion tibutaria (No obligados a llevar contabilidad)</legend>

                <div class="row">
                    <!-- Activos -->
                    <div class="col-md-6 mb-3">
                        <fieldset class="border p-3 bg-custom">
                            <legend class="w-auto"><h5>Activos</h5></legend>
                            <div class="mb-3 row">
                                <label for="act_caja_banco" class="col-sm-8 col-form-label">Caja y Bancos</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_caja_banco">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_ctas_cobrar" class="col-sm-8 col-form-label">Cuentas por Cobrar</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_ctas_cobrar">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_inv_mercaderia" class="col-sm-8 col-form-label">Inventario de Mercadería</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_inv_mercaderia">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_vehiculo_maquinaria" class="col-sm-8 col-form-label">Vehículos y Maquinaria</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_vehiculo_maquinaria">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_equipos_oficinas" class="col-sm-8 col-form-label">Equipos de Oficina</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_equipos_oficinas">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_edificios_locales" class="col-sm-8 col-form-label">Edificios y Locales</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_edificios_locales">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_terrenos" class="col-sm-8 col-form-label">Terrenos</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_terrenos">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_total_activos" class="col-sm-8 col-form-label">Total Activos (A)</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_total_activos" required>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                    <!-- Pasivos -->
                    <div class="col-md-6 mb-3">
                        <fieldset class="border p-3 bg-custom">
                            <legend class="w-auto"><h5>Pasivos</h5></legend>
                            <div class="mb-3 row">
                                <label for="pas_ctas_dctos_pagar" class="col-sm-8 col-form-label">Cuentas y Documentos por Pagar</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_ctas_dctos_pagar">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_obligaciones_financieras" class="col-sm-8 col-form-label">Obligaciones Financieras</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_obligaciones_financieras">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_otras_ctas_pagar" class="col-sm-8 col-form-label">Otras Cuentas por Pagar</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_otras_ctas_pagar">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_otros_pasivos" class="col-sm-8 col-form-label">Otros Pasivos</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_otros_pasivos">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_total_pasivos" class="col-sm-8 col-form-label">Total Pasivos (B)</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_total_pasivos" required>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
                <div class="row">
                    <!-- Patrimonio -->
                    <div class="col-md-6 mb-3">
                        <fieldset class="border p-3 mb-4">
                            <legend class="w-auto"><h5>Patrimonio</h5></legend>
                            <div class="row">
                                <div class="mb-3 row">
                                    <label for="pas_ctas_dctos_pagar" class="col-sm-6 col-form-label">Patrimonio total (A-B)</label>
                                    <div class="col-sm-6">
                                        <input type="number" class="form-control" id="pas_ctas_dctos_pagar">
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
            </fieldset>

            <!-- Patrimonio -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Declaracion tributaria obligados a llevar contabilidad</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <div class="mb-3 row">
                            <label for="act_caja_banco" class="col-sm-6 col-form-label">Total activos</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_caja_banco">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_ctas_cobrar" class="col-sm-6 col-form-label">(-) Total Pasivos</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_ctas_cobrar">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Patrimonio</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_inv_mercaderia">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Total de ingresos percibidos en el Canton San Vicente</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_inv_mercaderia">
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <div class="mb-3 row">
                            <label for="act_caja_banco" class="col-sm-6 col-form-label">Formulario SRI (No.)</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_caja_banco">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_ctas_cobrar" class="col-sm-6 col-form-label">Fecha de declaracion</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_ctas_cobrar">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Original/sustitutiva</label>
                            <div class="col-sm-6">
                                <select class="form-select" id="original" required>
                                    <option value="">Seleccione</option>
                                    <option value="1">Original</option>
                                    <option value="2">Sustitutiva</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>


            <!-- Botón de envío -->
            <button type="submit" class="btn btn-primary">Generar Patente</button>
        </form>
    </div>
    <!-- Modal para Propietario -->
    <div class="modal fade" id="modalContribuyente" tabindex="-1" aria-labelledby="propietarioModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Buscar Contribuyente</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="tablaContribuyente" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th>Acciones</th>
                                        <th>RUC</th>
                                        <th>Razon Social</th>
                                    </tr>
                                </thead>
                                <tbody>
                                </tbody>

                            </table>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
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
<script>
    $(document).ready(function(){
        tablaContribuyente = $("#tablaContribuyente").DataTable({
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
                "url": '{{ url("/catastrocontribuyente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                    d.tipo = 'contribuyente';
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'ruc', name: 'ruc'},
                {width: '',data: 'razon_social', name: 'razon_social'},
            ],
            "fixedColumns" : true
        });
    })
</script>
<script>
    let token = "{{csrf_token()}}";
    function limpiarCampos() {
        document.getElementById("propietario_nombre").value = "";
        document.getElementById("propietario_cedula").value = "";
        document.getElementById("ruc_cedula").value = "";
        document.getElementById("nombre_contribuyente").value = "";
        document.getElementById("razon_social").value = "";
        document.getElementById("domicilio").value = "";
        document.getElementById("Telefono_domicilio").value = "";
        document.getElementById("correo_electronico").value = "";
        document.getElementById("representante_legal").value = "";
        document.getElementById("cedula_representante").value = "";
        document.getElementById("inicio_actividades").value = "";
        document.getElementById("estado_contribuyente").value = "";
        document.getElementById("clase_contribuyente").value = "";
        document.getElementById("tipo_contribuyente").value = "";
        document.getElementById("num_establecimientos").value = "";
        document.getElementById("estado_establecimiento").value = "";
        document.getElementById("local_propio").value = "";
    }
    function seleccionarcontribuyente(id){
        limpiarCampos();
        axios.post("{{route('get.catastro')}}", {
            _token: token,
            id:id
        }).then(function(res) {
            if(res.status==200) {
                const data = res.data.contribuyente; // Los datos del contribuyente
                const datapropietario = res.data.propietarios; // Los datos del contribuyente
                const dataclase = res.data.clase_contribuyente; // Los datos del contribuyente
                const actividadesComerciales = res.data.actividades; // Los datos del contribuyente
                let estadoContribuyente;
                let tipoContribuyente;
                let estadoEstablecimiento;
                let localPropio;

                if (data.estado_contribuyente_id === 1) {
                    estadoContribuyente = "Activo";
                } else if (estado_contribuyente_id === 2) {
                    estadoContribuyente = "Inactivo";
                } else if (estado_contribuyente_id === 3) {
                    estadoContribuyente = "Suspendido";
                } else {
                    estadoContribuyente = "Desconocido"; // Opcional: texto si el estado no coincide
                }
                if (data.tipo_contribuyente === 1) {
                    tipoContribuyente = "Persona natural";
                } else if (tipo_contribuyente === 2) {
                    tipoContribuyente = "Sociedades";
                } else {
                    tipoContribuyente = "Desconocido"; // Opcional: texto si el estado no coincide
                }
                if (data.estado_establecimiento === 1) {
                    estadoEstablecimiento = "Abierto";
                } else if (tipo_contribuyente === 2) {
                    estadoEstablecimiento = "Cerrado";
                } else {
                    estadoEstablecimiento = "Desconocido"; // Opcional: texto si el estado no coincide
                }
                if (data.local_propio === 1) {
                    localPropio = "Propio";
                } else if (tipo_contribuyente === 2) {
                    localPropio = "Arrendado";
                } else {
                    localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
                }
                // Asigna los datos a los campos del formulario
                document.getElementById("propietario_nombre").value = datapropietario.nombres ?? ""+" "+datapropietario.apellidos ?? "";
                document.getElementById("propietario_cedula").value = datapropietario.ci_ruc ?? "";
                document.getElementById("ruc_cedula").value = data.ruc ?? "";
                document.getElementById("nombre_contribuyente").value = data.nombre_fantasia_comercial ?? "" // Nombre de fantasía comercial
                document.getElementById("razon_social").value = data.razon_social ?? "";
                document.getElementById("domicilio").value = `${data.calle_principal ?? ""} ${data.calle_secundaria ?? ""} ${data.referencia_ubicacion ?? ""}`.trim();
                document.getElementById("Telefono_domicilio").value = data.telefono ?? "";
                document.getElementById("correo_electronico").value = data.correo_1 ?? "";
                document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
                document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
                document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
                document.getElementById("estado_contribuyente").value = estadoContribuyente;
                document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
                document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
                document.getElementById("num_establecimientos").value = data.id ?? "";
                document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
                document.getElementById("local_propio").value = localPropio ?? "";
                actividadesComerciales.forEach((actividad) => {
                    seleccionarActividad(actividad.id,actividad.descripcion,actividad.ciiu);
                });



            }else{
                alert(res.status);
            }
        }).catch(function(err) {
            console.log(err)
        }).then(function() {

        });
        var modal = bootstrap.Modal.getInstance(representanteModal)
        modal.hide();
    }

    function seleccionarActividad(id, nombre, ciiu) {
        // Crear un nuevo elemento <tr>
        const nuevaFila = document.createElement("tr");

        // Definir el contenido de la nueva fila, incluyendo inputs ocultos para enviar a Laravel
        nuevaFila.innerHTML = `
            <td>
                <input class="form-check-input checkbox-grande" type="checkbox" name="actividades[${id}][id]" value="${id}">
            </td>
            <td>
                <input type="hidden" name="actividades[${id}][ciiu]" value="${ciiu}">
                ${ciiu}
            </td>
            <td>
                <input type="hidden" name="actividades[${id}][nombre]" value="${nombre}">
                ${nombre}
            </td>

        `;

        // Añadir la nueva fila a la tabla
        document.querySelector("#tablaActividades tbody").appendChild(nuevaFila);

        // Cerrar el modal
        var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
        modal.hide();
    }
    function eliminarFila(boton) {
            // Eliminar la fila padre del botón clicado
            boton.closest("tr").remove();
        }
    document.getElementById('propietario').addEventListener('keypress', function(event) {
        // Verificar si la tecla presionada es 'Enter' (keyCode 13)
        if (event.key === 'Enter') {
            // Obtener el valor del input
            event.preventDefault();
            let query = event.target.value;
            const spinner = document.getElementById('spinner');
            spinner.style.display = 'inline-block';
            nombresPropietario.value = '';
            nombresPropietario2.value = '';
            propietario_id.value = '';

            // Asegurarte que no esté vacío
            if (query.trim() !== '') {
                axios.post('{{route('getentecedula.ente')}}', {
                    _token: token,
                    query:query
                    }).then(function(res) {
                        if(res.status==200) {
                            propietario.value = res.data.ci_ruc;
                            nombresPropietario.value = res.data.nombres+' '+res.data.apellidos;
                            nombresPropietario2.value = res.data.nombres+' '+res.data.apellidos;
                            propietario_id.value = res.data.id;
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function(err) {
                        if(err.response.status == 500){
                            console.log('error al consultar al servidor');
                        }

                        if(err.response.status == 419){
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
            } else {
                spinner.style.display = 'none';
            }
        }
    });


    function cargarparroquia(idcanton){
        var canton_id = document.getElementById('canton_id');

        axios.post('{{route('getparroquia.catastro')}}', {
            _token: token,
            idcanton:idcanton
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando parroquia");
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
    (() => {
        'use strict'

        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        const forms = document.querySelectorAll('.needs-validation')

        // Loop over them and prevent submission
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }

            form.classList.add('was-validated')
            }, false)
        })
    })()
</script>
@endpush
