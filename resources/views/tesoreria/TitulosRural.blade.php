@extends('layouts.appv2')
@section('title', 'Titulo Rural')
@push('styles')
<link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
@endpush
@section('content')
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h4 class="h2">Impresion titulos de credito rural(Coactiva)</h4>
    <div class="btn-toolbar mb-2 mb-md-0">
    <div class="btn-group me-2">
        <button type="button" class="btn btn-sm btn-outline-secondary">Share</button>
        <button type="button" class="btn btn-sm btn-outline-secondary">Export</button>
    </div>
    <button type="button" class="btn btn-sm btn-outline-secondary dropdown-toggle d-flex align-items-center gap-1">
        <svg class="bi"><use xlink:href="#calendar3"/></svg>
        This week
    </button>
    </div>
</div>


    <div class="row">
        <div class="col">
            @if (session('status'))
                <div class="alert alert-success">
                    {{ session('status') }}
                </div>
            @endif
        </div>
    </div>
    <form id="formConsulta" action="{{'tituloscoactivarural'}}" method="post">
        @csrf
        <div class="row justify-content-md-center">
            <div class="col-3">
                <div class="mb-3">
                    <label for="num_predio">* Tipo : </label>
                    <select class="form-select" aria-label="Default select example" id="tipo" name="tipo" onchange="cambiaTipo()">
                        <option value="1" {{ old('tipo') == '1' ? 'selected' : '' }}>Cédula</option>
                        <option value="2" {{ old('tipo') == '2' ? 'selected' : '' }}>Clave Catastral</option>
                    </select>

                </div>
            </div>
            <div class="col-3" id="div_cedula">
                <div class="mb-3">
                    <label for="num_predio">* Cedula : </label>
                   
                    <input type="number" class="form-control {{$errors->has('inputcedula') ? 'is-invalid' : ''}}" id="cedula" name="cedula" value="{{old('cedula')}}" >
                   
                </div>
            </div>
            <div class="col-3" id="div_clave" style="display:none">
                <div class="mb-3">
                    <label for="num_predio">* Clave Catastral : </label>
                   
                    <input type="number" class="form-control {{$errors->has('inputclave') ? 'is-invalid' : ''}}" id="clave" name="clave" value="{{old('clave')}}" >
                   
                </div>
            </div>
            <div class="col-3">
                <div class="mb-3">
                    <br>
                    <button id="btnConsulta" class="btn btn-primary" type="button" onclick="llenarTabla()" >
                        <span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span>
                        Consultar
                    </button>
                </div>
            </div>

        </div>

    </form>

    <form class="" action="tituloscoactivarural/imprimir" id="formExonerar" name="formExonerar" method="post" enctype="multipart/form-data">
        <div class="row">
            <div class="col-12">
                <div class="mb-3">
                    <!-- <a id="reporteLiquidacion1" class="btn btn-secondary" rel="noopener noreferrer">
                        <i class="bi bi-filetype-pdf"></i>
                        General titulos
                    </a> -->
                    <button type="button" class="btn btn-secondary" rel="noopener noreferrer" onclick="generarTitulos()"> 
                        <i class="bi bi-filetype-pdf"></i> General titulos
                    </button>

                </div>

                
                  
            </div>
        </div>
        <div class="row mt-3">
            @if ($num_predio > 0)
            <input type="hidden" class="form-control {{$errors->has('inputMatricula') ? 'is-invalid' : ''}}" id="inputMatricula" name="inputMatricula" value="{{$num_predio}}" autofocus required>
            @endif
            <div class="col-12">
                <h3>Lista de liquidaciones</h3>
            </div>
            <div class="col-md-12">
                @csrf
                <div class="table-responsive">
                    <table class="table table-bordered table-hover" id="tableRural" style="width: 100%">
                        <thead>
                            <tr>
                            <th scope="col">*</th>
                            <th scope="col">Seleccionar</th>
                            <th scope="col">Año</th>
                            <th>Liquidación</th>
                            <th scope="col">Cod. Catastral</th>
                            <th scope="col">Propietario</th>
                            <th scope="col">Total de pago</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyurban">

                            <!-- @isset($liquidacionRural)
                                @foreach ($liquidacionRural as $it )
                                    @php
                                        $anio=explode("-",$it->CarVe_NumTitulo);
                                    @endphp
                                   
                                    <tr>
                                        @if ($it->CarVe_Estado == 'C')
                                        <td><i class="bi bi-circle-fill" style="color:green;"></i></td>
                                        @else
                                        <td><i class="bi bi-circle-fill" style="color:red;"></i></td>
                                        @endif
                                        <td><input class="form-check-input" type="checkbox" value="{{$it->CarVe_NumTitulo}}" name="checkLiquidacion[]"></td>
                                        <td>{{$anio[0]}}</td>
                                        <td>{{$it->CarVe_NumTitulo}}</td>
                                        <td>{{$it->Pre_CodigoCatastral}}</td>
                                        @if($it->Ciu_Nombres != '')
                                            <td>{{$it->Ciu_Nombres.' '.$it->Ciu_Apellidos}}</td>
                                        @else
                                        <td>{{$it->CarVe_Nombres}}</td>
                                        @endif
                                        <td>{{$it->CarVe_CI}}</td>
                                    </tr>
                                @endforeach
                            @endisset -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>

    <div class="modal fade" id="modalContri" tabindex="-1" aria-labelledby="ContribuyenteModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Actualizar Contribuyente</h5>
                    <!-- <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
                </div>
                @csrf
                <div class="modal-body">
                    <div class="col-md-12">
                        <div class="row">
                            <!-- Columna izquierda -->
                            <div class="col-md-12">
                                
                                <div class="mb-3">
                                    <label for="cmb_ruc" class="form-label">CEDULA/RUC</label>
                                    <input type="hidden" class="form-control" name="id_liquidacion" id="id_liquidacion" >
                                    <input type="hidden" class="form-control" name="id_contribuyente" id="id_contribuyente" >
                                    <input type="text" class="form-control" name="cedula_ruc_cont" id="cedula_ruc_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="contribuyente" class="form-label">NOMBRES</label>
                                    <input type="text" class="form-control" name="nombre_cont" id="nombre_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="cmb_ruc_rep" class="form-label">APELLIDOS</label>
                                    <input type="text" class="form-control" name="apellido_cont" id="apellido_cont" >
                                </div>

                                <div class="mb-3">
                                    <label for="representante" class="form-label">DIRECCION</label>
                                    <input type="text" class="form-control" name="direccion_cont" id="direccion_cont" >
                                </div>

                                
                            </div>

                          
                        </div>

                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" onclick="actualizaContribuyente()">
                        Actualizar
                    </button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
