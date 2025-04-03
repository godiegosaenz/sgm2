/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.managedbeans.catastro;

import com.origami.sgm.entities.CatConfig;
import com.origami.sgm.entities.CtlgDescuentoEmision;
import com.origami.sgm.lazymodels.BaseLazyDataModel;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import org.jboss.logging.Logger;

/**
 *
 * @author Angel Navarro
 */
@Named
@ViewScoped
public class VariablesConfig implements Serializable {

    public static final long serialVersionUID = 1l;

    @javax.inject.Inject
    private Entitymanager manager;
    protected BaseLazyDataModel<CatConfig> configs;
    protected CatConfig config;

    @PostConstruct
    public void init() {
        try {
            configs = new BaseLazyDataModel<CatConfig>(CatConfig.class, "nombre", "ASC");
        } catch (Exception e) {
            Logger.getLogger(VariablesConfig.class.getName()).log(Logger.Level.FATAL, null, e);
        }
    }
    public void editar(CatConfig editar) {
        try {
            this.config = editar;
        } catch (Exception e) {
        }
    }
    public void nuevo() {
        config = new CatConfig();
    }

    public void guardar() {
        try {
            this.manager.persist(config);
        } catch (Exception e) {
        }

    }

    public void eliminar(CtlgDescuentoEmision delete) {

        try {
           this.manager.delete(delete);
        } catch (Exception e) {
        }
    }

    public BaseLazyDataModel<CatConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(BaseLazyDataModel<CatConfig> configs) {
        this.configs = configs;
    }

    public CatConfig getConfig() {
        return config;
    }

    public void setConfig(CatConfig config) {
        this.config = config;
    }

    

}
