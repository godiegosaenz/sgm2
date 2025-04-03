/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.entities.FnResolucion;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class ResolucionesDLG implements Serializable {
    
    private BaseLazyDataModel<FnResolucion> resoluciones;
    private String msg;
    
    @PostConstruct
    public void initView(){
        resoluciones = new BaseLazyDataModel<>(FnResolucion.class, "fechaIngreso", "desc");
    }
    
    public void select(FnResolucion object) {
        RequestContext.getCurrentInstance().closeDialog(object);
    }

    public BaseLazyDataModel<FnResolucion> getResoluciones() {
        return resoluciones;
    }

    public void setResoluciones(BaseLazyDataModel<FnResolucion> resoluciones) {
        this.resoluciones = resoluciones;
    }
    
    public ResolucionesDLG() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
