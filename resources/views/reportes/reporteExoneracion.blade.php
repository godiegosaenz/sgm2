<!DOCTYPE html>
<html>
<head>
    <title>Generacion de recibo</title>
    <link href="{{ asset('css/bootstrap.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/bootstrap-icons.css') }}" rel="stylesheet">
    <link href="{{ asset('font/font.css') }}" rel="stylesheet">
</head>
<body>
    <div class="row justify-content-center align-items-center">
        <div class="col-12">
            <img src="{{ asset('img/cabeceraReporteTesoreria.jpg') }}" alt="" width="100%" >
        </div>
    </div>
    <br>
    <div class="row justify-content-center align-items-center" style="font-family: Arial, Helvetica, sans-serif;">
        <div class="col-12">
            <h3>INFORME DE EXONERACIÓN DE TERCERA EDAD</h3>
        </div>
    </div>
    <br>
    <div class="row">
        <div class="col-10">
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
        <div class="col-10" style="margin-top: 20px;">
            <table width="100%" style="border: 1px solid;border-color:darkgrey;font-family: Arial, Helvetica, sans-serif;">
                <tr style="background-color: #BCDCF9">
                    <th colspan="5" style="text-align: center">Detalle de liquidaciones afectadas</th>
                </tr>
                <tr>
                    <th style="border: 1px solid; border-color:darkgrey">Id</th>
                    <th style="border: 1px solid; border-color:darkgrey">Liquidaciones</th>
                    <th style="border: 1px solid; border-color:darkgrey">Valor</th>
                    <th style="border: 1px solid; border-color:darkgrey">Valor anterior</th>
                    <th style="border: 1px solid; border-color:darkgrey">fecha</th>
                </tr>
                @foreach ($ExoneracionAnterior->exoneracion_detalle as $ed)
                    <tr >
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->liquidacion_id}}</td>
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->cod_liquidacion}}</td>
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->valor}}</td>
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->valor_anterior}}</td>
                        <td style="border: 1px solid; border-color:darkgrey">{{$ed->created_at}}</td>
                    </tr>
                @endforeach
            </table>

        </div>
    </div>
</body>
</html>
