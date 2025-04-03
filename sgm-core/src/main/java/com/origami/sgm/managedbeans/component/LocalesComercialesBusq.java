/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalCategoria;
import com.origami.sgm.entities.RenLocalComercial;
import com.origami.sgm.entities.RenLocalUbicacion;
import com.origami.sgm.lazymodels.RenLocalComercialLazy;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.EntityBeanCopy;
import util.JsfUti;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 06/05/2016
 */
@Named
@ViewScoped
public class LocalesComercialesBusq implements Serializable{

    @javax.inject.Inject
    private com.origami.sgm.services.ejbs.ServiceLists list;
    
    private RenLocalComercialLazy localesLazy;

    @PostConstruct
    public void initView() {
        localesLazy = new RenLocalComercialLazy();
    }

    public void selectLocal(RenLocalComercial lc) {
        RequestContext.getCurrentInstance().closeDialog(lc);
    }
    
    public List<RenActividadComercial> getActividadesLocal(RenLocalComercial lc) {
        List l = null;
        if (lc != null) {
            l = (List) EntityBeanCopy.clone(lc.getRenActividadComercialCollection());
        } else {
            l = null;
        }
        return l;
    }
    
    public void nuevoLocal(){
        JsfUti.redirectFacesNewTab("/faces/rentas/mantenimiento/localcomercial.xhtml");
    }
    
    public RenLocalComercialLazy getLocalesLazy() {
        return localesLazy;
    }

    public void setLocalesLazy(RenLocalComercialLazy localesLazy) {
        this.localesLazy = localesLazy;
    }

    public List<RenActividadComercial> getActividadesLocal(){
        return list.getActividadComercials();
    }
    
    public List<RenLocalUbicacion> getUbicacionesLocal(){
        return list.getLocalUbicacions();
    }
    
    public List<RenLocalCategoria> getCategorias(){
        return list.getLocalCategorias();
    }
    
    public RenActividadComercial getActividad(RenLocalComercial lc){
        return Utils.get(lc.getRenActividadComercialCollection(), 0);
    }
    public LocalesComercialesBusq() {
    }

}
