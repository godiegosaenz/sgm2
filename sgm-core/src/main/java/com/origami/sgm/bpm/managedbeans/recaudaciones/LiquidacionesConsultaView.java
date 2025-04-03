/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.recaudaciones;

import com.origami.session.UserSession;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.lazymodels.RenLiquidacionesLazy;
import com.origami.sgm.services.interfaces.financiero.RecaudacionesService;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class LiquidacionesConsultaView implements Serializable {

    public static final Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(LiquidacionesConsultaView.class.getName());
    
    @javax.inject.Inject
    private RecaudacionesService recaudacion;
    @javax.inject.Inject
    private Entitymanager manager;    
    @Inject
    private UserSession session;
    
    protected RenLiquidacionesLazy titulosCredito;
    protected RenLiquidacion liquidacion;
    
    @PostConstruct
    public void initView() {
        try {
            if (session.esLogueado()) {
                titulosCredito = new RenLiquidacionesLazy();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public RenLiquidacionesLazy getTitulosCredito() {
        return titulosCredito;
    }

    public void setTitulosCredito(RenLiquidacionesLazy titulosCredito) {
        this.titulosCredito = titulosCredito;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }
    
}
