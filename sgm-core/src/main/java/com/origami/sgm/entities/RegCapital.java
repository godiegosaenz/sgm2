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
@Table(name = "reg_capital",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegCapital.findAll", query = "SELECT r FROM RegCapital r"),
    @NamedQuery(name = "RegCapital.findById", query = "SELECT r FROM RegCapital r WHERE r.id = :id"),
    @NamedQuery(name = "RegCapital.findByNombre", query = "SELECT r FROM RegCapital r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegCapital.findByEstado", query = "SELECT r FROM RegCapital r WHERE r.estado = :estado")})
public class RegCapital implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "nombre", length = 100)
    private String nombre;
    @Column(name = "estado")
    private Boolean estado;
    @OneToMany(mappedBy = "capital", fetch = FetchType.LAZY)
    private Collection<RegMovimientoCapital> regMovimientoCapitalCollection;

    public RegCapital() {
    }

    public RegCapital(Long id) {
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoCapital> getRegMovimientoCapitalCollection() {
        return regMovimientoCapitalCollection;
    }

    public void setRegMovimientoCapitalCollection(Collection<RegMovimientoCapital> regMovimientoCapitalCollection) {
        this.regMovimientoCapitalCollection = regMovimientoCapitalCollection;
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
        if (!(object instanceof RegCapital)) {
            return false;
        }
        RegCapital other = (RegCapital) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegCapital[ id=" + id + " ]";
    }
    
}
