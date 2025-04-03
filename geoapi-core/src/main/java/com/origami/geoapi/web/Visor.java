/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.geoapi.web;

import com.origami.geoapi.predios.CatastroGeoConfigs;
import java.io.Serializable;
import javax.inject.Inject;

/**
 *
 * @author Fernando
 */
public abstract class Visor implements Serializable{
    
    @Inject
    protected CatastroGeoConfigs geoConf;
    
    public String getGroupLayer(){
        return "";
    }
    
    
    
}
