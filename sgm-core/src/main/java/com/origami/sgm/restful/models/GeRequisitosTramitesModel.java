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
 * @author Joao Sanga
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GeRequisitosTramitesModel implements Serializable {
    
    private Long id;
    private String nombre;
    private Boolean tiene_comprobante;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getTiene_comprobante() {
        return tiene_comprobante;
    }

    public void setTiene_comprobante(Boolean tiene_comprobante) {
        this.tiene_comprobante = tiene_comprobante;
    }
    
    
}
