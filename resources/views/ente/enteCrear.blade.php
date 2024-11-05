@extends('layouts.appv2')
@section('title', 'Actualizar cliente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Registrar cliente</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <button type="button" class="btn btn-sm btn-primary d-flex align-items-center gap-1">
            Guardar cliente
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
    <form>
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-5">Tipo de persona</legend>
             <!-- Campo de Selección de Tipo de Persona -->
             <div class="mb-4">
                <select class="form-select" id="tipo_persona" name="tipo_persona" onchange="toggleFields()">
                    <option value="natural" selected>Persona Natural</option>
                    <option value="juridica">Persona Jurídica</option>
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
                        <label for="cedula" class="form-label">Cédula</label>
                        <input type="text" class="form-control" id="cedula" name="cedula">
                    </div>
                    <div class="mb-3">
                        <label for="nombres" class="form-label">Nombres</label>
                        <input type="text" class="form-control" id="nombres" name="nombres">
                    </div>
                    <div class="mb-3">
                        <label for="apellidos" class="form-label">Apellidos</label>
                        <input type="text" class="form-control" id="apellidos" name="apellidos">
                    </div>
                </div>

                <!-- Campos para Persona Jurídica -->
                <div id="personaJuridicaFields" class="col-md-6" style="display: none;">
                    <div class="mb-3">
                        <label for="ruc" class="form-label">RUC</label>
                        <input type="text" class="form-control" id="ruc" name="ruc">
                    </div>
                    <div class="mb-3">
                        <label for="razon_social" class="form-label">Razón Social</label>
                        <input type="text" class="form-control" id="razon_social" name="razon_social">
                    </div>
                    <div class="mb-3">
                        <label for="nombre_comercial" class="form-label">Nombre Comercial</label>
                        <input type="text" class="form-control" id="nombre_comercial" name="nombre_comercial">
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="fecha_nacimiento" class="form-label">Fecha de Nacimiento</label>
                        <input type="date" class="form-control" id="fecha_nacimiento" name="fecha_nacimiento">
                    </div>
                    <div class="mb-3">
                        <label for="nacionalidad" class="form-label">Direccion</label>
                        <input type="text" class="form-control" id="nacionalidad" name="nacionalidad">
                    </div>
                    <div class="form-check mb-3 mt-5">
                        <input class="form-check-input" type="checkbox" id="contabilidad" name="contabilidad" onchange="toggleDiscapacidadFields()">
                        <label class="form-check-label" for="contabilidad">¿Lleva contabilidad?</label>
                    </div>
                </div>
        </fieldset>
        <!-- Sección de Discapacidad -->
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-6">Información de Discapacidad</legend>

            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="discapacidad" name="discapacidad" onchange="toggleDiscapacidadFields()">
                <label class="form-check-label" for="discapacidad">¿Tiene discapacidad?</label>
            </div>

            <!-- Campos de Discapacidad -->
            <div class="row g-3">
                <div class="col-md-6 mb-3">
                    <label for="tipo_discapacidad" class="form-label">Tipo de Discapacidad</label>
                    <select class="form-select" id="tipo_discapacidad" name="tipo_discapacidad" disabled>
                        <option value="" selected disabled>Seleccione el tipo de discapacidad</option>
                        <option value="fisica">Física</option>
                        <option value="sensorial">Sensorial</option>
                        <option value="intelectual">Intelectual</option>
                        <option value="psiquica">Psíquica</option>
                        <option value="otra">Otra</option>
                    </select>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="porcentaje_discapacidad" class="form-label">Porcentaje de Discapacidad</label>
                    <input type="number" class="form-control" id="porcentaje_discapacidad" name="porcentaje_discapacidad" min="0" max="100" placeholder="0%" disabled>
                </div>
            </div>
        </fieldset>
        <!-- Sección de Información de Contacto -->
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3">Información de Contacto</legend>
            <div class="row g-3">
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="direccion" class="form-label">telefono</label>
                        <input type="text" class="form-control" id="direccion" name="direccion">
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="mb-3">
                        <label for="correo" class="form-label">Correo Electrónico</label>
                        <input type="email" class="form-control" id="correo" name="correo">
                    </div>
                </div>
            </div>
        </fieldset>
        <!-- Botón de envío -->
        <div class="col-12">
            <button type="submit" class="btn btn-primary">Enviar</button>
        </div>
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

        if (tipoPersona === 'natural') {
            personaNaturalFields.style.display = 'block';
            personaJuridicaFields.style.display = 'none';
        } else if (tipoPersona === 'juridica') {
            personaNaturalFields.style.display = 'none';
            personaJuridicaFields.style.display = 'block';
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
</script>
@endpush
