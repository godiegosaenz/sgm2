/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author CarlosLoorVargas
 */
public class DatoSeguro implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identificacion;
    private String descripcion;
    private String genero;
    private String condicion;
    private Date fecNacto;
    private String fecConst;
    private Date fecVenc;
    private String nacionalidad;
    private String estadoCivil;
    private String conyuge;
    private String direccion;
    private String email;
    private String telefono;
    private String objSocial;

    public DatoSeguro() {
    }

    public DatoSeguro(String identificacion, String descripcion, String genero, String condicion, Date fecNacto, String fecConst, Date fecVenc, String nacionalidad, String estadoCivil, String conyuge, String direccion, String email, String telefono, String objSocial) {
        this.identificacion = identificacion;
        this.descripcion = descripcion;
        this.genero = genero;
        this.condicion = condicion;
        this.fecNacto = fecNacto;
        this.fecConst = fecConst;
        this.fecVenc = fecVenc;
        this.nacionalidad = nacionalidad;
        this.estadoCivil = estadoCivil;
        this.conyuge = conyuge;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
        this.objSocial = objSocial;
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

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Date getFecNacto() {
        return fecNacto;
    }

    public void setFecNacto(Date fecNacto) {
        this.fecNacto = fecNacto;
    }

    public String getFecConst() {
        return fecConst;
    }

    public void setFecConst(String fecConst) {
        this.fecConst = fecConst;
    }

    public Date getFecVenc() {
        return fecVenc;
    }

    public void setFecVenc(Date fecVenc) {
        this.fecVenc = fecVenc;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getConyuge() {
        return conyuge;
    }

    public void setConyuge(String conyuge) {
        this.conyuge = conyuge;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getObjSocial() {
        return objSocial;
    }

    public void setObjSocial(String objSocial) {
        this.objSocial = objSocial;
    }

}
