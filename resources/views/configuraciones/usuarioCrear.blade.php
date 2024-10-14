@extends('layouts.app')
@section('title', 'Crear usuario')
@push('styles')
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Formulario para crear usuario</h2>
                </div>
            </div>
        </div>


        <form action="/action_page.php">
            <div class="row justify-content-center">
                <div class="col-5">
                    <div class="mb-3">
                        <label for="email" class="form-label">Correo:</label>
                        <input type="email" class="form-control" id="email" placeholder="Enter email" name="email">
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email:</label>
                        <input type="email" class="form-control" id="email" placeholder="Enter email" name="email">
                    </div>
                    <div class="mb-3">
                        <label for="pwd" class="form-label">Password:</label>
                        <input type="password" class="form-control" id="pwd" placeholder="Enter password" name="pswd">
                    </div>
                    <div class="mb-3">
                        <label for="pwd" class="form-label">Password:</label>
                        <input type="password" class="form-control" id="pwd" placeholder="Enter password" name="pswd">
                    </div>
                </div>
                <div class="col-5">
                    <label class="mb-2" for="">Seleccione a una persona:</label>
                    <div class="input-group mb-3">
                        <button id="buttonBuscar" class="btn btn-outline-secondary" type="button" id="button-addon1">Buscar</button>
                        <input id="inputCedulaPersona" type="text" class="form-control" placeholder="" aria-label="Example text with button addon" aria-describedby="button-addon1" disabled>
                    </div>
                    <div class="mb-3">
                        <label for="inputNombresPersona" class="form-label">Nombres :</label>
                        <input type="text" class="form-control" id="inputNombresPersona" placeholder="Enter email" name="inputNombresPersona" disabled>
                    </div>
                    <input type="hidden" name="persona_id" id="persona_id" value="{{old('persona_id')}}">
                </div>
                <div class="col-5">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </div>

            </div>
        </form>
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
<script src="//code.jquery.com/jquery-3.5.1.js"></script>
<!-- DataTables -->
<script src="//cdn.datatables.net/1.12.1/js/jquery.dataTables.min.js"></script>
<script src="//cdn.datatables.net/1.12.1/js/dataTables.bootstrap5.min.js"></script>
<script src="//cdn.datatables.net/rowreorder/1.2.8/js/dataTables.rowReorder.min.js"></script>
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
        var inputCedulaPersona = document.getElementById('inputCedulaPersona');
        var inputNombresPersona = document.getElementById('inputNombresPersona');
        var persona_id = document.getElementById('persona_id');
        inputCedulaPersona.value = cedula;
        inputNombresPersona.value = nombres+' '+apellidos;
        persona_id.value = id;
        var modal = bootstrap.Modal.getInstance(modalPersona)
        modal.hide();
    }
</script>
@endpush

