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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_rangos_valores", schema = SchemasConfig.FINANCIERO)
public class RenRangosValores implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "valor_inicio")
    private BigDecimal valorInicio;
    @Column(name = "valor_fin")
    private BigDecimal valorFin;
    
    @JoinColumn(name = "tipo_rango", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoRango tipoRango;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public BigDecimal getValorInicio() {
        return valorInicio;
    }

    public void setValorInicio(BigDecimal valorInicio) {
        this.valorInicio = valorInicio;
    }

    public BigDecimal getValorFin() {
        return valorFin;
    }

    public void setValorFin(BigDecimal valorFin) {
        this.valorFin = valorFin;
    }

    public RenTipoRango getTipoRango() {
        return tipoRango;
    }

    public void setTipoRango(RenTipoRango tipoRango) {
        this.tipoRango = tipoRango;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RenRangosValores other = (RenRangosValores) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RenRangosValores{" + "id=" + id + '}';
    }
    
}
