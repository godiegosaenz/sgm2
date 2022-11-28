@extends('layouts.app')
@section('title', 'Categorias')
@push('styles')
@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Panel de categorias</h2>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                @if(session('guardado'))
                    <div class="alert alert-success" role="alert">
                        {{session('guardado')}}
                    </div>
                @endif
            </div>
        </div>
        <div class="row">

            <div class="col-6">
                <div class="card">
                    <div class="card-body">
                        <form action="{{route('store.categoria')}}" method="post">
                            @csrf
                            <h5 class="card-title">Formulario de categoria</h5>
                            <h6 class="card-subtitle mb-2 text-muted">Ingrese los datos</h6>
                            <hr>

                            <div class="mb-3">
                                <label for="nombre">* Nombres:</label>
                                <input type="text" class="form-control {{$errors->has('nombre') ? 'is-invalid' : ''}}" id="nombre" name="nombre" value="{{ old('nombre',$Categoria->nombre) }}">
                                <div class="invalid-feedback">
                                    @if($errors->has('nombre'))
                                        {{$errors->first('nombre')}}
                                    @endif
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="valor">* Valor:</label>
                                <input type="text" class="form-control {{$errors->has('valor') ? 'is-invalid' : ''}}" id="valor" name="valor" value="{{ old('valor',$Categoria->valor) }}">
                                <div class="invalid-feedback">
                                    @if($errors->has('valor'))
                                        {{$errors->first('valor')}}
                                    @endif
                                </div>
                            </div>
                            <div class="mb-3">
                                <button type="submit" class="btn btn-primary">Enviar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div class="col-6">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Lista de categorias</h5>
                        <h6 class="card-subtitle mb-2 text-muted">Ingrese los datos</h6>
                        <hr>
                        <table class="table table-bordered">
                            <thead>
                                {{$Categoria}}
                                <tr>
                                    <th>Nombre</th>
                                    <th>Valor</th>
                                    <th>Acción</th>
                                </tr>
                            </thead>
                            <tbody>
                                @if($Categoria->count() > 0)

                                @endisset
                            </tbody>
                        </table>

                    </div>
                </div>
            </div>
        </div>
        <br>

    </div>
@endsection
@push('scripts')
@endpush
