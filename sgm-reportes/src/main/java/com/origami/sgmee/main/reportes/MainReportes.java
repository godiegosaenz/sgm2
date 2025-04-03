/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgmee.main.reportes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Angel Navarro
 */
public class MainReportes {

    private static final Logger LOG = Logger.getLogger(MainReportes.class.getName());
    public static final String META = "/META-INF";
    public static final String RESOURCES = "/resources";
    public static final String REPORTES = "/reportes";
    public static final String DELIMITADOR = "/";

    /**
     *
     * @param nombreReporte Nombre del reporte
     * @return ruta del reporte si existe caso contrario null
     */
    public InputStream getReporte(String nombreReporte) {
        try {
            URL resource = null;
            if (!nombreReporte.startsWith(DELIMITADOR)) {
                nombreReporte = DELIMITADOR + nombreReporte;
            }
            if (nombreReporte.contains(REPORTES)) {
                resource = MainReportes.class.getResource(META + RESOURCES + nombreReporte);
            } else {
                resource = MainReportes.class.getResource(META + RESOURCES + REPORTES + nombreReporte);
            }
            if (resource != null) {
                return resource.openStream();
//                return resource.getPath();
            } else {
                LOG.log(Level.WARNING, "Recurso no encontrado: {0}", nombreReporte);
                return null;
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, nombreReporte, e);
        }
        return null;
    }

    /**
     *
     * @param subCarpeta Nombre de la subcarpeta
     * @param nombreReporte Nombre del reporte
     * @return ruta del reporte si existe caso contrario null
     */
    public String getReporte(String subCarpeta, String nombreReporte) {
        try {
            if (!nombreReporte.startsWith(DELIMITADOR)) {
                nombreReporte = DELIMITADOR + nombreReporte;
            }
            URL resource = null;
            if (nombreReporte.contains(REPORTES)) {
                resource = MainReportes.class.getResource(META + RESOURCES + DELIMITADOR + subCarpeta + nombreReporte);
            } else {
                resource = MainReportes.class.getResource(META + RESOURCES + REPORTES + "/" + subCarpeta + nombreReporte);
            }
            if (resource != null) {
                return resource.getPath();
            } else {
                LOG.log(Level.WARNING, "Recurso no encontrado: {0}", nombreReporte);
                return null;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, nombreReporte, e);
        }
        return null;
    }

}
