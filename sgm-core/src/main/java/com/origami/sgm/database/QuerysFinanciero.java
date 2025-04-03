/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.database;

/**
 *
 * @author Joao Sanga
 */
public class QuerysFinanciero {

    public static String getRenTransacciones = "SELECT c FROM RenTransacciones c WHERE c.estado = true ORDER BY c.transaccionPadre asc";

    //Obtiene las transacciones por el id del padre.
    public static String getRenTransaccionesRubros = "SELECT c FROM RenTransacciones c WHERE c.transaccionPadre = :transaccionPadre AND c.estado = :estado ORDER BY c.descripcion DESC";

    public static String getIdEntidadBanco = "SELECT r.id FROM RenEntidadBancaria r where UPPER(r.descripcion) = :descripcion";
    public static String getRenFactorPorMetroList = "SELECT r.id FROM RenFactorPorMetro r where estado=true";
    public static String getTipoTransacciones = "SELECT r FROM RenTipoTransaccion r where r.estado = true ";
    public static String getRenEntidadBancariaList = "SELECT r FROM RenEntidadBancaria r where r.estado = true ORDER BY r.descripcion asc";
    public static String getRenLiquidacionesList = "SELECT r FROM RenLiquidacion r ORDER BY r.fechaIngreso";
    public static String getRenLiquidacionesByPredioYTipoLiquidacion = "SELECT r FROM RenLiquidacion r WHERE r.predio = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion";
    public static String getRenLiquidacionesByTipoLiquidacionYEstado = "SELECT r FROM RenLiquidacion r WHERE r.predio = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion and r.estadoLiquidacion.id = :estadoLiquidacion";
    public static String getRenLiquidacionesByTipoLiquidacionEstadoPeriodo = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion and r.estadoLiquidacion.id = :estadoLiquidacion and r.estaExonerado = false and r.predio.sector > :sector and r.anio between :anioAnt and :anioAct "
            + "and r.predio not in (select distinct e.predio from RenLiquidacion e where e.anio = (:anioAnt -1) and e.estaExonerado = false and e.predio.sector >= :sector and e.tipoLiquidacion = :tipoLiquidacion and e.estadoLiquidacion.id = :estadoLiquidacion)";

    public static String getPrediosExcluidos = "SELECT e FROM RenLiquidacion e WHERE e.anio = :anioAnt and e.estaExonerado = false and e.predio.sector > :sector and e.tipoLiquidacion.id = :tipoLiquidacion and e.estadoLiquidacion.id = :estadoLiquidacion";

    public static String getRenLiquidacionesByTipoLiquidacionEstadoPeriodoNativo = "SELECT r.* FROM sgm_financiero.Ren_Liquidacion r "
            + "inner join  sgm_app.cat_predio cp on cp.id = r.predio "
            + "WHERE r.tipo_Liquidacion = :tipoLiquidacion and r.estado_Liquidacion = :estadoLiquidacion and r.Exonerado = false and cp.sector > :sector and r.anio between :anioAnt and :anioAct  "
            + "            and r.predio not in (select distinct e.predio from sgm_financiero.Ren_Liquidacion e "
            + "            inner join  sgm_app.cat_predio px on px.id = e.predio "
            + "             where e.anio < :anioAnt and e.exonerado = false and px.sector > :sector and e.tipo_Liquidacion = :tipoLiquidacion and e.estado_Liquidacion = :estadoLiquidacion) order by r.anio asc ";

    public static String getRenLiquidacionesByPredioYTipoLiquidacionExon = "SELECT r FROM RenLiquidacion r WHERE r.predio = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion AND r.estadoLiquidacion = 4";
    public static String getRenLiquidacionesByPredioRusticoYTipoLiquidacion = "SELECT r FROM RenLiquidacion r WHERE r.predioRustico = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion";
    public static String getValorRangoAvaluoRustico = "SELECT r.porcentaje FROM CatRangoAvaluosRustico WHERE :valorPorcentaje BETWEEN r.desde AND r.hasta";
    public static String getRenLiquidacionByTipoYNumLiquidacion = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion.id = :tipoLiq AND r.numLiquidacion = :numLiq";
    public static String getRenLiquidacionesByPredioYTipoLiquidacionNoPagadas = "SELECT r FROM RenLiquidacion r WHERE r.predio = :predio AND r.anio = :anio AND r.tipoLiquidacion.id = :idTipoLiquidacion AND r.estadoLiquidacion.id = 2";
    public static String getRenLiquidacionesListByPredio = "SELECT r FROM  r WHERE r.numPredio = :numPredio ORDER BY r.fechaIngreso";
    public static String getRenTipoValorList = "SELECT r FROM RenTipoValor r WHERE r.estado = true";
    public static String getRenActividadesComercialesList = "SELECT r FROM RenActividadComercial r WHERE r.estado = true";
    //public static String getRenTransaccionesPadres = "SELECT r FROM RenTipoLiRenLiquidacionquidacion r WHERE r.transaccionPadre = :idPadre AND r.nombreTransaccion IS NOT NULL AND r.mostrarTransaccion = true "
    //      + " ORDER BY r.transaccionPadre, r.nombreTransaccion ASC";
    
