<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Impuesto Anual - Patentes Municipales</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            font-size: 12px;
        }
        .tabla {
            width: 100%;
            border-collapse: collapse;
        }
        td, th {
            height: 8px; /* Ajusta la altura según necesidad */
            padding: 3px; /* Espaciado interno para mejor visualización */
            vertical-align: middle; /* Asegura que el contenido esté centrado */
        }
        .tabla, .tabla th, .tabla td {
            border: 1px solid black;
        }
        .encabezado {
            text-align: center;
            font-size: 14px;
            font-weight: bold;
            background-color: #A7E1FB;
        }
        .titulo {
            text-align: center;
            font-weight: bold;
        }
        .negrita {
            font-weight: bold;
        }
        .centrado {
            text-align: center;
        }
        .resaltado {
            color: red;
            font-weight: bold;
        }
    </style>
</head>
<body>
    @php
        $titulo="";
        $color="";
        if($patente->estado==1){
            $titulo="IMPUESTO ANUAL DE PATENTES MUNICIPALES";
            $color="black";
        }else if($patente->estado==2){
            $titulo="SIMULACION IMPUESTO ANUAL DE PATENTES MUNICIPALES";
            $color="red";
        }
    @endphp
   
    <table class="tabla">
        <tr>
            <td rowspan="2" width="10%" class="centrado">
                <img src="logo4.png" alt="Logo" width="50">
            </td>
            <th colspan="10" class="encabezado">G.A.D. MUNICIPAL DEL CANTÓN SAN VICENTE</th>
        </tr>
        <tr>
            <td colspan="5" class="negrita">RUC: {{$patente->ruc}}</td>
            <td colspan="5" class="centrado">San Vicente, {{ $fecha_formateada }}</td>
        </tr>

        <tr>
            <td rowspan="2" colspan="7" width="10%" class="centrado">
                <b style="color:{{ $color }}">{{ $titulo }}</b>
            </td>

            <td rowspan="2"  colspan="2" width="10%" class="centrado">
                <b>TITULO DE <br> CREDITO</b>
            
            </td>
            <td colspan="2"><b>Nº</b></td>
        
        </tr>

        <tr>
            <td colspan="2"><b>POR: $. {{$patente->valor_patente}}</b></td>
        </tr>

        <tr>
            <td colspan="5"><b>CONTRIBUYENTE</b></td>
            <td colspan="3"><b>CEDULA-RUC</b></td>
            <td colspan="2"><b>DOMICILIO</b></td>
            <td colspan="1">{{$patente->canton}}</td>
        </tr>

        <tr>
            <td colspan="5">{{ $patente->contribuyente }}</td>
            <td colspan="3">{{$patente->ruc}}</td>
            <td colspan="3">{{strtoupper($patente->calle)}}</td>
        
        </tr>

        <tr>
            <td colspan="2"><b>RAZON SOCIAL</b></td>
            <td colspan="6">{{$patente->razon_social }}</td>
            <td colspan="2"></td>
            <td colspan="1"><b>VALOR RECIBIDO</b></td>

        
        </tr>
        @php
            $fecha = $patente->fecha_declaracion;
            $anio = substr($fecha, 0, 4);
        @endphp
        <tr>
            <td colspan="2"><b>PERIODO</b></td>
            <td colspan="6">AÑO {{$anio }}</td>
            <td colspan="2"><b>POR USD</b></td>
            <td colspan="1" style="text-align: right;">{{$patente->valor_patente}}</td>

        
        </tr>

        <tr>
            <td colspan="2" rowspan="2"><b>ACTIVIDADES</b></td>
            <td colspan="6"  rowspan="2">
                @foreach($patente->act as $data)
                    {{ $data }}</br>
                @endforeach
            </td>
            <td colspan="2"><b>RECARGOS</b></td>
            <td colspan="1"></td>

        
        </tr>

        <tr>
        
            <td colspan="2"><b>INTERESES</b></td>
            <td colspan="1"></td>

        
        </tr>

        <tr>
            <td colspan="2"><b>UBICACION</b></td>
            <td colspan="6">{{ strtoupper($patente->calle) }}</td>
            <td colspan="2"><b>A PAGAR</b></td>
            <td colspan="1" style="text-align: right;">{{$patente->valor_patente}}</td>

        
        </tr>

    </table>

    <table width="100%" style="margin-top: 30px;">
        <tr>
            <td width="5%"></td>
            <td width="20%">
                <br>
                <br>
               
                
                
                <hr>
                <p style="font-size:12px;text-align: center"><b>JEFE DE RENTAS</b></p>
            </td>
            <td width="5%"></td>

            <td width="5%"></td>
            
           
            <td width="20%">
                <br>
                <br>
            
                
                
                <hr>
                <p style="font-size:12px;text-align: center"><b>TESORERO MUNICIPAL</b></p>
            </td>
            <td width="5%"></td>

        </tr>
    </table>

    <table width="100%" style="margin-top: 10px;">
        <tr>
            <td width="5%"><b>LA PATENTE MUNICIPAL NO AUTORIZA EL FUNCIONAMIENTO DE LOCAL ALGUNO. POR LO TANTO, TRAMITE EL PERMISO DE FUNCIONAMIENTO RESPECTIVO </b></td>
            
        </tr>
    </table>

    @if(!is_null($patente->valor_activo_total) && $patente->es_activo==true)
        <div style="page-break-before: always; break-before: page;">
            @php
                $titulo="";
                $color="";
                if($patente->estado==1){
                    $titulo="IMPUESTO ANUAL DEL 1.5 POR MIL SOBRE LOS ACTIVOS TOTALES";
                    $color="black";
                }else if($patente->estado==2){
                    $titulo="SIMULACION IMPUESTO ANUAL DEL 1.5 POR MIL SOBRE LOS ACTIVOS TOTALES";
                    $color="red";
                }
            @endphp
    
            <table class="tabla">
                <tr>
                    <td rowspan="2" width="10%" class="centrado">
                        <img src="logo4.png" alt="Logo" width="50">
                    </td>
                    <th colspan="10" class="encabezado">G.A.D. MUNICIPAL DEL CANTÓN SAN VICENTE</th>
                </tr>
                <tr>
                    <td colspan="5" class="negrita">RUC: {{$patente->ruc}}</td>
                    <td colspan="5" class="centrado">San Vicente, {{ $fecha_formateada }}</td>
                </tr>

                <tr>
                    <td rowspan="2" colspan="7" width="10%" class="centrado">
                        <b style="color:{{ $color }}">{{ $titulo }}</b>
                    </td>

                    <td rowspan="2"  colspan="2" width="10%" class="centrado">
                        <b>TITULO DE <br> CREDITO</b>
                    
                    </td>
                    <td colspan="2"><b>Nº</b></td>
                
                </tr>

                <tr>
                    <td colspan="2"><b>POR: $. {{$patente->valor_activo_total}}</b></td>
                </tr>

                <tr>
                    <td colspan="5"><b>CONTRIBUYENTE</b></td>
                    <td colspan="3"><b>CEDULA-RUC</b></td>
                    <td colspan="2"><b>DOMICILIO</b></td>
                    <td colspan="1">{{$patente->canton}}</td>
                </tr>

                <tr>
                    <td colspan="5">{{ $patente->contribuyente }}</td>
                    <td colspan="3">{{$patente->ruc}}</td>
                    <td colspan="3">{{strtoupper($patente->calle)}}</td>
                
                </tr>

                <tr>
                    <td colspan="2"><b>RAZON SOCIAL</b></td>
                    <td colspan="6">{{$patente->razon_social }}</td>
                    <td colspan="2"></td>
                    <td colspan="1"><b>VALOR RECIBIDO</b></td>

                
                </tr>
                @php
                    $fecha = $patente->fecha_declaracion;
                    $anio = substr($fecha, 0, 4);
                @endphp
                <tr>
                    <td colspan="2"><b>PERIODO</b></td>
                    <td colspan="6">AÑO {{$anio }}</td>
                    <td colspan="2"><b>POR USD</b></td>
                    <td colspan="1" style="text-align: right;">{{$patente->valor_activo_total}}</td>

                
                </tr>

                <tr>
                    <td colspan="2" rowspan="2"><b>ACTIVIDADES</b></td>
                    <td colspan="6"  rowspan="2">
                        @foreach($patente->act as $data)
                            {{ $data }}</br>
                        @endforeach
                    </td>
                    <td colspan="2"><b>RECARGOS</b></td>
                    <td colspan="1"></td>

                
                </tr>

                <tr>
                
                    <td colspan="2"><b>INTERESES</b></td>
                    <td colspan="1"></td>

                
                </tr>

                <tr>
                    <td colspan="2"><b>UBICACION</b></td>
                    <td colspan="6">{{ strtoupper($patente->calle) }}</td>
                    <td colspan="2"><b>A PAGAR</b></td>
                    <td colspan="1" style="text-align: right;">{{$patente->valor_activo_total}}</td>

                
                </tr>

            </table>

            <table width="100%" style="margin-top: 30px;">
                <tr>
                    <td width="5%"></td>
                    <td width="20%">
                        <br>
                        <br>
                    
                        
                        
                        <hr>
                        <p style="font-size:12px;text-align: center"><b>JEFE DE RENTAS</b></p>
                    </td>
                    <td width="5%"></td>

                    <td width="5%"></td>
                    
                
                    <td width="20%">
                        <br>
                        <br>
                    
                        
                        
                        <hr>
                        <p style="font-size:12px;text-align: center"><b>TESORERO MUNICIPAL</b></p>
                    </td>
                    <td width="5%"></td>

                </tr>
            </table>

            
        </div>

    @endif

</body>
</html>
