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
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">REPORTE PARTE DIARIO</h4>
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
           
            <div class="col-3" id="div_cedula1">
                <div class="mb-3">
                    <label for="num_predio">* Fecha : </label>
                   
                    <input type="date" class="form-control {{$errors->has('inputfecha') ? 'is-invalid' : ''}}" id="fecha" name="fecha" value="{{old('fecha')}}" >
                   
                </div>
            </div>
            
            <div class="col-3">
                <div class="mb-3">
                    <br>
                    <button id="btnConsulta" class="btn btn-primary" type="button" onclick="generarParte()" >
                        <span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span>
                        Consultar
                    </button>
                </div>
            </div>

        </div>

    </form>

   
@endsection
@push('scripts')
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
<script>

   
</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/partediario/generar.js?v='.rand())}}"></script>

@endpush
