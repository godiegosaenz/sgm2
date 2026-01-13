@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Reporte Recaudacion</h4>
        <div class="btn-toolbar mb-2 mb-md-0">

        </div>
    </div>
    <!-- Sección de filtros -->
    <div id="vista_buscador">
        <form id="formReporteria" method="POST"  action=""  enctype="multipart/form-data">
            @csrf
            <div class="row mb-4">
                <div class="col-md-6">
                    <label for="filtroDesde">Fecha:</label>
                    <input type="date" class="form-control" id="filtroDesde" onchange="cambioData()"  placeholder="Desde">
                </div>
                <div class="col-md-6">
                    <label for="filtroDesde">Tipo:</label>
                    <select id="filtroArea" class="form-select" onchange="cambioData()">
                    
                        <option value="Rural">Rural</option>
                    </select>
                </div>
            
            
                <!-- <div class="col-md-3">
                    <button class="btn btn-primary" id="btnFiltrar">Filtrar</button>
                </div> -->
            </div>
        </form>

        <div class="row" >

            <div class="col-md-12"  id="btn_consultar">
                <center>
                    <button type="button" class="btn btn-sm btn-primary" onclick="mostrarData()">Consultar</button>
                </center>
            </div>

            <div class="col-md-12"  style="display:none" id="btn_descargar">
                <center>
                    <h3 style="text-align:center">TOTAL RECAUDADO</h3>
                    <h3 style="text-align:center"><span id="total_recaudado"></span></h3>
                    <button type="button" class="btn btn-sm btn-success" onclick="verDetalle()">Descargar</button>
                </center>
            </div>
            <!-- <div class="col-md-12" id="tabla_listado" style="display:none"> -->
            <div class="col-md-12" id="tabla_listado" >
                <h4 >Listado</h4>

                <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                    <table id="tabla_ingreso" width="100%"class="table table-bordered table-striped">
                        <thead>
                            <tr>
                                <th class="text-center">Codigo</th>
                                <th class="text-center">Descripcion</th>
                                <th class="text-center">Anteriores</th>
                                <th class="text-center">Año {{ date('Y') }}</th>
                                <th class="text-center">Total</th>
                            
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="5"><center>No hay Datos Disponibles</td>
                            </tr>
                            
                        </tbody>
                    
                    </table>  
                </div>
                
            </div>

        </div>
    </div>
    <div class="row" id="vista_detalle" style="display:none">
        <div class="col-md-12"   id="btn_descargar_">
            <center>
                <!-- <button type="button" class="btn btn-sm btn-success" onclick="descargarPdf()">Descargar</button> -->
                <button id="btnExcel" class="btn btn-success btn-sm">
                    <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                </button>
                <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
            </center>
        </div>
        <!-- <div class="col-md-12" id="tabla_listado" style="display:none"> -->
        <div class="col-md-12" id="tabla_listado_detalle" >
            <h4 >Listado</h4>

            <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                <table id="tabla_detalle" width="100%"class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th class="text-center">Año</th>
                            <th class="text-center">Num Titulo</th>
                            <th class="text-center">Imp Predial</th>
                            <th class="text-center">Interes</th>
                            <th class="text-center">Desc</th>
                            <th class="text-center">Recar</th>
                            <th class="text-center">Bomb</th>
                            <th class="text-center">Seguridad</th>
                            <th class="text-center">Serv Adm</th>
                            <th class="text-center">Total</th>
                           
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="10"><center>No hay Datos Disponibles</td>
                        </tr>
                        
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
<!-- DataTables Buttons -->
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>

<!-- Excel -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>

<script src="{{ asset('js/recaudacion/reporteria.js?v='.rand())}}"></script>

@endpush
