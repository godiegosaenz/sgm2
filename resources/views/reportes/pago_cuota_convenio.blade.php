<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reporte en PDF</title>
    <style>
        @page {
            margin-top: 0em;
            margin-left:1em;
            margin-right:1em;
            margin-bottom: 12em;
        }
        body {
            font-family: Arial, sans-serif;
            font-size: 12px;
        }
        .cabecera {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 3px;
        }
        .cabecera td {
            vertical-align: middle;
        }
        .cabecera .logo {
            width: 15%;
            text-align: left;
        }
        .cabecera .encabezado {
            width: 60%;
            text-align: left;
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

        .ltable
        {
            border-collapse: collapse;
            font-family: sans-serif;
        }

        

    </style>
</head>
<body>
    @php
        $sumatotal = 0;
    @endphp
   
        <table class="cabecera">
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="80">
                </td>
                <td class="encabezado">
                    Gobierno Autónomo Descentralizado Municipal del Cantón San Vicente
                    <!-- <div class="subtitulo">Título de Crédito</div> -->
                </td>
            </tr>
        </table>
        
        <table width="100%" border="0" >
            <tr>
                <td colspan="3" style="text-align: center;">
                    <b>PAGO DE CUOTA DE CONVENIO </b><br>
                  
                    
                </td>
            </tr>
            <tr style="line-height: 20px;">
                <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;border-top:0px;width:45%">
                </td>
            </tr>
            <tr style="font-size: 11px;">
               
                <td><b>Ruc/CC:</b> .......</td>
            
                <td style="width: 1%; text-align:left"></td>

                <td><b>Contribuyente:</b> ........</td>
               
            </tr>

            <tr style="font-size: 11px;">
               
                <td><b>Matricula/Clave:</b> .......</td>
            
                <td style="width: 1%; text-align:left"></td>

                <td><b>Fecha Convenio:</b> ........</td>
               
            </tr>
            
           
        </table> 

       
        <table width="100%" style="border-collapse: collapse; margin-top:10px">
            <tr style="font-size: 11px;">
                <!-- TABLA IZQUIERDA -->
                <td style="width:27%; vertical-align: top;padding-right:1px;">
                    <table width="100%" style="border-collapse: collapse; border-spacing: 0;">
                        <tr>
                            <td style="border: 1px solid white;width:60%;text-align:right"><b>Valor Deuda Convenio</b></td>
                            <td style="border: 1px solid white; text-align:right">555</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Intereses</b></td>
                            <td style="border: 1px solid white; text-align:right">555</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Final</b></td>
                            <td style="border: 1px solid white; text-align:right">555</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Cancelado</b></td>
                            <td style="border: 1px solid white; text-align:right">555</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Pendiente</b></td>
                            <td style="border: 1px solid white; text-align:right">555</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                    </table>
                </td>

                <!-- TABLA DERECHA -->
                <td style="width:73%; vertical-align: top;padding-left:10px;">
                    <table width="100%" style="border-collapse: collapse; border-spacing: 0;">
                        <tr style="font-size: 11px">
                            <td style="border: 1px solid #000; text-align:center"><b>#Cuota</b></td>
                            <td style="border: 1px solid #000; text-align:center"><b>Fecha Pago</b></td>
                            <td style="border: 1px solid #000; text-align:center"><b>Valor Convenio</b></td>
                            <td style="border: 1px solid #000; text-align:center"><b>Valor Interes</b></td>
                            <td style="border: 1px solid #000; text-align:center"><b>Valor Abono</b></td>
                            <td style="border: 1px solid #000; text-align:center"><b>Valor a Pagar</b></td>
                            <td style="border: 1px solid #000; text-align:center"><b>Estado</b></td>
                        </tr>
                        @foreach ($data->cuotas as $i=> $info)
                        
                      
                            <tr style="font-size: 11px">
                                <td style="border: 1px solid #000;text-align:center">
                                    {{ $info->cuota_inicial === true ? 'Inicial': $i }}
                                </td>
                               
                                <td style="border: 1px solid #000;text-align:center">{{ $info->fecha }}</td>
                                <td style="border: 1px solid #000;text-align:right">{{ $info->valor_cuota }}</td>
                                <td style="border: 1px solid #000;text-align:right">0.00</td>
                                <td style="border: 1px solid #000;text-align:right">{{ $info->saldo_abono === null ? '0.00' : $info->saldo_abono}}</td>
                                <td style="border: 1px solid #000;text-align:right">222</td>
                                <td style="border: 1px solid #000;">{{ $info->estado }}</td>
                            </tr>
                        @endforeach
                    </table>
                </td>
            </tr>
        </table>
</body>
</html>
