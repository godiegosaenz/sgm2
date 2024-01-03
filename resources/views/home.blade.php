@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-12 text-center mb-2">
            <h1 class="">Sistema de gestión municipal </h1>
            <h5>Bienvenid@, {{ auth()->user()->name }}</h5>
            <hr class="my-4">
            <div class="">
                <img width="150px" src="{{ asset('img/logo.jpg')}}" alt="">
            </div>
        </div>

    </div>

</div>
@endsection
