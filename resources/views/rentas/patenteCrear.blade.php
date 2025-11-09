@extends('layouts.appv2')
@section('title', 'Declaracion de patente')
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
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Declaracion de patente</h4>
     <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal"
                data-bs-target="#modalEditarRangos"> <i class="bi bi-table"></i> Tarifa </button>
           
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
@if ($errors->any())
    <div class="alert alert-danger">
        <ul>
            @foreach ($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif
<!-- <form id="formPatente" method="POST" action="{{route('store.patente')}}"> -->
<form id="formPatente" method="POST"  action=""  enctype="multipart/form-data">
    @csrf
        <!-- Información General -->
    <!-- <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Seleccione contribuyente</legend>
        <div class="row">
            <div class="col-md-6 mb-3">
                <div class="input-group mb-3">
                    <input type="text" class="form-control {{$errors->has('catastro_id') ? 'is-invalid' : ''}}" placeholder="Buscar por ruc" id="buscar_contribuyente" name="buscar_contribuyente" value="{{old('buscar_contribuyente')}}">
                    <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#modalContribuyente">Buscar</button>
                    <div class="invalid-feedback">
                        @if($errors->has('catastro_id'))
                            {{$errors->first('catastro_id')}}
                        @endif
                    </div>
                  
                </div>
            </div>
        </div>
    </fieldset> -->
    <input type="hidden" id="catastro_id" name="catastro_id" value="{{old('catastro_id')}}">
    <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Informacion de contribuyente</legend>
        <div class="container">
            <div class="row">

                <div class="d-flex justify-content-end" style="margin-bottom:20px">
                    <button type="button" class="btn btn-success btn-sm" onclick="NuevoContribuyente()">Nuevo</button>
                </div>

                <!-- Columna izquierda -->
                <div class="col-md-6 mb-3">
                    <!-- <div class="row mb-3">
                        <label for="propietario_cedula" class="col-md-4 col-form-label fw-bold">Propietario cédula</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="propietario_cedula" maxlength="255" disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="propietario_nombre" class="col-md-4 col-form-label fw-bold">Propietario</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="propietario_nombre" maxlength="13" disabled>
                        </div>
                    </div> -->
                    <div class="row mb-3">
                        <label for="propietario_nombre" class="col-md-4 col-form-label fw-bold">Propietario</label>
                        <div class="col-md-8">
                            <select id="cmb_propietario" name="cmb_propietario" class="col-md-4 col-form-label fw-bold form-control" style="width: 100%;" data-bs-theme="dark" onchange="cargaInfoContribuyente()">
                                <option value=""></option>
                            </select>
                        </div>
                    </div>
                  
                    <div class="row mb-3">
                        <label for="ruc_cedula" class="col-md-4 col-form-label fw-bold">RUC</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="ruc_cedula" maxlength="13" disabled >
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="razon_social" class="col-md-4 col-form-label fw-bold">Contribuyente/Razon Social</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="razon_social" maxlength="255" disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="propietario_nombre" class="col-md-4 col-form-label fw-bold">N° de Establecimientos</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="num_establecimientos"  disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="tipo_contribuyente" class="col-md-4 col-form-label fw-bold">Tipo de contribuyente</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="tipo_contribuyente"  disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="tipo_contribuyente" class="col-md-4 col-form-label fw-bold">Obligado Contabilidad</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="lleva_cont"  disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="tipo_contribuyente" class="col-md-4 col-form-label fw-bold">Artesano</label>
                        <div class="col-md-7">
                            <input type="text" class="form-control form-control-sm" id="artesano_cont"  disabled>
                        </div>

                        <div class="col-md-1">
                            <input type="hidden" name="pdf_artesano" id="pdf_artesano">
                            <button type="button" class="btn btn-xs btn-primary" onclick="verPdfArtesano()">
                                <i class="fa fa-file-pdf"></i>
                            </button>
                        </div>
                    </div>

                    <!-- <div class="row mb-3">
                        <label for="nombre_contribuyente" class="col-md-4 col-form-label fw-bold">Fantasia comercial</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="nombre_contribuyente"  disabled>
                        </div>
                    </div> -->
                    <!-- <div class="row mb-3">
                        <label for="domicilio" class="col-md-4 col-form-label fw-bold">Domicilio</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="domicilio" maxlength="255" disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="Telefono_domicilio" class="col-md-4 col-form-label fw-bold">Teléfono</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="Telefono_domicilio" maxlength="15" disabled>
                        </div>
                    </div> -->
                   
                    <!-- <div class="row mb-3">
                        <label for="estado_establecimiento" class="col-md-4 col-form-label fw-bold">Estado establecimiento</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="estado_establecimiento"  disabled>
                        </div>
                    </div> -->

                    

                </div>

                <!-- Columna derecha -->
                <div class="col-md-6 mb-3">
                    <div class="row mb-3">
                        <label for="representante_legal" class="col-md-4 col-form-label fw-bold">Rep. legal (sociedades)</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="representante_legal" maxlength="255" disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="cedula_representante" class="col-md-4 col-form-label fw-bold">Cédula del Rep. Legal</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="cedula_representante" maxlength="13" disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="inicio_actividades" class="col-md-4 col-form-label fw-bold">Inicio de Actividades</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="inicio_actividades" maxlength="255" disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="estado_contribuyente" class="col-md-4 col-form-label fw-bold">Estado de contribuyente</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="estado_contribuyente"  disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="clase_contribuyente" class="col-md-4 col-form-label fw-bold">Clase de contribuyente</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="clase_contribuyente"  disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="tipo_contribuyente" class="col-md-4 col-form-label fw-bold">Edad (Años)</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="edad_cont"  disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="tipo_contribuyente" class="col-md-4 col-form-label fw-bold">Documento RUC</label>
                        <div class="col-md-7">
                            <input type="text" class="form-control form-control-sm" id="artesano_cont" value="Si"  disabled>
                        </div>
                        <div class="col-md-1">
                            <input type="hidden" name="pdf_ruc" id="pdf_ruc">
                            <button type="button" class="btn btn-xs btn-primary" onclick="verPdfRuc()">
                                <i class="fa fa-file-pdf"></i>
                            </button>
                        </div>
                    </div>
                    
                    <!-- <div class="row mb-3">
                        <label for="local_propio" class="col-md-4 col-form-label fw-bold">Establecimiento Propio/Arrendado</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="local_propio"  disabled>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label for="correo_electronico" class="col-md-4 col-form-label fw-bold">Correo electrónico</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="correo_electronico" maxlength="15" disabled>
                        </div>
                    </div> -->

                    <!-- <div class="row mb-3">
                        <label for="local_propio" class="col-md-4 col-form-label fw-bold">Mayor Edad</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="local_propio"  disabled>
                        </div>
                    </div> -->

                    <!-- <div class="row mb-3">
                        <label for="lleva_cont" class="col-md-4 col-form-label fw-bold">LLeva Contabilidad</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="lleva_cont"  disabled>
                        </div>
                    </div> -->

                </div>
            </div>
        </div>
    </fieldset>

    @if(old('actividades'))
    dd(old('actividades'))
    @endif
    <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Locales</legend>
        @if($errors->has('actividades'))
        <div class="alert alert-danger">
            {{$errors->first('actividades')}}
        </div>
        @elseif ($errors->has('actividades.*.id'))
        <div class="alert alert-danger">
            {{$errors->first('actividades.*.id')}}
        </div>
        @endif
        <div class="row">
            <div class="col-md-12 mb-3 mt-3">
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-success btn-sm" onclick="NuevoLocal()">Nuevo</button>
                </div>

                <table class="table table-striped" id="tablaLocales" style="margin-top:12px">
                    <thead>
                        <tr>
                            <th scope="col">Seleccione</th>
                            <th scope="col">Nombre Comercial</th>
                            <th scope="col">Ubicacion</th>
                            <th scope="col">Local</th>
                            <th scope="col">Estado</th>
                            <th scope="col"></th>
                        </tr>
                    </thead>
                    <tbody>
                        @if(old('locales'))
                            @foreach(old('locales') as $key => $local)
                                <td>
                                    <input class="form-check-input checkbox-grande" type="checkbox" name="locales[{{$key}}][id]" value="{{ $key }}" {{ old("locales.$key.id") ? 'checked' : '' }}>
                                </td>
                                <td>
                                    <input type="hidden" name="locales[{{$key}}][local_descripcion]" value="{{ $local['local_descripcion'] }}">
                                    {{ $local['local_descripcion'] }}
                                </td>

                                <td>
                                    <input type="hidden" name="locales[{{$key}}][direccion]" value="{{ $local['direccion'] }}">
                                    {{ $local['direccion'] }}
                                </td>
                                <td>
                                    <input type="hidden" name="locales[{{$key}}][local_propio]" value="{{ $local['local_propio'] }}">
                                    {{ $local['local_propio'] }}
                                </td>
                                <td>
                                    <input type="hidden" name="locales[{{$key}}][local_propio]" value="{{ $local['local_propio'] }}">
                                    {{ $local['local_propio'] }}
                                </td>
                                <td>
                                    <input type="hidden" name="locales[{{$key}}][local_propio]" value="{{ $local['local_propio'] }}">
                                    {{ $local['local_propio'] }}
                                </td>

                            @endforeach
                        @endif
                    </tbody>
                </table>
            </div>
        </div>
    </fieldset>

    @if(old('actividades'))
    dd(old('actividades'))
    @endif
    <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Actividades comerciales</legend>
        @if($errors->has('actividades'))
        <div class="alert alert-danger">
            {{$errors->first('actividades')}}
        </div>
        @elseif ($errors->has('actividades.*.id'))
        <div class="alert alert-danger">
            {{$errors->first('actividades.*.id')}}
        </div>
        @endif
        <div class="row">
            <div class="d-flex justify-content-end">
                <button type="button" class="btn btn-success btn-sm" onclick="NuevoActividad()">Nuevo</button>
            </div>
            <div class="col-md-12 mb-3 mt-3">
                <table class="table table-striped" id="tablaActividades">
                    <thead>
                        <tr>
                            <th scope="col">Seleccione</th>
                            <th scope="col">CIIU</th>
                            <th scope="col">Actividad</th>
                            <th scope="col"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- @if(old('actividades'))
                            @foreach(old('actividades') as $key => $actividad)
                                <td>
                                    <input class="form-check-input checkbox-grande" type="checkbox" name="actividades[{{$key}}][id]" value="{{ $key }}" {{ old("actividades.$key.id") ? 'checked' : '' }}>
                                </td>
                                <td>
                                    <input type="hidden" name="actividades[{{$key}}][ciiu]" value="{{ $actividad['ciiu'] }}">
                                    {{ $actividad['ciiu'] }}
                                </td>
                                <td>
                                    <input type="hidden" name="actividades[{{$key}}][nombre]" value="{{ $actividad['nombre'] }}">
                                    {{ $actividad['nombre'] }}
                                </td>

                            @endforeach
                        @endif -->
                    </tbody>
                </table>
            </div>
        </div>
    </fieldset>

 
    <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Datos de declaracion</legend>
        <div class="row">
            <div class="col-md-6 mb-3">
                <label for="year_declaracion" class="form-label">Año de declaracion</label>
                <select class="form-select {{$errors->has('year_declaracion') ? 'is-invalid' : ''}}" id="year_declaracion" name="year_declaracion">
                    <option value="">Seleccione</option>
                    @foreach ($PsqlYearDeclaracion as $year)
                        <option value="{{$year->id}}" {{ old('year_declaracion') == $year->id ? 'selected' : '' }}>{{$year->year_declaracion.' >> Periodo Fiscal '.$year->year_ejercicio_fiscal}}</option>
                    @endforeach
                </select>
                <div class="invalid-feedback">
                    @if($errors->has('year_declaracion'))
                        {{$errors->first('year_declaracion')}}
                    @endif
                </div>
            </div>
            <div class="col-md-6 mb-3">
                <label for="fecha_declaracion" class="form-label">Fecha de Declaración</label>
                <input type="date" class="form-control {{$errors->has('fecha_declaracion') ? 'is-invalid' : ''}}" id="fecha_declaracion" name="fecha_declaracion" value="{{old('fecha_declaracion')}}">
                <div class="invalid-feedback">
                    @if($errors->has('fecha_declaracion'))
                        {{$errors->first('fecha_declaracion')}}
                    @endif
                </div>
            </div>
            <div class="mb-3">
                <div class="form-check" style="display: none;">
                    <input class="form-check-input checkbox-grande" type="checkbox" id="lleva_contabilidad" name="lleva_contabilidad" value="1" {{ old('lleva_contabilidad') ? 'checked' : '' }}>
                    <label class="form-check-label" for="lleva_contabilidad">Lleva Contabilidad</label>

                   
                </div>
                <div class="form-check">
                    <input class="form-check-input checkbox-grande" type="checkbox" id="profesionales" name="profesionales" value="1" {{ old('profesionales') ? 'checked' : '' }}>
                    <label class="form-check-label" for="profesionales">Profesionales (Sin valores declarados en el periodo)</label>
                </div>
                
            </div>

            
        </div>
    </fieldset>

    <!-- Declaracion tributaria -->
    <fieldset class="border p-3 mb-4" id="no_obligados_fieldset">
        <legend class="float-none w-auto px-3 fs-5">Declaracion tibutaria (No obligados a llevar contabilidad)</legend>
        <div class="row">
            <!-- Activos -->
            <div class="col-md-6 mb-3">
                <fieldset class="border p-3 bg-custom">
                    <legend class="w-auto"><h5>Activos</h5></legend>
                    <div class="mb-3 row">
                        <label for="act_caja_banco" class="col-sm-7 col-form-label">Caja y Bancos</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_caja_banco') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_caja_banco" name="act_caja_banco" value="{{old('act_caja_banco')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_ctas_cobrar" class="col-sm-7 col-form-label">Cuentas por Cobrar</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_ctas_cobrar') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_ctas_cobrar" name="act_ctas_cobrar" value="{{old('act_ctas_cobrar')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_inv_mercaderia" class="col-sm-7 col-form-label">Inventario de Mercadería</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_inv_mercaderia') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_inv_mercaderia" name="act_inv_mercaderia" value="{{old('act_inv_mercaderia')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_vehiculo_maquinaria" class="col-sm-7 col-form-label">Vehículos y Maquinaria</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_vehiculo_maquinaria') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_vehiculo_maquinaria" name="act_vehiculo_maquinaria" value="{{old('act_vehiculo_maquinaria')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_equipos_oficinas" class="col-sm-7 col-form-label">Equipos de Oficina</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_equipos_oficinas') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_equipos_oficinas" name="act_equipos_oficinas" value="{{old('act_equipos_oficinas')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_edificios_locales" class="col-sm-7 col-form-label">Edificios y Locales</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_edificios_locales') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_edificios_locales" name="act_edificios_locales" value="{{old('act_edificios_locales')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_terrenos" class="col-sm-7 col-form-label">Terrenos</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_terrenos') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_terrenos" name="act_terrenos" value="{{old('act_terrenos')}}"  onblur="sumarValores('obligado_contabilidad', 'act_total_activos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_total_activos" class="col-sm-7 col-form-label">Total Activos (A)</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <button class="btn btn-outline-secondary" type="button" onclick="sumarValores('obligado_contabilidad', 'act_total_activos')">+</button>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('act_total_activos') ? 'is-invalid' : ''}} input-decimales totales_pasivos_activos" id="act_total_activos" name="act_total_activos" value="{{old('act_total_activos')}}">
                            </div>
                        </div>
                    </div>


                </fieldset>
            </div>
            <!-- Pasivos -->
            <div class="col-md-6 mb-3">
                <fieldset class="border p-3 bg-custom">
                    <legend class="w-auto"><h5>Pasivos</h5></legend>
                    <div class="mb-3 row">
                        <label for="pas_ctas_dctos_pagar" class="col-sm-7 col-form-label">Ctas y doc. por Pagar</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('pas_ctas_dctos_pagar') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_ctas_dctos_pagar" name="pas_ctas_dctos_pagar" value="{{old('pas_ctas_dctos_pagar')}}" onblur="sumarValores('obligado_contabilidad_pasivos', 'pas_total_pasivos')">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3 row">
                        <label for="pas_obligaciones_financieras" class="col-sm-7 col-form-label">Obligaciones Financieras</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('pas_obligaciones_financieras') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_obligaciones_financieras" name="pas_obligaciones_financieras" value="{{old('pas_obligaciones_financieras')}}" onblur="sumarValores('obligado_contabilidad_pasivos', 'pas_total_pasivos')">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3 row">
                        <label for="pas_otras_ctas_pagar" class=" col-sm-7 col-form-label">Otras Cuentas por Pagar</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('pas_otras_ctas_pagar') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_otras_ctas_pagar" name="pas_otras_ctas_pagar" value="{{old('pas_otras_ctas_pagar')}}" onblur="sumarValores('obligado_contabilidad_pasivos', 'pas_total_pasivos')">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3 row">
                        <label for="pas_otros_pasivos" class="col-sm-7 col-form-label">Otros Pasivos</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('pas_otros_pasivos') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_otros_pasivos" name="pas_otros_pasivos" value="{{old('pas_otros_pasivos')}}"  onblur="sumarValores('obligado_contabilidad_pasivos', 'pas_total_pasivos')">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="pas_total_pasivos" class="col-sm-7 col-form-label">Total Pasivos (B)</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <button type="button" class=" btn btn-outline-secondary" onclick="sumarValores('obligado_contabilidad_pasivos', 'pas_total_pasivos')">+</button>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('pas_total_pasivos') ? 'is-invalid' : ''}} input-decimales totales_pasivos_activos" id="pas_total_pasivos" name="pas_total_pasivos" value="{{old('pas_total_pasivos')}}">
                            </div>
                        </div>
                    </div>


                </fieldset>
            </div>
        </div>
        <div class="row">
            <!-- Patrimonio -->
            <div class="col-md-6 mb-3">
                <fieldset class="border p-3 mb-4">
                    <legend class="float-none w-auto px-3 fs-5">Patrimonio</legend>
                    <div class="mb-3 row">
                        <label for="patrimonio_total" class="col-sm-7 col-form-label">Patrimonio total (A-B)</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <button type="button" class="btn btn-outline-secondary" onclick="restarValores('totales_pasivos_activos', 'patrimonio_total')">=</button>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('patrimonio_total') ? 'is-invalid' : ''}} input-decimales totales_pasivos_activos" id="patrimonio_total" name="patrimonio_total" value="{{old('patrimonio_total')}}">
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>
    </fieldset>

    <!-- declaracion obligados a llevar contabilidad -->
    <!-- <div class="row" id="obligados_fieldset"style="display:none;"> -->
            
        <fieldset class="border p-3 mb-4 " id="obligados_fieldset" style="display:none;">
            
            <legend class="float-none w-auto px-3 fs-5">Declaracion Obligados a llevar contabilidad</legend>
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <fieldset class="border p-3 bg-custom">
                        <legend class="w-auto"><h5>Patente Municipal</h5></legend>

                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">% de ingresos percibidos en el Canton</label>
                            <div class="col-sm-6">
                                <div class="input-group mb-3">
                                    <button class="btn btn-outline-secondary" type="button" id="button-addon33" >%</button>
                                    <input type="number" class="desabilita_txt form-control {{$errors->has('cont_total_percibidos_sv') ? 'is-invalid' : ''}}" id="cont_total_percibidos_sv" name="cont_total_percibidos_sv" value="{{old('cont_total_percibidos_sv')}}" >
                                </div>
                            </div>
                        </div>

                        <div class="mb-3 row">
                            <label for="cont_total_activos" class="col-sm-6 col-form-label">Total activos</label>
                            <div class="col-sm-6">
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="desabilita_txt form-control {{$errors->has('cont_total_activos') ? 'is-invalid' : ''}} input-decimales cont_pasivos_activos" id="cont_total_activos" name="cont_total_activos" value="{{old('cont_total_activos')}}"  onblur="restarValores('cont_pasivos_activos', 'cont_total_patrimonio')" >
                                </div>
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="cont_total_pasivos" class="col-sm-6 col-form-label">Total Pasivos</label>
                            <div class="col-sm-6">
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="desabilita_txt form-control {{$errors->has('cont_total_pasivos') ? 'is-invalid' : ''}} input-decimales cont_pasivos_activos" name="cont_total_pasivos" id="cont_total_pasivos" value="{{old('cont_total_pasivos')}}" onblur="restarValores('cont_pasivos_activos', 'cont_total_patrimonio')">
                                </div>
                            </div>
                        </div>

                        <div class="mb-3 row">
                            <label for="cont_total_patrimonio" class="col-sm-6 col-form-label">Patrimonio</label>
                            <div class="col-sm-6">
                                <div class="input-group mb-3">
                                    <button class="btn btn-outline-secondary" type="button" id="button-addon2" onclick="restarValores('cont_pasivos_activos', 'cont_total_patrimonio')">=</button>
                                    <input type="number" class=" desabilita_txt form-control {{$errors->has('cont_total_patrimonio') ? 'is-invalid' : ''}} input-decimales" id="cont_total_patrimonio" name="cont_total_patrimonio" value="{{old('cont_total_patrimonio')}}" aria-label="Example text with button addon" aria-describedby="button-addon2" readonly>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-9" style="display: none;">
                            <div class="form-check">
                                <input class="form-check-input checkbox-grande" type="checkbox" id="calcula_patente" name="calcula_patente" value="1" {{ old('calcula_patente') ? 'checked' : '' }}>
                                <label class="form-check-label" for="calcula_patente">Patente</label>

                            </div>
                        </div>

                    </fieldset>
                </div>
                <div class="col-md-6 mb-3">
                    <fieldset class="border p-3 bg-custom">
                        <legend class="w-auto"><h5>Impuesto 1.5 X Mil</h5></legend>

                        <!-- <div class="mb-3 row">
                            <label for="total_activo15" class="col-sm-6 col-form-label">Total Activos</label>
                            <div class="col-sm-6">
                                
                                <input type="number" class="desabilita_txt form-control {{$errors->has('total_activo15') ? 'is-invalid' : ''}}" id="total_activo15" name="total_activo15" value="{{old('total_activo15')}}">
                            </div>
                        </div> -->

                        
                        <div class="mb-3 row">
                            <label for="total_activo15" class="col-sm-6 col-form-label">Total Activos</label>
                            <div class="col-sm-6">
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="desabilita_txt form-control {{$errors->has('total_activo15') ? 'is-invalid' : ''}} input-decimales total_activo15" name="total_activo15" id="total_activo15" value="{{old('total_activo15')}}" onblur="calcularBI()">
                                </div>
                            </div>
                        </div>


                        <div class="mb-3 row">
                            <label for="cont_total_pasivos_corriente" class="col-sm-6 col-form-label">Pasivo Corriente</label>
                            <div class="col-sm-6">
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="desabilita_txt form-control {{$errors->has('cont_total_pasivos_corriente') ? 'is-invalid' : ''}} input-decimales cont_total_pasivos_corriente" name="cont_total_pasivos_corriente" id="cont_total_pasivos_corriente" value="{{old('cont_total_pasivos_corriente')}}" onblur="calcularBI()">
                                </div>
                            </div>
                        </div>

                        <div class="mb-3 row">
                            <label for="cont_total_bi_act_total" class="col-sm-6 col-form-label">Base Imponible</label>
                            <div class="col-sm-6">
                                <div class="input-group mb-3">
                                    <button class="btn btn-outline-secondary" type="button" id="button-addon2" onclick="restarValores('cont_total_pasivos_corriente', 'cont_total_bi_act_total')">=</button>
                                    <input type="number" class=" desabilita_txt form-control {{$errors->has('cont_total_bi_act_total') ? 'is-invalid' : ''}} input-decimales" id="cont_total_bi_act_total" name="cont_total_bi_act_total" value="{{old('cont_total_bi_act_total')}}" aria-label="Example text with button addon" aria-describedby="button-addon2" readonly>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3 row">
                            
                            <div class="col-sm-9">
                                <div class="form-check">
                                    <input class="form-check-input checkbox-grande" type="checkbox" id="impuesto_1punto5" name="impuesto_1punto5" value="1" {{ old('impuesto_1punto5') ? 'checked' : '' }}>
                                    <label class="form-check-label" for="impuesto_1punto5">Impuesto Anual del 1.5 por mil  sobre los activos totales</label>

                                    <input type="hidden" name="emision" id="emision">
                                </div>
                            </div>
                            
                        </div>
                        


                        <!-- <div class="mb-3 row">
                            <label for="cont_form_sri" class="col-sm-6 col-form-label">Formulario SRI (No.)</label>
                            <div class="col-sm-6">
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_form_sri') ? 'is-invalid' : ''}}" id="cont_form_sri" name="cont_form_sri" value="{{old('cont_form_sri')}}">
                            </div>
                        </div>

                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Original/sustitutiva</label>
                            <div class="col-sm-6">
                                <select class="desabilita_txt form-select {{$errors->has('cont_original') ? 'is-invalid' : ''}}" id="cont_original" name="cont_original">
                                    <option value="">Seleccione</option>
                                    <option value="1" {{ old('cont_original') == '1' ? 'selected' : '' }}>Original</option>
                                    <option value="2" {{ old('cont_original') == '2' ? 'selected' : '' }}>Sustitutiva</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="mb-3 row">
                            
                            <div class="col-sm-9">
                                <div class="form-check">
                                    <input class="form-check-input checkbox-grande" type="checkbox" id="impuesto_1punto5" name="impuesto_1punto5" value="1" {{ old('impuesto_1punto5') ? 'checked' : '' }}>
                                    <label class="form-check-label" for="impuesto_1punto5">Impuesto Anual del 1.5 por mil  sobre los activos totales</label>

                                    <input type="hidden" name="emision" id="emision">
                                </div>
                            </div>
                            
                        </div> -->
                    </fieldset>

                </div>

                <div class="col-md-12 mb-3">
                    <fieldset class="border p-3 bg-custom">
                        <legend class="w-auto"><h5>Informacion Adicional</h5></legend>

                      

                        <div class="mb-3 row">
                            <label for="cont_total_patrimonio" class="col-sm-3 col-form-label" style="text-align: last;">Cargar Documentos</label>
                            <div class="col-sm-6">
                                <div class="input-group mb-3">
                                    
                                    <input type="file" class=" desabilita_txt form-control {{$errors->has('archivo_patente') ? 'is-invalid' : ''}} input-decimales" id="archivo_patente" name="archivo_patente" value="{{old('archivo_patente')}}" aria-label="Example text with button addon" aria-describedby="button-addon2">
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-3 row">
                            <label for="cont_form_sri" class="col-sm-3 col-form-label" style="text-align: last;">Formulario SRI (No.)</label>
                            <div class="col-sm-6">
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_form_sri') ? 'is-invalid' : ''}}" id="cont_form_sri" name="cont_form_sri" value="{{old('cont_form_sri')}}">
                            </div>
                        </div>

                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-3 col-form-label"  style="text-align: last;">Original/sustitutiva</label>
                            <div class="col-sm-6">
                                <select class="desabilita_txt form-select {{$errors->has('cont_original') ? 'is-invalid' : ''}}" id="cont_original" name="cont_original">
                                    <option value="">Seleccione</option>
                                    <option value="1" {{ old('cont_original') == '1' ? 'selected' : '' }}>Original</option>
                                    <option value="2" {{ old('cont_original') == '2' ? 'selected' : '' }}>Sustitutiva</option>
                                </select>
                              

                            </div>
                        </div>
                        
                     
                    </fieldset>

                </div>
            </div>

        </fieldset>

        
    <!-- </div> -->
       <!-- Exoneraciones -->
    <!-- <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Exoneraciones</legend>
        <div class="row">
            <div class="col-md-4 mb-3">
                <div class="form-check">
                    <input class="form-check-input checkbox-grande" type="checkbox" id="calificacion_artesanal" name="calificacion_artesanal" value="1" {{ old('calificacion_artesanal') ? 'checked' : '' }}>
                    <label class="form-check-label" for="calificacion_artesanal">Calificacion artesanal</label>
                    <div class="invalid-feedback">
                        @if($errors->has('calificacion_artesanal'))
                            {{$errors->first('calificacion_artesanal')}}
                        @endif
                    </div>
                </div>
            </div>

            <div class="col-md-4 mb-3">
                <div class="form-check">
                    <input class="form-check-input checkbox-grande" type="checkbox" id="tercera_edad" name="tercera_edad" value="1" {{ old('tercera_edad') ? 'checked' : '' }}>
                    <label class="form-check-label" for="tercera_edad">Tercera Edad</label>
                    <div class="invalid-feedback">
                        @if($errors->has('tercera_edad'))
                            {{$errors->first('tercera_edad')}}
                        @endif
                    </div>
                </div>
            </div>

        </div>
    </fieldset> -->

    <!-- calculo patente municipal -->
    <div class="row">
        <fieldset class="col-md-6 border p-3 mb-4" id="patente_fieldset" >
            <legend class="float-none w-auto px-3 fs-5">Valor Patente municipal</legend>
            <div class="row">
                <div class="col-md-12 mb-3">
                    
                    <div class="mb-3 row">
                        <label for="cont_impuesto" class="col-sm-6 col-form-label">Impuesto</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_impuesto') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_impuesto" id="cont_impuesto" value="{{old('cont_impuesto')}}" onblur="sumarValores('cont_patente_muni', 'cont_pago_patente')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_exoneracion" class="col-sm-6 col-form-label">Exoneracion</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_exoneracion') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_exoneracion" id="cont_exoneracion" value="{{old('cont_exoneracion')}}" onblur="sumarValores('cont_patente_muni', 'cont_pago_patente')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_sta" class="col-sm-6 col-form-label">Servicio Técnico Administrativo</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_sta') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_sta" id="cont_sta" value="{{old('cont_sta')}}" onblur="sumarValores('cont_patente_muni', 'cont_pago_patente')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_intereses" class="col-sm-6 col-form-label">Intereses</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_intereses') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_intereses" id="cont_intereses" value="{{old('cont_intereses')}}" onblur="sumarValores('cont_patente_muni', 'cont_pago_patente')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_recargos" class="col-sm-6 col-form-label">Recargos</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_recargos') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_recargos" id="cont_recargos" value="{{old('cont_recargos')}}" onblur="sumarValores('cont_patente_muni', 'cont_pago_patente')" readonly>
                            </div>
                        </div>
                    </div>

                    <!-- <div class="mb-3 row">
                        <label for="cont_pf" class="col-sm-6 col-form-label">Permiso de Funcionamiento</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_pf') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_pf" id="cont_pf" value="{{old('cont_pf')}}" onblur="sumarValores('cont_patente_muni', 'cont_pago_patente')">
                            </div>
                        </div>
                    </div> -->

                    <div class="mb-3 row">
                        <label for="cont_pago_patente" class="col-sm-6 col-form-label">Valor a Pagar</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <button class="btn btn-outline-secondary" type="button" id="button-addon2"onclick="sumarValores('cont_patente_muni', 'cont_pago_patente')">=</button>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_pago_patente') ? 'is-invalid' : ''}} input-decimales cont_patente_muni" name="cont_pago_patente" id="cont_pago_patente" value="{{old('cont_pago_patente')}}" aria-label="Example text with button addon" aria-describedby="button-addon2" readonly>
                            </div>
                        </div>
                    </div>
                

                </div>
                
            </div>

        </fieldset>

        <fieldset class="col-md-6 border p-3 mb-4" id="patente_fieldset1" >
            <legend class="float-none w-auto px-3 fs-5">Valor Activo Totales</legend>
            <div class="row">
                <div class="col-md-12 mb-3">
                    
                    <div class="mb-3 row">
                        <label for="cont_impuesto_act" class="col-sm-6 col-form-label">Impuesto</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_impuesto_act') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_impuesto_act" id="cont_impuesto_act" value="{{old('cont_impuesto_act')}}" onblur="sumarValores('cont_pago_acti', 'cont_pago_activo_total')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_exoneracion_act" class="col-sm-6 col-form-label">Exoneracion</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_exoneracion_act') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_exoneracion_act" id="cont_exoneracion_act" value="{{old('cont_exoneracion_act')}}" onblur="sumarValores('cont_pago_acti', 'cont_pago_activo_total')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_sta_act" class="col-sm-6 col-form-label">Servicio Técnico Administrativo</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_sta_act') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_sta_act" id="cont_sta_act" value="{{old('cont_sta_act')}}" onblur="sumarValores('cont_pago_acti', 'cont_pago_activo_total')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_intereses_act" class="col-sm-6 col-form-label">Intereses</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_intereses_act') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_intereses_act" id="cont_intereses_act" value="{{old('cont_intereses_act')}}" onblur="sumarValores('cont_pago_acti', 'cont_pago_activo_total')" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="cont_recargos_act" class="col-sm-6 col-form-label">Recargos</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_recargos_act') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_recargos_act" id="cont_recargos_act" value="{{old('cont_recargos_act')}}" onblur="sumarValores('cont_pago_acti', 'cont_pago_activo_total')" readonly>
                            </div>
                        </div>
                    </div>



                    <!-- <div class="mb-3 row">
                        <label for="cont_pf_act" class="col-sm-6 col-form-label">Permiso de Funcionamiento</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_pf_act') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_pf_act" id="cont_pf_act" value="{{old('cont_pf_act')}}" onblur="sumarValores('cont_pago_acti', 'cont_pago_activo_total')">
                            </div>
                        </div>
                    </div> -->

                    <div class="mb-3 row">
                        <label for="cont_pago_activo_total" class="col-sm-6 col-form-label">Valor a Pagar</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <button class="btn btn-outline-secondary" type="button" id="button-addon2"onclick="sumarValores('cont_pago_acti', 'cont_pago_activo_total')">=</button>
                                <input type="number" class="desabilita_txt form-control {{$errors->has('cont_pago_activo_total') ? 'is-invalid' : ''}} input-decimales cont_pago_acti" name="cont_pago_activo_total" id="cont_pago_activo_total" value="{{old('cont_pago_activo_total')}}" aria-label="Example text with button addon" aria-describedby="button-addon2" readonly>
                            </div>
                        </div>
                    </div>
                

                </div>
                
            </div>

        </fieldset>
    </div>
    <div class="row">
        <fieldset class="col-md-12 border p-3 mb-4" id="patente_fieldset1" >
            <legend class="float-none w-auto px-3 fs-5">Valor Total</legend>
            <div class="row">
                <div class="col-md-12 mb-3">
                    
                    <div class="mb-3 row">
                        <label for="cont_impuesto_act" class="col-sm-3 col-form-label" style="text-align: right;">Total a Pagar</label>
                        <div class="col-sm-6">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="desabilita_txt form-control "name="total_final" id="total_final" value=""readonly>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>
