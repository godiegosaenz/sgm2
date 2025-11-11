<!DOCTYPE html>
<html>
<head>
  <title></title>

     <style type="text/css">
        @page {
            margin-top: 7em;
            margin-left: 3em;
            margin-right:3em;
            margin-bottom: 4em;
        }
        header { position: fixed;  top: -100px; left: 0px; right: 0px; background-color: white; height: 40px; margin-right: 99px}
       
        /* header {
        position: fixed;
        top: 0px;
        left: 0px;
        right: 0px;
        height: 70px;
        background-color: white;
    } */


        .ltable
        {
            border-collapse: collapse;
            font-family: sans-serif;
        }
        td, th /* Asigna un borde a las etiquetas td Y th */
        {
            border: 1px solid white;
        }

        .sinbordeencabezado /* Asigna un borde a las etiquetas td Y th */
        {
            border: 0px solid black;
        }
        .fuenteSubtitulo{
            font-size: 12px;
        }
        .pad{
            padding-left:5px;
            padding-right:5px;
        }
        p{
            line-height: 1px;
        }

        
     </style>
      <style type="text/css">
        .preview_firma{
            width: 156px;
            border: solid 1px #000;
        }
        .img_firma{
            width: 80px;
        }
        .btn_azul{
            color: #fff;
            background-color: #337ab7;
            border-color: #2e6da4;
        }

       
        .hr{
        page-break-after: always;
        border: none;
        margin: 0;
        padding: 0;
        }
        .encabezado{
            /*border: 1px solid;*/
            text-align: center;
            font-size: 9px;

        }
        .encabezado1{
            text-align: center;
            padding-top: 1px;
            font-size: 9px;
        }
        .encabezado2{
            text-align: center;
            padding-top: 10px;
            padding-bottom: 10px;
            font-size: 9px;
        }


        .fila-alta {
            background-color: #ffcccc !important; /* rojo suave */
        }

        .fila-baja {
            background-color: #ccffcc !important; /* verde suave */
        }

    </style>

  
</head>

