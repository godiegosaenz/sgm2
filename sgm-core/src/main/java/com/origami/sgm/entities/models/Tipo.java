/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author Angel Navarro
 * @date 20/07/2016
 */
public class Tipo implements Serializable{

    private Integer id;
    private BigInteger idb;
    private String descripcion;

    public Tipo(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Tipo(BigInteger idb, String descripcion) {
        this.idb = idb;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigInteger getIdb() {
        return idb;
    }

    public void setIdb(BigInteger idb) {
        this.idb = idb;
    }
    
}
