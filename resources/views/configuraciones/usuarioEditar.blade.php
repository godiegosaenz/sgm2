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
            <button type="button" class="btn btn-sm btn-outline-primary">Actualizar usuario</button>
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

<form action="{{route('update.usuario',$User)}}" class="needs-validation" method="post" novalidate>
    @csrf
    @method('PATCH')
    <div class="row justify-content-center">
        <div class="col-5">
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
            <input type="hidden" name="name" id="name" value="{{old('name',$User->name)}}">
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
        <div class="col-5">
            <button type="submit" class="btn btn-primary">Actualizar usuario</button>
        </div>

    </div>
</form>
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
        var name = document.getElementById('name');
        var persona_id = document.getElementById('persona_id');
        inputNombresPersona.value = nombres+' '+apellidos;
        name.value = nombres+' '+apellidos;
        persona_id.value = id;
        var modal = bootstrap.Modal.getInstance(modalPersona)
        modal.hide();
    }
</script>
@endpush

