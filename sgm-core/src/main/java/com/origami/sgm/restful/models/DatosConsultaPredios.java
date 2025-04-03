/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful.models;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatosConsultaPredios implements Serializable {

    private static final Long serialVersionUID = 1L;
    private Integer tipo;
    private BigInteger numPredio;
    private String contribuyente;
    private short sector;
    private short mz;
    private short cdla;
    private short mzDiv;
    private short solar;
    private short div1;
    private short div2;
    private short div3;
    private short div4;
    private short div5;
    private short div6;
    private short div7;
    private short div8;
    private short div9;
    private short phv;
    private short phh;
    private Long IdUrb;
    private String mzUrb;
    private String slUrb;

    public DatosConsultaPredios(Integer tipo, BigInteger numPredio, String contribuyente, short sector, short mz, short cdla, short mzDiv, short solar, short div1, short div2, short div3, short div4, short div5, short div6, short div7, short div8, short div9, short phv, short phh, Long IdUrb, String mzUrb, String slUrb) {
        this.tipo = tipo;
        this.numPredio = numPredio;
        this.contribuyente = contribuyente;
        this.sector = sector;
        this.mz = mz;
        this.cdla = cdla;
        this.mzDiv = mzDiv;
        this.solar = solar;
        this.div1 = div1;
        this.div2 = div2;
        this.div3 = div3;
        this.div4 = div4;
        this.div5 = div5;
        this.div6 = div6;
        this.div7 = div7;
        this.div8 = div8;
        this.div9 = div9;
        this.phv = phv;
        this.phh = phh;
        this.IdUrb = IdUrb;
        this.mzUrb = mzUrb;
        this.slUrb = slUrb;
    }

    public DatosConsultaPredios(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public DatosConsultaPredios(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    public DatosConsultaPredios(short sector, short mz, short cdla, short mzDiv, short solar, short div1, short div2, short div3, short div4, short div5, short div6, short div9, short phv, short phh) {
        this.sector = sector;
        this.mz = mz;
        this.cdla = cdla;
        this.mzDiv = mzDiv;
        this.solar = solar;
        this.div1 = div1;
        this.div2 = div2;
        this.div3 = div3;
        this.div4 = div4;
        this.div5 = div5;
        this.div6 = div6;
        this.div9 = div9;
        this.phv = phv;
        this.phh = phh;
    }

    public DatosConsultaPredios(Long IdUrb, String mzUrb, String slUrb) {
        this.IdUrb = IdUrb;
        this.mzUrb = mzUrb;
        this.slUrb = slUrb;
    }
    
    public DatosConsultaPredios() {
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public String getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    public short getSector() {
        return sector;
    }

    public void setSector(short sector) {
        this.sector = sector;
    }

    public short getMz() {
        return mz;
    }

    public void setMz(short mz) {
        this.mz = mz;
    }

    public short getCdla() {
        return cdla;
    }

    public void setCdla(short cdla) {
        this.cdla = cdla;
    }

    public short getMzDiv() {
        return mzDiv;
    }

    public void setMzDiv(short mzDiv) {
        this.mzDiv = mzDiv;
    }

    public short getSolar() {
        return solar;
    }

    public void setSolar(short solar) {
        this.solar = solar;
    }

    public short getDiv1() {
        return div1;
    }

    public void setDiv1(short div1) {
        this.div1 = div1;
    }

    public short getDiv2() {
        return div2;
    }

    public void setDiv2(short div2) {
        this.div2 = div2;
    }

    public short getDiv3() {
        return div3;
    }

    public void setDiv3(short div3) {
        this.div3 = div3;
    }

    public short getDiv4() {
        return div4;
    }

    public void setDiv4(short div4) {
        this.div4 = div4;
    }

    public short getDiv5() {
        return div5;
    }

    public void setDiv5(short div5) {
        this.div5 = div5;
    }

    public short getDiv6() {
        return div6;
    }

    public void setDiv6(short div6) {
        this.div6 = div6;
    }

    public short getDiv7() {
        return div7;
    }

    public void setDiv7(short div7) {
        this.div7 = div7;
    }

    public short getDiv8() {
        return div8;
    }

    public void setDiv8(short div8) {
        this.div8 = div8;
    }

    public short getDiv9() {
        return div9;
    }

    public void setDiv9(short div9) {
        this.div9 = div9;
    }

    public short getPhv() {
        return phv;
    }

    public void setPhv(short phv) {
        this.phv = phv;
    }

    public short getPhh() {
        return phh;
    }

    public void setPhh(short phh) {
        this.phh = phh;
    }

    public Long getIdUrb() {
        return IdUrb;
    }

    public void setIdUrb(Long IdUrb) {
        this.IdUrb = IdUrb;
    }

    public String getMzUrb() {
        return mzUrb;
    }

    public void setMzUrb(String mzUrb) {
        this.mzUrb = mzUrb;
    }

    public String getSlUrb() {
        return slUrb;
    }

    public void setSlUrb(String slUrb) {
        this.slUrb = slUrb;
    }

}
