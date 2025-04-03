--04-06-2017


--04-06-2017



INSERT INTO sgm_app.ctlg_catalogo(id, nombre)	VALUES (DEFAULT, 'predio.avaluo');  
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'TOPOGRAFIA DEL SOLAR', 'topografia', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'RELACION FRENTE FONDO', 'frente_fondo', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'SUPERFICIE M2', 'coeficiente_superficie', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'USO DEL SOLAR', 'uso_solar_aval', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS', 'servicios', 1);



UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'USO DEL SOLAR' AND CODENAME = 'uso_solar_aval' ),
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'USO DEL SOLAR' AND CODENAME = 'uso_solar_aval' )
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.uso_solar');



INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'AGUA POTABLE', 'agua_potable', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));

INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'ALCANTARILLADO SANITARIO', 'alcantarillado', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));

INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'ALCANTARILLADO PLUVIAL', 'alcantarillado_pluvial', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));

INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'ENERGIA ELECTRICA', 'energia_electrica', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));

INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'TELEFONIA FIJA', 'telefono', 1,  
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),  
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));

INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'TRANSPORTE URBANO', 'trans_urbano', 1,  
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),  
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'RED TELEFÓNICA', 'telefono', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'RECOLECCION DE BASURA', 'recolesbasura', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'ASEO DE CALLES', 'aseocalles', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS' AND CODENAME = 'servicios' ));




INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'MAYOR A 0.5000', 'frente_fondo_5000', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ), 0.5000);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '0.4999 A 0.3333', 'frente_fondo_4999', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ), 0.4999, 0.3333);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '0.3332 A 0.3333', 'frente_fondo_3332', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ), 0.3332, 0.3333);

INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '0.1999 A 0.1000', 'frente_fondo_1999', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ), 0.1999, 0.1000);


	INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '0.0999 A 0.0001', 'frente_fondo_0001', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'RELACION FRENTE FONDO' AND CODENAME = 'frente_fondo' ), 0.0999, 0.0001);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'LOCALIZACION', 'localizacion', 1);


UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'LOCALIZACION' AND CODENAME = 'localizacion' ),
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'LOCALIZACION' AND CODENAME = 'localizacion' )
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.loc_manzana');




INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'VIAS - MATERIAL DE RODADURA', 'vias', 1);


UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'VIAS - MATERIAL DE RODADURA' AND CODENAME = 'vias' ),
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'VIAS - MATERIAL DE RODADURA' AND CODENAME = 'vias' )
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.vias');


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'ACERAS', 'aceras', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'VIAS - MATERIAL DE RODADURA' AND CODENAME = 'vias' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'VIAS - MATERIAL DE RODADURA' AND CODENAME = 'vias' ));


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'BORDILLOS', 'bordillos', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'VIAS - MATERIAL DE RODADURA' AND CODENAME = 'vias' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'VIAS - MATERIAL DE RODADURA' AND CODENAME = 'vias' ));




INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '1 a 200', 'superf_200', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ), 1,200);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '201 a 500', 'superf_500', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),201,500);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '501 a 1000', 'superf_100', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),501,1000);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '1001 a 2500', 'superf_2500', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ), 1001, 2500);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '2501 a 5000', 'superf_5000', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ), 2501, 5000);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde, rango_hasta)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  '5001 a 1000', 'superf_5001', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ), 5001, 1000);



INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden, padre, referencia, rango_desde)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'),  'MAYOR a 1001', 'superf_1001', 1,
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ),
	(SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'SUPERFICIE M2' AND CODENAME = 'coeficiente_superficie' ), 1001);



INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'CARACTERISTICA DEL SUELO', 'suelito', 1);


INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'FORMA DEL SOLAR', 'forma_solar', 1);




UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'CARACTERISTICA DEL SUELO' AND CODENAME = 'suelito' ),
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'CARACTERISTICA DEL SUELO' AND CODENAME = 'suelito' )
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.tipo_suelo');


UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'FORMA DEL SOLAR' AND CODENAME = 'forma_solar' ),
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'FORMA DEL SOLAR' AND CODENAME = 'forma_solar' )
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.forma_solar');




INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'INSTALACIONES ESPECIALES', 'ins_especial', 1);



UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INSTALACIONES ESPECIALES' AND CODENAME = 'ins_especial' ),
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'INSTALACIONES ESPECIALES' AND CODENAME = 'ins_especial' )
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.instalacion_especial');




UPDATE SGM_APP.CTLG_ITEM SET PADRE = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'TOPOGRAFIA DEL SOLAR' AND CODENAME = 'topografia' ), 
REFERENCIA = (SELECT ID FROM SGM_APP.CTLG_ITEM WHERE VALOR = 'TOPOGRAFIA DEL SOLAR' AND CODENAME = 'topografia' ) 
WHERE CATALOGO = (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE nombre = 'predio.topografia');
  
  --09062017

--ALTER TABLE sgm_app.ctlg_item DROP COLUMN prototipo;
--DROP TABLE sgm_app.valor_m2_prototipo;
--DROP TABLE sgm_app.rango_valor_coeficiente;


  -- Table: sgm_app.aval_banda_impositiva

