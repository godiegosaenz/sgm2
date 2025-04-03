/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;

/**
 *
 * @author Joao Sanga
 */
public class FotosInspeccionMigracion implements Serializable{
    
    public static final Long serialVersionUID = 1L;
    
    private String imagen_nombre;
    private byte[] imagen;
    private Long idInspeccion;

    public String getImagen_nombre() {
        return imagen_nombre;
    }

    public void setImagen_nombre(String imagen_nombre) {
        this.imagen_nombre = imagen_nombre;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Long getIdInspeccion() {
        return idInspeccion;
    }

    public void setIdInspeccion(Long idInspeccion) {
        this.idInspeccion = idInspeccion;
    }    
    
}
