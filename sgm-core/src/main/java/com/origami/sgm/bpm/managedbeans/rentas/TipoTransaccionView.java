/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.rentas;

import com.origami.session.UserSession;
import com.origami.sgm.entities.RenTipoTransaccion;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.services.interfaces.rentas.RentasServices;
import java.io.Serializable;
import javax.annotation.PostConstruct;
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
public class TipoTransaccionView implements Serializable{
    
    private static final Long serialVersionUID = 1L;
    
    @Inject
    private UserSession uSession;
    
    @javax.inject.Inject
    private Entitymanager services;
    
    @javax.inject.Inject
    protected RentasServices servicesRentas;
    
    private RenTipoTransaccion tipoTransaccion;
    private BaseLazyDataModel<RenTipoTransaccion> tiposLazy;
    
    @PostConstruct
    public void initView() {
        
        try {
            if (uSession != null) {
                tiposLazy = new BaseLazyDataModel<>(RenTipoTransaccion.class);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardarNuevo(){
        try{
            if(services.persist(tipoTransaccion)!=null){
                JsfUti.messageInfo(null, "Info", "Se creó correctamente el tipo de transacción");
            }else{
                JsfUti.messageError(null, "Error", "Hubo un problema al generar el tipo de transacción");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void guardarEdicion(){
        try{
            if(services.update(tipoTransaccion)){
                JsfUti.messageInfo(null, "Info", "Se editó correctamente el tipo de transacción");
            }else{
                JsfUti.messageError(null, "Error", "Hubo un problema al editar el tipo de transacción");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void nuevoTipoTransaccion(){
        tipoTransaccion = new RenTipoTransaccion();
        tipoTransaccion.setEstado(true);
    }

    public BaseLazyDataModel<RenTipoTransaccion> getTiposLazy() {
        return tiposLazy;
    }

    public void setTiposLazy(BaseLazyDataModel<RenTipoTransaccion> tiposLazy) {
        this.tiposLazy = tiposLazy;
    }

    public RenTipoTransaccion getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(RenTipoTransaccion tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public UserSession getuSession() {
        return uSession;
    }

    public void setuSession(UserSession uSession) {
        this.uSession = uSession;
    }
    
}
