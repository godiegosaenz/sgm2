/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.ResTareasUsuarios;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.util.List;
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
public class AsignacionTecnicoJuridicoDP extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @Inject
    private UserSession uSession;
    
    private HistoricoTramites ht;
    private ResTareasUsuarios resumenTecnico;
    private List<ResTareasUsuarios> resTecnicos;
    
    @PostConstruct
    public void init(){
        if (uSession != null && uSession.getTaskID() != null) {
            this.setTaskId(uSession.getTaskID());
            ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
            
            if (ht != null) {
                
            }
        }
    
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public ResTareasUsuarios getResumenTecnico() {
        return resumenTecnico;
    }

    public void setResumenTecnico(ResTareasUsuarios resumenTecnico) {
        this.resumenTecnico = resumenTecnico;
    }

    public List<ResTareasUsuarios> getResTecnicos() {
        return resTecnicos;
    }

    public void setResTecnicos(List<ResTareasUsuarios> resTecnicos) {
        this.resTecnicos = resTecnicos;
    }
    
    
}
