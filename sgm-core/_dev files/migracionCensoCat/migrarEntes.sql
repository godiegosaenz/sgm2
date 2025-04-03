--UPDATE MIGRACION."TituloP"  SET num_tit = ' 2010-0001-PP' WHERE num_tit = '2010-0001-PP' AND clave_catastral = '0205500001'

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
DECLARE recargo DECIMAL;
DECLARE 
--SE PUSO UNA COUMNA DE DIREENCIAA XK EN ALGUNAS LIQUIDACIONES EL PAGO ERA MENOR O MAYOR AL TOTAL EMITIDO Y NO SE PUDO IDDENTIFICAR SI ERA UN ABONO.
--CURSORES
        /*
    parroquia character varying(50) COLLATE pg_catalog."default",
    direccion character varying(100) COLLATE pg_catalog."default",
     timestamp without time zone NOT NULL,
     character varying(4) COLLATE pg_catalog."default" NOT NULL,
     character varying(18) COLLATE pg_catalog."default" NOT NULL,
    impuestop numeric(18, 2) NOT NULL,
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
                       SELECT P.id as predio,           
			clave_catastral AS pre_codigo_catastral, num_tit AS num_titulo, aterreno AS valor_terr_predio,  
                        aconstruccion AS valor_edif_predio , acomercial AS val_comer_predio, impuestop AS ipu  , 
                        bomberos AS bomberos, fecha_emision AS fecha_emision, total as total, tcobrado as pago,
			total- (tcobrado - interes + descuento) AS diferencia,
			interes as interes, descuento as descuento,
			ESTADO.estado as estado_liquidacion, fecha_recaudacion as fecha_pago, 
                        ser_adminis as tasa_administrativa,  
                        ESTADO.observaciones as observaciones, usuario as usuario, cedula as ci, direccion as direccion_cont, 
                        COALESCE(TRIM(apellidos), '') || ' ' || COALESCE(TRIM(nombres), '') as nombre_comprador, basura as basura, 
                        "año_emision" AS anio, 
                        SUBSTRING(num_tit, 7, 4)::BIGINT AS num_liquidacion, num_tit AS num_titulo		
                                               
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
        bomberoexist = TRUE;    
    ELSE
        bomberoexist = FALSE;    
    END IF;
     
    IF T.estado_liquidacion = 'P' THEN estadoLiquidacion = 1; estado_pago = true; saldo = 0.00; END IF;
    IF T.estado_liquidacion = 'N' THEN estadoLiquidacion = 2; estado_pago = false; END IF;
    IF T.estado_liquidacion = 'Q' THEN estadoLiquidacion = 3; estado_pago = false; END IF;
    IF T.estado_liquidacion = 'B' THEN estadoLiquidacion = 4; estado_pago = false; END IF;

    tipoLiquidacion = 13; predioUrbano = T.predio; predioRural = null;  
    
    IF (T.diferencia = T.total AND (T.estado_liquidacion = 'B' OR T.estado_liquidacion = 'N') ) THEN recargo = 0.00; saldo = T.total; END IF;
    IF (T.diferencia > 0 AND T.diferencia < T.total AND (T.estado_liquidacion = 'B' OR T.estado_liquidacion = 'N')) THEN recargo = 0.00; saldo = T.diferencia; END IF;

    
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
        VALOR_COMERCIAL, 
            AVALUO_MUNICIPAL, AVALUO_CONSTRUCCION, AVALUO_SOLAR, BOMBERO, 
            NOMBRE_COMPRADOR, estado_coactiva, saldo, predio_rustico)
   
      VALUES ( T.num_liquidacion, T.num_titulo, 
               tipoLiquidacion, T.total, T.fecha_emision, comprador, 
               T.fecha_emision,  estadoLiquidacion, 
               predioUrbano, T.pre_codigo_catastral, T.anio, 
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
           recargo, T.interes, observacion, comprador, T.nombre_comprador)
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
        IF(T.predio IS NOT NULL) THEN    
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