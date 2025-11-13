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
                        $total=$item->saldo+$item->interes+$item->recargos;
                        $total=$total- $item->desc;
                    @endphp
                    <tr>
                        <td>{{$item->num_predio}}</td>
                        <td>{{$item->anio}}</td>
                        <td>{{$item->clave_cat}}</td>
                        @if($item->nombres != '')
                        <td>{{$item->nombres.' '.$item->apellidos}}</td>
                        @else
                        <td>{{$item->nombre_comprador}}</td>
                        @endif
                        
                        <td style="text-align:right">{{$item->saldo}}</td>
                        <td style="text-align:right">{{$item->desc}}</td>
                        <td style="text-align:right">{{$item->interes}}</td>
                        <td style="text-align:right">{{$item->recargos}}</td>
                        <td style="text-align:right">{{number_format($total,2)}}    </td>
                        </tr>
                        @php
                            $total_emi=$total_emi +$item->saldo;
                            $total_des=$total_des +$item->desc;
                            $total_int=$total_int +$item->interes;
                            $total_rec=$total_rec +$item->recargos;

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
