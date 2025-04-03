/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.entities.GeRequisitosTramite;
import com.origami.sgm.entities.GeTipoConsultas;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.lazymodels.GeRequisitosTramiteLazy;
import com.origami.sgm.lazymodels.GeTipoTramiteLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
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
public class TramiteRequisitos implements Serializable{
    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;
    
    private LazyDataModel<GeRequisitosTramite> tramiteRequisitosLazy;
    private LazyDataModel<GeTipoTramite> tiposTramiteLazy;
    private GeRequisitosTramiteLazy listTramiteRequisitosLazy = new GeRequisitosTramiteLazy();
    private GeTipoTramiteLazy lisTiposTramiteLazy = new GeTipoTramiteLazy();
    private GeRequisitosTramite nuevaTramiteRequisitos = new GeRequisitosTramite();
    private GeRequisitosTramite editTramiteRequisitos = new GeRequisitosTramite();
    private List<GeTipoConsultas> geTipoConsultas;
    private List<GeTipoTramite> lisGeTipoTramite = new ArrayList<GeTipoTramite>();
    private Boolean esDialogAgregar = true;
    private String nombres;
    
    
    
    public TramiteRequisitos(){}
    

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<GeRequisitosTramite> getTramiteRequisitosLazy() {
        return tramiteRequisitosLazy;
    }

    public void setTramiteRequisitosLazy(LazyDataModel<GeRequisitosTramite> tramiteRequisitosLazy) {
        this.tramiteRequisitosLazy = tramiteRequisitosLazy;
    }

    public GeRequisitosTramiteLazy getListTramiteRequisitosLazy() {
        return listTramiteRequisitosLazy;
    }

    public void setListTramiteRequisitosLazy(GeRequisitosTramiteLazy listTramiteRequisitosLazy) {
        this.listTramiteRequisitosLazy = listTramiteRequisitosLazy;
    }

    public GeRequisitosTramite getNuevaTramiteRequisitos() {
        return nuevaTramiteRequisitos;
    }

    public void setNuevaTramiteRequisitos(GeRequisitosTramite nuevaTramiteRequisitos) {
        this.nuevaTramiteRequisitos = nuevaTramiteRequisitos;
    }

    public GeRequisitosTramite getEditTramiteRequisitos() {
        return editTramiteRequisitos;
    }

    public void setEditTramiteRequisitos(GeRequisitosTramite editTramiteRequisitos) {
        this.editTramiteRequisitos = editTramiteRequisitos;
    }

    public List<GeTipoConsultas> getGeTipoConsultas() {
        return geTipoConsultas;
    }

    public void setGeTipoConsultas(List<GeTipoConsultas> geTipoConsultas) {
        this.geTipoConsultas = geTipoConsultas;
    }

    public List<GeTipoTramite> getLisGeTipoTramite() {
        return lisGeTipoTramite;
    }

    public void setLisGeTipoTramite(List<GeTipoTramite> lisGeTipoTramite) {
        this.lisGeTipoTramite = lisGeTipoTramite;
    }

    public LazyDataModel<GeTipoTramite> getTiposTramiteLazy() {
        return tiposTramiteLazy;
    }

    public void setTiposTramiteLazy(LazyDataModel<GeTipoTramite> tiposTramiteLazy) {
        this.tiposTramiteLazy = tiposTramiteLazy;
    }

    public GeTipoTramiteLazy getLisTiposTramiteLazy() {
        return lisTiposTramiteLazy;
    }

    public void setLisTiposTramiteLazy(GeTipoTramiteLazy lisTiposTramiteLazy) {
        this.lisTiposTramiteLazy = lisTiposTramiteLazy;
    }

    public Boolean getEsDialogAgregar() {
        return esDialogAgregar;
    }

    public void setEsDialogAgregar(Boolean esDialogAgregar) {
        this.esDialogAgregar = esDialogAgregar;
    }
    

    
       
//    public void doPreRenderView() {
//        if (!JsfUti.isAjaxRequest()) {
//            iniView();
//        }
//    }
    
    @PostConstruct
    protected void iniView() {
        tiposTramiteLazy = new GeTipoTramiteLazy();
        tramiteRequisitosLazy = new GeRequisitosTramiteLazy();
        geTipoConsultas = aclServices.findAll(GeTipoConsultas.class);
    }
    
    
      
    public SelectItem[] getLisTipoConsulta() {
        int cantRegis = geTipoConsultas.size();
        SelectItem[] options = new SelectItem[cantRegis + 1];
        options[0] = new SelectItem("", "Seleccione");
        for (int i = 0; i < cantRegis; i++) {
            options[i + 1] = new SelectItem(geTipoConsultas.get(i).getConsultaPor(), geTipoConsultas.get(i).getConsultaPor());
        }
        return options;
    }
    
    
    public void showDlgNew() {
        nuevaTramiteRequisitos = new GeRequisitosTramite();
        JsfUti.update("formNewTramiteRequisitos");
        JsfUti.executeJS("PF('dlgAgrgTramiteRequisitos').show();");
    }

    public void showDlgEdit(GeRequisitosTramite c) {
        editTramiteRequisitos = c;
        lisGeTipoTramite = (List<GeTipoTramite>) editTramiteRequisitos.getGeTipoTramiteCollection();
        JsfUti.update("frmEditarTramiteRequisitos");
        JsfUti.executeJS("PF('dlgEditTramiteRequisitos').show();");
    }
    
    
    public void abrirDialogTipoTramite(){
        esDialogAgregar = true;
        JsfUti.update("formSelectTramite");
        JsfUti.executeJS("PF('dlgTiposTramiteLazy').show();");
    }
    
    public void abrirDialogTipoTramiteEdit(){
        esDialogAgregar = false;
        JsfUti.update("formSelectTramite");
        JsfUti.executeJS("PF('dlgTiposTramiteLazy').show();");
    }
    
    public void eliminarTipoTramite(GeTipoTramite item){
        for(int p=0; p< lisGeTipoTramite.size(); p++){
            if(lisGeTipoTramite.get(p).equals(item))
                lisGeTipoTramite.remove(p);
        }
        if(esDialogAgregar)
            JsfUti.update("formNewTramiteRequisitos");
        else
            JsfUti.update("frmEditarTramiteRequisitos");
    }

    
    public void agregarTiposTramite(GeTipoTramite item){
        if (item.getEstado()) {            
            if (lisGeTipoTramite.contains(item)) {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            } else {
                lisGeTipoTramite.add(item);
                if(esDialogAgregar)
                    JsfUti.update("formNewTramiteRequisitos");
                else
                    JsfUti.update("frmEditarTramiteRequisitos");
            }
        } else {
            JsfUti.messageInfo(null, Messages.elementoInactivo, "");
        }
    }
        
    public void guardarTramiteRequisitosNuevo() {
        if(nuevaTramiteRequisitos.getNombre()== null){
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            nuevaTramiteRequisitos.setGeTipoTramiteCollection(lisGeTipoTramite);
            nuevaTramiteRequisitos = (GeRequisitosTramite) aclServices.persist(nuevaTramiteRequisitos);
            if(nuevaTramiteRequisitos.getId() != null){
                JsfUti.redirectFaces("/admin/catastro/tramiteRequisitos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }
    
    public void guardarTramiteRequisitosEditado(){
        if(editTramiteRequisitos.getNombre() == null ){
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else {
            editTramiteRequisitos.setGeTipoTramiteCollection(lisGeTipoTramite);
            Boolean b = aclServices.update(editTramiteRequisitos);
            if(b){
                JsfUti.redirectFaces("/admin/catastro/tramiteRequisitos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }    
        }
    }

    
}
