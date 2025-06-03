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
            margin-bottom: 10px;
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
            font-size: 12px;
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
    @foreach($datosTitulo as $item)
    @php
        $impuesto = $item['TransitoImpuesto'];
    @endphp
        <table class="cabecera">
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="80">
                </td>
                <td class="encabezado">
                    <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <h4>UNIDAD MUNICIPAL DE TRANSITO, TRANSPORTE TERRESTRE <br>Y SEGURIDAD VIAL</h4></h3>
                    <!-- <table>
                        <tbody>
                            <td><h4>TITULO DE CREDITO11 N° {{$impuesto->numero_titulo}}</h4></td>
                            <td><p class="" style="font-size: 10px;"> - San Vicente, {{ $fecha_formateada }}</p></td>
                            <tr>
                                <td width="30%"><strong>Año de impuesto:</strong></td>
                                <td width="70%">{{$impuesto->year_impuesto}}</td>
                            </tr>
                        </tbody>
                    </table> -->

                </td>
                 <td class="logo">
                    <!-- <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="80"> -->
                </td>
            </tr>
        </table>
        <table width="100%" border="0"  style="">
            <tr>
                <td colspan="3" style="text-align: center;">
                    <b>TITULO DE CREDITO N° {{$impuesto->numero_titulo}}</b><br>
                    San Vicente, {{ $fecha_formateada }}
                </td>
            </tr>
        </table>
        
        <table class="tabla-principal" style="margin-top: 15px;">
            <tr>
                <td class="columna-izquierda">

                    <table class="tabla-izquierda" style="font-size: 12px;">                       
                        <tr>
                            <td width="50%"><b>Año de Impuesto :</b>{{$impuesto->year_impuesto}} </td>
                            
                            
                        </tr>  
                        
                        <tr>
                          
                            <td width="50%"><b>Cedula/RUC :</b>{{$item['cliente']->ci_ruc}} </td>
                           
                            
                        </tr>  

                        <tr>
                            
                            <td width="50%"><b>Contribuyente :</b>{{$item['cliente']->nombres.' '.$item['cliente']->apellidos}} </td>
                            
                        </tr>  
                      
                    </table>

                    <!-- <table class="tabla-izquierda" style="font-size: 12px;">
                        <thead>
                            <tr>
                                <th width="50%">Contribuyente : </th>
                                <th width="50%">Cedula :</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>

                                <td width="50%">{{$item['cliente']->nombres.' '.$item['cliente']->apellidos}}</td>
                                <td width="50%">{{$item['cliente']->ci_ruc}}</td>

                            </tr>
                        </tbody>
                    </table> -->
                    <table class="tabla-izquierda" style="font-size: 12px;">
                        <thead >
                            <tr>
                                <th colspan="2">Datos de Vehiculo</th>
                            </tr>
                        </thead>
                        <tbody>

                                <tr>
                                    <td width="30%"><strong>Placa:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->placa}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Chasis:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->chasis}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Avaluo:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->avaluo}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Año de modelo:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->year}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Clase tipo:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->tipo_vehiculo->descripcion}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Marca:</strong></td>
                                    <td width="70%">{{$item['vehiculo']->marca->descripcion}}</td>
                                </tr>

                        </tbody>
                    </table>

                </td>
                <td class="columna-derecha">
                    <table class="tabla-derecha" style="font-size: 10px;">
                        <thead>
                            <tr>
                                <th>RUBROS/CONCEPTO</th>
                                <th>VALOR</th>
                            </tr>
                        </thead>
                        <tbody>
                            @foreach($item['transitoimpuestoconcepto'] as $r)
                                <tr>
                                    <td>{{$r->concepto}}</td>
                                    <td style="text-align: right;">$ {{$r->pivot->valor}}</td>
                                </tr>
                            @endforeach

                        </tbody>
                        <tfoot>
                            <tr>
                                <td><STRONg>TOTAL</STRONg> </td>
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
        <br>
        <br>
        <hr style="border:Dotted;"/>
        <br>
        <br>
        @php
            $sumatotal = 10
        @endphp
    @endforeach


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
