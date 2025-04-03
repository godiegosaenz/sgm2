/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.MejObra;
import com.origami.sgm.entities.CatUbicacion;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Henry Pilco
 */
@Entity
@Table(name = "mej_valores_obra_ubicacion", schema = SchemasConfig.MEJORAS)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MejValoresObraUbicacion.findAll", query = "SELECT m FROM MejValoresObraUbicacion m")})
public class MejValoresObraUbicacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "obra", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MejObra obra;
    @JoinColumn(name = "ubicacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatUbicacion ubicacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
    @Column(name = "valor_recuperar")
    private BigDecimal valorRecuperar;
    @Column(name = "total_cem_anio")
    private BigDecimal totalCemAnio;

    public MejValoresObraUbicacion() {
    }

    public MejValoresObraUbicacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MejObra getObra() {
        return obra;
    }

    public void setObra(MejObra obra) {
        this.obra = obra;
    }

    public CatUbicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(CatUbicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public BigDecimal getValorRecuperar() {
        return valorRecuperar;
    }

    public void setValorRecuperar(BigDecimal valorRecuperar) {
        this.valorRecuperar = valorRecuperar;
    }

    public BigDecimal getTotalCemAnio() {
        return totalCemAnio;
    }

    public void setTotalCemAnio(BigDecimal totalCemAnio) {
        this.totalCemAnio = totalCemAnio;
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
        if (!(object instanceof MejValoresObraUbicacion)) {
            return false;
        }
        MejValoresObraUbicacion other = (MejValoresObraUbicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.MejValoresObraUbicacion[ id=" + id + " ]";
    }

}
