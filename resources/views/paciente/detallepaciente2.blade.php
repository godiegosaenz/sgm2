@extends('layouts.app')

@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12" pb="10">
                <h2 class="text-center">PANEL DE INFORMACION

                </h2>
            </div>
        </div>
        <div class="row justify-content-md-center">
            <div class="col-5">
                <table id="Cita-table" class="table table-sm table-bordered table-hover">
                    <tbody>
                        <tr style="background-color: #BCDCF9">
                            <td colspan="2" style="text-align: center"><strong>Informacion de paciente</strong></td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Cedula :</td>
                            <td>{{$persona->cedula}}</td>
                        </tr>
                        <tr>
                            <td>Historia clínica</td>
                            <td>{{$persona->historiaClinica}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Nombres :</td>
                            <td>{{$persona->nombres}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Apellidos :</td>
                            <td>{{$persona->apellidos}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-envelope"></i> Edad :</td>
                            <td>
                                <span>
                                    <?php
                                    $fechaN = explode('-',date($persona->fechaNacimiento));
                                    $year = date("Y");
                                    echo $year - $fechaN[0] . ' años';
                                    ?>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-check"></i> Fecha de nacimiento : </td>
                            <td>{{$persona->fechaNacimiento}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-phone"></i> Telefono :</td>
                            <td>{{$persona->telefono}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-hourglass"></i> Estado Civíl :</td>
                            <td>{{$persona->estadoCivil}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-envelope"></i> Provincia :</td>
                            <td>{{$persona->provincia}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-check"></i> Cantón : </td>
                            <td>{{$persona->canton}}</td>
                        </tr>
                        <tr>
                            <td style="width: 25%"><i class="fa fa-phone"></i> Ciudad :</td>
                            <td style="width: 25%">{{$persona->ciudad}}</td>
                        </tr>
                        <tr>
                            <td style="width: 25%"><i class="fa fa-hourglass"></i> Dirección :</td>
                            <td style="width: 25%">{{$persona->direccion}}</td>
                        </tr>
                        <tr>
                            <td style="width: 25%"><i class="fa fa-phone"></i> Discapacidad :</td>
                            <td style="width: 25%">{{$persona->discapacidad}}</td>
                        </tr>
                        <tr>
                            <td style="width: 25%"><i class="fa fa-hourglass"></i> Porcentaje :</td>
                            <td style="width: 25%">{{$persona->porcentaje}}</td>
                        </tr>
                    </tbody>

                </table>
            </div>
            <div class="col-5">
                <table id="Cita-table" class="table table-sm table-bordered">
                    <tr style="background-color: #BCDCF9">
                        <td colspan="4" style="text-align: center"><strong>Foto</strong></td>
                    </tr>
                    <tr>
                        <td rowspan="3">
                            @if(isset($persona->rutaimagen))
                                <div class="media">
                                    <img height="160px" src="{{ asset($persona->rutaimagen) }}" class="mr-3" alt="">
                                </div>

                            @else
                            <div class="media">
                                <img height="130px" src="{{ asset('img/perfil.png') }}" class="mr-3" alt="">
                            </div>

                            @endif
                        </td>
                        <td>Historia Clinica</td>
                    </tr>
                    <tr>
                        <td>{{$persona->historiaClinica}}</td>
                    </tr>
                </table>
                <table class="table table-sm table-bordered">
                    <tr style="background-color: #BCDCF9">
                        <td colspan="4" style="text-align: center"><strong>Documentos</strong></td>
                    </tr>
                    <tr>
                        <td style="width: 25%"><i class="fa fa-phone"></i> Fecha :</td>
                        <td style="width: 25%">{{$persona->fecha}}</td>
                        <td style="width: 25%"><i class="fa fa-hourglass"></i> Hora :</td>
                        <td style="width: 25%">{{$persona->hora}}</td>
                    </tr>
                    <tr>
                        <td style="width: 25%"><i class="fa fa-phone"></i> Estado :</td>
                        <td style="width: 25%">{{$persona->estado}}</td>
                        <td style="width: 25%"><i class="fa fa-hourglass"></i> Motivo :</td>
                        <td style="width: 25%">{{$persona->motivo}}</td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="row justify-content-md-center">
            <div class="col-10">
                <table class="table table table-sm table-bordered table-hover">
                    <thead>
                        <tr style="background-color: #BCDCF9">
                            <td colspan="5" style="text-align: center"><strong>Detalle de consultas</strong></td>
                        </tr>
                        <tr>
                            <th>Especialista</th>
                            <th>Fecha y Hora</th>
                            <th>Motivo</th>
                            <th>Diagnostico</th>
                            <th style="width: 30%">Tratamiento</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach ($persona->cita as $c)
                            <tr>
                                <td>{{$c->persona->nombres.' '.$c->persona->apellidos}}</td>
                                <td>{{$c->fecha.' '.$c->hora}}</td>
                                <td>{{$c->motivo}}</td>
                                @if($c->estado == 'pendiente')
                                    <td></td>
                                    <td></td>
                                @else
                                    <td>{{$c->consulta->diagnostico}}</td>
                                    <td>{{$c->consulta->tratamiento}}</td>
                                @endif
                            </tr>
                        @endforeach
                    </tbody>
                </table>
            </div>
        </div>

    </div>
@endsection
