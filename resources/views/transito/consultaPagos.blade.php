@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
<style>
     /* Menú desplegable */
    .dt-dropdown-content{
        display:none;
        position:absolute;
        right:0;
        top:100%;
        margin-top:2px;
        background:#fff;
        min-width:180px;
        box-shadow:0 6px 16px rgba(0,0,0,.15);
        border-radius:8px;
        overflow:hidden;
        z-index:9999;
        border:1px solid #e5e7eb;
    }

    /* Mostrar con clic (NO hover) */
    .dt-dropdown.active .dt-dropdown-content{
        display:block;
    }

    /* Opciones */
    .dt-dropdown-content a{
        display:flex;
        align-items:center;
        gap:8px;
        padding:9px 12px;
        text-decoration:none;
        color:#374151;
        font-size:13px;
        transition:background .15s ease, padding-left .15s ease;
    }

    .dt-dropdown-content a:hover{
        background:#f3f4f6;
        padding-left:16px;
    }

    /* Divisor */
    .dt-divider{
        height:1px;
        background:#e5e7eb;
        margin:4px 0;
    }

    /* Eliminar */
    .dt-dropdown-content a.eliminar{
        color:#dc2626;
    }

    .dt-dropdown-content a.eliminar:hover{
        background:#fee2e2;
    }

</style>
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
            <div class="col-md-6">
                <label for="filtroDesde">Desde:</label>
                <input type="date" class="form-control" id="filtroDesde" onchange="cambioData()"  placeholder="Desde">
            </div>
            <div class="col-md-6">
                <label for="filtroDesde">Hasta:</label>
                <input type="date" class="form-control" id="filtroHasta" onchange="cambioData()" placeholder="Hasta">
            </div>
           
            <div class="col-md-4" style="display: none;">
                <label for="filtroDesde">Filtro:</label>
                <select id="filtroTipo" class="form-select" onchange="cambioData()">
                    <option value="Todos">Todos</option>
                    <option value="Urbano">Urbano</option>
                    <option value="Rural">Rural</option>
                </select>
            </div> 
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
                <h3 style="text-align:center">TOTAL RECAUDADO</h3>
                 <h3 style="text-align:center"><span id="total_recaudado"></span></h3>
                <button type="button" class="btn btn-sm btn-success" onclick="descargarPdf()">Descargar</button>
            </center>
        </div>
        <!-- <div class="col-md-12" id="tabla_listado" style="display:none"> -->
        <div class="col-md-12" id="tabla_listado" >
            <h4 >Listado</h4>

            <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                <table id="tabla_ingreso" width="100%"class="table table-bordered table-striped">
                    <thead>
                        <tr>
                            <!-- <th class="text-center">Documento</th> -->
                            <th class="text-center">Vehiculo</th>
                            <th class="text-center">Propietario</th>
                            <th class="text-center">Detalle Pago</th>
                            <th class="text-center">Valor</th>                            
                            <th style="min-width: 30%" class="text-center">Opciones</th>
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

    <div class="modal fade" id="documentopdf" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                @csrf
                <div class="modal-body">
                <div class="row">
                        <div class="col-sm-12 col-xs-11 "style="height: auto ">
                                <iframe width="100%" height="500" frameborder="0"id="iframePdf"></iframe>
                                    <p style="color: #747373;font-size:15px"></p>
                            </div>
                        </div>
                </div>
                <div class="modal-footer"> 
                    <center>
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>
                            <a href=""id="vinculo"><button  type="button" id="descargar"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>                                 
                    </center>               
                </div>
            </div>
        </div>
    </div>

@endsection
@push('scripts')
<!-- jQuery -->
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script>

<!-- DataTables Buttons -->
    <script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>

<!-- DataTables -->

<script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
<script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
<script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>

<script src="{{ asset('js/transito/reporteria.js?v='.rand())}}"></script>

@endpush
