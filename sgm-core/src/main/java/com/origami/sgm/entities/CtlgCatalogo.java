/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
 import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name = "ctlg_catalogo", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CtlgCatalogo.findAll", query = "SELECT c FROM CtlgCatalogo c"),
    @NamedQuery(name = "CtlgCatalogo.findById", query = "SELECT c FROM CtlgCatalogo c WHERE c.id = :id"),
    @NamedQuery(name = "CtlgCatalogo.findByNombre", query = "SELECT c FROM CtlgCatalogo c WHERE c.nombre = :nombre")})
@SequenceGenerator(name = "ctlg_catalogo_id_seq", sequenceName = SchemasConfig.APP1 + ".ctlg_catalogo_id_seq", allocationSize = 1)
public class CtlgCatalogo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ctlg_catalogo_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "nombre", nullable = false, length = 30)
    @Expose
    private String nombre;
    @Size(min = 1, max = 30)
    @Column(name = "observacion")
    @Expose
    private String observacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "catalogo", fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    @Expose
    private Collection<CtlgItem> ctlgItemCollection;

    public CtlgCatalogo() {
    }

    public CtlgCatalogo(Long id) {
        this.id = id;
    }

    public CtlgCatalogo(Long id, String nombre) {
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

    @XmlTransient
    @JsonIgnore
    public Collection<CtlgItem> getCtlgItemCollection() {
        return ctlgItemCollection;
    }

    public void setCtlgItemCollection(Collection<CtlgItem> ctlgItemCollection) {
        this.ctlgItemCollection = ctlgItemCollection;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
        if (!(object instanceof CtlgCatalogo)) {
            return false;
        }
        CtlgCatalogo other = (CtlgCatalogo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CtlgCatalogo[ id=" + id + " ]";
    }
    
}
