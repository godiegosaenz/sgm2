<div class="modal fade" id="modalCrearPuesto" tabindex="-1" aria-labelledby="modalCrearEnteLabel"
    aria-hidden="true">
    <div class="modal-dialog modal-lg"> <!-- ancho grande -->
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalPersonaLabel">Formulario Puesto Empleo</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                <form id="formPuesto" autocomplete="off">
                    <div class="row g-3">

                        <div class="col-md-12" style="margin-top: 10px;">
                            <div class="row align-items-center">
                                <div class="col-md-3 text-end">
                                    <label for="marca_v" class="form-label mb-0">Puesto <span
                                            class="text-danger">*</span></label>
                                </div>
                                <div class="col-md-7">
                                    <input type="text"  autocomplete="of" class="form-control"
                                        id="nombre_puesto" name="nombre_puesto">
                                </div>
                            </div>
                        </div>

                        

                    </div>
                </form>
            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-success " id="btn_guarda_puesto" onclick="guardarPuesto()" >
                    Guardar
                </button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>