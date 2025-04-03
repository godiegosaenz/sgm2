/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.service.ordenes.model;

import java.io.Serializable;

/**
 *
 * @author Fernando
 */
public class ClaveLegacy implements Serializable{
    
    private Short zona;
    private Short sector;
    private Short mz;
    private Short solar;
    private Short div;

    public ClaveLegacy() {
    }

    public ClaveLegacy(Short zona, Short sector, Short mz, Short solar, Short div) {
        this.zona = zona;
        this.sector = sector;
        this.mz = mz;
        this.solar = solar;
        this.div = div;
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public Short getMz() {
        return mz;
    }

    public void setMz(Short mz) {
        this.mz = mz;
    }

    public Short getSolar() {
        return solar;
    }

    public void setSolar(Short solar) {
        this.solar = solar;
    }

    public Short getDiv() {
        return div;
    }

    public void setDiv(Short div) {
        this.div = div;
    }
    
    
    
}
