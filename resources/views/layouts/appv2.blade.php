<!doctype html>
<html lang="es" data-bs-theme="auto">
  <head><script src="{{ asset('js/color-modes.js') }}"></script>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="Sistema de gestion municipal San Vicente">
    <meta name="author" content="Diego Bermudez">
    <meta name="generator" content="SGM 2.0">
    <!-- CSRF Token -->
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>@yield('title')</title>
    <link rel="icon" href="img/iconofisioterapia.png">
    <link href="{{ asset('css/bootstrap53.min.css') }}" rel="stylesheet">
    <link href="{{ asset('css/bootstrap-icons.css') }}" rel="stylesheet">
    <script src="{{ asset('js/FontAwesomeKit.js') }}" crossorigin="anonymous"></script>

     <!-- PNotify -->
     <link href="{{asset('bower_components/pnotify/dist/pnotify.css')}}" rel="stylesheet">
    <link href="{{asset('bower_components/pnotify/dist/pnotify.buttons.css')}}" rel="stylesheet">
    <link href="{{asset('bower_components/pnotify/dist/pnotify.nonblock.css')}}" rel="stylesheet">

    <style>
      .bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        user-select: none;
      }

      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
          font-size: 3.5rem;
        }
      }

      .b-example-divider {
        width: 100%;
        height: 3rem;
        background-color: rgba(0, 0, 0, .1);
        border: solid rgba(0, 0, 0, .15);
        border-width: 1px 0;
        box-shadow: inset 0 .5em 1.5em rgba(0, 0, 0, .1), inset 0 .125em .5em rgba(0, 0, 0, .15);
      }

      .b-example-vr {
        flex-shrink: 0;
        width: 1.5rem;
        height: 100vh;
      }

      .bi {
        vertical-align: -.125em;
        fill: currentColor;
      }

      .nav-scroller {
        position: relative;
        z-index: 2;
        height: 2.75rem;
        overflow-y: hidden;
      }

      .nav-scroller .nav {
        display: flex;
        flex-wrap: nowrap;
        padding-bottom: 1rem;
        margin-top: -1px;
        overflow-x: auto;
        text-align: center;
        white-space: nowrap;
        -webkit-overflow-scrolling: touch;
      }

      .btn-bd-primary {
        --bd-violet-bg: #712cf9;
        --bd-violet-rgb: 112.520718, 44.062154, 249.437846;

        --bs-btn-font-weight: 600;
        --bs-btn-color: var(--bs-white);
        --bs-btn-bg: var(--bd-violet-bg);
        --bs-btn-border-color: var(--bd-violet-bg);
        --bs-btn-hover-color: var(--bs-white);
        --bs-btn-hover-bg: #6528e0;
        --bs-btn-hover-border-color: #6528e0;
        --bs-btn-focus-shadow-rgb: var(--bd-violet-rgb);
        --bs-btn-active-color: var(--bs-btn-hover-color);
        --bs-btn-active-bg: #5a23c8;
        --bs-btn-active-border-color: #5a23c8;
      }

      .bd-mode-toggle {
        z-index: 1500;
      }

      .bd-mode-toggle .dropdown-menu .active .bi {
        display: block !important;
      }
    </style>
    <!-- Custom styles for this template -->
    <link href="{{ asset('css/dashboard.css') }}" rel="stylesheet">
    @stack('styles')
  </head>
  <body class="bg-body-secondary">
    <svg xmlns="http://www.w3.org/2000/svg" class="d-none">
      <symbol id="check2" viewBox="0 0 16 16">
        <path d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0z"/>
      </symbol>
      <symbol id="circle-half" viewBox="0 0 16 16">
        <path d="M8 15A7 7 0 1 0 8 1v14zm0 1A8 8 0 1 1 8 0a8 8 0 0 1 0 16z"/>
      </symbol>
      <symbol id="moon-stars-fill" viewBox="0 0 16 16">
        <path d="M6 .278a.768.768 0 0 1 .08.858 7.208 7.208 0 0 0-.878 3.46c0 4.021 3.278 7.277 7.318 7.277.527 0 1.04-.055 1.533-.16a.787.787 0 0 1 .81.316.733.733 0 0 1-.031.893A8.349 8.349 0 0 1 8.344 16C3.734 16 0 12.286 0 7.71 0 4.266 2.114 1.312 5.124.06A.752.752 0 0 1 6 .278z"/>
        <path d="M10.794 3.148a.217.217 0 0 1 .412 0l.387 1.162c.173.518.579.924 1.097 1.097l1.162.387a.217.217 0 0 1 0 .412l-1.162.387a1.734 1.734 0 0 0-1.097 1.097l-.387 1.162a.217.217 0 0 1-.412 0l-.387-1.162A1.734 1.734 0 0 0 9.31 6.593l-1.162-.387a.217.217 0 0 1 0-.412l1.162-.387a1.734 1.734 0 0 0 1.097-1.097l.387-1.162zM13.863.099a.145.145 0 0 1 .274 0l.258.774c.115.346.386.617.732.732l.774.258a.145.145 0 0 1 0 .274l-.774.258a1.156 1.156 0 0 0-.732.732l-.258.774a.145.145 0 0 1-.274 0l-.258-.774a1.156 1.156 0 0 0-.732-.732l-.774-.258a.145.145 0 0 1 0-.274l.774-.258c.346-.115.617-.386.732-.732L13.863.1z"/>
      </symbol>
      <symbol id="sun-fill" viewBox="0 0 16 16">
        <path d="M8 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM8 0a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 0zm0 13a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 13zm8-5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2a.5.5 0 0 1 .5.5zM3 8a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2A.5.5 0 0 1 3 8zm10.657-5.657a.5.5 0 0 1 0 .707l-1.414 1.415a.5.5 0 1 1-.707-.708l1.414-1.414a.5.5 0 0 1 .707 0zm-9.193 9.193a.5.5 0 0 1 0 .707L3.05 13.657a.5.5 0 0 1-.707-.707l1.414-1.414a.5.5 0 0 1 .707 0zm9.193 2.121a.5.5 0 0 1-.707 0l-1.414-1.414a.5.5 0 0 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .707zM4.464 4.465a.5.5 0 0 1-.707 0L2.343 3.05a.5.5 0 1 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .708z"/>
      </symbol>
    </svg>

    <div class="dropdown position-fixed bottom-0 end-0 mb-3 me-3 bd-mode-toggle">
      <button class="btn btn-bd-primary py-2 dropdown-toggle d-flex align-items-center"
              id="bd-theme"
              type="button"
              aria-expanded="false"
              data-bs-toggle="dropdown"
              aria-label="Toggle theme (auto)">
        <svg class="bi my-1 theme-icon-active" width="1em" height="1em"><use href="#circle-half"></use></svg>
        <span class="visually-hidden" id="bd-theme-text">Toggle theme</span>
      </button>
      <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="bd-theme-text">
        <li>
          <button type="button" class="dropdown-item d-flex align-items-center tema" data-bs-theme-value="light" aria-pressed="false" onclick="cambiaTema('L')">
            <svg class="bi me-2 opacity-50" width="1em" height="1em"><use href="#sun-fill"></use></svg>
            Light
            <svg class="bi ms-auto d-none" width="1em" height="1em"><use href="#check2"></use></svg>
          </button>
        </li>
        <li>
          <button type="button" class="dropdown-item d-flex align-items-center tema" data-bs-theme-value="dark" aria-pressed="false" onclick="cambiaTema('D')">
            <svg class="bi me-2 opacity-50" width="1em" height="1em"><use href="#moon-stars-fill"></use></svg>
            Dark
            <svg class="bi ms-auto d-none" width="1em" height="1em"><use href="#check2"></use></svg>
          </button>
        </li>
        <li>
          <button type="button" class="dropdown-item d-flex align-items-center active" data-bs-theme-value="auto" aria-pressed="true" onclick="cambiaTema('A')">
            <svg class="bi me-2 opacity-50" width="1em" height="1em"><use href="#circle-half"></use></svg>
            Auto
            <svg class="bi ms-auto d-none" width="1em" height="1em"><use href="#check2"></use></svg>
          </button>
        </li>
      </ul>
    </div>

