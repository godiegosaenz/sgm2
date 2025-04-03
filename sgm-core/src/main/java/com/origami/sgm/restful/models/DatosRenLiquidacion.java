/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosRenLiquidacion implements Serializable {

    private static final Long serialVersionUID = 1L;
    private Long id;
    private BigInteger numComprobante;
    private BigInteger numLiquidacion;
    private String idLiquidacion;
    private BigDecimal totalPago;
    private String usuarioIngreso;
    private String numReporte;
    private Date fechaIngreso;
    private String vendedor;
    private String codigoLocal;
    private BigDecimal costoAdq;
    private BigDecimal cuantia;
    private Date fechaContratoAnt;
    private Long predio;
    private String observacion;
    private Integer anio;
    private BigDecimal valorComercial;
    private BigDecimal valorCatastral;
    private BigDecimal valorHipoteca;
    private BigDecimal valorNominal;
    private BigDecimal valorMora;
    private BigDecimal totalAdicionales;
    private BigDecimal otros;
    private BigDecimal valorCompra;
    private BigDecimal valorVenta;
    private BigDecimal valorMejoras;
    private BigDecimal areaTotal;
    private BigDecimal patrimonio;
    private BigDecimal avaluoConstruccion;
    private BigDecimal avaluoSolar;
    private BigDecimal avaluoMunicipal;
    private BigDecimal valorCoactiva = new BigDecimal("0.00");
    private BigDecimal descuento = new BigDecimal("0.00");
    private BigDecimal recargo = new BigDecimal("0.00");
    private BigDecimal interes = new BigDecimal("0.00");
    private BigDecimal pagoFinal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getNumComprobante() {
        return numComprobante;
    }

    public void setNumComprobante(BigInteger numComprobante) {
        this.numComprobante = numComprobante;
    }

    public BigInteger getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(BigInteger numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }

    public String getIdLiquidacion() {
        return idLiquidacion;
    }

    public void setIdLiquidacion(String idLiquidacion) {
        this.idLiquidacion = idLiquidacion;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public String getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(String numReporte) {
        this.numReporte = numReporte;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getCodigoLocal() {
        return codigoLocal;
    }

    public void setCodigoLocal(String codigoLocal) {
        this.codigoLocal = codigoLocal;
    }

    public BigDecimal getCostoAdq() {
        return costoAdq;
    }

    public void setCostoAdq(BigDecimal costoAdq) {
        this.costoAdq = costoAdq;
    }

    public BigDecimal getCuantia() {
        return cuantia;
    }

    public void setCuantia(BigDecimal cuantia) {
        this.cuantia = cuantia;
    }

    public Date getFechaContratoAnt() {
        return fechaContratoAnt;
    }

    public void setFechaContratoAnt(Date fechaContratoAnt) {
        this.fechaContratoAnt = fechaContratoAnt;
    }

    public Long getPredio() {
        return predio;
    }

    public void setPredio(Long predio) {
        this.predio = predio;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public BigDecimal getValorComercial() {
        return valorComercial;
    }

    public void setValorComercial(BigDecimal valorComercial) {
        this.valorComercial = valorComercial;
    }

    public BigDecimal getValorCatastral() {
        return valorCatastral;
    }

    public void setValorCatastral(BigDecimal valorCatastral) {
        this.valorCatastral = valorCatastral;
    }

    public BigDecimal getValorHipoteca() {
        return valorHipoteca;
    }

    public void setValorHipoteca(BigDecimal valorHipoteca) {
        this.valorHipoteca = valorHipoteca;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(BigDecimal valorNominal) {
        this.valorNominal = valorNominal;
    }

    public BigDecimal getValorMora() {
        return valorMora;
    }

    public void setValorMora(BigDecimal valorMora) {
        this.valorMora = valorMora;
    }

    public BigDecimal getTotalAdicionales() {
        return totalAdicionales;
    }

    public void setTotalAdicionales(BigDecimal totalAdicionales) {
        this.totalAdicionales = totalAdicionales;
    }

    public BigDecimal getOtros() {
        return otros;
    }

    public void setOtros(BigDecimal otros) {
        this.otros = otros;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valorVenta = valorVenta;
    }

    public BigDecimal getValorMejoras() {
        return valorMejoras;
    }

    public void setValorMejoras(BigDecimal valorMejoras) {
        this.valorMejoras = valorMejoras;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(BigDecimal patrimonio) {
        this.patrimonio = patrimonio;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public BigDecimal getValorCoactiva() {
        return valorCoactiva;
    }

    public void setValorCoactiva(BigDecimal valorCoactiva) {
        this.valorCoactiva = valorCoactiva;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getRecargo() {
        return recargo;
    }

    public void setRecargo(BigDecimal recargo) {
        this.recargo = recargo;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getPagoFinal() {
        return pagoFinal;
    }

    public void setPagoFinal(BigDecimal pagoFinal) {
        this.pagoFinal = pagoFinal;
    }

}
