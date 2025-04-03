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
import com.origami.sgm.entities.ParametrosDisparador;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.Archivo;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class BomberosView extends BpmManageBeanBaseRoot implements Serializable {

    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(BomberosView.class.getName());

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;

    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenPermisosFuncionamientoLocalComercial permiso;
    private BigDecimal areaBombero;

    @PostConstruct
    public void initView() {
        try {
            if (session != null && session.getTaskID() != null) {
                entradas = new HashMap<>();
                this.setTaskId(session.getTaskID());
                ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                if (ht != null) {
                    permiso = ht.getPermisoDeFuncionamientoLC();
                    
                    entradas.put("obs", new Observaciones());
                    entradas.put("localComercialList", new ArrayList<RenLocalComercial>());
                    ((List) entradas.get("localComercialList")).add(permiso.getLocalComercial());
                }

            } else {
                this.continuar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completarTarea() {
        try {
            String obs = ((Observaciones) entradas.get("obs")).getObservacion();
            if (obs == null) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                return;
            }
            if (obs.length() == 0) {
                JsfUti.messageError(null, "Advertencia", "Debe ingresar las Observaciones de la tarea.");
                return;
            }
            HashMap<String, Object> paramsActiviti = new HashMap();
            ((Observaciones) entradas.get("obs")).setEstado(Boolean.TRUE);
            ((Observaciones) entradas.get("obs")).setFecCre(new Date());
            ((Observaciones) entradas.get("obs")).setIdTramite(ht);
            ((Observaciones) entradas.get("obs")).setUserCre(session.getName_user());
            ((Observaciones) entradas.get("obs")).setTarea(this.getTaskDataByTaskID().getName());

            servicesACL.persist(((Observaciones) entradas.get("obs")));
            permiso.setAreaBombero(areaBombero);
            servicesACL.persist(permiso);
            
            paramsActiviti.put("carpeta", ht.getCarpetaRep());
            paramsActiviti.put("listaArchivos", this.getFiles());
            paramsActiviti.put("listaArchivosFinal", new ArrayList<Archivo>());
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("aprobado", this.getFiles().isEmpty());
            
            this.completeTask(this.getTaskId(), paramsActiviti);
            this.continuar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validarDoc() {
        if (this.getFiles() == null || this.getFiles().isEmpty()) {
            JsfUti.messageInfo(null, "Info", "Debe ingresar un documento");
        } else {
            JsfUti.executeJS("PF('obs').show()");
        }
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
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

    public BigDecimal getAreaBombero() {
        return areaBombero;
    }

    public void setAreaBombero(BigDecimal areaBombero) {
        this.areaBombero = areaBombero;
    }

}
