@extends('layouts.app')
@section('title', 'Procesar pago')
@push('styles')
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Procesar pago </h2>
                </div>
            </div>
        </div>

        @if ($contadorLiquidacion == 1)
        <div class="row justify-content-md-center">
            <div class="col-8">
                <div class="alert alert-info" role="alert">
                    El pago de la consulta ya fue realizado
                </div>
            </div>
        </div>
        @endif
        <br>
        <div class="row justify-content-md-center">
            <div class="col-8">
                @if(session('guardado'))
                    <div class="alert alert-success" role="alert">
                        {{session('guardado')}}
                    </div>
                @endif
            </div>

            @if(session('guardado') || $contadorLiquidacion == 1)
                <div class="col-8">
                    <div class="card">
                        <div class="card-body">

                            <div class="row">
                                <div class="col-12">

                                    <table>
                                        <tr>
                                            <td><img src="{{asset('img/SALADEFISIOTERAPIALOGO.jpg')}}" alt="" sizes="" srcset="" height="140px"></td>

                                            <td>
                                                <div class="card">
                                                    <div class="card-body">

                                                        <table>
                                                            <tr>
                                                                <td><strong>Comprobante</strong></td>
                                                            </tr>
                                                            <tr>
                                                                <td>N° {{$Liquidation->voucher_number}}</td>
                                                            </tr>
                                                        </table>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="3" style="align-content: center"><strong>Comprobante de pago</strong></td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">

                                    <table class="table table-bordered mt-3">
                                        <tr>
                                            <th colspan="3">Datos de usuario</th>
                                        </tr>
                                        <tr>
                                            <td>Cedula</td>
                                            <td>Usuario/a</td>
                                            <td>Fecha de expedicion</td>
                                        </tr>
                                        <tr>
                                            <td>{{$Cita->persona->cedula}}</td>
                                            <td>{{$Cita->persona->nombres.' '.$Cita->persona->apellido}}</td>
                                            <td>{{$Cita->fecha}}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-8">
                                    <table class="table table-bordered">
                                        <tr>

                                            <td>Concepto de pago</td>
                                            <td>Valor</td>
                                        </tr>
                                        @foreach ($Liquidation->liquidation_rubros as $lr)
                                            <tr>
                                                <td>{{$lr->name}}</td>
                                                <td>{{$lr->pivot->value}}</td>
                                            </tr>
                                        @endforeach
                                        <tr>
                                            <td>Total</td>
                                            <td>{{$Liquidation->total_payment}}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-8">
                                    <table class="mt-5" width="100%">
                                        <tr>
                                            <td style="width: 50%">________________________</td>
                                            <td style="width: 50%">________________________</td>
                                        </tr>
                                        <tr>
                                            <td>Alanista de rentas</td>
                                            <td>Unidad de Fisioterapia</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
                <div class="col-8 mt-3">
                    <a target="_blank" href="{{route('recibo.pago',$Cita->id)}}" class="btn btn-secondary mb-3" disabled><i class="bi bi-receipt"></i> Generar recibo</a>
                </div>
            @else
            <div class="col-8">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Procesar pago</h5>
                        <h6 class="card-subtitle mb-2 text-muted">Card subtitle</h6>
                        <form action="{{route('store.pago')}}" method="post" id="formPago">
                            @csrf
                            <input type="hidden" name="cita_id" value="{{$Cita->id}}">
                            <div class="row">
                                <div class="col-6">
                                    <div class="mb-3">
                                        <label for="diagnostico">* Categoria : </label>
                                        <select class="form-select" aria-label="Default select example" id="categoria_id" name="categoria_id">
                                            <option selected>Seleccione categoria</option>
                                            @isset($Categoria)
                                                @foreach ($Categoria as $c)
                                                    <option value="{{$c->id}}">{{$c->nombre}}</option>
                                                @endforeach
                                            @endisset
                                          </select>
                                    </div>
                                    <div class="mb-3">
                                        <label for="diagnostico">* Valor recibido : </label>
                                        <input type="text" class="form-control" id="inputValorRecibido" name="inputValorRecibido" value="" onkeyup="calcularCambio()">
                                    </div>
                                    <div class="mb-3">
                                        <label for="diagnostico">* Valor a cobrar : </label>
                                        <input type="text" class="form-control" id="inputValorCobrar" name="inputValorCobrar" value="0.00">
                                    </div>
                                    <div class="mb-3">
                                        <label for="diagnostico">* Cambio : </label>
                                        <input type="text" class="form-control" id="inputCambio" name="inputCambio" value="0.00">
                                    </div>
                                    <div class="mb-3">
                                        <button type="button" id="btnProcesarPago" class="btn btn-primary mb-3"><i class="bi bi-wallet"></i> Procesar</button>

                                    </div>

                                </div>
                                <div class="col-6">
                                    <p class="fs-1">TOTAL A PAGAR</p>
                                    <h1><span class="badge bg-primary" id="spanValor">$ 0.00</span></h1>
                                </div>
                            </div>
                        </form>

                    </div>
                </div>
            </div>
            @endif
        </div>
    </div>
@endsection
@push('scripts')
<script>
    var inputValorRecibido = document.getElementById('inputValorRecibido');
    var inputValorCobrar = document.getElementById('inputValorCobrar');
    var inputCambio = document.getElementById('inputCambio');
    var btnProcesarPago = document.getElementById('btnProcesarPago');

    var resultado = 0;
    let token = "{{csrf_token()}}";

    btnProcesarPago.addEventListener('click', function() {
        var formPago = document.getElementById('formPago').submit();
    });
    /*inputValorCobrar.addEventListener('keyup', function() {
        alert('hola');
        if (event.keyCode === 13) {
            resultado = inputValorRecibido.value - inputValorCobrar.value;
            inputCambio.value = resultado;
        }
    });*/

    function calcularCambio(){
        if (event.keyCode === 13) {
            resultado = inputValorRecibido.value - inputValorCobrar.value;
            inputCambio.value = resultado;
        }
    }

    var categoria_id = document.getElementById('categoria_id');
    categoria_id.addEventListener('change', function() {
        var selectedOption = this.options[categoria_id.selectedIndex];
        var spanValor = document.getElementById('spanValor');
        axios.post('{{route('obtener.categoria')}}',{
            id: categoria_id.value,
            _token: token
        }).then(function(res) {
            if(res.status==200) {
                if(res.data.respuesta == true){
                    spanValor.innerHTML = '$ '+res.data.valor;
                    inputValorCobrar.value = res.data.valor;
                }
            }
        }).catch(function(err) {
            if(err.response.status == 500){
                toastr.error('Error al comunicarse con el servidor, contacte al administrador de Sistemas');
                console.log('error al consultar al servidor');
            }

            if(err.response.status == 419){
                toastr.error('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }
        }).then(function() {

        });
    });
</script>
@endpush
