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
    <table class="ltable " width="117.5%"  >                
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

            @if(isset($resultados_final) && sizeof($resultados_final)>0 ) 
                <div style="margin-top: 12px; margin-bottom: 32px;">
                    <center>
                        <span style="font-size:14px; text-align:center"><b>Analitica de Predios Urbano y Rural por Rango de Avaluo</b></span>
                    </center> 
                    
                    <table class="ltable"  border="0" width="100%" s>
                        <tr>
                            <td colspan="6" style="text-align: right;">
                                <div style="color: red; font-weight: bold; font-size: 9px;">ALTO</div>
                            </td>
                            <td colspan="1"  style="text-align: right;">
                                <div style="color: white; font-weight: bold; font-size: 9px;"></div>
                            </td>
                            <td colspan="6" style="text-align: left;">
                                <div style="color: green; font-weight: bold; font-size: 9px;">BAJO</div>
                            </td>
                        </tr>
                    </table>
                        
                    <table class="ltable"  border="0" width="100%" style="padding-bottom:2px !important; margin-top:5px; margin-bottom: 30px !importan;">
                                    
                        <tr style="font-size: 10px !important; background-color: #D3D3D3;line-height:15px; "> 
                            
                            <th width="5%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">#</th>

                            <th width="15%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">RANGO $</th>

                            <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTAL URBANO </th>

                            <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTAL RURAL </th>

                            <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTAL  </th>
                    
                        </tr>

                        <tbody>      
                            @php
                                $sorted = collect($resultados_final)->sortByDesc('cantidad_total')->values();
                                $altas = $sorted->take(1)->pluck('cantidad_total')->toArray();
                                $bajas = $sorted->slice(-1)->pluck('cantidad_total')->toArray();
                            @endphp            
                            
                            @if(isset($resultados_final))    
                                @foreach ($resultados_final as $key=> $data)
                                    @php
                                        $claseFila = '';
                                        if (in_array($data['cantidad_total'], $altas)) $claseFila = 'fila-alta';
                                        elseif (in_array($data['cantidad_total'], $bajas)) $claseFila = 'fila-baja';
                                    @endphp
                                    <tr class="{{ $claseFila }}" style="font-size: 9px !important;line-height:15px; "> 

                                        <td align="center" style=" border-center:0px;border-color: #D3D3D3">
                                            {{ $key+1 }}
                                        </td>

                                        <td align="center" style="border-center:0px;border-color: #D3D3D3">
                                            {{ $data['rango'] }}
                                        </td>

                                        <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                            {{ $data['cantidad_urbano'] }}
                                            
                                        </td>

                                        <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                            {{ $data['cantidad_rural'] }}
                                            
                                        </td>

                                        <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                            {{ $data['cantidad_total'] }}
                                            
                                        </td>

                                    </tr>
                                @endforeach    
                                    
                            @endif                          

                        </tbody>
                    
                    </table>
                </div>
            @endif 

            @if(isset($datos_urbano) && sizeof($datos_urbano)>0) 
                <div style="margin-top: 12px; margin-bottom: 32px;">
                    <center>
                        <span style="font-size:14px; text-align:center"><b>Analitica de Predios Urbano por Rango de Avaluo</b></span>
                    </center> 

                    <table class="ltable"  border="0" width="100%" s>
                        <tr>
                            <td colspan="6" style="text-align: right;">
                                <div style="color: red; font-weight: bold; font-size: 9px;">ALTO</div>
                            </td>
                            <td colspan="1"  style="text-align: right;">
                                <div style="color: white; font-weight: bold; font-size: 9px;"></div>
                            </td>
                            <td colspan="6" style="text-align: left;">
                                <div style="color: green; font-weight: bold; font-size: 9px;">BAJO</div>
                            </td>
                        </tr>
                    </table>
                        
                    <table class="ltable"  border="0" width="100%" style="padding-bottom:2px !important; margin-top:5px; margin-bottom: 30px !importan;">
                                    
                        <tr style="font-size: 10px !important; background-color: #D3D3D3;line-height:15px; "> 
                            
                            <th width="5%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">#</th>

                            <th width="15%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">RANGO $</th>

                            <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTAL </th>
                    
                        </tr>

                        <tbody>   
                            
                        @php
                            $sorted = collect($datos_urbano)->sortByDesc('cantidad')->values();
                            $altas = $sorted->take(1)->pluck('cantidad')->toArray();
                            $bajas = $sorted->slice(-1)->pluck('cantidad')->toArray();
                        @endphp
                            
                            @if(isset($datos_urbano))    
                                @foreach ($datos_urbano as $key=> $data)

                                @php
                                    $claseFila = '';
                                    if (in_array($data['cantidad'], $altas)) $claseFila = 'fila-alta';
                                    elseif (in_array($data['cantidad'], $bajas)) $claseFila = 'fila-baja';
                                @endphp

                                <tr class="{{ $claseFila }}" style="font-size: 9px !important;line-height:15px; "> 

                                    <td align="center" style=" border-center:0px;border-color: #D3D3D3">
                                        {{ $key+1 }}
                                    </td>

                                    <td align="center" style="border-center:0px;border-color: #D3D3D3">
                                        {{ $data['rango'] }}
                                    </td>

                                    <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                        {{ $data['cantidad'] }}
                                        
                                    </td>

                                </tr>
                                @endforeach    
                                    
                            @endif                          

                        </tbody>
                    
                    </table>
                </div>

            @endif   
            
            @if(isset($datos_rural) && sizeof($datos_rural)>0)
                <div style="margin-top: 12px; margin-bottom: 32px;">
                    <center>
                        <span style="font-size:14px; text-align:center"><b>Analitica de Predios Rurales por Rango de Avaluo</b></span>
                    </center> 

                    <table class="ltable"  border="0" width="100%" s>
                        <tr>
                            <td colspan="6" style="text-align: right;">
                                <div style="color: red; font-weight: bold; font-size: 9px;">ALTO</div>
                            </td>
                            <td colspan="1"  style="text-align: right;">
                                <div style="color: white; font-weight: bold; font-size: 9px;"></div>
                            </td>
                            <td colspan="6" style="text-align: left;">
                                <div style="color: green; font-weight: bold; font-size: 9px;">BAJO</div>
                            </td>
                        </tr>
                    </table>
                        
                    <table class="ltable"  border="0" width="100%" style="padding-bottom:2px !important; margin-top:5px;">
                                    
                        <tr style="font-size: 10px !important; background-color: #D3D3D3;line-height:15px; "> 
                            
                            <th width="5%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">#</th>

                            <th width="15%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">RANGO $</th>

                            <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTAL </th>
                    
                        </tr>

                        <tbody>   
                            
                            @php
                                $sorted = collect($datos_rural)->sortByDesc('cantidad')->values();
                                $altas = $sorted->take(1)->pluck('cantidad')->toArray();
                                $bajas = $sorted->slice(-1)->pluck('cantidad')->toArray();
                            @endphp
                                
                            @if(isset($datos_rural))    
                                @foreach ($datos_rural as $key=> $data)

                                    @php
                                        $claseFila = '';
                                        if (in_array($data['cantidad'], $altas)) $claseFila = 'fila-alta';
                                        elseif (in_array($data['cantidad'], $bajas)) $claseFila = 'fila-baja';
                                    @endphp

                                    <tr class="{{ $claseFila }}" style="font-size: 9px !important;line-height:15px; "> 

                                        <td align="center" style=" border-center:0px;border-color: #D3D3D3">
                                            {{ $key+1 }}
                                        </td>

                                        <td align="center" style="border-center:0px;border-color: #D3D3D3">
                                            {{ $data['rango'] }}
                                        </td>

                                        <td align="right" style="border-left: 0px; border-center:0px;border-color: #D3D3D3">
                                            {{ $data['cantidad'] }}
                                            
                                        </td>

                                    </tr>
                                @endforeach    
                                    
                            @endif                          

                        </tbody>
                    
                    </table>
                </div>
                
            @endif
           
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