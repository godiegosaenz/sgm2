<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reporte en PDF</title>
    <style>
       @page {
            margin-top: 3mm;  /* Ajustado */
            margin-right: 10mm;
            margin-bottom: 10mm;
            margin-left: 10mm;
        }

        body {
            font-family: Arial, sans-serif;
            font-size: 10px;
            position: relative;
            margin: 0;
            padding: 0;
        }

        /* Imagen de fondo en la mitad superior */
        .watermark {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 50%; /* Solo ocupa la mitad superior */
            background-image: url('{{ asset('img/logo4.png') }}');
            background-repeat: no-repeat;
            background-size: contain;
            background-position: center;
            opacity: 0.05; /* Opacidad muy baja */
            z-index: -1; /* La imagen queda detrás del contenido */
        }

        /* Estilos de las tablas y contenido */
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
            font-weight: bold;
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
            margin-bottom: 2px;
            font-size: 11px;
        }

        .tabla-izquierda th, .tabla-izquierda td {
            border: none;
            padding: 4px;
            text-align: left;
        }

        .tabla-derecha {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
            font-size: 10px;
        }

        .tabla-derecha th, .tabla-derecha td {
            border: 1px solid #000;
            padding: 4px;
            text-align: left;
        }

        .tabla-derecha th {
            background-color: #f2f2f2;
        }
        p{
            margin-left: 20px;
            font-size: 13px;
        }

    </style>
</head>
<body>
    <!-- Imagen de fondo (marca de agua) -->
    <div class="watermark"></div>

   

        <table class="cabecera" style="font-size:10px !important">
            <tr>
                <td class="logo">
                    <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="70">
                </td>
                <td class="encabezado">
                    <h3>GAD MUNICIPAL DEL CANTÓN SAN VICENTE <h4>DIRECCION FINANCIERA<br>
                    SAN VICENTE - MANABI - ECUADOR</h4></h3>
                </td>
                 <td class="logo">
                </td>
            </tr>
        </table>

        <p><b>CEDULA /RUC: </b>{{ $Datos->cedula_ruc }}</p>
        <p><b>NOMBRES: </b>{{ $Datos->nombres }}</p>
        <p><b>APELLIDOS: </b>{{ $Datos->apellidos }}</p>
        <p><b>CIUDAD DOMICILIARIA: </b>{{ $Datos->ciudad_domiciliaria }}</p>
        <p><b>DIRECCION DOMICILIARIA: </b>{{ $Datos->direccion_domiciliaria }}</p>
        <p><b>CORREO: </b>
        @foreach (json_decode($Datos->correo, true) as $correo)
             <li style="margin-left:60px !important; font-size: 13px !important;">{{ $correo }}</li>
        @endforeach
        </p>

        <p><b>TELEFONO: </b>
        @foreach (json_decode($Datos->telefono, true) as $telefono)
             <li style="margin-left:60px !important; font-size: 13px !important;">{{ $telefono }}</li>
        @endforeach
        </p>
        <p><b>PREDIO: </b>{{ $Datos->predio }}</p>
        <p><b>USUARIO ACTUALIZA: </b>{{ $Datos->usuario_nombre }}</p>
        <p><b>FECHA ACTUALIZACION: </b>{{ $Datos->fecha_actualizacion }}</p>
</body>
</html>
