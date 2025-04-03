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
@Table(name = "cat_normas_construccion_tipo", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatNormasConstruccionTipo.findAll", query = "SELECT c FROM CatNormasConstruccionTipo c"),
    @NamedQuery(name = "CatNormasConstruccionTipo.findById", query = "SELECT c FROM CatNormasConstruccionTipo c WHERE c.id = :id"),
    @NamedQuery(name = "CatNormasConstruccionTipo.findByTipo", query = "SELECT c FROM CatNormasConstruccionTipo c WHERE c.tipo = :tipo"),
    @NamedQuery(name = "CatNormasConstruccionTipo.findByIsEspecial", query = "SELECT c FROM CatNormasConstruccionTipo c WHERE c.isEspecial = :isEspecial")})
public class CatNormasConstruccionTipo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 200)
    @Column(name = "tipo", length = 200)
    private String tipo;
    @Column(name = "is_especial")
    private Boolean isEspecial;
    @OneToMany(mappedBy = "tipoNorma", fetch = FetchType.LAZY)
    private Collection<CatNormasConstruccion> catNormasConstruccionCollection;

    public CatNormasConstruccionTipo() {
    }

    public CatNormasConstruccionTipo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getIsEspecial() {
        return isEspecial;
    }

    public void setIsEspecial(Boolean isEspecial) {
        this.isEspecial = isEspecial;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatNormasConstruccion> getCatNormasConstruccionCollection() {
        return catNormasConstruccionCollection;
    }

    public void setCatNormasConstruccionCollection(Collection<CatNormasConstruccion> catNormasConstruccionCollection) {
        this.catNormasConstruccionCollection = catNormasConstruccionCollection;
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
        if (!(object instanceof CatNormasConstruccionTipo)) {
            return false;
        }
        CatNormasConstruccionTipo other = (CatNormasConstruccionTipo) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatNormasConstruccionTipo[ id=" + id + " ]";
    }
    
}
