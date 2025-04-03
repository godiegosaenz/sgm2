/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.secGral;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CargarDocumentoSecGeneral extends BpmManageBeanBaseRoot implements Serializable {
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @Inject
    private UserSession uSession;
    
    private HashMap<String, Object> params;
    private Observaciones obs;
    private HistoricoTramites ht;
    
     @PostConstruct
    public void init() {
        try{
            if (uSession != null && uSession.getTaskID() != null) {
                params = new HashMap<>();
                obs = new Observaciones();
                this.setTaskId(uSession.getTaskID());
                String s = this.getVariable(uSession.getTaskID(), "tramite").toString();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void completarTarea(){
        try{
            if(this.getFiles()!=null && !this.getFiles().isEmpty()){
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                this.params.put("prioridad", 50);
                this.params.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }
    
}
