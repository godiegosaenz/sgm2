/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.config;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class ConfigGeodataPredial implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6836477714116532296L;
	private Boolean useGeoapi = false;
    private String geoapiUrl = null;
    
    public ConfigGeodataPredial() {
    }

    public Boolean getUseGeoapi() {
        return useGeoapi;
    }

    public void setUseGeoapi(Boolean useGeoapi) {
        this.useGeoapi = useGeoapi;
    }

    public String getGeoapiUrl() {
        return geoapiUrl;
    }

    public void setGeoapiUrl(String geoapiUrl) {
        this.geoapiUrl = geoapiUrl;
    }
    
    
    
}
