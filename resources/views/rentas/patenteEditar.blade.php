@extends('layouts.appv2')
@section('title', 'Editar patente')
@push('styles')
<style>
.checkbox-grande {
    transform: scale(1.5); /* Ajusta el número para el tamaño deseado */
    margin-right: 8px; /* Ajuste opcional */
}
</style>

@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Editar declaracion de patente</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
        <button type="button" class="btn btn-sm btn-primary d-flex align-items-center gap-1 me-2" onclick="enviarFormulario()">
            Actualizar Declaracion
        </button>
        <a href="{{ route('create.patente') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
            Nuevo Declaracion
        </a>
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

<form id="formPatente" method="POST" action="{{route('update.patente',['id' => $patente->id])}}">
    @csrf
    @method('PATCH')
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
                    <div class="row mb-3">
                        <label for="nombre_contribuyente" class="col-md-4 col-form-label fw-bold">Fantasia comercial</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="nombre_contribuyente"  disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
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
                    </div>
                    <div class="row mb-3">
                        <label for="correo_electronico" class="col-md-4 col-form-label fw-bold">Correo electrónico</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="correo_electronico" maxlength="15" disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <label for="estado_establecimiento" class="col-md-4 col-form-label fw-bold">Estado establecimiento</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="estado_establecimiento"  disabled>
                        </div>
                    </div>

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
                    <div class="row mb-3">
                        <label for="local_propio" class="col-md-4 col-form-label fw-bold">Establecimiento Propio/Arrendado</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control form-control-sm" id="local_propio"  disabled>
                        </div>
                    </div>
                </div>
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
                                <input type="number" class="form-control {{$errors->has('act_caja_banco') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_caja_banco" name="act_caja_banco" value="{{old('act_caja_banco')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_ctas_cobrar" class="col-sm-7 col-form-label">Cuentas por Cobrar</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('act_ctas_cobrar') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_ctas_cobrar" name="act_ctas_cobrar" value="{{old('act_ctas_cobrar')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_inv_mercaderia" class="col-sm-7 col-form-label">Inventario de Mercadería</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('act_inv_mercaderia') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_inv_mercaderia" name="act_inv_mercaderia" value="{{old('act_inv_mercaderia')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_vehiculo_maquinaria" class="col-sm-7 col-form-label">Vehículos y Maquinaria</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('act_vehiculo_maquinaria') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_vehiculo_maquinaria" name="act_vehiculo_maquinaria" value="{{old('act_vehiculo_maquinaria')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_equipos_oficinas" class="col-sm-7 col-form-label">Equipos de Oficina</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('act_equipos_oficinas') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_equipos_oficinas" name="act_equipos_oficinas" value="{{old('act_equipos_oficinas')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_edificios_locales" class="col-sm-7 col-form-label">Edificios y Locales</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('act_edificios_locales') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_edificios_locales" name="act_edificios_locales" value="{{old('act_edificios_locales')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_terrenos" class="col-sm-7 col-form-label">Terrenos</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('act_terrenos') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad" id="act_terrenos" name="act_terrenos" value="{{old('act_terrenos')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="act_total_activos" class="col-sm-7 col-form-label">Total Activos (A)</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <button class="btn btn-outline-secondary" type="button" onclick="sumarValores('obligado_contabilidad', 'act_total_activos')">+</button>
                                <input type="number" class="form-control {{$errors->has('act_total_activos') ? 'is-invalid' : ''}} input-decimales totales_pasivos_activos" id="act_total_activos" name="act_total_activos" value="{{old('act_total_activos')}}">
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
                                <input type="number" class="form-control {{$errors->has('pas_ctas_dctos_pagar') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_ctas_dctos_pagar" name="pas_ctas_dctos_pagar" value="{{old('pas_ctas_dctos_pagar')}}">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3 row">
                        <label for="pas_obligaciones_financieras" class="col-sm-7 col-form-label">Obligaciones Financieras</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('pas_obligaciones_financieras') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_obligaciones_financieras" name="pas_obligaciones_financieras" value="{{old('pas_obligaciones_financieras')}}">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3 row">
                        <label for="pas_otras_ctas_pagar" class="col-sm-7 col-form-label">Otras Cuentas por Pagar</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('pas_otras_ctas_pagar') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_otras_ctas_pagar" name="pas_otras_ctas_pagar" value="{{old('pas_otras_ctas_pagar')}}">
                            </div>
                        </div>
                    </div>
                    <div class="mb-3 row">
                        <label for="pas_otros_pasivos" class="col-sm-7 col-form-label">Otros Pasivos</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control {{$errors->has('pas_otros_pasivos') ? 'is-invalid' : ''}} input-decimales obligado_contabilidad_pasivos" id="pas_otros_pasivos" name="pas_otros_pasivos" value="{{old('pas_otros_pasivos')}}">
                            </div>
                        </div>
                    </div>

                    <div class="mb-3 row">
                        <label for="pas_total_pasivos" class="col-sm-7 col-form-label">Total Pasivos (B)</label>
                        <div class="col-sm-5">
                            <div class="input-group">
                                <button type="button" class="btn btn-outline-secondary" onclick="sumarValores('obligado_contabilidad_pasivos', 'pas_total_pasivos')">+</button>
                                <input type="number" class="form-control {{$errors->has('pas_total_pasivos') ? 'is-invalid' : ''}} input-decimales totales_pasivos_activos" id="pas_total_pasivos" name="pas_total_pasivos" value="{{old('pas_total_pasivos')}}">
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
                                <input type="number" class="form-control {{$errors->has('patrimonio_total') ? 'is-invalid' : ''}} input-decimales totales_pasivos_activos" id="patrimonio_total" name="patrimonio_total" value="{{old('patrimonio_total')}}">
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>
        </div>
    </fieldset>

    <!-- declaracion obligados a llevar contabilidad -->
    <fieldset class="border p-3 mb-4" id="obligados_fieldset" style="display:none;">
        <legend class="float-none w-auto px-3 fs-5">Declaracion tributaria obligados a llevar contabilidad</legend>
        <div class="row">
            <div class="col-md-6 mb-3">
                <div class="mb-3 row">
                    <label for="cont_total_activos" class="col-sm-6 col-form-label">Total activos</label>
                    <div class="col-sm-6">
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" class="form-control {{$errors->has('cont_total_activos') ? 'is-invalid' : ''}} input-decimales cont_pasivos_activos" id="cont_total_activos" name="cont_total_activos" value="{{old('cont_total_activos')}}">
                        </div>
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="cont_total_pasivos" class="col-sm-6 col-form-label">(-) Total Pasivos</label>
                    <div class="col-sm-6">
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" class="form-control {{$errors->has('cont_total_pasivos') ? 'is-invalid' : ''}} input-decimales cont_pasivos_activos" name="cont_total_pasivos" id="cont_total_pasivos" value="{{old('cont_total_pasivos')}}">
                        </div>
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="cont_total_patrimonio" class="col-sm-6 col-form-label">Patrimonio</label>
                    <div class="col-sm-6">
                        <div class="input-group mb-3">
                            <button class="btn btn-outline-secondary" type="button" id="button-addon2" onclick="restarValores('cont_pasivos_activos', 'cont_total_patrimonio')">=</button>
                            <input type="number" class="form-control {{$errors->has('cont_total_patrimonio') ? 'is-invalid' : ''}} input-decimales" id="cont_total_patrimonio" name="cont_total_patrimonio" value="{{old('cont_total_patrimonio')}}" aria-label="Example text with button addon" aria-describedby="button-addon2">
                        </div>
                    </div>
                </div>

            </div>
            <div class="col-md-6 mb-3">
                <div class="mb-3 row">
                    <label for="cont_form_sri" class="col-sm-6 col-form-label">Formulario SRI (No.)</label>
                    <div class="col-sm-6">
                        <input type="number" class="form-control {{$errors->has('cont_form_sri') ? 'is-invalid' : ''}}" id="cont_form_sri" name="cont_form_sri" value="{{old('cont_form_sri')}}">
                    </div>
                </div>

                <div class="mb-3 row">
                    <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Original/sustitutiva</label>
                    <div class="col-sm-6">
                        <select class="form-select {{$errors->has('cont_original') ? 'is-invalid' : ''}}" id="cont_original" name="cont_original">
                            <option value="">Seleccione</option>
                            <option value="1" {{ old('cont_original') == '1' ? 'selected' : '' }}>Original</option>
                            <option value="2" {{ old('cont_original') == '2' ? 'selected' : '' }}>Sustitutiva</option>
                        </select>
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Total de ingresos percibidos en el Canton San Vicente</label>
                    <div class="col-sm-6">
                        <input type="number" class="form-control {{$errors->has('cont_total_percibidos_sv') ? 'is-invalid' : ''}}" id="cont_total_percibidos_sv" name="cont_total_percibidos_sv" value="{{old('cont_total_percibidos_sv')}}">
                    </div>
                </div>
            </div>
        </div>

    </fieldset>
</form>
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
        document.getElementById("catastro_id").value = "";
    }

    function enviarFormulario() {
            // Selecciona el formulario y lo envía
            document.getElementById("formPatente").submit();
    }
    // funcion para seleccionar contribuyente y cargar los datos
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
                document.getElementById("catastro_id").value = data.id;
                document.getElementById("buscar_contribuyente").value = data.ruc;
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
                document.getElementById("catastro_id").value = data.id;
                document.getElementById("buscar_contribuyente").value = data.ruc;
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
    //una vez cargados los contribuyentes esta funcion hace cargar las actividades asociadas
    function seleccionarActividad(id, nombre, ciiu) {
        // Crear un nuevo elemento <tr>
        limpiarTabla();
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
    }

    function restarValores(claseInputs, idExclusion) {
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

        // Asigna el total al campo excluido
        document.getElementById(idExclusion).value = total.toFixed(2);
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
        if (checkbox.checked) {
            // Mostrar el fieldset para "Obligados a llevar contabilidad"
            obligadosFieldset.style.display = 'block';
            // Ocultar el fieldset para "No obligados a llevar contabilidad"
            noObligadosFieldset.style.display = 'none';
        } else {
            // Mostrar el fieldset para "No obligados a llevar contabilidad"
            noObligadosFieldset.style.display = 'block';
            // Ocultar el fieldset para "Obligados a llevar contabilidad"
            obligadosFieldset.style.display = 'none';
        }
    }

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
</script>
@endpush
