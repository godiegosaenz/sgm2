/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.consultas;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import util.JsfUti;

/**
 *
 * @author origami-idea
 */
@Named
@ViewScoped
public class Consultas extends BpmManageBeanBaseRoot implements Serializable {
    
    private Boolean tipo1 = true;
    private Boolean tipo2 = false;
    private Boolean hayTramite = false;
    private Boolean selected = true;
    
    private String cedula = "";
    private String numeroTramite = "";
    
    private List<Observaciones> obsList;
    private List<HistoricTaskInstance> tareas;
    private List<HistoricoTramites> hts;
    private CatEnte enteEncontrado;
    private List<CatEnte> enteList;
    
    private HistoricTaskInstance htinst;
    private HistoricoTramites hs;
    
    @javax.inject.Inject
    private Entitymanager services;
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    
    public void cambiarTipo1() {
        selected = tipo1;
        if (tipo1 && tipo2) {
            tipo2 = false;
            hayTramite = false;
            tareas = null;
            selected = tipo1;
        }
        if (!tipo1) {
            if (!tipo2) {
                hayTramite = false;
                tareas = null;
                selected = tipo1;
            }
        }
    }
    
    public void cambiarTipo2() {
        selected = tipo2;
        if (tipo1 && tipo2) {
            tipo1 = false;
            hayTramite = false;
            tareas = null;
            selected = tipo2;
        }
        if (!tipo2) {
            if (!tipo1) {
                hayTramite = false;
                tareas = null;
                selected = tipo2;
            }
        }
    }
    
    @PostConstruct
    public void init() {
        hts = new ArrayList<>();
    }
    
    public void verObservaciones(HistoricoTramites htr) {
        hs = htr;
        obsList = (List<Observaciones>) hs.getObservacionesCollection();
    }
    
