@extends('layouts.appv2')
@section('title', 'Mostrar lista de patente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Mostrar lista de patentes</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <a href="{{ route('create.patente') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
                Nuevo declaracion de patente
            </a>
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
        <div class="col-md-12">
            <div class="table-responsive">
                @csrf
                <table class="table table-bordered" style="width:100%" id="tablapatente">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Contribuyente</th>
                            <th scope="col">Razon Social</th>
                            <th scope="col">Fecha declaracion</th>
                            <th scope="col">Contabilidad</th>
                            <th scope="col">Año declaracion</th>
                            <th scope="col">Año balance</th>
                            <th scope="col">Estado</th>

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
 <script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
 <!-- DataTables -->

 <script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>

<script>
    $(document).ready(function(){
        tablapatente = $("#tablapatente").DataTable({
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
                "url": '{{ url("/patente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'ruc', name: 'ruc'},
                {width: '',data: 'contribuyente_name', name: 'contribuyente_name'},
                {width: '',data: 'fecha_declaracion'},
                {width: '',data: 'lleva_contabilidad'},
                {width: '',data: 'year_declaracion'},
                {width: '',data: 'year_balance'},
                {width: '',data: 'estado'},
            ],
            "fixedColumns" : true
        });
    })

    function verPatente(id){
        alert(id)
    }

</script>
@endpush
