/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.sgm.bpm.managedbeans.BpmManageBeanBaseRoot;
import com.origami.sgm.database.Querys;
import com.origami.sgm.entities.RegCertificado;
import com.origami.sgm.entities.RegTipoCertificado;
import com.origami.sgm.lazymodels.RegCertificadoLazy;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Certificados extends BpmManageBeanBaseRoot implements Serializable {

    @Inject
    private ServletSession servletSession;

    protected RegCertificadoLazy certificadoLazy = new RegCertificadoLazy();
    protected RegCertificado certificado = new RegCertificado();

    public void doPreRenderView() {
        if (!JsfUti.isAjaxRequest()) {
            iniView();
        }
    }

    protected void iniView() {
        try {

        } catch (Exception e) {
            Logger.getLogger(Certificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void generarDocumento(RegCertificado cert) {
        try {
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setEncuadernacion(Boolean.TRUE);
            servletSession.setNombreReporte("CertificadoGeneral");
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("ID_CERTIFICADO", cert.getId());
            servletSession.agregarParametro("USER", cert.getUserCreador().getUsuario());
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
            //servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            //servletSession.agregarParametro("IMG_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
        } catch (Exception e) {
            Logger.getLogger(Certificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void certificadoWord(RegCertificado cert) {
        try {
            //Calendar cal = Calendar.getInstance();
            //cal.setTime(cert.getFechaEmision());
            servletSession.instanciarParametros();
            servletSession.setTieneDatasource(true);
            servletSession.setEncuadernacion(Boolean.TRUE);
            servletSession.setNombreReporte("CertificadoWord");
            //servletSession.setNombreDocumento(cert.getNumCertificado() + "-" + cal.get(Calendar.YEAR) + "-" + cert.getSecuencia());
            servletSession.setNombreSubCarpeta("registroPropiedad");
            servletSession.agregarParametro("ID_CERTIFICADO", cert.getId());
            servletSession.agregarParametro("USER", cert.getUserCreador().getUsuario());
            servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
            servletSession.agregarParametro("LOGO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("ESCUDO_URL", JsfUti.getRealPath(SisVars.logoReportes));
            servletSession.agregarParametro("IMG_FOOTER", JsfUti.getRealPath(SisVars.sisLogo1));
            JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/Documento");
            //JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/DocumentoWord");
        } catch (Exception e) {
            Logger.getLogger(Certificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void editarCertificado(RegCertificado cert) {
        certificado = cert;
        JsfUti.update("formEdit");
        JsfUti.executeJS("PF('dlgEditCert').show();");
        //JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/editarCertificado.xhtml?code=" + cert.getId());
    }

    public void modificarCertificado() {
        try {
            if (certificado.getBeneficiario() != null) {
                if (certificado.getObservacion() != null) {
                    certificado.setUserEdicion(session.getUserId());
                    certificado.setFechaEdicion(new Date());
                    acl.persist(certificado);
                    JsfUti.redirectFaces("/vistaprocesos/registroPropiedad/certificados.xhtml");
                } else {
                    JsfUti.messageError(null, "Falta ingresar el contenido del Certificado.", "");
                }
            } else {
                JsfUti.messageError(null, "Falta ingresar el nombre del beneficiario.", "");
            }
        } catch (Exception e) {
            Logger.getLogger(Certificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void descargarDocumento() {
        try {
            RegTipoCertificado t = (RegTipoCertificado) acl.find(Querys.getTipoCertificadoByTipoPlantilla, new String[]{"tipo"}, new Object[]{1});
            if (t != null) {
                JsfUti.redirectNewTab(SisVars.urlServidorPublica + "/DescargarDocsRepositorio?id=" + t.getPlantilla());
            }
        } catch (Exception e) {
            JsfUti.messageError(null, Messages.error, "");
            Logger.getLogger(Certificados.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public RegCertificadoLazy getCertificadoLazy() {
        return certificadoLazy;
    }

    public void setCertificadoLazy(RegCertificadoLazy certificadoLazy) {
        this.certificadoLazy = certificadoLazy;
    }

    public RegCertificado getCertificado() {
        return certificado;
    }

    public void setCertificado(RegCertificado certificado) {
        this.certificado = certificado;
    }

    public ServletSession getServletSession() {
        return servletSession;
    }

    public void setServletSession(ServletSession servletSession) {
        this.servletSession = servletSession;
    }

}
