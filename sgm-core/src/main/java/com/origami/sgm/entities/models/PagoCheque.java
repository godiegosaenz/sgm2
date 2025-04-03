/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import com.origami.sgm.entities.RenEntidadBancaria;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Henry Pilco
 */
public class PagoCheque implements Serializable{
    private static final long serialVersionUID = 1L;
    private String numCheque;
    private String numCuenta;
    private Integer tipoPago;
    private RenEntidadBancaria banco;
    private BigDecimal valor= new BigDecimal("0.00");

    public PagoCheque() {
    }

    public String getNumCheque() {
        return numCheque;
    }

    public void setNumCheque(String numCheque) {
        this.numCheque = numCheque;
    }

    public String getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }

    public Integer getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(Integer tipoPago) {
        this.tipoPago = tipoPago;
    }

    public RenEntidadBancaria getBanco() {
        return banco;
    }

    public void setBanco(RenEntidadBancaria banco) {
        this.banco = banco;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PagoCheque)) {
            return false;
        }
        PagoCheque other = (PagoCheque) object;
        if (
                (this.banco == null && other.banco != null) || (this.banco != null && !this.banco.equals(other.banco))
                || (this.numCheque == null && other.numCheque != null) || (this.numCheque != null && !this.numCheque.equals(other.numCheque))
                || (this.numCuenta == null && other.numCuenta != null) || (this.numCuenta != null && !this.numCuenta.equals(other.numCuenta))
                || (this.valor == null && other.valor != null) || (this.valor != null && !this.valor.equals(other.valor))
                ) {
            return false;
        }
        return true;
    }
}
