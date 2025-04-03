/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "acl_rol", schema = SchemasConfig.APP1)
@SequenceGenerator(name = "acl_rol_id_seq", sequenceName = SchemasConfig.APP1 + ".acl_rol_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "AclRol.findAll", query = "SELECT a FROM AclRol a where a.departamento IS NULL")
    ,
    @NamedQuery(name = "AclRol.findAllByEstado", query = "SELECT a FROM AclRol a where a.estado = :estado")
})
public class AclRol implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acl_rol_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "is_director")
    private Boolean isDirector = Boolean.FALSE;
    @Column(name = "es_sub_director")
    private Boolean esSubDirector = Boolean.FALSE;
    @Column(name = "estado")
    private Boolean estado = Boolean.TRUE;
    @OneToMany(mappedBy = "rol")
    private Collection<GeTipoTramite> geTipoTramiteCollection;
    @JoinColumn(name = "departamento", referencedColumnName = "id")
    @ManyToOne
    private GeDepartamento departamento;
    @ManyToMany(mappedBy = "aclRolCollection", fetch = FetchType.LAZY)
    @OrderBy("usuario ASC")
    private Collection<AclUser> aclUserCollection;

//    @OneToMany(mappedBy = "url")
//    private Collection<AclUrlHasRol> urlHasRolColl;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getIsDirector() {
        return isDirector;
    }

    public void setIsDirector(Boolean isDirector) {
        this.isDirector = isDirector;
    }

    public Boolean getEsSubDirector() {
        return esSubDirector;
    }

    public void setEsSubDirector(Boolean esSubDirector) {
        this.esSubDirector = esSubDirector;
    }

    public Collection<AclUser> getAclUserCollection() {
        return aclUserCollection;
    }

    public void setAclUserCollection(Collection<AclUser> aclUserCollection) {
        this.aclUserCollection = aclUserCollection;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTipoTramite> getGeTipoTramiteCollection() {
        return geTipoTramiteCollection;
    }

    public void setGeTipoTramiteCollection(Collection<GeTipoTramite> geTipoTramiteCollection) {
        this.geTipoTramiteCollection = geTipoTramiteCollection;
    }

    public AclRol() {
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
        if (!(object instanceof AclRol)) {
            return false;
        }
        AclRol other = (AclRol) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.AclRol[ id=" + id + " ]";
    }
}
