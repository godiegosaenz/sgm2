





---SOLO PARA ANDY




--12291
--12221

SELECT COUNT(*) FROM DBO."PREDIO"


/*SELECT COUNT(*) FROM SGM_APP.CAT_PREDIO CP where VALOR_M2 is not null;
INNER JOIN dbo."VALORES_PREDIO" PRE ON CP.PREDIALANT = PRE."Pre_CodigoCatastral";*/

UPDATE SGM_APP.CAT_PREDIO CP SET VALOR_M2 = (SELECT "ValPr_ValM2TerrAfec" FROM dbo."VALORES_PREDIO" PRE WHERE CP.PREDIALANT = PRE."Pre_CodigoCatastral")
UPDATE SGM_APP.CAT_PREDIO_AVAL_HISTORICO CP SET valor_base_m2 = (SELECT VALOR_M2 FROM SGM_APP.CAT_PREDIO  PRE WHERE CP.PREDIO = PRE.ID);


---QUERY PARA ACTUALIZAR A LOS QUE NO TIENE COMPRADOR DEFINIDO SE LES PUSO UN ENTE QUE SRE LLAM  SIN INFORMACION
UPDATE sgm_financiero.ren_liquidacion  SET comprador =  254839, nombre_comprador = NULL
  WHERE id in (
  SELECT  id from sgm_financiero.ren_liquidacion liq 
  WHERE predio_migrado = true and nombre_comprador like '%SIN %' and comprador is null);



--- QUERY PARA VER LAS CANTIDADES DE NOMBRES DE COMPRADORES QUE NO ESTRAN REGISTRADOS  EN EL SISTEMA



  --2093
  SELECT COUNT(*) from sgm_financiero.ren_liquidacion liq WHERE predio_migrado = true 
  --820
  SELECT COUNT(*) from sgm_financiero.ren_liquidacion liq WHERE predio_migrado = true and comprador is not null;
  ---1385
  SELECT distinct nombre_comprador  from sgm_financiero.ren_liquidacion liq 
  WHERE predio_migrado = true and nombre_comprador is not null and comprador is null;
  
  UPDATE sgm_financiero.ren_liquidacion  SET comprador =  254839, nombre_comprador = NULL
  WHERE id in (
  SELECT  id from sgm_financiero.ren_liquidacion liq 
  WHERE predio_migrado = true and nombre_comprador like '%SIN %' and comprador is null);

  SELECT * FROM sgm_app.cat_ente where 
/*
select sgm_app.insert_predios_urbanos_ame();
--2366
--273
select sum(total_pago)
from sgm_financiero.ren_liquidacion liq
left outer join dbo."TITULOS_PREDIO" tp on tp."TitPr_NumTitulo" = liq.id_liquidacion
left outer join dbo."CARTERA_VENCIDA" cv on cv."CarVe_NumTitulo" = liq.id_liquidacion
where predio is null and predio_rustico  is null and estado_referencia = 'E' 
and id_liquidacion like '%PU%' and total_pago > 0.0*/


SELECT * FROM sgm_financiero.ren_liquidacion  where predio = 4161

-------FUNCION PARA INSERTAR PREDIOS DEL AMZE Y DDE PASO PARA PONER LOS EEN REN LIQUIDZCIPN SI ESTOS NO EXIXTIOESEN COMO TAL :D :D :_

CREATE OR REPLACE FUNCTION sgm_financiero.insert_predios_urbanos_ame()
  RETURNS void AS
$BODY$

