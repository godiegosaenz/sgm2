@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')

@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h3 class="text-center">Detalle de contribuyente</h3>
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

        <div class="row">
            <!-- Propietario -->
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">Propietario</div>
                    <div class="card-body">
                        <p><strong>Cédula:</strong> {{ $contribuyente->propietario->ci_ruc ?? 'N/A' }}</p>
                        <p><strong>Nombres:</strong> {{ $contribuyente->propietario->nombres ?? 'N/A' }}</p>
                        <p><strong>Apellidos:</strong> {{ $contribuyente->propietario->apellidos ?? 'N/A' }}</p>
                    </div>
                </div>
            </div>

            <!-- Representante Legal -->
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">Representante Legal</div>
                    <div class="card-body">
                        <p><strong>Cédula:</strong> {{ $contribuyente->representante_legal->ci_ruc ?? 'N/A' }}</p>
                        <p><strong>Nombres:</strong> {{ $contribuyente->representante_legal->nombres ?? 'N/A' }}</p>
                        <p><strong>Apellidos:</strong> {{ $contribuyente->representante_legal->apellidos ?? 'N/A' }}</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Información General y Otros Detalles -->
        <div class="card mb-4">
            <div class="card-header">Información General y Otros Detalles</div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>RUC:</strong> {{ $contribuyente->ruc }}</p>
                        <p><strong>Razón Social:</strong> {{ $contribuyente->razon_social }}</p>
                        <!-- Estado del Contribuyente con etiqueta de color -->
                        <p><strong>Estado del Contribuyente:</strong>
                            @switch($contribuyente->estado_contribuyente_id)
                                @case(1)
                                    <span class="badge bg-success">Activo</span>
                                    @break
                                @case(2)
                                    <span class="badge bg-danger">Inactivo</span>
                                    @break
                                @case(3)
                                    <span class="badge bg-warning">Suspendido</span>
                                    @break
                                @default
                                    <span class="badge bg-secondary">Desconocido</span>
                            @endswitch
                        </p>
                        <p><strong>Tipo de Contribuyente:</strong>
                            @switch($contribuyente->tipo_contribuyente)
                                @case(1)
                                    Persona Natural
                                    @break
                                @case(2)
                                    Sociedad
                                    @break
                                @default
                                    N/A
                            @endswitch
                        </p>
                        <p><strong>Nombre Fantasía Comercial:</strong> {{ $contribuyente->nombre_fantasia_comercial ?? 'N/A' }}</p>
                         <!-- Clase Contribuyente -->
                        <p><strong>Clase Contribuyente:</strong> {{ $contribuyente->clase_contribuyente->nombre ?? 'N/A' }}</p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Obligado a Contabilidad:</strong> {{ $contribuyente->obligado_contabilidad ? 'Sí' : 'No' }}</p>
                        <p><strong>Local Propio:</strong>
                            @switch($contribuyente->local_propio)
                                @case(1)
                                    Propio
                                    @break
                                @case(2)
                                    Arrendado
                                    @break
                                @default
                                    N/A
                            @endswitch
                        </p>
                        <p><strong>Es Matriz:</strong> {{ $contribuyente->es_matriz ? 'Sí' : 'No' }}</p>
                        <p><strong>Es Turismo:</strong> {{ $contribuyente->es_turismo ? 'Sí' : 'No' }}</p>
                        <p><strong>Estado del Establecimiento:</strong>
                            @switch($contribuyente->estado_establecimiento)
                                @case(1)
                                    Abierto
                                    @break
                                @case(2)
                                    Cerrado
                                    @break
                                @default
                                    N/A
                            @endswitch
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Actividades Comerciales -->
        <div class="card mb-4">
            <div class="card-header">Actividades Comerciales</div>
            <div class="card-body">
                @if($contribuyente->actividades && $contribuyente->actividades->isNotEmpty())
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>CIIU</th>
                                    <th>Descripción de la Actividad Comercial</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($contribuyente->actividades as $actividad)
                                    <tr>
                                        <td>{{ $actividad->ciiu }}</td>
                                        <td>{{ $actividad->descripcion }}</td>
                                    </tr>
                                @endforeach
                            </tbody>
                        </table>
                    </div>
                @else
                    <p>No se encontraron actividades comerciales para este contribuyente.</p>
                @endif
            </div>
        </div>

        <!-- Fechas Importantes -->
        <div class="card mb-4">
            <div class="card-header">Fechas Importantes</div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>Fecha Inicio Actividades:</strong> {{ $contribuyente->fecha_inicio_actividades }}</p>
                        <p><strong>Fecha Actualización Actividades:</strong> {{ $contribuyente->fecha_actualizacion_actividades ?? 'N/A' }}</p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Fecha Reinicio Actividades:</strong> {{ $contribuyente->fecha_reinicio_actividades ?? 'N/A' }}</p>
                        <p><strong>Fecha Suspensión Definitiva:</strong> {{ $contribuyente->fecha_suspension_definitiva ?? 'N/A' }}</p>
                    </div>
                </div>
            </div>
        </div>
        <!-- Información de Ubicación -->
        <div class="card mb-4">
            <div class="card-header">Ubicación</div>
            <div class="card-body">
                <p><strong>Provincia:</strong> {{ $contribuyente->provincia->descripcion ?? 'N/A' }}</p>
                <p><strong>Cantón:</strong> {{ $contribuyente->canton->nombre ?? 'N/A' }}</p>
                <p><strong>Parroquia:</strong> {{ $contribuyente->parroquia->descripcion ?? 'N/A' }}</p>
                <p><strong>Calle Principal:</strong> {{ $contribuyente->calle_principal }}</p>
                <p><strong>Calle Secundaria:</strong> {{ $contribuyente->calle_secundaria ?? 'N/A' }}</p>
                <p><strong>Referencia de Ubicación:</strong> {{ $contribuyente->referencia_ubicacion ?? 'N/A' }}</p>
            </div>
        </div>

        <!-- Información de Contacto -->
        <div class="card mb-4">
            <div class="card-header">Contacto</div>
            <div class="card-body">
                <p><strong>Correo:</strong> {{ $contribuyente->correo_1 }}</p>
                <p><strong>Teléfono:</strong> {{ $contribuyente->telefono }}</p>
            </div>
        </div>
    </div>
@endsection
@push('scripts')

@endpush
