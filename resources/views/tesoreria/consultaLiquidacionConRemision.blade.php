@extends('layouts.appv2')
@section('title', 'Liquidaciones')
@push('styles')
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Liquidaciones</h4>
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
            <div style="display:none" id="aletMensajes" class="alert alert-info">
            </div>
    </div>
</div>
<form id="formConsulta" action="{{route('store.liquidacion.remision')}}" method="post">
    @csrf
    <div class="row justify-content-md-center">
        <div class="col-3">
            <div class="mb-3">
                <label for="inputMatricula">* Matricula inmobiliaria : </label>
                <input type="number" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="inputMatricula" name="inputMatricula" value="{{old('inputMatricula')}}" autofocus required>
                <div class="invalid-feedback">
                    @if($errors->has('inputMatricula'))
                        {{$errors->first('inputMatricula')}}
                    @endif
                </div>
            </div>
        </div>
        <div class="col-3">
            <div class="mb-3">
                <br>
                <button id="btnConsulta" class="btn btn-primary" type="submit">
                    <span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span>
                    Consultar
                </button>
</form>
                <a id="reporteLiquidacion" class="btn btn-secondary" target="_blank" rel="noopener noreferrer">
                    <i class="bi bi-filetype-pdf"></i>
                    Imprimir
                </a>
            </div>
        </div>

    </div>



<div class="row mt-3">

    <div class="col-12">
        <h3>Lista de liquidaciones</h3>
    </div>
    <div class="col-10">
        @csrf
        <div class="table-responsive">
            <table class="table table-bordered table-hover" id="tableCita" style="width: 100%">
                <thead>
                    <tr>
                    <th scope="col">No. Predio</th>
                    <th scope="col">Año</th>
                    <th scope="col">Cod. Predial</th>
                    <th>Contribuyente</th>
                    <th scope="col">Emision</th>
                    <th scope="col">interes</th>
                    <th scope="col">Recargo</th>
                    <th scope="col">Descuento</th>
                    <th scope="col">Total</th>
                    </tr>
                </thead>
                <tbody id="tbodyurban">
                    @isset($DatosLiquidacion)
                        @foreach ($DatosLiquidacion as $item)
                        <tr>
                            <td>{{$num_predio2}}</td>
                            <td>{{$item['anio']}}</td>
                            <td>{{$clave_cat2}}</td>
                            @if($item['nombres'] != null)
                            <td>{{$item['nombres'].' '.$item['apellidos']}}</td>
                            @else
                            <td>{{$item['nombre_comprador']}}</td>
                            @endif
                            <td>{{$item['total_pago']}}</td>
                            <td>{{$item['interes']}}</td>
                            <td>{{$item['recargo']}}</td>
                            <td>0.00</td>
                            <td>{{$item['suma_emision_interes_recargos']}}</td>
                            </tr>
                        @endforeach
                        <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td><strong>{{$suma_emision}}</strong></td>
                            <td><strong>{{$suma_interes}}</strong></td>
                            <td><strong>{{$suma_recargo}}</strong></td>
                            <td><strong>0.00</strong></td>
                            <td><strong>{{$suma_total}}</strong></td>
                        </tr>

                    @endisset
                </tbody>
            </table>

        </div>
    </div>
</div>


@endsection
@push('scripts')
<script>
    let inputMatricula = document.getElementById('inputMatricula');
    const reporteLiquidacion = document.getElementById('reporteLiquidacion');

    reporteLiquidacion.addEventListener('click', (e) => {
        if(inputMatricula.value != ''){
            let url = '{{ url("liquidacion/imprimir/")}}';
            reporteLiquidacion.setAttribute("href", url+'/'+inputMatricula.value);
        }else{
            return false;
        }
    });
</script>
@endpush
