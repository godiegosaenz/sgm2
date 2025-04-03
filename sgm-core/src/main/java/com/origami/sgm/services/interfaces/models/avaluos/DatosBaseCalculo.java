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
public class DatosBaseCalculo implements Serializable {

    private static final Long serialVersionUID = 1L;
    private BigInteger id;
    private BigInteger numpredio;
    private String clavecat;
    private BigDecimal areasolar;
    private BigDecimal frente1;
    private Integer servicios;
    private BigInteger subsector;
    private BigDecimal valorm2;
    private BigDecimal areaconstruccion;
    private BigDecimal avaluoedificacion;

    public DatosBaseCalculo() {
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getNumpredio() {
        return numpredio;
    }

    public void setNumpredio(BigInteger numpredio) {
        this.numpredio = numpredio;
    }

    public String getClavecat() {
        return clavecat;
    }

    public void setClavecat(String clavecat) {
        this.clavecat = clavecat;
    }

    public BigDecimal getAreasolar() {
        return areasolar;
    }

    public void setAreasolar(BigDecimal areasolar) {
        this.areasolar = areasolar;
    }

    public BigDecimal getFrente1() {
        return frente1;
    }

    public void setFrente1(BigDecimal frente1) {
        this.frente1 = frente1;
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

    public BigDecimal getValorm2() {
        return valorm2;
    }

    public void setValorm2(BigDecimal valorm2) {
        this.valorm2 = valorm2;
    }

    public BigDecimal getAreaconstruccion() {
        return areaconstruccion;
    }

    public void setAreaconstruccion(BigDecimal areaconstruccion) {
        this.areaconstruccion = areaconstruccion;
    }

    public BigDecimal getAvaluoedificacion() {
        return avaluoedificacion;
    }

    public void setAvaluoedificacion(BigDecimal avaluoedificacion) {
        this.avaluoedificacion = avaluoedificacion;
    }

}
