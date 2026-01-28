@extends('layouts.appv2')
@section('title', 'Ocupacional')
@push('styles')
    <link rel="stylesheet" href="{{asset('bower_components/sweetalert/sweetalert.css')}}">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/css/select2.min.css" rel="stylesheet" />
    <style>
        .checkbox-grande {
            transform: scale(1.5);
            /* Ajusta el número para el tamaño deseado */
            margin-right: 8px;
            /* Ajuste opcional */
        }

        .desabilita_txt {
            display: block;
        }

        .readonly-checkbox {
            pointer-events: none;
            /* Evita la interacción */
            opacity: 0.5;
            /* Visualmente deshabilitado */
        }

        .select2-container .select2-selection--single {
            height: 36px !important;
        }

        .fa-custom-border {
            border: 2px solid #28a745;
            /* verde */
            padding: 5px;
            /* espacio interno para que se vea el borde */
            border-radius: 4px;
            /* opcional, esquinas redondeadas */
        }

        .badge-green {
            display: inline-block;
            padding: 4px 10px;
            border: 2px solid #28a745;
            /* verde */
            color: #28a745;
            border-radius: 8px;
            /* estilo badge */
            font-size: 12px;
            font-weight: 600;
        }

        .badge-blue-low {
            display: inline-block;
            padding: 4px 10px;
            border: 2px solid #4da3ff;
            /* azul claro / azul bajo */
            color: #4da3ff;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
        }

        .fila-verde {
            background-color: #28a745 !important;
        }

        .fila-azul {
            background-color: #4da3ff !important;
        }

        .p-4 {
            padding: 0.5rem !important;
        }

        /* SOLO esta tabla */
        .tabla-examen-fisico {
        border-collapse: collapse;
        width: 100%;
        font-family: Arial, sans-serif;
        font-size: 13px;
        }

        .tabla-examen-fisico td {
        border: 1px solid #999;
        padding: 6px;
        vertical-align: top;
        /* background-color: #ccffff; */
        }

        /* Texto vertical */
        .tabla-examen-fisico .vertical {
        writing-mode: vertical-rl;
        transform: rotate(180deg);
        text-align: center;
        font-weight: bold;
        white-space: nowrap;
        }

    </style>


