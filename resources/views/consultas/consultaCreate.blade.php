@extends('layouts.app')
@section('title', 'Registrar consulta')
@section('content')
    <div class="container">
        <div class="row justify-content-center align-items-center">

            <div class="col-md-9 ">
                <div class="row mb-1">
                    <h2>Registro de consulta</h2>
                </div>
                @if(session('guardado'))
                    <div id="alertCitaActualizada" class="alert alert-success" role="alert">
                        {{session('guardado')}}
                    </div>
                @endif
                <form method="POST" action="{{route('store.consulta')}}">
                    @csrf
                    <div class="row">
                        <div class="col-12">

                            <div class="alert alert-info alert-dismissible">
                                <div>
                                    <h5><i class="bi-info-circle"></i> Recuerda!</h5>
                                    Todos los campos con un asterisco al final. Son campos obligatorios.
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-12">
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
                    </div>
                    <div class="row">
                        <div class="col-12">
                            <h4>Informacion Consulta :</h4>
                            <hr>
                        </div>
                        <input type="hidden" id="cita_id" name="cita_id" value="{{$Cita->id}}">
                        <div class="co-12">
                            <div class="mb-3">
                                <label for="diagnostico">* Diagnostico : </label>
                                <textarea class="form-control {{$errors->has('diagnostico') ? 'is-invalid' : ''}}" id="diagnostico" name="diagnostico" rows="3"></textarea>
                                <div class="invalid-feedback">
                                    @if($errors->has('diagnostico'))
                                        {{$errors->first('diagnostico')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="co-12">
                            <div class="mb-3">
                                <label for="tratamiento">* Tratamiento : </label>
                                <textarea class="form-control {{$errors->has('tratamiento') ? 'is-invalid' : ''}}" id="tratamiento" name="tratamiento" rows="3"></textarea>
                                <div class="invalid-feedback">
                                    @if($errors->has('tratamiento'))
                                        {{$errors->first('tratamiento')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-12">
                            <button class="btn btn-primary">Registrar</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
@endsection
