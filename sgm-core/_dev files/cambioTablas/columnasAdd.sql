
-- 2017-05-29

ALTER TABLE sgm_app.cat_predio RENAME alicuota_sac  TO alicuota_util;

ALTER TABLE sgm_app.cat_predio ADD COLUMN ficha_madre boolean DEFAULT false;
ALTER TABLE sgm_app.cat_predio ADD COLUMN admin_nombres_apellidos character varying(500);
ALTER TABLE sgm_app.cat_predio ADD COLUMN admin_cedula character varying(20);
ALTER TABLE sgm_app.cat_predio ADD COLUMN admin_telefono character varying(50);
ALTER TABLE sgm_app.cat_predio ADD COLUMN admin_celular character varying(50);

ALTER TABLE sgm_app.cat_predio ADD COLUMN cant_alicuotas integer DEFAULT 0;

-- 2017-05-31 Registro de alicuotas
ALTER TABLE sgm_app.cat_escritura ADD COLUMN tipo_ph integer;
ALTER TABLE sgm_app.cat_escritura ADD COLUMN resolucion character varying(100);
ALTER TABLE sgm_app.cat_escritura ADD COLUMN fecha_resolucion timestamp without time zone;
ALTER TABLE sgm_app.cat_escritura ADD COLUMN cant_bloques integer;
ALTER TABLE sgm_app.cat_escritura ADD COLUMN cant_alicuotas integer;

-- 2017-06-01
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_dpto boolean;
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_bodegas boolean;
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_parqueos boolean;
ALTER TABLE sgm_app.cat_predio ADD COLUMN tiene_escritura boolean;
--2017-03-06
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_bodegas_area numeric(19,2);
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_dpto_area numeric(19,2);
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_bodegas_numero character varying(200);
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_parqueos_numero character varying(200);
ALTER TABLE sgm_app.cat_predio ADD COLUMN componente_parqueos_area numeric(19,2);

--04-06-2017


