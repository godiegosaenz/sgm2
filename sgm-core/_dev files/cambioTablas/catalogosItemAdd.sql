SELECT setval('sgm_app.cat_edf_prop_id_seq', 96, true);
/*
ALTER SCHEMA agenda RENAME TO sgm_agenda;
ALTER SCHEMA app1 RENAME TO sgm_app;
ALTER SCHEMA application RENAME TO sgm_application;
ALTER SCHEMA bancos RENAME TO sgm_bancos;
ALTER SCHEMA bitacora RENAME TO sgm_bitacora;
ALTER SCHEMA censocat RENAME TO sgm_censocat;
ALTER SCHEMA financiero RENAME TO sgm_financiero;
ALTER SCHEMA flow RENAME TO sgm_flow;
ALTER SCHEMA geodata RENAME TO sgm_geodata;
ALTER SCHEMA historico RENAME TO sgm_historico;
ALTER SCHEMA secuencias RENAME TO sgm_secuencias;
*/

	
CREATE TABLE sgm_app.cat_predio_s6_has_instalacion_especial
(
  predio_s6 bigint,
  ctlg_item bigint,
  CONSTRAINT cat_predio_s6_has_instalacion_especial_ctlg_item_fkey FOREIGN KEY (ctlg_item)
      REFERENCES sgm_app.ctlg_item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cat_predio_s6_has_instalacion_especial_predio_s6_fkey FOREIGN KEY (predio_s6)
      REFERENCES sgm_app.cat_predio_s6 (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sgm_app.cat_predio_s6_has_instalacion_especial
  OWNER TO postgres;

ALTER TABLE sgm_app.cat_predio_s4 ADD COLUMN tipo_obra_mejora bigint  REFERENCES sgm_app.ctlg_item (id);
ALTER TABLE sgm_app.cat_predio_s4 ADD COLUMN material_mejora bigint  REFERENCES sgm_app.ctlg_item (id);
ALTER TABLE sgm_app.cat_predio_s4 ADD COLUMN area_mejora numeric(10,6)  ;
ALTER TABLE sgm_app.cat_predio_s4 ADD COLUMN estado_mejora bigint  REFERENCES sgm_app.ctlg_item (id);
ALTER TABLE SGM_APP.CAT_PREDIO ADD COLUMN tenencia_vivienda BIGINT REFERENCES SGM_APP.CTLG_ITEM(ID);


	ALTER TABLE sgm_app.cat_predio_s6 ADD COLUMN abas_agua_recibe bigint  REFERENCES sgm_app.ctlg_item (id);
	
	ALTER TABLE sgm_app.cat_predio_s6 RENAME COLUMN abast_agua  TO abast_agua_proviene;
	
	
		ALTER TABLE sgm_app.cat_predio ADD COLUMN unidad_medida bigint REFERENCES sgm_app.ctlg_item (id);
	
	ALTER TABLE sgm_app.cat_predio ADD COLUMN ente_horizontal bigint REFERENCES sgm_app.cat_ente (id);
	ALTER TABLE sgm_app.cat_predio ADD COLUMN clasif_horizontal bigint REFERENCES sgm_app.ctlg_item (id);
	ALTER TABLE sgm_app.cat_predio ADD COLUMN num_hogares smallint;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN num_habitaciones smallint;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN num_dormitorios smallint;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN ocupacion_viv_horizontal boolean;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN tipo_vivienda_horizontal boolean;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN num_espacios_banios smallint;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN num_celulares smallint;
	ALTER TABLE sgm_app.cat_predio ADD COLUMN otro_tipo_via bigint REFERENCES sgm_app.ctlg_item (id);

	
ALTER TABLE sgm_app.cat_predio ADD COLUMN responsable_actualizador_predial bigint REFERENCES sgm_app.cat_ente (id);	
ALTER TABLE sgm_app.cat_predio ADD COLUMN responsable_fiscalizador_predial bigint REFERENCES sgm_app.cat_ente (id);

ALTER TABLE sgm_app.cat_predio_s4 ADD COLUMN area_grafica_lote numeric(10,3);	 
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN valor_cultural bigint REFERENCES sgm_app.ctlg_item (id) ; 
ALTER TABLE sgm_app.cat_predio_edificacion ADD COLUMN uso_constructivo_piso bigint REFERENCES sgm_app.ctlg_item (id) ;


INSERT INTO sgm_app.ctlg_catalogo(nombre)
    VALUES ('predio.instalacion_especial'); --33
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'No tiene', 'noTiene', 1);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Ascensor', 'ascensor', 2);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Circuito Cerrado de Televisión', 'circuito_television', 3);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Montacargas', 'montacargas', 4);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Sistema Alternativo de Energía Eléctrica', 'sistema_alternativo_energia_electrica', 5);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Sistema Central de Aire Acondicionado', 'sistema_acondicionado', 6);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Sistema Contra Incendios', 'sistema_incendios', 7);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Sistema de Gas Centralizado ', 'sistema_gas', 8);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Sistema de Ventilación Mecánica', 'sistema_ventilación', 9);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.instalacion_especial'), 'Sistema de Voz y Datos ', 'sistema_voz_datos', 10);


