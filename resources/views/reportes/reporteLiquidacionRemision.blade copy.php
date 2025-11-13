<!DOCTYPE html>
<html>
<head>
    <title>INFORME</title>
    <style>
        table.blueTable {
        border: 1px solid #1C6EA4;
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

        table.blueTable tfoot td {
        font-size: 14px;
        }
        table.blueTable tfoot .links {
        text-align: right;
        }
        table.blueTable tfoot .links a{
        display: inline-block;
        background: #1C6EA4;
        color: #FFFFFF;
        padding: 2px 8px;
        border-radius: 5px;
        }
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
            <th>interes</th>
            <th>Recargo</th>

            <th>Total   </th>
            </tr>
            </thead>
            <tbody>
                @isset($DatosLiquidacion)
                    @foreach ($DatosLiquidacion as $item)
                    <tr>
                        <td>{{$num_predio2}}</td>
                        <td>{{$item['anio']}}</td>
                        <td>{{$clave_cat2}}</td>
                        @if($item['nombres'] != null)
                        <td>{{$item['nombres'].' '.$item['apellidos']}}</td>
                        @else
                        <td>{{$item['nombre_comprador']}}</td>
                        @endif
                        <td>{{$item['total_pago']}}</td>
                        <td>{{$item['interes']}}</td>
                        <td>{{$item['recargo']}}</td>

                        <td>{{$item['suma_emision_interes_recargos']}}</td>
                        </tr>
                    @endforeach
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td><strong>{{$suma_emision}}</strong></td>
                        <td><strong>{{$suma_interes}}</strong></td>
                        <td><strong>{{$suma_recargo}}</strong></td>
                        <td><strong>{{$suma_total}}</strong></td>
                    </tr>

                @endisset
            </tbody>
            </table>
    </div>
</body>
</html>
