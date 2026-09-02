<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Cuotas Convenio</title>

    <style>
        body {
            font-family: DejaVu Sans, sans-serif;
            font-size: 12px;
            line-height: 1.2;
            /* margin-bottom: 90px; */
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
            /* margin-top: 50px; */
            margin-bottom: 200px !important;
        }

        @page {
            margin-top: 7em;
            margin-left:2em;
            margin-right:2em;
            margin-bottom: 12em;
        }
        header { position: fixed;  top: -119px; left: -70px; right: -50px; background-color: white; height: 60px; margin-right: 99px}

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
            bottom: -221px;   /* ⬅️ BAJA el footer */
            left: -70px;
            right: -50px;
            height: 150px;
        }

        .footer-firma {
            position: fixed;
            bottom: -60px; /* encima del footer institucional */
            width: 100%;
            text-align: center;
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

        .tabla-principal {
            width: 100%;
            border-collapse: collapse;
        }
        .tabla-principal td {
            vertical-align: top;
        }
        .tabla-principal .columna-izquierda {
            width: 55%;
        }
        .tabla-principal .columna-derecha {
            width: 45%;
        }
        .tabla-izquierda {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 8px;
            font-size: 10px;
        }
        .tabla-izquierda th, .tabla-izquierda td {
            border: none;
            padding: 4px;
            text-align: left;
        }
        .tabla-derecha {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 8px;
            font-size: 9px;
        }
        .tabla-derecha th, .tabla-derecha td {
            border: 1px solid #000;
            padding: 4px;
            text-align: left;
        }
        .tabla-derecha th {
            background-color: #f2f2f2;
        }

        header, footer {
            display: block;
        }

        .no-header-footer header, 
        .no-header-footer footer {
            display: none;
        }

        


    </style>
</head>
<body>

    <header>
        <table class="ltable " width="112.5%"  >                
                <tr>
                    <td height="50px"  style="border: 0px;" align="left" >
                        <img src="fondo.png" width="830px" height="120px">
                    </td>
                
                </tr>             
            </table>
    </header>
    <footer>
        <table width="112.5%" class="ltable">
            <tr>
                <td style="border:0;" align="left">
                    <img src="fonfopiecoa.png" width="830px" height="120px">
                </td>
            </tr>
        </table>
    </footer>

        @php
            function fechaFormatoTexto($fecha =null)
            {
                $meses = [
                    1 => 'enero', 2 => 'febrero', 3 => 'marzo',
                    4 => 'abril', 5 => 'mayo', 6 => 'junio',
                    7 => 'julio', 8 => 'agosto', 9 => 'septiembre',
                    10 => 'octubre', 11 => 'noviembre', 12 => 'diciembre'
                ];
                // $fecha=date('d/m/Y');
                $fecha = $fecha ? \Carbon\Carbon::parse($fecha) : now();

                return $fecha->day . ' de ' . $meses[$fecha->month] . ' del ' . $fecha->year;
            }

           
        @endphp
       
       
    <div class="no-header-footer">
    <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">CONVENIO DE PAGO</p>
       
    </div>


    <table style="width: 100%;">
        <tr style="font-size: 10px !important;">
            <td>
                <b>Generado por:</b> {{ $Datos[0]['convenio']['usuario_registra'] }}
            </td>
            <td>
                <b>Fecha Generacion:</b> {{ $Datos[0]['convenio']['fecha_registra'] }}
            </td>
        </tr>

        <tr style="font-size: 10px !important;">
            <td>
                <b>Valor Deuda:</b> {{ $Datos[0]['convenio']['valor_adeudado'] }}
            </td>
            <td>
                <b>Contribuyente:</b> {{ $nombre }}
            </td>
        </tr>
    </table>

    <table style="width: 100%; border-collapse: collapse; margin-top:20px">
        <thead style="font-size:10px">
            <tr>
                <th style="border: 1px solid #AAAAAA; text-align: left;"># Cuota</th>
                <th style="border: 1px solid #AAAAAA; text-align: left;">Fecha Pago</th>
                <th style="border: 1px solid #AAAAAA; text-align: left;">Valor</th>
            </tr>
        </thead>
        <tbody>
            @foreach ($Datos as $key => $data)
                <tr style="font-size:10px">
                    <td style="border: 1px solid #AAAAAA;">{{ $data->cuota_inicial == true ? 'Inicial' : $key }}</td>
                    <td style="border: 1px solid #AAAAAA;">{{ $data->fecha }}</td>
                    <td style="border: 1px solid #AAAAAA;">{{ $data->valor_cuota }}</td>
                </tr>
            @endforeach
        </tbody>
    </table>

    
  
</body>
</html>
