/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author origami
 */
public class ActosPorLiquidaciones implements Serializable{
    
    private BigInteger acto;
    private BigInteger rubro;
    private Integer codsac;
    private BigInteger cantidad;
    private BigDecimal valor;

    public BigInteger getActo() {
        return acto;
    }

    public void setActo(BigInteger acto) {
        this.acto = acto;
    }

    public BigInteger getRubro() {
        return rubro;
    }

    public void setRubro(BigInteger rubro) {
        this.rubro = rubro;
    }

    public Integer getCodsac() {
        return codsac;
    }

    public void setCodsac(Integer codsac) {
        this.codsac = codsac;
    }
    
    public BigInteger getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigInteger cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
