/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Henry Pilco
 */
public class PagoNotaCredito implements Serializable{
    private static final long serialVersionUID = 1L;
    private String numCredito;
    private Date fecha;
    private Integer tipoPago;
    private BigDecimal valor= new BigDecimal("0.00");

    public PagoNotaCredito() {
    }

    public String getNumCredito() {
        return numCredito;
    }

    public void setNumCredito(String numCredito) {
        this.numCredito = numCredito;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(Integer tipoPago) {
        this.tipoPago = tipoPago;
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
        if (!(object instanceof PagoNotaCredito)) {
            return false;
        }
        PagoNotaCredito other = (PagoNotaCredito) object;
        if (
                (this.numCredito == null && other.numCredito != null) || (this.numCredito != null && !this.numCredito.equals(other.numCredito))
                || (this.valor == null && other.valor != null) || (this.valor != null && !this.valor.equals(other.valor))
                ) {
            return false;
        }
        return true;
    }
}
