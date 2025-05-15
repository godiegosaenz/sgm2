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
            width: 55%;
        }
        .tabla-principal .columna-derecha {
            width: 45%;
        }
        .tabla-izquierda {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            font-size: 11px;
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
    @php
        $sumatotal = 0;
    @endphp
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

                <table class="tabla-izquierda-" style="margin-bottom: 10px;">
                    <thead>
                        <tr>
                            <th width="50%">San Vicente, 01 Enero {{ $d[0]->anio + 1 }} </th>
                        </tr>
                    </thead>
                    
                </table>

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
                            <th>RUBROS/CONCEPTO</th>
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
                            <th>Fecha desde la cual se desvenga intereses:</th>
                            <<th>Enero {{ $d[0]->anio + 1 }}</th>
                        </tr>
                        <tr>
                            <th>Intereses hasta la fecha de emision:</th>
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
    <center><b>Fecha Impresion:</b>{{ $fecha_formateada }}<br>
    <b>Ley Coa Aticulo 268</b></center>
    <br>
    <br>
    <hr style="border:Dotted;"/>
    <br>
    <br>
    @php
        $sumatotal = $sumatotal + $d[0]->total_pago + $d[0]->valor_complemento;
    @endphp
    @endforeach
    <h2 style="text-align: center">TOTAL DE TÍTULO DE CRÉDITO: <span class="badge text-bg-secondary">{{$sumatotal}};</span></h2>
    <p>CONCEPTO POR EL CUAL SE EMITE: PAGO DE IMPUESTO PREDIAL VENCIDO.
        EL VALOR TOTAL DEL TÍTULO DE CRÉDITO CAUSARÁ EL INTERES RESPECTIVO A PARTIR DE LA FECHA DE
        NOTIFICACIÓN SEGÚN LO EXPUESTO Y CONFORME LO ESTABLECE EL ART. 265 DEL COA LIQUIDACIÓN DE
        INTERESES Y MULTAS</p>
    <br>
    <br>
    <br>
    <br>
    <table width="100%">
        <tbody>
            <tr>
                <td style="text-align: center">
                    __________________________________________
                </td>
                <td style="text-align: center">
                    __________________________________________
                </td>
            </tr>
            <tr>
                <td style="text-align: center">
                    Ing. Jacinta María Mendoza Cusme
                </td>
                <td style="text-align: center">
                    Mgs. Lucía Alvarez Zambrano
                </td>
            </tr>
            <tr>
                <td style="text-align: center">
                    Tesorera Municipal Juez de Coactiva
                </td>
                <td style="text-align: center">
                    Directora Financiera
                </td>
            </tr>
        </tbody>
    </table>

</body>
</html>
