/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.events;

import java.util.List;

/**
 *
 * @author Fernando
 */
public class DivisionPrediosPost {
    
    protected String codPredioDividir;
    protected List<String> codPrediosFinales;
    
    public DivisionPrediosPost() {
    }

    public DivisionPrediosPost(String codPredioDividir) {
        this.codPredioDividir = codPredioDividir;
    }
    
    public DivisionPrediosPost(String codPredioDividir, List<String> codPrediosFinales) {
        this.codPredioDividir = codPredioDividir;
        this.codPrediosFinales = codPrediosFinales;
    }

    
    public String getCodPredioDividir() {
        return codPredioDividir;
    }

    public void setCodPredioDividir(String codPredioDividir) {
        this.codPredioDividir = codPredioDividir;
    }

    public List<String> getCodPrediosFinales() {
        return codPrediosFinales;
    }

    public void setCodPrediosFinales(List<String> codPrediosFinales) {
        this.codPrediosFinales = codPrediosFinales;
    }
    
    
    
}
