/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "cat_rango_avaluos_rusticos", schema = SchemasConfig.APP1)
public class CatRangoAvaluosRustico implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "anio_desde")
    private BigInteger anioDesde;
    @Column(name = "anio_hasta")
    private BigInteger anioHasta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_desde")
    private BigDecimal valorDesde;
    @Column(name = "valor_hasta")
    private BigDecimal valorHasta;
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(BigInteger anioDesde) {
        this.anioDesde = anioDesde;
    }

    public BigInteger getAnioHasta() {
        return anioHasta;
    }

    public void setAnioHasta(BigInteger anioHasta) {
        this.anioHasta = anioHasta;
    }

    public BigDecimal getValorDesde() {
        return valorDesde;
    }

    public void setValorDesde(BigDecimal valorDesde) {
        this.valorDesde = valorDesde;
    }

    public BigDecimal getValorHasta() {
        return valorHasta;
    }

    public void setValorHasta(BigDecimal valorHasta) {
        this.valorHasta = valorHasta;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CatRangoAvaluosRustico other = (CatRangoAvaluosRustico) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CatRangoAvaluosRustico{" + "id=" + id + '}';
    }
    
}
