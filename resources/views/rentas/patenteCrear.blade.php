@extends('layouts.appv2')
@section('title', 'Catastro contribuyente')
@push('styles')
<style>


</style>

@endpush
@section('content')
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="col-md-12">
                    <h2 class="text-center">Registrar patente</h2>
                </div>
            </div>
        </div>
        @if(@session('error'))
                <div class="alert alert-danger">
                    {{session('error')}}
                </div>
        @endif
        @if(@session('success'))
                <div class="alert alert-success">
                    {{session('success')}}
                </div>
        @endif

        <form>
                <!-- Información General -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Información contribuyente</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="buscar_contribuyente" class="form-label">Seleccionar Contribuyente</label>
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" placeholder="Buscar Contribuyente" id="buscar_contribuyente">
                            <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#modalContribuyente">Buscar</button>
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <div class="col-md-12 mb-3">
                            <label for="direccion_actividad" class="form-label">Actividad comercial</label>
                            <textarea class="form-control" id="direccion_actividad" rows="3"></textarea>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <!-- Columna izquierda -->
                    <div class="col-md-6 mb-3">

                        <div class="mb-3">
                            <label for="ruc_cedula" class="form-label">RUC o Cédula</label>
                            <input type="text" class="form-control" id="ruc_cedula" maxlength="13" disabled required>
                        </div>
                        <div class="mb-3">
                            <label for="nombre_contribuyente" class="form-label">Contribuyente</label>
                            <input type="text" class="form-control" id="nombre_contribuyente" maxlength="255" required>
                        </div>
                        <div class="mb-3">
                            <label for="domicilio" class="form-label">Razon Social</label>
                            <input type="text" class="form-control" id="domicilio" maxlength="255">
                        </div>
                        <div class="mb-3">
                            <label for="domicilio" class="form-label">Domicilio</label>
                            <input type="text" class="form-control" id="domicilio" maxlength="255">
                        </div>
                        <div class="mb-3">
                            <label for="Telefono_domicilio" class="form-label">Teléfono </label>
                            <input type="text" class="form-control" id="Telefono_domicilio" maxlength="15">
                        </div>
                        <div class="mb-3">
                            <label for="Telefono_domicilio" class="form-label">Correo electronico</label>
                            <input type="text" class="form-control" id="Telefono_domicilio" maxlength="15">
                        </div>
                        <div class="mb-3">
                            <label for="representante_legal" class="form-label">Rep. legal (sociedades)</label>
                            <input type="text" class="form-control" id="representante_legal" maxlength="255">
                        </div>
                        <div class="mb-3">
                            <label for="cedula_representante" class="form-label">Cédula del Rep. Legal</label>
                            <input type="text" class="form-control" id="cedula_representante" maxlength="13">
                        </div>
                    </div>
                    <!-- Columna derecha -->
                    <div class="col-md-6 mb-3">

                        <div class="mb-3">
                            <label for="inicio_actividades" class="form-label">Inicio de Actividades</label>
                            <input type="text" class="form-control" id="inicio_actividades" maxlength="255">
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Fantasia comercial</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Estado de contribuyente</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Clase de contribuyente</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Tipo de contribuyente</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Numero de establecimientos</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Estado establecimiento</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                        <div class="mb-3">
                            <label for="year_declaracion" class="form-label">Establecimiento Propio/Arrendado</label>
                            <input type="number" class="form-control" id="year_declaracion" required>
                        </div>
                    </div>
                </div>
            </fieldset>


            <!-- Exoneraciones -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Exoneraciones y deducciones</legend>
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Calificacion artesanal</label>
                        </div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Tercera edad</label>
                        </div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Discapacidad</label>
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Datos de declaracion</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="estado_contribuyente_id" class="form-label">Año de declaracion</label>
                        <select class="form-select" id="estado_contribuyente_id" required>
                            <option value="">Seleccione</option>
                            <option value="1">2024 >> Periodo Fiscal 2023</option>
                            <option value="2">2023 >> Periodo Fiscal 2022</option>
                            <option value="2">2022 >> Periodo Fiscal 2021</option>
                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="fecha_declaracion" class="form-label">Fecha de Declaración</label>
                        <input type="date" class="form-control" id="fecha_declaracion" required>
                    </div>
                    <div class="mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="lleva_contabilidad">
                            <label class="form-check-label" for="lleva_contabilidad">Lleva Contabilidad</label>
                        </div>
                    </div>

                </div>
            </fieldset>

            <!-- Declaracion tributaria -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Declaracion tibutaria (No obligados a llevar contabilidad)</legend>

                <div class="row">
                    <!-- Activos -->
                    <div class="col-md-6 mb-3">
                        <fieldset class="border p-3 bg-custom">
                            <legend class="w-auto"><h5>Activos</h5></legend>
                            <div class="mb-3 row">
                                <label for="act_caja_banco" class="col-sm-8 col-form-label">Caja y Bancos</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_caja_banco">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_ctas_cobrar" class="col-sm-8 col-form-label">Cuentas por Cobrar</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_ctas_cobrar">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_inv_mercaderia" class="col-sm-8 col-form-label">Inventario de Mercadería</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_inv_mercaderia">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_vehiculo_maquinaria" class="col-sm-8 col-form-label">Vehículos y Maquinaria</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_vehiculo_maquinaria">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_equipos_oficinas" class="col-sm-8 col-form-label">Equipos de Oficina</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_equipos_oficinas">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_edificios_locales" class="col-sm-8 col-form-label">Edificios y Locales</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_edificios_locales">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_terrenos" class="col-sm-8 col-form-label">Terrenos</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_terrenos">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="act_total_activos" class="col-sm-8 col-form-label">Total Activos (A)</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="act_total_activos" required>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                    <!-- Pasivos -->
                    <div class="col-md-6 mb-3">
                        <fieldset class="border p-3 bg-custom">
                            <legend class="w-auto"><h5>Pasivos</h5></legend>
                            <div class="mb-3 row">
                                <label for="pas_ctas_dctos_pagar" class="col-sm-8 col-form-label">Cuentas y Documentos por Pagar</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_ctas_dctos_pagar">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_obligaciones_financieras" class="col-sm-8 col-form-label">Obligaciones Financieras</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_obligaciones_financieras">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_otras_ctas_pagar" class="col-sm-8 col-form-label">Otras Cuentas por Pagar</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_otras_ctas_pagar">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_otros_pasivos" class="col-sm-8 col-form-label">Otros Pasivos</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_otros_pasivos">
                                </div>
                            </div>
                            <div class="mb-3 row">
                                <label for="pas_total_pasivos" class="col-sm-8 col-form-label">Total Pasivos (B)</label>
                                <div class="col-sm-4">
                                    <input type="number" class="form-control" id="pas_total_pasivos" required>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
                <div class="row">
                    <!-- Patrimonio -->
                    <div class="col-md-6 mb-3">
                        <fieldset class="border p-3 mb-4">
                            <legend class="w-auto"><h5>Patrimonio</h5></legend>
                            <div class="row">
                                <div class="mb-3 row">
                                    <label for="pas_ctas_dctos_pagar" class="col-sm-6 col-form-label">Patrimonio total (A-B)</label>
                                    <div class="col-sm-6">
                                        <input type="number" class="form-control" id="pas_ctas_dctos_pagar">
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </div>
                </div>
            </fieldset>

            <!-- Patrimonio -->
            <fieldset class="border p-3 mb-4">
                <legend class="w-auto">Declaracion tributaria obligados a llevar contabilidad</legend>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <div class="mb-3 row">
                            <label for="act_caja_banco" class="col-sm-6 col-form-label">Total activos</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_caja_banco">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_ctas_cobrar" class="col-sm-6 col-form-label">(-) Total Pasivos</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_ctas_cobrar">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Patrimonio</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_inv_mercaderia">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Total de ingresos percibidos en el Canton San Vicente</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_inv_mercaderia">
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <div class="mb-3 row">
                            <label for="act_caja_banco" class="col-sm-6 col-form-label">Formulario SRI (No.)</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_caja_banco">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_ctas_cobrar" class="col-sm-6 col-form-label">Fecha de declaracion</label>
                            <div class="col-sm-6">
                                <input type="number" class="form-control" id="act_ctas_cobrar">
                            </div>
                        </div>
                        <div class="mb-3 row">
                            <label for="act_inv_mercaderia" class="col-sm-6 col-form-label">Original/sustitutiva</label>
                            <div class="col-sm-6">
                                <select class="form-select" id="estado_contribuyente_id" required>
                                    <option value="">Seleccione</option>
                                    <option value="1">Original</option>
                                    <option value="2">Sustitutiva</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

            </fieldset>


            <!-- Botón de envío -->
            <button type="submit" class="btn btn-primary">Generar Patente</button>
        </form>
    </div>
    <!-- Modal de Búsqueda de Contribuyente -->
    <div class="modal fade" id="modalContribuyente" tabindex="-1" aria-labelledby="modalContribuyenteLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalContribuyenteLabel">Buscar Contribuyente</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <input type="text" class="form-control" id="buscar_input" placeholder="Ingrese RUC o Nombre para buscar">
                    </div>
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>RUC</th>
                                <th>Nombre</th>
                                <th>Domicilio</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Aquí irían los datos de búsqueda del contribuyente -->
                            <tr>
                                <td>1</td>
                                <td>0123456789</td>
                                <td>Juan Pérez</td>
                                <td>Av. Siempre Viva 123</td>
                                <td><button class="btn btn-sm btn-primary">Seleccionar</button></td>
                            </tr>
                            <!-- Fin de datos de búsqueda -->
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
@endsection
@push('scripts')

@endpush
