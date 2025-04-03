﻿-- Function: censocat.migrar_predios_edificacion_censo_sgm()

-- DROP FUNCTION censocat.migrar_predios_edificacion_censo_sgm();

CREATE OR REPLACE FUNCTION censocat.migrar_predios_edificacion_censo_sgm()
  RETURNS void AS
$BODY$

DECLARE
	C_PREDIOS CURSOR FOR 
		SELECT P.ID, P.EDIFICACION, P.CARACTERISTICA, P.PORCENTAJE, P.ESTADO, E.PREDIO
		FROM CENSOCAT.EDIFICACION_CARACT P 
		INNER JOIN APP1.CAT_PREDIO_EDIFICACION E ON E.ID=P.EDIFICACION;
	ACIERTOS INTEGER := 0;
	PORC BOOLEAN := FALSE;
	FORMA CHARACTER VARYING(1);
	PREDIO BIGINT;
BEGIN
	FOR C IN C_PREDIOS LOOP
		SELECT ID INTO PREDIO FROM APP1.CAT_PREDIO_EDIFICACION WHERE ID = C.EDIFICACION AND ESTADO = TRUE LIMIT 1;
		IF(PREDIO IS NOT NULL) THEN
			SELECT ID INTO PREDIO FROM APP1.CAT_PREDIO_EDIFICACION_PROP WHERE EDIFICACION=C.EDIFICACION AND PROP=C.CARACTERISTICA;
			IF (PREDIO IS NOT NULL) THEN 
				
			ELSE 
				INSERT INTO APP1.CAT_PREDIO_EDIFICACION_PROP(ID,EDIFICACION,PROP,PORCENTAJE,PREDIO,ESTADO)
				VALUES (DEFAULT, C.EDIFICACION, CASE WHEN C.CARACTERISTICA IS NULL THEN 2 ELSE C.CARACTERISTICA END, C.PORCENTAJE,C.PREDIO, C.ESTADO);
			END IF;
			ACIERTOS := ACIERTOS + 1;
		END IF;
	END LOOP;
	RAISE NOTICE 'CARACT EDIFICACIONES CREADAS %',ACIERTOS;
	ACIERTOS := 0;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION censocat.migrar_predios_edificacion_censo_sgm()
  OWNER TO sisapp;


-- SELECT censocat.migrar_predios_edificacion_censo_sgm() -- 33130
-- DELETE FROM app1.CAT_PREDIO_EDIFICACION
-- truncate table app1.CTLG_CATALOGO restart identity cascade;

--SELECT * FROM APP1.CAT_PREDIO_EDIFICACION T where t.id = 476
--SELECT * FROM CENSOCAT.EDIFICACION_CARACT WHERE ESTADO = FALSE
--SELECT * FROM APP1.CAT_PREDIO_EDIFICACION_PROP
-- TRUNCATE TABLE APP1.CAT_PREDIO_EDIFICACION_PROP RESTART IDENTITY CASCADE
SELECT COUNT(*) FROM censocat.edificacion_caract  -- 33159