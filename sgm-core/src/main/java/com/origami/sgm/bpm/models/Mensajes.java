/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;

/**
 *
 * @author Henry Pilco
 */
public class Mensajes implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String mensaje;
    private String detalle;
    private Boolean estado;

    public Mensajes() {
    }

    public Mensajes(String mensaje, String detalle, Boolean estado) {
        this.mensaje = mensaje;
        this.detalle = detalle;
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
}
