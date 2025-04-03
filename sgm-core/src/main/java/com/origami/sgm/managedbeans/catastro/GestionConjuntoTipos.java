/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.lazymodels.CatTipoConjuntoLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author MauricioGuzmán
 */
@Named
@ViewScoped
public class GestionConjuntoTipos implements Serializable {

    public static final Long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GestionConjuntoTipos.class.getName());

    @javax.inject.Inject
    private Entitymanager aclServices;
//    
    private LazyDataModel<CatTipoConjunto> tipoConjuntoLazy;
    private CatTipoConjuntoLazy listConjuntoTiposLazy = new CatTipoConjuntoLazy();
    private CatTipoConjunto nuevoTipoConjunto = new CatTipoConjunto();
    private CatTipoConjunto editTipoConjunto = new CatTipoConjunto();
    private String nombres;

    public GestionConjuntoTipos() {
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<CatTipoConjunto> getTipoConjuntoLazy() {
        return tipoConjuntoLazy;
    }

    public void setTipoConjuntoLazy(LazyDataModel<CatTipoConjunto> tipoConjuntoLazy) {
        this.tipoConjuntoLazy = tipoConjuntoLazy;
    }

    public CatTipoConjuntoLazy getListConjuntoTiposLazy() {
        return listConjuntoTiposLazy;
    }

    public void setListConjuntoTiposLazy(CatTipoConjuntoLazy listConjuntoTiposLazy) {
        this.listConjuntoTiposLazy = listConjuntoTiposLazy;
    }

    public CatTipoConjunto getNuevoTipoConjunto() {
        return nuevoTipoConjunto;
    }

    public void setNuevoTipoConjunto(CatTipoConjunto nuevoTipoConjunto) {
        this.nuevoTipoConjunto = nuevoTipoConjunto;
    }

    public CatTipoConjunto getEditTipoConjunto() {
        return editTipoConjunto;
    }

    public void setEditTipoConjunto(CatTipoConjunto editTipoConjunto) {
        this.editTipoConjunto = editTipoConjunto;
    }

//    public void doPreRenderView() {
//        if (!JsfUti.isAjaxRequest()) {
//            iniView();
//        }
//    }

    @PostConstruct
    protected void iniView() {
        tipoConjuntoLazy = new CatTipoConjuntoLazy();
    }

    public void showDlgNew() {
        nuevoTipoConjunto = new CatTipoConjunto();
        JsfUti.update("formNewTipoConjunto");
        JsfUti.executeJS("PF('dlgAgrgTipoConjunto').show();");
    }

    public void showDlgEdit(CatTipoConjunto c) {
        editTipoConjunto = c;
        JsfUti.update("frmEditarTipoConjunto");
        JsfUti.executeJS("dlgEditTipoConjunto.show();");
    }

    public void guardarTipoConjuntoNuevo() {
        if (nuevoTipoConjunto.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            nuevoTipoConjunto = (CatTipoConjunto) aclServices.persist(nuevoTipoConjunto);
            if (nuevoTipoConjunto.getId() != null) {
                JsfUti.redirectFaces("/admin/catastro/conjuntoTipos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public void guardarTipoConjuntoEditado() {
        if (editTipoConjunto.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b = aclServices.update(editTipoConjunto);
            if (b) {
                JsfUti.redirectFaces("/admin/catastro/conjuntoTipos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }
    
    public void eliminarTipoCon(CatTipoConjunto delete){
        try {
            aclServices.delete(delete);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Eliminat tipo Conj.", e);
        }
    }

}
