/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.financiero;

import com.origami.session.UserSession;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class CorreccionOficio implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    private RentasServices services;

    @Inject
    private UserSession uSession;

    @PostConstruct
    public void initView() {
        try{
            if (uSession.esLogueado() && uSession.getTaskID() != null){
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
