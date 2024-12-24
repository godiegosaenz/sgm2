@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
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
        <h3 class="h2">Registrar contribuyente de Patentes</h3>
        <div class="btn-toolbar mb-2 mb-md-0">
            <button type="button" class="btn btn-sm btn-primary d-flex align-items-center gap-1 me-2" onclick="enviarFormulario()">
                Guardar Contribuyente
            </button>
            <a href="{{ route('create.catastro') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
                Nuevo Contribuyente
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
    <!-- Mensaje general de errores -->
    @if($errors->any())
        <div class="alert alert-danger">
            "Por favor, revisa los campos obligatorios y corrige los errores indicados para poder continuar."
        </div>
    @endif
    <form id="formularioCatastroContribuyente" method="POST" action="{{route('store.catastro')}}" class="needs-validation" novalidate enctype="multipart/form-data" class="position-relative">
        @csrf
        <!-- Propietario y Representante Legal -->
        <fieldset class="border p-3 mb-4">
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="propietario" class="form-label">Propietario <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <input type="number" class="form-control {{$errors->has('propietario_id') ? 'is-invalid' : ''}}" id="propietario" name="propietario" placeholder="Cedula del propietario" value="{{old('propietario')}}" required>
                        <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#propietarioModal">
                            Buscar
                            <span id="spinner" class="spinner-border spinner-border-sm" style="display:none;"></span>
                        </button>
                        <div class="invalid-feedback">
                            @if($errors->has('propietario_id'))
                                {{$errors->first('propietario_id')}}
                            @endif
                        </div>
                    </div>
                </div>
                <input type="hidden" id="propietario_id" name="propietario_id" value="{{old('propietario_id')}}">
                <div class="col-md-6 mb-3">
                    <label for="representante" class="form-label">Representante Legal</label>
                    <div class="input-group">
                        <input type="number" class="form-control" id="representante" name="representante" placeholder="Cedula del representante legal" value="{{old('representante')}}">
                        <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#representanteModal">
                            Buscar
                            <span id="spinnerrepresentante" class="spinner-border spinner-border-sm" style="display:none;"></span>
                        </button>
                    </div>
                </div>
                <input type="hidden" id="representante_id" name="representante_id" value="{{old('representante_id')}}">
                <div class="col-md-6 mb-3">
                    <label for="razon_social" class="form-label">Nombres y apellidos (Propietario)</label>
                    <input type="text" class="form-control" id="nombresPropietario" name="nombresPropietario" maxlength="255" value="{{old('nombresPropietario2')}}" disabled>
                    <input type="hidden" class="form-control" id="nombresPropietario2" name="nombresPropietario2" maxlength="255" value="{{old('nombresPropietario2')}}">
                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Nombres y apellidos (rep. legal)</label>
                    <input type="text" class="form-control" id="nombresRepresentante" maxlength="255" value="{{old('nombresRepresentante2')}}" disabled>
                    <input type="hidden" class="form-control" id="nombresRepresentante2" name="nombresRepresentante2" value="{{old('nombresRepresentante2')}}" maxlength="255">
                </div>
            </div>
        </fieldset>
        <!-- Información básica -->
        <fieldset class="border p-3 mb-4">
            <legend class="w-auto">Información Básica</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="ruc" class="form-label">RUC <span class="text-danger">*</span></label>
                    <input type="number" class="form-control {{$errors->has('ruc') ? 'is-invalid' : ''}}" id="ruc" name="ruc" maxlength="13" value="{{old('ruc')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('ruc'))
                            {{$errors->first('ruc')}}
                        @else
                        El campo ruc es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="razon_social" class="form-label">Razón Social  <span class="text-danger">*</span></label>
                    <input type="text" class="form-control {{$errors->has('razon_social') ? 'is-invalid' : ''}}" id="razon_social" name="razon_social" maxlength="255" value="{{old('razon_social')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('razon_social'))
                            {{$errors->first('razon_social')}}
                        @else
                        El campo Razon Social es requerido
                        @endif
                    </div>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="estado_contribuyente_id" class="form-label">Estado Contribuyente <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('estado_contribuyente_id') ? 'is-invalid' : ''}}" id="estado_contribuyente_id" name="estado_contribuyente_id" required>
                        <option value="">Seleccione un estado</option>
                        <option value="1" {{ old('estado_contribuyente_id') == '1' ? 'selected' : '' }}>Activo</option>
                        <option value="2" {{ old('estado_contribuyente_id') == '2' ? 'selected' : '' }}>Inactivo</option>
                        <option value="3" {{ old('estado_contribuyente_id') == '3' ? 'selected' : '' }}>Suspendido</option>
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('estado_contribuyente_id'))
                            {{$errors->first('estado_contribuyente_id')}}
                        @else
                        El campo estado contribuyente es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombre_comercial" class="form-label">Nombre Fantasia Comercial </label>
                    <input type="text" class="form-control" id="nombre_comercial" name="nombre_comercial" maxlength="255" value="{{old('nombre_comercial')}}">
                </div>
                <div class="col-md-6 mb-3">
                    <label for="clase_contribuyente_id" class="form-label">Clase Contribuyente <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('clase_contribuyente_id') ? 'is-invalid' : ''}}" id="clase_contribuyente_id" name="clase_contribuyente_id" required>
                        <option value="">Seleccione una clase</option>
                        @foreach ($clase as $c)
                        <option value="{{$c->id}}" {{ old('clase_contribuyente_id') == $c->id ? 'selected' : '' }}>{{$c->nombre}}</option>
                        @endforeach
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('clase_contribuyente_id'))
                            {{$errors->first('clase_contribuyente_id')}}
                        @else
                        El campo clase contribuyente es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="tipo_contribuyente_id" class="form-label">Tipo Contribuyente <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('tipo_contribuyente_id') ? 'is-invalid' : ''}}" id="tipo_contribuyente_id" name="tipo_contribuyente_id" required>
                        <option value="">Seleccione un tipo</option>
                        <option value="1" {{ old('tipo_contribuyente_id') == '1' ? 'selected' : '' }}>Persona natural</option>
                        <option value="2" {{ old('tipo_contribuyente_id') == '2' ? 'selected' : '' }}>Sociedades</option>
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('tipo_contribuyente_id'))
                            {{$errors->first('tipo_contribuyente_id')}}
                        @else
                        El campo Tipo contribuyente es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="estado_establecimiento_id" class="form-label">Estado Establecimiento <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('estado_establecimiento_id') ? 'is-invalid' : ''}}" id="estado_establecimiento_id" name="estado_establecimiento_id" required>
                        <option value="">Seleccione un estado</option>
                        <option value="1" {{ old('estado_establecimiento_id') == '1' ? 'selected' : '' }}>Abierto</option>
                        <option value="2" {{ old('estado_establecimiento_id') == '2' ? 'selected' : '' }}>Cerrado</option>
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('estado_establecimiento_id'))
                            {{$errors->first('estado_establecimiento_id')}}
                        @else
                        El campo estado establecimiento es requerido
                        @endif
                    </div>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="tipo_local" class="form-label">Tipo de local <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('estado_establecimiento_id') ? 'is-invalid' : ''}}" id="tipo_local" name="tipo_local" required>
                        <option value="">Seleccione un tipo</option>
                        <option value="1" {{ old('tipo_local') == '1' ? 'selected' : '' }}>Propio</option>
                        <option value="2" {{ old('tipo_local') == '2' ? 'selected' : '' }}>Arrendado</option>
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('tipo_local'))
                            {{$errors->first('tipo_local')}}
                        @else
                        El campo tipo de local es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-2 mb-3 mt-4">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="obligado_contabilidad" name="obligado_contabilidad" value="1" {{ old('obligado_contabilidad') ? 'checked' : '' }}>
                        <label class="form-check-label" for="obligado_contabilidad">Obligado a llevar contabilidad</label>
                    </div>
                </div>
                <div class="col-md-2 mb-3 mt-4">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="es_matriz" name="es_matriz" value="1" {{ old('es_matriz') ? 'checked' : '' }}>
                        <label class="form-check-label" for="es_matriz">¿Es matriz?</label>
                    </div>
                </div>
                <div class="col-md-2 mb-3 mt-4">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="es_turismo" name="es_turismo" value="1" {{ old('es_turismo') ? 'checked' : '' }}>
                        <label class="form-check-label" for="es_turismo">¿Es turismo?</label>
                    </div>
                </div>
            </div>
        </fieldset>
        <!-- Actividad comercial -->
        <fieldset class="border p-3 mb-4">
            <legend class="w-auto">Actividad comercial</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="propietario" class="form-label">Seleccione actividad comercial</label>
                    <div class="input-group">
                        <input type="text" class="form-control {{$errors->has('actividades') ? 'is-invalid' : ''}}" id="actividadcomercial" placeholder="Buscar actividad">
                        <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#actividadModal">Buscar</button>
                        <div class="invalid-feedback">
                            @if($errors->has('actividades'))
                                {{$errors->first('actividades')}}
                            @endif
                        </div>
                    </div>
                </div>

                <div class="col-md-12 mb-3 mt-3">
                    <table class="table table-striped" id="tablaActividades">
                        <thead>
                            <tr>
                                <th scope="col">CIIU</th>
                                <th scope="col">Actividad</th>
                                <th scope="col" class="text-end">Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            @if(old('actividades'))
                                @foreach(old('actividades') as $key => $actividad)
                                    <tr>
                                        <td>
                                            <input type="hidden" name="actividades[{{$key}}][ciiu]" value="{{ $actividad['ciiu'] }}">
                                            {{ $actividad['ciiu'] }}
                                        </td>
                                        <td>
                                            <input type="hidden" name="actividades[{{$key}}][nombre]" value="{{ $actividad['nombre'] }}">
                                            {{ $actividad['nombre'] }}
                                        </td>
                                        <td class="text-end">

                                            <button type="button" class="btn btn-sm btn-danger" onclick="eliminarFila(this)"><i class="bi bi-trash"></i></button>
                                        </td>
                                    </tr>
                                @endforeach
                            @endif
                        </tbody>
                    </table>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="fecha_declaracion" class="form-label">Fecha de inicio actividades</label>
                    <input type="date" class="form-control {{$errors->has('fecha_inicio_actividad') ? 'is-invalid' : ''}}" id="fecha_inicio_actividad" name="fecha_inicio_actividad" value="{{old('fecha_inicio_actividad')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('fecha_inicio_actividad'))
                            {{$errors->first('fecha_inicio_actividad')}}
                        @else
                        El campo fecha de inicio de actividad es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="fecha_declaracion" class="form-label">Fecha de actualizacion actividades</label>
                    <input type="date" class="form-control" id="fecha_actualizacion_actividades" name="fecha_actualizacion_actividades" value="{{old('fecha_actualizacion_actividades')}}">
                </div>
                <div class="col-md-6 mb-3">
                    <label for="fecha_declaracion" class="form-label">Fecha de reinicio actividades</label>
                    <input type="date" class="form-control" id="fecha_reinicio_actividades" name="fecha_reinicio_actividades" value="{{old('fecha_reinicio_actividades')}}">
                </div>
                <div class="col-md-6 mb-3">
                    <label for="fecha_declaracion" class="form-label">Fecha de suspension definitiva</label>
                    <input type="date" class="form-control" id="fecha_suspension_definitiva" name="fecha_suspension_definitiva" value="{{old('fecha_reinicio_actividades')}}">
                </div>
            </div>
        </fieldset>
        <!-- Ubicación -->
        <fieldset class="border p-3 mb-4">
            <legend class="w-auto">Ubicación</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="provincia_id" class="form-label">Provincia <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('provincia_id') ? 'is-invalid' : ''}}" id="provincia_id" name="provincia_id" required>
                        <option value="">Seleccione una provincia</option>
                        @foreach ($PsqlProvincia as $p)
                            <option value="{{$p->id}}" {{ old('provincia_id') == $p->id ? 'selected' : '' }}>{{$p->descripcion}}</option>
                        @endforeach
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('provincia_id'))
                            {{$errors->first('provincia_id')}}
                        @else
                            El campo provincia es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="canton_id" class="form-label">Cantón <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('canton_id') ? 'is-invalid' : ''}}" id="canton_id" name="canton_id" required>
                        <option value="" id="optionSelectCanton">Seleccione un cantón</option>
                        @if(old('provincia_id'))
                            @foreach (session('cantones') as $c)
                                <option value="{{$c->id}}" {{ old('canton_id') == $c->id ? 'selected' : '' }}>{{$c->nombre}}</option>
                            @endforeach
                        @endif
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('canton_id'))
                            {{$errors->first('canton_id')}}
                        @else
                            El campo canton es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="parroquia_id" class="form-label">Parroquia <span class="text-danger">*</span></label>
                    <select class="form-select {{$errors->has('parroquia_id') ? 'is-invalid' : ''}}" id="parroquia_id" name="parroquia_id" required>
                        <option value="" id="optionSelectParroquia">Seleccione una parroquia</option>
                        @if(old('canton_id'))
                            @foreach (session('parroquia') as $p)
                                <option value="{{$p->id}}" {{ old('parroquia_id') == $p->id ? 'selected' : '' }}>{{$p->descripcion}}</option>
                            @endforeach
                        @endif
                    </select>
                    <div class="invalid-feedback">
                        @if($errors->has('parroquia_id'))
                            {{$errors->first('parroquia_id')}}
                        @else
                            El campo parroquia es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="calle_principal" class="form-label">Calle Principal <span class="text-danger">*</span></label>
                    <input type="text" class="form-control {{$errors->has('calle_principal') ? 'is-invalid' : ''}}" id="calle_principal" name="calle_principal" maxlength="255" value="{{old('calle_principal')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('calle_principal'))
                            {{$errors->first('calle_principal')}}
                        @else
                            El campo calle principal es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="calle_secundaria" class="form-label">Calle Secundaria</label>
                    <input type="text" class="form-control {{$errors->has('calle_secundaria') ? 'is-invalid' : ''}}" id="calle_secundaria" name="calle_secundaria" maxlength="255" value="{{old('calle_secundaria')}}">
                    <div class="invalid-feedback">
                        @if($errors->has('calle_secundaria'))
                            {{$errors->first('calle_secundaria')}}
                        @else
                            El campo calle secundaria es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="referencia_ubicacion" class="form-label">Referencia de Ubicación <span class="text-danger">*</span></label>
                    <input type="text" class="form-control {{$errors->has('referencia_ubicacion') ? 'is-invalid' : ''}}" id="referencia_ubicacion" name="referencia_ubicacion" maxlength="255" value="{{old('referencia_ubicacion')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('referencia_ubicacion'))
                            {{$errors->first('referencia_ubicacion')}}
                        @else
                            El campo referencia ubicacion es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="direccion" class="form-label">Dirección</label>
                    <input type="text" class="form-control {{$errors->has('direccion') ? 'is-invalid' : ''}}" id="direccion" name="direccion" maxlength="255" value="{{old('direccion')}}">
                    <div class="invalid-feedback">
                        @if($errors->has('direccion'))
                            {{$errors->first('direccion')}}
                        @endif
                    </div>
                </div>
            </div>
        </fieldset>

        <!-- Contacto -->
        <fieldset class="border p-3 mb-4">
            <legend class="w-auto">Informacion de Contacto</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="correo" class="form-label">Correo electronico <span class="text-danger">*</span></label>
                    <input type="email" class="form-control {{$errors->has('correo') ? 'is-invalid' : ''}}" id="correo" name="correo" value="{{old('correo')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('correo'))
                            {{$errors->first('correo')}}
                        @else
                            El campo correo electronico es requerido
                        @endif
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="telefono" class="form-label">Teléfono celular <span class="text-danger">*</span></label>
                    <input type="tel" class="form-control {{$errors->has('telefono') ? 'is-invalid' : ''}}" id="telefono" name="telefono" maxlength="50" value="{{old('telefono')}}" required>
                    <div class="invalid-feedback">
                        @if($errors->has('telefono'))
                            {{$errors->first('telefono')}}
                        @else
                            El campo telefono celular es requerido
                        @endif
                    </div>
                </div>
            </div>
        </fieldset>

    </form>
    <!-- Modal para Propietario -->
    <div class="modal fade" id="propietarioModal" tabindex="-1" aria-labelledby="propietarioModalLabel" aria-hidden="true">
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
                            <table class="table table-bordered" id="tablaEnte" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th>Acciones</th>
                                        <th>Cedula/RUC</th>
                                        <th>Nombres</th>
                                        <th>Apellidos</th>
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

    <!-- Modal para Representante Legal -->
    <div class="modal fade" id="representanteModal" tabindex="-1" aria-labelledby="representanteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="representanteModalLabel">Buscar Representante Legal</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="tablaEnteRepresentante" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th>Acciones</th>
                                        <th>Cedula/RUC</th>
                                        <th>Nombres</th>
                                        <th>Apellidos</th>
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
                    <button type="button" class="btn btn-primary">Seleccionar</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Modal para Actividad economica -->
    <div class="modal fade" id="actividadModal" tabindex="-1" aria-labelledby="propietarioModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Lista de actividades comerciales</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="tableactividad" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th>Acciones</th>
                                        <th>CIIU</th>
                                        <th>Actividad Comercial</th>
                                        <th>Nivel</th>
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
        tableEnte = $("#tablaEnte").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/ente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                    d.tipo = 'propietario';
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'ci_ruc', name: 'ci_ruc'},
                {width: '',data: 'nombres', name: 'nombres'},
                {width: '',data: 'apellidos', name: 'apellidos'},

            ],
            "fixedColumns" : true
        });
        tablaEnteRepresentante = $("#tablaEnteRepresentante").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/ente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                    d.tipo = 'representante';
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'ci_ruc', name: 'ci_ruc'},
                {width: '',data: 'nombres', name: 'nombres'},
                {width: '',data: 'apellidos', name: 'apellidos'},

            ],
            "fixedColumns" : true
        });

        tableactividad = $("#tableactividad").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": false,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/actividadcomercial/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '10%',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '10%',data: 'ciiu', name: 'ciiu'},
                {width: '60%',data: 'descripcion', name: 'descripcion'},
                {width: '20%',data: 'nivel', name: 'nivel'},

            ],
            "fixedColumns" : true
        });

    })
