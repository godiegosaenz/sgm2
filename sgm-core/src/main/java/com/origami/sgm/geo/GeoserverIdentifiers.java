/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.geo;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Fernando
 */
@Singleton
@Lock(LockType.READ)
@ApplicationScoped
public class GeoserverIdentifiers {
    
    private String separatorNs = ":";
    protected String namespaceDefault = "sanmiguel";
    protected String colindanteLayer = "catastro_colindantes";
    protected String predioSeleccionadoLayer = "catastro_ficha";

    public GeoserverIdentifiers() {
    }

    public String getNamespaceDefault() {
        return namespaceDefault;
    }

    public String getColindanteLayer() {
        return colindanteLayer;
    }

    public String getPredioSeleccionadoLayer() {
        return predioSeleccionadoLayer;
    }

    public String concatNs(String capa){
        if(getNamespaceDefault()==null || getNamespaceDefault().trim().isEmpty()){
            return capa;
        }
        else{
            return getNamespaceDefault() + getSeparatorNs() + capa;
        }
    }

    public String getSeparatorNs() {
        return separatorNs;
    }
    
    
    
}
