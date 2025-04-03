/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.session.UserSession;
import com.origami.sgm.entities.CatBloqueObraEspecial;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.JsfUti;

/**
 *
 * @author OrigamiSolutions
 */
@Named
@ViewScoped
public class ObrasEspeciales implements Serializable{
    private static final Logger LOG = Logger.getLogger(ObrasEspeciales.class.getName());    
    private Boolean nuevo;
    private CatBloqueObraEspecial obrEspecial;
    private String idCatBloqueObrEspecial;
    private String idBloque;
    private String esNuevo;
    private String ver;
    private Boolean esVer;
    
    private List<CtlgItem> orden;
    
    private BaseLazyDataModel<CatBloqueObraEspecial> obrasEspeciales;
    private List<CatBloqueObraEspecial> seleccionados;
    @javax.inject.Inject
    private CatastroServices ejb;
    @Inject
    private UserSession us;
    @javax.inject.Inject
    private Entitymanager manager;
    
    public void initView() {
        try {
            if (!JsfUti.isAjaxRequest()) {
                obrasEspeciales = new BaseLazyDataModel<>(CatBloqueObraEspecial.class);
                nuevo = Boolean.valueOf(esNuevo);
                esVer = Boolean.valueOf(ver);
                if (nuevo) {
                    if (idBloque == null) {
                        return;
                    }
                    obrEspecial = new CatBloqueObraEspecial();
//                    obrEspecial.setBloque(new CatPredioBloque(Long.valueOf(idBloque)));                    
                    obrEspecial.setUsuario(us.getName_user());
                    obrEspecial.setEstado("A");
                } else {
                    if (idCatBloqueObrEspecial == null) {
                        return;
                    }
                    obrEspecial = ejb.getBloqueObraEspecialById(Long.valueOf(idCatBloqueObrEspecial));
                }
            }
        } catch (Exception ne) {
            LOG.log(Level.SEVERE, null, ne);
        }
    }
    
    public void agregarObraEspecial() {
        try {
            if (obrEspecial == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            obrEspecial.setFecha(new Date());
            RequestContext.getCurrentInstance().closeDialog(ejb.guardarObraEspecial(obrEspecial, us.getName_user()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void modificarObraEspecial() {
        try {
            if (obrEspecial == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            obrEspecial.setModificado(us.getName_user());
            obrEspecial = ejb.guardarObraEspecial(obrEspecial, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(obrEspecial);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
    
    public void selecObrasEspeciales() {
        if (seleccionados != null) {
            RequestContext.getCurrentInstance().closeDialog(seleccionados);
        }
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public CatBloqueObraEspecial getObrEspecial() {
        return obrEspecial;
    }

    public void setObrEspecial(CatBloqueObraEspecial obrEspecial) {
        this.obrEspecial = obrEspecial;
    }

    public String getIdCatBloqueObrEspecial() {
        return idCatBloqueObrEspecial;
    }

    public void setIdCatBloqueObrEspecial(String idCatBloqueObrEspecial) {
        this.idCatBloqueObrEspecial = idCatBloqueObrEspecial;
    }

    public String getIdBloque() {
        return idBloque;
    }

    public void setIdBloque(String idBloque) {
        this.idBloque = idBloque;
    }

    public String getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(String esNuevo) {
        this.esNuevo = esNuevo;
    }

    public List<CtlgItem> getOrden() {
        return orden;
    }

    public void setOrden(List<CtlgItem> orden) {
        this.orden = orden;
    }

    public BaseLazyDataModel<CatBloqueObraEspecial> getObrasEspeciales() {
        return obrasEspeciales;
    }

    public void setObrasEspeciales(BaseLazyDataModel<CatBloqueObraEspecial> obrasEspeciales) {
        this.obrasEspeciales = obrasEspeciales;
    }

    public List<CatBloqueObraEspecial> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatBloqueObraEspecial> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public CatastroServices getEjb() {
        return ejb;
    }

    public void setEjb(CatastroServices ejb) {
        this.ejb = ejb;
    }

    public UserSession getUs() {
        return us;
    }

    public void setUs(UserSession us) {
        this.us = us;
    }

    public Entitymanager getManager() {
        return manager;
    }

    public void setManager(Entitymanager manager) {
        this.manager = manager;
    }
    
    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public Boolean getEsVer() {
        return esVer;
    }

    public void setEsVer(Boolean esVer) {
        this.esVer = esVer;
    }
    
}
