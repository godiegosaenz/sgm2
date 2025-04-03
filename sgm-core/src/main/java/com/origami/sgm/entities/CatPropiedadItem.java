/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "cat_propiedad_item", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codename"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPropiedadItem.findAll", query = "SELECT c FROM CatPropiedadItem c")
    ,
    @NamedQuery(name = "CatPropiedadItem.findById", query = "SELECT c FROM CatPropiedadItem c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPropiedadItem.findByNombre", query = "SELECT c FROM CatPropiedadItem c WHERE c.nombre = :nombre")
    ,
    @NamedQuery(name = "CatPropiedadItem.findByCodename", query = "SELECT c FROM CatPropiedadItem c WHERE c.codename = :codename")
    ,
    @NamedQuery(name = "CatPropiedadItem.findByOrden", query = "SELECT c FROM CatPropiedadItem c WHERE c.orden = :orden")})
public class CatPropiedadItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    @Expose
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "codename", nullable = false, length = 20)
    private String codename;
    @Column(name = "orden")
    private BigInteger orden;
    @OneToMany(mappedBy = "propiedad", fetch = FetchType.LAZY)
    private Collection<CatPredio> catPredioCollection;

    public CatPropiedadItem() {
    }

    public CatPropiedadItem(Long id) {
        this.id = id;
    }

    public CatPropiedadItem(Long id, String nombre, String codename) {
        this.id = id;
        this.nombre = nombre;
        this.codename = codename;
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

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public BigInteger getOrden() {
        return orden;
    }

    public void setOrden(BigInteger orden) {
        this.orden = orden;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredio> getCatPredioCollection() {
        return catPredioCollection;
    }

    public void setCatPredioCollection(Collection<CatPredio> catPredioCollection) {
        this.catPredioCollection = catPredioCollection;
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
        if (!(object instanceof CatPropiedadItem)) {
            return false;
        }
        CatPropiedadItem other = (CatPropiedadItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPropiedadItem[ id=" + id + " ]";
    }

}
