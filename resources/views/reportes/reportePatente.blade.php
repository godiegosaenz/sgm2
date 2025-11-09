<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reporte en PDF</title>
    <style>
        @page {
            margin: 10mm;
            /* Puedes ajustar aquí el margen de toda la hoja */
        }

        body {
            font-family: Arial, sans-serif;
            font-size: 10px;
        }

        .cabecera {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 1px;
        }

        .cabecera td {
            vertical-align: middle;
        }

        .cabecera .logo {
            width: 10%;
            text-align: left;
        }

        .cabecera .encabezado {
            width: 90%;
            text-align: center;
            font-size: 9px;
            font-weight: bold;
        }

        .cabecera .subtitulo {
            font-size: 10px;
            font-weight: normal;
            margin-top: 2px;
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
            margin-bottom: 10px;
            font-size: 11px;
        }

        .tabla-izquierda th,
        .tabla-izquierda td {
            border: none;
            padding: 4px;
            text-align: left;
        }

        .tabla-derecha {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
            font-size: 9px;
        }

        .tabla-derecha th,
        .tabla-derecha td {
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

    <table class="cabecera" style="font-size:8px !important">
        <tr>
            <td class="logo">
                <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="60">
            </td>
            <td class="encabezado">

                <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <h4>DIRECCION FINANCIERA<br>
                        SAN VICENTE - MANABI - ECUADOR</h4>
                </h3>

            </td>
            <td class="logo">

            </td>
        </tr>
    </table>
    <table width="100%" border="0" style="font-size:10px">
        <tr>
            <td colspan="3" style="text-align: left; line-height: 15px;">
                <b>&nbsp;RUC: </b>1360014850001<br>
                <b>&nbsp;Lugar y Fecha: </b>San Vicente, {{ $fecha_formateada }}
            </td>

            <td colspan="3" style="text-align: center;">
                <b>TITULO DE CREDITO N° {{$patente->codigo}} </b>

            </td>


        </tr>
    </table>

    <table class="tabla-principal" style="margin-top: 5px;">
        <tr>
            <td class="columna-izquierda">

                <table class="tabla-izquierda" style="font-size: 10px;">
                    <tr>
                        <td width="50%"><b>PATENTE ANUAL PARA ACTIVIDADES ECONOMICAS </b></td>


                    </tr>
                    <tr>
                        <td width="50%"><b>Periodo: </b>{{ $patente->year_ejercicio_fiscal }} </td>


                    </tr>

                    <tr>

                        <td width="50%" style=" line-height: 15px;">
                            <b>Contribuyente: </b> {{ $patente->razon_social }}<br>
                            <b>Cedula/RUC: </b> {{ $patente->ruc }}
                        </td>

                    </tr>

                </table>

                <table class="tabla-izquierda" style="font-size: 10px;">
                    <thead>
                        <tr>
                            <th colspan="2">DATOS DEL ESTABLECIMIENTO</th>
                        </tr>
                    </thead>
                    <tbody>

                        <tr style="line-height: 10px;">
                            <td width="30%"><strong>Nombre Comercial: </strong></td>
                            <td width="70%">{{ $patente->actividad_descripcion }}</td>
                        </tr>
                        <tr style="line-height: 8px;">
                            <td width="30%"><strong>Direccion: </strong></td>
                            <td width="70%">{{ $patente->calle }}</td>
                        </tr>


                    </tbody>
                </table>

            </td>
            <td class="columna-derecha">
                <table class="tabla-derecha">
                    <thead>
                        <tr>
                            <th style="text-align:center">RUBROS/CONCEPTO</th>
                            <th style="width:18% !important; text-align:center">VALOR</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>IMPUESTO PATENTE MUNICIPAL</td>

                            <td style="text-align:right">{{ $patente->valor_impuesto }}</td>
                        </tr>

                        <tr>
                            <td>EXONERACION </td>

                            <td style="text-align:right">-{{number_format($patente->valor_exoneracion, 2)}}</td>
                        </tr>

                        <tr>
                            <td>SERVICIOS ADMINISTRATIVOS</td>

                            <td style="text-align:right">{{number_format($patente->valor_sta, 2)}}</td>
                        </tr>

                        <tr>
                            <td>INTERESES</td>

                            <td style="text-align:right">{{ $patente->valor_intereses }}</td>
                        </tr>

                        <tr>
                            <td>RECARGOS </td>

                            <td style="text-align:right">{{ $patente->valor_recargos }}</td>
                        </tr>

                        <tr>
                            <td>TOTAL A PAGAR </td>

                            <td style="text-align:right">{{ $patente->valor_patente }}</td>
                        </tr>
                    </tbody>

                </table>

            </td>
        </tr>
    </table>

    <table class="tabla-izquierda-" style="font-size: 10px;">
        <thead>
            <tr>
                <th colspan="3" style="text-align:left">ACTIVIDADES</th>
            </tr>
        </thead>
        <tbody>
            @foreach($patente->act as $data)
                <tr>
                    <td colspan="3">{{ $data }}</td>

                </tr>
            @endforeach

        </tbody>
    </table>
    <p style="text-align: center;"><b>Fecha Impresion: </b>{{ date('d-m-Y H:i:s') }}</p>

    <table width="100%" style="margin-top:0px">
        <tbody>

            <tr style="line-height: 5px;">
                <td style="text-align: center">


                    <img src="{{ asset('FIRMA-RENTA.png') }}" height="115px">

                </td>
                <td style="text-align: center">

                </td>

                <td style="text-align: center">

                </td>

            </tr>

            <tr style="line-height: 5px;">
                <td style="text-align: center">
                    __________________________________________
                </td>
                <td style="text-align: center">
                    __________________________________________
                </td>

                <td style="text-align: center">
                    __________________________________________
                </td>

            </tr>

            <tr>
                <td style="text-align: center">
                    <b>RENTAS</b>
                </td>
                <td style="text-align: center">
                    <b>TESORERIA</b>
                </td>
                <td style="text-align: center">
                    <b>RECAUDADOR</b>
                </td>
            </tr>
        </tbody>
    </table>

    @if(!is_null($patente->valor_activo_total) && $patente->es_activo == true)
        <div style="page-break-before: always; break-before: page;"></div>

        <table class="cabecera" style="font-size:8px !important">
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="60">
                </td>
                <td class="encabezado">

                    <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <h4>DIRECCION FINANCIERA<br>
                            SAN VICENTE - MANABI - ECUADOR</h4>
                    </h3>

                </td>
                <td class="logo">

                </td>
            </tr>
        </table>
        <table width="100%" border="0" style="font-size:10px">
            <tr>
                <td colspan="3" style="text-align: left; line-height: 15px;">
                    <b>&nbsp;RUC: </b>1360014850001<br>
                    <b>&nbsp;Lugar y Fecha: </b>San Vicente, {{ $fecha_formateada }}
                </td>

                <td colspan="3" style="text-align: center;">
                    <b>TITULO DE CREDITO N° {{$patente->codigo_act}} </b>

                </td>


            </tr>
        </table>

        <table class="tabla-principal" style="margin-top: 5px;">
            <tr>
                <td class="columna-izquierda">

                    <table class="tabla-izquierda" style="font-size: 10px;">
                        <tr>
                            <td width="50%"><b> IMPUESTO ANUAL DEL 1.5 POR MIL SOBRE LOS ACTIVOS TOTALES </b></td>


                        </tr>
                        <tr>
                            <td width="50%"><b>Periodo: </b>{{ $patente->year_ejercicio_fiscal }} </td>


                        </tr>

                        <tr>

                            <td width="50%" style=" line-height: 15px;">
                                <b>Contribuyente: </b> {{ $patente->razon_social }}<br>
                                <b>Cedula/RUC: </b> {{ $patente->ruc }}
                            </td>

                        </tr>

                    </table>

                    <table class="tabla-izquierda" style="font-size: 10px;">
                        <thead>
                            <tr>
                                <th colspan="2">DATOS DEL ESTABLECIMIENTO</th>
                            </tr>
                        </thead>
                        <tbody>

                            <tr style="line-height: 10px;">
                                <td width="30%"><strong>Nombre Comercial: </strong></td>
                                <td width="70%">{{ $patente->actividad_descripcion }}</td>
                            </tr>
                            <tr style="line-height: 8px;">
                                <td width="30%"><strong>Direccion: </strong></td>
                                <td width="70%">{{ $patente->calle }}</td>
                            </tr>


                        </tbody>
                    </table>



                </td>
                <td class="columna-derecha">
                    <table class="tabla-derecha">
                        <thead>
                            <tr>
                                <th style="text-align:center">RUBROS/CONCEPTO</th>
                                <th style="width:18% !important; text-align:center">VALOR</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>IMPUESTO PATENTE MUNICIPAL</td>

                                <td style="text-align:right">{{ $patente->valor_impuesto_act }}</td>
                            </tr>

                            <tr>
                                <td>EXONERACION </td>

                                <td style="text-align:right">-{{number_format($patente->valor_exoneracion_act, 2)}}</td>
                            </tr>

                            <tr>
                                <td>SERVICIOS ADMINISTRATIVOS</td>

                                <td style="text-align:right">{{number_format($patente->valor_sta_act, 2)}}</td>
                            </tr>

                            <tr>
                                <td>INTERESES</td>

                                <td style="text-align:right">{{ $patente->valor_intereses_act }}</td>
                            </tr>

                            <tr>
                                <td>RECARGOS </td>

                                <td style="text-align:right">{{ $patente->valor_recargos_act }}</td>
                            </tr>

                            <tr>
                                <td>TOTAL A PAGAR </td>

                                <td style="text-align:right">{{ $patente->valor_activo_total }}</td>
                            </tr>
                        </tbody>

                    </table>

                </td>
            </tr>
        </table>

        <table class="tabla-izquierda-" style="font-size: 10px;">
            <thead>
                <tr>
                    <th colspan="3" style="text-align:left">ACTIVIDADES</th>
                </tr>
            </thead>
            <tbody>
                @foreach($patente->act as $data)
                    <tr>
                        <td colspan="3">{{ $data }}</td>

                    </tr>
                @endforeach

            </tbody>
        </table>
        <p style="text-align: center;"><b>Fecha Impresion: </b>{{ date('d-m-Y H:i:s') }}</p>

        <table width="100%" style="margin-top:0px">
            <tbody>

                <tr style="line-height: 5px;">
                    <td style="text-align: center">


                        <img src="{{ asset('FIRMA-RENTA.png') }}" height="115px">

                    </td>
                    <td style="text-align: center">

                    </td>

                    <td style="text-align: center">

                    </td>

                </tr>

                <tr style="line-height: 5px;">
                    <td style="text-align: center">
                        __________________________________________
                    </td>
                    <td style="text-align: center">
                        __________________________________________
                    </td>

                    <td style="text-align: center">
                        __________________________________________
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center">
                        <b>RENTAS</b>
                    </td>
                    <td style="text-align: center">
                        <b>TESORERIA</b>
                    </td>
                    <td style="text-align: center">
                        <b>RECAUDADOR</b>
                    </td>
                </tr>
            </tbody>
        </table>

    @endif

</body>

</html>