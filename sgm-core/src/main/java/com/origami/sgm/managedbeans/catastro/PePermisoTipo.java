/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.PeTipoPermiso;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
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
public class PePermisoTipo implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;
//    
    private LazyDataModel<PeTipoPermiso> tipoPermisoLazy;
    private BaseLazyDataModel<PeTipoPermiso> lisTipoPermisoLazy = new BaseLazyDataModel<>(PeTipoPermiso.class);
    private PeTipoPermiso nuevoTipoPermiso = new PeTipoPermiso();
    private PeTipoPermiso editTipoPermiso = new PeTipoPermiso();
    
    /*
    private CatCantonLazy listCantonLazy = new CatCantonLazy();
    private CatCanton nuevoCanton = new CatCanton();
    private CatCanton editCanton = new CatCanton();
    private List<CatProvincia> provincias;
    private List<CatProvincia> provinciasMenu;
    private String nombres;
    */

    public PePermisoTipo() {
    }
    
    /*

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

    public CatCantonLazy getListCantonLazy() {
        return listCantonLazy;
    }

    public void setListCantonLazy(CatCantonLazy listCantonLazy) {
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
    */

    public LazyDataModel<PeTipoPermiso> getTipoPermisoLazy() {
        return tipoPermisoLazy;
    }

    public void setTipoPermisoLazy(LazyDataModel<PeTipoPermiso> tipoPermisoLazy) {
        this.tipoPermisoLazy = tipoPermisoLazy;
    }

    public BaseLazyDataModel<PeTipoPermiso> getLisTipoPermisoLazy() {
        return lisTipoPermisoLazy;
    }

    public void setLisTipoPermisoLazy(BaseLazyDataModel<PeTipoPermiso> lisTipoPermisoLazy) {
        this.lisTipoPermisoLazy = lisTipoPermisoLazy;
    }

    public PeTipoPermiso getNuevoTipoPermiso() {
        return nuevoTipoPermiso;
    }

    public void setNuevoTipoPermiso(PeTipoPermiso nuevoTipoPermiso) {
        this.nuevoTipoPermiso = nuevoTipoPermiso;
    }

    public PeTipoPermiso getEditTipoPermiso() {
        return editTipoPermiso;
    }

    public void setEditTipoPermiso(PeTipoPermiso editTipoPermiso) {
        this.editTipoPermiso = editTipoPermiso;
    }
    
    
    

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        tipoPermisoLazy = new BaseLazyDataModel<>(PeTipoPermiso.class);
        //cantonLazy = new CatCantonLazy();
        //provincias = aclServices.findAll(CatProvincia.class);
        //provinciasMenu = aclServices.findAllEntCopy(CatProvincia.class);
    }

    /*
    public SelectItem[] getLisProvincias() {
        int cantRegis = provincias.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(provincias.get(i).getDescripcion(), provincias.get(i).getDescripcion());
        }
        return options;
    }
    */

    public void showDlgNew() {
        nuevoTipoPermiso = new PeTipoPermiso();
        JsfUti.update("formNewTipoPermiso");
        JsfUti.executeJS("PF('dlgAgrgTipoPermiso').show();");
    }

    public void showDlgEdit(PeTipoPermiso c) {
        editTipoPermiso = c;
        JsfUti.update("frmEditarTipoPermiso");
        JsfUti.executeJS("PF('dlgEditTipoPermiso').show();");
    }

    public void guardarTipoPermisoNuevo() {
        if (nuevoTipoPermiso.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            if(nuevoTipoPermiso.getCodigo().length() > 3){
                JsfUti.messageInfo(null, Messages.tamanioCorto, "");
            } else {
                nuevoTipoPermiso = (PeTipoPermiso) aclServices.persist(nuevoTipoPermiso);
                if (nuevoTipoPermiso.getId() != null) {
                    JsfUti.redirectFaces("/admin/catastro/pePermisoTipos.xhtml");
                } else {
                    JsfUti.messageInfo(null, Messages.error, "");
                }
            }
        }
    }

    public void guardarTipoPermisoEditado() {
        if (editTipoPermiso.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b = aclServices.update(editTipoPermiso);
            if (b) {
                JsfUti.redirectFaces("/admin/catastro/pePermisoTipos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }    
    }

}
