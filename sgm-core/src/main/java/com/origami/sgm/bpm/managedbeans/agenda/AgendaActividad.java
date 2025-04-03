/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.agenda;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.AgendaModel;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.Agenda;
import com.origami.sgm.entities.AgendaDet;
import com.origami.sgm.services.interfaces.agenda.AgendaServ;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class AgendaActividad extends BpmManageBeanBaseRoot implements Serializable {

    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private AgendaServ serv;
    private Agenda agenda;
    private AgendaDet det;
    private AclUser us;
    private HashMap<String, Object> params;
    private AgendaModel datos;
    private boolean asistencia = false, confirmacion = false;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    protected void init() {
        if (sess != null) {
            us = manager.find(AclUser.class, sess.getUserId());
            params = new HashMap<>();
            if (sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                datos = (AgendaModel) this.getExecutionVariable(sess.getTaskID(), this.getTaskDataByTaskID(this.getTaskId()).getExecutionId(), "agenda");
                if (datos != null) {
                    det = manager.find(AgendaDet.class, datos.getIdDet());
                }
            }
        }
    }

    public void guardar() {
        try {
            if (us.getEnte().getId().equals(det.getAgenda().getResponsable().getId())) {
                det.getAgenda().setFinalizado(new Date());
                manager.persist(det.getAgenda());
                det.getAgenda().getTramite().setEstado("Finalizado");
                manager.persist(det.getAgenda().getTramite());
            }
            if (det.getAgenda().getConfirmacion()) {
                if (!confirmacion) {
                    Faces.messageWarning(null, "Advertencia", "Esta actividad requqiere su confirmacion");
                    return;
                }
            }
            if (det.getAgenda().getContestacion()) {
                if (!asistencia) {
                    Faces.messageWarning(null, "Advertencia", "Esta actividad requqiere su contestacion en la seccion de detalle");
                    return;
                }
            }
            if (det.getDetalle() != null) {
                det.setFecAtencion(new Date());
                datos.setDescripcion(det.getDetalle());
                manager.persist(det);
                params.put("carpeta", det.getAgenda().getTramite().getTipoTramite().getCarpeta());
                if (det.getAgenda().getTipo().getReqDoc()) {
                    if (this.getFiles().isEmpty()) {
                        Faces.messageWarning(null, "Advertencia", "Esta actividad requqiere que adjunte un documento");
                        return;
                    }
                }
                if (this.getFiles() != null) {
                    if (!this.getFiles().isEmpty()) {
                        params.put("listaArchivos", this.getFiles());
                        params.put("listaArchivosFinal", new ArrayList<>());
                        params.put("estatus", 2);
                    } else {
                        params.put("estatus", 1);
                    }
                } else {
                    params.put("estatus", 1);
                }
                params.put("agenda", datos);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            } else {
                Faces.messageWarning(null, "Advertencia", "Ingrese el detalle  de la actividad respectivo");
            }
        } catch (Exception e) {
            Logger.getLogger(AgendaActividad.class.getName()).log(Level.SEVERE, null, e);
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

    public AgendaDet getDet() {
        return det;
    }

    public void setDet(AgendaDet det) {
        this.det = det;
    }

    public AgendaModel getDatos() {
        return datos;
    }

    public void setDatos(AgendaModel datos) {
        this.datos = datos;
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

}
