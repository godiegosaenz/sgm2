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
@Table(name = "ctlg_cargo", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CtlgCargo.findAll", query = "SELECT c FROM CtlgCargo c"),
    @NamedQuery(name = "CtlgCargo.findById", query = "SELECT c FROM CtlgCargo c WHERE c.id = :id"),
    @NamedQuery(name = "CtlgCargo.findByNombre", query = "SELECT c FROM CtlgCargo c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "CtlgCargo.findByEstado", query = "SELECT c FROM CtlgCargo c WHERE c.estado = :estado")})
public class CtlgCargo implements Serializable {
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
    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY)
    private Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection;

    public CtlgCargo() {
    }

    public CtlgCargo(Long id) {
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
    public Collection<RegMovimientoRepresentante> getRegMovimientoRepresentanteCollection() {
        return regMovimientoRepresentanteCollection;
    }

    public void setRegMovimientoRepresentanteCollection(Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection) {
        this.regMovimientoRepresentanteCollection = regMovimientoRepresentanteCollection;
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
        if (!(object instanceof CtlgCargo)) {
            return false;
        }
        CtlgCargo other = (CtlgCargo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CtlgCargo[ id=" + id + " ]";
    }
    
}
