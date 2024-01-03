@extends('layouts.app')
@push('styles')
<link rel="stylesheet" href="//cdn.datatables.net/1.12.1/css/dataTables.bootstrap5.min.css">
<link rel="stylesheet" href="//cdn.datatables.net/rowreorder/1.2.8/css/rowReorder.bootstrap5.min.css">
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12">
                <h2 class="text-center">LISTA DE PERSONAS</h2>
            </div>
        </div>
        <div class="row">
            @csrf
            <div class="table-responsive">
                <table class="table table-bordered" style="width:100%" id="tablePersona">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th>Foto</th>
                            <th scope="col">Cedula</th>
                            <th scope="col">Historia clinica</th>
                            <th scope="col">Nombres</th>
                            <th scope="col">Apellidos</th>
                            <th scope="col">Fecha Nacimiento</th>
                            <th scope="col">Provincia</th>
                            <th scope="col">Canton</th>
                            <th scope="col">Ciudad</th>
                            <th scope="col">Direccion</th>
                        </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
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
        tablePersona = $("#tablePersona").DataTable({
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
                    d.formulario = "persona";
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'foto', name: 'foto', orderable: false, searchable: false},
                {width: '',data: 'cedula'},
                {width: '',data: 'historiaClinica'},
                {width: '',data: 'nombres'},
                {width: '',data: 'apellidos'},
                {width: '',data: 'fechaNacimiento'},
                {width: '',data: 'provincia'},
                {width: '',data: 'canton'},
                {width: '',data: 'ciudad'},
                {width: '',data: 'direccion'},

            ],
            "fixedColumns" : true
        });
    })
</script>
@endpush
