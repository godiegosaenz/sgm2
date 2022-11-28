@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-12 text-center mb-2">
            <h1 class="">Sistema de gestión de fisioterapia</h1>
            <h5>Welcome, {{ auth()->user()->email }}</h5>
            <hr class="my-4">
            <div class="">
                <img width="150px" src="{{ asset('img/logo.jpg')}}" alt="">
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-header">Total de pacientes</div>

                <div class="card-body">
                    <span class="badge badge-secondary"><h1>0</h1></span>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-header">Tercera edad</div>

                <div class="card-body">
                    <span class="badge badge-secondary"><h1>0</h1></span>
                </div>
            </div>
        </div>
    </div>
    <div class="row justify-content-center mt-4">

        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-header">Discapacitados</div>

                <div class="card-body">
                    <span class="badge badge-secondary"><h1>0</h1></span>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-header">Niños</div>

                <div class="card-body">
                    <span class="badge badge-secondary"><h1>0</h1></span>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
