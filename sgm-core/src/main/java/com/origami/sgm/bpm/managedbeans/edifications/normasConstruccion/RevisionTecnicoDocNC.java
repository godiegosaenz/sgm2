/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author supergold
 */
@Named
@ViewScoped
public class RevisionTecnicoDocNC extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;
    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;

    @Inject
    private ServletSession ss;

    @Inject
    protected UserSession sessions;
    
    @javax.inject.Inject
    private Entitymanager serv;

    protected HistoricoTramites ht;
    protected Observaciones observaciones;
    protected MsgFormatoNotificacion msg;
    protected AclUser usr;

    protected HashMap<String, Object> paramt;

    @PostConstruct
    public void initView() {
        if (sessions != null || sessions.getTaskID() != null) {
            ht = new HistoricoTramites();
            setTaskId(sessions.getTaskID());
            ht = permisoService.getHistoricoTramiteById(new Long(this.getVariable(sessions.getTaskID(), "tramite").toString()));
            if (ht != null) {
                this.setCorreosAdjuntos(getCorreosByCatEnte(ht.getSolicitante()));
            }
            msg = normasServices.getMsgFormatoNotificacionByTipo(1L);
            usr = permisoService.getAclUserByUser(this.getVariable(sessions.getTaskID(), "digitalizador").toString());
        } else {
            this.continuar();
        }
    }

    public void mostrarObservaciones(Boolean aprobar) {
        observaciones = new Observaciones();
        if (aprobar) {
            JsfUti.update("frmObs");
            JsfUti.executeJS("PF('obs').show();");
        } else {
            JsfUti.update("frmObsCor");
            JsfUti.executeJS("PF('dlgObsCorr').show();");
        }
    }

    public void aprobar(int x) {
        if (observaciones.getObservacion() == null) {
            JsfUti.messageInfo(null, Messages.campoVacio, "");
            return;
        }
        paramt = new HashMap<>();
        observaciones.setEstado(Boolean.TRUE);
        observaciones.setFecCre(new Date());
        observaciones.setUserCre(sessions.getName_user());
        observaciones.setIdTramite(ht);
        observaciones.setTarea(this.getTaskDataByTaskID().getName());

        paramt.put("from", SisVars.correo);
        ht.setCorreccion(0L);
        switch (x) {
            case 0:
                if (usr != null && usr.getEnte() != null) {
                    if (!usr.getEnte().getEnteCorreoCollection().isEmpty()) {
                        paramt.put("to", this.getCorreosByCatEnte(usr.getEnte()));
                    }
                    paramt.put("subject", "Cambiar el tipo de tramite ");
                    paramt.put("message", msg.getHeader() + "<br/>" + observaciones.getObservacion() + "<br/>" + msg.getFooter());
                }
                paramt.put("actualizarTramite", false);
                break;
            case 1:
                paramt.put("to", this.getCorreosAdjuntos());
                paramt.put("actualizarTramite", true);
                paramt.put("tramite", ht.getId());
                paramt.put("carpeta", ht.getTipoTramite().getCarpeta());
                paramt.put("listaArchivos", this.getVariable(this.getTaskId(), "listaArchivosFinal"));
                paramt.put("listaArchivosFinal", new ArrayList());
                paramt.put("director", this.getVariable(this.getTaskId(), "asignador"));
                paramt.put("tecnico", this.getTaskDataByTaskID().getAssignee());
                paramt.put("idproceso", ht.getTipoTramite().getActivitykey());
                break;
            case 2:
                paramt.put("actualizarTramite", false);
                paramt.put("to", this.getCorreosAdjuntos());
                paramt.put("subject", "Documentos faltantes Tramite No. " + ht.getId());
                paramt.put("message", msg.getHeader() + observaciones.getObservacion() + msg.getFooter());
                ht.setCorreccion(1L);
                break;
        }
        paramt.put("aprobado", x);
        paramt.put("prioridad", 50);
        serv.persist(ht);
        if (permisoService.guardarObservacion(observaciones) != null) {
            this.completeTask(this.getTaskId(), paramt);
            this.continuar();
        } else {
            JsfUti.messageWarning(null, "Advertencia", "La acción no se pudo realizar");
        }

    }

    public ServletSession getSs() {
        return ss;
    }

    public void setSs(ServletSession ss) {
        this.ss = ss;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Observaciones getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Observaciones observaciones) {
        this.observaciones = observaciones;
    }

    public UserSession getSessions() {
        return sessions;
    }

    public void setSessions(UserSession sessions) {
        this.sessions = sessions;
    }

    public RevisionTecnicoDocNC() {
    }

}
