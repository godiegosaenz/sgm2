/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.ResTareasUsuarios;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class AsignarTecnico extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    private PropiedadHorizontalServices services;

    protected String observ;
    protected HistoricoTramites ht;
    private ResTareasUsuarios tecnico;
    private List<ResTareasUsuarios> resTecnicos;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            this.setTaskId(session.getTaskID());
            ht = new HistoricoTramites();
            llenarTecnicos();

            ht = permisoServices.getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
        } else {
            this.continuar();
        }
    }

    public void llenarTecnicos() {
        try {
            resTecnicos = new ArrayList<>();
            List<AclUser> tecnicos = new ArrayList<>();
            for (Long dept : session.getDepts()) {
                GeDepartamento dJ = (GeDepartamento) acl.find(GeDepartamento.class, dept);
                for (AclRol r : dJ.getAclRolCollection()) {
                    if (!r.getIsDirector()) {// Director Juridico 77
                        for (AclUser aclUser : r.getAclUserCollection()) {
                            if (!tecnicos.contains(aclUser)) {
                                tecnicos.add(aclUser);
                            }
                        }
                    }
                }
            }
            for (AclUser t : tecnicos) {
                if (t.getEnte() != null && t.getEnte().getEnteCorreoCollection().isEmpty() != true) {
                    resTecnicos.add(this.getResumenTareasUsuarios(t.getUsuario(), t.getEnte().getEnteCorreoCollection().get(0).getEmail()));
                } else {
                    resTecnicos.add(this.getResumenTareasUsuarios(t.getUsuario(), null));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AsignarTecnico.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    public void completar() {
        if (observ == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        Observaciones ob = services.guardarObservaciones(ht, session.getName_user(), observ, this.getTaskDataByTaskID().getTaskDefinitionKey());
        if (ob != null) {
            HashMap<String, Object> paramt = new HashMap<>();
            if ("asignarTecnicoSecretGeneral".equalsIgnoreCase(this.getTaskDataByTaskID().getTaskDefinitionKey())) {
                paramt.put("tecnicoSecretariaGeneral", tecnico.getUser());
            }
            if ("asignacionTecnicoJuridico".equalsIgnoreCase(this.getTaskDataByTaskID().getTaskDefinitionKey())) {
                paramt.put("tecnicoJuridico", tecnico.getUser());
            }
            if ("asignarTecnicoCatastro".equalsIgnoreCase(this.getTaskDataByTaskID().getTaskDefinitionKey())) {
                paramt.put("tecnicoCatastro", tecnico.getUser());
            }
            paramt.put("prioridad", 50);
            this.completeTask(this.getTaskId(), paramt);
        }
        this.continuar();
    }

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public ResTareasUsuarios getTecnico() {
        return tecnico;
    }

    public void setTecnico(ResTareasUsuarios tecnico) {
        this.tecnico = tecnico;
    }

    public List<ResTareasUsuarios> getResTecnicos() {
        return resTecnicos;
    }

    public void setResTecnicos(List<ResTareasUsuarios> resTecnicos) {
        this.resTecnicos = resTecnicos;
    }

    public AsignarTecnico() {
    }

}
