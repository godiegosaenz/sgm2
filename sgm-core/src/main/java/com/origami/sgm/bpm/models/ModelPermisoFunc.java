/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Angel Navarro
 * @date 28/10/2016
 */
public class ModelPermisoFunc implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Boolean primeraVez = false;
    private Integer numTrabajadores;
    private Long claseLocal;
    private Long afiliacionCamara;
    private Long idLocal;
    private Long contador;
    // Rotulos
    private Boolean turismo;
    private Boolean tasaHabilitacion;
    private Boolean rotulos;
    private String tipoRotulo = "rotulo";
    private BigDecimal mtrsRotulo;
    // Balance Patente
    private Boolean patente;
    private BigDecimal capital;
    private Integer anioBalance;
    private Date fechaBalance;
    // Activos Totales
    private Boolean activos;
    private BigDecimal activoTotal;
    private BigDecimal pasivoTotal;
    private BigDecimal pasivoContingente;
    private BigDecimal porcentajeIngreso;

    public ModelPermisoFunc() {
    }

    public ModelPermisoFunc(Long id, Integer numTrabajadores, Long claseLocal, Long afiliacionCamara, Boolean turismo, Boolean tasaHabilitacion, Boolean rotulos, BigDecimal mtrsRotulo, Boolean patente, BigDecimal capital, Integer anioBalance, Date fechaBalance, Boolean activos, BigDecimal activoTotal, BigDecimal pasivoTotal, BigDecimal pasivoContingente, BigDecimal porcentajeIngreso) {
        this.id = id;
        this.numTrabajadores = numTrabajadores;
        this.claseLocal = claseLocal;
        this.afiliacionCamara = afiliacionCamara;
        this.turismo = turismo;
        this.tasaHabilitacion = tasaHabilitacion;
        this.rotulos = rotulos;
        this.mtrsRotulo = mtrsRotulo;
        this.patente = patente;
        this.capital = capital;
        this.anioBalance = anioBalance;
        this.fechaBalance = fechaBalance;
        this.activos = activos;
        this.activoTotal = activoTotal;
        this.pasivoTotal = pasivoTotal;
        this.pasivoContingente = pasivoContingente;
        this.porcentajeIngreso = porcentajeIngreso;
    }

    public Boolean getPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(Boolean primeraVez) {
        this.primeraVez = primeraVez;
    }

    public Integer getNumTrabajadores() {
        return numTrabajadores;
    }

    public void setNumTrabajadores(Integer numTrabajadores) {
        this.numTrabajadores = numTrabajadores;
    }

    public Long getClaseLocal() {
        return claseLocal;
    }

    public void setClaseLocal(Long claseLocal) {
        this.claseLocal = claseLocal;
    }

    public Long getAfiliacionCamara() {
        return afiliacionCamara;
    }

    public void setAfiliacionCamara(Long afiliacionCamara) {
        this.afiliacionCamara = afiliacionCamara;
    }

    public Boolean getTurismo() {
        return turismo;
    }

    public void setTurismo(Boolean turismo) {
        this.turismo = turismo;
    }

    public Boolean getTasaHabilitacion() {
        return tasaHabilitacion;
    }

    public void setTasaHabilitacion(Boolean tasaHabilitacion) {
        this.tasaHabilitacion = tasaHabilitacion;
    }

    public Boolean getRotulos() {
        return rotulos;
    }

    public void setRotulos(Boolean rotulos) {
        this.rotulos = rotulos;
    }

    public String getTipoRotulo() {
        return tipoRotulo;
    }

    public void setTipoRotulo(String tipoRotulo) {
        this.tipoRotulo = tipoRotulo;
    }

    public BigDecimal getMtrsRotulo() {
        return mtrsRotulo;
    }

    public void setMtrsRotulo(BigDecimal mtrsRotulo) {
        this.mtrsRotulo = mtrsRotulo;
    }

    public Boolean getPatente() {
        return patente;
    }

    public void setPatente(Boolean patente) {
        this.patente = patente;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public Integer getAnioBalance() {
        return anioBalance;
    }

    public void setAnioBalance(Integer anioBalance) {
        this.anioBalance = anioBalance;
    }

    public Date getFechaBalance() {
        return fechaBalance;
    }

    public void setFechaBalance(Date fechaBalance) {
        this.fechaBalance = fechaBalance;
    }

    public Boolean getActivos() {
        return activos;
    }

    public void setActivos(Boolean activos) {
        this.activos = activos;
    }

    public BigDecimal getActivoTotal() {
        return activoTotal;
    }

    public void setActivoTotal(BigDecimal activoTotal) {
        this.activoTotal = activoTotal;
    }

    public BigDecimal getPasivoTotal() {
        return pasivoTotal;
    }

    public void setPasivoTotal(BigDecimal pasivoTotal) {
        this.pasivoTotal = pasivoTotal;
    }

    public BigDecimal getPasivoContingente() {
        return pasivoContingente;
    }

    public void setPasivoContingente(BigDecimal pasivoContingente) {
        this.pasivoContingente = pasivoContingente;
    }

    public BigDecimal getPorcentajeIngreso() {
        return porcentajeIngreso;
    }

    public void setPorcentajeIngreso(BigDecimal porcentajeIngreso) {
        this.porcentajeIngreso = porcentajeIngreso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public Long getContador() {
        return contador;
    }

    public void setContador(Long contador) {
        this.contador = contador;
    }

    @Override
    public String toString() {
        return ModelPermisoFunc.class.getPackage().getName() + ModelPermisoFunc.class.getSimpleName() + "[ " + id + " ]";
    }

    
}
