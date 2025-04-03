ALTER TABLE sgm_app.parametros_disparador ALTER COLUMN interfaz DROP NOT NULL;
ALTER TABLE sgm_app.parametros_disparador ALTER COLUMN var_interfaz DROP NOT NULL;
ALTER TABLE sgm_app.parametros_disparador ALTER COLUMN var_resp DROP NOT NULL;

INSERT INTO sgm_app.parametros_disparador(id, tipo_tramite, interfaz, var_interfaz, responsable, var_resp, fec_cre, incial)
    VALUES (DEFAULT, 20, null, null, 2284, 'asistenteAlcaldia', now(), false);
INSERT INTO sgm_app.parametros_disparador(id, tipo_tramite, interfaz, var_interfaz, responsable, var_resp, fec_cre, incial)
    VALUES (DEFAULT, 20, null, null, 2286, 'secretariaAlcaldia', now(), false);
INSERT INTO sgm_app.parametros_disparador(id, tipo_tramite, interfaz, var_interfaz, responsable, var_resp, fec_cre, incial)
    VALUES (DEFAULT, 20, null, null, 2284, 'asistenteAlcaldia_audiencia', now(), false);
INSERT INTO sgm_app.parametros_disparador(id, tipo_tramite, interfaz, var_interfaz, responsable, var_resp, fec_cre, incial)
    VALUES (DEFAULT, 20, null, null, 2286, 'secretariaAlcaldia_audiencia', now(), false);

