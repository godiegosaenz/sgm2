@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">
<link href="{{ asset('css/rowReorder.bootstrap5.min.css') }}" rel="stylesheet">
<link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
<style>
    tfoot input {
        width: 100%;
        padding: 3px;
        box-sizing: border-box;
    }
</style>
@endpush
@section('content')
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h3 class="h2">Impuestos Unidad de Transito</h3>

    </div>
    @if(@session('error'))
            <div class="alert alert-danger">
                {{session('error')}}
            </div>
    @endif
    @if(@session('success'))
            <div class="alert alert-success">
                {{session('success')}}
            </div>
    @endif
    <!-- Mensaje general de errores -->
    @if($errors->any())
        <div class="alert alert-danger">
            "Por favor, revisa los campos obligatorios y corrige los errores indicados para poder continuar."
        </div>
    @endif
    <form method="POST" action="{{ route('store.transito') }}">
        @csrf
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-5">Datos de cliente</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="propietario" class="form-label">Busqueda por cedula <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <input type="number" class="form-control {{$errors->has('cliente_id') ? 'is-invalid' : ''}}" id="cliente_id" name="cliente_id" placeholder="Ingrese cedula o ruc" value="{{old('cliente_id')}}" required>
                        <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#modalCrearEnte">
                            Nuevo
                            <span id="spinner" class="spinner-border spinner-border-sm" style="display:none;"></span>
                        </button>
                        <div class="invalid-feedback" id="error_cliente_id">
                        </div>
                    </div>
                </div>
                <input type="hidden" id="cliente_id_2" name="cliente_id_2" value="">

                <div class="col-md-6 mb-3">
                    <label for="razon_social" class="form-label">Nombres y apellidos</label>
                    <input type="text" class="form-control" id="nombrescliente" name="nombrescliente" maxlength="255" value="{{old('nombresPropietario2')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Correo</label>
                    <input type="text" class="form-control" id="correocliente" maxlength="255" value="{{old('correocliente')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Telefono</label>
                    <input type="text" class="form-control" id="telefonocliente" maxlength="255" value="{{old('telefonocliente')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Direccion</label>
                    <input type="text" class="form-control" id="direccioncliente" maxlength="255" value="{{old('direccioncliente')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Fecha de nacimiento</label>
                    <input type="text" class="form-control" id="fechanacimientocliente" maxlength="255" value="{{old('fechanacimientocliente')}}" disabled>

                </div>
            </div>
        </fieldset>

        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-5">Datos de vehiculo</legend>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="propietario" class="form-label">Busqueda por placa <span class="text-danger">*</span></label>
                    <div class="input-group">

                        <input type="text" class="form-control {{$errors->has('vehiculo_id') ? 'is-invalid' : ''}}" id="vehiculo_id" name="vehiculo_id" placeholder="Ingrese una placa" value="{{old('vehiculo_id')}}" required>
                        <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#propietarioModal">
                            Nuevo
                            <span id="spinner2" class="spinner-border spinner-border-sm" style="display:none;"></span>
                        </button>
                        <div class="invalid-feedback" id="error_vehiculo_id">

                        </div>
                    </div>
                </div>
                <input type="hidden" id="vehiculo_id_2" name="vehiculo_id_2" value="">

                <div class="col-md-6 mb-3">
                    <label for="razon_social" class="form-label">Avaluo</label>
                    <input type="text" class="form-control" id="avaluo" name="nombresPropietario" maxlength="255" value="{{old('nombresPropietario2')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Chasis</label>
                    <input type="text" class="form-control" id="chasis" maxlength="255" value="{{old('nombresRepresentante2')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Año modelo</label>
                    <input type="text" class="form-control" id="year_modelo" maxlength="255" value="{{old('nombresRepresentante2')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Marca</label>
                    <input type="text" class="form-control" id="marca" maxlength="255" value="{{old('nombresRepresentante2')}}" disabled>

                </div>
                <div class="col-md-6 mb-3">
                    <label for="nombresRepresentante" class="form-label">Tipo</label>
                    <input type="text" class="form-control" id="tipo" maxlength="255" value="{{old('nombresRepresentante2')}}" disabled>

                </div>
            </div>
        </fieldset>

        <!-- Conceptos -->
        <fieldset class="border p-3 mb-4">
            <legend class="float-none w-auto px-3 fs-5">Detalle de impuestp</legend>
            <div class="row align-items-end mb-3">
                <div class="col-md-6">
                    <select class="form-select {{ $errors->has('year_declaracion') ? 'is-invalid' : '' }}" id="year_declaracion" name="year_declaracion">
                        <option value="">Seleccione año</option>
                        @foreach ($year as $y)
                            <option value="{{ $y->year }}">{{ $y->year }}</option>
                        @endforeach
                    </select>
                    <div class="invalid-feedback" id="error_year_declaracion"></div>
                </div>

                <div class="col-md-6 d-grid gap-2">
                    <button class="btn btn-primary" id="btn-calcular">
                        <span id="btn-text">Calcular</span>
                        <span id="spinner-btn" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                    </button>
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Rubros</label>
                <div id="lista-conceptos">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Seleccionar</th>
                                <th>Descripción</th>
                                <th>Valor (USD)</th>
                            </tr>
                        </thead>
                        <tbody id="tabla-conceptos">
                            @foreach($conceptos as $concepto)
                                <tr>
                                    <td>
                                        <input type="checkbox" class="form-check-input concepto-check" data-id="{{ $concepto->id }}">
                                    </td>
                                    <td>{{ $concepto->concepto }}</td>
                                    <td>
                                        <input type="number" step="0.01" class="form-control concepto-valor" id="valor_{{ $concepto->id }}" value="{{ $concepto->valor }}">
                                    </td>
                                </tr>
                            @endforeach
                        </tbody>
                        <tfoot>
                            <tr>
                                <th></th>
                                <th>TOTAL</th>
                                <th><input type="number" step="0.01" class="form-control" id="total_concepto" value=""></th>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </fieldset>

        <!-- Botón de envío -->
        <button id="btn-guardar" class="btn btn-primary" type="button">
            <span id="spinner" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
            Registrar impuesto
        </button>
        <br>
        <br>
    </form>
    <!-- Modal para cliente -->
    <div class="modal fade" id="modalCrearEnte" tabindex="-1" aria-labelledby="modalCrearEnteLabel" aria-hidden="true">
        <div class="modal-dialog">
          <form method="POST" action="{{ route('store.ente') }}">
            @csrf
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" id="modalCrearEnteLabel">Nuevo Cliente</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Cédula o RUC</label>
                    <input type="text" class="form-control" name="identificacion" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Nombres</label>
                    <input type="text" class="form-control" name="nombres" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Apellidos</label>
                    <input type="text" class="form-control" name="apellidos" required>
                </div>
              </div>
              <div class="modal-footer">
                <button type="submit" class="btn btn-success">Guardar Cliente</button>
              </div>
            </div>
          </form>
        </div>
    </div>

