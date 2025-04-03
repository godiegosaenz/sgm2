-- Funct
CREATE OR REPLACE FUNCTION sgm_FINANCIERO.insertarLiquidacion()
  RETURNS void AS
$BODY$
DECLARE comprador BIGINT;
DECLARE ID_REN_LIQUIDACION BIGINT;
DECLARE ID_PAGO BIGINT;
DECLARE bomberoexist BOOLEAN;
DECLARE rubro BIGINT;
DECLARE estadoLiquidacion BIGINT;
DECLARE tipoLiquidacion BIGINT;
DECLARE observacion TEXT;
DECLARE estado_pago BOOLEAN;
DECLARE saldo DECIMAL;
DECLARE predioUrbano BIGINT;
DECLARE predioRural BIGINT;
DECLARE 
--SE PUSO UNA COUMNA DE DIREENCIAA XK EN ALGUNAS LIQUIDACIONES EL PAGO ERA MENOR O MAYOR AL TOTAL EMITIDO Y NO SE PUDO IDDENTIFICAR SI ERA UN ABONO.
--CURSORES
		T_TITULO_PREDIAL CURSOR FOR 
					SELECT 	       P.ID AS predio , PR.id as predio_rustico,
							"Pre_CodigoCatastral" AS pre_codigo_catastral, "TitPr_NumTitulo" AS num_titulo, "TitPr_ValTotalTerrPredio" AS valor_terr_predio,  
						       "TitPr_ValTotalEdifPredio" AS valor_edif_predio , "TitPr_ValCultivos" AS val_cultivo, "TitPr_ValForestales" AS val_forestal, 
						       "TitPr_ValObrasInter" AS val_obras_inter, "TitPr_ValOtrasInver" AS otras_liquidaciones, "TitPr_ValComerPredio" AS val_comer_predio, 
						       "TitPr_IPU" AS ipu  , "TitPr_SolNoEdif" AS solar_no_edificado, "TitPr_Bomberos" AS bomberos, "TitPr_FechaEmision" AS fecha_emision, 
						       "TitPr_ValorEmitido" as total, "TitPr_ValorTCobrado" as pago,
						       "TitPr_ValorEmitido"- ("TitPr_ValorTCobrado" + "TitPr_Descuento" - "TitPr_Recargo"  - "TitPr_Interes") AS diferencia,
							"TitPr_Descuento" as descuent, "TitPr_Recargo" as recargo, "TitPr_Interes" as interes, 
							"TitPr_Estado" as estado_liquidacion, "TitPr_FechaRecaudacion" as fecha_pago, 
						       "TitPr_TasaAdministrativa" as tasa_administrativa, "TitPr_RebajaHipotec" as hipoteca, "TitPr_BaseImponible" as base_imponible, 
						       "TitPr_ConstObsoleta" AS construccion_obsoleta, "TitPr_TituloGral", "TitPr_SNERecargo"  as sne_recargo, 
						       "TitPr_Observaciones" as observaciones, "Usu_usuario" as usuario, "Titpr_RUC_CI" as ci, "TitPr_DireccionCont" as direccion_cont, 
						       "TitPr_Nombres" as nombre_comprador, "Titpr_Propietario" as propietario, "TitPr_Tipo" as tipo, "TitPr_Valor1" as basura, 
						       "TitPr_Valor2" as valor_2,
						       SUBSTRING("TitPr_NumTitulo", 1, 4)::INT AS anio, 
						       SUBSTRING("TitPr_NumTitulo", 6, 6)::BIGINT AS num_liquidacion, 
						       SUBSTRING("TitPr_NumTitulo", 13, 2) AS tipo_predio
										       
						       FROM MIGRACION."TITULOS_PREDIO" ESTADO 
						       
						       LEFT OUTER JOIN sgm_app.CAT_PREDIO P ON P.PREDIALANT = ESTADO."Pre_CodigoCatastral"
						       LEFT OUTER JOIN sgm_app.cat_predio_rustico pr ON ESTADO."Pre_CodigoCatastral" = pr.reg_catastral;
						       



			       
