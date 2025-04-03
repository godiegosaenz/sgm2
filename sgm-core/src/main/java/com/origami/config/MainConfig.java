/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.config;

import java.io.Serializable;

/**
 *
 * @author OrigamiV2
 */
public class MainConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4477855666233225628L;
    private ConfigFichaPredial fichaPredial = new ConfigFichaPredial();
    private ConfigGeodataPredial geodataPredial = new ConfigGeodataPredial();
    public static String PREDIENTE = "Pendiente";
    public static String FINALIZADO = "Finalizado";
    public static String INACTIVO = "Inactivo";
    public static Boolean validarNumTramite = Boolean.FALSE;
    

    public MainConfig() {
    }

    public ConfigFichaPredial getFichaPredial() {
        return fichaPredial;
    }

    public void setFichaPredial(ConfigFichaPredial fichaPredial) {
        this.fichaPredial = fichaPredial;
    }

    public ConfigGeodataPredial getGeodataPredial() {
        return geodataPredial;
    }

    public void setGeodataPredial(ConfigGeodataPredial geodataPredial) {
        this.geodataPredial = geodataPredial;
    }

}
