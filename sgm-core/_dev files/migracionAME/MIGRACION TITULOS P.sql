--UPDATE MIGRACION."TituloP"  SET num_tit = ' 2010-0001-PP' WHERE num_tit = '2010-0001-PP' AND clave_catastral = '0205500001'

CREATE OR REPLACE FUNCTION sgm_FINANCIERO.insertarTitulosParroquia()
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
DECLARE recargo DECIMAL;
DECLARE predioUrbano BIGINT;
DECLARE predioRural BIGINT;
DECLARE 
--SE PUSO UNA COUMNA DE DIREENCIAA XK EN ALGUNAS LIQUIDACIONES EL PAGO ERA MENOR O MAYOR AL TOTAL EMITIDO Y NO SE PUDO IDDENTIFICAR SI ERA UN ABONO.
--CURSORES
        T_TITULO_PREDIAL CURSOR FOR 
                       SELECT P.ID predio,
			clave_catastral AS pre_codigo_catastral, num_tit AS num_titulo, aterreno AS valor_terr_predio,  
                        aconstruccion AS valor_edif_predio , acomercial AS val_comer_predio, impuestop AS ipu  , 
                        bomberos AS bomberos, fecha_emision AS fecha_emision, total as total, tcobrado as pago,
			total- (tcobrado - interes + descuento) AS diferencia,
			interes as interes,  descuento as descuento,
			ESTADO.estado as estado_liquidacion, fecha_recaudacion as fecha_pago, 
                        ser_adminis as tasa_administrativa, 
                        ESTADO.observaciones as observaciones, usuario as usuario, cedula as ci, direccion as direccion_cont, 
                        COALESCE(TRIM(apellidos), '') || ' ' || COALESCE(TRIM(nombres), '') as nombre_comprador, basura as basura, 
                        "año_emision" AS anio, 
                        SUBSTRING(num_tit, 7, 4)::BIGINT AS num_liquidacion
                                               
                        FROM MIGRACION."TituloP" ESTADO 
                        LEFT OUTER JOIN sgm_app.CAT_PREDIO P ON P.PREDIALANT = ESTADO.clave_catastral; -- WHERE num_tit = ' 2010-0076-PP';

                        
