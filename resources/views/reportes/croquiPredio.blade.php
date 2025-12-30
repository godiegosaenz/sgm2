<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Requerimiento de Pago Voluntario</title>

    <style>
        body {
            font-family: DejaVu Sans, sans-serif;
            font-size: 12px;
            line-height: 1.2;
        }
        .titulo {
            text-align: center;
            font-weight: bold;
            margin-bottom: 25px;
            text-transform: uppercase;
        }
        .fecha {
            text-align: right;
            margin-bottom: 20px;
        }
        .contenido {
            text-align: justify;
        }

        .liquidaciones {
            text-align: justify;
        }

        .firma {
            /* margin-top: 40px; */
            margin-bottom: 200px !important;
        }

        @page {
            margin-top: 7em;
            margin-left:4em;
            margin-right:4em;
            margin-bottom: 12em;
        }
        header { position: fixed;  top: -115px; left: -80px; right: -50px; background-color: white; height: 60px; margin-right: 99px}

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
            font-size: 10px;
        }
        .pad{
            padding-left:5px;
            padding-right:5px;
        }

        footer {
            position: fixed;
            bottom: -212px;   /* ⬅️ BAJA el footer */
            left: -80px;
            right: -50px;
            height: 140px;
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
        font-size: 10px;
        }
        table.blueTable thead {
        background: #BCDCF9;
        border-bottom: 1px solid #444444;
        border-top: 1px solid #444444;
        }
        table.blueTable thead th {
        font-size: 12px;
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

    <header>
        <table class="ltable " width="112.5%"  >                
                <tr>
                    <td height="50px"  style="border: 0px;" align="left" >
                        <img src="fondo.png" width="800px" height="120px">
                    </td>
                
                </tr>             
            </table>
    </header>
    <footer>
        <table width="112.5%" class="ltable">
            <tr>
                <td style="border:0;" align="left">
                    <img src="fonfopiecoa.png" width="800px" height="120px">
                </td>
            </tr>
        </table>
    </footer>

    <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">CROQUIS PREDIO<br></p>
        <p>{{ $nombre_persona }}</p>
        
    </div>
    <div class="contenido">
       
        @foreach ($DatosLiquidacion as $key=>$data)            
            @foreach ($data as $key2=>$info)
                @if($key2==0)
                    <center><p><b>{{ $key }}</b> -- {{$info->direcc_cont}}</p></center>
                    <img src="http://192.168.0.175:8080/geoapi/rest/predio/croquis/{{ $key }}" alt="" width="100%">
                    <div style="page-break-after: always;"></div>
                @endif
            @endforeach
        @endforeach
        
           
    </div>

   
<!-- 
    <div class="firma." style="margin-botton:50px !important">
       
    </div> -->

</body>
</html>
