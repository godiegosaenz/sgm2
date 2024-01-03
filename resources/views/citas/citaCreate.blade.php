@extends('layouts.app')
@section('title', 'Creando Cita')
@push('styles')
<link rel="stylesheet" href="//cdn.datatables.net/1.12.1/css/dataTables.bootstrap5.min.css">
<link rel="stylesheet" href="//cdn.datatables.net/rowreorder/1.2.8/css/rowReorder.bootstrap5.min.css">
@endpush
@section('content')
    <div class="container">
        <div class="row justify-content-center align-items-center">

            <div class="col-md-9 ">
                <div class="row mb-1">
                    <h2>Ingreso de citas</h2>
                </div>

                <form method="POST" action="{{route('store.cita')}}">
                    @csrf
                    <div class="row">
                        <div class="col-12">

                            <div class="alert alert-info alert-dismissible">
                                <div>
                                    <h5><i class="bi-info-circle"></i> Recuerda!</h5>
                                    Todos los campos con un asterisco al final. Son campos obligatorios.
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-12">
                            <h4>Seleccione Paciente :</h4>
                            <hr>
                        </div>
                        <div class="col-6">
                            <label for="cc_ruc"> Selecciona cedula/Ruc *</label>
                            <div class="input-group mb-3">
                                <button class="btn btn-outline-primary" id="buttonModalPersona" type="button">Buscar</button>
                                <input id="inputCedulaPersona" type="text" class="form-control {{$errors->has('persona_id') ? 'is-invalid' : ''}}" placeholder="" aria-label="Example text with button addon" aria-describedby="buttonModalPersona" value="{{old('persona_cedula')}}" disabled>
                                <div class="invalid-feedback">
                                    @if($errors->has('persona_id'))
                                        {{$errors->first('persona_id')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <input type="hidden" name="persona_id" id="persona_id" value="{{old('persona_id')}}">
                        <input type="hidden" name="persona_name" id="persona_name" value="{{old('persona_name')}}">
                        <input type="hidden" name="persona_cedula" id="persona_cedula" value="{{old('persona_cedula')}}">
                        <div class="col-6">
                            <div class="mb-3">
                                <label for="name2">* Nombres : </label>
                                <input type="text" class="form-control {{$errors->has('persona_id') ? 'is-invalid' : ''}}" id="inputNombresPersona" name="inputNombresPersona" value="{{old('persona_name')}}" disabled>
                                <div class="invalid-feedback">
                                    @if($errors->has('persona_id'))
                                        {{$errors->first('persona_id')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-12">
                            <h4>Seleccione Especialista :</h4>
                            <hr>
                        </div>
                        <div class="col-6">
                            <label for="cc_ruc"> Selecciona cedula/Ruc *</label>
                            <div class="input-group mb-3">
                                <button class="btn btn-outline-primary" type="button" id="buttonModalEspecialista">Buscar</button>
                                <input id="inputCedulaEspecialista" type="text" class="form-control {{$errors->has('especialista_id') ? 'is-invalid' : ''}}" placeholder="" aria-label="Example text with button addon" aria-describedby="buttonModalEspecialista" value="{{ old('especialista_cedula') }}" disabled>
                                <div class="invalid-feedback">
                                    @if($errors->has('especialista_id'))
                                        {{$errors->first('especialista_id')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <input type="hidden" name="especialista_id" id="especialista_id" value="{{old('especialista_id')}}">
                        <input type="hidden" name="especialista_name" id="especialista_name" value="{{old('especialista_name')}}">
                        <input type="hidden" name="especialista_cedula" id="especialista_cedula" value="{{old('especialista_cedula')}}">
                        <div class="col-6">
                            <div class="mb-3">
                                <label for="inputNombresEspecialista">* Nombres y apellidos:</label>
                                <input type="text" class="form-control {{$errors->has('especialista_id') ? 'is-invalid' : ''}}" id="inputNombresEspecialista" name="inputNombresEspecialista" value="{{ old('especialista_name') }}" disabled>
                                <div class="invalid-feedback">
                                    @if($errors->has('especialista_id'))
                                        {{$errors->first('especialista_id')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-12">
                            <h4>Informacion general :</h4>
                            <hr>
                        </div>
                        <div class="col-6">
                            <div class="mb-3">
                                <label for="fecha">* Fecha de programacion : </label>
                                <input class="form-control {{$errors->has('fecha') ? 'is-invalid' : ''}}" type="date" id="fecha" name="fecha" value="{{ old('fecha')}}">
                                <div class="invalid-feedback">
                                    @if($errors->has('fecha'))
                                        {{$errors->first('fecha')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="mb-3">
                                <label for="hora">* Hora : </label>
                                <input type="time" class="form-control {{$errors->has('hora') ? 'is-invalid' : ''}}" id="hora" name="hora" value="{{ old('hora') }}" >
                                <div class="invalid-feedback">
                                    @if($errors->has('hora'))
                                        {{$errors->first('hora')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="co-12">
                            <div class="mb-3">
                                <label for="motivo">* Motivo : </label>
                                <textarea class="form-control {{$errors->has('motivo') ? 'is-invalid' : ''}}" id="motivo" name="motivo" rows="3"></textarea>
                                <div class="invalid-feedback">
                                    @if($errors->has('motivo'))
                                        {{$errors->first('motivo')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-12">
                            <button class="btn btn-primary">Agendar Cita</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
        <!-- Modal persona -->
    <div class="modal fade" id="modalPersona" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
        <div class="modal-content">
            <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">Selecciona Persona</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                @csrf
                <div class="row">
                    <div class="table-responsive">
                        <table class="table table-bordered" style="width:100%" id="tablePersonaCita">
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
            <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="button" class="btn btn-primary">Save changes</button>
            </div>
        </div>
        </div>
    </div>
        <!-- Modal especialistas -->
    <div class="modal fade" id="modalEspecialista" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
        <div class="modal-content">
            <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">Selecciona Especialista</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                @csrf
                <div class="row">
                    <div class="table-responsive">
                        <table class="table table-bordered" style="width:100%" id="tableEspecialistaCita">
                            <thead>
                                <tr>
                                    <th scope="col">Accion</th>
                                    <th scope="col">Cedula</th>
                                    <th scope="col">Nombres</th>
                                    <th scope="col">Apellidos</th>
                                    <th scope="col">Especialidad</th>
                                </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="button" class="btn btn-primary">Save changes</button>
            </div>
        </div>
        </div>
    </div>
@endsection
@push('scripts')
<!-- jQuery -->
<script src="//code.jquery.com/jquery-3.5.1.js"></script>
<!-- DataTables -->
<script src="//cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
<script src="//cdn.datatables.net/1.12.1/js/dataTables.bootstrap5.min.js"></script>
<script src="//cdn.datatables.net/rowreorder/1.2.8/js/dataTables.rowReorder.min.js"></script>
<script>
    $(document).ready(function(){

        tablePersonaCita = $("#tablePersonaCita").DataTable({
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
        tableEspecialistaCita = $("#tableEspecialistaCita").DataTable({
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
                "url": '{{ url("/especialista/lista") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'cedula'},
                {width: '',data: 'nombres'},
                {width: '',data: 'apellidos'},
                {width: '',data: 'especialidad'},

            ],
            "fixedColumns" : true
        });
    })
</script>
<script>
    var buttonModalPersona = document.getElementById('buttonModalPersona');
    var buttonModalEspecialista = document.getElementById('buttonModalEspecialista');
    let token = "{{csrf_token()}}";




    buttonModalPersona.addEventListener('click', function() {
        var modalPersona = new bootstrap.Modal(document.getElementById('modalPersona'), {
        keyboard: false
        })
        modalPersona.show();
    });
    buttonModalEspecialista.addEventListener('click', function() {
        var modalEspecialista = new bootstrap.Modal(document.getElementById('modalEspecialista'), {
        keyboard: false
        })
        modalEspecialista.show();
    });
    function cargarcantones(idprovincia){
        var canton_id = document.getElementById('canton_id');

        axios.post('{{route('canton.obtener')}}', {
            _token: token,
            idprovincia:idprovincia
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando cantones");
                canton_id.innerHTML = res.data;
            }
        }).catch(function(err) {
            if(err.response.status == 500){
                toastr.error('Error al comunicarse con el servidor, contacte al administrador de Sistemas');
                console.log('error al consultar al servidor');
            }

            if(err.response.status == 419){
                toastr.error('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }
        }).then(function() {

        });
    }

    function seleccionarpersona(id,cedula,nombres,apellidos){
        var inputCedulaPersona = document.getElementById('inputCedulaPersona');
        var inputNombresPersona = document.getElementById('inputNombresPersona');
        var persona_id = document.getElementById('persona_id');
        ///////input para carga de paciente //////
        var persona_name = document.getElementById('persona_name');
        var persona_cedula = document.getElementById('persona_cedula');
        /////////////////////////////////////
        inputCedulaPersona.value = cedula;
        inputNombresPersona.value = nombres+' '+apellidos;
        persona_name.value = nombres+' '+apellidos;
        persona_cedula.value = cedula;
        persona_id.value = id;
        var modal = bootstrap.Modal.getInstance(modalPersona)
        modal.hide();
    }

    function seleccionarespecialista(id,cedula,nombres,apellidos){
        var inputCedulaEspecialista = document.getElementById('inputCedulaEspecialista');
        var inputNombresEspecialista = document.getElementById('inputNombresEspecialista');
        var especialista_id = document.getElementById('especialista_id');
        ///////input para carga de especialista //////
        var especialista_name = document.getElementById('especialista_name');
        var especialista_cedula = document.getElementById('especialista_cedula');
        /////////////////////////////////////
        inputCedulaEspecialista.value = cedula;
        inputNombresEspecialista.value = nombres+' '+apellidos;
        especialista_name.value = nombres+' '+apellidos;
        especialista_cedula.value = cedula;
        especialista_id.value = id;
        var modal = bootstrap.Modal.getInstance(modalEspecialista)
        modal.hide();
    }

</script>
@endpush
