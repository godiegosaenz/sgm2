/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatTipoConjunto;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author adrian
 */
@Named
@ViewScoped
public class GestionCiudadelas implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;
//    
    private BaseLazyDataModel<CatCiudadela> ciudadelaLazy; 
    private CatCiudadela nuevaCiudadela = new CatCiudadela();
    private CatCiudadela editCiudadela = new CatCiudadela();
    private List<CatParroquia> parroquias;
    private List<CatTipoConjunto> conjuntoTipos;
    private List<CatTipoConjunto> conjuntoTiposMenu;

    public GestionCiudadelas() {
    }

    private String nombres;

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        ciudadelaLazy = new BaseLazyDataModel<>(CatCiudadela.class);
        conjuntoTipos = aclServices.findAll(CatTipoConjunto.class);
        Map<String, Object> paramt = new HashMap<>();
//        paramt.put("idCanton", aclServices.find(Querys.getParroquiasByCanton, new String[]{"codigoNacional", "codNac"}, new Object[]{SisVars.CANTON, SisVars.PROVINCIA}));
//        parroquias = aclServices.findObjectByParameterOrderList(CatParroquia.class, paramt, new String[]{"codigoParroquia"}, true);
        parroquias = aclServices.findAllEntCopy(CatParroquia.class);
        conjuntoTiposMenu = aclServices.findAllEntCopy(CatTipoConjunto.class);
    }

    public BaseLazyDataModel<CatCiudadela> getCiudadelaLazy() {
        return ciudadelaLazy;
    }

    public void setCiudadelaLazy(BaseLazyDataModel<CatCiudadela> ciudadelaLazy) {
        this.ciudadelaLazy = ciudadelaLazy;
    }

    public SelectItem[] getLisTipoConjunto() {
        int cantRegis = conjuntoTipos.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(conjuntoTipos.get(i).getNombre(), conjuntoTipos.get(i).getNombre());
        }
        return options;
    }

    public void showDlgNew() {
        nuevaCiudadela = new CatCiudadela();
        JsfUti.update("formNewCiudadela");
        JsfUti.executeJS("PF('dlgAgrgCiudadela').show();");
    }

    public void showDlgEdit(CatCiudadela c) {
        editCiudadela = c;
        JsfUti.update("frmEditarCiudadela");
        JsfUti.executeJS("dlgEditCiu.show();");
    }

    public void guardarCiudadelaNuevo() {
        if (nuevaCiudadela.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            //nuevaCiudadela.setEstado(Boolean.TRUE);
            nuevaCiudadela = (CatCiudadela) aclServices.persist(nuevaCiudadela);
            if (nuevaCiudadela.getId() != null) {
                JsfUti.redirectFaces("/admin/catastro/ciudadelas.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public void guardarCiudadelaEditado() {
        if (editCiudadela.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b = aclServices.update(editCiudadela);
            if (b) {
                JsfUti.redirectFaces("/admin/catastro/ciudadelas.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public CatCiudadela getNuevaCiudadela() {
        return nuevaCiudadela;
    }

    public void setNuevaCiudadela(CatCiudadela nuevaCiudadela) {
        this.nuevaCiudadela = nuevaCiudadela;
    }

    public CatCiudadela getEditCiudadela() {
        return editCiudadela;
    }

    public void setEditCiudadela(CatCiudadela editCiudadela) {
        this.editCiudadela = editCiudadela;
    }

    public List<CatTipoConjunto> getConjuntoTipos() {
        return conjuntoTipos;
    }

    public void setConjuntoTipos(List<CatTipoConjunto> conjuntoTipos) {
        this.conjuntoTipos = conjuntoTipos;
    }

    public List<CatTipoConjunto> getConjuntoTiposMenu() {
        return conjuntoTiposMenu;
    }

    public void setConjuntoTiposMenu(List<CatTipoConjunto> conjuntoTiposMenu) {
        this.conjuntoTiposMenu = conjuntoTiposMenu;
    }

    public List<CatParroquia> getParroquias() {
        return parroquias;
    }

    public void setParroquias(List<CatParroquia> parroquias) {
        this.parroquias = parroquias;
    }

}
