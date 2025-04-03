@extends('layouts.appv2')
@section('title', 'Declaracion de patente')
@push('styles')
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

</style>

@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Declaracion de patente</h4>
   
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
    <fieldset class="border p-3 mb-4">
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
    </fieldset>
    <input type="hidden" id="catastro_id" name="catastro_id" value="{{old('catastro_id')}}">
    <fieldset class="border p-3 mb-4">
        <legend class="float-none w-auto px-3 fs-5">Informacion de contribuyente</legend>
        <div class="container">
            <div class="row">
                <!-- Columna izquierda -->
                <div class="col-md-6 mb-3">
                    <div class="row mb-3">
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
                    </div>
                    <div class="row mb-3">
                        <label for="num_establecimientos" class="col-md-4 col-form-label fw-bold">Numero de establecimientos</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="num_establecimientos"  disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="ruc_cedula" class="col-md-4 col-form-label fw-bold">RUC</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="ruc_cedula" maxlength="13" disabled >
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="razon_social" class="col-md-4 col-form-label fw-bold">Razon Social</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="razon_social" maxlength="255" disabled>
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
                        <label for="tipo_contribuyente" class="col-md-4 col-form-label fw-bold">Tipo de contribuyente</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="tipo_contribuyente"  disabled>
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
                <table class="table table-striped" id="tablaLocales">
                    <thead>
                        <tr>
                            <th scope="col">Seleccione</th>
                            <th scope="col">Nombre Comercial</th>
                            <th scope="col">Ubicacion</th>
                            <th scope="col">Local</th>
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
                        @endif
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
                <div class="form-check">
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
                                    <input type="number" class="desabilita_txt form-control {{$errors->has('cont_total_pasivos_corriente') ? 'is-invalid' : ''}} input-decimales cont_pasivos_activos" name="cont_total_pasivos_corriente" id="cont_total_pasivos_corriente" value="{{old('cont_total_pasivos_corriente')}}" onblur="calcularBI()">
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
    <fieldset class="border p-3 mb-4">
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
    </fieldset>

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
    let token = "{{csrf_token()}}";

    let inputs = document.querySelectorAll(".desabilita_txt"); // Selecciona todos los inputs con la clase "miClase"
    inputs.forEach(input => input.disabled = true); // Bloquea cada input

   
    
    function limpiarCampos() {
        document.getElementById("propietario_nombre").value = "";
        document.getElementById("propietario_cedula").value = "";
        document.getElementById("ruc_cedula").value = "";
        // document.getElementById("nombre_contribuyente").value = "";
        document.getElementById("razon_social").value = "";
        // document.getElementById("domicilio").value = "";
        // document.getElementById("Telefono_domicilio").value = "";
        // document.getElementById("correo_electronico").value = "";
        document.getElementById("representante_legal").value = "";
        document.getElementById("cedula_representante").value = "";
        document.getElementById("inicio_actividades").value = "";
        document.getElementById("estado_contribuyente").value = "";
        document.getElementById("clase_contribuyente").value = "";
        document.getElementById("tipo_contribuyente").value = "";
        document.getElementById("num_establecimientos").value = "";
        // document.getElementById("estado_establecimiento").value = "";
        // document.getElementById("local_propio").value = "";
        document.getElementById("catastro_id").value = "";
        // document.getElementById("lleva_cont").value = "";
        
    }
    globalThis.Emitir=0
    function enviarFormulario() {        
        Emitir=1    
        $('#emision').val(1)
        $("#formPatente").trigger("submit");
        // $("#formPatente").submit();
    }

    function simular() { 
        // $('#documentopdf').modal('show')
        // verpdf("c.pdf");
        // return
        Emitir=0   
        $('#emision').val(2)        
        $("#formPatente").trigger("submit");
    }

    function verpdf(ruta){
        var iframe=$('#iframePdf');
        iframe.attr("src", "patente/documento/"+ruta);   
        $("#vinculo").attr("href", 'patente/descargar-documento/'+ruta);
        $("#documentopdf").modal("show");
    }

    $('#documentopdf').on('hidden.bs.modal', function () {
        if(Emitir==1){
            location.reload();
        }
    });
    // funcion para seleccionar contribuyente y cargar los datos
    function seleccionarcontribuyente(id){
        limpiarCampos();
        axios.post("{{route('get.catastro')}}", {
            _token: token,
            id:id
        }).then(function(res) {
            console.log(res)
            if(res.status==200) {


                let inputs = document.querySelectorAll(".desabilita_txt");
                inputs.forEach(input => input.disabled = false); // Desbloquea cada input
               
                               
              
                const data = res.data.contribuyente; // Los datos del contribuyente
                const datapropietario = res.data.propietarios; // Los datos del contribuyente
                const dataclase = res.data.clase_contribuyente; // Los datos del contribuyente
                const actividadesComerciales = res.data.actividades; // Los datos del contribuyente
                const locales=res.data.locales
                console.log(locales)
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
                // if (data.local_propio === "1") {
                //     localPropio = "Propio";
                // } else if (tipo_contribuyente === "2") {
                //     localPropio = "Arrendado";
                // } else {
                //     localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
                // }

                let checkbox = document.getElementById("lleva_contabilidad");    
                const noObligadosFieldset1 = document.getElementById('no_obligados_fieldset');
                const obligadosFieldset1 = document.getElementById('obligados_fieldset');

                // if(data.obligado_contabilidad!=true){ 
                   
                //     checkbox.checked =  true; 
                //     checkbox.classList.add("readonly-checkbox");
                //     obligadosFieldset1.style.display = 'block';
                //     // Ocultar el fieldset para "No obligados a llevar contabilidad"
                //     noObligadosFieldset1.style.display = 'none';

                // }else{
                   
                //     checkbox.checked =  false; 
                //     checkbox.classList.add("readonly-checkbox");
                //     // Mostrar el fieldset para "No obligados a llevar contabilidad"
                //     noObligadosFieldset1.style.display = 'block';
                //     // Ocultar el fieldset para "Obligados a llevar contabilidad"
                //     obligadosFieldset1.style.display = 'none';
                    
                // }              
                

                // Asigna los datos a los campos del formulario
                document.getElementById("catastro_id").value = data.id;
                document.getElementById("buscar_contribuyente").value = data.ruc;
                document.getElementById("propietario_nombre").value = datapropietario.nombres ?? ""+" "+datapropietario.apellidos ?? "";
                document.getElementById("propietario_cedula").value = datapropietario.ci_ruc ?? "";
                document.getElementById("ruc_cedula").value = data.ruc ?? "";
                // document.getElementById("nombre_contribuyente").value = data.nombre_fantasia_comercial ?? "" // Nombre de fantasía comercial
                document.getElementById("razon_social").value = data.razon_social ?? "";
                // document.getElementById("domicilio").value = `${data.calle_principal ?? ""} ${data.calle_secundaria ?? ""} ${data.referencia_ubicacion ?? ""}`.trim();
                // document.getElementById("Telefono_domicilio").value = data.telefono ?? "";
                // document.getElementById("correo_electronico").value = data.correo_1 ?? "";
                document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
                document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
                document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
                document.getElementById("estado_contribuyente").value = estadoContribuyente;
                document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
                document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
                document.getElementById("num_establecimientos").value = data.id ?? "";
                // document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
                // document.getElementById("local_propio").value = localPropio ?? "";
                limpiarTabla();
                // document.getElementById("lleva_cont").value = LocalPropio ?? "";
                actividadesComerciales.forEach((actividad) => {
                    console.log(actividad)
                    seleccionarActividad(actividad.id,actividad.descripcion,actividad.ciiu);
                });
                limpiarTablaLocal();
                locales.forEach((local) => {
                    console.log("qqqqqqqqqqqqq")
                    console.log(local)
                    let direccion=local.provincia.descripcion+"-"+local.canton.nombre+"-"+local.parroquia.descripcion+", Calle "+local.calle_principal+" y "+local.calle_secundaria
                    seleccionarLocales(local.id,local.actividad_descripcion,direccion,local.local_propio);
                });

                
                 // Cerrar el modal
                // Selecciona el elemento modal

              
                

            }else{
                alert(res.status);
            }
        }).catch(function(err) {
            console.log(err)
        }).then(function() {

        });
        var modalElement = document.getElementById('modalContribuyente');
        // Crea una instancia del modal o toma la instancia existente
        var modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        // Oculta el modal
        modal.hide();
    }
    globalThis.LocalComercial=""
    function seleccionarcontribuyentesinactividad(id){
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
                const locales=res.data.locales
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
                // if (data.estado_establecimiento === 1) {
                //     estadoEstablecimiento = "Abierto";
                // } else if (tipo_contribuyente === 2) {
                //     estadoEstablecimiento = "Cerrado";
                // } else {
                //     estadoEstablecimiento = "Desconocido"; // Opcional: texto si el estado no coincide
                // }
                // if (data.local_propio === 1) {
                //     localPropio = "Propio";
                // } else if (tipo_contribuyente === 2) {
                //     localPropio = "Arrendado";
                // } else {
                //     localPropio = "Desconocido"; // Opcional: texto si el estado no coincide
                // }
                // LocalComercial=localPropio
                // Asigna los datos a los campos del formulario
                document.getElementById("catastro_id").value = data.id;
                document.getElementById("buscar_contribuyente").value = data.ruc;
                document.getElementById("propietario_nombre").value = datapropietario.nombres ?? ""+" "+datapropietario.apellidos ?? "";
                document.getElementById("propietario_cedula").value = datapropietario.ci_ruc ?? "";
                document.getElementById("ruc_cedula").value = data.ruc ?? "";
                document.getElementById("nombre_contribuyente").value = data.nombre_fantasia_comercial ?? "" // Nombre de fantasía comercial
                document.getElementById("razon_social").value = data.razon_social ?? "";
                document.getElementById("domicilio").value = `${data.calle_principal ?? ""} ${data.calle_secundaria ?? ""} ${data.referencia_ubicacion ?? ""}`.trim();
                document.getElementById("Telefono_domicilio").value = data.telefono ?? "";
                // document.getElementById("correo_electronico").value = data.correo_1 ?? "";
                document.getElementById("representante_legal").value = data.representante_legal_id ?? "";
                document.getElementById("cedula_representante").value = data.representante_legal_id ?? ""; // Ajustar si tienes datos separados para la cédula
                document.getElementById("inicio_actividades").value = data.fecha_inicio_actividades ?? "";
                document.getElementById("estado_contribuyente").value = estadoContribuyente;
                document.getElementById("clase_contribuyente").value = dataclase.nombre ?? "";
                document.getElementById("tipo_contribuyente").value = tipoContribuyente ?? "";
                document.getElementById("num_establecimientos").value = data.id ?? "";
                // document.getElementById("estado_establecimiento").value = estadoEstablecimiento ?? "";
                // document.getElementById("local_propio").value = localPropio ?? "";

               
                // document.getElementById('lleva_contabilidad').value=1

            }else{
                alert(res.status);
            }
        }).catch(function(err) {
            console.log(err)
        }).then(function() {

        });
        var modalElement = document.getElementById('modalContribuyente');
        // Crea una instancia del modal o toma la instancia existente
        var modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        // Oculta el modal
        modal.hide();
    }
    function limpiarTablaLocal(){
        // Obtener el cuerpo de la tabla
        const tbody = document.querySelector("#tablaLocales tbody");

        // Eliminar todas las filas existentes
        while (tbody.firstChild) {
            tbody.removeChild(tbody.firstChild);
        }
    }
    function seleccionarLocales(id, local_descripcion, direccion, local_propio_data ){
        // alert(local_descripcion)
        // limpiarTablaLocal();
        const nuevaFila = document.createElement("tr");
        let local_propio=""
        if (local_propio_data === 1) {
            local_propio = "Propio";
        } else if (local_propio_data === 2) {
            local_propio = "Arrendado";
        } else {
            local_propio = "Desconocido"; // Opcional: texto si el estado no coincide
        }

        // Definir el contenido de la nueva fila, incluyendo inputs ocultos para enviar a Laravel
        nuevaFila.innerHTML = `
            <td>
                <input class="form-check-input checkbox-grande" type="checkbox" name="locales[${id}][id]" value="${id}">
            </td>
            <td>
                <input type="hidden" name="locales[${id}][local_descripcion]" value="${local_descripcion}">
                ${local_descripcion}
            </td>
            <td>
                <input type="hidden" name="locales[${id}][direccion]" value="${direccion}">
                ${direccion}
            </td>
            <td>
                <input type="hidden" name="locales[${id}][local_propio]" value="${local_propio}">
                ${local_propio}
            </td>

        `;

        // Añadir la nueva fila a la tabla
        document.querySelector("#tablaLocales tbody").appendChild(nuevaFila);

        // Cerrar el modal
        // var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
        // modal.hide();
    }
    //una vez cargados los contribuyentes esta funcion hace cargar las actividades asociadas
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
        // var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
        // modal.hide();
    }
    //limpia las filas de la tabla cuando se selecciona una persona
    function limpiarTabla() {
        // Obtener el cuerpo de la tabla
        const tbody = document.querySelector("#tablaActividades tbody");

        // Eliminar todas las filas existentes
        while (tbody.firstChild) {
            tbody.removeChild(tbody.firstChild);
        }
    }
    function eliminarFila(boton) {
            // Eliminar la fila padre del botón clicado
            boton.closest("tr").remove();
    }

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

    document.querySelectorAll(".input-decimales").forEach(input => {
        // Formato al perder el foco
        input.addEventListener("blur", function() {
            formatearConDosDecimales(this);
        });

        // Formato al presionar Enter
        input.addEventListener("keypress", function(event) {
            if (event.key === "Enter") {
                formatearConDosDecimales(this);
            }
        });
    });

    function sumarValores(claseInputs, idExclusion) {
        // Selecciona todos los elementos con la clase especificada, excluyendo el campo con el id dado
        const inputs = document.querySelectorAll(`.${claseInputs}:not(#${idExclusion})`);
        let total = 0;

        // Suma los valores de los campos input
        inputs.forEach(input => {
            const value = parseFloat(input.value) || 0;
            total += value;
        });

        // Asigna el total al campo excluido
        document.getElementById(idExclusion).value = total.toFixed(2);

        //si  total activo y pasivo es diferente a null, calcula patrimonio_total
        var act=document.getElementById("act_total_activos").value
        var pas=document.getElementById("pas_total_pasivos").value
        if(pas!="" && act!=""){
            // alert(act+pas)
            // sumarValores('obligado_contabilidad', 'act_total_activos')
            restarValores('totales_pasivos_activos', 'patrimonio_total')
        }
    }

    function calcularBI(){
       var activo_total=$('#total_activo15').val()
       var pasivo_corriente=$('#cont_total_pasivos_corriente').val()
       var patrimonio_act=activo_total - pasivo_corriente
       patrimonio_act=patrimonio_act.toFixed(2)
       $('#cont_total_bi_act_total').val(patrimonio_act)
    }

    globalThis.LlevaContabilidad=0

    function restarValores(claseInputs, idExclusion) {
        // alert("sa")
       
        // Selecciona todos los elementos con la clase especificada, excluyendo el campo con el id dado
        const inputs = document.querySelectorAll(`.${claseInputs}:not(#${idExclusion})`);
        let total = parseFloat(inputs[0]?.value) || 0; // Inicia con el primer valor

        // Resta los valores de los campos input a partir del segundo
        inputs.forEach((input, index) => {
            if (index > 0) { // Omite el primer elemento ya que se usó para inicializar `total`
                const value = parseFloat(input.value) || 0;
                total -= value;
            }
        });

        let es_activo_total=0
        var total_activo15=$('#total_activo15').val()

        if(LlevaContabilidad==1){
            // alert(LlevaContabilidad)
            var cont_total_activos=$('#cont_total_activos').val()
            // alert(cont_total_activos)
            // cont_total_activos=cont_total_activos.toFixed(2)
            $('#total_activo15').val(cont_total_activos)

        }
        // alert(total_activo15)

        // Asigna el total al campo excluido
        document.getElementById(idExclusion).value = total.toFixed(2);

        calculaPatente()
    }


    // Función para formatear el valor a dos decimales
    function formatearConDosDecimales(input) {
        let valor = parseFloat(input.value);
        if (!isNaN(valor)) {
            input.value = valor.toFixed(2);
        }
    }
    
    // Obtener el checkbox y los fieldsets
    const checkbox = document.getElementById('lleva_contabilidad');
    const noObligadosFieldset = document.getElementById('no_obligados_fieldset');
    const obligadosFieldset = document.getElementById('obligados_fieldset');

    // Función para actualizar la visibilidad de los fieldsets
    function toggleFieldsets() {
        let checkboxprofesional = document.getElementById("profesionales");
        if (checkbox.checked) {
            // Mostrar el fieldset para "Obligados a llevar contabilidad"
            obligadosFieldset.style.display = 'block';
            let div = document.getElementById("obligados_fieldset");
            div.classList.toggle("mostrar");
            // Ocultar el fieldset para "No obligados a llevar contabilidad"
            noObligadosFieldset.style.display = 'none';

            checkboxprofesional.checked =false
        } else {
            // Mostrar el fieldset para "No obligados a llevar contabilidad"
            noObligadosFieldset.style.display = 'block';
            // Ocultar el fieldset para "Obligados a llevar contabilidad"
            obligadosFieldset.style.display = 'none';
            checkboxprofesional.checked =false
        }
    }

