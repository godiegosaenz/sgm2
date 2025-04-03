/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.PeFirma;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
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
@Table(name = "re_procesos",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ReProcesos.findAll", query = "SELECT r FROM ReProcesos r"),
    @NamedQuery(name = "ReProcesos.findById", query = "SELECT r FROM ReProcesos r WHERE r.id = :id"),
    @NamedQuery(name = "ReProcesos.findByTipo", query = "SELECT r FROM ReProcesos r WHERE r.tipo = :tipo"),
    @NamedQuery(name = "ReProcesos.findByValor", query = "SELECT r FROM ReProcesos r WHERE r.valor = :valor")})
public class ReProcesos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;
    @Size(max = 100)
    @Column(name = "valor", length = 100)
    private String valor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proceso", fetch = FetchType.LAZY)
    private Collection<PeFirma> peFirmaCollection;

    public ReProcesos() {
    }

    public ReProcesos(Long id) {
        this.id = id;
    }

    public ReProcesos(Long id, String tipo) {
        this.id = id;
        this.tipo = tipo;
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeFirma> getPeFirmaCollection() {
        return peFirmaCollection;
    }

    public void setPeFirmaCollection(Collection<PeFirma> peFirmaCollection) {
        this.peFirmaCollection = peFirmaCollection;
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
        if (!(object instanceof ReProcesos)) {
            return false;
        }
        ReProcesos other = (ReProcesos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.ReProcesos[ id=" + id + " ]";
    }
    
}
