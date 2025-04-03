/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.propiedadHorizontal;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
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
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class RevisionTecnicoDocPH extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;
    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    @javax.inject.Inject
    private PropiedadHorizontalServices services;
    @Inject
    private ServletSession ss;
    @javax.inject.Inject
    private Entitymanager serv;

    protected HistoricoTramites ht;
    protected Observaciones observaciones;
    protected MsgFormatoNotificacion msg;
    protected AclUser usr;
    protected String observ;

    protected HashMap<String, Object> paramt;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            ht = new HistoricoTramites();
            setTaskId(session.getTaskID());
            ht = permisoService.getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
            if (ht != null) {
                if (ht.getSolicitante() != null) {
                    this.setCorreosAdjuntos(this.getCorreosByCatEnte(ht.getSolicitante()));
                }
            }
            msg = normasServices.getMsgFormatoNotificacionByTipo(2L);
            usr = permisoService.getAclUserByUser(this.getVariable(session.getTaskID(), "digitalizador").toString());
        } else {
            this.continuar();
        }
    }

    public void mostrarObservaciones(Boolean aprobar) {
        observaciones = new Observaciones();
        observ = "";
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
        observaciones.setUserCre(session.getName_user());
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
                paramt.put("subject", "Correcciones de Trámite #. " + ht.getId());
                paramt.put("message", msg.getHeader() + "<br/>" + observaciones.getObservacion() + "<br/>" + msg.getFooter());
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

    public void complatarJuridico(Boolean aprobado) {
        if (observ == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        paramt = new HashMap<>();
        String subject;
        String mensaje;
        if (!aprobado) {
            String director = ht.getTipoTramite().getUserDireccion();
            AclUser userDirector = services.getPermiso().getAclUserByUser(director);
            subject = "EL trámite " + ht.getId() + " Rechazado";
            mensaje = msg.getHeader()
                    + " <br/><br/><h2>Su trámite fue rechazado por el Consejo. </h2><br/><br/>"
                    + observ
                    + "<br/><br/>"
                    + "att.<br/>"
                    + "Arq. " + userDirector.getFirma().getNomCompleto() + "<br/><br/>"
                    + msg.getFooter();
            paramt.put("to", ht.getCorreos());
            paramt.put("subject", subject);
            paramt.put("message", mensaje);
            ht.setEstado("Finalizado");
            services.actualizarHistoricoTramites(ht);
        }

        paramt.put("carpeta", ht.getCarpetaRep());
        paramt.put("listaArchivos", this.getFiles());
        paramt.put("listaArchivosFinal", new ArrayList());
        paramt.put("aprobado", aprobado);

//        System.out.println("Definicion Key " + this.getTaskDataByTaskID().getTaskDefinitionKey());
        Observaciones ob = services.guardarObservaciones(ht, session.getName_user(), observ, this.getTaskDataByTaskID().getTaskDefinitionKey());
        if (ob != null) {
            this.completeTask(this.getTaskId(), paramt);
            JsfUti.update("formRevTec");
            JsfUti.executeJS("PF('obs').show();");

        }
        this.continuar();
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

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public RevisionTecnicoDocPH() {
    }

}
