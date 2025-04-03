/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.config.SisVars;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
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
 * @author origami
 */
@Named
@ViewScoped
public class InformeSolicitud extends BpmManageBeanBaseRoot implements Serializable {

    private static final Logger LOG = Logger.getLogger(InformeSolicitud.class.getName());

    @javax.inject.Inject
    private SolicitudServicosServices service;

    protected HistoricoTramites ht;
    protected SvSolicitudServicios solicitud = new SvSolicitudServicios();
    protected MsgFormatoNotificacion msg;
    protected String observ;

    @PostConstruct
    public void initView() {
        if (session != null && session.getTaskID() != null) {
            try {
                setTaskId(session.getTaskID());
                ht = service.getPropiedadHorizontalServices().getPermiso().getHistoricoTramiteById(new Long(this.getVariable(session.getTaskID(), "tramite").toString()));
                solicitud = service.getSolicitudServicioByTramite(ht.getIdTramite());
                if (solicitud.getFechaInspeccion() == null) {
                    solicitud.setFechaInspeccion(new Date());
                }

                msg = service.getNormasConstruccion().getMsgFormatoNotificacionByTipo(2L);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, Messages.error, e);
            }

        } else {
            this.continuar();
        }
    }

    public void copiarInformesDirecciones() {
        String informeSolicitud = "";
        for (SvSolicitudDepartamento depSolicitud : solicitud.getSvSolicitudDepartamentoCollection()) {
            if (!depSolicitud.getAccion().equals(3L)) {
                informeSolicitud = informeSolicitud + "Departamento: " + depSolicitud.getDepartamento().getNombre() + "\nInforme: " + depSolicitud.getInforme() + ".";
            }
            informeSolicitud = informeSolicitud.trim();
        }
        solicitud.setNotificacion(informeSolicitud);
    }

    public void observacionDefault(Long l) {
        if (observ == null) {
            if (l.equals(1L)) {
                observ = "TRAMITE: " + ht.getId();
            } else {
                observ = solicitud.getNotificacion();
            }
        }
    }

    public void nofiticarSolicitante() {
        solicitud.setArchivar(Boolean.FALSE);
        if (solicitud.getNotificacion() == null) {
            JsfUti.messageError(null, "", "Informe Final, campo obligatorio.");
            return;
        }
        JsfUti.executeJS("PF('dlgInfSolicitud').show()");
    }

    public void archivarSolicitud() {
        solicitud.setArchivar(Boolean.TRUE);
        JsfUti.executeJS("PF('dlgInfSolicitud').show()");
    }

    public void completarInformeSolicitud() {
        HashMap<String, Object> paramt = new HashMap<>();
        String mensaje;
        if (observ == null) {
            JsfUti.messageError(null, "", Messages.observaciones);
            return;
        }
        try {
            solicitud.setStatus("Finalizado");
            ht.setEstado("Finalizado");
            solicitud = service.actualizarSolicitudServcioyObservaciones(solicitud, ht, session.getName_user(), observ, this.getTaskDataByTaskID().getName());
            if (solicitud != null) {
                if (solicitud.getNotificar()) {
                    mensaje = msg.getHeader() + "<br/>"
                            + "Saludos,<br/><br/>"
                            + solicitud.getNotificacion()
                            + "<br/><br/>Gracias <br/><br/>" + msg.getFooter();
                    if (solicitud.getArchivar()) {
                        mensaje = msg.getHeader() + "<br/>"
                                + "Saludos,<br/><br/>"
                                + "SOLICITUD ARCHIVADA."
                                + "<br/><br/>Gracias <br/><br/>" + msg.getFooter();
                    }

                    paramt.put("from", SisVars.correo);
                    paramt.put("to", getCorreos(ht.getSolicitante().getEnteCorreoCollection()));
                    paramt.put("subject", "Servicio Solicitado " + ht.getId());
                    paramt.put("message", mensaje);

                } else {
                    paramt.put("to", null);
                }
                this.completeTask(session.getTaskID(), paramt);
            } else {
                JsfUti.messageError(null, "", Messages.error);
            }
            this.continuar();
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

}
