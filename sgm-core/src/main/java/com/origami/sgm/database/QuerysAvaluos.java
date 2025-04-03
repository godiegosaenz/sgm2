/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.database;

import java.math.BigDecimal;

/**
 *
 * @author CarlosLoorVargas
 */
public class QuerysAvaluos {

    public static String getCoeficientes = "select e from RangoValorCoeficiente e where e.estado = true and :rango >= e.minVal and :rango <= e.maxVal and e.prefijo = :prefijo";

    //public static String getAreaTipo = "SELECT (SUM(P.AREA_SOLAR)/COUNT(P.NUM_PREDIO)) AS AREA_TIPO FROM  sgm_app.CAT_PREDIO P WHERE P.PHV = 0 AND P.PHH = 0 AND ESTADO = 'A' AND P.CLAVE_CAT = ? ";
    public static String getAreaTipo = "SELECT P.AREA_SOLAR AS AREA_TIPO FROM  sgm_app.CAT_PREDIO P WHERE P.PHV = 0 AND P.PHH = 0 AND ESTADO = 'A' AND P.CLAVE_CAT = ? ";
    //public static String getTotalFrenteTipo = "SELECT (SUM(S4.FRENTE1)/COUNT(S4.PREDIO)) AS FRENTE_TIPO FROM  sgm_app.CAT_PREDIO P INNER JOIN  sgm_app.CAT_PREDIO_S4 S4 ON S4.PREDIO = P.ID WHERE P.PHV = 0 AND P.PHH = 0 AND ESTADO = 'A' AND P.CLAVE_CAT = ?";
    public static String getTotalFrenteTipo = "SELECT S4.FRENTE1 AS FRENTE_TIPO FROM  sgm_app.CAT_PREDIO P INNER JOIN  sgm_app.CAT_PREDIO_S4 S4 ON S4.PREDIO = P.ID WHERE P.PHV = 0 AND P.PHH = 0 AND ESTADO = 'A' AND P.CLAVE_CAT = ?";

    public static String getSectorValorizacion = "select e from SectorValorizacion e where e.sector = :sector and e.estado = true";

    public static String getPrediosBase = "SELECT PX.ID, PX.NUM_PREDIO AS NUMPREDIO,PX.CLAVE_CAT AS CLAVECAT, PX.AREA_SOLAR AS AREASOLAR, S4.FRENTE1, "
            + "(SVL.SERVICIOS) AS SERVICIOS, "
            + "PX.SUBSECTOR, SVL.VALOR_M2 AS VALORM2, "
            + "(SELECT SUM(PE.AREA_CONS_CENSO) "
            + "FROM  sgm_app.CAT_PREDIO_EDIFICACION PE "
            + "INNER JOIN  sgm_app.CAT_PREDIO CP ON CP.ID = PE.PREDIO "
            + "INNER JOIN  sgm_app.VALOR_M2_PROTOTIPO PRT ON PRT.PROTOTIPO = PE.PROTOTIPO "
            + "WHERE CP.NUM_PREDIO = PX.NUM_PREDIO AND PE.AREA_CONS_CENSO >0) AS AREACONSTRUCCION, "
            + "(SELECT SUM(PE.AREA_CONS_CENSO * PRT.VALOR * PE.FACTOR_DEPRECIACION) AS AVALUOEDIFICACION "
            + "FROM  sgm_app.CAT_PREDIO_EDIFICACION PE "
            + "INNER JOIN  sgm_app.CAT_PREDIO CP ON CP.ID = PE.PREDIO "
            + "INNER JOIN  sgm_app.VALOR_M2_PROTOTIPO PRT ON PRT.PROTOTIPO = PE.PROTOTIPO "
            + "WHERE CP.NUM_PREDIO = PX.NUM_PREDIO AND PE.AREA_CONS_CENSO >0) AS AVALUOEDIFICACION "
            + "FROM  sgm_app.CAT_PREDIO PX "
            + "INNER JOIN  sgm_app.CAT_PREDIO_S4 S4 ON S4.PREDIO = PX.ID "
            + "INNER JOIN  sgm_app.SECTOR_VALORIZACION SVL ON SVL.SECTOR = PX.SUBSECTOR "
            + "WHERE PX.SUBSECTOR IS NOT NULL "
            + "AND PX.PHV = 0 AND PX.PHH = 0 "//35949
            + "AND PX.ESTADO = 'A' ORDER BY PX.NUM_PREDIO ASC";

