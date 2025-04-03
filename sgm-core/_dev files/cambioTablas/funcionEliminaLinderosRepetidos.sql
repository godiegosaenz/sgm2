-- Function: sgm_app.update_forma_solar_ctl_item()

-- DROP FUNCTION sgm_app.update_forma_solar_ctl_item();


--select sgm_app.linderos_repeat();


CREATE OR REPLACE FUNCTION sgm_app.linderos_repeat()
  RETURNS void AS
$BODY$
DECLARE SECUENCIAMAX BIGINT;
DECLARE IDCTLGITEM BIGINT;
DECLARE
	C_LINDEROS CURSOR FOR 
		--ALTER TABLE SGM_APP.CAT_PREDIO_LINDERoS  ADD  estado_respaldo character varying (1);
		--select estado into estado_res from SGM_APP.CAT_PREDIO_LINDERoS;
		--UDATE sgm_app.cat_predio_linderos SET estado_respaldo = estado;
		select * from sgm_app.cat_predio_linderos;
  
	ACIERTOS INTEGER := 0;
BEGIN

	FOR C IN C_LINDEROS LOOP



		IF((SELECT COUNT(*) FROM SGM_APP.CAT_PREDIO_LINDEROS 
			WHERE predio = c.predio AND colindante = c.colindante AND estado = 'A') > 1) THEN 

			UPDATE SGM_APP.CAT_PREDIO_LINDEROS  SET ESTADO = 'I' WHERE id = c.id;
			

		END IF;

	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.linderos_repeat()
  OWNER TO sisapp;


