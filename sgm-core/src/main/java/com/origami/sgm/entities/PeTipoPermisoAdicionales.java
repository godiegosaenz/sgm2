/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_tipo_permiso_adicionales",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeTipoPermisoAdicionales.findAll", query = "SELECT p FROM PeTipoPermisoAdicionales p"),
    @NamedQuery(name = "PeTipoPermisoAdicionales.findById", query = "SELECT p FROM PeTipoPermisoAdicionales p WHERE p.id = :id"),
    @NamedQuery(name = "PeTipoPermisoAdicionales.findByDescripcion", query = "SELECT p FROM PeTipoPermisoAdicionales p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "PeTipoPermisoAdicionales.findByCodigo", query = "SELECT p FROM PeTipoPermisoAdicionales p WHERE p.codigo = :codigo")})
public class PeTipoPermisoAdicionales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 60)
    @Column(name = "descripcion", length = 60)
    private String descripcion;
    @Size(max = 10)
    @Column(name = "codigo", length = 10)
    private String codigo;
    @OneToMany(mappedBy = "tipoPermisoAdicional", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection;

    public PeTipoPermisoAdicionales() {
    }

    public PeTipoPermisoAdicionales(Long id) {
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection() {
        return pePermisosAdicionalesCollection;
    }

    public void setPePermisosAdicionalesCollection(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection) {
        this.pePermisosAdicionalesCollection = pePermisosAdicionalesCollection;
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
        if (!(object instanceof PeTipoPermisoAdicionales)) {
            return false;
        }
        PeTipoPermisoAdicionales other = (PeTipoPermisoAdicionales) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeTipoPermisoAdicionales[ id=" + id + " ]";
    }
    
}
