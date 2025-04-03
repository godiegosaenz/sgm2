/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.models;

import com.origami.sgm.entities.CatPredio;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosLoorVargas
 */
public class ResultData implements Serializable {

    private Short resultado;
    private List<CatPredio> predios;
    private Boolean estado;
    private String mensaje;
    private static final Long serialVersionUID = 1L;

    public ResultData() {
    }

    public Short getResultado() {
        return resultado;
    }

    public void setResultado(Short resultado) {
        this.resultado = resultado;
    }

    public List<CatPredio> getPredios() {
        return predios;
    }

    public void setPredios(List<CatPredio> predios) {
        this.predios = predios;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
