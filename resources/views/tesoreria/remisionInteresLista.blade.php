@extends('layouts.appv2')
@section('title', 'Lista de remisiones')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Lista de remisiones</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    <div class="btn-group me-2">
        <button type="button" class="btn btn-sm btn-outline-secondary">Share</button>
        <button type="button" class="btn btn-sm btn-outline-secondary">Export</button>
    </div>
    <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
        <svg class="bi"><use xlink:href="#calendar3"/></svg>
        This week
    </button>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        @csrf
        <div class="table-responsive">
            <table class="table table-bordered" id="tableremison" style="width: 100%">
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
        tableremison = $("#tableremison").DataTable({
            "lengthMenu": [ 5, 10],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : false,
            "serverSide": false,
            "fixedColumns" : true
        });
    });
</script>
@endpush
