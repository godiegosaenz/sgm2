/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
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
@Table(name = "cat_pais", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPais.findAll", query = "SELECT c FROM CatPais c"),
    @NamedQuery(name = "CatPais.findById", query = "SELECT c FROM CatPais c WHERE c.id = :id"),
    @NamedQuery(name = "CatPais.findByCodigo", query = "SELECT c FROM CatPais c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "CatPais.findByDescripcion", query = "SELECT c FROM CatPais c WHERE c.descripcion = :descripcion")})
public class CatPais implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 4)
    @Column(name = "codigo", length = 4)
    private String codigo;
    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    @OneToMany(mappedBy = "idPais", fetch = FetchType.LAZY)
    private Collection<CatProvincia> catProvinciaCollection;
    @OneToMany(mappedBy = "pais")
    private List<CatEnte> catEntes;

    public CatPais() {
    }

    public CatPais(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatProvincia> getCatProvinciaCollection() {
        return catProvinciaCollection;
    }

    public void setCatProvinciaCollection(Collection<CatProvincia> catProvinciaCollection) {
        this.catProvinciaCollection = catProvinciaCollection;
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
        if (!(object instanceof CatPais)) {
            return false;
        }
        CatPais other = (CatPais) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPais[ id=" + id + " ]";
    }

    public List<CatEnte> getCatEntes() {
        return catEntes;
    }

    public void setCatEntes(List<CatEnte> catEntes) {
        this.catEntes = catEntes;
    }
    
}