INSERT INTO sgm_app.ctlg_catalogo(nombre)
    VALUES ( 'predio.otro_tipo_via');--34
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.otro_tipo_via'), 'No tiene',  'noTiene', 1);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.otro_tipo_via'), 'Aérea', 'aerea', 2);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.otro_tipo_via'), 'Férrea', 'ferrea', 3);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.otro_tipo_via'), 'Fluvial', 'fluvial', 4);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='predio.otro_tipo_via'), 'Marítima', 'maritima', 5);

	
INSERT INTO sgm_app.ctlg_catalogo(nombre)
    VALUES ( 'edif.valor_cultural'); --35
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.valor_cultural'), 'No tiene',  'noTiene', 1);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.valor_cultural'), 'Ancestral', 'ancestral', 2);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.valor_cultural'), 'Arquitectónico', 'arquitectonico', 3);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.valor_cultural'), 'Histórico', 'historico', 4);




INSERT INTO sgm_app.cat_edf_categ_prop(nombre, is_porcentual, gui_orden)
    VALUES ('Uso Constructivo del Piso', TRUE, 11);
	
INSERT INTO sgm_app.ctlg_catalogo(nombre)
    VALUES ('edif.uso_constructivo_piso'); --36
	
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'No tiene', 'noTiene', 1);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Balcón', 'balcon', 2);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Banco', 'banco', 3);

INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Baños Sauna / Turco / Hidroma', 'banios', 4);


INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Bodega', 'bodega', 5);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Casa', 'casa', 6);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Casa comunal', 'casa_comunal', 7);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Cuarto de máquinas / Basura', 'maquinas', 8);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Departamento', 'departamento', 9);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Garita / Guardianía', 'garita', 10);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Gimnasio', 'gym', 11);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Guardería', 'guarderia', 12);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Hospital', 'hospital', 13);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Hostal', 'hostal',14);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Hostería', 'hosteria', 15);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Hotel', 'hotel', 16);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Iglesia', 'iglesia', 17);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Lavandería ', 'lavanderia', 18);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Local comercial', 'local_comercial', 19);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Malecón', 'malecon', 20);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Maternidad', 'maternidad', 21);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Mercado', 'mercado', 22);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Mirador', 'mirador', 23);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Motel', 'motel', 24);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Museo', 'museo', 25);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Nave industrial', 'nave_industrial', 26);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Oficina', 'oficina', 27);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Orfanato', 'orfanato', 28);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Organismos Internacionales', 'organismos_internacionales', 29);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Otros', 'otros', 30);
    

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Parqueadero', 'parqueadero', 31);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Patio / Jardín', 'patio', 32);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Pensión', 'pension', 33);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Plantel avícola', 'plantel_avicola', 34);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Plaza de toros', 'plaza_toros', 35);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Porqueriza', 'porqueriza', 36);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Recinto militar', 'recinto_militar', 37);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Recinto policial', 'recinto_policial', 38);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Reclusorio', 'reclusorio', 39);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Representaciones Diplomáticas', 'representaciones_diplomaticas', 40);



INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Restaurante', 'restaurante', 41);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Retén policial', 'reten_policial', 42);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Sala comunal', 'sala_comunal', 43);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Sala de Cine', 'sala_cine', 44);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Sala de exposición', 'sala_exposición', 45);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Sala de Juegos', 'sala_juegos', 46);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Sala de Ordeño', 'sala_ordenio', 47);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Salas de Culto / Templo', 'templo', 48);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Salas de Hospitalización', 'salas_hospitalización', 49);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Salón de eventos', 'salón_eventos', 50);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Teatro', 'teatro', 51);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Terminal de transferencia', 'transferencia', 52);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Terminal Interprovincial', 'interprovincial', 53);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Terraza', 'terraza', 54);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.uso_constructivo_piso'), 'Unidad de Policía Comunitaria', 'policia_comunitaria', 55);


	
	
	INSERT INTO sgm_app.ctlg_catalogo(
            nombre)
    VALUES ('edif.unidad_medida'); --37

	
	INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'No tiene', 'noTiene', 1);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'M2', 'm2', 2);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'Ha', 'ha', 3);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'Cuadra', 'cuadra', 4);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'Solar', 'solar', 5);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'Leguas', 'leguas', 6);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'Acre', 'acre', 7);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT ID FROM sgm_app.ctlg_catalogo WHERE NOMBRE='edif.unidad_medida'), 'Otra', 'otra', 8);
	
	

	
INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Mampostería Soportante', false, 11);

	
	INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Mampostería Soportante'), 'No tiene', 1);

INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Mampostería Soportante'), 'Adobe', 2);

INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Mampostería Soportante'), 'Bloque', 3);


INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Mampostería Soportante'), 'Ladrillo', 4);


INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Mampostería Soportante'), 'Piedra', 5);


INSERT INTO sgm_app.cat_edf_prop(categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Mampostería Soportante'), 'Tapial', 6);
INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Columnas', false, 12);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'No tiene', 1);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Acero', 2);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Caña', 3);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Hierro', 4);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Hormigón armado', 5);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Madera común', 6);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Pilotaje de Hormigón Armado', 8);
INSERT INTO sgm_app.cat_edf_prop(
            categoria, nombre,  orden)
    VALUES ((SELECT ID FROM sgm_app.cat_edf_categ_prop WHERE NOMBRE='Columnas'), 'Mixto( Metal y Hormigón)', 7);
	
UPDATE sgm_app.cat_edf_prop
   SET nombre= 'Madera Común'
 WHERE id =19;


INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Bahareque', 11);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Ferro Cemento', 12);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Gypsum', 13);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Prefabricado Hormigón Simple', 14);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Madera Procesada Fina', 15);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Malla', 16);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Zinc', 17);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Lona', 18);
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES (3, 'Piedra', 19);
    
INSERT INTO app1.cat_edf_categ_prop(nombre, is_porcentual, gui_orden) VALUES ('Revestimiento Cubierta', false, 14);	
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'No tiene', 1, 175, 0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Arena cemento', 2, 101, 0.77097);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Asbesto Cemento', 3, 0, 0);
INSERT INTO app1.cat_edf_prop( categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Cady Paja', 4, 168, 163319);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Cerámica', 5, 106, 223784);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Chova', 6, 0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Ferro Cemento', 7, 104, 172031);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Madera Ladrillo', 8, 0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Policarbonato', 9, 0,0);
INSERT INTO app1.cat_edf_prop( categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Teja ordinaria', 10, 103,189429);    
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Teja vidriada', 11, 102, 241263);   
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Tejuelo', 12,0,0);    
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'Zinc', 13,105,145147); 
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Cubierta'),'BALDOSA CEMENTO', 14,107,157705); 


INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Revestimiento Pared', false, 13);

  INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'No tiene', 1);

    
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Calciminas', 2);



INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Caucho', 3);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Esmalte', 4);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Graniplast', 5);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Alucobond', 6);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Cerámica', 7);


INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Fachaleta', 8);


INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Revestimiento Pared'),
    'Laca', 9);




 
	INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Vigas', false, 15);	 
		
		
		
		
		INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'No tiene', 1);

    
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'Acero', 2);



INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'Caña', 3);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'Hierro', 4);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'Hormigón Armado', 5);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'Madera Común', 6);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vigas'),
    'Madera Procesada Fina', 7);




 INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Contrapiso', false, 16);	 

INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Entrepiso', false, 17);	 
	
	
	
	
	
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'No tiene', 1);

    
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'Acero Hormigón', 2);



INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'Hierro - Hormigón', 3);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'Losa Hormigón Armado', 4);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'Madera - Hormigón', 5);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'Madera Común', 6);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Entrepiso'),
    'Madera procesada fina', 7);



 	INSERT INTO sgm_app.cat_edf_prop(
		     categoria, nombre,  orden)
	    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Contrapiso'),
	    'No tiene', 1);

	    
	INSERT INTO sgm_app.cat_edf_prop(
		     categoria, nombre,  orden)
	    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Contrapiso'),
	    'Hormigón simple', 2);



	INSERT INTO sgm_app.cat_edf_prop(
		     categoria, nombre,  orden)
	    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Contrapiso'),
	    'Ladrillo visto', 3);

	INSERT INTO sgm_app.cat_edf_prop(
		     categoria, nombre,  orden)
	    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Contrapiso'),
	    'Tierra', 4);

	INSERT INTO sgm_app.cat_edf_prop(
		     categoria, nombre,  orden)
	    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Contrapiso'),
	    'Caña', 5);




	 
    
INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Ventanas', false, 18);	 
	

			


	  
	    		
		INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'No tiene', 1);

    
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'Aluminio', 2);



INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'Caña', 3);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'Hierro', 4);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'Madera común', 5);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'Plástico preformado', 7);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Ventanas'),
    'Madera Procesada Fina', 6);



		    
INSERT INTO sgm_app.cat_edf_categ_prop(
	nombre, is_porcentual, gui_orden)
    VALUES ('Vidrios', false, 18);	 
	
	
	
		
		INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vidrios'),
    'No tiene', 1);

    
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vidrios'),
    'Malla', 2);



INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vidrios'),
    'Vidrio común', 3);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vidrios'),
    'Vidrio templado', 4);

INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre,  orden)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Vidrios'),
    'Vidrio Catedral', 5);






INSERT INTO app1.cat_edf_categ_prop(id,nombre, is_porcentual, gui_orden) VALUES (nextval('app1.app_uni_seq'::regclass),'Puertas', false, 19);	    	
INSERT INTO app1.cat_edf_prop(id, categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'), 'No tiene', 1, 110,0.0000);  
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'), 'Aluminio-Vidrio', 2, 113, 251029);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'),'Hierro', 3, 114, 182345);
INSERT INTO app1.cat_edf_prop(id, categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'),'Madera Panelada', 4, 0, 0);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'), 'Madera Tamboreada', 5, 0, 0);
INSERT INTO app1.cat_edf_prop(id, categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'),'Metálica Enrollable', 6, 116, 139559);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'),'Plástico Preformado', 7, 0, 0);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'), 'Tol', 8, 0, 0);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'), 'Caña', 10,0,0);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'), 'Malla', 11,0,0);
INSERT INTO app1.cat_edf_prop(id,categoria, nombre,  orden, codigo, peso)
    VALUES (nextval('app1.app_uni_seq'::regclass),(SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Puertas'),'Hierro - MADERA', 3,0,0);
INSERT INTO app1.cat_edf_categ_prop( nombre, is_porcentual, gui_orden) VALUES ('Revestimiento Interior', false, 10);
INSERT INTO app1.cat_edf_prop( categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'No tiene', 1, 68, 0.00000);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'MADERA FINA', 2, 69, 379834);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'MADERA COMUN', 3, 70, 269949);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'AREAN CEMENTO', 4, 71, 103946);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'TIERRA', 5, 0, 0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'AZULEJO / Cerámica', 6,73,318366);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'MARMOL / MARMOLINA', 7,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'GRAFIADO / CHAFADO / AFINES', 8,74,110194);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'ALUMINIO', 9,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'PIEDRA / LADRILLO ORNAMENTAL', 10,75,337981);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Calciminas', 11,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Caucho', 12,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Esmalte', 13,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Graniplast', 14,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Alucobond', 15,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Fachaleta', 16,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Interior'),'Laca', 17,0,0);


