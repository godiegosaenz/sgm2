/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.agenda;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.Agenda;
import com.origami.sgm.entities.AgendaDet;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.agenda.AgendaServ;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.Faces;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class AprobacionDirAgenda extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private AgendaServ serv;
    private Agenda agenda;
    private Observaciones obs;
    private AclUser us;
    private HashMap<String, Object> params;
    private String observaciones;
    List<AgendaDet> intervinientes;
    private boolean asistencia = false, confirmacion = false;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void init() {
        if (sess != null) {
            us = manager.find(AclUser.class, sess.getUserId());
            params = new HashMap<>();
            if (sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                if (this.getVariable(sess.getTaskID(), "agenda") != null) {
                    agenda = manager.find(Agenda.class, this.getVariable(sess.getTaskID(), "agenda"));
                    obs = new Observaciones();
                }
            }
        }
    }

    public void guardar() {
        try {
            if (obs.getObservacion() != null) {
                obs.setTarea(this.getTaskDataByTaskID().getName());
                obs.setIdTramite(agenda.getTramite());
                obs.setFecCre(new Date());
                obs.setUserCre(sess.getName_user());
                serv.actIntervinientes(agenda, intervinientes);
                manager.persist(obs);
                params.put("aprobado", confirmacion);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            } else {
                Faces.messageWarning(null, "Advertencia", "Ingrese la observacion respectiva");
            }
        } catch (Exception e) {
            Logger.getLogger(EdicionAgenda.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(boolean asistencia) {
        this.asistencia = asistencia;
    }

    public boolean getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(boolean confirmacion) {
        this.confirmacion = confirmacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<AgendaDet> getIntervinientes() {
        return intervinientes;
    }

    public void setIntervinientes(List<AgendaDet> intervinientes) {
        this.intervinientes = intervinientes;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }
    

}
