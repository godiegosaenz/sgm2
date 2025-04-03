WITH consulta as (
SELECT clave_cat , p.num_predio,  trim(sgm_app.propietarios_por_predio(p.id)) as propietarios, l.total_pago as total_2019,
(SELECT ll.total_pago FROM sgm_financiero.ren_liquidacion ll INNER JOIN sgm_app.cat_predio pp on pp.id = ll.predio
WHERE ll.anio = 2018 AND ll.estado_liquidacion IN (1,2) 
AND ll.tipo_liquidacion = 13 AND pp.id = p.id) as total_pago_2018
FROM sgm_financiero.ren_liquidacion l
INNER JOIN sgm_app.cat_predio p on p.id = l.predio
WHERE l.anio = 2019 AND l.estado_liquidacion IN (2)
AND l.tipo_liquidacion = 13  and p.es_avaluo_verificado = true
)
SELECT * , (c.total_2019 - c.total_pago_2018) as diff  
FROM consulta c WHERE (c.total_2019 - c.total_pago_2018) < 0 
ORDER BY 6



