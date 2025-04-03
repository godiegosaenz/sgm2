/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

/**
 *
 * @author Joao Sanga
 */
public class AvaluoInspeccion implements Comparable<AvaluoInspeccion>{
    
    private Long id;
    
    private Integer cantidad;
    
    public AvaluoInspeccion(Long id){
        this.id = id;
        cantidad = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public int compareTo(AvaluoInspeccion avaluo) {
	
        int compareQuantity = ((AvaluoInspeccion) avaluo).getCantidad(); 

        //descending  order
        return compareQuantity - this.cantidad;

    }	
}
