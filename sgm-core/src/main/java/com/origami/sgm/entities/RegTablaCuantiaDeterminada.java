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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "reg_tabla_cuantia_determinada", schema = SchemasConfig.APP1)
@NamedQueries({
    @NamedQuery(name = "RegTablaCuantiaDeterminada.findAll", query = "SELECT r FROM RegTablaCuantiaDeterminada r")})
public class RegTablaCuantiaDeterminada implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "valor_inicial", precision = 12, scale = 2)
    private BigDecimal valorInicial;
    @Column(name = "valor_final", precision = 12, scale = 2)
    private BigDecimal valorFinal;
    @Column(name = "total_cobrar", precision = 12, scale = 2)
    private BigDecimal totalCobrar;
    @Column(name = "exceso_valor", precision = 12, scale = 6)
    private BigDecimal excesoValor;
    @Column(name = "valor_base", precision = 12, scale = 2)
    private BigDecimal valorBase;
    @Column(name = "cantidad_base", precision = 12, scale = 2)
    private BigDecimal cantidadBase;

    public RegTablaCuantiaDeterminada() {
    }

    public RegTablaCuantiaDeterminada(Long id) {
        this.id = id;
    }

    public RegTablaCuantiaDeterminada(Long id, BigDecimal valorInicial) {
        this.id = id;
        this.valorInicial = valorInicial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getTotalCobrar() {
        return totalCobrar;
    }

    public void setTotalCobrar(BigDecimal totalCobrar) {
        this.totalCobrar = totalCobrar;
    }

    public BigDecimal getExcesoValor() {
        return excesoValor;
    }

    public void setExcesoValor(BigDecimal excesoValor) {
        this.excesoValor = excesoValor;
    }

    public BigDecimal getValorBase() {
        return valorBase;
    }

    public void setValorBase(BigDecimal valorBase) {
        this.valorBase = valorBase;
    }

    public BigDecimal getCantidadBase() {
        return cantidadBase;
    }

    public void setCantidadBase(BigDecimal cantidadBase) {
        this.cantidadBase = cantidadBase;
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
        if (!(object instanceof RegTablaCuantiaDeterminada)) {
            return false;
        }
        RegTablaCuantiaDeterminada other = (RegTablaCuantiaDeterminada) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegTablaCuantiaDeterminada[ id=" + id + " ]";
    }
    
}
