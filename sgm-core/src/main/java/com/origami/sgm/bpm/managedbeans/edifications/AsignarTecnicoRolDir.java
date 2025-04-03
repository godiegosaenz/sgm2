/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.ResTareasUsuarios;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
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

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class AsignarTecnicoRolDir extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    private PermisoConstruccionServices permisoServices;
    private HistoricoTramites tramite;
    private ResTareasUsuarios resumenTecnico;
    private List<ResTareasUsuarios> resTecnicos;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private MsgFormatoNotificacion msg;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    public void initView() {
        try {
            if (sess.esLogueado() && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                this.llenarTecnicos();

                tramite = (HistoricoTramites) acl.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                obs = new Observaciones();
                params = new HashMap<>();
                msg = (MsgFormatoNotificacion) acl.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{1L});
            }
        } catch (Exception e) {
            Logger.getLogger(AsignarTecnicoRolDir.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void completarTarea() {
        try {
            if (this.getResumenTecnico() != null) {
                obs.setEstado(Boolean.TRUE);
                obs.setFecCre(new Date());
                obs.setIdTramite(tramite);
                obs.setUserCre(sess.getName_user());
                obs.setTarea(this.getTaskDataByTaskID().getName());
                if (acl.persist(obs) != null) {
                    params.put("tecnico", this.getResumenTecnico().getUser());
                    params.put("revisor", this.getResumenTecnico().getUser());
                    params.put("to", this.getResumenTecnico().getMailUser());
                    params.put("from", SisVars.correo);
                    params.put("subject", "Revision tecnica de documentos Tramite: " + tramite.getId());
                    params.put("message", msg.getHeader() + " Revision tecnica de documentos Tramite: " + tramite.getId() + "<br/> " + obs.getObservacion() + "<br/> " + msg.getFooter());
                    this.completeTask(this.getTaskId(), params);
                    this.continuar();
                }
            }
        } catch (Exception e) {
            Logger.getLogger(AsignarTecnicoRolDir.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void llenarTecnicos() {
        try {
            resTecnicos = new ArrayList<>();
            List<Long> roles = new ArrayList();
            
            for(Long t : sess.getDepts()){
                roles.addAll((List<Long>)acl.findAll(Querys.getIdAclRolByDept, new String[]{"dept"}, new Object[]{t}));
            }
            List<AclUser> tecnicos = acl.getTecnicosByRol(roles);
            
            for (AclUser t : tecnicos) {
                resTecnicos.add(this.getResumenTareasUsuariosNew(t.getUsuario(), t.getEnte() == null ? null : t.getEnte().getEmails()));
            }
        } catch (Exception e) {
            Logger.getLogger(AsignarTecnicoRolDir.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    public void observacionDefault() {
        if (obs != null && obs.getObservacion() == null) {
            obs.setObservacion(this.getTaskDataByTaskID().getName());
        }
    }

    public void seleccionarResumenTecnico(ResTareasUsuarios rt) {
        resumenTecnico = rt;
    }

    public ResTareasUsuarios getResumenTecnico() {
        return resumenTecnico;
    }

    public void setResumenTecnico(ResTareasUsuarios resumenTecnico) {
        this.resumenTecnico = resumenTecnico;
    }

    public List<ResTareasUsuarios> getResTecnicos() {
        return resTecnicos;
    }

    public void setResTecnicos(List<ResTareasUsuarios> resTecnicos) {
        this.resTecnicos = resTecnicos;
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

}
