
SELECT
  CP.NUM_PREDIO, LPAD(CP.PROVINCIA::TEXT, 2, '0') AS PROVINCIA, LPAD(CP.CANTON::TEXT, 2, '0')
  AS CANTON, LPAD(CP.PARROQUIA::TEXT, 2, '0') AS PARROQUIA, LPAD(CP.ZONA::TEXT, 2, '0')
  AS ZONA, LPAD(CP.SECTOR::TEXT, 2, '0') AS SECTOR, LPAD(CP.MZ::TEXT, 3, '0') AS MZ, LPAD(CP.SOLAR::TEXT, 3, '0')
   AS SOLAR, LPAD(CP.BLOQUE::TEXT, 3, '0') AS BLOQUE, LPAD(CP.PISO::TEXT, 2, '0') AS PISO, LPAD(CP.UNIDAD::TEXT, 3, '0')
   AS UNIDAD, (CP.BLOQUE > 0 AND CP.PISO > 0 AND CP.UNIDAD > 0) ES_PH,
  CP.CLAVE_CAT, CP.PREDIALANT, CP.TIPO_PREDIO,CP.CALLE, CP.CALLE_S,
  TEN.NOMBRE AS TENENCIA, UPPER(USO.VALOR) AS USO_SOLAR,
  prrq.descripcion AS nombre_parroquia,
  CP.AREA_SOLAR, UPPER(tipovia.valor) AS tipovia, cp.avaluo_solar, cp.avaluo_construccion, cp.avaluo_municipal,
  CP.NUMERO_VIVIENDA, cp.ZONA AS zona_num,
/*Escritura*/
  es.notaria, CCT.NOMBRE AS nombre_canton, ES.AREA_SOLAR AS area_escritura, ES.UNIDAD_AREA,ES.fec_inscripcion,
  ES.fec_escritura, ES.folio_desde, ES.folio_hasta, es.alicuota,
  es.num_repertorio, prv.descripcion AS nombre_provincia,
  UPPER(does.nombre) AS dominio,
  UPPER(LOC.VALOR) AS LOC_MZ, UPPER(COB_PRD.VALOR) AS COB_PRED, UPPER(EC_PRD.VALOR) AS ECOS_PRED,
  cp.nombre_edificio AS nombre_predio, cp.requiere_perfeccionamiento, cp.anios_sin_perfeccionamiento, cp.anios_posesion, nombre_pueblo_etnia,
/*S4*/
  CP4.FRENTE1, CP4.FONDO1, otva.valor AS otro_tipo_via, material_mejora.valor AS material_mejora, tipo_obra_mejora.valor AS tipo_obra_mejora,
/*S6*/
  (select string_agg(UPPER(CI.VALOR), ', ' order by CI.ORDEN)
  from sgm_app.cat_predio_s6_has_vias CT INNER JOIN sgm_app.ctlg_item CI ON CT.ctlg_item=CI.ID WHERE predio_s6=CP6.ID) AS RODADURA,
  (SELECT string_agg(UPPER(ie.valor), ', ' ORDER BY ie.orden)
   FROM sgm_app.cat_predio_s6_has_instalacion_especial pie INNER JOIN sgm_app.ctlg_item ie ON pie.ctlg_item=ie.ID WHERE pie.predio_s6=cp6.id) AS instalaciones_especiales, cp6.tiene_aceras, cp6.tiene_bordillo, cp6.tiene_agua_potable, UPPER(abast.valor) AS abastecimiento, UPPER(eva_ser.valor) AS evacuacion_agua_serv, cp6.tpublico, UPPER(cirb.valor) AS recoleccion_bas,
  resp_fisc.ci_ruc AS resp_fisc_ci_ruc, (resp_fisc.apellidos || ' ' || resp_fisc.nombres) AS nombres_resp_fisc,
  resp_act.ci_ruc AS resp_act_ci_ruc, (resp_act.apellidos || ' ' || resp_act.nombres) AS nombres_resp_act,
  cc.nombre AS nombre_cdla, tc.nombre AS tipo_conjunto,
