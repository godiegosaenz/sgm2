/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.config.SisVars;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.TipoOtrosTramites;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
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
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class RevisionTecnicaDocs extends BpmManageBeanBaseRoot implements Serializable {
    
    @Inject
    private UserSession sess;
    
    @javax.inject.Inject
    private Entitymanager serv;
    
    @javax.inject.Inject
    protected DivisionPredioServices servicesDP;
    
    private HistoricoTramites tramite;
    private Observaciones obs;
    private HashMap<String, Object> params;
    private boolean habOpt = true;
    private String mailUsr;
    private CatPredio predio;
    private List<CatPredio> predfus;
    private MsgFormatoNotificacion msg;
    private AclUser usr;
    private TipoOtrosTramites tipoTramite;
    private static final Long serialVersionUID = 1L;
    
    @PostConstruct
    public void initView() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                obs = new Observaciones();
                params = new HashMap<>();
                msg = (MsgFormatoNotificacion) serv.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{2L});
                usr = (AclUser) serv.find(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{this.getVariable(sess.getTaskID(), "digitalizador")});
                tramite = (HistoricoTramites) serv.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                if (tramite != null) {
                    this.setCorreosAdjuntos(getCorreosByCatEnte(tramite.getSolicitante()));
                }
                
                if (this.getVariable(sess.getTaskID(), "tipoOtrosTramites") != null) {
                    tipoTramite = (TipoOtrosTramites) serv.find(Querys.getTipoOtrosTramitesByIdentificacion, new String[]{"ident"}, new Object[]{this.getVariable(sess.getTaskID(), "tipoOtrosTramites")});
                }
                //tramite = servicesDP.obtenerHistoricoTramitePorID(Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString()));
                if (tramite.getNumPredio() != null) {
                    predio = (CatPredio) serv.find(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{tramite.getNumPredio()});
                    //predio = servicesDP.obtenerCatPredioPorQuery(Querys.getPredioByNum, new String[]{"numPredio"}, new Object[]{tramite.getNumPredio()});

                    //usr = servicesDP.obtenerAclUserPorQuery(Querys.getAclUserByUser, new String[]{"user"}, new Object[]{this.getVariable(sess.getTaskID(), "digitalizador")});
                }
            }
        } catch (Exception e) {
            Logger.getLogger(EntCarpTec.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void aprobar(int x) {
        try {
            obs.setEstado(Boolean.TRUE);
            obs.setFecCre(new Date());
            obs.setIdTramite(tramite);
            obs.setUserCre(sess.getName_user());
            params.put("from", SisVars.correo);
            obs.setTarea(this.getTaskDataByTaskID().getName());
            tramite.setCorreccion(0L);
            switch (x) {
                case 0:
                    if (usr != null && usr.getEnte() != null) {
                        params.put("to", this.getCorreosByCatEnte(usr.getEnte()));
                        params.put("subject", "Cambiar el tipo de tramite ");
                        params.put("message", msg.getHeader() + "<br/>" + obs.getObservacion() + "<br/>" + msg.getFooter());
                        obs.setObservacion(obs.getObservacion() + " - Se selecciona un nuevo tipo de trámite");
                    }
                    params.put("actualizarTramite", false);
                    break;
                case 1:
                    params.put("to", this.getCorreosAdjuntos());
                    params.put("actualizarTramite", true);
                    params.put("tramite", tramite.getId());
                    params.put("carpeta", tramite.getTipoTramite().getCarpeta());
                    params.put("listaArchivos", this.getVariable(this.getTaskId(), "listaArchivosFinal"));
                    params.put("listaArchivosFinal", new ArrayList());
                    params.put("director", this.getVariable(this.getTaskId(), "asignador"));
                    params.put("tecnico", this.getTaskDataByTaskID().getAssignee());
                    obs.setObservacion(obs.getObservacion() + " - Procede normalmente al flujo de " + tramite.getTipoTramite().getDescripcion());
                    if (tipoTramite != null) {
                        params.put("idproceso", tipoTramite.getActKeyValue());
                    } else {
                        params.put("idproceso", tramite.getTipoTramite().getActivitykey());
                    }
                    break;
                case 2:
                    params.put("actualizarTramite", false);
                    params.put("to", this.getCorreosAdjuntos());
                    params.put("subject", "Documentos faltantes Tramite No. " + tramite.getId());
                    params.put("message", msg.getHeader() + obs.getObservacion() + msg.getFooter());
                    obs.setObservacion(obs.getObservacion() + " - Se vuelve a revisar los documentos.");
                    tramite.setCorreccion(1L);
                    break;
            }
            params.put("aprobado", x);
            params.put("prioridad", 50);
            serv.persist(tramite);
            if (servicesDP.guardarObservacion(obs) != null) {
                this.completeTask(this.getTaskId(), params);
                this.continuar();
            } else {
                JsfUti.messageWarning(null, "Advertencia", "La accion no se pudo realizar");
            }
        } catch (Exception e) {
            Logger.getLogger(EntCarpTec.class.getName()).log(Level.SEVERE, "Tramite # " + tramite.getId() ,e);
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
    
    public boolean getHabOpt() {
        return habOpt;
    }
    
    public void setHabOpt(boolean habOpt) {
        this.habOpt = habOpt;
    }
    
    public CatPredio getPredio() {
        return predio;
    }
    
    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }
    
    public HashMap<String, Object> getParams() {
        return params;
    }
    
    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }
    
    public String getMailUsr() {
        return mailUsr;
    }
    
    public void setMailUsr(String mailUsr) {
        this.mailUsr = mailUsr;
    }
    
    public MsgFormatoNotificacion getMsg() {
        return msg;
    }
    
    public void setMsg(MsgFormatoNotificacion msg) {
        this.msg = msg;
    }
    
    public AclUser getUsr() {
        return usr;
    }
    
    public void setUsr(AclUser usr) {
        this.usr = usr;
    }
    
}
