/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegCatPapel;
import com.origami.sgm.lazymodels.RegPapelLazy;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class Papeles extends BpmManageBeanBaseRoot implements Serializable {
    
    private RegPapelLazy papelLazy = new RegPapelLazy();
    private RegCatPapel papel = new RegCatPapel();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            papelLazy = new RegPapelLazy();
        } catch (Exception e) {
            Logger.getLogger(Papeles.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgNew() {
        papel = new RegCatPapel();
        JsfUti.update("formPapel");
        JsfUti.executeJS("PF('dlgNuevoPapel').show();");
    }

    public void showDlgEdit(RegCatPapel paper) {
        papel = paper;
        JsfUti.update("formPapel");
        JsfUti.executeJS("PF('dlgNuevoPapel').show();");
    }

    public void guardar() {
        try {
            if(papel.getPapel() != null && papel.getAbreviatura() != null){
                if(papel.getId() == null){
                    papel.setUserCre(session.getName_user());
                    papel.setFechaCre(new Date());
                    acl.persist(papel);
                } else {
                    papel.setUserEdicion(session.getName_user());
                    papel.setFechaEdicion(new Date());
                    acl.persist(papel);
                }
                JsfUti.redirectFaces("/admin/registro/papeles.xhtml");
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(Papeles.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegPapelLazy getPapelLazy() {
        return papelLazy;
    }

    public void setPapelLazy(RegPapelLazy papelLazy) {
        this.papelLazy = papelLazy;
    }

    public RegCatPapel getPapel() {
        return papel;
    }

    public void setPapel(RegCatPapel papel) {
        this.papel = papel;
    }

}