DECLARE
	C_PREDIO CURSOR FOR 

		SELECT pre."Pre_CodigoCatastral", 
					CAST(SUBSTRING(pre."Pre_CodigoCatastral", 9, 2) AS SMALLINT) AS SECTOR,
					CAST(SUBSTRING(pre."Pre_CodigoCatastral", 11, 2) AS SMALLINT) AS MZ, 
					CAST(SUBSTRING(pre."Pre_CodigoCatastral", 13, 3) AS SMALLINT) AS LOTE, 
					false soporta_hipoteca, now() inst_creacion, 9 usuario_creador,
					false propiedad_horizontal , 'M' estado, 
					valor."ValPr_ValTotalTerrPredio" as avaluo_solar, "ValPr_ValTotalEdifPredio" avaluo_construccion,
					 "ValPr_ValComerPredio" avaluo_municipal, "Pre_AreaTotalTer" area_solar, 
					 "Pre_NombrePredio" as nombre_edificio, pre."Pre_CodigoCatastral" predialant,
					 'Predio fue migrado por las liquidaciones pendientes de pago' observaciones,
					 CAST(SUBSTRING(pre."Pre_CodigoCatastral", 7, 2) AS SMALLINT) AS ZONA,
					 CAST(SUBSTRING(pre."Pre_CodigoCatastral", 16, 2) AS  SMALLINT) AS BLOQUE ,
					 0 piso, 0  unidad , 
					 CAST(SUBSTRING(pre."Pre_CodigoCatastral", 3, 2) AS  SMALLINT) AS CANTON,
					CAST(SUBSTRING(pre."Pre_CodigoCatastral", 1, 2) AS SMALLINT) AS provincia, 
					CAST(SUBSTRING(pre."Pre_CodigoCatastral", 5, 2) AS SMALLINT) AS PARROQUIA,
					'U' tipo_predio, pre."Pre_AreaTotalConst" area_declarada_const,
					
					true predialante_migrado, valor."ValPr_ValM2TerrAfec" valor_m2, 0  cdla
					
					FROM dbo."PREDIO" pre 
					INNER JOIN dbo."VALORES_PREDIO" valor ON valor."Pre_CodigoCatastral" = pre."Pre_CodigoCatastral"
					
					
					WHERE pre."Pre_CodigoCatastral" in (
					select DISTINCT
					CASE
					WHEN cv."Pre_CodigoCatastral" is null THEN tp."Pre_CodigoCatastral"::CHARACTER VARYING
					ELSE cv."Pre_CodigoCatastral"::CHARACTER VARYING END AS "Clave Catastral Segun AME"
					from sgm_financiero.ren_liquidacion liq
					left outer join dbo."TITULOS_PREDIO" tp on tp."TitPr_NumTitulo" = liq.id_liquidacion
					left outer join dbo."CARTERA_VENCIDA" cv on cv."CarVe_NumTitulo" = liq.id_liquidacion
					where predio is null and predio_rustico  is null and estado_referencia = 'E' 
					and id_liquidacion like '%PU%' and total_pago > 0.0) ORDER BY 22 DESC;

		
	NUM_PREDIO_MX BIGINT := 0;
	ID_PREDIO BIGINT := 0;
	titulos_creditos RECORD;
BEGIN

	FOR C IN C_PREDIO LOOP


		NUM_PREDIO_MX := (SELECT MAX(predio_cat.NUM_PREDIO) FROM sgm_app.cat_predio predio_cat);
		NUM_PREDIO_MX := NUM_PREDIO_MX + 1;
		
		INSERT INTO sgm_app.cat_predio(
					sector, mz, solar,  num_predio,  soporta_hipoteca,  inst_creacion, usuario_creador, 
					propiedad_horizontal, estado, avaluo_solar, avaluo_construccion, avaluo_municipal, 
					area_solar,  nombre_edificio,  predialant, observaciones, zona, lote, bloque, piso, 
					unidad,  canton, provincia, parroquia, tipo_predio, area_declarada_const, 
					predialante_migrado,valor_m2,  cdla)
		VALUES (
					c.sector, c.mz, c.lote,  NUM_PREDIO_MX,  c.soporta_hipoteca, c.inst_creacion, 
					c.usuario_creador, c.propiedad_horizontal, c.estado, c.avaluo_solar, 
					c.avaluo_construccion, c.avaluo_municipal, c.area_solar,  c.nombre_edificio, 
					c.predialant, c.observaciones, c.zona, c.lote, c.bloque, c.piso, c.unidad, 
					c.canton, c.provincia, c.parroquia, c.tipo_predio, c.area_declarada_const, 
					c.predialante_migrado, c.valor_m2, c.cdla)
		RETURNING ID INTO ID_PREDIO;

		FOR titulos_creditos IN (SELECT tp."TitPr_NumTitulo" FROM  dbo."TITULOS_PREDIO" tp WHERE tp."Pre_CodigoCatastral" = c."Pre_CodigoCatastral")
		LOOP 
			UPDATE sgm_financiero.ren_liquidacion liq SET predio = ID_PREDIO , predio_migrado = true
			where predio is null and predio_rustico  is null and estado_referencia = 'E' 
					and id_liquidacion = titulos_creditos."TitPr_NumTitulo"  and total_pago > 0.0  ;
		END LOOP;
		titulos_creditos := null;
		FOR titulos_creditos IN (SELECT tp."CarVe_NumTitulo" FROM  dbo."CARTERA_VENCIDA" tp WHERE tp."Pre_CodigoCatastral" = c."Pre_CodigoCatastral")
		LOOP 
			UPDATE sgm_financiero.ren_liquidacion liq SET predio = ID_PREDIO , predio_migrado = true
			where predio is null and predio_rustico  is null and estado_referencia = 'E' 
					and id_liquidacion = titulos_creditos."CarVe_NumTitulo"   and total_pago > 0.0  ;
		END LOOP;
		

	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.insert_predios_urbanos_ame()
  OWNER TO sisapp;


