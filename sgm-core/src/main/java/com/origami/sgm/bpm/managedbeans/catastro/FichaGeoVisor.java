/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.app.AppProps;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * 
 * @author Fernando
 */
@ApplicationScoped
@Named("fichaGeoVisor")
public class FichaGeoVisor  {

    public FichaGeoVisor() {
    }
    
    public Boolean getGeovisorEnabled() {
        return AppProps.getBoolean("com.origami.sgmee.FichaGeoVisor.geovisorEnabled", Boolean.FALSE);
    }

    public String getJsIncludesFacelet() {
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.jsIncludesFacelet", "/geo/ficha-visor/jsinclude.xhtml");
    }
    
    public String getFaceletMap(){
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.faceletMap", "/geo/ficha-visor/ficha-visor.xhtml");
    }

    public String getFaceletJsData() {
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.faceletJsData", "/geo/ficha-visor/visor-datafields.xhtml");
    }
    
    public String getGeoserverUrl() {
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.geoserverDomain", "http://localhost/geoserver");
    }
    
    public String getSrid(){
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.srid", "EPSG:3857");
    }
    
    public String getBackGroupLayer(){
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.backGroupLayer", "ibarra:solar_habil");
    }
    
    public String getPredioWfsLayer(){
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.predioWfsLayer", "ibarra:solar_habil");
    }
    
    public String getPredioWfsLayer_type(){
        String[] parts = getPredioWfsLayer().split(Pattern.quote(":")); 
        return parts[1];
    }
    
    public String getPredioWfsLayer_prefix(){
        String[] parts = getPredioWfsLayer().split(Pattern.quote(":")); 
        return parts[0];
    }
    
    public String getPredioWfsNsuri(){
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.predioWfsNsuri", "ibarra");
    }
    
    public String getPredioWfsLayer_codigoField(){
        return AppProps.getString("com.origami.sgmee.FichaGeoVisor.predioWfsLayer_codigoField", "codigo");
    }
    
    
    
}
