@extends('layouts.appv2')
@section('title', 'Exoneraciones anteriores')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Lista de exoneraciones anteriores</h2>
                </div>
            </div>
        </div>

        <br>
        <div class="row">
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered" id="tableExoneracion" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col">Acciones</th>
                            <th scope="col">Matricula</th>
                            <th scope="col">Resolucion</th>
                            <th scope="col">Tipo de exoneracion</th>
                            <th scope="col">fecha</th>
                            <th scope="col">Observacion</th>
                            </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
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
        tableExoneracion = $("#tableExoneracion").DataTable({
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
                "url": '{{ url("/exoneracion/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'num_predio'},
                {width: '',data: 'num_resolucion'},
                {width: '',data: 'tipo'},
                {width: '',data: 'created_at'},
                {width: '',data: 'observacion'},

            ],
            "fixedColumns" : true
        });
    });
</script>
@endpush
