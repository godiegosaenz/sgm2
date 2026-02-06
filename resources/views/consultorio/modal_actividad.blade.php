<div class="modal fade" id="modalActividad" tabindex="-1" aria-labelledby="modalCrearEnteLabel"
    aria-hidden="true">
    <div class="modal-dialog modal-xl"> <!-- ancho grande -->
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalPersona2Label">FORMULARIO ANTECEDENTES DE EMPLEOS ANTERIORES Y/O TRABAJO ACTUAL</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                <form id="formActividad" autocomplete="off">
                    <div class="row g-3">

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Centro de Trabajo</label>
                                </div>
                                <div class="col-md-7">
                                    <input type="text"  autocomplete="of" class="form-control modal_act"
                                        id="centro_trab_empl" name="centro_trab_empl">
                            </div>
                        </div>

                     
                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Actividades que desempeñaba </label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control modal_act"
                                        id="actividades_desempeniaba" name="actividades_desempeniaba" rows="2"></textarea>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Trabajo</label>
                                </div>
                                <div class="col-md-7">
                                    <select class="form-select modal_act" aria-label="Default select example" id="trabajo" name="trabajo">
                                        <option value="Anterior" selected>Anterior</option>
                                        <option value="Actual">Actual</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Tiempo de Trabajo </label>
                                </div>
                                <div class="col-md-7">
                                    <input type="text"  autocomplete="of" class="form-control modal_act"
                                        id="tiempo_trab_empl" name="tiempo_trab_empl">
                            </div>
                        </div>
                        

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Incidente</label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control modal_act"
                                        id="incidente_empl" name="incidente_empl" rows="2"></textarea>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Accidente</label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control modal_act"
                                        id="accidente_empl" name="accidente_empl" rows="2"></textarea>
                                </div>
                            </div>
                        </div>

                         <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Enfermedad Profesional </label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control modal_act"
                                        id="enfermedad_prof_empl" name="enfermedad_prof_empl" rows="2"></textarea>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Calificado por IESS</label>
                                </div>
                                <div class="col-md-7">
                                    <select class="form-select modal_act" aria-label="Default select example" id="califica_iess" name="califica_iess">
                                        <option value="Si" selected>Si</option>
                                        <option value="No">No</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Fecha </label>
                                </div>
                                <div class="col-md-7">
                                    <input type="date"  autocomplete="of" class="form-control modal_act"
                                        id="fecha_califica" name="fecha_califica">
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Especificar</label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control modal_act"
                                        id="especificar_calif_empl" name="especificar_calif_empl" rows="2"></textarea>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Observaciones</label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control modal_act"
                                        id="observaciones_calif_empl" name="observaciones_calif_empl" rows="2"></textarea>
                                </div>
                            </div>
                        </div>


                        

                    </div>
                </form>
            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-success " id="btn_guarda_medida" onclick="guardarActividad()" >
                    Guardar
                </button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>