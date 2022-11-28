<!DOCTYPE html>
<html>
<head>
    <title>Generacion de recibo</title>
    <link href="{{ asset('css/bootstrap.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/bootstrap-icons.css') }}" rel="stylesheet">
</head>
<body>
    <div class="row">
        <table id="Cita-table" class="table table-sm table-bordered table-hover">
            <tbody>
                <tr style="background-color: #BCDCF9">
                    <td colspan="4" style="text-align: center"><strong>Informacion de paciente</strong></td>
                </tr>
                <tr>
                    <td><i class="fa fa-user"></i> Cedula :</td>
                    <td>{{$Cita->persona->cedula}}</td>
                    <td>Historia clínica</td>
                    <td>{{$Cita->persona->historiaClinica}}</td>
                </tr>
                <tr>
                    <td><i class="fa fa-user"></i> Nombres :</td>
                    <td>{{$Cita->persona->nombres}}</td>
                    <td><i class="fa fa-user"></i> Apellidos :</td>
                    <td>{{$Cita->persona->apellidos}}</td>
                </tr>
                <tr>
                    <td><i class="fa fa-envelope"></i> Edad :</td>
                    <td></td>
                    <td><i class="fa fa-check"></i> Fecha de nacimiento : </td>
                    <td>{{$Cita->persona->fechaNacimiento}}</td>
                </tr>
                <tr>
                    <td><i class="fa fa-phone"></i> Telefono :</td>
                    <td>{{$Cita->persona->telefono}}</td>
                    <td><i class="fa fa-hourglass"></i> Estado Civíl :</td>
                    <td>{{$Cita->persona->estadoCivil}}</td>
                </tr>
                <tr>
                    <td><i class="fa fa-envelope"></i> Provincia :</td>
                    <td>{{$Cita->persona->provincia}}</td>
                    <td><i class="fa fa-check"></i> Cantón : </td>
                    <td>{{$Cita->persona->canton}}</td>
                </tr>
                <tr>
                    <td style="width: 25%"><i class="fa fa-phone"></i> Ciudad :</td>
                    <td style="width: 25%">{{$Cita->persona->ciudad}}</td>
                    <td style="width: 25%"><i class="fa fa-hourglass"></i> Dirección :</td>
                    <td style="width: 25%">{{$Cita->persona->direccion}}</td>
                </tr>
                <tr>
                    <td style="width: 25%"><i class="fa fa-phone"></i> Discapacidad :</td>
                    <td style="width: 25%">{{$Cita->persona->discapacidad}}</td>
                    <td style="width: 25%"><i class="fa fa-hourglass"></i> Porcentaje :</td>
                    <td style="width: 25%">{{$Cita->persona->porcentaje}}</td>
                </tr>
                <tr style="background-color: #BCDCF9">
                    <td colspan="4" style="text-align: center"><strong>Informacion de la cita</strong></td>
                </tr>
                <tr>
                    <td style="width: 25%"><i class="fa fa-phone"></i> Fecha :</td>
                    <td style="width: 25%">{{$Cita->fecha}}</td>
                    <td style="width: 25%"><i class="fa fa-hourglass"></i> Hora :</td>
                    <td style="width: 25%">{{$Cita->hora}}</td>
                </tr>
                <tr>
                    <td style="width: 25%"><i class="fa fa-phone"></i> Estado :</td>
                    <td style="width: 25%">{{$Cita->estado}}</td>
                    <td style="width: 25%"><i class="fa fa-hourglass"></i> Motivo :</td>
                    <td style="width: 25%">{{$Cita->motivo}}</td>
                </tr>
            </tbody>

        </table>
    </div>
</body>
</html>
