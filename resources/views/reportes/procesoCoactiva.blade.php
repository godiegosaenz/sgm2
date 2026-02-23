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
                    if($ubicacion==1){
                        $coordenas_txt = ' (coordenadas <strong>X</strong> ' . $info->coordx . ' <strong>Y</strong> ' . $info->coordy.'),';
                    }

                }
                $total_final = ($total_final ?? 0) + $total;
                
            @endphp
       
      
            
    @endforeach

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

    

    <div style="page-break-after: always;"></div>

    <div class="titulo" style="font-size:17px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE</p>
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1.5; margin-top: 28px;">
       RAZÓN. - Siento como tal y para los fines de ley pertinentes, que de acuerdo al oficio/pago voluntario de fecha {{ fechaFormatoTexto() }}, suscrito por su autoridad que el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong>, se le concedió un plazo de 10 días conforme a lo señalado en el Art. 271 del COA, para acercarse a realizarse el pago voluntario de las obligaciones pendientes por concepto de Predios Urbanos  con este GAD Municipal de San Vicente el mismo que  NO registra pago alguno de las obligaciones ni método de pago alguno,  es todo lo que puedo certificar en honor a la verdad.</p>
       
    <p style="margin: 0; line-height: 1.2; margin-top:50px; text-align: left; font-weigth: 500 !important; font-size: 14px;">
        San Vicente a los 6 días del mes de febrero del 2026 a las {{ date('h:i a') }}.</p>


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
       VISTOS: En lo principal, de los Títulos de Crédito respectivos emitidas por el órgano responsable de su emisión, desprendiéndose que el contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I.  <strong>{{ $ci_ruc }}</strong> adeuda al Gobierno Autónomo Descentralizado Municipal del cantón San Vicente la suma de la CANTIDAD DE <strong>{{ numeroEnLetras($total_final) }}</strong> por el concepto de PREDIOS URBANOS, la cual corresponde <br>
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
                    if($ubicacion==1){
                        $coordenas_txt = ' (coordenadas <strong>X</strong> ' . $info->coordx . ' <strong>Y</strong> ' . $info->coordy.'),';
                    }

                }
                // $total_final=$total_final+$total;
                $total_final = ($total_final ?? 0) + $total;
                
            @endphp
       
          
               
                A la matricula inmobiliaria <strong>{{ $key  }}</strong>, cuya deuda asciende a la cantidad de
                <strong>{{ numeroEnLetras($total) }}</strong>, correspondiente a los ejercicios fiscales <b>{{ $anio_uno }} - {{ $info->anio }}</b><br>
            
         @endforeach
       
      Sin que a la presente fecha haya pagado la obligación tributaria liquida, determinada y de plazo vencido que actualmente exigible, por lo que de conformidad como lo establece el Art. 157 y siguientes del Código Tributario,  en perfecta relación con lo prescrito en el Art. 279 del COA, dicto el presente Auto De Pago, disponiendo que el deudor pague al ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN SAN VICENTE, en el término de 03 Días, la CANTIDAD DE <strong>{{ numeroEnLetras($total_final) }}</strong>, valor al que sumaran los intereses, recargos, costas procesales y otros valores adicionales que genere la obligación, hasta que no se dé la cancelación de la deuda; o dimita bienes equivalentes, previniéndole que de no hacerlo se procederá al embargue de bienes suficientes para cubrir las obligaciones vencidas. En uso de la facultad concedida en el Art. 164 del Código Tributario,  279, 280 y 281 del Código Orgánico Administrativo (COA), se tomara las medidas precautelarías que correspondan que a continuación se detallan hasta que cancele las obligaciones vencidas, se ordena: 1): Pagar o dimitir bienes en el término de tres días a partir de la citación, apercibiéndole que de no hacerlo, se embargarán los bienes equivalentes al capital, intereses, multas y costas.- 2): Bloquear y retener fondos de cuentas corrientes y/o ahorros, pólizas de acumulación o cualquier tipo de inversión  que tuviere la/el coactivada/o hasta por un valor de la CANTIDAD DE MIL QUINIENTOS CINCUENTA Y NUEVE CON 94/100  DOLARES AMERICANOS (USD $1559,94), para lo cual se oficiará a la Superintendencia de Bancos y a la Superintendencia de Economía Popular y Solidaria para lo cual se oficiará a la Superintendencia de Bancos y a la Superintendencia de Economía Popular y Solidaria.- 3): Prohibir la enajenación de los vehículos de propiedad de la/el coactivada/o, para lo cual se oficiará a la Agencia Nacional de Transito.- 4): Prohibir la enajenación de los bienes inmuebles que se hallaren inscritos a nombre de la/el coactivada/o en el Registro de la Propiedad del Cantón San Vicente.- 5) Poner en conocimiento al Ministerio de Trabajo que el/la coactivado/a es deudor del GAD Municipal de San Vicente, a fin que registre el impedimento de ejercer cargo público.-Actúe en calidad de Secretario de este Órgano Ejecutor Abogada Jessica Karina Zambrano Pincay; JUZGADO DE COACTIVA DEL GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DE SAN VICENTE. El GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL SAN VICENTE se reserva el derecho expreso de continuar acciónales legales en contra de los obligados solidarios y subsidiarios conforme a las leyes, estatutos y reglamentos vigentes. Notifíquese al coactivado, con el contenido del acto administrativo conforme lo previsto en el Art. 280 del Código Orgánico Administrativo. Se previene al coactivado(a), de la obligación de señalar correo electrónico y/o casillero judicial para posteriores notificaciones. - CÚMPLASE, OFICIESE Y NOTIFÍQUESE.</p>


    <div style="page-break-after: always;"></div>
    <div class="fecha">
        San Vicente, {{ fechaFormatoTexto() }}
    </div>
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


    @endphp
   
    
    <!-- <table class="cabecera" style="font-family: Arial;font-size:12px !important">
        <tr>
            <td class="logo">
                <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="100">
            </td>
            <td class="encabezado">
                <p style="font-size:16px !important"><b>GAD MUNICIPAL DEL CANTÓN SAN VICENTE<br> DIRECCION FINANCIERA TESORERIA
                <br> SAN VICENTE - MANABI - ECUADOR </b></p>
            </td>
                <td class="logo">
            </td>
        </tr>
    </table> -->
    <div class="" style="font-family: Arial; font-size:11; margin-top: 20px;">
        <div class="">
            <center>
               
              <h3 style="margin: 0; padding: 0;">
                 Liquidación de predios {{ $ubicacion == 1 ? 'urbanos' : 'rurales' }}
            </h3>

            <h3 style="margin: 4px 0 0 0; padding: 0;">
                Fecha liquidación {{date('d/m/Y')}}
            </h3>
 
            </center>
        </div>
    </div>
    <div style="width:100%;margin-top:10px;margin-bottom:5px;" class="liquidaciones">
      
        <table class="blueTable">
            <thead>
            <tr>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">No. Predio </th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Año</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Cod. Predial</th>
           
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Emisión</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Descuento</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Interes</th>
            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Recargo</th>

            <th style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">Total</th>
            </tr>
            </thead>
            <tbody>
                @isset($DatosLiquidacion)
                    @php
                        $total_final_emi=0;
                        $total_final_des=0;
                        $total_final_int=0;
                        $total_final_rec=0;
                        $total_final_deuda=0
                    @endphp
                    @foreach ($DatosLiquidacion as $key=> $data)
            
                        @php
                            $total_emi=0;
                            $total_des=0;
                            $total_int=0;
                            $total_rec=0;
                            $total_final=0
                        @endphp
                        @foreach ($data as $item)
                        @php
                            $subtotal_emi = (float) str_replace(',', '', $item->subtotal_emi);
                            $intereses = (float) str_replace(',', '',$item->intereses);
                            
                            $total=$subtotal_emi+$intereses+$item->recargo;
                            // $total = (float)$item->subtotal_emi
                            // + (float)$item->intereses
                            // + (float)$item->recargo;

                            $total=$total + $item->descuento;
                            $anio=explode("-",$item->num_titulo);

                            $num_matricula="";
                            if($ubicacion==1){
                                $num_matricula = $item->num_predio;
                            }
                            $nombre_persona=$item->nombre_per;
                            if(is_null($item->nombre_per)){
                                $nombre_persona=$item->nombre_contr1;
                            }
                        @endphp
                        <tr>
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$num_matricula}}</td>
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$anio[0]}}</td>
                            <td style="border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{$item->clave}}</td>
                        
                           
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{ number_format($subtotal_emi,2)}}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{ number_format($item->descuento ?? 0.00, 2) }}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{number_format($intereses,2)}}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{ number_format($item->recargo ?? 0.00, 2) }}</td>
                            <td style="text-align:right;border-top: 0px;border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;">{{number_format($total,2)}}    </td>
                            </tr>
                            @php
                                $total_emi=$total_emi +$subtotal_emi;
                                $total_des=$total_des +$item->descuento;
                                $total_int=$total_int +$intereses;
                                $total_rec=$total_rec +$item->recargo;

                            @endphp
                        @endforeach
                        <tfoot>
                            @php
                                $total_final= $total_emi + $total_int + $total_rec;
                                $total_final=$total_final +$total_des;

                            @endphp
                            <tr style="font-size:9px !important;line-height:5px" style="">

                                <td  colspan="3"style="font-size:9px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                                    <b></b>
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:9px">
                                    <b>$ {{number_format($total_emi,2)}}</b>                            
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:9px">
                                    <b>$ {{number_format($total_des,2)}}</b>                            
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:9px">
                                    <b>$ {{number_format($total_int,2)}}</b>                            
                                </td>

                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black; text-align: right; font-size:9px">
                                    <b>$ {{number_format($total_rec,2)}}</b>                            
                                </td>
                            
                            
                                <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;; border-color:black;text-align: right; font-size:9px">
                                    <b>$ {{number_format($total_final,2)}}</b>                            
                                </td>
                                
                            
                            </tr>
                            <tr>
                                <td colspan="8" style="height:9px;border:0;"></td>
                            </tr>
                             @php
                                $total_final_emi=$total_final_emi + $total_emi;
                                $total_final_des=$total_final_des+$total_des;
                                $total_final_int=$total_final_int+$total_int;
                                $total_final_rec=$total_final_rec+$total_rec;
                                $total_final_deuda=$total_final_deuda+$total_final;

                            @endphp
                        </tfoot>
                
                    @endforeach
                @endisset
            </tbody>
            <tfoot>
                 <tr style="font-size:10px !important;line-height:5px" style="">

                    <td  colspan="3"style="font-size:9px;border: 0px; border-color: #D3D3D3;  text-align: right;">
                        <b>TOTALES</b>
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-top:0px;text-align: right; font-size:9px">
                        <b>$ {{number_format($total_final_emi,2)}}</b>                            
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px; border-top:0px;text-align: right; font-size:9px">
                        <b>$ {{number_format($total_final_des,2)}}</b>                            
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-top:0pxk;text-align: right; font-size:9px">
                        <b>$ {{number_format($total_final_int,2)}}</b>                            
                    </td>

                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px; border-top:0px; text-align: right; font-size:9px">
                        <b>$ {{number_format($total_final_rec,2)}}</b>                            
                    </td>
                
                
                    <td style="border-left: 0px; border-bottom: 0px;border-center:0px;border-right:0px;border-top:0px;text-align: right; font-size:9px">
                        <b>$ {{number_format($total_final_deuda,2)}}</b>                            
                    </td>
                    
                
                </tr>

            </tfoot>
        </table>
           
    </div>

   
<!-- 
    <div class="firma." style="margin-botton:50px !important">
       
    </div> -->

</body>
</html>
