/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author OrigamiV2
 */
public class PrediosManzanaDTO implements Serializable {

    private Integer id;
    private Short parroquia = new Short("0");
    private Short mz = new Short("0");
    private Short solar = new Short("0");
    private Short zona = new Short("0");
    private Short sector = new Short("0");
    private BigInteger cantidadsolar;
    private Integer edad;


    public PrediosManzanaDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigInteger getCantidadsolar() {
        return cantidadsolar;
    }

    public void setCantidadsolar(BigInteger cantidadsolar) {
        this.cantidadsolar = cantidadsolar;
    }
    
    public void setCANTIDADSOLAR(BigDecimal CANTIDADSOLAR) {
        this.setCantidadsolar(CANTIDADSOLAR.toBigIntegerExact());
    }

    public Short getParroquia() {
        return parroquia;
    }

    public void setParroquia(Short parroquia) {
        this.parroquia = parroquia;
    }

    public void setPARROQUIA(BigDecimal PARROQUIA) {
        this.setParroquia(PARROQUIA.shortValueExact());
    }

    public Short getMz() {
        return mz;
    }

    public void setMz(Short mz) {
        this.mz = mz;
    }
    
    public void setMZ(BigDecimal MZ) {
        this.setMz(MZ.shortValueExact());
    }

    public Short getSolar() {
        return solar;
    }

    public void setSolar(Short solar) {
        this.solar = solar;
    }

    public void setSOLAR(BigDecimal SOLAR) {
        this.setSolar(SOLAR.shortValueExact());
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public void setZONA(BigDecimal ZONA) {
        this.setZona(ZONA.shortValueExact());
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public void setSECTOR(BigDecimal SECTOR) {
        this.setSector(SECTOR.shortValueExact());
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }
    
    public void setEDAD(BigDecimal EDAD) {
        this.setEdad(EDAD.intValue());
    }
    
    @Override
    public String toString() {
        return "PrediosManzanaDTO{" + "parroquia=" + parroquia + ", zona=" + zona +  ", sector=" + sector +", mz=" + mz + ", solar=" + solar   + ", cantidadSolar=" + cantidadsolar + '}';
    }

}
