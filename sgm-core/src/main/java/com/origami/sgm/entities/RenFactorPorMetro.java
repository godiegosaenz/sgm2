/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "ren_factor_por_metro", schema = SchemasConfig.FINANCIERO)
public class RenFactorPorMetro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "anio_desde")
    private Integer anioDesde;
    @Column(name = "anio_hasta")
    private Integer anioHasta;
    @Column(name = "desde")
    private BigDecimal desde;
    @Column(name = "hasta")
    private BigDecimal hasta;
    @Column(name = "fraccion")
    private BigDecimal fraccion;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "salario_basico")
    private BigDecimal salarioBasico;
    @Column(name = "porcentaje_salario_basico")
    private BigDecimal porcentajeSalarioBasico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(Integer anioDesde) {
        this.anioDesde = anioDesde;
    }

    public Integer getAnioHasta() {
        return anioHasta;
    }

    public void setAnioHasta(Integer anioHasta) {
        this.anioHasta = anioHasta;
    }

    public BigDecimal getDesde() {
        return desde;
    }

    public void setDesde(BigDecimal desde) {
        this.desde = desde;
    }

    public BigDecimal getHasta() {
        return hasta;
    }

    public void setHasta(BigDecimal hasta) {
        this.hasta = hasta;
    }

    public BigDecimal getFraccion() {
        return fraccion;
    }

    public void setFraccion(BigDecimal fraccion) {
        this.fraccion = fraccion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSalarioBasico() {
        return salarioBasico;
    }

    public void setSalarioBasico(BigDecimal salarioBasico) {
        this.salarioBasico = salarioBasico;
    }

    public BigDecimal getPorcentajeSalarioBasico() {
        return porcentajeSalarioBasico;
    }

    public void setPorcentajeSalarioBasico(BigDecimal porcentajeSalarioBasico) {
        this.porcentajeSalarioBasico = porcentajeSalarioBasico;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final RenFactorPorMetro other = (RenFactorPorMetro) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "RenFactorPorCapital{" + "id=" + id + '}';
    }
    
}
