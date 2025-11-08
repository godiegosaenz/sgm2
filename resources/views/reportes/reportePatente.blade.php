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
            margin-bottom: 1px;
        }
        .cabecera td {
            vertical-align: middle;
        }
        .cabecera .logo {
            width: 20%;
            text-align: left;
        }
        .cabecera .encabezado {
            width: 80%;
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
  
    <table class="cabecera">
        <tr>
            <td class="logo">
                <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="100">
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
                <b>TITULO DE CREDITO - PATENTE #{{str_pad($patente->codigo, 5, '0', STR_PAD_LEFT)}} </b><br>
                San Vicente, {{ $fecha_formateada }}
                
            </td>
        </tr>
        <tr style="line-height: 20px;">
            <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">
            </td>
        </tr>
        <tr style="font-size: 11px;">
            <td style="width: 48%;"><b>RUC/C.I.:</b> {{ $patente->ruc }}</td>
            
        
            <td style="width: 1%; text-align:left"></td>

            <td><b>RAZON SOCIAL:</b> {{ $patente->razon_social }} </td>
            
            
        
        </tr>
        <tr style="font-size: 11px;">
            <td><b>Obligado Contabilidad:</b> {{  $patente->obligado_contabilidad ? 'SI' : 'NO' }}</td>
            
            <td></td>
            <td><b>Regimen:</b> {{ $patente->regimen }}</td>
            
        </tr>

        <tr style="font-size: 11px;">
            <td><b>Periodo:</b> {{ $patente->year_ejercicio_fiscal }}</td>
            
            <td></td>
            <td><b></b> </td>
            
        </tr>
        
    </table>
    
    <table style="border-collapse: collapse; width: 100%;">
        <tr style="font-size: 11px;">
            <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;width:60%">
                <table style="width:100%">
                    <tr>
                        <td colspan="2" style="text-align: center;"><b>LOCAL</b></td>
                        
                    </tr>

                    <tr>
                        <td style="width:35%"><b>Nombre Comercial:</b></td>
                        <td>{{ $patente->actividad_descripcion }}</td>
                        
                        
                    </tr>

                    <tr>
                        <td><b>Direccion:</b></td>
                        <td>{{ $patente->calle }}</td>
                        
                    </tr>

                    <tr>
                        <td><b>Local:</b></td>
                        <td>{{ $patente->local_propio == 1 ? 'Propio' : 'Arrendado'}}</td>
                        
                    </tr>
                    
                    <tr style="line-height: 20px;">
                        <td colspan="2" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                    
                    </tr>

                        <tr>
                        <td colspan="2" style="text-align: center;"><b>ACTIVIDADES</b></td>
                        
                    </tr>

                    @foreach($patente->act as $data)
                        <tr>
                            <td colspan="2">* {{ $data }}</td>
                            
                        </tr>
                    @endforeach

                    
                </table>
            
            </td>
            
            <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px">
                <table style="width:100%">
                    <tr>
                        <td><b>RUBROS/CONCEPTO</b></td>
                        <td></td>
                        <td><b>VALORES</b></td>
                    </tr>
                    <tr style="line-height: 20px;">
                        <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                    
                    </tr>

                    <tr>
                        <td><b>Impuesto Patente Municipal:</b> </td>
                        <td>$</td>
                        <td style="text-align:right">{{ $patente->valor_impuesto }}</td>
                    </tr>

                    <tr>
                        <td><b>Exoneracion:</b> </td>
                        <td>$</td>
                            <td style="text-align:right">-{{number_format($patente->valor_exoneracion,2)}}</td>
                    </tr>

                    <tr>
                        <td><b>Servicios Administrativo:</b> </td>
                        <td>$</td>
                        <td style="text-align:right">{{number_format($patente->valor_sta,2)}}</td>
                    </tr>


                    <!-- @php
                        $valor_emitido= ($patente->valor_patente - $patente->valor_intereses - $patente->valor_recargos);

                    @endphp
                    
                    <tr style="margin-top:20px">
                        <td><b>Valor Emitido:</b> </td>
                        <td>$</td>
                        <td style="text-align:right">{{ number_format($valor_emitido,2) }}</td>
                    </tr> -->

                    


                

                    <tr>
                        <td><b>Intereses:</b> </td>
                        <td>$</td>
                        <td style="text-align:right">{{ $patente->valor_intereses }}</td>
                    </tr>

                    <tr>
                        <td><b>Recargos:</b> </td>
                        <td>$</td>
                        <td style="text-align:right">{{ $patente->valor_recargos }}</td>
                    </tr>

                    <tr>
                        <td><b>Total A Pagar:</b> </td>
                        <td>$</td>
                        <td style="text-align:right">{{ $patente->valor_patente }}</td>
                    </tr>

                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">

            </td>
        </tr>
    
    </table>

    
    <br>
    <center><b>Fecha Impresion: </b>{{ $fecha_formateada_hoy }}<br>
    
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
                    Ing. Jandry Fernando Loor Macay
                </td>
            </tr>
            <tr>
                <td style="text-align: center">
                    Tesorera Municipal Juez de Coactiva
                </td>
                <td style="text-align: center">
                Director Financiero (E)
                </td>
            </tr>
        </tbody>
    </table>

    @if(!is_null($patente->valor_activo_total) && $patente->es_activo==true)
        <div style="page-break-before: always; break-before: page;"></div>
            <table class="cabecera">
                <tr>
                    <td class="logo">
                        <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="100">
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
                        <b>TITULO DE CREDITO  IMPUESTO ANUAL DEL 1.5 POR MIL <br>SOBRE LOS ACTIVOS TOTALES #{{str_pad($patente->codigo_act, 5, '0', STR_PAD_LEFT)}} </b><br>
                        San Vicente, {{ $fecha_formateada }}
                        
                    </td>
                </tr>
                <tr style="line-height: 20px;">
                    <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">
                    </td>
                </tr>
                <tr style="font-size: 11px;">
                    <td style="width: 48%;"><b>RUC/C.I.:</b> {{ $patente->ruc }}</td>
                    
                
                    <td style="width: 1%; text-align:left"></td>

                    <td><b>RAZON SOCIAL:</b> {{ $patente->razon_social }} </td>
                    
                    
                
                </tr>
                <tr style="font-size: 11px;">
                    <td><b>Obligado Contabilidad:</b> {{  $patente->obligado_contabilidad ? 'SI' : 'NO' }}</td>
                    
                    <td></td>
                    <td><b>Regimen:</b> {{ $patente->regimen }}</td>
                    
                </tr>

                <tr style="font-size: 11px;">
                    <td><b>Periodo:</b> {{ $patente->year_ejercicio_fiscal }}</td>
                    
                    <td></td>
                    <td><b></b> </td>
                    
                </tr>
                
            </table>
            
            <table style="border-collapse: collapse; width: 100%;">
                <tr style="font-size: 11px;">
                    <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;width:60%">
                        <table style="width:100%">
                            <tr>
                                <td colspan="2" style="text-align: center;"><b>LOCAL</b></td>
                                
                            </tr>

                            <tr>
                                <td style="width:35%"><b>Nombre Comercial:</b></td>
                                <td>{{ $patente->actividad_descripcion }}</td>
                                
                                
                            </tr>

                            <tr>
                                <td><b>Direccion:</b></td>
                                <td>{{ $patente->calle }}</td>
                                
                            </tr>

                            <tr>
                                <td><b>Local:</b></td>
                                <td>{{ $patente->local_propio == 1 ? 'Propio' : 'Arrendado'}}</td>
                               
                                
                            </tr>
                            
                            <tr style="line-height: 20px;">
                                <td colspan="2" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                            
                            </tr>

                                <tr>
                                <td colspan="2" style="text-align: center;"><b>ACTIVIDADES</b></td>
                                
                            </tr>

                            @foreach($patente->act as $data)
                                <tr>
                                    <td colspan="2">* {{ $data }}</td>
                                    
                                </tr>
                            @endforeach

                            
                        </table>
                    
                    </td>
                    
                    <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px">
                        <table style="width:100%">
                            <tr>
                                <td><b>RUBROS/CONCEPTO</b></td>
                                <td></td>
                                <td><b>VALORES</b></td>
                            </tr>

                            <tr style="line-height: 20px;">
                                <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                            
                            </tr>

                            <tr>
                                <td><b>Impuesto 1.5 x mil Activos Totales:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">{{ number_format($patente->valor_impuesto_act,2) }}</td>
                            </tr>

                            <tr>
                                <td><b>Exoneracion:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">-{{number_format($patente->valor_exoneracion_act,2)}}</td>
                            </tr>

                            <tr>
                                <td><b>Servicios Administrativo:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">{{number_format($patente->valor_sta_act,2)}}</td>
                            </tr>


                            
                            
                            <!-- <tr style="margin-top:20px">
                                <td><b>Valor Emitido:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">{{ $patente->valor_activo_total }}</td>
                            </tr>

                            <tr style="line-height: 20px;">
                                <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                            
                            </tr> -->


                        

                            <tr>
                                <td><b>Intereses:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">{{ $patente->valor_intereses_act }}</td>
                            </tr>

                            <tr>
                                <td><b>Recargos:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">{{ $patente->valor_recargos_act }}</td>
                            </tr>

                            <tr>
                                <td><b>Total Deuda:</b> </td>
                                <td>$</td>
                                <td style="text-align:right">{{ $patente->valor_activo_total }}</td>
                            </tr>

                        </table>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">

                    </td>
                </tr>
            
            </table>

        
            <br>
            <center><b>Fecha Impresion: </b>{{ $fecha_formateada_hoy }}<br>
            
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
                            Ing. Jandry Fernando Loor Macay
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center">
                            Tesorera Municipal Juez de Coactiva
                        </td>
                        <td style="text-align: center">
                        Director Financiero (E)
                        </td>
                    </tr>
                </tbody>
            </table>


    @endif
    

    

</body>
</html>