BEGIN
    FOR T IN T_TITULO_PREDIAL LOOP

     recargo = 0.00; saldo = 0.00;
    --HUBIERON PAGOS CON FECHA NULOS Y TAMBIEN LIQUIDACIONES MEDIAS RARAS QUE APARECEN QUE YA ESTAN PAGADAS PERO SIGUEN EN ESTADO N =(
    if (T.fecha_pago is null ) THEN T.fecha_pago = T.fecha_emision; END IF;
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

    IF T.estado_liquidacion = 'P' THEN estadoLiquidacion = 1; estado_pago = true; END IF;
    IF T.estado_liquidacion = 'N' THEN 
	IF (T.total < T.pago) THEN 
		estadoLiquidacion = 1; estado_pago = TRUE;
	ELSE
		estadoLiquidacion = 2; estado_pago = FALSE;
	END IF; 
    END IF;
    IF T.estado_liquidacion = 'Q' THEN estadoLiquidacion = 3; estado_pago = false;  END IF;
    IF T.estado_liquidacion = 'B' THEN estadoLiquidacion = 4; estado_pago = false;  END IF;

    --PARA CUANDO NO SE HAYA PAGADO NAADA 
    IF (T.diferencia = T.total) THEN recargo = 0.00;  saldo = T.total; END IF;
    --PARA CUANDO SE HAYA PAGADO TODO SIN RECARGO 
    IF (T.diferencia = 0.00) THEN recargo = 0.00;  saldo = 0.00; END IF;
    --PARA CUANDO SE HAYA PAGADO TODO
    IF (T.total > T.diferencia ) THEN recargo = T.diferencia * -1;  saldo = 0.00; END IF;
    --PARA CUANDO NO HAYA NINGUN PAGO POR SI LAS MOSCAS
    IF (T.pago = 0.00) THEN recargo = 0.00; saldo = T.total; END IF;    
    IF (T.pago < T.total AND T.total <> (T.pago - t.interes + t.descuento + t.diferencia) ) THEN recargo = 0.00; saldo = T.total; END IF;    
    --ESTE PARA CUANDO HAYAS DATOS NULOS EN TCOBRADO , INTERES O EN DESCUENTO
    IF (T.diferencia IS NULL AND T.estado_liquidacion = 'N') THEN recargo = 0.00;  saldo = T.total; END IF;  
    --ESTE ES UN ABONO
    IF (T.diferencia > 0.00 AND T.diferencia < T.total) THEN recargo = 0.00; saldo = T.diferencia; END IF;
	
    tipoLiquidacion = 13; predioUrbano = T.predio; predioRural = null;
    
        INSERT INTO sgm_FINANCIERO.REN_LIQUIDACION( 
            NUM_LIQUIDACION, ID_LIQUIDACION, 
            TIPO_LIQUIDACION, TOTAL_PAGO, 
        FECHA_INGRESO, COMPRADOR, FECHA_CONTRATO_ANT, 
            ESTADO_LIQUIDACION, PREDIO, 
            OBSERVACION, ANIO, 
        VALOR_COMERCIAL, 
            AVALUO_MUNICIPAL, AVALUO_CONSTRUCCION, AVALUO_SOLAR, BOMBERO, 
            NOMBRE_COMPRADOR, estado_coactiva, saldo, predio_rustico)
   
      VALUES ( T.num_liquidacion, T.num_titulo, 
               tipoLiquidacion, T.total, T.fecha_emision, comprador, 
               T.fecha_emision,  estadoLiquidacion, 
               predioUrbano, (' CLAVE ANTERIOR: '||COALESCE (T.pre_codigo_catastral, '')||' OBSERVACION: '||COALESCE(t.observaciones,'')), T.anio::BIGINT, 
               T.val_comer_predio, 
               T.valor_edif_predio + T.valor_terr_predio, T.valor_edif_predio, T.valor_terr_predio, 
               bomberoexist,    
               T.nombre_comprador, 1, saldo, predioRural)

               
    RETURNING id INTO ID_REN_LIQUIDACION;    
	
IF (T.estado_liquidacion = 'P') THEN

    --REN PAGO
    INSERT INTO sgm_FINANCIERO.ren_pago(
            fecha_pago, liquidacion, valor, estado, 
            descuento, recargo, interes, observacion, contribuyente, nombre_contribuyente)
        VALUES (T.fecha_pago, ID_REN_LIQUIDACION, T.pago, estado_pago, T.descuento,
           recargo, T.interes, T.observaciones, comprador, T.nombre_comprador)
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
        
            IF(T.ipu > 0) THEN
                rubro =  2;
                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.ipu);

                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);    
            END IF;
       
        
            IF(T.bomberos > 0) THEN
                rubro =  7;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.bomberos);

            END IF;
            IF(T.basura > 0) THEN
                rubro =  8;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, T.basura);

                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.basura);

            END IF;
            IF(T.tasa_administrativa > 0) THEN
                rubro =  3;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
            END IF; 
    END IF; 
--insercion de rubros en det liquidacion cuando esta no esta pagada SOLO EN REN DET LIQUIDACION 
    IF(T.estado_liquidacion = 'N') THEN 
            IF(T.ipu > 0) THEN
                rubro =  2;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                liquidacion, rubro, valor, estado, 
                valor_recaudado)
                VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);    
            END IF;
            IF(T.bomberos > 0) THEN
                rubro =  7;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                     liquidacion, rubro, valor, estado, 
                    valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);
            END IF;
            IF(T.basura > 0) THEN
                rubro =  8;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                     liquidacion, rubro, valor, estado, 
                    valor_recaudado)
                    
                    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, 0.00);
            END IF;
        IF(T.tasa_administrativa > 0) THEN
            rubro =  3;
            
            INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                 liquidacion, rubro, valor, estado, 
                valor_recaudado)
                
                VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);    
        END IF;
    END IF;
    
