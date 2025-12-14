<!DOCTYPE html>
<html>
<head>
    <title>INFORME</title>
    <style>
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
    </style>

</head>
<body>
    <div class="">
        <div class="">
            <img src="{{ asset('img/cabecera_tesoreria.png') }}" alt="" width="100%" >
        </div>
    </div>
    <div class="" style="font-family: Arial; font-size:12;">
        <div class="">
                <h3>Liquidaciones</h3>
        </div>
    </div>
    <div style="width:100%;margin-top:5px;margin-bottom:5px;">
        <table class="blueTable">
            <thead>
            <tr>
            <th>No. Predio</th>
            <th>Año</th>
            <th>Cod. Predial</th>
            <th>Contribuyente</th>
            <th>Emisión</th>
            <th>Descuento</th>
            <th>Interes</th>
            <th>Recargo</th>

            <th>Total   </th>
            </tr>
            </thead>
            <tbody>
                @isset($DatosLiquidacion)
                    @php
                        $total_emi=0;
                        $total_des=0;
                        $total_int=0;
                        $total_rec=0;
                        $total_final=0
                    @endphp
                    @foreach ($DatosLiquidacion as $item)
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
                        <td>{{$num_matricula}}</td>
                        <td>{{$anio[0]}}</td>
                        <td>{{$item->clave}}</td>
                       
                        <td>{{$item->nombre_per}}</td>
                        
                        <td style="text-align:right">{{$item->subtotal_emi}}</td>
                        <td style="text-align:right">{{ number_format($item->descuento ?? 0.00, 2) }}</td>
                        <td style="text-align:right">{{$item->intereses}}</td>
                        <td style="text-align:right">{{ number_format($item->recargo ?? 0.00, 2) }}</td>
                        <td style="text-align:right">{{number_format($total,2)}}    </td>
                        </tr>
                        @php
                            $total_emi=$total_emi +$item->subtotal_emi;
                            $total_des=$total_des +$item->descuento;
                            $total_int=$total_int +$item->intereses;
                            $total_rec=$total_rec +$item->recargo;

                        @endphp
                    @endforeach
                     <tfoot >
                    @php
                        $total_final= $total_emi + $total_int + $total_rec;
                        $total_final=$total_final -$total_des;

                    @endphp
                    <tr style="font-size:13px !important;line-height:5px" style="">

                        <td  colspan="4"style="font-size:13px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                            <b>TOTAL</b>
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:13px">
                           {{number_format($total_emi,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:13px">
                           {{number_format($total_des,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:13px">
                           {{number_format($total_int,2)}}                            
                        </td>

                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:13px">
                           {{number_format($total_rec,2)}}                            
                        </td>
                      
                       
                        <td style="border: 0px;border-color: #D3D3D3;  text-align: right; font-size:13px">
                           {{number_format($total_final,2)}}                            
                        </td>
                        
                      
                    </tr>

                </tfoot>
                   
                @endisset
            </tbody>
            </table>
    </div>
</body>
</html>
