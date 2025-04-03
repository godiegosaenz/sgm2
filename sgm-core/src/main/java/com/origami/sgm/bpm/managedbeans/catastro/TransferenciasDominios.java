/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.sgm.entities.CatEscrituraPropietario;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class TransferenciasDominios implements Serializable {

    private static final Logger LOG = Logger.getLogger(TransferenciasDominios.class.getName());

    protected CatEscrituraPropietario cep;
    protected BaseLazyDataModel<CatEscrituraPropietario> lazy;

    @PostConstruct
    public void load() {
        try {
            lazy = new BaseLazyDataModel<>(CatEscrituraPropietario.class, "id");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public CatEscrituraPropietario getCep() {
        return cep;
    }

    public void setCep(CatEscrituraPropietario cep) {
        this.cep = cep;
    }

    public BaseLazyDataModel<CatEscrituraPropietario> getLazy() {
        return lazy;
    }

    public void setLazy(BaseLazyDataModel<CatEscrituraPropietario> lazy) {
        this.lazy = lazy;
    }

}
