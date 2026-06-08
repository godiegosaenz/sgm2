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
                    <img src="pieteso2.png" width="800px" height="120px">
                </td>
            </tr>
        </table>
    </footer>

    @php
        $predio_txt="";
        $total_final=0;
        $coordenas_txt="";

        if (!function_exists('numeroEnLetras')) {
            function numeroEnLetras($numero) {
                $valor = str_replace(',', '', $numero);
                $valor = (float)$valor;
                $entero = floor($valor);
                $decimal = round(($valor - $entero) * 100);
                $formatter = new \Luecano\NumeroALetras\NumeroALetras();
                $letras = mb_strtoupper($formatter->toWords($entero), 'UTF-8');

                return $letras . " CON " . str_pad($decimal, 2, '0', STR_PAD_LEFT)
                    . "/100 DÓLARES DE LOS ESTADOS UNIDOS DE AMÉRICA ($"
                    . number_format($numero, 2, ',', '.') . ")";
            }
        }

        if (!function_exists('numeroEnLetras2')) {
            function numeroEnLetras2($numero) {
                $valor = str_replace(',', '', $numero);
                $valor = (float)$valor;
                $entero = floor($valor);
                $decimal = round(($valor - $entero) * 100);
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
        <div class="titulo" style="font-size:13px !important">
            <br>
            <p style="margin: 0; line-height: 0.8; text-align:right">RESOLUCION -GADMCSV-DF-TES-{{ str_pad($num_proceso, 4, "0", STR_PAD_LEFT) }}-{{ date('Y') }}</p>
            <p style="margin: 0; line-height: 1.2;text-align:left">FACILIDADES DE PAGO. <br> TESORERA MUNICIPAL </p>
            <p style="margin: 0; line-height: 1.2;text-align:center">CONSIDERANDO</p>
        </div>

        <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 20px;">
            <b>Que,</b> el literal l), numeral 7, articulo 76 de la Constitución de la República señala como garantías del debido proceso lo siguiente: “las resoluciones de los poderes públicos deberán ser motivados. No habrá motivación si en la Resolución no se enuncian las normas o principios jurídicos en que se funda y no se explica la pertinencia de aplicación a los antecedentes de hecho. Los actos administrativos, resoluciones o fallos que no se encuentren motivados se considerarán nulos. Las servidoras o servidores responsables serán sancionados.<br><br>

            <b>Que,</b> el artículo 226 de la Constitución de la República del Ecuador establece que las instituciones del Estado, sus organismos, dependencias, las servidoras o servidores públicos y las personas que actúen en virtud de una potestad estatal ejercerán solamente las competencias y facultades que les sean atribuidas en la Constitución y la ley. Tendrán el deber de coordinar acciones para el cumplimiento de sus fines y hacer efectivo el goce y ejercicio de los derechos reconocidos en la Constitución.<br><br>

            <b>Que,</b> el artículo 227 de la Constitución de la República del Ecuador establece que la administración pública constituye un servicio a la colectividad que se rige por los principios de eficacia, eficiencia, calidad, jerarquía, desconcentración, descentralización, coordinación, participación, planificación, transparencia y evaluación.<br><br>

            <b>Que,</b> el artículo 300 de la Constitución de la República, dispone que el régimen tributario se regirá, entre otros, por los principios de eficiencia, simplicidad administrativa y suficiencia recaudadora, en concordancia con el artículo 172 del COOTAD; y, artículo 73 del Código Tributario.<br><br>

            <b>Que,</b> el artículo 340 del COOTAD, establece los deberes de la máxima autoridad financiera las que se deriven de las funciones que a la dependencia bajo su dirección le compete, las que se señalen en este Código y resolver los reclamos que se originen de ellos, tendrá además las atribuciones derivadas del ejercicio de la gestión tributaria, incluida la facultad sancionadora de conformidad con lo previsto en esta Ley, para lo cual deberá contar con la autorización previa del ejecutivo del GAD-Municipal. La autoridad financiera podrá dar de baja a créditos incobrables, así como previo al ejercicio de la acción coactiva agotará especialmente para grupos de atención prioritaria instancias de negociación y mediación. En ambos casos deberá contar con la autorización previa del ejecutivo de los gobiernos autónomos descentralizados.<br><br>

            <b>Que,</b> el artículo 65 del Código Tributario, establece que, en el ámbito municipal, la dirección de la administración tributaria le corresponderá al Alcalde, quien la ejercerá a través de las dependencias, direcciones u órganos administrativos que la Ley determine.<br><br>

            <b>Que,</b> el artículo 72 del Código Tributario determina que las gestiones de la administración tributaria comprenderán dos gestiones distintas y separadas la de determinación y recaudación de los tributos y la resolución de las reclamaciones que contra aquellas se presenten.<br><br>
            
            <b>Que,</b> en el segundo suplemento del Registro oficial #31, del 07 de julio del 2017, se publicó el Código Orgánico Administrativo, denominado COA;<br><br>

            <b>Que,</b> el Art. 152 del Código Tributario, señala, que practicado por el deudor o por la administración un acto de liquidación o determinación tributaria, o notificado de la emisión de un título de crédito o del auto de pago, el contribuyente o responsable podrá solicitar a la autoridad administrativa que tiene competencia para conocer los reclamos en única y definitiva instancia, que se compensen esas obligaciones conforme los artículos 51 y 52 de este Código o se le concedan facilidades para el pago. <br><br>

            La petición será motivada y contendrá los requisitos del artículo 119 de este Código con excepción de los numerales 4 y 6 y, en el caso de facilidades de pago, además, los siguientes: <br>
            1. Indicación clara y precisa de las obligaciones tributarias contenidas in las liquidaciones o determinaciones o en los títulos de crédito, respecto de las cuales se solicita facilidades para el pago; <br>
            2. Razones fundadas que impidan realizar el pago de contado; <br>
            3. Forma en que se pagará la obligación tributaria; y <br>
            4. Indicación de la garantía por la diferencia de la obligación, en el caso especial del artículo siguiente, normada según la resolución que la Administración Tributaria emita para el efecto. <br><br>
            No se concederán facilidades de pago sobre los tributos percibidos y retenidos por agentes de percepción y retención, pagos anticipados u obligaciones tributarias cuyo monto sea igual o menor al establecido para las obligaciones de recuperación onerosa, ni para las obligaciones tributarias aduaneras salvo aquellas determinadas en procedimientos de control posterior, conforme los requisitos previstos en el Reglamento.<br><br>

           <b>Que conforme al Art. 275 del COA, las solicitudes, la petición contendrá:</b> <br><br>
            1. Indicación clara y precisa de las obligaciones con respecto a las cuales se solicita facilidades para el pago;<br>
            2. La forma en la que se pagará la obligación; y,<br>
            3. Indicación de la garantía para la obligación <br><br>

            Que mediante resolución administrativa No. 103-DF-NMFM-2023 de fecha 01 de septiembre del 2023, emitida por la Directora Financiera Municipal, se delega la Tesorera para que proceda a realizar la actividad de suscribir los convenios de facilidades de pago, debiendo verificar que se cumpla con los requisitos legales y se encuentre acorde a lo establecido en el Art. 474 y siguientes del Código Orgánico Administrativo, en perfecta relación con lo señalado en el Art. 142 y 153 del Código Tributario. <br><br>
            
            Que mediante oficio {{ $fecha_formateada }}, el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I. <strong>{{ $ci_ruc }}</strong>, presenta solicitud de pago mediante convenio de las 

            @php
                $total_final=0;
                $coordenas_txt="";
                $diferencia=$convenio->valor_adeudado-$convenio->cuota_inicial;
                $diferencia=number_format($diferencia,2);
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
           
                <b>MATRICULA INMOBILIARIA</b>: <strong>{{ $key  }}</strong>, <b> de los años {{ $anio_uno }} hasta el año fiscal {{ $info->anio }}{{ !$loop->last ? ',' : ',' }} 
                        
                @if (!$loop->last)/ @endif
                   
                </b>
            @endforeach
           
            obligaciones pendientes de pago por el valor de <strong>{{ numeroEnLetras($convenio->valor_adeudado) }}</strong>, para realizar un convenio de pago con un abono de <b>(USD ${{$convenio->cuota_inicial  }}) {{ numeroEnLetras2($convenio->cuota_inicial) }}</b> y la diferencia esto es la cantidad de <b>(USD. ${{$diferencia}}) {{ numeroEnLetras2($diferencia) }}</b> en el plazo de {{$convenio->numero_cuotas}} meses con cuotas mensuales de <b>(USD ${{$convenio->cuotas[1]->valor_cuota  }}) {{ numeroEnLetras2($convenio->cuotas[1]->valor_cuota ) }}</b>, lo cual guarda coherencia con lo determinado en el Art. 274 y 275 del Código Orgánico Administrativo. <br><br>

            En uso de sus atribuciones legales:
        </p>

        <center><b style="font-size: 14px;">RESUELVO</b></center> 
        
        <p style="font-size: 14px; text-align: justify; line-height: 1;">
            <b>PRIMERO. -</b> Conceder facilidades de pago por tributos que el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I. <strong>{{ $ci_ruc }}</strong>, de la   

            @php
                $total_final=0;
                $coordenas_txt="";
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
           
                <b>MATRICULA INMOBILIARIA</b>: <strong>{{ $key  }}</strong>, <b> de los años {{ $anio_uno }} hasta el año fiscal {{ $info->anio }}{{ !$loop->last ? ',' : ',' }} 
                        
                @if (!$loop->last)/ @endif
                </b>
            @endforeach
           
            obligaciones pendientes de pago por el valor de <strong>{{ numeroEnLetras($convenio->valor_adeudado) }}</strong>, para realizar un convenio de pago con un abono de <b>(USD ${{$convenio->cuota_inicial  }}) {{ numeroEnLetras2($convenio->cuota_inicial) }}</b> y la diferencia esto es la cantidad de <b>(USD. ${{$diferencia}}) {{ numeroEnLetras2($diferencia) }}</b> en el plazo de {{$convenio->numero_cuotas}} meses con cuotas mensuales de <b>(USD ${{$convenio->cuotas[1]->valor_cuota  }}) {{ numeroEnLetras2($convenio->cuotas[1]->valor_cuota ) }}</b>,, lo cual guarda coherencia con lo determinado en el Art. 274 y 275 del Código Orgánico Administrativo, en perfecta relación con lo señalado en el Art. 142 y 153 del Código Tributario.<br><br>

            <b>SEGUNDO:</b> El contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I. <strong>{{ $ci_ruc }}</strong>, a partir de la fecha de esta notificación pagara la suma de <b>(USD $36,09) TREINTA Y SEIS CON   09/100 DOLARES AMERICANOS, más intereses por 3 meses contados a partir de la presente fecha</b>, estableciéndose la fecha de pago el {{ date('d') }} de cada mes y un saldo de <b>(USD. $108,27) CIENTO OCHO CON 27/100 DOLARES DE LOS ESTADOS UNIDOS.</b> <br><br>

            <b>TERCERO. –</b> Con la aprobación de la presente facilidad de pago, se suspende el proceso coactivo, en contra <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I. <strong>{{ $ci_ruc }}</strong>. Advertir que la concesión de facilidades de pago está condicionada al cumplimiento estricto de los pagos determinados anteriormente. En consecuencia, si requerido el deudor para el pago de cualquiera de los dividendos en mora, no lo hiciera en el plazo de ocho días se tendrá por terminada la concesión de facilidades de pago y podrá disponer el reanudar el procedimiento coactivo de acuerdo a lo establecido en el COA. <br><br>

            <b>CUARTO.-</b> Informar al contribuyente que el GADM San Vicente, se reserva el derecho de verificar oportunamente la veracidad de la información que consta en el expediente administrativo y, si existiera un acto de simulación, ocultación, falsedad o engaño que induzca a error de esta resolución, se considerara defraudación fiscal, sancionada según lo establece el Art. 342 y siguientes de la Codificación del Código Tributario y la emisión del correspondiente título de crédito por el impuesto más los intereses de ley y multas correspondientes. <br><br>

            <b>QUINTO.-</b> Verificar el estricto cumplimiento de los pagos parciales que se establecen in esta resolución.<br><br>

            <b>SEXTO.-</b> Notificar al <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I. <strong>{{ $ci_ruc }}</strong>, en los términos establecido en el Art. 164 y siguientes del COA, para lo cual señala dirección física: San Vicente, Rancho Rojo, y teléfono No. 0999880478. <br><br>

            Dado y firmado en el despacho de la Alcaldía del GAD Municipal del Cantón San Vicente, al {{ $fecha_formateada }}.
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
           
        <div style="text-align: center;">  
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{ $funcionarios->tesorera }}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{ $funcionarios->cedula_teso }}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>TESORERA MUNICIPAL GAD SAN VICENTE {{ env('TESORERA_MUNI') }}</strong>
            </p> 
        </div>
        
        <br><br>
        
        <div>
            <table style="border-collapse: collapse; width: 100%;">
                <tr >
                    <td width="12%" style="border:1px solid black; padding:2px; line-height:1.5; font-size: 12px;">
                        <b>Acción:</b>
                    </td>
                    <td width="40%" style="border:1px solid black; padding:2px; line-height:1.5; font-size: 12px;">
                        <b>Servidor(a):</b>
                    </td>
                    <td width="28%" style="border:1px solid black; padding:2px; line-height:1.5; font-size: 12px;">
                        <b>Puesto:</b>
                    </td>
                    <td width="25%" style="border:1px solid black; padding:2px; line-height:1.5; font-size: 12px;">
                        <b>Firma:</b>
                    </td>
                </tr>
                <tr>
                    <td style="border:1px solid black; padding:2px; line-height:1.5;">
                        Elaborado
                    </td>
                    <td style="border:1px solid black; padding:2px; line-height:1.5;">
                       {{  ucwords(strtolower($funcionarios->secretario)) }}
                    </td>
                    <td style="border:1px solid black; padding:2px; line-height:1.5;">
                        Analista Jurídico Coactiva {{ env('COACTIVA_CARGO') }}
                    </td>
                    <td style="border:1px solid black; padding:2px; line-height:1.5;">
                        &nbsp;
                    </td>
                </tr>
            </table>
        </div>
    </div>

   
</body>
</html>