    public static String getRenTransaccionesPadres = "SELECT r FROM RenTipoLiquidacion r WHERE r.transaccionPadre = :idPadre AND r.nombreTransaccion IS NOT NULL AND r.mostrarTransaccion = true "
            + " ORDER BY r.transaccionPadre, r.nombreTransaccion ASC";
 
    public static String getRenTransaccionesHijos = "SELECT r FROM RenTipoLiquidacion r WHERE r.transaccionPadre=:idPadre AND r.nombreTransaccion IS NOT NULL AND r.mostrarTransaccion = true AND  r.tipoTransaccion = :tTransaccion"
            + " ORDER BY r.transaccionPadre, r.nombreTransaccion ASC";
    public static String getRenTipoLiquidacionList = "SELECT r FROM RenTipoLiquidacion r where r.nombreTransaccion IN (select distinct m.nombreTransaccion  "
            + "                      from RenTipoLiquidacion m  ) ORDER BY r.transaccionPadre, r.nombreTransaccion ASC ";

    public static String getRenTipoLiquidacionByCodTitRep = "SELECT r FROM RenTipoLiquidacion r WHERE r.estado = true AND r.codigoTituloReporte IN (93, 95, 11, 14, 15, 97, 7) ORDER BY r.nombreTransaccion ASC";
    public static String getRenTipoLiquidacionByCodTitRepPA = "SELECT r FROM RenTipoLiquidacion r WHERE r.estado = true AND r.codigoTituloReporte IN (93, 95) ORDER BY r.nombreTransaccion ASC";
    public static String getRenTipoLiquidacionByCodTitReporte = "SELECT r FROM RenTipoLiquidacion r WHERE r.codigoTituloReporte = :codtitrep";
    /**
     * Filtra por el codigo de titulo de reporte: 11, 14, 15, 98, 53, 206
     *
     */
    public static String getRenTipoLiquidacionByCodTitRepLC = "SELECT r FROM RenTipoLiquidacion r WHERE r.estado = true AND r.codigoTituloReporte IN (11, 14, 15, 98, 53, 206) ORDER BY r.nombreTransaccion ASC";
    /**
     * Filtra por el codigo de titulo de reporte: 13, 16, 27, 28, 29, 119, 120,
     * 121, 122, 188
     */
    public static String getRenTipoLiquidacionByCodTitRepEdf = "SELECT r FROM RenTipoLiquidacion r WHERE r.estado = true AND r.codigoTituloReporte IN (195, 191, 16, 27, 175, 176,119, 120, 121, 122, 13, 29, 123, 124, 198, 199, 201, 202, 203, 204, 227, 214, 218, 217) ORDER BY r.nombreTransaccion ASC";

    public static String getRenLocalTipoAccesorioList = "SELECT r FROM RenLocalTipoAccesorio r WHERE r.estado = true";

    public static String getValorBaseTasaHabilitacion = "SELECT r FROM RenFactorPorMetro r WHERE :metros BETWEEN r.desde AND r.hasta AND r.anioDesde=2015";
    public static String getValorBasePatenteComercial = "SELECT r FROM RenFactorPorCapital r WHERE :capital BETWEEN r.desde AND r.hasta";

    public static String getRenTipoValorSelectIn = "SELECT r FROM RenTipoValor r WHERE r.id in (:ids)";

    public static String getNumSolicitudExoneracion = "SELECT f.id FROM FnSolicitudExoneracion f WHERE f.exoneracionTipo.id = :exoneracionTipo AND f.anioFin = :anioFin AND f.predio.id = :idPredio";
    public static String getSolicitudExoneracionByTramite = "SELECT f FROM FnSolicitudExoneracion f WHERE f.tramite = :idTramite AND f.estado.id = :estado";
    public static String getSolicitudesExoneracionPendientes = "SELECT f FROM FnSolicitudExoneracionAutomatica f WHERE f.estado.id = 2";

    public static String getSalarioByAnio = "SELECT f FROM CtlgSalario f WHERE f.anio = :anio";

    public static String getSolicitudExoneracionByPredioAndTipo = "SELECT f FROM FnSolicitudExoneracion f WHERE f.predio = :predio AND f.exoneracionTipo = :tipo";
    public static String getSolicitudExoneracionByPredioAndTipoAndAnioFin = "SELECT f FROM FnSolicitudExoneracion f WHERE f.predio = :predio AND f.exoneracionTipo = :tipo AND f.anioInicio<=:anio AND f.anioFin>=:anio AND f.estado IN (1,2)";
    public static String getSolicitudExoneracionByPredioRuralAndTipoAndAnioFin = "SELECT f FROM FnSolicitudExoneracion f INNER JOIN f.solicitudExoneracionPredios sp WHERE sp.predioRural = :predio AND f.exoneracionTipo = :tipo AND f.anioInicio<=:anio AND f.anioFin>=:anio";
    public static String getSolicitudExoneracionByPredioRural2017AndTipoAndAnioFin = "SELECT f FROM FnSolicitudExoneracion f INNER JOIN f.solicitudExoneracionPredios sp WHERE sp.predioRural2017 = :predio AND f.exoneracionTipo = :tipo AND f.anioInicio<=:anio AND f.anioFin>=:anio";
    public static String getSolicitudExoneracionByResolucion = "SELECT f FROM FnSolicitudExoneracion f WHERE upper(f.numResolucionSac) = :numResolucion";

