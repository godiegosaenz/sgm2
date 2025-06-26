@extends('layouts.appv2')
@section('title', 'Titulo Rural')
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
</style>

<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Consulta Repetidos</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    <div class="btn-group me-2">
        <button type="button" class="btn btn-sm btn-outline-secondary">Share</button>
        <button type="button" class="btn btn-sm btn-outline-secondary">Export</button>
    </div>
    <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
        <svg class="bi"><use xlink:href="#calendar3"/></svg>
        This week
    </button>
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
    <form id="formConsulta" action="{{'tituloscoactivarural'}}" method="post">
        @csrf
        <div class="row justify-content-md-center">
            
            <div class="col-6" id="div_cedula1">
                <div class="mb-3">
                    <label for="num_predio">* Estado: </label>
                   
                    <select class="form-select" aria-label="Default select example" id="tipo" name="tipo" onchange="llenarTabla()">
                         <option value="2" {{ old('tipo') == '2' ? 'selected' : '' }}>Por Pagar</option>
                        <option value="1" {{ old('tipo') == '1' ? 'selected' : '' }}>Cancelado</option>
                       
                      
                    </select>

                   
                </div>
            </div>
          
        </div>

    </form>

    <form class="" action="tituloscoactivarural/imprimir" id="formExonerar" name="formExonerar" method="post" enctype="multipart/form-data">
        <div class="row">
            <div class="col-12">
                <div class="mb-3">
                    <!-- <a id="reporteLiquidacion1" class="btn btn-secondary" rel="noopener noreferrer">
                        <i class="bi bi-filetype-pdf"></i>
                        General titulos
                    </a> -->
                    <button type="button" class="btn btn-success" rel="noopener noreferrer" onclick="quitarDuplicados()"> 
                        <i class="fa fa-refresh"></i> Quitar Duplicados
                    </button>

                </div>

                
                  
            </div>
        </div>
        <div class="row mt-3">
           
            <div class="col-12">
                <h3>Lista de Deudas</h3>
            </div>
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tablaRepetidos" style="width: 100%">
                        <thead>
                            <tr>
                                <th scope="col">Cedula</th>
                                <th scope="col">Nombres</th>
                                <th scope="col">Fecha Emision</th>
                                <th scope="col">Num Titulo</th>
                                <th scope="col">Estado</th>
                               
                            </tr>
                        </thead>
                        <tbody id="tbodyurban">

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>

@endsection
@push('scripts')
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
<script src="{{ asset('js/jquery-3.5.1.js') }}"></script><!-- DataTables -->

<script src="{{ asset('js/jquery.dataTables.min.js') }}"></script>
<script src="{{ asset('js/dataTables.bootstrap5.min.js') }}"></script>
<script src="{{ asset('js/dataTables.rowReorder.min.js') }}"></script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/emisiones/repetidos.js?v='.rand())}}"></script>
<script>
    // alert("ee")
    llenarTabla()
</script>

@endpush
