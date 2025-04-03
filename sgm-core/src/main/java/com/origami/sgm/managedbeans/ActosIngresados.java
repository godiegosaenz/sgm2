/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.entities.RegActo;
import com.origami.sgm.lazymodels.RegActosLazy;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class ActosIngresados implements Serializable {
    public static final long serialVersionUID = 1L;

    protected RegActosLazy listActosLazy;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            listActosLazy = new RegActosLazy();
        } catch (Exception e) {
            Logger.getLogger(ActosIngresados.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void showDlgNew(){
        JsfUti.redirectFaces("/admin/registro/nuevoActo.xhtml");
    }
    
    public void showDlgEdit(RegActo a){
        JsfUti.redirectFaces("/admin/registro/nuevoActo.xhtml?code="+a.getId());
    }

    public RegActosLazy getListActosLazy() {
        return listActosLazy;
    }

    public void setListActosLazy(RegActosLazy listActosLazy) {
        this.listActosLazy = listActosLazy;
    }
}
