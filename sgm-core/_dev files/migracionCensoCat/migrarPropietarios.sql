-- Function: censocat.migrar_predios_propietarios_censo_sgm()

-- DROP FUNCTION censocat.migrar_predios_propietarios_censo_sgm();

CREATE OR REPLACE FUNCTION censocat.migrar_predios_propietarios_censo_sgm()
  RETURNS void AS
$BODY$

DECLARE
	C_PREDIOS CURSOR FOR 
		

		SELECT ID, PREDIO, PROPIETARIO,ESTADO FROM censocat.predio_propietario p where p.propietario in (SELECT id FROM sgm_app.cat_ente );
		
	ACIERTOS INTEGER := 0;
	ID_ENTE BIGINT;
BEGIN
	FOR C IN C_PREDIOS LOOP
		
		INSERT INTO SGM_APP.CAT_PREDIO_PROPIETARIO (ID,PREDIO,ENTE, ESTADO)
		VALUES(DEFAULT, C.PREDIO, C.PROPIETARIO, 'A');	
		ACIERTOS := ACIERTOS +1;	
	END LOOP;
	RAISE NOTICE 'PROPIETARIOS CREADOS %',ACIERTOS;
	ACIERTOS := 0;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION censocat.migrar_predios_propietarios_censo_sgm()
  OWNER TO sisapp;


-- SELECT censocat.migrar_predios_propietarios_censo_sgm()	
-- DELETE FROM APP1.CAT_PREDIO
-- truncate table app1.CTLG_CATALOGO restart identity cascade;
-- SELECT COUNT(*) FROM CENSOCAT.PREDIO_PROPIETARIO -- 3702