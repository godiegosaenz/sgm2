@extends('layouts.appv2')
@section('title', 'Listado Notificaciones')
@push('styles')
<link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css" rel="stylesheet" />
<style>
.checkbox-grande {
    transform: scale(1.5); /* Ajusta el número para el tamaño deseado */
    margin-right: 8px; /* Ajuste opcional */
}
.desabilita_txt{
    display: block;
}
.readonly-checkbox {
    pointer-events: none; /* Evita la interacción */
    opacity: 0.5; /* Visualmente deshabilitado */
}

.select2-container .select2-selection--single {
    height: 36px !important;
}

.fa-custom-border {
    border: 2px solid #28a745; /* verde */
    padding: 5px;              /* espacio interno para que se vea el borde */
    border-radius: 4px;        /* opcional, esquinas redondeadas */
}

.badge-green {
    display: inline-block;
    padding: 4px 10px;
    border: 2px solid #28a745; /* verde */
    color: #28a745;
    border-radius: 8px; /* estilo badge */
    font-size: 12px;
    font-weight: 600;
}

.badge-blue-low {
    display: inline-block;
    padding: 4px 10px;
    border: 2px solid #4da3ff; /* azul claro / azul bajo */
    color: #4da3ff;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;
}

.fila-verde{
    background-color: #28a745 !important;
}

.fila-azul{
    background-color: #4da3ff !important;
}

