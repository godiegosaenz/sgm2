/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Xndy Sxnchez :v
 */
@Entity
@Table(name = "aval_valor_suelo", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"zona", "sector", "mz", "parroquia", "anio_inicio", "anio_fin"})})
@XmlRootElement
@SequenceGenerator(name = "aval_valor_suelo_id_seq", sequenceName = SchemasConfig.APP1 + ".aval_valor_suelo_id_seq", allocationSize = 1)
public class AvalValorSuelo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aval_valor_suelo_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private short parroquia;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private short zona;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private short sector;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private short mz;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_m2", nullable = false, precision = 12, scale = 4)
    private BigDecimal valorM2;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    private short solar;

    public AvalValorSuelo() {
    }

    public AvalValorSuelo(Long id) {
        this.id = id;
    }

    public AvalValorSuelo(Long id, short parroquia, short zona, short sector, short mz, BigDecimal valorM2) {
        this.id = id;
        this.parroquia = parroquia;
        this.zona = zona;
        this.sector = sector;
        this.mz = mz;
        this.valorM2 = valorM2;
    }

    public short getSolar() {
        return solar;
    }

    public void setSolar(short solar) {
        this.solar = solar;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public short getParroquia() {
        return parroquia;
    }

    public void setParroquia(short parroquia) {
        this.parroquia = parroquia;
    }

    public short getZona() {
        return zona;
    }

    public void setZona(short zona) {
        this.zona = zona;
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

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
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
        if (!(object instanceof AvalValorSuelo)) {
            return false;
        }
        AvalValorSuelo other = (AvalValorSuelo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AvalValorSuelo{" + "id=" + id + ", parroquia=" + parroquia + ", zona=" + zona + ", sector=" + sector + ", mz=" + mz + ", valorM2=" + valorM2 + ", anioInicio=" + anioInicio + ", anioFin=" + anioFin + ", solar=" + solar + '}';
    }

    
    
}
