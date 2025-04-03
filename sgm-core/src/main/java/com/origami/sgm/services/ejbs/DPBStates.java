/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

/**
 * DeshabilitadorPersistencialBloques estados request
 * @author Fernando
 */
@RequestScoped
public class DPBStates {
    
    private Boolean aroundYaIniciado;
    
    @PostConstruct
    protected void inicializar(){
        aroundYaIniciado = false;
    }
    
    public DPBStates() {
    }

    public Boolean getAroundYaIniciado() {
        return aroundYaIniciado;
    }

    public void setAroundYaIniciado(Boolean aroundYaIniciado) {
        this.aroundYaIniciado = aroundYaIniciado;
    }
    
    
    
}