@endsection
@push('scripts')
<script>
    var tipo=$('#tipo').val()
   
    if(tipo=="1"){
        $('#div_cedula').show()
        $('#div_clave').hide()
    }else{
        $('#div_cedula').hide()
        $('#div_clave').show()
    }
    var buttonBuscar = document.getElementById('buttonBuscar');
    let token = "{{csrf_token()}}";

    var formExonerar = document.getElementById('formExonerar');
    function verificarSeleccionCasillas(){
        let formData2 = new FormData(formExonerar);
        if(formData2.getAll("checkLiquidacion[]").length > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    const reporteLiquidacion = document.getElementById('reporteLiquidacion');

    reporteLiquidacion.addEventListener('click', (e) => {
        if(verificarSeleccionCasillas() == true){
            let formData = new FormData(formExonerar);
            formExonerar.submit();
            formData.append('num_predio',inputMatricula);
            /*axios.post('tituloscoactiva/imprimir',formData).then(function(res) {
                alert('dentro del formulario');

            }).catch(function(err) {
                console.log(err);
                if(err.response.status == 500){

                    console.log('error al consultar al servidor');
                }

                if(err.response.status == 419){

                }
                if(err.response.status == 422){

                }
            }).then(function() {
                    //loading.style.display = 'none';
            });*/

        }else{
            alert('Seleccione al menos un registro');
            return false;
        }
    });

</script>
<script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

<script src="{{ asset('js/titulocredito/titulocreditorural.js?v='.rand())}}"></script>

@endpush
