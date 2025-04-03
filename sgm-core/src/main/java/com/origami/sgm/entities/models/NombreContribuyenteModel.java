/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;

/**
 *
 * @author Joao Sanga
 */
public class NombreContribuyenteModel implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String fecha;
    private String nombre;
    
    public NombreContribuyenteModel(String usuario, String fecha, String nombre){
        this.username = usuario;
        this.fecha = fecha;
        this.nombre = nombre;
    }
    
    public NombreContribuyenteModel(){
    
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