INSERT INTO app1.cat_edf_categ_prop( nombre, is_porcentual, gui_orden) VALUES ('Revestimiento Exterior', false, 11);
INSERT INTO app1.cat_edf_prop( categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'No tiene', 1, 76,0.00000);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'MADERA FINA', 2, 77, 195017);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'MADERA COMUN', 3, 78,137579);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'AREAN CEMENTO', 4, 79,0.47863);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'TIERRA', 5, 80,0.38940);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'AZULEJO / Cerámica', 6,73,318366);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'MARMOL / MARMOLINA', 7,81,130962);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'GRAFIADO / CHAFADO / AFINES', 8,82,0.50864);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'ALUMINIO', 9,83,273806);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'PIEDRA / LADRILLO ORNAMENTAL', 10,84,0.59558);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Calciminas', 11,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Caucho', 12,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Esmalte', 13,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Graniplast', 14,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Alucobond', 15,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Fachaleta', 16,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Exterior'),'Laca', 17,0,0);



INSERT INTO app1.cat_edf_categ_prop( nombre, is_porcentual, gui_orden) VALUES ('Revestimiento Escalera', false, 11);
INSERT INTO app1.cat_edf_prop( categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'No tiene', 1, 85,0.00000);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'MADERA FINA', 2, 86,0.05218);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'MADERA COMUN', 3, 87,0.03265);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'AREAN CEMENTO', 4, 88,0.01654);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'ACERO', 5, 80,0.38940);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'AZULEJO / Cerámica', 6,73,318366);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'MARMOL / MARMOLINA', 7,90,0.44810);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'BALDOSA CEMENTO / VINIL', 8,92,0.03353);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'ALUMINIO', 9,83,273806);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'PIEDRA / LADRILLO ORNAMENTAL', 10,91,0.17978);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'PISO FLOTANTE', 11,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'ALFONBRA', 12,0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre,  orden, codigo, peso)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Revestimiento Escalera'),'PORCELANATO', 13,0,0);
	  
	  
	  INSERT INTO sgm_app.ctlg_catalogo(
             nombre)
    VALUES ('predio.tipo_obra_mejora');

	    
	INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'No tiene', 'noTiene',1  );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Aceras y cercas', 'aceras', 2);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Canal de riego ocasional', 'canal_riego', 3);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Canal de Riego Permanente', 'canal_riego_permanente	', 4);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Cerramiento', 'cerramiento', 5);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Desecación de Pantanos', 'pantanos', 6);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Establo', 'establo', 7);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Estanque/reservorio', 'estanque', 8);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Funiculares', 'funiculares', 9);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Galpón Avícola', 'avicola', 10);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Invernaderos', 'invernaderos', 11);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Muro de Contención', 'contención', 12);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Parques, Jardines y Parques', 'parques_jardines', 13);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Piscina Camaronera', 'camaronera',14);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Piscina Piscícola', 'piscina_piscícola', 15);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Piscinas de Natación', 'natación', 16);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Pista de Aterrizaje', 'pista_aterrizaje', 17);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Planta de Pos Cosecha ', 'pos_cosecha', 18);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Pozo de riego', 'pozo_riego', 19);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Rellenos de Quebradas', 'quebradas', 20);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Repavimentación Urbana', 'repavimentación_urbana', 21);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Sala de Ordeño', 'sala_ordenio', 22);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Silo/Almacenamientos', 'almacenamientos', 23);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Tendales', 'tendales', 24);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Vías Internas', 'vías_internas', 25);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Viveros', 'viveros', 26);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.tipo_obra_mejora'), 'Otros', 'otros', 27);


	
		INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.abastecimiento_agua'), 'No tiene', 'noTiene' );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.abastecimiento_agua'), 'Río, Vertiente, Acequia', 'río_acequia');

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.abastecimiento_agua'), 'Pozo', 'pozo');

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.abastecimiento_agua'), 'Otro (Agua lluvia)', 'otros_lluvia');
	
	
	
	
	
	
	  INSERT INTO sgm_app.ctlg_catalogo(
             nombre)
    VALUES ('predio.medio_abas_agua');
	
	
	
	
	INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.medio_abas_agua'), 'No tiene', 'noTiene' );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.medio_abas_agua'), 'Por Tubería dentro de la Vivienda', 'tuberia_dentro_vivienda');

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.medio_abas_agua'), 'Por Tubería fuera de la Vivienda pero dentro del Edificio, Lote o Terreno', 'tuberia_dentro_edif');

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.medio_abas_agua'), 'Por Tubería Fuera del Edificio, Lote o Terreno', 'tuberia_fuera_edif');


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.medio_abas_agua'), 'No recibe agua por Tubería sino por Otros Medios', 'otros_medios');
	
	
	
	
	
		INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.evac_aguas_serv'), 'Con Descarga Directa al Mar, Río,  Lago o Quebrada', 'descarga_directa' );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.evac_aguas_serv'), 'Conectado a Pozo Ciego', 'pozo_ciego');

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.evac_aguas_serv'), 'Letrina', 'letrina');
	
	
	
	INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.abaste_electrico'), 'Generador de luz (Planta Eléctrica)', 'generador_luz');

 
	

