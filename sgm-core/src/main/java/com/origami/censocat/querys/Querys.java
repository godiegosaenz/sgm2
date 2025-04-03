/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.querys;

import java.io.Serializable;

/**
 *
 * @author CarlosLoorVargas
 */
public class Querys implements Serializable{

    //SGM
    public static String getPredios = "select cp.id,cp.sector,cp.mz,cp.mzdiv,cp.solar,cp.div1,cp.div2,cp.div3,cp.div4,cp.div5,cp.div6,cp.div7,cp.div8,cp.div9,"
            + "cp.phv,cp.phh,cp.nombre_urb,cp.num_predio,cp.soporta_hipoteca,cp.urb_mz,cp.inst_creacion,cp.cdla,urb_solarnew,cp.urb_secnew,cp.calle,"
            + "cp.num_departamento,cp.nombre_edificio,cp.propiedad_horizontal,cp.estado,cp.avaluo_solar,cp.avaluo_construccion,cp.avaluo_municipal,"
            + "cp.uso_ph,cp.division_urb,cp.num_pisos, cc.nombre as ciudadela, cc.id as idCiudadela "
            + "from  sgm_app.cat_predio cp "
            + " inner join  sgm_app.cat_ciudadela cc on cc.id = cp.ciudadela "
            + " where cp.estado = 'A' "
            + " order by cp.num_predio asc";
    public static String getOrdenes = "select e from OrdenTrabajo e where e.responsable=:responsable AND e.estadoOt = :estadoOt";
    public static String getOrdenesMayorFecha = "SELECT e FROM OrdenTrabajo e WHERE e.responsable=:responsable AND e.estadoOt = :estadoOt AND e.fecCre > :fecCre";


    public static String getPrediosRest(String cond) {
        if (cond == null) {
            return getPredios;
        } else {
            return "select cp.id,cp.sector,cp.mz,cp.mzdiv,cp.solar,cp.div1,cp.div2,cp.div3,cp.div4,cp.div5,cp.div6,cp.div7,cp.div8,cp.div9,"
                    + "cp.phv,cp.phh,cp.nombre_urb,cp.num_predio,cp.soporta_hipoteca,cp.urb_mz,cp.inst_creacion,cp.cdla,urb_solarnew,cp.urb_secnew,cp.calle,"
                    + "cp.num_departamento,cp.nombre_edificio,cp.propiedad_horizontal,cp.estado,cp.avaluo_solar,cp.avaluo_construccion,cp.avaluo_municipal,"
                    + "cp.uso_ph,cp.division_urb,cp.num_pisos, cc.nombre as ciudadela, cc.id as idCiudadela "
                    + "from  sgm_app.cat_predio cp "
                    + " inner join  sgm_app.cat_ciudadela cc on cc.id = cp.ciudadela "
                    + "where " + cond + " and cp.estado = 'A' order by cp.sector,cp.mz,cp.mzdiv,cp.solar,cp.div1,cp.div2,cp.div3,cp.div4,cp.div5,cp.div6,cp.div7,cp.div8,cp.div9,"
                    + "cp.phv,cp.phh asc";
        }
    }

    public static String getCountPredios = "select count(*) from  sgm_app.cat_predio";

    public static String getCiudadelas = "select nombre from  sgm_app.cat_ciudadela cc where cc.estado = true order by nombre asc";
    public static String getPropietarios = "select p.id, p.tipo,e.id as ente, e.ci_ruc,e.es_persona,e.nombres,e.apellidos,e.razon_social "
            + "from  sgm_app.cat_predio_propietario p inner join  sgm_app.cat_ente e on e.id = p.ente where p.predio = ?";
    public static String getEdificaciones = "select p.id, p.no_edificacion, p.num_pisos, p.area_cons_censo, p.area_cons_permiso, p.area_cons_losa, "
            + "p.anio_cons, p.esta_rentado, p.num_pisosnew, p.vidautil, p.estado from  sgm_app.cat_predio_edificacion p where p.predio = ?";

    public static String getOrdenes(String cond) {
        return "select e from OrdenTrabajo e where " + cond;
    }

    public static String getDetOrdenes = "select e from OrdenDet e where e.orden.numOrden = :numOrden";
    public static String getDetOrden = "select e from OrdenDet e where e.orden.numOrden = :numOrden and e.numPredio = :numPredio";
    public static String getDetOrdenUUID = "select e from OrdenDet e where e.orden.numOrden = :numOrden and e.uuid = :uuid";

