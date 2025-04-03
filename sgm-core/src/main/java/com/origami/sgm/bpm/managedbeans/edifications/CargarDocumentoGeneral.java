/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.Resolucion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Archivo;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class CargarDocumentoGeneral extends BpmManageBeanBaseRoot implements Serializable {
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @Inject
    private UserSession uSession;
    
    private HashMap<String, Object> params;
    private Observaciones obs;
    private HistoricoTramites ht;
    private Resolucion resolucion;
    private Boolean tieneRes;
    
     @PostConstruct
    public void init() {
        try{
            if (uSession != null && uSession.getTaskID() != null) {
                params = new HashMap<>();
                obs = new Observaciones();
                this.setTaskId(uSession.getTaskID());
                String s = this.getVariable(uSession.getTaskID(), "tramite").toString();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                resolucion = new Resolucion();
                resolucion.setEstado(Boolean.TRUE);
                resolucion.setFecha(new Date());
                resolucion.setTramite(ht);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void verificarResolucion(){
        if(resolucion.getNumResolucion()==null && tieneRes){
            JsfUti.executeJS("PF('confirmDlg').show()");
        }else{
            JsfUti.executeJS("PF('obs').show()");
        }
    }
    
    public void completarTarea(){
        try{
            if(this.getFiles()!=null && !this.getFiles().isEmpty()){
                if(resolucion.getNumResolucion()!=null){
                    services.persist(resolucion);
                }
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(uSession.getName_user());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                
                services.persist(obs);
                this.params.put("carpeta", ht.getCarpetaRep());
                this.params.put("listaArchivos", this.getFiles());
                this.params.put("listaArchivosFinal", new ArrayList<Archivo>());
                this.params.put("prioridad", 50);
                this.params.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            }else{
                JsfUti.messageError(null, "Error", "No ha subido ningún documento");
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

    public Resolucion getResolucion() {
        return resolucion;
    }

    public void setResolucion(Resolucion resolucion) {
        this.resolucion = resolucion;
    }

    public Boolean getTieneRes() {
        return tieneRes;
    }

    public void setTieneRes(Boolean tieneRes) {
        this.tieneRes = tieneRes;
    }
    
}
