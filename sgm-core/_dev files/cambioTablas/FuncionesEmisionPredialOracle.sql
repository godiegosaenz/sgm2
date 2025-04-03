-- Function: sgm_app.calculo_bomberos(numeric)

-- DROP FUNCTION sgm_app.calculo_bomberos(numeric);

CREATE OR REPLACE FUNCTION sgm_app.calculo_bomberos(avaluo_municipal numeric)
  RETURN numeric AS

		es_vacio number(1,0) :=0;
		calculo_bombero numeric := 0.0;
		
	BEGIN
		calculo_bombero := (avaluo_municipal * 0.15) / 1000;
		RETURN calculo_bombero;
	EXCEPTION
          WHEN NO_DATA_FOUND THEN
            RETURN 1; 
          WHEN OTHERS THEN
            RETURN 1;
	END;
