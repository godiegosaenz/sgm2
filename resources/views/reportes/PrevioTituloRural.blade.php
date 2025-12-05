<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Titulo de Crédito</title>
    <style>
        @page {
            margin-top: 3mm;  /* <-- antes estaba 10mm */
            margin-right: 10mm;
            margin-bottom: 10mm;
            margin-left: 10mm;
        }
        body {
            font-family: Arial, sans-serif;
            font-size: 12px;
        }

        /* Imagen de fondo en la mitad superior */
        .watermark {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 50%; /* Solo ocupa la mitad superior */
            background-image: url('{{ asset('img/novalido.png') }}');
            background-repeat: no-repeat;
            background-size: contain;
            background-position: center;
            opacity: 0.08; /* Opacidad muy baja */
            z-index: -1; /* La imagen queda detrás del contenido */
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
            font-size: 11px;
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
        $tamanio=sizeof($liquidacionRural);
        $menos1=$tamanio -1;
        $es_copia=$copia;
        $color='white';
        if($es_copia=="copia"){
            $color='black';
        }
    @endphp
    
    @foreach ($liquidacionRural as $key=>$data )
        <div class="watermark"></div>
        <table class="cabecera" style="font-size:10px !important"  width="100%" >
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="60">
                </td>
                <td class="encabezado">
                
                    <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <br> DIRECCION FINANCIERA 
                    </h3>
                    

                </td>
                    <td class="logo">
                </td>
            </tr>
        </table>
        
        <table width="100%" border="0" >
            <tr>
                <td colspan="1" style="text-align: left;">
                    <b>IMPUESTO PREDIAL RURAL</b>
                </td>

            
                <td colspan="2" style="text-align: right;">
                    <b>TITULO DE CREDITO N° {{ $data->num_titulo }}</b>
                </td>
            </tr>
            <tr style="line-height: 20px;">
                <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">
                </td>
            </tr>
            <tr style="font-size: 11px; line-height: 20px;">
                <td><b>Contribuyente:</b> {{ $data->nombres }}</td>
            
                <td style="width: 1%; text-align:left"></td>

                <td><b>Clave Catastral:</b> {{ $data->clave }}</td>
                
            
            </tr>
            <tr style="font-size: 11px;">
                <td><b>Ruc/CC:</b> {{ $data->num_ident }}</td>
            
                <td></td>
                <td><b>Sitio Barrio:</b> {{ $data->direccion }} </td>
                
            </tr>
            <tr style="font-size: 11px;">
                <td><b>Direccion Domicilio:</b> {{ $data->direccion }}</td>
            
                <td></td>
                <td><b>Nombre del predio:</b> {{ $data->calle }}</td>
                
            </tr>
        </table>
            
        <table style="border-collapse: collapse; width: 100%; margin-top:10px">
            <tr style="font-size: 11px; line-height: 15px;">
                <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;width:45%">
                    <table style="width:100%">
                        <tr>
                            <td colspan="3" style="text-align: center;"><b>AVALUO</b></td>
                            
                        </tr>

                        <tr>
                            <td style="text-align:right" width="60%">Terreno</td>
                            <td width="40%">$</td>
                            
                            <td style="text-align:right">{{ number_format($data->total_terreno_predio,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">Construccion</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->valorEdifPredio,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">Otras Inversiones</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->valor_otras_inv,2) }}</td>
                        </tr>
                        
                        <tr style="line-height: 20px;">
                            <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                        
                        </tr>

                        <tr>
                            <td style="text-align:right">Valor de la Propiedad</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->valor_comer_predio,2) }}</td>
                        </tr>


                        <tr>
                            <td style="text-align:right">Rebaja Hipotecaria</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->valor_rebaja,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">Base Imponible</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->base_imp,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">Fecha de Emision:</td>
                            <td style="color:white"></td>
                            <td style="text-align:left">{{ $data->fecha_emi }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">Fecha de Consulta:</td>
                            <td style="color:white"></td>
                            <td style="text-align:left">{{ date('d/m/Y')}}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right; color:  ">&nbsp;</td>
                            <td style="color:red">&nbsp;</td>
                            <td style="text-align:left; color:white">&nbsp;</td>
                        </tr>

                        <tr>
                            <td colspan="3" style="font-size:16px;text:align:center; color:{{ $color }}"><b>&nbsp;</b></td>
                        </tr>
                        

                    </table>
                
                </td>
                
                <td style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px">
                    <table style="width:100%">
                        <tr>
                            <td style="text-align:center"><b>RUBROS</b></td>
                            <td></td>
                            <td><b>VALORES</b></td>
                        </tr>

                        <tr>
                            <td style="text-align:right" width="60%">Impuesto Predial Rural: </td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->ipu,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">Servicios Administrativo: </td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->tasa_adm,2) }}</td>
                        </tr>


                        <tr>
                            <td style="text-align:right">Bomberos:</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->bomberos,2) }}</td>
                        </tr>
                        @if(date('Y')>2025)
                            <tr>
                                <td style="text-align:right">Seguridad Ciudadana:</td>
                                <td>$</td>
                                <td style="text-align:right">{{ number_format($data->seguridad,2) }}</td>
                            </tr>
                        @endif
                     

                        <tr style="line-height: 20px;">
                            <td colspan="3" style="border: 1px solid #000; border-left:0px; border-bottom:0px;border-right:0px"></td>
                        
                        </tr>


                        <tr>
                            <td style="text-align:right">VALOR EMITIDO: </td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->valor_emitido,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">DESCUENTO:</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->descuento,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">RECARGOS:</td>
                            <td>$</td>
                            <td style="text-align:right"> {{ number_format($data->recargo,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">INTERESES:</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->intereses,2) }}</td>
                        </tr>

                        <tr>
                            <td style="text-align:right">TOTAL A PAGAR:</td>
                            <td>$</td>
                            <td style="text-align:right">{{ number_format($data->total_pagar,2) }}</td>
                        </tr>

                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="border: 1px solid #000; border-left:0px; border-bottom:0px; border-right:0px;width:45%">

                </td>
            </tr>
        
        </table>

        
        @if ($key != $menos1)
            <div style="page-break-after: always;"></div>
        @endif
        
    
    @endforeach

</body>
</html>
