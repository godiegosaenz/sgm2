/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author supergold
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtLog implements Serializable{
    private static final long serialVersionUID = 1l;
    
    private Integer tipo;
    private String mensaje;
    private String detalle;

    public ExtLog() {
    }

    public ExtLog(Integer tipo, String mensaje, String detalle) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.detalle = detalle;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
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
    
    
    
}
