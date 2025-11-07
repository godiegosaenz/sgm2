<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reporte en PDF</title>
    <style>
        @page {
            margin: 10mm; /* Puedes ajustar aquí el margen de toda la hoja */
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
        .tabla-izquierda th, .tabla-izquierda td {
            border: none;
            padding: 4px;
            text-align: left;
        }
        .tabla-derecha {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
            font-size: 8px;
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
    @foreach($datosTitulo as $item)
    @php
        $impuesto = $item['TransitoImpuesto'];
    @endphp
        <table class="cabecera" style="font-size:8px !important">
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="60">
                </td>
                <td class="encabezado">
                    <!-- <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <h4>UNIDAD MUNICIPAL DE TRANSITO, TRANSPORTE TERRESTRE <br>Y SEGURIDAD VIAL</h4></h3> -->

                    <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <h4>DIRECCION FINANCIERA<br>
                    SAN VICENTE - MANABI - ECUADOR</h4></h3>
                   

                </td>
                 <td class="logo">
                    <!-- <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="80"> -->
                </td>
            </tr>
        </table>
        <table width="100%" border="0"  style="font-size:10px">
            <tr>
                <td colspan="3" style="text-align: left; line-height: 15px;">          
                    <b>&nbsp;RUC: </b>1360014850001<br>         
                    <b>&nbsp;Lugar y Fecha: </b>San Vicente, {{ $fecha_formateada }}
                </td>

                <td colspan="3" style="text-align: center;">
                    <b>TITULO DE CREDITO N° {{$impuesto->numero_titulo}}</b>
                    <!-- San Vicente, {{ $fecha_formateada }} -->
                </td>

                
            </tr>
        </table>
        
        <table class="tabla-principal" style="margin-top: 5px;">
            <tr>
                <td class="columna-izquierda">

                    <table class="tabla-izquierda" style="font-size: 10px;">   
                        <tr>
                            <td width="50%"><b>UNIDAD MUNICIPAL DE TRANSITO, TRANSPORTE TERRESTRE <br>Y SEGURIDAD VIAL</b></td>
                            
                            
                        </tr>                     
                        <tr>
                            <td width="50%"><b>Proceso de Matriculacion Vehicular :</b>{{$impuesto->year_impuesto}} </td>
                            
                            
                        </tr>  
                        
                        <tr>
                            
                            <td width="50%" style=" line-height: 15px;">
                                <b>Contribuyente :</b>{{$item['cliente']->nombres.' '.$item['cliente']->apellidos}} <br>
                                <b>Cedula/RUC :</b>{{$item['cliente']->ci_ruc}}
                            </td>
                            
                        </tr>  
                        <!-- <tr>
                          
                            <td width="50%"><b>Cedula/RUC :</b>{{$item['cliente']->ci_ruc}} </td>
                           
                            
                        </tr>  -->
                      
                    </table>

                    <table class="tabla-izquierda" style="font-size: 10px;">
                        <thead >
                            <tr>
                                <th colspan="2">DATOS DEL VEHICULO</th>
                            </tr>
                        </thead>
                        <tbody>

                                <tr style="line-height: 10px;">
                                    <td width="30%"><strong>Placa/CPN/RAMV:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->placa_cpn_ramv}}</td>
                                </tr>
                                <tr style="line-height: 8px;">
                                    <td width="30%"><strong>Chasis:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->chasis}}</td>
                                </tr>
                                <tr style="line-height: 8px;">
                                    <td width="30%"><strong>Avaluo:</strong></td>
                                    <td width="70%">${{number_format($item['vehiculo']->avaluo,2)}}  </td>
                                </tr>
                                <tr style="line-height: 8px;">
                                    <td width="30%"><strong>Año de modelo:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->year}}</td>
                                </tr>
                                <tr style="line-height: 8px;">
                                    <td width="30%"><strong>Clase tipo:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->clase_vehiculo->descripcion}}</td>
                                </tr>
                                <tr style="line-height: 8px;">
                                    <td width="30%"><strong>Marca:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->marca->descripcion}}</td>
                                </tr>
                                <tr style="line-height: 8px;">
                                    <td width="30%"><strong>Tipo:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->tipo_vehi ?? 'PUBLICO'}}</td>
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
                            @foreach($item['transitoimpuestoconcepto'] as $r)
                                @php
                                    $concepto=$r->concepto;
                                    if($r->codigo=='RTV'){
                                        //$concepto=$r->concepto ." (".implode(', ', $r->agrupado). ")";
                                        $concepto=$r->concepto ." (".$item['vehiculo']->tipo_vehiculo->descripcion. ")";
                                    }

                                    if($r->codigo=='REC' && !is_null($impuesto['calendarizacion'])){
                                        $concepto=$r->concepto ." (CALENDARIZACION ".$impuesto['calendarizacion'].")";
                                    }
                                @endphp
                                
                                <tr>
                                    <td>
                                        {{$concepto}}
                                    </td>
                                    <td style="text-align: right; vertical-align:middle">$ {{$r->pivot->valor}}</td>
                                </tr>
                            @endforeach

                        </tbody>
                        <tfoot>
                            <tr>
                                <td><STRONg>TOTAL A PAGAR</STRONg> </td>
                                <td style="text-align: right;"><STRONg>$ {{$impuesto->total_pagar}}</STRONg> </td>
                            </tr>
                        </tfoot>
                    </table>
                    <!-- <table class="tabla-izquierda">
                        <tbody>
                            <tr>
                                <th>Fecha desde la cual se desvenga intereses:</th>
                                <<th>Enero </th>
                            </tr>
                            <tr>
                                <th>Intereses hasta la fecha de emision:</th>
                                <th>$ </th>
                            </tr>
                            <tr>
                                <th>Total de Deuda:</th>
                                <th>$ </th>
                            </tr>
                        </tbody>

                    </table> -->
                </td>
            </tr>
        </table>
        <!-- <br>
        <br>
        <hr style="border:Dotted;"/>
        <br>
        <br> -->
        @php
            $sumatotal = 10
        @endphp
    @endforeach


    <table width="100%" style="margin-top:0px">
        <tbody>

            <tr style="line-height: 5px;">
                <td style="text-align: center">
                    <!-- tenia 3 en el esatdo -->
                    <!-- @if($impuesto->estado==13)            
                        <img src="{{ asset('qrfirma/Rentas.png') }}"  width="60%">
                    @endif -->
                    @if($impuesto->estado==3 || $impuesto->estado==1)  
                        <img src="{{ asset('FIRMA-RENTA.png') }}"   height="115px">
                    @endif
                </td>
                <td style="text-align: center">
                    @if($impuesto->estado==13)
                        <img src="{{ asset('qrfirma/Tesoreria.png') }}"  width="60%">
                    @endif
                </td>

                <td style="text-align: center">
                    @if($impuesto->estado==13)
                        <img src="{{ asset('qrfirma/Recaudador.png') }}"  width="60%">
                    @endif
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

</body>
</html>