UPDATE sgm_app.ctlg_item
   SET  valor='Red de Empresa Eléctrica de Servicio Público'
 WHERE valor = 'Combustibles Fosiles';

UPDATE sgm_app.ctlg_item
   SET  valor='Otros (Sistemas Eolicos ...)'
 WHERE valor = 'Sistemas Eolicos';

 
		  INSERT INTO sgm_app.ctlg_catalogo(
				 nombre)
		VALUES ('predio.recol_basura_medio');



		INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'No tiene', 'noTiene',1  );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'Por Carro Recolector', 'recolector_carro', 2);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'La arrojan en Terreno Baldío o Quebrada', 'arroja_terreno', 3);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'La arrojan al Río, Acequia o Canal', 'arroja_rio', 4);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'Quema', 'quema', 5);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'La Entierran', 'entierran', 6);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.recol_basura_medio'), 'De Otra Forma', 'otra_manera', 7);
	
	
	

	
		  INSERT INTO sgm_app.ctlg_catalogo(
		     nombre)
	    VALUES ('predio.clasif_unidad_vivienda');
		
		
		
			INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'No Aplica', 'noAplica',1  );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Bodega', 'bodega', 2);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Casa', 'casa_hori', 3);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Choza', 'chozo_hori	', 4);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Covacha', 'covacha', 5);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Cuarto en Casa de Inquilinato', 'pantanos', 6);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Departamento en casa o Edificio', 'depart_casa_edif', 7);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Local Comercial', 'comercial_local', 8);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Mediagua', 'mediagua', 9);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Oficina', 'oficina', 10);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Otra Vivienda Particular', 'particular', 11);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Parqueadero', 'parqueadero', 12);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Rancho', 'rancho', 13);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.clasif_unidad_vivienda'), 'Villa', 'villa',14);
	
	
	
					  INSERT INTO sgm_app.ctlg_catalogo(
					 nombre)
				VALUES ('predio.material_construccion');
				
				
				
	INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.material_construccion'), 'No Especificado', 'noEspecificado',1  );


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.material_construccion'), 'Hormigón Simple', 'h_simple', 2);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.material_construccion'), 'Tapiales', 'mat_tapiales', 3);

INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.material_construccion'), 'Metálica', 'mat_metalica', 4);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.material_construccion'), 'Madera', 'madera', 5);


INSERT INTO sgm_app.ctlg_item(
             catalogo, valor, codename, orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo 
    WHERE nombre = 'predio.material_construccion'), 'Hormigón Armado', 'hormigon_armado', 6);

	
	
	UPDATE sgm_app.cat_edf_prop
   SET nombre='Madera Común'
 WHERE nombre = 'Madera' and categoria = (SELECT id from sgm_app.ctlg_catalogo WHERE nombre = 'Pared');
 
 
 
 INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Pared'),
    'Madera procesada fina');

    INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Pared'),
    'Zinc');

    INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Pared'),
    'Gypsum');

    INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Pared'),
    'Lona');

        INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Pared'),
    'Ferro Cemento');




        INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Pared'),
    'Bahareque');
	
	
	
	INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Cubierta'),
    'Madera Comun');

    
INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Cubierta'),
    'Madera procesada fina');

    INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Cubierta'),
    'Acero');

    INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Cubierta'),
    'Caña');

    INSERT INTO sgm_app.cat_edf_prop(
             categoria, nombre)
    VALUES ((SELECT id FROM sgm_app.cat_edf_categ_prop WHERE nombre = 'Cubierta'),
    'Hierro');
	
	
	
	INSERT INTO app1.cat_edf_prop(categoria, nombre, codigo, orden)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Tumbado'), 'Madera triplex',0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre, codigo, orden)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Tumbado'),'Madera procesada fina', 94, 220660);
INSERT INTO app1.cat_edf_prop(categoria, nombre, codigo, orden)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Tumbado'),'Malla enlucida',0,0);
INSERT INTO app1.cat_edf_prop(categoria, nombre, codigo, orden)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Tumbado'),'Fibra mineral',100, 205198);
INSERT INTO app1.cat_edf_prop(categoria, nombre, codigo, orden)
    VALUES ((SELECT id FROM app1.cat_edf_categ_prop WHERE nombre = 'Tumbado'),'Caña Enlucida', 0,0);


ALTER TABLE SGM_APP.CAT_PREDIO ADD COLUMN tenencia_vivienda BIGINT REFERENCES SGM_APP.CTLG_ITEM(ID);

INSERT INTO sgm_app.ctlg_catalogo(nombre) VALUES ('predio.tenencia_vivienda');
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Anticresis', 'anticresis',1);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Arrendada', 'arrendada',2);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Por servicios', 'porServicios',3);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Prestada o cedida (no pagada)', 'prestadaCedida',4);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Propia (regalada, donada, heredada o por posesión)', 'propia',5);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Propia y la está pagando', 'propiaYPagando',6);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.tenencia_vivienda'), 'Propia y totalmente pagada', 'propiaPagada',7);


ALTER TABLE SGM_APP.CAT_PREDIO ADD COLUMN clasificacion_suelo BIGINT REFERENCES SGM_APP.CTLG_ITEM(ID);

INSERT INTO sgm_app.ctlg_catalogo(nombre) VALUES ('predio.clasificacion_suelo');
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'No aplica', 'noAplica',1);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'Consolidado (Urbana)', 'consolidado',2);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'No consolidado (Urbana)', 'noConsolidado',3);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'De protección (Urbana)', 'deProteccionUrbano',4);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'De producción (Rural)', 'deProduccionRural',5);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'De extracción (Rural)', 'deExtraccion',6);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'De expansión urbana (Rural)', 'deExpansion',7);
INSERT INTO sgm_app.ctlg_item(catalogo, valor, codename,  orden)
    VALUES ((SELECT id FROM sgm_app.ctlg_catalogo WHERE nombre = 'predio.clasificacion_suelo'), 'De protección (Rural)', 'deProteccionRural',7);

	