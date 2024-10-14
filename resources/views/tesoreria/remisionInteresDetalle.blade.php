@extends('layouts.appv2')
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
                @session('success')
                <div class="alert alert-info" role="alert">
                        {{ $value }}
                    </div>
                @endsession
            </div>
            @if($RemisionInteres->estado == 'creado')
            <div class="col-10">
                <div class="alert alert-info" role="alert">
                    Antes de aplicar la remision debe realizar el cobro en el sistema de gestion municipal
                </div>
            </div>
            @endif
        </div>
        <div class="row justify-content-md-center mt-3">
            <div class="col-10">
                <table id="exoneraciontable" class="table table-sm table-bordered table-hover">
                    <tbody>
                        <tr style="background-color: #BCDCF9">
                            <td colspan="4" style="text-align: center"><strong>Informacion de remision de intereses</strong></td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Matricula Inmobiliaria :</td>
                            <td>{{$RemisionInteres->num_predio}}</td>
                            <td><i class="fa fa-user"></i> Contribuyente :</td>
                            <td><i class="fa fa-user"></i> {{$RemisionInteres->usuario}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Estado :</td>
                            @if ($RemisionInteres->estado == 'creado')
                            <td><span class="badge bg-primary">Creado</span></td>
                            @else
                            <td><span class="badge bg-success">Aplicado</span></td>
                            @endif

                            <td><i class="fa fa-user"></i> Contribuyente :</td>
                            <td><i class="fa fa-user"></i> {{$RemisionInteres->usuario}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Documento adjunto :</td>
                            <td><a href="{{route('descargar.remision',$RemisionInteres->id)}}" target="_blank" rel="noopener noreferrer">{{$RemisionInteres->num_resolucion}}</a></td>
                            <td><i class="fa fa-user"></i> Fecha de creacion :</td>
                            <td>{{$RemisionInteres->created_at}}</td>
                        </tr>
                        <tr>
                            <td><i class="fa fa-user"></i> Observacion :</td>
                            <td colspan="3">{{$RemisionInteres->observacion}}</td>
                        </tr>


                    </tbody>

                </table>
            </div>
            <div class="col-10">
                <table id="detalle" class="table table-sm table-bordered table-hover">
                    <thead>
                        <tr>
                            <th>id</th>
                            <th>Cod Liquidacion</th>
                            <th>Emision</th>
                            <th>Interes</th>
                            <th>Recargo</th>
                            <th>Valor total sin remision</th>
                            <th>Valor total con remision</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach ($RemisionInteres->remision_liquidacion_detalle as $rdl)
                            <tr>
                                <td>{{$rdl->liquidacion_id}}</td>
                                <td>{{$rdl->cod_liquidacion}}</td>
                                <td>{{$rdl->emision}}</td>
                                <td>{{$rdl->interes}}</td>
                                <td>{{$rdl->recargo}}</td>
                                <td>{{$rdl->valor_total_sin_remision}}</td>
                                <td>{{$rdl->valor_total_con_remision}}</td>
                            </tr>
                        @endforeach
                    </tbody>

                </table>
            </div>
            <div class="col-10">
                <form action="{{route('update.remision',$RemisionInteres->id)}}" method="post">
                    @method('PATCH')
                    @csrf
                    <input type="hidden" name="remision_id" id="remision_id" value="{{$RemisionInteres->id}}">

                    @if($RemisionInteres->estado == 'creado')
                    <div class="col-10">
                        <button type="submit" class="btn btn-primary"> Aplicar remision</button>
                    </div>
                    @else

                    @endif
                </form>
            </div>
        </div>

    </div>
@endsection
