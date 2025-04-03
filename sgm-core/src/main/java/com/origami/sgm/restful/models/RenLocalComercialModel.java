/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.restful.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Angel Navarro
 */
public class RenLocalComercialModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombreLocal;
    private BigInteger numPredio;
    private String numLocal;
    private BigDecimal area;
    private Boolean turismo;
    private Boolean contabilidad = false;
    private Collection<DatosRenLiquidacion> renLiquidacionCollection;
    private String tipoLocal;
    private String categoria;
    private String ubicacion;
    private String actividad;
    private String razonSocial;
    private Date inicioActividad;
    private Boolean matriz;

    public RenLocalComercialModel() {
    }

    public RenLocalComercialModel(Long id) {
        this.id = id;
    }

    public RenLocalComercialModel(Long id, String nombreLocal, BigInteger numPredio, String numLocal, BigDecimal area, Boolean turismo, Collection<DatosRenLiquidacion> renLiquidacionCollection, String tipoLocal, String categoria, String ubicacion, String actividad, String razonSocial, Date inicioActividad, Boolean matriz) {
        this.id = id;
        this.nombreLocal = nombreLocal;
        this.numPredio = numPredio;
        this.numLocal = numLocal;
        this.area = area;
        this.turismo = turismo;
        this.renLiquidacionCollection = renLiquidacionCollection;
        this.tipoLocal = tipoLocal;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.actividad = actividad;
        this.razonSocial = razonSocial;
        this.inicioActividad = inicioActividad;
        this.matriz = matriz;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreLocal() {
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public String getNumLocal() {
        return numLocal;
    }

    public void setNumLocal(String numLocal) {
        this.numLocal = numLocal;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Boolean getTurismo() {
        return turismo;
    }

    public void setTurismo(Boolean turismo) {
        this.turismo = turismo;
    }

    public Boolean getContabilidad() {
        return contabilidad;
    }

    public void setContabilidad(Boolean contabilidad) {
        this.contabilidad = contabilidad;
    }

    public Collection<DatosRenLiquidacion> getRenLiquidacionCollection() {
        return renLiquidacionCollection;
    }

    public void setRenLiquidacionCollection(Collection<DatosRenLiquidacion> renLiquidacionCollection) {
        this.renLiquidacionCollection = renLiquidacionCollection;
    }

    public String getTipoLocal() {
        return tipoLocal;
    }

    public void setTipoLocal(String tipoLocal) {
        this.tipoLocal = tipoLocal;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Date getInicioActividad() {
        return inicioActividad;
    }

    public void setInicioActividad(Date inicioActividad) {
        this.inicioActividad = inicioActividad;
    }

    public Boolean getMatriz() {
        return matriz;
    }

    public void setMatriz(Boolean matriz) {
        this.matriz = matriz;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
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
        if (!(object instanceof RenLocalComercialModel)) {
            return false;
        }
        RenLocalComercialModel other = (RenLocalComercialModel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.restful.models[ id=" + id + " ]";
    }

}
