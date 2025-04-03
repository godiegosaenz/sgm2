/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.bpm.models.DepartamentoSolicitudServicio;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.SvSolicitudDepartamento;
import com.origami.sgm.entities.SvSolicitudServicios;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author supergold
 */
@Named
@ViewScoped
public class ValidarInforme extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(ValidarInforme.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;

    protected HistoricoTramites ht;
    protected MsgFormatoNotificacion msg;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();
    // para el dialog de observaciones.
    protected String placeholderObs;
    protected Integer opcion;

    protected String observ;
    protected Boolean audiencia;
    
    protected DepartamentoSolicitudServicio dss;
    protected SvSolicitudDepartamento detalleDepartamento;
    protected SvSolicitudDepartamento depDireccion;
    protected SvSolicitudDepartamento depSubDireccion;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            ht = new HistoricoTramites();
            setTaskId(session.getTaskID());
            ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
            msg = service.getNormasConstruccion().getMsgFormatoNotificacionByTipo(2L);
            solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
            
            if (solicitud != null) {
                audiencia = solicitud.getTipoServicio().getId().compareTo(783L) == 0;
                dss = (DepartamentoSolicitudServicio)this.getVariable(session.getTaskID(), "departamento");
                detalleDepartamento = service.getSvSolicitudDepartamentoById(dss.getIdDetalleSolicitud());
            }
        } else {
            this.continuar();
        }
    }

    public void mostrarObs(int accion) {
        placeholderObs = "Las observaciones son obligatorias para continuar con el tramite";

        opcion = accion;
        JsfUti.executeJS("PF('obs').show()");
        JsfUti.update("frmObsCor");
    }

    public void completarTarea() {
        HashMap<String, Object> paramt = new HashMap<>();
        if (observ.trim().isEmpty()) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }

        try {
            switch (opcion) {
                case 1:
                    paramt.put("carpeta", ht.getCarpetaRep());
                    paramt.put("tramite", ht.getId());
                    if (detalleDepartamento.getPadre()==null) {// CUANDO ES UNA DIRECCION
                        dss.setAccionValidacion(1L);
                        depSubDireccion=service.getSvSolicitudDepartamentoByIdPadre(detalleDepartamento.getId());
                        if(depSubDireccion!=null){
                            depSubDireccion.setFechaFinalizado(new Date());
                        }
                    }else{//CUANDO ES SUBDIRECCION
                        depDireccion= service.getSvSolicitudDepartamentoById(detalleDepartamento.getPadre());
                        if (depDireccion!=null) {
                            dss.setAccionValidacion(3L);
                            dss.setIdDetalleSolicitud(depDireccion.getId());
                            dss.setDirector(depDireccion.getDirector().getUsuario());
                            dss.setCorreoDirector(depDireccion.getDirector().getEnte().getEnteCorreoCollection().get(0).getEmail());
                            dss.setResponsable(depDireccion.getResponsable().getUsuario());
                            dss.setCorreoResponsable(depDireccion.getResponsable().getEnte().getEnteCorreoCollection().get(0).getEmail());
                            dss.setValidar(depDireccion.getValidar());
                            //SI TIENE UN PADRE ES DEBIDO QUE EL RESPONSABLE ES UN SUBDIRECTOR
                            // POR LO TANTO 
                            dss.setAccion(4L);
                            // ACUALIZAR dss con los valores del padre
                            
                            //ACTUALIZAR EL INFORME DE LA DIRECCION CON LO APROBADO POR LA SUBDIRECCION
                            if (depDireccion.getInforme()==null) {
                                depDireccion.setInforme(detalleDepartamento.getInforme());
                            }else{
                                depDireccion.setInforme(depDireccion.getInforme()+" "+detalleDepartamento.getInforme());
                            }
                        }
                    }
                    break;
                case 2:// CUANDO ES RECHAZADO EL INFORME
                    detalleDepartamento.setComunicado(detalleDepartamento.getComunicado()+". "+observ);
                    dss.setAccionValidacion(2L);
                    if (detalleDepartamento.getPadre()==null) {// CUANDO ES UNA DIRECCION
                        SvSolicitudDepartamento depSubDireccion=service.getSvSolicitudDepartamentoByIdPadre(detalleDepartamento.getId());
                        dss.setAccion(2L);
                        if (depSubDireccion!=null) {// CUANDO FUE ASIGNADO A UNA SUBDIRECCION
                            dss.setAccion(4L);
                            dss.setIdDetalleSolicitud(depSubDireccion.getId());
                            dss.setDirector(depSubDireccion.getDirector().getUsuario());
                            dss.setCorreoDirector(depSubDireccion.getDirector().getEnte().getEnteCorreoCollection().get(0).getEmail());
                            dss.setResponsable(depSubDireccion.getResponsable().getUsuario());
                            dss.setCorreoResponsable(depSubDireccion.getResponsable().getEnte().getEnteCorreoCollection().get(0).getEmail());
                            dss.setValidar(depSubDireccion.getValidar());
                        }
                    }else{//CUANDO ES SUBDIRECCION
                        
                    }
                    break;
            }
            paramt.put("prioridad", 50);
            paramt.put("departamento", dss);
            for (SvSolicitudDepartamento ge : solicitud.getSvSolicitudDepartamentoCollection()) {
                if (ge.getId().equals(detalleDepartamento.getId())) {
                    ge.setComunicado(detalleDepartamento.getComunicado());
                    ge.setInforme(detalleDepartamento.getInforme());
                    ge.setAccion(detalleDepartamento.getAccion());
                    ge.setResponsable(detalleDepartamento.getResponsable());
                }
                if (depDireccion!=null &&ge.getId().equals(depDireccion.getId())) {
                    ge.setComunicado(depDireccion.getComunicado());
                    ge.setInforme(depDireccion.getInforme());
                    ge.setAccion(depDireccion.getAccion());
                    ge.setResponsable(depDireccion.getResponsable());
                }
                if (depSubDireccion!=null &&ge.getId().equals(depSubDireccion.getId())) {
                    ge.setComunicado(depSubDireccion.getComunicado());
                    ge.setInforme(depSubDireccion.getInforme());
                    ge.setAccion(depSubDireccion.getAccion());
                    ge.setResponsable(depSubDireccion.getResponsable());
                    ge.setFechaFinalizado(depSubDireccion.getFechaFinalizado());
                }
            }
            if (service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getDescription()) != null) {
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

    public String getObserv() {
        return observ;
    }

    public void setObserv(String observ) {
        this.observ = observ;
    }

    public Boolean getAudiencia() {
        return audiencia;
    }

    public void setAudiencia(Boolean audiencia) {
        this.audiencia = audiencia;
    }

    public ValidarInforme() {
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

    public Integer getOpcion() {
        return opcion;
    }

    public void setOpcion(Integer opcion) {
        this.opcion = opcion;
    }

}
