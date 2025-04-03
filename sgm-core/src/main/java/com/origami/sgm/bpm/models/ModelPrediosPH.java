/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS6;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Angel Navarro
 */
public class ModelPrediosPH implements Serializable{
    
    protected CatPredio predio = new CatPredio();
    protected CatPredioS4 predioS4 = new CatPredioS4();
    protected CatPredioS6 predioS6 = new CatPredioS6(); 
//    protected CatEscritura escritura =new CatEscritura();
    protected ModelPropietariosPredio propietarios = new ModelPropietariosPredio();
    protected List<ModelPropietariosPredio> listaPropietarios = new ArrayList<>();
    protected Boolean image;

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredioS4 getPredioS4() {
        return predioS4;
    }

    public void setPredioS4(CatPredioS4 predioS4) {
        this.predioS4 = predioS4;
    }

    public CatPredioS6 getPredioS6() {
        return predioS6;
    }

    public void setPredioS6(CatPredioS6 predioS6) {
        this.predioS6 = predioS6;
    }

//    public CatEscritura getEscritura() {
//        return escritura;
//    }
//
//    public void setEscritura(CatEscritura escritura) {
//        this.escritura = escritura;
//    }

    public ModelPropietariosPredio getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(ModelPropietariosPredio propietarios) {
        this.propietarios = propietarios;
    }

    public List<ModelPropietariosPredio> getListaPropietarios() {
        return listaPropietarios;
    }

    public void setListaPropietarios(List<ModelPropietariosPredio> listaPropietarios) {
        this.listaPropietarios = listaPropietarios;
    }

    public Boolean getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image = image;
    }

}