BEGIN
	FOR T IN T_TITULO_PREDIAL LOOP

	predioUrbano = null;
	predioRural = null;

	IF((SELECT COUNT(*) FROM sgm_app.cat_ente ente WHERE ci_ruc = T.ci)>=1 ) THEN
		comprador = (SELECT id FROM sgm_app.cat_ente WHERE ci_ruc = T.ci);	
	ELSE
		comprador = NULL;
	END IF;
	IF( T.bomberos > 0 ) THEN
		bomberoexist = TRUE;	
	ELSE
		bomberoexist = FALSE;	
	END IF;

	IF T.estado_liquidacion = 'C' THEN estadoLiquidacion = 1; estado_pago = true; saldo = 0.00; END IF;
	IF T.estado_liquidacion = 'E' THEN estadoLiquidacion = 2; estado_pago = false; saldo = T.total; END IF;
	IF T.estado_liquidacion = 'Q' THEN estadoLiquidacion = 3; estado_pago = false; saldo = T.total; END IF;
	IF T.estado_liquidacion = 'N' THEN estadoLiquidacion = 4; estado_pago = false; saldo = T.total; END IF;
		
	IF(T.tipo = 'Urbano') THEN tipoLiquidacion = 13; predioUrbano = T.predio; predioRural = null; END IF;	
	IF(T.tipo = 'Rural') THEN tipoLiquidacion = 7; predioRural = T.predio_rustico; predioUrbano = null; END IF;	
	--DIFERENCIA PARA REN PAGO
	IF (T.diferencia = 1 ) THEN observacion := 'EL PAGO TUVO UNA DIFERENCIA DE ' || T.diferencia ||' FRENTE AL TOTAL EMITIDO'; END IF;	
	IF (T.diferencia = 0 ) THEN observacion = ''; END IF;	
	IF (T.diferencia = -1 ) THEN observacion := 'EL PAGO TUVO UNA DIFERENCIA DE ' || T.diferencia || ' FRENTE AL TOTAL EMITIDO'; END IF;
	
	
        INSERT INTO sgm_FINANCIERO.REN_LIQUIDACION( 
            NUM_LIQUIDACION, ID_LIQUIDACION, 
            TIPO_LIQUIDACION, TOTAL_PAGO, 
	    FECHA_INGRESO, COMPRADOR, FECHA_CONTRATO_ANT, 
            ESTADO_LIQUIDACION, PREDIO, 
            OBSERVACION, ANIO, 
	    VALOR_COMERCIAL, VALOR_HIPOTECA, 
            AVALUO_MUNICIPAL, AVALUO_CONSTRUCCION, AVALUO_SOLAR, BOMBERO, 
            NOMBRE_COMPRADOR, estado_coactiva, saldo, predio_rustico)
   
      VALUES ( T.num_liquidacion, T.num_titulo, 
               tipoLiquidacion, T.total, T.fecha_emision, comprador, 
               T.fecha_emision,  estadoLiquidacion, 
               predioUrbano, T.observaciones, T.anio, 
               T.val_comer_predio, T.hipoteca, 
               T.valor_edif_predio + T.valor_terr_predio, T.valor_edif_predio, T.valor_terr_predio, 
               bomberoexist,	
               T.nombre_comprador, 1, saldo, predioRural)

               
	RETURNING id INTO ID_REN_LIQUIDACION;	


	IF (T.estado_liquidacion = 'C' OR T.estado_liquidacion = 'Q' ) THEN

	--REN PAGO
	INSERT INTO sgm_FINANCIERO.ren_pago(
            fecha_pago, liquidacion, valor, estado, 
            descuento, recargo, interes, observacion, contribuyente, nombre_contribuyente)
	    VALUES (T.fecha_pago, ID_REN_LIQUIDACION, T.pago, estado_pago, T.descuent,
		   T.recargo, T.interes, observacion, comprador, T.nombre_comprador)
	RETURNING id INTO ID_PAGO;

	--ACTUALIZA EL NUMERO DEL COMPROBANTE EN LA TABLA REN LIQUIDACION
	UPDATE sgm_FINANCIERO.ren_liquidacion
	   SET num_comprobante= (SELECT num_comprobante FROM sgm_FINANCIERO.ren_pago WHERE id =  ID_PAGO)
	 WHERE ID = ID_REN_LIQUIDACION;

		--PAGO DETALLE
		INSERT INTO sgm_FINANCIERO.ren_pago_detalle(
		    tipo_pago, pago, valor)
		VALUES (1, ID_PAGO, T.pago);

		--REN DET LIQUIDACION Y REN PAGO RUBRO 
		--PREDIOS RURALES 
		IF(T.tipo_predio = 'PU') THEN
			IF(T.ipu > 0) THEN
				rubro =  2;
				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.ipu);

				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);	
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN
			IF(T.solar_no_edificado > 0) THEN
				rubro =  4;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.solar_no_edificado, TRUE, T.solar_no_edificado);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.solar_no_edificado);    
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.bomberos > 0) THEN
				rubro =  7;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.bomberos);

			END IF;
		END IF;
		
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.basura > 0) THEN
				rubro =  8;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, T.basura);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.basura);

			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.tasa_administrativa > 0) THEN
			
				rubro =  3;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.sne_recargo > 0) THEN
			
				rubro =  5;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.sne_recargo, TRUE, T.sne_recargo);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.sne_recargo);
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.construccion_obsoleta > 0) THEN
			
				rubro =  6;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.construccion_obsoleta, TRUE, T.construccion_obsoleta);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.construccion_obsoleta);
			END IF;
		END IF;
		
		--PREDIOS RURALES
		
		IF(T.tipo_predio = 'PR') THEN	
			IF(T.ipu > 0) THEN
				rubro = 18;
				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
						pago, rubro, valor)
					    VALUES (ID_PAGO, rubro, 0.00);

					INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
					liquidacion, rubro, valor, estado, 
					valor_recaudado)
					    
					VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);
			END IF;
		END IF;
		
		IF(T.tipo_predio = 'PR') THEN	
			IF(T.tasa_administrativa > 0) THEN
			
				rubro =  23;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
			END IF;
		END IF;

		IF(T.tipo_predio = 'PR') THEN	
			IF(T.bomberos > 0) THEN
				rubro =  21;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.bomberos);

			END IF;
		END IF;
	
	END IF; 
