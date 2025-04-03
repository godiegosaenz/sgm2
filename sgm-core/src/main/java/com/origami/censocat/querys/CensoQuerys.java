/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.querys;

/**
 *
 * @author CarlosLoorVargas
 */
public class CensoQuerys {

    public static String getPredios = "SELECT id, num_predio, provincia, canton, parroquia, sector, mz, solar,  "
            + "       div1, div2, div3, div4, div5, div6, div7, div8, div9, phv, phh,  "
            + "       prop_horizontal, ubicacion, ciudadela, tipo_propiedad, uso_predio,  "
            + "       topografia, tipo_suelo, forma, sec_urb, mz_urb, solar_urb, edificio,  "
            + "       direccion, planta, av_calle_p, av_calle_i, av_calle_urb, observaciones,  "
            + "       usr_cre, fec_cre, usr_act, fec_act, estado, alicuota, area_levantada,  "
            + "       cdla, tipo_conjunto, tipo_uso_edif, num_edificio, av_calle_num,  "
            + "       arrendado, fec_arriendo, mz_div, av_calle_s, nuevo, cerramiento,  "
            + "       uso_suelo, permiso_const, num_inspeccion, fecha_inspeccion, fecha_permiso,  "
            + "       localizacion, constructividad, etapa_edif, procesados, coordx,  "
            + "       coordy "
            + "  FROM catastro.predio "
            + "	WHERE num_predio = ?";

    public static String getEdificaciones = "SELECT e.id, e.predio, e.num_bloques, e.sec_bloque, e.num_edif, e.num_pisos, e.area_cons,  "
            + "       e.porc_cons, e.condicion, e.estado_edif, e.ano_fin_cons, e.rentado, e.usr_cre,  "
            + "       e.fec_cre, e.fec_act, e.estado "
            + "  FROM catastro.predio_edificacion e "
            + "	INNER JOIN catastro.predio p on p.id = e.predio "
            + "  WHERE p.num_predio = ? "
            + " ORDER BY id asc ";

    public static String getEdifcacionCaract = "SELECT c.id, c.edificacion, c.caracteristica, c.porcentaje, c.usr_cre, c.fec_cre,  "
            + "       c.fec_act, c.estado "
            + "  FROM catastro.edificacion_caract c "
            + "	INNER JOIN catastro.predio_edificacion pe on pe.id = c.edificacion "
            + "WHERE c.edificacion = ?";

    public static String getPredioServicios = "SELECT id, predio, alcan_sanitario, alcan_pluvial, abast_agua, conex_domiciliaria,  "
            + "       medidor_agua, num_medidor, pozo, abast_tanquero, ee, red_pub,  "
            + "       alum_pub, medidor_energia, num_medidor_energia, planta_ee, recol_basura,  "
            + "       aseo_calles, trans_pub, internet, gas_tub, tv_pag, user_cre,  "
            + "       user_act, fec_cre, fec_act, estado, asan_red_pub "
            + "  FROM catastro.predio_servicio  "
            + "  WHERE predio = ?";

    public static String getTipoEsctructura = "SELECT id, nombre, porcentual, orden, fec_cre, fec_act, estado "
            + "  FROM catastro.tipo_estructura where id = ?";

    public static String getDetalleTipoEstruc = "SELECT id, tipo, nombre, porcentual, orden, peso, fec_cre, fec_act,  "
            + "       estado, referencia "
            + "  FROM catastro.estructura_espec where id = ?";

    public static String getCtlg = "SELECT id, nombre, fec_cre, fec_act, estado "
            + "  FROM catastro.catalogo WHERE id = ?";

    public static String getCtlgItem = "SELECT id, catalogo, valor, codename, fec_cre, fec_act, estado, referencia "
            + "  FROM catastro.catalogo_item WHERE id = ?";

    public static String getUsosEdif = "SELECT id, predio, uso, fec_cre, fec_act, usr_cre, usr_act, estado "
            + "  FROM catastro.predio_uso_edificacion WHERE predio = ?";

    public static String getUsoPH = "SELECT id, predio, componente, descripcion, usr_cre, fec_cre, usr_act,  "
            + "       fec_act, estado "
            + "  FROM catastro.predio_uso WHERE predio = ?";

}
