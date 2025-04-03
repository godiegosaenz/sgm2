/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.session.UserSession;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.CatPredioCultivo;
import com.origami.sgm.entities.CtlgItem;
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
import util.Faces;
import util.JsfUti;

/**
 *
 * @author elcid
 */
@Named
@ViewScoped
public class Cultivos implements Serializable {

    private static final Logger LOG = Logger.getLogger(Cultivos.class.getName());

    private Boolean nuevo;
    private CatPredioCultivo cult;
    private String idCatPredioCult;
    private String idPredio;
    private String esNuevo;
    private String ver;
    private Boolean esVer;

    private List<CatPredioCultivo> seleccionados;

    @javax.inject.Inject
    private CatastroServices ejb;
    @javax.inject.Inject
    private Entitymanager manager;
    @Inject
    private UserSession us;

    private Long padreItem;
    private Long hijoItem;

    public void initView() {
        try {
            if (!JsfUti.isAjaxRequest()) {
                nuevo = Boolean.valueOf(esNuevo);
                esVer = Boolean.valueOf(ver);
                if (nuevo) {
                    if (idPredio == null) {
                        return;
                    }
                    cult = new CatPredioCultivo();
                    cult.setPredio(ejb.getPredioId(Long.valueOf(idPredio)));
                    cult.setUsuario(us.getName_user());
                    cult.setEstado("A");
                } else {
                    if (idCatPredioCult == null) {
                        return;
                    }
                    if (Long.valueOf(idCatPredioCult) > 0l) {
                        cult = ejb.getPredioCultivoById(Long.valueOf(idCatPredioCult));
                        CtlgItem hijo = ejb.getCtlgitemById(cult.getTipo().getHijo().longValue());
                        hijoItem = cult.getTipo().getHijo().longValue();
                        padreItem = hijo.getPadre().longValue();
                    } else {
                        cult = (CatPredioCultivo) Faces.getSessionBean("cultivo");
                        Faces.setSessionBean("cultivo", null);
                        if (cult == null) {
                            return;
                        }
                    }
                }
            }
        } catch (NumberFormatException ne) {
            LOG.log(Level.SEVERE, null, ne);
        }
    }

    public void agregarCultivo() {
        try {
            if (cult == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            cult.setFecha(new Date());
            RequestContext.getCurrentInstance().closeDialog(ejb.guardarCultivo(cult, us.getName_user()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void modificarCultivo() {
        try {
            if (cult == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            cult.setModificado(us.getName_user());
            cult = ejb.guardarCultivo(cult, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(cult);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public List<CtlgItem> getListadoCultivos() {
        if (padreItem != null && padreItem > 0) {
            return manager.findAllEntCopy(Querys.getCtlgItemaByCultivos, new String[]{"padre"}, new Object[]{padreItem});
        }
        return null;
    }

    public List<CtlgItem> getListadoItemsCultivos() {
        if (hijoItem != null && hijoItem > 0) {
            return manager.findAllEntCopy(Querys.getCtlgItemaByCultivosHijos, new String[]{"hijo"}, new Object[]{hijoItem});
        }
        return null;
    }

    public void selecCultivos() {
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

    public CatPredioCultivo getCult() {
        return cult;
    }

    public void setCult(CatPredioCultivo cult) {
        this.cult = cult;
    }

    public String getIdCatPredioCult() {
        return idCatPredioCult;
    }

    public void setIdCatPredioCult(String idCatPredioCult) {
        this.idCatPredioCult = idCatPredioCult;
    }

    public String getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(String idPredio) {
        this.idPredio = idPredio;
    }

    public String getEsNuevo() {
        return esNuevo;
    }

    public void setEsNuevo(String esNuevo) {
        this.esNuevo = esNuevo;
    }

    public List<CatPredioCultivo> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatPredioCultivo> seleccionados) {
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

    public Long getPadreItem() {
        return padreItem;
    }

    public void setPadreItem(Long padreItem) {
        this.padreItem = padreItem;
    }

    public Long getHijoItem() {
        return hijoItem;
    }

    public void setHijoItem(Long hijoItem) {
        this.hijoItem = hijoItem;
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
