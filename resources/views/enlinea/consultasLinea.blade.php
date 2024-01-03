@extends('layouts.app')
@section('title', 'Consulta en linea')
@push('styles')
@endpush
@section('content')
<div class="container-fluid">

    <form action="{{route('store.consultar')}}" method="post" id="formConsulta" name="formConsulta">
        @csrf
        <div class="row">
            <div class="col">
                <h2 style="color:#002171;">CONSULTA TUS DEUDAS</h3>

            </div>
        </div>
        <div class="row">
            <div class="col">
                    <div style="display:none" id="aletMensajes" class="alert alert-info">

                    </div>
            </div>
        </div>
        <div class="row">
            <div class="col-3">
                <select name="selectTipo" id="selectTipo" class="form-select">
                    <option value="1">Cedula/ruc</option>
                    <option value="2">Matricula Inmobiliaria</option>
                </select>
            </div>
            <div class="col-5">
                <input name="catCedula" id="catCedula" type="text" class="form-control" placeholder="ingrese numero de cédula">
            </div>
            <div class="col-3">
                <button id="btnConsulta" class="btn btn-primary" type="submit">
                    <span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span>
                    Consultar
                </button>

            </div>
        </div>
    </form>
</div>
<br>
<div style="display:none;" id="container_table" class="container-fluid">

    <div class="row">
        <div class="col-3">
            <div class="card" style="width: 18rem;">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item">TOTAL A PAGAR</li>
                  <li class="list-group-item"><h1 id="totalpagar">0.00</h1></li>

                </ul>
            </div>
        </div>
        <div class="col-6">
            <div class="row">
                <div class="col-12">
                    <div class="card">
                        <ul class="list-group list-group-flush">
                          <li class="list-group-item">IMPUESTO PREDIAL URBANO</li>
                        </ul>
                    </div>
                </div>
            </div>
            <br>
            <div class="row">
                <div class="col-12">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Año</th>
                                <th>Clave catastral</th>
                                <th>Contribuyente</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyurban">

                        </tbody>
                    </table>
                </div>
            </div>
            <br>
            <div class="row">
                <div class="col-12">

                    <div class="card">
                        <ul class="list-group list-group-flush">
                          <li class="list-group-item">IMPUESTO PREDIAL RURAL</li>
                        </ul>
                    </div>
                </div>
            </div>
            <br>
            <div class="row">
                <div class="col-12">

                    <table id="tabla_rural" class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Año</th>
                                <th>Clave catastral</th>
                                <th>Contribuyente</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody id="tbodyrural">

                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>