<svg xmlns="http://www.w3.org/2000/svg" class="d-none">
  <symbol id="door-closed" viewBox="0 0 16 16">
    <path d="M3 2a1 1 0 0 1 1-1h8a1 1 0 0 1 1 1v13h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H3V2zm1 13h8V2H4v13z"/>
    <path d="M9 9a1 1 0 1 0 2 0 1 1 0 0 0-2 0z"/>
  </symbol>
  <symbol id="file-earmark" viewBox="0 0 16 16">
    <path d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5L14 4.5zm-3 0A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V4.5h-2z"/>
  </symbol>
  <symbol id="gear-wide-connected" viewBox="0 0 16 16">
    <path d="M7.068.727c.243-.97 1.62-.97 1.864 0l.071.286a.96.96 0 0 0 1.622.434l.205-.211c.695-.719 1.888-.03 1.613.931l-.08.284a.96.96 0 0 0 1.187 1.187l.283-.081c.96-.275 1.65.918.931 1.613l-.211.205a.96.96 0 0 0 .434 1.622l.286.071c.97.243.97 1.62 0 1.864l-.286.071a.96.96 0 0 0-.434 1.622l.211.205c.719.695.03 1.888-.931 1.613l-.284-.08a.96.96 0 0 0-1.187 1.187l.081.283c.275.96-.918 1.65-1.613.931l-.205-.211a.96.96 0 0 0-1.622.434l-.071.286c-.243.97-1.62.97-1.864 0l-.071-.286a.96.96 0 0 0-1.622-.434l-.205.211c-.695.719-1.888.03-1.613-.931l.08-.284a.96.96 0 0 0-1.186-1.187l-.284.081c-.96.275-1.65-.918-.931-1.613l.211-.205a.96.96 0 0 0-.434-1.622l-.286-.071c-.97-.243-.97-1.62 0-1.864l.286-.071a.96.96 0 0 0 .434-1.622l-.211-.205c-.719-.695-.03-1.888.931-1.613l.284.08a.96.96 0 0 0 1.187-1.186l-.081-.284c-.275-.96.918-1.65 1.613-.931l.205.211a.96.96 0 0 0 1.622-.434l.071-.286zM12.973 8.5H8.25l-2.834 3.779A4.998 4.998 0 0 0 12.973 8.5zm0-1a4.998 4.998 0 0 0-7.557-3.779l2.834 3.78h4.723zM5.048 3.967c-.03.021-.058.043-.087.065l.087-.065zm-.431.355A4.984 4.984 0 0 0 3.002 8c0 1.455.622 2.765 1.615 3.678L7.375 8 4.617 4.322zm.344 7.646.087.065-.087-.065z"/>
  </symbol>
  <symbol id="house-fill" viewBox="0 0 16 16">
    <path d="M8.707 1.5a1 1 0 0 0-1.414 0L.646 8.146a.5.5 0 0 0 .708.708L8 2.207l6.646 6.647a.5.5 0 0 0 .708-.708L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293L8.707 1.5Z"/>
    <path d="m8 3.293 6 6V13.5a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13.5V9.293l6-6Z"/>
  </symbol>
  <symbol id="people" viewBox="0 0 16 16">
    <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1h8Zm-7.978-1A.261.261 0 0 1 7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002a.274.274 0 0 1-.014.002H7.022ZM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4Zm3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0ZM6.936 9.28a5.88 5.88 0 0 0-1.23-.247A7.35 7.35 0 0 0 5 9c-4 0-5 3-5 4 0 .667.333 1 1 1h4.216A2.238 2.238 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816ZM4.92 10A5.493 5.493 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0Zm3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4Z"/>
  </symbol>
  <symbol id="plus-circle" viewBox="0 0 16 16">
    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
    <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
  </symbol>
  <symbol id="puzzle" viewBox="0 0 16 16">
    <path d="M3.112 3.645A1.5 1.5 0 0 1 4.605 2H7a.5.5 0 0 1 .5.5v.382c0 .696-.497 1.182-.872 1.469a.459.459 0 0 0-.115.118.113.113 0 0 0-.012.025L6.5 4.5v.003l.003.01c.004.01.014.028.036.053a.86.86 0 0 0 .27.194C7.09 4.9 7.51 5 8 5c.492 0 .912-.1 1.19-.24a.86.86 0 0 0 .271-.194.213.213 0 0 0 .039-.063v-.009a.112.112 0 0 0-.012-.025.459.459 0 0 0-.115-.118c-.375-.287-.872-.773-.872-1.469V2.5A.5.5 0 0 1 9 2h2.395a1.5 1.5 0 0 1 1.493 1.645L12.645 6.5h.237c.195 0 .42-.147.675-.48.21-.274.528-.52.943-.52.568 0 .947.447 1.154.862C15.877 6.807 16 7.387 16 8s-.123 1.193-.346 1.638c-.207.415-.586.862-1.154.862-.415 0-.733-.246-.943-.52-.255-.333-.48-.48-.675-.48h-.237l.243 2.855A1.5 1.5 0 0 1 11.395 14H9a.5.5 0 0 1-.5-.5v-.382c0-.696.497-1.182.872-1.469a.459.459 0 0 0 .115-.118.113.113 0 0 0 .012-.025L9.5 11.5v-.003a.214.214 0 0 0-.039-.064.859.859 0 0 0-.27-.193C8.91 11.1 8.49 11 8 11c-.491 0-.912.1-1.19.24a.859.859 0 0 0-.271.194.214.214 0 0 0-.039.063v.003l.001.006a.113.113 0 0 0 .012.025c.016.027.05.068.115.118.375.287.872.773.872 1.469v.382a.5.5 0 0 1-.5.5H4.605a1.5 1.5 0 0 1-1.493-1.645L3.356 9.5h-.238c-.195 0-.42.147-.675.48-.21.274-.528.52-.943.52-.568 0-.947-.447-1.154-.862C.123 9.193 0 8.613 0 8s.123-1.193.346-1.638C.553 5.947.932 5.5 1.5 5.5c.415 0 .733.246.943.52.255.333.48.48.675.48h.238l-.244-2.855zM4.605 3a.5.5 0 0 0-.498.55l.001.007.29 3.4A.5.5 0 0 1 3.9 7.5h-.782c-.696 0-1.182-.497-1.469-.872a.459.459 0 0 0-.118-.115.112.112 0 0 0-.025-.012L1.5 6.5h-.003a.213.213 0 0 0-.064.039.86.86 0 0 0-.193.27C1.1 7.09 1 7.51 1 8c0 .491.1.912.24 1.19.07.14.14.225.194.271a.213.213 0 0 0 .063.039H1.5l.006-.001a.112.112 0 0 0 .025-.012.459.459 0 0 0 .118-.115c.287-.375.773-.872 1.469-.872H3.9a.5.5 0 0 1 .498.542l-.29 3.408a.5.5 0 0 0 .497.55h1.878c-.048-.166-.195-.352-.463-.557-.274-.21-.52-.528-.52-.943 0-.568.447-.947.862-1.154C6.807 10.123 7.387 10 8 10s1.193.123 1.638.346c.415.207.862.586.862 1.154 0 .415-.246.733-.52.943-.268.205-.415.39-.463.557h1.878a.5.5 0 0 0 .498-.55l-.001-.007-.29-3.4A.5.5 0 0 1 12.1 8.5h.782c.696 0 1.182.497 1.469.872.05.065.091.099.118.115.013.008.021.01.025.012a.02.02 0 0 0 .006.001h.003a.214.214 0 0 0 .064-.039.86.86 0 0 0 .193-.27c.14-.28.24-.7.24-1.191 0-.492-.1-.912-.24-1.19a.86.86 0 0 0-.194-.271.215.215 0 0 0-.063-.039H14.5l-.006.001a.113.113 0 0 0-.025.012.459.459 0 0 0-.118.115c-.287.375-.773.872-1.469.872H12.1a.5.5 0 0 1-.498-.543l.29-3.407a.5.5 0 0 0-.497-.55H9.517c.048.166.195.352.463.557.274.21.52.528.52.943 0 .568-.447.947-.862 1.154C9.193 5.877 8.613 6 8 6s-1.193-.123-1.638-.346C5.947 5.447 5.5 5.068 5.5 4.5c0-.415.246-.733.52-.943.268-.205.415-.39.463-.557H4.605z"/>
  </symbol>
  <symbol id="search" viewBox="0 0 16 16">
    <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
  </symbol>
  <symbol id="dollar-icon" viewBox="0 0 24 24">
    <!-- Círculo de fondo -->
    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="1.5" fill="none" />

    <!-- Símbolo de dólar en el centro -->
    <path d="M12 5v14M10 8h4a2 2 0 1 1 0 4h-4a2 2 0 1 0 0 4h4"
          fill="none" stroke="currentColor" stroke-width="1.5"
          stroke-linecap="round" stroke-linejoin="round"/>
  </symbol>
  <symbol id="tax-icon" viewBox="0 0 24 24">
    <!-- Documento -->
    <path d="M6 2h8l4 4v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z"
          fill="none" stroke="currentColor" stroke-width="1.5"
          stroke-linecap="round" stroke-linejoin="round" />

    <!-- Símbolo de dólar sobre el documento -->
    <path d="M12 8v8M10 10h4a2 2 0 1 1 0 4h-4a2 2 0 1 0 0 4h4"
          fill="none" stroke="currentColor" stroke-width="1.5"
          stroke-linecap="round" stroke-linejoin="round"/>
  </symbol>
  <symbol id="report-icon" viewBox="0 0 24 24">
    <!-- Documento -->
    <path d="M6 2h8l4 4v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z"
          fill="none" stroke="currentColor" stroke-width="1.5"
          stroke-linecap="round" stroke-linejoin="round"/>

    <!-- Gráfico de barras -->
    <rect x="8" y="12" width="1.5" height="4" fill="currentColor"/>
    <rect x="11" y="10" width="1.5" height="6" fill="currentColor"/>
    <rect x="14" y="8" width="1.5" height="8" fill="currentColor"/>
  </symbol>
  <symbol id="calculator-icon" viewBox="0 0 16 16">
    <!-- Marco de la calculadora -->
    <rect x="2" y="1" width="12" height="14" rx="2" fill="none" stroke="currentColor" stroke-width="1.5"/>

    <!-- Pantalla de la calculadora -->
    <rect x="3" y="2.5" width="10" height="2" fill="currentColor"/>

    <!-- Botones de la calculadora -->
    <circle cx="4.5" cy="7" r="0.8" fill="currentColor"/>
    <circle cx="7.5" cy="7" r="0.8" fill="currentColor"/>
    <circle cx="10.5" cy="7" r="0.8" fill="currentColor"/>

    <circle cx="4.5" cy="9.5" r="0.8" fill="currentColor"/>
    <circle cx="7.5" cy="9.5" r="0.8" fill="currentColor"/>
    <circle cx="10.5" cy="9.5" r="0.8" fill="currentColor"/>

    <circle cx="4.5" cy="12" r="0.8" fill="currentColor"/>
    <circle cx="7.5" cy="12" r="0.8" fill="currentColor"/>
    <rect x="9.8" y="11" width="1.5" height="2" fill="currentColor"/>
  </symbol>
