/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.managedbeans.component;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author dfcalderio
 */
@Named(value = "propietariosDefaultView")
@ApplicationScoped
public class PropietariosDefaultSeccion implements Serializable {

    protected Boolean renderInfoEnte;
    protected String secDatosPersonales;
    protected String secTransferenciaDominio;

    @PostConstruct
    public void init() {
        defaultValues();
    }

    public void defaultValues() {
        renderInfoEnte = Boolean.TRUE;
        secDatosPersonales = "_datosPersonales.xhtml";
        secTransferenciaDominio = "_nuevosPropietarios.xhtml";
    }

    public Boolean getRenderInfoEnte() {
        return renderInfoEnte;
    }

    public void setRenderInfoEnte(Boolean renderInfoEnte) {
        this.renderInfoEnte = renderInfoEnte;
    }

    public String getSecDatosPersonales() {
        return secDatosPersonales;
    }

    public void setSecDatosPersonales(String secDatosPersonales) {
        this.secDatosPersonales = secDatosPersonales;
    }

    public String getSecTransferenciaDominio() {
        return secTransferenciaDominio;
    }

    public void setSecTransferenciaDominio(String secTransferenciaDominio) {
        this.secTransferenciaDominio = secTransferenciaDominio;
    }

}