tpp.valor as tipo_poseedor,
afectl.valor as afectacion_lote,
cp4.tiene_hipoteca as tiene_hipoteca,
cp4.inst_financiera_hip as inst_financiera_hip,
cp4.lote_en_conflicto as lote_en_conflicto,
cp4.opbserv_lote_en_conflicto as opbserv_lote_en_conflicto,
cp4.tiene_permiso_const as tiene_permiso_const,
cp4.tiene_retiros as tiene_retiros,
cp4.tiene_adosamiento as tiene_adosamiento,
cp6.cobertura_celular as cobertura_celular,
tipo_prot.valor as tipo_protocolizacion,
es.precio_compra as precio_compra,
sts.valor AS estado_solar,
const.valor AS constructividad
/*es.lind_escr_norte AS lin_nor,
es.lind_escr_norte_con AS lin_nor_con,
es.lind_escr_este AS lin_este,
es.lind_escr_este_con AS lin_este_con,
es.lind_escr_sur AS lin_sur,
es.lind_escr_sur_con AS lin_sur_con,
es.lind_escr_oeste AS lin_oeste,
es.lind_escr_oeste_con lind_oest_con,
es.lind_inferior AS lin_inf,
es.lind_inferior_con AS lin_inf_con,
es.lind_superior AS lin_sup,
es.lind_superior_con As lin_sup_con*/
FROM
  sgm_app.CAT_PREDIO CP
  LEFT OUTER JOIN sgm_app.cat_parroquia prrq ON (prrq.codigo_parroquia=cP.parroquia AND prrq.id_canton =
  (SELECT id FROM sgm_app.cat_canton WHERE codigo_nacional = cp.canton AND id_provincia = cp.provincia ))
  LEFT OUTER JOIN sgm_app.cat_propiedad_item TEN ON (cp.propiedad=TEN.id)

  LEFT OUTER JOIN sgm_app.ctlg_item uso ON uso.id=cp.uso_solar
  LEFT OUTER JOIN sgm_app.ctlg_item tpp ON tpp.id=cp.tipo_poseedor

  LEFT OUTER JOIN (
	SELECT id_escritura, secuencia, predio, num_ficha, canton, fec_escritura, 
	num_registro, num_repertorio, folio_desde, folio_hasta, area_solar, 
	area_construccion, alicuota,fec_inscripcion, trasl_dom, 
	estado, num_tramite, anio, notaria, 
	unidad_area, fecha_protocolizacion, 
	cant_alicuotas, cant_bloques, fec_cre, fecha_resolucion, precio_compra, 
	resolucion, tipo_ph, propietario, tipo_protocolizacion
	FROM sgm_app.cat_escritura WHERE estado = 'A' ORDER BY id_escritura DESC 
   ) es ON es.predio=cp.id 
  
  LEFT OUTER JOIN sgm_app.ctlg_item const ON cp.constructividad=const.id
  LEFT OUTER JOIN sgm_app.ctlg_item tipo_prot ON tipo_prot.id=es.tipo_protocolizacion
  LEFT OUTER JOIN sgm_app.CAT_CANTON CCT ON CCT.ID=ES.CANTON
  LEFT OUTER JOIN sgm_app.cat_tipos_dominio does ON does.id=es.trasl_dom
  LEFT OUTER JOIN sgm_app.cat_provincia prv ON prv.id = cct.id_provincia
  LEFT OUTER JOIN sgm_app.cat_predio_s4 cp4 ON (cp.id=cp4.predio)
  LEFT OUTER JOIN sgm_app.cat_predio_s6 cp6 on (cp.id=cp6.predio)

  LEFT OUTER JOIN sgm_app.ctlg_item LOC on (cp4.loc_manzana=LOC.id)
  LEFT OUTER JOIN sgm_app.ctlg_item afectl on (cp4.afectacion_lote=afectl.id)
  LEFT OUTER JOIN sgm_app.ctlg_item COB_PRD on (cp4.cobertura_predominante=COB_PRD.id)
  LEFT OUTER JOIN sgm_app.ctlg_item EC_PRD on (cp4.ecosistema_relevante=EC_PRD.id)
  LEFT OUTER JOIN sgm_app.ctlg_item tipo_obra_mejora on (cp4.tipo_obra_mejora=tipo_obra_mejora.id)
  LEFT OUTER JOIN sgm_app.ctlg_item material_mejora on (cp4.ecosistema_relevante=material_mejora.id)
  left outer join sgm_app.ctlg_item sts ON cp4.estado_solar=sts.id

  LEFT OUTER JOIN sgm_app.ctlg_item tipovia on (cp.tipo_via=tipovia.id)
  LEFT OUTER JOIN sgm_app.ctlg_item abast on (cp6.abast_agua_proviene=abast.id)
  LEFT OUTER JOIN sgm_app.ctlg_item eva_ser on (cp6.evac_aguas_serv=eva_ser.id)
  LEFT OUTER JOIN sgm_app.ctlg_item cirb on (cp6.recol_basura=cirb.id)
  LEFT OUTER JOIN sgm_app.ctlg_item otva ON otva.id=cp.otro_tipo_via

  LEFT OUTER JOIN sgm_app.cat_ente resp_fisc ON resp_fisc.id=cp.responsable_fiscalizador_predial
  LEFT OUTER JOIN sgm_app.cat_ente resp_act ON resp_act.id=cp.responsable_actualizador_predial

  LEFT OUTER JOIN sgm_app.cat_tipo_conjunto tc ON tc.id=cp.tipo_conjunto
  LEFT OUTER JOIN sgm_app.cat_ciudadela cc on cc.id = cp.ciudadela

  LEFT OUTER JOIN sgm_app.cat_predio_linderos linderos ON 


  select 

