/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.permisosFuncionamiento;

import com.origami.session.UserSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.Observaciones;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
public class RealizarInspeccionTView extends BpmManageBeanBaseRoot implements Serializable {
    
    public static Long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(RealizarInspeccionTView.class.getName());

    @javax.inject.Inject
    private RentasServices services;
    @Inject
    private UserSession session;
    @javax.inject.Inject
    private Entitymanager servicesACL;
    
    private Map<String, Object> entradas;
    private HistoricoTramites ht;
    private RenPermisosFuncionamientoLocalComercial permiso;
    private Boolean inspeccionRealizada;
    
    @PostConstruct
    public void initView(){
        try{
            if (session != null){ //&& session.getTaskID() != null) {
                entradas = new HashMap<>();
                //this.setTaskId(session.getTaskID());
                //ht = services.permisoServices().getHistoricoTramiteById(Long.parseLong(this.getVariable(session.getTaskID(), "tramite").toString()));
                //if (ht != null) {
                //    permiso = ht.getPermisoDeFuncionamientoLC();
                    inspeccionRealizada = true;
                    entradas.put("solicitud", services.getSolicitudExoneracion(ht));
                    entradas.put("obs", new Observaciones());
                    entradas.put("localComercialList", new ArrayList<RenLocalComercial>().add(null));
                //}

            } else {
                this.continuar();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void validar(){
        try{
            if(inspeccionRealizada){
                JsfUti.update("frmObs");
                JsfUti.executeJS("PF('obs').show()");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void completarTarea(){
        try{
            HashMap paramsActiviti = new HashMap<String, Object>();
            paramsActiviti.put("prioridad", 50);
            paramsActiviti.put("taskdef", this.getTaskDataByTaskID().getTaskDefinitionKey());
            paramsActiviti.put("idTarea", this.getTaskId());
        }catch(Exception e){
            
        }
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

    public Boolean getInspeccionRealizada() {
        return inspeccionRealizada;
    }

    public void setInspeccionRealizada(Boolean inspeccionRealizada) {
        this.inspeccionRealizada = inspeccionRealizada;
    }
    
}
