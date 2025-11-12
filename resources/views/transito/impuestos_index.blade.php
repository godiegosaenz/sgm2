@extends('layouts.appv2')
@section('title', 'Lista de Impuesto de transito')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Lista de impuestos de transito</h4>
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
                <table class="table table-bordered" style="width:100%" id="tablaImpuesto">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Cedula/Ruc</th>
                            <th scope="col">Contribuyente</th>
                            <th scope="col">Vehiculo</th>
                            <th scope="col">Numero de titulo</th>
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

                        <div class="col-md-5">
                            
                            <span><b>Proceso de Matriculacion Vehicular:</b></span> <span id="anio_proceso"></span><br>
                            <span><b>Contribuyente:</b></span> <span id="persona"></span><br>
                            <span><b>Cedula/RUC :</b></span> <span id="ci_persona"></span><br><br>
                            

                            <center><span><b>Datos de Vehiculo</b></span></center>
                            <span><b>Placa/CPN/RAMV:</b></span> <span id="placa_det"></span><br>
                            <span><b>Chasis:</b></span> <span id="chasis_det"></span><br>
                            <span><b>Avaluo:</b></span> <span id="avaluo_det"></span><br>
                            <span><b>Año de modelo:</b></span> <span id="modelo_det"></span><br>
                            <span><b>Clase Tipo:</b></span> <span id="clase_tipo_det"></span><br>
                            <span><b>Marca:</b></span> <span id="marca_det"></span><br>

                            <center><span><b>Otros Datos</b></span></center>
                            <span><b>Generado por:</b></span> <span id="generado"></span><br>
                            <span><b>Fecha:</b></span> <span id="fecha_generado"></span><br>
                        </div>

                        <div class="col-md-7">
                            <div class="table-responsive">
              
                                <table class="table table-bordered" style="width:100%" id="tablaImpuestoDetalle">
                                    <thead>
                                        <tr>
                                            <th scope="col">Rubros/Concepto</th>
                                            <th scope="col">Valor</th>
                                           
                                        </tr>
                                    </thead>
                                    <tbody id="tbodyDetalle">

                                    </tbody>
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
        tablaImpuesto = $("#tablaImpuesto").DataTable({
            "lengthMenu": [ 10,20],
            "language" : {
                "url": '{{ asset("/js/spanish.json") }}',
            },
            "autoWidth": true,
            "rowReorder": true,
            "order": [], //Initial no order
            "processing" : true,
            "serverSide": true,
            "ajax": {
                "url": '{{ url("/transito/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'cc_ruc'},
                {width: '',data: 'contribuyente', name: 'contribuyente'},
                {width: '',data: 'vehiculo'},
                {width: '',data: 'numero_titulo'},
                {width: '',data: 'created_at'},
                {width: '',data: 'total_pagar'},
               
            ],
            "columnDefs": [
                { targets: [6], className: "text-end" } // <-- alinea la columna 5 a la derecha
            ],
            "fixedColumns" : true
        });
    })

    function generarPdf(id){
       
        vistacargando("m","Espere por favor")
        $.get("../transito-imprimir/"+id+'/G', function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            verpdf(data.pdf)
            console.log(data)
            
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    function verpdf(ruta){
        
        var iframe=$('#iframePdf');
        iframe.attr("src", "../patente/documento/"+ruta);   
        $("#vinculo").attr("href", '../patente/descargar-documento/'+ruta);
        $("#documentopdf").modal("show");
    }
    globalThis.CargarPagina=0
    $('#documentopdf').on('hidden.bs.modal', function () {
       if(CargarPagina==1){
            $("#documentopdf").modal("hide");
            location.reload();
        }
    });

    function eliminarTitulo(id){
        $('#tituloEliminaModal').modal('show')
        $('#id_impuesto').val(id)
    }
    function procesaEliminacion(id){
        vistacargando("m","Espere por favor")
        $.get("../elimina-titulo-transito/"+id, function(data){
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            verpdf(data.pdf)
            console.log(data)
            
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    $("#form_baja").submit(function(e){
        e.preventDefault();
        
        //validamos los campos obligatorios
        let motivo_baja=$('#motivo_baja').val()
        let id_impuesto=$('#id_impuesto').val()
    
        if(motivo_baja=="" || motivo_baja==null){
            alertNotificar("Debe ingresar el motivo","error")
            $('#motivo_baja').focus()
            return
        } 

        if(confirm('¿Estas seguro que quieres dar de baja al titulo?'))
        {
            vistacargando("m","Espere por favor")
            $.ajaxSetup({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                }
            });

            //comprobamos si es registro o edicion
            let tipo="POST"
            let url_form="../baja-titulo-transito"
            var FrmData=$("#form_baja").serialize();

            $.ajax({
                    
                type: tipo,
                url: url_form,
                method: tipo,             
                data: FrmData,      
                
                processData:false, 

                success: function(data){
                    vistacargando("");                
                    if(data.error==true){
                        alertNotificar(data.mensaje,'error');
                        return;                      
                    }
                
                    alertNotificar(data.mensaje,"success");
                    cancelarBaja()
                    setTimeout(function() {
                        location.href = location.href;
                    }, 1000);
                                    
                }, error:function (data) {
                    console.log(data)

                    vistacargando("");
                    alertNotificar('Ocurrió un error','error');
                }
            });
        }
    })

    function cancelarBaja(){
        $('#motivo_baja').val('')
        $('#id_impuesto').val('')
        $('#tituloEliminaModal').modal('hide')
    }

    function cobrarTitulo(id){
        $("#tablaImpuestoDetalle tbody").html('');
        $('#tablaImpuestoDetalle tbody').empty();
        
        $('#id_impuesto').val(id)

        vistacargando("m","Espere por favor")
        $.get("../detalle-titulo/"+id, function(data){
            console.log(data)
            vistacargando("")
            if(data.error==true){
                alertNotificar(data.mensaje,"error");
                return;   
            }
            let avaluo=parseFloat(data.info[0].vehiculo.avaluo)
           
            $('#anio_proceso').html(data.info[0].TransitoImpuesto.year_impuesto)

            $('#persona').html(data.info[0].cliente.apellidos+" "+data.info[0].cliente.nombres)
            $('#ci_persona').html(data.info[0].cliente.ci_ruc)

            $('#placa_det').html(data.info[0].vehiculo.placa_cpn_ramv)
            $('#chasis_det').html(data.info[0].vehiculo.chasis)
            $('#avaluo_det').html('$'+avaluo.toFixed(2))
            $('#modelo_det').html(data.info[0].vehiculo.year)
            $('#clase_tipo_det').html(data.info[0].vehiculo.clase_vehiculo.descripcion)
            $('#marca_det').html(data.info[0].vehiculo.marca.descripcion)

            $('#generado').html(data.persona.nombre)
            $('#fecha_generado').html(data.info[0].TransitoImpuesto.created_at)

            
            
            $("#tablaImpuestoDetalle tbody").html('');
            
            $.each(data.data,function(i, item){
                var concepto=item.concepto;
                if(item.codigo=='RTV'){
                    // concepto=item.concepto + ' ( '+item.agrupado.join(', ')+' )';
                    concepto=item.concepto + ' ( '+data.info[0].vehiculo.tipo_vehiculo.descripcion+' )';
                }

                if(item.codigo=='REC' && data.info[0].TransitoImpuesto.calendarizacion!=null){
                    concepto=item.concepto + ' (Calendarizacion '+data.info[0].TransitoImpuesto.calendarizacion+')';
                }

                $('#tablaImpuestoDetalle').append(`<tr>
                                                <td style="width:75%; text-align:left; vertical-align:middle">
                                                    ${concepto}
                                                </td>
                                                <td style="width:25%; text-align:right; vertical-align:middle">
                                                    ${item.pivot.valor}
                                                </td>
                                        </tr>`);
            })

            // Agregar el tfoot con el total
            $('#tablaImpuestoDetalle tfoot').remove(); // Por si ya existe uno previo

            $('#tablaImpuestoDetalle').append(`
                <tfoot>
                    <tr>
                        <td style="text-align:right; font-weight:bold">TOTAL</td>
                        <td style="text-align:right; font-weight:bold">${data.info[0].TransitoImpuesto.total_pagar}</td>
                    </tr>
                </tfoot>
            `);
            $('#PagoModal').modal('show')
                        
            
        }).fail(function(){
            vistacargando("")
            alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
        });
    }

    function cerrarCobro(){
        $('#PagoModal').modal('hide')
    }

    function registrarCobro(){
        var id=$('#id_impuesto').val()
        //alert(id)
       
        if(confirm('¿Estas seguro que quieres realizar el cobro?'))
        {
            
            vistacargando("m","Espere por favor")
            $.get("../registrar-cobro-transito/"+id, function(data){
                vistacargando("")
                if(data.error==true){
                    alertNotificar(data.mensaje,"error");
                    return;   
                }
                cerrarCobro()
                alertNotificar(data.mensaje,"success");
                CargarPagina=1
                verpdf(data.pdf)

               
                // location.reload();
            }).fail(function(){
                vistacargando("")
                alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
            });
        }
    }

    function anularCobro(){
        var id=$('#id_impuesto').val()
       
        if(confirm('¿Estas seguro que quieres anular el registro?'))
        {
           
            vistacargando("m","Espere por favor")
            $.get("../anular-cobro-transito/"+id, function(data){
                vistacargando("")
                if(data.error==true){
                    alertNotificar(data.mensaje,"error");
                    return;   
                }
                alertNotificar(data.mensaje,"success");
                
                setTimeout(() => {
                    cerrarCobro()
                    location.reload();
                }, 2000);
                
               
            }).fail(function(){
                vistacargando("")
                alertNotificar("Se produjo un error, por favor intentelo más tarde","error");  
            });
        }
    }

</script>
@endpush