    public static String existeSolicitudPredio = "SELECT s FROM FnSolicitudExoneracion s LEFT JOIN s.tramite ht LEFT JOIN ht.historicoTramiteDetCollection htd WHERE htd.predio = :predio AND s.anioInicio BETWEEN :anioInicio AND :anioFin AND s.exoneracionTipo = :exoneracionTipo AND s.anioFin BETWEEN :anioInicio AND :anioFin AND s.estado.id = :estado";
    public static String getComprobante = "SELECT TO_CHAR(p.fechaPago, 'DD/MM/YYYY'), p.liquidacion.id, p.numComprobante FROM RenPago p WHERE p.liquidacion = :liquidacion AND p.estado = true GROUP BY TO_CHAR(p.fechaPago, 'DD/MM/YYYY'), p.liquidacion.id,p.numComprobante";
    public static String getCondonaciones = "Select p FROM FnCondonacionPorcentajes p WHERE p.estado";

    public static String getRenLiquidacionesListByTramite = "SELECT liquidacion FROM RenLiquidacion liquidacion WHERE liquidacion.tramite = :tramite AND liquidacion.estadoLiquidacion.id = 2";

    public static String getRenTipoValoresById = "SELECT valores FROM RenTipoValor valores WHERE valores.id = :id AND valores.estado = true";
    public static String getRenTipoValoresByPrefijo = "SELECT valores FROM RenTipoValor valores WHERE valores.prefijo = :prefijo AND valores.estado = true";
    public static String getCodigoTituloReporte = "SELECT r.codigoTituloReporte FROM RenTipoLiquidacion r where r.prefijo = :prefijo AND r.nombreTitulo = :nomTitulo";
    public static String getLastCodigoTituloReporte = "SELECT r.codigoTituloReporte FROM RenTipoLiquidacion r WHERE r.codigoTituloReporte IS NOT NULL ORDER BY r.codigoTituloReporte DESC";
    public static String getLastCodigoRubro = "SELECT r.codigoRubro FROM RenRubrosLiquidacion r WHERE r.tipoLiquidacion.id = :tipoLiq AND r.estado = true AND r.codigoRubro IS NOT NULL ORDER BY r.codigoRubro DESC";
    public static String getRubrosByTipoLiquidacionCodRubroASC = "SELECT r FROM RenRubrosLiquidacion r WHERE r.tipoLiquidacion.id = :tipoLiq AND r.estado = true AND r.codigoRubro IS NOT NULL ORDER BY r.codigoRubro ASC";
    public static String getLastNumeroSolicitudCondonacion = "SELECT r.numero FROM FnSolicitudCondonacion r WHERE r.numero IS NOT NULL ORDER BY r.numero DESC";
    public static String getCondonacionesList = "SELECT r FROM FnCondonacion r WHERE r.estado = :estado";
    public static String getCatCategoriasPredio = "SELECT r FROM CatCategoriasPredio r WHERE r.estado = :estado";
    public static String getLastMultaByTramite = "SELECT r FROM CmMultas r WHERE r.tramite = :tramite ORDER BY r.fechaIngreso DESC";
    public static String getFechasCaducidadPermisosLC = "SELECT r.localComercial FROM RenPermisosFuncionamientoLocalComercial r WHERE r.fechaCaducidad = :hoy";

    public static String getInteres = "SELECT SUM(r.porcentaje) FROM RenIntereses r WHERE r.desde>=:desde AND r.hasta<=:hasta";
    public static String getInteresNativo = "SELECT SUM(porcentaje*dias)/360 FROM sgm_financiero.ren_intereses i WHERE i.desde>=TO_DATE(?,'DD-MM-YYYY') AND i.hasta<=?";

    public static String getInteresByAnio = "SELECT i FROM RenIntereses i WHERE i.anio =:anio";
    
    public static String getRenLocalCantidadLast = "SELECT r FROM RenLocalCantidadAccesorios r WHERE r.estado = true AND r.localComercial = :local ORDER BY r.fechaIngreso DESC";

    public static String verificarPropietarioPredios = "SELECT c.ente.id FROM CatPredioPropietario c WHERE c.ente = :ente AND c.estado = :estado AND c.predio = :predio";
    public static String ultimoPagoByLiquidacion = "SELECT p FROM RenPago p WHERE p.liquidacion=:liquidacion AND p.estado=true ORDER BY p.numComprobante DESC";

    public static String getExoneracionesAutoByAnio = "SELECT p FROM FnSolicitudExoneracionAutomatica p WHERE anio = :anio";
    public static String getExoneracionesDisponibles = "SELECT p FROM FnSolicitudExoneracion p WHERE :anio BETWEEN p.anioInicio AND p.anioFin OR (p.exoneracionTipo.id = 17 OR p.exoneracionTipo.id = 44) ORDER BY p.id";

