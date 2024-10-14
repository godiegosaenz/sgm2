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
        .table-container {
            width: 100%;
            display: flex; /* Utiliza flexbox para alinear las tablas */
            justify-content: space-between; /* Espacio entre las tablas */
        }
        .tabla {
            width: 48%; /* Ajusta el ancho de cada tabla */
            margin: 0 10px; /* Margen opcional */
        }
    </style>

</head>
<body>
    <div class="" style="font-family: Arial; font-size:14;">
        <table class="">
            <thead>
                <tr>
                    <th><img src="{{ asset('img/logo3.png') }}" alt="" width="50%" ></th>
                    <th width="80%">GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DEL CANTON SAN VICENTE</th>
                </tr>
            </thead>
        </table>
    </div>
    <br>
    <div class="" style="font-family: Arial; font-size:14;width:100%;margin-bottom:30px;">
        <table width="100%" style="text-align: left;">
            <thead>
                <tr>
                    <th width="50%">Contribuyente : </th>
                    <th width="50%">Cedula :</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td width="50%">Diego Andres Bermudez Saenz</td>
                    <td width="50%">1314801349</td>
                </tr>
            </tbody>
        </table>
    </div>


    <div class="table-container">
        <div class="table" style="font-family: Arial; font-size:14;width:100%;margin-bottom:30px;">
            <table class="" width="100%" style="text-align: left;">
                <thead >
                    <tr>
                        <th colspan="2">TITULO DE CREDITO - PREDIO URBANO # 2023-009705-PU</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td width="30%"><strong>COD. PREDIAL:</strong></td>
                        <td width="70%">132250010101901400000000</td>
                    </tr>
                    <tr>
                        <td width="30%"><strong>MAT. INMOBILIARIA:</strong></td>
                        <td width="70%">366</td>
                    </tr>
                    <tr>
                        <td width="30%"><strong>DIRECCION:</strong></td>
                        <td width="70%">LOS GUAYACANES MZ: 19 SL: 14</td>
                    </tr>
                </tbody>
            </table>
            <table width="100%" style="text-align: left;margin-top:20px">
                <thead>
                    <tr>
                        <th>Avalúo Solar</th>
                        <th>Avalúo Construcción</th>
                        <th>Avalúo Propiedad</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>$ 27.350,40</td>
                        <td>$ 0,00</td>
                        <td>$ 27.350,40</td>
                    </tr>
                </tbody>
            </table>
            <br>
            <label for=""><strong>FECHA DE EMISIÓN: 01/01/2024</strong> </label>
        </div>
        <div class="table" style="font-family: Arial; font-size:14;width:50%;margin-top:20px;margin-bottom:30px;display:inline-block;vertical-align: top;">
            <table class="blueTable" width="100%" style="text-align: left;">
                <thead>
                    <tr>
                        <th>RUBROS</th>
                        <th>VALOR</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>IMPUESTO PREDIAL URBANO</td>
                        <td>$ 31,19</td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr>
                        <td><STRONg>TOTAL</STRONg> </td>
                        <td><STRONg>$ 31,19</STRONg> </td>
                    </tr>
                </tfoot>
            </table>
            <br>
            <table class="" width="100%" style="text-align: left;">

                <tbody>
                    <tr>
                        <td><strong>Recargo + Impuestos:</strong>  </td>
                        <td><strong>$ 3,12</strong></td>
                    </tr>
                    <tr>
                        <td><strong>Total de Deuda:</strong> </td>
                        <td><strong>$ 50,78</strong></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
