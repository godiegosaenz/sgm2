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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_detalle_estructura_especial", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeDetalleEstructuraEspecial.findAll", query = "SELECT p FROM PeDetalleEstructuraEspecial p"),
    @NamedQuery(name = "PeDetalleEstructuraEspecial.findById", query = "SELECT p FROM PeDetalleEstructuraEspecial p WHERE p.id = :id"),
    @NamedQuery(name = "PeDetalleEstructuraEspecial.findByDescripcion", query = "SELECT p FROM PeDetalleEstructuraEspecial p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "PeDetalleEstructuraEspecial.findByCantidad", query = "SELECT p FROM PeDetalleEstructuraEspecial p WHERE p.cantidad = :cantidad")})
public class PeDetalleEstructuraEspecial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad", precision = 15, scale = 2)
    private BigDecimal cantidad;
    @JoinColumn(name = "unidad_medida", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PeUnidadMedida unidadMedida;
    @JoinColumn(name = "id_tipo_estructura", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PeTiposEstructuraEspecial idTipoEstructura;
    @JoinColumn(name = "id_estructura_especial", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PeEstructuraEspecial idEstructuraEspecial;

    public PeDetalleEstructuraEspecial() {
    }

    public PeDetalleEstructuraEspecial(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public PeUnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(PeUnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public PeTiposEstructuraEspecial getIdTipoEstructura() {
        return idTipoEstructura;
    }

    public void setIdTipoEstructura(PeTiposEstructuraEspecial idTipoEstructura) {
        this.idTipoEstructura = idTipoEstructura;
    }

    public PeEstructuraEspecial getIdEstructuraEspecial() {
        return idEstructuraEspecial;
    }

    public void setIdEstructuraEspecial(PeEstructuraEspecial idEstructuraEspecial) {
        this.idEstructuraEspecial = idEstructuraEspecial;
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
        if (!(object instanceof PeDetalleEstructuraEspecial)) {
            return false;
        }
        PeDetalleEstructuraEspecial other = (PeDetalleEstructuraEspecial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeDetalleEstructuraEspecial[ id=" + id + " ]";
    }
    
}
