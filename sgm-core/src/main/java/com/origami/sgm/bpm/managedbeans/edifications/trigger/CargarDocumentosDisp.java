/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.trigger;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CargarDocumentosDisp extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;
    private Observaciones obs = null;
    private HistoricoTramites ht = null;
    private List<GeRequisitosTramite> requisitos;
    private HashMap<String, Object> params;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    private void load() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                obs = new Observaciones();
                params = new HashMap<>();
                ht = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.valueOf(this.getVariable(sess.getTaskID(), "tramite").toString())});
                Hibernate.initialize(ht.getTipoTramite());
                requisitos = this.getRequisitos(ht.getTipoTramite(), true, null);
            }
        } catch (NumberFormatException | HibernateException e) {
            Logger.getLogger(CargarDocumentosDisp.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void validarDocumentos() {
        try {
            if (!this.getFiles().isEmpty()) {
                params.put("listaArchivos", this.getFiles());
                params.put("listaArchivosFinal", new ArrayList<>());
                if (ht.getTipoTramite().getId().compareTo(2L) == 0) {
                    params.put("reasignar", new Integer(this.getVariable(sess.getTaskID(), "reasignar").toString()));
                } else {
                    params.put("reasignar", 2);
                }
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setUserCre(sess.getName_user());
                obs.setTarea((this.getTaskDataByTaskID() == null? "Cargar Documentos" : this.getTaskDataByTaskID().getName()));
                if (serv.persist(obs) != null) {
                    this.completeTask(this.getTaskId(), params);
                    this.continuar();
                } else {
                    JsfUti.messageError(null, "Error", "No se pudo realizar la tarea");
                }
            } else {
                JsfUti.messageWarning(null, "Advertencia", Messages.documentos);
            }
        } catch (Exception e) {
            Logger.getLogger(CargarDocumentosDisp.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void observacionDefault(){
        if (obs!=null && obs.getObservacion()==null) {
            obs.setObservacion(this.getTaskDataByTaskID().getName());
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
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

    public List<GeRequisitosTramite> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramite> requisitos) {
        this.requisitos = requisitos;
    }

}
