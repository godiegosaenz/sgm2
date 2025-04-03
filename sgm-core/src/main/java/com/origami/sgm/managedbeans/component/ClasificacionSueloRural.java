/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import com.origami.session.UserSession;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioClasificRural;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.sgm.services.interfaces.catastro.CatastroServices;
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
 * @author OrigamiSolutions
 */
@Named
@ViewScoped
public class ClasificacionSueloRural implements Serializable {

    private static final Logger LOG = Logger.getLogger(ClasificacionSueloRural.class.getName());

    private Boolean nuevo;
    private CatPredioClasificRural preClaRu;
    private String idCatClasiSueloRural;
    private String idPredio;
    private String esNuevo;

    private BaseLazyDataModel<CatPredioClasificRural> clasificaciones;
    private List<CatPredioClasificRural> seleccionados;

    @javax.inject.Inject
    private CatastroServices ejb;
    @Inject
    private UserSession us;

    public void initView() {
        try {
            if (!JsfUti.isAjaxRequest()) {
                nuevo = Boolean.valueOf(esNuevo);
                if (nuevo) {
                    if (idPredio == null) {
                        return;
                    }
                    preClaRu = new CatPredioClasificRural();
                    preClaRu.setPredio(new CatPredio(Long.valueOf(idPredio)));
                    preClaRu.setUsuario(us.getName_user());
                    preClaRu.setEstado("A");
                } else {
                    if (idCatClasiSueloRural == null) {
                        return;
                    }
                    if (Long.valueOf(idCatClasiSueloRural) > 0) {
                        preClaRu = ejb.getPredioClasificRuralById(Long.valueOf(idCatClasiSueloRural));
                    } else {
                        preClaRu = (CatPredioClasificRural) Faces.getSessionBean("clasificacionRural");
                        Faces.setSessionBean("clasificacionRural", null);
                        if (preClaRu == null) {
                            return;
                        }
                    }
                }
            }
        } catch (NumberFormatException ne) {
            LOG.log(Level.SEVERE, null, ne);
        }
    }

    public void agregarClasificSueloRural() {
        try {
            if (preClaRu == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a guardar");
                return;
            }
            preClaRu.setFecha(new Date());
            RequestContext.getCurrentInstance().closeDialog(ejb.guardarClasificacionSueloRural(preClaRu, us.getName_user()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void modificarClasificSueloRural() {
        try {
            if (preClaRu == null) {
                JsfUti.messageError(null, "Error", "No se encontro registro a actualizar");
                return;
            }
            preClaRu.setModificado(us.getName_user());
            preClaRu = ejb.guardarClasificacionSueloRural(preClaRu, us.getName_user());
            RequestContext.getCurrentInstance().closeDialog(preClaRu);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    public void selecClasificSueloRural() {
        if (seleccionados != null) {
            RequestContext.getCurrentInstance().closeDialog(seleccionados);
        }
    }
    
    public void cerrar() {
        try {
            RequestContext.getCurrentInstance().closeDialog(null);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Cerrar Dialog", e);
        }
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public CatPredioClasificRural getPreClaRu() {
        return preClaRu;
    }

    public void setPreClaRu(CatPredioClasificRural preClaRu) {
        this.preClaRu = preClaRu;
    }

    public String getIdCatClasiSueloRural() {
        return idCatClasiSueloRural;
    }

    public void setIdCatClasiSueloRural(String idCatClasiSueloRural) {
        this.idCatClasiSueloRural = idCatClasiSueloRural;
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

    public BaseLazyDataModel<CatPredioClasificRural> getClasificaciones() {
        return clasificaciones;
    }

    public void setClasificaciones(BaseLazyDataModel<CatPredioClasificRural> clasificaciones) {
        this.clasificaciones = clasificaciones;
    }

    public List<CatPredioClasificRural> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<CatPredioClasificRural> seleccionados) {
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

}
