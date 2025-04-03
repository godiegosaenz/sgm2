package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Almacena temporalmente un {@link CatEnte} con cada una de las lista de
 * entites que estan asociadas.
 *
 * @author Angel Navarro
 */
public class ModelDepuracionEntes implements Serializable {

    private CatEnte enteElemento = new CatEnte();
    private List<ListCollectionsReff> elementosAsociados = new ArrayList<>();

    public CatEnte getEnteElemento() {
        return enteElemento;
    }

    public void setEnteElemento(CatEnte enteElemento) {
        this.enteElemento = enteElemento;
    }

    public List<ListCollectionsReff> getElementosAsociados() {
        return elementosAsociados;
    }

    public void setElementosAsociados(List<ListCollectionsReff> elementosAsociados) {
        this.elementosAsociados = elementosAsociados;
    }

    public ModelDepuracionEntes() {
    }

    public ModelDepuracionEntes(CatEnte elemento, List<ListCollectionsReff> elementosAsociados) {
        this.enteElemento = elemento;
        this.elementosAsociados = elementosAsociados;
    }

}