--insercion de rubros en det liquidacion cuando esta no esta pagada SOLO EN REN DET LIQUIDACION 
	IF(T.estado_liquidacion = 'E') THEN 
		IF(T.tipo_predio = 'PU') THEN
			IF(T.ipu > 0) THEN
				rubro =  2;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);	
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN
			IF(T.solar_no_edificado > 0) THEN
				rubro =  4;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.solar_no_edificado, TRUE, 0.00);
 
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.bomberos > 0) THEN
				rubro =  7;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);


			END IF;
		END IF;
		
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.basura > 0) THEN
				rubro =  8;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, 0.00);

				

			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
		IF(T.tasa_administrativa > 0) THEN
			rubro =  3;
			
			INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
			     liquidacion, rubro, valor, estado, 
			    valor_recaudado)
			    
			    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);
		END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.sne_recargo > 0) THEN
			
				rubro =  5;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.sne_recargo, TRUE, 0.00);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.sne_recargo);
			END IF;
		END IF;
		IF(T.tipo_predio = 'PU') THEN	
			IF(T.construccion_obsoleta > 0) THEN
			
				rubro =  6;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.construccion_obsoleta, TRUE, 0.00);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.construccion_obsoleta);
			END IF;
		END IF;
--PREDIOS RURALES
		IF(T.tipo_predio = 'PR') THEN	
			IF(T.tasa_administrativa > 0) THEN
				rubro =  23;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);

			END IF;
		END IF;
		IF(T.tipo_predio = 'PR') THEN	
			IF(T.ipu > 0) THEN
				rubro = 18;
			

				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);

			END IF;
		END IF;

		IF(T.tipo_predio = 'PR') THEN	
			IF(T.bomberos > 0) THEN
				rubro =  21;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);

			END IF;
		END IF;
	
	END IF;
		
	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE;


/*

ALTER TABLE sgm_FINANCIERO.ren_liquidacion ADD COLUMN migrado boolean;


SELECT * FROM migracion."TITULOS_PREDIO" WHERE  "TitPr_IPU" > 0.00 AND "TitPr_Tipo" = 'Rural'

select * from sgm_FINANCIERO.ren_pago where liquidacion in(
select id from sgm_FINANCIERO.ren_liquidacion where estado_liquidacion = 3) ORDER BY ID

SELECT * FROM sgm_FINANCIERO.ren_liquidacion where predio_rustico is  null and predio is null
SELECT * FROM sgm_FINANCIERO.ren_pago WHERE observacion  like '%EL %'
SELECT * FROM sgm_FINANCIERO.ren_pago_rubro
SELECT * FROM sgm_FINANCIERO.ren_det_liquidacion WHERE rubro = 18

DELETE from sgm_FINANCIERO.ren_pago
DELETE FROM sgm_FINANCIERO.ren_pago_detalle
DELETE FROM sgm_FINANCIERO.ren_pago_rubro
DELETE FROM sgm_FINANCIERO.ren_liquidacion
DELETE FROM sgm_FINANCIERO.ren_det_liquidacion

TRUNCATE

SELECT  distinct det.rubro, sum(det.valor_recaudado) FROM sgm_FINANCIERO.ren_liquidacion liq
INNER JOIN sgm_FINANCIERO.ren_det_liquidacion det ON liq.id = det.liquidacion 
where liq.tipo_liquidacion = 7
GROUP BY det.rubro

select det.* FROM sgm_FINANCIERO.ren_liquidacion liq
INNER JOIN sgm_FINANCIERO.ren_det_liquidacion det ON liq.id = det.liquidacion 
where liq.estado_liquidacion = 2

 TRUNCATE sgm_FINANCIERO.ren_liquidacion  restart identity cascade; 
select sgm_FINANCIERO.insertarLiquidacion()

 
--"020550010225001000"
 -- select * from sgm_app.cat_ente*/
 
 
 CREATE OR REPLACE FUNCTION sgm_FINANCIERO.insertarLiquidacionCarteraVencida()
  RETURNS void AS
