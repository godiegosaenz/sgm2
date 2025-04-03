-- Function: censocat.migrar_ciudadela_censo_sgm()

-- DROP FUNCTION censocat.migrar_ciudadela_censo_sgm();

CREATE OR REPLACE FUNCTION censocat.migrar_ciudadela_censo_sgm()
  RETURNS void AS
$BODY$

DECLARE
	C_CONTACTOS CURSOR FOR 
		SELECT PARROQUIA, NOMBRE
		FROM CENSOCAT.CIUDADELA;
	ACIERTOS INTEGER := 0;
	TPERSONA BOOLEAN := FALSE;
BEGIN
	FOR C IN C_CONTACTOS LOOP
		INSERT INTO SGM_APP.CAT_CIUDADELA (ID, COD_PARROQUIA, NOMBRE, ESTADO) 
		VALUES(DEFAULT, C.PARROQUIA, C.NOMBRE, TRUE);
		ACIERTOS := ACIERTOS +1;
		RAISE NOTICE 'CIUDADELAS CREADAS %',ACIERTOS;
	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION censocat.migrar_ciudadela_censo_sgm()
  OWNER TO sisapp;


-- SELECT censocat.migrar_ciudadela_censo_sgm()
-- DELETE FROM APP1.CAT_ENTE
-- truncate table app1.cat_pais restart identity cascade;