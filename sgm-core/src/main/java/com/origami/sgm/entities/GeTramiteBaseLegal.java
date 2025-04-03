/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_tramite_base_legal", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeTramiteBaseLegal.findAll", query = "SELECT g FROM GeTramiteBaseLegal g"),
    @NamedQuery(name = "GeTramiteBaseLegal.findById", query = "SELECT g FROM GeTramiteBaseLegal g WHERE g.id = :id"),
    @NamedQuery(name = "GeTramiteBaseLegal.findByEstado", query = "SELECT g FROM GeTramiteBaseLegal g WHERE g.estado = :estado")})
public class GeTramiteBaseLegal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "tramite", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite tramite;
    @JoinColumn(name = "base_legal", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeDocumentos baseLegal;

    public GeTramiteBaseLegal() {
    }

    public GeTramiteBaseLegal(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public GeTipoTramite getTramite() {
        return tramite;
    }

    public void setTramite(GeTipoTramite tramite) {
        this.tramite = tramite;
    }

    public GeDocumentos getBaseLegal() {
        return baseLegal;
    }

    public void setBaseLegal(GeDocumentos baseLegal) {
        this.baseLegal = baseLegal;
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
        if (!(object instanceof GeTramiteBaseLegal)) {
            return false;
        }
        GeTramiteBaseLegal other = (GeTramiteBaseLegal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeTramiteBaseLegal[ id=" + id + " ]";
    }
    
}
