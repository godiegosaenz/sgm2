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

    </style>

  
</head>

<body>

  <header>
    <table class="ltable " width="112.5%"  >                
            <tr>
                <td height="50px" colspan="3" style="border: 0px;" align="left" >
                    <img src="logo.png" width="100px" height="70px">
                </td>
                <td height="60px" colspan="3" style="border: 0px;" align="center" style="font-size:11px" ></td>
               
            </tr> 

            <!-- <tr>
                <td height="50px" colspan="3" style="border: 0px;" align="left" >
                    
                </td>
                <td height="60px" colspan="3" style="border: 0px;" align="center" style="font-size:11px" ><b>PARTE DIARIO DE RECAUDACION - TESORERIA MUNICIPAL CANTON <br>SAN VICENTE DEL DIA {{$fecha}}</b></td>
               
            </tr> 
             -->
            
        </table>
  </header>

 
    <div style="margin-bottom:10px; margin-top:0px;">

        <!-- <table class="ltable" style="" border="0" width="100%" style="padding-bottom:2px !important">
          
            <tr style="font-size: 10px"  class="fuenteSubtitulo " style=""> 
                <th colspan="11" style="border-color:white;height:35px;text-align: center;border:0 px" width="100%"  >  
                    PARTE DIARIO DE RECAUDACION - TESORERIA MUNICIPAL CANTON SAN VICENTE <br>DEL DIA {{$fecha}}</b></span>                             
                </th>
            
            </tr>

        </table> -->
        <div style="margin-top:2px;">
            <center><span style="font-size:12px; text-align:center"><b>PARTE DIARIO DE RECAUDACION - TESORERIA MUNICIPAL <br>CANTON SAN VICENTE DEL DIA {{$fecha}} </b></span></center>                         
            <table class="ltable"  border="0" width="100%" style="padding-bottom:2px !important; margin-top:5px">
              
                
                <!-- <tr style="font-size: 10px !important; background-color: #D3D3D3;line-height:15px; "> 
                    
                    <th width="15%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">CODIGO</th>

                    <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">DETALLE</th>

                    <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">ANTERIORES </th>

                    <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">AÑO 2025</th>

                    <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTALES</th>

                                     
                </tr> -->

                <tbody>
                    
                    @if(isset($datos))

                        @php
                            $total=0;
                            $total_final=0;
                            $total_anio_actual=0;
                            $total_anio_anteriores=0;
                        @endphp
                     
                        @foreach($datos as $e=>$dato)

                            @php
                                $total_tipo=0;
                                $total_anio_actual_tipo=0;
                                $total_anio_anteriores_tipo=0;
                            @endphp
                           
                            <tr style="font-size: 9px !important; background-color: #D3D3D3;line-height:15px; "> 
                    
                                <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">CODIGO</th>

                                <th width="40%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:15px">DETALLE</th>

                                <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">ANTERIORES </th>

                                <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">AÑO 2025</th>

                                <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">TOTALES</th>

                                                
                            </tr>

                            <tr style="font-size: 9px !important;line-height:15px; "> 
                                <td align="center" style="border-top: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                        
                                </td>
                                <th align="left" colspan="1" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                    {{$e}}
                                </th>
                                <th></th>
                                <th></th>
                                <td align="center" style="border-top: 0px; border-bottom: 0px;border-center:0px;border-left:0px;border-color: #D3D3D3">
                                        
                                </td>
                                
                            </tr>


                            @foreach($dato as $info)
                                @php
                                    $total_parcial=0;
                                    $total_parcial=$info->total_pago_anteriores + $info->total_pago_anio_actual;
                                @endphp
                                <tr style="font-size: 9px !important;line-height:15px; "> 

                                    <td align="center" style="border-top: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                        {{$info->codigo}}
                                    </td>


                                    <td align="left" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                        {{$info->detalle_imp}}

                                    </td>

                                    <td align="right" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                       
                                        {{number_format($info->total_pago_anteriores,2)}}
                                    </td>


                                    <td align="right" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                        
                                        {{number_format($info->total_pago_anio_actual,2)}}
                                    </td>

                                    <td align="right" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-color: #D3D3D3">
                                        {{number_format($total_parcial,2)}}
                                    </td>

                                </tr>

                                @php
                               
                                    $total=$info->total_pago_anteriores+ $info->total_pago_anio_actual;
                                    $total_anio_anteriores_tipo=$total_anio_anteriores_tipo + $info->total_pago_anteriores;
                                    $total_anio_actual_tipo=$total_anio_actual_tipo + $info->total_pago_anio_actual;
                                    $total_tipo=$total_tipo + $total_parcial;

                                @endphp
                            @endforeach

                            <tfoot >
                                <tr style="font-size:10px !important;line-height:25px" style="">
                                    <td  colspan="3"style=" border-top: 0px; border-bottom: 0px;border-right:0px; font-size:10px; border-color: #D3D3D3;  text-align: right;">
                                        <b>{{number_format($total_anio_anteriores_tipo,2)}}</b> 
                                    </td>
                                    <td style="border-top: 0px; border-bottom: 0px;border-left:0px;border-right:0px; border-color: #D3D3D3;  text-align: right; font-size:10px">
                                        <b>{{number_format($total_anio_actual_tipo,2)}}</b> 
                                    
                                    </td>
                                    <td style="border-top: 0px; border-bottom: 0px;border-left:0px;border-color: #D3D3D3;  text-align: right; font-size:10px">
                                        <b>{{number_format($total_tipo,2)}}</b> 
                                    
                                    </td>
                                
                                </tr>

                                <tr style="font-size:5px !important;line-height:15px" style="">
                                    <td  colspan="3"style=" border-bottom: 0px;border-right:0px;border-left:0px; font-size:10px; border-color: #D3D3D3;  text-align: right;">
                                        <b style="color:white">xxx</b> 
                                    </td>
                                    <td style=" border-bottom: 0px;border-left:0px;border-right:0px; border-color: #D3D3D3;  text-align: right; font-size:10px">
                                        <b></b> 
                                    
                                    </td>
                                    <td style="border-bottom: 0px;border-left:0px;border-right:0px;border-color: #D3D3D3;  text-align: right; font-size:10px">
                                        <b></b> 
                                    
                                    </td>
                                
                                </tr>

                              
                            </tfoot>

                            @php
                                $total_anio_anteriores=$total_anio_anteriores + $total_anio_anteriores_tipo;
                                $total_anio_actual=$total_anio_actual + $total_anio_actual_tipo;
                                
                            @endphp

                        @endforeach
                    @endif

                    @php 
                        $total_final=$total_anio_anteriores +$total_anio_actual;
                      
                    @endphp

                    <tfoot >
                        <tr style="font-size:10px !important;line-height:25px" style="">
                            <td  colspan="2"style="font-size:10px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                                <b>TOTAL RECAUDADO</b> 
                            </td>
                            <td  colspan="1"style="font-size:10px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                                <b>{{number_format($total_anio_anteriores,2)}}</b> 
                            </td>
                            <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:10px">
                                <b>{{number_format($total_anio_actual,2)}}</b> 
                            
                            </td>
                            <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:10px">
                                <b>{{number_format($total_final,2)}}</b> 
                            
                            </td>
                        
                        </tr>

                        
                    </tfoot>
                </tbody>
               
            </table>
            
           
        </div>

        

       
        
        {{-- $font = $fontMetrics->get_font("Arial, Helvetica, sans-serif", "normal");
        $pdf->text(490, 820, "Página $PAGE_NUM de $PAGE_COUNT", $font, 9); --}}
       
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