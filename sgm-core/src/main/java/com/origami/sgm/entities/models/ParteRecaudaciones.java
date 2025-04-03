/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Angel Navarro
 * @date 26/07/2016
 */
public class ParteRecaudaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger id;
    private String codigo;
    private String descripcion;
    private String ctaTransaccion;
    private Integer orden;
    private Integer padre;
    private Integer tipo;
    private Boolean anioAnterior;

    private BigDecimal valorAcumulado;

    public ParteRecaudaciones() {
    }

    public ParteRecaudaciones(BigInteger id) {
        this.id = id;
    }

    public ParteRecaudaciones(BigInteger id, Integer orden) {
        this.id = id;
        this.orden = orden;
    }

    public ParteRecaudaciones(BigInteger id, String codigo, String descripcion, String ctaTransaccion, Integer orden, Integer padre, BigDecimal valorAcumulado, Integer tipo) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.ctaTransaccion = ctaTransaccion;
        this.orden = orden;
        this.padre = padre;
        this.tipo = tipo;
        this.valorAcumulado = valorAcumulado;
    }
    
    

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCtaTransaccion() {
        return ctaTransaccion;
    }

    public void setCtaTransaccion(String ctaTransaccion) {
        this.ctaTransaccion = ctaTransaccion;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getPadre() {
        return padre;
    }

    public void setPadre(Integer padre) {
        this.padre = padre;
    }

    public BigDecimal getValorAcumulado() {
        return valorAcumulado;
    }

    public void setValorAcumulado(BigDecimal valorAcumulado) {
        this.valorAcumulado = valorAcumulado;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Boolean getAnioAnterior() {
        return anioAnterior;
    }

    public void setAnioAnterior(Boolean anioAnterior) {
        this.anioAnterior = anioAnterior;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if ((object instanceof ParteRecaudaciones)) {
            ParteRecaudaciones other = (ParteRecaudaciones) object;
            if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
                return false;
            }
        } else if ((object instanceof com.origami.sgm.entities.ParteRecaudaciones)) {
            com.origami.sgm.entities.ParteRecaudaciones other = (com.origami.sgm.entities.ParteRecaudaciones) object;
            if ((this.id == null && other.getId() != null) || (this.id != null && !(this.id.longValue() == other.getId()))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.models.ParteRecaudaciones[ id=" + id + " ]";
    }
    
    

    
    
}