</form>

<div class="btn-toolbar1 d-flex justify-content-center gap-2" style="margin-bottom: 22px;">
    <button type="button" class="btn btn-sm btn-success d-flex align-items-center" onclick="simular()">
        Simular
    </button>

    <button type="button" class="btn btn-sm btn-primary d-flex align-items-center" onclick="enviarFormulario()">
        Emitir
    </button>
</div>

    <!-- Modal para Propietario -->
    <div class="modal fade" id="modalContribuyente" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
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

    <!-- <div class="modal fade" id="documentopdf" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
    
                <div class="modal-body">
                    <span style="font-size: 150%; color: green" class="fa fa-file"></span> <label id="titulo" class="modal-title" style="font-size: 130%; color: black ;">NOTA CREDITO</label>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span style="font-size: 35px"aria-hidden="true">&times;</span>
                    </button>
                        <br><br>
                        <div class="row">
                            <div class="col-sm-12 col-xs-11 "style="height: auto ">
                                <iframe width="100%" height="500" frameborder="0"id="iframePdf"></iframe>
                                    <p style="color: #747373;font-size:15px"></p>
                            </div>
                        </div>
                            
                        
                </div>

                <div class="modal-footer"> 
                    <center>
                            <button type="button" class="btn btn-default" data-dismiss="modal"><i class="fa fa-mail-reply-all"></i> Salir</button>  
                            <a href=""id="vinculo"><button  type="button" id="descargar"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>                                 
                    </center>               
                </div>


            </div>
        </div>
    </div> -->

    <div class="modal fade" id="modalLocal" tabindex="-1" aria-labelledby="LocalModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalLocalLabel">Nuevo Local</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-12 mb-3">
                                
                                <div class="mb-3 row">
                                    <label for="provincia_local" class="col-sm-3 col-form-label" style="text-align: end;">Provincia</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">                                            
                                            <input type="text" class="desabilita_txt form-control" name="provincia_local" id="provincia_local" value="{{old('provincia_local')}}" value="Manabi">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="canton_local" class="col-sm-3 col-form-label" style="text-align: end;">Canton</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="desabilita_txt form-control" name="canton_local" id="canton_local" value="{{old('canton_local')}}" value="San Vicente">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="parroquia_local" class="col-sm-3 col-form-label" style="text-align: end;">Parroquia</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <select class="form-select {{$errors->has('parroquia_id') ? 'is-invalid' : ''}}" id="parroquia_id" name="parroquia_id" required>
                                                <option value="" id="optionSelectParroquia">Seleccione una parroquia</option>
                                                @if(old('canton_id'))
                                                    @foreach (session('parroquia') as $p)
                                                        <option value="{{$p->id}}" {{ old('parroquia_id') == $p->id ? 'selected' : '' }}>{{$p->descripcion}}</option>
                                                    @endforeach
                                                @endif
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="callep_local" class="col-sm-3 col-form-label" style="text-align: end;">Calle Principal</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="desabilita_txt form-control" name="callep_local" id="callep_local" value="{{old('callep_local')}}">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="calles_local" class="col-sm-3 col-form-label" style="text-align: end;">Calle Secundaria</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="desabilita_txt form-control" name="calles_local" id="calles_local" value="{{old('calles_local')}}">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="referencia_local" class="col-sm-3 col-form-label" style="text-align: end;">Referencia Ubicacion</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="desabilita_txt form-control" name="referencia_local" id="referencia_local" value="{{old('referencia_local')}}">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="ncomercial_local" class="col-sm-3 col-form-label" style="text-align: end;">Nombre Comercial</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <input type="text" class="desabilita_txt form-control" name="ncomercial_local" id="ncomercial_local" value="{{old('ncomercial_local')}}">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="estado_local" class="col-sm-3 col-form-label" style="text-align: end;">Estado</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <select class="form-select {{$errors->has('estado_establecimiento_id') ? 'is-invalid' : ''}}" id="estado_establecimiento_id" name="estado_establecimiento_id" required>
                                                <option value="">Seleccione un estado</option>
                                                <option value="1" {{ old('estado_establecimiento_id') == '1' ? 'selected' : '' }}>Abierto</option>
                                                <option value="2" {{ old('estado_establecimiento_id') == '2' ? 'selected' : '' }}>Cerrado</option>
                                        </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3 row">
                                    <label for="tipo_local" class="col-sm-3 col-form-label" style="text-align: end;">Local</label>
                                    <div class="col-sm-8">
                                        <div class="input-group">
                                            <select class="form-select {{$errors->has('estado_establecimiento_id') ? 'is-invalid' : ''}}" id="tipo_local" name="tipo_local" required>
                                                <option value="">Seleccione un tipo</option>
                                                <option value="1" {{ old('tipo_local') == '1' ? 'selected' : '' }}>Propio</option>
                                                <option value="2" {{ old('tipo_local') == '2' ? 'selected' : '' }}>Arrendado</option>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            
                            </div>
                    
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" onclick="guardaLocal()">
                        <span id="label_btn_local"></span>
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

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

                                
                            </div>
                    
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" onclick="guardaActividad()">
                        <span id="label_btn_actividad"></span>
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>


    <div class="modal fade" id="modalContri" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Nuevo Contribuyente</h5>
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
                                            <label for="cmb_ruc" class="form-label">RUC-Contribuyente </label>
                                            <!-- <select id="cmb_ruc" name="cmb_ruc" class="form-control modal_new_cont" onchange="cargaContribuyente()">
                                                <option value=""></option>
                                            </select> -->

                                            <!-- <input type="text" name="cmb_ruc" class="form-control modal_new_cont" id="cmb_ruc"> -->

                                            <input type="text" class="form-control" name="cmb_ruc" id="cmb_ruc" onblur="BuscaContribuyente()" >

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
                                            <input type="text" class="form-control" name="cmb_ruc_rep" id="cmb_ruc_rep" onblur="BuscaContribuyenteRL()" >
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
                            <span id="label_btn_contribuyente"></span>
                        </button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
            </div>
        </div>
    </div>


     <div class="modal fade" id="modalEditarRangos" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Tarifa anual del año {{ date('Y') }}</h5>
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

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Imp sobre Fraccion Exc</label>
                                </div>
                                <div class="col-md-7">
                                    <input type="number" step="0.01" class="form-control" id="impuesto_fracion" name="impuesto_fracion"
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
                                <th>Imp. sobre fraccion exc</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            
                        </tbody>
                    </table>
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
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>

<script src="{{ asset('js/patente/crear.js?v='.rand())}}"></script>

<script>
    $(document).ready(function(){
        tablaContribuyente = $("#tablaContribuyente").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/catastrocontribuyente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
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
    $('#doc_artesano').prop('disabled', true);
    let token = "{{csrf_token()}}";

    
// alert(token)

let tk=$('meta[name="csrf-token"]').attr('content')
// alert(tk)


</script>
@endpush
