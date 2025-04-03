/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.registro;

import com.origami.config.SisVars;
import com.origami.session.ServletSession;
import com.origami.session.UserSession;
import com.origami.sgm.entities.CatTransferenciaDominio;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.JsfUti;

/**
 *
 * @author CarlosLoorVargas
 */
@Named
@ViewScoped
public class HistTransfDom implements Serializable {

    private static final Long serialVersionUID = 1L;
    protected BaseLazyDataModel<CatTransferenciaDominio> transferencias;
    @Inject
    protected ServletSession servletSession;
    @Inject
    protected UserSession session;

    @PostConstruct
    protected void init() {
        transferencias = new BaseLazyDataModel<>(CatTransferenciaDominio.class);
    }

    public void imprimir(Long id) {
        servletSession.instanciarParametros();
        servletSession.setNombreReporte("transferenciaDominio");
        servletSession.setNombreSubCarpeta("registroPropiedad");
        servletSession.setTieneDatasource(true);
        servletSession.agregarParametro("LOGO", JsfUti.getRealPath(SisVars.logoReportes));
        servletSession.agregarParametro("SUBREPORT_DIR", JsfUti.getRealPath("/") + "reportes/registroPropiedad/");
        servletSession.agregarParametro("USUARIO", session.getName_user());
        servletSession.agregarParametro("IDTRANSFERENCIA", id);
        JsfUti.redirectNewTab(com.origami.config.SisVars.urlbase + "Documento");
    }

    public BaseLazyDataModel<CatTransferenciaDominio> getTransferencias() {
        return transferencias;
    }

    public void setTransferencias(BaseLazyDataModel<CatTransferenciaDominio> transferencias) {
        this.transferencias = transferencias;
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
