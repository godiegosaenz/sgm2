@extends('layouts.appv2')
@section('title', 'Crear rol')
@push('styles')
<link href="{{ asset('css/dataTables.bootstrap5.min.css') }}" rel="stylesheet">

@endpush
@section('content')
<div class="container">
    <h1 class="mb-4">Crear Rol</h1>
    <form action="{{ route('store.roles') }}" method="POST">
        @csrf

        <div class="mb-3">
            <label for="name" class="form-label">Nombre del Rol</label>
            <input type="text" name="name" id="name" class="form-control @error('name') is-invalid @enderror"
                   value="{{ old('name') }}" required>
            @error('name')
                <div class="invalid-feedback">{{ $message }}</div>
            @enderror
        </div>

        <h5 class="mt-4">Permisos</h5>
        <div class="row">
            @foreach($permissions as $permission)
                <div class="form-check">
                    <input class="form-check-input"
                            type="checkbox"
                            name="permissions[]"
                            value="{{ $permission->id }}"
                            id="permiso{{ $permission->id }}">
                    <label class="form-check-label" for="permiso{{ $permission->id }}">
                        {{ $permission->name }}
                    </label>
                </div>
            @endforeach
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-primary">Guardar Rol</button>
            <a href="" class="btn btn-secondary">Cancelar</a>
        </div>
        <br>
        <br>
    </form>
</div>
@endsection
