-- Table: sgm_financiero.ren_actividad_contribuyente

-- DROP TABLE sgm_financiero.ren_actividad_contribuyente;

CREATE TABLE sgm_financiero.ren_actividad_contribuyente
(
  id bigint NOT NULL DEFAULT nextval('sgm_financiero.ren_actividad_contribuyente'::regclass),
  nombre_local character varying(150),
  predio bigint,
  contribuyente bigint,
  representante_legal bigint,
  contador bigint,
  razon_social character varying(250),
  calificacion_artesanal boolean DEFAULT false,
  numero_sucursales numeric(19,0),
  lleva_contabilidad boolean DEFAULT false,
  usuario_ingreso character varying(20),
  fecha_ingreso timestamp without time zone DEFAULT now(),
  estado boolean DEFAULT true,
  es_propio boolean,
  pagina_web character varying(150),
  direccion_actividades character varying(150),
  inicio_actividad date,
  CONSTRAINT ren_ren_actividad_por_contribuyente_pkey PRIMARY KEY (id),
  CONSTRAINT ren_actividad_x_contribuyente_contador FOREIGN KEY (contador)
      REFERENCES sgm_app.cat_ente (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ren_actividad_x_contribuyente_contribuyente FOREIGN KEY (contribuyente)
      REFERENCES sgm_app.cat_ente (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ren_actividad_x_contribuyente_representante_legal FOREIGN KEY (representante_legal)
      REFERENCES sgm_app.cat_ente (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_financiero.ren_actividad_contribuyente
  OWNER TO sisapp;


ALTER TABLE sgm_app.cat_predio_sumas_anuales_ubicacion ADD COLUMN porcentaje_avaluo numeric(20,2);
ALTER TABLE sgm_app.cat_predio_sumas_anuales_ubicacion ADD COLUMN total_cem_anio numeric(20,2);

ALTER TABLE sgm_app.cat_predio_sumas_anuales_ubicacion ADD COLUMN total_cem numeric(20,2);
ALTER TABLE sgm_app.cat_ubicacion ADD COLUMN predio bigint;

ALTER TABLE sgm_mejoras.mej_valores_obra_ubicacion ADD COLUMN total_cem_anio numeric(18,2);

ALTER TABLE sgm_mejoras.mej_obra ADD COLUMN valor_monto_obra numeric(18,2);
ALTER TABLE sgm_mejoras.mej_obra ADD COLUMN valor_subcidio_niveles_monto_obra numeric(18,2);
ALTER TABLE sgm_mejoras.mej_obra ADD COLUMN porcentaje_subsidio_niveles numeric(18,2);