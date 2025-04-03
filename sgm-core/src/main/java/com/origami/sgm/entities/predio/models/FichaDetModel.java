/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.predio.models;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author CarlosLoorVargas
 */
public class FichaDetModel implements Serializable {
    @Expose(serialize = true, deserialize = true)
    private Long codPredio;
    @Expose(serialize = true, deserialize = true)
    private Long idEdif;
    @Expose(serialize = true, deserialize = true)
    private Long codEdif;
    @Expose(serialize = true, deserialize = true)
    private Long codDetEspec;
    @Expose
    private BigDecimal porcentaje;
    @Expose(serialize = true, deserialize = true)
    private boolean estatus = true;
    private static final long serialVersionUID = 1L;

    public FichaDetModel() {
    }

    public Long getIdEdif() {
        return idEdif;
    }

    public void setIdEdif(Long idEdif) {
        this.idEdif = idEdif;
    }
    
    public Long getCodPredio() {
        return codPredio;
    }

    public void setCodPredio(Long codPredio) {
        this.codPredio = codPredio;
    }

    public Long getCodDetEspec() {
        return codDetEspec;
    }

    public void setCodDetEspec(Long codDetEspec) {
        this.codDetEspec = codDetEspec;
    }

    public Long getCodEdif() {
        return codEdif;
    }

    public void setCodEdif(Long codEdif) {
        this.codEdif = codEdif;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public boolean isEstatus() {
        return estatus;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return "FichaDetModel{" + "codPredio=" + codPredio + ", idEdif=" + idEdif + ", codEdif=" + codEdif + ", codDetEspec=" + codDetEspec + ", porcentaje=" + porcentaje + ", estatus=" + estatus + '}';
    }
    
    

}
