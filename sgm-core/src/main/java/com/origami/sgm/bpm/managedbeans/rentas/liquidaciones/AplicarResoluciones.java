/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas.liquidaciones;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author origami
 */
@Named
@ViewScoped
public class AplicarResoluciones implements Serializable{
    public static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AplicarResoluciones.class.getName());
    
    @PostConstruct
    public void initView() {
        try {
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
        
}
