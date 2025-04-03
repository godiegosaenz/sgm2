/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.predio.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author CarlosLoorVargas
 */
public class AvaluosModel implements Serializable {

    private static final Long serialVersionUID = 1L;
    private BigInteger idpredio;
    private BigInteger numpredio;
    private BigInteger liquidacion;
    private Integer periodo;
    private Integer numVersion;
    private BigDecimal areatotal;
    private BigDecimal avaluosolar;
    private BigDecimal avaluoconstruccion;
    private BigDecimal avaluomunicipal;
    private BigDecimal ipliq;
    private BigDecimal descuentoliq;
    private BigDecimal mejorasliq;
    private BigDecimal solnedifliq;
    private BigDecimal emisionliq;
    private BigDecimal bomberosliq;
    private BigDecimal tasamantliq;
    private BigDecimal areaTotalCalc;
    private BigDecimal avaluoSolarCalc;
    private BigDecimal avaluoEdifCalc;
    private BigDecimal avaluoMunicipalCalc;
    private BigDecimal ipCalc;
    private BigDecimal solarEdifCalc;
    private BigDecimal emisionCalc;
    private BigDecimal bomberosCalc;
    private BigDecimal tasaMantCalc;

    public AvaluosModel() {
    }

    public BigInteger getIdpredio() {
        return idpredio;
    }

    public void setIdpredio(BigInteger idpredio) {
        this.idpredio = idpredio;
    }

    public BigInteger getNumpredio() {
        return numpredio;
    }

    public void setNumpredio(BigInteger numpredio) {
        this.numpredio = numpredio;
    }

    public BigInteger getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(BigInteger liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(Integer numVersion) {
        this.numVersion = numVersion;
    }

    public BigDecimal getAreatotal() {
        return areatotal;
    }

    public void setAreatotal(BigDecimal areatotal) {
        this.areatotal = areatotal;
    }

    public BigDecimal getAvaluosolar() {
        return avaluosolar;
    }

    public void setAvaluosolar(BigDecimal avaluosolar) {
        this.avaluosolar = avaluosolar;
    }

    public BigDecimal getAvaluoconstruccion() {
        return avaluoconstruccion;
    }

    public void setAvaluoconstruccion(BigDecimal avaluoconstruccion) {
        this.avaluoconstruccion = avaluoconstruccion;
    }

    public BigDecimal getAvaluomunicipal() {
        return avaluomunicipal;
    }

    public void setAvaluomunicipal(BigDecimal avaluomunicipal) {
        this.avaluomunicipal = avaluomunicipal;
    }

    public BigDecimal getIpliq() {
        return ipliq;
    }

    public void setIpliq(BigDecimal ipliq) {
        this.ipliq = ipliq;
    }

    public BigDecimal getDescuentoliq() {
        return descuentoliq;
    }

    public void setDescuentoliq(BigDecimal descuentoliq) {
        this.descuentoliq = descuentoliq;
    }

    public BigDecimal getMejorasliq() {
        return mejorasliq;
    }

    public void setMejorasliq(BigDecimal mejorasliq) {
        this.mejorasliq = mejorasliq;
    }

    public BigDecimal getSolnedifliq() {
        return solnedifliq;
    }

    public void setSolnedifliq(BigDecimal solnedifliq) {
        this.solnedifliq = solnedifliq;
    }

    public BigDecimal getEmisionliq() {
        return emisionliq;
    }

    public void setEmisionliq(BigDecimal emisionliq) {
        this.emisionliq = emisionliq;
    }

    public BigDecimal getBomberosliq() {
        return bomberosliq;
    }

    public void setBomberosliq(BigDecimal bomberosliq) {
        this.bomberosliq = bomberosliq;
    }

    public BigDecimal getTasamantliq() {
        return tasamantliq;
    }

    public void setTasamantliq(BigDecimal tasamantliq) {
        this.tasamantliq = tasamantliq;
    }

    public BigDecimal getAreaTotalCalc() {
        return areaTotalCalc;
    }

    public void setAreaTotalCalc(BigDecimal areaTotalCalc) {
        this.areaTotalCalc = areaTotalCalc;
    }

    public BigDecimal getAvaluoSolarCalc() {
        return avaluoSolarCalc;
    }

    public void setAvaluoSolarCalc(BigDecimal avaluoSolarCalc) {
        this.avaluoSolarCalc = avaluoSolarCalc;
    }

    public BigDecimal getAvaluoEdifCalc() {
        return avaluoEdifCalc;
    }

    public void setAvaluoEdifCalc(BigDecimal avaluoEdifCalc) {
        this.avaluoEdifCalc = avaluoEdifCalc;
    }
    

    public BigDecimal getAvaluoMunicipalCalc() {
        return avaluoMunicipalCalc;
    }

    public void setAvaluoMunicipalCalc(BigDecimal avaluoMunicipalCalc) {
        this.avaluoMunicipalCalc = avaluoMunicipalCalc;
    }

    public BigDecimal getIpCalc() {
        return ipCalc;
    }

    public void setIpCalc(BigDecimal ipCalc) {
        this.ipCalc = ipCalc;
    }

    public BigDecimal getSolarEdifCalc() {
        return solarEdifCalc;
    }

    public void setSolarEdifCalc(BigDecimal solarEdifCalc) {
        this.solarEdifCalc = solarEdifCalc;
    }

    public BigDecimal getEmisionCalc() {
        return emisionCalc;
    }

    public void setEmisionCalc(BigDecimal emisionCalc) {
        this.emisionCalc = emisionCalc;
    }

    public BigDecimal getBomberosCalc() {
        return bomberosCalc;
    }

    public void setBomberosCalc(BigDecimal bomberosCalc) {
        this.bomberosCalc = bomberosCalc;
    }

    public BigDecimal getTasaMantCalc() {
        return tasaMantCalc;
    }

    public void setTasaMantCalc(BigDecimal tasaMantCalc) {
        this.tasaMantCalc = tasaMantCalc;
    }
    

}
