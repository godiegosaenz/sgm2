/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "ge_tipos_solicitudes_servicio", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeTiposSolicitudesServicio.findAll", query = "SELECT g FROM GeTiposSolicitudesServicio g"),
    @NamedQuery(name = "GeTiposSolicitudesServicio.findById", query = "SELECT g FROM GeTiposSolicitudesServicio g WHERE g.id = :id"),
    @NamedQuery(name = "GeTiposSolicitudesServicio.findByDescripcion", query = "SELECT g FROM GeTiposSolicitudesServicio g WHERE g.descripcion = :descripcion"),
    @NamedQuery(name = "GeTiposSolicitudesServicio.findByEstado", query = "SELECT g FROM GeTiposSolicitudesServicio g WHERE g.estado = :estado")})
public class GeTiposSolicitudesServicio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 200)
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;
    @JoinColumn(name = "id_tramite", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite idTramite;

    public GeTiposSolicitudesServicio() {
    }

    public GeTiposSolicitudesServicio(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public GeTipoTramite getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(GeTipoTramite idTramite) {
        this.idTramite = idTramite;
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
        if (!(object instanceof GeTiposSolicitudesServicio)) {
            return false;
        }
        GeTiposSolicitudesServicio other = (GeTiposSolicitudesServicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeTiposSolicitudesServicio[ id=" + id + " ]";
    }
    
}
