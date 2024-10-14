<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reporte en PDF</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            font-size: 12px;
        }
        .cabecera {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        .cabecera td {
            vertical-align: middle;
        }
        .cabecera .logo {
            width: 30%;
            text-align: left;
        }
        .cabecera .encabezado {
            width: 70%;
            text-align: center;
            font-size: 14px;
            font-weight: bold;
        }
        .cabecera .subtitulo {
            font-size: 12px;
            font-weight: normal;
            margin-top: 5px;
        }
        .tabla-principal {
            width: 100%;
            border-collapse: collapse;
        }
        .tabla-principal td {
            vertical-align: top;
        }
        .tabla-principal .columna-izquierda {
            width: 60%;
        }
        .tabla-principal .columna-derecha {
            width: 40%;
        }
        .tabla-izquierda {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            font-size: 12px;
        }
        .tabla-izquierda th, .tabla-izquierda td {
            border: none;
            padding: 4px;
            text-align: left;
        }
        .tabla-derecha {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            font-size: 10px;
        }
        .tabla-derecha th, .tabla-derecha td {
            border: 1px solid #000;
            padding: 4px;
            text-align: left;
        }
        .tabla-derecha th {
            background-color: #f2f2f2;
        }

    </style>
</head>
<body>
    @foreach ($DatosLiquidacion as $d)
    <table class="cabecera">
        <tr>
            <td class="logo">
                <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="100">
            </td>
            <td class="encabezado">
                Gobierno Autónomo Descentralizado Municipal del Cantón San Vicente
                <div class="subtitulo">Título de Crédito</div>
            </td>
        </tr>
    </table>
    <table class="tabla-principal">
        <tr>
            <td class="columna-izquierda">
                <table class="tabla-izquierda">
                    <thead>
                        <tr>
                            <th width="50%">Contribuyente : </th>
                            <th width="50%">Cedula :</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td width="50%">{{$d[0]->nombres}}</td>
                            <td width="50%">{{$d[0]->cedula}}</td>
                        </tr>
                    </tbody>
                </table>
                <table class="tabla-izquierda">
                    <thead >
                        <tr>
                            <th colspan="2">TITULO DE CREDITO - PREDIO URBANO # {{$d[0]->id_liquidacion}}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td width="30%"><strong>Cod. predial:</strong></td>
                            <td width="70%">{{$d[0]->cod_predial}}</td>
                        </tr>
                        <tr>
                            <td width="30%"><strong>Mat. inmobiliaria:</strong></td>
                            <td width="70%">{{$d[0]->num_predio}}</td>
                        </tr>
                        <tr>
                            <td width="30%"><strong>Direccion:</strong></td>
                            <td width="70%">{{$d[0]->direccion}}</td>
                        </tr>
                    </tbody>
                </table>
                <table class="tabla-izquierda">
                    <thead>
                        <tr>
                            <th>Avalúo Solar</th>
                            <th>Avalúo Construcción</th>
                            <th>Avalúo Propiedad</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>$ {{$d[0]->avaluo_solar}}</td>
                            <td>$ {{$d[0]->avaluo_construccion}}</td>
                            <td>$ {{$d[0]->avaluo_municipal}}</td>
                        </tr>
                    </tbody>
                </table>
            </td>
            <td class="columna-derecha">
                <table class="tabla-derecha">
                    <thead>
                        <tr>
                            <th>RUBROS</th>
                            <th>VALOR</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach ($d['rubros'] as $r)
                            <tr>
                                <td>{{$r->descripcion}}</td>
                                <td>$ {{$r->valor}}</td>
                            </tr>
                        @endforeach

                    </tbody>
                    <tfoot>
                        <tr>
                            <td><STRONg>TOTAL</STRONg> </td>
                            <td><STRONg>$ {{$d[0]->total_pago}}</STRONg> </td>
                        </tr>
                    </tfoot>
                </table>
                <table class="tabla-izquierda">
                    <tbody>
                        <tr>
                            <th>Recargo + Impuestos:</th>
                            <th>$ {{$d[0]->valor_complemento}}</th>
                        </tr>
                        <tr>
                            <th>Total de Deuda:</th>
                            <th>$ {{($d[0]->total_pago + $d[0]->valor_complemento)}}</th>
                        </tr>
                    </tbody>

                </table>
            </td>
        </tr>
    </table>
    <br>
    <br>
    <hr style="border:Dotted;"/>
    <br>
    <br>
    @endforeach

</body>
</html>
