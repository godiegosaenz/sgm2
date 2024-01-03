@extends('layouts.app')

@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12" pb="10">
                <h2 class="text-center">Informacion personal

                </h2>
            </div>
        </div>
        <div class="row">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item">
                                            @if(isset($persona->rutaimagen))
                                            <div class="media">
                                                <img height="160px" src="{{ asset($persona->rutaimagen) }}" class="mr-3" alt="">
                                            </div>
                                            <form method="POST" action="{{route('foto.persona')}}" enctype="multipart/form-data">
                                                @csrf
                                                <input type="hidden" name="idpersona" value="{{$persona->id}}">
                                                <input type="file" class="form-control-file {{$errors->has('txtFoto') ? 'is-invalid' : ''}}" id="txtFoto" name="txtFoto" required>
                                                <input class="btn btn-default btn-sm" type="submit" class="" value="Subir Imagen">
                                            </form>
                                            @else
                                            <div class="media">
                                                <img height="130px" src="{{ asset('img/perfil.png') }}" class="mr-3" alt="">
                                            </div>
                                            <form method="POST" action="{{route('foto.persona')}}" enctype="multipart/form-data">
                                                @csrf
                                                <input type="hidden" name="idpersona" value="{{$persona->id}}">
                                                <input type="file" class="form-control-file {{$errors->has('txtFoto') ? 'is-invalid' : ''}}" id="txtFoto" name="txtFoto" required>
                                                <input type="submit" class="" value="Subir Imagen">
                                            </form>

                                            @endif
                                    </li>
                                </ul>
                            </div>
                            <div class="col-md-4">
                                <ul class="list-group list-group-flush">

                                    <li class="list-group-item"><strong>Codigo : </strong></li>
                                    <li class="list-group-item"><strong>Cedula : </strong></li>
                                    <li class="list-group-item"><strong>Nombres : </strong></li>
                                    <li class="list-group-item"><strong>Apellidos : </strong></li>
                                    <li class="list-group-item"><strong>Fecha Nacimiento : </strong></li>
                                    
                                </ul>
                            </div>
                            <div class="col-md-4">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item"><span>{{ $persona->id}}</span></li>
                                    <li class="list-group-item"><span>{{ $persona->cedula}}</span></li>
                                    <li class="list-group-item"><span>{{ $persona->nombres}}</span></li>
                                    <li class="list-group-item"><span>{{ $persona->apellidos}}</span></li>
                                    <li class="list-group-item"><span>{{ $persona->fechaNacimiento}}</span></li>
                                    
                                </ul>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                    <ul class="list-group list-group-flush">
                                        <li class="list-group-item"><strong>Estado Civil : </strong></li>
                                        <li class="list-group-item"><strong>Fecha de ingreso : </strong></li>
                                        <li class="list-group-item"><strong>Ocupacion : </strong></li>
                                        <li class="list-group-item"><strong>Edad : </strong></li>
                                        <li class="list-group-item"><strong>Telefono : </strong></li>
                                    </ul>
                            </div>
                            <div class="col-md-8">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item"><span>{{ $persona->estadoCivil}}</span></li>    
                                    <li class="list-group-item"><span>{{ $persona->created_at}}</span></li>
                                    <li class="list-group-item"><span>{{ $persona->ocupacion}}</span></li>
                                    <li class="list-group-item">
                                        <span>
                                            <?php
                                            $fechaN = explode('-',date($persona->fechaNacimiento));
                                            $year = date("Y");
                                            echo $year - $fechaN[0] . ' años';
                                            ?>
                                        </span>
                                    </li>
                                    <li class="list-group-item">
                                        <span>
                                                @isset($persona->telefono)
                                                {{ $persona->telefono}}
                                                @else
                                                    {{'N/A'}}
                                                @endisset
                                        </span>
                                    </li>
                                    
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item"><strong>Discapacidad : </strong></li>
                                    <li class="list-group-item"><strong>Porcentaje : </strong></li>
                                </ul>
                            </div>
                            <div class="col-md-8">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item">
                                        <span>
                                            {{$persona->discapacidad}}
                                        </span>
                                    </li>
                                    <li class="list-group-item">
                                        <span>
                                            @isset($persona->porcentaje)
                                            {{ $persona->porcentaje}}
                                            @else
                                                {{'N/A'}}
                                            @endisset
                                        </span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card mt-4">
                    <div class="card-body">
                            <div class="row">
                                    <div class="col-md-4">
                                            <ul class="list-group list-group-flush">
                                                <li class="list-group-item"><strong>Provincia : </strong></li>
                                                <li class="list-group-item"><strong>Canton : </strong></li>
                                                <li class="list-group-item"><strong>Ciudad : </strong></li>
                                                <li class="list-group-item"><strong>Direccion :</strong></li>

                                            </ul>
                                    </div>
                                    <div class="col-md-8">
                                        <ul class="list-group list-group-flush">
                                            <li class="list-group-item"><span>{{ $persona->provincia}}</span></li>
                                            <li class="list-group-item"><span>{{ $persona->canton}}</span></li>
                                            <li class="list-group-item">
                                                <span>
                                                    @isset($persona->ciudad)
                                                    {{ $persona->ciudad}}
                                                    @else
                                                        {{'N/A'}}
                                                    @endisset
                                                </span>
                                            </li>
                                            <li class="list-group-item">
                                                <span>
                                                    @isset($persona->direccion)
                                                    {{ $persona->direccion}}
                                                    @else
                                                        {{'N/A'}}
                                                    @endisset
                                                </span>
                                            </li>

                                        </ul>
                                    </div>
                            </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                    <div class="card mt-4">
                        <div class="card-body">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item"><strong>Nota : </strong></li>
                                <li class="list-group-item">
                                    @isset($persona->nota)
                                        {{$persona->nota}}
                                    @else
                                        {{'N/A'}}
                                    @endisset
                                </li>
                            </ul>
                        </div>
                    </div>
            </div>

        </div>
    </div>
@endsection
