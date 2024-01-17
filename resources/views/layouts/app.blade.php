<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSRF Token -->
    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>@yield('title')</title>
    <link rel="icon" href="img/iconofisioterapia.png">
    <!-- Fonts -->
    <link rel="dns-prefetch" href="//fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600&display=swap" rel="stylesheet">

    <!-- Styles -->
    <link href="{{ asset('css/bootstrap.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/bootstrap-icons.css') }}" rel="stylesheet">
    @stack('styles')
    <!-- Scripts -->
    <script src="{{ asset('js/bootstrap.bundle.min.js') }}" defer></script>
    <script src="{{ asset('js/axios.min.js') }}" defer></script>
</head>
<body>
    <div id="app">
        @auth
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container-fluid">
              <a class="navbar-brand" href="#">SGM2</a>
              <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
              </button>
              <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mb-2 mb-lg-0">
                  <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="{{ route('home')}}"><i class="bi bi-house-fill"></i></a>
                  </li>
                  <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                      Personas
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <li>
                            <a class="dropdown-item" href="{{ route('mostrar.persona') }}">
                                Lista de personas
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="{{ route('ingresar.persona') }}">
                                Ingreso de personas
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#">Reportes</a></li>
                    </ul>
                  </li>
                  <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                      Tesoreria
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <li>
                            <a class="dropdown-item" href="{{ route('index.tesoreria') }}">
                                Aplicacion de exoneracion - urbano
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="{{ route('rural.exoneracion') }}">
                                Aplicacion de exoneracion - rural
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="{{ route('lista.exoneracion') }}">
                                Lista exoneracion
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#">Reportes</a></li>
                    </ul>
                  </li>

                  <li class="nav-item dropdown me-auto">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                      Bienvenido {{auth()->user()->name}}
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                        <li>
                            <a class="dropdown-item" href="{{ route('mostrar.persona') }}">
                                Perfil
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="{{ route('ingresar.persona') }}">
                                Cambiar contraseña
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <form action="{{route('logout')}}" method="post">
                            @csrf
                            <li><a onclick="this.closest('form').submit()" class="dropdown-item" href="#">Cerrar sesión</a></li>
                        </form>
                    </ul>
                  </li>
                </ul>

              </div>
            </div>
        </nav>
        @endauth

        <main class="py-4">
            @yield('content')
        </main>
    </div>
@stack('scripts')
</body>
</html>
