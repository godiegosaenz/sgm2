/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.lazymodels.RegEnteJudicialLazy;
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
public class EntesJudiciales extends BpmManageBeanBaseRoot implements Serializable {

    private RegEnteJudicialLazy lazyJudiciales = new RegEnteJudicialLazy();
    private RegEnteJudiciales ente = new RegEnteJudiciales();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            lazyJudiciales = new RegEnteJudicialLazy();
        } catch (Exception e) {
            Logger.getLogger(EntesJudiciales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void showDlgNew() {
        ente = new RegEnteJudiciales();
        JsfUti.update("formEnteJu");
        JsfUti.executeJS("PF('dlgEnteJudicial').show();");
    }

    public void showDlgEdit(RegEnteJudiciales en) {
        ente = en;
        JsfUti.update("formEnteJu");
        JsfUti.executeJS("PF('dlgEnteJudicial').show();");
    }

    public void guardar() {
        try {
            if(ente.getNombre() != null && ente.getAbreviatura() != null){
                if(ente.getId() == null){
                    ente.setFechaCreacion(new Date());
                    ente.setUserCreacion(session.getName_user());
                    acl.persist(ente);
                } else {
                    ente.setFechaEdicion(new Date());
                    ente.setUserEdicion(session.getName_user());
                    acl.persist(ente);
                }
                JsfUti.redirectFaces("/admin/registro/entejudiciales.xhtml");
            } else {
                JsfUti.messageError(null, Messages.faltanCampos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(EntesJudiciales.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegEnteJudiciales getEnte() {
        return ente;
    }

    public void setEnte(RegEnteJudiciales ente) {
        this.ente = ente;
    }

    public RegEnteJudicialLazy getLazyJudiciales() {
        return lazyJudiciales;
    }

    public void setLazyJudiciales(RegEnteJudicialLazy lazyJudiciales) {
        this.lazyJudiciales = lazyJudiciales;
    }

}