$BODY$
DECLARE comprador BIGINT;
DECLARE ID_REN_LIQUIDACION BIGINT;
DECLARE ID_PAGO BIGINT;
DECLARE bomberoexist BOOLEAN;
DECLARE rubro BIGINT;
DECLARE estadoLiquidacion BIGINT;
DECLARE tipoLiquidacion BIGINT;
DECLARE observacion TEXT;
DECLARE estado_pago BOOLEAN;
DECLARE saldo DECIMAL;
DECLARE predioUrbano BIGINT;
DECLARE predioRural BIGINT;
DECLARE 
--SE PUSO UNA COUMNA DE DIREENCIAA XK EN ALGUNAS LIQUIDACIONES EL PAGO ERA MENOR O MAYOR AL TOTAL EMITIDO Y NO SE PUDO IDDENTIFICAR SI ERA UN ABONO.
--CURSORES
		T_TITULO_PREDIAL CURSOR FOR 
					SELECT 	       P.ID AS predio , PR.id as predio_rustico,
							"Pre_CodigoCatastral" AS pre_codigo_catastral, "CarVe_NumTitulo" AS num_titulo, "CarVe_ValTotalTerrPredio" AS valor_terr_predio,  
						       "CarVe_ValTotalEdifPredio" AS valor_edif_predio , "CarVe_ValCultivos" AS val_cultivo, "CarVe_ValForestales" AS val_forestal, 
						       "CarVe_ValObrasInter" AS val_obras_inter, "CarVe_ValOtrasInver" AS otras_liquidaciones, "CarVe_ValComerPredio" AS val_comer_predio, 
						       "CarVe_IPU" AS ipu  , "CarVe_SolNoEdif" AS solar_no_edificado, "CarVe_Bomberos" AS bomberos, "CarVe_FechaEmision" AS fecha_emision, 
						       "CarVe_ValorEmitido" as total, "CarVe_ValorTCobrado" as pago,
						       "CarVe_ValorEmitido"- ("CarVe_ValorTCobrado" - "CarVe_Interes") AS diferencia,
							"CarVe_Interes" as interes, 
							"CarVe_Estado" as estado_liquidacion, "CarVe_FechaRecaudacion" as fecha_pago, 
						       "CarVe_TasaAdministrativa" as tasa_administrativa, "CarVe_RebajaHipotec" as hipoteca, "CarVe_BaseImponible" as base_imponible, 
						       "CarVe_ConstObsoleta" AS construccion_obsoleta, "CarVe_TituloGral", "CarVe_SNERecargo"  as sne_recargo, 
						       "CarVe_Observaciones" as observaciones, "Usu_usuario" as usuario, "CarVe_CI" as ci, "CarVe_direccPropietario" as direccion_cont, 
						       "CarVe_Nombres" as nombre_comprador, "CarVe_Nombres" as propietario, "Carve_Valor1" as basura, 
						       "Carve_Valor2" as valor_2,
						       SUBSTRING("CarVe_NumTitulo", 1, 4)::INT AS anio, 
						       SUBSTRING("CarVe_NumTitulo", 6, 6)::BIGINT AS num_liquidacion, 
						       SUBSTRING("CarVe_NumTitulo", 13, 2) AS tipo_predio
										       
						       FROM MIGRACION."CARTERA_VENCIDA" ESTADO 
						       
						       LEFT OUTER JOIN sgm_app.CAT_PREDIO P ON P.PREDIALANT = ESTADO."Pre_CodigoCatastral"
						       LEFT OUTER JOIN sgm_app.cat_predio_rustico pr ON ESTADO."Pre_CodigoCatastral" = pr.reg_catastral;
						       



			       
