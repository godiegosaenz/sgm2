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
    <br>
    <div class="" style="font-family: Arial; font-size:14;">
        <div class="">
                <h3>Liquidacion</h3>
        </div>
    </div>
    <br>
    <div style="width:100%;margin-top:20px;margin-bottom:30px;">
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
            <th>Descuento</th>
            <th>Total   </th>
            </tr>
            </thead>
            <tbody>
            <tr>
            <td>cell1_1</td>
            <td>cell2_1</td>
            <td>cell3_1</td>
            <td>cell4_1</td>
            <td>cell4_1</td>
            <td>cell4_1</td>
            <td>cell4_1</td>
            <td>cell4_1</td>
            <td>cell4_1</td>
            </tr>

            </tbody>
            </table>
    </div>
</body>
</html>
