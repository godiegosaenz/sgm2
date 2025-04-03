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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author XndySxnchez
 */
@Entity
@Table(name = "aval_depreciacion_solar", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"anios", "espec"})})
public class AvalDepreciacionSolar implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anios", nullable = false)
    private short anios;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "bueno", nullable = false, precision = 7, scale = 6)
    private BigDecimal bueno;
    @Basic(optional = false)
    @NotNull
    @Column(name = "regular", nullable = false, precision = 7, scale = 6)
    private BigDecimal regular;
    @Basic(optional = false)
    @NotNull
    @Column(name = "malo", nullable = false, precision = 7, scale = 6)
    private BigDecimal malo;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @JoinColumn(name = "espec", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatEdfProp espec;

    public AvalDepreciacionSolar() {
    }

    public AvalDepreciacionSolar(Integer id) {
        this.id = id;
    }

    public CatEdfProp getEspec() {
        return espec;
    }

    public void setEspec(CatEdfProp espec) {
        this.espec = espec;
    }

    public AvalDepreciacionSolar(Integer id, short anios, BigDecimal bueno, BigDecimal regular, BigDecimal malo) {
        this.id = id;
        this.anios = anios;
        this.bueno = bueno;
        this.regular = regular;
        this.malo = malo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public short getAnios() {
        return anios;
    }

    public void setAnios(short anios) {
        this.anios = anios;
    }

    public BigDecimal getBueno() {
        return bueno;
    }

    public void setBueno(BigDecimal bueno) {
        this.bueno = bueno;
    }

    public BigDecimal getRegular() {
        return regular;
    }

    public void setRegular(BigDecimal regular) {
        this.regular = regular;
    }

    public BigDecimal getMalo() {
        return malo;
    }

    public void setMalo(BigDecimal malo) {
        this.malo = malo;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
    }

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
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
        if (!(object instanceof AvalDepreciacionSolar)) {
            return false;
        }
        AvalDepreciacionSolar other = (AvalDepreciacionSolar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.AvalDepreciacionSolar[ id=" + id + " ]";
    }

}
