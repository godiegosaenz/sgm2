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
public class PrediosRusticosUrbanosModel implements Serializable{
    
    public static final Long serialVersionUID = 1L;
    
    private String codigoPredio;
    private Long idPredio;
    private Long tipoPredio;
    
    public PrediosRusticosUrbanosModel(String codPredio, Long id, Long tipoPredio){
        codigoPredio = codPredio;
        this.idPredio = id;
        this.tipoPredio = tipoPredio;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public Long getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(Long idPredio) {
        this.idPredio = idPredio;
    }

    public Long getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(Long tipoPredio) {
        this.tipoPredio = tipoPredio;
    }
    
    
}
