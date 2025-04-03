/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.censocat.models;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author CarlosLoorVargas
 */
public class ResumenOrdenes implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long orden;
    private String responsable;
    private Short finalizadas;
    private Short pendientes;
    private Short reprocesados;
    private Date fecha;

    public ResumenOrdenes() {
    }

    public ResumenOrdenes(Long orden, String responsable, Short finalizadas, Short pendientes, Date fecha) {
        this.orden = orden;
        this.responsable = responsable;
        this.finalizadas = finalizadas;
        this.pendientes = pendientes;
        this.fecha = fecha;
    }

    public ResumenOrdenes(String responsable, Short finalizadas, Short pendientes) {
        this.responsable = responsable;
        this.finalizadas = finalizadas;
        this.pendientes = pendientes;
    }
    
    public ResumenOrdenes(String responsable, Short finalizadas, Short pendientes, Short reprocesados) {
        this.responsable = responsable;
        this.finalizadas = finalizadas;
        this.pendientes = pendientes;
        this.reprocesados = reprocesados;
    }

    public Long getOrden() {
        return orden;
    }

    public void setOrden(Long orden) {
        this.orden = orden;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Short getFinalizadas() {
        return finalizadas;
    }

    public void setFinalizadas(Short finalizadas) {
        this.finalizadas = finalizadas;
    }

    public Short getPendientes() {
        return pendientes;
    }

    public void setPendientes(Short pendientes) {
        this.pendientes = pendientes;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Short getReprocesados() {
        return reprocesados;
    }

    public void setReprocesados(Short reprocesados) {
        this.reprocesados = reprocesados;
    }

}
