@extends('layouts.app')
@section('title', 'Detalle exoneracion')
@push('styles')
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-12" pb="10">
                <h2 class="text-center">PANEL DE INFORMACION

                </h2>
            </div>
        </div>
        <div class="row justify-content-md-center mt-3">
            <div class="col-10">
                <table id="exoneraciontable" class="table table-sm table-bordered table-hover">
                    <tbody>
                        <tr style="background-color: #BCDCF9">
                            <td colspan="4" style="text-align: center"><strong>Informacion de exoneracion de {{ $ExoneracionAnterior->tipo }}</strong></td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Matricula Inmobiliaria :</td>
                            <td>{{$ExoneracionAnterior->num_predio}}</td>
                            <td><i class="fa fa-user"></i> Usuario :</td>
                            <td><i class="fa fa-user"></i> {{$ExoneracionAnterior->usuario}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Resolución :</td>
                            <td><a href="{{route('descargar.exoneracion',$ExoneracionAnterior->id)}}" target="_blank" rel="noopener noreferrer">{{$ExoneracionAnterior->num_resolucion}}</a></td>
                            <td><i class="fa fa-user"></i> Fecha de creacion :</td>
                            <td>{{$ExoneracionAnterior->created_at}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Observacion :</td>
                            <td colspan="3">{{$ExoneracionAnterior->observacion}}</td>
                        </tr>


                    </tbody>

                </table>
                {{$ExoneracionAnterior->ruta_archivo}}
            </div>
            <div class="col-10">
                <table class="table table-sm table-bordered table-hover">
                    <tr style="background-color: #BCDCF9">
                        <th colspan="6" style="text-align: center">Detalle de liquidaciones afectadas</th>
                    </tr>
                    <tr>
                        <th>Id</th>
                        <th>Liquidaciones</th>
                        <th>Valor</th>
                        <th>Valor anterior</th>
                        <th>fecha</th>
                    </tr>
                    @foreach ($ExoneracionAnterior->exoneracion_detalle as $ed)
                        <tr>
                            <td>{{$ed->liquidacion_id}}</td>
                            <td>{{$ed->cod_liquidacion}}</td>
                            <td>{{$ed->valor}}</td>
                            <td>{{$ed->valor_anterior}}</td>
                            <td>{{$ed->created_at}}</td>
                        </tr>
                    @endforeach
                </table>

            </div>
            <div class="col-10">
                <a class="btn btn-secondary" href="{{route('imprimir.reporte.exoneracion',$ExoneracionAnterior->id)}}" target="_blank" rel="noopener noreferrer"><i class="bi bi-filetype-pdf"></i> Imprimir</a>
            </div>
        </div>

    </div>
@endsection
