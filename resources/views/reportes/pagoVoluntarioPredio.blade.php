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
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE</p>
        <p style="margin: 0; line-height: 1.2;">REQUERIMIENTO DE PAGO VOLUNTARIO</p>
    </div>

    <div class="fecha">
        San Vicente, {{ fechaFormatoTexto() }}
    </div>
    @php
        $monto=4;
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
    <p><strong>Señor(a):</strong></p>
    <p>{{ $nombre_persona }}</p>
    <p>Ciudad.</p>

    <div class="contenido">
        <p>
            Para los fines pertinentes, me permito comunicarle a usted, la siguiente notificación respecto
            a los valores por concepto de pago de <strong>PREDIO MUNICIPAL</strong> que adeuda al
            <strong>GAD MUNICIPAL DEL CANTÓN SAN VICENTE</strong>, ubicado en  {{ $direcc_cont }}.
        </p>
        @php
            $total_final=0;
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
       
            <p>
                La obligación corresponde al predio con matrícula inmobiliaria
                <strong>{{ $key  }}</strong>, cuya deuda asciende a la cantidad de
                <strong>{{ numeroEnLetras($total) }}</strong>, correspondiente a los ejercicios fiscales <b>{{ $anio_uno }} - {{ $info->anio }}</b>.
            </p>
         @endforeach
        <p>
            Por cuanto su obligación asciende a la CANTIDAD DE <strong>{{ numeroEnLetras($total_final) }}</strong> con lo cual se verifica que la mencionada obligación es determinada y actualmente exigible.
        </p>
        <p>
            Acorde con lo previsto en el art. 271 del Código Orgánico Administrativo se realiza el requerimiento de <strong>PAGO VOLUNTARIO</strong> para lo cual se concede el plazo de diez (10) días hábiles, que se contaran a partir del día siguiente de la notificación en conocimiento, con la finalidad de que se acerque a las ventanillas de tesorería de esta institución para que proceda resolver con los valores adeudados más los intereses de ley correspondientes, es preciso recalcar que  existe la opción de realizar <strong>convenio de pago</strong> con una cuota inicial.
        </p>
        <p>
            <strong>En el evento de no proceder a la cancelación de lo adeudado se iniciará coactiva y en efecto se dictaran las medidas precautelares correspondientes, entre estas:</strong> Bloquear y retener fondos de cuentas corrientes y/o ahorros, prohibir la enagenación de los vehículos, bienes inmuebles que se hallaren inscritos a nombre que tuviere la/el coactivado que adeude al, <strong> GAD MUNICIPAL DEL CANTÓN SAN VICENTE.</strong> 
        </p>

        
    </div>

    <div class="firma">
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
    <br><br>
    
   
        <p style="margin: 0; line-height: 1.2;">
            <strong>Ab. Jessica Karina Zambrano Pincay</strong>
        </p>
        <p style="margin: 0; line-height: 1.2;">
            <strong>ANALISTA JURÍDICO Y COACTIVAS DEL GAD MUNICIPAL DEL CANTÓN SAN VICENTE</strong>
        </p>
    </div>

<!-- 
    <div class="firma." style="margin-botton:50px !important">
       
    </div> -->

</body>
</html>
