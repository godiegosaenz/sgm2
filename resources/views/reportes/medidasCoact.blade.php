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
            $num_oficio1=$medidas->num_oficio1;
            $num_oficio2=$medidas->num_oficio2;
            $num_oficio3=$medidas->num_oficio3;
            $predio_txt="";
            if($lugar_predio=="Urbano"){
                $predio_txt="Predios Urbanos";
            }else{
                $predio_txt="Predios Rurales";
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

            $predio_txt="";
            if($lugar_predio=="Urbano"){
                $predio_txt="Predios Urbanos";
            }else{
                $predio_txt="Predios Rurales";
            }
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
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN<br>
        SAN VICENTE <br><br>PROCESO No. {{ str_pad($secuencial->num_proceso, 6, "0", STR_PAD_LEFT) }}</p>
        <p style="margin: 0; line-height: 1.2;">MEDIDA CAUTELAR</p>
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 28px;">
        <b>VISTOS: San Vicente, {{ fechaFormatoTexto() }},</b> en lo principal, de la liquidación obtenida del sistema gestión municipal, misma que se incorpora al proceso se deprende que el/la contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I/RUC.  <strong>{{ $ci_ruc }}</strong>; adeuda al Gobierno Autónomo Descentralizado Municipal del cantón San Vicente la suma de <strong>{{ numeroEnLetras($total_final) }}</strong>, por el concepto de {{ $predio_txt }}, la cual corresponde a la 
      
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
        ,sin que  a la presente fecha haya pagado la obligación tributaria liquida, determinada y de plazo vencido que actualmente exigible, por lo que de conformidad como lo establece el Art. 281 del Código Orgánico Administrativo se disponer las  siguientes medidas cautelares:  1): Retener fondos de cuentas corrientes y/o ahorros, pólizas de acumulación o cualquier tipo de inversión  que tuviere la/el coactivada/o hasta por un valor de <strong>{{ numeroEnLetras($total_final) }}</strong>, para lo cual se oficiará a la Superintendencia de Bancos y a la Superintendencia de Economía Popular y Solidaria para lo cual se oficiará a la Superintendencia de Bancos y a la Superintendencia de Economía Popular y Solidaria.- 2): Prohibir la enajenación de los vehículos de propiedad de la/el coactivada/o, para lo cual se oficiará a la Agencia Nacional de Transito.- 3): Prohibir la enajenación de los bienes inmuebles que se hallaren inscritos a nombre de la/el coactivada/o en el Registro de la Propiedad del Cantón San Vicente.- 4) Poner en conocimiento al Ministerio de Trabajo que el/la coactivado/a es deudor del GAD Municipal de San Vicente, a fin que registre el impedimento de ejercer cargo público.-Actúe en calidad de Secretaria de este Órgano Ejecutor {{ $secr }};<strong> JUZGADO DE COACTIVA DEL GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DE SAN VICENTE. - CÚMPLASE, OFICIESE Y NOTIFÍQUESE.</strong>
       
     

    <div style="margin-bottom:28px; margin-top: 120px;">
        <center>
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{$funcionarios->tesorera}}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>TESORERA MUNICIPAL</strong>
            </p>
        </center>
    </div>

    <div style="page-break-after: always;"></div>

    <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">DEPARTAMENTO DE COACTIVA DEL GOBIERNO AUTONOMO DESCENTRALIZADO<br> MUNICIPAL DEL CANTON SAN VICENTE
        
        
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 28px;">
        <span>Oficio N° GADMCSV-COAC-JKZP-{{str_pad($num_oficio1, 3, "0", STR_PAD_LEFT) }}-2026</span><br>
        <span>San Vicente {{ fechaFormatoTexto() }}.</span><br><br>
        <b>Señores:</b><br><br><br>

        <b>SUPERINTENDENCIA DE BANCOS Y COMPAÑIAS</b><br><br>

        <span>Presente. -</span><br><br>

        <span>Dentro del Proceso Coactivo No {{ str_pad($secuencial->num_proceso, 6, "0", STR_PAD_LEFT) }} que el Gobierno Autónomo Descentralizado Municipal del Cantón San Vicente sigue en contra del Coactivado <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I/RUC.  <strong>{{ $ci_ruc }}</strong>; mediante providencia se ha dispuesto lo siguiente:
        </span><br><br><br>

        <span>
            <strong><em>“...Ofíciese a la Superintendencia de Bancos y Compañías a fin de se proceda a registrar Bloqueo y Retención de Fondos de Cuentas Corrientes, Ahorros y/o pólizas de acumulación o cualquier tipo de inversión que tuviera el coactivado por las circunstancias de que el coactivado es deudor de esta Institución...”</em></strong>
        </span> <br><br><br><br>

        <span>Particular que llevo a su comunicación parar los fines de ley.</span><br><br>
        <span>Atentamente,</span><br><br>

    </p>
       
    <div style="margin-bottom:28px; margin-top: 120px;">
        <center>
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{$funcionarios->secretario}}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>ANALISTA JURIDICO Y DE COACTIVA DEL GAD SAN VICENTE</strong>
            </p>
        </center>
    </div>

    <div style="page-break-after: always;"></div>

    <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">ORGANO EJECUTOR DE COACTIVA DEL GAD MUNICIPAL DEL CANTÓN
       
        <br>  SAN VICENTE<br><br>PROCESO No. {{ str_pad($secuencial->num_proceso, 6, "0", STR_PAD_LEFT) }}</p>
        <p style="margin: 0; line-height: 1.2;">MEDIDA CAUTELAR</p>
        
        
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 28px;">
        <span>Oficio N° GADMCSV-COAC-JKZP-{{str_pad($num_oficio2, 3, "0", STR_PAD_LEFT) }}-2026</span><br>
        <span>San Vicente {{ fechaFormatoTexto() }}.</span><br><br>
        <b>Señores:</b><br><br><br>

        <b>REGISTRADOR DE LA PROPIEDAD DEL CANTON SAN VICENTE</b><br><br>

        <b>CIUDAD</b><br><br>

        <b>De mi consideración. -</b><br><br>

        <b>Dentro del PROCESO No. {{ str_pad($secuencial->num_proceso, 6, "0", STR_PAD_LEFT) }}, que se sigue en contra del contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I/RUC.  <strong>{{ $ci_ruc }}</strong>; se ha dispuesto lo siguiente</b><br><br>


       <span style="margin-left:20px; display:block;">
            3): Prohibir la enajenación de los bienes inmuebles que se hallaren inscritos a nombre de la/el coactivada/o en el Registro de la Propiedad del Cantón San Vicente.
        </span><br><br>

        <span>
            Por lo expuesto solicito a usted muy comedidamente proceda a la inscripción de lo aquí ordenado en los libros y registro a su cargo.
        </span>

       
    </p>
       
    <div style="margin-bottom:28px; margin-top: 120px;">
        <center>
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{$funcionarios->secretario}}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>ANALISTA JURIDICO Y DE COACTIVA DEL GAD SAN VICENTE</strong>
            </p>
        </center>
    </div>

    <div style="page-break-after: always;"></div>

    <div class="titulo" style="font-size:13px !important">
        <br>
        <p style="margin: 0; line-height: 0.8;">DEPARTAMENTO DE COACTIVA DEL GOBIERNO AUTONOMO DESCENTRALIZADO<br> MUNICIPAL DEL CANTON SAN VICENTE
        
        
    </div>

    <p style="font-size: 14px; text-align: justify; line-height: 1; margin-top: 28px;">
        <span>Oficio N° GADMCSV-COAC-JKZP-{{str_pad($num_oficio3, 3, "0", STR_PAD_LEFT) }}-2026</span><br>
        <span>San Vicente {{ fechaFormatoTexto() }}.</span><br><br>
        <b>Señor:</b><br><br><br>

        <b>JEFE DE LA AGENCIA MUNICIPAL DE TRÁNSITO DEL CANTON SAN VICENTE</b><br><br>

        <b>CIUDAD</b><br><br>

        <b>De mi consideración. -</b><br><br>

        <b>Dentro del PROCESO No. {{ str_pad($secuencial->num_proceso, 6, "0", STR_PAD_LEFT) }}, que se sigue en contra del contribuyente <strong>{{ strtoupper($nombre_persona) }} </strong> con C.I/RUC.  <strong>{{ $ci_ruc }}</strong>; se ha dispuesto lo siguiente</b><br><br>


       <span style="margin-left:20px; display:block;">
            2): Prohibir la enajenación de los vehículos de propiedad de la/el coactivada/o, para lo cual se oficiará a la Agencia Nacional de Tránsito.
        </span><br><br>

        <span>
            Por lo expuesto solicito a usted muy comedidamente proceda a la inscripción de lo aquí ordenado en los libros y registro a su cargo.
        </span><br><br>

        <span>
           Particular que llevo a su comunicación parar los fines de ley.
        </span><br><br>

        <span>
           Atentamente.
        </span>

       
    </p>
       
    <div style="margin-bottom:28px; margin-top: 120px;">
        <center>
            <p style="margin: 0; line-height: 0.9;">
                <strong>{{$funcionarios->secretario}}</strong>
            </p>
            <p style="margin: 0; line-height: 0.9;">
                <strong>ANALISTA JURIDICO Y DE COACTIVA DEL GAD SAN VICENTE</strong>
            </p>
        </center>
    </div>
  
</body>
</html>
