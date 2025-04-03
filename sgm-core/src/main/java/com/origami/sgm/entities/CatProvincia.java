/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPais;
import com.origami.sgm.entities.CatCanton;
import com.google.gson.annotations.Expose;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
 import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_provincia", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatProvincia.findAll", query = "SELECT c FROM CatProvincia c"),
    @NamedQuery(name = "CatProvincia.findById", query = "SELECT c FROM CatProvincia c WHERE c.id = :id"),
    @NamedQuery(name = "CatProvincia.findByDescripcion", query = "SELECT c FROM CatProvincia c WHERE c.descripcion = :descripcion")})
public class CatProvincia implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Size(max = 50)
    @Column(name = "descripcion", length = 50)
    @Expose
    private String descripcion;
    @JoinColumn(name = "id_pais", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPais idPais;
    @OneToMany(mappedBy = "idProvincia", fetch = FetchType.LAZY)
    @Expose
    private Collection<CatCanton> catCantonCollection;
    @Column(name = "cod_nac")
    @Expose
    private Short codNac;
    

    public CatProvincia() {
    }

    public CatProvincia(Long id) {
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

    public CatPais getIdPais() {
        return idPais;
    }

    public void setIdPais(CatPais idPais) {
        this.idPais = idPais;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatCanton> getCatCantonCollection() {
        return catCantonCollection;
    }

    public void setCatCantonCollection(Collection<CatCanton> catCantonCollection) {
        this.catCantonCollection = catCantonCollection;
    }

    public Short getCodNac() {
        return codNac;
    }

    public void setCodNac(Short codNac) {
        this.codNac = codNac;
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
        if (!(object instanceof CatProvincia)) {
            return false;
        }
        CatProvincia other = (CatProvincia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatProvincia[ id=" + id + " ]";
    }
    
}
