@extends('layouts.appv2')
@section('title', 'Crear usuario')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Actualizar usuario</h4>
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
@if ($errors->any())
    <div class="alert alert-danger">
        <ul>
            @foreach ($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif
<ul class="nav nav-tabs" role="tablist">
  <li class="nav-item" role="presentation">
    <a class="nav-link active" id="simple-tab-0" data-bs-toggle="tab" href="#simple-tabpanel-0" role="tab" aria-controls="simple-tabpanel-0" aria-selected="true">Datos generales</a>
  </li>
  <li class="nav-item" role="presentation">
    <a class="nav-link" id="simple-tab-1" data-bs-toggle="tab" href="#simple-tabpanel-1" role="tab" aria-controls="simple-tabpanel-1" aria-selected="false">Roles</a>
  </li>
  <li class="nav-item" role="presentation">
    <a class="nav-link" id="simple-tab-2" data-bs-toggle="tab" href="#simple-tabpanel-2" role="tab" aria-controls="simple-tabpanel-2" aria-selected="false">Permisos especiales</a>
  </li>
</ul>
<div class="tab-content pt-5" id="tab-content">
  <div class="tab-pane active" id="simple-tabpanel-0" role="tabpanel" aria-labelledby="simple-tab-0">
        <form id="formUsuario" action="{{route('update.usuario',$User->id)}}" class="needs-validation" method="post" novalidate>
            @csrf
            @method('PATCH')
            <div class="row justify-content-center">
                <div class="text-end mt-3">
                    <button type="button" class="btn btn-sm btn-primary" onclick="enviarFormulario()">Actualizar usuario</button>
                </div>
                <div class="col-5">
                    <div class="mb-3">
                        <label for="name" class="form-label">Nombre de usuario:</label>
                        <input type="text" class="form-control {{$errors->has('name') ? 'is-invalid' : ''}}" id="name" placeholder="Nombre de usuario" name="name" value="{{ old('name',$User->name)}}" required>
                        <div class="invalid-feedback">
                            @if($errors->has('name'))
                                {{$errors->first('name')}}
                            @endif
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Correo:</label>
                        <input type="email" class="form-control {{$errors->has('email') ? 'is-invalid' : ''}}" id="email" placeholder="Enter email" name="email" value="{{ old('fecha',$User->email)}}" required>
                        <div class="invalid-feedback">
                            @if($errors->has('email'))
                                {{$errors->first('email')}}
                            @endif
                        </div>
                    </div>
                </div>
                <div class="col-5">
                    <label class="mb-2" for="">Seleccione a una persona:</label>
                    <div class="input-group mb-3">
                        <button id="buttonBuscar" class="btn btn-outline-secondary" type="button" id="button-addon1">Buscar</button>
                        <input id="inputNombresPersona" name="inputNombresPersona" type="text" class="form-control {{$errors->has('persona_id') ? 'is-invalid' : ''}}" placeholder="" aria-label="Example text with button addon" aria-describedby="button-addon1" value="{{old('name',$User->name)}}" disabled>
                        <div class="invalid-feedback">
                            @if($errors->has('persona_id'))
                                {{$errors->first('persona_id')}}
                            @endif
                        </div>
                    </div>
                    <input type="hidden" name="persona_id" id="persona_id" value="{{old('persona_id',$User->idpersona)}}">
                    <div class="mb-3">
                        <label for="status" class="form-label">Estado:</label>
                        <select name="status" id="status" class="form-select mb-0 {{$errors->has('status') ? 'is-invalid' : ''}}" aria-label="Large select example">
                            <option value="" selected>Seleccione estado</option>
                            <option value="1" {{ old('status') == '1' || $User->status == '1' ? 'selected' : '' }}>Activo</option>
                            <option value="0" {{ old('status') == '0' || $User->status == '0' ? 'selected' : '' }}>Inactivo</option>
                        </select>
                        <div class="invalid-feedback">
                            @if($errors->has('status'))
                                {{$errors->first('status')}}
                            @endif
                        </div>
                    </div>
                </div>

            </div>

        </form>
  </div>
  <div class="tab-pane" id="simple-tabpanel-1" role="tabpanel" aria-labelledby="simple-tab-1">

    <div class="row justify-content-center">
        <div class="text-end mt-3">
            <button type="button" id="btnActualizarRol" class="btn btn-sm btn-primary" disabled>
                <span id="spinner" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                <span id="btnText">Actualizar Rol</span>
            </button>
        </div>
        <div class="mb-3">
            <label for="selectRol" class="form-label">Roles:</label>
            <select id="selectRol" class="form-select" name="selectRol">
                <option selected disabled>Seleccione un rol</option>
                @foreach ($roles as $r)
                    @if ($r->name === $roluser)
                        <option value="{{ $r->name }}" selected>{{ $r->name }}</option>
                    @else
                        <option value="{{ $r->name }}">{{ $r->name }}</option>
                    @endif
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Permisos de rol seleccionado:</label>

            @foreach($permissions as $permission)
                @if($rolPermissionuser->contains($permission->name))
                    <div class="form-check">
                        <input class="form-check-input"
                            type="checkbox"
                            name="permissions[]"
                            value="{{ $permission->id }}"
                            id="permiso{{ $permission->id }}" checked>
                        <label class="form-check-label" for="permiso{{ $permission->id }}">
                            {{ $permission->name }}
                        </label>
                    </div>
                @else
                    <div class="form-check">
                        <input class="form-check-input"
                            type="checkbox"
                            name="permissions[]"
                            value="{{ $permission->id }}"
                            id="permiso{{ $permission->id }}">
                        <label class="form-check-label" for="permiso{{ $permission->id }}">
                            {{ $permission->name }}
                        </label>
                    </div>
                @endif

            @endforeach


        </div>
    </div>


  </div>
  <div class="tab-pane" id="simple-tabpanel-2" role="tabpanel" aria-labelledby="simple-tab-2">
        <div class="text-end mt-3">
            <button type="button" id="btnActualizarPermiso" class="btn btn-sm btn-primary">
                <span id="spinnerpermiso" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                <span id="btnTextpermiso">Actualizar Permisos</span>
            </button>
        </div>
        <div class="mb-3">
            <label class="form-label">Permisos especiales:</label>

            @foreach($permissions as $permission)
                @if($rolPermissionuser->contains($permission->name))
                    <div class="form-check">
                        <input class="form-check-input"
                            type="checkbox"
                            name="permissions_especiales[]"
                            value="{{ $permission->name }}"
                            id="permiso{{ $permission->name }}" checked disabled>
                        <label class="form-check-label" for="permiso{{ $permission->name }}">
                            {{ $permission->name }}
                        </label>
                    </div>
                @else
                    @if($permissionsuser->contains($permission->name))
                        <div class="form-check">
                            <input class="form-check-input permiso-checkbox"
                                type="checkbox"
                                name="permissions_especiales[]"
                                value="{{ $permission->name }}"
                                id="permiso{{ $permission->name }}" checked>
                            <label class="form-check-label" for="permiso{{ $permission->name }}">
                                {{ $permission->name }}
                            </label>
                        </div>
                    @else
                        <div class="form-check">
                            <input class="form-check-input permiso-checkbox"
                                type="checkbox"
                                name="permissions_especiales[]"
                                value="{{ $permission->name }}"
                                id="permiso{{ $permission->name }}">
                            <label class="form-check-label" for="permiso{{ $permission->name }}">
                                {{ $permission->name }}
                            </label>
                        </div>
                    @endif

                @endif

            @endforeach


        </div>
  </div>
</div>

    <!-- Modal Persona -->
    <div class="modal fade" id="modalPersona" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
        <div class="modal-content">
            <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">Seleccione una persona</h5>

            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                @csrf
                <div class="row">
                    <div class="table-responsive">
                        <table class="table table-bordered" style="width:100%" id="tablepersona">
                            <thead>
                                <tr>
                                    <th scope="col">Accion</th>
                                    <th scope="col">Cedula</th>
                                    <th scope="col">Nombres</th>
                                    <th scope="col">Apellidos</th>
                                </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
        </div>
    </div>
    <!-- Finar modal personas -->
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
        tablepersona = $("#tablepersona").DataTable({
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
                "url": '{{ url("/paciente/listar") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                    d.formulario = "cita";
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'cedula'},
                {width: '',data: 'nombres'},
                {width: '',data: 'apellidos'},

            ],
            "fixedColumns" : true
        });
    })
