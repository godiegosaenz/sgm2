/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.fusion;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
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
public class ValidarPagoLiqFP extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private UserSession sess;
    @javax.inject.Inject
    protected Entitymanager services;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;
    private HistoricoTramites tramite;
    private Observaciones obs;
    private String mensaje;
    private Boolean mostrarBoton = false;
    private Boolean mostarMensaja = true;
    private String pagado = null;
    private HashMap<String, Object> params;
    private static final long serialVersionUID = 1L;

    @PostConstruct
    private void load() {
        try {
            if (sess != null && sess.getTaskID() != null) {
                this.setTaskId(sess.getTaskID());
                obs = new Observaciones();
                params = new HashMap<>();
                tramite = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(sess.getTaskID(), "tramite").toString())});
                if (tramite.getTipoTramite().getId().intValue() == 7 || tramite.getTipoTramite().getId().intValue() == 8 || tramite.getTipoTramite().getId().intValue() == 9 || tramite.getTipoTramite().getId().intValue() == 36
                        || tramite.getTipoTramite().getId().intValue() == 15 || tramite.getTipoTramite().getId().intValue() == 17 || (tramite.getId().compareTo(new Long("1450")) == 1)) {
                    if (tramite.getNumLiquidacion() != null) {
                        pagado = permisoServices.consultaPagoLiquidacion(tramite.getTipoTramite().getId().intValue(), tramite);
                    }
                    if (pagado == null) {
                        mostarMensaja = false;
                    } else {
                        if ("P".equalsIgnoreCase(pagado)) {
                            mostrarBoton = true;
                            mensaje = "El Usuario ya Cancelo la Tasa Dar clic en el boton 'Tasa Cancelada' para continuar con las siguientes Tareas";
                        } else {
                            mostrarBoton = false;
                            mensaje = "Hasta el Momento el Usuario aun NO CANCELA su Tasa de Liquidación, al momento que el Usuario Cancela su Tasa de Liquidación se mostrara un Boton 'Tasa Cancelada' ";
                        }
                    }
                } else {
                    mensaje = "El Usuario ya Cancelo la Tasa Dar clic en el boton 'Tasa Cancelada'";
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ValidarPagoLiqFP.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }
    //pendiete por verificiar
    public void completarTarea() {
        if (obs.getObservacion() != null) {
            try {
                obs.setEstado(true);
                obs.setFecCre(new Date());
                obs.setIdTramite(tramite);
                obs.setTarea("Consultar el Pago de la liquidación");
                obs.setUserCre(sess.getName_user());
                obs = permisoServices.guardarObservacion(obs);
                if (obs != null) {
                    if (tramite.getId() == 2L) {
                        tramite.setEstado("Finalizado");
                        permisoServices.actualizarHistoricoTramites(tramite);
                    }
                    params.put("idProcess", sess.getTaskID());
                    this.completeTask(this.sess.getTaskID(), params);
                    this.continuar();
                }
            } catch (Exception e) {
                Logger.getLogger(ValidarPagoLiqFP.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JsfUti.messageInfo(null, "Debe Ingresar la Observación de la Tarea.", "");
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getMostrarBoton() {
        return mostrarBoton;
    }

    public void setMostrarBoton(Boolean mostrarBoton) {
        this.mostrarBoton = mostrarBoton;
    }

    public Boolean getMostarMensaja() {
        return mostarMensaja;
    }

    public void setMostarMensaja(Boolean mostarMensaja) {
        this.mostarMensaja = mostarMensaja;
    }

}
