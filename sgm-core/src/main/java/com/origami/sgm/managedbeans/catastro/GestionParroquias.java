/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatProvincia;
import com.origami.sgm.lazymodels.CatParroquiaLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
public class GestionParroquias implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;
//    
    private LazyDataModel<CatParroquia> parroquiaLazy;
    private CatParroquiaLazy listParroquiaLazy = new CatParroquiaLazy();
    private CatParroquia nuevaParroquia = new CatParroquia();
    private CatParroquia editParroquia = new CatParroquia();
    private List<CatCanton> cantones;
    private List<CatCanton> cantonesMenu;
    private List<CatProvincia> provincias;
    private String nombres;

    public GestionParroquias() {
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<CatParroquia> getParroquiaLazy() {
        return parroquiaLazy;
    }

    public void setParroquiaLazy(LazyDataModel<CatParroquia> parroquiaLazy) {
        this.parroquiaLazy = parroquiaLazy;
    }

    public CatParroquiaLazy getListParroquiaLazy() {
        return listParroquiaLazy;
    }

    public void setListParroquiaLazy(CatParroquiaLazy listParroquiaLazy) {
        this.listParroquiaLazy = listParroquiaLazy;
    }

    public CatParroquia getNuevaParroquia() {
        return nuevaParroquia;
    }

    public void setNuevaParroquia(CatParroquia nuevaParroquia) {
        this.nuevaParroquia = nuevaParroquia;
    }

    public CatParroquia getEditParroquia() {
        return editParroquia;
    }

    public void setEditParroquia(CatParroquia editParroquia) {
        this.editParroquia = editParroquia;
    }

    public List<CatCanton> getCantones() {
        return cantones;
    }

    public void setCantones(List<CatCanton> cantones) {
        this.cantones = cantones;
    }

    public List<CatProvincia> getProvincias() {
        return provincias;
    }

    public void setProvincias(List<CatProvincia> provincias) {
        this.provincias = provincias;
    }

    public List<CatCanton> getCantonesMenu() {
        return cantonesMenu;
    }

    public void setCantonesMenu(List<CatCanton> cantonesMenu) {
        this.cantonesMenu = cantonesMenu;
    }

//    public void doPreRenderView() {
//        if (!JsfUti.isAjaxRequest()) {
//            iniView();
//        }
//    }

    @PostConstruct
    protected void iniView() {
        parroquiaLazy = new CatParroquiaLazy();
        cantones = aclServices.findAll(CatCanton.class);
        provincias = aclServices.findAll(CatProvincia.class);
        cantonesMenu = aclServices.findAllEntCopy(CatCanton.class);
    }

    public SelectItem[] getLisCantones() {
        int cantRegis = cantones.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(cantones.get(i).getNombre(), cantones.get(i).getNombre());
        }
        return options;
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
        nuevaParroquia = new CatParroquia();
        JsfUti.update("formNewParroquia");
        JsfUti.executeJS("PF('dlgAgrgParroquia').show();");
    }

    public void showDlgEdit(CatParroquia c) {
        editParroquia = c;
        JsfUti.update("frmEditarParroquia");
        JsfUti.executeJS("dlgEditParroquia.show();");
    }

    public void guardarParroquiaNuevo() {
        if (nuevaParroquia.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            nuevaParroquia = (CatParroquia) aclServices.persist(nuevaParroquia);
            if (nuevaParroquia.getId() != null) {
                JsfUti.redirectFaces("/admin/catastro/parroquias.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }
    
    public void addParroquia() {
        if (nuevaParroquia.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            nuevaParroquia = (CatParroquia) aclServices.persist(nuevaParroquia);
            if (nuevaParroquia.getId() != null) {
                JsfUti.redirectFaces("/vistaprocesos/catastro/parroquiasuser.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public void updateParroquia() {
        if (editParroquia.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b = aclServices.update(editParroquia);
            if (b) {
                JsfUti.redirectFaces("/vistaprocesos/catastro/parroquiasuser.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }
    
    public void guardarParroquiaEditado() {
        if (editParroquia.getDescripcion() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b = aclServices.update(editParroquia);
            if (b) {
                JsfUti.redirectFaces("/admin/catastro/parroquias.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }
    /*METODO AGREGADO*/
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

}
