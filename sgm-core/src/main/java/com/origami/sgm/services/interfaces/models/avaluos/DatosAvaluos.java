/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.models.avaluos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosAvaluos implements Serializable {

    private BigInteger version;
    private Long id;
    private Integer periodo;
    private BigInteger numPredio;
    private String claveCat;
    private Boolean matriz;
    private BigInteger numMatriz;
    private BigDecimal areaSolar;
    private BigDecimal areaTipo;
    private BigDecimal frente;
    private BigDecimal frenteTipo;
    private Integer servicios;
    private BigInteger subsector;
    private BigDecimal valorM2;
    private BigDecimal alfa;
    private BigDecimal beta;
    private BigDecimal y;
    private BigDecimal factorExp;
    private BigDecimal factorFrente;
    private BigDecimal factorGeometrico;
    private BigDecimal factorServicios;
    private BigDecimal factorCorrelacion;
    private BigDecimal alicuota;
    private BigDecimal avaluoSolar;
    private BigDecimal avaluoEdificacion;
    private BigDecimal avaluoMunicipal;
    private BigDecimal avaluoMunicipalMatriz;

    public DatosAvaluos() {
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public String getClaveCat() {
        return claveCat;
    }

    public void setClaveCat(String claveCat) {
        this.claveCat = claveCat;
    }

    public Boolean getMatriz() {
        return matriz;
    }

    public void setMatriz(Boolean matriz) {
        this.matriz = matriz;
    }

    public BigInteger getNumMatriz() {
        return numMatriz;
    }

    public void setNumMatriz(BigInteger numMatriz) {
        this.numMatriz = numMatriz;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAreaTipo() {
        return areaTipo;
    }

    public void setAreaTipo(BigDecimal areaTipo) {
        this.areaTipo = areaTipo;
    }

    public BigDecimal getFrente() {
        return frente;
    }

    public void setFrente(BigDecimal frente) {
        this.frente = frente;
    }

    public BigDecimal getFrenteTipo() {
        return frenteTipo;
    }

    public void setFrenteTipo(BigDecimal frenteTipo) {
        this.frenteTipo = frenteTipo;
    }

    public Integer getServicios() {
        return servicios;
    }

    public void setServicios(Integer servicios) {
        this.servicios = servicios;
    }

    public BigInteger getSubsector() {
        return subsector;
    }

    public void setSubsector(BigInteger subsector) {
        this.subsector = subsector;
    }

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public BigDecimal getAlfa() {
        return alfa;
    }

    public void setAlfa(BigDecimal alfa) {
        this.alfa = alfa;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public BigDecimal getFactorExp() {
        return factorExp;
    }

    public void setFactorExp(BigDecimal factorExp) {
        this.factorExp = factorExp;
    }

    public BigDecimal getFactorFrente() {
        return factorFrente;
    }

    public void setFactorFrente(BigDecimal factorFrente) {
        this.factorFrente = factorFrente;
    }

    public BigDecimal getFactorGeometrico() {
        return factorGeometrico;
    }

    public void setFactorGeometrico(BigDecimal factorGeometrico) {
        this.factorGeometrico = factorGeometrico;
    }

    public BigDecimal getFactorServicios() {
        return factorServicios;
    }

    public void setFactorServicios(BigDecimal factorServicios) {
        this.factorServicios = factorServicios;
    }

    public BigDecimal getFactorCorrelacion() {
        return factorCorrelacion;
    }

    public void setFactorCorrelacion(BigDecimal FactorCorrelacion) {
        this.factorCorrelacion = FactorCorrelacion;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoEdificacion() {
        return avaluoEdificacion;
    }

    public void setAvaluoEdificacion(BigDecimal avaluoEdificacion) {
        this.avaluoEdificacion = avaluoEdificacion;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public BigDecimal getAvaluoMunicipalMatriz() {
        return avaluoMunicipalMatriz;
    }

    public void setAvaluoMunicipalMatriz(BigDecimal avaluoMunicipalMatriz) {
        this.avaluoMunicipalMatriz = avaluoMunicipalMatriz;
    }

}
