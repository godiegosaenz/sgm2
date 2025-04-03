/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.activiti.engine.task.Attachment;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class EntregarCertificadoView extends BpmManageBeanBaseRoot implements Serializable {

    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(EntregarCertificadoView.class.getName());
    
    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;
    private List<Attachment> certificadoUsoDeSuelo;
    private RenPermisosFuncionamientoLocalComercial permiso;
    private Attachment certificado;
    private Map<String, Object> entradas;
    
    private HistoricoTramites ht;
    private Observaciones obs;
    private String observacion;
    
    @PostConstruct
    public void initView() {
        try {
            if (session.esLogueado() && session.getTaskID() != null) {
                this.setTaskId(session.getTaskID());
                String var = (String) this.getVariable(session.getTaskID(), "tramite").toString();
                ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                if (ht != null) {
                    permiso = ht.getPermisoDeFuncionamientoLC();
                    entradas = new HashMap<>();
                    obs = new Observaciones();
                    
                    entradas.put("localComercialList", new ArrayList<RenLocalComercial>());
                    
                    ((List)entradas.get("localComercialList")).add(permiso.getLocalComercial());
                    for(Attachment temp : this.getProcessInstanceAttachmentsFiles()){
                        if(certificado == null)
                            certificado = temp;
                        else{
                            if(temp.getTime().after(certificado.getTime()))
                                certificado = temp;
                        }
                    }
                    if(certificado != null){
                        certificadoUsoDeSuelo = new ArrayList();
                        certificadoUsoDeSuelo.add(certificado);
                    }
                }else
                    this.continuar();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completarTarea() {
        try {
            if (session != null && session.getTaskID() != null) {
                String obs = observacion;
                if (obs == null) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                    return;
                }
                if (obs.length() == 0) {
                    JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                    return;
                }
                this.obs.setObservacion(observacion);
                this.obs.setEstado(Boolean.TRUE);
                this.obs.setFecCre(new Date());
                this.obs.setIdTramite(ht);
                this.obs.setUserCre(session.getName_user());
                this.obs.setTarea(this.getTaskDataByTaskID().getName());

                servicesACL.persist(obs);

                this.completeTask(session.getTaskID(), new HashMap<String, Object>());
                this.continuar();
            } else {
                this.continuar();
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, params);
        }
    }
    
    public List<Attachment> getCertificadoUsoDeSuelo() {
        return certificadoUsoDeSuelo;
    }

    public void setCertificadoUsoDeSuelo(List<Attachment> certificadoUsoDeSuelo) {
        this.certificadoUsoDeSuelo = certificadoUsoDeSuelo;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
}