@endpush
@section('content')
    <div id="busqueda_paciente">
        <div
            class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
            <h4 class="h2">Evaluacion Medica Ocupacional</h4>
            <div class="btn-toolbar mb-2 mb-md-0">
                <button type="button" class="btn btn-sm btn-outline-secondary d-flex align-items-center"
                    onclick="abrirModalPaciente()">
                    <i class="bi bi-person-fill"></i>
                </button>

            </div>
        </div>


        <div class="row">
            <div class="col">
                @if (session('status'))
                    <div class="alert alert-success">
                        {{ session('status') }}
                    </div>
                @endif
            </div>
        </div>
        <form id="formConsulta" action="{{''}}" method="post">
            @csrf
            <div class="row justify-content-md-center">



                <div class="col-3 class_urb">
                    <div class="mb-3">
                        <label for="num_predio">* Tipo : </label>
                        <select class="form-select" aria-label="Default select example" id="tipo_busq" name="tipo_busq"
                            onchange="cambiaTipoU()">
                            <option value="1" selected>Cedula</option>
                            <option value="2">Nombres</option>
                        </select>

                    </div>
                </div>
                <div class="col-3 class_urb" id="div_cedula_emp">
                    <div class="mb-3">
                        <label for="num_predio">* Cedula : </label>

                        <input type="number" class="form-control buscarCont" id="cedula_emp" name="cedula_emp">

                    </div>
                </div>

                <div class="col-5 " id="div_nombres" style="display:none">
                    <div class="mb-3">
                        <label for="num_predio">* Nombres : </label>

                        <input type="text" class="form-control buscarCont" id="nombre_empl" name="nombre_empl">

                    </div>
                </div>


            </div>

        </form>

        <div>

            <div class="row mt-3">


                <div class="col-md-12">
                    @csrf
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover" id="tablaPaciente" style="width: 100%">
                            <thead>
                                <tr>
                                    <th scope="col"></th>
                                    <th scope="col">Cedula/RUC</th>
                                    <th scope="col">Nombres</th>
                                    <th>Telefono</th>
                                    <th scope="col">Correo</th>
                                    <th scope="col">Edad</th>
                                </tr>
                            </thead>
                            <tbody id="tbodyPaciente">
                                <tr>
                                    <td colspan="6" style="text-align:center">No hay datos disponibles</td>
                                </tr>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="modalCrearPaciente" tabindex="-1" aria-labelledby="modalCrearEnteLabel"
            aria-hidden="true">
            <div class="modal-dialog modal-lg"> <!-- ancho grande -->
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalPersonaLabel">Formulario Paciente</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <form id="formPersona" autocomplete="off">
                            <div class="row g-3">

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Cedula <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="number" step="0.01" autocomplete="of" class="form-control"
                                                id="cedula_empleado" name="cedula_empleado" onblur="capturaInfoPersona()"
                                                onkeyup="bloqueaInpust(this)">
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Primer Apellido <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" step="0.01" class="form-control" autocomplete="of"
                                                id="primer_apellido" name="primer_apellido" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Segundo Apellido <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" step="0.01" class="form-control" id="segundo_apellido"
                                                autocomplete="of" name="segundo_apellido" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Primer Nombre <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" step="0.01" class="form-control" id="primer_nombre"
                                                autocomplete="of" name="primer_nombre" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Segundo Nombre <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" step="0.01" class="form-control" id="segundo_nombre"
                                                autocomplete="of" name="segundo_nombre" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Fecha de Nacimiento <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="date" step="0.01" class="form-control" id="fecha_nacimiento"
                                                autocomplete="of" name="fecha_nacimiento" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Sexo <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <select class="form-select" id="sexo" name="sexo">
                                                <option value="">Seleccione una opción</option>
                                                <option value="Hombre">Hombre</option>
                                                <option value="Mujer">Mujer</option>

                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Grupo Sanguineo <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" class="form-control" id="grupo_sanguineo"
                                                autocomplete="of" name="grupo_sanguineo" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Lateralidad  <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text"  class="form-control" id="lateridad"
                                                autocomplete="of" name="lateridad" required>
                                        </div>
                                    </div>
                                </div>

                                  <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Correo </label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="email" step="0.01" class="form-control" id="correo"
                                                autocomplete="of" name="correo" required>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12" style="margin-top: 10px;">
                                    <div class="row align-items-center">
                                        <div class="col-md-3 text-end">
                                            <label for="marca_v" class="form-label mb-0">Teléfono <span
                                                    class="text-danger">*</span></label>
                                        </div>
                                        <div class="col-md-7">
                                            <input type="text" step="0.01" class="form-control" id="telefono"
                                                autocomplete="of" name="telefono" required>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">

                        <button type="button" class="btn " id="btn_guarda_act_persona" onclick="guardarPersona()">
                            <span id="nombre_btn_persona"></span>
                        </button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="atencion_paciente" style="display:none">
        <div
            class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
            <h4 class="h2">Atencion Paciente</h4>
            <div class="btn-toolbar mb-2 mb-md-0">
                <button type="button" class="btn btn-sm btn-outline-danger d-flex align-items-center"
                    onclick="volverBusqueda()" style="margin-right:5px">
                    <i class="fa fa-reply-all"></i>
                </button>
                <button type="button" class="btn btn-sm btn-outline-success d-flex align-items-center"
                    onclick="abrirModalPaciente()">
                    <i class="fa fa-refresh"></i>
                </button>


            </div>
        </div>

        <div class="row">

            <div class="col-md-12" style="margin-bottom:7px">
                <div class="container mt-2">

                    <div class="accordion m-0 p-0" id="accordionExample">

                         <!-- Item 0 -->
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingCero">
                                <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseCero" aria-expanded="true" aria-controls="collapseCero">
                                    A. DATOS DEL PACIENTE
                                </button>
                            </h2>
                            <div id="collapseCero" class="accordion-collapse collapse" aria-labelledby="headingCero"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form id="formActualizaSeccionA">

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Cedula<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control seccion_a"
                                                    placeholder="Ingrese el numero de cedula" name="num_cedula_empleado"
                                                    id="num_cedula_empleado">
                                                <input type="hidden" class="form-control seccion_a"
                                                    placeholder="Ingrese el numero de cedula" name="id_empleado"
                                                    id="id_empleado">


                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Primer Apellido<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control seccion_a"
                                                    placeholder="Ingrese el primer apellido" name="primer_apellido_empleado"
                                                    id="primer_apellido_empleado">

                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Segundo Apellido<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control seccion_a"
                                                    placeholder="Ingrese el segundo apellido" name="segundo_apellido_empleado"
                                                    id="segundo_apellido_empleado">

                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Primer Nombre<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control seccion_a"
                                                    placeholder="Ingrese el primer nombre" name="primer_nombre_empleado"
                                                    id="primer_nombre_empleado">

                                            </div>

                                        </div><div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Segundo Nombre<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control seccion_a"
                                                    placeholder="ingrese segundo nombre" name="segundo_nombre_empleado"
                                                    id="segundo_nombre_empleado">

                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha de Nacimiento<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="date" class="form-control seccion_a" name="fecha_nacimiento_empleado"
                                                    id="fecha_nacimiento_empleado" onchange="calculaEdad()">
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Edad<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control seccion_a"
                                                    name="edad_empleado"
                                                    id="edad_empleado" readonly>

                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Sexo<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <select class="form-select seccion_a" id="sexo_empleado" name="sexo_empleado" onchange="seleccionSexo()">
                                                    <option value="">Seleccione una opción</option>
                                                    <option value="Hombre">Hombre</option>
                                                    <option value="Mujer">Mujer</option>

                                                </select>
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Grupo Sanguineo<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control "
                                                    placeholder="Ingrese grupo sanguineo " name="grupo_sanguineo_empleado"
                                                    id="grupo_sanguineo_empleado">

                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                               Lateralidad<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="text" class="form-control "
                                                    placeholder="Ingrese grupo lateralidad" name="lateralidad_empleado"
                                                    id="lateralidad_empleado">

                                            </div>

                                        </div>
                                        <center>
                                            <button type="submit" class="btn btn-sm btn-outline-success d-flex align-items-center"
                                                style="margin-right:5px">
                                            <i class="fa fa-refresh"> </i> &nbsp; Actualizar
                                            </button>
                                        </center>

                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Item 1 -->
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingOne">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
                                    B. MOTIVO DE CONSULTA
                                </button>
                            </h2>
                            <div id="collapseOne" class="accordion-collapse collapse" aria-labelledby="headingOne"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form id="formMotivo"  enctype="multipart/form-data">

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Puesto de Trabajo CIUO<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-8">
                                                <select class="form-select"                                                   id="puesto_cmb" name="puesto_cmb" required>

                                    </select>
                                            </div>
                                            <div class="col-md-1">
                                                <button type="button" onclick="abrirModalPuesto()" class="btn btn-sm btn-outline-success d-flex align-items-center">
                                                    <i class="fa fa-plus"></i>
                                                </button>
                                            </div>

                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha de Atención<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <input type="date" class="form-control " name="fecha_atencion"
                                                    id="fecha_atencion" readonly>
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center" id="div_apellidos">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha de Ingreso al trabajo
                                            </label>
                                            <div class="col-md-9">
                                                <input type="date" class="form-control " name="fecha_ingreso"
                                                    id="fecha_ingreso">
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha de Reintegro

                                            </label>
                                            <div class="col-md-9">
                                                <input type="date" class="form-control " name="fecha_reingreso"
                                                    id="fecha_reingreso">
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Fecha del Último día laboral/salida

                                            </label>
                                            <div class="col-md-9">
                                                <input type="date" class="form-control " name="fecha_ultimo_dia"
                                                    id="fecha_ultimo_dia">
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Tipo Atencion<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <select class="form-select" id="tipo_atencion" name="tipo_atencion">
                                                    <option value="">Seleccione una opción</option>
                                                    <option value="Ingreso">Ingreso</option>
                                                    <option value="Periódico">Periódico</option>
                                                    <option value="Reintegro">Reintegro</option>
                                                    <option value="Retiro">Retiro</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Motivo<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <textarea class="form-control " name="motivo" id="motivo"></textarea>
                                            </div>
                                        </div>

                                        <center>
                                            <button type="submit" class="btn btn-sm btn-outline-success d-flex align-items-center"
                                                style="margin-right:5px">
                                            <i class="fa fa-floppy-o"> </i> &nbsp;&nbsp;Guardar
                                            </button>
                                        </center>

                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Item 2 -->
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingTwo">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                    C. ANTECEDENTES PERSONALES
                                </button>
                            </h2>
                            <div id="collapseTwo" class="accordion-collapse collapse" aria-labelledby="headingTwo"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form id="formAntecedentes" method="POST" action="" enctype="multipart/form-data">
                                        <hr>
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                ANTECEDENTES CLÍNICOS Y QUIRÚRGICOS<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <textarea class="form-control " name="antecedente_cq"
                                                    id="antecedente_cq"></textarea>
                                            </div>
                                        </div>
                                        <hr>
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                ANTECEDENTES FAMILIARES<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <textarea class="form-control " name="antecedente_familiares"
                                                    id="antecedente_familiares"></textarea>
                                            </div>
                                        </div>
                                        <hr>
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-12 col-form-label " style="margin-left:50px">
                                                Condición especial para las atenciónes de urgencia,emergencia,y tratamiento
                                                médico (referido por el paciente).
                                            </label>

                                        </div>
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                En caso de requerir transfusiones autoriza<span class="text-danger">
                                                    *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <select class="form-select" id="autoriza_transfusion"
                                                    name="autoriza_transfusion">
                                                    <option value="">Seleccione una opción</option>
                                                    <option value="Si">Si</option>
                                                    <option value="No">No</option>

                                                </select>
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                Se encuentra bajo algún tratamiento hormonal<span class="text-danger">
                                                    *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <select class="form-select" id="tratamiento_ormonal"
                                                    name="tratamiento_ormonal" onchange="seleccionTratamientoOrmonal()">
                                                    <option value="">Seleccione una opción</option>
                                                    <option value="Si">Si</option>
                                                    <option value="No">No</option>

                                                </select>
                                            </div>
                                        </div>

                                        <div class="row mb-3 align-items-center" style="display:none"
                                            id="seccion_txt_tratamiento_ormonal">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                ¿Cual Describir?<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <textarea class="form-control " name="txt_tratamiento_ormonal"
                                                    id="txt_tratamiento_ormonal"></textarea>
                                            </div>
                                        </div>
                                        <hr>
                                        <div id="seccion_femenino">
                                            <div class="row mb-3 align-items-center">
                                                <label class="col-md-12 col-form-label " style="margin-left:50px">
                                                    <b>ANTECEDENTES GINECO OBSTÉTRICOS</b>
                                                </label>

                                            </div>

                                            <div class="row mb-3 align-items-center">
                                                <label class="col-md-3 col-form-label " style="text-align: right;">
                                                    Fecha de Última menstruacion<span class="text-danger"> *</span>
                                                </label>
                                                <div class="col-md-9">
                                                    <input type="date" class="form-control " name="fecha_ultima_menstruacion"
                                                        id="fecha_ultima_menstruacion">
                                                </div>
                                            </div>
                                            <div class="row">
                                                <label class="col-md-3 col-form-label " style="text-align: right;">
                                                    <span class="text-danger"> </span>
                                                </label>
                                                <div class="col-md-2">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="gestas"
                                                            name="gestas" value="gestas">
                                                        <label class="form-check-label" for="-">Gestas</label>
                                                    </div>
                                                </div>

                                                <div class="col-md-2">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="partos"
                                                            name="partos" value="partos">
                                                        <label class="form-check-label" for="partos">Partos</label>
                                                    </div>
                                                </div>

                                                <div class="col-md-2">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="cesareas"
                                                            name="cesareas" value="cesareas">
                                                        <label class="form-check-label" for="cesareas">Cesáreas</label>
                                                    </div>
                                                </div>

                                                <div class="col-md-2">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="abortos"
                                                            name="abortos" value="abortos">
                                                        <label class="form-check-label" for="abortos">Abortos</label>
                                                    </div>
                                                </div>
                                            </div>



                                            <!-- CONTENEDOR GENERAL -->
                                            <div id="examenes-container" style="margin-top:12px">

                                                <!-- PRIMER BLOQUE -->
                                                <div class="examen-item">

                                                    <!-- Exámenes realizados -->
                                                    <div class="row mb-3 align-items-center">
                                                        <label class="col-md-3 col-form-label" style="text-align: right;">
                                                            Exámenes realizados
                                                        </label>
                                                        <div class="col-md-9 d-flex gap-2">
                                                            <input type="text" class="form-control" name="examenes[]">
                                                            <!-- SOLO EL PRIMERO TIENE AGREGAR -->
                                                            <button type="button" class="btn btn-success btnAgregar">+</button>
                                                        </div>
                                                    </div>

                                                    <!-- Tiempo -->
                                                    <div class="row mb-3 align-items-center">
                                                        <label class="col-md-3 col-form-label" style="text-align: right;">
                                                            Tiempo
                                                        </label>
                                                        <div class="col-md-9">
                                                            <input type="text" class="form-control" name="tiempo_exa[]">
                                                        </div>
                                                    </div>

                                                    <!-- Resultado -->
                                                    <div class="row mb-3 align-items-center">
                                                        <label class="col-md-3 col-form-label" style="text-align: right;">
                                                            Registrar resultado únicamente si interfiere con la actividad
                                                            laboral y previa autorización del titular
                                                        </label>
                                                        <div class="col-md-9">
                                                            <textarea class="form-control" rows="3"
                                                                name="resultado[]"></textarea>
                                                        </div>
                                                    </div>


                                                </div>

                                            </div>

                                            <div class="row mb-3 align-items-center" style="margin-top: 10px;">
                                                <label class="col-md-3 col-form-label " style="text-align: right;">
                                                    Metodo de planificacion familiar<span class="text-danger"> *</span>
                                                </label>
                                                <div class="col-md-9">
                                                    <select class="form-select" id="metodo_planificacion"
                                                        name="metodo_planificacion"
                                                        onchange="seleccionMetodoPlanificacionFamiliar()">
                                                        <option value="">Seleccione una opción</option>
                                                        <option value="Si">Si</option>
                                                        <option value="No">No</option>
                                                        <option value="No Responde">No Responde</option>

                                                    </select>
                                                </div>
                                            </div>

                                            <div class="row mb-3 align-items-center" style="display:none"
                                                id="seccion_txt_metodo_planificcion_familiar">
                                                <label class="col-md-3 col-form-label " style="text-align: right;">
                                                    ¿Cual?<span class="text-danger"> *</span>
                                                </label>
                                                <div class="col-md-9">
                                                    <textarea class="form-control " name="txt_metodo_planificcion_familiar"
                                                        id="txt_metodo_planificcion_familiar"></textarea>
                                                </div>
                                            </div>
                                        </div>

                                        <hr>
                                        <div id="seccion_masculino">
                                            <div class="row mb-3 align-items-center">
                                                <label class="col-md-12 col-form-label " style="margin-left:50px">
                                                    <b>ANTECEDENTES REPRODUCTIVOS MASCULINOS</b>
                                                </label>

                                            </div>

                                            <div id="examenes-container-masculino">

                                                <!-- PRIMER BLOQUE -->
                                                <div class="examen-item-masculino">

                                                    <!-- Exámenes realizados -->
                                                    <div class="row mb-3 align-items-center">
                                                        <label class="col-md-3 col-form-label" style="text-align: right;">
                                                            Exámenes realizados
                                                        </label>
                                                        <div class="col-md-9 d-flex gap-2">
                                                            <input type="text" class="form-control" name="examenes_masculino[]">
                                                            <!-- SOLO EL PRIMERO TIENE AGREGAR -->
                                                            <button type="button"
                                                                class="btn btn-success btnAgregarMasculino">+</button>
                                                        </div>
                                                    </div>

                                                    <!-- Tiempo -->
                                                    <div class="row mb-3 align-items-center">
                                                        <label class="col-md-3 col-form-label" style="text-align: right;">
                                                            Tiempo
                                                        </label>
                                                        <div class="col-md-9">
                                                            <input type="text" class="form-control" name="tiempo_masculino[]">
                                                        </div>
                                                    </div>

                                                    <!-- Resultado -->
                                                    <div class="row mb-3 align-items-center">
                                                        <label class="col-md-3 col-form-label" style="text-align: right;">
                                                            Registrar resultado únicamente si interfiere con la actividad
                                                            laboral y previa autorización del titular
                                                        </label>
                                                        <div class="col-md-9">
                                                            <textarea class="form-control" rows="3"
                                                                name="resultado_masculino[]"></textarea>
                                                        </div>
                                                    </div>


                                                </div>

                                            </div>

                                            <div class="row mb-3 align-items-center" style="margin-top: 10px;">
                                                <label class="col-md-3 col-form-label " style="text-align: right;">
                                                    Metodo de planificacion familiar<span class="text-danger"> *</span>
                                                </label>
                                                <div class="col-md-9">
                                                    <select class="form-select" id="metodo_planificacion_masculino"
                                                        name="metodo_planificacion_masculino"
                                                        onchange="seleccionMetodoPlanificacionFamiliarMasculino()">
                                                        <option value="">Seleccione una opción</option>
                                                        <option value="Si">Si</option>
                                                        <option value="No">No</option>
                                                        <option value="No Responde">No Responde</option>

                                                    </select>
                                                </div>
                                            </div>

                                            <div class="row mb-3 align-items-center" style="display:none"
                                                id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                <label class="col-md-3 col-form-label " style="text-align: right;">
                                                    ¿Cual?<span class="text-danger"> *</span>
                                                </label>
                                                <div class="col-md-9">
                                                    <textarea class="form-control "
                                                        name="txt_metodo_planificcion_familiar_masculino"
                                                        id="txt_metodo_planificcion_familiar_masculino"></textarea>
                                                </div>
                                            </div>
                                            <hr>

                                            <div class="row align-items-center">
                                                <label class="col-md-12 col-form-label " style="margin-left:50px">
                                                    <b>CONSUMO DE SUSTANCIAS</b>
                                                </label>

                                            </div>


                                            <div class="col-md-12">
                                                <table width="100%">
                                                    <tr>
                                                        <td colspan="2">
                                                            TABACO
                                                        </td>

                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    Tiempo de consumo (meses)
                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    Ex consumidor &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    Tiempo de abstenencia (meses)
                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    >
                                                                </div>
                                                            </div>
                                                        </td>

                                                        <td>
                                                            <div class="row" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    No consume &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                    </tr>

                                                    <tr>
                                                        <td colspan="2">
                                                            ALCOHOL
                                                        </td>

                                                        <td>
                                                            <div class="row" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                    </tr>

                                                    <tr>
                                                        <td>
                                                            OTROS
                                                        </td>
                                                        <td>
                                                            <div class="row" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>

                                                        </td>
                                                        <td>
                                                            <div class="row" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row " id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">

                                                                    </label>

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="txt_metodo_planificcion_familiar_masculino"
                                                                    id="txt_metodo_planificcion_familiar_masculino"
                                                                    rows="3">
                                                                </div>
                                                            </div>
                                                        </td>
                                                    </tr>


                                                </table>
                                            </div>
                                        </div>
                                        <hr>
                                        <div class="row align-items-center">
                                            <label class="col-md-12 col-form-label " style="margin-left:50px">
                                                <b>ESTILO DE VIDA</b>
                                            </label>
                                        </div>

                                        <div class="col-md-12">
                                            <table width="100%";>
                                                <tr>
                                                    <td></td>
                                                    <td>¿CUÁL?</td>
                                                    <td>TIEMPO</td>
                                                </tr>

                                                <tr>
                                                    <td rowspan="3" style="text-align:center; vertical-align:middle;">
                                                        ACTIVIDAD FÍSICA
                                                    </td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>

                                                <tr>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="activida_fisica[0]"
                                                            id="activida_fisica_1"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="tiempo[0]"
                                                            id="tiempo_1"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="activida_fisica[1]"
                                                            id="activida_fisica_2"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="tiempo[1]"
                                                            id="tiempo_2"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>

                                                    </td>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="activida_fisica[2]"
                                                            id="activida_fisica_3"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                     <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="tiempo[2]"
                                                            id="tiempo_3"
                                                            rows="3">
                                                        </div>
                                                    </td>

                                                </tr>
                                            </table>

                                        </div>

                                        <hr>
                                        <div class="row align-items-center">
                                            <label class="col-md-12 col-form-label " style="margin-left:50px">
                                                <b>CONDICION PREEXISTENTE</b>
                                            </label>
                                        </div>

                                        <div class="col-md-12">
                                            <table width="100%";>
                                                <tr>
                                                    <td></td>
                                                    <td>¿CUÁL?</td>
                                                    <td>TIEMPO</td>
                                                </tr>

                                                <tr>
                                                    <td rowspan="3" style="text-align:center; vertical-align:middle;">
                                                        MEDICACION HABITUAL
                                                    </td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>

                                                <tr>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="medicacion[]"
                                                            id="medicacion_1"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="tiempo_medicacion[]"
                                                            id="tiempo_medicacion_1"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="medicacion[]"
                                                            id="medicacion_2"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="tiempo_medicacion[]"
                                                            id="tiempo_medicacion_2"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>

                                                    </td>
                                                    <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="medicacion[]"
                                                            id="medicacion_3"
                                                            rows="3">
                                                        </div>
                                                    </td>
                                                     <td>
                                                        <div class="col-md-12">
                                                            <label class="form-label">

                                                            </label>

                                                            <input type="text"
                                                            class="form-control"
                                                            name="tiempo_medicacion[]"
                                                            id="tiempo_medicacion_3"
                                                            rows="3">
                                                        </div>
                                                    </td>

                                                </tr>
                                            </table>

                                        </div>
                                        <hr>
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                OBSERVACION<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <textarea class="form-control " name="observacion_antecedentes"
                                                    id="observacion_antecedentes"></textarea>
                                            </div>
                                        </div>
                                        <hr>
                                        <center>
                                            <button type="submit" class="btn btn-sm btn-outline-success d-flex align-items-center"
                                                style="margin-right:5px">
                                            <i class="fa fa-floppy-o"> </i> &nbsp;&nbsp;Guardar
                                            </button>
                                        </center>

                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Item 3 -->
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingThree">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
                                    D. ENFERMEDAD O PROBLEMA ACTUAL
                                </button>
                            </h2>
                            <div id="collapseThree" class="accordion-collapse collapse" aria-labelledby="headingThree"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form id="formEnfermedad">
                                        <div class="row mb-3 align-items-center">
                                            <label class="col-md-3 col-form-label " style="text-align: right;">
                                                DESCRIPCION<span class="text-danger"> *</span>
                                            </label>
                                            <div class="col-md-9">
                                                <textarea class="form-control " name="enfermedad_problema_actual"
                                                    id="enfermedad_problema_actual"></textarea>
                                            </div>
                                        </div>
                                        <center>
                                            <button type="submit" class="btn btn-sm btn-outline-success d-flex align-items-center"
                                                style="margin-right:5px">
                                            <i class="fa fa-floppy-o"> </i> &nbsp;&nbsp;Guardar
                                            </button>
                                        </center>

                                    </form>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingFour">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
                                    E. CONSTANTES VITALES Y ANTROPOMETRÍA 
                                </button>
                            </h2>
                            <div id="collapseFour" class="accordion-collapse collapse" aria-labelledby="headingFour"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <form id="formConstanteVitales">
                                        <div class="row mb-3 align-items-center">
                                            <div class="col-md-12">
                                                <table width="100%">
                                                    <tr>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    TEMPERATURA (°C)
                                                                    </label>


                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    PRESIÓN ARTERIAL (mmHg)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    FRECUENCIA CARDIACA (Lat/min)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    FRECUENCIA RESPIRATORIA (fr/min)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    SATURACIÓN DE OXÍGENO (O2%)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    PESO (Kg)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    TALLA (cm)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    ÍNDICE DE MASA CORPORAL (kg/m2)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">
                                                                    <label class="form-label">
                                                                    PERÍMETRO ABDOMINAL (cm)
                                                                    </label>

                                                                </div>
                                                            </div>
                                                        </td>

                                                    </tr>

                                                    <tr>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="temperatura"
                                                                    id="temperatura"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="presion_arterial"
                                                                    id="presion_arterial"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="frecuencia_cardiaca"
                                                                    id="frecuencia_cardiaca"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="frecuencia_respiratoria"
                                                                    id="frecuencia_respiratoria"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="saturacion"
                                                                    id="saturacion"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="peso"
                                                                    id="peso"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="talla"
                                                                    id="talla"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="imc"
                                                                    id="imc"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div class="row mb-3" id="seccion_txt_metodo_planificcion_familiar_masculino">
                                                                <div class="col-md-12">

                                                                    <input type="text"
                                                                    class="form-control"
                                                                    name="perimetro_abdominal"
                                                                    id="perimetro_abdominal"
                                                                >
                                                                </div>
                                                            </div>
                                                        </td>

                                                    </tr>

                                                </table>
                                            </div>
                                        </div>
                                    <center>
                                            <button type="submit" class="btn btn-sm btn-outline-success d-flex align-items-center"
                                                style="margin-right:5px">
                                            <i class="fa fa-floppy-o"> </i> &nbsp;&nbsp;Guardar
                                            </button>
                                        </center>

                                    </form>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingFive">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseFive" aria-expanded="false" aria-controls="collapseFive">
                                    F. EXAMEN FÍSICO REGIONAL
                                </button>
                            </h2>
                            <div id="collapseFive" class="accordion-collapse collapse" aria-labelledby="headingFive"
                                data-bs-parent="#accordionExample">
                                <form id="formExamenFisico">
                                    <div class="accordion-body">
                                        <div class="row mb-3 align-items-center">
                                            <div class="col-md-12">

                                                <table class="tabla-examen-fisico">
                                                    <tr>
                                                        <td class="bg vertical">1. Piel</td>
                                                        <td class="bg">
                                                        a. Cicatríces <br>
                                                        c. Piel  y Faneras
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="cicatrices" value="cicatrices"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="piel_fanera" value="piel_fanera">

                                                        </td>

                                                        <td class="bg vertical">2. Ojos</td>
                                                        <td class="bg">
                                                        a. Párpados<br>
                                                        b. Conjuntivas<br>
                                                        c. Pupilas<br>
                                                        d. Córneas<br>
                                                        e. Motilidad<br>
                                                        </td>
                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="parpados" value="parpados"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="conjuntiva" value="conjuntiva"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="pupila" value="pupila"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="corneas" value="corneas"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="motilidad" value="motilidad"><br>

                                                        </td>

                                                        <td class="bg vertical">3. Oidos</td>
                                                        <td class="bg">
                                                        a. C. auditivo externo<br>
                                                        b. Pabellón<br>
                                                        c. Tímpanos
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="auditvo_externo" value="auditvo_externo"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="pabellon" value="pabellon"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="timpano" value="timpano"><br>


                                                        </td>

                                                        <td class="bg vertical">4. Oro faringe</td>
                                                        <td class="bg">
                                                        a. Labios<br>
                                                        b. Lengua<br>
                                                        c. Faringe<br>
                                                        d. Amígdalas<br>
                                                        e. Dentadura
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="labios" value="labios"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="lengua" value="lengua"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="faringe" value="faringe"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="amigdalas" value="amigdalas"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="dentadura" value="dentadura"><br>


                                                        </td>

                                                        <td class="bg vertical">5. Nariz</td>
                                                        <td class="bg">
                                                        a. Tabique<br>
                                                        b. Cornetes<br>
                                                        c. Mucosas<br>
                                                        d. Senos paranasales
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="tabique" value="tabique"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="cornete" value="cornete"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="mucosas" value="mucosas"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="senos_paranasales" value="senos_paranasales"><br>

                                                        </td>


                                                    </tr>

                                                    <tr>
                                                        <td class="bg vertical">6. Cuello</td>
                                                        <td class="bg">
                                                        a. Tiroides / masas <br>
                                                        b. Movilidad
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="tiroides" value="tiroides"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="movilidad" value="movilidad">

                                                        </td>

                                                        <td class="bg vertical">7. Tórax</td>
                                                        <td class="bg">
                                                        a. Mamas<br>

                                                        </td>
                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="mamas" value="mamas"><br>

                                                        </td>

                                                        <td class="bg vertical">8. Tórax</td>
                                                        <td class="bg">
                                                        a. Pulmones<br>
                                                        b. Corazón<br>
                                                        c. Parrilla costal
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="pulmones" value="pulmones"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="corazon" value="corazon"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="parilla_costal" value="parilla_costal"><br>


                                                        </td>

                                                        <td class="bg vertical">9. Abdomen</td>
                                                        <td class="bg">
                                                        a. Vísceras<br>
                                                        b. Pared abdominal<br>

                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="visceras" value="visceras"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="pared_abdominal" value="pared_abdominal"><br>

                                                        </td>

                                                        <td class="bg vertical">10. Columna</td>
                                                        <td class="bg">
                                                        a. Flexibilidad<br>
                                                        b. Desviación<br>
                                                        c. Dolor<br>

                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="flexibilidad" value="flexibilidad"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="desviacion" value="desviacion"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="dolor" value="dolor"><br>


                                                        </td>


                                                    </tr>

                                                    <tr>
                                                        <td class="bg vertical">11. Pelvis</td>
                                                        <td class="bg">
                                                        a. Pelvis <br>
                                                        b. Genitales
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="pelvis" value="pelvis"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="genitales" value="genitales">

                                                        </td>

                                                        <td class="bg vertical">12. Extremidades</td>
                                                        <td class="bg">
                                                        a. Vascular<br>
                                                        b. Miembros superiores<br>
                                                        c. Miembros inferiores<br>

                                                        </td>
                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="vascular" value="vascular"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="miembros_superiores" value="miembros_superiores"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="miembros_inferiores" value="miembros_inferiores"><br>

                                                        </td>

                                                        <td class="bg vertical">13. Neurológico</td>
                                                        <td class="bg">
                                                        a. Fuerza<br>
                                                        b. Sensibilidad<br>
                                                        c. Marcha<br>   
                                                        d. Reflejos
                                                        </td>

                                                        <td class="">
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="fuerza" value="fuerza"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="sensibilidad" value="sensibilidad"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="marcha" value="marcha"><br>
                                                            <input class="form-check-input" type="checkbox" 
                                                            name="reflejos" value="reflejos"><br>

                                                        </td>

                                                        <td class="bg vertical" colspan="6"></td>

                                                    </tr>
                                                    <tr>
                                                        <td colspan="15">
                                                            SI EXISTE EVIDENCIA DE PATOLOGÍA MARCAR CON "X" Y DESCRIBIR EN LA SIGUIENTE SECCIÓN COLOCANDO EL NUMERAL
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="15">
                                                            <div class="row mb-3 align-items-center">
                                                                <label class="col-md-2 col-form-label " style="text-align: right;">
                                                                    Observación:<span class="text-danger"> *</span>
                                                                </label>
                                                                <div class="col-md-10">
                                                                    <textarea class="form-control" name="motivo_examen" id="motivo_examen"></textarea>
                                                                </div>
                                                            </div>
                                                        </td>
                                                    </tr>

                                                </table>

                                            </div>
                                        </div>

                                        <center>
                                            <button type="submit" class="btn btn-sm btn-outline-success d-flex align-items-center"
                                                style="margin-right:5px">
                                            <i class="fa fa-floppy-o"> </i> &nbsp;&nbsp;Guardar
                                            </button>
                                        </center>

                                    </div>
                                  

                                </form>
                               
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingSix">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseSix" aria-expanded="false" aria-controls="collapseSix">
                                    G. FACTORES DE RIESGO DEL TRABAJO ACTUAL   
                                </button>
                            </h2>
                            <div id="collapseSix" class="accordion-collapse collapse" aria-labelledby="headingSix"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">
                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico" style="margin-bottom: 12px;">

                                                <tr>
                                                    <td colspan="3">PUESTO DE TRABAJO</td>
                                                    <td colspan="6"></td>
                                                </tr>

                                                <tr>
                                                    <td colspan="3"></td>
                                                    <td width="130px">1</td>
                                                    <td width="130px">2</td>
                                                    <td width="130px">3</td>
                                                    <td width="130px">4</td>
                                                    <td width="130px">5</td>
                                                    <td width="130px">6</td>
                                                </tr>

                                                <tr>
                                                    <td colspan="3">ACTIVIDADES IMPORTANTES DENTRO DE LA JORNADA LABORAL</td>
                                                    <td>COLABORACION EN ACTIVIDADES VARIAS </td>
                                                    <td>ATENCION AL PUBLICO</td>
                                                    <td>ARCHIVO DE DOCUMENTOS </td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>

                                                <tr>
                                                    <!-- Columna FÍSICO con rowspan -->
                                                    <td rowspan="10" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                        FÍSICO
                                                    </td>

                                                    <td colspan="2">Temperaturas altas</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="temp_alta[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=1
                                                                data-codigo="tem_alta_1_{{ $i }}"
                                                                id="tem_alta_1_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Temperaturas bajas</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="temp_baja[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=2
                                                                data-codigo="tem_baja_2_{{ $i }}"
                                                                id="tem_baja_2_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Radiación ionizante</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="radiacion_ionizante[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=3
                                                                data-codigo="radiacion_ioni_3_{{ $i }}"
                                                                id="radiacion_ioni_3_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor
                                                </tr>

                                                <tr>
                                                    <td colspan="2">Radiación no ionizante</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="radiacion_no_ionizante[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=4
                                                                data-codigo="radiacion_no_ioni_4_{{ $i }}"
                                                                id="radiacion_no_ioni_4_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Ruido</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="ruido[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=5
                                                                data-codigo="ruido_5_{{ $i }}"
                                                                id="ruido_5_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Vibración</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="vibracion[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=6
                                                                data-codigo="vibracion_6_{{ $i }}"
                                                                id="vibracion_6_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Iluminación</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="iluminacion[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=7
                                                                data-codigo="iluminacion_7_{{ $i }}"
                                                                id="iluminacion_7_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Ventilación</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="ventilacion[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=8
                                                                data-codigo="ventilacion_8_{{ $i }}"
                                                                id="ventilacion_8_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Fluido eléctrico</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="fluido_electrico[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=9
                                                                data-codigo="fluido_electr_9_{{ $i }}"
                                                                id="fluido_electr_9_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Otros</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="otros_fisico[]"
                                                                value="{{ $i }}"
                                                                data-tipo="fisico"
                                                                data-fisico=10
                                                                data-codigo="otros_fisico_10_{{ $i }}"
                                                                id="otros_fisico_10_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>


                                                <tr>
                                                    <!-- Columna FÍSICO con rowspan -->
                                                    <td rowspan="15" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                        DE SEGURIDAD
                                                    </td>
                                                    <td>Locativos</td>
                                                    <td colspan="1">Falta de señalización,aseo, desorden</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="aseo_desorden[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=1
                                                                data-codigo="aseo_desorden_1_{{ $i }}"
                                                                id="aseo_desorden_1_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td rowspan="12"class="bg vertical">Mecánicos</td>
                                                    <td >Atrapamiento entre Máquinas y o superficies</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="atrapamiento_maquinas[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=2
                                                                data-codigo="atrapamiento_maquinas_2_{{ $i }}"
                                                                id="atrapamiento_maquinas_2_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>



                                                <tr>
                                                    <td >Atrapamiento entre objetos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="atrapamineto_obj_seg[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=3
                                                                data-codigo="atrapamineto_obj_seg_3_{{ $i }}"
                                                                id="atrapamineto_obj_seg_3_{{ $i }}"
                                                            >

                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td >Caída de objetos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="caida_objetos_seg[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=4
                                                                data-codigo="caida_objetos_seg_4_{{ $i }}"
                                                                id="caida_objetos_seg_4_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td >Caídas al mismo nivel</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="caidas_mismo_nivel[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=5
                                                                data-codigo="caidas_mismo_nivel_5_{{ $i }}"
                                                                id="caidas_mismo_nivel_5_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td >Caídas a diferente nivel</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="caidas_diferente_nivel[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=6
                                                                data-codigo="caidas_diferente_nivel_6_{{ $i }}"
                                                                id="caidas_diferente_nivel_6_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td >Pinchazos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="pinchazos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=7
                                                                data-codigo="pinchazos_7_{{ $i }}"
                                                                id="pinchazos_7_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td >Cortes</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="cortes[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=8
                                                                data-codigo="cortes_8_{{ $i }}"
                                                                id="cortes_8_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td>Choques /colisión vehicular</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="choques_colisión_vehicular[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=9
                                                                data-codigo="choques_colisión_vehicular_9_{{ $i }}"
                                                                id="choques_colisión_vehicular_9_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td>Atropellamientos por vehículos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="atropellamientos_por_vehículos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=10
                                                                data-codigo="atropellamientos_por_vehículos_10_{{ $i }}"
                                                                id="atropellamientos_por_vehículos_10_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                 <tr>
                                                    <td>Proyección de fluidos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="proyección_fluidos[{{ $i }}]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=11
                                                                data-codigo="proyección_fluidos_11_{{ $i }}"
                                                                id="proyección_fluidos_11_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td>Proyección de partículas - fragmentos</td>
                                                     @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="proyección_partículas_fragmentos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=12
                                                                data-codigo="proyección_partículas_fragmentos_12_{{ $i }}"
                                                                id="proyección_partículas_fragmentos_12_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td>Contacto con superficies de trabajos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="contacto_con_superficies_trabajos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=13
                                                                data-codigo="contacto_con_superficies_trabajos_13_{{ $i }}"
                                                                id="contacto_con_superficies_trabajos_13_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td>Eléctrico</td>
                                                    <td >Contacto eléctrico</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="contacto_elec_seg[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=14
                                                                data-codigo="contacto_elec_seg_14_{{ $i }}"
                                                                id="contacto_elec_seg_14_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td >Otros</td>
                                                    <td></td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="otros_otros_seg[]"
                                                                value="{{ $i }}"
                                                                data-tipo="seguridad"
                                                                data-fisico=15
                                                                data-codigo="otros_otros_seg_15_{{ $i }}"
                                                                id="otros_otros_seg_15_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>



                                                <tr>
                                                    <!-- Columna FÍSICO con rowspan -->
                                                    <td rowspan="9" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                        QUÍMICO
                                                    </td>

                                                    <td colspan="2">Polvos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_polvo[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=1
                                                                data-codigo="quimico_polvo_1_{{ $i }}"
                                                                id="quimico_polvo_1_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>


                                                <tr>
                                                    <td colspan="2">Sólidos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_solidos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=2
                                                                data-codigo="quimico_polvo_2_{{ $i }}"
                                                                id="quimico_polvo_2_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Humos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_humos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=3
                                                                data-codigo="quimico_humos_3_{{ $i }}"
                                                                id="quimico_humos_3_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Líquidos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_liquido[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=4
                                                                data-codigo="quimico_liquido_4_{{ $i }}"
                                                                id="quimico_liquido_4_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Vapores</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_vapores[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=5
                                                                data-codigo="quimico_vapores_5_{{ $i }}"
                                                                id="quimico_vapores_5_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Aerosoles</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_aerosoles[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=6
                                                                data-codigo="quimico_aerosoles_6_{{ $i }}"
                                                                id="quimico_aerosoles_6_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Neblinas</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_neblinas[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=7
                                                                data-codigo="quimico_neblinas_7_{{ $i }}"
                                                                id="quimico_neblinas_7_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Gaseosos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_gaseosos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=8
                                                                data-codigo="quimico_gaseosos_8_{{ $i }}"
                                                                id="quimico_gaseosos_8_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Otros</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="quimico_otros[{{ $i }}]"
                                                                value="{{ $i }}"
                                                                data-tipo="quimico"
                                                                data-fisico=9
                                                                data-codigo="quimico_otros_9_{{ $i }}"
                                                                id="quimico_otros_9_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>


                                                <tr>
                                                    <!-- Columna FÍSICO con rowspan -->
                                                    <td rowspan="7" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                        BIOLÓGICO
                                                    </td>

                                                    <td colspan="2">Virus</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologicos_virus[]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=1
                                                                data-codigo="biologicos_virus_1_{{ $i }}"
                                                                id="biologicos_virus_1_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Hongos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologicos_hongos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=2
                                                                data-codigo="biologicos_hongos_2_{{ $i }}"
                                                                id="biologicos_hongos_2_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Bacterias</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologicos_bacterias[]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=3
                                                                data-codigo="biologicos_bacterias_3_{{ $i }}"
                                                                id="biologicos_bacterias_3_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Parásitos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologicos_parasistos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=4
                                                                data-codigo="biologicos_parasistos_4_{{ $i }}"
                                                                id="biologicos_parasistos_4_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Exposición a vectores</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologico_exposicion_vect[]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=5
                                                                data-codigo="biologico_exposicion_vect_5_{{ $i }}"
                                                                id="biologico_exposicion_vect_5_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Exposición a animales selváticos </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologicos_exposicion_anim_selvaticos[{{ $i }}]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=6
                                                                data-codigo="biologicos_exposicion_anim_selvaticos_6_{{ $i }}"
                                                                id="biologicos_exposicion_anim_selvaticos_6_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Otros</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="biologicos_otros[]"
                                                                value="{{ $i }}"
                                                                data-tipo="biologico"
                                                                data-fisico=7
                                                                data-codigo="biologicos_otros_7_{{ $i }}"
                                                                id="biologicos_otros_7_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor
                                                </tr>



                                                <tr>
                                                    <!-- Columna FÍSICO con rowspan -->
                                                    <td rowspan="6" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                        ERGONÓMICO
                                                    </td>

                                                    <td colspan="2">Manejo manual de cargas</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="engornomico_carga[]"
                                                                value="{{ $i }}"
                                                                data-tipo="ergonomico"
                                                                data-fisico=1
                                                                data-codigo="engornomico_carga_1_{{ $i }}"
                                                                id="engornomico_carga_1_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Movimiento repetitivos</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="engornomico_mov_repet[]"
                                                                value="{{ $i }}"
                                                                data-tipo="ergonomico"
                                                                data-fisico=2
                                                                data-codigo="engornomico_mov_repet_2_{{ $i }}"
                                                                id="engornomico_mov_repet_2_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Posturas forzadas</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="engornomico_postura_forzadas[]"
                                                                value="{{ $i }}"
                                                                data-tipo="ergonomico"
                                                                data-fisico=3
                                                                data-codigo="engornomico_postura_forzadas_3_{{ $i }}"
                                                                id="engornomico_postura_forzadas_3_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Trabajos con PVD</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="engornomico_trab_pvd[]"
                                                                value="{{ $i }}"
                                                                data-tipo="ergonomico"
                                                                data-fisico=4
                                                                data-codigo="engornomico_trab_pvd_4_{{ $i }}"
                                                                id="engornomico_trab_pvd_4_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Diseño Inadecuado del puesto</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="engornomico_disenio_inadecaudo_puesto[]"
                                                                value="{{ $i }}"
                                                                data-tipo="ergonomico"
                                                                data-fisico=5
                                                                data-codigo="engornomico_disenio_inadecaudo_puesto_5_{{ $i }}"
                                                                id="engornomico_disenio_inadecaudo_puesto_5_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Otros</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="engornomico_otros[]"
                                                                value="{{ $i }}"
                                                                data-tipo="ergonomico"
                                                                data-fisico=6
                                                                data-codigo="engornomico_otros_6_{{ $i }}"
                                                                id="engornomico_otros_6_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>



                                                <tr>
                                                    <!-- Columna FÍSICO con rowspan -->
                                                    <td rowspan="14" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                        PSICOSOCIAL
                                                    </td>

                                                    <td colspan="2">Monotonía del trabajo</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_monotomia_trabajo[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=1
                                                                data-codigo="psicosocial_monotomia_trabajo_1_{{ $i }}"
                                                                id="psicosocial_monotomia_trabajo_1_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Sobrecarga laboral</td>
                                                   @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_sobrecarga_laboral[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=2
                                                                data-codigo="psicosocial_sobrecarga_laboral_2_{{ $i }}"
                                                                id="psicosocial_sobrecarga_laboral_2_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Minuciosidad de la tarea </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_minuciosidad[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=3
                                                                data-codigo="psicosocial_minuciosidad_3_{{ $i }}"
                                                                id="psicosocial_minuciosidad_3_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Alta responsabilidad</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_alta_responsabilidad[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=4
                                                                data-codigo="psicosocial_alta_responsabilidad_4_{{ $i }}"
                                                                id="psicosocial_alta_responsabilidad_4_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Autonomía  en la toma de decisiones</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_autonomia_decisiones[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=5
                                                                data-codigo="psicosocial_autonomia_decisiones_5_{{ $i }}"
                                                                id="psicosocial_autonomia_decisiones_5_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Supervisión y estilos de dirección deficiente</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_supervision[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=6
                                                                data-codigo="psicosocial_supervision_6_{{ $i }}"
                                                                id="psicosocial_supervision_6_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Conflicto de rol</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_conflicto_rol[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=7
                                                                data-codigo="psicosocial_conflicto_rol_7_{{ $i }}"
                                                                id="psicosocial_conflicto_rol_7_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Falta de Claridad en las funciones</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_falta_claridad[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=8
                                                                data-codigo="psicosocial_falta_claridad_8_{{ $i }}"
                                                                id="psicosocial_falta_claridad_8_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Incorrecta distribución del trabajo </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_incorrecta_distrib_trab[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=9
                                                                data-codigo="psicosocial_incorrecta_distrib_trab_9_{{ $i }}"
                                                                id="psicosocial_incorrecta_distrib_trab_9_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Turnos rotativos </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_turnos_rotativos[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=10
                                                                data-codigo="psicosocial_turnos_rotativos_10_{{ $i }}"
                                                                id="psicosocial_turnos_rotativos_10_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Relaciónes interpersonales </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_relaciones_interpersonales[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=11
                                                                data-codigo="psicosocial_relaciones_interpersonales_11_{{ $i }}"
                                                                id="psicosocial_relaciones_interpersonales_11_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>


                                                <tr>
                                                    <td colspan="2">Inestabilidad laboral </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_inestabilidad_laboral[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=12
                                                                data-codigo="psicosocial_inestabilidad_laboral_12_{{ $i }}"
                                                                id="psicosocial_inestabilidad_laboral_12_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                                <tr>
                                                    <td colspan="2">Amenaza Delincuencial </td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_amenaza_delicuencial[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=13
                                                                data-codigo="psicosocial_amenaza_delicuencial_13_{{ $i }}"
                                                                id="psicosocial_amenaza_delicuencial_13_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>


                                                <tr>
                                                    <td colspan="2">Otros</td>
                                                    @for($i = 1; $i <= 6; $i++)
                                                        <td>
                                                            <input
                                                                class="form-check-input check_riesgo"
                                                                type="checkbox"
                                                                name="psicosocial_otros[]"
                                                                value="{{ $i }}"
                                                                data-tipo="psicosocial"
                                                                data-fisico=14
                                                                data-codigo="psicosocial_otros_14_{{ $i }}"
                                                                id="psicosocial_otros_14_{{ $i }}"
                                                            >
                                                        </td>
                                                    @endfor

                                                </tr>

                                               <!-- <tr>
                                                    <td rowspan="5" colspan="3" style="text-align:center; font-weight:bold; vertical-align:middle;">
                                                       MEDIDAS PREVENTIVAS 
                                                       <button type="button" class="btn btn-sm btn-success  align-items-center" onclick="abrirModalMedidasPreventivas()">
                                                            <i class="fa fa-plus" aria-hidden="true"></i>
                                                        </button>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td style="line-height:12px;">Charlas de salud</td>
                                                    <td style="line-height:12px;">Controles medicos rutinarios </td>
                                                    <td style="line-height:12px;">Uso adecuado de prenda de proteccion persona</td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                                <tr>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                                <tr>
                                                    <td ></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                                <tr>
                                                    <td ></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr> -->
                                            </table>
                                            
                                            <center><b>MEDIDAS PREVENTIVAS</b>
                                                <button type="button" class="btn btn-sm btn-success  align-items-center" onclick="abrirModalMedidasPreventivas()">
                                                    <i class="fa fa-plus" aria-hidden="true"></i>
                                                </button>
                                            </center>
                                            <table class="tabla-examen-fisico" style="margin-top: 12px;">
                                                <tr>
                                                    <td>CHARLAS DE SALUD </td>
                                                    <td>CONTROLES MEDICOS RUTINARIOS </td>
                                                    <td>USO ADECUADO DE PRENDA DE PROTECCION PERSONAL </td>
                                                </tr>
                                                <tbody id="tbodyMedida">
                                                    <tr>
                                                        <td colspan="3" style="text-align:center">No hay datos disponibles</td>
                                                    </tr>

                                                </tbody>
                                            </table>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingSeven">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseSeven" aria-expanded="false" aria-controls="collapseSeven">
                                    H. ACTIVIDAD LABORAL/ INCIDENTES/ACCIDENTES / ENFERMEDADES OCUPACIONALES  
                                </button>
                            </h2>
                            <div id="collapseSeven" class="accordion-collapse collapse" aria-labelledby="headingSeven"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td colspan="13">
                                                        ANTECEDENTES DE EMPLEOS ANTERIORES Y/O TRABAJO ACTUAL
                                                        <button type="button" class="btn btn-sm btn-success  align-items-center" onclick="abrirModalActividades()">
                                                            <i class="fa fa-plus" aria-hidden="true"></i>
                                                        </button>
                                                    </td>

                                                </tr>
                                                <tr>
                                                    <td rowspan="2" class="wide" width="10%">CENTRO DE TRABAJO</td>
                                                    <td rowspan="2" class="wide">ACTIVIDADES QUE DESEMPEÑABA</td>

                                                    <td colspan="3" width="10%">TRABAJO</td>

                                                    <td colspan="3">De los Accidentes de Trabajo y las Enfermedades</td>

                                                    <td colspan="10">CALIFICADO POR INSTITUTO ECUATORIANO DE SEGURIDAD SOCIAL</td>
                                                </tr>

                                                <tr>
                                                    <!-- Trabajo -->
                                                    <td class="vertical small">ANTERIOR</td>
                                                    <td class="vertical small">ACTUAL</td>
                                                    <td class="vertical small">TIEMPO DE TRABAJO</td>
                                                    <td class="vertical small">INCIDENTE</td>
                                                    <td class="vertical small">ACCIDENTE</td>
                                                    <td class="vertical small">ENFERMEDAD PROFESIONAL</td>

                                                    <!-- Accidentes y enfermedades -->
                                                    <td class="small">SI</td>
                                                    <td class="small">NO</td>
                                                    <td class="small">FECHA<br>aaaa/mm/dd</td>
                                                    <td class="wide">ESPECIFICAR</td>

                                                    <!-- IESS -->
                                                    <td class="wide">Observaciones</td>
                                                </tr>
                                                <tbody id="tbodyActividadLaboral">
                                                    <tr>
                                                        <td colspan="13"><center>No hay Datos Disponibles</center></td>
                                                        
                                                    </tr>

                                                </tbody>

                                            </table>
                                        </div>


                                    </div>
                                </div>
                            </div>

                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingEigth">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseEigth" aria-expanded="false" aria-controls="collapseEigth">
                                    I. ACTIVIDADES EXTRA LABORALES 
                                </button>
                            </h2>
                            <div id="collapseEigth" class="accordion-collapse collapse" aria-labelledby="headingEigth"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td>TIPO DE ACTIVIDAD</td>
                                                    <td>( FECHA)    aaaa/mm/dd</td>

                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingNine">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseNine" aria-expanded="false" aria-controls="collapseNine">
                                    J. RESULTADOS DE EXÁMENES GENERALES Y ESPECÍFICOS DE ACUERDO AL RIESGO Y PUESTO DE TRABAJO (IMAGEN, LABORATORIO Y OTROS)
                                </button>
                            </h2>
                            <div id="collapseNine" class="accordion-collapse collapse" aria-labelledby="headingNine"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td>NOMBRE DEL EXAMEN </td>
                                                    <td>FECHA</td>
                                                    <td>RESULTADOS</td>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingTen">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseTen" aria-expanded="false" aria-controls="collapseTen">
                                    K. DIAGNÓSTICO &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  PRE:PRESUNTIVO DEF: DEFINITIVO
                                </button>
                            </h2>
                            <div id="collapseTen" class="accordion-collapse collapse" aria-labelledby="headingTen"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td>CIE-10 </td>
                                                    <td>Descripción</td>
                                                    <td>PRE</td>
                                                    <td>DEF</td>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingEleven">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseEleven" aria-expanded="false" aria-controls="collapseEleven">
                                    L. APTITUD MÉDICA PARA EL TRABAJO
                                </button>
                            </h2>
                            <div id="collapseEleven" class="accordion-collapse collapse" aria-labelledby="headingEleven"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td>APTO </td>
                                                    <td></td>
                                                    <td>APTO EN OBSERVACIÓN</td>
                                                    <td></td>
                                                    <td>APTO CON LIMITACIONES </td>
                                                    <td></td>
                                                    <td>NO APTO</td>
                                                    <td></td>
                                                </tr>
                                                <tr>
                                                    <td colspan="8">
                                                        Observaciones<input type="text" name="observ-apt-med" class="form-control">
                                                    </td>

                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingTwelve">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseTwelve" aria-expanded="false" aria-controls="collapseTwelve">
                                    M. RECOMENDACIONES Y/O TRATAMIENTO
                                </button>
                            </h2>
                            <div id="collapseTwelve" class="accordion-collapse collapse" aria-labelledby="headingTwelve"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td colspan="8">
                                                        Descripción<textarea name="observ-apt-med" class="form-control" rows="5"></textarea>
                                                    </td>

                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item">
                            <h2 class="accordion-header" id="headingThirteen">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#collapseThirteen" aria-expanded="false" aria-controls="collapseThirteen">
                                    N.RETIRO (evaluación) 
                                </button>
                            </h2>
                            <div id="collapseThirteen" class="accordion-collapse collapse" aria-labelledby="headingThirteen"
                                data-bs-parent="#accordionExample">
                                <div class="accordion-body">
                                    <div class="row mb-3 align-items-center">

                                        <div class="col-md-12">

                                            <table class="tabla-examen-fisico">

                                                <tr>
                                                    <td>
                                                       SE REALIZA LA EVALUACIÓN
                                                    </td>
                                                    <td>
                                                       SI
                                                    </td>
                                                    <td>

                                                    </td>
                                                     <td>
                                                       NO
                                                    </td>
                                                    <td>

                                                    </td>


                                                </tr>

                                                <tr>
                                                    <td>
                                                       LA CONDICIÓN DE SALUD ESTA RELACIONADA CON EL TRABAJO
                                                    </td>
                                                    <td>
                                                       SI
                                                    </td>
                                                    <td>

                                                    </td>
                                                     <td>
                                                       NO
                                                    </td>
                                                    <td>

                                                    </td>


                                                </tr>
                                                <tr>
                                                    <td colspan="5">
                                                        Observación<textarea name="observ-apt-med" class="form-control" rows="5"></textarea>
                                                    </td>

                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

            </div>
            <hr>
        </div>
    </div>
    @include('consultorio.modal_puesto_trabajo')
    @include('consultorio.modal_medida_preventiva')
    @include('consultorio.modal_actividad')
@endsection
    @push('scripts')
        <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>
        <script>

            const isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
            if (isDarkMode) {
                applyDarkModeStyles('D')
            } else {
                applyDarkModeStyles('L')
            }


            function applyDarkModeStyles(enable) {
                let styleTag = document.getElementById('dark-mode-styles');

                if (enable == 'D') {
                    // Si el estilo ya existe, no lo agregamos de nuevo
                    if (!styleTag) {
                        styleTag = document.createElement('style');
                        styleTag.id = 'dark-mode-styles';
                        styleTag.innerHTML = `
                            .select2-container--default .select2-results__option {
                                background-color: #212529 !important;
                                color: #fff !important;
                            }
                            .select2-container--default .select2-results__option--highlighted {
                                background-color: #343a40 !important;
                                color: #fff !important;
                            }
                            .select2-container--default .select2-results__option[aria-selected="true"] {
                                background-color: #555 !important;
                                color: #fff !important;
                            }
                            .select2-container--default .select2-selection--single {
                                background-color: #212529 !important;
                                color: #fff !important;
                                border: 1px solid #555 !important;
                            }
                            .select2-container--default .select2-selection__arrow b {
                                border-color: #fff transparent transparent transparent !important;
                            }
                            .select2-container--default .select2-search--dropdown .select2-search__field {
                                background-color: #333 !important;
                                color: #fff !important;
                                border: 1px solid #555 !important;
                            }
                            .select2-container--default .select2-results__message {
                                background-color: #212529 !important;
                                color: #bbb !important;
                            }
                            .select2-container--default .select2-selection__rendered {
                                color: white !important;
                            }

                            .sweet-alert {
                                background-color: #18191a;
                            }
                        `;
                        document.head.appendChild(styleTag);
                    }
                } else {
                    // Si el usuario cambia a modo claro, eliminamos la etiqueta de estilos oscuros
                    if (styleTag) {
                        styleTag.remove();
                    }
                }
            }
            // Llamar a la función cuando cambie el tema
            function cambiaTema(isDark) {
                applyDarkModeStyles(isDark);
            }
            function setTheme(theme) {
                localStorage.setItem('theme', theme);  // Guardar preferencia en localStorage
                applyTheme(theme);
            }

            // Cuando la página carga, recuperamos la preferencia
            window.addEventListener('DOMContentLoaded', (event) => {
                const savedTheme = localStorage.getItem('theme');
                if (savedTheme) {
                    applyTheme(savedTheme);  // Aplica el tema guardado
                }
            });

            // Función para aplicar el tema (claro u oscuro)
            function applyTheme(theme) {
                // alert(theme)
                if (theme == 'dark') {
                    applyDarkModeStyles('D')
                } else if (theme == 'light') {
                    applyDarkModeStyles('L')
                }
            }
            function getSystemTheme() {
                return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
            }
            var tipo = $('#tipo').val()

            // if(tipo=="1"){
            //     $('#div_cedula').show()
            //     $('#div_clave').hide()
            //     $('#div_nombres').hide()
            // }else if(tipo=="2"){
            //     $('#div_cedula').hide()
            //     $('#div_nombres').hide()
            //     $('#div_clave').show()
            // }else{
            //     $('#div_cedula').hide()
            //     $('#div_nombres').show()
            //     $('#div_clave').hide()
            // }
            var buttonBuscar = document.getElementById('buttonBuscar');
            let token = "{{csrf_token()}}";

            var formExonerar = document.getElementById('formExonerar');
            function verificarSeleccionCasillas() {
                let formData2 = new FormData(formExonerar);
                if (formData2.getAll("checkLiquidacion[]").length > 0) {
                    return true;
                }
                else {
                    return false;
                }
            }

            function printIframe() {
                const iframe = document.getElementById('iframePdf');
                iframe.contentWindow.focus();
                iframe.contentWindow.print();
            }


        </script>
        <script src="{{asset('bower_components/sweetalert/sweetalert.js')}}"></script>

        <script src="{{ asset('js/consultorio/evaluacion.js?v=' . rand())}}"></script>

    @endpush