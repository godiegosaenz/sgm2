/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author Joao Sanga
 */
public class EstadisticaModel implements Serializable{
    
    public static final Long serialVersionUID = 1L;
    
    private String tipoTramite;
    private BigInteger finalizados;
    private BigInteger pendientes;
    private BigInteger inactivos;
    private BigInteger muertos;
    
    public EstadisticaModel(String tipoNombre, BigInteger finalizados, BigInteger pendientes, BigInteger inactivos, BigInteger muertos){
        this.tipoTramite = tipoNombre;
        this.finalizados = finalizados;
        this.pendientes = pendientes;
        this.inactivos = inactivos;
        this.muertos = muertos;
    }

    public String getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public BigInteger getFinalizados() {
        return finalizados;
    }

    public void setFinalizados(BigInteger finalizados) {
        this.finalizados = finalizados;
    }

    public BigInteger getPendientes() {
        return pendientes;
    }

    public void setPendientes(BigInteger pendientes) {
        this.pendientes = pendientes;
    }

    public BigInteger getInactivos() {
        return inactivos;
    }

    public void setInactivos(BigInteger inactivos) {
        this.inactivos = inactivos;
    }

    public BigInteger getMuertos() {
        return muertos;
    }

    public void setMuertos(BigInteger muertos) {
        this.muertos = muertos;
    }
    
}
