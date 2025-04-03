/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;

/**
 *
 * @author Angel Navarro
 */
public class Empresa implements Serializable{

    private String razonSocial;
    private String nombreComercial;
    private Short provincia;
    private Short canton;
    private String direccion;
    private String correo;
    private String telefono;
    private String ruc;

    public Empresa() {
    }

    public Empresa(String razonSocial, String nombreComercial, Short provincia, Short canton, String direccion, String correo, String telefono, String ruc) {
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.provincia = provincia;
        this.canton = canton;
        this.direccion = direccion;
        this.correo = correo;
        this.telefono = telefono;
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public Short getProvincia() {
        return provincia;
    }

    public void setProvincia(Short provincia) {
        this.provincia = provincia;
    }

    public Short getCanton() {
        return canton;
    }

    public void setCanton(Short canton) {
        this.canton = canton;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.models.Empresa [" + this.razonSocial + "]";
    }

}
