/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.util;

/**
 *
 * @author dfcalderio
 */
public class OpcionesBusquedaPredio {

    private short provinciaCod;
    private short cantonCod;
    private short parroquiaCod;
    private short zonaCod;
    private short sectorCod;
    private short manzanaCod;
    private short solarCod;
    private short bloqueCod;
    private short pisoCod;
    private short unidadCod;

    private String palabraClave;

    private boolean buscando;
    private boolean ejecutandoAccion;

    public OpcionesBusquedaPredio() {
        buscando = false;
        ejecutandoAccion = false;
    }

    public short getProvinciaCod() {
        return provinciaCod;
    }

    public void setProvinciaCod(short provinciaCod) {
        this.provinciaCod = provinciaCod;
    }

    public short getCantonCod() {
        return cantonCod;
    }

    public void setCantonCod(short cantonCod) {
        this.cantonCod = cantonCod;
    }

    public short getParroquiaCod() {
        return parroquiaCod;
    }

    public void setParroquiaCod(short parroquiaCod) {
        this.parroquiaCod = parroquiaCod;
    }

    public short getZonaCod() {
        return zonaCod;
    }

    public void setZonaCod(short zonaCod) {
        this.zonaCod = zonaCod;
    }

    public short getSectorCod() {
        return sectorCod;
    }

    public void setSectorCod(short sectorCod) {
        this.sectorCod = sectorCod;
    }

    public short getManzanaCod() {
        return manzanaCod;
    }

    public void setManzanaCod(short manzanaCod) {
        this.manzanaCod = manzanaCod;
    }

    public short getSolarCod() {
        return solarCod;
    }

    public void setSolarCod(short solarCod) {
        this.solarCod = solarCod;
    }

    public short getBloqueCod() {
        return bloqueCod;
    }

    public void setBloqueCod(short bloqueCod) {
        this.bloqueCod = bloqueCod;
    }

    public short getPisoCod() {
        return pisoCod;
    }

    public void setPisoCod(short pisoCod) {
        this.pisoCod = pisoCod;
    }

    public short getUnidadCod() {
        return unidadCod;
    }

    public void setUnidadCod(short unidadCod) {
        this.unidadCod = unidadCod;
    }

    public String getPalabraClave() {
        return palabraClave;
    }

    public void setPalabraClave(String palabraClave) {
        this.palabraClave = palabraClave;
    }

    public boolean isBuscando() {
        return buscando;
    }

    public void setBuscando(boolean buscando) {
        this.buscando = buscando;
    }

    public boolean isEjecutandoAccion() {
        return ejecutandoAccion;
    }

    public void setEjecutandoAccion(boolean ejecutandoAccion) {
        this.ejecutandoAccion = ejecutandoAccion;
    }

}
