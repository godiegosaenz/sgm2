@extends('layouts.appv2')
@section('title', 'Lista de Impuesto de transito')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Lista de impuestos de transito</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            
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
    <div class="row" id="seccion_listado" >
        <div class="col-md-12">
            <div class="table-responsive">
                @csrf
                <table class="table table-bordered" style="width:100%" id="tablaImpuesto">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Cedula/Ruc</th>
                            <th scope="col">Contribuyente</th>
                            <th scope="col">Vehiculo</th>
                            <th scope="col">Numero de titulo</th>
                            <th scope="col">Fecha</th>
                            <th scope="col">Total a pagar</th>
                            
                        </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
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
                            
                            <button class="btn btn-success" onclick="printIframe()">Imprimir</button>
                            <a href=""id="vinculo"><button  type="button" id="descargar"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>  
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>                               
                    </center>               
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="tituloEliminaModal" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Baja de Titulo</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
               
                <div class="modal-body">
                    <div class="row">
                            
                        <form method="POST" action="" id="form_baja">
                            @csrf

                            <div class="col-md-12">
                                <div class="row align-items-center">
                                    <div class="col-md-2 text-end">
                                        <label for="marca_v" class="form-label mb-0">Motivo</label>
                                    </div>
                                    <div class="col-md-9">
                                        <input type="hidden" class="form-control" id="id_impuesto" name="id_impuesto">
                                        <textarea class="form-control" id="motivo_baja" name="motivo_baja"
                                            required rows="4"></textarea>
                                    </div>
                                </div>
                            </div>


                            <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                <div class="row align-items-center">
                                    <div class="col-md-2 text-end">
                                        <label for="marca_v" class="form-label mb-0"></label>
                                    </div>
                                    <div class="col-md-9">
                                        <button type="submit" class="btn btn-success btn-sm"><span
                                                id="btn_tipo">Aceptar</span></button>
                                        <button type="button" class="btn btn-warning btn-sm"
                                            onclick="cancelarBaja()">Cancelar</button>
                                    </div>
                                </div>
                            </div>

                        </form>

                    </div>
                
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="DetalleTitulo" tabindex="-1" aria-labelledby="ContribuyenteModalLabelz" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Detalle</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
               
                <div class="modal-body">
                    <div class="row">

                        <div class="col-md-5">
                            
                            <span><b>Proceso de Matriculacion Vehicular:</b></span> <span id="anio_proceso"></span><br>
                            <span><b>Contribuyente:</b></span> <span id="persona"></span><br>
                            <span><b>Cedula/RUC :</b></span> <span id="ci_persona"></span><br><br>
                            

                            <center><span><b>Datos de Vehiculo</b></span></center>
                            <span><b>Placa/CPN/RAMV:</b></span> <span id="placa_det"></span><br>
                            <span><b>Chasis:</b></span> <span id="chasis_det"></span><br>
                            <span><b>Avaluo:</b></span> <span id="avaluo_det"></span><br>
                            <span><b>Año de modelo:</b></span> <span id="modelo_det"></span><br>
                            <span><b>Clase Tipo:</b></span> <span id="clase_tipo_det"></span><br>
                            <span><b>Marca:</b></span> <span id="marca_det"></span><br>

                            <center><span><b>Otros Datos</b></span></center>
                            <span><b>Generado por:</b></span> <span id="generado"></span><br>
                            <span><b>Fecha:</b></span> <span id="fecha_generado"></span><br>
                        </div>

                        <div class="col-md-7">
                            <div class="table-responsive">
              
                                <table class="table table-bordered" style="width:100%" id="tablaImpuestoDetalle">
                                    <thead>
                                        <tr>
                                            <th scope="col">Rubros/Concepto</th>
                                            <th scope="col">Valor</th>
                                           
                                        </tr>
                                    </thead>
                                    <tbody id="tbodyDetalle">

                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="col-md-12">
                            <center> 
                                <button type="button" class="btn btn-success btn-sm" onclick="editarCobro()">Editar</button>
                                <button type="button" class="btn btn-danger btn-sm" onclick="cerrarCobro()">Cerrar</button>
                                
                            </center>
                        </div>
                        
                    </div>
                
                </div>
            </div>
        </div>
    </div>

    <div class="row" id="seccion_edicion" style="display:none">
        <form method="POST" action="{{ route('store.transito') }}">
            @csrf
            <fieldset class="border p-3 mb-4">
                <legend class="float-none w-auto px-3 fs-5">Datos de cliente</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="propietario" class="form-label">Busqueda por cedula <span
                                class="text-danger">*</span></label>
                        <div class="input-group">
                            <input type="hidden" name="id_impuesto_editar" id="id_impuesto_editar">
                            <input type="number" class="form-control {{$errors->has('cliente_id') ? 'is-invalid' : ''}}"
                                id="cliente_id" name="cliente_id" placeholder="Ingrese cedula o ruc"
                                value="{{old('cliente_id')}}" required>
                            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal"
                                data-bs-target="#modalCrearEnte">
                                Nuevo
                                <span id="spinner" class="spinner-border spinner-border-sm" style="display:none;"></span>
                            </button>
                            <div class="invalid-feedback" id="error_cliente_id">
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="cliente_id_2" name="cliente_id_2" value="">

                    <div class="col-md-6 mb-3">
                        <label for="razon_social" class="form-label">Nombres y apellidos</label>
                        <input type="text" class="form-control" id="nombrescliente" name="nombrescliente" maxlength="255"
                            value="{{old('nombresPropietario2')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Correo</label>
                        <input type="text" class="form-control" id="correocliente" maxlength="255"
                            value="{{old('correocliente')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Telefono</label>
                        <input type="text" class="form-control" id="telefonocliente" maxlength="255"
                            value="{{old('telefonocliente')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Direccion</label>
                        <input type="text" class="form-control" id="direccioncliente" maxlength="255"
                            value="{{old('direccioncliente')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Fecha de nacimiento</label>
                        <input type="text" class="form-control" id="fechanacimientocliente" maxlength="255"
                            value="{{old('fechanacimientocliente')}}" disabled>

                    </div>
                </div>
            </fieldset>

            <fieldset class="border p-3 mb-4">
                <legend class="float-none w-auto px-3 fs-5">Datos de vehiculo</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="propietario" class="form-label">Busqueda por PLACA/CPN/RAMV <span
                                class="text-danger">*</span></label>
                        <div class="input-group">

                            <input type="text" class="form-control {{$errors->has('vehiculo_id') ? 'is-invalid' : ''}}"
                                id="vehiculo_id" name="vehiculo_id" placeholder="Ingrese una placa o cpn o ramv"
                                value="{{old('vehiculo_id')}}" onkeyup="convertirMayuscula(this)" required>
                            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal"
                                data-bs-target="#vehiculoModal">
                                Nuevo
                                <span id="spinner2" class="spinner-border spinner-border-sm" style="display:none;"></span>
                            </button>
                            <div class="invalid-feedback" id="error_vehiculo_id">

                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="vehiculo_id_2" name="vehiculo_id_2" value="">

                    <div class="col-md-6 mb-3">
                        <label for="razon_social" class="form-label">Avaluo</label>
                        <input type="text" class="form-control" id="avaluo" name="nombresPropietario" maxlength="255"
                            value="{{old('nombresPropietario2')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Chasis</label>
                        <input type="text" class="form-control" id="chasis" maxlength="255"
                            value="{{old('nombresRepresentante2')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Año modelo</label>
                        <input type="text" class="form-control" id="year_modelo" maxlength="255"
                            value="{{old('nombresRepresentante2')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Marca</label>
                        <input type="text" class="form-control" id="marca" maxlength="255"
                            value="{{old('nombresRepresentante2')}}" disabled>

                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Clase Vehiculo</label>
                        <input type="text" class="form-control" id="clase_tipo" maxlength="255"
                            value="{{old('nombresRepresentante2')}}" disabled>

                    </div>

                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">RTV Cuadro Tarifario</label>
                        <input type="text" class="form-control" id="tipo" maxlength="255"
                            value="{{old('nombresRepresentante2')}}" disabled>

                    </div>

                    <div class="col-md-6 mb-3">
                        <label for="nombresRepresentante" class="form-label">Tipo Servicio</label>
                        <input type="text" class="form-control" id="tipo_vehi" maxlength="255"
                            value="{{old('nombresRepresentante2')}}" disabled>

                    </div>
                </div>
            </fieldset>

            <!-- Conceptos -->
            <fieldset class="border p-3 mb-4">
                <legend class="float-none w-auto px-3 fs-5">Detalle de impuesto</legend>
                <div class="row align-items-end mb-3">

                    <div class="col-md-3">
                        <label for="nombresRepresentante" class="form-label"> Proceso de Matriculacion Vehicular </label>
                        <select class="form-select {{ $errors->has('year_declaracion') ? 'is-invalid' : '' }}"
                            id="year_declaracion" name="year_declaracion" onchange="calcularImpuesto()">
                            <!-- <option value="">Seleccione año</option> -->
                            @foreach ($year as $y)
                                <option value="{{ $y->year }}" selected>{{ $y->year }}</option>
                            @endforeach
                        </select>
                        <div class="invalid-feedback" id="error_year_declaracion"></div>
                    </div>

                    <div class="col-md-3">
                        <label for="nombresRepresentante" class="form-label">Ultimo Año Matriculacion</label>
                        <select class="form-select {{ $errors->has('last_year_declaracion') ? 'is-invalid' : '' }}"
                            id="last_year_declaracion" name="last_year_declaracion" onchange="calcularImpuesto()">
                            @for ($i = 1; $i <= 15; $i++)
                                <option value="{{ date('Y') - $i }}">{{ date('Y') - $i }}</option>
                            @endfor
                        </select>
                        <div class="invalid-feedback" id="error_last_year_declaracion"></div>
                    </div>
                    <div class="col-md-3">
                        <label for="nombresRepresentante" class="form-label">¿Es solo un duplicado?</label>
                        <select class="form-select {{ $errors->has('solo_duplicado') ? 'is-invalid' : '' }}"
                            id="solo_duplicado" name="solo_duplicado" onchange="calcularImpuesto()">
                            <option value="no">No</option>
                            <option value="sticker">Sí, duplicado de Sticker</option>
                            <option value="matricula">Sí, duplicado de Matrícula</option>
                        </select>
                    
                    </div>

                    <div class="col-md-3 d-grid gap-2">
                        <button class="btn btn-primary" id="btn-calcular">
                            <span id="btn-text">Calcular</span>
                            <span id="spinner-btn" class="spinner-border spinner-border-sm d-none" role="status"
                                aria-hidden="true"></span>
                        </button>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Rubros</label>
                    <div id="lista-conceptos">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Seleccionar</th>
                                    <th>Descripción</th>
                                    <th>Valor (USD)</th>
                                </tr>
                            </thead>
                            <tbody id="tabla-conceptos">
                            
                            
                                @foreach($conceptos as $concepto)
                                    @php
                                        $checked='checked';
                                        if($concepto->codigo=='DM' || $concepto->codigo=='DE'){
                                            $checked='';
                                        }

                                    @endphp
                                    
                                
                                    <tr>
                                        <td>
                                            <input type="checkbox" class="form-check-input concepto-check"
                                                data-id="{{ $concepto->id }}" {{ $checked }} id="check_valor_{{ $concepto->codigo }}">
                                        </td>
                                        <td>{{ $concepto->concepto }}</td>
                                        <td>
                                            <input type="number" step="0.01" class="form-control concepto-valor"
                                                id="valor_{{ $concepto->id }}" value="{{ $concepto->valor }}">
                                        </td>
                                    </tr>
                                @endforeach
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th></th>
                                    <th>TOTAL</th>
                                    <th><input type="number" step="0.01" class="form-control" id="total_concepto" value=""></th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </fieldset>

            <!-- Botón de envío -->
            <button id="btn-guardar" class="btn btn-primary" type="button">
                <span id="spinner" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                Actualizar impuesto
            </button>

             <button id="btn-cancelar" class="btn btn-warning" type="button" onclick="cancelarActualizacion()">
               
                Cancelar
            </button>
            <br>
            <br>
        </form>
    </div>

    <div class="modal fade" id="modalCrearEnte" tabindex="-1" aria-labelledby="modalCrearEnteLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg"> <!-- ancho grande -->
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalPersonaLabel">Formulario Persona</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    <form id="formPersona" autocomplete="off">
                        <div class="row g-3">

                            <div class="col-md-6">
                                <label for="es_persona" class="form-label">Tipo Persona <span
                                        class="text-danger">*</span></label>
                                <select class="form-select" id="es_persona" name="es_persona">
                                    <option value="1">Persona Natural</option>
                                    <option value="0">Persona Jurídica</option>
                                </select>
                                <div class="invalid-feedback" id="error-es_persona"></div>
                            </div>

                            <div class="col-md-6">
                                <label for="ci_ruc" class="form-label">CI / RUC <span class="text-danger">*</span></label>
                                <input type="hidden" class="form-control" id="es_transito" name="es_transito" value="S">
                                <input type="number" class="form-control" id="ci_ruc" name="ci_ruc" onblur="capturaInfoPersona()" onkeyup="bloqueaInpust(this)">
                                <div class="invalid-feedback" id="error-ci_ruc"></div>
                            </div>
                            <div class="col-md-6">
                                <label for="apellidos" class="form-label" id="label-apellidos">Apellidos <span
                                        class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="apellidos" name="apellidos" autocomplete="of">
                                <div class="invalid-feedback" id="error-apellidos"></div>
                            </div>
                            <div class="col-md-6">
                                <label for="nombres" class="form-label" id="label-nombres">Nombres <span
                                        class="text-danger">*</span></label>

                                <input type="text" class="form-control" id="nombres" name="nombres" autocomplete="of">
                                <div class="invalid-feedback" id="error-nombres"></div>
                            </div>
                           
                            <div class="col-md-6">
                                <label for="direccion" class="form-label">Dirección </label>
                                <input type="text" class="form-control" id="direccion" name="direccion" autocomplete="of">
                                <div class="invalid-feedback" id="error-direccion"></div>
                            </div>
                            <div class="col-md-6">
                                <label for="fecha_nacimiento" class="form-label">Fecha de Nacimiento </label>
                                <input type="date" class="form-control" id="fecha_nacimiento" name="fecha_nacimiento" autocomplete="of">
                                <div class="invalid-feedback" id="error-fecha_nacimiento"></div>
                            </div>
                            <div class="col-md-6">
                                <label for="correo" class="form-label">Correo </label>
                                <input type="email" class="form-control" id="correo" name="correo" autocomplete="of">
                                <div class="invalid-feedback" id="error-correo"></div>
                            </div>
                            <div class="col-md-6">
                                <label for="telefono" class="form-label">Teléfono </label>
                                <input type="number" class="form-control" id="telefono" name="telefono" autocomplete="of">
                                <div class="invalid-feedback" id="error-telefono"></div>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    
                    <button type="button" class="btn" id="btn_guarda_act_persona" onclick="guardarPersona()">
                        <span id="nombre_btn_persona"></span>
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>


    <div class="modal fade" id="vehiculoModal" tabindex="-1" aria-labelledby="vehiculoModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg"> <!-- Aumentamos el tamaño del modal -->
            <form id="vehiculoForm" autocomplete="off">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Formulario Vehículo</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row g-3">
                            <!-- Columna 1 -->
                            <div class="col-md-6">

                                <div class="mb-3">
                                    <label for="placa_v" class="form-label">Tipo Identificacipn</label>
                                    <select class="form-select {{ $errors->has('tipo_ident') ? 'is-invalid' : '' }}" id="tipo_ident"
                                        name="tipo_ident" required onchange="cambioTipoidentif()">
                                        <!-- <option value="">Seleccione tipo</option> -->
                                        <option value="PLACA">PLACA</option>
                                        <option value="CPN">CPN</option>
                                        <option value="RAMV">RAMV</option>

                                    </select>
                                    <div class="invalid-feedback" id="error-placa_v"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="chasis_v" class="form-label">Chasis</label>
                                    <input type="text" class="form-control" id="chasis_v" name="chasis_v" required >
                                    <div class="invalid-feedback" id="error-chasis_v"></div>
                                </div>
                                <div class="mb-3">
                                    <label for="avaluo_v" class="form-label">Avalúo ($)</label>
                                    <input type="number" step="0.01" class="form-control" id="avaluo_v" name="avaluo_v"
                                        required>
                                    <div class="invalid-feedback" id="error-avaluo_v"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="tipo_v" class="form-label">Clase de vehiculo</label>
                                    <select class="form-select {{ $errors->has('tipo_v') ? 'is-invalid' : '' }}" id="clase_tipo_v"
                                        name="clase_tipo_v" required onchange="seleccionaTipoVeh()">

                                    </select>
                                    <div class="invalid-feedback" id="error_clasetipo_v"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="placa_v" class="form-label">Tipo de servicio</label>
                                    <select class="form-select {{ $errors->has('tipo_vehiculo') ? 'is-invalid' : '' }}" id="tipo_vehiculo"name="tipo_vehiculo" required >
                                        <option value="">Seleccione un tipo servicio </option>
                                        <option value="PUBLICO">PUBLICO</option>
                                        <option value="PARTICULAR">PARTICULAR</option>
                                        <option value="COMERCIAL">COMERCIAL</option>
                                        
                                    </select>
                                    <div class="invalid-feedback" id="error-placa_v"></div>
                                </div>
                            </div>

                            <!-- Columna 2 -->
                            <div class="col-md-6">

                                <div class="mb-3">
                                    <label for="placa_v" class="form-label label_plac_cpn_ramv" >Placa</label>
                                    <input type="text" class="form-control" id="placa_v" name="placa_v" required onblur="capturaInfoVehiculo()" onkeyup="convertirMayuscula(this)">
                                    <div class="invalid-feedback" id="error-placa_v"></div>
                                </div>


                                <div class="mb-3">
                                    <label for="year_v" class="form-label">Año Modelo</label>
                                    <input type="number" class="form-control" id="year_v" name="year_v" min="1900"
                                        max="2100" required>
                                    <div class="invalid-feedback" id="error-year_v"></div>
                                </div>
                                <div class="mb-3">
                                    <label for="marca_v" class="form-label">Marca</label>
                                    <select class="form-select {{ $errors->has('marca_v') ? 'is-invalid' : '' }}"
                                        id="marca_v" name="marca_v" required>
                                       
                                    </select>
                                    <div class="invalid-feedback" id="error-marca"></div>
                                </div>

                                <div class="mb-3">
                                    <label for="tipo_v" class="form-label">RTV Cuadro Tarifario</label>
                                    <select class="form-select {{ $errors->has('tipo_v') ? 'is-invalid' : '' }}" id="tipo_v"
                                        name="tipo_v" required >
                                        
                                    </select>
                                    <div class="invalid-feedback" id="error_tipo_v"></div>
                                </div>
                                
                            </div>
                        </div> <!-- End row -->
                    </div>

                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success" id="btn_guarda_act_vehiculo">
                            <span id="nombre_btn_vehiculo"></span>
                        </button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </form>
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
        tablaImpuesto = $("#tablaImpuesto").DataTable({
            "lengthMenu": [ 10,20],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/transito-edicion/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'cc_ruc'},
                {width: '',data: 'contribuyente', name: 'contribuyente'},
                {width: '',data: 'vehiculo'},
                {width: '',data: 'numero_titulo'},
                {width: '',data: 'created_at'},
                {width: '',data: 'total_pagar'},
               
            ],
            "columnDefs": [
                { targets: [6], className: "text-end" } // <-- alinea la columna 5 a la derecha
            ],
            "fixedColumns" : true
        });
    })

    function generarPdf1(id){
       
        vistacargando("m","Espere por favor")
        $.get("transito-imprimir/"+id+'/G', function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            verpdf(data.pdf)
            console.log(data)
            
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    function verpdf(ruta){
        
        var iframe=$('#iframePdf');
        iframe.attr("src", "../patente/documento/"+ruta);   
        $("#vinculo").attr("href", '../patente/descargar-documento/'+ruta);
        $("#documentopdf").modal("show");
    }
    globalThis.CargarPagina=0
    $('#documentopdf').on('hidden.bs.modal', function () {
       if(CargarPagina==1){
            $("#documentopdf").modal("hide");
            location.reload();
        }
    });

    function eliminarTitulo(id){
        $('#tituloEliminaModal').modal('show')
        $('#id_impuesto').val(id)
    }
    function procesaEliminacion(id){
        vistacargando("m","Espere por favor")
        $.get("../elimina-titulo-transito/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            verpdf(data.pdf)
            console.log(data)
            
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    $("#form_baja").submit(function(e){
        e.preventDefault();
        
        //validamos los campos obligatorios
        let motivo_baja=$('#motivo_baja').val()
        let id_impuesto=$('#id_impuesto').val()
    
        if(motivo_baja=="" || motivo_baja==null){
            alertNotificar("Debe ingresar el motivo","error")
            $('#motivo_baja').focus()
            return
        } 

        if(confirm('¿Estas seguro que quieres dar de baja al titulo?'))
        {
            vistacargando("m","Espere por favor")
            $.ajaxSetup({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                }
            });

            //comprobamos si es registro o edicion
            let tipo="POST"
            let url_form="../baja-titulo-transito"
            var FrmData=$("#form_baja").serialize();

            $.ajax({
                    
                type: tipo,
                url: url_form,
                method: tipo,             
                data: FrmData,      
                
                processData:false, 

                success: function(data){
                    vistacargando("");                
                    if(data.error==true){
                        alertNotificar(data.mensaje,'error');
                        return;                      
                    }
                
                    alertNotificar(data.mensaje,"success");
                    cancelarBaja()
                    setTimeout(function() {
                        location.href = location.href;
                    }, 1000);
                                    
                }, error:function (data) {
                    console.log(data)

                    vistacargando("");
                    alertNotificar('Ocurrió un error','error');
                }
            });
        }
    })

    function cancelarBaja(){
        $('#motivo_baja').val('')
        $('#id_impuesto').val('')
        $('#tituloEliminaModal').modal('hide')
    }

    function cobrarTitulo(id){
        $("#tablaImpuestoDetalle tbody").html('');
        $('#tablaImpuestoDetalle tbody').empty();
        
        $('#id_impuesto').val(id)

        vistacargando("m","Espere por favor")
        $.get("../detalle-titulo/"+id, function(data){
            console.log(data)
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            let avaluo=parseFloat(data.info[0].vehiculo.avaluo)
           
            $('#anio_proceso').html(data.info[0].TransitoImpuesto.year_impuesto)

            $('#persona').html(data.info[0].cliente.apellidos+" "+data.info[0].cliente.nombres)
            $('#ci_persona').html(data.info[0].cliente.ci_ruc)

            $('#placa_det').html(data.info[0].vehiculo.placa_cpn_ramv)
            $('#chasis_det').html(data.info[0].vehiculo.chasis)
            $('#avaluo_det').html('$'+avaluo.toFixed(2))
            $('#modelo_det').html(data.info[0].vehiculo.year)
            $('#clase_tipo_det').html(data.info[0].vehiculo.clase_vehiculo.descripcion)
            $('#marca_det').html(data.info[0].vehiculo.marca.descripcion)

            $('#generado').html(data.persona.nombre)
            $('#fecha_generado').html(data.info[0].TransitoImpuesto.created_at)

            
            
            $("#tablaImpuestoDetalle tbody").html('');
            
            $.each(data.data,function(i, item){
                var concepto=item.concepto;
                if(item.codigo=='RTV'){
                    // concepto=item.concepto + ' ( '+item.agrupado.join(', ')+' )';
                    concepto=item.concepto + ' ( '+data.info[0].vehiculo.tipo_vehiculo.descripcion+' )';
                }

                if(item.codigo=='REC' && data.info[0].TransitoImpuesto.calendarizacion!=null){
                    concepto=item.concepto + ' (Calendarizacion '+data.info[0].TransitoImpuesto.calendarizacion+')';
                }

                $('#tablaImpuestoDetalle').append(`<tr>
                                                <td style="width:75%; text-align:left; vertical-align:middle">
                                                    ${concepto}
                                                </td>
                                                <td style="width:25%; text-align:right; vertical-align:middle">
                                                    ${item.pivot.valor}
                                                </td>
                                        </tr>`);
            })

            // Agregar el tfoot con el total
            $('#tablaImpuestoDetalle tfoot').remove(); // Por si ya existe uno previo

            $('#tablaImpuestoDetalle').append(`
                <tfoot>
                    <tr>
                        <td style="text-align:right; font-weight:bold">TOTAL</td>
                        <td style="text-align:right; font-weight:bold">${data.info[0].TransitoImpuesto.total_pagar}</td>
                    </tr>
                </tfoot>
            `);
            $('#DetalleTitulo').modal('show')
                        
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    function cerrarCobro(){
        $('#DetalleTitulo').modal('hide')
    }

    function registrarCobro(){
        var id=$('#id_impuesto').val()
        //alert(id)
       
        if(confirm('¿Estas seguro que quieres realizar el cobro?'))
        {
            
            vistacargando("m","Espere por favor")
            $.get("../registrar-cobro-transito/"+id, function(data){
                vistacargando("")
                if(data.error==true){
                    alertNotificar(data.mensaje,"error");
                    return;   
                }
                cerrarCobro()
                alertNotificar(data.mensaje,"success");
                CargarPagina=1
                verpdf(data.pdf)

               
                // location.reload();
            }).fail(function(){
                vistacargando("")
                alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
            });
        }
    }

    function anularCobro(){
        var id=$('#id_impuesto').val()
       
        if(confirm('¿Estas seguro que quieres anular el registro?'))
        {
           
            vistacargando("m","Espere por favor")
            $.get("../anular-cobro-transito/"+id, function(data){
                vistacargando("")
                if(data.error==true){
                    alertNotificar(data.mensaje,"error");
                    return;   
                }
                alertNotificar(data.mensaje,"success");
                
                setTimeout(() => {
                    cerrarCobro()
                    location.reload();
                }, 2000);
                
               
            }).fail(function(){
                vistacargando("")
                alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
            });
        }
    }
    function printIframe() {
        const iframe = document.getElementById('iframePdf');
        iframe.contentWindow.focus();
        iframe.contentWindow.print();
    }

    function editarCobro(){
       
        var id=$('#id_impuesto').val()
        
        vistacargando("m","Espere por favor")
        $.get("../editar-cobro-transito/"+id, function(data){
            console.log(data)
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            if(data.resultado==null){
                alertNotificar('La informacion ya no se encuentra disponible de actualizar',"error");
                return; 
            }
            
            $('#DetalleTitulo').modal('hide')
            $('.h2').html('Edicion de Impuesto Transito')

            $('#seccion_listado').hide()
            $('#seccion_edicion').show()

            $('#cliente_id').val(data.resultado.cliente.ci_ruc)
            $('#vehiculo_id').val(data.resultado.vehiculo.placa_cpn_ramv)

            $('#id_impuesto_editar').val(id)

            let nombrescliente = data.resultado.cliente.apellidos + ' ' + data.resultado.cliente.nombres;
            let correocliente = data.resultado.cliente.correo ?? 'S/N';
            let telefonocliente = data.resultado.cliente.telefono ?? 'S/N';
            let direccioncliente = data.resultado.cliente.direccion ?? 'S/N';
            let fechanacimientocliente = data.resultado.cliente.fecha_nacimiento ?? 'S/N';
            let cliente_id_2 = data.resultado.cliente.id ?? 0;

            $('#nombrescliente').val(nombrescliente)
            $('#correocliente').val(correocliente)
            $('#telefonocliente').val(telefonocliente)
            $('#direccioncliente').val(direccioncliente)
            $('#fechanacimientocliente').val(fechanacimientocliente)
            $('#cliente_id_2').val(cliente_id_2)

            let chasis = data.resultado.vehiculo.chasis ?? 'S/N';
            let tipo = data.resultado.vehiculo.Tipo ?? 'S/N';
            let clase_tipo = data.resultado.vehiculo.clase_vehiculo.descripcion ?? 'S/N';                            
            let tipo_vehi = data.resultado.vehiculo.tipo_vehi ?? 'S/N';
            let year_modelo= data.resultado.vehiculo.year ?? 'S/N';
            let avaluo= data.resultado.vehiculo.avaluo ?? 'S/N';
            let marca = data.resultado.vehiculo.Marcav ?? 'S/N';
            let vehiculo_id_2 = data.resultado.vehiculo.id ?? "";

            $('#avaluo').val(avaluo)
            $('#chasis').val(chasis)
            $('#year_modelo').val(year_modelo)
            $('#marca').val(marca)
            $('#clase_tipo').val(clase_tipo)
            $('#tipo').val(tipo)
            $('#tipo_vehi').val(tipo_vehi)
            
            $('#vehiculo_id_2').val(vehiculo_id_2)
          
            $('#last_year_declaracion').val(data.resultado.last_year_declaracion)
            if(data.resultado.solo_duplicado != null){              
                $('#solo_duplicado').val(data.resultado.solo_duplicado)
            }

           

            $.each(data.resultado.conceptos,function(i, item){
                if(item.codigo=='IAV'){
                    $('#check_valor_IAV').prop('checked',true)
                }

                if(item.codigo=='RTV'){
                    $('#check_valor_RTV').prop('checked',true)
                }

                if(item.codigo=='SRV'){
                    $('#check_valor_SRV').prop('checked',true)
                }

                if(item.codigo=='TSA'){
                    $('#check_valor_TSA').prop('checked',true)
                }

                if(item.codigo=='DM'){
                    $('#check_valor_DM').prop('checked',true)
                }

                if(item.codigo=='REC'){
                    $('#check_valor_REC').prop('checked',true)
                }

                if(item.codigo=='DE'){
                    $('#check_valor_DE').prop('checked',true)
                }
            
            })

            setTimeout(() => {
                calcularImpuesto()
            }, 1000);
            


            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    function cancelarActualizacion(){
        window.location.reload();
    }

    document.getElementById('vehiculoForm').addEventListener('submit', function (e) {
            e.preventDefault();

            document.querySelectorAll('.invalid-feedback').forEach(el => el.innerText = '');
            document.querySelectorAll('.form-control, .form-select').forEach(el => el.classList.remove('is-invalid'));

            const formData = new FormData(this);

            axios.post('{{ route("store.vehiculo") }}', formData)
                .then(response => {
                    console.log(response)
                    // alert('Vehículo registrado correctamente');
                    alertNotificar(response.data.mensaje,"success")
                    document.getElementById('vehiculoForm').reset();
                    const modal = bootstrap.Modal.getInstance(document.getElementById('vehiculoModal'));
                    modal.hide();
                })
                .catch(error => {
                    if (error.response.status === 422) {
                        const errors = error.response.data.errors;
                        for (let field in errors) {
                            const input = document.getElementById(field);
                            const errorDiv = document.getElementById(`error-${field}`);
                            if (input) input.classList.add('is-invalid');
                            if (errorDiv) errorDiv.innerText = errors[field][0];
                        }
                    }
                });
        });

        function guardarPersona() {
            
            const form = document.getElementById('formPersona');
            var ci_ruc=$('#ci_ruc').val()
            if(ci_ruc=="" || ci_ruc==null){
                alertNotificar("Ingrese el numero de cedua o ruc","error")
                return
            }
            var cant=$('#ci_ruc').val().length;
          
            if(cant!=10 && cant!=13){
                alertNotificar("El numero de Identificacion debe tener 10 o 13 digitos", "error")
                return
            }
            
            const formData = new FormData(form);

            // Limpiar errores previos
            form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
            form.querySelectorAll('.invalid-feedback').forEach(el => el.innerText = '');

            axios.post('{{ route("store.ente.sgmapp") }}', formData)
                .then(response => {
                    console.log(response)
                     console.log(response.data)
                    if(response.status==500){
                        alertNotificar(response.data.message,"error")
                        return
                    }
                    // alert('Persona registrada correctamente');
                    alertNotificar(response.data.message,"success")
                    document.getElementById('formPersona').reset();
                    const modal2Element = document.getElementById('modalCrearEnte');
                    const modal2 = bootstrap.Modal.getInstance(modal2Element) || new bootstrap.Modal(modal2Element);
                    modal2.hide();
                })
                .catch(error => {
                    console.log(error.response);
                    if(error.response.data.error==true){
                        alertNotificar(error.response.data.message,"error")
                        return                    }
                 
                    if (error.response && error.response.status === 422) {
                        const errores = error.response.data.errors;
                        for (const campo in errores) {
                            const input = document.getElementById(campo);
                            const errorDiv = document.getElementById(`error-${campo}`);
                            if (input) {
                                input.classList.add('is-invalid');
                            }
                            if (errorDiv) {
                                errorDiv.innerText = errores[campo][0];
                            }
                        }
                    }
                });
        }

        $('#check_valor_TSA').prop('disabled', true)
        let token = "{{csrf_token()}}";
        document.getElementById('vehiculo_id').addEventListener('keypress', function (event) {
            // Verificar si la tecla presionada es 'Enter' (keyCode 13)
            if (event.key === 'Enter') {
                // Obtener el valor del input
                event.preventDefault();
                let query = event.target.value;
                const spinner = document.getElementById('spinner');
                spinner.style.display = 'inline-block';
                let chasis = document.getElementById('chasis');
                let tipo = document.getElementById('tipo');
                let clase_tipo = document.getElementById('clase_tipo');
                let tipo_vehi = document.getElementById('tipo_vehi');
                let year_modelo = document.getElementById('year_modelo');
                let avaluo = document.getElementById('avaluo');
                let marca = document.getElementById('marca');
                let vehiculo_id_2 = document.getElementById('vehiculo_id_2');


                // Asegurarte que no esté vacío
                if (query.trim() !== '') {
                    axios.post('{{route('get.placa.transitovehiculo')}}', {
                        _token: token,
                        query: query
                    }).then(function (res) {
                        //propietario.value = res.data.ci_ruc;
                        if (res.status == 200) {
                            console.log(res)
                            chasis.value = res.data.chasis ?? 'S/N';
                            tipo.value = res.data.Tipo ?? 'S/N';
                            clase_tipo.value = res.data.clase_vehiculo.descripcion ?? 'S/N';                            
                            tipo_vehi.value = res.data.tipo_vehi ?? 'S/N';
                            year_modelo.value = res.data.year ?? 'S/N';
                            avaluo.value = res.data.avaluo ?? 'S/N';
                            marca.value = res.data.Marcav ?? 'S/N';
                            vehiculo_id_2.value = res.data.id ?? "";
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function (err) {
                        console.log(err)
                        if (err.request.status == 500) {
                            console.log('error al consultar al servidor');
                        }
                        if (err.request.status == 404) {
                            let errorResponseS = JSON.parse(err.request.response);

                            chasis.value = errorResponseS.message;
                            tipo.value = errorResponseS.message;
                            clase_tipo.value = errorResponseS.message;
                            year_modelo.value = errorResponseS.message;
                            avaluo.value = errorResponseS.message;
                            marca.value = errorResponseS.message;
                            vehiculo_id_2.value = "";
                        }
                        if (err.request.status == 419) {
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
                } else {
                    spinner.style.display = 'none';
                }
                

                setTimeout(function() {

                    let nombres=$('#nombrescliente').val()
                    let avaluo_aux=$('#avaluo').val()
                    
                    if(avaluo_aux=="No encontrado"){
                        avaluo_aux=""
                    }
                    if(nombres!="" && avaluo_aux!=""){
                        calcularImpuesto()                   
                    }
                }, 3000);

               
                    
            }
        });
        document.getElementById('cliente_id').addEventListener('keypress', function (event) {
            // Verificar si la tecla presionada es 'Enter' (keyCode 13)
            if (event.key === 'Enter') {
                // Obtener el valor del input
                event.preventDefault();
                let query = event.target.value;
                const spinner = document.getElementById('spinner');
                spinner.style.display = 'inline-block';
                let nombrescliente = document.getElementById('nombrescliente');
                let telefonocliente = document.getElementById('telefonocliente');
                let correocliente = document.getElementById('correocliente');
                let direccioncliente = document.getElementById('direccioncliente');
                let fechanacimientocliente = document.getElementById('fechanacimientocliente');
                let cliente_id_2 = document.getElementById('cliente_id_2');


                // Asegurarte que no esté vacío
                if (query.trim() !== '') {
                    axios.post('{{route('get.cedula.transitoente')}}', {
                        _token: token,
                        query: query
                    }).then(function (res) {
                        console.log(res)
                        //propietario.value = res.data.ci_ruc;
                        if (res.status == 200) {
                            nombrescliente.value = res.data.apellidos + ' ' + res.data.nombres;
                            correocliente.value = res.data.correo ?? 'S/N';
                            telefonocliente.value = res.data.telefono ?? 'S/N';
                            direccioncliente.value = res.data.direccion ?? 'S/N';
                            fechanacimientocliente.value = res.data.fecha_nacimiento ?? 'S/N';
                            cliente_id_2.value = res.data.id ?? 0;
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function (err) {
                        console.log(err)
                        if (err.request.status == 500) {
                            console.log('error al consultar al servidor');
                        }
                        if (err.request.status == 404) {
                            alertNotificar("No se encontro la persona","error")
                            let errorResponse = JSON.parse(err.request.response);
                            // nombrescliente.value = errorResponse.message;
                            // nombrescliente.value = errorResponse.message;
                            // correocliente.value = errorResponse.message;
                            // telefonocliente.value = errorResponse.message;
                            // direccioncliente.value = errorResponse.message;
                            // fechanacimientocliente.value = errorResponse.message;

                            nombrescliente.value = "";
                            nombrescliente.value = "";
                            correocliente.value = "";
                            telefonocliente.value = "";
                            direccioncliente.value ="";
                            fechanacimientocliente.value = "";
                            cliente_id_2.value = "";
                        }
                        if (err.request.status == 419) {
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
                } else {
                    spinner.style.display = 'none';
                }
            }
        });
        document.getElementById('btn-calcular').addEventListener('click', function (e) {
            e.preventDefault(); // Previene que el formulario se envíe si está dentro de uno

            const conceptos = [];
            const token = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
            const vehiculo_id = document.getElementById('vehiculo_id_2').value;
            const cliente_id = document.getElementById('cliente_id_2').value;
            const year = document.getElementById('year_declaracion').value;
            const last_year_declaracion = document.getElementById('last_year_declaracion').value;
            const solo_duplicado = document.getElementById('solo_duplicado').value;
            const tipo_serv = document.getElementById('tipo_vehi').value;
            const year_modelo = document.getElementById('year_modelo').value;

            const currentYear = new Date().getFullYear();
            let menos3=currentYear-3
            
            if(tipo_serv=="PARTICULAR" && year_modelo > menos3){
              
                $('#check_valor_RTV').prop('checked',false)
            }else{
                if(solo_duplicado=='no'){
                    $('#check_valor_RTV').prop('checked',true)
                }
            }

            // alert(year)

            if (!vehiculo_id || !cliente_id || !year) {
                alert('Por favor llene los campos: vehiculo, cliente y Año de impuesto');
                return;
            }

            // Mostrar spinner en el botón
            document.getElementById('spinner-btn').classList.remove('d-none');
            document.getElementById('btn-text').textContent = 'Calculando...';

            document.querySelectorAll('.concepto-check:checked').forEach(checkbox => {
                const id = checkbox.getAttribute('data-id');
                const valor = parseFloat(document.getElementById(`valor_${id}`).value) || 0;
                conceptos.push({ id, valor });
            });

            
            if (conceptos.length > 0) {
                
                const spinner = document.getElementById('spinner-total');
                if (spinner) spinner.style.display = 'inline-block';

                axios.post('{{ route("calcular.transito") }}', {
                    _token: token,
                    conceptos: conceptos,
                    vehiculo_id: vehiculo_id,
                    cliente_id: cliente_id,
                    year: year,
                    last_year_declaracion: last_year_declaracion,
                    solo_duplicado: solo_duplicado,
                    tipo_serv:tipo_serv
                }).then(function (res) {
                    console.log("txt")
                    console.log(res)

                    if (res.status === 200) {
                        // Reemplazar los valores en los inputs
                        let total = 0;
                        res.data.conceptos.forEach(function (concepto) {
                            console.log(concepto)
                            const input = document.getElementById('valor_' + concepto.id);
                            if (input) {
                                console.log(input)
                                input.value = concepto.nuevo_valor.toFixed(2);
                                // var valor = Number(concepto.nuevo_valor)
                                // input.value = valor.toFixed(2);
                                total += parseFloat(concepto.nuevo_valor);
                            }

                            
                        });

                        
                        // $('#check_valor_RTV').prop('checked',true)
                        // $('#check_valor_IAV').prop('checked',true)
                        // $('#check_valor_SRV').prop('checked',true)
                        // $('#check_valor_DM').prop('checked',false)
                        // $('#check_valor_DE').prop('checked',false)
                        // $('#check_valor_TSA').prop('readonly',true)
                        // $('#check_valor_RTV').prop('checked',true)


                        // if(res.data.desmarca_rtv=="S"){
                        //    $('#check_valor_RTV').prop('checked',false)
                        // }else{
                        //     $('#check_valor_RTV').prop('checked',true)
                        // }
                       
                        if(solo_duplicado=='matricula'){
                            
                            $('#check_valor_IAV').prop('checked',false)
                            $('#check_valor_RTV').prop('checked',false)
                            $('#check_valor_SRV').prop('checked',false)
                            $('#check_valor_DE').prop('checked',false)
                            $('#check_valor_RTV').prop('checked',false)
                            $('#check_valor_REC').prop('checked',false)
                            $('#check_valor_DM').prop('checked',true)
                        }

                        if(solo_duplicado=='sticker'){
                           
                            $('#check_valor_IAV').prop('checked',false)
                            $('#check_valor_RTV').prop('checked',false)
                            $('#check_valor_SRV').prop('checked',false)
                            $('#check_valor_DM').prop('checked',false)
                            $('#check_valor_RTV').prop('checked',false)
                            $('#check_valor_REC').prop('checked',false)
                            $('#check_valor_DE').prop('checked',true)
                        }

                        // Mostrar el total con dos decimales
                        document.getElementById('total_concepto').value = total.toFixed(2);
                        console.log(total);
                    }
                    if (spinner) spinner.style.display = 'none';
                }).catch(function (err) {
                    console.log(err)
                    if (err.request.status === 500) {
                        console.log('Error al consultar al servidor.');
                    }
                    if (err.request.status === 419) {
                        console.log('Sesión caducada, vuelve a iniciar sesión.');
                    }
                    if (spinner) spinner.style.display = 'none';
                }).finally(function () {
                    // Aquí se detiene el spinner y se restaura el texto del botón
                    document.getElementById('spinner-btn').classList.add('d-none');
                    document.getElementById('btn-text').textContent = 'Calcular';
                });;

                // 2. Poner en 0.00 los NO seleccionados
            document.querySelectorAll('.concepto-check:not(:checked)').forEach(checkbox => {
                const id = checkbox.getAttribute('data-id');
                const input = document.getElementById(`valor_${id}`);
                input.value = '0.00';
            });

            } else {
                alert('Selecciona al menos un concepto para calcular.');
            }
        });


        document.getElementById('btn-guardar').addEventListener('click', function () {

            let nombres=$('#nombrescliente').val()
            let avaluo_aux=$('#avaluo').val()
                
            if(avaluo_aux=="No encontrado"){
                avaluo_aux=""
            }

            if(nombres=="" || avaluo_aux==""){
                alertNotificar("Selecccione un Cliente y/o Vehiculo existente","error")
                return
            }

            // limpiar errores
            ['vehiculo_id', 'cliente_id', 'year_declaracion'].forEach(id => {
                const input = document.getElementById(id);
                const errorDiv = document.getElementById('error_' + id);

                if (input) {
                    input.addEventListener('input', limpiarError);
                    input.addEventListener('change', limpiarError); // para tipo number y selects

                    function limpiarError() {
                        input.classList.remove('is-invalid');
                        if (errorDiv) errorDiv.textContent = '';
                    }
                }
            });

            const id_impuesto_editar=$('#id_impuesto_editar').val()
            const vehiculo_id_2 = document.getElementById('vehiculo_id_2').value;
            const cliente_id_2 = document.getElementById('cliente_id_2').value;
            const year_declaracion = document.getElementById('year_declaracion').value;
            const last_year_declaracion = document.getElementById('last_year_declaracion').value;
            const solo_dupli = document.getElementById('solo_duplicado').value;
            const btn = document.getElementById('btn-guardar');
            const spinner = btn.querySelector('.spinner-border');
            btn.disabled = true;
            spinner.classList.remove('d-none');

            const conceptosSeleccionados = [];

            document.querySelectorAll('.concepto-check:checked').forEach(function (checkbox) {
                const id = checkbox.dataset.id;
                const valor = document.getElementById('valor_' + id).value;
                conceptosSeleccionados.push({
                    id: id,
                    valor: valor
                });
            });

            // Validación simple: debe haber al menos uno
            if (conceptosSeleccionados.length === 0) {
                alert('Debes seleccionar al menos un concepto.');
                // En el then o catch
                btn.disabled = false;
                spinner.classList.add('d-none');
                return;
            }

            axios.post('{{ route("actualizar.transito") }}', {
                _token: '{{ csrf_token() }}',
                conceptos: conceptosSeleccionados,
                vehiculo_id_2: vehiculo_id_2,
                cliente_id_2: cliente_id_2,
                year_declaracion: year_declaracion,
                last_year_declaracion: last_year_declaracion,
                solo_dupli:solo_dupli,
                id_impuesto_editar:id_impuesto_editar
            })
                .then(function (res) {
                    // limpiar errores
                    ['vehiculo_id', 'cliente_id', 'year_declaracion','last_year_declaracion'].forEach(id => {
                        const input = document.getElementById(id);
                        const errorDiv = document.getElementById('error_' + id);

                        if (input) {
                            input.addEventListener('input', limpiarError);
                            input.addEventListener('change', limpiarError); // para tipo number y selects

                            function limpiarError() {
                                input.classList.remove('is-invalid');
                                if (errorDiv) errorDiv.textContent = '';
                            }
                        }
                    });
                    if (res.status == 200) {

                        if(res.data.error==true){
                            alertNotificar(res.data.mensaje,"error")
                            return
                        }
                        const id = res.data.id;
                        generarPdf(id)

                    }
                    console.log(res);
                    // console.log(response.data.id)
                    //const id = response.data.id; // Suponiendo que el id está en la respuesta
                    // Redirigir a la página con el id obtenido
                    //window.location.href = `/transito/previsualizar/${id}`; // Laravel buscará esta ruta con el id
                })
                .catch(function (err) {
                    if (err.response && err.response.status === 422) {
                        const errores = err.response.data.errors;

                        // Mapear campos internos a visibles
                        const campoVisible = {
                            vehiculo_id_2: 'vehiculo_id',
                            cliente_id_2: 'cliente_id',
                            year_declaracion: 'year_declaracion',
                            last_year_declaracion:'last_year_declaracion'
                        };

                        Object.keys(errores).forEach(function (campo) {
                            const visibleCampo = campoVisible[campo] || campo;
                            const input = document.getElementById(visibleCampo);
                            const errorDiv = document.getElementById('error_' + visibleCampo);

                            if (input && errorDiv) {
                                input.classList.add('is-invalid');
                                errorDiv.textContent = errores[campo][0];
                            }
                        });

                    } else {
                        alert('Error al guardar los datos.');
                    }
                })
                .finally(function () {
                    // En el then o catch
                    btn.disabled = false;
                    spinner.classList.add('d-none');
                });
        });

</script>
<script src="{{ asset('js/transito/edicion_impuesto.js?v=' . rand())}}"></script>
@endpush
