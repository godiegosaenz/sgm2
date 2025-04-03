/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatProvincia;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
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
public class GestionCantones implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;
//    
    private LazyDataModel<CatCanton> cantonLazy;
    private BaseLazyDataModel<CatCanton> listCantonLazy = new BaseLazyDataModel(CatCanton.class);
    private CatCanton nuevoCanton = new CatCanton();
    private CatCanton editCanton = new CatCanton();
    private List<CatProvincia> provincias;
    private List<CatProvincia> provinciasMenu;
    private String nombres;

    public GestionCantones() {
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<CatCanton> getCantonLazy() {
        return cantonLazy;
    }

    public void setCantonLazy(LazyDataModel<CatCanton> cantonLazy) {
        this.cantonLazy = cantonLazy;
    }

    public BaseLazyDataModel<CatCanton> getListCantonLazy() {
        return listCantonLazy;
    }

    public void setListCantonLazy(BaseLazyDataModel<CatCanton> listCantonLazy) {
        this.listCantonLazy = listCantonLazy;
    }

    public CatCanton getNuevoCanton() {
        return nuevoCanton;
    }

    public void setNuevoCanton(CatCanton nuevoCanton) {
        this.nuevoCanton = nuevoCanton;
    }

    public CatCanton getEditCanton() {
        return editCanton;
    }

    public void setEditCanton(CatCanton editCanton) {
        this.editCanton = editCanton;
    }

    public List<CatProvincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(List<CatProvincia> provincias) {
        this.provincias = provincias;
    }

    public List<CatProvincia> getProvinciasMenu() {
        return provinciasMenu;
    }

    public void setProvinciasMenu(List<CatProvincia> provinciasMenu) {
        this.provinciasMenu = provinciasMenu;
    }

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        cantonLazy = new BaseLazyDataModel<>(CatCanton.class);
        provincias = aclServices.findAll(CatProvincia.class);
        provinciasMenu = aclServices.findAllEntCopy(CatProvincia.class);
    }

    public SelectItem[] getLisProvincias() {
        int cantRegis = provincias.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(provincias.get(i).getDescripcion(), provincias.get(i).getDescripcion());
        }
        return options;
    }

    public void showDlgNew() {
        nuevoCanton = new CatCanton();
        JsfUti.update("formNewCanton");
        JsfUti.executeJS("PF('dlgAgrgCanton').show();");
    }

    public void showDlgEdit(CatCanton c) {
        editCanton = c;
        JsfUti.update("frmEditarCanton");
        JsfUti.executeJS("dlgEditCanton.show();");
    }

    public void guardarCantonNuevo() {
        if (nuevoCanton.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            nuevoCanton = (CatCanton) aclServices.persist(nuevoCanton);
            if (nuevoCanton.getId() != null) {
                JsfUti.redirectFaces("/admin/catastro/cantones.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public void guardarCantonEditado() {
        if (editCanton.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b = aclServices.update(editCanton);
            if (b) {
                JsfUti.redirectFaces("/admin/catastro/cantones.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

}
