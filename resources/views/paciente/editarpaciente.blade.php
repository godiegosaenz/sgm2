@extends('layouts.app')

@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12" pb="10">
                <h2 class="text-center">PANEL DE INFORMACION

                </h2>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <!-- Nav tabs -->
                <ul class="nav nav-tabs" id="myTab" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home" type="button" role="tab" aria-controls="home" aria-selected="true">Informacion</button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="profile-tab" data-bs-toggle="tab" data-bs-target="#profile" type="button" role="tab" aria-controls="profile" aria-selected="false">Archivos</button>
                    </li>
                </ul>

                <!-- Tab panes -->
                <div class="tab-content" id="myTabContent">
                    <div class="tab-pane fade show active" id="home" role="tabpanel" aria-labelledby="home-tab">
                        <form method="POST" action="{{route('actualizar.paciente',$persona->id)}}" enctype="multipart/form-data">
                            @csrf
                            @method('PATCH')
                            <div class="container">

                                @if($value = session('estado'))
                                    @if($value == 'actualizado')
                                        <div class="row">
                                            <div class="col-12 mt-4 mb-0">
                                                <div class="alert alert-success alert-dismissible fade show" role="alert">
                                                    <strong>Felicidades, </strong> tus datos se actualizaron correctamente.

                                                </div>
                                            </div>

                                        </div>
                                    @else
                                        <div class="row">
                                            <div class="col-12 mt-4 mb-0">
                                                <div class="alert alert-danger" role="alert">
                                                    Llena todos los campos obligatorios
                                                  </div>
                                            </div>

                                        </div>
                                    @endif
                                @endisset

                                @if ($errors->any())
                                <div class="row">
                                    <div class="col-12 mt-4 mb-0">
                                        <div class="alert alert-danger" role="alert">
                                            <strong>Lo sentimos, </strong> Llena todos los campos obligatorios.

                                        </div>
                                    </div>

                                </div>
                                @endif
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="card mt-4">
                                            <div class="card-body">
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <ul class="list-group list-group-flush">
                                                            <li class="list-group-item">
                                                                <div class="form-group row mb-2">
                                                                    <label for="txtCodigo" class="col-sm-3 col-form-label">Codigo </label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('txtCodigo') ? 'is-invalid' : ''}}" id="txtCodigo" name="txtCodigo" value="{{ $persona->id}}" disabled>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="row mt-3 mb-2">
                                                                    <label for="cedula" class="col-sm-3 col-form-label">* Cédula </label>
                                                                    <div class="col-sm-9">
                                                                    <input type="text" class="form-control {{$errors->has('cedula') ? 'is-invalid' : ''}}" id="cedula" name="cedula" value="{{$errors->any() ? old('cedula') : $persona->cedula}}">

                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('cedula'))
                                                                                {{$errors->first('cedula')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="row mt-3 mb-2">
                                                                    <label for="nombres" class="col-sm-3 col-form-label">* Nombres </label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('nombres') ? 'is-invalid' : ''}}" id="nombres" name="nombres" value="{{$errors->any() ? old('nombres') : $persona->nombres}}">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('nombres'))
                                                                                {{$errors->first('nombres')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="row mt-3 mb-2">
                                                                    <label for="apellidos" class="col-sm-3 col-form-label">* Apellidos </label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('apellidos') ? 'is-invalid' : ''}}" id="apellidos" name="apellidos" value="{{$errors->any() ? old('apellidos') : $persona->apellidos}}">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('apellidos'))
                                                                                {{$errors->first('apellidos')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="form-group row mt-3">
                                                                    <label for="fechaNacimiento" class="col-3 col-form-label">* Fecha Nacimiento </label>
                                                                    <div class="col-9">
                                                                        <input class="form-control {{$errors->has('fechaNacimiento') ? 'is-invalid' : ''}}" type="date" id="fechaNacimiento" name="fechaNacimiento" value="{{$errors->any() ? old('fechaNacimiento') : $persona->fechaNacimiento}}">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('fechaNacimiento'))
                                                                                {{$errors->first('fechaNacimiento')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="row mt-3 mb-2">
                                                                    <label for="estadoCivil" class="col-3 col-form-label">* Estado Civil </label>
                                                                    <div class="col-9">
                                                                        <select class="form-select {{$errors->has('estadoCivil') ? 'is-invalid' : ''}}" id="estadoCivil" name="estadoCivil">
                                                                            <option value="">Seleccione Estado</option>
                                                                                @if ($persona->estadoCivil == 'SOLTERO/A')
                                                                                <option selected="selected" value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                                                                <option value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                                                                <option value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                                                                <option value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                                                                <option value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                                                                @elseif ($persona->estadoCivil == 'CASADO/A')
                                                                                    <option value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                                                                    <option selected="selected" value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                                                                    <option value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                                                                    <option value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                                                                    <option value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                                                                @elseif ($persona->estadoCivil == 'DIVORSIADO/A')
                                                                                    <option value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                                                                    <option value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                                                                    <option selected="selected" value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                                                                    <option value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                                                                    <option value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                                                                @elseif ($persona->estadoCivil == 'VIUDO/A')
                                                                                    <option value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                                                                    <option value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                                                                    <option value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                                                                    <option selected="selected" value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                                                                    <option value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                                                                @elseif ($persona->estadoCivil == 'UNION LIBRE')
                                                                                    <option value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                                                                    <option value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                                                                    <option value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                                                                    <option value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                                                                    <option selected="selected" value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                                                                @else
                                                                                <option value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                                                                <option value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                                                                <option value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                                                                <option value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                                                                <option value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                                                                @endif
                                                                        </select>
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('estadoCivil'))
                                                                                {{$errors->first('estadoCivil')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>

                                                            <li class="list-group-item">
                                                                <div class="form-group row mt-3 mb-2">
                                                                    <label for="ocupacion" class="col-sm-3 col-form-label">Ocupacion </label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('ocupacion') ? 'is-invalid' : ''}}" id="ocupacion" name="ocupacion" value="{{$errors->any() ? old('ocupacion') : $persona->ocupacion}}">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('ocupacion'))
                                                                                {{$errors->first('ocupacion')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="row mt-3 mb-2">
                                                                    <label for="txtEdad" class="col-sm-3 col-form-label">Edad </label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('txtEdad') ? 'is-invalid' : ''}}" id="txtEdad" name="txtEdad" disabled value="<?php
                                                                                if(isset($persona->fechaNacimiento)){
                                                                                    $fechaN = explode('-',date($persona->fechaNacimiento));
                                                                                    $valida = checkdate($fechaN[2], $fechaN[1], $fechaN[0]);
                                                                                    if($valida){
                                                                                        $year = date("Y");
                                                                                        echo $year - $fechaN[0] . ' años';
                                                                                    }

                                                                                }else{

                                                                                }

                                                                                ?>">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('txtEdad'))
                                                                                {{$errors->first('txtEdad')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="form-group row mt-3">
                                                                    <label for="telefono" class="col-sm-3 col-form-label">Telefono</label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('telefono') ? 'is-invalid' : ''}}" id="telefono" name="telefono" value="{{$errors->any() ? old('telefono') : $persona->telefono}}">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('telefono'))
                                                                                {{$errors->first('telefono')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                        </ul>
                                                    </div>

                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="card mt-4">
                                            <div class="card-body">
                                                <div class="row">
                                                    <div class="col-6">

                                                        @if(isset($persona->rutaimagen))
                                                            <div class="media">
                                                                <img height="160px" src="{{ asset($persona->rutaimagen) }}" class="mr-3" alt="">
                                                            </div>

                                                        @else
                                                        <div class="media">
                                                            <img height="130px" src="{{ asset('img/perfil.png') }}" class="mr-3" alt="">
                                                        </div>
                                                        <div class="mb-3">
                                                            <input class="form-control" id="txtFoto" name="txtFoto" type="file" >
                                                        </div>


                                                        @endif
                                                    </div>
                                                    <div class="col-6">
                                                            <h4>* Historia Clínica</h4>
                                                            <div class="form-group row">

                                                                <div class="col-sm-12">
                                                                    <input type="text" class="form-control {{$errors->has('historiaClinica') ? 'is-invalid' : ''}}" id="historiaClinica" name="historiaClinica" value="{{$errors->any() ? old('historiaClinica'): $persona->historiaClinica }}">
                                                                    <div class="invalid-feedback">
                                                                        @if($errors->has('historiaClinica'))
                                                                            {{$errors->first('historiaClinica')}}
                                                                        @endif
                                                                    </div>
                                                                </div>
                                                            </div>
                                                    </div>
                                                </div>


                                            </div>
                                        </div>

                                        <div class="card mt-4">
                                            <div class="card-body">
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <ul class="list-group list-group-flush">
                                                            <li class="list-group-item">
                                                                <div class="row mb-2">
                                                                    <label for="discapacidad" class="col-3 col-form-label">Discapacidad</label>
                                                                    <div class="col-9">
                                                                        <select class="form-select {{$errors->has('discapacidad') ? 'is-invalid' : ''}}" id="discapacidad" name="discapacidad">
                                                                            @if ($persona->discapacidad == 'SI')
                                                                                <option {{ old('discapacidad') == 'NO' ? 'selected' : '' }}>NO</option>
                                                                                <option {{ old('discapacidad') == 'SI' ? 'selected' : 'selected' }}>SI</option>
                                                                            @else
                                                                                <option {{ old('discapacidad') == 'NO' ? 'selected' : 'selected' }}>NO</option>
                                                                                <option {{ old('discapacidad') == 'SI' ? 'selected' : '' }}>SI</option>
                                                                            @endif

                                                                        </select>
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('discapacidad'))
                                                                                {{$errors->first('discapacidad')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                            <li class="list-group-item">
                                                                <div class="row mt-3">
                                                                    <label for="porcentaje" class="col-sm-3 col-form-label">Porcentaje :</label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('porcentaje') ? 'is-invalid' : ''}}" id="porcentaje" name="porcentaje" value="{{$errors->any() ? old('porcentaje') : $persona->porcentaje}}">
                                                                        <div class="invalid-feedback">
                                                                            @if($errors->has('porcentaje'))
                                                                                {{$errors->first('porcentaje')}}
                                                                            @endif
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                        </ul>
                                                    </div>

                                                </div>
                                            </div>
                                        </div>
                                        <div class="card mt-4">
                                            <div class="card-body">
                                                    <div class="row">
                                                            <div class="col-md-12">
                                                                    <ul class="list-group list-group-flush">
                                                                        <li class="list-group-item">
                                                                            <div class="row mb-3">
                                                                                <label for="provincia_id" class="col-3 col-form-label">* Provincia</label>
                                                                                    <div class="col-9">
                                                                                        <select class="form-select {{$errors->has('provincia_id') ? 'is-invalid' : ''}}" id="provincia_id" name="provincia_id">
                                                                                            <option value="">Seleccione provincia</option>
                                                                                            @foreach ($provincias as $p)
                                                                                                <option value="{{$p->id}}" {{ $persona->provincia == $p->nombre ? 'selected' : '' }}>{{$p->nombre}}</option>
                                                                                            @endforeach
                                                                                        </select>
                                                                                        <div class="invalid-feedback">
                                                                                            @if($errors->has('provincia_id'))
                                                                                                {{$errors->first('provincia_id')}}
                                                                                            @endif
                                                                                        </div>
                                                                                    </div>
                                                                            </div>
                                                                        </li>
                                                                        <li class="list-group-item">
                                                                            <div class="row mb-3">
                                                                                <label for="canton_id" class="col-3 col-form-label">* Canton</label>
                                                                                <div class="col-9">
                                                                                    <select class="form-select {{$errors->has('canton_id') ? 'is-invalid' : ''}}" id="canton_id" name="canton_id">
                                                                                        <option value="" id="optionSelectCanton">Seleccione canton</option>
                                                                                        @isset($cantones)
                                                                                            @foreach ($cantones as $c)
                                                                                                <option value="{{$c->id}}" {{ $persona->canton_id == $c->id ? 'selected' : '' }}>{{$c->nombre}}</option>
                                                                                            @endforeach
                                                                                        @endisset
                                                                                    </select>
                                                                                    <div class="invalid-feedback">
                                                                                        @if($errors->has('canton_id'))
                                                                                            {{$errors->first('canton_id')}}
                                                                                        @endif
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </li>
                                                                        <li class="list-group-item">
                                                                            <div class="form-group row mt-3 mb-2">
                                                                                <label for="ciudad" class="col-sm-3 col-form-label">Ciudad </label>
                                                                                <div class="col-sm-9">
                                                                                    <input type="text" class="form-control {{$errors->has('ciudad') ? 'is-invalid' : ''}}" id="ciudad" name="ciudad" value="{{$errors->any() ? old('ciudad') : $persona->ciudad}}">

                                                                                </div>
                                                                            </div>
                                                                        </li>
                                                                        <li class="list-group-item">
                                                                            <div class="form-group row mt-3 mb-2">
                                                                                <label for="direccion" class="col-sm-3 col-form-label">Direccion </label>
                                                                                <div class="col-sm-9">
                                                                                    <input type="text" class="form-control {{$errors->has('direccion') ? 'is-invalid' : ''}}" id="direccion" name="direccion" value="{{ $errors->any() ? old('direccion') : $persona->direccion}}">

                                                                                </div>
                                                                            </div>
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
                                                        <li class="list-group-item">
                                                            <div class="form-group row mt-3">
                                                                <label for="nota" class="col-sm-2 col-form-label">Nota : </label>
                                                                <div class="col-sm-10">
                                                                    <textarea class="form-control {{$errors->has('nota') ? 'is-invalid' : ''}}" id="nota" name="nota" rows="3">{{$errors->any() ? old('nota') : $persona->nota}}</textarea>
                                                                    <div class="invalid-feedback">
                                                                        @if($errors->has('nota'))
                                                                            {{$errors->first('nota')}}
                                                                        @endif
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </li>

                                                    </ul>
                                                </div>
                                            </div>
                                    </div>

                                </div>
                                <div class="row">
                                    <div class="col-6">
                                        <div class="card mt-4">
                                            <div class="card-body">
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <ul class="list-group list-group-flush">
                                                            <li class="list-group-item">
                                                                <div class="form-group row mt-3">
                                                                    <label for="created_at" class="col-sm-3 col-form-label">Fecha de Ingreso </label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('created_at') ? 'is-invalid' : ''}}" id="created_at" name="created_at" value="{{ $persona->created_at}}" disabled>

                                                                    </div>
                                                                </div>
                                                            </li>

                                                        </ul>
                                                    </div>

                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div class="card mt-4">
                                            <div class="card-body">
                                                <div class="row">
                                                    <div class="col-md-12">
                                                        <ul class="list-group list-group-flush">

                                                            <li class="list-group-item">
                                                                <div class="form-group row mt-3">
                                                                    <label for="updated_at" class="col-sm-3 col-form-label">Ultima actualizacion</label>
                                                                    <div class="col-sm-9">
                                                                        <input type="text" class="form-control {{$errors->has('updated_at') ? 'is-invalid' : ''}}" id="updated_at" name="updated_at" value="{{ $persona->updated_at}}" disabled>

                                                                    </div>
                                                                </div>
                                                            </li>
                                                        </ul>
                                                    </div>

                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-12">
                                        <div class="form-group row mt-3">
                                            <div class="col-sm-12 col-md-12">
                                                <input type="hidden" name="idpersona" value="{{$persona->id}}">
                                                <button type="submit" name="btn_Guardar_Persona" class="btn btn-secondary btn-block">Actualizar datos</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            @csrf
                        </form>
                    </div>
                    <div class="tab-pane fade" id="profile" role="tabpanel" aria-labelledby="profile-tab">
                        <div class="container">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="card mt-4">
                                        <div class="card-body">
                                            <h3>Guardar foto</h3>
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
                                                            <input class="btn btn-default" type="submit" class="" value="Subir Imagen">
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
                                            <br>

                                        </div>
                                    </div>



                                </div>
                                <div class="col-md-6">
                                    <div class="card mt-4">
                                        <div class="card-body">
                                            <h3>Guardar archivos</h3>
                                            <ul class="list-group list-group-flush">
                                                <form method="POST" action="{{route('guardar.archivo')}}" enctype="multipart/form-data">
                                                    @csrf
                                                    <input type="hidden" name="idpersona" value="{{$persona->id}}">
                                                    <li class="list-group-item">
                                                        <div class="form-group row mt-3">
                                                            <label for="nombres" class="col-sm-3 col-form-label">Nombre del archivo :</label>
                                                            <div class="col-sm-9">
                                                                <input type="text" class="form-control {{$errors->has('nombres') ? 'is-invalid' : ''}}" id="nombres" name="nombres" value="">

                                                            </div>
                                                        </div>
                                                    </li>
                                                    <li class="list-group-item">
                                                        <div class="form-group row mt-3">
                                                            <label for="txtArchivo" class="col-sm-3 col-form-label">Archivo :</label>
                                                            <div class="col-sm-9">
                                                                <input type="file" class="form-control-file {{$errors->has('txtArchivo') ? 'is-invalid' : ''}}" id="txtArchivo" name="txtArchivo">
                                                                <div class="invalid-feedback">
                                                                    @if($errors->has('txtArchivo'))
                                                                        {{$errors->first('txtArchivo')}}
                                                                    @endif
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <li class="list-group-item">
                                                        <input type="submit" class="btn btn-default" value="Subir Archivo">
                                                    </li>

                                                </form>
                                            </ul>
                                        </div>
                                    </div>

                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="card mt-3">
                                        <div class="card-body">
                                        <h3>Lista archivos</h3>
                                        <hr>
                                            <div class="table-responsive">
                                                <table class="table table-bordered" id="tableDocumento">
                                                    <thead>
                                                        <tr>
                                                        <th scope="col">#</th>
                                                        <th>miniatura</th>
                                                        <th scope="col">Archivo</th>
                                                        <th scope="col">Accion</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>


                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

        </div>

    </div>
@endsection
@push('scripts')
<script>
    let token = "{{csrf_token()}}";


    function ocultarAlertas(){
        document.getElementById('alertPersonaExiste').setAttribute('style','display:none');
        document.getElementById('alertPersonaNoExiste').setAttribute('style','display:none');
        document.getElementById('alertPersona').setAttribute('style','display:none');
    }

    var provincia_id = document.getElementById('provincia_id');
    provincia_id.addEventListener('change', function() {
        var optionSelectCanton = document.getElementById('optionSelectCanton');
        optionSelectCanton.innerHTML = 'Cargando...';
        var selectedOption = this.options[provincia_id.selectedIndex];
        //console.log(selectedOption.value + ': ' + selectedOption.text);
        cargarcantones(selectedOption.value);
    });
    function cargarcantones(idprovincia){
        var canton_id = document.getElementById('canton_id');

        axios.post('{{route('canton.obtener')}}', {
            _token: token,
            idprovincia:idprovincia
        }).then(function(res) {
            if(res.status==200) {
                console.log("cargando cantones");
                canton_id.innerHTML = res.data;
            }
        }).catch(function(err) {
            if(err.response.status == 500){
                toastr.error('Error al comunicarse con el servidor, contacte al administrador de Sistemas');
                console.log('error al consultar al servidor');
            }

            if(err.response.status == 419){
                toastr.error('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }
        }).then(function() {

        });
    }
</script>
@endpush
@push('styles')
<style>
    /*Profile Pic Start*/
.picture-container{
    position: relative;
    cursor: pointer;
    text-align: center;
}
.picture{
    width: 106px;
    height: 106px;
    background-color: #999999;
    border: 4px solid #CCCCCC;
    color: #FFFFFF;
    border-radius: 50%;
    margin: 0px auto;
    overflow: hidden;
    transition: all 0.2s;
    -webkit-transition: all 0.2s;
}
.picture:hover{
    border-color: #2ca8ff;
}
.content.ct-wizard-green .picture:hover{
    border-color: #05ae0e;
}
.content.ct-wizard-blue .picture:hover{
    border-color: #3472f7;
}
.content.ct-wizard-orange .picture:hover{
    border-color: #ff9500;
}
.content.ct-wizard-red .picture:hover{
    border-color: #ff3b30;
}
.picture input[type="file"] {
    cursor: pointer;
    display: block;
    height: 100%;
    left: 0;
    opacity: 0 !important;
    position: absolute;
    top: 0;
    width: 100%;
}

.picture-src{
    width: 100%;

}
</style>
@endpush
