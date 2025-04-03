
package com.origami.sgm.events;

import com.origami.sgm.entities.CatPredio;

/**
 *
 * @author Fernando
 */
public class HistoricoPredioPost {
    
    private String codPredio;
    
    private Long numPredio;
    
    private CatPredio predio;

    public HistoricoPredioPost() {
    }

    public HistoricoPredioPost(CatPredio predio) {
        this.predio = predio;
    }

    public HistoricoPredioPost(Long numPredio) {
        this.numPredio = numPredio;
    }

    public HistoricoPredioPost(String codPredio) {
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
    
    
    
}
