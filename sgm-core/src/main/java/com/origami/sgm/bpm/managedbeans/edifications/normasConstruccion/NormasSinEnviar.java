/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
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
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class NormasSinEnviar extends BpmManageBeanBaseRoot implements Serializable{

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoService;
            
    private MsgFormatoNotificacion ms;
    protected GeTipoTramite tramite;
    protected HistoricoTramites ht;
    protected Observaciones obs;
    
    
    @PostConstruct
    public void InitView(){
        if (session != null && session.getTaskID() != null) {
            ht = new HistoricoTramites();
            tramite = new GeTipoTramite();
            setTaskId(session.getTaskID());

            ht = permisoService.getHistoricoTramiteById((Long) this.getVariable(session.getTaskID(), "tramite"));
            tramite = permisoService.getGeTipoTramiteById(ht.getTipoTramite().getId());

        } else {
            this.continuar();
        }
    }
    
    public void validar(){
        if(ht.getCorreos() == null){
            JsfUti.messageInfo(null, Messages.correosVacios, "");
            return;
        }
        obs = new Observaciones();
        JsfUti.update("frmObsCor");
        JsfUti.executeJS("PF('obs').show()");
    }
    
    public void completar(Boolean finalizar){
        if (obs.getObservacion() == null) {
            JsfUti.messageInfo(null, Messages.observaciones, "");
            return;
        }
        obs.setEstado(Boolean.TRUE);
        obs.setFecCre(new Date());
        obs.setIdTramite(ht);
        obs.setUserCre(session.getName_user());
        obs.setTarea("Reenviar Correo");
        
        if (permisoService.guardarObservacion(obs) != null) {
            try {
                HashMap<String, Object> paramt = new HashMap<>();
                if(finalizar){
                    paramt.put("finalizar", false);
                    ht.setEstado("Pendiente");
                }else{
                    paramt.put("finalizar", true);
                    ht.setEstado("Finalizado");
                }
                this.completeTask(session.getTaskID(), paramt);
                permisoService.actualizarHistoricoTramites(ht);
                this.continuar();
            } catch (Exception e) {
                Logger.getLogger(SolicitudNormasConstruccion.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        JsfUti.executeJS("PF('obs').hide()");
    }
    
    public void finalizarTramiteSinCorreo(){
        
    }

    public MsgFormatoNotificacion getMs() {
        return ms;
    }

    public void setMs(MsgFormatoNotificacion ms) {
        this.ms = ms;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }
    
    public NormasSinEnviar() {
    }
    
}
