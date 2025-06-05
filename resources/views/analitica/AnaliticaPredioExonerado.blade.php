@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Analitica de Predios Exonerados</h4>
        <div class="btn-toolbar mb-2 mb-md-0">

        </div>
    </div>
    <!-- Sección de filtros -->
    <form id="formAnaliticaPredio" method="POST"  action=""  enctype="multipart/form-data">
        @csrf
        <div class="row mb-4">
            <div class="col-md-2"></div>
            <div class="col-md-8">
                <label for="filtroDesde">Filtro:</label>
                <select id="filtroTipo" class="form-select" onchange="cambioData()">
                    <!-- <option value="Todos">Todos</option> -->
                    <option value="Urbano">Urbano</option>
                    <!-- <option value="Rural">Rural</option> -->
                </select>
            </div>
            <div class="col-md-2"></div>
           
            <!-- <div class="col-md-3">
                <button class="btn btn-primary" id="btnFiltrar">Filtrar</button>
            </div> -->
        </div>
    </form>

    <div class="row">

        <div class="col-md-12"  id="btn_consultar">
            <center>
                <button type="button" class="btn btn-sm btn-primary" onclick="mostrarData()">Consultar</button>
            </center>
        </div>

        <div class="col-md-12"  style="display:none" id="btn_descargar">
            <center>
                <button type="button" class="btn btn-sm btn-success" onclick="descargarPdf()">Descargar</button>
            </center>
        </div>
        <div class="col-md-12" id="grafico_urbano" style="display:none">
            <h3 >Urbano</h3>
            <div id="total-info-urb"></div>
            <canvas id="myChart" width="600" height="200"></canvas>
        </div>

        <div class="col-md-12" id="grafico_rural" style="display:none">
            <h3 >Rural</h3>
            <canvas id="myChartRural" width="600" height="200"></canvas>
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

<script src="{{ asset('js/analitica/predio-exonerado.js?v='.rand())}}"></script>

@endpush
