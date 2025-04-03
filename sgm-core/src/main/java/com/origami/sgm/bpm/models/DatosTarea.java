/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Origami Integrales
 */
public class DatosTarea implements Serializable {

    private String tipoPermiso;
    private String procesoId;
    private String numero_TramiteNuevo;
    private Long idrolDirectora_proceso;
    private String correosElectronicos;
    private String correoUserTecnico;
    private String correoDigitalizador;
    private String emaildirector;
    private String observacionRequerimiento;
    private String nomDepartamento;
    private String nombSolicitante;
    private String liquidador;
    private String num_identidad;
    private String nombreCiudadela;
    private String mz;
    private String solar;
    private String nombreSolicitante;
    private BigDecimal avaluo;
    private String telefSolicitante;
    private String emailSolicitante;
    private BigDecimal tasaCatastro;
    private Long idTramite;
    private Long numPredio;
    private String tramitesIngresado;
    protected BigDecimal totalLiquidado = BigDecimal.ZERO;
    protected Integer opIniciarTramite;
    private static final Long serialVersionUID = 1L;

    public Integer getOpIniciarTramite() {
        return opIniciarTramite;
    }

    public void setOpIniciarTramite(Integer opIniciarTramite) {
        this.opIniciarTramite = opIniciarTramite;
    }

    public Long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Long numPredio) {
        this.numPredio = numPredio;
    }

    public BigDecimal getTotalLiquidado() {
        return totalLiquidado;
    }

    public void setTotalLiquidado(BigDecimal totalLiquidado) {
        this.totalLiquidado = totalLiquidado;
    }

    public String getTramitesIngresado() {
        return tramitesIngresado;
    }

    public void setTramitesIngresado(String tramitesIngresado) {
        this.tramitesIngresado = tramitesIngresado;
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public String getNomDepartamento() {
        return nomDepartamento;
    }

    public void setNomDepartamento(String nomDepartamento) {
        this.nomDepartamento = nomDepartamento;
    }

    public String getNombSolicitante() {
        return nombSolicitante;
    }

    public void setNombSolicitante(String nombSolicitante) {
        this.nombSolicitante = nombSolicitante;
    }

    public String getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(String tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public String getObservacionRequerimiento() {
        return observacionRequerimiento;
    }

    public void setObservacionRequerimiento(String observacionRequerimiento) {
        this.observacionRequerimiento = observacionRequerimiento;
    }

    public String getNumero_TramiteNuevo() {
        return numero_TramiteNuevo;
    }

    public void setNumero_TramiteNuevo(String numero_TramiteNuevo) {
        this.numero_TramiteNuevo = numero_TramiteNuevo;
    }

    public String getEmaildirector() {
        return emaildirector;
    }

    public void setEmaildirector(String emaildirector) {
        this.emaildirector = emaildirector;
    }

    public String getCorreoDigitalizador() {
        return correoDigitalizador;
    }

    public void setCorreoDigitalizador(String correoDigitalizador) {
        this.correoDigitalizador = correoDigitalizador;
    }

    public String getCorreoUserTecnico() {
        return correoUserTecnico;
    }

    public void setCorreoUserTecnico(String correoUserTecnico) {
        this.correoUserTecnico = correoUserTecnico;
    }

    public String getCorreosElectronicos() {
        return correosElectronicos;
    }

    public void setCorreosElectronicos(String correosElectronicos) {
        this.correosElectronicos = correosElectronicos;
    }

    public String getProcesoId() {
        return procesoId;
    }

    public void setProcesoId(String procesoId) {
        this.procesoId = procesoId;
    }

    public Long getIdrolDirectora_proceso() {
        return idrolDirectora_proceso;
    }

    public void setIdrolDirectora_proceso(Long idrolDirectora_proceso) {
        this.idrolDirectora_proceso = idrolDirectora_proceso;
    }

    public String getLiquidador() {
        return liquidador;
    }

    public void setLiquidador(String liquidador) {
        this.liquidador = liquidador;
    }

    public String getNum_identidad() {
        return num_identidad;
    }

    public void setNum_identidad(String num_identidad) {
        this.num_identidad = num_identidad;
    }

    public String getNombreCiudadela() {
        return nombreCiudadela;
    }

    public void setNombreCiudadela(String nombreCiudadela) {
        this.nombreCiudadela = nombreCiudadela;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public String getSolar() {
        return solar;
    }

    public void setSolar(String solar) {
        this.solar = solar;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public BigDecimal getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(BigDecimal avaluo) {
        this.avaluo = avaluo;
    }

    public String getTelefSolicitante() {
        return telefSolicitante;
    }

    public void setTelefSolicitante(String telefSolicitante) {
        this.telefSolicitante = telefSolicitante;
    }

    public String getEmailSolicitante() {
        return emailSolicitante;
    }

    public void setEmailSolicitante(String emailSolicitante) {
        this.emailSolicitante = emailSolicitante;
    }

    public BigDecimal getTasaCatastro() {
        return tasaCatastro;
    }

    public void setTasaCatastro(BigDecimal tasaCatastro) {
        this.tasaCatastro = tasaCatastro;
    }
}