    public static String getRenTurismoServiciosByTipo = "SELECT p FROM RenTurismoServicios p WHERE p.tipo = :tipo";
    public static String getRenTipoLiquidacionesAll = "SELECT p FROM RenTipoLiquidacion p WHERE p.estado = true ORDER BY p.nombreTitulo ASC";
    public static String getRubrosLiquidacionTipoLiqCodRubro = "select e from RenRubrosLiquidacion e where e.tipoLiquidacion.id = :tipo and e.codigoRubro = :rubro and e.estado = true";
    //////////*************************************///////////////
    /**
     * ***** INICIO CONSULTAS PARA PARTE DE RECAUDACIONES ******
     */
    /**
     * SE REALIZA LA COMPARACION DE LA TABLA PARTE_RECAUDADIONES DE LA COLUMNA
     * CODIGO CON LA COLUMNA CUENTA_PRESUPUESTO DE LA TABLA
     * REN_RUBROS_LIQUIDACION
     */
    public static String getSumRubroByCtaPres = "SELECT pt, SUM(rpr.valor) FROM ParteRecaudaciones pt, RenPago p INNER JOIN p.liquidacion l "
            + "INNER JOIN p.renPagoRubros rpr INNER JOIN rpr.rubro r "
            + "WHERE CAST(p.fechaPago AS date) = :fecha AND pt.tipo = 1 AND p.estado = true "
            + "AND CASE pt.codigo WHEN '130199041000000' THEN r.ctaPresupuesto = '130199040000000' WHEN '130402100000000' THEN r.ctaPresupuesto = '130402000000000' ELSE r.ctaPresupuesto = pt.codigo END "
            + "AND CASE WHEN (pt.codigo ='110201010000000' OR pt.codigo ='110201040000000') THEN l.anio = :anio ELSE p.estado = true END "
            + "AND CASE WHEN pt.anioAnterior = true THEN l.anio < :anio ELSE p.estado = true END "
            + "GROUP BY pt.ctaTransaccion, pt.id, pt.anioAnterior, pt.codigo, pt.descripcion, pt.padre, pt.tipo, pt.orden ORDER BY pt.orden";
    /**
     * SE REALIZA LA COMPARACION DE LA TABLA PARTE_RECAUDADIONES DE LA COLUMNA
     * CTA_TRANSACCION CON LA COLUMNA CUENTA_CONTABLE DE LA TABLA
     * REN_RUBROS_LIQUIDACION
     */
    public static String getSumBomberos = "SELECT SUM(dl.valor) AS \"valorAcumulado\", pr.descripcion AS descripcion, pr.tipo, pr.orden FROM sgm_temporal.parte_recaudaciones pr "
            + "LEFT OUTER JOIN sgm_financiero.ren_tipo_liquidacion t ON t.codigo_titulo_reporte = pr.padre LEFT OUTER JOIN sgm_financiero.ren_rubros_liquidacion rl on rl.tipo_liquidacion = t.id "
            + "LEFT OUTER JOIN sgm_financiero.ren_pago_rubro dl ON rl.id = dl.rubro LEFT OUTER JOIN sgm_financiero.ren_pago p ON p.id = dl.pago "
            + "LEFT OUTER JOIN sgm_financiero.ren_liquidacion l ON l.id = P.liquidacion LEFT OUTER JOIN  sgm_app.cat_predio_rustico cpr ON cpr.id = l.predio_rustico "
            + "LEFT OUTER JOIN  sgm_app.cat_predio cp ON cp.id = l.predio LEFT OUTER JOIN  sgm_app.cat_ciudadela cc ON (cc.id = cp.CIUDADELA) "
            + "LEFT OUTER JOIN  sgm_app.emisiones_rurales_excel er ON er.id=l.rural_excel "
            + "WHERE pr.tipo = 2 AND p.estado = true AND CAST(p.fecha_pago AS date) = :fecha AND (CASE t.codigo_titulo_reporte WHEN 1 THEN rl.codigo_rubro = 4 AND (CASE WHEN l.rural_excel IS NOT NULL THEN UPPER(er.parroquia) = 'SAN VICENTE' ELSE UPPER(er.parroquia) = 'CANOA' END) WHEN 7 THEN rl.codigo_rubro = 11 ELSE rl.codigo_rubro = 1 END ) "
            + "GROUP BY pr.descripcion, pr.tipo, pr.orden;";

