/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.inspeccionFinal;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.MsgFormatoNotificacion;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.PeInspeccionFinal;
import com.origami.sgm.entities.PePermiso;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@Named
@ViewScoped
public class AsignarFechaDeInspeccion extends BpmManageBeanBaseRoot implements Serializable {
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;

    @javax.inject.Inject
    private Entitymanager services;
    
    private MsgFormatoNotificacion formatoMsg;
    
    private Date fechaInspeccion;
    private HashMap<String, Object> paramsActiviti;
    private Observaciones obs;
    private HistoricoTramites ht, htEncontrado;
    private String detalleCorreo;
    private Boolean validar;
    private String numTramite, codigoCatastral;
    private List<CatPredioPropietario> lisPropietarios;
    private CatPredio datosPredio;
    private PeInspeccionFinal peInspeccionFinalV;
    private List<PePermiso> permisosList;
    private PePermiso pePermiso;
    
    @PostConstruct
    public void init(){
        validar = false;
        if (uSession != null && uSession.getTaskID() != null) {
            this.setTaskId(uSession.getTaskID());
            formatoMsg = (MsgFormatoNotificacion) services.find(Querys.getMsgNotificacionByTipo, new String[]{"tipo"}, new Object[]{Long.parseLong("2")});
            ht = (HistoricoTramites) services.find(Querys.getHistoricoTramiteById, new String[]{"id"}, new Object[]{Long.parseLong(this.getVariable(uSession.getTaskID(), "tramite").toString())});
            numTramite = ht.getIdTramite()+"";
            obs = new Observaciones();
            paramsActiviti = new HashMap<String, Object>();
            
        }
    }
    
    /**
     * Completa la tareaa y guardas todas las variables necesarias del proceso.
     * 
     */
    public void terminarTarea(){
        
        String msg="";
        
        obs.setEstado(Boolean.TRUE);
        obs.setFecCre(new Date());
        obs.setIdTramite(ht);
        obs.setUserCre(uSession.getName_user());
        obs.setTarea(this.getTaskDataByTaskID().getName());
        
        msg = formatoMsg.getHeader() + detalleCorreo + formatoMsg.getFooter();

        paramsActiviti.put("prioridad", 50);
        paramsActiviti.put("carpeta", ht.getTipoTramite().getCarpeta());
        paramsActiviti.put("to", ht.getSolicitante().getEmails());
        paramsActiviti.put("message", msg);
        paramsActiviti.put("urlTec", "/faces/vistaprocesos/edificaciones/inspeccionFinal/cargarDatosAlCatastro.xhtml");
        paramsActiviti.put("subject", "Trámite: "+ht.getId());
        paramsActiviti.put("fechaInspeccion", fechaInspeccion);
        paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());

        if(services.persist(obs)!=null){
            this.completeTask(this.getTaskId(), paramsActiviti);
            this.continuar();
        }
        
    }
    
    /**
     * Al seleccionar una fecha de inspección se genera un mensaje que se envía
     * al usuario solicitante.
     * 
     */
    public void agregarMensaje(){
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(fechaInspeccion);
        detalleCorreo = "Tramite #"+ht.getId()+".  \n" +
                        "Fecha de la Inspección Final: "+fecha+". Se le recuerda que una persona debe estar en la propiedad";
    }
    
    /**
     * Valida los campos necesarios para continuar con el proceso.
     * 
     */
    public void validarCampos(){        
        if(fechaInspeccion != null && detalleCorreo !=null){
            Date fecha = new Date();
            if(fecha.getDate() <= fechaInspeccion.getDate()){ 
                JsfUti.executeJS("PF('dlgObs').show()");
            }else{
                JsfUti.messageError(null, "Error", "La fecha debe ser posterior a la fecha actual.");
                fechaInspeccion = null;
                detalleCorreo = null;
                JsfUti.update("frmMain");
            }
        }
        else{
            JsfUti.messageError(null, "Error", "No se ha agregado información importante.");
        }        
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }

    public Date getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(Date fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    public HashMap<String, Object> getParamsActiviti() {
        return paramsActiviti;
    }

    public void setParamsActiviti(HashMap<String, Object> paramsActiviti) {
        this.paramsActiviti = paramsActiviti;
    }

    public Observaciones getObs() {
        return obs;
    }

    public void setObs(Observaciones obs) {
        this.obs = obs;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public String getDetalleCorreo() {
        return detalleCorreo;
    }

    public void setDetalleCorreo(String detalleCorreo) {
        this.detalleCorreo = detalleCorreo;
    }

    public Boolean getValidar() {
        return validar;
    }

    public void setValidar(Boolean validar) {
        this.validar = validar;
    }
    
}