</svg>



<header class="navbar sticky-top bg-dark flex-md-nowrap p-0 shadow" data-bs-theme="dark">
  <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3 fs-6 text-white" href="#">GADM San Vicente</a>

  <div id="navbarSearch" class="navbar-search w-100 collapse">

  </div>
</header>

<div class="container-fluid">
  <div class="row">
    @php
        $menuItems = [
            [
                'name' => 'INICIO',
                'route' => 'home',
                'icon' => 'house-fill',
                'active' => request()->routeIs('home')
            ],
            [
                'name' => 'ADMINISTRACION',
                'icon' => 'people',
                'permission' => 'menu de administracion',
                'subMenu' => [
                    [
                        'name' => 'Lista de clientes',
                        'route' => 'index.ente',
                        'permission' => 'Lista clientes',
                        'active' => request()->routeIs('index.ente')
                    ],
                    [
                        'name' => 'Ingresar cliente',
                        'route' => 'create.ente',
                        'permission' => 'Ingresar clientes',
                        'active' => request()->routeIs('create.ente')
                    ],
                ],
                'active' => request()->routeIs('mostrar.persona') || request()->routeIs('ingresar.persona')
            ],
            [
                'name' => 'TESORERIA',
                'icon' => 'dollar-icon',
                'permission' => 'menu de tesoreria',
                'subMenu' => [
                    [
                        'name' => 'Exoneracion tercera edad',
                        'route' => 'index.tesoreria',
                        'permission' => 'Exoneracion tercera edad',
                        'active' => request()->routeIs('index.tesoreria')
                    ],
                    [
                        'name' => 'Lista exoneracion',
                        'route' => 'lista.exoneracion',
                        'permission' => 'Lista de exoneraciones',
                        'active' => request()->routeIs('lista.exoneracion')
                    ],
                    [
                        'name' => 'Remision de interes',
                        'route' => 'create.remision',
                        'permission' => 'Remision de interes',
                        'active' => request()->routeIs('create.remision')
                    ],
                    [
                        'name' => 'Lista de remisiones',
                        'route' => 'index.remision',
                        'permission' => 'Lista de remisiones',
                        'active' => request()->routeIs('index.remision')
                    ],
                    [
                        'name' => 'Impresion de titulos Urbanos',
                        'route' => 'index.titulocredito',
                        'permission' => 'Impresion de titulos urbanos',
                        'active' => request()->routeIs('index.titulocredito')
                    ],
                    [
                        'name' => 'Impresion de titulos Rural',
                        'route' => 'index.TitulosRural',
                        'permission' => 'Impresion de titulos rurales',
                        'active' => request()->routeIs('index.TitulosRural')
                    ],
                    [
                        'name' => 'Reporte de liquidaciones',
                        'route' => 'consulta.liquidacion.remision',
                        'permission' => 'Reporte de liquidacion',
                        'active' => request()->routeIs('consulta.liquidacion.remision')
                    ],
                ],
                'active' => request()->routeIs('lista.exoneracion') || request()->routeIs('create.remision') || request()->routeIs('index.remision') || request()->routeIs('index.titulocredito') || request()->routeIs('index.TitulosRural')  || request()->routeIs('consulta.liquidacion.remision')
            ],
            [
                'name' => 'RENTAS',
                'icon' => 'calculator-icon',
                'permission' => 'menu de rentas',
                'subMenu' => [
                    [
                        'name' => 'Catastro contribuyentes',
                        'route' => 'index.catastro',
                        'permission' => 'Catastro contribuyentes',
                        'active' => request()->routeIs('index.catastro')
                    ],
                    [
                        'name' => 'Lista de patente',
                        'route' => 'index.patente',
                        'permission' => 'Lista de patentes',
                        'active' => request()->routeIs('index.patente')
                    ],
                    [
                        'name' => 'Declarar patente',
                        'route' => 'create.patente',
                        'permission' => 'Declarar patentes',
                        'active' => request()->routeIs('create.patente')
                    ],
                    [
                        'name' => 'Impuesto transitos',
                        'route' => 'create.transito',
                        'permission' => 'Lista de impuestos transito',
                        'active' => request()->routeIs('create.transito')
                    ],
                    [
                        'name' => 'Listar impuestos transito',
                        'route' => 'index.transito',
                        'permission' => 'Impuestos transito',
                        'active' => request()->routeIs('index.transito')
                    ],

                    [
                        'name' => 'Reporteria Transito',
                        'route' => 'vistaReporteTransito.transito',
                        'permission' => 'Reporte transito',
                        'active' => request()->routeIs('vistaReporteTransito.transito')
                    ],
                ],
                'active' => request()->routeIs('index.catastro') || request()->routeIs('create.catastro') || request()->routeIs('create.patente') || request()->routeIs('create.patente') || request()->routeIs('index.patente') || request()->routeIs('vistaReporteTransito.transito') || request()->routeIs('index.transito') || request()->routeIs('create.transito')
            ],
            [
                'name' => 'CONFIGURACION',
                'icon' => 'gear-wide-connected',
                'permission' => 'menu de configuracion',
                'subMenu' => [
                    [
                        'name' => 'Lista de usuarios',
                        'route' => 'lista.usuario',
                        'permission' => 'Lista de usuarios',
                        'active' => request()->routeIs('lista.usuario')
                    ],
                    [
                        'name' => 'Crear usuario',
                        'route' => 'create.usuario',
                        'permission' => 'Crear usuario',
                        'active' => request()->routeIs('create.usuario')
                    ],
                    [
                        'name' => 'Lista de empleados',
                        'route' => 'mostrar.persona',
                        'permission' => 'Lista de empleados',
                        'active' => request()->routeIs('mostrar.persona')
                    ],
                    [
                        'name' => 'Ingresar empleado',
                        'route' => 'ingresar.persona',
                        'permission' => 'Ingreso de empleados',
                        'active' => request()->routeIs('ingresar.persona')
                    ],
                    [
                        'name' => 'Crear Roles',
                        'route' => 'create.roles',
                        'permission' => 'Crear Roles',
                        'active' => request()->routeIs('create.roles')
                    ],
                ],
                'active' => request()->routeIs('lista.usuario') || request()->routeIs('create.usuario') || request()->routeIs('create.roles')
            ],
            [
                'name' => 'REPORTES',
                'icon' => 'report-icon',
                'permission' => 'menu de reportes',
                'active' => false // Puedes agregar más lógica si necesitas submenús
            ],
            [
                'name' => 'ANALITICA',
                'icon' => 'gear-wide-connected',
                'permission' => 'menu de analitica',
                'subMenu' => [
                    [
                        'name' => 'Analitica contribuyentes',
                        'route' => 'analitica.contribuyente',
                        'active' => request()->routeIs('analitica.contribuyente')
                    ],

                    [
                        'name' => ' Predios por Rango',
                        'route' => 'analitica.predios',
                        'active' => request()->routeIs('analitica.predios')
                    ],

                    [
                        'name' => ' Predios Exonerados',
                        'route' => 'analiticaExonerados.predios',
                        'active' => request()->routeIs('analiticaExonerados.predios')
                    ],

                ],
                'active' => request()->routeIs('analitica.contribuyente') || request()->routeIs('analitica.predios') || request()->routeIs('analiticaExonerados.predios')
            ],
        ];
    @endphp

    <div class="sidebar border border-right col-md-3 col-lg-2 p-0 bg-body-tertiary min-vh-100" data-bs-theme="dark">
        <div class="offcanvas-md offcanvas-end bg-body-tertiary" tabindex="-1" id="sidebarMenu" aria-labelledby="sidebarMenuLabel">
            <div class="offcanvas-header">
                <h5 class="offcanvas-title" id="sidebarMenuLabel">GADM San Vicente</h5>
                <button type="button" class="btn-close" data-bs-dismiss="offcanvas" data-bs-target="#sidebarMenu" aria-label="Close"></button>
            </div>
            <div class="offcanvas-body d-md-flex flex-column p-0 pt-lg-3 overflow-y-auto">
                <ul class="nav flex-column">
                    @foreach ($menuItems as $item)
                        @if (!isset($item['permission']) || auth()->user()->can($item['permission']))
                        <li class="nav-item">
                            @if (isset($item['subMenu']))
                                <a class="nav-link d-flex justify-content-between align-items-center gap-2" data-bs-toggle="collapse" href="#submenu{{ $loop->index }}" role="button" aria-expanded="{{ $item['active'] ? 'true' : 'false' }}">
                                    <div class="d-flex align-items-center gap-2">
                                        <svg class="bi"><use xlink:href="#{{ $item['icon'] }}"/></svg>
                                        {{ $item['name'] }}
                                    </div>
                                </a>
                                <div class="collapse {{ $item['active'] ? 'show' : '' }}" id="submenu{{ $loop->index }}">
                                    <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                                        @foreach ($item['subMenu'] as $subItem)
                                            @if (!isset($subItem['permission']) || auth()->user()->can($subItem['permission']))
                                                <li class="nav-item d-flex align-items-center">
                                                    <a href="{{ route($subItem['route']) }}" class="nav-link ms-4 me-2 {{ $subItem['active'] ? 'active' : '' }}">{{ $subItem['name'] }}</a>
                                                </li>
                                            @endif
                                        @endforeach
                                    </ul>
                                </div>

                            @else
                                <a class="nav-link d-flex align-items-center gap-2 {{ $item['active'] ?? false ? 'active' : '' }}" href="{{ isset($item['route']) ? route($item['route']) : '#' }}">
                                    <svg class="bi"><use xlink:href="#{{ $item['icon'] }}"/></svg>
                                    {{ $item['name'] }}
                                </a>
                            @endif
                        </li>
                        @endif
                    @endforeach
                </ul>
                <hr class="my-3">
                <ul class="nav flex-column mb-auto">
                    <li class="nav-item">
                    <form action="{{route('logout')}}" method="post">
                        @csrf
                        <a onclick="this.closest('form').submit()" class="nav-link d-flex align-items-center gap-2" href="#">
                            <svg class="bi"><use xlink:href="#door-closed"/></svg>
                            Cerrar sesión
                        </a>
                    </form>
                    </li>
                </ul>
            </div>
        </div>
    </div>


    <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
      @yield('content')

    </main>
  </div>
