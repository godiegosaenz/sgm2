/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.alcaldia.solicitudServicio;

import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.entities.VuItems;
import com.origami.sgm.lazymodels.VuItemsLazy;
import com.origami.sgm.services.interfaces.solicitudServico.SolicitudServicosServices;
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
 * Manteniento a los tipo de Solicitud de Servicios.
 * Se puede Crear y Editar Registros de la Tabla VuItems con el registro de VuCatalogo=42.
 * 
 * @author origami
 */
@Named
@ViewScoped
public class TipoSolicitudServicio implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = Logger.getLogger(TipoSolicitudServicio.class.getName());
    
    @javax.inject.Inject
    private SolicitudServicosServices service;
    
    @javax.inject.Inject
    protected Entitymanager acl;
    
    protected VuCatalogo catalogo;
    protected LazyDataModel<VuItems> tipoSolicitudes;
    protected VuItems vuItem;
    
    @PostConstruct
    public void initView() {
        try {
            catalogo = service.getVuCatalogoById(42L);
            tipoSolicitudes= new VuItemsLazy(catalogo);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void mantenimientoTipoSolicitud(VuItems vu){
        if (vu==null) {// NUEVO REGISTRO
            vuItem= new VuItems();
        }else{// EDICION DE REGISTRO
            vuItem= vu;
        }
        JsfUti.executeJS("PF('dlgTipoSolicitud').show();");
    }
    
    public void guardarCambios(){
        try {
            vuItem.setCatalogo(catalogo);
            acl.update(vuItem);
            tipoSolicitudes= new VuItemsLazy(catalogo);
            JsfUti.messageInfo(null, "Cambios grabados exitosamente.", "");
            JsfUti.executeJS("PF('dlgTipoSolicitud').hide();");
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public LazyDataModel<VuItems> getTipoSolicitudes() {
        return tipoSolicitudes;
    }

    public void setTipoSolicitudes(LazyDataModel<VuItems> tipoSolicitudes) {
        this.tipoSolicitudes = tipoSolicitudes;
    }

    public VuItems getVuItem() {
        return vuItem;
    }

    public void setVuItem(VuItems vuItem) {
        this.vuItem = vuItem;
    }

    public VuCatalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(VuCatalogo catalogo) {
        this.catalogo = catalogo;
    }
    
}
