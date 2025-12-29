@extends('layouts.appv2')
@section('title', 'Liquidaciones')
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

.fa-custom-border {
    border: 2px solid #28a745; /* verde */
    padding: 5px;              /* espacio interno para que se vea el borde */
    border-radius: 4px;        /* opcional, esquinas redondeadas */
}

.badge-green {
    display: inline-block;
    padding: 4px 10px;
    border: 2px solid #28a745; /* verde */
    color: #28a745;
    border-radius: 8px; /* estilo badge */
    font-size: 12px;
    font-weight: 600;
}

.badge-blue-low {
    display: inline-block;
    padding: 4px 10px;
    border: 2px solid #4da3ff; /* azul claro / azul bajo */
    color: #4da3ff;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;
}

.fila-verde{
    background-color: #28a745 !important;
}

.fila-azul{
    background-color: #4da3ff !important;
}

.p-4 {
    padding: 0.5rem !important;
}

</style>


@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Actualizacion</h4>
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
                    <label for="num_predio">* Ubicacion: </label>
                    <select class="form-select" aria-label="Default select example" id="lugar" name="lugar"  onchange="cambiaLugar()">
                        <option value="1" {{ old('lugar') == '1' ? 'selected' : '' }}>Urbana</option>
                        <option value="2" {{ old('lugar') == '2' ? 'selected' : '' }}>Rural</option>
                      
                    </select>

                </div>
            </div>
            <div class="col-3 class_rural" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Tipo : </label>
                    <select class="form-select" aria-label="Default select example" id="tipo" name="tipo" onchange="cambiaTipo()">
                        <option value="1" selected>Cédula</option>
                        <option value="2" >Clave Catastral</option>
                        <option value="3">Nombres</option>
                    </select>

                </div>
            </div>
            <div class="col-3 class_rural" id="div_cedula" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Cedula : </label>
                   
                    <input type="number" class="form-control buscarCont" id="cedula" name="cedula" >
                   
                </div>
            </div>

            <div class="col-3 " id="div_clave" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Clave Catastral : </label>
                   
                    <input type="number" class="form-control buscarCont" id="clave" name="clave" >
                   
                </div>
            </div>

            <div class="col-5 " id="div_nombres" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Nombres : </label>
                   
                   <!-- <select id="cmb_nombres" name="cmb_nombres" class="form-control modal_new_cont"  style="width: 100%;">
                        <option value=""></option>
                    </select> -->
                    <input type="text" class="form-control buscarCont" id="nombre" name="nombre" >
                   
                </div>
            </div>


            <div class="col-3 class_urb">
                <div class="mb-3">
                    <label for="num_predio">* Tipo : </label>
                    <select class="form-select" aria-label="Default select example" id="tipo_urb" name="tipo_urb" onchange="cambiaTipoU()">
                        <option value="1" selected>Matricula</option>
                        <option value="2" >Clave Catastral</option>
                        <option value="3">Nombres</option>
                    </select>

                </div>
            </div>
            <div class="col-3 class_urb" id="div_matricula_urb">
                <div class="mb-3">
                    <label for="num_predio">* Matricula : </label>
                   
                    <input type="number" class="form-control buscarCont" id="matricula_urb" name="matricula_urb" >
                   
                </div>
            </div>

            <div class="col-3 " id="div_clave_urb" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Clave Catastral : </label>
                   
                    <input type="number" class="form-control buscarCont" id="clave_urb" name="clave_urb" >
                   
                </div>
            </div>

            <div class="col-5 " id="div_nombres_urb" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Nombres : </label>
                   
                   <!-- <select id="cmb_nombres" name="cmb_nombres" class="form-control modal_new_cont"  style="width: 100%;">
                        <option value=""></option>
                    </select> -->
                    <input type="text" class="form-control buscarCont" id="nombre_urb" name="nombre_urb" >
                   
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
                            <th scope="col">Cedula/RUC</th>
                            <th scope="col">Nombres</th>
                            <th>Telefono</th>
                            <th scope="col">Correo</th>
                            <th scope="col">Direccion</th>
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

    <div class="modal fade" id="modalContri" tabindex="-1"
     aria-labelledby="ContribuyenteModalLabel"
     aria-hidden="true"
     data-bs-backdrop="static"
     data-bs-keyboard="false">

        <div class="modal-dialog modal-xl">
            <div class="modal-content">

                <!-- HEADER -->
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">
                        Predios
                    </h5>
                    <button type="button" class="btn-close" onclick="cerrarModalPago()"></button>
                </div>

                <!-- BODY -->
                <div class="modal-body">

                    <!-- TABS -->
                    <ul class="nav nav-tabs mb-3" role="tablist">
                        <li class="nav-item">
                            <button class="nav-link active"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-actual"
                                    type="button" onclick="ejecuta()">
                                <i class="bi bi-list-check text-success"></i> Listado
                            </button>
                        </li>
                        <li class="nav-item">
                            <button class="nav-link"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-vacio"
                                    type="button">
                                <i class="bi bi-bell-fill text-danger"></i> Actualizacion
                            </button>
                        </li>
                    </ul>

                    <!-- TAB CONTENT -->
                    <div class="tab-content">

                        <!-- TAB 1: CONTENIDO ACTUAL (SIN CAMBIOS) -->
                        <div class="tab-pane fade show active" id="tab-actual">

                            <form class=""
                                action="tituloscoactivarural/imprimir"
                                id="formExonerar"
                                name="formExonerar"
                                method="post"
                                enctype="multipart/form-data">
                                @csrf

                                <div class="col-md-12">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <b>Contribuyente:</b>
                                            <span id="nombre_contr"></span><br>
                                        </div>

                                        <div class="col-md-6">
                                            <b>C.I./RUC:</b>
                                            <span id="num_ident_contr"></span><br>
                                        </div>
                                    </div>

                                    <div class="col-md-12 mt-3">
                                        <table class="table table-bordered table-hover"
                                            id="tableDetalleRural"
                                            style="width:100%">
                                            <thead>
                                                <tr>
                                                   
                                                    <th>Matricula</th>
                                                   
                                                    <th>Codigo</th>
                                                    <th>Direccion</th>
                                                    <th>CI/RUC</th>
                                                    
                                                </tr>
                                            </thead>
                                            <tbody id="tbodyRuralDetalle"></tbody>
                                        </table>
                                    </div>

                                    <div class="row mt-3">
                                       
                                        <div class="col-md-12 text-center mt-2">
                                           
                                            <button type="button"
                                                    class="btn btn-sm btn-danger"
                                                    onclick="cerrarModalPago()">
                                                Cerrar
                                            </button>
                                        </div>
                                    </div>

                                </div>
                            </form>

                        </div>

                        <!-- TAB 2: VACÍO -->
                        <div class="tab-pane fade" id="tab-vacio">
                            <div class="text-center text-muted p-4" style="paddin: 0.5em !important;">
                                
                                <div class="p-4" style="border-radius:8px;">

                                    <form id="formActualiza" method="POST" action="" enctype="multipart/form-data">

                                        <!-- CÉDULA -->
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Cédula/RUC<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text"
                                                    class="form-control "
                                                    placeholder="Ejemplo 1314801349" name="ci_ruc_contribuyente"
                                                    id="ci_ruc_contribuyente" >
                                                <input type="hidden" name="lugar_not" id="lugar_not">
                                                <input type="hidden" name="id_cont" id="id_cont">
                                            </div>
                                           
                                        </div>

                                        <!-- NOMBRES -->
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Nombres<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text"
                                                    class="form-control "
                                                     name="nombre_contribuyente"
                                                    id="nombre_contribuyente" >
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center" id="div_apellidos">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Apellidos<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text"
                                                    class="form-control "
                                                    name="apellido_contribuyente"
                                                    id="apellido_contribuyente" >
                                            </div>
                                        </div>

                                        
                                        <!-- CIUDAD Domiciliaria -->
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Ciudad Domiciliaria<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text"
                                                    class="form-control "
                                                    name="ciudad_contribuyente"
                                                    id="ciudad_contribuyente" >
                                            </div>
                                        </div>

                                        <!-- DIRECCION -->
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Direccion Domiciliaria<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text"
                                                    class="form-control "
                                                    name="direccion_contribuyente"
                                                    id="direccion_contribuyente" >
                                            </div>
                                        </div>

                                        <!-- CORREO -->
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label text-end">
                                                Correo<span class="text-danger"> *</span> 
                                            </label>

                                            <div class="col-md-9">
                                                <div id="correos-container">
                                                    <div class="input-group mb-2">
                                                        <input type="email"
                                                            class="form-control"
                                                            name="correo[]">

                                                        <button class="btn btn-outline-success"
                                                                type="button"
                                                                onclick="agregarInput('correos-container', 'email', 'correo[]')">
                                                            <i class="bi bi-plus-lg"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>


                                        <!-- TELEFONO -->
                                       
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label text-end">
                                                Teléfono<span class="text-danger"> *</span> 
                                            </label>

                                            <div class="col-md-9">
                                                <div id="telefonos-container">
                                                    <div class="input-group mb-2">
                                                        <input type="text"
                                                            class="form-control"
                                                            name="telefono[]">

                                                        <button class="btn btn-outline-success"
                                                                type="button"
                                                                onclick="agregarInput('telefonos-container', 'text', 'telefono[]')">
                                                            <i class="bi bi-plus-lg"></i>
                                                        </button>
                                                      
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            
                                            <div class="col-md-12">
                                                <center>    
                                                    <button type="submit" class="btn bt-sm btn-primary" id="btn_enviar">Actualizar</button>
                                                    <button type="button" onclick="cancelarActualizacion()" class="btn bt-sm btn-danger"  id="btn_cancelar">Cancelar</button>
                                                </center>
                                            </div>
                                        </div>

                                    </form>

                                    
                                </div>

                            </div>
                        </div>

                    </div>
                </div>

            </div>
        </div>
    </div>


    <div class="modal fade" id="documentopdf" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true"
     data-bs-backdrop="static" data-bs-keyboard="false">
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
                            <button class="btn btn-success" onclick="printIframe()">Imprimir</button>                           
                            <a href=""id="vinculo"><button  type="button" id="descargar"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>                                 
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
   
    // if(tipo=="1"){
    //     $('#div_cedula').show()
    //     $('#div_clave').hide()
    //     $('#div_nombres').hide()
    // }else if(tipo=="2"){
    //     $('#div_cedula').hide()
    //     $('#div_nombres').hide()
    //     $('#div_clave').show()
    // }else{
    //     $('#div_cedula').hide()
    //     $('#div_nombres').show()
    //     $('#div_clave').hide()
    // }
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

    function printIframe() {
        const iframe = document.getElementById('iframePdf');
        iframe.contentWindow.focus();
        iframe.contentWindow.print();
    }


</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/titulocredito/actualizacion.js?v='.rand())}}"></script>

@endpush
