@extends('layouts.app')
@section('content')
    <div class="container">
        <div class="row justify-content-center align-items-center">

            <div class="col-md-9 ">
                <div class="row mb-5">
                    <h2>Formulario para el ingreso de Personas</h2>
                </div>

                <div class="row">
                    <form method="POST" action="{{route('guardar.persona')}}" enctype="multipart/form-data">
                        <div class="row mb-3">
                            <div id="alertPersona" class="alert alert-danger" role="alert" style="display: none;">
                                    El campo cedula debe estar lleno
                            </div>
                            <div id="alertPersonaNoExiste" class="alert alert-warning" role="alert" style="display: none;">
                                <span><i class="bi bi-info-circle"></i></span> La cedula ingresada no se encuentra en los registros, proceda el ingreso.
                            </div>
                            <div id="alertPersonaExiste" class="alert alert-info" role="alert" style="display: none;">
                                <span><i class="bi bi-info-circle"></i></span> La cedula ya se encuentra ingresada a otra persona.
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label for="cedula" class="col-sm-2 col-form-label">* Cédula</label>
                            <div class="col-sm-6">
                                <input @if($errors->any()) type="hidden" @else type="text" @endif class="form-control {{$errors->has('cedula') ? 'is-invalid' : ''}}" id="cedula" name="cedula" placeholder="Ejemplo 1314801349" value="{{ old('cedula')}}">
                                <input @if($errors->any()) type="text" @else type="hidden" @endif class="form-control {{$errors->has('cedula') ? 'is-invalid' : ''}}" id="cedula2" name="cedula2" placeholder="Ejemplo 1314801349" value="{{ old('cedula')}}" disabled>
                                <div id="msjErrorCedula" class="invalid-feedback">
                                    @if($errors->has('cedula'))
                                        {{$errors->first('cedula')}}
                                    @endif
                                </div>
                            </div>
                            <div class="col-sm-2">
                                <div class="d-grid gap-2">
                                    <button id="buttonVerificarPersona" class="btn btn-primary" type="button">
                                        <span id="spanSpinnerVerificando" class="spinner-border spinner-border-sm" role="status" aria-hidden="true" style="display: none"></span>
                                        <span id="spanVerificando">Verificar</span>
                                      </button>
                                </div>
                            </div>
                            <div class="col-sm-2">
                                <div class="d-grid gap-2">
                                    <button type="button" class="btn btn-primary" style="display: none;">Editar</button>
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="nombres" class="col-sm-2 col-form-label">* Nombres</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control {{$errors->has('nombres') ? 'is-invalid' : ''}}" id="nombres" name="nombres" placeholder="Ejemplo Juan" value="{{ old('nombres')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('nombres'))
                                        {{$errors->first('nombres')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="apellidos" class="col-sm-2 col-form-label">* Apellidos</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control {{$errors->has('apellidos') ? 'is-invalid' : ''}}" id="apellidos" name="apellidos" placeholder="Ejemplo Solorzano" value="{{ old('apellidos')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('apellidos'))
                                        {{$errors->first('apellidos')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="fechaNacimiento" class="col-2 col-form-label">* Fecha Nacimiento</label>
                            <div class="col-10">
                                <input class="form-control {{$errors->has('fechaNacimiento') ? 'is-invalid' : ''}}" type="date" id="fechaNacimiento" name="fechaNacimiento" value="{{ old('fechaNacimiento')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('fechaNacimiento'))
                                        {{$errors->first('fechaNacimiento')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="estadoCivil" class="col-2 col-form-label">* Estado Civil</label>
                            <div class="col-10">
                                <select class="form-select {{$errors->has('estadoCivil') ? 'is-invalid' : ''}}" id="estadoCivil" name="estadoCivil" @if($errors->any())  @else disabled @endif>
                                    <option value="">Seleccione Estado</option>
                                    <option value="SOLTERO/A" {{ old('estadoCivil') == 'SOLTERO/A' ? 'selected' : '' }}>SOLTERO/A</option>
                                    <option value="CASADO/A" {{ old('estadoCivil') == 'CASADO/A' ? 'selected' : '' }}>CASADO/A</option>
                                    <option value="DIVORSIADO/A" {{ old('estadoCivil') == 'DIVORSIADO/A' ? 'selected' : '' }}>DIVORSIADO/A</option>
                                    <option value="VIUDO/A" {{ old('estadoCivil') == 'VIUDO/A' ? 'selected' : '' }}>VIUDO/A</option>
                                    <option value="UNION LIBRE" {{ old('estadoCivil') == 'UNION LIBRE' ? 'selected' : '' }}>UNION LIBRE</option>
                                </select>
                                <div class="invalid-feedback">
                                    @if($errors->has('estadoCivil'))
                                        {{$errors->first('estadoCivil')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="ocupacion" class="col-sm-2 col-form-label">* Ocupacion</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control {{$errors->has('ocupacion') ? 'is-invalid' : ''}}" id="ocupacion" name="ocupacion" placeholder="Ejemplo Contador" value="{{ old('ocupacion')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('ocupacion'))
                                        {{$errors->first('ocupacion')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="provincia_id" class="col-2 col-form-label">* Provincia</label>
                            <div class="col-10">
                                <select class="form-select {{$errors->has('provincia_id') ? 'is-invalid' : ''}}" id="provincia_id" name="provincia_id" @if($errors->any())  @else disabled @endif>
                                    <option value="">Seleccione provincia</option>
                                    @foreach ($provincias as $p)
                                        <option value="{{$p->id}}" {{ old('provincia_id') == $p->id ? 'selected' : '' }}>{{$p->nombre}}</option>
                                    @endforeach
                                </select>
                                <div class="invalid-feedback">
                                    @if($errors->has('provincia_id'))
                                        {{$errors->first('provincia_id')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="canton_id" class="col-2 col-form-label">* Canton</label>
                            <div class="col-10">
                                <select class="form-select {{$errors->has('canton_id') ? 'is-invalid' : ''}}" id="canton_id" name="canton_id" @if($errors->any())  @else disabled @endif>
                                    <option value="" id="optionSelectCanton">Seleccione canton</option>
                                    @if(is_countable($cantones) && count($cantones) > 0)
                                        @foreach ($cantones as $c)
                                            <option value="{{$c->id}}" {{ old('canton_id') == $c->id ? 'selected' : '' }}>{{$c->nombre}}</option>
                                        @endforeach
                                    @endif
                                </select>
                                <div class="invalid-feedback">
                                    @if($errors->has('canton_id'))
                                        {{$errors->first('canton_id')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="ciudad" class="col-sm-2 col-form-label">Ciudad</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control {{$errors->has('ciudad') ? 'is-invalid' : ''}}" id="ciudad" name="ciudad" placeholder="Ejemplo Bahia de Caraquez" value="{{ old('ciudad')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('ciudad'))
                                        {{$errors->first('ciudad')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="direccion" class="col-sm-2 col-form-label">Direccion</label>
                            <div class="col-sm-10">
                                <textarea class="form-control {{$errors->has('direccion') ? 'is-invalid' : ''}}" id="direccion" name="direccion" rows="3" @if($errors->any())  @else disabled @endif>{{ old('direccion')}}</textarea>
                                <div class="invalid-feedback">
                                    @if($errors->has('direccion'))
                                        {{$errors->first('direccion')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="telefono" class="col-sm-2 col-form-label">Telefono</label>
                            <div class="col-sm-10">
                                <input type="number" class="form-control {{$errors->has('telefono') ? 'is-invalid' : ''}}" id="telefono" name="telefono" placeholder="Ejemplo 0965456544" value="{{ old('telefono')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('telefono'))
                                        {{$errors->first('telefono')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="discapacidad" class="col-2 col-form-label">Discapacidad</label>
                            <div class="col-10">
                                <select class="form-select {{$errors->has('discapacidad') ? 'is-invalid' : ''}}" id="discapacidad" name="discapacidad" @if($errors->any())  @else disabled @endif>
                                    <option {{ old('discapacidad') == 'NO' ? 'selected' : '' }}>NO</option>
                                    <option {{ old('discapacidad') == 'SI' ? 'selected' : '' }}>SI</option>
                                </select>
                                <div class="invalid-feedback">
                                    @if($errors->has('discapacidad'))
                                        {{$errors->first('discapacidad')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="porcentaje" class="col-sm-2 col-form-label">Porcentaje</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control {{$errors->has('porcentaje') ? 'is-invalid' : ''}}" id="porcentaje" name="porcentaje" placeholder="Ejemplo 10" value="{{ old('porcentaje')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('porcentaje'))
                                        {{$errors->first('porcentaje')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="nota" class="col-sm-2 col-form-label">Nota</label>
                            <div class="col-sm-10">
                                <textarea class="form-control {{$errors->has('nota') ? 'is-invalid' : ''}}" id="nota" name="nota" rows="3" @if($errors->any())  @else disabled @endif>{{ old('nota')}}</textarea>
                                <div class="invalid-feedback">
                                    @if($errors->has('nota'))
                                        {{$errors->first('nota')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="historiaClinica" class="col-sm-2 col-form-label">* Historia Clinica</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control {{$errors->has('historiaClinica') ? 'is-invalid' : ''}}" id="historiaClinica" name="historiaClinica" placeholder="Ejemplo 1-002" value="{{ old('historiaClinica')}}" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('historiaClinica'))
                                        {{$errors->first('historiaClinica')}}
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <label for="formFile" class="col-sm-2 col-form-label">Foto</label>
                            <div class="col-sm-10">
                                <input class="form-control {{$errors->has('txtFoto') ? 'is-invalid' : ''}}" type="file" id="txtFoto" name="txtFoto" @if($errors->any())  @else disabled @endif>
                                <div class="invalid-feedback">
                                    @if($errors->has('txtFoto'))
                                        {{$errors->first('txtFoto')}}
                                    @endif
                                </div>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-sm-6 col-md-6">
                                <div class="d-grid gap-2">
                                    <button type="submit" id="btn_Guardar_Persona" name="btn_Guardar_Persona" class="btn btn-primary" @if($errors->any())  @else disabled @endif>Guardar Persona</button>
                                </div>
                            </div>
                        </div>
                        @csrf
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection
@push('scripts')
<script>
    var buttonverificar = document.getElementById('buttonVerificarPersona');
    let token = "{{csrf_token()}}";
    buttonverificar.addEventListener('click', function(e) {
        ocultarAlertas();
        var alertPersona = document.getElementById('alertPersona');
        alertPersona.setAttribute('style','display:none');
        var cedula = document.getElementById('cedula');
        if(cedula.value.trim() === ''){
            cedula.setAttribute('class','form-control is-invalid');
            alertPersona.setAttribute('style','');
            alertPersona.innerHTML = 'El campo cedula debe estar lleno';
            return false;
        }

        if(cedula.value.length != 10){
                cedula.setAttribute('class','form-control is-invalid');
                alertPersona.setAttribute('style','');
                alertPersona.innerHTML = 'El campo cedula debe contener 10 caracteres';
                return false;
        }
        verificarPersona(cedula.value);
    });

    function ocultarAlertas(){
        document.getElementById('alertPersonaExiste').setAttribute('style','display:none');
        document.getElementById('alertPersonaNoExiste').setAttribute('style','display:none');
        document.getElementById('alertPersona').setAttribute('style','display:none');
    }

    function habilitarFormulario(){
        var nombres = document.getElementById('nombres').removeAttribute('disabled');
        var apellidos = document.getElementById('apellidos').removeAttribute('disabled');
        var fechaNacimiento = document.getElementById('fechaNacimiento').removeAttribute('disabled');
        var estadoCivil = document.getElementById('estadoCivil').removeAttribute('disabled');
        var ocupacion = document.getElementById('ocupacion').removeAttribute('disabled');
        var provincia_id = document.getElementById('provincia_id').removeAttribute('disabled');
        var canton_id = document.getElementById('canton_id').removeAttribute('disabled');
        var ciudad = document.getElementById('ciudad').removeAttribute('disabled');
        var direccion = document.getElementById('direccion').removeAttribute('disabled');
        var telefono = document.getElementById('telefono').removeAttribute('disabled');
        var discapacidad = document.getElementById('discapacidad').removeAttribute('disabled');
        var porcentaje = document.getElementById('porcentaje').removeAttribute('disabled');
        var nota = document.getElementById('nota').removeAttribute('disabled');
        var historiaClinica = document.getElementById('historiaClinica').removeAttribute('disabled');
        var txtFoto = document.getElementById('txtFoto').removeAttribute('disabled');
        var btn_Guardar_Persona = document.getElementById('btn_Guardar_Persona').removeAttribute('disabled');
    }

    function verificarPersona(cedula){
        ocultarAlertas();
        spanSpinnerVerificando = document.getElementById('spanSpinnerVerificando');
        spanSpinnerVerificando.setAttribute('style','');
        axios.post('{{route('verificar.persona')}}',{
            cedula: cedula,
            _token: token
        }).then(function(res) {
            if(res.status==200) {
                var alertPersonaExiste = document.getElementById('alertPersonaExiste');
                txtcedula = document.getElementById('cedula');
                txtcedula2 = document.getElementById('cedula2');
                txtcedula2.value = cedula;
                if(res.data.respuesta == false){
                    habilitarFormulario();
                    alertPersonaNoExiste.setAttribute('style','');
                    txtcedula.setAttribute('type','hidden');
                    txtcedula2.setAttribute('type','text');
                }else{
                    alertPersonaExiste.setAttribute('style','');
                    txtcedula.setAttribute('type','text');
                    txtcedula2.setAttribute('type','hidden');
                }
                spanSpinnerVerificando.setAttribute('style','display:none');
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