CREATE TABLE sgm_app.aval_categoria_valor_suelo
(
  id bigserial NOT NULL,
  parroquia smallint NOT NULL,
  zona smallint NOT NULL,
  sector smallint NOT NULL,
  mz smallint NOT NULL,
  valor_m2 numeric(12,4) NOT NULL,
  anio integer,
  categoria integer,

  CONSTRAINT aval_categoria_mz_pkey PRIMARY KEY (id),
  CONSTRAINT aval_categoria_valor_suelo_zona_sector_mz_key UNIQUE (zona, sector, mz, parroquia, anio)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.aval_categoria_valor_suelo
  OWNER TO sisapp;


  ALTER TABLE sgm_app.aval_categoria_valor_suelo ADD COLUMN solar smallint;
ALTER TABLE sgm_app.aval_categoria_valor_suelo ALTER COLUMN solar SET NOT NULL;




-- Table: sgm_app.aval_coeficientes

-- DROP TABLE sgm_app.aval_coeficientes;

CREATE TABLE sgm_app.aval_coeficientes
(
  id bigserial NOT NULL,
  categoria_construccion bigint,
  categoria_solar bigint, -- esta columna tiene datos de las caracteristicas del  solar, las del suelo como tiene borddillos , la topograica entre otras :D :)
  valor_coeficiente numeric(19,2),
  anio integer,
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







ALTER TABLE sgm_app.cat_predio ALTER COLUMN forma_solar type bigint using forma_solar::bigint;




-- Function: censocat.update_entes_ame()

-- DROP FUNCTION censocat.update_entes_ame();

CREATE OR REPLACE FUNCTION sgm_app.update_forma_solar_ctl_item()
  RETURNS void AS
$BODY$
DECLARE SECUENCIAMAX BIGINT;
DECLARE IDCTLGITEM BIGINT;
DECLARE
	C_FORMA CURSOR FOR

		SELECT id, forma_solar FROM SGM_APP.CAT_PREDIO WHERE forma_solar is not null;

	ACIERTOS INTEGER := 0;
BEGIN

	FOR C IN C_FORMA LOOP

		IDCTLGITEM = (SELECT  id FROM sgm_app.ctlg_item WHERE referencia = c.forma_solar AND catalogo = (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.forma_solar'));
		UPDATE sgm_app.cat_predio SET forma_solar = IDCTLGITEM WHERE id = c.id;

	END LOOP;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION sgm_app.update_forma_solar_ctl_item()
  OWNER TO sisapp;


select sgm_app.update_forma_solar_ctl_item();

ALTER TABLE sgm_app.cat_predio ADD FOREIGN KEY (forma_solar)  REFERENCES sgm_app.ctlg_item(id);


ALTER TABLE sgm_application.empresa ADD COLUMN url_predio character varying;




-- Table: sgm_app.cat_predio_fusion_division

-- DROP TABLE sgm_app.cat_predio_fusion_division;

CREATE TABLE sgm_app.cat_predio_fusion_division
(
  id bigserial NOT NULL,
  predio_raiz bigserial NOT NULL,
  predio_resultante bigserial NOT NULL,
  tipo bigint,
  estado boolean,
  CONSTRAINT cat_predio_fusion_division_pkey PRIMARY KEY (id),
  CONSTRAINT cat_predio_fusion_division_predio_raiz_fkey FOREIGN KEY (predio_raiz)
      REFERENCES sgm_app.cat_predio (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cat_predio_fusion_division_predio_resultante_fkey FOREIGN KEY (predio_resultante)
      REFERENCES sgm_app.cat_predio (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cat_predio_fusion_division_tipo_fkey FOREIGN KEY (tipo)
      REFERENCES sgm_app.ctlg_item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.cat_predio_fusion_division
  OWNER TO sisapp;






INSERT INTO sgm_app.ctlg_catalogo(id, nombre)	VALUES (DEFAULT, 'predio.avaluo', 'AVALUOS');
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'TOPOGRAFIA DEL SOLAR', 'topografia', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'RELACION FRENTE FONDO', 'frente_fondo', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'COEFICIENTE DE SUPERFICIE', 'coeficiente_superficie', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'USO DEL SOLAR', 'uso_solar_aval', 1);
INSERT INTO sgm_app.ctlg_item(id, catalogo, valor, codename, orden)
	VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre='predio.avaluo'), 'INFRAESTRUCTURA, INSTALACION Y SERVICIOS', 'servicios', 1);




ALTER TABLE sgm_app.cat_predio_alicuota_componente ALTER COLUMN area_construccion TYPE numeric(10,4);
ALTER TABLE sgm_app.cat_predio_alicuota_componente ALTER COLUMN area_declarada TYPE numeric(10,4);
ALTER TABLE sgm_app.cat_predio_alicuota_componente ALTER COLUMN alicuota_util TYPE numeric(10,4);
ALTER TABLE sgm_app.cat_predio_alicuota_componente ALTER COLUMN alicuota_comunal TYPE numeric(10,4);

-- 07-06-2017
-- Table: sgm_app.cat_predio_linderos

-- DROP TABLE sgm_app.cat_predio_linderos;

CREATE TABLE sgm_app.cat_predio_linderos
(
  id bigserial NOT NULL,
  predio bigint NOT NULL,
  predio_colindante bigint,
  colindante character varying,
  orientacion bigint,
  estado character varying(1) DEFAULT 'A'::character varying,
  CONSTRAINT cat_predio_linderos_pkey PRIMARY KEY (id),
  CONSTRAINT cat_predio_linderos_monto_prestamo_fkey FOREIGN KEY (orientacion)
      REFERENCES sgm_app.ctlg_item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cat_predio_linderos_predio_colindante_fkey FOREIGN KEY (predio_colindante)
      REFERENCES sgm_app.cat_predio (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cat_predio_linderos_predio_fkey FOREIGN KEY (predio)
      REFERENCES sgm_app.cat_predio (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.cat_predio_linderos
  OWNER TO sisapp;
-- catalogo de linderos
INSERT INTO SGM_APP.CTLG_CATALOGO (ID, NOMBRE) VALUES (DEFAULT, 'predio.lindero_orientacion');
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.lindero_orientacion'), 'NORTE', 'norte', 1);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.lindero_orientacion'), 'SUR', 'sur', 2);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.lindero_orientacion'), 'ESTE', 'este', 3);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.lindero_orientacion'), 'OESTE', 'oeste', 4);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.lindero_orientacion'), 'SUPERIOR', 'superior', 5);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.lindero_orientacion'), 'INFERIOR', 'inferior', 6);