</script>
<script>
    var propietario_id = document.getElementById('propietario_id');
    var propietario = document.getElementById('propietario');
    var nombresPropietario = document.getElementById('nombresPropietario');
    var nombresPropietario2 = document.getElementById('nombresPropietario2');
    var representante_id = document.getElementById('representante_id');
    var representante = document.getElementById('representante');
    var nombresRepresentante = document.getElementById('nombresRepresentante');
    var nombresRepresentante2 = document.getElementById('nombresRepresentante2');
    let token = "{{csrf_token()}}";
    function seleccionarpropietario(id,cedula,nombres,apellidos){
        propietario.value = cedula;
        nombresPropietario.value = nombres+' '+apellidos;
        nombresPropietario2.value = nombres+' '+apellidos;
        propietario_id.value = id;
        var modal = bootstrap.Modal.getInstance(propietarioModal)
        modal.hide();
    }
    function seleccionarrepresentante(id,cedula,nombres,apellidos){
        representante.value = cedula;
        nombresRepresentante.value = nombres+' '+apellidos;
        nombresRepresentante2.value = nombres+' '+apellidos;
        representante_id.value = id;
        var modal = bootstrap.Modal.getInstance(representanteModal)
        modal.hide();
    }
    function enviarFormulario() {
            // Selecciona el formulario y lo envía
            document.getElementById("formularioCatastroContribuyente").submit();
    }
    function seleccionarActividad(id, nombre, ciiu) {
        // Crear un nuevo elemento <tr>
        const nuevaFila = document.createElement("tr");

        // Definir el contenido de la nueva fila, incluyendo inputs ocultos para enviar a Laravel
        nuevaFila.innerHTML = `
            <td>
                <input type="hidden" name="actividades[${id}][ciiu]" value="${ciiu}">
                ${ciiu}
            </td>
            <td>
                <input type="hidden" name="actividades[${id}][nombre]" value="${nombre}">
                ${nombre}
            </td>
            <td class="text-end">
                <input type="hidden" name="actividades[${id}][id]" value="${id}">
                <button class="btn btn-sm btn-danger" onclick="eliminarFila(this)">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;

        // Añadir la nueva fila a la tabla
        document.querySelector("#tablaActividades tbody").appendChild(nuevaFila);

        // Cerrar el modal
        var modal = bootstrap.Modal.getInstance(document.getElementById('actividadModal'));
        modal.hide();
    }
    function eliminarFila(boton) {
            // Eliminar la fila padre del botón clicado
            boton.closest("tr").remove();
        }
    document.getElementById('propietario').addEventListener('keypress', function(event) {
        // Verificar si la tecla presionada es 'Enter' (keyCode 13)
        if (event.key === 'Enter') {
            // Obtener el valor del input
            event.preventDefault();
            let query = event.target.value;
            const spinner = document.getElementById('spinner');
            spinner.style.display = 'inline-block';
            nombresPropietario.value = '';
            nombresPropietario2.value = '';
            propietario_id.value = '';

            // Asegurarte que no esté vacío
            if (query.trim() !== '') {
                axios.post('{{route('getentecedula.ente')}}', {
                    _token: token,
                    query:query
                    }).then(function(res) {
                        if(res.status==200) {
                            propietario.value = res.data.ci_ruc;
                            nombresPropietario.value = res.data.nombres+' '+res.data.apellidos;
                            nombresPropietario2.value = res.data.nombres+' '+res.data.apellidos;
                            propietario_id.value = res.data.id;
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function(err) {
                        if(err.response.status == 500){
                            console.log('error al consultar al servidor');
                        }

                        if(err.response.status == 419){
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
            } else {
                spinner.style.display = 'none';
            }
        }
    });
    document.getElementById('representante').addEventListener('keypress', function(event) {
        // Verificar si la tecla presionada es 'Enter' (keyCode 13)
        if (event.key === 'Enter') {
            // Obtener el valor del input
            event.preventDefault();
            let query = event.target.value;
            const spinner = document.getElementById('spinnerrepresentante');
            spinner.style.display = 'inline-block';
            nombresRepresentante.value = '';
            representante_id.value = '';

            // Asegurarte que no esté vacío
            if (query.trim() !== '') {
                axios.post('{{route('getentecedula.ente')}}', {
                    _token: token,
                    query:query
                    }).then(function(res) {
                        if(res.status==200) {
                            representante.value = res.data.ci_ruc;
                            nombresRepresentante.value = res.data.nombres+' '+res.data.apellidos;
                            representante_id.value = res.data.id;
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function(err) {
                        if(err.response.status == 500){
                            console.log('error al consultar al servidor');
                        }

                        if(err.response.status == 419){
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
            } else {
                spinner.style.display = 'none';
            }
        }
    });
    var provincia_id = document.getElementById('provincia_id');
    provincia_id.addEventListener('change', function() {
        var optionSelectCanton = document.getElementById('optionSelectCanton');
        optionSelectCanton.innerHTML = 'Cargando...';
        var selectedOption = this.options[provincia_id.selectedIndex];
        cargarcantones(selectedOption.value);
        cargarparroquia(null);
    });
    function cargarcantones(idprovincia){
        var canton_id = document.getElementById('canton_id');

        axios.post('{{route('getcanton.catastro')}}', {
            _token: token,
            idprovincia:idprovincia
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando cantones");
                canton_id.innerHTML = res.data;
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
    var canton_id = document.getElementById('canton_id');
    canton_id.addEventListener('change', function() {
        var optionSelectParroquia = document.getElementById('optionSelectParroquia');
        optionSelectParroquia.innerHTML = 'Cargando...';
        var selectedOption = this.options[canton_id.selectedIndex];
        //console.log(selectedOption.value + ': ' + selectedOption.text);
        cargarparroquia(selectedOption.value);
    });
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
    /*
    (() => {
        'use strict'

        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        const forms = document.querySelectorAll('.needs-validation')

        // Loop over them and prevent submission
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }

            form.classList.add('was-validated')
            }, false)
        })
    })()*/
</script>
@endpush

