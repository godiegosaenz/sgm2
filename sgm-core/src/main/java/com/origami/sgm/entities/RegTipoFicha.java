/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_tipo_ficha",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegTipoFicha.findAll", query = "SELECT r FROM RegTipoFicha r"),
    @NamedQuery(name = "RegTipoFicha.findById", query = "SELECT r FROM RegTipoFicha r WHERE r.id = :id"),
    @NamedQuery(name = "RegTipoFicha.findByNombre", query = "SELECT r FROM RegTipoFicha r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegTipoFicha.findByCodigo", query = "SELECT r FROM RegTipoFicha r WHERE r.codigo = :codigo")})
public class RegTipoFicha implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 255)
    @Column(name = "nombre", length = 255)
    private String nombre;
    @Column(name = "codigo")
    private BigInteger codigo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipo", fetch = FetchType.LAZY)
    private Collection<RegFicha> regFichaCollection;

    public RegTipoFicha() {
    }

    public RegTipoFicha(Long id) {
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

    public BigInteger getCodigo() {
        return codigo;
    }

    public void setCodigo(BigInteger codigo) {
        this.codigo = codigo;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegFicha> getRegFichaCollection() {
        return regFichaCollection;
    }

    public void setRegFichaCollection(Collection<RegFicha> regFichaCollection) {
        this.regFichaCollection = regFichaCollection;
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
        if (!(object instanceof RegTipoFicha)) {
            return false;
        }
        RegTipoFicha other = (RegTipoFicha) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegTipoFicha[ id=" + id + " ]";
    }
    
}
