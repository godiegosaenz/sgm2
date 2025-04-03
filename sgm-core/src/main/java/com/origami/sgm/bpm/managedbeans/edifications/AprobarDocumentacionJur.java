/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications;

import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.NormasConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.edificaciones.PropiedadHorizontalServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class AprobarDocumentacionJur extends BpmManageBeanBaseRoot implements Serializable {

    private static final Long serialVersionUID = 1L;
    @javax.inject.Inject
    protected NormasConstruccionServices normasServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
    @javax.inject.Inject
    private PropiedadHorizontalServices services;
    @Inject
    private ServletSession ss;

    protected HistoricoTramites ht;
    protected Observaciones observaciones;
    protected MsgFormatoNotificacion msg;
    protected String observ;
    protected String tipoArchivos = "/(\\.|\\/)(gif|jpe?g|png|pdf|xlsx|docx|xlsm|dwg|shp|doc|xls|ppt|pptx|tif|txt)$/";

    protected HashMap<String, Object> paramt;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            ht = new HistoricoTramites();
            setTaskId(session.getTaskID());
            ht = permisoService.getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
            msg = normasServices.getMsgFormatoNotificacionByTipo(2L);
        } else {
            this.continuar();
        }
    }

    public void mostrarObservaciones(Boolean aprobar) {
        observaciones = new Observaciones();
        observ = "";
        if (this.getFiles().isEmpty()) {
            JsfUti.messageError(null, "", Messages.faltaSubirDocumento);
            return;
        }
        if (aprobar) {
            JsfUti.update("frmObs");
            JsfUti.executeJS("PF('obs').show();");
        } else {
            JsfUti.update("frmObsCor");
            JsfUti.executeJS("PF('dlgObsCorr').show();");
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
            subject = "EL trámite " + ht.getId() + " fue rechazado";
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

        paramt.put("tramite", ht.getId());
        paramt.put("descripcion", ht.getTipoTramite().getDescripcion());
        paramt.put("prioridad", 50);

        paramt.put("carpeta", ht.getCarpetaRep());
        paramt.put("listaArchivos", this.getFiles());
        paramt.put("listaArchivosFinal", new ArrayList());
        paramt.put("aprobado", aprobado);

//        System.out.println("Definicion Key " + this.getTaskDataByTaskID().getTaskDefinitionKey());
        Observaciones ob = services.guardarObservaciones(ht, session.getName_user(), observ, this.getTaskDataByTaskID().getTaskDefinitionKey());
        if (ob != null) {
            GeDepartamento dC = (GeDepartamento) acl.find(GeDepartamento.class, 26L);
            for (AclRol r : dC.getAclRolCollection()) {
                if (r.getIsDirector()) { // Director Catastro 68
                    for (AclUser aclUser : r.getAclUserCollection()) {
                        if (aclUser.getSisEnabled() && aclUser.getUserIsDirector()) {
                            paramt.put("secretariaGeneral", aclUser.getUsuario());
                        }
                    }
                }
            }
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

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }

    public AprobarDocumentacionJur() {
    }

}