    public static String getSumFondAjenos = "SELECT "
            + "   CASE WHEN pr.cta_transaccion = '212030400000000' THEN "
            /*+ "    (SELECT COALESCE((SELECT SUM(PR.valor) FROM sgm_financiero.ren_pago_rubro pr INNER JOIN sgm_financiero.ren_pago pBT ON pBT.ID = PR.PAGO "
            + "        INNER JOIN sgm_financiero.REN_LIQUIDACION LBT ON LBT.ID = pBT.LIQUIDACION LEFT OUTER JOIN sgm_financiero.ren_rubros_liquidacion rlBT ON rlBT.id = PR.rubro "
            + "        LEFT OUTER JOIN sgm_financiero.ren_tipo_liquidacion tBT ON tBT.id = LBT.tipo_liquidacion INNER JOIN  sgm_app.CAT_PREDIO CPBT ON CPBT.ID = LBT.PREDIO "
            + "        WHERE CAST(pbt.fecha_pago AS date) = :fecha AND PBT.ESTADO = TRUE AND LBT.ANIO >= 2014 "
            + "        AND (TBT.CODIGO_TITULO_REPORTE = 7 AND RLBT.CODIGO_RUBRO = 11 AND CPBT.CIUDADELA = 42)), 0)) "
            + "   + (SELECT COALESCE((SELECT SUM(PR.valor) FROM sgm_financiero.ren_pago_rubro pr LEFT OUTER JOIN sgm_financiero.ren_pago pB ON pB.ID = PR.PAGO "
            + "        INNER JOIN sgm_financiero.REN_LIQUIDACION LB ON LB.ID = PB.LIQUIDACION LEFT OUTER JOIN sgm_financiero.ren_tipo_liquidacion tB ON tB.id = LB.tipo_liquidacion "
            + "        INNER JOIN  sgm_app.CAT_PREDIO CPB ON CPB.ID = LB.PREDIO WHERE CAST(pb.fecha_pago AS date) = :fecha AND PB.ESTADO = TRUE AND LB.ANIO >= 2014 "
            + "        AND TB.CODIGO_TITULO_REPORTE = 50 AND CPB.CIUDADELA = 42 ), 0)) "
            + "    + (SELECT COALESCE((SELECT SUM(pr.valor) FROM sgm_financiero.ren_pago_rubro pr LEFT OUTER JOIN sgm_financiero.ren_rubros_liquidacion rlB ON rlB.id = PR.rubro "
            + "        LEFT OUTER JOIN  sgm_financiero.ren_tipo_liquidacion tB ON tB.id = rlB.tipo_liquidacion LEFT OUTER JOIN  sgm_financiero.ren_pago pB ON pB.ID = PR.PAGO "
            + "        INNER JOIN  sgm_financiero.REN_LIQUIDACION LB ON LB.ID = PB.LIQUIDACION INNER JOIN  sgm_app.CAT_PREDIO_RUSTICO CPRB ON CPRB.ID = LB.PREDIO_RUSTICO "
            + "        WHERE PB.ESTADO = TRUE AND CAST(pb.fecha_pago AS date) = :fecha AND (TB.CODIGO_TITULO_REPORTE = 1 AND RLB.CODIGO_RUBRO = 4 "
            + "        AND CPRB.PARROQUIA = 3) OR (TB.CODIGO_TITULO_REPORTE = 50 AND RLB.CODIGO_RUBRO = 4)), 0)) "*/
            + "     (SELECT SUM(PR.VALOR) FROM  sgm_financiero.REN_PAGO P INNER JOIN  sgm_financiero.REN_LIQUIDACION L ON L.ID = P.LIQUIDACION "
            + "INNER JOIN  sgm_financiero.REN_PAGO_RUBRO PR ON PR.PAGO = P.ID LEFT OUTER JOIN  sgm_app.CAT_PREDIO CP ON CP.ID = L.PREDIO "
            + "LEFT OUTER JOIN  sgm_app.CAT_CIUDADELA C ON C.ID = CP.CIUDADELA LEFT OUTER JOIN  sgm_app.CAT_PREDIO_RUSTICO CPR ON CPR.ID = L.PREDIO_RUSTICO "
            + "LEFT OUTER JOIN  sgm_app.emisiones_rurales_excel er ON er.id=l.rural_excel "
            + "WHERE CAST(P.FECHA_PAGO AS DATE) = :fecha AND L.TIPO_LIQUIDACION IN (13, 7) AND PR.RUBRO IN (12, 21) AND (CASE WHEN L.TIPO_LIQUIDACION = 13 THEN L.ANIO >= 2004 ELSE TRUE END) "
            + "AND (CASE PR.RUBRO WHEN 21 THEN (CASE WHEN L.RURAL_EXCEL IS NOT NULL THEN UPPER(er.parroquia) = 'TARIFA' ELSE CPR.PARROQUIA = 3 END) ELSE C.COD_PARROQUIA = 3 END) AND P.ESTADO=TRUE)"
            + "   ELSE "
            + "     SUM(rpr.valor) END AS \"valorAcumulado\", "
            + "   pr.descripcion AS descripcion, pr.tipo, pr.orden "
            + "FROM sgm_temporal.parte_recaudaciones pr LEFT OUTER JOIN  sgm_financiero.ren_rubros_liquidacion rl on rl.cuenta_contable = pr.cta_transaccion "
            + "LEFT OUTER JOIN sgm_financiero.ren_tipo_liquidacion t ON t.id = rl.tipo_liquidacion LEFT OUTER JOIN sgm_financiero.ren_pago_rubro rpr ON rpr.rubro = rl.id "
            + "LEFT OUTER JOIN sgm_financiero.ren_pago p ON p.id = rpr.pago "
            + "WHERE pr.tipo = 3 AND p.estado = true AND CAST(p.fecha_pago AS date) = :fecha "
            + "GROUP BY pr.descripcion, pr.cta_transaccion, pr.orden, pr.tipo ORDER BY pr.orden ASC";

    public static String getDescuento = "SELECT SUM(p.descuento) FROM RenPago p INNER JOIN p.liquidacion l INNER JOIN l.tipoLiquidacion t "
            + "WHERE t.codigoTituloReporte IN (7, 1) AND CAST(p.fechaPago AS date) = :fecha AND p.estado = true";

