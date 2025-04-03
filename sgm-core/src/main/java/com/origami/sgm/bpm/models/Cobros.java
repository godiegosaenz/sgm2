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
 * @author supergold
 * Date Jun 11, 2016
 */
public class Cobros implements Serializable {

    private Long liquidacion;
    private String nombreTitular;
    private String nombreBanco;
    private String numeroCuenta;
    private String numeroCheque;
    private BigDecimal pago;
    private Integer tipoPago;
    private BigInteger comprobante;

    /**
     * 
     * @param liquidacion id de liquidacion.
     * @param nombreBanco Nombre del banco o entidad.
     * @param numeroCuenta Numero de cuenta o nuemro de tarjeta, numero de transaccion
     * @param numeroCheque nuemro de cheque o numero de voucher.
     * @param pago valor del pago.
     * @param tipoPago numero de tipo pago.
     * @param nombreTitular Nonbre del titular.
     */
    public Cobros(Long liquidacion, String nombreBanco, String numeroCuenta, String numeroCheque, BigDecimal pago, Integer tipoPago, String nombreTitular) {
        this.liquidacion = liquidacion;
        this.nombreBanco = nombreBanco;
        this.numeroCuenta = numeroCuenta;
        this.numeroCheque = numeroCheque;
        this.pago = pago;
        this.tipoPago = tipoPago;
        this.nombreTitular = nombreTitular;
    }

    public Cobros(Long liquidacion, String nombreTitular, BigDecimal pago, BigInteger comprobante) {
        this.liquidacion = liquidacion;
        this.nombreTitular = nombreTitular;
        this.pago = pago;
        this.comprobante = comprobante;
    }

    public Cobros(Long liquidacion, BigInteger comprobante) {
        this.liquidacion = liquidacion;
        this.comprobante = comprobante;
    }

    public Long getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Long liquidacion) {
        this.liquidacion = liquidacion;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public Integer getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(Integer tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public BigInteger getComprobante() {
        return comprobante;
    }

    public void setComprobante(BigInteger comprobante) {
        this.comprobante = comprobante;
    }
        
}