BEGIN
	FOR T IN T_TITULO_PREDIAL LOOP

	predioUrbano = null;
	predioRural = null;

	IF((SELECT COUNT(*) FROM sgm_app.cat_ente ente WHERE ci_ruc = T.ci)>=1 ) THEN
		comprador = (SELECT id FROM sgm_app.cat_ente WHERE ci_ruc = T.ci);	
	ELSE
		comprador = NULL;
	END IF;
	IF( T.bomberos > 0 ) THEN
		bomberoexist = TRUE;	
	ELSE
		bomberoexist = FALSE;	
	END IF;

	IF T.estado_liquidacion = 'C' THEN estadoLiquidacion = 1; estado_pago = true; saldo = 0.00; END IF;
	IF T.estado_liquidacion = 'E' THEN estadoLiquidacion = 2; estado_pago = false; saldo = T.total; END IF;
	IF T.estado_liquidacion = 'Q' THEN estadoLiquidacion = 3; estado_pago = false; saldo = T.total; END IF;
	IF T.estado_liquidacion = 'N' THEN estadoLiquidacion = 4; estado_pago = false; saldo = T.total; END IF;
		
	IF(T.predio IS NOT NULL) THEN tipoLiquidacion = 13; predioUrbano = T.predio; predioRural = null; END IF;	
	IF(T.predio_rustico IS NOT NULL) THEN tipoLiquidacion = 7; predioRural = T.predio_rustico; predioUrbano = null; END IF;	
	--DIFERENCIA PARA REN PAGO
	IF (T.diferencia = 1 ) THEN observacion := 'EL PAGO TUVO UNA DIFERENCIA DE ' || T.diferencia ||' FRENTE AL TOTAL EMITIDO'; END IF;	
	IF (T.diferencia = 0 ) THEN observacion = ''; END IF;	
	IF (T.diferencia = -1 ) THEN observacion := 'EL PAGO TUVO UNA DIFERENCIA DE ' || T.diferencia || ' FRENTE AL TOTAL EMITIDO'; END IF;
	
	
        INSERT INTO sgm_FINANCIERO.REN_LIQUIDACION( 
            NUM_LIQUIDACION, ID_LIQUIDACION, 
            TIPO_LIQUIDACION, TOTAL_PAGO, 
	    FECHA_INGRESO, COMPRADOR, FECHA_CONTRATO_ANT, 
            ESTADO_LIQUIDACION, PREDIO, 
            OBSERVACION, ANIO, 
	    VALOR_COMERCIAL, VALOR_HIPOTECA, 
            AVALUO_MUNICIPAL, AVALUO_CONSTRUCCION, AVALUO_SOLAR, BOMBERO, 
            NOMBRE_COMPRADOR, estado_coactiva, saldo, predio_rustico)
   
      VALUES ( T.num_liquidacion, T.num_titulo, 
               tipoLiquidacion, T.total, T.fecha_emision, comprador, 
               T.fecha_emision,  estadoLiquidacion, 
               predioUrbano, T.observaciones, T.anio, 
               T.val_comer_predio, T.hipoteca, 
               T.valor_edif_predio + T.valor_terr_predio, T.valor_edif_predio, T.valor_terr_predio, 
               bomberoexist,	
               T.nombre_comprador, 1, saldo, predioRural)

               
	RETURNING id INTO ID_REN_LIQUIDACION;	


	IF (T.estado_liquidacion = 'C' OR T.estado_liquidacion = 'Q' ) THEN

	--REN PAGO
	INSERT INTO sgm_FINANCIERO.ren_pago(
            fecha_pago, liquidacion, valor, estado, 
            descuento, recargo, interes, observacion, contribuyente, nombre_contribuyente)
	    VALUES (T.fecha_pago, ID_REN_LIQUIDACION, T.pago, estado_pago, 0.00,
		   0.00, T.interes, observacion, comprador, T.nombre_comprador)
	RETURNING id INTO ID_PAGO;

	--ACTUALIZA EL NUMERO DEL COMPROBANTE EN LA TABLA REN LIQUIDACION
	UPDATE sgm_FINANCIERO.ren_liquidacion
	   SET num_comprobante= (SELECT num_comprobante FROM sgm_FINANCIERO.ren_pago WHERE id =  ID_PAGO)
	 WHERE ID = ID_REN_LIQUIDACION;

		--PAGO DETALLE
		INSERT INTO sgm_FINANCIERO.ren_pago_detalle(
		    tipo_pago, pago, valor)
		VALUES (1, ID_PAGO, T.pago);

		--REN DET LIQUIDACION Y REN PAGO RUBRO 
		--PREDIOS 
		IF(T.predio IS NOT NULL) THEN
			IF(T.ipu > 0) THEN
				rubro =  2;
				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.ipu);

				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);	
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN
			IF(T.solar_no_edificado > 0) THEN
				rubro =  4;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.solar_no_edificado, TRUE, T.solar_no_edificado);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.solar_no_edificado);    
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  7;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.bomberos);

			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.basura > 0) THEN
				rubro =  8;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, T.basura);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.basura);

			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.tasa_administrativa > 0) THEN
			
				rubro =  3;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.sne_recargo > 0) THEN
			
				rubro =  5;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.sne_recargo, TRUE, T.sne_recargo);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.sne_recargo);
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.construccion_obsoleta > 0) THEN
			
				rubro =  6;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.construccion_obsoleta, TRUE, T.construccion_obsoleta);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.construccion_obsoleta);
			END IF;
		END IF;
		
		--PREDIOS RURALES
		
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.ipu > 0) THEN
				rubro = 18;
				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
						pago, rubro, valor)
					    VALUES (ID_PAGO, rubro, 0.00);

					INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
					liquidacion, rubro, valor, estado, 
					valor_recaudado)
					    
					VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);
			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.tasa_administrativa > 0) THEN
			
				rubro =  23;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  21;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.bomberos);

			END IF;
		END IF;
	
	END IF; 