-----PARA CUANDO SE QUIERA CAMBIAR ALGUN DDATO DESDE EL QGIS :#:# :D

CREATE TRIGGER trigger_update_area_solar
  AFTER UPDATE
  ON geodata.geo_solar
  FOR EACH ROW
  EXECUTE PROCEDURE geodata.update_area_solar();


  CREATE OR REPLACE FUNCTION geodata.update_area_solar()
  RETURNS trigger AS
$BODY$
BEGIN
 
 UPDATE sgm_app.cat_predio cp SET area_solar = (SELECT  area_final FROM geodata.geo_solar gs WHERE gs.cod_predia = cp.num_predio AND gs.cod_cat_nu = cp.clave_cat) ;
 
 RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE  



-----EN DESARROLLO 



CREATE TRIGGER trigger_update_area_construccion
  AFTER UPDATE
  ON geodata.geo_bloque
  FOR EACH ROW
  EXECUTE PROCEDURE geodata.update_area_construccion();


  CREATE OR REPLACE FUNCTION geodata.update_area_construccion()
  RETURNS trigger AS
$BODY$
BEGIN
 
 UPDATE sgm_app.cat_predio cp SET area_solar = (SELECT  area_final FROM geodata.geo_solar gs WHERE gs.cod_predia = cp.num_predio AND gs.cod_cat_nu = cp.clave_cat) ;
 
 RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE  

SELECT total_area::NUMERIC, SUBSTRING(bloque, 2) FROM geodata.geo_bloque 









CREATE OR REPLACE FUNCTION sgm_app.update_nombres_propietario(
	)
  RETURNS void AS
$BODY$



DECLARE
	C_PREDIO CURSOR FOR 

		SELECT predio , ente   FROM sgm_app.cat_predio_propietario prop; 
		
	NOMBRE_SAVED CHARACTER VARYING := '';
    NOMBRE_TO_SAVE CHARACTER VARYING := '';
BEGIN
+++
	FOR C IN C_PREDIO LOOP

		NOMBRE_TO_SAVE := sgm_app.propietarios_por_predio(c.predio);
        
        RAISE NOTICE 'DATA 1% ', NOMBRE_TO_SAVE;
        NOMBRE_TO_SAVE := (SELECT REPLACE(NOMBRE_TO_SAVE, '/', ''));
        RAISE NOTICE 'DATA 2% ' ,NOMBRE_TO_SAVE;
		UPDATE sgm_app.cat_predio SET nombres_propietario = NOMBRE_TO_SAVE  WHERE id = c.predio;
			

	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.update_nombres_propietario()
  OWNER TO sisapp;



--SELECT sgm_app.update_nombres_propietario();







-- Function: sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer)

