<!DOCTYPE html>
<html>
<head>
    <title>INFORME</title>

</head>
<body>
    <div class="">
        <div class="">
            <img src="{{ asset('img/cabecera_tesoreria.png') }}" alt="" width="100%" >
        </div>
    </div>
    <br>
    <div class="" style="font-family: Arial, Helvetica, sans-serif;">
        <div class="">
            @if($ExoneracionAnterior->tipo == 'tercera_edad')
                <h3>INFORME DE EXONERACIÓN DE TERCERA EDAD</h3>
            @else
                <h3>INFORME DE EXONERACIÓN DE DISCAPACIDAD</h3>
            @endif
        </div>
    </div>
    <br>
    <div style="width:100%;margin-top:20px;margin-bottom:30px;">
        <table id="exoneraciontable" width="100%" style="font-family: Arial, Helvetica, sans-serif;">
            <tbody>
                <tr style="background-color: #BCDCF9">
                    <td colspan="4" style="text-align: center"><strong>Informacion de exoneracion</strong></td>
                </tr>
                <tr style="border: 1px solid; border-color:darkgrey">
                    <td style="border: 1px solid; border-color:darkgrey"><i class="fa fa-user"></i><strong>Matricula Inmobiliaria :</strong></td>
                    <td style="border: 1px solid; border-color:darkgrey">{{$ExoneracionAnterior->num_predio}}</td>
                    <td style="border: 1px solid; border-color:darkgrey"><i class="fa fa-user"></i><strong>Usuario :</strong></td>
                    <td style="border: 1px solid; border-color:darkgrey"><i class="fa fa-user"></i> {{$ExoneracionAnterior->usuario}}</td>
                </tr>
                <tr style="border: 1px solid; border-color:darkgrey">
                    <td style="border: 1px solid; border-color:darkgrey"><i class="fa fa-user"></i><strong>Resolución :</strong> </td>
                    <td style="border: 1px solid; border-color:darkgrey"><a href="{{route('descargar.exoneracion',$ExoneracionAnterior->id)}}" target="_blank" rel="noopener noreferrer">{{$ExoneracionAnterior->num_resolucion}}</a></td>
                    <td style="border: 1px solid; border-color:darkgrey"><i class="fa fa-user"></i><strong>Fecha de creacion :</strong> </td>
                    <td style="border: 1px solid; border-color:darkgrey">{{$ExoneracionAnterior->created_at}}</td>
                </tr>
                <tr style="border: 1px solid; border-color:darkgrey">
                    <td style="border: 1px solid; border-color:darkgrey"><i class="fa fa-user"></i><strong>Observacion :</strong> </td>
                    <td style="border: 1px solid; border-color:darkgrey" colspan="3">{{$ExoneracionAnterior->observacion}}</td>
                </tr>


            </tbody>

        </table>
    </div>
    <div style="width:100%">
            @foreach ($ExoneracionAnterior->exoneracion_detalle as $ed)
            <div style="width:100%; display:inline-block;">
                <table width="100%" style="font-family: Arial, Helvetica, sans-serif;font-size:10px;">
                    <tr style="background-color: #BCDCF9">
                        <th colspan="6" style="text-align: center">Detalle liquidacion anterior</th>
                    </tr>
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <th style="border: 1px solid; border-color:darkgrey">Id</th>
                        <th style="border: 1px solid; border-color:darkgrey">Liquidaciones</th>

                        <th style="border: 1px solid; border-color:darkgrey">fecha</th>
                        <th style="border: 1px solid; border-color:darkgrey">Total</th>
                    </tr>
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->liquidacion_id}}</td>
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->cod_liquidacion}}</td>

                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->created_at}}</td>
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->valor_anterior}}</td>
                    </tr>
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <th style="border: 1px solid; border-color:darkgrey" colspan="3">Rubros</th>
                        <th style="border: 1px solid; border-color:darkgrey">Valor</th>
                    </tr>
                    @foreach ($ed->detalle_liquidacion as $detalle)
                        <tr style="border: 1px solid; border-color:darkgrey">
                            <td style="border: 1px solid; border-color:darkgrey" colspan="3">{{$detalle->descripcion}}</td>
                            @if($detalle->rubro == 2)
                            <td style="border: 1px solid; border-color:darkgrey" colspan="3">{{$ed->impuesto_predial_anterior}}</td>
                            @else
                            <td style="border: 1px solid; border-color:darkgrey">{{$detalle->valor}}</td>
                            @endif
                        </tr>
                    @endforeach
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <th style="border: 1px solid; border-color:darkgrey" colspan="3">Total</th>
                        <th style="border: 1px solid; border-color:darkgrey">{{$ed->valor_anterior}}</th>
                    </tr>
                </table>
            </div>
            @endforeach

            @foreach ($ExoneracionAnterior->exoneracion_detalle as $ed)
            <div style="width:100%; display:inline-block;">
                <table width="100%"  style="font-family: Arial, Helvetica, sans-serif;font-size:10px;">
                    <tr style="background-color: #BCDCF9">
                        <th colspan="6" style="text-align: center">Detalle liquidacion afectada</th>
                    </tr>
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <th>Id</th>
                        <th>Liquidaciones</th>
                        <th>fecha</th>
                        <th>Total</th>

                    </tr>
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <td>{{$ed->liquidacion_id}}</td>
                        <td>{{$ed->cod_liquidacion}}</td>
                        <td>{{$ed->created_at}}</td>
                        <td>{{$ed->valor}}</td>
                    </tr>
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <th colspan="3">Rubros</th>
                        <th>Valor</th>
                    </tr>
                    @foreach ($ed->detalle_liquidacion as $detalle)
                        <tr style="border: 1px solid; border-color:darkgrey">
                            <td colspan="3">{{$detalle->descripcion}}</td>
                            @if($detalle->rubro == 2)
                            <td colspan="3">{{$ed->impuesto_predial_actual}}</td>
                            @else
                            <td>{{$detalle->valor}}</td>
                            @endif
                        </tr>
                    @endforeach
                    <tr style="border: 1px solid; border-color:darkgrey">
                        <th colspan="3">Total</th>
                        <th>{{$ed->valor}}</th>
                        </tr>
                </table>
            </div>
            @endforeach
    </div>
</body>
</html>
