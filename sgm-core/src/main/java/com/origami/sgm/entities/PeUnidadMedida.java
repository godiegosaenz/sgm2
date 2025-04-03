/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.PeDetallePermisosAdicionales;
import com.origami.sgm.entities.PeDetalleEstructuraEspecial;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_unidad_medida",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeUnidadMedida.findAll", query = "SELECT p FROM PeUnidadMedida p"),
    @NamedQuery(name = "PeUnidadMedida.findById", query = "SELECT p FROM PeUnidadMedida p WHERE p.id = :id"),
    @NamedQuery(name = "PeUnidadMedida.findByDescripcion", query = "SELECT p FROM PeUnidadMedida p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "PeUnidadMedida.findByAbreviatura", query = "SELECT p FROM PeUnidadMedida p WHERE p.abreviatura = :abreviatura")})
public class PeUnidadMedida implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;
    @Size(max = 50)
    @Column(name = "abreviatura", length = 50)
    private String abreviatura;
    @OneToMany(mappedBy = "unidadMedida", fetch = FetchType.LAZY)
    private Collection<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unidadMedida", fetch = FetchType.LAZY)
    private Collection<PeDetalleEstructuraEspecial> peDetalleEstructuraEspecialCollection;

    public PeUnidadMedida() {
    }

    public PeUnidadMedida(Long id) {
        this.id = id;
    }

    public PeUnidadMedida(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
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

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeDetallePermisosAdicionales> getPeDetallePermisosAdicionalesCollection() {
        return peDetallePermisosAdicionalesCollection;
    }

    public void setPeDetallePermisosAdicionalesCollection(Collection<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesCollection) {
        this.peDetallePermisosAdicionalesCollection = peDetallePermisosAdicionalesCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeDetalleEstructuraEspecial> getPeDetalleEstructuraEspecialCollection() {
        return peDetalleEstructuraEspecialCollection;
    }

    public void setPeDetalleEstructuraEspecialCollection(Collection<PeDetalleEstructuraEspecial> peDetalleEstructuraEspecialCollection) {
        this.peDetalleEstructuraEspecialCollection = peDetalleEstructuraEspecialCollection;
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
        if (!(object instanceof PeUnidadMedida)) {
            return false;
        }
        PeUnidadMedida other = (PeUnidadMedida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeUnidadMedida[ id=" + id + " ]";
    }
    
}