    public static String getPredioBase = "SELECT PX.ID, PX.NUM_PREDIO AS NUMPREDIO,PX.CLAVE_CAT AS CLAVECAT, PX.AREA_SOLAR AS AREASOLAR, S4.FRENTE1, "
            + "(SVL.SERVICIOS) AS SERVICIOS, "
            + "PX.SUBSECTOR, SVL.VALOR_M2 AS VALORM2, "
            + "(SELECT SUM(PE.AREA_CONS_CENSO) "
            + "FROM  sgm_app.CAT_PREDIO_EDIFICACION PE "
            + "INNER JOIN  sgm_app.CAT_PREDIO CP ON CP.ID = PE.PREDIO "
            + "INNER JOIN  sgm_app.VALOR_M2_PROTOTIPO PRT ON PRT.PROTOTIPO = PE.PROTOTIPO "
            + "WHERE CP.NUM_PREDIO = PX.NUM_PREDIO AND PE.AREA_CONS_CENSO >0) AS AREACONSTRUCCION, "
            + "(SELECT SUM(PE.AREA_CONS_CENSO * PRT.VALOR * PE.FACTOR_DEPRECIACION) AS AVALUOEDIFICACION "
            + "FROM  sgm_app.CAT_PREDIO_EDIFICACION PE "
            + "INNER JOIN  sgm_app.CAT_PREDIO CP ON CP.ID = PE.PREDIO "
            + "INNER JOIN  sgm_app.VALOR_M2_PROTOTIPO PRT ON PRT.PROTOTIPO = PE.PROTOTIPO "
            + "WHERE CP.NUM_PREDIO = PX.NUM_PREDIO AND PE.AREA_CONS_CENSO >0) AS AVALUOEDIFICACION "
            + "FROM  sgm_app.CAT_PREDIO PX "
            + "INNER JOIN  sgm_app.CAT_PREDIO_S4 S4 ON S4.PREDIO = PX.ID "
            + "INNER JOIN  sgm_app.SECTOR_VALORIZACION SVL ON SVL.SECTOR = PX.SUBSECTOR "
            + "WHERE PX.SUBSECTOR IS NOT NULL "
            + "AND PX.BLOQUE = 0 AND PX.PISO = 0 AND PX.UNIDAD = 0 "
            + "AND PX.NUM_PREDIO = ? "
            + "AND PX.ESTADO = 'A' ORDER BY PX.NUM_PREDIO ASC";

    public static String getPrediosAlicuotas = "select e from CatPredio e where e.phv > 0 and e.phh > 0 and e.estado = 'A'";

    public static String getEnlacePh = "SELECT CODIGO_MATRIZ FROM  sgm_app.ENLACE_PH EPH WHERE EPH.NUM_PREDIO = ?";

    public static String getPredioByNumPredio = "select e from CatPredio e where e.numPredio = :numPredio and e.estado = 'A'";
    public static String getPredioByNumPredioBase = "select e from CatPredio e where e.numPredio = :numPredio and e.estado = 'A' and e.phv = 0 and e.phh = 0";

    public static String getAvaluoPredial = "select e from ValoracionPredial e where e.numVersion = :version and e.periodo = :periodo and e.numPredio = :numPredio";
    public static String getAvaluoPredialGral = "select e from ValoracionPredial e where e.numVersion = :version and e.periodo = :periodo and e.matriz = false order by e.numPredio";

    ///PRUEBA VALORACION
    public static String getAvaluoPredialGral_TEST = "select e from ValoracionPredial e where e.numVersion = :version and e.periodo = :periodo and e.matriz = false and e.sneAct > 0  and e.avaluoEdificacion = 0";

    public static String getPredioCodCatastral = "select e from CatPredio e where e.sector = :sector and e.mz = :mz and e.cdla = :cdla and e.mzdiv = :mzdiv and "
            + "e.solar = :solar and e.div1 = :d1 and e.div2 = :d2 and e.div3 = :d3 and e.div4 = :d4 and e.div5 = :d5 and e.div6 = :d6 and e.div7 = :d7 and e.div8 = :d8 "
            + "and e.div9 = :d9 and e.phv = 0 and e.phh = 0 and e.estado = 'A'";

