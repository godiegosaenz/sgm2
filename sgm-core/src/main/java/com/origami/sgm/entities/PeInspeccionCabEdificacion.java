/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_inspeccion_cab_edificacion", schema = SchemasConfig.APP1)
@XmlRootElement 

@NamedQueries({
    @NamedQuery(name = "PeInspeccionCabEdificacion.findAll", query = "SELECT p FROM PeInspeccionCabEdificacion p"),
    @NamedQuery(name = "PeInspeccionCabEdificacion.findById", query = "SELECT p FROM PeInspeccionCabEdificacion p WHERE p.id = :id"),
    @NamedQuery(name = "PeInspeccionCabEdificacion.findByNumEdificacion", query = "SELECT p FROM PeInspeccionCabEdificacion p WHERE p.numEdificacion = :numEdificacion"),
    @NamedQuery(name = "PeInspeccionCabEdificacion.findByCantidadPisos", query = "SELECT p FROM PeInspeccionCabEdificacion p WHERE p.cantidadPisos = :cantidadPisos"),
    @NamedQuery(name = "PeInspeccionCabEdificacion.findByAreaConstruccion", query = "SELECT p FROM PeInspeccionCabEdificacion p WHERE p.areaConstruccion = :areaConstruccion"),
    @NamedQuery(name = "PeInspeccionCabEdificacion.findByDescEdificacion", query = "SELECT p FROM PeInspeccionCabEdificacion p WHERE p.descEdificacion = :descEdificacion")})
public class PeInspeccionCabEdificacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_edificacion")
    private Integer numEdificacion;
    @Column(name = "cantidad_pisos")
    private Integer cantidadPisos;
    @Column(name = "estado")
    private Boolean estado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_construccion", precision = 15, scale = 2)
    private BigDecimal areaConstruccion;
    @Size(max = 100)
    @Column(name = "desc_edificacion", length = 100)
    private String descEdificacion;
    @JoinColumn(name = "id_inspeccion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeInspeccionFinal idInspeccion;
    @OneToMany(mappedBy = "edificacion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<PeDetalleInspeccion> peDetalleInspeccionCollection;
    
    public PeInspeccionCabEdificacion() {
    }
    
    public PeInspeccionCabEdificacion(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getNumEdificacion() {
        return numEdificacion;
    }
    
    public void setNumEdificacion(Integer numEdificacion) {
        this.numEdificacion = numEdificacion;
    }
    
    public Integer getCantidadPisos() {
        return cantidadPisos;
    }
    
    public void setCantidadPisos(Integer cantidadPisos) {
        this.cantidadPisos = cantidadPisos;
    }
    
    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }
    
    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }
    
    public String getDescEdificacion() {
        return descEdificacion;
    }
    
    public void setDescEdificacion(String descEdificacion) {
        this.descEdificacion = descEdificacion;
    }
    
    public PeInspeccionFinal getIdInspeccion() {
        return idInspeccion;
    }
    
    public void setIdInspeccion(PeInspeccionFinal idInspeccion) {
        this.idInspeccion = idInspeccion;
    }
    
    @XmlTransient
    @JsonIgnore
    public Collection<PeDetalleInspeccion> getPeDetalleInspeccionCollection() {
        return peDetalleInspeccionCollection;
    }
    
    public void setPeDetalleInspeccionCollection(Collection<PeDetalleInspeccion> peDetalleInspeccionCollection) {
        this.peDetalleInspeccionCollection = peDetalleInspeccionCollection;
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
        if (!(object instanceof PeInspeccionCabEdificacion)) {
            return false;
        }
        PeInspeccionCabEdificacion other = (PeInspeccionCabEdificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "gob.samborondon.entities.PeInspeccionCabEdificacion[ id=" + id + " ]";
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
}
