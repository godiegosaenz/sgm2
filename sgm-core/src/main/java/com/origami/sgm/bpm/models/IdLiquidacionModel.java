/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;

/**
 *
 * @author Joao Sanga
 */
public class IdLiquidacionModel implements Serializable {
    
    private Long id_liquidacion;
    
    public IdLiquidacionModel(Long id_liquidacion){
        this.id_liquidacion = id_liquidacion;
    }

    public Long getId_liquidacion() {
        return id_liquidacion;
    }

    public void setId_liquidacion(Long id_liquidacion) {
        this.id_liquidacion = id_liquidacion;
    }
    
}
