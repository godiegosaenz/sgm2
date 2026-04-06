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

        .watermark {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 50%; /* Solo ocupa la mitad superior */
            background-image: url('{{ asset('img/logo4.png') }}');
            background-repeat: no-repeat;
            background-size: contain;
            background-position: center;
            opacity: 0.05; /* Opacidad muy baja */
            z-index: -1; /* La imagen queda detrás del contenido */
        }

        

    </style>
</head>
<body>
    @php
        $sumatotal = 0;
    @endphp
        <div class="watermark"></div>
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
                    <b>PAGO DE CUOTA DE CONVENIO #{{ $num_cuota }}</b><br>
                  
                    
                </td>
            </tr>
            <tr style="line-height: 20px;">
                <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;border-top:0px;width:45%">
                </td>
            </tr>
            <tr style="font-size: 11px;">
               
                <td style="width: 50%;"><b>Ruc/CC:</b>{{ $cedula }}</td>
            
                <td style="width: 1%; text-align:left"></td>

                <td><b>Contribuyente:</b>{{ $contr }}</td>
               
            </tr>

            <tr style="font-size: 11px;">
               
               
                <td><b>Fecha Convenio:</b> {{ $data->fecha_registra }}</td>
            
                <td style="width: 1%; text-align:left"></td>

                <td><b>Fecha Impresion:</b> {{ date('Y-m-d H:i:s') }}</td>
               
            </tr>
            <tr style="font-size: 11px;">
               
               <td colspan="3"><b>Matricula/Clave:</b> {{ implode(',', $clave) }}</td>

                
               
            </tr>
            
           
        </table> 
        @php
            // $valor_pend=round((float)$data->valor_cancelado,2) - round((float)$total,2);
            $valor_pend=round((float)$total,2) - round((float)$data->valor_cancelado,2);
            if($valor_pend<0){
                $valor_pend='0.00';
            }

        @endphp
       
        <table width="100%" style="border-collapse: collapse; margin-top:10px">
            <tr style="font-size: 11px;">
                <!-- TABLA IZQUIERDA -->
                <td style="width:30%; vertical-align: top;padding-right:1px;">
                    <table width="100%" style="border-collapse: collapse; border-spacing: 0;">

                        <tr>
                            <td colspan="2" style="text-align:center"><b>Datos del Pago</b></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Fecha Pago</b></td>
                            <td style="border: 1px solid white; text-align:right" > {{ date('d/m/Y', strtotime($cuota->fecha_cobro)) }}</td>
                         <td style="border: 1px solid white; text-align:right"></td>
                           
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Pagado</b></td>
                            <td style="border: 1px solid white; text-align:right">{{ $cuota->valor_cobrado }}</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>

                        <tr>
                            <td style="border: 1px solid white;text-align:right"><span style="color:white">..</span></td>
                            <td style="border: 1px solid white; text-align:right"><span style="color:white">..</span></td>
                            <td style="border: 1px solid white; text-align:right"><span style="color:white">..</span></td>
                           
                        </tr>

                        <tr>
                            <td colspan="3" style="text-align:center"><b>Datos del Convenio</b></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;width:60%;text-align:right"><b>Valor Deuda Convenio</b></td>
                            <td style="border: 1px solid white; text-align:right">{{ $data->valor_adeudado }}</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Intereses</b></td>
                            <td style="border: 1px solid white; text-align:right">{{  number_format($interes,2) }}</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Final</b></td>
                            <td style="border: 1px solid white; text-align:right">{{$total}}</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Cancelado</b></td>
                            <td style="border: 1px solid white; text-align:right">{{ $data->valor_cancelado }}</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                        <tr>
                            <td style="border: 1px solid white;text-align:right"><b>Valor Pendiente</b></td>
                            <td style="border: 1px solid white; text-align:right">{{ $valor_pend }}</td>
                            <td style="border: 1px solid white; text-align:right"></td>
                        </tr>
                    </table>
                </td>

                <!-- TABLA DERECHA -->
                <td style="width:70%; vertical-align: top;padding-left:10px;">
                    <table width="100%" style="border-collapse: collapse; border-spacing: 0;">
                        <tr style="font-size: 11px">
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;" ><b>#Cuota</b></td>
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;"><b>Fecha Pago</b></td>
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;"><b>Valor Convenio</b></td>
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;"><b>Valor Interes</b></td>
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;"><b>Valor Abono</b></td>
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;"><b>Valor a Pagar</b></td>
                            <td style="border: 1px solid #000; text-align:center; background-color:#D6D3D2;"><b>Estado</b></td>
                        </tr>
                        @php
                            $tamanio=sizeof($data->cuotas);
                            $interes_valor='0.00';  
                        @endphp
                        @foreach ($data->cuotas as $i=> $info)
                            @php
                                $cuota=$info->valor_cuota;
                                $valor_abono=$info->saldo_abono === null ? '0' : $info->saldo_abono;
                                if($i==$tamanio-1){
                                    $interes_valor=$interes;
                                    $cuota=round((float)$info->valor_cuota,2) + round((float)$interes,2) - round((float)$valor_abono,2);
                                }else{
                                    $cuota=round((float)$info->valor_cuota,2) - round((float)$valor_abono,2);
                                }
                            @endphp
                      
                            <tr style="font-size: 11px">
                                <td style="border: 1px solid #000;text-align:center">
                                    {{ $info->cuota_inicial === true ? 'Inicial': $i }}
                                </td>
                               
                                <td style="border: 1px solid #000;text-align:center">{{ $info->fecha }}</td>
                                <td style="border: 1px solid #000;text-align:right">{{ $info->valor_cuota }}</td>
                                <td style="border: 1px solid #000;text-align:right">{{ $interes_valor}}</td>
                                <td style="border: 1px solid #000;text-align:right">{{ $info->saldo_abono === null ? '0.00' : $info->saldo_abono}}</td>
                                <td style="border: 1px solid #000;text-align:right">{{ number_format($cuota,2) }}</td>
                                <td style="border: 1px solid #000;text-align:center">{{ $info->estado }}</td>
                            </tr>
                        @endforeach
                    </table>
                </td>
            </tr>
        </table>
</body>
</html>
