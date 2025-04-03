/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Joao Sanga
 */
public class SolicitudExoneracionEnte implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private BigInteger idPropietario;
    private Integer cont;
    private BigDecimal valor;
    
    public SolicitudExoneracionEnte(BigInteger idPropietario, BigDecimal valor){
        this.valor = valor;
        this.idPropietario = idPropietario;
        cont = 1;
    }
    
    public void sumarValor(BigDecimal valor){
        this.valor = this.valor.add(valor);
        cont++;
    }

    public Integer getCont() {
        return cont;
    }

    public void setCont(Integer cont) {
        this.cont = cont;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigInteger getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(BigInteger idPropietario) {
        this.idPropietario = idPropietario;
    }
    
}