</div>
@endsection
@push('scripts')
<script>
    function cambiarAtributoButton(){
        btnConsulta.removeAttribute('disabled')
        btnConsulta.innerHTML = '<span id="spanConsulta" class="bi bi-search" role="status" aria-hidden="true"></span> Consultar';
    }

    function msj(mensaje,tipoAlerta){
        aletMensajes.removeAttribute('style');
        aletMensajes.setAttribute('class',tipoAlerta);
        aletMensajes.innerHTML = mensaje;
    }
    let token = "{{csrf_token()}}";
    //let loading = document.getElementById('loading');
    var formConsulta = document.getElementById('formConsulta');
    formConsulta.addEventListener('submit', function(e) {
        e.preventDefault()
        var btnConsulta = document.getElementById('btnConsulta');
        btnConsulta.setAttribute("disabled", "disabled");
        btnConsulta.innerHTML = '<span id="spanConsulta" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Consultando...';
        //var loading = document.getElementById('loading');
        var selectTipo = document.getElementById('selectTipo');
        var catCedula = document.getElementById('catCedula');
        let formData = new FormData(this);
        formData.append('_token',token);
        formData.append('selectTipo',selectTipo.value);
        formData.append('catCedula',catCedula.value);
        var aletMensajes = document.getElementById('aletMensajes');
        axios.post('/consultar/deudas',formData).then(function(res) {
            aletMensajes.setAttribute("style","display: none");
            if(res.status==200) {
                if(res.data.estado == 'ok'){
                    var total = 0;
                    var array_rural = res.data.liquidacionRural;
                    var array_urbano = res.data.liquidacionUrbana;
                    var countRural = Object.keys(array_rural).length;
                    var countUrban = Object.keys(array_urbano).length;
                    var tbodyrural = document.getElementById('tbodyrural');
                    var tbodyurban = document.getElementById('tbodyurban');
                    var tablahtmlUrbano = '';
                    var container_table = document.getElementById('container_table');
                    container_table.removeAttribute('style');
                    if(countUrban > 0){
                        for (let clave2 in array_urbano){
                            var contadorUrbano = 0;
                            for (let clave3 of array_urbano[clave2]) {
                                console.log(array_urbano[clave2][contadorUrbano]['saldo']);
                                console.log(array_urbano[clave2][contadorUrbano]['anio']);
                                var saldoUrbano = parseFloat((array_urbano[clave2][contadorUrbano]['saldo']) + (array_urbano[clave2][contadorUrbano]['valor_complemento']));
                                total = total + saldoUrbano;
                                //d = new Date(array_rural[clave][0]['TitPr_FechaEmision']);
                                tablahtmlUrbano += '<tr>';
                                tablahtmlUrbano += '<td>';
                                tablahtmlUrbano += array_urbano[clave2][contadorUrbano]['anio'];
                                tablahtmlUrbano += '</td>';
                                tablahtmlUrbano += '<td>';
                                tablahtmlUrbano += array_urbano[clave2][contadorUrbano]['clave_cat'];
                                tablahtmlUrbano += '</td>';
                                tablahtmlUrbano += '<td>';
                                tablahtmlUrbano += array_urbano[clave2][contadorUrbano]['contribuyente'];
                                tablahtmlUrbano += '</td>';
                                tablahtmlUrbano += '<td>';
                                tablahtmlUrbano += saldoUrbano.toFixed(2)
                                tablahtmlUrbano += '</td>';
                                tablahtmlUrbano += '</tr>';
                                contadorUrbano = contadorUrbano + 1;
                            }

                        }
                    }
                    //rural
                    var tablahtml = '';

                    if(countRural > 0){
                        for (let clave4 in array_rural){
                            var contadorRural = 0;
                            for (let clave5 of array_rural[clave4]) {
                                var saldoRural =  parseFloat(array_rural[clave4][contadorRural]['TitPr_ValorTCobrado']);
                                total = total + saldoRural;
                                d = new Date(array_rural[clave4][contadorRural]['TitPr_FechaEmision']);
                                tablahtml += '<tr>';
                                tablahtml += '<td>';
                                tablahtml += d.getFullYear();
                                tablahtml += '</td>';
                                tablahtml += '<td>';
                                tablahtml += array_rural[clave4][contadorRural]['Pre_CodigoCatastral'];
                                tablahtml += '</td>';
                                tablahtml += '<td>';
                                tablahtml += array_rural[clave4][contadorRural]['TitPr_Nombres'];
                                tablahtml += '</td>';
                                tablahtml += '<td>';
                                tablahtml += saldoRural.toFixed(2);
                                tablahtml += '</td>';
                                tablahtml += '</tr>';
                                contadorRural = contadorRural + 1;
                            }
                        }
                    }
                    var totalpagar = document.getElementById('totalpagar');
                    totalpagar.innerHTML = total.toFixed(2);
                    tbodyrural.innerHTML = tablahtml;
                    tbodyurban.innerHTML = tablahtmlUrbano;

                    cambiarAtributoButton();

                }else{

                    aletMensajes.removeAttribute('style');
                    aletMensajes.innerHTML = res.data.mensaje;
                    cambiarAtributoButton();
                    console.log('error al consultar al servidor');
                }

            }
        }).catch(function(err) {
            console.log(err);
            if(err.response.status == 500){
                //toastr.error('Error al comunicarse con el servidor, contacte al administrador de Sistemas');
                console.log('error al consultar al servidor');
                msj("Error de conexión, intenta mas tarde","alert alert-danger");
            }

            if(err.response.status == 419){
                //toastr.error('Es posible que tu session haya caducado, vuelve a iniciar sesion');
                console.log('Es posible que tu session haya caducado, vuelve a iniciar sesion');
            }
            if(err.response.status == 422){
                //toastr.error('Revise la validacion del archivo');

            }
            cambiarAtributoButton();
        }).then(function() {
                //loading.style.display = 'none';
        });
    });

</script>
@endpush
