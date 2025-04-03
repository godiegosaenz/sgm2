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
import org.hibernate.annotations.FilterDef;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_detalle_permisos_adicionales", schema = SchemasConfig.APP1)
@XmlRootElement
@FilterDef(name = "activos", 
    defaultCondition = "estado = "+ SchemasConfig.FILTER_ESTADO)
@NamedQueries({
    @NamedQuery(name = "PeDetallePermisosAdicionales.findAll", query = "SELECT p FROM PeDetallePermisosAdicionales p"),
    @NamedQuery(name = "PeDetallePermisosAdicionales.findById", query = "SELECT p FROM PeDetallePermisosAdicionales p WHERE p.id = :id"),
    @NamedQuery(name = "PeDetallePermisosAdicionales.findByArea", query = "SELECT p FROM PeDetallePermisosAdicionales p WHERE p.area = :area"),
    @NamedQuery(name = "PeDetallePermisosAdicionales.findByDescripcion", query = "SELECT p FROM PeDetallePermisosAdicionales p WHERE p.descripcion = :descripcion")})
public class PeDetallePermisosAdicionales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area", precision = 15, scale = 2)
    private BigDecimal area;
    @Size(max = 60)
    @Column(name = "descripcion", length = 60)
    private String descripcion;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "unidad_medida", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeUnidadMedida unidadMedida;
    @JoinColumn(name = "id_permisos_adicionales", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PePermisosAdicionales idPermisosAdicionales;

    public PeDetallePermisosAdicionales() {
    }

    public PeDetallePermisosAdicionales(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public PeUnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(PeUnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public PePermisosAdicionales getIdPermisosAdicionales() {
        return idPermisosAdicionales;
    }

    public void setIdPermisosAdicionales(PePermisosAdicionales idPermisosAdicionales) {
        this.idPermisosAdicionales = idPermisosAdicionales;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
        if (!(object instanceof PeDetallePermisosAdicionales)) {
            return false;
        }
        PeDetallePermisosAdicionales other = (PeDetallePermisosAdicionales) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeDetallePermisosAdicionales[ id=" + id + " ]";
    }
    
}
