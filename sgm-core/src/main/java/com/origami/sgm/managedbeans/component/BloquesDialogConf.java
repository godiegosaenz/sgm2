/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Fernando
 */
@ViewScoped
public class BloquesDialogConf implements Serializable{
    
    private Boolean transitorio = false;
    
    @PostConstruct
    public void inicializar(){
        transitorio = false;
    }
    
    public BloquesDialogConf() {
    }

    public Boolean getTransitorio() {
        return transitorio;
    }

    public void setTransitorio(Boolean transitorio) {
        this.transitorio = transitorio;
    }
    
    
    
}
