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

        Que mediante resolución administrativa No. 103-DF-NMFM-2023 de fecha 01 de septiembre del 2023,  emitida por la Directora Financiera Municipal, se delega  la Tesorera para que proceda a realizar la actividad de suscribir los convenios de facilidades de pago, debiendo  verificar que se cumpla con los requisitos legales y se encuentre acorde a lo establecido en el Art. 474 y siguientes del Código Orgánico Administrativo, en perfecta relación con lo señalado en el Art. 142 y 153 del Código Tributario



        <br><br><br><br><br><br><br><br><br><br>
        
       <b>VISTOS:</b> En lo principal, de los Títulos de Crédito respectivos emitidas por el órgano responsable de su emisión, desprendiéndose que el contribuyente  <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong> adeuda al Gobierno Autónomo Descentralizado Municipal del cantón San Vicente la suma de la CANTIDAD DE <strong>{{ numeroEnLetras($total_final) }}</strong> por el concepto de <b> impuesto predial</b>, la cual corresponde a la  
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
       
          
               
                <b>MATRICULA INMOBILIARIA</b>: <strong>{{ $key  }}</strong>, <b> DESDEL EL {{ $anio_uno }} HASTA EL EJERCICIO FISCAL {{ $info->anio }} 
                    
                @if (!$loop->last)/ @endif
            </b>
            
         @endforeach
       
      sin que a la presente fecha haya pagado la obligación tributaria liquida, determinada y de plazo vencido que actualmente exigible, por lo que de conformidad como lo establece el Art. 157 y siguientes del Código Tributario,  en perfecta relación con lo prescrito en el Art. 279 del COA, dicto el presente Auto De Pago, disponiendo que el deudor pague al ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN SAN VICENTE, en el término de 03 Días, la CANTIDAD DE <strong>{{ numeroEnLetras($total_final) }}</strong>, valor al que sumaran los intereses, recargos, costas procesales y otros valores adicionales que genere la obligación, hasta que no se dé la cancelación de la deuda; o dimita bienes equivalentes, previniéndole que de no hacerlo se procederá al embargue de bienes suficientes para cubrir las obligaciones vencidas. En uso de la facultad concedida en el Art. 164 del Código Tributario,  279, 280 y 281 del Código Orgánico Administrativo (COA), se tomara las medidas precautelarías que correspondan que a continuación se detallan hasta que cancele las obligaciones vencidas, se ordena: 1): Pagar o dimitir bienes en el término de tres días a partir de la citación, apercibiéndole que de no hacerlo, se embargarán los bienes equivalentes al capital, intereses, multas y costas.- 2): Bloquear y retener fondos de cuentas corrientes y/o ahorros, pólizas de acumulación o cualquier tipo de inversión  que tuviere la/el coactivada/o hasta por un valor de la <strong> CANTIDAD {{ numeroEnLetras($total_final) }}</strong>, para lo cual se oficiará a la Superintendencia de Bancos y a la Superintendencia de Economía Popular y Solidaria para lo cual se oficiará a la Superintendencia de Bancos y a la Superintendencia de Economía Popular y Solidaria.- 3): Prohibir la enajenación de los vehículos de propiedad de la/el coactivada/o, para lo cual se oficiará a la Agencia Nacional de Transito.- 4): Prohibir la enajenación de los bienes inmuebles que se hallaren inscritos a nombre de la/el coactivada/o en el Registro de la Propiedad del Cantón San Vicente.- 5) Poner en conocimiento al Ministerio de Trabajo que el/la coactivado/a es deudor del GAD Municipal de San Vicente, a fin que registre el impedimento de ejercer cargo público.-Actúe en calidad de Secretario de este Órgano Ejecutor {{$funcionarios->secretario}}; <strong>JUZGADO DE COACTIVA DEL GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DE SAN VICENTE. El GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL SAN VICENTE</strong> se reserva el derecho expreso de continuar acciónales legales en contra de los obligados solidarios y subsidiarios conforme a las leyes, estatutos y reglamentos vigentes. Notifíquese al coactivado, con el contenido del acto administrativo conforme lo previsto en el Art. 280 del Código Orgánico Administrativo. Se previene al coactivado(a), de la obligación de señalar correo electrónico y/o casillero judicial para posteriores notificaciones. - CÚMPLASE, OFICIESE Y NOTIFÍQUESE.</p>

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
