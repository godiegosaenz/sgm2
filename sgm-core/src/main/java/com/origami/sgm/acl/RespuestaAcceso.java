/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class RespuestaAcceso implements Serializable{
    
    private Boolean accessGranted = false;
    
    
    public RespuestaAcceso(){
    }

    public Boolean getAccessGranted() {
        return accessGranted;
    }

    public void setAccessGranted(Boolean accessGranted) {
        this.accessGranted = accessGranted;
    }
    
    
    
    
}
