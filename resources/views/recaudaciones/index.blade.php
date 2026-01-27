@extends('layouts.appv2')
@section('title', 'Recaudaciones')
@push('styles')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
<style>
    table.dataTable {
    width: 100% !important;

    }

    .dataTables_scrollHeadInner,
    .dataTables_scrollHeadInner table {
        width: 100% !important;
    }

    /* CLAVE para alinear thead y tbody */
.dataTables_scrollHeadInner table,
.dataTables_scrollBody table {
    table-layout: fixed !important;
    width: 100% !important;
}



</style>
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2" id="titulo_recaudacion">Reporte Recaudacion</h4>
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
                        <option value="Urbano">Urbano</option>
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
                    <button type="button" class="btn btn-sm btn-success" onclick="verDetalle()">Detalle</button>
                </center>
            </div>

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
                                <td colspan="5"><center>No hay Datos Disponibles</center></td>
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


    <div class="row" id="vista_detalle_urb" style="display:none">

        <ul class="nav nav-tabs" id="miTab" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home" type="button"
                    role="tab" aria-controls="home" aria-selected="true">
                    Efectivo
                </button>
            </li>

            <li class="nav-item" role="presentation">
                <button class="nav-link" id="perfil-tab" data-bs-toggle="tab" data-bs-target="#perfil" type="button" role="tab"
                    aria-controls="perfil" aria-selected="false">
                    Notas de Credito
                </button>
            </li>

            <li class="nav-item" role="presentation">
                <button class="nav-link" id="contacto-tab" data-bs-toggle="tab" data-bs-target="#contacto" type="button"
                    role="tab" aria-controls="contacto" aria-selected="false">
                    Cheque
                </button>
            </li>

             <li class="nav-item" role="presentation">
                <button class="nav-link" id="tarjeta_credito-tab" data-bs-toggle="tab" data-bs-target="#tarjeta_credito" type="button"
                    role="tab" aria-controls="tarjeta_credito" aria-selected="false">
                    Tarjeta Credito
                </button>
            </li>

             <li class="nav-item" role="presentation">
                <button class="nav-link" id="transferencia-tab" data-bs-toggle="tab" data-bs-target="#transferencia" type="button"
                    role="tab" aria-controls="transferencia" aria-selected="false">
                    Transferencia
                </button>
            </li>

             <li class="nav-item" role="presentation">
                <button class="nav-link" id="compensaciones-tab" data-bs-toggle="tab" data-bs-target="#compensaciones" type="button"
                    role="tab" aria-controls="compensaciones" aria-selected="false">
                    Compensaciones
                </button>
            </li>
        </ul>

        <div class="tab-content border border-top-0 p-3" id="miTabContent">
            <div class="tab-pane fade show active" id="home" role="tabpanel" aria-labelledby="home-tab" tabindex="0">
               

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio">
                    <h4>Listado Predial Urbano</h4>

                    <div class="col-md-12" id="btn_descargar_1">
                        <center>
                            <button id="btnExcelUrb" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Imp Predial Urbano</th>
                                    <th class="text-center">Interes</th>
                                    <th class="text-center">Descuentos</th>
                                    <th class="text-center">Recargos</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="7">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_cem">
                    <h4>Listado CEM</h4>

                    <div class="col-md-12" id="btn_descargar_1">
                        <center>
                            <button id="btnExcelUrbCem" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_cem" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANTA MARTHA</th>
                                    <th class="text-center">CEM-ALCANTARILLADOS Y VIAS</th>
                                    <th class="text-center">CEM-AREA RECREACIONAL</th>

                                    <th class="text-center">CEM-MERCADO MUNICIPA</th>
                                    <th class="text-center">CEM-PARQUES Y PLAZAS</th>
                                    <th class="text-center">CEM-PAVIMENTACION MALLA URBANA SV</th>
                                    <th class="text-center">CEM REGENERACION MALECON SV</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="11">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_otros">
                    <h4>Listado Otros</h4>

                    <div class="col-md-12" id="btn_descargar_2">
                        <center>
                            <button id="btnExcelUrbOtros" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_otros" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Seguridad Ciudadana</th>
                                    <th class="text-center">Cuerpo Bomberos Urbanos</th>
                                    <th class="text-center">Servicios Administrativos Urbanos</th>


                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

            </div>

            <div class="tab-pane fade" id="perfil" role="tabpanel" aria-labelledby="perfil-tab" tabindex="0">

                <div class="col-md-12" id="tabla_listado_detalle_urb_predioNC">
                    <h4>Listado Predial Urbano</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbNC" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urbNC" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Imp Predial Urbano</th>
                                    <th class="text-center">Interes</th>
                                    <th class="text-center">Descuentos</th>
                                    <th class="text-center">Recargos</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="7">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_cemNC">
                    <h4>Listado CEM</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCemNC" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_cemNC" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANTA MARTHA</th>
                                    <th class="text-center">CEM-ALCANTARILLADOS Y VIAS</th>
                                    <th class="text-center">CEM-AREA RECREACIONAL</th>

                                    <th class="text-center">CEM-MERCADO MUNICIPA</th>
                                    <th class="text-center">CEM-PARQUES Y PLAZAS</th>
                                    <th class="text-center">CEM-PAVIMENTACION MALLA URBANA SV</th>
                                    <th class="text-center">CEM REGENERACION MALECON SV</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="11">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_otrosNC">
                    <h4>Listado Otros</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbOtrosNC" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive col-md-12" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_otrosNC" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Seguridad Ciudadana</th>
                                    <th class="text-center">Cuerpo Bomberos Urbanos</th>
                                    <th class="text-center">Servicios Administrativos Urbanos</th>


                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>


            </div>

            <div class="tab-pane fade" id="contacto" role="tabpanel" aria-labelledby="contacto-tab" tabindex="0">
                <div class="col-md-12" id="tabla_listado_detalle_urb_predioCH">
                    <h4>Listado Predial Urbano</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCH" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urbCH" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Imp Predial Urbano</th>
                                    <th class="text-center">Interes</th>
                                    <th class="text-center">Descuentos</th>
                                    <th class="text-center">Recargos</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="7">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_cemNC">
                    <h4>Listado CEM</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCemCH" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_cemCH" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANTA MARTHA</th>
                                    <th class="text-center">CEM-ALCANTARILLADOS Y VIAS</th>
                                    <th class="text-center">CEM-AREA RECREACIONAL</th>

                                    <th class="text-center">CEM-MERCADO MUNICIPA</th>
                                    <th class="text-center">CEM-PARQUES Y PLAZAS</th>
                                    <th class="text-center">CEM-PAVIMENTACION MALLA URBANA SV</th>
                                    <th class="text-center">CEM REGENERACION MALECON SV</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="11">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_otrosNC">
                    <h4>Listado Otros</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbOtrosCH" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_otrosCH" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Seguridad Ciudadana</th>
                                    <th class="text-center">Cuerpo Bomberos Urbanos</th>
                                    <th class="text-center">Servicios Administrativos Urbanos</th>


                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>
            </div>

            <div class="tab-pane fade" id="tarjeta_credito" role="tabpanel" aria-labelledby="tarjeta_credito-tab" tabindex="0">
                <div class="col-md-12" id="tabla_listado_detalle_urb_predioTC">
                    <h4>Listado Predial Urbano</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbTC" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urbTC" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Imp Predial Urbano</th>
                                    <th class="text-center">Interes</th>
                                    <th class="text-center">Descuentos</th>
                                    <th class="text-center">Recargos</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="7">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_cemTC">
                    <h4>Listado CEM</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCemTC" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_cemTC" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANTA MARTHA</th>
                                    <th class="text-center">CEM-ALCANTARILLADOS Y VIAS</th>
                                    <th class="text-center">CEM-AREA RECREACIONAL</th>

                                    <th class="text-center">CEM-MERCADO MUNICIPA</th>
                                    <th class="text-center">CEM-PARQUES Y PLAZAS</th>
                                    <th class="text-center">CEM-PAVIMENTACION MALLA URBANA SV</th>
                                    <th class="text-center">CEM REGENERACION MALECON SV</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="11">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_otrosTC">
                    <h4>Listado Otros</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbOtrosTC" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_otrosTC" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Seguridad Ciudadana</th>
                                    <th class="text-center">Cuerpo Bomberos Urbanos</th>
                                    <th class="text-center">Servicios Administrativos Urbanos</th>


                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>
            </div>

            <div class="tab-pane fade" id="transferencia" role="tabpanel" aria-labelledby="transferencia-tab" tabindex="0">
                <div class="col-md-12" id="tabla_listado_detalle_urb_predioTR">
                    <h4>Listado Predial Urbano</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbTR" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urbTR" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Imp Predial Urbano</th>
                                    <th class="text-center">Interes</th>
                                    <th class="text-center">Descuentos</th>
                                    <th class="text-center">Recargos</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="7">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_cemTR">
                    <h4>Listado CEM</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCemTR" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_cemTR" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANTA MARTHA</th>
                                    <th class="text-center">CEM-ALCANTARILLADOS Y VIAS</th>
                                    <th class="text-center">CEM-AREA RECREACIONAL</th>

                                    <th class="text-center">CEM-MERCADO MUNICIPA</th>
                                    <th class="text-center">CEM-PARQUES Y PLAZAS</th>
                                    <th class="text-center">CEM-PAVIMENTACION MALLA URBANA SV</th>
                                    <th class="text-center">CEM REGENERACION MALECON SV</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="11">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_otrosTR">
                    <h4>Listado Otros</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbOtrosTR" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_otrosTR" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Seguridad Ciudadana</th>
                                    <th class="text-center">Cuerpo Bomberos Urbanos</th>
                                    <th class="text-center">Servicios Administrativos Urbanos</th>


                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>
            </div>

            <div class="tab-pane fade" id="compensaciones" role="tabpanel" aria-labelledby="compensaciones-tab" tabindex="0">
                <div class="col-md-12" id="tabla_listado_detalle_urb_predioCOM">
                    <h4>Listado Predial Urbano</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCOM" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urbCOM" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Imp Predial Urbano</th>
                                    <th class="text-center">Interes</th>
                                    <th class="text-center">Descuentos</th>
                                    <th class="text-center">Recargos</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="7">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_cemTC">
                    <h4>Listado CEM</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbCemCOM" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_cemCOM" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANITARIO PLUVIAL AAPP MALLA URBANA SV</th>
                                    <th class="text-center">CEM-ALCANTARILLADO SANTA MARTHA</th>
                                    <th class="text-center">CEM-ALCANTARILLADOS Y VIAS</th>
                                    <th class="text-center">CEM-AREA RECREACIONAL</th>

                                    <th class="text-center">CEM-MERCADO MUNICIPA</th>
                                    <th class="text-center">CEM-PARQUES Y PLAZAS</th>
                                    <th class="text-center">CEM-PAVIMENTACION MALLA URBANA SV</th>
                                    <th class="text-center">CEM REGENERACION MALECON SV</th>

                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="11">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>

                <div class="col-md-12" id="tabla_listado_detalle_urb_predio_otrosCOM">
                    <h4>Listado Otros</h4>

                    <div class="col-md-12" >
                        <center>
                            <button id="btnExcelUrbOtrosCOM" class="btn btn-success btn-sm">
                                <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" onclick="regresar()">Regresar</button>
                        </center>
                    </div>

                    <div class="table-responsive" style="margin-bottom:20px; margin-top:10px">
                        <table id="tabla_detalle_urb_otrosCOM" width="100%" class="table table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center">Año</th>
                                    <th class="text-center">Num Titulo</th>
                                    <th class="text-center">Seguridad Ciudadana</th>
                                    <th class="text-center">Cuerpo Bomberos Urbanos</th>
                                    <th class="text-center">Servicios Administrativos Urbanos</th>


                                    <th class="text-center">Total</th>

                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colspan="6">
                                        <center>No hay Datos Disponibles
                                    </td>
                                </tr>

                            </tbody>

                        </table>
                    </div>

                </div>
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
<!-- DataTables Buttons -->
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>

<!-- Excel -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>

<script src="{{ asset('js/recaudacion/reporteria.js?v=' . rand())}}"></script>

@endpush