-- DROP TABLE sgm_app.aval_banda_impositiva;

CREATE TABLE sgm_app.aval_banda_impositiva
(
  id bigserial NOT NULL, --
  multiplo_impuesto_predial numeric(19,2),
  anio_inicio integer,
  anio_fin integer,
  CONSTRAINT aval_banda_impositiva_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.aval_banda_impositiva
  OWNER TO sisapp;
COMMENT ON COLUMN sgm_app.aval_banda_impositiva.id IS '
';




  -- Table: sgm_app.aval_categoria_valor_suelo

-- DROP TABLE sgm_app.aval_categoria_valor_suelo;

CREATE TABLE sgm_app.aval_categoria_valor_suelo
(
  id bigserial NOT NULL,
  parroquia smallint NOT NULL,
  zona smallint NOT NULL,
  sector smallint NOT NULL,
  mz smallint NOT NULL,
  valor_m2 numeric(12,4) NOT NULL,
  solar smallint NOT NULL,
  anio_inicio integer,
  anio_fin integer,
  CONSTRAINT aval_categoria_mz_pkey PRIMARY KEY (id),
  CONSTRAINT aval_categoria_valor_suelo_zona_sector_mz_key UNIQUE (zona, sector, mz, parroquia, anio_inicio, anio_fin)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.aval_categoria_valor_suelo
  OWNER TO sisapp;


  -- Table: sgm_app.aval_coeficientes

-- DROP TABLE sgm_app.aval_coeficientes;

CREATE TABLE sgm_app.aval_coeficientes
(
  id bigserial NOT NULL,
  categoria_construccion bigint,
  categoria_solar bigint, -- esta columna tiene datos de las caracteristicas del  solar, las del suelo como tiene borddillos , la topograica entre otras :D :)
  valor_coeficiente numeric(19,2),
  anio_inicio integer,
  valor_coef_inferior numeric(19,2),
  valor_coef_superior numeric(19,2),
  anio_fin integer,
  CONSTRAINT aval_coeficientes_pkey PRIMARY KEY (id),
  CONSTRAINT aval_coeficientes_categoria_construccion_fkey FOREIGN KEY (categoria_construccion)
      REFERENCES sgm_app.cat_edf_prop (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT aval_coeficientes_categoria_solar_fkey FOREIGN KEY (categoria_solar)
      REFERENCES sgm_app.ctlg_item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.aval_coeficientes
  OWNER TO sisapp;
COMMENT ON COLUMN sgm_app.aval_coeficientes.categoria_solar IS 'esta columna tiene datos de las caracteristicas del  solar, las del suelo como tiene borddillos , la topograica entre otras :D :)';








-- Table: sgm_app.aval_coeficientes

-- DROP TABLE sgm_app.aval_coeficientes;

CREATE TABLE sgm_app.cat_predio_aval_historico
(
  id bigserial NOT NULL,
  predio bigint REFERENCES sgm_app.cat_predio(id),
  avaluo_solar numeric(19,5),
  avaluo_construccion numeric(19,5),
  avaluo_cultivo numeric(19,5),
  avaluo_municipal numeric(19,5),
  fecha_actualizacion  timestamp without time zone NOT NULL DEFAULT now(),
  anio_inicio integer,
  anio_fin integer,
  CONSTRAINT cat_predio_aval_historico_pkey PRIMARY KEY (id)

)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.cat_predio_aval_historico
  OWNER TO sisapp;





CREATE TABLE sgm_app.aval_impuesto_predios
(
  id bigserial NOT NULL,
  parroquia smallint NOT NULL,
  zona smallint NOT NULL,
  sector smallint NOT NULL,
  mz smallint NOT NULL,
  banda_impositiva bigint,
  anio_inicio integer,
  anio_fin integer,
  solar smallint,
  estado character varying(1),
  cobro_bombero boolean,
  cobro_mejoras boolean,
  pago_solar_no_edificado boolean,
  CONSTRAINT aval_valor_banda_impositiva_pkey PRIMARY KEY (id),
  CONSTRAINT aval_impuesto_predios_banda_impositiva_fkey FOREIGN KEY (banda_impositiva)
      REFERENCES sgm_app.aval_banda_impositiva (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.aval_impuesto_predios
  OWNER TO sisapp;




  ALTER TABLE sgm_app.aval_banda_impositiva ADD COLUMN desde_us numeric (19,5);
ALTER TABLE sgm_app.aval_banda_impositiva ADD COLUMN hasta_us numeric (19,5);



ALTER TABLE sgm_app.aval_banda_impositiva ADD COLUMN estado character varying(1);












--11-07-2017


DROP SEQUENCE sgm_app.cat_predio_aval_historico_id_seq CASCADE;

CREATE SEQUENCE sgm_app.aval_historico_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE sgm_app.aval_historico_id_seq
  OWNER TO sisapp;

ALTER TABLE sgm_app.cat_predio_aval_historico ALTER COLUMN id 
SET DEFAULT nextval('sgm_app.aval_historico_id_seq');



