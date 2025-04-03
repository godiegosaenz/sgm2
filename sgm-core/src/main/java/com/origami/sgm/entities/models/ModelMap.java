/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities.models;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Transient;

/**
 *
 * @author Angel Navarro
 * @Date 11/06/2016
 */
public abstract class ModelMap {
    @Transient
    protected Map<String, Object> transients = new HashMap<>();
    
    public Map<String, Object> getTransients() {
        return transients;
    }

    public void setTransients(Map<String, Object> transients) {
        this.transients = transients;
    }

    
    public void add(String key, Object value){
        transients.put(key, value);
    }
    
    public <T>T get(String key){
        return (T) transients.get(key);
    }
    
    
}
