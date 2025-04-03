/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_liquidacion_detalles", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "RegpLiquidacionDetalles.findAll", query = "SELECT r FROM RegpLiquidacionDetalles r")})
public class RegpLiquidacionDetalles implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "num_predio")
    private Integer numPredio;
    @Column(name = "nom_urb")
    private String nomUrb;
    @Column(name = "mz_urb")
    private String mzUrb;
    @Column(name = "sl_urb")
    private String slUrb;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "avaluo", precision = 14, scale = 2)
    private BigDecimal avaluo;
    @Column(name = "cuantia", precision = 14, scale = 2)
    private BigDecimal cuantia;
    @Column(name = "cantidad")
    private Integer cantidad = 1;
    @Column(name = "valor_unitario", precision = 14, scale = 2)
    private BigDecimal valorUnitario = BigDecimal.ZERO;
    @Column(name = "descuento", precision = 14, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    @Column(name = "valor_total", precision = 14, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;
    @Column(name = "observacion")
    private String observacion;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpLiquidacionDerechosAranceles liquidacion;
    @JoinColumn(name = "acto", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpActosIngreso acto;

    public RegpLiquidacionDetalles() {
    }

    public RegpLiquidacionDetalles(Long id) {
        this.id = id;
    }

    public RegpLiquidacionDetalles(Long id, Integer cantidad, BigDecimal valorUnitario, BigDecimal descuento, BigDecimal valorTotal) {
        this.id = id;
        this.cantidad = cantidad;
        this.valorUnitario = valorUnitario;
        this.descuento = descuento;
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(Integer numPredio) {
        this.numPredio = numPredio;
    }

    public String getNomUrb() {
        return nomUrb;
    }

    public void setNomUrb(String nomUrb) {
        this.nomUrb = nomUrb;
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

    public BigDecimal getAvaluo() {
        return avaluo;
    }

    public void setAvaluo(BigDecimal avaluo) {
        this.avaluo = avaluo;
    }

    public BigDecimal getCuantia() {
        return cuantia;
    }

    public void setCuantia(BigDecimal cuantia) {
        this.cuantia = cuantia;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public RegpLiquidacionDerechosAranceles getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RegpLiquidacionDerechosAranceles liquidacion) {
        this.liquidacion = liquidacion;
    }

    public RegpActosIngreso getActo() {
        return acto;
    }

    public void setActo(RegpActosIngreso acto) {
        this.acto = acto;
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
        if (!(object instanceof RegpLiquidacionDetalles)) {
            return false;
        }
        RegpLiquidacionDetalles other = (RegpLiquidacionDetalles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpLiquidacionDetalles[ id=" + id + " ]";
    }
    
}
