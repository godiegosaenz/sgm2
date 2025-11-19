<!DOCTYPE html>
<html>
<head>
  <title></title>

     <style type="text/css">
        @page {
            margin-top: 8em;
            margin-left: 3em;
            margin-right:3em;
            margin-bottom: 5em;
        }
        header { position: fixed;  top: -100px; left: 0px; right: 0px; background-color: white; height: 60px; margin-right: 99px}
       
       

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
            font-size: 10px;

        }
        .encabezado1{
            text-align: center;
            padding-top: 1px;
            font-size: 10px;
        }
        .encabezado2{
            text-align: center;
            padding-top: 10px;
            padding-bottom: 10px;
            font-size: 10px;
        }

    </style>

  
</head>

<body>

  <header>
    <table class="ltable " width="112.5%"  >                
            <tr>
                <td height="50px" colspan="3" style="border: 0px;" align="left" >
                    <img src="logo1.jpg" width="300px" height="80px">
                </td>
                <td height="60px" colspan="2" style="border: 0px;" align="center" ></td>
               
            </tr>             
        </table>
  </header>

    @php
        $txt = match($tipo) {
            'IPA' => 'IMPUESTO PREDIAL URBANO',
            'D' => 'DESCUENTOS',
            'R' => 'RECARGOS (MULTA TRIBUTARIA)',
            'I' => 'INTERES POR MORA TRIBUTARIA',
            default => ''
        };
    @endphp

    <div style="margin-bottom:30px; margin-top:12px;">

        <table class="ltable" style="" border="0" width="100%" style="padding-bottom:2px !important">
          
            <tr style="font-size: 12px"  class="fuenteSubtitulo " style=""> 
                <th colspan="11" style="border-color:white;height:35px;text-align: center;border:0 px" width="100%"  >  
                    {{$txt}}
                </th>
            
            </tr>

            <tr style="font-size: 10px"  class="fuenteSubtitulo " style=""> 
                <td style="border-color:white;height:5px;text-align: left;border:0 px" width="50% !important"  >  
                    <b>DESDE:</b> {{$desde}} 
               
                </td>

                <td style="border-color:white;height:5px;text-align: left;border:0 px" width="50% !important"  >  

                    <b>HASTA:</b> {{$hasta}}
                </td>
            
            </tr>

           
        </table>
        <div style="margin-top:12px;">

            <table class="ltable"  border="0" width="100%" style="padding-bottom:2px !important">
              
                
                <tr style="font-size: 10px !important; background-color: #D3D3D3;line-height:10px; "> 
                    
                    <th width="15%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:10px">PREDIO</th>

                    <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center; line-height:10px">AÑO</th>

                    <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">COBRADOR </th>

                    <th width="20%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">FECHA COBRO</th>

                    <th width="10%" style="border: 0px; ;border-color: #D3D3D3; text-align: center">VALOR</th>

                                     
                </tr>

                <tbody>
                    
                    @if(isset($datos))

                        @php
                            $total=0;
                            $total_anio_actual=0;
                            $total_anio_anteriores=0;
                        @endphp
                     
                        @foreach($datos['resultado'] as $e=>$dato)
                            <tr style="font-size: 10px !important;line-height:10px; "> 

                                <td align="center" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                    {{$dato->num_predio}}
                                </td>


                                <td align="left" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                    {{$dato->anio_predio}}
                                </td>
                        
                                <td align="center" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                    {{$dato->usuario}}
                                </td>

                               
                                <td align="center" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                    {{$dato->fecha_pago}} 
                                </td>

                                <td align="center" style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-color: #D3D3D3">
                                    {{$dato->valor}}
                                </td>

                               
                               
                            </tr>

                            @php
                                if(intval($datos['anio_actual'])===$dato->anio_predio){
                                    $total_anio_actual=$total_anio_actual+ $dato->valor;
                                }else{
                                    $total_anio_anteriores=$total_anio_anteriores+ $dato->valor;
                                }
                                $total=$total + $dato->valor;

                            @endphp

                        @endforeach
                    @endif
                </tbody>
                <tfoot >
                    <tr style="font-size:10px !important;line-height:15px" style="">
                        <td  colspan="4"style="font-size:9px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                            <b>ANTERIORES</b>
                        </td>
                        <td style="border: 0px;border-color: #D3D3D3;  text-align: center; font-size:9px">
                           {{number_format($total_anio_anteriores,2)}} 
                           
                        </td>
                      
                    </tr>

                    <tr style="font-size:10px !important;line-height:5px" style="">
                        <td  colspan="4"style="font-size:9px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                            <b>AÑO {{$datos['anio_actual']}}</b>
                        </td>
                        <td style="border: 0px;border-color: #D3D3D3;  text-align: center; font-size:9px">
                           {{number_format($total_anio_actual,2)}} 
                           
                        </td>
                      
                    </tr>

                    <tr style="font-size:10px !important;line-height:5px" style="">
                        <td  colspan="4"style="font-size:9px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                            <b>TOTAL</b>
                        </td>
                        <td style="border: 0px;border-color: #D3D3D3;  text-align: center; font-size:9px">
                           {{number_format($total,2)}} 
                           
                        </td>
                      
                    </tr>

                </tfoot>
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