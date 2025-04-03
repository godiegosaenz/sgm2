-- Function: censocat.actualizar_formaasuelo_predios_censo_sgm()

-- DROP FUNCTION censocat.actualizar_formaasuelo_predios_censo_sgm();

CREATE OR REPLACE FUNCTION censocat.actualizar_clave_catastral_predios_censo_sgm()
  RETURNS void AS
$BODY$

DECLARE
	C_PREDIOS CURSOR FOR 
	
		SELECT ID, PROVINCIA, CANTON, PARROQUIA, ZONA, SECTOR, MZ, SOLAR, BLOQUE, PISO, UNIDAD , 
		LPAD(PROVINCIA::TEXT, 2, '0')||''||LPAD(CANTON::TEXT, 2, '0')||''||LPAD(PARROQUIA::TEXT, 2, '0')
		||''||LPAD(ZONA::TEXT, 2, '0')||''||LPAD(SECTOR::TEXT, 2, '0')||''||LPAD(MZ::TEXT, 3, '0') 
		||''||LPAD(SOLAR::TEXT, 3, '0')||''||LPAD(BLOQUE::TEXT, 2, '0')||''||LPAD(PISO::TEXT, 3, '0')
		||''||LPAD(UNIDAD::TEXT, 3, '0') AS CLAVE
		 FROM SGM_APP.CAT_PREDIO WHERE CLAVE_CAT IS NULL;
		
	ACIERTOS INTEGER := 0;
	PORC BOOLEAN := FALSE;
	FORMA CHARACTER VARYING(1);
BEGIN
	FOR C IN C_PREDIOS LOOP
		
		UPDATE SGM_APP.CAT_PREDIO SET CLAVE_CAT = C.CLAVE WHERE SGM_APP.CAT_PREDIO.ID = C.ID;
		ACIERTOS := ACIERTOS +1;	
	END LOOP;
	RAISE NOTICE 'PREDIOS CREADOS %',ACIERTOS;
	ACIERTOS := 0;	
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION censocat.actualizar_clave_catastral_predios_censo_sgm()
  OWNER TO sisapp;

SELECT CLAVE_CAT, * FROM SGM_APP.CAT_PREDIO  

SELECT censocat.actualizar_clave_catastral_predios_censo_sgm()