--INSERCION PARA LAS DADAS DE BAJA Y TIENEN PAGO
IF (T.estado_liquidacion = 'B' AND t.pago > 0.00) THEN

    --REN PAGO
    INSERT INTO sgm_FINANCIERO.ren_pago(
            fecha_pago, liquidacion, valor, estado, 
            descuento, recargo, interes, observacion, contribuyente, nombre_contribuyente)
        VALUES (T.fecha_pago, ID_REN_LIQUIDACION, T.pago, estado_pago, T.descuento,
           recargo, T.interes, T.observaciones, comprador, T.nombre_comprador)
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
        
            IF(T.ipu > 0) THEN
                rubro =  2;
                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.ipu);

                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, T.ipu);    
            END IF;
       
        
            IF(T.bomberos > 0) THEN
                rubro =  7;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, T.bomberos);

                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.bomberos);

            END IF;
            IF(T.basura > 0) THEN
                rubro =  8;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, T.basura);

                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.basura);

            END IF;
            IF(T.tasa_administrativa > 0) THEN
                rubro =  3;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(liquidacion, rubro, valor, estado, valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, T.tasa_administrativa);

                INSERT INTO sgm_FINANCIERO.ren_pago_rubro(pago, rubro, valor)
                    VALUES (ID_PAGO, rubro, T.tasa_administrativa);
            END IF; 
    END IF; 
--insercion de LAS LIQUIDAACIONES DADAS DE BAJA Y SIN PAGO
    IF(T.estado_liquidacion = 'B' AND t.pago = 0.00) THEN 
            IF(T.ipu > 0) THEN
                rubro =  2;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                liquidacion, rubro, valor, estado, 
                valor_recaudado)
                VALUES (ID_REN_LIQUIDACION, rubro, T.ipu, TRUE, 0.00);    
            END IF;
            IF(T.bomberos > 0) THEN
                rubro =  7;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                     liquidacion, rubro, valor, estado, 
                    valor_recaudado)
                    VALUES (ID_REN_LIQUIDACION, rubro, T.bomberos, TRUE, 0.00);
            END IF;
            IF(T.basura > 0) THEN
                rubro =  8;
                INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                     liquidacion, rubro, valor, estado, 
                    valor_recaudado)
                    
                    VALUES (ID_REN_LIQUIDACION, rubro, T.basura, TRUE, 0.00);
            END IF;
        IF(T.tasa_administrativa > 0) THEN
            rubro =  3;
            
            INSERT INTO sgm_FINANCIERO.ren_det_liquidacion(
                 liquidacion, rubro, valor, estado, 
                valor_recaudado)
                
                VALUES (ID_REN_LIQUIDACION, rubro, T.tasa_administrativa, TRUE, 0.00);    
        END IF;
    END IF;
    

        
    END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE;  

--
/*
SELECT * FROM MIGRACION."TituloP" WHERE num_tit in ( 
SELECT * FROM sgm_financiero.ren_liquidacion where estado_liquidacion = 2 and saldo = 0.00)
SELECT * FROM sgm_financiero.ren_pago WHERE recargo < 0.00
--SELECT sgm_FINANCIERO.insertarTitulosParroquia();
*/
/* 
DELETE FROM sgm_financiero.ren_pago_rubro  where pago in (SELECT liquidacion FROM sgm_financiero.ren_pago WHERE liquidacion > 139264 );
DELETE FROM sgm_financiero.ren_det_liquidacion where liquidacion > 139264 ;
DELETE FROM sgm_financiero.ren_pago  where liquidacion in (SELECT id FROM sgm_financiero.ren_liquidacion WHERE liquidacion > 139264 );
DELETE FROM sgm_financiero.ren_liquidacion where id > 139264 ;
TRUNCATE TABLE sgm_financiero.ren_liquidacion RESTART IDENTITY CASCADE ;

UPDATE sgm_app.cat_predio_rustico SET parroquia = 2 WHERE reg_catastral LIKE '132251%';
UPDATE sgm_app.cat_predio_rustico SET parroquia = 1 WHERE reg_catastral LIKE '132250%';
*/


