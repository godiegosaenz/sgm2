@extends('layouts.appv2')
@section('title', 'Notificacion')
@push('styles')
<link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
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
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Notificacion Boleta</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    
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
    <form id="formConsulta" action="{{'tituloscoactivarural'}}" method="post">
        @csrf
        <div class="row justify-content-md-center">
            <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">* Tipo : </label>
                    <select class="form-select" aria-label="Default select example" id="tipo" name="tipo" onchange="cambiaTipo()">
                        <option value="0" {{ old('tipo') == '0' ? 'selected' : '' }}>Matricula</option>
                        <!-- <option value="1" {{ old('tipo') == '1' ? 'selected' : '' }}>Cédula</option> -->
                        <option value="2" {{ old('tipo') == '2' ? 'selected' : '' }}>Clave Catastral</option>
                        <option value="3" {{ old('tipo') == '3' ? 'selected' : '' }}>Nombres</option>
                    </select>

                </div>
            </div>

            <div class="col-3" id="div_matricula" >
                <div class="mb-3">
                    <label for="num_predio">* Matricula : </label>
                   
                    <input type="number" class="form-control {{$errors->has('inputmatricula') ? 'is-invalid' : ''}}" id="matricula" name="matricula" value="{{old('matricula')}}" >
                   
                </div>
            </div>

            <div class="col-3" id="div_cedula" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Cedula : </label>
                   
                    <input type="number" class="form-control {{$errors->has('inputcedula') ? 'is-invalid' : ''}}" id="cedula" name="cedula" value="{{old('cedula')}}" >
                   
                </div>
            </div>
            <div class="col-3" id="div_clave" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Clave Catastral : </label>
                   
                    <input type="number" class="form-control {{$errors->has('inputclave') ? 'is-invalid' : ''}}" id="clave" name="clave" value="{{old('clave')}}" >
                   
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
                    <button id="btnConsulta" class="btn btn-primary" type="button" onclick="llenarTabla()" >
                        <span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span>
                        Consultar
                    </button>
                </div>
            </div>

        </div>

    </form>

    <form class="" action="tituloscoactivarural/imprimir" id="formExonerar" name="formExonerar" method="post" enctype="multipart/form-data">
        <div class="row">
            <div class="col-12">
                <div class="mb-3">
                    <!-- <a id="reporteLiquidacion1" class="btn btn-secondary" rel="noopener noreferrer">
                        <i class="bi bi-filetype-pdf"></i>
                        General titulos
                    </a> -->
                    <button type="button" class="btn btn-secondary botones" rel="noopener noreferrer" onclick="generarTitulos()" disabled> 
                        <i class="bi bi-filetype-pdf"></i> General titulos
                    </button>

                    <button type="button" class="btn btn-warning botones" rel="noopener noreferrer" onclick="coactivarTitulos()" disabled> 
                        <i class="bi bi-envelope"></i> Notificar
                    </button>

                </div>

                
                  
            </div>
        </div>
        <div class="row mt-3">
           
            <div class="col-12">
                <h3>Lista de Emisiones</h3>
            </div>
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tableUrbana" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col">*</th>
                            <th scope="col">Seleccionar</th>
                            <th scope="col">Año</th>
                            <th>Liquidación</th>
                            <th scope="col">Cod. Catastral</th>
                            <th scope="col">Propietario</th>
                            <th scope="col">Emision</th>
                            <th scope="col">Intereses</th>
                            <th scope="col">Recargos</th>
                            <th scope="col">Total</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyurban">
                            <tr>
                                <td colspan="10" style="text-align:center">No existen registros</td>
                            </tr>

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
                    <h5 class="modal-title" id="modalContribuyenteLabel">Notificacion Titulos Coactiva</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                <form class="" action="" id="formNotificar" name="formNotificar" method="post" enctype="multipart/form-data">
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <!-- Columna izquierda -->
                            <div class="col-md-12">

                                <div class="mb-3">
                                    <label for="cmb_ruc" class="form-label">CONTRIBUYENTE</label>                                  
                                    <input type="text" class="form-control " name="contr_selecc" id="contr_selecc" readonly >
                                    <input type="text" class="form-control " name="idcontr_selecc" id="idcontr_selecc" readonly >
                                    
                                </div>
                                
                                <div class="mb-3">
                                    <label for="cmb_ruc" class="form-label">TITULOS</label>
                                  
                                    <textarea class="form-control " name="titulos_selecc" id="titulos_selecc" readonly ></textarea>
                                    <input type="hidden" id="inputId" name="id_liquidacion">
                                </div>

                                <div class="mb-3">
                                    <label for="contribuyente" class="form-label">OBSERVACION</label>
                                    <textarea class="form-control txt_coact" name="observacion" id="observacion" ></textarea>
                                </div>

                                <div class="mb-3">
                                    <label for="cmb_ruc_rep" class="form-label">DOCUMENTO NOTIFICACION</label>
                                    <input type="file" class="form-control txt_coact" name="archivo" id="archivo" >
                                </div>

                                <div class="mb-3">
                                    <label for="cmb_ruc" class="form-label">CORREOS</label>                                  
                                    <input type="text" class="form-control " name="email_selecc" id="email_selecc"  >
                                    
                                </div>

                              

                            </div>

                          
                        </div>

                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success">
                        Registrar
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
                </form>
            </div>
        </div>
    </div>
@endsection
@push('scripts')
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
<script>

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
    var tipo=$('#tipo').val()
    if(tipo=="0"){
        $('#div_matricula').show()
        $('#div_cedula').hide()
        $('#div_nombres').hide()
        $('#div_clave').hide()
    }
    else if(tipo=="1"){
        $('#div_cedula').show()
        $('#div_clave').hide()
        $('#div_nombres').hide()
        $('#div_matricula').hide()
    }else if(tipo=="2"){
        $('#div_matricula').hide()
        $('#div_cedula').hide()
        $('#div_nombres').hide()
        $('#div_clave').show()
    }else{
        $('#div_matricula').hide()
        $('#div_cedula').hide()
        $('#div_nombres').show()
        $('#div_clave').hide()
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

    
</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/coactiva/notificacion.js?v='.rand())}}"></script>

@endpush
