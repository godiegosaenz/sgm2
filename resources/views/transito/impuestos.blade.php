@extends('layouts.appv2')
@section('title', 'Transito')
@push('styles')
    <link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
    <link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
    <style>
        tfoot input {
            width: 100%;
            padding: 3px;
            box-sizing: border-box;
        }
    </style>
@endpush
@section('content') 
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h3 class="h2">Impuestos Unidad de Transito</h3>
        <div class="btn-toolbar mb-2 mb-md-0">
            <div class="btn-group me-2">
                <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal"
                    data-bs-target="#modalEditarRangos"> <i class="bi bi-table"></i> Impuesto Rodaje Municipal </button>
                <button type="button" class="btn btn-sm btn-outline-secondary" onclick="abrirModalMantenimiento()"><i
                        class="bi bi-gear-fill"></i> Conf</button>
            </div>
            <button type="button" class="btn btn-sm btn-outline-secondary d-flex align-items-center">
                <i class="bi bi-info-circle"></i>
            </button>
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
            "Por favor, revisa los campos obligatorios y corrige los errores indicados para poder continuar."
        </div>
    @endif
    <form method="POST" action="{{ route('store.transito') }}">
        @csrf
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-5">Datos de cliente</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="propietario" class="form-label">Busqueda por cedula <span
                            class="text-danger">*</span></label>
                    <div class="input-group">
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
                    <label for="nombresRepresentante" class="form-label">Cuadro Tarifario RTV</label>
                    <input type="text" class="form-control" id="tipo" maxlength="255"
                        value="{{old('nombresRepresentante2')}}" disabled>

                </div>
            </div>
        </fieldset>

        <!-- Conceptos -->
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-5">Detalle de impuesto</legend>
            <div class="row align-items-end mb-3">

                <div class="col-md-4">
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

                <div class="col-md-4">
                    <label for="nombresRepresentante" class="form-label">Ultimo Año Matriculacion</label>
                    <select class="form-select {{ $errors->has('last_year_declaracion') ? 'is-invalid' : '' }}"
                        id="last_year_declaracion" name="last_year_declaracion" onchange="calcularImpuesto()">
                        @for ($i = 1; $i <= 15; $i++)
                            <option value="{{ date('Y') - $i }}">{{ date('Y') - $i }}</option>
                        @endfor
                    </select>
                    <div class="invalid-feedback" id="error_last_year_declaracion"></div>
                </div>

                <div class="col-md-4 d-grid gap-2">
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
            Registrar impuesto
        </button>
        <br>
        <br>
    </form>
    <!-- Modal para cliente -->

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
                                <label for="nombres" class="form-label" id="label-nombres">Nombres <span
                                        class="text-danger">*</span></label>

                                <input type="text" class="form-control" id="nombres" name="nombres" autocomplete="of">
                                <div class="invalid-feedback" id="error-nombres"></div>
                            </div>
                            <div class="col-md-6">
                                <label for="apellidos" class="form-label" id="label-apellidos">Apellidos <span
                                        class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="apellidos" name="apellidos" autocomplete="of">
                                <div class="invalid-feedback" id="error-apellidos"></div>
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

    <!-- Modal vehiculo-->
    <!-- Modal -->
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
                                    <label for="tipo_v" class="form-label">Cuadro Tarifario RTV</label>
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
    <!-- Modal -->
    <div class="modal fade" id="modalEditarRangos" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Impuesto Rodaje Municipal del año {{ date('Y') }}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">

                    <form method="POST" action="" id="form_base">
                        @csrf

                        <div class="col-md-12">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Desde</label>
                                </div>
                                <div class="col-md-7">
                                    <input type="hidden" class="form-control" id="id_base" name="id_base">
                                    <input type="number" step="0.01" class="form-control" id="desde_base" name="desde_base"
                                        required>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Hasta</label>
                                </div>
                                <div class="col-md-7">
                                    <input type="number" step="0.01" class="form-control" id="hasta_base" name="hasta_base"
                                        >
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Valor</label>
                                </div>
                                <div class="col-md-7">
                                    <input type="number" step="0.01" class="form-control" id="valor_base" name="valor_base"
                                        required>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0"></label>
                                </div>
                                <div class="col-md-7">
                                    <button type="submit" class="btn btn-success btn-sm"><span
                                            id="btn_base">Guardar</span></button>
                                    <button type="button" class="btn btn-warning btn-sm"
                                        onclick="cancelarRango()">Cancelar</button>
                                </div>
                            </div>
                        </div>

                    </form>

                    <table class="table table-bordered" id="tablaRangos">
                        <thead>
                            <tr>
                                <th>Desde</th>
                                <th>Hasta</th>
                                <th>Valor</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- @foreach($rangos as $rango)
                                <tr id="rango-{{ $rango->id }}">
                                <td>
                                    <input type="number" class="form-control" id="desde-{{ $rango->id }}" value="{{ $rango->desde }}" disabled>
                                    <div class="invalid-feedback" id="error-desde-{{ $rango->id }}"></div>
                                </td>
                                <td>
                                    <input type="number" class="form-control" id="hasta-{{ $rango->id }}" value="{{ $rango->hasta }}" disabled>
                                    <div class="invalid-feedback" id="error-hasta-{{ $rango->id }}"></div>
                                </td>
                                <td>
                                    <input type="number" class="form-control" id="valor-{{ $rango->id }}" value="{{ $rango->valor }}" disabled>
                                    <div class="invalid-feedback" id="error-valor-{{ $rango->id }}"></div>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-warning" onclick="habilitarFila({{ $rango->id }})"><i class="bi bi-pencil-square"></i></button>
                                    <button class="btn btn-sm btn-primary d-none" id="guardar-btn-{{ $rango->id }}" onclick="guardarFila({{ $rango->id }})"><i class="bi bi-save"></i></button>
                                </td>
                                </tr>
                                @endforeach -->
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>


    <div class="modal fade" id="modalMantenimiento" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Mantenimiento</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <!-- Nav tabs -->
                    <ul class="nav nav-tabs" id="tabsMantenimiento" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="tab1-tab" data-bs-toggle="tab" data-bs-target="#tab1"
                                type="button" role="tab" aria-controls="tab1" aria-selected="true">
                                Marca
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="tab2-tab" data-bs-toggle="tab" data-bs-target="#tab2" type="button"
                                role="tab" aria-controls="tab2" aria-selected="false">
                                Cuadro Tarifario RTV  {{date('Y')  }}
                            </button>
                        </li>

                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="tab4-tab" data-bs-toggle="tab" data-bs-target="#tab4" type="button"
                                role="tab" aria-controls="tab4" aria-selected="false">
                                Clase Vehiculo Año  {{date('Y')  }}
                            </button>
                        </li>

                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="tab3-tab" data-bs-toggle="tab" data-bs-target="#tab3" type="button"
                                role="tab" aria-controls="tab3" aria-selected="false">
                                Concepto Año {{date('Y')  }}
                            </button>
                        </li>

                        

                    </ul>

                    <!-- Tab content -->
                    <div class="tab-content mt-3">
                        <div class="tab-pane fade show active" id="tab1" role="tabpanel" aria-labelledby="tab1-tab">
                            <form method="POST" action="" id="form_marca">
                                @csrf

                                <div class="col-md-12">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Marca</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="hidden" class="form-control" id="id_marca_vehi"
                                                name="id_marca_vehi">
                                            <input type="text" class="form-control" id="marca_vehi" name="marca_vehi"
                                                required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0"></label>
                                        </div>
                                        <div class="col-md-7">
                                            <button type="submit" class="btn btn-success btn-sm" ><span
                                                    id="btn_marca">Guardar</span></button>
                                            <button type="button" class="btn btn-warning btn-sm"
                                                onclick="cancelarMarca()">Cancelar</button>
                                        </div>
                                    </div>
                                </div>

                            </form>

                            <table class="table table-bordered" id="tablaMarca">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Marca</th>

                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                        </div>

                        <div class="tab-pane fade" id="tab2" role="tabpanel" aria-labelledby="tab2-tab">

                            <form method="POST" action="" id="form_tipo">
                                @csrf

                                <div class="col-md-12">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Descripcion</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="hidden" class="form-control" id="id_tipo_vehi" name="id_tipo_vehi">
                                            <input type="text" class="form-control" id="tipo_vehi" name="tipo_vehi"
                                                required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Valor</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="number" class="form-control" id="tipo_valor" name="tipo_valor"
                                                required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0"></label>
                                        </div>
                                        <div class="col-md-7">
                                            <button type="submit" class="btn btn-success btn-sm"><span
                                                    id="btn_tipo">Guardar</span></button>
                                            <button type="button" class="btn btn-warning btn-sm"
                                                onclick="cancelarTipo()">Cancelar</button>
                                        </div>
                                    </div>
                                </div>

                            </form>

                            <table class="table table-bordered" id="tablaTipoVehiculo">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Descripcion</th>
                                        <th>Valor</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>

                        </div>

                        <div class="tab-pane fade" id="tab3" role="tabpanel" aria-labelledby="tab3-tab">

                            <form method="POST" action="" id="form_concepto">
                                @csrf

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Concepto</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="hidden" class="form-control" id="id_concepto" name="id_concepto"
                                                required>
                                            <input type="text" class="form-control" id="txt_concepto" name="txt_concepto"
                                                required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Valor</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="number" class="form-control" id="valor_concepto" name="valor_concepto"
                                                required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0"></label>
                                        </div>
                                        <div class="col-md-7">
                                            <button type="submit" class="btn btn-success btn-sm btn_conc" disabled><span
                                                    id="btn_concepto">Guardar</span></button>
                                            <button type="button" class="btn btn-warning btn-sm"
                                                onclick="cancelarConcepto()">Cancelar</button>
                                        </div>
                                    </div>
                                </div>

                            </form>

                            <table class="table table-bordered" id="tablaConceptoVehiculo">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Concepto</th>
                                        <th>Valor</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>

                        </div>

                        <div class="tab-pane fade" id="tab4" role="tabpanel" aria-labelledby="tab4-tab">

                            <form method="POST" action="" id="form_clase_tipo">
                                @csrf

                                <div class="col-md-12">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Grupo</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="hidden" class="form-control" id="id_clase_vehi" name="id_clase_vehi">
                                             <select class="form-select {{ $errors->has('year_declaracion') ? 'is-invalid' : '' }}"
                                                id="id_tipo" name="id_tipo" >
                                                <option value="">Seleccione una opcion</option>
                                                @foreach ($tipo_vehiculo as $y)
                                                    <option value="{{ $y->id }}">{{ $y->descripcion }}</option>
                                                @endforeach
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Descripcion</label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" class="form-control" id="descripcion_clase" name="descripcion_clase"
                                                required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0"></label>
                                        </div>
                                        <div class="col-md-7">
                                            <button type="submit" class="btn btn-success btn-sm"><span
                                                    id="btn_tipo_clase">Guardar</span></button>
                                            <button type="button" class="btn btn-warning btn-sm"
                                                onclick="cancelarClaseTipo()">Cancelar</button>
                                        </div>
                                    </div>
                                </div>

                            </form>

                            <table class="table table-bordered" id="tablaTipoClaseVehiculo">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Grupo</th>
                                        <th>Descripcion</th>
                                        <th>Acciones</th>
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
    <script>
        
        $('#check_valor_REC').prop('disabled', true)
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
                            nombrescliente.value = res.data.nombres + ' ' + res.data.apellidos;
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
                    last_year_declaracion: last_year_declaracion
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

                        
                        $('#check_valor_RTV').prop('checked',true)
                       
                        if(res.data.desmarca_rtv=="S"){
                           
                            $('#check_valor_RTV').prop('checked',false)
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
            const vehiculo_id_2 = document.getElementById('vehiculo_id_2').value;
            const cliente_id_2 = document.getElementById('cliente_id_2').value;
            const year_declaracion = document.getElementById('year_declaracion').value;
            const last_year_declaracion = document.getElementById('last_year_declaracion').value;
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

            axios.post('{{ route("store.transito") }}', {
                _token: '{{ csrf_token() }}',
                conceptos: conceptosSeleccionados,
                vehiculo_id_2: vehiculo_id_2,
                cliente_id_2: cliente_id_2,
                year_declaracion: year_declaracion,
                last_year_declaracion: last_year_declaracion
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
        document.getElementById('vehiculoForm').addEventListener('submit', function (e) {
            e.preventDefault();

            // Limpiar errores previos
            //document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
            //document.querySelectorAll('.invalid-feedback').forEach(el => el.innerText = '');
            // Limpiar errores anteriores
            document.querySelectorAll('.invalid-feedback').forEach(el => el.innerText = '');
            document.querySelectorAll('.form-control, .form-select').forEach(el => el.classList.remove('is-invalid'));

            /* const formData = {
                 placa_v: document.getElementById('placa_v').value,
                 chasis_v: document.getElementById('chasis_v').value,
                 avaluo_v: document.getElementById('avaluo_v').value,
                 year_v: document.getElementById('year_v').value,
                 marca_v: document.getElementById('marca_v').value,
                 tipo_v: document.getElementById('tipo_v').value
             };*/
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
        function habilitarFila(id) {
            document.getElementById(`desde-${id}`).disabled = false;
            document.getElementById(`hasta-${id}`).disabled = false;
            document.getElementById(`valor-${id}`).disabled = false;

            document.getElementById(`guardar-btn-${id}`).classList.remove('d-none');
        }
        document.getElementById('es_persona').addEventListener('change', function () {
            const tipo = this.value;
            const labelNombres = document.getElementById('label-nombres');
            const labelApellidos = document.getElementById('label-apellidos');

            if (tipo == 0) {
                labelNombres.textContent = 'Razón Social *';
                labelApellidos.textContent = 'Nombre Comercial *';
            } else {
                labelNombres.textContent = 'Nombres *';
                labelApellidos.textContent = 'Apellidos *';
            }
        });
    </script>
    <script src="{{ asset('js/transito/impuesto.js?v=' . rand())}}"></script>
@endpush