<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <title>Laravel</title>

        <!-- Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@200;600&display=swap" rel="stylesheet">

        <!-- Styles -->
         <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css">
    </head>
    <body>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
        <script src="https://unpkg.com/axios/dist/axios.min.js"></script>

        <div class="container-fluid">
            <div class="row">
                @session('status')
                    <div class="alert alert-danger" role="alert">
                    {{session('status')}}
                    </div>
                @endsession
            </div>
            <form action="{{route('catpredio.consulta')}}" method="post" id="formConsulta" name="formConsulta">
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
        <div id="container_table" class="container-fluid">

            <div class="row">
                <div class="col-3">
                    <div class="card" style="width: 18rem;">
                        <ul class="list-group list-group-flush">
                          <li class="list-group-item">TOTAL A PAGAR</li>
                          <li class="list-group-item"><h1 id="totalpagar">0.00</h1></li>

                        </ul>
                    </div>

                    @isset($valort)
                        <div class="card mt-2" style="width: 18rem;">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item">TOTAL A PAGAR URBANO</li>
                                <li class="list-group-item"><h1 id="totalpagar">{{$valort}}</h1></li>
                            </ul>
                        </div>
                    @endisset

                </div>
                <div class="col-9">
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <ul class="list-group list-group-flush">
                                  <li class="list-group-item"> <h3>IMPUESTO PREDIAL URBANO</h3></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <br>
                    @isset($liquidacionUrbana)

                        @foreach ($liquidacionUrbana as $item)
                            <div class="row">
                                <div class="col-12">
                                    <table class="table table-bordered">
                                        <thead>
                                            <tr>
                                                <th></th>
                                                <th>Año</th>
                                                <th>Matricula</th>
                                                <th>Id liquidacion</th>
                                                <th>Clave catastral</th>
                                                <th>Contribuyente</th>
                                                <th>Total</th>
                                            </tr>
                                        </thead>
                                        <tbody id="tbodyurban">
                                            @foreach ($item as $item2)
                                                <tr>
                                                    @if ($item2->estado_liquidacion == 1)
                                                    <td><i class="bi bi-circle-fill" style="color:green;"></i></td>
                                                    @else
                                                    <td><i class="bi bi-circle-fill" style="color:red;"></i></td>
                                                    @endif
                                                    <td>{{$item2->anio}}</td>
                                                    <td>{{$item2->num_predio}}</td>
                                                    <td>{{$item2->id_liquidacion}}</td>
                                                    <td>{{$item2->clave_cat}}</td>
                                                    @if ($item2->nombres != null)
                                                    <td>{{$item2->nombres.' '.$item2->apellidos }}</td>
                                                    @else
                                                    <td>{{$item2->nombre_comprador}}</td>
                                                    @endif
                                                    <td>{{$item2->total_pago}}</td>
                                                </tr>
                                            @endforeach
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <br>
                        @endforeach
                    @endisset


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
    </body>
</html>
