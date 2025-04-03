
package com.origami.sgm.events;

import com.origami.sgm.entities.CatPredio;

/**
 *
 * @author Fernando
 */
public class EliminacionPredioPost {
    
    private String codPredio;
    
    private Long numPredio;
    
    private CatPredio predio;
    
    private Boolean habilitar = Boolean.FALSE;

    public EliminacionPredioPost() {
    }

    public EliminacionPredioPost(CatPredio predio) {
        this.predio = predio;
    }

    public EliminacionPredioPost(Long numPredio) {
        this.numPredio = numPredio;
    }

    public EliminacionPredioPost(String codPredio) {
        this.codPredio = codPredio;
    }

    public String getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(String codPredio) {
        this.codPredio = codPredio;
    }

    public Long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Long numPredio) {
        this.numPredio = numPredio;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Boolean getHabilitar() {
        return habilitar;
    }

    public void setHabilitar(Boolean habilitar) {
        this.habilitar = habilitar;
    }
    
    
    
    
}
