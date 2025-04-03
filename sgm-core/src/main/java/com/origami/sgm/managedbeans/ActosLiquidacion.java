/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.entities.RegpActosIngreso;
import com.origami.sgm.lazymodels.RegpActosIngresoLazy;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class ActosLiquidacion implements Serializable{
    
    protected RegpActosIngresoLazy actosLazy;
    
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            actosLazy = new RegpActosIngresoLazy();
        } catch (Exception e) {
            Logger.getLogger(ActosIngresados.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void showDlgNew(){
        JsfUti.redirectFaces("/admin/registro/nuevoActoLiquidacion.xhtml");
    }
    
    public void showDlgEdit(RegpActosIngreso a){
        JsfUti.redirectFaces("/admin/registro/nuevoActoLiquidacion.xhtml?code="+a.getId());
    }

    public RegpActosIngresoLazy getActosLazy() {
        return actosLazy;
    }

    public void setActosLazy(RegpActosIngresoLazy actosLazy) {
        this.actosLazy = actosLazy;
    }
    
}
