-- Function: sgm_app.avaluar_edificacion(bigint, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_edificacion(bigint, integer, integer);



-- Function: sgm_app.avaluar_predio(bigint, text, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_predio(bigint, text, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.avaluar_predio(
    _predio_id bigint,
    usuario text,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS text AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		resp text;
		
	BEGIN
		-- predio
	--SE LE ENVIA CERO CUANDO SSE VAN A EVALUAR TODOS LOS PREDIOS
		IF (_predio_id = 0) THEN 
			FOR predio IN SELECT * FROM sgm_app.cat_predio WHERE estado = 'A' LOOP
				resp := sgm_app.emitir_predio(predio.id, anio_inicio_val, anio_fin_val,  usuario );
				--RAISE NOTICE 'TODO OKI :V';
			END LOOP;
		ELSE --RAISE NOTICE  'NO TODOS OkI';
			FOR predio IN SELECT * FROM sgm_app.cat_predio WHERE id = _predio_id LOOP
				resp := sgm_app.emitir_predio(predio.id, anio_inicio_val, anio_fin_val, usuario );
			END LOOP;
		END IF;
		
		RETURN 'OkI';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.avaluar_predio(bigint, text, integer, integer)
  OWNER TO sisapp;



  -- Function: sgm_app.avaluar_solar(bigint, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_solar(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.avaluar_solar(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		vsuelo RECORD;
		valor numeric := 0.0;
		vsuelom2 numeric := 0.0;
		suma_coefs numeric:= 0.0;
		mz_avaluar integer := 0;
	BEGIN
		-- predio
		SELECT * INTO predio
			FROM sgm_app.cat_predio AS prd1 WHERE prd1.id = _predio_id;

			
		-- valor_suelo row 


		mz_avaluar = (SELECT count(*) FROM sgm_app.aval_valor_suelo AS vs1 WHERE vs1.parroquia = predio.parroquia AND vs1.zona = predio.zona 
				AND vs1.sector = predio.sector AND vs1.mz = predio.mz AND vs1.anio_inicio = anio_inicio_val AND vs1.anio_fin = anio_fin_val );

		IF(mz_avaluar = 1 )THEN 
			SELECT * INTO vsuelo FROM sgm_app.aval_valor_suelo AS vs1  WHERE vs1.parroquia = predio.parroquia AND vs1.zona = predio.zona 
				AND vs1.sector = predio.sector AND vs1.mz = predio.mz AND vs1.anio_inicio = anio_inicio_val AND vs1.anio_fin = anio_fin_val;
			vsuelom2 := vsuelo.valor_m2;
			--RAISE NOTICE 'DATA VALOR_M2 - %', vsuelo.valor_m2 ;
		
		ELSE
			mz_avaluar := 0;
		
			mz_avaluar := (SELECT count(*) FROM sgm_app.aval_valor_suelo AS vs1 
			WHERE vs1.parroquia = predio.parroquia AND vs1.zona = predio.zona 
				AND vs1.sector = predio.sector AND vs1.mz = predio.mz AND vs1.solar = predio.solar  
				AND vs1.anio_inicio = anio_inicio_val AND vs1.anio_fin = anio_fin_val);

			IF(mz_avaluar = 1 )THEN 
				SELECT * INTO vsuelo FROM sgm_app.aval_valor_suelo AS vs1 
				WHERE vs1.parroquia = predio.parroquia AND vs1.zona = predio.zona 
				AND vs1.sector = predio.sector AND vs1.mz = predio.mz AND vs1.solar = predio.solar  
				AND vs1.anio_inicio = anio_inicio_val AND vs1.anio_fin = anio_fin_val;
				vsuelom2 := vsuelo.valor_m2;
				--RAISE NOTICE 'DATA VALOR_M2 TOMO EL SUYO %', vsuelom2;
			ELSE
				SELECT * INTO vsuelo FROM sgm_app.aval_valor_suelo AS vs1  WHERE vs1.parroquia = predio.parroquia AND vs1.zona = predio.zona 
				AND vs1.sector = predio.sector AND vs1.mz = predio.mz AND vs1.anio_inicio = anio_inicio_val AND vs1.anio_fin = anio_fin_val;
				vsuelom2 := vsuelo.valor_m2;
				--RAISE NOTICE 'DATA VALOR_M2 tomo el de default%', vsuelom2;
			END IF;
		END IF;


	-- suma de coeficientes:
		suma_coefs := sgm_app.avaluar_solar_gen_ccl(_predio_id, anio_inicio_val, anio_fin_val)
			+ sgm_app.avaluar_solar_gen_cib(_predio_id, anio_inicio_val, anio_fin_val)
			+ sgm_app.avaluar_solar_gen_cic(_predio_id, anio_inicio_val, anio_fin_val)
			+ sgm_app.avaluar_solar_gen_cus(_predio_id, anio_inicio_val, anio_fin_val);
		-- area * valorsuelom2 * coefs
		--IF (suma_coefs != null) THEN 
			--IF(suma_coefs > 0) THEN 
			valor := predio.area_solar * vsuelom2 * suma_coefs; 
			--END IF;
		--ELSE
		--	valor := predio.area_solar * vsuelom2;
		--END IF;
		
		RETURN valor;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.avaluar_solar(bigint, integer, integer)
  OWNER TO sisapp;



-- Function: sgm_app.avaluar_solar_gen_ccl(bigint, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_solar_gen_ccl(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.avaluar_solar_gen_ccl(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		predio_s4 RECORD;
		coeficientes RECORD;
		suma numeric := 0.0;
		coef_relff numeric := 0.0;
		coef_sup numeric := 0.0;
		frente_fondo numeric(19,5) := 0.0;
		id_frente_fondo bigint :=0;
		superficie numeric(19,5) := 0.0;
		id_superficie bigint :=0;
	BEGIN
		--Cccl = coeficientes caracteristicas del lote
		-- predio
		SELECT * INTO predio
			FROM sgm_app.cat_predio AS prd1 WHERE prd1.id = _predio_id;
		--predio s4
		-- predio
		SELECT * INTO predio_s4
			FROM sgm_app.cat_predio_s4 AS prd4 WHERE prd4.predio = _predio_id;

		--RAISE NOTICE 'DATA PREDIO S4%', predio_s4.id;

		-- obtener coeficientes:
		coeficientes = null;
		-- Topografia:  predio.topografia
		IF( predio.topografia_solar > 0 ) THEN
			--RAISE NOTICE 'TUVO TOPOGRAFIA';
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = predio.topografia_solar;
		--SE VALIDA XK TODO NUMERO MULTIPLICADO POR CERO DA CERO :V 
			IF(coeficientes.valor_coef_inferior = 0 AND coeficientes.valor_coef_superior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_superior);
				--RAISE NOTICE 'TUVO TOPOGRAFIA Y FUE COEFICIENTE SUPERIOR SOLAR Y EL VALOR ES: %', suma;
			END IF;	
			IF(coeficientes.valor_coef_superior = 0 AND coeficientes.valor_coef_inferior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_inferior);
				--RAISE NOTICE 'TUVO TOPOGRAFIA Y FUE COEFICIENTE INFERIOR SOLAR Y EL VALOR ES: %', suma;
			END IF;	
		END IF;
		coeficientes = null;
		--LOCALIZACION
		IF( predio_s4.loc_manzana > 0 ) THEN
			--RAISE NOTICE 'TUVO loc_manzana';
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
				WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = predio_s4.loc_manzana;
		
			IF(coeficientes.valor_coef_inferior = 0 AND coeficientes.valor_coef_superior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_superior);
				--RAISE NOTICE 'TUVO loc_manzana Y FUE COEFICIENTE SUPERIOR SOLAR Y EL VALOR ES: %', suma;
			END IF;	
			IF(coeficientes.valor_coef_superior = 0 AND coeficientes.valor_coef_inferior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_inferior);
				--RAISE NOTICE 'TUVO loc_manzana Y FUE COEFICIENTE valor_coef_inferior SOLAR Y EL VALOR ES: %', suma;	
			END IF;	
		END IF;

		--TIPO DEL SUELO Caracteristica del Suelo
		IF( predio.tipo_suelo > 0 ) THEN
			--RAISE NOTICE 'TUVO tipo_suelo';
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
				WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = predio.tipo_suelo;
		
			IF(coeficientes.valor_coef_inferior = 0 AND coeficientes.valor_coef_superior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_superior);
				--RAISE NOTICE 'TUVO tipo_suelo Y FUE COEFICIENTE SUPERIOR SOLAR Y EL VALOR ES: %', suma;
			END IF;	

			IF(coeficientes.valor_coef_superior = 0 AND coeficientes.valor_coef_inferior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_inferior);
				--RAISE NOTICE 'TUVO loc_manzana Y FUE COEFICIENTE valor_coef_inferior SOLAR Y EL VALOR ES: %', suma;	
			END IF;	
		END IF;
		coeficientes = null;
		--Forma Poligonal del Predio
		IF( predio.forma_solar > 0 ) THEN
			--RAISE NOTICE 'TUVO tipo_suelo';
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
				WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = predio.forma_solar;
		
			IF(coeficientes.valor_coef_inferior = 0 AND coeficientes.valor_coef_superior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_superior);
				--RAISE NOTICE 'TUVO forma_solar Y FUE COEFICIENTE SUPERIOR SOLAR Y EL VALOR ES: %', suma;
			END IF;	

			IF(coeficientes.valor_coef_superior = 0 AND coeficientes.valor_coef_inferior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_inferior);
				--RAISE NOTICE 'TUVO forma_solar Y FUE COEFICIENTE valor_coef_inferior SOLAR Y EL VALOR ES: %', suma;
			END IF;	
			
		END IF;
		
		coeficientes = null;
		-- relacion frente fondo (default)
		if(predio_s4.fondo1 > 0) THEN frente_fondo = predio_s4.frente1 / predio_s4.fondo1; END IF;
		
		id_frente_fondo = 
		(SELECT id FROM sgm_app.ctlg_item item  
			WHERE
			referencia = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'RELACION FRENTE FONDO') 
			AND padre = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'RELACION FRENTE FONDO') 
			AND frente_fondo > rango_desde AND frente_fondo < rango_hasta);


		IF (id_frente_fondo != 0) THEN 
			--RAISE NOTICE 'TUVO id_frente_fondo';
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
				WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = id_frente_fondo;
		
			IF(coeficientes.valor_coef_inferior = 0 AND coeficientes.valor_coef_superior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_superior);
				--RAISE NOTICE 'TUVO id_frente_fondo Y FUE COEFICIENTE valor_coef_superior SOLAR Y EL VALOR ES: %', suma;
			END IF;	

			IF(coeficientes.valor_coef_superior = 0 AND coeficientes.valor_coef_inferior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_inferior);
				--RAISE NOTICE 'TUVO id_frente_fondo Y FUE COEFICIENTE valor_coef_inferior SOLAR Y EL VALOR ES: %', suma;
			END IF;	
		END IF;
		
		


		--SUPERFICIE
		coeficientes = null;
		IF (predio_s4.superficie is not null AND predio_s4.superficie > 0) THEN
			--RAISE NOTICE 'TUVO superficie';
			
			SELECT id FROM sgm_app.ctlg_item item  
				WHERE
				referencia = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'SUPERFICIE M2') 
				AND padre = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'SUPERFICIE M2') 
				AND superficie > rango_desde AND superficie < rango_hasta;
				
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
				WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = id_superficie;
		
			IF(coeficientes.valor_coef_inferior = 0 AND coeficientes.valor_coef_superior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_superior);
				--RAISE NOTICE 'TUVO superficie Y FUE COEFICIENTE valor_coef_superior SOLAR Y EL VALOR ES: %', suma;
			END IF;	

			IF(coeficientes.valor_coef_superior = 0 AND coeficientes.valor_coef_inferior > 0) THEN 
				suma := suma + (coeficientes.valor_coeficiente * coeficientes.valor_coef_inferior);
				--RAISE NOTICE 'TUVO superficie Y FUE COEFICIENTE valor_coef_inferior SOLAR Y EL VALOR ES: %', suma;
			END IF;	
			
		END IF;
		--RAISE NOTICE 'LA SUMA TOTAL ES: %', suma;
		RETURN suma;
		
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.avaluar_solar_gen_ccl(bigint, integer, integer)
  OWNER TO sisapp;


-- Function: sgm_app.avaluar_solar_gen_cib(bigint, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_solar_gen_cib(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.avaluar_solar_gen_cib(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
	    pserv RECORD;
		carc RECORD;
		coeficientes RECORD;
		valor_calculado numeric := 0.0;
		valorasumar numeric(19,4) := 0.0;
	BEGIN
		--Cib = coeficientes infraestrucctura basica
		-- obtener predio_servicio
		SELECT * INTO pserv
			FROM sgm_app.cat_predio_s6 AS ser1 WHERE ser1.predio = _predio_id;
		-- obtener coeficientes:
		-- sumar agua potable:
		IF (pserv.tiene_agua_potable = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'AGUA POTABLE' and referencia is not null AND padre is not null);	
			
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE

			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'AGUA POTABLE' and referencia is not null AND padre is not null);	

			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
		END IF;
		coeficientes = null;
		-- sumar alcantarillado sanitario:
		IF (pserv.tiene_alcantarillado = TRUE) THEN
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ALCANTARILLADO SANITARIO' and referencia is not null AND padre is not null);	
		
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ALCANTARILLADO SANITARIO' and referencia is not null AND padre is not null);	

			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
		END IF;

		coeficientes = null;
		-- Alcantarillafo pluvial:
		IF (pserv.alcantarillado_pluvial = TRUE) THEN
		
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ALCANTARILLADO PLUVIAL' and referencia is not null AND padre is not null);	
		
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ALCANTARILLADO PLUVIAL' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
		END IF;
		coeficientes = null;
		-- ELECTRICIDAd
		IF (pserv.tiene_electricidad = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ENERGIA ELECTRICA' and referencia is not null AND padre is not null);	
		
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ENERGIA ELECTRICA' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
			
		END IF;
		coeficientes = null;
		
		-- ASEO DE CALLES
		IF (pserv.aseo_calles = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ASEO DE CALLES' and referencia is not null AND padre is not null);	
		
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ASEO DE CALLES' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
			
		END IF;

		-- RECOLECCION BASURA
		IF (pserv.recoleccion_basura = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'RECOLECCION DE BASURA' and referencia is not null AND padre is not null);	
		
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'RECOLECCION DE BASURA' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
			
		END IF;



		
		FOR carc IN (SELECT  * FROM sgm_app.cat_predio_s6_has_vias WHERE predio_s6 = pserv.id) LOOP
			--SE TRAE LOS ID QUE ESTAN ALMACENADADOS EN PREDIO S6HAS VIAS 
			coeficientes = null;
			valorasumar := 0.0;
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1  WHERE cfc1.categoria_solar  = carc.ctlg_item;

			IF (coeficientes is not null) THEN
				
				IF( coeficientes.valor_coef_inferior > 0) THEN valorasumar :=  coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente; END IF;
				IF( coeficientes.valor_coef_superior > 0) THEN  valorasumar :=  coeficientes.valor_coef_superior * coeficientes.valor_coeficiente; END IF;
			
			END IF;
			
			valor_calculado := valor_calculado + valorasumar;

		END LOOP;
		FOR carc IN (SELECT  * FROM sgm_app.cat_predio_s6_has_instalacion_especial WHERE predio_s6 = pserv.id) LOOP
			--SE TRAE LOS ID QUE ESTAN ALMACENADADOS EN PREDIO S6HAS instalacion especial 
			coeficientes = null;
			valorasumar := 0.0;
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1  WHERE cfc1.categoria_solar  = carc.ctlg_item;

			IF (coeficientes is not null) THEN
				
				IF( coeficientes.valor_coef_inferior > 0) THEN valorasumar :=  coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente; END IF;
				IF( coeficientes.valor_coef_superior > 0) THEN  valorasumar :=  coeficientes.valor_coef_superior * coeficientes.valor_coeficiente; END IF;
				
			END IF;
				valor_calculado := valor_calculado + valorasumar;

		END LOOP;

		-- devolver valor:
		RETURN valor_calculado;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.avaluar_solar_gen_cib(bigint, integer, integer)
  OWNER TO sisapp;



-- Function: sgm_app.avaluar_solar_gen_cic(bigint, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_solar_gen_cic(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.avaluar_solar_gen_cic(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		pserv RECORD;
		coeficientes RECORD;
		valor_calculado numeric := 0.0;
	BEGIN
		--Cic = coeficientes infraestruccutura complementaria
		-- predio

		SELECT * INTO pserv
		FROM sgm_app.cat_predio_s6 AS ser1 WHERE ser1.predio = _predio_id;
		


		IF (pserv.tiene_telf_fijo = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'RED TELEFÓNICA' and referencia is not null AND padre is not null);	
			
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'RED TELEFÓNICA' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
		END IF;
		coeficientes = null;
		IF (pserv.tiene_aceras = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ACERAS' and referencia is not null AND padre is not null);	
			
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'ACERAS' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
		END IF;
		coeficientes = null;
		IF (pserv.tiene_bordillo = TRUE) THEN
			
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'BORDILLOS' and referencia is not null AND padre is not null);	
			
			valor_calculado := valor_calculado + (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
		ELSE
			SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = 
				(SELECT  id FROM sgm_app.ctlg_item WHERE valor = 'BORDILLOS' and referencia is not null AND padre is not null);	
				
			valor_calculado := valor_calculado + (coeficientes.valor_coef_inferior * coeficientes.valor_coeficiente);
		END IF;

		-- return suma
		RETURN valor_calculado;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.avaluar_solar_gen_cic(bigint, integer, integer)
  OWNER TO sisapp;





  -- Function: sgm_app.avaluar_solar_gen_cus(bigint, integer, integer)

-- DROP FUNCTION sgm_app.avaluar_solar_gen_cus(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.avaluar_solar_gen_cus(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
	    predio RECORD;
		coeficientes RECORD;
		suma numeric := 0.0;
	BEGIN
	---coeficientes usso del suelo  = cus
		-- predio
		SELECT * INTO predio
			FROM sgm_app.cat_predio AS prd1 WHERE prd1.id = _predio_id;
		-- obtener coeficientes:
		-- casos de predio.uso_suelo
		SELECT * INTO coeficientes FROM sgm_app.aval_coeficientes_suelo AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.categoria_solar = predio.uso_solar;

		suma := (coeficientes.valor_coef_superior * coeficientes.valor_coeficiente);
			
		RETURN suma;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.avaluar_solar_gen_cus(bigint, integer, integer)
  OWNER TO sisapp;


-- Function: sgm_app.calculo_bomberos(numeric)

-- DROP FUNCTION sgm_app.calculo_bomberos(numeric);

CREATE OR REPLACE FUNCTION sgm_app.calculo_bomberos(avaluo_municipal numeric)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		calculo_bombero numeric := 0.0;
		
	BEGIN
		calculo_bombero := (avaluo_municipal * 0.15) / 1000;
		RETURN calculo_bombero;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.calculo_bomberos(numeric)
  OWNER TO sisapp;




-- Function: sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer)

-- DROP FUNCTION sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.calculo_impuesto_predial(
    _predio_id bigint,
    usuario text,
    avaluo_municipal numeric,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS text AS
$BODY$
	<<fn>>
	DECLARE
		aval_impuesto_predios RECORD;
		banda RECORD;
		impuesto_predial numeric :=0.00;
		solar_no_edif numeric := 0.0;
		calculo_bombero numeric := 0.0;
		calculo_mejora numeric := 0.0;
		resut text;
		valor_banda numeric := 0.0;
	BEGIN
		--Obteneer cat predio avaluo_municipal
		-- obtener banda impositiva: 
		IF((SELECT count(*) FROM sgm_app.aval_banda_impositiva AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND predeterminada = true AND cfc1.estado = 'A') > 0) THEN 

				IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp  WHERE  imp.anio_inicio = anio_inicio_val AND imp.anio_fin = anio_fin_val AND imp.estado = 'A') = 1) THEN

				
					SELECT * INTO aval_impuesto_predios  FROM sgm_app.aval_impuesto_predios imp  WHERE  imp.anio_inicio = anio_inicio_val AND imp.anio_fin = anio_fin_val AND imp.estado = 'A';
					
					SELECT * INTO banda FROM sgm_app.aval_banda_impositiva AS cfc1 WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND predeterminada = true AND cfc1.estado = 'A';
							IF(aval_impuesto_predios.pago_solar_no_edificado = true) THEN 
								solar_no_edif := sgm_app.solar_no_edif_no_edif(_predio_id, avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_bombero = true) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;
							RAISE NOTICE 'IMPUESTO MUNICIPAL 1%', valor_banda;
					
				ELSE 
					FOR aval_impuesto_predios IN SELECT * FROM sgm_app.aval_impuesto_predios imp  WHERE  imp.anio_inicio = anio_inicio_val AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' LOOP

						IF(aval_impuesto_predios.solar > 0 AND aval_impuesto_predios.mz > 0 AND aval_impuesto_predios.zona > 0 AND aval_impuesto_predios.sector > 0 AND aval_impuesto_predios.parroquia > 0) THEN

							SELECT * INTO banda FROM sgm_app.aval_banda_impositiva imp WHERE imp.id = aval_impuesto_predios.banda_impositiva;
							
							IF(aval_impuesto_predios.pago_solar_no_edificado = true) THEN 
								solar_no_edif := sgm_app.solar_no_edif_no_edif(_predio_id, avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_bombero = true) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;
							RAISE NOTICE 'IMPUESTO MUNICIPAL 2%', valor_banda;

						END IF;
						
						IF(aval_impuesto_predios.solar = 0 AND aval_impuesto_predios.mz > 0 AND aval_impuesto_predios.zona > 0 AND aval_impuesto_predios.sector > 0 AND aval_impuesto_predios.parroquia > 0) THEN

							SELECT * INTO banda FROM sgm_app.aval_banda_impositiva imp WHERE imp.id = aval_impuesto_predios.banda_impositiva;
							
							IF(aval_impuesto_predios.pago_solar_no_edificado = true) THEN 
								solar_no_edif := sgm_app.solar_no_edif_no_edif(_predio_id, avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_bombero = true) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;
							RAISE NOTICE 'IMPUESTO MUNICIPAL 3%', valor_banda;
						END IF;

						IF(aval_impuesto_predios.solar = 0 AND aval_impuesto_predios.mz = 0 AND aval_impuesto_predios.zona > 0 AND aval_impuesto_predios.sector > 0 AND aval_impuesto_predios.parroquia > 0) THEN

							SELECT * INTO banda FROM sgm_app.aval_banda_impositiva imp WHERE imp.id = aval_impuesto_predios.banda_impositiva;
							
							IF(aval_impuesto_predios.pago_solar_no_edificado = true) THEN 
								solar_no_edif := sgm_app.solar_no_edif_no_edif(_predio_id, avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_bombero = true) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;
							RAISE NOTICE 'IMPUESTO MUNICIPAL 4%', valor_banda;
						END IF;

						IF(aval_impuesto_predios.solar = 0 AND aval_impuesto_predios.mz = 0 AND aval_impuesto_predios.zona = 0 AND aval_impuesto_predios.sector > 0 AND aval_impuesto_predios.parroquia > 0) THEN

							SELECT * INTO banda FROM sgm_app.aval_banda_impositiva imp WHERE imp.id = aval_impuesto_predios.banda_impositiva;

							IF(predio.pago_solar_no_edificado = true) THEN 
								solar_no_edif := sgm_app.solar_no_edif_no_edif(_predio_id, avaluo_municipal);
							END IF;
							IF(predio.cobro_bombero = true) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							IF(predio.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;
							RAISE NOTICE 'IMPUESTO MUNICIPAL 5%', valor_banda;
						END IF;

						IF(aval_impuesto_predios.solar = 0 AND aval_impuesto_predios.mz = 0 AND aval_impuesto_predios.zona = 0 AND aval_impuesto_predios.sector = 0 AND aval_impuesto_predios.parroquia > 0) THEN

							SELECT * INTO banda FROM sgm_app.aval_banda_impositiva imp WHERE imp.id = aval_impuesto_predios.banda_impositiva;
							
							IF(aval_impuesto_predios.pago_solar_no_edificado = true) THEN 
								solar_no_edif := sgm_app.solar_no_edif_no_edif(_predio_id, avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_bombero = true) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							IF(aval_impuesto_predios.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;

							RAISE NOTICE 'IMPUESTO MUNICIPAL 6%', valor_banda;
							
						END IF;

						RAISE NOTICE 'IMPUESTO MUNICIPAL %', avaluo_municipal;
						
					END LOOP;
				END IF;
		END IF;

		
		resut  := sgm_app.save_ren_liquidacion(_predio_id,  usuario, avaluo_municipal, impuesto_predial, solar_no_edif, calculo_bombero, calculo_mejora, anio_inicio_val, anio_fin_val, valor_banda );
		
			
		RETURN 'ok';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer)
  OWNER TO sisapp;




  -- Function: sgm_app.calculo_mejoras(numeric)

-- DROP FUNCTION sgm_app.calculo_mejoras(numeric);

CREATE OR REPLACE FUNCTION sgm_app.calculo_mejoras(avaluo_municipal numeric)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		calculo_mejora numeric := 0.0;
		
	BEGIN
		calculo_mejora := (avaluo_municipal * 0.15) / 1000;
		RETURN calculo_mejora;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.calculo_mejoras(numeric)
  OWNER TO sisapp;



-- Function: sgm_app.calculo_solar_no_edif(bigint, numeric)

-- DROP FUNCTION sgm_app.calculo_solar_no_edif(bigint, numeric);

CREATE OR REPLACE FUNCTION sgm_app.calculo_solar_no_edif(
    _predio_id bigint,
    avaluo_municipal numeric)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		calculo_solar numeric := 0.0;
		
	BEGIN
		-- predio se consulta si el solar esta xvacio si o no 
		IF((SELECT count(*) FROM sgm_app.cat_predio_s4 WHERE predio = _predio_id AND estado_solar = (SELECT id FROM sgm_app.ctlg_item WHERE catalogo = 
		(SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.estado_solar') AND valor = 'VACIO')) > 0) THEN 
			calculo_solar := (avaluo_municipal * 2) / 1000;
		END IF;
		RETURN calculo_solar;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.calculo_solar_no_edif(bigint, numeric)
  OWNER TO sisapp;



-- Function: sgm_app.clean_avaluos(text, bigint)

-- DROP FUNCTION sgm_app.clean_avaluos(text, bigint);




-- Function: sgm_app.depreciacion_edificacion(bigint, integer, integer)

-- DROP FUNCTION sgm_app.depreciacion_edificacion(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.depreciacion_edificacion(
    _id_edif bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		estado_condicion_bueno bigint;
		estado_condicion_malo bigint;
		estado_condicion_muy_bueno bigint;
		estado_condicion_obsoleto bigint;
		estado_condicion_regular bigint;
		 anio_actual integer;
		edificacion1 RECORD;
		deprec_row RECORD;
		edad smallint := 0;
	BEGIN

		anio_actual := (select extract(year from now())::INTEGER);
		-- obtener datos de edificacion
		SELECT edif1.anio_cons, pred1.zona, pred1.parroquia, edif1.estado_conservacion as condicion
			INTO edificacion1 
			FROM sgm_app.cat_predio_edificacion AS edif1 
			INNER JOIN sgm_app.cat_predio AS pred1 ON pred1.id = edif1.predio
			WHERE edif1.id = _id_edif;
		
		-- comprobar si edificacion tiene año-fin-construccion, caso contrario elegir por default:

		IF (edificacion1.anio_cons IS NULL) THEN
			SELECT ed1.edad INTO edad FROM sgm_app.aval_edad_zona_const AS ed1
				WHERE ed1.zona = edificacion1.zona AND ed1.parroquia = edificacion1.parroquia;
		ELSE
			IF(edificacion1.anio_cons < 1000) THEN 
				SELECT ed1.edad INTO edad FROM sgm_app.aval_edad_zona_const AS ed1
				WHERE ed1.zona = edificacion1.zona AND ed1.parroquia = edificacion1.parroquia;
			ELSE		
				edad := anio_actual - edificacion1.anio_cons;
			END IF;
		END IF;
		-- obtener row de depreciacion
		SELECT dep1.* INTO deprec_row
			FROM sgm_app.cat_predio_edificacion_prop AS car1
			INNER JOIN sgm_app.cat_edf_prop AS esp1 ON esp1.id = car1.prop
			INNER JOIN sgm_app.aval_depreciacion_solar AS dep1 ON dep1.espec = esp1.id
			WHERE car1.edificacion = _id_edif AND esp1.tipo = 1 AND dep1.anios = fn.edad AND dep1.anio_fin =  anio_fin_val AND dep1.anio_inicio = anio_inicio_val
			ORDER BY car1.porcentaje DESC LIMIT 1;
		-- si no hay row de depreciacion, return 1:
		IF(deprec_row IS NULL) THEN
			RETURN 1::numeric;
		END IF;

		estado_condicion_bueno = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'BUENO' AND catalogo = (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.bloque.estadoconservaci'))::INTEGER;
		estado_condicion_malo = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'MALO' AND catalogo = (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.bloque.estadoconservaci'))::INTEGER;
		estado_condicion_muy_bueno = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'MUY BUENO' AND catalogo = (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.bloque.estadoconservaci'))::INTEGER;
		estado_condicion_obsoleto = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'OBSOLETO' AND catalogo = (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.bloque.estadoconservaci'))::INTEGER;
		estado_condicion_regular = (SELECT id FROM sgm_app.ctlg_item WHERE valor = 'REGULAR' AND catalogo = (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.bloque.estadoconservaci'))::INTEGER;
		
		-- dependiendo de la condicion, retornar el coeficiente
		IF ( edificacion1.condicion = estado_condicion_bueno OR edificacion1.condicion = estado_condicion_muy_bueno ) THEN
			-- bueno:
			RETURN deprec_row.bueno;
		ELSE
			IF ( edificacion1.condicion = estado_condicion_malo OR edificacion1.condicion = estado_condicion_obsoleto ) THEN
				-- malo:
				RETURN deprec_row.malo;
			ELSE
				RETURN deprec_row.regular;
			END IF;
		END IF;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.depreciacion_edificacion(bigint, integer, integer)
  OWNER TO sisapp;




-- Function: sgm_app.depreciar(integer, integer, bigint)

-- DROP FUNCTION sgm_app.depreciar(integer, integer, bigint);

CREATE OR REPLACE FUNCTION sgm_app.depreciar(
    vidautil integer,
    panios integer,
    estado bigint)
  RETURNS numeric AS
$BODY$
DECLARE
 C_RANGO CURSOR FOR 
	SELECT  ID, case
			when estado = 86 then NUEVO
			when estado = 43  then BUENO
			when estado = 46 then REGULAR
			when estado = 45 then MALO
			when estado = 47 then OBSOLETO
			else 0
		end as valor
		FROM sgm_app.RANGOS_DEPRECIACION RD 
	WHERE RD.ANIOS = (CASE WHEN panios = (EXTRACT (YEAR FROM NOW())) THEN 1 ELSE (EXTRACT (YEAR FROM NOW())) - panios END) AND VIDAUTIL BETWEEN RD.VIDAUTILDESDE AND RD.VIDAUTILHASTA;
	--vidautil >= RD.VIDAUTILDESDE AND vidautil <= RD.VIDAUTILHASTA
 C_VALOR RECORD;
 DATO NUMERIC :=0;

BEGIN
	OPEN C_RANGO;
	IF(C_RANGO IS NOT NULL) THEN
		FETCH C_RANGO INTO C_VALOR;
		CLOSE C_RANGO;
		--RAISE NOTICE 'FACTOR DEPRECIACIO (%) ', C_VALOR.ID||' - '||C_VALOR.VALOR;
		RETURN C_VALOR.VALOR;
	END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.depreciar(integer, integer, bigint)
  OWNER TO sisapp;



-- Function: sgm_app.emision_predial_ren_liquidacion(bigint, text, integer, integer)

-- DROP FUNCTION sgm_app.emision_predial_ren_liquidacion(bigint, text, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.emision_predial_ren_liquidacion(
    _predio_id bigint,
    usuario text,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS text AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		resp text;
		
	BEGIN
		-- predio
	--SE LE ENVIA CERO CUANDO SSE VAN A EVALUAR TODOS LOS PREDIOS
		IF (_predio_id = 0) THEN 
			FOR predio IN SELECT * FROM sgm_app.cat_predio WHERE estado = 'A' and avaluo_municipal is not null and avaluo_municipal > 0 LOOP
				resp := sgm_app.calculo_impuesto_predial(predio.id, usuario, predio.avaluo_municipal,  anio_inicio_val, anio_fin_val );
				
			END LOOP;
		ELSE
			FOR predio IN SELECT * FROM sgm_app.cat_predio WHERE id = _predio_id LOOP
				resp := sgm_app.calculo_impuesto_predial(predio.id, usuario, predio.avaluo_municipal, anio_inicio_val, anio_fin_val );
			END LOOP;
		END IF;
		
		RETURN 'OkI';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.emision_predial_ren_liquidacion(bigint, text, integer, integer)
  OWNER TO sisapp;



-- Function: sgm_app.emitir_predio(bigint, integer, integer, text)

-- DROP FUNCTION sgm_app.emitir_predio(bigint, integer, integer, text);

CREATE OR REPLACE FUNCTION sgm_app.emitir_predio(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer,
    usuario text)
  RETURNS text AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		edif RECORD;
		avalsolar numeric:= 0.0;
		avaledif numeric:= 0.0;
		avaltotal numeric:= 0.0;
		valorpagar numeric:= 0.0;
		suma_coefs numeric:= 0.0;
		banda numeric:= 0.0;
		predio_castjs text;
		predio_edificacion_castjs text;
	BEGIN
		
		-- alavuar solar:
		--RAISE NOTICE 'DATA QUE VA PA AVALUOS SOLAR%', _predio_id;
		avalsolar := sgm_app.avaluar_solar(_predio_id, anio_inicio_val, anio_fin_val );
		IF(avalsolar is null) THEN avalsolar := 0.0; END IF;
		---RAISE NOTICE 'DATA AVALUOS SOLAR%', avalsolar;
		-- avaluar edificaciones y sumarlas:
		avaledif := 0.0;
		FOR edif IN 
			(SELECT * FROM sgm_app.cat_predio_edificacion AS edf1 WHERE edf1.predio=_predio_id) LOOP
			avaledif:= avaledif + sgm_app.avaluar_edificacion(edif.id, anio_inicio_val, anio_fin_val );
		END LOOP;
		IF(avaledif is null) THEN avaledif := 0.0; END IF;
		-- total:
		avaltotal:= avalsolar + avaledif;
		--RAISE NOTICE 'DATA AVALUOS SOLAR%', avalsolar;
		-- guardar:
		UPDATE sgm_app.cat_predio SET avaluo_construccion = avaledif,  avaluo_solar = avalsolar,
			avaluo_municipal = avaltotal WHERE id = _predio_id;
			
		IF(avalsolar != null or avalsolar > 0.0 or  avaledif != null or avaledif >0 ) THEN
			suma_coefs := sgm_app.suma_coeficientes(_predio_id , anio_inicio_val , anio_fin_val);
			predio_castjs = (select to_json(p) from sgm_app.cat_predio p where p.id =  _predio_id);
			predio_edificacion_castjs = (select array_to_json(array_agg(p)) from  sgm_app.cat_predio_edificacion p where p.predio = _predio_id);
		
			INSERT INTO sgm_app.cat_predio_aval_historico(id, predio, avaluo_solar, avaluo_construccion, 
								avaluo_municipal, fecha_actualizacion, anio_inicio, anio_fin, usuario_ingreso, predio_json, predio_edificacion_json, suma_coeficientes)
		VALUES (DEFAULT, _predio_id, avalsolar, avaledif, avalsolar + avaledif, now(), anio_inicio_val, anio_fin_val, usuario, predio_castjs, predio_edificacion_castjs, suma_coefs );
		END IF;	
		
		
			
		RETURN 'ok';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.emitir_predio(bigint, integer, integer, text)
  OWNER TO sisapp;



-- Function: sgm_app.save_ren_liquidacion(bigint, text, numeric, numeric, numeric, numeric, numeric, integer, integer, numeric)

-- DROP FUNCTION sgm_app.save_ren_liquidacion(bigint, text, numeric, numeric, numeric, numeric, numeric, integer, integer, numeric);

CREATE OR REPLACE FUNCTION sgm_app.save_ren_liquidacion(
    _predio_id bigint,
    usuario text,
    avaluo_municipal numeric,
    impuesto_predial numeric,
    solar_no_edif numeric,
    bombero numeric,
    mejoras numeric,
    anio_inicio integer,
    anio_fin integer,
    banda_impositiva_aplicada numeric)
  RETURNS text AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		max_num_liquidacion bigint;
		total_liquidacion numeric  := 0.0;
		comprador_cpp bigint := 0;
		bomberoexit boolean := false;
		id_ren_liquidacion bigint;
		rubro bigint;
		
	BEGIN

	--buscamos la ssecuencia maxima de liquidacion 
	max_num_liquidacion := 0;
	IF((SELECT count(secuencia) FROM sgm_financiero.ren_secuencia_num_liquidacion seq WHERE seq.anio = anio_inicio AND seq.id_tipo_liquidacion = 13) = 0) THEN
		INSERT INTO sgm_financiero.ren_secuencia_num_liquidacion(id, secuencia, anio, id_tipo_liquidacion)
		VALUES (DEFAULT, 1, anio_inicio, 13);
		max_num_liquidacion = 1;
	ELSE 
		max_num_liquidacion =(SELECT max(secuencia) FROM sgm_financiero.ren_secuencia_num_liquidacion seq WHERE seq.anio = anio_inicio AND seq.id_tipo_liquidacion = 13);
		max_num_liquidacion = max_num_liquidacion + 1::bigint;
		INSERT INTO sgm_financiero.ren_secuencia_num_liquidacion(id, secuencia, anio, id_tipo_liquidacion)
		VALUES (DEFAULT, max_num_liquidacion, anio_inicio, 13);
	END IF;

	--calculo del total dr la liquidacion
	total_liquidacion = impuesto_predial + solar_no_edif + bombero + mejoras;


	--trae aal comprador 
	IF((SELECT count(*) FROM sgm_app.cat_predio_propietario cpp WHERE cpp.predio = _predio_id) > 0) THEN

		comprador_cpp = (SELECT ente FROM sgm_app.cat_predio_propietario cpp
					LEFT OUTER JOIN sgm_app.ctlg_item ti ON ti.id = cpp.tipo
					 WHERE cpp.predio = _predio_id AND cpp.estado = 'A' AND ti.valor = 'PROPIETARIO' LIMIT 1);

		SELECT * INTO predio FROM sgm_app.cat_predio pre WHERE 	pre.id = _predio_id;
		
		IF(bombero = 0.00::numeric) THEN bomberoexit := true; END IF;
		
		INSERT INTO sgm_financiero.ren_liquidacion(id,
			    num_liquidacion, id_liquidacion, 
			    tipo_liquidacion, total_pago, usuario_ingreso,
			    fecha_ingreso, comprador, costo_adq, cuantia,
			    estado_liquidacion, predio, coactiva, estado_coactiva,
			    observacion, anio, 
			    valor_comercial, valor_hipoteca, 
			    avaluo_municipal, avaluo_construccion, avaluo_solar, bombero, 
			    estado_coactiva, saldo,  valor_catastral, 
			    valor_nominal, valor_mora, total_adicionales, otros, valor_compra, valor_venta, 
			    valor_mejoras, area_total, patrimonio, exonerado, banda_impositiva )
   
		VALUES (DEFAULT, max_num_liquidacion,  'PUR-'||''||max_num_liquidacion::CHARACTER VARYING, 
		       13, total_liquidacion, usuario, now(), comprador_cpp, 0.00, 0.00, 2, 
		       _predio_id, false, 1, 'Emision Predial '||' '||anio_inicio::TEXT , anio_inicio, 
		       0.00, 0.0, 
		       predio.avaluo_municipal, predio.avaluo_construccion, predio.avaluo_solar, 
		       bomberoexit, 1, total_liquidacion, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00,
		       mejoras, predio.area_solar, 0.00, false, banda_impositiva_aplicada) 

		RETURNING id INTO id_ren_liquidacion;

		IF (impuesto_predial > 0.00::NUMERIC ) THEN 

			rubro = (SELECT id FROM sgm_financiero.ren_rubros_liquidacion WHERE tipo_liquidacion = 13 AND descripcion = 'Impuesto Predial Urbano');

			INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, impuesto_predial, TRUE, 0.00);
			
		END IF;
		IF (solar_no_edif > 0.00::NUMERIC ) THEN 

			rubro = (SELECT id FROM sgm_financiero.ren_rubros_liquidacion WHERE tipo_liquidacion = 13 AND descripcion = 'Impuesto Solar No Edif');

			INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, solar_no_edif, TRUE, 0.00);
			
		END IF;	
		IF (bombero > 0.00::NUMERIC ) THEN 

			rubro = (SELECT id FROM sgm_financiero.ren_rubros_liquidacion WHERE tipo_liquidacion = 13 AND descripcion = 'Bomberos ');

			INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, bombero, TRUE, 0.00);
			
		END IF;		
		
		UPDATE sgm_app.cat_predio SET base_imponible =  banda_impositiva_aplicada WHERE id = _predio_id;
	

	END IF;
	RETURN ':v';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.save_ren_liquidacion(bigint, text, numeric, numeric, numeric, numeric, numeric, integer, integer, numeric)
  OWNER TO sisapp;


-- Function: sgm_app.suma_coeficientes(bigint, integer, integer)

-- DROP FUNCTION sgm_app.suma_coeficientes(bigint, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.suma_coeficientes(
    _predio_id bigint,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS numeric AS
$BODY$
	<<fn>>
	DECLARE
		predio RECORD;
		vsuelo RECORD;
		valor numeric := 0.0;
		vsuelom2 numeric := 0.0;
		suma_coefs numeric:= 0.0;
		mz_avaluar integer := 0;
	BEGIN
		
		suma_coefs := sgm_app.avaluar_solar_gen_ccl(_predio_id, anio_inicio_val, anio_fin_val)
			+ sgm_app.avaluar_solar_gen_cib(_predio_id, anio_inicio_val, anio_fin_val)
			+ sgm_app.avaluar_solar_gen_cic(_predio_id, anio_inicio_val, anio_fin_val)
			+ sgm_app.avaluar_solar_gen_cus(_predio_id, anio_inicio_val, anio_fin_val);

		
		RETURN suma_coefs;
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.suma_coeficientes(bigint, integer, integer)
  OWNER TO sisapp;















