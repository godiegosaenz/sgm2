@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
<link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
<style>
    tfoot input {
        width: 100%;
        padding: 3px;
        box-sizing: border-box;
    }
</style>
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h3 class="h2">Impuestos de rodaje</h3>
        <div class="btn-toolbar mb-2 mb-md-0">
            <form class="" action="{{route('reportetituloimpuesto.transito',['id' => $TransitoImpuesto->id])}}" id="formExonerar" name="formExonerar" method="post" enctype="multipart/form-data">
                @csrf
                <a id="reporteLiquidacion" class="btn btn-secondary" rel="noopener noreferrer">
                    <i class="bi bi-filetype-pdf"></i>
                    General titulos
                </a>
            </form>
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
            "Por favor, revisa los campos obligatorios y corrige los errores indicados para poder continuar."
        </div>
    @endif
    <div class="container">

        <div class="card mb-4">
            <div class="card-header">Datos del Contribuyente</div>
            <div class="card-body">

                    <p><strong>Cédula/RUC:</strong> {{$cliente->cc_ruc}}</p>
                    <p><strong>Nombres y apellidos:</strong> {{$cliente->nombres.' '.$cliente->apellidos}}</p>
                    <p><strong>Correo:</strong> {{$cliente->correo}}</p>
                    <p><strong>Telefono:</strong> {{$cliente->telefono}}</p>
                    <p><strong>Direccion:</strong> {{$cliente->direccion}}</p>
                    <p><strong>Fecha de nacimiento:</strong> {{$cliente->fecha_nacimiento}}</p>

            </div>
        </div>
        <div class="card mb-4">
            <div class="card-header">Datos del vehiculo</div>
            <div class="card-body">

                    <p><strong>Placa:</strong> {{$vehiculo->placa}}</p>
                    <p><strong>Chasis:</strong> {{$vehiculo->chasis}}</p>
                    <p><strong>Marca:</strong> {{$vehiculo->placa}}</p>
                    <p><strong>Avalúo:</strong> {{$vehiculo->placa}}</p>
                    <p><strong>Año de modelo:</strong> {{$vehiculo->placa}}</p>


            </div>
        </div>

        <div class="card mb-4">
            <div class="card-header">Conceptos</div>
        </div>

        <div class="card mb-4">
            <div id="lista-conceptos">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Seleccionar</th>
                            <th>Descripción</th>
                            <th>Valor (USD)</th>
                        </tr>
                    </thead>
                    <tbody id="tabla-conceptos">
                        @foreach($transitoimpuestoconcepto as $concepto)
                            <tr>
                                <td>

                                </td>
                                <td>{{ $concepto->concepto }}</td>
                                <td>
                                    {{ $concepto->pivot->valor }}
                                </td>
                            </tr>
                        @endforeach
                    </tbody>
                    <tfoot>
                        <tr>
                            <th></th>
                            <th>TOTAL</th>
                            <th>{{$TransitoImpuesto->total_pagar}}</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>

    </div>
@endsection
@push('scripts')
<script>
    let token = "{{csrf_token()}}";

    var formExonerar = document.getElementById('formExonerar');

    const reporteLiquidacion = document.getElementById('reporteLiquidacion');
    reporteLiquidacion.addEventListener('click', (e) => {

            let formData = new FormData(formExonerar);
            formData.append('id',{{$TransitoImpuesto->id}});
            formExonerar.submit();
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


    });
</script>
@endpush