--insercion de rubros en det liquidacion cuando esta no esta pagada SOLO EN REN DET LIQUIDACION 
	IF(T.estado_liquidacion = 'E') THEN 
		IF(T.predio IS NOT NULL) THEN
			IF(T.ipu > 0) THEN
				rubro =  2;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);	
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN
			IF(T.solar_no_edificado > 0) THEN
				rubro =  4;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.solar_no_edificado, TRUE, 0.00);
 
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  7;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);


			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.basura > 0) THEN
				rubro =  8;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, 0.00);

				

			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
		IF(T.tasa_administrativa > 0) THEN
			rubro =  3;
			
			INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
			     liquidacion, rubro, valor, estado, 
			    valor_recaudado)
			    
			    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);

			
		END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.sne_recargo > 0) THEN
			
				rubro =  5;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.sne_recargo, TRUE, 0.00);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.sne_recargo);
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.construccion_obsoleta > 0) THEN
			
				rubro =  6;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.construccion_obsoleta, TRUE, 0.00);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.construccion_obsoleta);
			END IF;
		END IF;
		
		--PREDIOS RURALES
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.tasa_administrativa > 0) THEN
				rubro =  23;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);

			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.ipu > 0) THEN
				rubro = 18;
			

				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);

			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  21;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);

			END IF;
		END IF;
		
	END IF;
		
	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE;

 
 CREATE OR REPLACE FUNCTION sgm_FINANCIERO.insertarLiquidacionCarteraVencida()
  RETURNS void AS
$BODY$
DECLARE comprador BIGINT;
DECLARE ID_REN_LIQUIDACION BIGINT;
DECLARE ID_PAGO BIGINT;
DECLARE bomberoexist BOOLEAN;
DECLARE rubro BIGINT;
DECLARE estadoLiquidacion BIGINT;
DECLARE tipoLiquidacion BIGINT;
DECLARE observacion TEXT;
DECLARE estado_pago BOOLEAN;
DECLARE saldo DECIMAL;
DECLARE predioUrbano BIGINT;
DECLARE predioRural BIGINT;
DECLARE 
--SE PUSO UNA COUMNA DE DIREENCIAA XK EN ALGUNAS LIQUIDACIONES EL PAGO ERA MENOR O MAYOR AL TOTAL EMITIDO Y NO SE PUDO IDDENTIFICAR SI ERA UN ABONO.
--CURSORES
		/*
    parroquia character varying(50) COLLATE pg_catalog."default",
    direccion character varying(100) COLLATE pg_catalog."default",
     numeric(18, 2) NOT NULL,
    ser_adminis numeric(18, 2) NOT NULL,
    total numeric(18, 2),
    descuento numeric(18, 2),
    interes numeric(18, 2),
    tcobrado numeric(18, 2),
    estado character varying(1) COLLATE pg_catalog."default" NOT NULL,
    fecha_recaudacion timestamp without time zone,
    observaciones character varying(500) COLLATE pg_catalog."default",
    seleccion smallint NOT NULL,
    tipo character varying(50) COLLATE pg_catalog."default" NOT NULL,    
		*/
		T_TITULO_PREDIAL CURSOR FOR 
					SELECT 	       P.ID AS predio , PR.id as predio_rustico,
							clave_catastral AS pre_codigo_catastral, num_tit AS num_titulo, aterreno AS valor_terr_predio,  
						       aconstruccion AS valor_edif_predio , "CarVe_ValCultivos" AS val_cultivo, "CarVe_ValForestales" AS val_forestal, 
						       "CarVe_ValObrasInter" AS val_obras_inter, "CarVe_ValOtrasInver" AS otras_liquidaciones, acomercial AS val_comer_predio, 
						       impuestop AS ipu  , "CarVe_SolNoEdif" AS solar_no_edificado, bomberos AS bomberos, fecha_emision AS fecha_emision, 
						       "CarVe_ValorEmitido" as total, "CarVe_ValorTCobrado" as pago,
						       "CarVe_ValorEmitido"- ("CarVe_ValorTCobrado" - "CarVe_Interes") AS diferencia,
							"CarVe_Interes" as interes, 
							"CarVe_Estado" as estado_liquidacion, "CarVe_FechaRecaudacion" as fecha_pago, 
						       "CarVe_TasaAdministrativa" as tasa_administrativa, "CarVe_RebajaHipotec" as hipoteca, "CarVe_BaseImponible" as base_imponible, 
						       "CarVe_ConstObsoleta" AS construccion_obsoleta, "CarVe_TituloGral", "CarVe_SNERecargo"  as sne_recargo, 
						       "CarVe_Observaciones" as observaciones, usuario as usuario, cedula as ci, "CarVe_direccPropietario" as direccion_cont, 
						       COALESCE(TRIM(apellidos), '') || ' ' || COALESCE(TRIM(nombres), '') as nombre_comprador, basura as basura, 
						       "Carve_Valor2" as valor_2,
						       "año_emision" AS anio, 
						       SUBSTRING(num_tit, 6, 4)::BIGINT AS num_liquidacion, 
										       
						       FROM MIGRACION."TituloP" ESTADO 
						       
						       LEFT OUTER JOIN sgm_app.CAT_PREDIO P ON P.PREDIALANT = ESTADO.clave_catastral;
			       
