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
    @php
        $sumatotal = 0;
    @endphp
    @foreach ($DatosLiquidacion as $d)

        @php
            $anio=explode("-",$d[0]->CarVe_NumTitulo);
        @endphp
   
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
                    <b>TITULO DE CREDITO - PREDIO RURAL #{{$d[0]->CarVe_NumTitulo}} </b><br>
                    San Vicente, 01 Enero {{$anio[0]+1}}
                    
                </td>
            </tr>
            <tr style="line-height: 20px;">
                <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">
                </td>
            </tr>
            <tr style="font-size: 11px;">
                <td><b>Contribuyente:</b> {{$d[0]->Ciu_Apellidos}} {{$d[0]->Ciu_Nombres}}</td>
            
                <td style="width: 1%; text-align:left"></td>

                <td><b>Clave Catastral:</b> {{$d[0]->Pre_CodigoCatastral}}</td>
                
            
            </tr>
            <tr style="font-size: 11px;">
                <td><b>Ruc/CC:</b> {{$d[0]->CarVe_CI}}</td>
            
                <td></td>
                <td><b>Sitio Barrio:</b> </td>
                
            </tr>
            <tr style="font-size: 11px;">
                <td><b>Direccion Domicilio:</b> {{$d[0]->Pro_DireccionDomicilio}}</td>
            
                <td></td>
                <td><b>Nombre del predio:</b> {{$d[0]->Pre_NombrePredio}}</td>
                
            </tr>
        </table>
        
        <table style="border-collapse: collapse; width: 100%;">
            <tr style="font-size: 11px;">
                <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;width:45%">
                    <table style="width:100%">
                        <tr>
                            <td colspan="3" style="text-align: center;"><b>AVALUO</b></td>
                            
                        </tr>

                        <tr>
                            <td><b>Terreno</b></td>
                            <td>$</td>
                            
                            <td style="text-align:right">{{number_format($d[0]->CarVe_ValTotalTerrPredio,2)}}</td>
                        </tr>

                        <tr>
                            <td><b>Construccion</b></td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_ValTotalEdifPredio,2)}}</td>
                        </tr>

                        <tr>
                            <td><b>Otras Inversiones</b></td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_ValOtrasInver,2)}}</td>
                        </tr>
                        
                        <tr style="line-height: 20px;">
                            <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                        
                        </tr>

                        <tr>
                            <td><b>Valor de la Propiedad</b></td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_ValComerPredio,2)}}</td>
                        </tr>


                        <tr>
                            <td><b>Rebaja Hipotecaria</b></td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_RebajaHipotec,2)}}</td>
                        </tr>

                        <tr>
                            <td><b>Base Imponible</b></td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_BaseImponible,2)}}</td>
                        </tr>
                    </table>
                
                </td>
                
                <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px">
                    <table style="width:100%">
                        <tr>
                            <td><b>RUBROS/CONCEPTO</b></td>
                            <td></td>
                            <td><b>VALORES</b></td>
                        </tr>

                        <tr>
                            <td><b>Impuesto Predial Rural:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_IPU,2)}}</td>
                        </tr>

                        <tr>
                            <td><b>Servicios Administrativo:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_TasaAdministrativa,2)}}</td>
                        </tr>


                        <tr>
                            <td><b>Bomberos:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_Bomberos,2)}}</td>
                        </tr>
                        
                        <tr style="margin-top:20px">
                            <td><b>Valor Emitido:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->CarVe_ValorEmitido,2)}}</td>
                        </tr>

                        <tr style="line-height: 20px;">
                            <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                        
                        </tr>


                    

                        <tr>
                            <td><b>Fecha desde la cual se desvenga intereses:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">Enero {{$anio[0]+1}}</td>
                        </tr>

                        <tr>
                            <td><b>Intereses hasta la fecha de emision:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->intereses,2)}}</td>
                        </tr>

                        <tr>
                            <td><b>Total Deuda:</b> </td>
                            <td>$</td>
                            <td style="text-align:right">{{number_format($d[0]->total_pagar,2)}}</td>
                        </tr>

                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">

                </td>
            </tr>
        
        </table>

        </table>
        <center><b>Fecha Impresion:</b> {{ $fecha_formateada }}<br>
        <b>Ley COA Aticulo 268</b></center>
        <br>
        <br>
        <br>
        <br>
        <br>
        <hr style="border:Dotted;"/>
        <br>
        <br>

        @php
            $sumatotal = $sumatotal + $d[0]->total_pagar;
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
