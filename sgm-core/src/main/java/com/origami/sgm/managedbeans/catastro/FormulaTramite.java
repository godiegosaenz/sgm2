/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.MatFormulaTramite;
import com.origami.sgm.lazymodels.MatFormulaTramiteLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import util.GroovyUtil;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author MauricioGuzmán
 */
@Named
@ViewScoped
public class FormulaTramite implements Serializable {

    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    protected Entitymanager aclServices;

    private LazyDataModel<MatFormulaTramite> formulaTramiteLazy;
    private MatFormulaTramiteLazy listFormulaTramiteLazy = new MatFormulaTramiteLazy();
    private MatFormulaTramite nuevaFormulaTramite = new MatFormulaTramite();
    private MatFormulaTramite editFormulaTramite = new MatFormulaTramite();
    private MatFormulaTramite editFormulaTramiteCopia = new MatFormulaTramite();
    private List<GeTipoTramite> geTipoTramites;
    private String nombres;
    private GroovyUtil util;

    private Map<String, Object> datos;

    public FormulaTramite() {
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<MatFormulaTramite> getFormulaTramiteLazy() {
        return formulaTramiteLazy;
    }

    public void setFormulaTramiteLazy(LazyDataModel<MatFormulaTramite> formulaTramiteLazy) {
        this.formulaTramiteLazy = formulaTramiteLazy;
    }

    public MatFormulaTramiteLazy getListFormulaTramiteLazy() {
        return listFormulaTramiteLazy;
    }

    public void setListFormulaTramiteLazy(MatFormulaTramiteLazy listFormulaTramiteLazy) {
        this.listFormulaTramiteLazy = listFormulaTramiteLazy;
    }

    public MatFormulaTramite getNuevaFormulaTramite() {
        return nuevaFormulaTramite;
    }

    public void setNuevaFormulaTramite(MatFormulaTramite nuevaFormulaTramite) {
        this.nuevaFormulaTramite = nuevaFormulaTramite;
    }

    public MatFormulaTramite getEditFormulaTramite() {
        return editFormulaTramite;
    }

    public void setEditFormulaTramite(MatFormulaTramite editFormulaTramite) {
        this.editFormulaTramite = editFormulaTramite;
    }

    public List<GeTipoTramite> getGeTipoTramites() {
        return geTipoTramites;
    }

    public void setGeTipoTramites(List<GeTipoTramite> geTipoTramites) {
        this.geTipoTramites = geTipoTramites;
    }

    public MatFormulaTramite getEditFormulaTramiteCopia() {
        return editFormulaTramiteCopia;
    }

    public void setEditFormulaTramiteCopia(MatFormulaTramite editFormulaTramiteCopia) {
        this.editFormulaTramiteCopia = editFormulaTramiteCopia;
    }

    public Map<String, Object> getDatos() {
        return datos;
    }

    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }

//    public void doPreRenderView() {
//        if (!JsfUti.isAjaxRequest()) {
//            iniView();
//        }
//    }
    @PostConstruct
    protected void iniView() {
        formulaTramiteLazy = new MatFormulaTramiteLazy();
        geTipoTramites = aclServices.findAll(GeTipoTramite.class);

        datos = new HashMap<>();
    }

    public SelectItem[] getLisTipoTramite() {
        int cantRegis = geTipoTramites.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(geTipoTramites.get(i).getDescripcion(), geTipoTramites.get(i).getDescripcion());
        }
        return options;
    }

    public void showDlgNew() {
        nuevaFormulaTramite = new MatFormulaTramite();
        datos.put("header", "Agregar Fórmula Trámite");
        datos.put("estado", false);
        JsfUti.update("formNewFormulaTramite");
        JsfUti.executeJS("PF('dlgAgrgFormulaTramite').show();");
    }

    public void showDlgEdit(MatFormulaTramite c) {
        editFormulaTramite = c;
        editFormulaTramiteCopia = new MatFormulaTramite();
        editFormulaTramiteCopia.setNombre(editFormulaTramite.getNombre());
        editFormulaTramiteCopia.setEstado(editFormulaTramite.getEstado());
        editFormulaTramiteCopia.setFormula(editFormulaTramite.getFormula());
        editFormulaTramiteCopia.setnVersion(editFormulaTramite.getnVersion());
        editFormulaTramiteCopia.setTipoTramite(editFormulaTramite.getTipoTramite());
        editFormulaTramiteCopia.setPrefijo(editFormulaTramite.getPrefijo());
        datos.put("header", "Editar Fórmula Trámite");
        datos.put("estado", true);
        JsfUti.update("frmEditarFormulaTramite");
        JsfUti.executeJS("PF('dlgEditFormulaTramite').show();");
    }

    public void showDlgEditEstado(MatFormulaTramite c) {
        editFormulaTramite = c;
        datos.put("header", "Editar Fórmula Trámite");
        datos.put("estado", true);
        JsfUti.update("formNewFormulaTramite");
        JsfUti.executeJS("PF('dlgAgrgFormulaTramite').show();");
    }

    public void guardar(int x) {
        switch (x) {
            case 1: // Guardar nuevo
                guardarFormulaTramiteNuevo();
                break;
            case 2:
                guardarFormulaTramiteEditado();
                break;
            case 3:
                break;
        }
    }

    public void guardarFormulaTramiteNuevo() {
        if (nuevaFormulaTramite.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "Nombre");
        } else if (nuevaFormulaTramite.getPrefijo() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "Prefijo");
        } else if (nuevaFormulaTramite.getFormula()== null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "Formula");
        }else {
            nuevaFormulaTramite.setnVersion(new Integer(1));
            nuevaFormulaTramite.setFecCre(new Date());
            nuevaFormulaTramite = (MatFormulaTramite) aclServices.persist(nuevaFormulaTramite);
            if (nuevaFormulaTramite.getId() != null) {
                JsfUti.redirectFaces("/admin/catastro/formulaTramites.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

    public void guardarFormulaTramiteEditado() {
        if (editFormulaTramiteCopia.getNombre() == null) {
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            Boolean b;
            if (editFormulaTramite.getFormula().compareTo(editFormulaTramiteCopia.getFormula()) == 0) {
                editFormulaTramite.setNombre(editFormulaTramiteCopia.getNombre());
                editFormulaTramite.setPrefijo(editFormulaTramiteCopia.getPrefijo());
                editFormulaTramite.setTipoTramite(editFormulaTramiteCopia.getTipoTramite());
                editFormulaTramite.setEstado(editFormulaTramiteCopia.getEstado());
                b = aclServices.update(editFormulaTramite);
            } else {
                editFormulaTramite.setEstado(Boolean.FALSE);
                editFormulaTramiteCopia.setFecCre(new Date());
                editFormulaTramiteCopia.setnVersion(editFormulaTramite.getnVersion() + 1);
                b = aclServices.update(editFormulaTramite);
                editFormulaTramiteCopia = (MatFormulaTramite) aclServices.persist(editFormulaTramiteCopia);
            }
            if (b) {
                JsfUti.redirectFaces("/admin/catastro/formulaTramites.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }

}