BEGIN
	FOR T IN T_TITULO_PREDIAL LOOP

	predioUrbano = null;
	predioRural = null;

	IF((SELECT COUNT(*) FROM sgm_app.cat_ente ente WHERE ci_ruc = T.ci)>=1 ) THEN
		comprador = (SELECT id FROM sgm_app.cat_ente WHERE ci_ruc = T.ci);	
	ELSE
		comprador = NULL;
	END IF;
	IF( T.bomberos > 0 ) THEN
		bomberoexist = TRUE;	
	ELSE
		bomberoexist = FALSE;	
	END IF;

	IF T.estado_liquidacion = 'C' THEN estadoLiquidacion = 1; estado_pago = true; saldo = 0.00; END IF;
	IF T.estado_liquidacion = 'E' THEN estadoLiquidacion = 2; estado_pago = false; saldo = T.total; END IF;
	IF T.estado_liquidacion = 'Q' THEN estadoLiquidacion = 3; estado_pago = false; saldo = T.total; END IF;
	IF T.estado_liquidacion = 'N' THEN estadoLiquidacion = 4; estado_pago = false; saldo = T.total; END IF;
		
	IF(T.predio IS NOT NULL) THEN tipoLiquidacion = 13; predioUrbano = T.predio; predioRural = null; END IF;	
	IF(T.predio_rustico IS NOT NULL) THEN tipoLiquidacion = 7; predioRural = T.predio_rustico; predioUrbano = null; END IF;	
	--DIFERENCIA PARA REN PAGO
	IF (T.diferencia = 1 ) THEN observacion := 'EL PAGO TUVO UNA DIFERENCIA DE ' || T.diferencia ||' FRENTE AL TOTAL EMITIDO'; END IF;	
	IF (T.diferencia = 0 ) THEN observacion = ''; END IF;	
	IF (T.diferencia = -1 ) THEN observacion := 'EL PAGO TUVO UNA DIFERENCIA DE ' || T.diferencia || ' FRENTE AL TOTAL EMITIDO'; END IF;
	
	
        INSERT INTO sgm_FINANCIERO.REN_LIQUIDACION( 
            NUM_LIQUIDACION, ID_LIQUIDACION, 
            TIPO_LIQUIDACION, TOTAL_PAGO, 
	    FECHA_INGRESO, COMPRADOR, FECHA_CONTRATO_ANT, 
            ESTADO_LIQUIDACION, PREDIO, 
            OBSERVACION, ANIO, 
	    VALOR_COMERCIAL, VALOR_HIPOTECA, 
            AVALUO_MUNICIPAL, AVALUO_CONSTRUCCION, AVALUO_SOLAR, BOMBERO, 
            NOMBRE_COMPRADOR, estado_coactiva, saldo, predio_rustico)
   
      VALUES ( T.num_liquidacion, T.num_titulo, 
               tipoLiquidacion, T.total, T.fecha_emision, comprador, 
               T.fecha_emision,  estadoLiquidacion, 
               predioUrbano, T.observaciones, T.anio, 
               T.val_comer_predio, T.hipoteca, 
               T.valor_edif_predio + T.valor_terr_predio, T.valor_edif_predio, T.valor_terr_predio, 
               bomberoexist,	
               T.nombre_comprador, 1, saldo, predioRural)

               
	RETURNING id INTO ID_REN_LIQUIDACION;	


	IF (T.estado_liquidacion = 'C' OR T.estado_liquidacion = 'Q' ) THEN

	--REN PAGO
	INSERT INTO sgm_FINANCIERO.ren_pago(
            fecha_pago, liquidacion, valor, estado, 
            descuento, recargo, interes, observacion, contribuyente, nombre_contribuyente)
	    VALUES (T.fecha_pago, ID_REN_LIQUIDACION, T.pago, estado_pago, 0.00,
		   0.00, T.interes, observacion, comprador, T.nombre_comprador)
	RETURNING id INTO ID_PAGO;

	--ACTUALIZA EL NUMERO DEL COMPROBANTE EN LA TABLA REN LIQUIDACION
	UPDATE sgm_FINANCIERO.ren_liquidacion
	   SET num_comprobante= (SELECT num_comprobante FROM sgm_FINANCIERO.ren_pago WHERE id =  ID_PAGO)
	 WHERE ID = ID_REN_LIQUIDACION;

		--PAGO DETALLE
		INSERT INTO sgm_FINANCIERO.ren_pago_detalle(
		    tipo_pago, pago, valor)
		VALUES (1, ID_PAGO, T.pago);

		--REN DET LIQUIDACION Y REN PAGO RUBRO 
		--PREDIOS 
		IF(T.predio IS NOT NULL) THEN
			IF(T.ipu > 0) THEN
				rubro =  2;
				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.ipu);

				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);	
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN
			IF(T.solar_no_edificado > 0) THEN
				rubro =  4;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.solar_no_edificado, TRUE, T.solar_no_edificado);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.solar_no_edificado);    
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  7;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.bomberos);

			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.basura > 0) THEN
				rubro =  8;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, T.basura);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.basura);

			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.tasa_administrativa > 0) THEN
			
				rubro =  3;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.sne_recargo > 0) THEN
			
				rubro =  5;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.sne_recargo, TRUE, T.sne_recargo);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.sne_recargo);
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.construccion_obsoleta > 0) THEN
			
				rubro =  6;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.construccion_obsoleta, TRUE, T.construccion_obsoleta);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.construccion_obsoleta);
			END IF;
		END IF;
		
		--PREDIOS RURALES
		
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.ipu > 0) THEN
				rubro = 18;
				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
						pago, rubro, valor)
					    VALUES (ID_PAGO, rubro, 0.00);

					INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
					liquidacion, rubro, valor, estado, 
					valor_recaudado)
					    
					VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);
			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.tasa_administrativa > 0) THEN
			
				rubro =  23;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  21;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(
					pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.bomberos);

			END IF;
		END IF;
	
	END IF; 
