/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.permisoconstruccion;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.services.interfaces.edificaciones.DivisionPredioServices;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class consultarPagoLiquidacion extends BpmManageBeanBaseRoot implements Serializable {

    protected HistoricoTramites ht;
    protected Observaciones obs;
    protected GeTipoTramite tramite;
    protected String mensaje;
    protected Boolean mostrarBoton = false;
    protected Boolean mostarMensaja = true;
    protected HashMap<String, Object> paramt;

    @Inject
    private UserSession sess;

    @javax.inject.Inject
    protected DivisionPredioServices divisonServices;
    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    public consultarPagoLiquidacion() {
    }

    @PostConstruct
    public void initView() {
        if (session.getTaskID() != null) {
            this.setTaskId(session.getTaskID());
            ht = permisoServices.getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
            if (ht.getId().compareTo(1209L) == 1) {
                mostarMensaja = true;
            }
            String pagado = null;
            tramite = permisoServices.getGeTipoTramiteById(ht.getTipoTramite().getId());
            if (tramite.getId().intValue() == 7 || tramite.getId().intValue() == 8 || tramite.getId().intValue() == 9
                    || tramite.getId().intValue() == 4 || tramite.getId().intValue() == 6 // Línea agregada por Joao Sanga, para adicionar la tasa de la inspección y la tasa de permisos adicionales 
                    || tramite.getId().intValue() == 15 || tramite.getId().intValue() == 17
                    || tramite.getId().intValue() == 2) {
                if (ht.getNumLiquidacion() != null) {
                    pagado = permisoServices.consultaPagoLiquidacion(tramite.getId().intValue(), ht);
                }
                if (pagado == null) {
                    mostarMensaja = false;
                } else {
                    if ("P".equalsIgnoreCase(pagado)) {
                        mostrarBoton = true;
                        mensaje = "El Usuario ya Cancelo la Tasa Dar clic en el botón 'Tasa Cancelada' para continuar con las siguientes Tareas";
                    } else {
                        mostrarBoton = false;
                        mensaje = "Hasta el Momento el Usuario aún NO CANCELA su Tasa de Liquidación, al momento que el Usuario Cancela su Tasa de Liquidación se mostrará un Botón 'Tasa Cancelada";
                    }
                }
            } else {
                mensaje = "El Usuario ya Canceló la Tasa Dar clic en el botón 'Tasa Cancelada'";
            }
        } else {
            this.continuar();
        }
    }

    public void mostrarObservaciones() {
        obs = new Observaciones();
        paramt = new HashMap<>();
        JsfUti.executeJS("PF('obs').show()");
    }

    public void completarTarea() {
        if (obs.getObservacion() != null) {
            try {
                obs.setEstado(true);
                obs.setFecCre(new Date());
                obs.setIdTramite(ht);
                obs.setTarea("Consultar el Pago de la liquidación");
                obs.setUserCre(sess.getName_user());
                obs = permisoServices.guardarObservacion(obs);
                if (obs != null) {
                    if (tramite.getId() == 2L) {
                        ht.setEstado("Finalizado");
                        permisoServices.actualizarHistoricoTramites(ht);
                    }
                    paramt.put("idProcess", sess.getTaskID());
                    this.completeTask(this.sess.getTaskID(), paramt);
                    this.continuar();
                }
            } catch (Exception e) {
                Logger.getLogger(consultarPagoLiquidacion.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            JsfUti.messageInfo(null, "Debe Ingresar la Observación de la Tarea.", "");
        }

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
