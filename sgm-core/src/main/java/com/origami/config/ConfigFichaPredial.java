/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.config;

import java.io.Serializable;

/**
 *
 * @author OrigamiV2
 */
public class ConfigFichaPredial implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2522569892223642028L;
    private Boolean renderDialogUploadDocument = Boolean.FALSE;
    private Boolean mostrarPorcentajeEdificacion = Boolean.FALSE;
    private Boolean redenerFichaIb = Boolean.FALSE;
    private Boolean camposAdicionales = Boolean.FALSE;
    private Boolean aplicarExoneracionPredioRural = Boolean.FALSE;
    //LAS FOTOS DE LAS LIBERTAD SE LA OBTIENE POR MEDIO DE UN REST SERVICES
    //VARIABLE TAMBIEN ES UTILIZADA PARA EL CAMPO SECTOR
    private Boolean libertad = Boolean.FALSE;
    
    //IBARRA DEBE TENER FALSE :)
    private Boolean areaConstruccion = Boolean.TRUE;
    
    private Boolean renderSanVicente = Boolean.TRUE;

    public ConfigFichaPredial() {
    }

    public Boolean getRenderDialogUploadDocument() {
        return renderDialogUploadDocument;
    }

    public void setRenderDialogUploadDocument(Boolean renderDialogUploadDocument) {
        this.renderDialogUploadDocument = renderDialogUploadDocument;
    }

    public Boolean getMostrarPorcentajeEdificacion() {
        return mostrarPorcentajeEdificacion;
    }

    public void setMostrarPorcentajeEdificacion(Boolean mostrarPorcentajeEdificacion) {
        this.mostrarPorcentajeEdificacion = mostrarPorcentajeEdificacion;
    }

    public Boolean getRedenerFichaIb() {
        return redenerFichaIb;
    }

    public void setRedenerFichaIb(Boolean redenerFichaIb) {
        this.redenerFichaIb = redenerFichaIb;
    }

    public Boolean getCamposAdicionales() {
        return camposAdicionales;
    }

    public void setCamposAdicionales(Boolean camposAdicionales) {
        this.camposAdicionales = camposAdicionales;
    }

    public Boolean getLibertad() {
        return libertad;
    }

    public void setLibertad(Boolean libertad) {
        this.libertad = libertad;
    }

    public Boolean getAplicarExoneracionPredioRural() {
        return aplicarExoneracionPredioRural;
    }

    public void setAplicarExoneracionPredioRural(Boolean aplicarExoneracionPredioRural) {
        this.aplicarExoneracionPredioRural = aplicarExoneracionPredioRural;
    }

    public Boolean getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(Boolean areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }


    public Boolean getRenderSanVicente() {
        return renderSanVicente;
    }

    public void setRenderSanVicente(Boolean renderSanVicente) {
        this.renderSanVicente = renderSanVicente;
    }

    
    
}
