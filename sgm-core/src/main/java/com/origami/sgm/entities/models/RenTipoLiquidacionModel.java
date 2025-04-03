/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import com.origami.sgm.entities.RenRubrosLiquidacion;
import com.origami.sgm.entities.RenTipoLiquidacion;
import java.io.Serializable;

/**
 *
 * @author Joao Sanga
 */
public class RenTipoLiquidacionModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nombreTipoLiq;
    private Boolean tieneRubrosAjenos = false;
    private Boolean tieneRubrosPropios = false;
    
    public RenTipoLiquidacionModel(RenTipoLiquidacion liq){
        this.id = liq.getId();
        this.nombreTipoLiq = liq.getNombreTitulo();
        if(this.nombreTipoLiq != null)
            this.nombreTipoLiq = this.nombreTipoLiq.toUpperCase();
        for(RenRubrosLiquidacion temp : liq.getRenRubrosLiquidacionCollection()){
            if(!temp.getRubroDelMunicipio())
                this.tieneRubrosAjenos = true;
            else
                this.tieneRubrosPropios = true;
        }
        
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreTipoLiq() {
        return nombreTipoLiq;
    }

    public void setNombreTipoLiq(String nombreTipoLiq) {
        this.nombreTipoLiq = nombreTipoLiq;
    }

    public Boolean getTieneRubrosAjenos() {
        return tieneRubrosAjenos;
    }

    public void setTieneRubrosAjenos(Boolean tieneRubrosAjenos) {
        this.tieneRubrosAjenos = tieneRubrosAjenos;
    }

    public Boolean getTieneRubrosPropios() {
        return tieneRubrosPropios;
    }

    public void setTieneRubrosPropios(Boolean tieneRubrosPropios) {
        this.tieneRubrosPropios = tieneRubrosPropios;
    }
    
    
}
