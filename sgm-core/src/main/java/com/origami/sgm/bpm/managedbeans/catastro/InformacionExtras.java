/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author dfcalderio
 */
@Named(value = "infoExtrasView")
@ApplicationScoped
public class InformacionExtras implements Serializable {

    private String labelEscrituraNoRegistro;

    private String viewCatPredioPropitarioExtra;
    private boolean renderviewCatPredioPropitarioExtra;

    private boolean renderFechasResponsables;

    private String labelBloqueAnioConstruccion;
    private boolean renderBloqueAnioFinal;

    @PostConstruct
    public void init() {
        defaultValues();
    }

    public void defaultValues() {

        labelEscrituraNoRegistro = "(*) No. Registro";
        viewCatPredioPropitarioExtra = "propietariosExtras.xhtml";
        renderviewCatPredioPropitarioExtra = false;
        renderFechasResponsables = false;
        labelBloqueAnioConstruccion = "(*) Año Construcción";
        renderBloqueAnioFinal = false;
    }

    public String getLabelEscrituraNoRegistro() {
        return labelEscrituraNoRegistro;
    }

    public void setLabelEscrituraNoRegistro(String labelEscrituraNoRegistro) {
        this.labelEscrituraNoRegistro = labelEscrituraNoRegistro;
    }

    public String getViewCatPredioPropitarioExtra() {
        return viewCatPredioPropitarioExtra;
    }

    public void setViewCatPredioPropitarioExtra(String viewCatPredioPropitarioExtra) {
        this.viewCatPredioPropitarioExtra = viewCatPredioPropitarioExtra;
    }

    public boolean isRenderviewCatPredioPropitarioExtra() {
        return renderviewCatPredioPropitarioExtra;
    }

    public void setRenderviewCatPredioPropitarioExtra(boolean renderviewCatPredioPropitarioExtra) {
        this.renderviewCatPredioPropitarioExtra = renderviewCatPredioPropitarioExtra;
    }

    public boolean isRenderFechasResponsables() {
        return renderFechasResponsables;
    }

    public void setRenderFechasResponsables(boolean renderFechasResponsables) {
        this.renderFechasResponsables = renderFechasResponsables;
    }

    public String getLabelBloqueAnioConstruccion() {
        return labelBloqueAnioConstruccion;
    }

    public void setLabelBloqueAnioConstruccion(String labelBloqueAnioConstruccion) {
        this.labelBloqueAnioConstruccion = labelBloqueAnioConstruccion;
    }

    public boolean isRenderBloqueAnioFinal() {
        return renderBloqueAnioFinal;
    }

    public void setRenderBloqueAnioFinal(boolean renderBloqueAnioFinal) {
        this.renderBloqueAnioFinal = renderBloqueAnioFinal;
    }

}