</div>
<script src="{{ asset('js/bootstrap53.bundle.min.js') }}" defer></script>
<script src="{{ asset('js/axios.min.js') }}" defer></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.3.2/dist/chart.umd.js" integrity="sha384-eI7PSr3L1XLISH8JdDII5YN/njoSsxfbrkCTnJrzXt+ENP5MOVBxD+l6sEG4zoLp" crossorigin="anonymous"></script>
{{-- PNotify --}}
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pnotify/3.2.1/pnotify.js"></script>

  <script src="{{asset('bower_components/pnotify/dist/pnotify.js')}}"></script>
  <script src="{{asset('bower_components/pnotify/dist/pnotify.buttons.js')}}"></script>
<script>
  function vistacargando(estado){
    mostarOcultarVentanaCarga(estado,'');
  }

  function vistacargando(estado, mensaje){
    mostarOcultarVentanaCarga(estado, mensaje);
  }

  function mostarOcultarVentanaCarga(estado, mensaje){
    //estado --> M:mostrar, otra letra: Ocultamos la ventana
    // mensaje --> el texto que se carga al mostrar la ventana de carga
    if(estado=='M' || estado=='m'){
        // console.log(mensaje);
        $('#modal_cargando_title').html(mensaje);
        $('#modal_cargando').show();
        $('body').css('overflow', 'hidden');
    }else{
        $('#modal_cargando_title').html('Cargando');
        $('#modal_cargando').hide();
        $('body').css('overflow', '');
    }
  }

  function alertNotificar(texto, tipo,time=3000){
      PNotify.removeAll()
      new PNotify({
          title: 'Mensaje de Información',
          text: texto,
          type: tipo,
          hide: true,
          delay: time,
          styling: 'bootstrap3',
          addclass: ''
      });
  }
</script>
@stack('scripts')
@include('divcargando')
</html>
