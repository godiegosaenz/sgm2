/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.predios;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

/**
The @Resource annotation can be used to inject several things including DataSources, Topics, Queues, etc. Most of these are container supplied objects.

It is possible, however, to supply your own values to be injected via an <env-entry> in your ejb-jar.xml or web.xml deployment descriptor. Java EE 6 supported <env-entry> types are limited to the following:

java.lang.String
java.lang.Integer
java.lang.Short
java.lang.Float
java.lang.Double
java.lang.Byte
java.lang.Character
java.lang.Boolean
java.lang.Class
java.lang.Enum (any enum)

* 
 * @author Fernando
 */
@Singleton
@Lock(LockType.READ)
@ApplicationScoped
public class CatastroGeoConfigs {
    
    @Resource
    private String predioLayer;
    @Resource
    private String wfsUrl;
    @Resource
    private String wmsUrl;
    @Resource
    private String claveAttrName;
    @Resource
    private String wfsVersion;
    @Resource
    private String wmsVersion;
    @Resource
    private Double envelopeAdd;
    @Resource
    private String croquisLayer;
    @Resource
    private String srid;
    
    
    public CatastroGeoConfigs() {
    }

    public String getPredioLayer() {
        return predioLayer;
    }

    public String getWfsUrl() {
        return wfsUrl;
    }

    public String getClaveAttrName() {
        return claveAttrName;
    }

    public String getWfsVersion() {
        return wfsVersion;
    }

    public Double getEnvelopeAdd() {
        return envelopeAdd;
    }

    public String getWmsUrl() {
        return wmsUrl;
    }

    public String getWmsVersion() {
        return wmsVersion;
    }

    public String getCroquisLayer() {
        return croquisLayer;
    }

	public String getSrid() {
		return srid;
	}

	public void setSrid(String srid) {
		this.srid = srid;
	}


    
    
}
