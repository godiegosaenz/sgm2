<!DOCTYPE html>
<html>
<head>
    <title>Generacion de recibo</title>
    <link href="{{ asset('css/bootstrap.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/bootstrap-icons.css') }}" rel="stylesheet">
    <link href="{{ asset('font/font.css') }}" rel="stylesheet">
    <style>

    </style>
</head>
<body>
    <div class="row">
        <div class="col-8">
            <div class="card">
                <div class="card-body">

                    <div class="row">
                        <div class="col-12">

                            <table>
                                <tr>
                                    <td><img src="{{asset('img/SALADEFISIOTERAPIALOGO.jpg')}}" alt="" sizes="" srcset="" height="95px"></td>

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
                                    <td colspan="3" style="text-align: center"><strong><h3>Comprobante de pago</h3></strong> </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-12">

                            <table style="border: 1px solid;" width="100%">
                                <tr style="border: 1px solid; border-color:darkgrey">
                                    <th style="border: 1px solid; border-color:darkgrey" colspan="3">Datos de usuario</th>
                                </tr>
                                <tr style="border: 1px solid; border-color:darkgrey">
                                    <td style="border: 1px solid; border-color:darkgrey">Cedula</td>
                                    <td style="border: 1px solid; border-color:darkgrey">Usuario/a</td>
                                    <td style="border: 1px solid; border-color:darkgrey">Fecha de expedicion</td>
                                </tr>
                                <tr style="border: 1px solid; border-color:darkgrey">
                                    <td style="border: 1px solid; border-color:darkgrey">{{$Cita->persona->cedula}}</td>
                                    <td style="border: 1px solid; border-color:darkgrey">{{$Cita->persona->nombres.' '.$Cita->persona->apellidos}}</td>
                                    <td style="border: 1px solid; border-color:darkgrey">{{$Cita->fecha}}</td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-8">
                            <table class="" width="60%" style="margin-top: 20px">
                                <tr style="border: 1px solid; border-color:darkgrey">
                                    <td style="border: 1px solid; border-color:darkgrey"><strong>Concepto de pago</strong></td>
                                    <td style="border: 1px solid; border-color:darkgrey"><strong>Valor</strong></td>
                                </tr>
                                @foreach ($Liquidation->liquidation_rubros as $lr)
                                    <tr style="border: 1px solid; border-color:darkgrey">
                                        <td style="border: 1px solid; border-color:darkgrey">{{$lr->name}}</td>
                                        <td style="border: 1px solid; border-color:darkgrey">{{$lr->pivot->value}}</td>
                                    </tr>
                                @endforeach
                                <tr style="border: 1px solid; border-color:darkgrey">
                                    <td style="border: 1px solid; border-color:darkgrey">Total</td>
                                    <td style="border: 1px solid; border-color:darkgrey">{{$Liquidation->total_payment}}</td>
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
    </div>
</body>
</html>