    public void buscarTramite() {
        hts = null;
        enteEncontrado = null;
        enteList = null;
        
        if (tipo1) {
            if (cedula != null) {
                if (cedula.length() == 10 || cedula.length() == 13) {
                    enteEncontrado = (CatEnte) services.find(Querys.getEnteByIdent, new String[]{"ciRuc"}, new Object[]{cedula});
                    if (enteEncontrado != null) {
                        hts = (List<HistoricoTramites>) services.findAll(Querys.getHistoricoTramitesByEnte, new String[]{"persona"}, new Object[]{enteEncontrado});
                        if (hts != null) {
                            hayTramite = true;
                            enteList = new ArrayList();
                            enteList.add(enteEncontrado);
                        }
                    } else {
                        JsfUti.messageError(null, "Error", "No se ha encontrado ningún ente. Vuelva a intentarlo");
                        return;
                    }
                } else {
                    JsfUti.messageError(null, "Error", "El número de dígitos es incorrecto.");
                    return;
                }
            } else {
                JsfUti.messageError(null, "Error", "No ha ingresado ningún número.");
                return;
            }
        }
        if (tipo2) {
            if (numeroTramite != null) {
                try {
                    hts = new ArrayList<>();
                    HistoricoTramites htTemp = permisoServices.getHistoricoTramiteById(Long.parseLong(numeroTramite));
                    if (htTemp == null) {
                        hts = null;
                        tareas = null;
                        JsfUti.messageError(null, "Error", "No se encontró ningún trámite, intente otro número.");
                        return;
                    }
                    hts.add(htTemp);
                    if (hts != null) {
                        hayTramite = true;
                    }
                } catch (Exception e) {
                    Logger.getLogger(Consultas.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        
    }
    
    public void setearTareas(HistoricoTramites htr) {
        hs = htr;
        try {
            if(hs.getIdProcesoTemp() != null || hs.getIdProceso() != null){
                if (hs.getIdProcesoTemp() != null) {
                    tareas = this.getTaskByProcessInstanceIdMain(htr.getIdProcesoTemp());
                } else {
                    tareas = this.getTaskByProcessInstanceIdMain(htr.getIdProceso());
                }
            }
        } catch (Exception e) {
            tareas = null;
        }
    }
    
    public void verificarDoc(HistoricoTramites htr) {
        hs = htr;
        JsfUti.executeJS("PF('docAsoc').show()");
        JsfUti.update("formDoc");
        
    }
    
    public List<Attachment> getAttachments() {
        if (hs != null) {
            if(hs.getIdProcesoTemp() != null || hs.getIdProceso() != null){
                if (hs.getIdProceso() != null) {
                    return getProcessInstanceAllAttachmentsFiles(hs.getIdProceso());
                } else {
                    return getProcessInstanceAllAttachmentsFiles(hs.getIdProcesoTemp());
                }
            }
        }
        return null;
    }
    
    public String usuariosCandidatos(String idTask) {
        String candidatosAsignados = "";
        try {
            Task t = this.getTaskDataByTaskID(idTask);
            if (t != null) {
                if (t.getAssignee() == null) {
                    List<IdentityLink> listIdentityLinks = this.obtenerIdentityLinkByIdTask(idTask);
                    if (!listIdentityLinks.isEmpty()) {
                        for (IdentityLink identityLink : listIdentityLinks) {
                            if (identityLink.getType().equalsIgnoreCase("candidate")) {
                                candidatosAsignados = candidatosAsignados + identityLink.getUserId() + ", ";
                            }
                        }
                    }
                }
            } else {
                if (!hs.getEstado().equalsIgnoreCase("Finalizado")) {
                    for (HistoricIdentityLink identityLink : this.obtenerHistoricIdentityLinkByIdTask(idTask)) {
                        candidatosAsignados = candidatosAsignados + identityLink.getUserId() + ", ";
                    }
                } 
            }
        } catch (Exception e) {
            Logger.getLogger(Consultas.class.getName()).log(Level.SEVERE, null, e);
        }
        if(candidatosAsignados.length() > 2){
            return candidatosAsignados.substring(0, candidatosAsignados.length() - 2);
        }else{
            return null;
        }
        
    }
    
    public Boolean getSelected() {
        return selected;
    }
    
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
    public Boolean getTipo1() {
        return tipo1;
    }
    
    public void setTipo1(Boolean tipo1) {
        this.tipo1 = tipo1;
    }
    
    public Boolean getTipo2() {
        return tipo2;
    }
    
    public void setTipo2(Boolean tipo2) {
        this.tipo2 = tipo2;
    }
    
    public Boolean getHayTramite() {
        return hayTramite;
    }
    
    public void setHayTramite(Boolean hayTramite) {
        this.hayTramite = hayTramite;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getNumeroTramite() {
        return numeroTramite;
    }
    
    public void setNumeroTramite(String numeroTramite) {
        this.numeroTramite = numeroTramite;
    }
    
    public List<HistoricTaskInstance> getTareas() {
        return tareas;
    }
    
    public void setTareas(List<HistoricTaskInstance> tareas) {
        this.tareas = tareas;
    }
    
    public List<Observaciones> getObsList() {
        return obsList;
    }
    
    public void setObsList(List<Observaciones> obsList) {
        this.obsList = obsList;
    }
    
    public HistoricTaskInstance getHtinst() {
        return htinst;
    }
    
    public void setHtinst(HistoricTaskInstance htinst) {
        this.htinst = htinst;
    }
    
    public List<HistoricoTramites> getHts() {
        return hts;
    }
    
    public void setHts(List<HistoricoTramites> hts) {
        this.hts = hts;
    }
    
    public HistoricoTramites getHs() {
        return hs;
    }
    
    public void setHs(HistoricoTramites hs) {
        this.hs = hs;
    }
    
    public CatEnte getEnteEncontrado() {
        return enteEncontrado;
    }
    
    public void setEnteEncontrado(CatEnte enteEncontrado) {
        this.enteEncontrado = enteEncontrado;
    }
    
    public List<CatEnte> getEnteList() {
        return enteList;
    }
    
    public void setEnteList(List<CatEnte> enteList) {
        this.enteList = enteList;
    }
    
}
