<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- CSRF Token -->
    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>{{ config('app.name', 'Laravel') }}</title>

    <!-- Scripts -->
    <script src="{{ asset('js/bootstrap.bundle.min.js') }}" defer></script>

    <!-- Fonts -->
    <link rel="dns-prefetch" href="//fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@400;600&display=swap" rel="stylesheet">

    <!-- Styles -->
    <link href="{{ asset('css/bootstrap.min.css') }}" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="row justify-content-md-center">
        <div class="col-12">
            <section class="vh-100">
                <div class="container py-5 h-100">
                  <div class="row d-flex justify-content-center align-items-center h-100">
                    <div class="col col-xl-10">
                      <div class="card" style="border-radius: 1rem;">
                        <div class="row g-0">
                          <div class="col-md-6 col-lg-5 d-none d-md-block">
                            <img src="{{asset('img/fondologin.jpg')}}"
                              alt="login form" class="img-fluid" style="border-radius: 1rem 0 0 1rem;" />
                          </div>

                          <div class="col-md-6 col-lg-7 d-flex align-items-center">
                            <div class="card-body p-4 p-lg-5 text-black">

                              <form class="requires-validation" action="{{route('login')}}" method="POST" novalidate>
                                @csrf
                                <div class="d-flex align-items-center mb-3 pb-1">
                                  <i class="fas fa-cubes fa-2x me-3" style="color: #ff6219;"></i>
                                  <span class="h1 fw-bold mb-0"><img src="{{asset('img/logosv.png')}}" alt="" height="100px"></span>

                                </div>

                                <h5 class="fw-normal mb-3 pb-3" style="letter-spacing: 1px;">Inicia sesión en tu cuenta</h5>

                                <div class="form-floating mb-4">
                                  <input type="email" id="email" name="email" class="form-control form-control-lg" placeholder="Correo electrónico" aria-describedby="validationemail"/>
                                  <label class="form-label" for="email">Correo electrónico</label>
                                  @error('email')
                                    <span style="color: #ff6219">
                                        {{$message}}
                                    </span>
                                  @enderror
                                </div>


                                <div class="form-floating mb-4">
                                  <input type="password" id="password" name="password" class="form-control form-control-lg" placeholder="Contraseña"/>
                                  <label class="form-label" for="password">Contraseña</label>
                                  @error('password')
                                  <span style="color: #ff6219">
                                      {{$message}}
                                  </span>
                                @enderror
                                </div>

                                <div class="pt-1 mb-4">
                                  <button class="btn btn-primary btn-lg btn-block" type="submit">Entrar</button>
                                </div>

                                <a class="small text-muted" href="#!">¿ Olvidaste tu contraseña?</a>
                                <p class="mb-5 pb-lg-2" style="color: #393f81;">Don't have an account? <a href="#!"
                                    style="color: #393f81;">Register here</a></p>
                                <a href="#!" class="small text-muted">Terms of use.</a>
                                <a href="#!" class="small text-muted">Privacy policy</a>
                              </form>

                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
            </section>
        </div>
    </div>
</div>
@if ($errors->any())
    <div class="alert alert-danger">
        <ul>
            @foreach ($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif
</body>
</html>
