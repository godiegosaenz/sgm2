<!DOCTYPE html>
<html lang="es" data-bs-theme="light">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SGME</title>
    <link href="{{ asset('css/bootstrap53.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/bootstrap-icons.css') }}" rel="stylesheet">
    <script src="{{ asset('js/FontAwesomeKit.js') }}" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="{{ asset('css/style.css') }}">
    @stack('styles')
</head>

<body>
    <div class="wrapper">
        <aside id="sidebar" class="js-sidebar">
            <!-- Content For Sidebar -->
            <div class="h-100">
                <div class="sidebar-logo">
                    <a href="#">SGME</a>
                </div>
                <ul class="sidebar-nav">
                    <li class="sidebar-header">
                        Modulo Financiero
                    </li>
                    <li class="sidebar-item">
                        <a href="#" class="sidebar-link">
                            <i class="fa-solid fa-list pe-2"></i>
                            Panel de inicio
                        </a>
                    </li>
                    <li class="sidebar-item">
                        <a href="#" class="sidebar-link collapsed" data-bs-target="#pages" data-bs-toggle="collapse"
                            aria-expanded="false"><i class="fa-solid fa-file-lines pe-2"></i>
                            Administracion
                        </a>
                        <ul id="pages" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                            <li class="sidebar-item">
                                <a href="{{ route('mostrar.persona') }}" class="sidebar-link">Lista de clientes</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('ingresar.persona') }}" class="sidebar-link">Crear cliente</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-item">
                        <a href="#" class="sidebar-link collapsed" data-bs-target="#posts" data-bs-toggle="collapse"
                            aria-expanded="false"><i class="fa-solid fa-sliders pe-2"></i>
                            Tesoreria
                        </a>
                        <ul id="posts" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                            <li class="sidebar-item">
                                <a href="{{ route('index.tesoreria') }}" class="sidebar-link">Exoneracion tercera edad</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('lista.exoneracion') }}" class="sidebar-link">Lista de exoneraciones</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('create.remision') }}" class="sidebar-link">Remision de interes</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('index.remision') }}" class="sidebar-link">Lista de remisiones</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('index.titulocredito') }}" class="sidebar-link">Impresion de titulos</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('consulta.liquidacion.remision')}}" class="sidebar-link">Reporte de liquidaciones</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-item">
                        <a href="#" class="sidebar-link collapsed" data-bs-target="#auth" data-bs-toggle="collapse"
                            aria-expanded="false"><i class="fa-regular fa-user pe-2"></i>
                            Rentas
                        </a>
                        <ul id="auth" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                            <li class="sidebar-item">
                                <a href="{{route('create.catastro')}}" class="sidebar-link">Registrar catastro patente</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{route('create.patente')}}" class="sidebar-link">Crear Patente</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-item">
                        <a href="#" class="sidebar-link collapsed" data-bs-target="#configuraciones" data-bs-toggle="collapse"
                            aria-expanded="false"><i class="fa-regular fa-user pe-2"></i>
                            Configuracion
                        </a>
                        <ul id="configuraciones" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                            <li class="sidebar-item">
                                <a href="{{route('lista.usuario')}}" class="sidebar-link">Lista de usuarios</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('create.usuario') }}" class="sidebar-link">Crear usuario</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('mostrar.persona') }}" class="sidebar-link">Lista de empleados</a>
                            </li>
                            <li class="sidebar-item">
                                <a href="{{ route('ingresar.persona') }}" class="sidebar-link">Crear empleado</a>
                            </li>
                        </ul>
                    </li>
                    <li class="sidebar-header">
                        Multi Level Menu
                    </li>
                    <li class="sidebar-item">
                        <a href="#" class="sidebar-link collapsed" data-bs-target="#multi" data-bs-toggle="collapse"
                            aria-expanded="false"><i class="fa-solid fa-share-nodes pe-2"></i>
                            Multi Dropdown
                        </a>
                        <ul id="multi" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                            <li class="sidebar-item">
                                <a href="#" class="sidebar-link collapsed" data-bs-target="#level-1"
                                    data-bs-toggle="collapse" aria-expanded="false">Level 1</a>
                                <ul id="level-1" class="sidebar-dropdown list-unstyled collapse">
                                    <li class="sidebar-item">
                                        <a href="#" class="sidebar-link">Level 1.1</a>
                                    </li>
                                    <li class="sidebar-item">
                                        <a href="#" class="sidebar-link">Level 1.2</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </aside>
        <div class="main">
            <nav class="navbar navbar-expand px-3 border-bottom">
                <button class="btn" id="sidebar-toggle" type="button">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="navbar-collapse navbar">
                    <ul class="navbar-nav">
                        <li class="nav-item dropdown">
                            <a href="#" data-bs-toggle="dropdown" class="nav-icon pe-md-0">
                                <img style="background:white;" src="{{asset('img/User-avatar.png')}}" class="avatar img-fluid rounded" alt="">
                            </a>
                            <div class="dropdown-menu dropdown-menu-end">
                                <a href="#" class="dropdown-item">{{Auth()->user()->name}}</a>
                                <form action="{{route('logout')}}" method="post">
                                    @csrf
                                    <a onclick="this.closest('form').submit()" class="dropdown-item" href="#">Cerrar sesión</a>
                                </form>
                            </div>
                        </li>
                    </ul>
                </div>
            </nav>
            <main class="content px-3 py-2">
                @yield('content')
            </main>
            <a href="#" class="theme-toggle">
                <i class="fa-regular fa-moon"></i>
                <i class="fa-regular fa-sun"></i>
            </a>
            <footer class="footer">
                <div class="container-fluid">
                    <div class="row text-muted">
                        <div class="col-6 text-start">
                            <p class="mb-0">
                                <a href="#" class="text-muted">
                                    <strong>SGME</strong>
                                </a>
                            </p>
                        </div>
                        <div class="col-6 text-end">
                            <ul class="list-inline">
                                <li class="list-inline-item">
                                    <a href="#" class="text-muted">Conctacto</a>
                                </li>
                                <li class="list-inline-item">
                                    <a href="#" class="text-muted">Acerca de</a>
                                </li>
                                <li class="list-inline-item">
                                    <a href="#" class="text-muted">Terminos</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </footer>
        </div>
    </div>
    <script src="{{ asset('js/bootstrap53.bundle.min.js') }}" defer></script>
    <script src="{{ asset('js/script.js') }}"></script>
    <script src="{{ asset('js/axios.min.js') }}" defer></script>
@stack('scripts')
</body>

</html>
