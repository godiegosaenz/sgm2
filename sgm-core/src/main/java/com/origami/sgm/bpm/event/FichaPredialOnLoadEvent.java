/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.event;

import com.origami.sgm.entities.CatPredio;

/**
 *
 * @author dfcalderio
 *
 */
public class FichaPredialOnLoadEvent {

    private CatPredio predio;

    public FichaPredialOnLoadEvent() {
    }

    public FichaPredialOnLoadEvent(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

}
