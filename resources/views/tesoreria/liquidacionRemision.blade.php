@extends('layouts.appv2')
@section('title', 'Tercera edad')
@push('styles')
<link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css" rel="stylesheet" />
<style>
    .select2-container .select2-selection--single {
        height: 36px !important;
    }
</style>
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Liquidacion</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    <div class="btn-group me-2">
        <button type="button" class="btn btn-sm btn-outline-secondary">Share</button>
        <button type="button" class="btn btn-sm btn-outline-secondary">Export</button>
    </div>
    <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
        <svg class="bi"><use xlink:href="#calendar3"/></svg>
        This week
    </button>
    </div>
</div>
 

    <div class="row">
        <div class="col">
            @if (session('status'))
                <div class="alert alert-success">
                    {{ session('status') }}
                </div>
            @endif
        </div>
    </div>
    <form id="formConsulta" action="{{route('store.liquidacion.remision')}}" method="post">
        @csrf
        <div class="row justify-content-md-center">
            <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">* Tipo : </label>
                    <select class="form-select" aria-label="Default select example" id="tipo" name="tipo" onchange="cambiaTipo()">
                        <option value="1" {{ old('tipo') == '1' ? 'selected' : '' }}>Matricula</option>
                        <option value="2" {{ old('tipo') == '2' ? 'selected' : '' }}>Clave Catastral</option>
                        <!-- <option value="3" {{ old('tipo') == '3' ? 'selected' : '' }}>Nombres</option> -->
                    </select>

                </div>
            </div>
            <div class="col-3" id="div_matricula">            
                <div class="mb-3">
                    <label for="num_predio">* Matricula inmobiliaria : </label>
                    @if($num_predio == 0)
                    <input type="number" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="num_predio" name="num_predio" value="{{old('num_predio')}}" >
                    @else
                    <input type="number" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="num_predio" name="num_predio" value="{{$num_predio}}" >
                    @endif
                    <div class="invalid-feedback">
                        @if($errors->has('num_predio'))
                            {{$errors->first('num_predio')}}
                        @endif
                    </div>
                </div>
            </div>

            <div class="col-5" id="div_clave" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Clave Catastral : </label>
                   
                    <!-- <input type="number" class="form-control {{$errors->has('inputclave') ? 'is-invalid' : ''}}" id="clave" name="clave" value="{{old('clave')}}" > -->
                    <select id="clave" name="clave" class="form-control modal_new_cont"  style="width: 100%;">
                        <option value=""></option>
                    </select>
                   
                </div>
            </div>

            <div class="col-5" id="div_nombres" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Nombres : </label>
                   
                   <select id="cmb_nombres" name="cmb_nombres" class="form-control modal_new_cont"  style="width: 100%;">
                        <option value=""></option>
                    </select>
                   
                </div>
            </div>
            
            <div class="col-3">
                <div class="mb-3">
                    <br>
                    <button id="btnConsulta" class="btn btn-primary" type="submit">
                        <span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span>
                        Consultar
                    </button>

                    <button id="btnImprimir" class="btn btn-success" type="button" onclick="descargarPdfLiquidacion()" disabled>
                        <span id="spanConsulta" class="bi bi-filetype-pdf" role="status" aria-hidden="true"></span>
                        Imprimir
                    </button>

                </div>
            </div>

        </div>

    </form>

   
        
        <div class="row mt-3">
            @if ($num_predio > 0)
            <input type="hidden" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="inputMatricula" name="inputMatricula" value="{{$num_predio}}" autofocus required>
            @endif
            <div class="col-12">
                <h3>Lista de liquidaciones</h3>
            </div>
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tableCita" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col">Num Predio</th>
                           
                            <th scope="col">Año</th>
                            <th>Liquidación</th>
                            <th scope="col">Cod. Catastral</th>
                            <th scope="col">C.I/RUC</th>
                            <th scope="col">Propietario</th>
                            <th scope="col">Total de pago</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyLiquidacion">

                            @isset($liquidacionUrbana)
                                @foreach ($liquidacionUrbana as $it )
                                    <tr>
                                        <td>{{$it->num_predio}}</td>
                                        <td>{{$it->anio}}</td>
                                        <td>{{$it->id_liquidacion}}</td>
                                        <td>{{$it->clave_cat}}</td>
                                        <td>{{$it->ci_ruc}}</td>
                                        @if($it->nombres != '')
                                        <td>{{$it->nombres.' '.$it->apellidos}}</td>
                                        @else
                                        <td>{{$it->nombre_comprador}}</td>
                                        @endif
                                        <td>{{$it->total_complemento}}</td>
                                    </tr>
                                @endforeach
                            @endisset
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>

    <div class="modal fade" id="modalContri" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Actualizar Contribuyente</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <!-- Columna izquierda -->
                            <div class="col-md-12">
                                
                                <div class="mb-3">
                                    <label for="cmb_ruc" class="form-label">CEDULA/RUC</label>
                                    <input type="hidden" class="form-control" name="id_liquidacion" id="id_liquidacion" >
                                    <input type="hidden" class="form-control" name="id_contribuyente" id="id_contribuyente" >
                                    <input type="text" class="form-control" name="cedula_ruc_cont" id="cedula_ruc_cont" onblur="BuscaContribuyente()" >
                                </div>

                                <div class="mb-3">
                                    <label for="contribuyente" class="form-label">NOMBRES</label>
                                    <input type="text" class="form-control" name="nombre_cont" id="nombre_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="cmb_ruc_rep" class="form-label">APELLIDOS</label>
                                    <input type="text" class="form-control" name="apellido_cont" id="apellido_cont" >
                                </div>

                                <div class="mb-3" style="display: none;">
                                    <label for="representante" class="form-label">DIRECCION</label>
                                    <input type="text" class="form-control" name="direccion_cont" id="direccion_cont" >
                                </div>

                                
                            </div>

                          
                        </div>

                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" onclick="actualizaContribuyente()">
                        Actualizar
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
@endsection
@push('scripts')
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>

<script>
    let tamanio=$('#tbodyLiquidacion tr').length
    if(tamanio>0){
        $('#btnImprimir').prop('disabled',false)
    }else{
         $('#btnImprimir').prop('disabled',true)
    }
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

    var buttonBuscar = document.getElementById('buttonBuscar');
    let token = "{{csrf_token()}}";

    var formExonerar = document.getElementById('formExonerar');
    function verificarSeleccionCasillas(){
        let formData2 = new FormData(formExonerar);
        if(formData2.getAll("checkLiquidacion[]").length > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    const reporteLiquidacion = document.getElementById('reporteLiquidacion');

    reporteLiquidacion.addEventListener('click', (e) => {
        if(verificarSeleccionCasillas() == true){
            let formData = new FormData(formExonerar);
            formExonerar.submit();
            formData.append('num_predio',inputMatricula);
            /*axios.post('tituloscoactiva/imprimir',formData).then(function(res) {
                alert('dentro del formulario');

            }).catch(function(err) {
                console.log(err);
                if(err.response.status == 500){

                    console.log('error al consultar al servidor');
                }

                if(err.response.status == 419){

                }
                if(err.response.status == 422){

                }
            }).then(function() {
                    //loading.style.display = 'none';
            });*/

        }else{
            alert('Seleccione al menos un registro');
            return false;
        }
    });

</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/coactiva/reporte.js?v='.rand())}}"></script>

@endpush
