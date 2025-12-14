<!DOCTYPE html>
<html>
<head>
    <title>INFORME</title>
    <style>
         @page {
            margin-top: 10mm;  /* Ajustado */
            margin-right: 10mm;
            margin-bottom: 10mm;
            margin-left: 10mm;
        }
        table.blueTable {
        /*border: 1px solid #1C6EA4;*/
        background-color: #FFFFFF;
        width: 100%;
        text-align: left;
        border-collapse: collapse;
        }
        table.blueTable td, table.blueTable th {
        border: 1px solid #AAAAAA;
        padding: 3px 2px;
        }
        table.blueTable tbody td {
        font-size: 13px;
        }
        table.blueTable thead {
        background: #BCDCF9;
        border-bottom: 1px solid #444444;
        border-top: 1px solid #444444;
        }
        table.blueTable thead th {
        font-size: 15px;
        font-weight: bold;
        color: #262626;
        border-left: 1px solid #586168;
        }
        table.blueTable thead th:first-child {
        border-left: none;
        }

        /* table.blueTable tfoot td {
        font-size: 14px;
        }
        table.blueTable tfoot .links {
        text-align: right;
        } */
        /* table.blueTable tfoot .links a{
        display: inline-block;
        background: #1C6EA4;
        color: #FFFFFF;
        padding: 2px 8px;
        border-radius: 5px;
        } */
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
            font-size: 12px;
        
        }

        .cabecera .subtitulo {
            font-size: 10px;
            font-weight: normal;
            margin-top: 2px;
        }
    </style>

</head>
<body>
  
    <table class="cabecera" style="font-family: Arial;font-size:12px !important">
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="100">
                </td>
                <td class="encabezado">
                    <p style="font-size:16px !important"><b>GAD MUNICIPAL DEL CANTÓN SAN VICENTE<br> DIRECCION FINANCIERA TESORERIA
                   <br> SAN VICENTE - MANABI - ECUADOR</b></p>
                </td>
                 <td class="logo">
                </td>
            </tr>
        </table>
 
    <div class="" style="font-family: Arial; font-size:11;">
        <div class="">
            <center>
               
              <h3 style="margin: 0; padding: 0;">
                 Liquidación de predios {{ $ubicacion == 1 ? 'urbanos' : 'rurales' }}
            </h3>

            <h3 style="margin: 4px 0 0 0; padding: 0;">
                Fecha liquidación {{date('d/m/Y')}}
            </h3>

            </center>
        </div>
    </div>
    <div style="width:100%;margin-top:10px;margin-bottom:5px;">
      
        <table class="blueTable">
            <thead>
            <tr>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">No. Predio </th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Año</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Cod. Predial</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Contribuyente</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Emisión</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Descuento</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Interes</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Recargo</th>

            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Total</th>
            </tr>
            </thead>
            <tbody>
                @isset($DatosLiquidacion)
                    @php
                        $total_final_emi=0;
                        $total_final_des=0;
                        $total_final_int=0;
                        $total_final_rec=0;
                        $total_final_deuda=0
                    @endphp
                    @foreach ($DatosLiquidacion as $key=> $data)
            
                        @php
                            $total_emi=0;
                            $total_des=0;
                            $total_int=0;
                            $total_rec=0;
                            $total_final=0
                        @endphp
                        @foreach ($data as $item)
                        @php
                            $total=$item->subtotal_emi+$item->intereses+$item->recargo;
                            $total=$total- $item->descuento;
                            $anio=explode("-",$item->num_titulo);

                            $num_matricula="";
                            if($ubicacion==1){
                                $num_matricula = $item->num_predio;
                            }
                        @endphp
                        <tr>
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$num_matricula}}</td>
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$anio[0]}}</td>
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$item->clave}}</td>
                        
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$item->nombre_per}}</td>
                            
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$item->subtotal_emi}}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{ number_format($item->descuento ?? 0.00, 2) }}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{number_format($item->intereses ?? 0.00, 2) }}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{ number_format($item->recargo ?? 0.00, 2) }}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{number_format($total,2)}}    </td>
                            </tr>
                            @php
                                $total_emi=$total_emi +$item->subtotal_emi;
                                $total_des=$total_des +$item->descuento;
                                $total_int=$total_int +$item->intereses;
                                $total_rec=$total_rec +$item->recargo;

                            @endphp
                        @endforeach
                        <tfoot>
                            @php
                                $total_final= $total_emi + $total_int + $total_rec;
                                $total_final=$total_final -$total_des;

                            @endphp
                            <tr style="font-size:13px !important;line-height:5px" style="">

                                <td  colspan="4"style="font-size:13px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                                    <b></b>
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:13px">
                                    <b>$ {{number_format($total_emi,2)}}</b>                            
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:13px">
                                    <b>$ {{number_format($total_des,2)}}</b>                            
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:13px">
                                    <b>$ {{number_format($total_int,2)}}</b>                            
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black; text-align: right; font-size:13px">
                                    <b>$ {{number_format($total_rec,2)}}</b>                            
                                </td>
                            
                            
                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:13px">
                                    <b>$ {{number_format($total_final,2)}}</b>                            
                                </td>
                                
                            
                            </tr>
                            <tr>
                                <td colspan="9" style="height:10px;border:0;"></td>
                            </tr>
                             @php
                                $total_final_emi=$total_final_emi + $total_emi;
                                $total_final_des=$total_final_des+$total_des;
                                $total_final_int=$total_final_int+$total_int;
                                $total_final_rec=$total_final_rec+$total_rec;
                                $total_final_deuda=$total_final_deuda+$total_final;

                            @endphp
                        </tfoot>
                
                    @endforeach
                @endisset
            </tbody>
            <tfoot>
                 <tr style="font-size:13px !important;line-height:5px" style="">

                    <td  colspan="4"style="font-size:13px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                        <b>TOTALES</b>
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-top:0px;text-align: right; font-size:13px">
                        <b>$ {{number_format($total_final_emi,2)}}</b>                            
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px; border-top:0px;text-align: right; font-size:13px">
                        <b>$ {{number_format($total_final_des,2)}}</b>                            
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-top:0pxk;text-align: right; font-size:13px">
                        <b>$ {{number_format($total_final_int,2)}}</b>                            
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px; border-top:0px; text-align: right; font-size:13px">
                        <b>$ {{number_format($total_final_rec,2)}}</b>                            
                    </td>
                
                
                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-top:0px;text-align: right; font-size:13px">
                        <b>$ {{number_format($total_final_deuda,2)}}</b>                            
                    </td>
                    
                
                </tr>

            </tfoot>
        </table>
           
    </div>
</body>
</html>
