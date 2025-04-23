@extends('layouts.appv2')
@section('title', 'Tercera edad')
@push('styles')
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Impresion titulos de credito (Coactiva)</h4>
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
    <form id="formConsulta" action="{{route('consulta.titulocredito')}}" method="post">
        @csrf
        <div class="row justify-content-md-center">
            <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">* Matricula inmobiliaria : </label>
                    @if($num_predio == 0)
                    <input type="number" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="num_predio" name="num_predio" value="{{old('num_predio')}}" autofocus required>
                    @else
                    <input type="number" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="num_predio" name="num_predio" value="{{$num_predio}}" autofocus required>
                    @endif
                    <div class="invalid-feedback">
                        @if($errors->has('num_predio'))
                            {{$errors->first('num_predio')}}
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
                </div>
            </div>

        </div>

    </form>

    <form class="" action="{{route('reportecoactiva.titulos')}}" id="formExonerar" name="formExonerar" method="post" enctype="multipart/form-data">
        <div class="row">
            <div class="col-12">
                <div class="mb-3">
                    <a id="reporteLiquidacion" class="btn btn-secondary" rel="noopener noreferrer">
                        <i class="bi bi-filetype-pdf"></i>
                        General titulos
                    </a>

                    <button type="button" class="btn btn-success" rel="noopener noreferrer">
                        <i class="fa fa-edit"></i>
                        Actualizar Contribuyente
                    </button>
               
                </div>

                
                  
            </div>
        </div>
        <div class="row mt-3">
            @if ($num_predio > 0)
            <input type="hidden" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="inputMatricula" name="inputMatricula" value="{{$num_predio}}" autofocus required>
            @endif
            <div class="col-12">
                <h3>Lista de liquidaciones</h3>
            </div>
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tableCita" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col">*</th>
                            <th scope="col">Seleccionar</th>
                            <th scope="col">Año</th>
                            <th>Liquidación</th>
                            <th scope="col">Cod. Catastral</th>
                            <th scope="col">Propietario</th>
                            <th scope="col">Total de pago</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyurban">

                            @isset($liquidacionUrbana)
                                @foreach ($liquidacionUrbana as $it )
                                    <tr>
                                        @if ($it->estado_liquidacion == 1)
                                        <td><i class="bi bi-circle-fill" style="color:green;"></i></td>
                                        @else
                                        <td><i class="bi bi-circle-fill" style="color:red;"></i></td>
                                        @endif
                                        <td><input class="form-check-input" type="checkbox" value="{{$it->id}}" name="checkLiquidacion[]"></td>
                                        <td>{{$it->anio}}</td>
                                        <td>{{$it->id_liquidacion}}</td>
                                        <td>{{$it->clave_cat}}</td>
                                        @if($it->nombres != '')
                                        <td>{{$it->nombres.' '.$it->apellidos}}</td>
                                        @else
                                        <td>{{$it->nombre_comprador}}</td>
                                        @endif
                                        <td>{{$it->total_pago}}</td>
                                    </tr>
                                @endforeach
                            @endisset
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>
@endsection
@push('scripts')
<script>

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

    const reporteLiquidacion = document.getElementById('reporteLiquidacion');

    reporteLiquidacion.addEventListener('click', (e) => {
        if(verificarSeleccionCasillas() == true){
            let formData = new FormData(formExonerar);
            formExonerar.submit();
            formData.append('num_predio',inputMatricula);
            /*axios.post('tituloscoactiva/imprimir',formData).then(function(res) {
                alert('dentro del formulario');

            }).catch(function(err) {
                console.log(err);
                if(err.response.status == 500){

                    console.log('error al consultar al servidor');
                }

                if(err.response.status == 419){

                }
                if(err.response.status == 422){

                }
            }).then(function() {
                    //loading.style.display = 'none';
            });*/

        }else{
            alert('Seleccione al menos un registro');
            return false;
        }
    });

</script>
@endpush
