@extends('layouts.appv2')
@section('title', 'Cambiar Contraseña')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h4 class="h2">Cambiar Contraseña</h4>
        <div class="btn-toolbar mb-2 mb-md-0">
        
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
            <form method="POST" action="" id="form_contrasenia" enctype="multipart/form-data">
                @csrf

                
                <div class="col-md-12" style="margin-top: 20px; margin-bottom: 20px;">
                    <div class="row align-items-center">
                        <div class="col-md-2 text-end">
                            <label for="marca_v" class="form-label mb-0">Contraseña Actual</label>
                        </div>
                        <div class="col-md-9">
                            <input type="password" class="form-control" name="password_act" id="password_act" >  
                        </div>
                    </div>
                </div>

                <div class="col-md-12" style="margin-top: 20px; margin-bottom: 20px;">
                    <div class="row align-items-center">
                        <div class="col-md-2 text-end">
                            <label for="marca_v" class="form-label mb-0">Nueva Contraseña</label>
                        </div>
                        <div class="col-md-9">
                            <input type="password" class="form-control" name="password_new" id="password_new" >  
                        </div>
                    </div>
                </div>

                <div class="col-md-12" style="margin-top: 20px; margin-bottom: 20px;">
                    <div class="row align-items-center">
                        <div class="col-md-2 text-end">
                            <label for="marca_v" class="form-label mb-0">Repetir Contraseña</label>
                        </div>
                        <div class="col-md-9">
                            <input type="password" class="form-control" name="password_rep" id="password_rep" >  
                        </div>
                    </div>
                </div>


                <div class="col-md-12" style="margin-top: 10px; margin-bottom: 20px;">
                    <div class="row align-items-center">
                        <div class="col-md-2 text-end">
                            <label for="marca_v" class="form-label mb-0"></label>
                        </div>
                        <div class="col-md-9">
                            <button type="button" class="btn btn-success btn-sm" onclick="cambiaContrasenia()"><span
                                    id="btn_tipo">Guardar</span></button>
                            <button type="button" class="btn btn-warning btn-sm"
                                onclick="limpiarCampos()">Cancelar</button>
                        </div>
                    </div>
                </div>

            </form>
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


<script src="{{ asset('js/contrasenia/mantenimiento.js?v='.rand())}}"></script>

@endpush