-- DROP FUNCTION sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.calculo_por_estratificacion(
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
		predio RECORD;
		id_aval_impuesto BIGINT := 0;
	BEGIN
		
		SELECT * INTO predio FROM sgm_app.cat_predio cp WHERE cp.id = _predio_id;
		RAISE NOTICE 'calculo_impuesto_predial predio%', predio;
		--Obteneer cat predio avaluo_municipal
		-- obtener banda impositiva: 
		IF((SELECT count(*) FROM sgm_app.aval_banda_impositiva AS cfc1 
			WHERE cfc1.anio_inicio = anio_inicio_val AND cfc1.anio_fin = anio_fin_val AND cfc1.estado = 'A') > 0) THEN 
				----SI A TODO EL CANTON SE LE COBRARA 
				IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp  
					WHERE  imp.anio_inicio = anio_inicio_val AND imp.anio_fin = anio_fin_val AND imp.estado = 'A') = 1) THEN
						
							id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp  WHERE  imp.anio_inicio = anio_inicio_val AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');									
							select sgm_app.calculo_impuesto_predial(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );
					
				ELSE 
				
					------POR SOLAR INICIO
					IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp WHERE
							imp.parroquia = predio.parroquia  AND imp.zona  = predio.zona
							AND imp.sector = predio.sector AND imp.mz = predio.mz AND imp.solar = predio.solar AND  imp.anio_inicio = anio_inicio_val 
							AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' )    ) THEN
									
									
									id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp WHERE imp.parroquia = predio.parroquia  
														 			AND imp.zona  = predio.zona AND imp.sector = predio.sector AND imp.mz = predio.mz AND imp.solar = predio.solar 
														 			AND  imp.anio_inicio = anio_inicio_val  AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');
																	
										select sgm_app.calculo_por_estratificacion(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );
					ELSE 	
							---POR MANZANA INICIO.					

							IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp WHERE
									imp.parroquia = predio.parroquia  AND imp.zona  = predio.zona
									AND imp.sector = predio.sector AND imp.mz = predio.mz AND imp.solar = 0 AND  imp.anio_inicio = anio_inicio_val 
									AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' )    ) THEN


													id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp WHERE imp.parroquia = predio.parroquia  
														AND imp.zona  = predio.zona AND imp.sector = predio.sector AND imp.mz = predio.mz AND imp.solar = 0
														AND  imp.anio_inicio = anio_inicio_val  AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');

													select sgm_app.calculo_por_estratificacion(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );

							ELSE
									---POR SECTOR INICIO
									IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp WHERE
											imp.parroquia = predio.parroquia  AND imp.zona  = predio.zona
											AND imp.sector = predio.sector AND imp.mz = 0 AND imp.solar = 0 AND  imp.anio_inicio = anio_inicio_val 
											AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' )    ) THEN

													id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp WHERE imp.parroquia = predio.parroquia  
																AND imp.zona  = predio.zona AND imp.sector = predio.sector AND imp.mz =0 AND imp.solar = 0
																AND  imp.anio_inicio = anio_inicio_val  AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');

													select sgm_app.calculo_por_estratificacion(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );
									ELSE
												-----POR ZONA INICIO
												IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp WHERE
														imp.parroquia = predio.parroquia  AND imp.zona  = predio.zona
														AND imp.sector = 0 AND imp.mz = 0 AND imp.solar = 0 AND  imp.anio_inicio = anio_inicio_val 
														AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' )    ) THEN

																	id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp WHERE imp.parroquia = predio.parroquia  
																				AND imp.zona  = predio.zona AND imp.sector = 0 AND imp.mz =0 AND imp.solar = 0
																				AND  imp.anio_inicio = anio_inicio_val  AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');

																	select sgm_app.calculo_por_estratificacion(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );
												ELSE					
														------POR PARROQUIA INICIO
														IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp WHERE
																imp.parroquia = predio.parroquia  AND imp.zona  = 0 
																AND imp.sector = 0 AND imp.mz = 0 AND imp.solar = 0 AND  imp.anio_inicio = anio_inicio_val 
																AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' )    ) THEN

																		id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp WHERE imp.parroquia = predio.parroquia  
																				AND imp.zona  = 0 AND imp.sector = 0 AND imp.mz =0 AND imp.solar = 0
																				AND  imp.anio_inicio = anio_inicio_val  AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');

																		select sgm_app.calculo_por_estratificacion(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );
														ELSE
																--- VA POR TODO 
																IF((SELECT count(*) FROM sgm_app.aval_impuesto_predios imp WHERE
																								imp.parroquia = 0  AND imp.zona  = 0 
																								AND imp.sector = 0 AND imp.mz = 0 AND imp.solar = 0 AND  imp.anio_inicio = anio_inicio_val 
																								AND imp.anio_fin = anio_fin_val AND imp.estado = 'A' )    ) THEN

																			id_aval_impuesto := (SELECT id FROM sgm_app.aval_impuesto_predios imp WHERE imp.parroquia = 0
																				AND imp.zona  =0  AND imp.sector = 0 AND imp.mz =0 AND imp.solar = 0
																				AND  imp.anio_inicio = anio_inicio_val  AND imp.anio_fin = anio_fin_val AND imp.estado = 'A');

																			select sgm_app.calculo_por_estratificacion(_predio_id, id_aval_impuesto, avaluo_municipal, anio_inicio_val, anio_fin_val );
																END IF;
																---FIN VA POR TODO
														END IF;
														---FIN PARROQUIA
												END IF;
												---FIN ZONA
									END IF;
									---FIN SECTOR
							END IF;
							---FIN MANZANA
					END IF;
					---FIN SOLAR					
				END IF;
		END IF;
		RETURN 'ok';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.calculo_por_estratificacion(bigint, text, numeric, integer, integer)
  OWNER TO sisapp;



