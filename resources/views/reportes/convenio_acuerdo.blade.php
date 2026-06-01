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
            margin-bottom: 200px !important;
        }

        /* ========================================== */
        /* CONFIGURACIÓN DE PÁGINAS Y MÁRGENES        */
        /* ========================================== */
        @page {
            margin-top: 7em;
            margin-left: 4em;
            margin-right: 4em;
            margin-bottom: 12em;
        }

        @page segundo_doc {
            margin: 50px !important;
        }

        .seccion-dos {
            page: segundo_doc;
            page-break-before: always;
        }

        /* ========================================== */
        /* TRUCO ABSOLUTO PARA DETENER EN DOMPDF      */
        /* ========================================== */
        header { 
            position: fixed;  
            top: -119px; 
            left: -80px; 
            right: -50px; 
            background-color: white; 
            height: 60px; 
            margin-right: 99px;
        }

        footer {
            position: fixed;
            bottom: -221px;   
            left: -80px;
            right: -50px;
            height: 150px;
        }

        /* Ocultamos los contenedores internos del header y footer 
           cuando DomPDF renderice cualquier página que NO sea la número 1
        */
        header .contenido-header, 
        footer .contenido-footer {
            display: block;
        }

        /* En el CSS de DomPDF, esto corta la impresión del contenido en páginas subsiguientes */
        .seccion-dos header *, 
        .seccion-dos footer * {
            display: none !important;
            visibility: hidden !important;
        }
        
        /* Estilos de tablas */
        .ltable {
            border-collapse: collapse;
            font-family: sans-serif;
        }
        td, th {
            border: 1px solid white;
        }
        .sinbordeencabezado { border: 0px solid black; }
        .fuenteSubtitulo { font-size: 10px; }
        .pad { padding-left: 5px; padding-right: 5px; }

        table.blueTable {
            background-color: #FFFFFF;
            width: 100%;
            text-align: left;
            border-collapse: collapse;
        }
        table.blueTable td, table.blueTable th {
            border: 1px solid #AAAAAA;
            padding: 3px 2px;
        }
        table.blueTable tbody td { font-size: 10px; }
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
        table.blueTable thead th:first-child { border-left: none; }
        table.blueTable tfoot td { font-size: 14px; }
        
        .cabecera {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 1px;
        }
        .cabecera td { vertical-align: middle; }
        .cabecera .logo { width: 10%; text-align: left; }
        .cabecera .encabezado { width: 90%; text-align: center; font-size: 12px; }
        
        .tabla-principal { width: 100%; border-collapse: collapse; }
        .tabla-principal td { vertical-align: top; }
        .tabla-principal .columna-izquierda { width: 55%; }
        .tabla-principal .columna-derecha { width: 45%; }
        
        .tabla-izquierda { width: 100%; border-collapse: collapse; margin-bottom: 8px; font-size: 10px; }
        .tabla-izquierda th, .tabla-izquierda td { border: none; padding: 4px; text-align: left; }
        
        .tabla-derecha { width: 100%; border-collapse: collapse; margin-bottom: 8px; font-size: 9px; }
        .tabla-derecha th, .tabla-derecha td { border: 1px solid #000; padding: 4px; text-align: left; }
        .tabla-derecha th { background-color: #f2f2f2; }
    </style>
</head>
<body>

   
    @php
        $predio_txt="";
        $total_final=0;
        $coordenas_txt="";

        if (!function_exists('numeroEnLetras')) {
            function numeroEnLetras($numero) {
                $entero = floor($numero);
                $decimal = round(($numero - $entero) * 100);
                $formatter = new \Luecano\NumeroALetras\NumeroALetras();
                $letras = mb_strtoupper($formatter->toWords($entero), 'UTF-8');

                return $letras . " CON " . str_pad($decimal, 2, '0', STR_PAD_LEFT)
                    . "/100 DÓLARES DE LOS ESTADOS UNIDOS DE AMÉRICA ($"
                    . number_format($numero, 2, ',', '.') . ")";
            }
        }

        if (!function_exists('numeroEnLetras2')) {
            function numeroEnLetras2($numero) {
                $entero = floor($numero);
                $decimal = round(($numero - $entero) * 100);
                $formatter = new \Luecano\NumeroALetras\NumeroALetras();
                $letras = mb_strtoupper($formatter->toWords($entero), 'UTF-8');

                return $letras . " CON " . str_pad($decimal, 2, '0', STR_PAD_LEFT)
                    . "/100 DÓLARES DE LOS ESTADOS UNIDOS DE AMÉRICA ";
            }
        }
    @endphp

    @foreach ($DatosLiquidacion as $key=>$data)
        @php
            $total=0;
            $anio_uno=0;
            foreach($data as $key2=> $info){
                if($key2==0){
                    $anio_uno=$info->anio;
                }
                $valor = $info->total_pagar ?? 0;                    
                $valor = str_replace(',', '', $valor);
                $total += (float) $valor;
            }
            $total_final = ($total_final ?? 0) + $total;
        @endphp
    @endforeach

    <div>
        <div class="titulo_" style="font-size:14px !important">
            <br>
            <p style="margin: 0; line-height: 0.8; text-align:left"><b>San Vicente, {{ $fecha_formateada}}</b></p>
           
        </div>

         <div class="titulo" style="font-size:14px !important">
            <br>
            <p style="margin: 0; line-height: 1.1; text-align:left">DIRECTOR FINANCIERO DEL GOBIERNO AUTÓNOMO DESCENTRALIZADO MUNICIPAL DEL CANTON SAN VICENTE</p>
           
        </div>

        <div class="titulo_" style="font-size:14px !important">
            <br>
            <p style="margin: 0; line-height: 0.8; text-align:left">Ciudad

            <br> <br>
            De mis Consideraciones
            <br> <br><br>

            Le deseo éxito en las funciones que diariamente desempeña al tiempo de manifestarle lo siguiente:


            </p>
           
        </div>

        @php
            $total_final=0;
            $coordenas_txt="";
            $diferencia=$convenio->valor_adeudado-$convenio->cuota_inicial;
            $diferencia=number_format($diferencia,2);
        @endphp

        <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 20px;">
            Refiriéndome al predio signado con matrícula inmobiliaria No. 1104; del contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I. <strong>{{ $ci_ruc }}</strong>, al amparo de lo establecido en el 273 y siguiente Código Orgánico Administrativo, me permito solicitar a Usted muy comedidamente se me otorgue facilidades de pago.
            <br><br>
            Me comprometo a cumplir con las obligaciones pendientes de pago por el valor de  <strong>{{ numeroEnLetras($convenio->valor_adeudado) }}</strong>, para realizar un convenio de pago con un abono de <b>(USD ${{$convenio->cuota_inicial  }}) {{ numeroEnLetras2($convenio->cuota_inicial) }}</b> y la diferencia esto es la cantidad de <b>(USD. ${{$diferencia}}) {{ numeroEnLetras2($diferencia) }}</b> en el plazo de {{$convenio->numero_cuotas}} meses con cuotas mensuales de <b>(USD ${{$convenio->cuotas[1]->valor_cuota  }}) {{ numeroEnLetras2($convenio->cuotas[1]->valor_cuota ) }}</b>, los cuales estarán sujetos al interés que por ley corresponda lo cual guarda coherencia con lo determinado en el Art. 274 y 275 del Código Orgánico Administrativo. <br><br>

          
        </p>

        
        <br><br><br>
           
        <div style="text-align: center;">  
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{ strtoupper($nombre_persona) }}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{ $ci_ruc }}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>CONTRIBUYENTE</strong>
            </p> 
        </div>

        <br><br><br>
           
       
    </div>

   
</body>
</html>