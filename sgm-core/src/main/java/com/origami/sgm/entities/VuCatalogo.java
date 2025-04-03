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
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "vu_catalogo",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VuCatalogo.findAll", query = "SELECT v FROM VuCatalogo v"),
    @NamedQuery(name = "VuCatalogo.findById", query = "SELECT v FROM VuCatalogo v WHERE v.id = :id"),
    @NamedQuery(name = "VuCatalogo.findByNombre", query = "SELECT v FROM VuCatalogo v WHERE v.nombre = :nombre")})
public class VuCatalogo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 200)
    @Column(name = "nombre", length = 200)
    private String nombre;
    @OneToMany(mappedBy = "catalogo", fetch = FetchType.LAZY)
    @OrderBy("nombre ASC")
    private Collection<VuItems> vuItemsCollection;

    public VuCatalogo() {
    }

    public VuCatalogo(Long id) {
        this.id = id;
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

    @XmlTransient
    @JsonIgnore
    public Collection<VuItems> getVuItemsCollection() {
        return vuItemsCollection;
    }

    public void setVuItemsCollection(Collection<VuItems> vuItemsCollection) {
        this.vuItemsCollection = vuItemsCollection;
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
        if (!(object instanceof VuCatalogo)) {
            return false;
        }
        VuCatalogo other = (VuCatalogo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.VuCatalogo[ id=" + id + " ]";
    }
    
}