</script>
<script>
    function enviarFormulario() {
            // Selecciona el formulario y lo envía
            document.getElementById("formUsuario").submit();
    }
    var buttonBuscar = document.getElementById('buttonBuscar');
    let token = "{{csrf_token()}}";
    buttonBuscar.addEventListener('click', function() {
        var modalPersona = new bootstrap.Modal(document.getElementById('modalPersona'), {
        keyboard: false
        })
        modalPersona.show();
    });

    function seleccionarpersona(id,cedula,nombres,apellidos){
        var inputNombresPersona = document.getElementById('inputNombresPersona');
        var persona_id = document.getElementById('persona_id');
        inputNombresPersona.value = nombres+' '+apellidos;
        persona_id.value = id;
        var modal = bootstrap.Modal.getInstance(modalPersona)
        modal.hide();
    }

    document.getElementById('selectRol').addEventListener('change', function () {
        const rolSeleccionado = this.value;

        // Limpiar todos los checkboxes primero
        document.querySelectorAll('input[name="permissions[]"]').forEach(cb => cb.checked = false);

        axios.post('{{route('permisos.roles')}}', {
            rol: rolSeleccionado
        })
        .then(response => {
            const permisos = response.data.permissions;
            permisos.forEach(id => {
                const checkbox = document.getElementById(`permiso${id}`);
                if (checkbox) {
                    checkbox.checked = true;
                }
            });
        })
        .catch(error => {
            console.error('Error al obtener permisos del rol:', error);
        });
    });

    const btn = document.getElementById('btnActualizarRol');
    const spinner = document.getElementById('spinner');
    const btnText = document.getElementById('btnText');
    const selectRol = document.getElementById('selectRol');

    const btnpermisos = document.getElementById('btnActualizarPermiso');
    const spinnerpermiso = document.getElementById('spinnerpermiso');
    const btnTextpermiso = document.getElementById('btnTextpermiso');

    // Habilitar botón cuando se seleccione un rol válido
    selectRol.addEventListener('change', function () {
        btn.disabled = !selectRol.value;
    });

    btn.addEventListener('click', function () {
        const rolSeleccionado = selectRol.value;
        let usuarioId = {{$User->id}}; // Reemplaza con ID real

        if (!rolSeleccionado) {
        alert('Seleccione un rol válido');
        return;
        }

        // Mostrar spinner y desactivar botón
        btn.disabled = true;
        spinner.classList.remove('d-none');
        btnText.textContent = 'Actualizando...';

        axios.post('{{route('rol.usuario')}}', {
        rol: rolSeleccionado,
        usuarioId : usuarioId
        })
        .then(response => {
        alert('Rol actualizado correctamente');
        })
        .catch(error => {
        console.error(error);
        alert('Error al actualizar el rol');
        })
        .finally(() => {
        // Restaurar botón y ocultar spinner
        btn.disabled = false;
        spinner.classList.add('d-none');
        btnText.textContent = 'Actualizar Rol';
        });
    });

    btnpermisos.addEventListener('click', function(){

        let usuarioId = {{$User->id}}; // Reemplaza con ID real


        // Mostrar spinner y desactivar botón
        spinnerpermiso.classList.remove('d-none');
        btnTextpermiso.textContent = 'Actualizando...';

        // Capturar permisos marcados
        let permisosSeleccionados = [];
        document.querySelectorAll('.permiso-checkbox:checked').forEach((checkbox) => {
            permisosSeleccionados.push(checkbox.value);
        });

        axios.post('{{route('permisos.usuario')}}', {
        usuarioId : usuarioId,
        permisos: permisosSeleccionados
        })
        .then(response => {
        alert('Permisos agregados correctamente');
        })
        .catch(error => {
        console.error(error);
        alert('Error al actualizar el rol');
        })
        .finally(() => {
        // Restaurar botón y ocultar spinner
        btnpermisos.disabled = false;
        spinnerpermiso.classList.add('d-none');
        btnTextpermiso.textContent = 'Actualizar Rol';
        });
    });
</script>
@endpush