@endsection
@push('scripts')
<script>
    let token = "{{csrf_token()}}";
    document.getElementById('vehiculo_id').addEventListener('keypress', function(event) {
        // Verificar si la tecla presionada es 'Enter' (keyCode 13)
        if (event.key === 'Enter') {
            // Obtener el valor del input
            event.preventDefault();
            let query = event.target.value;
            const spinner = document.getElementById('spinner');
            spinner.style.display = 'inline-block';
            let chasis = document.getElementById('chasis');
            let tipo = document.getElementById('tipo');
            let year_modelo = document.getElementById('year_modelo');
            let avaluo = document.getElementById('avaluo');
            let marca = document.getElementById('marca');
            let vehiculo_id_2 = document.getElementById('vehiculo_id_2');


            // Asegurarte que no esté vacío
            if (query.trim() !== '') {
                axios.post('{{route('get.placa.transitovehiculo')}}', {
                    _token: token,
                    query:query
                    }).then(function(res) {
                        //propietario.value = res.data.ci_ruc;
                        if(res.status == 200)
                        {
                            chasis.value = res.data.chasis ?? 'S/N';
                            tipo.value = res.data.tipo_id ?? 'S/N';
                            year_modelo.value = res.data.year ?? 'S/N';
                            avaluo.value = res.data.avaluo ?? 'S/N';
                            marca.value = res.data.marca_id ?? 'S/N';
                            vehiculo_id_2.value = res.data.id ?? "";
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function(err) {

                        if(err.request.status == 500){
                            console.log('error al consultar al servidor');
                        }
                        if(err.request.status == 404){
                            let errorResponseS = JSON.parse(err.request.response);

                            chasis.value = errorResponseS.message;
                            tipo.value = errorResponseS.message;
                            year_modelo.value = errorResponseS.message;
                            avaluo.value = errorResponseS.message;
                            marca.value = errorResponseS.message;
                            vehiculo_id_2.value = "";
                        }
                        if(err.request.status  == 419){
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
            } else {
                spinner.style.display = 'none';
            }
        }
    });
    document.getElementById('cliente_id').addEventListener('keypress', function(event) {
        // Verificar si la tecla presionada es 'Enter' (keyCode 13)
        if (event.key === 'Enter') {
            // Obtener el valor del input
            event.preventDefault();
            let query = event.target.value;
            const spinner = document.getElementById('spinner');
            spinner.style.display = 'inline-block';
            let nombrescliente = document.getElementById('nombrescliente');
            let telefonocliente = document.getElementById('telefonocliente');
            let correocliente = document.getElementById('correocliente');
            let direccioncliente = document.getElementById('direccioncliente');
            let fechanacimientocliente = document.getElementById('fechanacimientocliente');
            let cliente_id_2 = document.getElementById('cliente_id_2');


            // Asegurarte que no esté vacío
            if (query.trim() !== '') {
                axios.post('{{route('get.cedula.transitoente')}}', {
                    _token: token,
                    query:query
                    }).then(function(res) {
                        //propietario.value = res.data.ci_ruc;
                        if(res.status == 200)
                        {
                            nombrescliente.value = res.data.nombres+' '+res.data.apellidos;
                            correocliente.value = res.data.correo ?? 'S/N';
                            telefonocliente.value = res.data.telefono ?? 'S/N';
                            direccioncliente.value = res.data.direccion ?? 'S/N';
                            fechanacimientocliente.value = res.data.fecha_nacimiento ?? 'S/N';
                            cliente_id_2.value = res.data.id ?? 0;
                        }
                        //propietario.focus();
                        spinner.style.display = 'none';
                    }).catch(function(err) {

                        if(err.request.status == 500){
                            console.log('error al consultar al servidor');
                        }
                        if(err.request.status == 404){
                            let errorResponse = JSON.parse(err.request.response);
                            nombrescliente.value = errorResponse.message;
                            nombrescliente.value = errorResponse.message;
                            correocliente.value = errorResponse.message;
                            telefonocliente.value = errorResponse.message;
                            direccioncliente.value = errorResponse.message;
                            fechanacimientocliente.value = errorResponse.message;
                            cliente_id_2.value =  "";
                        }
                        if(err.request.status  == 419){
                            console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                        }
                        spinner.style.display = 'none';
                    });
            } else {
                spinner.style.display = 'none';
            }
        }
    });
    document.getElementById('btn-calcular').addEventListener('click', function (e) {
        e.preventDefault(); // Previene que el formulario se envíe si está dentro de uno

        const conceptos = [];
        const token = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
        const vehiculo_id = document.getElementById('vehiculo_id_2').value;
        const cliente_id = document.getElementById('cliente_id_2').value;
        const year = document.getElementById('year_declaracion').value;

        if (!vehiculo_id || !cliente_id || !year) {
            alert('Por favor llene los campos: vehiculo, cliente y Año de impuesto');
            return;
        }

        // Mostrar spinner en el botón
        document.getElementById('spinner-btn').classList.remove('d-none');
        document.getElementById('btn-text').textContent = 'Calculando...';

        document.querySelectorAll('.concepto-check:checked').forEach(checkbox => {
            const id = checkbox.getAttribute('data-id');
            const valor = parseFloat(document.getElementById(`valor_${id}`).value) || 0;
            conceptos.push({ id, valor });
        });

        if (conceptos.length > 0) {
            const spinner = document.getElementById('spinner-total');
            if (spinner) spinner.style.display = 'inline-block';

            axios.post('{{ route("calcular.transito") }}', {
                _token: token,
                conceptos: conceptos,
                vehiculo_id: vehiculo_id,
                cliente_id: cliente_id,
                year: year
            }).then(function(res) {
                if (res.status === 200) {
                    // Reemplazar los valores en los inputs
                    let total = 0;
                    res.data.conceptos.forEach(function (concepto) {
                        const input = document.getElementById('valor_' + concepto.id);
                        if (input) {
                            input.value = concepto.nuevo_valor;
                            total += parseFloat(concepto.nuevo_valor);
                        }
                    });
                    // Mostrar el total con dos decimales
                    document.getElementById('total_concepto').value = total.toFixed(2);
                    console.log(total);
                }
                if (spinner) spinner.style.display = 'none';
            }).catch(function(err) {
                if (err.request.status === 500) {
                    console.log('Error al consultar al servidor.');
                }
                if (err.request.status === 419) {
                    console.log('Sesión caducada, vuelve a iniciar sesión.');
                }
                if (spinner) spinner.style.display = 'none';
            }).finally(function () {
                // Aquí se detiene el spinner y se restaura el texto del botón
                document.getElementById('spinner-btn').classList.add('d-none');
                document.getElementById('btn-text').textContent = 'Calcular';
            });;
        } else {
            alert('Selecciona al menos un concepto para calcular.');
        }
    });

    document.getElementById('btn-guardar').addEventListener('click', function () {
        // limpiar errores
        ['vehiculo_id', 'cliente_id', 'year_declaracion'].forEach(id => {
            const input = document.getElementById(id);
            const errorDiv = document.getElementById('error_' + id);

            if (input) {
                input.addEventListener('input', limpiarError);
                input.addEventListener('change', limpiarError); // para tipo number y selects

                function limpiarError() {
                    input.classList.remove('is-invalid');
                    if (errorDiv) errorDiv.textContent = '';
                }
            }
        });
        const vehiculo_id_2 = document.getElementById('vehiculo_id_2').value;
        const cliente_id_2 = document.getElementById('cliente_id_2').value;
        const year_declaracion = document.getElementById('year_declaracion').value;
        const btn = document.getElementById('btn-guardar');
        const spinner = btn.querySelector('.spinner-border');
        btn.disabled = true;
        spinner.classList.remove('d-none');

        const conceptosSeleccionados = [];

        document.querySelectorAll('.concepto-check:checked').forEach(function (checkbox) {
            const id = checkbox.dataset.id;
            const valor = document.getElementById('valor_' + id).value;
            conceptosSeleccionados.push({
                id: id,
                valor: valor
            });
        });

        // Validación simple: debe haber al menos uno
        if (conceptosSeleccionados.length === 0) {
            alert('Debes seleccionar al menos un concepto.');
             // En el then o catch
             btn.disabled = false;
            spinner.classList.add('d-none');
            return;
        }

        axios.post('{{ route("store.transito") }}', {
            _token: '{{ csrf_token() }}',
            conceptos: conceptosSeleccionados,
            vehiculo_id_2: vehiculo_id_2,
            cliente_id_2: cliente_id_2,
            year_declaracion: year_declaracion
        })
        .then(function (res) {
             // limpiar errores
            ['vehiculo_id', 'cliente_id', 'year_declaracion'].forEach(id => {
                const input = document.getElementById(id);
                const errorDiv = document.getElementById('error_' + id);

                if (input) {
                    input.addEventListener('input', limpiarError);
                    input.addEventListener('change', limpiarError); // para tipo number y selects

                    function limpiarError() {
                        input.classList.remove('is-invalid');
                        if (errorDiv) errorDiv.textContent = '';
                    }
                }
            });
            if(res.status == 200)
            {

                const id = res.data.id;
                window.location.href = `/transito/previsualizar/${id}`; // Laravel buscará esta ruta con el id

            }
            console.log(res);
           // console.log(response.data.id)
            //const id = response.data.id; // Suponiendo que el id está en la respuesta
            // Redirigir a la página con el id obtenido
            //window.location.href = `/transito/previsualizar/${id}`; // Laravel buscará esta ruta con el id
        })
        .catch(function (err) {
            if (err.response && err.response.status === 422) {
                const errores = err.response.data.errors;

                // Mapear campos internos a visibles
                const campoVisible = {
                    vehiculo_id_2: 'vehiculo_id',
                    cliente_id_2: 'cliente_id',
                    year_declaracion: 'year_declaracion',
                };

                Object.keys(errores).forEach(function (campo) {
                    const visibleCampo = campoVisible[campo] || campo;
                    const input = document.getElementById(visibleCampo);
                    const errorDiv = document.getElementById('error_' + visibleCampo);

                    if (input && errorDiv) {
                        input.classList.add('is-invalid');
                        errorDiv.textContent = errores[campo][0];
                    }
                });

            } else {
                alert('Error al guardar los datos.');
            }
        })
        .finally(function () {
            // En el then o catch
            btn.disabled = false;
            spinner.classList.add('d-none');
        });
    });

</script>
@endpush
