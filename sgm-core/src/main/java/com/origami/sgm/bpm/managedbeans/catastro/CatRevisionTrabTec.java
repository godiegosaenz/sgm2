/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
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
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class CatRevisionTrabTec extends BpmManageBeanBaseRoot implements Serializable {

    private static final long serialVersionUID = 1L;
    private HistoricoTramites tramite;
    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private Entitymanager manager;
    private Observaciones obs;
    private HashMap<String, Object> params;

    @PostConstruct
    protected void load() {
        if (sess != null && sess.getTaskID() != null) {
            obs = new Observaciones();
            this.setTaskId(sess.getTaskID());
            params = new HashMap<>();
            tramite = (HistoricoTramites) manager.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
        }
    }

    public void completar(int x) {
        try {
            obs.setFecCre(new Date());
            obs.setUserCre(sess.getName_user());
            obs.setIdTramite(tramite);
            obs.setTarea(this.getTaskDataByTaskID().getName());
            if (obs != null && obs.getObservacion() != null) {
                switch (x) {
                    case 1:
                        params.put("aprobado", true);
                        break;
                    case 2:
                        params.put("descripcion", obs.getObservacion());
                        break;
                    case 3:
                        params.put("aprobado", false);
                        break;
                }
                params.put("estatus", x);
                if (this.getFiles() != null && !this.getFiles().isEmpty()) {
                    params.put("tdocs", true);
                    params.put("listaArchivos", this.getFiles());
                    params.put("listaArchivosFinal", new ArrayList<>());
                } else {
                    params.put("tdocs", false);
                }
                manager.persist(obs);
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            }
        } catch (Exception e) {
            Logger.getLogger(CatRevisionTrabTec.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
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

}
