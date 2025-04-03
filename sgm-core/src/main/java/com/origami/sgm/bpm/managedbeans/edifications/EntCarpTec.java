/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
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
import util.JsfUti;
import util.Messages;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class EntCarpTec extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager serv;
    private HistoricoTramites tramite;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private AclUser usr;
    private static final Long serialVersionUID = 1L;

    @PostConstruct
    public void initView() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                tramite = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                obs = new Observaciones();
                params = new HashMap<>();
                if (this.getVariable(sess.getTaskID(), "tecnico") != null) {
                    usr = (AclUser) serv.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{this.getVariable(sess.getTaskID(), "tecnico")});
                }
            }
        } catch (Exception e) {
            Logger.getLogger(EntCarpTec.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void completarTarea() {
        try {
            if (this.getFiles().isEmpty() != true) {
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(tramite);
                obs.setUserCre(sess.getName());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                if (serv.persist(obs) != null) {
                    params.put("listaArchivos", this.getFiles());
                    params.put("listaArchivosFinal", new ArrayList<>());
                    params.put("prioridad", 50);
                    this.completeTask(this.getTaskId(), params);
                    this.continuar();
                }
            } else {
                JsfUti.messageFatal(null, "Error", Messages.documentos);
            }
        } catch (Exception e) {
            Logger.getLogger(EntCarpTec.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public AclUser getUsr() {
        return usr;
    }

    public void setUsr(AclUser usr) {
        this.usr = usr;
    }

}
