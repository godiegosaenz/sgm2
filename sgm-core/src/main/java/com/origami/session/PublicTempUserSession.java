/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.session;

import com.origami.sgm.database.Querys;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import util.JsfUti;

/**
 *
 * @author Fernando
 */
@Named
@SessionScoped
public class PublicTempUserSession implements Serializable {

    protected String urlSolicitada;
    protected String perfil = "/image/perfiles/avatar2.jpg";

    public Boolean isLogged() {
        return false;
    }

    public void login(Long id) {
//        usuario = (RpubUsuario) acl.find(Querys.getRpubUsuarioById, new String[]{"id"}, new Object[]{id});
    }

    public Boolean login(String username, String password) {
        /*RpubUsuario usuario1 = null;
        if (username != null && password != null) {
            usuario1 = (RpubUsuario) acl.find(Querys.getRpubUsusariobyUserPass, new String[]{"user", "pass"}, new Object[]{username, password});
        }

        if (usuario1 != null) {
            this.usuario = usuario1;
            if (usuario1.getImagenPerfil() != null) {
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/image/perfiles") + File.separator + usuario1.getId() + ".jpg";
                File f = new File(usuario1.getImagenPerfil());
                if (f.length() != 0L) {
                    try {
                        byte[] resultado = new byte[(int) f.length()];
                        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
                        stream.read(resultado);
                        FileImageOutputStream imageOutput;

                        imageOutput = new FileImageOutputStream(new File(path));
                        imageOutput.write(resultado, 0, resultado.length);
                        imageOutput.flush();
                        imageOutput.close();
                        this.perfil = "/image/perfiles/" + usuario1.getId() + ".jpg";
                    } catch (Exception e) {
                        Logger.getLogger(PublicTempUserSession.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            } else {
                this.perfil = "/image/perfiles/avatar2.jpg";
            }
            JsfUti.update("perfilId");
            return true;
        }*/
        return false;
    }

    public void logout() {
        urlSolicitada = null;
        perfil = "/image/perfiles/avatar2.jpg";
    }

    public void redirectUrlSolicitada() {
        if (this.getUrlSolicitada() != null) {
            try {
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(this.getUrlSolicitada());
                this.setUrlSolicitada(null);
            } catch (IOException ex) {
                Logger.getLogger(PublicTempUserSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(ec.getRequestContextPath() + "/faces/publica/index.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(PublicTempUserSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        urlSolicitada = null;
    }

    public void persistReqUrl() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        String fullURI = servletRequest.getRequestURI();
        if (StringUtil.isNotEmpty(servletRequest.getQueryString())) {
            fullURI = fullURI + "?" + servletRequest.getQueryString();
        }
        this.setUrlSolicitada(fullURI);
    }

    public void redirectInvitado() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        String fullURI = servletRequest.getRequestURI();
        if (fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/publica/index.xhtml")
                || fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/publica/login.xhtml")
                || fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/publica/cliente/registrarCliente.xhtml")
                || fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/publica/cliente/cambioClaveCliente.xhtml")
                || fullURI.equalsIgnoreCase(servletRequest.getContextPath() + "/faces/publica/cliente/recuperarClavePublica.xhtml")) {
            return;
        }

        if (true) {
            this.persistReqUrl();
            try {
                ec.redirect(ec.getRequestContextPath() + "/faces/publica/login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(PublicTempUserSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String getUrlSolicitada() {
        return urlSolicitada;
    }

    public void setUrlSolicitada(String urlSolicitada) {
        this.urlSolicitada = urlSolicitada;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

}
