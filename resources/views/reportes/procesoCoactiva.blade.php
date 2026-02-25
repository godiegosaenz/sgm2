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
    <div class="titulo" style="font-size:17px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE</p>
       
    </div>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">PROCESO COACTIVO No. </p>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">AÑO: {{ date('Y') }}</p>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">OBLIGACION PENDIENTE: IMPUESTOS PREDIALES</p>

   <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">COACTIVADO:  <strong>{{ strtoupper($nombre_persona) }} </p></strong>

    <strong><p style="margin: 0; line-height: 0; margin-top:20px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        C.I. {{ $ci_ruc }} </p></strong>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        MATRICULA INMOBILIARIA: {{implode(', ', array_keys($DatosLiquidacion));}} </p>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        TITULO DE CREDITO: {{$rango}}</p>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        JUEZ DE COACTIVA: {{$funcionarios->juez_coactiva}}</p>

    <p style="margin: 0; line-height: 0; margin-top:20px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        TESORERA MUNICIPAL</p>

    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        SECRETARIO: {{$funcionarios->secretario}}</p>

    <p style="margin: 0; line-height: 0; margin-top:20px; text-align: center; font-weigth: 500 !important; font-size: 16px;">
        ANALISTA JURIDICO COACTIVO GAD SAN VICENTE</p>

    <div style="page-break-after: always;"></div>

    <div class="titulo" style="font-size:17px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE</p>
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1.5; margin-top: 28px;">
       VISTOS.-San Vicente, {{ fechaFormatoTexto() }}, las {{ date('h:i a') }}, En mi calidad de Tesorera Municipal del Gobierno Autónomo Descentralizado Municipal del Cantón San Vicente y por ende Juez de Coactiva, continuando con el presente proceso administrativo se ordena que el actuario del despacho siente razón en Autos indicando si el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, ha cancelado o hecho alguna fórmula de pago  de los valores pendientes de pago que posee en el GAD Municipal de San Vicente, y cuyo plazo se encuentra fenecido, una vez realizado dicha razón vuelvan los autos para proveer lo que en derecho corresponda -NOTIFIQUESE Y CÚMPLASE.</p>
       
    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
        Lo que comunico a usted para los fines de ley.</p>


    <p style="margin: 0; line-height: 1.2; margin-top:250px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
       {{$funcionarios->tesorera}}</p>

    <p style="margin: 0; line-height: 0; margin-top:15px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
        TESORERA MUNICIPAL</p>

    </div>

    <div style="page-break-after: always;"></div>

    <div class="titulo" style="font-size:17px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE</p>
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1.5; margin-top: 28px;">
       RAZÓN. - Siento como tal y para los fines de ley pertinentes, que de acuerdo al oficio/pago voluntario de fecha {{ fechaFormatoTexto() }}, suscrito por su autoridad que el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, se le concedió un plazo de 10 días conforme a lo señalado en el Art. 271 del COA, para acercarse a realizarse el pago voluntario de las obligaciones pendientes por concepto de Predios Urbanos  con este GAD Municipal de San Vicente el mismo que  NO registra pago alguno de las obligaciones ni método de pago alguno,  es todo lo que puedo certificar en honor a la verdad.</p>
       
    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
        San Vicente a los {{ fechaFormatoTexto2() }} a las {{ date('h:i a') }}.</p>


    <p style="margin: 0; line-height: 1.2; margin-top:250px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
       {{$funcionarios->secretario}}</p>

    <p style="margin: 0; line-height: 0; margin-top:15px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
        ANALISTA JURIDICO COACTIVO GAD SAN VICENTE</p>


    

    <div style="page-break-after: always;"></div>


    <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE <br>PROCESO No. </p>
        <p style="margin: 0; line-height: 1.2;">ORDEN DE PAGO INMEDIATO</p>
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 28px;">
       <b>VISTOS:</b> En lo principal, de los Títulos de Crédito respectivos emitidas por el órgano responsable de su emisión, desprendiéndose que el contribuyente  <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong> adeuda al Gobierno Autónomo Descentralizado Municipal del cantón San Vicente la suma de la CANTIDAD DE <strong>{{ numeroEnLetras($total_final) }}</strong> por el concepto de <b>PREDIOS URBANOS</b>, la cual corresponde a la  
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
    
    @php
        $sumatotal = 0;
    @endphp
    
        @foreach ($DatosLiquidaciones as $d)
            <div class="no-header-footer1">
            <table class="tabla-principal">
                <tr style="line-height:1">
                    <td class="columna-izquierda">

                        <table class="tabla-izquierda-" style="margin-bottom: 3px;">
                            <thead>
                                <tr>
                                    <th width="50%">San Vicente, 01 Enero {{ $d[0]->anio + 1 }} </th>
                                </tr>
                            </thead>
                            
                        </table>

                        <table class="tabla-izquierda">
                            <thead>
                                <tr>
                                    <th width="50%">Contribuyente : </th>
                                    <th width="50%">Cedula :</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td width="50%">{{$d[0]->nombres}}</td>
                                    <td width="50%">{{$d[0]->cedula}}</td>
                                </tr>
                            </tbody>
                        </table>
                        <table class="tabla-izquierda">
                            <thead >
                                <tr>
                                    <th colspan="2">TITULO DE CREDITO - PREDIO URBANO # {{$d[0]->id_liquidacion}}</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td width="30%"><strong>Cod. predial:</strong></td>
                                    <td width="70%">{{$d[0]->cod_predial}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Mat. inmobiliaria:</strong></td>
                                    <td width="70%">{{$d[0]->num_predio}}</td>
                                </tr>
                                <tr>
                                    <td width="30%"><strong>Direccion:</strong></td>
                                    <td width="70%">{{$d[0]->direccion}}</td>
                                </tr>
                            </tbody>
                        </table>
                        <table class="tabla-izquierda">
                            <thead>
                                <tr>
                                    <th>Avalúo Solar</th>
                                    <th>Avalúo Construcción</th>
                                    <th>Avalúo Propiedad</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>$ {{$d[0]->avaluo_solar}}</td>
                                    <td>$ {{$d[0]->avaluo_construccion}}</td>
                                    <td>$ {{$d[0]->avaluo_municipal}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                    <td class="columna-derecha">
                        <table class="tabla-derecha">
                            <thead>
                                <tr>
                                    <th>RUBROS/CONCEPTO</th>
                                    <th>VALOR</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach ($d['rubros'] as $r)
                                    <tr>
                                        <td>{{$r->descripcion}}</td>
                                        <td style="text-align: right;">$ {{$r->valor}}</td>
                                    </tr>
                                @endforeach

                            </tbody>
                            <tfoot>
                                <tr>
                                    <td><STRONg>TOTAL</STRONg> </td>
                                    <td style="text-align: right;"><STRONg>$ {{$d[0]->total_pago}}</STRONg> </td>
                                </tr>
                            </tfoot>
                        </table>
                        <table class="tabla-izquierda">
                            <tbody>
                                <tr>
                                    <th>Fecha desde la cual se desvenga intereses:</th>
                                    <th style="text-align:right">Enero {{ $d[0]->anio + 1 }}</th>
                                </tr>
                                <tr>
                                    <th>Intereses hasta la fecha de emision:</th>
                                    <th style="text-align: right;">$ {{$d[0]->interes}}</th>
                                </tr>

                                <tr>
                                    <th>Recargos:</th>
                                    <th style="text-align: right;">$ {{$d[0]->recargos}}</th>
                                </tr>

                                <tr>
                                    <th>Total de Deuda:</th>
                                    <th style="text-align: right;">$ {{($d[0]->total_complemento)}}</th>
                                </tr>
                            </tbody>

                        </table>
                    </td>
                </tr>
            </table>
            <center><b>Fecha Impresion:</b>{{ $fecha_formateada }}<br>
            <b>Ley COA Aticulo 268</b></center>
            <br>
            <hr style="border:Dotted;"/>
            <br>
    
            @php
                // $sumatotal = $sumatotal + $d[0]->total_pago + $d[0]->valor_complemento;
                $sumatotal = $sumatotal + $d[0]->total_complemento;
            @endphp
            </div>
        @endforeach
        <h2 style="text-align: center">TOTAL DE TÍTULO DE CRÉDITO: <span class="badge text-bg-secondary">{{$sumatotal}};</span></h2>
        <p>CONCEPTO POR EL CUAL SE EMITE: PAGO DE IMPUESTO PREDIAL VENCIDO.
            EL VALOR TOTAL DEL TÍTULO DE CRÉDITO CAUSARÁ EL INTERES RESPECTIVO A PARTIR DE LA FECHA DE
            NOTIFICACIÓN SEGÚN LO EXPUESTO Y CONFORME LO ESTABLECE EL ART. 265 DEL COA LIQUIDACIÓN DE
            INTERESES Y MULTAS</p>
        <br>
        <br>
        
        <table width="100%">
            <tbody>
                <tr>
                    <td style="text-align: center">
                        __________________________________________
                    </td>
                    <td style="text-align: center">
                        __________________________________________
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">
                        Ing. Jacinta María Mendoza Cusme
                    </td>
                    <td style="text-align: center">
                        Ing. Danes Steven Cedeño Choez
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">
                        Tesorera Municipal Juez de Coactiva
                    </td>
                    <td style="text-align: center">
                        Director Financiero
                    </td>
                </tr>
            </tbody>
        </table>
    

</body>
</html>
