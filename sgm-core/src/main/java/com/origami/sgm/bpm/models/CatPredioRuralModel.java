/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.EmisionesRuralesExcel;

/**
 *
 * @author origami
 */
public class CatPredioRuralModel {
    private static final Long serialVersionUID = 1L;
    
    private CatPredioRustico predioRustico;
    private EmisionesRuralesExcel predioRusctico2017;

    public CatPredioRuralModel() {
    }

    public CatPredioRuralModel(CatPredioRustico predioRustico, EmisionesRuralesExcel predioRusctico2017) {
        this.predioRustico = predioRustico;
        this.predioRusctico2017 = predioRusctico2017;
    }

    public CatPredioRustico getPredioRustico() {
        return predioRustico;
    }

    public void setPredioRustico(CatPredioRustico predioRustico) {
        this.predioRustico = predioRustico;
    }

    public EmisionesRuralesExcel getPredioRusctico2017() {
        return predioRusctico2017;
    }

    public void setPredioRusctico2017(EmisionesRuralesExcel predioRusctico2017) {
        this.predioRusctico2017 = predioRusctico2017;
    }
    
    
}
