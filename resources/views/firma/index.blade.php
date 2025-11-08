@extends('layouts.appv2')
@section('title', 'Firma Electronica')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Mi Firma Electronica</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        <div class="btn-group me-2">
            <a onclick="nuevaFirma()" class="btn btn-sm btn-success d-flex align-items-center gap-1">
                Nuevo Archivo
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
                <table class="table table-bordered" style="width:100%" id="tablaJefe">
                    <thead>
                        <tr>
                            <th scope="col">Accion</th>
                            <th scope="col">Fecha Registro</th>
                            <th scope="col">Fecha Emision</th>
                            <th scope="col">Fecha Vigencia</th>
                            <th scope="col">Estado</th>
                            
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

    <div class="modal fade" id="NuevaFirmaModal" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Firma Electronica</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
               
                <div class="modal-body">
                <div class="row">
                        
                    <form method="POST" action="" id="form_firma" enctype="multipart/form-data">
                        @csrf

                        <div class="col-md-12" style="margin-top: 20px;">
                            <div class="row align-items-center">
                                <div class="col-md-2 text-end">
                                    <label for="marca_v" class="form-label mb-0">Archivo</label>
                                </div>
                                <div class="col-md-9">
                                    <input type="file" class="form-control" name="p12" id="p12" accept=".p12">
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 20px; margin-bottom: 20px;">
                            <div class="row align-items-center">
                                <div class="col-md-2 text-end">
                                    <label for="marca_v" class="form-label mb-0">Contraseña</label>
                                </div>
                                <div class="col-md-9">
                                    <input type="password" class="form-control" name="password" id="password" >  
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
                                            id="btn_tipo">Guardar</span></button>
                                    <button type="button" class="btn btn-warning btn-sm"
                                        onclick="cancelarJefeArea()">Cancelar</button>
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
        tablaJefe = $("#tablaJefe").DataTable({
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
                "url": '{{ url("/mi-firma-electronica/datatables") }}',
                "type": "post",
                "data": function (d){
                    d._token = $("input[name=_token]").val();
                }
            },
            //"columnDefs": [{ targets: [3], "orderable": false}],
            "columns": [
                {width: '',data: 'action', name: 'action', orderable: false, searchable: false},
                {width: '',data: 'created_at'},
                {width: '',data: 'f_emision'},
                {width: '',data: 'f_expiracion', name: 'f_expiracion'},
                {width: '',data: 'estado'},
                
            ],
            "fixedColumns" : true
        });
    })

    // document.getElementById('p12').addEventListener('change', function () {
    //     const file = this.files[0];
    //     if (file && !file.name.toLowerCase().endsWith('.p12')) {
    //         alert('Solo se permiten archivos con extensión .p12');
    //         this.value = ''; // Limpia el input
    //     }
    // });
</script>

<script src="{{ asset('js/firmaElecronica/firma.js?v='.rand())}}"></script>

@endpush