.p-4 {
    padding: 0.5rem !important;
}
.badge-coactivado {
    background: linear-gradient(135deg, #e74c3c, #c0392b);
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0,0,0,.15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}
.badge-notificado{
   
    background: linear-gradient(135deg,#0d6efd,#36a2ff);
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0,0,0,.15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.badge-orden {
    background: linear-gradient(135deg, #ffa500, #ff7f00); /* Naranja bajo */
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, .15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.badge-cancelado {
    background: linear-gradient(135deg, #007bff, #0056b3); /* Azul bajo */
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, .15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.badge-acuerdo {
    background: linear-gradient(135deg, #28a745, #218838); /* Verde */
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, .15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.badge-medidas {
    background: linear-gradient(135deg, #ec8c6f, #ec8c6f); /* Color dorado */
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, .15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.badge-expirado{
    background: linear-gradient(135deg,#dc3545,#ff6b6b); /* rojo suave */
    color: white;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: .3px;
    box-shadow: 0 2px 6px rgba(0,0,0,.15);
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.badge-sin-expirar{
    background: linear-gradient(145deg,#1db954,#6fffa2); /* verde brillante */
    color: #0b1f14; /* texto oscuro para contraste */
    width: 34px;
    height: 34px;
    border-radius: 50%;
    font-weight: 700;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: auto;
    box-shadow: 
        0 0 0 3px rgba(29,185,84,0.25),
        0 4px 10px rgba(0,0,0,.35);
}
</style>


<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">

@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Procesos Coactivados</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    
   
    </div>
</div>


    <div class="row">
        <div class="col">
            @if (session('status'))
                <div class="alert alert-success">
                    {{ session('status') }}
                </div>
            @endif
        </div>
    </div>
   

    <div>
      
        <div class="row mt-3">
            <div class="col-md-12 row">
                <div class="col-md-3"></div>
                <div class="col-6">
                    <div class="mb-3">
                        <label for="num_predio">* Periodo: </label>
                        <input type="month" class="form-control" name="periodo" id="periodo" onchange="llenar_tabla_notificacion()">

                    </div>
                </div>

                <div class="col-md-3"></div>
            </div>
           
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tableCoactiva" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col"></th>
                            <th scope="col">Contribuyente</th>                            
                            <th>Fecha Coactiva</th>
                            <th scope="col">Debe Pago Voluntario</th>
                            <th scope="col">Debe Pago Inmediato</th>
                            <th scope="col">Valor Cancelado</th>
                            <th scope="col">Estado Pago</th>
                            <th scope="col">Estado Proceso</th>
                        
                            </tr>
                        </thead>
                        <tbody id="tbodyNotificacion">
                            <tr>
                                <td colspan="8" style="text-align:center">No hay datos disponibles</td>
                            </tr>
                           
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalDetalleNot" tabindex="-1"
     aria-labelledby="ContribuyenteModalLabel"
     aria-hidden="true"
     data-bs-backdrop="static"
     data-bs-keyboard="false">

        <div class="modal-dialog modal-xl">
            <div class="modal-content">

                <!-- HEADER -->
                <div class="modal-header">
                    <h5 class="modal-title titulo_modal" id="modalContribuyenteLabel">
                        Detalle
                    </h5>
                    <button type="button" class="btn-close" onclick="cerrarModalNot()"></button>
                </div>

                <!-- BODY -->
                <div class="modal-body">

                    <ul class="nav nav-tabs mb-3" role="tablist">
                        <li class="nav-item">
                            <button class="nav-link active"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-notifica"
                                    type="button">
                                <i class="bi bi-list-check text-success"></i> Notificacion
                            </button>
                        </li>
                        <li class="nav-item">
                            <button class="nav-link"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-coact"
                                    type="button">
                                <i class="bi bi-bell-fill text-danger"></i> Coactiva
                            </button>
                        </li>

                        <li class="nav-item">
                            <button class="nav-link"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-convenio"
                                    type="button">
                                <i class="fa fa-handshake-o text-success"></i> Convenio
                            </button>
                        </li>

                        <li class="nav-item">
                            <button class="nav-link"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-medidas"
                                    type="button">
                                <i class="fa fa-eyedropper text-warning"></i> Medidas
                            </button>
                        </li>

                        <li class="nav-item">
                            <button class="nav-link"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-cancelado"
                                    type="button">
                                <i class="fa fa-money text-primary"></i> Cancelado / Archivado
                            </button>
                        </li>

                    </ul>

                    <div class="tab-content">

                        <!-- TAB 1: CONTENIDO ACTUAL (SIN CAMBIOS) -->
                        <div class="tab-pane fade show active" id="tab-notifica">

                            <div class="col-md-12">
                                <form class=""
                                        action="tituloscoactivarural/imprimir"
                                        id="formExonerar"
                                        name="formExonerar"
                                        method="post"
                                        enctype="multipart/form-data">
                                        @csrf

                                        <div class="col-md-12">
                                            <div class="row">
                                                <center><h5>Datos del Notificador</h5></center>
                                                <div class="col-md-6">
                                                    <b>Usuario:</b>
                                                    <span id="nombre_notificador" class="label_not"></span><br>
                                                    <input type="hidden" name="id_notifica" id="id_notifica">
                                                </div>

                                                <div class="col-md-6">
                                                    <b>Fecha Notificacion:</b>
                                                    <span id="fecha_notificacion" class="label_not"></span><br>
                                                </div>

                                                <hr>

                                                <center><h5>Datos del Contribuyente</h5></center>
                                                <div class="col-md-6">                                           
                                                    <b>Contribuyente:</b>
                                                    <span id="num_ident_contr" class="label_not"></span><br>
                                                </div>
                                                <div class="col-md-6">
                                                    <b>C.I./RUC:</b>
                                                    <span id="nombre_contr" class="label_not"></span><br>
                                                </div>

                                                <hr>

                                                <center><h5>Otros Datos</h5></center>
                                                <div class="col-md-6">                                           
                                                    <b>Documento Generado:</b>
                                                    <span id="doc_generado" class="label_not"></span><br>
                                                </div>
                                                <div class="col-md-6">
                                                    <b>Documento Subido:</b>
                                                    <span id="doc_subido" class="label_not"></span><br>
                                                </div>

                                                
                                                <div class="col-md-6">
                                                    <b>Dias desde Notificacion:</b>
                                                    <span id="dias_notificado" class="label_not dias_notificado"></span><br>
                                                </div>

                                                <div class="col-md-6">                                           
                                                    <b>Predio:</b>
                                                    <span id="predio_localizacion" class="label_not"></span><br>
                                                </div>

                                                <div class="col-md-6">
                                                    <b>Matricula/Clave Catastral:</b>
                                                    <span id="matr_clave" class="label_not"></span><br>
                                                </div>

                                                <hr>


                                            </div>

                                            <div id="seccion_detalle">
                                                <div class="col-md-12 mt-3">
                                                    <h5><center>VALOR NOTIFICACION $<span class="valor_notificado"></span></center></h5>
                                                    <table class="table table-bordered table-hover"
                                                        id="tableDetNot"
                                                        style="width:100%">
                                                        <thead>
                                                            <tr>                                                   
                                                                <th>Matricula/Clave</th>
                                                                <th>Año</th>
                                                                <th>Subtotal</th>
                                                                <th>Interes</th>
                                                                <th>Descuento</th>
                                                                <th>Recargo</th>
                                                                <th>Total</th>
                                                                
                                                            </tr>
                                                        </thead>
                                                        <tbody id="tbodyNotificacionDetalle"></tbody>
                                                    </table>
                                                </div>

                                                <div class="row mt-3 botone_inicia_proceso">
                                                
                                                    <div class="col-md-12 text-center mt-2">
                                                        
                                                        <button type="button"
                                                                class="btn btn-sm btn-success"
                                                                onclick="detalleProcesoIniciaCoa()">
                                                            Iniciar Proceso Coactiva
                                                        </button>
                                                        <button type="button"
                                                                class="btn btn-sm btn-danger"
                                                                onclick="cerrarModalNot()">
                                                            Cerrar
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>

                                            <div id="seccion_inicia_proceso" style="display:none">
                                                <div class="col-md-12 mt-3">
                                                    <h5><center>VALOR PAGO VOLUNTARIO $<span class="valor_notificado"></span></center></h5>
                                                    <h5><center>VALOR PAGO INMEDIATO $<span class="valor_coa"></span></center></h5>
                                                    <table class="table table-bordered table-hover"
                                                        id="tableProcesoNot"
                                                        style="width:100%">
                                                        <thead>
                                                            <tr>                                                   
                                                                <th>Matricula/Clave</th>
                                                                <th>Año</th>
                                                                <th>Subtotal</th>
                                                                <th>Interes</th>
                                                                <th>Descuento</th>
                                                                <th>Recargo</th>
                                                                <th>Total</th>
                                                                
                                                            </tr>
                                                        </thead>
                                                        <tbody id="tbodyProcesoNotDetalle"></tbody>
                                                    </table>
                                                </div>

                                                <div class="row mt-3">
                                                
                                                    <div class="col-md-12 text-center mt-2">
                                                        
                                                        <button type="button"
                                                                class="btn btn-sm btn-primary"
                                                                onclick="iniciarProcesoCoact()">
                                                            Registrar Proceso Coactiva
                                                        </button>
                                                        <button type="button"
                                                                class="btn btn-sm btn-danger"
                                                                onclick="cerrarModalNot()">
                                                            Cerrar
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                           

                                        </div>
                                </form>
                            </div>
                        </div>

                        <div class="tab-pane fade show " id="tab-coact">
                            <div class="col-md-12">

                                <div class="seccion_detalle_coa row" style="display:none">
                                    <hr>
                                    <center><h5>Datos Coactiva</h5></center>
                                    <div class="col-md-6">
                                        <b>Usuario:</b>
                                        <span id="nombre_coactivador" class="label_not"></span><br>
                                        <input type="hidden" name="id_notifica" id="id_notifica">
                                    </div>

                                    <div class="col-md-6">
                                        <b>Fecha Coactiva:</b>
                                        <span id="fecha_coactivador" class="label_not"></span><br>
                                    </div>

                                    
                                    <div class="col-md-6">                                           
                                        <b>Documento Generado:</b>
                                        <span id="doc_generado_coa" class="label_not"></span><br>
                                    </div>
                                    <div class="col-md-6">
                                        <b>Documento Subido:</b>
                                        <span id="doc_subido_coa" class="label_not"></span><br>
                                    </div>

                                <hr>
                                </div>
                                <div class="seccion_detalle_coa" style="display:none">
                                    <div class="col-md-12 mt-3">
                                        <h5><center>VALOR PAGO INMEDIATO $<span class="valor_coa"></span></center></h5>
                                        <table class="table table-bordered table-hover"
                                            id="tableDetCoa"
                                            style="width:100%">
                                            <thead>
                                                <tr>                                                   
                                                    <th>Matricula/Clave</th>
                                                    <th>Año</th>
                                                    <th>Subtotal</th>
                                                    <th>Interes</th>
                                                    <th>Descuento</th>
                                                    <th>Recargo</th>
                                                    <th>Coactiva</th>
                                                    <th>Total</th>
                                                    
                                                </tr>
                                            </thead>
                                            <tbody id="tbodyCoactivaDetalle"></tbody>
                                        </table>
                                    </div>

                                    <!-- <div class="row mt-3">
                                    
                                        <div class="col-md-12 text-center mt-2">
                                            <button type="button"
                                                    class="btn btn-sm btn-primary"
                                                    onclick="cerrarModalNot()">
                                                Pagó
                                            </button>

                                            <button type="button"
                                                    class="btn btn-sm btn-warning"
                                                    onclick="cerrarModalNot()">
                                                Medidas Cautelares
                                            </button>
                                        
                                            <button type="button"
                                                    class="btn btn-sm btn-danger"
                                                    onclick="cerrarModalNot()">
                                                Cerrar
                                            </button>
                                        </div>
                                    </div> -->
                                </div>

                            </div>
                        </div>

                        <div class="tab-pane fade show" id="tab-convenio">

                            <div class="col-md-12">
                                <form class=""
                                        action=""
                                        id="FormConvenio"
                                        name="FormConvenio"
                                        method="post"
                                        enctype="multipart/form-data">
                                        @csrf

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Valor Adeudado<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="number" class="form-control txt_conv" step="0.01" min="0" name="valor_adeudado" id="valor_adeudado">
                                                <input type="hidden" name="lugar_not" id="lugar_not" value="Urbano">
                                                 <input type="hidden" id="idcoa_conv" name="idcoa_conv">
                                            </div>
                                           
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Cuota Inicial<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="number" class="form-control txt_conv"name="cuota_inicial" id="cuota_inicial" step="0.01" min="0">
                                                
                                            </div>
                                           
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Numero de cuotas<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="number" class="form-control txt_conv"name="num_cuotas" id="num_cuotas">
                                                
                                            </div>
                                           
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha Inicio<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="date" class="form-control txt_conv"name="f_ini" id="f_ini">
                                                
                                            </div>
                                           
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha Fin<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="date" class="form-control txt_conv"name="f_fin" id="f_fin">
                                                
                                            </div>
                                           
                                        </div>

                                         <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               
                                            </label>
                                            <div class="col-md-7">
                                                <button type="submit" class="btn btn-success btn-sm">Guardar</button>
                                                
                                            </div>
                                           
                                        </div>

                                </form>

                                 <div class="col-md-12 mt-3">

                                        <table class="table table-bordered table-hover"
                                            id="tableConvenio"
                                            style="width:100%">
                                            <thead>
                                                <tr>     
                                                    <th></th>     
                                                    <th>Fecha Registro</th>                                         
                                                    <th>Valor Adeudado</th>
                                                    <th>Cuota Inicial</th>
                                                    <th># Cuotas</th>
                                                    <th>Fecha Inicio</th>
                                                    <th>Fecha Fin</th>
                                                    <th>Estado</th>
                                                    
                                                    
                                                </tr>
                                            </thead>
                                            <tbody id="tbodyConvenioDetalle"></tbody>
                                        </table>
                                    </div>
                            </div>
                        </div>

                        <div class="tab-pane fade show" id="tab-medidas">

                            <div class="col-md-12">
                                <form class=""
                                        action=""
                                        id="FormMedidas"
                                        name="FormMedidas"
                                        method="post"
                                        enctype="multipart/form-data">
                                        @csrf

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Total Deuda<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="number" class="form-control txt_conv" step="0.01" min="0" name="total_valor_deuda" id="total_valor_deuda">
                                               
                                                <input type="hidden" id="idcoa_medida" name="idcoa_medida">
                                            </div>
                                           
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Medidas Impuestas<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <textarea class="form-control txt_conv" name="medidas_txt" id="medidas_txt" ></textarea>
                                                
                                            </div>
                                           
                                        </div>


                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               
                                            </label>
                                            <div class="col-md-7">
                                                <button type="submit" class="btn btn-success btn-sm">Guardar</button>
                                                
                                            </div>
                                           
                                        </div>

                                </form>

                                 <div class="col-md-12 mt-3">

                                        <table class="table table-bordered table-hover"
                                            id="tableMedidas"
                                            style="width:100%">
                                            <thead>
                                                <tr>     
                                                    <th></th>     
                                                    <th>Fecha Registro</th>                                         
                                                    <th>Total Deuda</th>
                                                    <th>Medidas Impuestas</th>
                                                  
                                                    <th>Estado</th>
                                                    
                                                    
                                                </tr>
                                            </thead>
                                            <tbody id="tbodyMedidasDetalle"></tbody>
                                        </table>
                                    </div>
                            </div>
                        </div>

                        <div class="tab-pane fade show" id="tab-cancelado">

                            <div class="col-md-12">
                                <form class=""
                                        action=""
                                        id="FormCancelado"
                                        name="FormCancelado"
                                        method="post"
                                        enctype="multipart/form-data">
                                        @csrf

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Valor Pago Immediato<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="number" class="form-control " step="0.01" min="0" name="valor_pago_inm" id="valor_pago_inm" readonly>
                                               
                                                <input type="hidden" id="idcoa_pago" name="idcoa_pago">
                                            </div>
                                           
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Valor Cancelado<span class="text-danger"> *</span> 
                                            </label>
                                            <div class="col-md-7">
                                                <input type="number" class="form-control txt_conv" step="0.01" min="0" name="valor_cancelado" id="valor_cancelado">
                                               
                                            </div>
                                           
                                        </div>

                                       
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               
                                            </label>
                                            <div class="col-md-7">
                                                <button type="submit" class="btn btn-success btn-sm">Guardar</button>
                                                
                                            </div>
                                           
                                        </div>

                                </form>

                                 <div class="col-md-12 mt-3">

                                        <table class="table table-bordered table-hover"
                                            id="tableCancelado"
                                            style="width:100%">
                                            <thead>
                                                <tr>     
                                                    <th></th>     
                                                    <th>Fecha Registro</th>                                         
                                                    <th>Valor Cancelado</th>
                                                   
                                                    
                                                    <th>Estado</th>
                                                    
                                                    
                                                </tr>
                                            </thead>
                                            <tbody id="tbodyCanceladoDetalle"></tbody>
                                        </table>
                                    </div>
                            </div>
                        </div>

                    </div>

                   
                </div>

            </div>
        </div>
    </div>


    <div class="modal fade" id="documentopdf" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true"
     data-bs-backdrop="static" data-bs-keyboard="false">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Documento Generado</h2>
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
                            <button class="btn btn-success" onclick="printIframe()">Imprimir</button>                           
                            <a href=""id="vinculo"><button  type="button" id="descargar"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>                                 
                    </center>               
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="documentopdf_subido" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true"
     data-bs-backdrop="static" data-bs-keyboard="false">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Documento Subido</h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                @csrf
                <div class="modal-body">
                <div class="row">
                        <div class="col-sm-12 col-xs-11 "style="height: auto ">
                                <iframe width="100%" height="500" frameborder="0"id="iframePdfSubido"></iframe>
                                    <p style="color: #747373;font-size:15px"></p>
                            </div>
                        </div>
                </div>
                <div class="modal-footer"> 
                    <center>
                            <button class="btn btn-success" onclick="abrirModalSubir()">Subir</button>                           
                            <a href=""id="vinculoSubido"><button  type="button" id="descargarSubido"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>                                 
                    </center>               
                </div>
            </div>
        </div>
    </div>

    
    <div class="modal fade" id="subir_documento" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true"
     data-bs-backdrop="static" data-bs-keyboard="false">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Subir Documento</h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form class="" action="" id="formArchivoFirmado" name="formArchivoFirmado" method="post" enctype="multipart/form-data">
                    @csrf
               
                <div class="modal-body">
                    <div class="row">

                        <div class="mb-3">
                                <label for="cmb_ruc_rep" class="form-label">DOCUMENTOS</label>
                                <input type="file" class="form-control txt_coact" name="archivo" id="archivo" >
                                <input type="hidden" id="idnoti" name="idnoti">
                                <input type="hidden" id="idcoa" name="idcoa">
                                <input type="hidden" id="es_coact" name="es_coact">
                        </div>

                    </div>
                    <div class="modal-footer"> 
                        <center>
                                <button class="btn btn-success" type="submit">Guardar</button>                           
                            
                                <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>                                 
                        </center>               
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="documentopdf_subido_coa" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true"
     data-bs-backdrop="static" data-bs-keyboard="false">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Documento Subido Coa</h2>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                @csrf
                <div class="modal-body">
                <div class="row">
                        <div class="col-sm-12 col-xs-11 "style="height: auto ">
                                <iframe width="100%" height="500" frameborder="0"id="iframePdfSubidoCoa"></iframe>
                                    <p style="color: #747373;font-size:15px"></p>
                            </div>
                        </div>
                </div>
                <div class="modal-footer"> 
                    <center>
                            <button class="btn btn-success" onclick="abrirModalSubir()">Subir</button>                           
                            <a href=""id="vinculoSubidoCoa"><button  type="button" id="descargarSubidoCoa"class="btn btn-primary"><i class="fa fa-mail"></i> Descargar</button> </a>
                            <button type="button" class="btn btn-danger" data-bs-dismiss="modal" >Salir</button>                                 
                    </center>               
                </div>
            </div>
        </div>
    </div>

    @include('coactiva.modal_subido_coa')

   


    
@endsection
@push('scripts')
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
 <!-- DataTables -->

 <script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>
<script>
    
    const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if(isDarkMode){
        applyDarkModeStyles('D')
    }else{
        applyDarkModeStyles('L')
    }


    function applyDarkModeStyles(enable) {
        let styleTag = document.getElementById('dark-mode-styles');

        if (enable=='D' ) {
            // Si el estilo ya existe, no lo agregamos de nuevo
            if (!styleTag) {
                styleTag = document.createElement('style');
                styleTag.id = 'dark-mode-styles';
                styleTag.innerHTML = `
                    .select2-container--default .select2-results__option {
                        background-color: #212529 !important;
                        color: #fff !important;
                    }
                    .select2-container--default .select2-results__option--highlighted {
                        background-color: #343a40 !important;
                        color: #fff !important;
                    }
                    .select2-container--default .select2-results__option[aria-selected="true"] {
                        background-color: #555 !important;
                        color: #fff !important;
                    }
                    .select2-container--default .select2-selection--single {
                        background-color: #212529 !important;
                        color: #fff !important;
                        border: 1px solid #555 !important;
                    }
                    .select2-container--default .select2-selection__arrow b {
                        border-color: #fff transparent transparent transparent !important;
                    }
                    .select2-container--default .select2-search--dropdown .select2-search__field {
                        background-color: #333 !important;
                        color: #fff !important;
                        border: 1px solid #555 !important;
                    }
                    .select2-container--default .select2-results__message {
                        background-color: #212529 !important;
                        color: #bbb !important;
                    }
                    .select2-container--default .select2-selection__rendered {
                        color: white !important;
                    }

                    .sweet-alert {
                        background-color: #18191a;
                    }
                `;
                document.head.appendChild(styleTag);
            }
        } else {           
            // Si el usuario cambia a modo claro, eliminamos la etiqueta de estilos oscuros
            if (styleTag) {
                styleTag.remove();
            }
        }
    }
    // Llamar a la función cuando cambie el tema
    function cambiaTema(isDark) {
        applyDarkModeStyles(isDark);       
    }
    function setTheme(theme) {
        localStorage.setItem('theme', theme);  // Guardar preferencia en localStorage
        applyTheme(theme);
    }

    // Cuando la página carga, recuperamos la preferencia
    window.addEventListener('DOMContentLoaded', (event) => {
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme) {
            applyTheme(savedTheme);  // Aplica el tema guardado
        }
    });

    // Función para aplicar el tema (claro u oscuro)
    function applyTheme(theme) {
        // alert(theme)
        if(theme=='dark'){
            applyDarkModeStyles('D')
        }else if(theme=='light'){
            applyDarkModeStyles('L')
        }
    }
    function getSystemTheme() {
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    var tipo=$('#tipo').val()
   
    // if(tipo=="1"){
    //     $('#div_cedula').show()
    //     $('#div_clave').hide()
    //     $('#div_nombres').hide()
    // }else if(tipo=="2"){
    //     $('#div_cedula').hide()
    //     $('#div_nombres').hide()
    //     $('#div_clave').show()
    // }else{
    //     $('#div_cedula').hide()
    //     $('#div_nombres').show()
    //     $('#div_clave').hide()
    // }
    var buttonBuscar = document.getElementById('buttonBuscar');
    let token = "{{csrf_token()}}";

    var formExonerar = document.getElementById('formExonerar');
    function verificarSeleccionCasillas(){
        let formData2 = new FormData(formExonerar);
        if(formData2.getAll("checkLiquidacion[]").length > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    function printIframe() {
        const iframe = document.getElementById('iframePdf');
        iframe.contentWindow.focus();
        iframe.contentWindow.print();
    }


</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/coactiva/listado_coactiva.js?v='.rand())}}"></script>
<script>
    // llenar_tabla_notificacion()
</script>
@endpush
    