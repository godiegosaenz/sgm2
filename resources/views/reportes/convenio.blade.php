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
            /* margin-top: 50px; */
            margin-bottom: 200px !important;
        }

        @page {
            margin-top: 7em;
            margin-left:4em;
            margin-right:4em;
            margin-bottom: 12em;
        }
        header { position: fixed;  top: -119px; left: -80px; right: -50px; background-color: white; height: 60px; margin-right: 99px}

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
            left: -80px;
            right: -50px;
            height: 150px;
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

        @php

            $predio_txt="";
           
            $total_final=0;
            $coordenas_txt="";

            function numeroEnLetras($numero)
            {
            $entero = floor($numero);
                $decimal = round(($numero - $entero) * 100);

                $formatter = new \Luecano\NumeroALetras\NumeroALetras();
                $letras = mb_strtoupper($formatter->toWords($entero), 'UTF-8');

                return $letras . " CON " . str_pad($decimal, 2, '0', STR_PAD_LEFT)
                    . "/100 DÓLARES DE LOS ESTADOS UNIDOS DE AMÉRICA ($"
                . number_format($numero, 2, ',', '.') . ")";
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
                    // Convertir a número y sumar
                    $total += (float) $valor;
                   
                }
                $total_final = ($total_final ?? 0) + $total;
                
            @endphp
       
      
            
    @endforeach
    <div class="no-header-footer">
        <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8; text-align:right">RESOLUCION -GADMCSV-DF-TES-{{ str_pad($num_proceso, 3, "0", STR_PAD_LEFT) }}-{{ date('Y') }}</p>
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

        
        <b>Que,</b> en el segundo suplemento del Registro oficial #31, del 07 de julio del 2017, se publicó el Código Orgánico Administrativo, denominad COA;<br><br>

        <b>Que,</b> el Art. 152 del Código Tributario, señala, que practicado por el deudor o por la administración un acto de liquidación o determinación tributaria, o notificado de la emisión de un título de crédito o del auto de pago, el contribuyente o responsable podrá solicitar a la autoridad administrativa que tiene competencia para conocer los reclamos en única y definitiva instancia, que se compensen esas obligaciones conforme los artículos 51 y 52 de este Código o se le concedan facilidades para el pago. <br><br>

        La petición será motivada y contendrá los requisitos del artículo 119 de este Código con excepción de los numerales 4 y 6 y, en el caso de facilidades de pago, además, los siguientes: <br>
        1. Indicación clara y precisa de las obligaciones tributarias contenidas en las liquidaciones o determinaciones o en los títulos de crédito, respecto de las cuales se solicita facilidades para el pago; <br>
        2. Razones fundadas que impidan realizar el pago de contado; <br>
        3. Forma en que se pagará la obligación tributaria; y <br>
        4. Indicación de la garantía por la diferencia de la obligación, en el caso especial del artículo siguiente, normada según la resolución que la Administración Tributaria emita para el efecto. <br><br>
        No se concederán facilidades de pago sobre los tributos percibidos y retenidos por agentes de percepción y retención, pagos anticipados u obligaciones tributarias cuyo monto sea igual o menor al establecido para las obligaciones de recuperación onerosa, ni para las obligaciones tributarias aduaneras salvo aquellas determinadas en procedimientos de control posterior, conforme los requisitos previstos en el Reglamento.<br><br>

       <b>Que conforme al Art. 275 del COA, las solicitudes, la petición contendrá:</b> <br><br>
        1. Indicación clara y precisa de las obligaciones con respecto a las cuales se solicita facilidades para el pago;
        2. La forma en la que se pagará la obligación; y,
        3. Indicación de la garantía para la obligación <br><br>

        Que mediante resolución administrativa No. 103-DF-NMFM-2023 de fecha 01 de septiembre del 2023,  emitida por la Directora Financiera Municipal, se delega  la Tesorera para que proceda a realizar la actividad de suscribir los convenios de facilidades de pago, debiendo  verificar que se cumpla con los requisitos legales y se encuentre acorde a lo establecido en el Art. 474 y siguientes del Código Orgánico Administrativo, en perfecta relación con lo señalado en el Art. 142 y 153 del Código Tributario. <br><br>
        
        Que mediante oficio {{ $fecha_formateada }}, el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, presenta solicitud de pago mediante convenio  de las 



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
                    // Convertir a número y sumar
                    $total += (float) $valor;
                    

                }
                // $total_final=$total_final+$total;
                $total_final = ($total_final ?? 0) + $total;
                
            @endphp
       
          
               
                <b>MATRICULA INMOBILIARIA</b>: <strong>{{ $key  }}</strong>, <b> de los años {{ $anio_uno }} hasta el año fiscal {{ $info->anio }}{{ !$loop->last ? ',' : ',' }} 
                    
                @if (!$loop->last)/ @endif
               
            </b>
            
         @endforeach
       
        obligaciones pendientes de pago  por el valor de <strong>{{ numeroEnLetras($total_final) }}</strong>, para realizar un convenio de pago con un abono de <b>(USD $300) TRESCIENTOS DOLARES AMERICANOS</b> y la diferencia esto es la cantidad de <b>(USD. $108,27) CIENTO OCHO CON 27/100 DOLARES DE LOS ESTADOS UNIDOS</b> en el plazo de 3 meses con cuotas mensuales de <b>(USD $36,09) TREINTA Y SEIS CON   09/100 DOLARES AMERICANOS</b>, lo cual guarda coherencia con lo determinado en el Art. 274 y 275 del Código Orgánico Administrativo. <br><br>

        En uso de sus atribuciones legales:



        <center><b style="font-size: 14px;">RESUELVO</b></center> 
        <p style="font-size: 14px; text-align: justify; line-height: 1;">
        <b>PRIMERO. -</b>  Conceder facilidades de pago por tributos que el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, de la   

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
                    // Convertir a número y sumar
                    $total += (float) $valor;
                    

                }
                // $total_final=$total_final+$total;
                $total_final = ($total_final ?? 0) + $total;
                
            @endphp
       
          
               
                <b>MATRICULA INMOBILIARIA</b>: <strong>{{ $key  }}</strong>, <b> de los años {{ $anio_uno }} hasta el año fiscal {{ $info->anio }}{{ !$loop->last ? ',' : ',' }} 
                    
                @if (!$loop->last)/ @endif
               
            </b>
            
         @endforeach
       
        obligaciones pendientes de pago  por el valor de <strong>{{ numeroEnLetras($total_final) }}</strong>, para realizar un convenio de pago con un abono de <b>(USD $300) TRESCIENTOS DOLARES AMERICANOS</b> y la diferencia esto es la cantidad de <b>(USD. $108,27) CIENTO OCHO CON 27/100 DOLARES DE LOS ESTADOS UNIDOS</b> en el plazo de 3 meses con cuotas mensuales de <b>(USD $36,09) TREINTA Y SEIS CON   09/100 DOLARES AMERICANOS</b>, lo cual guarda coherencia con lo determinado en el Art. 274 y 275 del Código Orgánico Administrativo, en perfecta relación con lo señalado en el Art. 142 y 153 del Código Tributario.<br><br>

        <b>SEGUNDO:</b> El contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, a partir de la fecha de esta notificación pagara la suma de <b>(USD $36,09) TREINTA Y SEIS CON   09/100 DOLARES AMERICANOS, más intereses por 3 meses contados a partir de la presente fecha</b>, estableciéndose la fecha de pago el {{ date('d') }} de cada mes y un saldo de <b>(USD. $108,27) CIENTO OCHO CON 27/100 DOLARES DE LOS ESTADOS UNIDOS.</b> <br><br>

        <b>TERCERO. –</b> Con la aprobación de la presente facilidad de pago, se suspende el proceso coactivo, en contra <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>. Advertir que la concesión de facilidades de pago está condicionada al cumplimiento estricto de los pagos determinados anteriormente. En consecuencia, si requerido el deudor para el pago de cualquiera de los dividendos en mora, no lo hiciera en el plazo de ocho días se tendrá por terminada la concesión de facilidades de pago y podrá disponer el reanudar el procedimiento coactivo de acuerdo a lo establecido en el COA. <br><br>

        <b>CUARTO.-</b> Informar al contribuyente que el GADM San Vicente,  se reserva el derecho de verificar oportunamente la veracidad de la información que consta en el expediente administrativo y, si existiera un acto de simulación, ocultación,  falsedad o engaño que induzca a error de esta  resolución, se  considerara defraudación fiscal, sancionada según lo establece el Art. 342 y siguientes de la Codificación del Código Tributario  y la emisión del correspondiente título de crédito por el impuesto más los intereses de ley y multas correspondientes. <br><br>

        <b>QUINTO.-</b> Verificar el estricto cumplimiento de los pagos parciales que se establecen en esta resolución.<br><br>

        <b>SEXTO.-</b> Notificar al <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, en los términos establecido en el Art. 164 y siguientes del COA, para lo cual señala dirección física: San Vicente, Rancho Rojo, y teléfono No. 0999880478. <br><br>

        Dado y firmado en el despacho de la Alcaldía del GAD Municipal del Cantón San Vicente, al 9 de marzo del año 2026.
       
       
       
       
       </p>
       
       
       
       
     

    <div style="margin-bottom:48px">
        <p>Atentamente,</p>
        <table style="
            border-collapse: collapse;
            width: 55%;
            margin-left: auto;
        ">
            <tr>
                <td width=45% style="border:1px solid black; padding:2px; line-height:1;font: size 8px;">
                    <b>Firma</b>
                </td>
                <td style="border:1px solid black; padding:2px; line-height:1;">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td style="border:1px solid black; padding:2px; line-height:1;font: size 8px;">
                    <b>Nombres y Apellidos</b>
                </td>
                <td style="border:1px solid black; padding:2px; line-height:1;">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td style="border:1px solid black; padding:2px; line-height:1;font: size 8px;">
                    <b>Cédula</b>
                </td>
                <td style="border:1px solid black; padding:2px; line-height:1;">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td style="border:1px solid black; padding:2px; line-height:1;font: size 8px;">
                    <b>Teléfono</b>
                </td>
                <td style="border:1px solid black; padding:2px; line-height:1;">
                    &nbsp;
                </td>
            </tr>
        </table>
        <p style="margin: 0; line-height: 1.2;">
            <strong>{{$funcionarios->tesorera}}</strong>
        </p>
        <p style="margin: 0; line-height: 1.2;">
            <strong>TESORERA MUNICIPAL</strong>
        </p>
    </div>

    <div style="page-break-after: always;"></div>
  
    @php
        $monto=4;
        

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

        function fechaFormatoTexto2($fecha =null)
        {
            $meses = [
                1 => 'enero', 2 => 'febrero', 3 => 'marzo',
                4 => 'abril', 5 => 'mayo', 6 => 'junio',
                7 => 'julio', 8 => 'agosto', 9 => 'septiembre',
                10 => 'octubre', 11 => 'noviembre', 12 => 'diciembre'
            ];
            // $fecha=date('d/m/Y');
            $fecha = $fecha ? \Carbon\Carbon::parse($fecha) : now();

            return $fecha->day . ' dias del mes de ' . $meses[$fecha->month] . ' del ' . $fecha->year;
        }


    @endphp
    
  

</body>
</html>
