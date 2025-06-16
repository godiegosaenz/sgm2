<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <style>
        body {
            font-family: DejaVu Sans, sans-serif;
            background-color: #f9f9f9;
            color: #000;
            font-size: 12px;
        }
        .container {
            width: 100%;
        }
        h2 {
            text-align: center;
            margin-bottom: 10px;
        }
        .section {
            border: 1px solid #000;
            margin-bottom: 10px;
            padding: 10px;
        }
        .section-title {
            background-color: #444;
            color: #fff;
            padding: 5px 10px;
            font-weight: bold;
        }
        .row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 4px;
        }
        .col {
            width: 48%;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 5px;
        }
        table th, table td {
            border: 1px solid #000;
            padding: 4px;
            text-align: left;
        }
        .label {
            font-weight: bold;
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
            width: 20%;
            text-align: left;
        }
        .cabecera .encabezado {
            width: 80%;
            text-align: left;
            font-size: 14px;
            font-weight: bold;
        }
        .cabecera .subtitulo {
            font-size: 12px;
            font-weight: normal;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <table class="cabecera">
        <tr style="border:0;">
            <td class="logo" style="border:0;">
                <img src="{{ asset('img/logo4.png') }}" alt="Logo" width="80">
            </td>
            <td class="encabezado" style="border:0; text-align: center;"  width="40%">
                Gobierno Autónomo Descentralizado Municipal del Cantón San Vicente
                <!-- <div class="subtitulo">Título de Crédito</div> -->
            </td>

             <td class="" style="border:0;"  width="20%">
                
                <!-- <div class="subtitulo">Título de Crédito</div> -->
            </td>
        </tr>
    </table>
    <div class="container">
        <h2>Contribuyente</h2>

        <div class="section">
            <div class="section-title">Informacion Personal</div>
            <div class="row">
                <div class="col"><span class="label">RUC:</span> {{ $data->ruc }}</div>
                <div class="col"><span class="label">Nombre:</span> {{ $data->razon_social }}</div>
            </div>
        </div>

         <div class="section">
            <div class="section-title">Informacion Representante Legal</div>
            <div class="row">
                <div class="col"><span class="label">RUC:</span> {{ $data->ruc_representante_legal }}</div>
                <div class="col"><span class="label">Nombre:</span> {{ $data->nombre_representante_legal }}</div>
            </div>
        </div>

        <div class="section">
            <div class="section-title">Información General</div>
            <table >
                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Estado:</span> {{ $data->estado_contribuyente_id }}</td>
                    <td style="border:0;"><span class="label">Tipo:</span> {{ $data->tipo_contribuyente }}</td>
                </tr>
                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Nacimiento:</span> {{ $data->fecha_nacimiento }}</td>
                    <td style="border:0;"><span class="label">RUC:</span> {{ $data->ruc }}</td>
                </tr>
                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Obligado Contabilidad:</span> {{ $data->obligado_contabilidad ? 'Sí' : 'No' }}</td>
                    <td style="border:0;"><span class="label">Régimen:</span> {{ $data->clase_cont }}</td>
                </tr>

                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Edad:</span> {{ $data->edad_contribuyente }}</td>
                    <td style="border:0;"><span class="label">Es Artesano:</span> {{ $data->es_artesano ? 'Sí' : 'No'}}</td>
                </tr>
            </table>
        </div>


        <div class="section">
             <div class="section-title">Fechas</div>
            
            <table >
                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Inicio Actividades:</span> {{ $data->fecha_inicio_actividades }}</td>
                    <td style="border:0;"><span class="label">Actualización:</span> {{ $data->fecha_actualizacion_actividades }}</td>
                </tr>
               
                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Reinicio:</span> {{ $data->fecha_reinicio_actividades }}</td>
                    <td style="border:0;"><span class="label">Suspensión:</span> {{ $data->fecha_suspension_definitiva }}</td>
                </tr>

            </table>
        </div>

        <div class="section">
            <div class="section-title">Ubicación y Contacto</div>
           
            <table>
                <tr style="line-height: 8px;" >
                    <td style="border:0;" width="50%"><span class="label">Teléfono:</span> {{ $data->telefono }}</td>
                    <td style="border:0;"><span class="label">Correo:</span> {{ $data->correo_1 }}</td>
                </tr>
               
                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Dirección:</span> {{ $data->direccion }}</td>
                    <td style="border:0;"><span class="label">Provincia:</span> {{ $data->nombre_provincia }}</td>
                </tr>

                <tr style="line-height: 8px;">
                    <td style="border:0;"><span class="label">Cantón:</span> {{ $data->nombre_canton }}</td>
                    <td style="border:0;"><span class="label">Parroquia:</span> {{ $data->nombre_parroquia }}</td>
                </tr>
            </table>

           
        </div>

        <div class="section">
            <div class="section-title">Locales</div>
            <table>
                <thead>
                    <tr>
                        <th>Nombre Comercial</th>
                        <th>Dirección</th>
                        <th>Arrendado</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($data->locales as $local)
                        <tr>
                            <td>{{ $local->actividad_descripcion }}</td>
                            <td>{{ $local->calle_principal." ".$local->calle_secundaria }}</td>
                            <td>{{ $local->local_propio == 1 ? 'No' : 'Si' }}</td>
                            <td>{{ $local->estado_establecimiento == 1 ? 'Abierto' : 'Cerrado'}}</td>
                        </tr>
                    @endforeach
                </tbody>
            </table>
        </div>

        <div class="section" style="margin-bottom:100px">
            <div class="section-title">Actividad Comercial</div>
            <table>
                <thead>
                    <tr>
                        <th>CIIU</th>
                        <th>Descripción</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach($data->actividades as $act)
                        <tr>
                            <td>{{ $act->ciiu }}</td>
                            <td>{{ $act->actividad}}</td>
                        </tr>
                    @endforeach
                </tbody>
            </table>
        </div>

    </div>
</body>
</html>
