/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.edifications.normasConstruccion;

import com.origami.sgm.entities.CatNormasConstruccion;
import com.origami.sgm.lazymodels.CatNormasConstruccionLazy;
import com.origami.sgm.services.interfaces.edificaciones.PermisoConstruccionServices;
import com.origami.sgm.services.interfaces.registro.FichaIngresoNuevoServices;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import util.JsfUti;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class NormasConstruccionConsulta implements Serializable {

    @javax.inject.Inject
    protected PermisoConstruccionServices permisoServices;

    @javax.inject.Inject
    protected FichaIngresoNuevoServices fichaServices;

    protected CatNormasConstruccionLazy normaLazy;
    protected List<String> ciudadelas;

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {
            normaLazy = new CatNormasConstruccionLazy("A");
            ciudadelas = fichaServices.getListNombresCdla();
        } catch (Exception e) {
            Logger.getLogger(NormasConstruccionConsulta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public SelectItem[] getListCiudadelas() {
        int cantRegis = this.ciudadelas.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(ciudadelas.get(i), ciudadelas.get(i));
        }
        return options;
    }

    public void redirecEditar(CatNormasConstruccion norma) {
        JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/normasConstruccionEdicion.xhtml?idNorma=" + norma.getId());
    }

    public void redirectNuevo() {
        JsfUti.redirectFaces("/faces/vistaprocesos/edificaciones/normasConstruccion/normasConstruccion.xhtml");
    }

    public CatNormasConstruccionLazy getNormaLazy() {
        return normaLazy;
    }

    public void setNormaLazy(CatNormasConstruccionLazy normaLazy) {
        this.normaLazy = normaLazy;
    }

}
