/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoReporteTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.HashMap;
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
public class AprobarCarpetaTecnicoGeneral extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;

    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    private HistoricoTramites ht;
    private Observaciones obs;
    private HashMap<String, Object> datosIniciales;
    private HashMap<String, Object> params;
    private List<HistoricoReporteTramite> hrts;
    private String nombreReporte;
    
    @PostConstruct
    public void init() {
        try{
            if (uSession != null && uSession.getTaskID() != null) {
                params = new HashMap();
                obs = new Observaciones();
                datosIniciales = new HashMap<>();
                this.setTaskId(uSession.getTaskID());
                ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
                hrts = services.findAll(Querys.getReporteByNombreTareaSinEstado, new String[]{"nombreTarea", "idProceso"}, new Object[]{nombreReporte, this.ht.getIdProceso()});
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void verDocumento(HistoricoReporteTramite doc) {
        this.showDocuments(doc.getUrl(), "pdf");
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

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HashMap<String, Object> getDatosIniciales() {
        return datosIniciales;
    }

    public void setDatosIniciales(HashMap<String, Object> datosIniciales) {
        this.datosIniciales = datosIniciales;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public List<HistoricoReporteTramite> getHrts() {
        return hrts;
    }

    public void setHrts(List<HistoricoReporteTramite> hrts) {
        this.hrts = hrts;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }
    
}
