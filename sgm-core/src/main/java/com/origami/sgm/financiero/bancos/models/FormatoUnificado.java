/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.financiero.bancos.models;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLOorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FormatoUnificado implements Serializable {

    private static final Long serialVersionUID = 1L;
    private Long liquidacion;
    private Long comprobante;
    private String tipoPersona;
    private String descPersona;
    private String identificacion;
    private String contribuyente;
    private String numPredio;
    private String vc109;
    private String calle;
    private String vc00000;
    private String vc01;
    private String vc02;
    private String periodo;
    private String vc03;
    private String emision;
    private String adicional;
    private String total;
    private String vcn;
    private String claveCatastral;
    private String claveAnterior;
    private String claveFormatoG;
    private String claveFormatoP;
    private String claveFormatoSF;
    private String vvCoactiva;
    private String vfCoactiva;
    private String tipoCobro;
    private String moneda;
    private String formaCobro;

    public FormatoUnificado() {
    }

    public Long getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Long liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Long getComprobante() {
        return comprobante;
    }

    public void setComprobante(Long comprobante) {
        this.comprobante = comprobante;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getDescPersona() {
        return descPersona;
    }

    public void setDescPersona(String descPersona) {
        this.descPersona = descPersona;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    public String getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(String numPredio) {
        this.numPredio = numPredio;
    }

    public String getVc109() {
        return vc109;
    }

    public void setVc109(String vc109) {
        this.vc109 = vc109;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getVc00000() {
        return vc00000;
    }

    public void setVc00000(String vc00000) {
        this.vc00000 = vc00000;
    }

    public String getVc01() {
        return vc01;
    }

    public void setVc01(String vc01) {
        this.vc01 = vc01;
    }

    public String getVc02() {
        return vc02;
    }

    public void setVc02(String vc02) {
        this.vc02 = vc02;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getVc03() {
        return vc03;
    }

    public void setVc03(String vc03) {
        this.vc03 = vc03;
    }

    public String getEmision() {
        return emision;
    }

    public void setEmision(String emision) {
        this.emision = emision;
    }

    public String getAdicional() {
        return adicional;
    }

    public void setAdicional(String adicional) {
        this.adicional = adicional;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getVcn() {
        return vcn;
    }

    public void setVcn(String vcn) {
        this.vcn = vcn;
    }

    public String getClaveCatastral() {
        return claveCatastral;
    }

    public void setClaveCatastral(String claveCatastral) {
        this.claveCatastral = claveCatastral;
    }

    public String getClaveAnterior() {
        return claveAnterior;
    }

    public void setClaveAnterior(String claveAnterior) {
        this.claveAnterior = claveAnterior;
    }

    public String getClaveFormatoG() {
        return claveFormatoG;
    }

    public void setClaveFormatoG(String claveFormatoG) {
        this.claveFormatoG = claveFormatoG;
    }

    public String getClaveFormatoP() {
        return claveFormatoP;
    }

    public void setClaveFormatoP(String claveFormatoP) {
        this.claveFormatoP = claveFormatoP;
    }

    public String getClaveFormatoSF() {
        return claveFormatoSF;
    }

    public void setClaveFormatoSF(String claveFormatoSF) {
        this.claveFormatoSF = claveFormatoSF;
    }

    public String getVvCoactiva() {
        return vvCoactiva;
    }

    public void setVvCoactiva(String vvCoactiva) {
        this.vvCoactiva = vvCoactiva;
    }

    public String getVfCoactiva() {
        return vfCoactiva;
    }

    public void setVfCoactiva(String vfCoactiva) {
        this.vfCoactiva = vfCoactiva;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getFormaCobro() {
        return formaCobro;
    }

    public void setFormaCobro(String formaCobro) {
        this.formaCobro = formaCobro;
    }

}
