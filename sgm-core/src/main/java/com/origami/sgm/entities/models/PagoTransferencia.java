/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import com.origami.sgm.entities.RenEntidadBancaria;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Henry Pilco
 */
public class PagoTransferencia implements Serializable{
    private static final long serialVersionUID = 1L;
    private String numTransferencia;
    private Date fecha;
    private Integer tipoPago;
    private RenEntidadBancaria banco;
    private BigDecimal valor= new BigDecimal("0.00");

    public PagoTransferencia() {
    }

    public String getNumTransferencia() {
        return numTransferencia;
    }

    public void setNumTransferencia(String numTransferencia) {
        this.numTransferencia = numTransferencia;
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
        if (!(object instanceof PagoTransferencia)) {
            return false;
        }
        PagoTransferencia other = (PagoTransferencia) object;
        if (
                (this.banco == null && other.banco != null) || (this.banco != null && !this.banco.equals(other.banco))
                || (this.numTransferencia == null && other.numTransferencia != null) || (this.numTransferencia != null && !this.numTransferencia.equals(other.numTransferencia))
                || (this.valor == null && other.valor != null) || (this.valor != null && !this.valor.equals(other.valor))
                ) {
            return false;
        }
        return true;
    }
}