    public static String getMoraMulta = "SELECT COALESCE(SUM(p.valor), 0) FROM RenPago p INNER JOIN p.liquidacion l INNER JOIN l.tipoLiquidacion t "
            + "WHERE CAST(p.fechaPago AS date) = :fecha AND p.estado = true AND (t.codigoTituloReporte = 114 OR t.codigoTituloReporte = 107)";
    public static String getInteresRecargo = "SELECT  COALESCE(SUM(p.interes), 0), COALESCE(SUM(p.recargo), 0) FROM RenPago p INNER JOIN p.liquidacion l INNER JOIN l.tipoLiquidacion t WHERE CAST(p.fechaPago AS DATE) = :fecha AND p.estado = true AND t.codigoTituloReporte <> 50";

    public static String getPredioUrbAnioAnt = "SELECT COALESCE(SUM(rp.valor), 0) FROM RenPago p INNER JOIN p.renPagoRubros rp INNER JOIN rp.rubro rl INNER JOIN p.liquidacion l INNER JOIN l.tipoLiquidacion tl "
            + "WHERE l.anio < :anio AND p.estado = true AND rl.rubroDelMunicipio = true "
            + "AND rl.codigoRubro <> 6 AND rl.codigoRubro <> 10 AND rl.codigoRubro <> 15 AND tl.codigoTituloReporte = 7 "
            + "AND CAST(p.fechaPago AS date) = :fecha ";
    public static String getPredioRusAnioAnt = "SELECT COALESCE(SUM(rp.valor), 0) FROM RenPago p INNER JOIN p.renPagoRubros rp INNER JOIN rp.rubro rl INNER JOIN p.liquidacion l INNER JOIN l.tipoLiquidacion tl "
            + " WHERE l.anio < :anio AND p.estado = true AND rl.rubroDelMunicipio = true  "
            + "AND (rl.codigoRubro = 1 OR rl.codigoRubro = 3) AND tl.codigoTituloReporte = 1 "
            + "AND CAST(p.fechaPago AS date) = :fecha ";

    public static String getRenPagoByNumComprobante = "select e from RenPago e where e.numComprobante = :numComprobante and e.estado = true";
    public static String getRenSecuenciaMaxNumComprobante = "select MAX(e.numComprobante) from RenSecuenciaNumComprobante e where e.anio =:anio";
    public static String existeLiquidacion = "SELECT l.estado_liquidacion FROM sgm_financiero.ren_liquidacion l WHERE l.tipo_liquidacion = ? AND l.anio  = ? AND l.local_comercial = ?";

    public static String buscarExoneracionTerceraEdadYDiscapacitadoPorPredio = "SELECT l FROM FnSolicitudExoneracion l WHERE l.predio = :predio AND l.exoneracionTipo.id IN (17, 18, 37, 44) AND l.estado.id IN(1, 2) ORDER BY l.anioInicio DESC";

    public static String buscarExoneracionPorPredio = "SELECT l FROM FnSolicitudExoneracion l WHERE l.predio = :predio  AND l.estado.id IN(1, 2) ORDER BY l.anioInicio DESC";
    
    public static String prediosUrbanosEnLiquidacionPorNombreContribuyente = "SELECT DISTINCT l.predio FROM RenLiquidacion l WHERE l.tipoLiquidacion.id=13 AND l.nombreComprador ILIKE :nombreComprador";
    public static String prediosRuralesEnLiquidacionPorNombreContribuyente = "SELECT DISTINCT l.predioRustico FROM RenLiquidacion l WHERE l.tipoLiquidacion.id=7 AND l.nombreComprador ILIKE :nombreComprador";

    /**
     * ***** INICIO CONSULTAS PARA PARTE DE RECAUDACIONES ******
     */
    /////////*************************************////////////////
    //BANCOS
    public static String getDiccionarioxFormatoAttrib = "select e from BancaDiccionario e where e.formato.id = :formato and atributo = :atributo and estado = true";
    public static String getFormatoBancaxPrefijo = "select e from FormatoBanca e where e.prefijo = :prefijo and e.estado = true";

    public static String getClaseExoneracionResoluciones = "SELECT ce FROM FnExoneracionClase ce WHERE ce.id IN (3,8,9) ORDER BY ce.descripcion";
    public static String getClaseExoneracionCatastro = "SELECT ce FROM FnExoneracionClase ce WHERE ce.id IN (9)";

