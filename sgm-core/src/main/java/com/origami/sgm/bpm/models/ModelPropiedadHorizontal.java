/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Angel Navarro
 */
public class ModelPropiedadHorizontal implements Serializable{
    
    protected List<ModelPrediosPH> alicuotasPredio = new ArrayList<>();
    protected List<ModelPropietariosPredio> propietarios = new ArrayList<>();
    protected ModelPrediosPH prediosPH = new ModelPrediosPH();
    protected ModelPropietariosPredio propietariosPredio = new ModelPropietariosPredio();

    public List<ModelPrediosPH> getAlicuotasPredio() {
        return alicuotasPredio;
    }

    public void setAlicuotasPredio(List<ModelPrediosPH> alicuotasPredio) {
        this.alicuotasPredio = alicuotasPredio;
    }

    public List<ModelPropietariosPredio> getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(List<ModelPropietariosPredio> propietarios) {
        this.propietarios = propietarios;
    }

    public ModelPrediosPH getPrediosPH() {
        return prediosPH;
    }

    public void setPrediosPH(ModelPrediosPH prediosPH) {
        this.prediosPH = prediosPH;
    }

    public ModelPropietariosPredio getPropietariosPredio() {
        return propietariosPredio;
    }

    public void setPropietariosPredio(ModelPropietariosPredio propietariosPredio) {
        this.propietariosPredio = propietariosPredio;
    }
    
}
