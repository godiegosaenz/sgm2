/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.DepartamentoSolicitudServicio;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.Archivo;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Angel
 * Navarro
 */
@Named
@ViewScoped
public class RevisionUserSolicitud extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(RevisionUserSolicitud.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;

    protected HistoricoTramites ht;
    protected MsgFormatoNotificacion msg;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();

    protected String tipoArchivos = SisVars.formatoArchivos;
    protected String placeholderObs;
    protected Boolean audiencia;
    protected Boolean esSubDirector;

    protected DepartamentoSolicitudServicio dss;
    protected SvSolicitudDepartamento detalleDepartamento;

    protected List<CatPredio> catPredioList;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            try {
                ht = new HistoricoTramites();
                setTaskId(session.getTaskID());
                ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
                msg = service.getNormasConstruccion().getMsgFormatoNotificacionByTipo(2L);
                solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
                this.setBpmTramite(ht);
                if (solicitud != null) {
                    audiencia = solicitud.getTipoServicio().getId().compareTo(783L) == 0;
                    dss = (DepartamentoSolicitudServicio) this.getVariable(session.getTaskID(), "departamento");
                    detalleDepartamento = service.getSvSolicitudDepartamentoById(dss.getIdDetalleSolicitud());
                    dss.setValidar(detalleDepartamento.getValidar());
                    catPredioList = this.initListPredios(ht);
                }

                esSubDirector = service.getRol(session.getRoles().get(0)).getEsSubDirector();
            } catch (Exception e) {
                System.out.println("ERROR RevisionUserSolicitud" + ht);
                Logger.getLogger(RevisionUserSolicitud.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            this.continuar();
        }
    }

    public void mostrarObs(Integer accion) {
        placeholderObs = "Las observaciones son obligatorias para continuar con el tramite";
        if (accion != 50) {
            if (detalleDepartamento.getArchivo()) {
                if (this.getFiles().isEmpty()) {
                    JsfUti.messageError(null, "", "Debe subir un archivo.");
                    return;
                } else {
                    if (detalleDepartamento.getInforme() == null) {
                        detalleDepartamento.setInforme("El detalle de la solicitud se encuentra en el docuemnto adjunto");
                    }
                }
            } else {
                if (detalleDepartamento.getInforme() == null) {
                    JsfUti.messageError(null, "Informe.", "Ingresar detalle de la resolucion.");
                    return;
                }
            }
        }

        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");
    }

    public void completarTarea() {
        HashMap<String, Object> paramt = new HashMap<>();
        try {
            if (detalleDepartamento.getInforme() == null) {
                JsfUti.messageError(null, "", "Debe Seleccionar un usuario a asignar.");
                return;
            }
            paramt.put("carpeta", ht.getCarpetaRep());
            paramt.put("listaArchivos", this.getFiles());
            paramt.put("listaArchivosFinal", new ArrayList<Archivo>());
            paramt.put("tramite", ht.getId());
            paramt.put("prioridad", 50);
            paramt.put("departamento", dss);
            for (SvSolicitudDepartamento ge : solicitud.getSvSolicitudDepartamentoCollection()) {
                if (ge.getId().equals(detalleDepartamento.getId())) {
                    ge.setComunicado(detalleDepartamento.getComunicado());
                    ge.setInforme(detalleDepartamento.getInforme());
                    ge.setAccion(detalleDepartamento.getAccion());
                    ge.setResponsable(detalleDepartamento.getResponsable());
                    break;
                }
            }
            if (service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), detalleDepartamento.getInforme(), this.getTaskDataByTaskID().getDescription()) != null) {
                this.completeTask(session.getTaskID(), paramt);
                this.continuar();
            } else {
                JsfUti.messageError(null, "", Messages.error);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Messages.errorTransaccion, e);
        }

    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public SvSolicitudServicios getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SvSolicitudServicios solicitud) {
        this.solicitud = solicitud;
    }

    public Boolean getAudiencia() {
        return audiencia;
    }

    public void setAudiencia(Boolean audiencia) {
        this.audiencia = audiencia;
    }

    public RevisionUserSolicitud() {
    }

    public String getTipoArchivos() {
        return tipoArchivos;
    }

    public void setTipoArchivos(String tipoArchivos) {
        this.tipoArchivos = tipoArchivos;
    }

    public Boolean getEsSubDirector() {
        return esSubDirector;
    }

    public void setEsSubDirector(Boolean esSubDirector) {
        this.esSubDirector = esSubDirector;
    }

    public String getPlaceholderObs() {
        return placeholderObs;
    }

    public void setPlaceholderObs(String placeholderObs) {
        this.placeholderObs = placeholderObs;
    }

    public SvSolicitudDepartamento getDetalleDepartamento() {
        return detalleDepartamento;
    }

    public void setDetalleDepartamento(SvSolicitudDepartamento detalleDepartamento) {
        this.detalleDepartamento = detalleDepartamento;
    }

    public List<CatPredio> getCatPredioList() {
        return catPredioList;
    }

    public void setCatPredioList(List<CatPredio> catPredioList) {
        this.catPredioList = catPredioList;
    }

}
