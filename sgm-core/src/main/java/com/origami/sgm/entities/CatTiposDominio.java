/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.AclUser;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "cat_tipos_dominio", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatTiposDominio.findAll", query = "SELECT c FROM CatTiposDominio c"),
    @NamedQuery(name = "CatTiposDominio.findById", query = "SELECT c FROM CatTiposDominio c WHERE c.id = :id"),
    @NamedQuery(name = "CatTiposDominio.findByNombre", query = "SELECT c FROM CatTiposDominio c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "CatTiposDominio.findByFechaIngreso", query = "SELECT c FROM CatTiposDominio c WHERE c.fechaIngreso = :fechaIngreso")})
public class CatTiposDominio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "nombre", nullable = false, length = 80)
    @Expose
    private String nombre;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @JoinColumn(name = "usuario_ingreso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AclUser usuarioIngreso;
    @OneToMany(mappedBy = "traslDom", fetch = FetchType.LAZY)
    private Collection<CatEscritura> catEscrituraCollection;
    @OneToMany(mappedBy = "trasldom", fetch = FetchType.LAZY)
    private Collection<RegEscritura> regEscrituraCollection;

    public CatTiposDominio() {
    }

    public CatTiposDominio(Long id) {
        this.id = id;
    }

    public CatTiposDominio(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

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

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public AclUser getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(AclUser usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatEscritura> getCatEscrituraCollection() {
        return catEscrituraCollection;
    }

    public void setCatEscrituraCollection(Collection<CatEscritura> catEscrituraCollection) {
        this.catEscrituraCollection = catEscrituraCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegEscritura> getRegEscrituraCollection() {
        return regEscrituraCollection;
    }

    public void setRegEscrituraCollection(Collection<RegEscritura> regEscrituraCollection) {
        this.regEscrituraCollection = regEscrituraCollection;
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
        if (!(object instanceof CatTiposDominio)) {
            return false;
        }
        CatTiposDominio other = (CatTiposDominio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatTiposDominio[ id=" + id + " ]";
    }
    
}
