/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.rentas;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.entities.FnRemisionSolicitud;
import com.origami.sgm.services.interfaces.financiero.RemisionInteresServices;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import util.Faces;
import util.JsfUti;

/**
 *
 * @author origami-idea
 */
@Named(value = "remisionIntereses")
@ViewScoped
public class RemisionIntereses implements Serializable {

    private static final Logger LOG = Logger.getLogger(RemisionIntereses.class.getName());

    @javax.inject.Inject
    protected RemisionInteresServices remisionInteresServices;

    @Inject
    private Entitymanager manager;

    private String fnRemisionSolicitudId;

    private FnRemisionSolicitud fnRemisionSolicitud;

    @Inject
    private ServletSession ss;

    @PostConstruct
    public void initView() {
        if (fnRemisionSolicitudId != null) {
            fnRemisionSolicitud = manager.find(FnRemisionSolicitud.class, Long.parseLong(fnRemisionSolicitudId));
        }
    }

    public void saveRemision() {
        FnRemisionSolicitud frs = remisionInteresServices.aprobarSolicitud(fnRemisionSolicitud);
        if (frs != null) {
            if (frs.getId() != null) {
                generarReimisionInteres(frs);
                JsfUti.messageInfo(null, "Info", "Datos Guardados Correctamente");
                RequestContext.getCurrentInstance().closeDialog(frs);
            } else {
                JsfUti.messageError(null, "Info", "Ocurrio un Problema Mientras Se persistian los datos");
            }
        } else {
            JsfUti.messageError(null, "Info", "Ocurrio un Problema Mientras Se persistian los datos");
        }
    }

    public void generarReimisionInteres(FnRemisionSolicitud frs) {
        try {
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            ss.borrarDatos();
            ss.borrarParametros();
            ss.instanciarParametros();
            ss.setTieneDatasource(Boolean.TRUE);
            ss.setNombreSubCarpeta("Financiero/ReimisionInteres");
            ss.agregarParametro("LOGO", Faces.getRealPath("/").concat(SisVars.sisLogo));

            ss.agregarParametro("ID_SOLICITUD", frs.getId());
            ss.setNombreReporte("sReporteReimision");

            JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
        } catch (Exception e) {
            Logger.getLogger(RemisionIntereses.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void cancelarRemision() {
        FnRemisionSolicitud frs = remisionInteresServices.aprobarSolicitud(fnRemisionSolicitud);
        if (frs != null) {
            if (frs.getId() != null) {
                JsfUti.messageInfo(null, "Info", "Datos Guardados Correctamente");
                RequestContext.getCurrentInstance().closeDialog(frs);
            } else {
                JsfUti.messageError(null, "Info", "Ocurrio un Problema Mientras Se persistian los datos");
            }
        } else {
            JsfUti.messageError(null, "Info", "Ocurrio un Problema Mientras Se persistian los datos");
        }
    }

    public FnRemisionSolicitud getFnRemisionSolicitud() {
        return fnRemisionSolicitud;
    }

    public void setFnRemisionSolicitud(FnRemisionSolicitud fnRemisionSolicitud) {
        this.fnRemisionSolicitud = fnRemisionSolicitud;
    }

    public String getFnRemisionSolicitudId() {
        return fnRemisionSolicitudId;
    }

    public void setFnRemisionSolicitudId(String fnRemisionSolicitudId) {
        this.fnRemisionSolicitudId = fnRemisionSolicitudId;
    }

}