    public static String getPrediosMatrices = "SELECT E.NUM_PREDIO, EPH.CODIGO_MATRIZ FROM  sgm_app.CAT_PREDIO E "
            + "INNER JOIN  sgm_app.ENLACE_PH EPH ON EPH.NUM_PREDIO = E.NUM_PREDIO WHERE E.PHV > 0 AND E.PHH > 0";

    public static String getCoeficientePredialAnt(BigDecimal val) {
        return "Select  sgm_app.coeficiente_predial2(" + val + ")";
    }

    public static String getLiquidacionesEmitidas = "select e from RenLiquidacion e where e.tipoLiquidacion = 13 and e.estadoLiquidacion = 2 and e.predio = :predio and e.anio = 2017";

    public static String getLiquidacionesNuevas = "select e from ValoracionPredial e where e.periodo = :periodo and e.numVersion = :version and e.matriz = false and e.idPredio not in "
            + "(select x.predio.id from RenLiquidacion x where x.tipoLiquidacion.id = 13 and x.estadoLiquidacion.id = 2 and x.totalPago = x.saldo and x.anio = 2017) order by e.numPredio asc";

    public static String getLiquidacionesxRecalcular = "SELECT T.NUM_PREDIO, C.ID AS LIQUIDACION, T.IP_ANT, T.IP_ACT, T.TASA_MANT  "
            + "FROM sgm_historico.VALORACION_PREDIAL T  "
            + "INNER JOIN sgm_financiero.EN_LIQUIDACION C ON C.PREDIO = T.ID_PREDIO "
            + "WHERE T.NUM_VERSION = 45 "
            + "AND C.TIPO_LIQUIDACION = 13 "
            + "AND C.ESTADO_LIQUIDACION = 2 "
            + "AND T.IP_ANT = 0 "
            + "AND T.BANDA_IMPOSITIVA < 1 "
            + "AND C.ANIO = 2017 "
            + "AND C.TOTAL_PAGO = C.SALDO "
            + "AND C.EXONERADO = FALSE "
            + "ORDER BY T.NUM_PREDIO ASC";

    public static String getLiquidacionesPagadas = "SELECT CP.ID AS idpredio, CP.NUM_PREDIO as numpredio, T.ID AS LIQUIDACION, T.ANIO AS PERIODO, T.AREA_TOTAL areatotal, T.AVALUO_SOLAR avaluosolar, T.AVALUO_CONSTRUCCION avaluoconstruccion, T.AVALUO_MUNICIPAL avaluomunicipal, "
            + "FINANCIERO.GET_VALOR_RUBRO(T.ID,2) AS IPLIQ, "
            + "RP.DESCUENTO AS descuentoliq, "
            + "FINANCIERO.GET_VALOR_RUBRO(T.ID,7) AS MEJORASLIQ, "
            + "FINANCIERO.GET_VALOR_RUBRO(T.ID,10) AS SOLNEDIFLIQ, "
            + "FINANCIERO.GET_VALOR_RUBRO(T.ID,11) AS EMISIONLIQ, "
            + "FINANCIERO.GET_VALOR_RUBRO(T.ID,12) AS BOMBEROSLIQ, "
            + "FINANCIERO.GET_VALOR_RUBRO(T.ID,636) AS TASAMANTLIQ "
            + "FROM sgm_financiero.EN_LIQUIDACION T "
            + "INNER JOIN  sgm_app.CAT_PREDIO CP ON CP.ID = T.PREDIO "
            + "LEFT JOIN sgm_financiero.EN_PAGO RP ON RP.LIQUIDACION = T.ID "
            + "WHERE T.TIPO_LIQUIDACION = 13 "
            + "AND T.ESTADO_LIQUIDACION in (1,2) "
            + "AND T.ANIO = 2017 "
            + "AND T.ID = :liquidacion "
            + "ORDER BY CP.NUM_PREDIO ASC ";

    public static String getPredioByNumPredio_Version = "select e from ValoracionPredial e where e.numPredio = :numPredio and e.numVersion = :numVersion";
}
