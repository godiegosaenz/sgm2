@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Catastro de contribuyentes</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <!-- <div class="btn-group me-2">
            <a href="{{ route('create.catastro') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
                Nuevo Contribuyente
            </a>
        </div> -->
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
    <!-- Sección de filtros -->
    <div class="row mb-4">
        <div class="col-md-3">
            <input type="text" class="form-control" id="filtroNombre" placeholder="Buscar por nombre">
        </div>
        <div class="col-md-3">
            <select id="filtroEstado" class="form-select">
                <option value="">Todos los estados</option>
                <option value="activo">Activo</option>
                <option value="inactivo">Inactivo</option>
            </select>
        </div>
        <div class="col-md-3">
            <button class="btn btn-primary" id="btnFiltrar">Filtrar</button>
        </div>
    </div>
    <!-- Cuadros de datos importantes -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card text-white bg-primary mb-3">
                <div class="card-body">
                    <h5 class="card-title">Total Clientes</h5>
                    <p class="card-text">{{$totalCatastro}}</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-white bg-success mb-3">
                <div class="card-body">
                    <h5 class="card-title">Clientes Activos</h5>
                    <p class="card-text">{{$totalCatastroActivo}}</p>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-white bg-danger mb-3">
                <div class="card-body">
                    <h5 class="card-title">Clientes Inactivos</h5>
                    <p class="card-text">{{$totalCatastroInactivo}}</p>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <div class="table-responsive">
                @csrf
                <table class="table table-bordered" style="width:100%" id="tablaCatastro">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Numero</th>
                            <th scope="col">RUC</th>
                            <th scope="col">Razon Social</th>
                            <th scope="col">Fecha inicio</th>
                            <th scope="col">Contabilidad</th>
                            <th scope="col">Estado</th>
                        </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
        </div>
    </div>
    @include('rentas.modal_locales')
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
        tablePersona = $("#tablaCatastro").DataTable({
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
                "url": '{{ url("/catastrocontribuyente/datatables2") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'locales.length'},
                {width: '',data: 'ruc', name: 'ruc'},
                {width: '',data: 'razon_social'},
                {width: '',data: 'fecha_inicio_actividades'},
                {width: '',data: 'obligado_contabilidad'},
                {width: '',data: 'estado_contribuyente_id'},
            ],
            "fixedColumns" : true
        });
    })

    function abrirModal(id){
        $('#modal_local').modal('show')
        $('#idcont').val(id)
        llenar_tabla_locales(id)
    }
</script>
@endpush