-- Table: sgm_app.acl_login

-- DROP TABLE sgm_app.acl_login;

CREATE TABLE sgm_app.acl_login
(
  id bigserial NOT NULL,
  ip_user_session character varying,
  fecha_do_login timestamp without time zone,
  fecha_do_logout timestamp without time zone,
  user_session_name character varying,
  user_sesion_id bigint,
  CONSTRAINT acl_login_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.acl_login
  OWNER TO sisapp;
ALTER TABLE sgm_app.ctlg_item ADD COLUMN observacion varchar;
ALTER TABLE sgm_app.ctlg_catalogo ADD COLUMN observacion varchar;


-- 2017-06-12 cambios Libertad

ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_bodegas_area;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_dpto_area;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_bodegas_numero;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_parqueos_numero;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_parqueos_area;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_dpto;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_bodegas;
ALTER TABLE sgm_app.cat_predio DROP COLUMN componente_parqueos;

ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_oficina character varying(100);
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_local character varying(100);
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_parqueo character varying(100);
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_bodega character varying(100);
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_acera BIGINT REFERENCES sgm_app.ctlg_item(id);
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_ubicacion character varying(100);

INSERT INTO SGM_APP.CTLG_CATALOGO (ID, NOMBRE) VALUES (DEFAULT, 'predio.acera');
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.acera'), 'IZQUIERDA', 'izquierda', 1);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='predio.acera'), 'DERECHA', 'derecha', 2);


ALTER TABLE sgm_app.cat_ente ADD COLUMN lleva_contabilidad boolean;

ALTER TABLE sgm_historico.predio ADD COLUMN cambios character varying(5000);


-- 2017-06-13

ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_sanitaria_economica integer;
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_sanitaria_media integer;
ALTER TABLE sgm_app.cat_predio ADD COLUMN ex_sanitaria_lujo integer;

ALTER TABLE sgm_app.cat_predio_obra_interna ADD COLUMN altura numeric;
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN condicion_fisica BIGINT REFERENCES sgm_app.ctlg_item(id);

INSERT INTO SGM_APP.CTLG_CATALOGO (ID, NOMBRE) VALUES (DEFAULT, 'edificacion.condicion_fisica');
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='edificacion.condicion_fisica'), 'EN CONSTRUCCION', 'enConstruccion', 0);
INSERT INTO sgm_app.ctlg_item(ID, CATALOGO, valor, codename, orden)
    VALUES (DEFAULT, (SELECT ID FROM SGM_APP.CTLG_CATALOGO WHERE NOMBRE='edificacion.condicion_fisica'), 'TERMINADA', 'terminada', 1);


ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN area_bloque numeric(19,2); -- AREA DELL BLOQUE O EDIFICACION (SUMA DE LOS PISO)
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN observaciones character varying(5000); -- OBSERVACIONES AL MOMENTO DE ELIMINAR EL BLOQUE O EDIFICACION
COMMENT ON COLUMN sgm_app.cat_predio_edificacion.no_edificacion IS '0=EP(edificio principal), numBloque en ibarra';
COMMENT ON COLUMN sgm_app.cat_predio_edificacion.area_bloque IS 'AREA DELL BLOQUE O EDIFICACION (SUMA DE LOS PISO)';
COMMENT ON COLUMN sgm_app.cat_predio_edificacion.observaciones IS 'OBSERVACIONES AL MOMENTO DE ELIMINAR EL BLOQUE O EDIFICACION';
COMMENT ON TABLE sgm_app.cat_predio_edificacion IS 'BLOQUES CONSTRUCTIVOS O EDIFICACIONES';
ALTER TABLE sgm_app.cat_edificacion_pisos_det ADD COLUMN nivel bigint NOT NULL REFERENCES sgm_app.ctlg_item (id);
COMMENT ON TABLE sgm_app.cat_edificacion_pisos_det IS 'DESCRIPCION DE CADA UNO DE LOS PISOS'; --edificacion
COMMENT ON COLUMN sgm_app.cat_edificacion_pisos_det.nivel IS 'DESCRIPCION DEL PISO O NIVEL';
COMMENT ON COLUMN sgm_app.cat_edificacion_pisos_det.edificacion IS 'REEFERENCIA DEL BLOQUE O EDIFICACION';
ALTER TABLE sgm_app.cat_predio_edificacion_prop DROP COLUMN predio;
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN modificado character varying(20);
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN usuario character varying(20);
ALTER TABLE sgm_app.cat_edificacion_pisos_det ALTER COLUMN estado TYPE character varying;
ALTER TABLE sgm_app.cat_predio_edificacion_prop ADD COLUMN fecha timestamp(6) without time zone DEFAULT now();
ALTER TABLE sgm_app.cat_predio_edificacion_prop ADD COLUMN usuario VARCHAR;
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN edad_construccion smallint;
ALTER TABLE sgm_app.cat_predio_edificacion
  ADD FOREIGN KEY (estado_conservacion) REFERENCES sgm_app.ctlg_item (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN condicion_fisica bigint REFERENCES sgm_app.ctlg_item(id);
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN anio_restaura integer;
ALTER TABLE sgm_app.cat_predio_edificacion ALTER COLUMN estado TYPE character varying;
ALTER TABLE sgm_app.cat_predio_edificacion_prop ADD COLUMN usuario character varying(20);















----23062017

ALTER TABLE sgm_app.cat_predio_aval_historico ADD COLUMN predio_json TEXT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD COLUMN predio_edificacion_json TEXT;



--29062017

ALTER TABLE sgm_app.cat_predio_edificacion DROP CONSTRAINT cat_predio_edificacion_predio_no_edificacion_key;
ALTER TABLE sgm_app.cat_predio_edificacion DROP CONSTRAINT uk_g46tdea6h5jy09flnch5cd2j0;


--07072017

ALTER TABLE sgm_financiero.ren_tipo_liquidacion ADD COLUMN permite_exoneracion boolean;
ALTER TABLE sgm_financiero.ren_tipo_liquidacion ALTER COLUMN permite_exoneracion SET DEFAULT false;

UPDATE  sgm_financiero.ren_tipo_liquidacion SET permite_exoneracion = false;




ALTER TABLE sgm_app.cat_predio_edificacion DROP CONSTRAINT uk_g46tdea6h5jy09flnch5cd2j0;


--2017-07-15

CREATE TABLE sgm_app.cat_escritura_propietario
(
  id bigserial,
  ente bigint,
  propietario bigint,
  escritura bigint NOT NULL,
  cedula character varying(20),
  porcentaje_posecion numeric,
  copropietario boolean,
  usuario character varying(100),
  fecha timestamp(6) without time zone DEFAULT now(),
  CONSTRAINT cat_escritura_propietario_pkey PRIMARY KEY (id),
  CONSTRAINT propietario_fkey FOREIGN KEY (propietario)
      REFERENCES sgm_app.cat_predio_propietario (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT escritura_fkey FOREIGN KEY (escritura)
      REFERENCES sgm_app.cat_escritura (id_escritura) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ente_fkey FOREIGN KEY (ente)
      REFERENCES sgm_app.cat_ente (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.cat_escritura_propietario
  OWNER TO sisapp;



-- 2017-07-21 

ALTER TABLE sgm_flow.historico_tramite_det ADD COLUMN to_json TEXT;



--2017-07-27

ALTER TABLE sgm_app.acl_user ALTER COLUMN id SET DEFAULT nextval('sgm_app.app_uni_seq');

--UPDATE sgm_app.cat_predio set nombre_pueblo_etnia = null;
ALTER TABLE sgm_app.cat_predio ALTER COLUMN nombre_pueblo_etnia TYPE BIGINT using nombre_pueblo_etnia::BIGINT ;
ALTER TABLE sgm_app.cat_predio ADD FOREIGN KEY (nombre_pueblo_etnia)  REFERENCES sgm_app.ctlg_item(id);



INSERT sgm_app.ctlg_catalogo  ( id , nombre) VALUES (DEFAULT, 'predio.pueblo_etnia' );

INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'NO TIENE', 'no_tiene', 1);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'ANDOAS', 'andoas', 2);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'COFANES', 'cofanes', 3);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'HUARANI', 'huarani', 4);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'QUICHUAS', 'quichuas', 5);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'SECOYAS', 'secoyas', 6);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'SHUAR', 'shuar', 7);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'SIONAS', 'sionas', 8);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'ZAPAROS', 'zaparos', 9);
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden ) VALUES (DEFAULT, (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.pueblo_etnia'), 'OTRA', 'otra_(especificar)', 10);



INSERT INTO sgm_app.ctlg_catalogo  ( id , nombre) VALUES (DEFAULT, 'predio.formaadquisicion');

 INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'ADJUDICACIÓN', 'adjudicacion', 1 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'COMPRAVENTA', 'compraventa',  2 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'DONACIÓN', 'donacion', 3 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'HERENCIA', 'herencia',  4 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'PARTICIÓN', 'particion',   5 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'PERMUTA', 'permuta',   6 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'POSESIÓN', 'posesion',  7 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'REMATE', 'remate',  8 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'PRESCRIPCIÓN', 'prescripcion',   9 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'REGULARIZACIÓN DE EXCEDENTES O DIFERENCIAS', 'regula_escedentes',   10 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'CESION DE ÁREAS', 'cesion_areas',  11 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'RESCILIACIÓN', 'resciliacion',  12 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'POSESIÓN EFECTIVA', 'posesion_efectiva',  13 ); 
INSERT INTO  sgm_app.ctlg_item  ( id ,  catalogo ,  valor ,  codename ,  orden )   VALUES (DEFAULT,  (SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre= 'predio.formaadquisicion'), 'TESTAMENTO', 'testamento',  14 ); 





alter table sgm_app.cat_predio_aval_historico add column area_construccion character varying(255);
alter table sgm_app.cat_predio_aval_historico add area_solar numeric (19,3);
alter table sgm_app.cat_predio_aval_historico add VALOR_BASE_M2 numeric (19,3);
alter table sgm_app.cat_predio_aval_historico add SUMA_COEF_EDIF character varying(255);
alter table sgm_app.cat_predio_aval_historico add DEPRECIACION  character varying(255);





-- 2017-08-14 


-- cat_predio_s4 dependencias--

ALTER TABLE sgm_app.cat_predio_s4_has_accesibilidad DROP CONSTRAINT fkb1e2c35157101fa;

ALTER TABLE sgm_app.cat_predio_s4_has_accesibilidad DROP CONSTRAINT fkb1e2c35f06493de;

ALTER TABLE sgm_app.cat_predio_s4_has_accesibilidad ADD CONSTRAINT fk_predios4_accesibilidad FOREIGN KEY (predio_s4) REFERENCES sgm_app.cat_predio_s4 (id) ON UPDATE CASCADE ON DELETE CASCADE;
  
ALTER TABLE sgm_app.cat_predio_s4_has_accesibilidad ADD CONSTRAINT "fk_ctlgItem_accesibilidad" FOREIGN KEY (accesibilidad_ctlg) REFERENCES sgm_app.ctlg_item (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_predio_s4 DROP CONSTRAINT fkf5893d30f5991f4b;

ALTER TABLE sgm_app.cat_predio_s4 ADD CONSTRAINT fk_s4_predio FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE CASCADE;
  
-- cat_predio_s6 dependencias--

ALTER TABLE sgm_app.cat_predio_s6_has_instalacion_especial DROP CONSTRAINT cat_predio_s6_has_instalacion_especial_ctlg_item_fkey;

ALTER TABLE sgm_app.cat_predio_s6_has_instalacion_especial DROP CONSTRAINT cat_predio_s6_has_instalacion_especial_predio_s6_fkey;

ALTER TABLE sgm_app.cat_predio_s6_has_instalacion_especial ADD CONSTRAINT fk_s6_instalaciones FOREIGN KEY (predio_s6) REFERENCES sgm_app.cat_predio_s6 (id) ON UPDATE CASCADE ON DELETE CASCADE;
  
ALTER TABLE sgm_app.cat_predio_s6_has_instalacion_especial ADD CONSTRAINT fk_ctlitem_instalaciones FOREIGN KEY (ctlg_item) REFERENCES sgm_app.ctlg_item (id) ON UPDATE CASCADE ON DELETE CASCADE;
  
ALTER TABLE sgm_app.cat_predio_s6_has_usos DROP CONSTRAINT cat_predio_s6_has_usos_predio_s6_fkey;

ALTER TABLE sgm_app.cat_predio_s6_has_usos DROP CONSTRAINT cat_predio_s6_has_usos_uso_fkey;

ALTER TABLE sgm_app.cat_predio_s6_has_usos ADD CONSTRAINT fk_s6_usos FOREIGN KEY (predio_s6) REFERENCES sgm_app.cat_predio_s6 (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_predio_s6_has_usos ADD CONSTRAINT fk_ctlgitem_uso FOREIGN KEY (uso) REFERENCES sgm_app.ctlg_item (id) ON UPDATE CASCADE ON DELETE CASCADE;

DELETE FROM sgm_app.cat_predio_s6_has_vias WHERE predio_s6 NOT IN (SELECT id FROM sgm_app.cat_predio_s6);

ALTER TABLE sgm_app.cat_predio_s6_has_vias DROP CONSTRAINT fk78ae9d17abd42dfe;

ALTER TABLE sgm_app.cat_predio_s6_has_vias ADD CONSTRAINT fk_s6_vias FOREIGN KEY (predio_s6) REFERENCES sgm_app.cat_predio_s6 (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_predio_s6_has_vias ADD CONSTRAINT fk_ctlgitem_vias FOREIGN KEY (ctlg_item) REFERENCES sgm_app.ctlg_item (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_predio_s6 DROP CONSTRAINT cat_predio_s6_predio_fkey;

ALTER TABLE sgm_app.cat_predio_s6 ADD CONSTRAINT fk_s6_predio FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE CASCADE;

--- cat_predio_propietario

ALTER TABLE sgm_app.cat_predio_propietario DROP CONSTRAINT fkef3e4fdf5991f4b;

ALTER TABLE sgm_app.cat_predio_propietario ADD CONSTRAINT fk_predio_propietario FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE CASCADE;


--- cat_predio_edificacion -- REVISAR LAS DEPENDENCIAS EN cat_predio_edificacion_prop YA QUE NO ESTA PRESENTE LA EDIFICACION

DELETE FROM sgm_app.cat_predio_edificacion_prop WHERE edificacion NOT IN (SELECT id FROM sgm_app.cat_predio_edificacion);

ALTER TABLE sgm_app.cat_predio_edificacion_prop ADD CONSTRAINT fk_edificacion_prop FOREIGN KEY (edificacion) REFERENCES sgm_app.cat_predio_edificacion (id) ON UPDATE CASCADE ON DELETE CASCADE;

DELETE FROM sgm_app.cat_edificacion_pisos_det WHERE edificacion NOT IN (SELECT id FROM sgm_app.cat_predio_edificacion);

ALTER TABLE sgm_app.cat_edificacion_pisos_det ADD CONSTRAINT fk_pisos_edificacion FOREIGN KEY (edificacion) REFERENCES sgm_app.cat_predio_edificacion (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_predio_edificacion DROP CONSTRAINT fkc0410421f5991f4b;

ALTER TABLE sgm_app.cat_predio_edificacion ADD CONSTRAINT fk_edificacion_predio FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE CASCADE;

--- cat_predio_bloque

ALTER TABLE sgm_app.cat_predio_bloque_piso DROP CONSTRAINT cat_predio_bloque_piso_bloque_fkey;

ALTER TABLE sgm_app.cat_predio_bloque_piso ADD CONSTRAINT fk_bloque_piso FOREIGN KEY (bloque) REFERENCES sgm_app.cat_predio_bloque (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_bloque_caracteristica DROP CONSTRAINT cat_bloque_caracteristica_bloque_fkey;

ALTER TABLE sgm_app.cat_bloque_caracteristica ADD CONSTRAINT fk_bloque_caract FOREIGN KEY (bloque) REFERENCES sgm_app.cat_predio_bloque (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_predio_bloque DROP CONSTRAINT cat_predio_bloque_predio_fkey;

ALTER TABLE sgm_app.cat_predio_bloque ADD CONSTRAINT fk_cat_predio_bloque FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE CASCADE;

--- historico_tramite_det

ALTER TABLE sgm_flow.historico_tramite_det DROP CONSTRAINT fka284ceb5f5991f4b;

ALTER TABLE sgm_flow.historico_tramite_det ADD CONSTRAINT fk_predio_detalle FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE SET NULL;

--- cat_escritura

ALTER TABLE sgm_app.cat_escritura_propietario DROP CONSTRAINT escritura_fkey;

ALTER TABLE sgm_app.cat_escritura_propietario ADD CONSTRAINT fk_escritura_propietario FOREIGN KEY (escritura) REFERENCES sgm_app.cat_escritura (id_escritura) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE sgm_app.cat_escritura DROP CONSTRAINT fkac939fb3f5991f4b;

ALTER TABLE sgm_app.cat_escritura ADD CONSTRAINT fk_escritura_predio FOREIGN KEY (predio) REFERENCES sgm_app.cat_predio (id) ON UPDATE CASCADE ON DELETE CASCADE;


--- para los bloques en geodata

ALTER TABLE geodata.temp_bloque ADD COLUMN habilitado boolean DEFAULT true;

ALTER TABLE geodata.geo_bloque ADD COLUMN habilitado boolean DEFAULT true;

ALTER TABLE geodata.temp_bloque ADD COLUMN num smallint DEFAULT 1;

ALTER TABLE geodata.geo_bloque ADD COLUMN num smallint DEFAULT 1;

ALTER TABLE geodata.temp_bloque ADD COLUMN piso SMALLINT;
ALTER TABLE sgm_app.geo_predios_divididos ADD COLUMN codigo_nuevo VARCHAR;
ALTER TABLE sgm_flow.historico_tramite_det ADD COLUMN to_json VARCHAR;


ALTER TABLE catastro.restricion_predio DROP CONSTRAINT pk_restricion_predio;
ALTER TABLE catastro.restricion_predio ADD COLUMN id SERIAL;
ALTER TABLE catastro.restricion_predio ADD COLUMN predio BIGINT ;
ALTER TABLE catastro.restricion_predio ADD PRIMARY KEY (id);
ALTER TABLE geodata.geo_bloque ADD COLUMN num smallint DEFAULT 1;

ALTER TABLE geodata.temp_bloque ADD COLUMN piso smallint DEFAULT 1;

ALTER TABLE geodata.geo_bloque ADD COLUMN piso smallint DEFAULT 1;
--------------
ALTER TABLE sgm_app.formato_reporte ADD COLUMN reporte character varying(100);


ALTER TABLE geodata.predios_tx ADD COLUMN clave_ante character varying(254);
ALTER TABLE geodata.predios_tx ADD COLUMN numeracion smallint;
ALTER TABLE geodata.predios_tx ADD COLUMN ficha_alfanumerica character varying(255);

ALTER TABLE geodata.geo_predio ADD COLUMN clave_anterior character varying(254);
ALTER TABLE geodata.geo_predio ADD COLUMN ficha_alfanumerica character varying(255);



ALTER TABLE sgm_app.cat_certificado_avaluo ALTER COLUMN  identificacion  TYPE character varying;


-- 2017-09-01 


ALTER TABLE sgm_app.cat_predio ADD COLUMN nombre_cambiado character varying(4000);
ALTER TABLE sgm_app.cat_predio ADD COLUMN cambio_nombre boolean;
ALTER TABLE sgm_app.cat_certificado_avaluo ADD COLUMN valor_m2 numeric;
ALTER TABLE sgm_app.cat_certificado_avaluo ADD COLUMN area_construccion numeric;



--2017 09 - 13

ALTER TABLE sgm_financiero.certificado_exoneracion_local_activos DROP CONSTRAINT certificado_exoneracion_local_activos_user_modificacion_fkey;
ALTER TABLE sgm_financiero.certificado_exoneracion_local_activos DROP CONSTRAINT certificado_exoneracion_local_activos_user_creador_fkey;




CREATE TABLE sgm_app.aval_det_cobro_impuesto_predios
(
  id bigserial NOT NULL,
  id_aval_impuesto_predio bigserial NOT NULL,
  id_rubro_cobrar bigserial NOT NULL,
  CONSTRAINT aval_det_cobro_impuesto_predios_pkey PRIMARY KEY (id),
  CONSTRAINT aval_impuesto_predios_fkey FOREIGN KEY (id_aval_impuesto_predio)
      REFERENCES sgm_app.aval_impuesto_predios (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT rubros_liquidacion_fkey FOREIGN KEY (id_rubro_cobrar)
	REFERENCES sgm_financiero.ren_rubros_liquidacion (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.aval_det_cobro_impuesto_predios
  OWNER TO sisapp;

ALTER TABLE sgm_financiero.ren_rubros_liquidacion  ADD COLUMN function_calculation CHARACTER VARYING;
UPDATE sgm_financiero.ren_rubros_liquidacion SET descripcion=UPPER(descripcion);




ALTER TABLE sgm_app.cat_predio_aval_historico ADD   topografia_solar  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   loc_manzana  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tipo_suelo  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   forma_solar  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   uso_suelo  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tiene_agua_potable  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tiene_alcantarillado  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   alcantarillado_pluvial  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tiene_electricidad  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   aseo_calles  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   recoleccion_basura  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tiene_telf_fijo  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tpublico  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tiene_aceras  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   tiene_bordillo  BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   area_construccion BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   area_solar BIGINT;
ALTER TABLE sgm_app.cat_predio_aval_historico ADD   valor_base_m2 BIGINT;


ALTER TABLE sgm_app.cat_ubicacion ADD COLUMN parroquia smallint DEFAULT 0 DEFAULT -1;
ALTER TABLE sgm_app.cat_ubicacion ALTER COLUMN parroquia SET NOT NULL;
ALTER TABLE sgm_app.cat_ubicacion ADD COLUMN zona smallint DEFAULT -1 ;
ALTER TABLE sgm_app.cat_ubicacion ALTER COLUMN zona SET NOT NULL; 
ALTER TABLE sgm_app.cat_ubicacion ADD COLUMN sector smallint DEFAULT -1;
ALTER TABLE sgm_app.cat_ubicacion ALTER COLUMN sector SET NOT NULL;
ALTER TABLE sgm_app.cat_ubicacion ADD COLUMN mz smallint DEFAULT -1;
ALTER TABLE sgm_app.cat_ubicacion ALTER COLUMN mz SET NOT NULL;
ALTER TABLE sgm_app.cat_ubicacion ADD COLUMN solar smallint DEFAULT -1;



-- Table: sgm_mejoras.mej_obra

-- DROP TABLE sgm_mejoras.mej_obra;

CREATE TABLE sgm_mejoras.mej_obra_ubicacion
(
  id bigserial NOT NULL,
  id_mejora bigserial NOT NULL,
  ubicacion bigint NOT NULL,
  CONSTRAINT pk_obra_ubicacion PRIMARY KEY (id),
  CONSTRAINT fk_obra_ubicaciones FOREIGN KEY (ubicacion)
      REFERENCES sgm_app.cat_ubicacion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_obra_ubicacion FOREIGN KEY (id_mejora)
      REFERENCES sgm_mejoras.mej_obra (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION    
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_mejoras.mej_obra_ubicacion
  OWNER TO sisapp;


ALTER TABLE sgm_mejoras.mej_obra ADD COLUMN rubro BIGINT;
ALTER TABLE sgm_mejoras.mej_obra
  ADD CONSTRAINT fk_obra_rubro FOREIGN KEY ( rubro)
      REFERENCES sgm_financiero.ren_rubros_liquidacion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;



ALTER TABLE sgm_app.cat_predio ADD COLUMN costo_directo NUMERIC (19,2);
ALTER TABLE sgm_app.cat_ciudadela ADD COLUMN es_marginal boolean;
