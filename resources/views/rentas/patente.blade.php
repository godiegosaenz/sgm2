@extends('layouts.appv2')
@section('title', 'Mostrar lista de patente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Listado de patentes e impuesto anual del 1.5 por mil sobre los activos totales</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            
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
                <table class="table table-bordered" style="width:100%" id="tablapatente">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Cedula/RUC</th>
                            <th scope="col">Contribuyente</th>
                            <th scope="col">Numero Titulo</th>
                            <th scope="col">Fecha</th>
                            <th scope="col">Total a pagar</th>
                         

                        </tr>
                    </thead>
                    <tbody>

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

    <div class="modal fade" id="tituloEliminaModal" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Baja de Titulo</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
               
                <div class="modal-body">
                    <div class="row">
                        
                        <form method="POST" action="" id="form_baja">
                            @csrf

                            <div class="col-md-12">
                                <div class="row align-items-center">
                                    <div class="col-md-2 text-end">
                                        <label for="marca_v" class="form-label mb-0">Motivo</label>
                                    </div>
                                    <div class="col-md-9">
                                        <input type="hidden" class="form-control" id="id_impuesto" name="id_impuesto">
                                        <textarea class="form-control" id="motivo_baja" name="motivo_baja"
                                            required rows="4"></textarea>
                                    </div>
                                </div>
                            </div>


                            <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                <div class="row align-items-center">
                                    <div class="col-md-2 text-end">
                                        <label for="marca_v" class="form-label mb-0"></label>
                                    </div>
                                    <div class="col-md-9">
                                        <button type="submit" class="btn btn-success btn-sm"><span
                                                id="btn_tipo">Aceptar</span></button>
                                        <button type="button" class="btn btn-warning btn-sm"
                                            onclick="cancelarBaja()">Cancelar</button>
                                    </div>
                                </div>
                            </div>

                        </form>

                    </div>
                
                </div>
            </div>
        </div>
    </div>    

     <div class="modal fade" id="PagoModal" tabindex="-1" aria-labelledby="ContribuyenteModalLabelz" aria-hidden="true">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Cobro Titulo</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
               
                <div class="modal-body">
                    <div class="row">

                        <div class="col-md-7">
                            
                            <center><span><b>Datos del Contribuyente:</b></span> <span id="anio_proceso" class="detalle_cobro"></span></center><br>
                            <span><b>Razon Social:</b></span> <span id="persona" class="detalle_cobro"></span><br>
                            <span><b>RUC :</b></span> <span id="ruc_cont" class="detalle_cobro"></span><br>
                            <span><b>Obligado a llevar contabilidad :</b></span> <span id="lleva_conta" class="detalle_cobro"></span><br>
                            <span><b>Regimen :</b></span> <span id="regimen"></span><br><br>
                            

                            <center><span><b>Datos del Local</b></span></center>
                            <span><b>Nombre Comercial:</b></span> <span id="nombre_local" class="detalle_cobro"></span><br>
                            <span><b>Direccion:</b></span> <span id="direccion_local" class="detalle_cobro"></span><br>
                            <span><b>Local:</b></span> <span id="estado_local" class="detalle_cobro"></span><br><br>
                            

                            <center><span><b>Actividades</b></span></center>
                            <div id="actividades" class="detalle_cobro"></div><br><br>
                            
                        </div>

                        <div class="col-md-5">
                            <div class="table-responsive">
                                
                                <table class="table table-bordered" style="width:100%" id="tablaImpuestoPatenteDetalle">
                                    <thead>
                                        <tr>
                                            <th scope="col">Rubros/Concepto</th>
                                            <th scope="col">Valor</th>
                                           
                                        </tr>
                                    </thead>
                                    <tbody id="tbodyDetalle">

                                        <tr>
                                            <td><b>Impuesto Patente Municipal:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b> </td>
                                           
                                            <td style="text-align:right"><span id="valor_imp_pat_muni" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Exoneracion:</b> </td>
                                           
                                            <td style="text-align:right">-<span id="exoneracion_pat_muni" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Servicios Administrativo:</b> </td>
                                           
                                            <td style="text-align:right"><span id="serv_adm_pat_muni" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Intereses:</b> </td>
                                            
                                            <td style="text-align:right"><span id="intereses_pat_muni" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Recargos:</b> </td>
                                           
                                            <td style="text-align:right"><span id="recargos_pat_muni" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Total A Pagar:</b> </td>
                                           
                                            <td style="text-align:right"><span id="total_pat_muni" class="detalle_cobro"></span></td>
                                        </tr>

                                    </tbody>
                                </table>

                                <table class="table table-bordered" style="width:100%" id="tablaImpuestoActivoDetalle">
                                    <thead>
                                        <tr>
                                            <th scope="col">Rubros/Concepto</th>
                                            <th scope="col">Valor</th>
                                           
                                        </tr>
                                    </thead>
                                    <tbody id="tbodyDetalle">

                                        <tr>
                                            <td><b>Impuesto 1.5 x mil Activos Totales:</b> </td>
                                           
                                            <td style="text-align:right"><span id="valor_imp_activo" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Exoneracion:</b> </td>
                                           
                                            <td style="text-align:right">-<span id="valor_exoneracion_activo" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Servicios Administrativo:</b> </td>
                                           
                                            <td style="text-align:right"><span id="valor_servicioadm_activo" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Intereses:</b> </td>
                                            
                                            <td style="text-align:right"><span id="valor_intereses_activo" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Recargos:</b> </td>
                                           
                                            <td style="text-align:right"><span id="valor_recargo_activo" class="detalle_cobro"></span></td>
                                        </tr>

                                        <tr>
                                            <td><b>Total A Pagar:</b> </td>
                                           
                                            <td style="text-align:right"><span id="valor_total_activo" class="detalle_cobro"></span></td>
                                        </tr>

                                    </tbody>
                                </table>

                                <table class="table table-bordered" style="width:100%" id="tablaImpuestoPatenteDetalle">
                                    <thead>
                                        <tr>
                                            <th scope="col">VALOR TOTAL &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
                                            <th scope="col" style="text-align:right"><span id="valor_totalpago" class="detalle_cobro"></span></th>
                                           
                                        </tr>
                                    </thead>
                                   
                                </table>
                            </div>
                        </div>

                        <div class="col-md-12">
                            <center> 
                                <button type="button" class="btn btn-success btn-sm" onclick="registrarCobro()">Registrar Cobro</button>
                                <button type="button" class="btn btn-danger btn-sm" onclick="cerrarCobro()">Cerrar</button>
                                <button type="button" class="btn btn-warning btn-sm" onclick="anularCobro()">Anular</button>
                            </center>
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

<script>
    $(document).ready(function(){
        tablapatente = $("#tablapatente").DataTable({
            "lengthMenu": [ 10, 20],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/patente/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'ruc', name: 'ruc'},
                // {width: '',data: 'contribuyente_name', name: 'contribuyente_name'},
                {width: '',data: 'contribuyente_name'},
                {width: '',data: 'codigo'},
                {width: '',data: 'created_at'},
                {width: '',data: 'total_pagar'},
              
            ],
             "columnDefs": [
                { targets: [0], className: "align-middle" },
                { targets: [1], className: "align-middle" },
                { targets: [2], className: "align-middle" },
                { targets: [3], className: "align-middle" },
                { targets: [4], className: "align-middle" },
                { targets: [5], className: "text-end align-middle" } // <-- alinea la columna 5 a la derecha
            ],
            "fixedColumns" : true
        });
    })
    

   
</script>
<script src="{{ asset('js/patente/listar.js?v='.rand())}}"></script>
@endpush
