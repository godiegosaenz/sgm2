/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Almacena temporalmente una lista de cualquier tipo de elemento, y el campo
 * que hace referencia de la tabla {@linkplain CatEnte}
 *
 * @author Angel Navarro
 */
public class ListCollectionsReff implements Serializable {

    private List<?> elementos = new ArrayList<>();
    private String campoAsociado;

    public List<?> getElementos() {
        return elementos;
    }

    public void setElementos(List<?> elementos) {
        this.elementos = elementos;
    }

    public String getCampoAsociado() {
        return campoAsociado;
    }

    public void setCampoAsociado(String campoAsociado) {
        this.campoAsociado = campoAsociado;
    }

}
