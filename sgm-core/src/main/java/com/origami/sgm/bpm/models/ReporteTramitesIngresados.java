/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author Pedrock, modificado nombre de campos por Angel Navarro
 */
public class ReporteTramitesIngresados {

    protected BigInteger numTramite;
    protected String numTramitesTrin;
    protected String ciudadela;
    protected String mzsl;
    protected String mztemp;
    protected String sltemp;
    protected String solicitante;
    protected Long tipoTramiteHistTram;
    protected String tipoTramite;
    protected String tipoTramiteNombrePrincipal;
    protected Date fechaInicio;
    protected String estadoTramite;
    protected Boolean tieneValija = false;

    public BigInteger getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(BigInteger numTramite) {
        this.numTramite = numTramite;
    }

    public String getNumTramitesTrin() {
        return numTramitesTrin;
    }

    public void setNumTramitesTrin(String numTramitesTrin) {
        this.numTramitesTrin = numTramitesTrin;
    }

    public String getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(String ciudadela) {
        this.ciudadela = ciudadela;
    }

    public String getMzsl() {
        return mzsl;
    }

    public void setMzsl(String mzsl) {
        this.mzsl = mzsl;
    }

    public String getMztemp() {
        return mztemp;
    }

    public void setMztemp(String mztemp) {
        this.mztemp = mztemp;
    }

    public String getSltemp() {
        return sltemp;
    }

    public void setSltemp(String sltemp) {
        this.sltemp = sltemp;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Long getTipoTramiteHistTram() {
        return tipoTramiteHistTram;
    }

    public void setTipoTramiteHistTram(Long tipoTramiteHistTram) {
        this.tipoTramiteHistTram = tipoTramiteHistTram;
    }

    public String getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public String getTipoTramiteNombrePrincipal() {
        return tipoTramiteNombrePrincipal;
    }

    public void setTipoTramiteNombrePrincipal(String tipoTramiteNombrePrincipal) {
        this.tipoTramiteNombrePrincipal = tipoTramiteNombrePrincipal;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstadoTramite() {
        return estadoTramite;
    }

    public void setEstadoTramite(String estadoTramite) {
        this.estadoTramite = estadoTramite;
    }

    public Boolean getTieneValija() {
        return tieneValija;
    }

    public void setTieneValija(Boolean tieneValija) {
        this.tieneValija = tieneValija;
    }

}
