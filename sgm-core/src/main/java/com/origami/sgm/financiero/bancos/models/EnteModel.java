/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.financiero.bancos.models;

/**
 *
 * @author CarlosLoorVargas
 */
public class EnteModel {

    private Long id;
    private String tipoPersona;
    private String identificacion;
    private String descripcion;
    private String descPersona;

    public EnteModel() {
    }

    public EnteModel(Long id, String tipoPersona, String identificacion, String descripcion) {
        this.id = id;
        this.tipoPersona = tipoPersona;
        this.identificacion = identificacion;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescPersona() {
        return descPersona;
    }

    public void setDescPersona(String descPersona) {
        this.descPersona = descPersona;
    }

}
