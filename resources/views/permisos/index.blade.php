@extends('layouts.appv2')
@section('title', 'Mostrar lista de patente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Permisos</h4>
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
            <button type="button" class="btn btn-sm btn-success " onclick="mantenimientoPermiso()">s</button>
            <div class="table-responsive">
                @csrf
                <table class="table table-bordered" style="width:100%" id="tabla_usuario">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Rol</th>
                            <th scope="col">Permisos</th>
                            <th scope="col"></th>

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

    <div class="modal fade_ detalle_class"  id="modal_Permisos" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span></button>
                    <h4 class="modal-title">AGREGAR PERMISOS</h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        
                        <div class="col-md-12">
                            
                            <form method="POST" action="" id="form_registro_permisos">
                                @csrf

                                <div class="col-md-12">
                                    <div class="row align-items-center">
                                        <div class="col-md-2 text-end">
                                            <label for="marca_v" class="form-label mb-0">Rol</label>
                                        </div>
                                        <div class="col-md-9">
                                            
                                            <input type="hidden" name="id_rol_selecc" id="id_rol_selecc">
                                            <input type="text" readonly class="form-control act_user" id="rol_selecc" name="rol_selecc"  >
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-2 text-end">
                                            <label for="marca_v" class="form-label mb-0">Permisos</label>
                                        </div>
                                        <div class="col-md-9">
                                            <select data-placeholder="Seleccione Un Permiso" style="width: 100%;" class="form-control select2" name="permisos" id="permisos" >
                                            @if(isset($permisos))
                                                <option value="">Selecciona el permiso</option>
                                                @foreach ($permisos as $dato)
                                                   
                                                    <option value="{{ $dato->id}}" >{{ $dato->name }} </option>
                                                @endforeach
                                            @endif
                                        </select>
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
                                                onclick="cancelarPermiso()">Cancelar</button>
                                        </div>
                                    </div>
                                </div>

                            </form>
                        </div>

                        <div class="table-responsive col-md-12">
                            <table id="tabla_permisos" width="100%"class="table table-bordered table-striped">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Descripcion</th>
                                                                           
                                        <th style="min-width: 30%">Opciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td colspan="3"><center>No hay Datos Disponibles</td>
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
  <script src="{{ asset('js/permisos/mantenimiento.js?v='.rand())}}"></script>

 <script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
 <script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>

<script>
   
    llenar_tabla_rol()
   
</script>

@endpush
