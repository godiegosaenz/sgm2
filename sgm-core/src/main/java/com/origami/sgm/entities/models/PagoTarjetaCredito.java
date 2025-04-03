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
public class PagoTarjetaCredito implements Serializable{
    private static final long serialVersionUID = 1L;
    private Integer tipoPago;
    private String numTarjeta;
    private Date fechaCaducidad;
    private String autorizacion;
    private String baucher;
    private String nombreTitular;
    private RenEntidadBancaria banco;
    private BigDecimal valor= new BigDecimal("0.00");

    public PagoTarjetaCredito() {
    }

    public Integer getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(Integer tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getBaucher() {
        return baucher;
    }

    public void setBaucher(String baucher) {
        this.baucher = baucher;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
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
        if (!(object instanceof PagoTarjetaCredito)) {
            return false;
        }
        PagoTarjetaCredito other = (PagoTarjetaCredito) object;
        if (
                (this.banco == null && other.banco != null) || (this.banco != null && !this.banco.equals(other.banco))
                || (this.numTarjeta == null && other.numTarjeta != null) || (this.numTarjeta != null && !this.numTarjeta.equals(other.numTarjeta))
                || (this.autorizacion == null && other.autorizacion != null) || (this.autorizacion != null && !this.autorizacion.equals(other.autorizacion))
                || (this.valor == null && other.valor != null) || (this.valor != null && !this.valor.equals(other.valor))
                ) {
            return false;
        }
        return true;
    }
}
