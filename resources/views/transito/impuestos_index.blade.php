@extends('layouts.appv2')
@section('title', 'Mostrar lista de patente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Lista de impuestos de transito</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <a href="{{ route('create.transito') }}" class="btn btn-sm btn-secondary d-flex align-items-center gap-1">
                Nuevo impuesto
            </a>
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
                            <th scope="col">Total a pagar</th>
                            <th scope="col">Fecha</th>
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
                {width: '',data: 'total_pagar'},
                {width: '',data: 'created_at'},
            ],
            "fixedColumns" : true
        });
    })

    function generarPdf(id){
        vistacargando("m","Espere por favor")
        $.get("../transito-imprimir/"+id, function(data){
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
</script>
@endpush
