/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.predio.models;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author OrigamiDi1
 */
public class NivelModel implements Serializable {

    Long serialVersionUID = 1L;
    private Long edificacion;
    private Short noEdificacion;
    private int nroNivel;
    private int cantDpto;
    private int cantBodegas;
    private int cantParqueos;

    public NivelModel() {
    }

    public NivelModel(int nroNivel, Short noEdificacion) {
        this.nroNivel = nroNivel;
        this.noEdificacion = noEdificacion;
    }

    public int getNroNivel() {
        return nroNivel;
    }

    public void setNroNivel(int nroNivel) {
        this.nroNivel = nroNivel;
    }

    public Short getNoEdificacion() {
        return noEdificacion;
    }

    public void setNoEdificacion(Short noEdificacion) {
        this.noEdificacion = noEdificacion;
    }

    public Long getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(Long edificacion) {
        this.edificacion = edificacion;
    }

    public int getCantDpto() {
        return cantDpto;
    }

    public void setCantDpto(int cantDpto) {
        this.cantDpto = cantDpto;
    }

    public int getCantBodegas() {
        return cantBodegas;
    }

    public void setCantBodegas(int cantBodegas) {
        this.cantBodegas = cantBodegas;
    }

    public int getCantParqueos() {
        return cantParqueos;
    }

    public void setCantParqueos(int cantParqueos) {
        this.cantParqueos = cantParqueos;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NivelModel)) {
            return false;
        }
        NivelModel other = (NivelModel) obj;
        return Objects.equals(this.edificacion, other.getEdificacion()) && this.nroNivel == other.nroNivel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.edificacion);
        hash = 31 * hash + this.nroNivel;
        return hash;
    }

}
