/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.FnResolucion;
import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author Joao Sanga
 */
@ManagedBean
@ViewScoped
public class FechaInspeccionTView extends BpmManageBeanBaseRoot implements Serializable {
    
    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(FechaInspeccionTView.class.getName());

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    
    private Date fechaInspeccion;
    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenPermisosFuncionamientoLocalComercial permiso;
    
    @PostConstruct
    public void initView(){
        try{
            if (session != null){ //&& session.getTaskID() != null) {
                entradas = new HashMap<>();
                //this.setTaskId(session.getTaskID());
                //ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                if (ht != null) {
                    permiso = ht.getPermisoDeFuncionamientoLC();
                    
                    entradas.put("solicitud", services.getSolicitudExoneracion(ht));
                    entradas.put("obs", new Observaciones());
                    entradas.put("detalleCorreo", "");
                    entradas.put("localComercialList", new ArrayList<RenLocalComercial>().add(null));
                }

            } else {
                this.continuar();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void agregarMensaje(){
        try{
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(fechaInspeccion);
            entradas.put("detalleCorreo", "Tramite #"+ht.getId()+".  \n" +
                            "Fecha de la Inspección Final: "+fecha+". Se le recuerda que una persona debe estar en la propiedad");
        }catch(Exception e){
            LOG.getLevel();
        }
    }
    
    public void validarCampos(){        
        if(fechaInspeccion != null && entradas.get("detalleCorreo") != ""){
            Date fecha = new Date();
            if(fecha.getDate() <= fechaInspeccion.getDate()){ 
                JsfUti.executeJS("PF('dlgObs').show()");
            }else{
                JsfUti.messageError(null, "Error", "La fecha debe ser posterior a la fecha actual.");
                fechaInspeccion = null;
                entradas.put("detalleCorreo", "");
                JsfUti.update("frmMain");
            }
        }
        else{
            JsfUti.messageError(null, "Error", "No se ha agregado información importante.");
        }        
    }
    
    public Date getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(Date fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    public Map<String, Object> getEntradas() {
        return entradas;
    }

    public void setEntradas(Map<String, Object> entradas) {
        this.entradas = entradas;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
    }
    
}