CASE 
	WHEN predio_colindante is not null 
		THEN cp.clave_cat||' CON: '||cp.area_solar  
	ELSE	
		replace (lin.colindante, '-', ' CON: ')
	END AS COLINDANTE
   
  from  sgm_app.cat_predio_linderos lin
  INNER JOIN sgm_app.ctlg_item orien ON lin.orientacion = orien.id
  LEFT OUTER JOIN sgm_app.cat_predio cp ON cp.id = lin.predio
  AND lin.estado = 'A' and orien.valor = 'SUPERIOR'
	
  select 

CASE 
	WHEN predio_colindante is not null 
		THEN cp.clave_cat||' CON: '||cp.area_solar  
	ELSE	
		replace (lin.colindante, '-', ' CON: ')
	END AS COLINDANTE
  
  from  sgm_app.cat_predio_linderos lin
  INNER JOIN sgm_app.ctlg_item orien ON lin.orientacion = orien.id
  LEFT OUTER JOIN sgm_app.cat_predio cp ON cp.id = lin.predio
  AND lin.estado = 'A' and orien.valor = 'INFERIOR'

  select 

	CASE 
	WHEN predio_colindante is not null 
		THEN cp.clave_cat||' CON: '||cp.area_solar  
	ELSE	
		replace (lin.colindante, '-', ' CON: ')
	END AS COLINDANTE, lin_nor
  
  from  sgm_app.cat_predio_linderos lin
  INNER JOIN sgm_app.ctlg_item orien ON lin.orientacion = orien.id
  LEFT OUTER JOIN sgm_app.cat_predio cp ON cp.id = lin.predio
  AND lin.estado = 'A' and orien.valor = 'NORTE'

  select 

	CASE 
	WHEN predio_colindante is not null 
		THEN cp.clave_cat||' CON: '||cp.area_solar  
	ELSE	
		replace (lin.colindante, '-', ' CON: ')
	END AS COLINDANTE
  
  from  sgm_app.cat_predio_linderos lin
  INNER JOIN sgm_app.ctlg_item orien ON lin.orientacion = orien.id
  LEFT OUTER JOIN sgm_app.cat_predio cp ON cp.id = lin.predio
  AND lin.estado = 'A' and orien.valor = 'SUR'

  select 
  CASE 
	WHEN predio_colindante is not null 
		THEN cp.clave_cat||' CON: '||cp.area_solar  
	ELSE	
		replace (lin.colindante, '-', ' CON: ')
	END AS COLINDANTE
  from  sgm_app.cat_predio_linderos lin
  INNER JOIN sgm_app.ctlg_item orien ON lin.orientacion = orien.id
  LEFT OUTER JOIN sgm_app.cat_predio cp ON cp.id = lin.predio
  AND lin.estado = 'A' and orien.valor = 'ESTE'  

  select 
	CASE 
	WHEN predio_colindante is not null 
		THEN cp.clave_cat||' CON: '||cp.area_solar  
	ELSE	
		replace (lin.colindante, '-', ' CON: ')
	END AS COLINDANTE
  from  sgm_app.cat_predio_linderos lin
  INNER JOIN sgm_app.ctlg_item orien ON lin.orientacion = orien.id
  LEFT OUTER JOIN sgm_app.cat_predio cp ON cp.id = lin.predio_colindante
  WHERE lin.estado = 'A' and orien.valor = 'OESTE';



 
  


  
  
  
  

  
  WHERE
  CP.ID=58