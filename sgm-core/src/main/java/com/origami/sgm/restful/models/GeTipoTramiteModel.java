/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Joao Sanga
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GeTipoTramiteModel implements Serializable {
    
    private Long id;
    private String descripcion;
    private String actkey;
    private List<GeRequisitosTramitesModel> requisitos;
            
    public GeTipoTramiteModel(){
        requisitos = new ArrayList();
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

    public String getActkey() {
        return actkey;
    }

    public void setActkey(String actkey) {
        this.actkey = actkey;
    }

    public List<GeRequisitosTramitesModel> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<GeRequisitosTramitesModel> requisitos) {
        this.requisitos = requisitos;
    }
    
}
