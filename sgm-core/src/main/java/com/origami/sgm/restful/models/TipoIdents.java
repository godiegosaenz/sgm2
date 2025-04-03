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
 * @author CarlosLoorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TipoIdents implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String descripcion;
    private String prefijo;

    public TipoIdents() {
    }

    public TipoIdents(Long id, String descripcion, String prefijo) {
        this.id = id;
        this.descripcion = descripcion;
        this.prefijo = prefijo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

}
