@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Reporte Pago Transito</h4>
        <div class="btn-toolbar mb-2 mb-md-0">

        </div>
    </div>
    <!-- Sección de filtros -->
    <form id="formReporteria" method="POST"  action=""  enctype="multipart/form-data">
        @csrf
        <div class="row mb-4">
            <div class="col-md-4">
                <label for="filtroDesde">Archivo:</label>
                <input type="file" class="form-control" id="archivo"   placeholder="Arhivo p12">
            </div>
            <div class="col-md-4">
                <label for="filtroDesde">Clave:</label>
                <input type="password" class="form-control" id="clave"  placeholder="Clave">
            </div>
            <div class="col-md-4">
                <label for="filtroDesde">Documento:</label>
                <input type="file" class="form-control" id="documento"  placeholder="documento">
            </div>
            <!-- <div class="col-md-3">
                <button class="btn btn-primary" id="btnFiltrar">Filtrar</button>
            </div> -->
        </div>

        <div class="col-md-12"  id="btn_consultar">
            <center>
                <button type="button" class="btn btn-sm btn-primary" onclick="firmar()">Firmar</button>
            </center>
        </div>
    </form>

   
@endsection
@push('scripts')
<!-- jQuery -->
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script>
<!-- DataTables -->

<script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
<script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
<script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>

<script src="{{ asset('js/transito/firma.js?v='.rand())}}"></script>

@endpush