-- Function: sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer)

 DROP FUNCTION sgm_app.calculo_impuesto_predial(bigint, text, numeric, integer, integer);

CREATE OR REPLACE FUNCTION sgm_app.calculo_impuesto_predial(
    _predio_id bigint,
    id_aval_impuesto bigint,
    avaluo_municipal numeric,
    anio_inicio_val integer,
    anio_fin_val integer)
  RETURNS void AS
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
		aval_det_cobro_impuesto_predios RECORD;
		predio RECORD;
	BEGIN
		
							SELECT * INTO aval_impuesto_predios  FROM sgm_app.aval_impuesto_predios imp  
									WHERE  imp.id = id_aval_impuesto;
					
							SELECT * INTO banda FROM sgm_app.aval_banda_impositiva AS cfc1 
									WHERE cfc1.id = aval_impuesto_predios.banda_impositiva;
						
							--COBRO DE SOLAR NO EDIFICADO RUBRO 
							IF((SELECT count(*) FROM sgm_app.aval_det_cobro_impuesto_predios cobro  
								WHERE  cobro.id_aval_impuesto_predio = aval_impuesto_predios.id AND cobro.id_rubro_cobrar = 4  ) = 1) THEN
								solar_no_edif := sgm_app.calculo_solar_no_edif(_predio_id, avaluo_municipal);
							END IF;

							----COBRO DE BOMBEROS DE SAN VICENTE :D RUBRO 7
							IF((SELECT count(*) FROM sgm_app.aval_det_cobro_impuesto_predios cobro  
								WHERE  cobro.id_aval_impuesto_predio = aval_impuesto_predios.id AND cobro.id_rubro_cobrar = 7  ) = 1) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;

							----IMPUESTO A INMUEBLES NO EDIFICADOS EN ZONAS DE PROMOCIÓN INMEDIATA RUBRO = 6
							IF((SELECT count(*) FROM sgm_app.aval_det_cobro_impuesto_predios cobro  
								WHERE  cobro.id_aval_impuesto_predio = aval_impuesto_predios.id AND cobro.id_rubro_cobrar = 7  ) = 1) THEN 
								calculo_bombero := sgm_app.calculo_solar_obsoleto(_predio_id, avaluo_municipal);
							END IF;

							----COBRO MEJORAS :V 
							IF((SELECT count(*) FROM sgm_app.aval_det_cobro_impuesto_predios cobro  
								WHERE  cobro.id_aval_impuesto_predio = aval_impuesto_predios.id AND cobro.id_rubro_cobrar = 7  ) = 1) THEN 
								calculo_bombero := sgm_app.calculo_bomberos(avaluo_municipal);
							END IF;
							
							IF(aval_impuesto_predios.cobro_mejoras = true) THEN 
								calculo_mejora := sgm_app.calculo_mejoras(avaluo_municipal);
							END IF;
							
								
							impuesto_predial := (avaluo_municipal * banda.multiplo_impuesto_predial)  / 1000;
							valor_banda := banda.multiplo_impuesto_predial;
							RAISE NOTICE 'IMPUESTO MUNICIPAL 1%', valor_banda;
					
		
		
		resut  := sgm_app.save_ren_liquidacion(_predio_id,  usuario, avaluo_municipal, impuesto_predial, solar_no_edif, calculo_bombero, calculo_mejora, anio_inicio_val, anio_fin_val, valor_banda );
		
			
		RETURN 'ok';
	END
	$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.calculo_impuesto_predial(bigint, bigint, numeric, integer, integer)
  OWNER TO sisapp;



------05-11-2017
UPDATE sgm_financiero.fn_exoneracion_tipo SET valida_remuneracion = FALSE WHERE id in (8, 9, 43, 45, 46, 47, 48);
UPDATE sgm_financiero.fn_exoneracion_tipo SET valida_remuneracion = TRUE WHERE id not in (8, 9, 43, 45, 46, 47, 48);