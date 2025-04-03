/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.otrosTramites;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class RevisionInformeTecnico extends BpmManageBeanBaseRoot implements Serializable {
    
    public static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession sess;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private Observaciones obs;
    private HistoricoTramites ht;
    private Boolean validar;
    private HashMap<String, Object> paramsActiviti;
    
    @PostConstruct
    public void initView() {
        if (sess.esLogueado() != null) {
            try {
                this.setTaskId(sess.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                if(ht==null)
                    return;
                paramsActiviti = new HashMap<>();
                obs = new Observaciones();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * Asigna el valor de una variable booleana y muestra el dialog de observación.
     * 
     * @param b 
     */
    public void validarDocs(Boolean b){
        validar = b;
        JsfUti.executeJS("PF('obs').show();");
    }
    
    /**
     * Termina la tarea y el proceso.
     * 
     */
    public void completarTarea(){
        if(obs.getObservacion()!=null){
            if(validar){
                ht.setEstado("Finalizado");
                paramsActiviti.put("estatus", 1);
                services.update(ht);
            }
            else
                paramsActiviti.put("estatus", 2);
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(ht);
            obs.setUserCre(sess.getName_user());
            obs.setTarea(this.getTaskDataByTaskID().getName());
            services.persist(obs);
            paramsActiviti.put("prioridad", 50);
            this.completeTask(this.getTaskId(), paramsActiviti);
            this.continuar();
        }else{
            JsfUti.messageError(null, "Error", "Debe ingresar una observación antes de continuar.");
        }
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null) {
            obs.setObservacion(this.getTaskDataByTaskID().getName());
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
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