    public static String getPredio = "select e from CatPredio e where e.numPredio = :numPredio";

    public static String getEscrituraActual = "select e from  sgm_app.reg_ficha e where e.";

    public static String getDetOrdenes(String arg, String valor) {
        return "select e from OrdenDet e where e." + arg + " = " + valor + " and e.estadoDet = 'P' order by e.orden.numOrden asc";
    }
    
    public static String getOrdenPredio = "select e from OrdenDet e where e.predio= :idPredio and e.estadoDet = 'P'";

    public static String getUsuario = "select e from AclUser e where e.usuario = :user and e.pass = :pass";

    public static String getContacto = "select e from CatEnte e where e.ciRuc = :identificacion";

    public static String getPropietario = "select e from CatPredioPropietario e where e.ente.ciRuc = :identificacion and e.predio.numPredio = :numPredio";

    public static String getCatalogoItem = "select e from CtlgItem e where e.catalogo.nombre = :nombre order by e.valor asc"; 

    public static String getEdifPred_Num = "select e from CatPredioEdificacion e where e.predio.id = :predio and e.numEdif = :edif";

    public static String getCatalogo = "select e from CtlgItem e where e.catalogo.nombre = :nombre and e.referencia = :referencia";

    public static String getCiudadelasOrd = "select e from CatCiudadela e where e.estado = true order by e.nombre asc";

    public static String getCatVias = "SELECT v1.nombre FROM CenmobSubidaVia v1 where v1.sector = :sector and v1.manzana = :manzana and v1.codUrbanizacion = :codigo";

    //RESUMEN GENERAL ORDENES
    public static String getResumenGralOts = "SELECT O.NUM_ORDEN AS ORDEN, U.USUARIO AS RESPONSABLE, COUNT((SELECT DT.ID FROM FLOW.ORDEN_DET DT WHERE DT.ID = D.ID AND DT.PREDIO IS NOT NULL)) AS FINALIZADAS, "
            + "COUNT((SELECT DT.ID FROM FLOW.ORDEN_DET DT WHERE DT.ID = D.ID AND DT.PREDIO IS NULL)) AS PENDIENTES, O.FEC_CRE AS FECHA "
            + "FROM FLOW.ORDEN_DET D "
            + "	INNER JOIN FLOW.ORDEN_TRABAJO O ON O.ID = D.ORDEN "
            + "	INNER JOIN  sgm_app.ACL_USER U ON U.ID = O.RESPONSABLE "
            + "WHERE "
            + "D.ESTADO = TRUE "
            + "GROUP BY O.NUM_ORDEN, U.USUARIO, O.FEC_CRE "
            + "ORDER BY U.USUARIO ASC;";

    public static String getResumenUsuarios(String usr) {
        String x = "SELECT U.USUARIO AS RESPONSABLE, COUNT((SELECT DT.ID FROM FLOW.ORDEN_DET DT WHERE DT.ID = D.ID AND DT.PREDIO IS NOT NULL)) AS FINALIZADAS, "
                + "COUNT((SELECT DT.ID FROM FLOW.ORDEN_DET DT WHERE DT.ID = D.ID AND DT.PREDIO IS NULL)) AS PENDIENTES,"
                + "COUNT((SELECT DT.ID FROM FLOW.ORDEN_DET DT LEFT OUTER JOIN  sgm_app.CAT_PREDIO PX ON PX.ID = DT.PREDIO WHERE DT.ID = D.ID AND PX.PROCESADOS = TRUE)) AS REPROCESADOS "
                + "FROM FLOW.ORDEN_DET D "
                + "	INNER JOIN FLOW.ORDEN_TRABAJO O ON O.ID = D.ORDEN "
                + "	INNER JOIN  sgm_app.ACL_USER U ON U.ID = O.RESPONSABLE "
                + "WHERE "
                + "D.ESTADO = TRUE ";
        if (usr != null) {
            x += " AND U.USUARIO = '" + usr + "' GROUP BY U.USUARIO ORDER BY U.USUARIO;";
        } else {
            x += " GROUP BY U.USUARIO ORDER BY U.USUARIO;";
        }
        return x;
    }

    public static String getFotosPredio = "select e from FotoPredio e where e.codPredio = :predio";

    public static String getUsuarioByNomb = "select e from Usuario e where e.USUARIO = :user";

    public static String getPrediosPendientes = "select e from OrdenDet e where e.estadoDet = 'P' and e.datoAct is not null and e.predio is not null";

    

}
