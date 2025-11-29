@extends('layouts.appv2')
@section('title', 'Titulo Rural')
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
    <h4 class="h2">Titulos Cobrados Rural</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    
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
    <form id="formConsulta" action="{{'tituloscoactivarural'}}" method="post">
        @csrf
        <div class="row justify-content-md-center">

            <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">* Tipo Persona: </label>
                    <select class="form-select" aria-label="Default select example" id="tipo_per" name="tipo_per" >
                        <option value="1" {{ old('tipo_per') == '1' ? 'selected' : '' }}>Natural</option>
                        <option value="2" {{ old('tipo_per') == '2' ? 'selected' : '' }}>Juridica</option>
                      
                    </select>

                </div>
            </div>
            <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">* Tipo : </label>
                    <select class="form-select" aria-label="Default select example" id="tipo" name="tipo" onchange="cambiaTipo()">
                        <option value="1" {{ old('tipo') == '1' ? 'selected' : '' }}>Cédula</option>
                        <option value="2" {{ old('tipo') == '2' ? 'selected' : '' }}>Clave Catastral</option>
                        <option value="3" {{ old('tipo') == '3' ? 'selected' : '' }}>Nombres</option>
                    </select>

                </div>
            </div>
            <div class="col-3" id="div_cedula">
                <div class="mb-3">
                    <label for="num_predio">* Cedula : </label>
                   
                    <input type="number" class="form-control buscarCont" id="cedula" name="cedula" >
                   
                </div>
            </div>
            <div class="col-3" id="div_clave" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Clave Catastral : </label>
                   
                    <input type="number" class="form-control buscarCont" id="clave" name="clave" >
                   
                </div>
            </div>

            <div class="col-5" id="div_nombres" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Nombres : </label>
                   
                   <!-- <select id="cmb_nombres" name="cmb_nombres" class="form-control modal_new_cont"  style="width: 100%;">
                        <option value=""></option>
                    </select> -->
                    <input type="text" class="form-control buscarCont" id="nombre" name="nombre" >
                   
                </div>
            </div>

           
        </div>

    </form>

    <div>
      
        <div class="row mt-3">
            
           
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tableRural" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col"></th>
                            <th scope="col">Tipo</th>
                            <th scope="col">Cod Catastral</th>
                            <th>Direccion</th>
                            <th scope="col">Apellidos y Nombres</th>
                            <th scope="col">RUC / CI</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyRural">
                            <tr>
                                <td colspan="6" style="text-align:center">No hay datos disponibles</td>
                            </tr>
                           
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalContri" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true"  data-bs-backdrop="static" data-bs-keyboard="false">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Copia de Titulos de Credito Pagados</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <!-- Columna izquierda -->
                         
                                
                                <div class="col-md-6">
                                   <b>Contribuyente:</b> <span id="nombre_contr"></span><br>
                                   <b>Direccion:</b> <span id="direccion_contr"></span>
                                </div>

                                <div class="col-md-6">
                                    <b>C.I./RUC:</b> <span id="num_ident_contr"></span><br>
                                    <b>Clave Catastral:</b> <span id="clave_contr"></span>
                                </div>

                        </div>

                        <div class="col-md-12" style="margin-top: 20px;">
                            <table class="table table-bordered table-hover" id="tableDetalleRural" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th scope="col">
                                            <center><input type="checkbox" id="selectAll"></center>
                                        </th>
                                        <th scope="col">Tipo</th>
                                        <th scope="col">Num Titulo</th>
                                        <th>Codigo</th>
                                        <th scope="col">Direccion</th>
                                        <th scope="col">Fecha de Cobro</th>
                                        <th scope="col">V. Cobrado</th>
                                        
                                    </tr>
                                </thead>
                                <tbody id="tbodyRuralDetalle">

                                
                                </tbody>
                            </table>

                        </div>

                        <div class="col-md-12 row">
                            <div class="col-md-12" >
                               <center><b>Total Cobrado: </b><span id="total_cobrado"></span></center>
                               
                            </div>

                            <!-- <div class="col-md-6" >
                                <center><b>Total Seleccionado: </b><span id="total_seleccionado"></span></center>                               
                            </div> -->

                            <div class="col-md-12" style="margin-top:20px">
                                <center>
                                    <button type="button" class="btn btn-sm btn-success" onclick="cobrarTituloUrbano()">Descargar</button>
                                    <button type="button" class="btn btn-sm btn-danger" onclick="cerrarModalPago()">Cerrar</button>
                                </center>
                            </div>
                        </div>

                    </div>
                </div>
               
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

                    .sweet-alert {
                        background-color: #18191a;
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
   
    if(tipo=="1"){
        $('#div_cedula').show()
        $('#div_clave').hide()
        $('#div_nombres').hide()
    }else if(tipo=="2"){
        $('#div_cedula').hide()
        $('#div_nombres').hide()
        $('#div_clave').show()
    }else{
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

<script src="{{ asset('js/titulocredito/tituloruralcobrado.js?v='.rand())}}"></script>

@endpush
