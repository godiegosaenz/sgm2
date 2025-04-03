/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.PeDetallePermiso;
import com.origami.sgm.entities.PePermiso;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_permiso_cab_edificacion",  schema = SchemasConfig.APP1)
@FilterDef(name = "activos", 
    defaultCondition = "estado = "+ SchemasConfig.FILTER_ESTADO)
@NamedQueries({
    @NamedQuery(name = "PePermisoCabEdificacion.findAll", query = "SELECT p FROM PePermisoCabEdificacion p"),
    @NamedQuery(name = "PePermisoCabEdificacion.findById", query = "SELECT p FROM PePermisoCabEdificacion p WHERE p.id = :id"),
    @NamedQuery(name = "PePermisoCabEdificacion.findByAreaConstruccion", query = "SELECT p FROM PePermisoCabEdificacion p WHERE p.areaConstruccion = :areaConstruccion"),
    @NamedQuery(name = "PePermisoCabEdificacion.findByNumeroPisos", query = "SELECT p FROM PePermisoCabEdificacion p WHERE p.numeroPisos = :numeroPisos"),
    @NamedQuery(name = "PePermisoCabEdificacion.findByNumEdificacion", query = "SELECT p FROM PePermisoCabEdificacion p WHERE p.numEdificacion = :numEdificacion"),
    @NamedQuery(name = "PePermisoCabEdificacion.findByDescEdificacion", query = "SELECT p FROM PePermisoCabEdificacion p WHERE p.descEdificacion = :descEdificacion")})
public class PePermisoCabEdificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_construccion", precision = 15, scale = 2)
    private BigDecimal areaConstruccion;
    @Column(name = "numero_pisos")
    private Short numeroPisos;
    @Column(name = "estado")
    private Boolean estado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_edificacion")
    private Short numEdificacion;
    @Size(max = 100)
    @Column(name = "desc_edificacion", length = 100)
    private String descEdificacion;
    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    @JoinColumn(name = "pe_permiso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PePermiso idPermiso;
    @OneToMany(mappedBy = "idPermisoEdificacion")
    @Where(clause = "estado")
    private Collection<PeDetallePermiso> peDetallePermisoCollection = new ArrayList<>();

    public PePermisoCabEdificacion() {
    }

    public PePermisoCabEdificacion(Long id) {
        this.id = id;
    }

    public PePermisoCabEdificacion(Long id, Short numEdificacion) {
        this.id = id;
        this.numEdificacion = numEdificacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public Short getNumeroPisos() {
        return numeroPisos;
    }

    public void setNumeroPisos(Short numeroPisos) {
        this.numeroPisos = numeroPisos;
    }

    public Short getNumEdificacion() {
        return numEdificacion;
    }

    public void setNumEdificacion(Short numEdificacion) {
        this.numEdificacion = numEdificacion;
    }

    public String getDescEdificacion() {
        return descEdificacion;
    }

    public void setDescEdificacion(String descEdificacion) {
        this.descEdificacion = descEdificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public PePermiso getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(PePermiso idPermiso) {
        this.idPermiso = idPermiso;
    }

    public Collection<PeDetallePermiso> getPeDetallePermisoCollection() {
        return peDetallePermisoCollection;
    }

    public void setPeDetallePermisoCollection(Collection<PeDetallePermiso> peDetallePermisoCollection) {
        this.peDetallePermisoCollection = peDetallePermisoCollection;
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
        if (!(object instanceof PePermisoCabEdificacion)) {
            return false;
        }
        PePermisoCabEdificacion other = (PePermisoCabEdificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PePermisoCabEdificacion[ id=" + id + " ]";
    }
    
}
