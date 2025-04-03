/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.lazymodels.MisTramitesLazy;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class MisTramites extends BpmManageBeanBaseRoot implements Serializable {

    public static final Long serialVersionUID = 1L;

    private MisTramitesLazy tramitesLazy;
    private HistoricoTramites ht;
    private List<HistoricTaskInstance> task;
    @Inject
    private UserSession sess;

    @PostConstruct
    protected void load() {
        if (sess != null) {
            tramitesLazy = new MisTramitesLazy(sess.getName_user());
        }
    }

    public void visualizarDatos(HistoricoTramites h) {
        ht = h;
        if (ht != null &&(ht.getIdProceso()!=null||ht.getIdProcesoTemp()!=null)) {
            if(ht.getIdProcesoTemp()!=null)
                task = this.getTaskByProcessInstanceIdMain(ht.getIdProcesoTemp());
            else
                task = this.getTaskByProcessInstanceIdMain(ht.getIdProceso());
        }else{
            task=null;
        }
        JsfUti.update("formTramite");
        JsfUti.executeJS("PF('datosTramite').show();");
    }
    
    public List<Attachment> getDocumentos() {
        if (ht != null && ht.getIdProceso()!=null) {
            return this.getProcessInstanceAllAttachmentsFiles(ht.getIdProceso());
        }
        return null;
    }

    public MisTramitesLazy getTramitesLazy() {
        return tramitesLazy;
    }

    public void setTramitesLazy(MisTramitesLazy tramitesLazy) {
        this.tramitesLazy = tramitesLazy;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public UserSession getSess() {
        return sess;
    }

    public void setSess(UserSession sess) {
        this.sess = sess;
    }

    public List<HistoricTaskInstance> getTask() {
        return task;
    }

    public void setTask(List<HistoricTaskInstance> task) {
        this.task = task;
    }
    
}