//     function toggleFieldsets() {
//     let checkbox = document.getElementById('lleva_contabilidad');
//     let checkboxprofesional = document.getElementById("profesionales");
//     let noObligadosFieldset = document.getElementById('no_obligados_fieldset');
//     let obligadosFieldset = document.getElementById('obligados_fieldset');

//     if (checkbox.checked) {
//         // Mostrar el fieldset para "Obligados a llevar contabilidad"
//         obligadosFieldset.classList.remove("d-none");
//         obligadosFieldset.classList.add("d-flex"); // Para que respete la estructura de Bootstrap
//         noObligadosFieldset.classList.add("d-none"); // Ocultar el otro fieldset
//         checkboxprofesional.checked = false;
//     } else {
//         // Mostrar el fieldset para "No obligados a llevar contabilidad"
//         noObligadosFieldset.classList.remove("d-none");
//         noObligadosFieldset.classList.add("d-flex");
//         obligadosFieldset.classList.add("d-none");
//         checkboxprofesional.checked = false;
//     }
// }


    // Ejecutar la función al cambiar el estado del checkbox
    checkbox.addEventListener('change', toggleFieldsets);

    // Ejecutar la función al cargar la página para establecer el estado inicial
    window.onload = toggleFieldsets;

    document.addEventListener("DOMContentLoaded", function() {
    // Ejecutar la llamada de Axios cuando el DOM esté listo
    @if (old('catastro_id'))
        @if (old('catastro_id') != '')
        seleccionarcontribuyentesinactividad({{old('catastro_id')}});
        @endif
    @endif

    document.addEventListener('keydown', function (event) {
            // Verifica si la tecla presionada es ArrowDown o ArrowUp
            if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                // Prevenir el comportamiento por defecto
                event.preventDefault();

                // Seleccionar todos los inputs con la clase "navegable"
                const inputs = Array.from(document.querySelectorAll('.input-decimales'));

                // Obtener el input actualmente enfocado
                const currentIndex = inputs.findIndex(input => input === document.activeElement);

                if (currentIndex !== -1) {
                    let nextIndex;
                    if (event.key === 'ArrowDown') {
                        // Siguiente índice (baja al siguiente input)
                        nextIndex = (currentIndex + 1) % inputs.length;
                    } else if (event.key === 'ArrowUp') {
                        // Índice anterior (sube al input anterior)
                        nextIndex = (currentIndex - 1 + inputs.length) % inputs.length;
                    }

                    // Enfocar el siguiente o anterior input
                    inputs[nextIndex].focus();
                }
            }
        });
    });

    

    document.getElementById("impuesto_1punto5").addEventListener("change", function() {
       
        LimpiarTotalActivo()
        LimpiarTotalPatente()

        calculaPatente()
    });


   
    document.getElementById("lleva_contabilidad").addEventListener("change", function() {
        document.getElementById("cont_exoneracion").value= parseFloat(Math.round(0.00)).toFixed(2);
        // document.getElementById("cont_pf").value = parseFloat(Math.round(0.00)).toFixed(2);
        document.getElementById("cont_impuesto").value = parseFloat(Math.round(0.00)).toFixed(2);
        document.getElementById("cont_sta").value = parseFloat(Math.round(0.00)).toFixed(2);
        document.getElementById("cont_pago_patente").value = parseFloat(Math.round(0.00)).toFixed(2);

        $('.desabilita_txt ').val('')
        $('#cont_total_percibidos_sv').val(100)

        let checkboxlleva_contabilidad = document.getElementById("lleva_contabilidad");
        if (checkboxlleva_contabilidad.checked) {
            LlevaContabilidad=1         
        }else{
            LlevaContabilidad=0        
        }
        LimpiarTotalActivo()
        LimpiarTotalPatente()

        // calculaPatente()
    });

    document.getElementById("calificacion_artesanal").addEventListener("change", function() {
        
        let checkbox = document.getElementById("calificacion_artesanal");
        let checkbox2 = document.getElementById("tercera_edad");
        if (checkbox.checked) {
            checkbox2.checked=false    
            TerceraEdadSeleccionado=0          
        }
        LimpiarTotalActivo()
        LimpiarTotalPatente()

        calculaPatente()
    });
    globalThis.TerceraEdadSeleccionado=0
    document.getElementById("tercera_edad").addEventListener("change", function() {
        let checkbox = document.getElementById("calificacion_artesanal");
        let checkbox2 = document.getElementById("tercera_edad");
        if (checkbox2.checked) {
            checkbox.checked=false   
            TerceraEdadSeleccionado=1         
        }else{
            TerceraEdadSeleccionado=0        
        }
        calculaPatente()
    });

    globalThis.EsProfesional=0
    document.getElementById("profesionales").addEventListener("change", function() {
        let checkboxlleva_contabilidad = document.getElementById("lleva_contabilidad");
        let checkboxprofesional = document.getElementById("profesionales");
        if (checkboxprofesional.checked) {
            checkboxlleva_contabilidad.checked=false   
            EsProfesional=1     
            $('#act_caja_banco').val(0.00)   
            $('#act_ctas_cobrar').val(0.00)    
            $('#act_inv_mercaderia').val(0)   
            $('#act_vehiculo_maquinaria').val(0)   
            $('#act_equipos_oficinas').val(0)   
            $('#act_edificios_locales').val(0)   
            $('#act_terrenos').val(0)   
            $('#act_total_activos').val(0)   
            $('#pas_ctas_dctos_pagar').val(0)   
            $('#pas_obligaciones_financieras').val(0)   
            $('#pas_otras_ctas_pagar').val(0)   
            $('#pas_otros_pasivos').val(0)   
            $('#pas_total_pasivos').val(0)   
            $('#patrimonio_total').val(0)   
            // $('#act_caja_banco').val(0)   
        }else{
            EsProfesional=0        
        }
        // Mostrar el fieldset para "No obligados a llevar contabilidad"
        noObligadosFieldset.style.display = 'none';
        // Ocultar el fieldset para "Obligados a llevar contabilidad"
        obligadosFieldset.style.display = 'none';
        calculaPatente()
    });

    document.getElementById("cont_total_percibidos_sv").addEventListener("blur", function() {
        let anio_declaracion=$('#year_declaracion').val() 
        valor=$('#cont_total_patrimonio').val()

        if(valor=="" || anio_declaracion==""){return}
        let porcentaje=$('#cont_total_percibidos_sv').val()
        if(porcentaje>100 || porcentaje<=0){
            alertNotificar("El porcentaje debe ser mayor a cero y menor o igual a 100","error")
            return
        }
        calculaPatente()
    });


    function calculaPatente(){
        LimpiarTotalActivo()
        LimpiarTotalPatente()
        let checkbox1 = document.getElementById("lleva_contabilidad");
        let anio_declaracion=$('#year_declaracion').val()
        let obligado="";
        let valor=0;
        let checkboxprofesional = document.getElementById("profesionales");
       
        if (checkbox1.checked) {
            obligado="S"
            let porcentaje=$('#cont_total_percibidos_sv').val()
            valor=$('#cont_total_patrimonio').val()
            valor = Number(valor) * Number(porcentaje/100)
            if(valor==0 || anio_declaracion==""){return}
        } else {
            obligado="N"
            valor=$('#patrimonio_total').val()
            
            if(!checkboxprofesional.checked){
                if(valor==0 || anio_declaracion==""){return}
            }
        }
        // var lc=document.getElementById("local_propio").value 
        // if(lc==""){
        //     alert("Seleccione el contribuyente")
        //     return
        // }
        
        var impuesto=0;
        var exoneracion=0
        var sta=0
        var pf=0
        var total=0
        document.getElementById("cont_exoneracion").value =parseFloat(Math.round(0.00)).toFixed(2);

        // alert("s")
        if(checkboxprofesional.checked){

            let checkbox_art = document.getElementById("calificacion_artesanal");
            let checkbox_te = document.getElementById("tercera_edad");
            let porcentaje_exon=0
            let exone=0
            if (checkbox_art.checked) {
               
                let exone= $('#cont_impuesto').val()/2
            
                document.getElementById("cont_exoneracion").value =exone.toFixed(2);
                porcentaje_exon=50
            }   
          
            else if (checkbox_te.checked) {               
                let exone= $('#cont_impuesto').val()   
            
                document.getElementById("cont_exoneracion").value =exone; 
                porcentaje_exon=100                  
                
            }
           
            valor=0
            var exoneracion=0
            // document.getElementById("cont_exoneracion").value= parseFloat(exone).toFixed(2);
            // document.getElementById("cont_pf").value = parseFloat(Math.round(5.00)).toFixed(2);
            document.getElementById("cont_impuesto").value = parseFloat(Math.round(30.00)).toFixed(2);
            // $('#cont_impuesto').val(parseFloat(Math.round(30.00)).toFixed(2))
            if(LocalComercial=="Desconocido"){
                // document.getElementById("cont_pf").value =parseFloat(Math.round(5.00)).toFixed(2);
            }
           

            document.getElementById("cont_sta").value = parseFloat(Math.round(5.00)).toFixed(2);

            exoneracion=document.getElementById("cont_exoneracion").value
            sta=document.getElementById("cont_sta").value
            // pf=document.getElementById("cont_pf").value
            impuesto=document.getElementById("cont_impuesto").value
            // alert(impuesto)

            total=(Number(impuesto) + Number(sta) + Number(pf)) - Number(exoneracion)
            document.getElementById("cont_pago_patente").value =total.toFixed(2);

            return
        }else {
            // valor=0

            document.getElementById("cont_exoneracion").value= parseFloat(Math.round(0.00)).toFixed(2);
            // document.getElementById("cont_pf").value = parseFloat(Math.round(0.00)).toFixed(2);
            document.getElementById("cont_impuesto").value = parseFloat(Math.round(0.00)).toFixed(2);
            document.getElementById("cont_sta").value = parseFloat(Math.round(0.00)).toFixed(2);
            document.getElementById("cont_pago_patente").value = parseFloat(Math.round(0.00)).toFixed(2);
            // $('.desabilita_txt ').val('')

            let checkboxlleva_contabilidas = document.getElementById("lleva_contabilidad");
            if(!checkboxlleva_contabilidas.checked){
                
                document.getElementById("cont_exoneracion").value= parseFloat(Math.round(0.00)).toFixed(2);
                // document.getElementById("cont_pf").value = parseFloat(Math.round(0.00)).toFixed(2);
                document.getElementById("cont_impuesto").value = parseFloat(Math.round(0.00)).toFixed(2);
                document.getElementById("cont_sta").value = parseFloat(Math.round(0.00)).toFixed(2);
                document.getElementById("cont_pago_patente").value = parseFloat(Math.round(0.00)).toFixed(2);

                // return
            }
           
        }
       
        vistacargando("m","Espere por favor")
       
        $.get("patente/calcular-impuesto/"+valor+"/"+obligado+"/"+anio_declaracion+"/"+TerceraEdadSeleccionado, function(data){
            console.log(data)
            vistacargando("")
            if(data.error==true){
                // alertNotificar(data.mensaje,"error");
                alert(data.mensaje);
                return;   
            }
           
            impuesto=data.calcular.sumar_total;
            if(impuesto>25000){
                impuesto=parseFloat(Math.round(25000.00)).toFixed(2);
            }
            $('#cont_impuesto').val(impuesto)
            let checkbox = document.getElementById("calificacion_artesanal");
            let checkbox2 = document.getElementById("tercera_edad");
            let porcentaje_exon=0
            if (checkbox.checked) {
               
                let exone= impuesto/2
                document.getElementById("cont_exoneracion").value =exone.toFixed(2);
                porcentaje_exon=50
            }   
          
            if (checkbox2.checked) {
               
                if(data.calcular.aplica==0){
                    let exone= impuesto   
                    document.getElementById("cont_exoneracion").value =exone; 
                    porcentaje_exon=100
                }else{
                    // document.getElementById("cont_exoneracion").value =data.calcular.comprobar; 
                }             
                
            }
           
            // document.getElementById("cont_pf").value = parseFloat(Math.round(5.00)).toFixed(2);
            if(LocalComercial=="Desconocido"){
                // document.getElementById("cont_pf").value =parseFloat(Math.round(5.00)).toFixed(2);
            }

            
            let checkboxImpAct = document.getElementById("impuesto_1punto5");
            let calculaActivo=0
            if (checkboxImpAct.checked) {
                calculaActivo=1
           }
        
            var patente = parseFloat(Math.round(0.00)).toFixed(2);

            document.getElementById("cont_sta").value = parseFloat(Math.round(5.00)).toFixed(2);

            exoneracion=document.getElementById("cont_exoneracion").value
            sta=document.getElementById("cont_sta").value
            // pf=document.getElementById("cont_pf").value
            pf=0;
           
            total=(Number(impuesto) + Number(sta) + Number(pf)) - Number(exoneracion)
            document.getElementById("cont_pago_patente").value =total.toFixed(2);

            if(obligado=="S" && calculaActivo==1){
                calculaActivoTotales(valor,porcentaje_exon)
            }

          
        }).fail(function(){
            vistacargando("")
            alert("Se produjo un error, por favor intentelo más tarde");  
        });

    }
    function calculaActivoTotales(valor,porcentaje_exon){
        let activo_total=Number(valor) * (1.5/1000)

        document.getElementById("cont_sta").value = parseFloat(Math.round(5.00)).toFixed(2);

        let exoneracion_act=Number(valor) * Number(porcentaje_exon/100)
        let sta_act=document.getElementById("cont_sta").value
        // let pf_act=document.getElementById("cont_pf").value
        let pf_act=0

        document.getElementById("cont_exoneracion_act").value = parseFloat(Math.round(exoneracion_act * 100) / 100).toFixed(2);;
        document.getElementById("cont_impuesto_act").value = Math.round(activo_total * 100) / 100;
        document.getElementById("cont_sta_act").value =Math.round(sta_act * 100) / 100;; 
        // document.getElementById("cont_pf_act").value =Math.round(pf_act * 100) / 100;; 
        
        total=(Number(activo_total) + Number(sta_act) + Number(pf_act)) - Number(exoneracion_act)
        document.getElementById("cont_pago_activo_total").value =total.toFixed(2);

    }

    function LimpiarTotalActivo(){
        document.getElementById("cont_exoneracion_act").value = "";
        document.getElementById("cont_impuesto_act").value ="";
        document.getElementById("cont_pago_activo_total").value ="";
        document.getElementById("cont_sta_act").value ="";
        
    }

    function LimpiarTotalPatente(){
        
        document.getElementById("cont_impuesto").value = "";
        document.getElementById("cont_sta").value = "";
        document.getElementById("cont_exoneracion").value=""
        document.getElementById("cont_pago_patente").value ="";
    }

    $("#formPatente").submit(function(e){
       
        e.preventDefault();
        
        //validamos los campos obligatorios
        let catastro_id=$('#catastro_id').val()

           
        if(catastro_id=="" || catastro_id==null){
            alertNotificar("Debe seleccionar un contribuyente","error")
            return
        } 

        var num_locales = $("#tablaLocales tbody tr").length;
       
        let actividadesSeleccionados = $("input[name^='actividades']:checked").length;
            
        if (actividadesSeleccionados === 0 ) {
            alertNotificar("Debe seleccionar al menos una actividad", "error");
            return;
        }

        let localesSeleccionados = $("input[name^='locales']:checked").length;
            
        if (localesSeleccionados === 0 || localesSeleccionados >1) {
            alertNotificar("Debe seleccionar un local", "error");
            return;
        }

        let anio_declaracion=$('#year_declaracion').val()
           
        if(anio_declaracion=="" || anio_declaracion==null){
            alertNotificar("Debe seleccionar el año de declaracion","error")
            return
        } 

        let fecha_declaracion=$('#fecha_declaracion').val()
           
        if(fecha_declaracion=="" || fecha_declaracion==null){
            alertNotificar("Debe seleccionar la fecha de declaracion","error")
            return
        } 

        if(EsProfesional==0){
            
            if(LlevaContabilidad==0){
                let act_caja_banco=$('#act_caja_banco').val()
           
                if(act_caja_banco=="" || act_caja_banco==null){
                    alertNotificar("Debe ingresar el activo caja y bancos","error")
                    $('#act_caja_banco').focus()
                    return
                } 

                let act_ctas_cobrar=$('#act_ctas_cobrar').val()
                if(act_ctas_cobrar=="" || act_ctas_cobrar==null){
                    alertNotificar("Debe ingresar el activo cuentas por cobrar","error")
                    $('#act_ctas_cobrar').focus()
                    return
                } 

                let act_inv_mercaderia=$('#act_inv_mercaderia').val()
                if(act_inv_mercaderia=="" || act_inv_mercaderia==null){
                    alertNotificar("Debe ingresar el activo inventario de mercaderia","error")
                    $('#act_inv_mercaderia').focus()
                    return
                }
                
                let act_vehiculo_maquinaria=$('#act_vehiculo_maquinaria').val()
                if(act_vehiculo_maquinaria=="" || act_vehiculo_maquinaria==null){
                    alertNotificar("Debe ingresar el activo vehiculo y maquinaria","error")
                    $('#act_vehiculo_maquinaria').focus()
                    return
                }

                let act_equipos_oficinas=$('#act_equipos_oficinas').val()
                if(act_equipos_oficinas=="" || act_equipos_oficinas==null){
                    alertNotificar("Debe ingresar el activo equipos de oficina","error")
                    $('#act_equipos_oficinas').focus()
                    return
                }

                let act_edificios_locales=$('#act_edificios_locales').val()
                if(act_edificios_locales=="" || act_edificios_locales==null){
                    alertNotificar("Debe ingresar el activo edificios y locales","error")
                    $('#act_edificios_locales').focus()
                    return
                }

                let act_terrenos=$('#act_terrenos').val()
                if(act_terrenos=="" || act_terrenos==null){
                    alertNotificar("Debe ingresar el activo terrenos","error")
                    $('#act_terrenos').focus()
                    return
                }

                let pas_ctas_dctos_pagar=$('#pas_ctas_dctos_pagar').val()
                if(pas_ctas_dctos_pagar=="" || pas_ctas_dctos_pagar==null){
                    alertNotificar("Debe ingresar el pasivo cuentas y documentos por pagar","error")
                    $('#pas_ctas_dctos_pagar').focus()
                    return
                }


                let pas_obligaciones_financieras=$('#pas_obligaciones_financieras').val()
                if(pas_obligaciones_financieras=="" || pas_obligaciones_financieras==null){
                    alertNotificar("Debe ingresar el pasivo obligaciones financieras","error")
                    $('#pas_obligaciones_financieras').focus()
                    return
                }

                let pas_otras_ctas_pagar=$('#pas_otras_ctas_pagar').val()
                if(pas_otras_ctas_pagar=="" || pas_otras_ctas_pagar==null){
                    alertNotificar("Debe ingresar el pasivo cuentas por pagar","error")
                    $('#pas_otras_ctas_pagar').focus()
                    return
                }

                let pas_otros_pasivos=$('#pas_otros_pasivos').val()
                if(pas_otros_pasivos=="" || pas_otros_pasivos==null){
                    alertNotificar("Debe ingresar el pasivo otros pasivos","error")
                    $('#pas_otros_pasivos').focus()
                    return
                }


            }else{
                let cont_total_activos=$('#cont_total_activos').val()
                if(cont_total_activos=="" || cont_total_activos==null){
                    alertNotificar("Debe ingresar el total de activos","error")
                    $('#cont_total_activos').focus()
                    return
                }

                let cont_total_pasivos=$('#cont_total_pasivos').val()
                if(cont_total_pasivos=="" || cont_total_pasivos==null){
                    alertNotificar("Debe ingresar el total de pasivos","error")
                    $('#cont_total_pasivos').focus()
                    return
                }

                // if(Emitir==1){
                    let cont_form_sri=$('#cont_form_sri').val()
                    if(cont_form_sri=="" || cont_form_sri==null){
                        alertNotificar("Debe ingresar numero formulario SRI","error")
                        $('#cont_form_sri').focus()
                        return
                    }

                    let cont_original=$('#cont_original').val()
                    if(cont_original=="" || cont_original==null){
                        alertNotificar("Debe seleccionar si es Original o Sustitutiva","error")
                        return
                    }

                    let archivo_patente=$('#archivo_patente').val()
                    if(archivo_patente=="" || archivo_patente==null){
                        alertNotificar("Debe seleccionar el documento","error")
                        return
                    }
                // }

            }
        }
        // var ruta="patente/simular-patente"
        // if(Emitir==1){
        //     ruta="patente"
        // }

        var ruta="patente"
        
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
                if(Emitir==1){
                    limpiarCampos()
                }
                alertNotificar(data.mensaje,"success");
                crearPdf(data.id)
                 
            }, error:function (data) {
                console.log(data)

                vistacargando("");     
                alertNotificar('Ocurrió un error','error');
            }
        });
    })

    function crearPdf(id){
        vistacargando("m","Espere por favor")
        $.get('patente/reporte/'+id, function(data){
            vistacargando("")
            if(data.error==true){
                // alertNotificar(data.mensaje,"error");
                alert(data.mensaje);
                return;   
            }

            alertNotificar("El documento se descargara en unos segundos","success")
            verpdf(data.pdf)

        }).fail(function(){
            vistacargando("")
            alertNotificar('Ocurrió un error','error');
        });
    }

</script>
@endpush