--insercion de rubros en det liquidacion cuando esta no esta pagada SOLO EN REN DET LIQUIDACION 
	IF(T.estado_liquidacion = 'E') THEN 
		IF(T.predio IS NOT NULL) THEN
			IF(T.ipu > 0) THEN
				rubro =  2;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);	
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN
			IF(T.solar_no_edificado > 0) THEN
				rubro =  4;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.solar_no_edificado, TRUE, 0.00);
 
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  7;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);


			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.basura > 0) THEN
				rubro =  8;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, 0.00);

				

			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
		IF(T.tasa_administrativa > 0) THEN
			rubro =  3;
			
			INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
			     liquidacion, rubro, valor, estado, 
			    valor_recaudado)
			    
			    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);

			
		END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.sne_recargo > 0) THEN
			
				rubro =  5;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.sne_recargo, TRUE, 0.00);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.sne_recargo);
			END IF;
		END IF;
		IF(T.predio IS NOT NULL) THEN	
			IF(T.construccion_obsoleta > 0) THEN
			
				rubro =  6;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado,valor_recaudado)
				    VALUES (ID_REN_LIQUIDACION, rubro, T.construccion_obsoleta, TRUE, 0.00);

				INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
				    VALUES (ID_PAGO, rubro, T.construccion_obsoleta);
			END IF;
		END IF;
		
		--PREDIOS RURALES
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.tasa_administrativa > 0) THEN
				rubro =  23;
				
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);

			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.ipu > 0) THEN
				rubro = 18;
			

				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				liquidacion, rubro, valor, estado, 
				valor_recaudado)
				    
				VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);

			END IF;
		END IF;
		IF(T.predio_rustico IS NOT NULL) THEN	
			IF(T.bomberos > 0) THEN
				rubro =  21;
				INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
				     liquidacion, rubro, valor, estado, 
				    valor_recaudado)
				    
				    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);

			END IF;
		END IF;
		
	END IF;
		
	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE;  
  
/*
TRUNCATE sgm_FINANCIERO.ren_liquidacion  restart identity cascade; 

SELECT sgm_FINANCIERO.insertarLiquidacionCarteraVencida();

select sgm_FINANCIERO.insertarLiquidacion();

*/