    public static String getClaseExoneracionByState = "SELECT ce FROM FnExoneracionClase ce WHERE ce.estado=true ORDER BY orden ASC";
    public static String getTipoExoneracionTipoByClaseAndState = "SELECT ce FROM FnExoneracionTipo ce WHERE ce.estado = true AND ce.exoneracionClase = :clase ORDER BY orden ASC";
    public static String verificarPagoBanco = "SELECT cb FROM ConsolidacionBanco cb WHERE cb.numPredio = :numPredio AND cb.estado IN ('P','N') AND cb.anio BETWEEN :anio AND :anioFin";
    public static String obtenerLiquidacionesPrediales = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.predio = :predio AND r.estadoLiquidacion IN (1, 2, 7) ORDER BY r.anio ASC";
    public static String obtenerLiquidacionesPredialesPendientes = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.predio = :predio AND r.estadoLiquidacion IN (2) ORDER BY r.anio ASC";
    //public static String obtenerLiquidacionesPrediales = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.predio = :predio AND r.estadoLiquidacion IN (1, 2, 7) ORDER BY r.anio ASC";
    public static String obtenerLiquidacionesPredialesJS = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.predio = :predio AND r.estadoLiquidacion IN (1, 2, 4) ORDER BY r.anio ASC";
    public static String getRenLiquidacionesByTipoLiquidacionYEstadoOrderDate = "SELECT r FROM RenLiquidacion r WHERE r.predio = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion and r.estadoLiquidacion.id = :estadoLiquidacion ORDER BY r.fechaIngreso DESC";
    public static String obtenerLiquidacionesPredialesRusticos = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.predioRustico = :predioRustico AND r.estadoLiquidacion IN (1, 2) ORDER BY r.anio ASC";
    public static String obtenerLiquidacionesPredialesRusticosPendientes = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.predioRustico = :predioRustico AND r.estadoLiquidacion IN (2) ORDER BY r.anio ASC";

    public static String getRenRangoValor2016ByValue = "SELECT r FROM RenRangosValores r WHERE r.tipoRango.id = :tipo AND :valor BETWEEN r.valorInicio AND r.valorFin";

    public static String pagosByLiquidacion = "SELECT COUNT(p) FROM RenPago p WHERE p.liquidacion=:liquidacion AND p.estado=TRUE";
    public static String getDetalleDeLiquidacion = "SELECT r FROM RenDetLiquidacion r WHERE r.liquidacion = :liquidacion ORDER BY r.rubro ASC";
    public static String getUbicacionByDetLiq = "SELECT r FROM MejDetRubroMejoras r WHERE r.rubroMejora = :detLiq AND r.estado = true";
    public static String getDetalleDeLiquidacionxRubro = "SELECT r FROM RenDetLiquidacion r WHERE r.liquidacion = :liquidacion and r.rubro = :rubro ORDER BY r.rubro ASC";

    /*QUERY DEL REPORTE DE BANCO*/
    public static String reporteBancoBolivariano = " select e from ConsolidacionBanco e where e.fechaCobro between :inicio and :fin";

    public static String getExoneracionLiquidacion = "SELECT ce FROM FnExoneracionLiquidacion ce WHERE ce.liquidacionOriginal = :liquidacion";

    public static String getLiquidacionPendientexAnioPredioTipo = "select e from RenLiquidacion e where e.anio = :anio and e.tipoLiquidacion.id = :tipo and e.predio.numPredio = :predio and e.estadoLiquidacion.id = (2)";
    public static String getLiquidacionPagadasxAnioPredioTipo = "select e from RenLiquidacion e where e.anio = :anio and e.tipoLiquidacion.id = :tipo and e.predio.numPredio = :predio and e.estadoLiquidacion.id = (1)";
    public static String getLiquidacionByPredio = "select e from RenLiquidacion e where e.anio = :anio and e.tipoLiquidacion.id = :tipo and e.predio.numPredio = :predio and e.estadoLiquidacion.id in (2, 1)";
    public static String getLiquidacionxAnioPredioTipo = "select e from RenLiquidacion e where e.anio = :anio and e.tipoLiquidacion.id = :tipo and e.predio.numPredio = :predio and e.estadoLiquidacion.id in (1,2)";
    public static String getRenLiquidacionesRuralByTipoLiquidacionYEstado = "SELECT r FROM RenLiquidacion r WHERE r.predioRustico = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion and r.estadoLiquidacion.id = :estadoLiquidacion";
    public static String getRenLiquidacionesRural2017ByTipoLiquidacionYEstado = "SELECT r FROM RenLiquidacion r WHERE r.ruralExcel = :predio AND r.anio = :anio AND r.tipoLiquidacion = :idTipoLiquidacion and r.estadoLiquidacion.id = :estadoLiquidacion";

    public static String obtenerLiquidacionesPredialesRusticosExcel2017 = "SELECT r FROM RenLiquidacion r WHERE r.tipoLiquidacion = :tipoLiquidacion AND r.ruralExcel = :ruralExcel AND r.estadoLiquidacion IN (1, 2) ORDER BY r.anio ASC";
    
    
    public static String pagosByPredioTipoLiquidacionAnio =             "SELECT p FROM RenPago p WHERE p.estado=TRUE AND (p.liquidacion.anio BETWEEN :desde AND :hasta) AND p.liquidacion.tipoLiquidacion=:tipoLiquidacion AND p.liquidacion.predio=:predio AND p.liquidacion.estadoLiquidacion.id IN (1,2)";    
    public static String pagosByPredioUrbanoTipoLiquidacionAnio =       "SELECT p FROM RenPago p WHERE p.estado=TRUE AND (p.liquidacion.anio BETWEEN :desde AND :hasta) AND p.liquidacion.tipoLiquidacion=:tipoLiquidacion AND p.liquidacion.predioRustico=:predioRural AND p.liquidacion.estadoLiquidacion.id IN (1,2)";    
    public static String pagosByPredioUrbano2017TipoLiquidacionAnio =   "SELECT p FROM RenPago p WHERE p.estado=TRUE AND (p.liquidacion.anio BETWEEN :desde AND :hasta) AND p.liquidacion.tipoLiquidacion=:tipoLiquidacion AND p.liquidacion.ruralExcel=:predioSig AND p.liquidacion.estadoLiquidacion.id IN (1,2)";

