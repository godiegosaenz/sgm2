<!DOCTYPE html>
<html>

<head>
    <title></title>

    <style type="text/css">
        @page {
            margin-top: 4em;
            margin-left: 1em;
            margin-right: 1em;
            margin-bottom: 3em;
        }

        header {
            position: fixed;
            top: -49px;
            left: 0px;
            right: 0px;
            background-color: white;
            height: 60px;
            margin-right: 75px;
            margin-left: -9px
        }



        .ltable {
            border-collapse: collapse;
            font-family: sans-serif;
        }

        td,
        th

        /* Asigna un borde a las etiquetas td Y th */
            {
            border: 1px solid rgb(95, 92, 92);
            /* border: 1px solid #000; */
            /* padding: 10px;
            text-align: center; */
        }

        table {
            border-collapse: collapse;
            width: 100%;
            position: relative;
            /* Necesario para posicionamiento absoluto en td */
        }

        td {
            border: 1px solid #000;
            /* padding: 10px; */
            text-align: center;
            position: relative;
            /* Necesario para que el triángulo se posicione dentro de td */
        }

        .sinbordeencabezado

        /* Asigna un borde a las etiquetas td Y th */
            {
            border: 0px solid rgb(95, 92, 92);
        }

        .fuenteSubtitulo {
            font-size: 12px;
        }

        .pad {
            padding-left: 5px;
            padding-right: 5px;
        }

        .custom-table {
            width: 50%;
            /* Ajusta el ancho según sea necesario */
            background-color: white;
            /* Fondo blanco */
            /* border: 1px solid rgb(95, 92, 92); Borde negro */
            border-collapse: collapse;
            /* Asegura que los bordes de las celdas se colapsen */
        }

        .custom-table th,
        .custom-table td {
            /* border: 1px solid rgb(95, 92, 92); Borde negro para las celdas */
            padding: 1px;
            /* Espaciado interno de las celdas */
        }

        .v-invertida {
            width: 0;
            height: 0;
            border-left: 10px solid transparent;
            border-right: 10px solid transparent;
            border-top: 20px solid black;
            margin: 50px;
            /* Para separar del borde del documento */
        }

        .evolucion-texto {
            white-space: pre-wrap;
            /* Respetar los saltos de línea */
            word-wrap: break-word;
            /* Evitar que el texto se desborde */
            margin: 0;
            /* Eliminar cualquier margen extra */
            padding: 0;
            /* Eliminar cualquier padding extra */
        }
    </style>
    <style type="text/css">
        .preview_firma {
            width: 156px;
            border: solid 1px #000;
        }

        .img_firma {
            width: 80px;
        }

        .btn_azul {
            color: #fff;
            background-color: #337ab7;
            border-color: #2e6da4;
        }

        .hr {
            page-break-after: always;
            border: none;
            margin: 0;
            padding: 0;
        }

        .encabezado {
            /*border: 1px solid;*/
            text-align: center;
            font-size: 10px;

        }

        .encabezado1 {
            text-align: center;
            padding-top: 1px;
            font-size: 10px;
        }

        .encabezado2 {
            text-align: center;
            padding-top: 10px;
            padding-bottom: 10px;
            font-size: 10px;
        }

        p {
            line-height: 1px;
        }

        .circle_centrado {
            width: 10px;
            /* Ancho del círculo */
            height: 10px;
            /* Altura del círculo */
            background-color: red;
            /* Color del círculo */
            border-radius: 50%;
            /* Hace el div redondo */
            position: absolute;
            /* Posicionamiento absoluto */
            top: 0px;
            /* Ajusta según sea necesario */
            /* left: 50%; */
            /* transform: translateX(-50%); Centra el triángulo horizontalmente */
            text-align: center !important;
            margin-left: 3px;
            /* top: -6px; */
        }

        .circle_ralla {
            width: 10px;
            /* Ancho del círculo */
            height: 10px;
            /* Altura del círculo */
            background-color: red;
            /* Color del círculo */
            border-radius: 50%;
            /* Hace el div redondo */
            position: absolute;
            /* Posicionamiento absoluto */
            top: 0px;
            /* Ajusta según sea necesario */
            /* left: 50%; */
            /* transform: translateX(-50%); Centra el triángulo horizontalmente */
            text-align: center !important;
            margin-left: 3px;
            top: -6px;
        }

        .triangle_centrado {
            width: 0;
            height: 0;
            border-left: 10px solid transparent;
            border-right: 10px solid transparent;
            border-bottom: 10px solid #191cbd;
            /* Color del triángulo */
            position: absolute;
            /* Posicionamiento absoluto */
            top: 0px;
            /* Ajusta según sea necesario */
            /* left: 50%; */
            transform: translateX(-50%);
            /* Centra el triángulo horizontalmente */

        }

        .triangle_ralla {
            width: 0;
            height: 0;
            border-left: 10px solid transparent;
            border-right: 10px solid transparent;
            border-bottom: 10px solid #191cbd;
            /* Color del triángulo */
            position: absolute;
            /* Posicionamiento absoluto */
            top: -7px;
            /* Ajusta según sea necesario */
            /* left: 50%; */
            transform: translateX(-50%);
            /* Centra el triángulo horizontalmente */
        }

        .triangle_centrado_td {
            width: 0;
            height: 0;
            border-left: 6px solid transparent;
            border-right: 6px solid transparent;
            border-bottom: 10px solid #191cbd;
            /* Color del triángulo */
            position: absolute;
            /* Posicionamiento absoluto */
            top: 0px;
            /* Ajusta según sea necesario */
            /* left: 50%; */
            transform: translateX(-50%);
            /* Centra el triángulo horizontalmente */
            margin-left: 3px;

        }

        .triangle_ralla_td {
            width: 0;
            height: 0;
            border-left: 6px solid transparent;
            border-right: 6px solid transparent;
            border-bottom: 10px solid #191cbd;
            /* Color del triángulo */
            position: absolute;
            /* Posicionamiento absoluto */
            top: -7px;
            /* Ajusta según sea necesario */
            /* left: 50%; */
            transform: translateX(-50%);
            /* Centra el triángulo horizontalmente */
            margin-left: 3px;
        }

        .vertical-text {
            writing-mode: vertical-rl !important;
            /* vertical-rl significa vertical de derecha a izquierda */
            text-align: center !important;
            /* Alineación opcional del texto dentro del td */
            white-space: nowrap !important;
            /* Evita que el texto se ajuste automáticamente */
        }

        .vertical{
            /* writing-mode: vertical-rl;
            /* transform: rotate(270deg); */
            /* text-align: center !important;
            white-space: nowrap; */ */


            /* writing-mode: vertical-rl;
            transform: rotate(270deg);
            text-align: center;
            font-weight: bold;
            white-space: nowrap; */

             /* writing-mode: vertical-rl;
    text-align:center;
    font-weight:bold;
    white-space:nowrap;
    height:70px;

     */
            writing-mode: vertical-rl;
            text-orientation: mixed;
            white-space: nowrap;
            text-align:center;
            vertical-align:middle;
            height:80px; /* MUY IMPORTANTE */
            font-weight:bold;
        }

        .small{
            font-size:9px;
        }

    </style>


