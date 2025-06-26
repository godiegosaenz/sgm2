@extends('layouts.appv2')
@section('title', 'Certificado Solvencia')
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

.sweet-alert {
    background-color: #d3dce9;
    color: #000000;
    width: 478px;
    padding: 17px;
    border-radius: 5px;
    text-align: center;
    position: fixed;
    left: 50%;
    top: 50%;
    margin-left: -256px;
    margin-top: -200px;
    overflow: hidden;
    display: none;
    z-index: 2000;
}

/* Estilo oscuro automático si body tiene la clase dark-mode */
body.dark-mode .sweet-alert {
    background-color:rgb(140, 167, 230);
    color:rgb(22, 22, 21);
}


</style>
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Consulta Deudas</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="nuevoCliente()">Nuevo Contribuyente</button>
        
        </div>
    
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
            
            <div class="col-3" id="div_cedula1">
                <div class="mb-3">
                    <label for="num_predio">* Cedula/RUC : </label>
                   
                    <input type="number" class="form-control {{$errors->has('inputcedula') ? 'is-invalid' : ''}}" id="cedula" name="cedula" value="{{old('cedula')}}" >
                   
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
                    <button type="button" class="btn btn-success" rel="noopener noreferrer" id="btn_genera" disabled onclick="generarTitulos()"> 
                        <i class="bi bi-filetype-pdf"></i> General No Deudor
                    </button>

                </div>

                
                  
            </div>
        </div>
        <div class="row mt-3">
           
            <div class="col-12">
                <h3>Lista de Deudas</h3>
            </div>
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tablaDeudas" style="width: 100%">
                        <thead>
                            <tr>
                                <th scope="col">Nombres</th>
                                <th scope="col">Tipo</th>
                                <th scope="col">Valor</th>
                                <th></th>
                            
                            </tr>
                        </thead>
                        <tbody id="tbodyurban">

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>

    <div class="modal fade" id="modalDetalle" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalDetalleLabel">Detalle</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <!-- Columna izquierda -->
                            

                                <div class="col-md-6">
                                  
                                    <ul class="nav nav-pills nav-stacked"style="margin-left:10px">
                                        <li style="border-color: white"><a><i class="fa fa-home text-blue"></i> <b class="text-black1" style="font-weight: 650 !important">Tipo</b>: <span  id="tipo_deudor"></span></a></li>
                                    
                                    </ul>
                                    
                                    <ul class="nav nav-pills nav-stacked"style="margin-left:10px">
                                        <li style="border-color: white"><a><i class="fa fa-credit-card text-blue"></i> <b class="text-black1" style="font-weight: 650 !important">Cedula/Ruc</b>: <span  id="ci_ruc_deudor"></span></a></li>
                                    
                                    </ul>
                                </div> 
                                
                                <div class="col-md-6">
                                    
                                    <ul class="nav nav-pills nav-stacked"style="margin-left:10px">
                                        <li style="border-color: white"><a><i class="fa fa-money text-blue"></i> <b class="text-black1" style="font-weight: 650 !important">Total</b>: <span  id="total_deudor"></span></a></li>
                                    
                                    </ul> 
                                    
                                    <ul class="nav nav-pills nav-stacked"style="margin-left:10px">
                                        <li style="border-color: white"><a><i class="fa fa-user text-blue"></i> <b class="text-black1" style="font-weight: 650 !important">Nombres</b>: <span  id="nombre_deudor"></span></a></li>
                                    
                                    </ul>
                                </div>   
                            <div class="col-md-12" style="margin-top:12px">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover" id="tablaDetalleDeudas" style="width: 100%">
                                        <thead>
                                            <tr>
                                                 <th scope="col">Codigo Clave</th>
                                                <th scope="col">Año</th>
                                                <th scope="col">Emision</th>
                                                <th scope="col">Descuento</th>
                                                <th scope="col">Recargo</th>
                                                <th scope="col">Intereses</th>
                                                <th scope="col">Total</th>
                                            
                                            </tr>
                                        </thead>
                                        <tbody id="tbodyDetalle">

                                        </tbody>
                                    </table>
                                </div>
                            </div>

                          
                        </div>

                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" onclick="imprimirTitulos()" >Imprimir</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

     <div class="modal fade" id="modalContri" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Nuevo Contribuyente</h5>
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
                                    
                                    <input type="text" class="form-control" name="cedula_ruc_cont" id="cedula_ruc_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="contribuyente" class="form-label">NOMBRES</label>
                                    <input type="text" class="form-control" name="nombre_cont" id="nombre_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="cmb_ruc_rep" class="form-label">APELLIDOS</label>
                                    <input type="text" class="form-control" name="apellido_cont" id="apellido_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="representante" class="form-label">DIRECCION</label>
                                    <input type="text" class="form-control" name="direccion_cont" id="direccion_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="representante" class="form-label">FECHA NACIMIENTO</label>
                                    <input type="date" class="form-control" name="fnacimiento_cont" id="fnacimiento_cont" >
                                </div>

                                
                            </div>

                          
                        </div>

                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" onclick="actualizaContribuyente()">
                        Guardar
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="documentopdf" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                @csrf
                <div class="modal-body">
                <div class="row">
                        <div class="col-sm-12 col-xs-11 "style="height: auto ">
                                <iframe width="100%" height="500" frameborder="0"id="iframePdf"></iframe>
                                    <p style="color: #747373;font-size:15px"></p>
                            </div>
                        </div>
                </div>
                <div class="modal-footer"> 
                    <center>
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>
                            <a href=""id="vinculo"><button  type="button" id="descargar"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>                                 
                    </center>               
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

    

    // const reporteLiquidacion = document.getElementById('reporteLiquidacion');

    // reporteLiquidacion.addEventListener('click', (e) => {
    //     if(verificarSeleccionCasillas() == true){
    //         let formData = new FormData(formExonerar);
    //         formExonerar.submit();
    //         formData.append('num_predio',inputMatricula);
    //         /*axios.post('tituloscoactiva/imprimir',formData).then(function(res) {
    //             alert('dentro del formulario');

    //         }).catch(function(err) {
    //             console.log(err);
    //             if(err.response.status == 500){

    //                 console.log('error al consultar al servidor');
    //             }

    //             if(err.response.status == 419){

    //             }
    //             if(err.response.status == 422){

    //             }
    //         }).then(function() {
    //                 //loading.style.display = 'none';
    //         });*/

    //     }else{
    //         alert('Seleccione al menos un registro');
    //         return false;
    //     }
    // });

</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/nodeudor/consultar.js?v='.rand())}}"></script>

@endpush