    public static String pagosByPredioTipoLiquidacionAnioPagado =             "SELECT p FROM RenPago p WHERE p.estado=TRUE AND (p.liquidacion.anio BETWEEN :desde AND :hasta) AND p.liquidacion.tipoLiquidacion=:tipoLiquidacion AND p.liquidacion.predio=:predio AND p.liquidacion.estadoLiquidacion.id IN (1)"; 
    
    public static String getCarteraVencidaAME = "SELECT p FROM CarteraVencida p WHERE p.preCodigocatastral = :claveCatastralAnterior AND p.carveEstado IN ('E', 'C')"; 
    public static String getTitulosPrediosAME = "SELECT p FROM TitulosPredio p WHERE p.preCodigocatastral = :claveCatastralAnterior AND p.titprEstado IN ('E', 'C')"; 
    
    public static String getRenLiquidacionIdTitulo = "SELECT p FROM RenLiquidacion p WHERE p.idLiquidacion = :numLiquidacion"; 
    
    public static String getRenLiquidacionByPredio = "SELECT r FROM RenLiquidacion r WHERE r.predio = :predio"; 
    
    public static String getRenLiquidacionByClaveAME = "SELECT r FROM RenLiquidacion r WHERE r.claveAME = :claveAnterior ORDER BY r.anio ASC"; 
    
    public static String getAllEmisionesFox = "SELECT e FROM EmisionesRuralesExcel e WHERE e.codigoCatastral = :codigoCatastralFox";
    
    public static String getLiquidacionFnRemisionLiquidacion = "SELECT f FROM FnRemisionLiquidacion f WHERE f.liquidacion.id = :liquidacion and f.estado = true and f.exoneracion.estado.id = 1 ORDER BY f.fechaAplicacion DESC";
    
    /*CONVENIOS DE PAGO*/
    public static String getCuotasByConvenioAndEstadoLiquidacion = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.convenio.id = :convenio AND f.liquidacion.estadoLiquidacion.id = :estado ORDER BY f.mes";
    public static String getLiqInicialByConvenioAndTipoLiquidacion = "SELECT l FROM RenLiquidacion l WHERE l.convenioPago.id = :convenio AND l.tipoLiquidacion.id = :tipo AND l.estadoLiquidacion.id IN (1,2)";
    public static String getLiqInicialByConvenioAndTipoLiquidacionJC = "SELECT l FROM RenLiquidacion l WHERE l.convenioPago.id = :convenio AND l.tipoLiquidacion.id NOT IN( 260, 261 )";
    public static String getRenLiquidacionesByConvenioPagoAndEstado = "SELECT liquidacion FROM RenLiquidacion liquidacion WHERE liquidacion.convenioPago.id = :convenio AND liquidacion.estadoLiquidacion.id = :estadoLiquidacion";
    public static String getRenLiquidacionesByConvenioPago = "SELECT liquidacion FROM RenLiquidacion liquidacion WHERE liquidacion.convenioPago.id = :convenio AND liquidacion.estadoLiquidacion.id <> :estadoLiquidacion";
    
    public static String getPagoInicialConvenio = "select e from RenPago e where e.liquidacion.convenioPago.id = :convenioPago AND e.liquidacion.tipoLiquidacion = 260";

    public static String getLiquidacionesCoactivaCuotasByConvenioPago = "select e from RenLiquidacion e where  e.tipoLiquidacion.id = 281 and e.convenioPago.id = :convenioPago ";

    public static String getLiquidacionesPredioCuotasCoactiva = "Select r from RenLiquidacion r where r.predio = :predio and r.tipoLiquidacion = :tipoLiq "
            + "and r.estadoLiquidacion.id in (1, 9) order by r.fechaIngreso";

    public static String getLiquidacionesAguaCuotasCoactiva = "Select r from RenLiquidacion r where r.cuenta = :cuentaAgua and r.tipoLiquidacion = :tipoLiq "
            + "and r.estadoLiquidacion.id in (1, 9) order by r.fechaIngreso";

    public static String getLiqInicialByConvenioAndTipoLiquidacionGenereda = "SELECT COUNT(l.id) FROM RenLiquidacion l WHERE l.convenioPago.id = :convenio AND l.tipoLiquidacion.id = :tipo AND l.estadoLiquidacion.id IN (1,2)";
    
    public static String getRenTipoLiquidacionByCodigoReporte = "SELECT r FROM RenTipoLiquidacion r WHERE r.estado = true AND r.codigoTituloReporte = :tituloReporte ORDER BY r.nombreTransaccion ASC";
    
    public static String getRenLiquidacionComprobanteCuentaPagoInicial = "SELECT l.numComprobante FROM RenLiquidacion  l WHERE l.convenioPago.id  = :convenio  AND l.tipoLiquidacion.id = 260 ";
    public static String getConvenioByPredios = "SELECT  e from FnConvenioPago e where e.predio.id = :predio";
    
}
