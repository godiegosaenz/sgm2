<div class="modal fade" id="modalMedida" tabindex="-1" aria-labelledby="modalCrearEnteLabel"
    aria-hidden="true">
    <div class="modal-dialog modal-xl"> <!-- ancho grande -->
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalPersona2Label">Formulario Medidas Preventivas</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                <form id="formMedidas" autocomplete="off">
                    <div class="row g-3">

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Charlas de salud <span
                                            class="text-danger">*</span></label>
                                </div>
                                <div class="col-md-7">
                                    <textarea  autocomplete="of" class="form-control"
                                        id="charla_salud_empl" name="charla_salud_empl" rows="3"></textarea>
                                </div>
                            </div>
                        </div>

                     
                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Controles medicos rutinarios <span
                                            class="text-danger">*</span></label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control"
                                        id="controles_med_empl" name="controles_med_empl" rows="3"></textarea>
                                </div>
                            </div>
                        </div>
                        

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Uso adecuado de prenda de proteccion persona <span
                                            class="text-danger">*</span></label>
                                </div>
                                <div class="col-md-7">
                                    <textarea autocomplete="of" class="form-control"
                                        id="prenda_proteccion_empl" name="prenda_proteccion_empl" rows="3"></textarea>
                                </div>
                            </div>
                        </div>

                        

                    </div>
                </form>
            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-success " id="btn_guarda_medida" onclick="guardarMedidaPreventiva()" >
                    Guardar
                </button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>