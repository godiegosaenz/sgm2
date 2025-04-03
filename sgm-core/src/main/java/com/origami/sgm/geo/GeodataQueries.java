/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.geo;

/**
 *
 * @author Fernando
 */
public class GeodataQueries {
    
    private String geopredios = "SELECT gid, id, parroquia, clave_catastral, zona, sector, manzana, solar, nombre, " +
        " codigo_mun, area_solar, capa_rod, nombre_de_, sector_par, tipo_via, nombre_bar, recolec_ba, " +
        " geom, substring(clave_catastral from 5 for 12) AS clave_min " +
        " FROM geodata.geo_predio; ";
    
    
    
}
