/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.interfaces.models.avaluos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author CarlosLoorVargas
 */
public class DatosNumericos implements Serializable {

    private static final Long serialVersionUID = 1L;
    private BigInteger numPredio;
    private BigDecimal valor;

    public DatosNumericos() {
    }

    public DatosNumericos(BigDecimal valor) {
        this.valor = valor;
    }

    public DatosNumericos(BigInteger numPredio, BigDecimal valor) {
        this.numPredio = numPredio;
        this.valor = valor;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

}
