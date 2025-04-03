/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.divisionPredio;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.Resolucion;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class CargarResolucion extends BpmManageBeanBaseRoot implements Serializable {
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    private HistoricoTramites ht;
    private List<HistoricoReporteTramite> hrts;
    private String nombreReporte;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private Boolean aprobar;
    private Resolucion resolucion;
    private Boolean tieneRes;
    
    @PostConstruct
    public void init() {
        try {
            if (uSession != null && uSession.getTaskID() != null) {
                this.setTaskId(uSession.getTaskID());
                obs = new Observaciones();
                resolucion = new Resolucion();
                params = new HashMap<>();
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                hrts = (List<HistoricoReporteTramite>) ht.getHistoricoReporteTramiteCollection();
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
                //ht.setEstado("Finalizado");
                if(servicesDP.guardarResolucion(resolucion, obs, ht)){
                    this.params.put("urlTec", "/vistaprocesos/edificaciones/divisionPredio/realizarDP.xhtml");
                    this.params.put("carpeta", ht.getCarpetaRep());
                    this.params.put("listaArchivos", this.getFiles());
                    this.params.put("listaArchivosFinal", new ArrayList<Archivo>());
                    this.params.put("prioridad", 50);
                    this.params.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
                    this.completeTask(this.getTaskId(), params);
                    this.continuar();
                }
            }else{
                JsfUti.messageError(null, "Error", "No ha subido ningún documento");
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
    
    public void mostrarObservaciones(boolean aprobado) {
        aprobar = aprobado;
        JsfUti.executeJS("PF('obs').show();");
        JsfUti.update("frmObs");
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

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public Boolean getAprobar() {
        return aprobar;
    }

    public void setAprobar(Boolean aprobar) {
        this.aprobar = aprobar;
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
