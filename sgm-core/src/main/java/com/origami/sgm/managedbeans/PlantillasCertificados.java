/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.RegTipoCertificado;
import com.origami.sgm.lazymodels.RegTipoCertificadoLazy;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import util.JsfUti;
import util.Messages;

/**
 *
 * @author Anyelo
 */
@Named
@ViewScoped
public class PlantillasCertificados implements Serializable {

    public static final Long serialVersionUID = 1L;

    @Inject
    private ServletSession servletSession;

    @Inject
    protected UserSession session;

    @javax.inject.Inject
    private Entitymanager acl;
    
    protected RegTipoCertificadoLazy lazyTipoCerts;
    protected RegTipoCertificado certificado = new RegTipoCertificado();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    public void showDlgEdit(RegTipoCertificado t) {
        certificado = t;
        JsfUti.update("formEdit");
        JsfUti.executeJS("PF('dlgEditCert').show();");
    }

    public void showDlgNew() {
        certificado = new RegTipoCertificado();
        JsfUti.update("formEdit");
        JsfUti.executeJS("PF('dlgEditCert').show();");
    }

    public void pruebaCertificado(RegTipoCertificado t) {
        this.cargarDatos(t.getId());
        JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
    }

    public void cargarDatos(Long id) {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setNombreReporte("ReportePruebaSamborondon");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("ID_CERTIFICADO", id);
        } catch (Exception e) {
            Logger.getLogger(PlantillasCertificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void guardarCertificado() {
        try {
            if (this.validacion()) {
                certificado.setNombreCertificado(certificado.getNombreCertificado().toUpperCase());
                if (certificado.getId() == null) {
                    certificado.setFechaCreacion(new Date());
                    certificado.setUserCreador(session.getName_user());
                    acl.persist(certificado);
                } else {
                    certificado.setFechaModificacion(new Date());
                    certificado.setUserModificacion(session.getName_user());
                    acl.update(certificado);
                }
                JsfUti.redirectFaces("/admin/registro/plantillasCertificados.xhtml");
            } else {
                JsfUti.messageWarning(null, Messages.campoVacio, "");
            }

        } catch (Exception e) {
            Logger.getLogger(PlantillasCertificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    protected Boolean validacion() {
        if (certificado.getNombreCertificado() == null) {
            return false;
        } else if (certificado.getPlantilla() == null) {
            return false;
        }
        return true;
    }

    protected void iniView() {
        lazyTipoCerts = new RegTipoCertificadoLazy(Boolean.TRUE);
    }

    public RegTipoCertificadoLazy getLazyTipoCerts() {
        return lazyTipoCerts;
    }

    public void setLazyTipoCerts(RegTipoCertificadoLazy lazyTipoCerts) {
        this.lazyTipoCerts = lazyTipoCerts;
    }

    public RegTipoCertificado getCertificado() {
        return certificado;
    }

    public void setCertificado(RegTipoCertificado certificado) {
        this.certificado = certificado;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

}