</head>

<body>

    <header>

        <table class="ltable " width="100%">
            <tr style="font-size: 14px !important;">
                <td height="10px" width="50%" colspan="6" style="border: 0px; text-align:center">
                    <img src="logo4.png" width="90px" height="65px">
                </td>


                <td height="10px" width="50%" colspan="6" style="border: 0px; text-align:right">
                    <strong>GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DEL CANTON SAN VICENTE</strong>
                </td>


            </tr>

        </table>
    </header>


    <div style="margin-top:20px;">
        <table class="ltable" width="100%" style="">

            <tr style="font-size: 10px !important; background-color: white;line-height:10px; ">

                <td colspan="68"
                    style="border-color:rgb(95, 92, 92); text-align: left; line-height:15px; background-color:#afaef3">
                    &nbsp; <b>A. DATOS DEL ESTABLECIMIENTO - DATOS DEL USUARIO</b></td>

            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="12"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">
                    <b> INSTITUCIÓN DEL SISTEMA</b>
                </td>

                <td colspan="13"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">
                    <b>RUC</b>
                </td>

                <td colspan="18"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">
                    <b> ESTABLECIMIENTO CENTRO DE TRABAJO</b>
                </td>

                <td colspan="15"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">
                    <b> NÚMERO DE HISTORIA CLÍNICA ÚNICA</b>
                </td>

                <td colspan="10"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">
                    <b> NÚMERO DE ARCHIVO</b>
                </td>


            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="12"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; border-bottom:0px">
                    GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DE SAN VICENTE
                </td>

                <td colspan="13"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; border-bottom:0px">
                    136001485001
                </td>

                <td colspan="18"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; border-bottom:0px">
                    GOBIERNO AUTONOMO DESCENTRALIZADO MUNICIPAL DE SAN VICENTE
                </td>

                <td colspan="15"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; border-bottom:0px">

                </td>

                <td colspan="10"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; border-bottom:0px">

                </td>



            </tr>
        </table>

        <table class="ltable" width="100%" style="padding-bottom:2px !important">

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="17"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; 
                    background-color:rgb(200, 247, 203)">
                    <b> PRIMER APELLIDO</b>
                </td>

                <td colspan="17"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; 
                    background-color:rgb(200, 247, 203)">
                    <b> SEGUNDO APELLIDO</b>
                </td>
                <td colspan="17"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; 
                    background-color:rgb(200, 247, 203)">
                    <b> PRIMER NOMBRE</b>
                </td>
                <td colspan="17"
                    style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; 
                    background-color:rgb(200, 247, 203)">
                    <b> SEGUNDO NOMBRE</b>
                </td>


            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    FDFFD
                </td>

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    DSDS
                </td>

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    CXC
                </td>

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    CXC
                </td>



            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="30" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">ATENCIÓN PRIORITARIA</td>
                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">SEXO</td>
                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">FECHA DE NACIMIENTO</td>
                <td colspan="10" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Edad</td>
                <td colspan="5" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">GRUPO SANGUÍNEO</td>
                <td colspan="10" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">LATERALIDAD</td>

            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="6"  style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Embarazada</td>

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Persona con Discapacidad</td>

                <td colspan="6"  style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">E. Catastrofica</td>

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Lactancia</td>

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Adulto Mayor</td>


                <td colspan="3"  style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Hombre</td>

                <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Mujer</td>

                <td colspan="3"  style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Año</td>

                <td colspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Mes</td>

                <td colspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Dia</td>




            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                 <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                 <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                 <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="2" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="10" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="10" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    xxx
                </td>



            </tr>
           
        </table>
    </div>

    <div style="margin-top:12px;">
        <table class="ltable"  width="100%" style="">
            
            <tr style="font-size: 10px !important; background-color: white;line-height:10px; "> 
                
                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:15px; background-color:#afaef3">&nbsp;  <b>B. MOTIVO CONSULTA</b></td>
            
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Puesto de Trabajo CIUO</td>

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    xxx
                </td>

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Fecha de Atención aaaa/mm/dd</td>

                <td colspan="17" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    xxx
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="22" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Fecha de Ingreso al trabajo   aaaa/mm/dd</td>


                <td colspan="22" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Fecha de Reintegro aaaa/mm/dd</td>

                <td colspan="24" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; background-color:rgb(200, 247, 203)">Fecha del Último día laboral/salida aaaa/mm/dd</td>

               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="22" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="22" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    
                </td>

                <td colspan="24" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    D
                </td>

              
            </tr>

             <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px; ">
                    AA
                </td>

               

            </tr>

            

           
        </table>

      
        
    </div>

    <div style="margin-top:12px;">
        <table class="ltable"  width="100%" style="">
            
            <tr style="font-size: 10px !important; background-color: white;line-height:10px; "> 
                
                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:15px; background-color:#afaef3">&nbsp;  <b>C. ANTECEDENTES PERSONALES</b></td>
            
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b>ANTECEDENTES CLÍNICOS Y QUIRÚRGICOS</b></td>
               
            </tr>
            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    FF
                </td>
              
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b>ANTECEDENTES FAMILIARES</b></td>
               
            </tr>
            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    FF
                </td>
              
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)">Condición especial para las atenciónes de urgencia,emergencia,y tratamiento médico (referido por el paciente).</td>
               
            </tr>

           

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td rowspan="2" colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                    En caso de requerir transfusiones autoriza:
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px;background-color:rgb(192, 232, 245)"  class="center">SI</td>
                <td  colspan="1" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px;background-color:rgb(192, 232, 245)" class="center">NO</td>

                <!-- Pregunta 2 -->
                <td colspan="10" rowspan="2"style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                    Se encuentra bajo algún tratamiento hormonal
                </td>

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)" class="center">SI</td>

                <td colspan="39" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)" class="center">
                    ¿Cuál describir?
                </td>

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)" class="center">NO</td>
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; "></td>
                <td  colspan="1" style="border-color:rgb(95, 92, 92); text-align: center; line-height:8px;" class="center"></td>

               
                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; " class="center">x</td>

                <td colspan="39" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; " class="center">
                    .......
                </td>

                <td colspan="5"style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; " class="center">x</td>
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b> ANTECEDENTES GINECO OBSTÉTRICOS</b></td>
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td rowspan="2" colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     FECHA DE LA ULTIMA MENSTRUACIÓNN aaaa /mm/dd
                </td>

                <td rowspan="2" colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     GESTAS
                </td>

                <td rowspan="2" colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     PARTOS
                </td>


                <td rowspan="2" colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      CESÁREAS
                </td>

                <td rowspan="2" colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     ABORTOS
                </td>

                 <td rowspan="1" colspan="59" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      MÉTODO DE PLANIFICACION FAMILIAR
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     Si
                </td>

                <td colspan="43" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     ¿Cual?
                </td>

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     No
                </td>

                <td colspan="10" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     No responde
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     ..
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="43" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="10" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     EXÁMENES REALIZADOS ¿CUAL?
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     TIEMPO
                </td>

                <td colspan="61" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      Registrar resultado únicamente si interfiere con la actividad laboral y previa autorización del titular
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="6" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                    --
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                   
                </td>

                <td colspan="61" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                     
                </td>
            </tr>


             <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b> ANTECEDENTES REPRODUCTIVOS MASCULINOS</b></td>
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td rowspan="2" colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                    EXÁMENES REALIZADOS ¿CUAL?
                </td>

                <td rowspan="2" colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     TIEMPO
                </td>

                <td rowspan="2" colspan="9" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     Registrar resultado únicamente si interfiere con la actividad laboral y previa autorización del titular
                </td>


               
                 <td rowspan="1" colspan="53" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      MÉTODO DE PLANIFICACION FAMILIAR
                </td>
            </tr>


             <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     Si
                </td>

                <td colspan="37" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     ¿Cual?
                </td>

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     No
                </td>

                <td colspan="10" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     No responde
                </td>
            </tr>

             <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="5" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     TIEMPO
                </td>

                <td colspan="9" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>

                 <td colspan="42" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>

                <td colspan="10" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>
            </tr>



            

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b> CONSUMO DE SUSTANCIAS</b></td>

                <td colspan="9" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b> ESTILO DE VIDA</b></td>

                <td colspan="52" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(200, 247, 203)"><b> CONDICION PREEXISTENTE</b></td>
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     TIEMPO DE CONSUMO (meses)
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      EX CONSUMIDOR
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                     TIEMPO DE ABSTINENCIA (meses)
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      NO CONSUME
                </td>

                <td colspan="1" rowspan="4" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    Actividad Fisica
                </td>

                 <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      Cual
                </td>

                 <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      Tiempo
                </td>

                <td colspan="4" rowspan="4" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      Medicacion Habitual
                </td>

                <td colspan="39" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      Cual
                </td>

                <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; background-color:rgb(192, 232, 245)">
                      Tiempo
                </td>
            </tr>


            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     TABACO
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

               

                 <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

                 <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

               

                <td colspan="39" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

                <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     ALCOHOL
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

               

                 <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

                 <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

               

                <td colspan="39" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

                <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="3" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     OTRAS: ¿Cuál?
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                    
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

                <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     
                </td>

               

                 <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

                 <td colspan="1" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

               

                <td colspan="39" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                      
                </td>

                <td colspan="8" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">
                      
                </td>
            </tr>

             <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; ">
                     Observacion
                </td>
            </tr>

           
            

        </table>

      
        
    </div>

    <div style="margin-top:12px;">
        <table class="ltable"  width="100%" style="">
            
            <tr style="font-size: 10px !important; background-color: white;line-height:10px; "> 
                
                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:15px; background-color:#afaef3">&nbsp;  <b>D. ENFERMEDAD O PROBLEMA  ACTUAL</b></td>
            
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="68" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;">.......</td>
               
            </tr>
        </table>
    </div>

    <div style="margin-top:12px;">
        <table class="ltable"  width="100%" style="">
            
            <tr style="font-size: 10px !important; background-color: white;line-height:10px; "> 
                
                <td colspan="63" style="border-color:rgb(95, 92, 92); text-align: left; line-height:15px; background-color:#afaef3">&nbsp;  <b>E. CONSTANTES VITALES Y ANTROPOMETRÍA </b></td>
            
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(200, 247, 203)">TEMPERATURA (°C)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(200, 247, 203)">PRESIÓN ARTERIAL (mmHg)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(200, 247, 203)">FRECUENCIA CARDIACA (Lat/min)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 background-color:rgb(200, 247, 203)">FRECUENCIA RESPIRATORIA (fr/min)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 background-color:rgb(200, 247, 203)">SATURACIÓN DE OXÍGENO (O2%)</td>

              

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 background-color:rgb(200, 247, 203)">PESO (Kg)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 background-color:rgb(200, 247, 203)">TALLA (cm)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 background-color:rgb(200, 247, 203)">ÍNDICE DE MASA CORPORAL (kg/m2)</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 background-color:rgb(200, 247, 203)">PERÍMETRO ABDOMINAL (cm)</td>


               
            </tr>

             <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                ">....</td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                "></td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                "></td>

              

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                "></td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                "></td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                "></td>

                <td colspan="7" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px;
                 "></td>


               
            </tr>
        </table>
    </div>

    <div style="margin-top:12px;">
        <table class="ltable"  width="100%" style="">
            
            <tr style="font-size: 10px !important; background-color: white;line-height:10px; "> 
                
                <td colspan="210" style="border-color:rgb(95, 92, 92); text-align: left; line-height:15px; background-color:#afaef3">&nbsp;  <b>F. EXAMEN FÍSICO REGIONAL </b></td>
            
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="210" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(200, 247, 203)">REGIONES</td>
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="20" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)">1. Piel</td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)">a. Cicatríces</td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                <td colspan="10" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)"> 3. Oído</td>

                 <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                 background-color:rgb(192, 232, 245)
                "> a. C. auditivo externo</td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                <td colspan="10" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)"> 5. Nariz</td>

                 <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                 background-color:rgb(192, 232, 245)
                "> a. Tabique</td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                  <td colspan="10" rowspan="2" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)">  8. Tórax</td>

                 <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                 background-color:rgb(192, 232, 245)
                ">  a. Pulmones</td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                

                
               
            </tr>

            <tr style="font-size: 7px !important; background-color: white;line-height:8px; ">

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)">c. Piel  y Faneras</td>

                 <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)"> b. Pabellón</td>

                  <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                 <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)"> b. Cornetes</td>

                  <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                background-color:rgb(192, 232, 245)"> b. Corazón</td>

                  <td colspan="20" style="border-color:rgb(95, 92, 92); text-align: left; line-height:8px; 
                "></td>

                
               
            </tr>

            

          

          
        </table>
    </div>




    <script type="text/php">
    if ( isset($pdf) ) {
        $pdf->page_script('
            $font = $fontMetrics->get_font("Arial, Helvetica, sans-serif", "normal");
            $pdf->text(490, 820, "Página $PAGE_NUM de $PAGE_COUNT", $font, 9); 
        ');
    }
</script>
</body>

</html>