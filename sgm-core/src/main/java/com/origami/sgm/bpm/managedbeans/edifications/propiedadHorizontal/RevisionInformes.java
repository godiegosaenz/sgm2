/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 * Revisión de Informes
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class RevisionInformes extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    @javax.inject.Inject
    private PropiedadHorizontalServices services;

    protected String observ;
    protected HistoricoTramites ht;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            this.setTaskId(session.getTaskID());
            ht = new HistoricoTramites();

            ht = permisoServices.getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));

        } else {
            this.continuar();
        }
    }

    public void completar() {
        if (observ == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        Observaciones ob = services.guardarObservaciones(ht, observ, session.getName_user(), observ);
        if (ob != null) {
            HashMap<String, Object> paramt = new HashMap<>();
            List<Long> roles = new ArrayList<>(); // "director_juridico" 77
            roles.add(77L);
            List<AclUser> users = acl.getTecnicosByRol(roles);
            for (AclUser usersj : users) {
                if (usersj.getSisEnabled() && usersj.getUserIsDirector()) {
                    paramt.put("directorJuridico", usersj.getUsuario());
                }
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

    public RevisionInformes() {
    }

}
