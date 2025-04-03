/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.catastro;

import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.RegistroProfesionalTecnico;
import com.origami.sgm.lazymodels.CatEnteLazy;
import com.origami.sgm.lazymodels.RegistroProfesionalTecnicoLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class ProfesionalTecnico implements Serializable{
    public static final Long serialVersionUID = 1L;

    @javax.inject.Inject
    private Entitymanager aclServices;
    
    private LazyDataModel<RegistroProfesionalTecnico> profesionalTecnicoLazy;
    private RegistroProfesionalTecnicoLazy lisProfesionalTecnicoLazy = new RegistroProfesionalTecnicoLazy();
    private RegistroProfesionalTecnico nuevoProfesionalTecnico = new RegistroProfesionalTecnico();
    private RegistroProfesionalTecnico editProfesionalTecnico = new RegistroProfesionalTecnico();
    private LazyDataModel<CatEnte> catEnteLazy;
    private CatEnteLazy lisCatEnteLazy = new CatEnteLazy();
    private List<CatEnte> lisCatEnte = new ArrayList<>();
    private List<CatEnte> lisEnteExist = new ArrayList<>();
    private Boolean estadoProfesionalTecnico = true;
    private Boolean esDialogAgregar = false;
    private String nombres;
    
    
    
    public ProfesionalTecnico(){}
    

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public LazyDataModel<RegistroProfesionalTecnico> getProfesionalTecnicoLazy() {
        return profesionalTecnicoLazy;
    }

    public void setProfesionalTecnicoLazy(LazyDataModel<RegistroProfesionalTecnico> profesionalTecnicoLazy) {
        this.profesionalTecnicoLazy = profesionalTecnicoLazy;
    }

    public RegistroProfesionalTecnicoLazy getLisProfesionalTecnicoLazy() {
        return lisProfesionalTecnicoLazy;
    }

    public void setLisProfesionalTecnicoLazy(RegistroProfesionalTecnicoLazy lisProfesionalTecnicoLazy) {
        this.lisProfesionalTecnicoLazy = lisProfesionalTecnicoLazy;
    }

    public RegistroProfesionalTecnico getNuevoProfesionalTecnico() {
        return nuevoProfesionalTecnico;
    }

    public void setNuevoProfesionalTecnico(RegistroProfesionalTecnico nuevoProfesionalTecnico) {
        this.nuevoProfesionalTecnico = nuevoProfesionalTecnico;
    }

    public RegistroProfesionalTecnico getEditProfesionalTecnico() {
        return editProfesionalTecnico;
    }

    public void setEditProfesionalTecnico(RegistroProfesionalTecnico editProfesionalTecnico) {
        this.editProfesionalTecnico = editProfesionalTecnico;
    }

    public List<CatEnte> getLisCatEnte() {
        return lisCatEnte;
    }

    public void setLisCatEnte(List<CatEnte> lisCatEnte) {
        this.lisCatEnte = lisCatEnte;
    }

    public Boolean getEsDialogAgregar() {
        return esDialogAgregar;
    }

    public void setEsDialogAgregar(Boolean esDialogAgregar) {
        this.esDialogAgregar = esDialogAgregar;
    }

    public LazyDataModel<CatEnte> getCatEnteLazy() {
        return catEnteLazy;
    }

    public void setCatEnteLazy(LazyDataModel<CatEnte> catEnteLazy) {
        this.catEnteLazy = catEnteLazy;
    }

    public CatEnteLazy getLisCatEnteLazy() {
        return lisCatEnteLazy;
    }

    public void setLisCatEnteLazy(CatEnteLazy lisCatEnteLazy) {
        this.lisCatEnteLazy = lisCatEnteLazy;
    }

    public Boolean getEstadoProfesionalTecnico() {
        return estadoProfesionalTecnico;
    }

    public void setEstadoProfesionalTecnico(Boolean estadoProfesionalTecnico) {
        this.estadoProfesionalTecnico = estadoProfesionalTecnico;
    }

    public List<CatEnte> getLisEnteExist() {
        return lisEnteExist;
    }

    public void setLisEnteExist(List<CatEnte> lisEnteExist) {
        this.lisEnteExist = lisEnteExist;
    }
    
    
    

    
        
    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }
    
    protected void iniView() {
        catEnteLazy= new CatEnteLazy();
        profesionalTecnicoLazy = new RegistroProfesionalTecnicoLazy();
    }
    
    
    public void showDlgNew() {
        lisCatEnte = new ArrayList<>();
        nuevoProfesionalTecnico = new RegistroProfesionalTecnico();
        JsfUti.update("formNewProfesionalTecnico");
        JsfUti.executeJS("PF('dlgAgrgProfesionalTecnico').show();");
    }
    
    public void showDlgEdit(RegistroProfesionalTecnico c){
        editProfesionalTecnico = c;
        lisCatEnte = new ArrayList<>();
        lisCatEnte.add((CatEnte) c.getEnte());
        JsfUti.update("frmEditarProfesionalTecnico");
        JsfUti.executeJS("PF('dlgEditProfesionalTecnico').show();");
    }
    
    public void abrirDialogCatEnte(){
        esDialogAgregar = true;
        JsfUti.update("formSelectCatEnte");
        JsfUti.executeJS("PF('dlgCatEnteLazy').show();");
    }
    
    public void abrirDialogCatEnteEdit(){
        esDialogAgregar = false;
        JsfUti.update("formSelectCatEnte");
        JsfUti.executeJS("PF('dlgCatEnteLazy').show();");
    }
    
    public void eliminarCatEnte(CatEnte item){
        lisCatEnte.remove(0);
        if(esDialogAgregar)
            JsfUti.update("formNewProfesionalTecnico");
        else
            JsfUti.update("frmEditarProfesionalTecnico");
    }

    
    public void agregarCatEnte(CatEnte item){        
        if (item.getEstado().equalsIgnoreCase("A")) {            
            if (lisCatEnte.contains(item)) {
                JsfUti.messageInfo(null, Messages.elementoRepetido, "");
            } else {
                if(lisCatEnte.size() >=1 ){
                    JsfUti.messageInfo(null, Messages.unSoloElemento, "");
                } else{
                    lisCatEnte.add(item);
                    if(esDialogAgregar)
                        JsfUti.update("formNewProfesionalTecnico");
                    else
                        JsfUti.update("frmEditarProfesionalTecnico");
                }
            }
        } else {
            JsfUti.messageInfo(null, Messages.elementoInactivo, "");
        }        
    }
        
    public void guardarProfesionalTecnicoNuevo() {
        Boolean fechaValida= false;
        lisEnteExist = aclServices.findAll(
                Querys.getRegistroProfesionalByCaTEnte, 
                new String[]{"id"}, 
                new Object[]{lisCatEnte.get(0).getId()});
        fechaValida = nuevoProfesionalTecnico.getFechaCaducidad().after(new Date());
        
        if(nuevoProfesionalTecnico.getCodigoTecnico() == null){
            JsfUti.messageInfo(null, Messages.faltanCampos, "");
        } else if(lisEnteExist.size() > 0){
            JsfUti.messageInfo(null, Messages.enteAsociadoExiste, "");
        } else if(!fechaValida){
            JsfUti.messageInfo(null, Messages.fechaInvalida, "");
        } else {
            nuevoProfesionalTecnico.setEstado(Boolean.TRUE);
            nuevoProfesionalTecnico.setFechaCreacion(new Date());
            if(lisCatEnte.size() > 0)
                nuevoProfesionalTecnico.setEnte(lisCatEnte.get(0));
            else
                nuevoProfesionalTecnico.setEnte(null);
            nuevoProfesionalTecnico = (RegistroProfesionalTecnico) aclServices.persist(nuevoProfesionalTecnico);
            if(nuevoProfesionalTecnico.getId() != null){
                JsfUti.redirectFaces("/admin/catastro/profesionalTecnicos.xhtml");
            } else {
                JsfUti.messageInfo(null, Messages.error, "");
            }
        }
    }
    
    public void guardarProfesionalTecnicoEditado(){
        Boolean b, fechaValida = false;
        if(lisCatEnte.size() > 0)
            editProfesionalTecnico.setEnte(lisCatEnte.get(0));
        else
            editProfesionalTecnico.setEnte(null);
        b = aclServices.update(editProfesionalTecnico);
        if(b){
            JsfUti.redirectFaces("/admin/catastro/profesionalTecnicos.xhtml");
        } else {
            JsfUti.messageInfo(null, Messages.error, "");
        }
    }

    
}