<body>

  <header>
    <table class="ltable " width="113.5%"  >                
            <tr>
                <td height="50px" colspan="3" style="border: 0px;" align="left" >
                    <img src="logo4.png" width="100px" height="70px">
                </td>
              
                <td height="60px" colspan="3" style="border: 0px;" align="center" style="font-size:11px" >
                    <!-- <img src="logo4.png" width="100px" height="70px"> -->
                </td>

                <td height="60px" colspan="6" style="border: 0px;" align="right" style="font-size:11px" >
                    <img src="esvida.png" width="150px" height="70px">
                </td>
            </tr> 

           
            
        </table>
  </header>

 
    <div style="margin-bottom:10px; margin-top:0px;">

        <div style="margin-top:2px;">

            
            <b style="margin-top: 12px; margin-bottom: 32px;">
                <center>
                    <span style="font-size:14px; text-align:center"><b>Pagos Transito Municipal</b><br></span>
                 
                </center> 
                <center> 
                    <table class="ltable"  border="0" width="100%" >
                        <tr style="font-size: 9px;">
                            <td width="10%"></td>

                            <td width="40%" style="text-align: left;">
                            <b>RESPONSABLE: </b>{{ auth()->user()->persona->nombres }} {{ auth()->user()->persona->apellidos }}
                            </td>
                            <td width="10%"></td>

                            <td width="40%" style="text-align: left;">
                                <b>FECHA DE IMPRESION:</b> {{ date('d/m/Y H:i:s') }}
                            </td>
                            
                        </tr>
                        <tr style="font-size: 9px;">
                            <td width="10%"></td>

                            <td width="40%" style="text-align: left;">
                                <b>DESDE:</b> {{date('d-m-Y', strtotime($desde))}}
                            </td>
                            <td width="10%"></td>

                            <td width="40%" style="text-align: left;">
                                <b>HASTA:</b> {{date('d-m-Y', strtotime($hasta))}}
                            </td>
                        </tr>
                    </table>
                </center>   
                
                <table class="ltable"  border="0" width="100%" style="padding-bottom:2px !important; margin-top:5px; margin-bottom: 30px !importan;">
                                
                    <tr style="font-size: 9px !important; background-color: #D3D3D3;line-height:10px; "> 
                        
                        <th width="2%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">#</th>

                        <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">Cedula</th>

                        <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">Cliente</th>

                        <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">Num Titulo</th>

                        <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">Placa/CPN/RAMV</th>

                        <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">Clase</th>

                        <th width="30%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">Cuadro Tarifario RTV</th>

                        

                       


                        <th width="5%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">Total  </th>
                
                    </tr>

                    <tbody>      
                       
                        @if(isset($datos))  
                            @php
                                $impuesto_anual=0;
                                $duplicado_matricula=0;
                                $revision_tecnica=0;
                                $sticker_revision_vehicular=0;
                                $tsa=0;
                                $total_final=0;
                            @endphp
                            @foreach ($datos as $key=> $item)
                               
                                <tr style="font-size: 9px !important;line-height:15px; "> 

                                    <td align="center" style=" border-center:0px;border-color: #D3D3D3">
                                        {{ $key+1 }}
                                    </td>

                                    <td align="left" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                          {{$item->identificacion_propietario}}
                                        
                                        
                                    </td>

                                    <td align="left" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                       {{$item->nombre_propietario}} {{$item->apellido_propietario}}
                                        
                                        
                                    </td>

                                     <td align="left" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                       {{$item->numero_titulo}}
                                        
                                        
                                    </td>

                                    <td align="left" style="border-center:0px;border-color: #D3D3D3">
                                        <li style="list-style: none;">{{$item->placa_cpn_ramv}}
                                    </td>

                                     <td align="left" style="border-center:0px;border-color: #D3D3D3">
                                        <li style="list-style: none;">{{$item->clase_desc}}
                                    </td>

                                  
                                    <td align="left" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{$item->clase}}

                                    </td>

                                   

                                    <!-- <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{ $item->conceptos[0]->valor ?? '' }}
                                        
                                    </td>

                                    <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{ $item->conceptos[1]->valor ?? '' }}
                                        
                                    </td>
                                    <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{ $item->conceptos[2]->valor ?? '' }}
                                        
                                    </td>
                                    <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{ $item->conceptos[3]->valor ?? '' }}
                                        
                                    </td>
                                    <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{ $item->conceptos[4]->valor ?? '' }}
                                        
                                    </td> -->
                                    <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{$item->total_pagar}}
                                        
                                    </td>

                                </tr>
                                @php
                                    //$impuesto_anual=$impuesto_anual + $item->conceptos[0]->valor ?? '';
                                    if (isset($item->conceptos[0])) {
                                        $impuesto_anual += floatval($item->conceptos[0]->valor);
                                    }
                                    if (isset($item->conceptos[1])) {
                                        $duplicado_matricula += floatval($item->conceptos[1]->valor);
                                    }
                                    if (isset($item->conceptos[2])) {
                                        $revision_tecnica += floatval($item->conceptos[2]->valor);
                                    }
                                    if (isset($item->conceptos[3])) {
                                        $sticker_revision_vehicular += floatval($item->conceptos[3]->valor);
                                    }
                                    if (isset($item->conceptos[4])) {
                                        $tsa += floatval($item->conceptos[4]->valor);
                                    }
                                    $total_final += floatval($item->total_pagar);
                                @endphp
                            @endforeach    
                                
                        @endif                          

                    </tbody>

                    <tfoot >
                    <tr style="font-size:10px !important;line-height:5px" style="">

                        <td  colspan="7"style="font-size:9px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                            <b>TOTAL</b>
                        </td>

                        <!-- <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:9px">
                           {{number_format($impuesto_anual,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:9px">
                           {{number_format($duplicado_matricula,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:9px">
                           {{number_format($revision_tecnica,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:9px">
                           {{number_format($sticker_revision_vehicular,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:9px">
                           {{number_format($tsa,2)}}                            
                        </td>
                         -->
                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:9px">
                           {{number_format($total_final,2)}}                            
                        </td>
                        
                      
                    </tr>

                </tfoot>
                
                </table>
            </div>
            
           
        </div>

        
    </div>

   
  <script type="text/php">
    if ( isset($pdf) ) {
        $pdf->page_script('
            $font = $fontMetrics->get_font("Arial, Helvetica, sans-serif", "normal");
            $pdf->text(490, 820, "Página $PAGE_NUM de $PAGE_COUNT", $font, 9); 
        ');
    }
</script>
</body>
</html>