@extends('layouts.app')
@section('title', 'Lista de remisiones')
@push('styles')
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Lista de remisiones de interes</h2>
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
                            <th scope="col">Estado</th>
                            <th scope="col">fecha</th>
                            <th scope="col">Interes</th>
                            <th scope="col">Total</th>
                            <th scope="col">Observacion</th>
                            </tr>
                        </thead>
                        <tbody>
                            @isset($RemisionInteres)
                                @foreach ($RemisionInteres as $item)
                                    <tr>
                                        <td><a href="{{route('show.remision',$item->id)}}" class="btn btn-primary btn-sm"><i class="bi bi-eye"></i></a></td>
                                        <td>{{$item->num_predio}}</td>
                                        <td>{{$item->num_resolucion}}</td>
                                        @if ($item->estado == 'creado')
                                        <td><span class="badge bg-primary">Creado</span></td>
                                        @else
                                        <td><span class="badge bg-success">Aplicado</span></td>
                                        @endif
                                        <td>{{$item->created_at}}</td>
                                        <td>{{$item->valorInteres}}</td>
                                        <td>{{$item->valorTotal}}</td>
                                        <td>{{$item->observacion}}</td>
                                    </tr>
                                @endforeach
                            @endisset
                        </tbody>
                    </table>
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
/*
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
                "url": '{{ url("/remision/datatables") }}',
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
                {width: '',data: 'estado'},
                {width: '',data: 'created_at'},
                {width: '',data: 'observacion'},

            ],
            "fixedColumns" : true
        });
    });*/
</script>
@endpush
