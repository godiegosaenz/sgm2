/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatPredioPropietario;
import java.io.Serializable;

/**
 *
 * @author Angel Navarro
 */
public class ModelPropietariosPredio implements Serializable {

    protected CatEnte ente = new CatEnte();
    protected CatPredioPropietario propietarios = new CatPredioPropietario();

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatPredioPropietario getPropietarios() {
        return propietarios;
    }

    public void setPropietarios(CatPredioPropietario propietarios) {
        this.propietarios = propietarios;
    }

    @Override
    public String toString() {
        if (ente != null) {
            if (ente.getEsPersona()) {
                return ente.getApellidos() + ente.getNombres();
            } else {
                return ente.getRazonSocial();
            }
        }
        return null;
    }
}
