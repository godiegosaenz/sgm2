@extends('layouts.appv2')
@section('title', 'Mostrar lista de patente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Lista de impuestos de transito</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <a href="{{ route('create.patente') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
                Nuevo impuesto
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
                <table class="table table-bordered" style="width:100%" id="tablaImpuesto">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Cedula/Ruc</th>
                            <th scope="col">Contribuyente</th>
                            <th scope="col">Vehiculo</th>
                            <th scope="col">Numero de titulo</th>
                            <th scope="col">Total a pagar</th>
                            <th scope="col">Fecha</th>
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
        tablaImpuesto = $("#tablaImpuesto").DataTable({
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
                "url": '{{ url("/transito/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'cc_ruc'},
                {width: '',data: 'contribuyente', name: 'contribuyente'},
                {width: '',data: 'vehiculo'},
                {width: '',data: 'numero_titulo'},
                {width: '',data: 'year_impuesto'},
                {width: '',data: 'created_at'},
            ],
            "fixedColumns" : true
        });
    })

</script>
@endpush
