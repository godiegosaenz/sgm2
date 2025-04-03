
select * from sgm_financiero.ren_pago where liquidacion in (select id from liquidacion_por_pagar_2018 )

--7360
--select id into liquidacion_por_pagar_2018 from sgm_financiero.ren_liquidacion  where estado_liquidacion = 2 and anio = 2018;


select  predio, count(predio ) from sgm_financiero.ren_liquidacion where estado_liquidacion in (2 , 1) and anio = 2018 group by predio  order by 2 desc