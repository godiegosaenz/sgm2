@extends('layouts.appv2')
@section('title', 'Actualizar cliente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Actualizar cliente</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <button type="button" class="btn btn-sm btn-primary d-flex align-items-center gap-1 me-2" onclick="enviarFormulario()">
            Actualizar cliente
        </button>
        <button type="button" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
            Nuevo cliente
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
    @if ($errors->any())
        <div class="alert alert-danger">
            Verifique las validaciones antes de actualizar
        </div>
    @endif
    <form id="formEnte" method="POST" action="{{route('update.ente',['id' => $ente->id])}}">
        @csrf
        @method('PATCH')
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-6">Tipo de persona</legend>
             <!-- Campo de Selección de Tipo de Persona -->
             <div class="mb-4">
                <select class="form-select" id="tipo_persona" name="tipo_persona" onchange="toggleFields()">
                    <option value="natural" {{ old('tipo_persona', $ente->es_persona ? 'natural' : '') === 'natural' ? 'selected' : '' }}>Persona Natural</option>
                    <option value="juridica" {{ old('tipo_persona', $ente->es_persona ? '' : 'juridica') === 'juridica' ? 'selected' : '' }}>Persona Jurídica</option>
                </select>
            </div>
        </fieldset>
        <!-- Sección de Información Personal -->
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-6">Información Personal</legend>
            <div class="row g-3">
                <!-- Campos para Persona Natural -->
                <div id="personaNaturalFields" class="col-md-6">
                    <div class="mb-3">
                        <label for="cedula" class="form-label">Cédula <span class="text-danger">*</span></label>
                        <input type="text" class="form-control {{$errors->has('cedula') ? 'is-invalid' : ''}}" id="cedula" name="cedula" value="{{$errors->any() ? old('cedula') : $ente->ci_ruc}}">
                        <div class="invalid-feedback">
                            @if($errors->has('cedula'))
                                {{$errors->first('cedula')}}
                            @endif
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="nombres" class="form-label">Nombres <span class="text-danger">*</span></label>
                        <input type="text" class="form-control {{$errors->has('nombres') ? 'is-invalid' : ''}}" id="nombres" name="nombres" value="{{$errors->any() ? old('nombres') : $ente->nombres}}">
                        <div class="invalid-feedback">
                            @if($errors->has('nombres'))
                                {{$errors->first('nombres')}}
                            @endif
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="apellidos" class="form-label">Apellidos <span class="text-danger">*</span></label>
                        <input type="text" class="form-control {{$errors->has('apellidos') ? 'is-invalid' : ''}}" id="apellidos" name="apellidos" value="{{$errors->any() ? old('apellidos') : $ente->apellidos}}">
                        <div class="invalid-feedback">
                            @if($errors->has('apellidos'))
                                {{$errors->first('apellidos')}}
                            @endif
                        </div>
                    </div>
                </div>

                <!-- Campos para Persona Jurídica -->
                <div id="personaJuridicaFields" class="col-md-6" style="display: none;">
                    <div class="mb-3">
                        <label for="ruc" class="form-label">RUC <span class="text-danger">*</span></label>
                        <input type="number" class="form-control {{$errors->has('ruc') ? 'is-invalid' : ''}}" id="ruc" name="ruc" value="{{$errors->any() ? old('ruc') : $ente->ci_ruc}}">
                        <div class="invalid-feedback">
                            @if($errors->has('ruc'))
                                {{$errors->first('ruc')}}
                            @endif
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="razon_social" class="form-label">Razón Social <span class="text-danger">*</span></label>
                        <input type="text" class="form-control {{$errors->has('razon_social') ? 'is-invalid' : ''}}" id="razon_social" name="razon_social" value="{{$errors->any() ? old('razon_social') : $ente->razon_social}}">
                        <div class="invalid-feedback">
                            @if($errors->has('razon_social'))
                                {{$errors->first('razon_social')}}
                            @endif
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="nombre_comercial" class="form-label">Nombre Comercial</label>
                        <input type="text" class="form-control {{$errors->has('nombre_comercial') ? 'is-invalid' : ''}}" id="nombre_comercial" name="nombre_comercial" value="{{$errors->any() ? old('nombre_comercial') : $ente->nombre_comercial}}">
                        <div class="invalid-feedback">
                            @if($errors->has('nombre_comercial'))
                                {{$errors->first('nombre_comercial')}}
                            @endif
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div id="fecha_nacimiento_div" class="mb-3" style="display:block">
                        <label for="fecha_nacimiento" class="form-label">Fecha de Nacimiento <span class="text-danger">*</span></label>
                        <input type="date" class="form-control {{$errors->has('fecha_nacimiento') ? 'is-invalid' : ''}}" id="fecha_nacimiento" name="fecha_nacimiento" value="{{ $ente->fecha_nacimiento ? $ente->FechaNacimientoFormateada : '' }}">
                        <div class="invalid-feedback">
                            @if($errors->has('fecha_nacimiento'))
                                {{$errors->first('fecha_nacimiento')}}
                            @endif
                        </div>
                    </div>
                    <div id="fecha_constitucion_div" class="mb-3" style="display: none;">
                        <label for="fecha_constitucion" class="form-label">Fecha de Constitución <span class="text-danger">*</span></label>
                        <input type="date" class="form-control {{$errors->has('fecha_constitucion') ? 'is-invalid' : ''}}" id="fecha_constitucion" name="fecha_constitucion" value="{{$errors->any() ? old('fecha_nacimiento') : $ente->fecha_nacimiento_formateada}}">
                        <div class="invalid-feedback">
                            @if($errors->has('fecha_constitucion'))
                                {{$errors->first('fecha_constitucion')}}
                            @endif
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="direccion" class="form-label">Direccion <span class="text-danger">*</span></label>
                        <input type="text" class="form-control {{$errors->has('direccion') ? 'is-invalid' : ''}}" id="direccion" name="direccion" value="{{$errors->any() ? old('direccion') : $ente->direccion}}">
                        <div class="invalid-feedback">
                            @if($errors->has('direccion'))
                                {{$errors->first('direccion')}}
                            @endif
                        </div>
                    </div>
                    <div class="form-check mb-3 mt-5">
                        <input class="form-check-input" type="checkbox" id="contabilidad" name="contabilidad" onchange="toggleDiscapacidadFields()">
                        <label class="form-check-label" for="contabilidad">¿Lleva contabilidad?</label>
                    </div>
                </div>
        </fieldset>
        <!-- Sección de Discapacidad -->
        <fieldset id="infodiscapacidad" class="border p-3 mb-4" style="display: block">
            <legend class="float-none w-auto px-3 fs-6">Información de Discapacidad</legend>

            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="discapacidad" name="discapacidad"
                    {{ in_array($ente->discapacidad, [201, 202, 203]) ? 'checked' : '' }}
                    onchange="toggleDiscapacidadFields()">
                <label class="form-check-label" for="discapacidad">¿Tiene discapacidad?</label>
            </div>

            <!-- Campos de Discapacidad -->
            <div class="row g-3">
                <div class="col-md-6 mb-3">
                    <label for="tipo_discapacidad" class="form-label">Tipo de Discapacidad</label>
                    <select class="form-select" id="tipo_discapacidad" name="tipo_discapacidad" disabled>
                        <option value="" selected disabled>Seleccione el tipo de discapacidad</option>
                        @foreach ($ctlg_item as $cg)
                            <option value="{{ $cg->id }}"
                                {{ (old('tipo_discapacidad') == $cg->id || (isset($ente) && $ente->discapacidad == $cg->id)) ? 'selected' : '' }}>
                                {{ $cg->valor }}
                            </option>
                        @endforeach
                    </select>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="porcentaje_discapacidad" class="form-label">Porcentaje de Discapacidad</label>
                    <input type="number" class="form-control" id="porcentaje_discapacidad" name="porcentaje_discapacidad" min="0" max="100" placeholder="0%" disabled value="{{$errors->any() ? old('porcentaje_discapacidad') : $ente->porcentaje}}">
                </div>
            </div>
        </fieldset>
        <!-- Sección de Información de Contacto -->
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3">Información de Contacto</legend>
            <div class="row g-3">
                <div class="col-md-6" id="telefono-container">
                    <div class="mb-3">
                        <button type="button" id="add-telefono-btn" class="btn btn-outline-secondary btn-sm mt-2">Agregar telefono</button>
                    </div>
                    @php
                        // Obtenemos los teléfonos del ente, o un array con un solo elemento vacío si no tiene teléfonos
                        $telefonos = old('telefono', $ente->telefono->pluck('telefono')->toArray() ?: ['']);
                    @endphp
                    @foreach ($telefonos as $index => $telefono)
                        <div class="form-group mt-2">
                            <label for="telefono">Teléfono: <span class="text-danger">*</span></label>
                            <input type="number" class="form-control {{ $errors->has("telefono.$index") ? 'is-invalid' : '' }}" name="telefono[]" value="{{ $telefono }}" required>

                            @if ($errors->has("telefono.$index"))
                                <div class="text-danger">{{ $errors->first("telefono.$index") }}</div>
                            @endif
                        </div>
                    @endforeach

                </div>
                <div class="col-md-6" id="correo-container">
                    <div class="mb-3">
                        <button type="button" id="add-correo-btn" class="btn btn-outline-secondary btn-sm mt-2">Agregar otro correo</button>
                    </div>
                    @php
                        // Obtenemos los correos del ente, o un array con un solo elemento vacío si no tiene correos
                        $correos = old('correo', $ente->correo->pluck('email')->toArray() ?: ['']);
                    @endphp
                    @foreach ($correos as $index => $correo)
                        <div class="form-group mt-2">
                            <label for="correo">Correo: <span class="text-danger">*</span></label>
                            <input type="email" class="form-control {{ $errors->has("correo.$index") ? 'is-invalid' : '' }}" name="correo[]" value="{{ $correo }}" required>

                            @if ($errors->has("correo.$index"))
                                <div class="text-danger">{{ $errors->first("correo.$index") }}</div>
                            @endif
                        </div>
                    @endforeach
                </div>
            </div>
        </fieldset>
    </form>
@endsection
@push('scripts')
<!-- jQuery -->
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
<!-- DataTables -->

<script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
<script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
<script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>
<script>
    // Mostrar los campos correctos al cargar la página
    document.addEventListener("DOMContentLoaded", () => {
        toggleFields();
        toggleDiscapacidadFields();
    });

    function toggleFields() {
        const tipoPersona = document.getElementById('tipo_persona').value;
        const personaNaturalFields = document.getElementById('personaNaturalFields');
        const personaJuridicaFields = document.getElementById('personaJuridicaFields');
        const fecha_constitucion_div = document.getElementById('fecha_constitucion_div');
        const fecha_nacimiento_div = document.getElementById('fecha_nacimiento_div');
        const infodiscapacidad = document.getElementById('infodiscapacidad');

        if (tipoPersona === 'natural') {
            personaNaturalFields.style.display = 'block';
            personaJuridicaFields.style.display = 'none';
            fecha_constitucion_div.style.display = 'none';
            fecha_nacimiento_div.style.display = 'block';
            infodiscapacidad.style.display = 'block';
        } else if (tipoPersona === 'juridica') {
            personaNaturalFields.style.display = 'none';
            personaJuridicaFields.style.display = 'block';
            fecha_constitucion_div.style.display = 'block';
            fecha_nacimiento_div.style.display = 'none';
            infodiscapacidad.style.display = 'none';
        }
    }

    function toggleDiscapacidadFields() {
        const discapacidadChecked = document.getElementById('discapacidad').checked;
        const tipoDiscapacidad = document.getElementById('tipo_discapacidad');
        const porcentajeDiscapacidad = document.getElementById('porcentaje_discapacidad');

        // Habilitar o deshabilitar los campos de discapacidad
        tipoDiscapacidad.disabled = !discapacidadChecked;
        porcentajeDiscapacidad.disabled = !discapacidadChecked;
    }
    function enviarFormulario() {
            // Selecciona el formulario y lo envía
            document.getElementById("formEnte").submit();
    }
    document.getElementById('add-correo-btn').addEventListener('click', function () {
        // Crea un nuevo div para el nuevo campo de correo
        const newCorreoDiv = document.createElement('div');
        newCorreoDiv.classList.add('form-group', 'mt-2');

        // Agrega el campo de entrada (input) de correo
        newCorreoDiv.innerHTML = `
            <label for="correo">Correo: <span class="text-danger">*</span></label>
            <input type="email" class="form-control" name="correo[]" required>
            <div class="text-danger"></div> <!-- Mensaje de error, si se necesita -->
        `;

        // Agrega el nuevo campo al contenedor de correos
        document.getElementById('correo-container').appendChild(newCorreoDiv);
    });
    document.getElementById('add-telefono-btn').addEventListener('click', function () {
        // Crea un nuevo div para el nuevo campo de correo
        const newtelefonoDiv = document.createElement('div');
        newtelefonoDiv.classList.add('form-group', 'mt-2');

        // Agrega el campo de entrada (input) de correo
        newtelefonoDiv.innerHTML = `
            <label for="telefono">Telefono: <span class="text-danger">*</span></label>
            <input type="number" class="form-control" name="telefono[]" required>
            <div class="text-danger"></div> <!-- Mensaje de error, si se necesita -->
        `;

        // Agrega el nuevo campo al contenedor de correos
        document.getElementById('telefono-container').appendChild(newtelefonoDiv);
    });
    document.getElementById('add-telefono-btn').addEventListener('click', function () {
        const telefonoContainer = document.getElementById('telefono-container');

        // Crea un nuevo div para el nuevo campo de teléfono
        const newTelefonoDiv = document.createElement('div');
        newTelefonoDiv.classList.add('form-group', 'mt-2');

        // Genera un nuevo input de teléfono
        newTelefonoDiv.innerHTML = `
            <label for="telefono">Teléfono:</label>
            <input type="text" class="form-control" name="telefono[]" required>
            <div class="text-danger"></div> <!-- Mensaje de error, si se necesita -->
        `;

        // Agrega el nuevo campo al contenedor de teléfonos
        telefonoContainer.appendChild(newTelefonoDiv);
    });
</script>
@endpush
