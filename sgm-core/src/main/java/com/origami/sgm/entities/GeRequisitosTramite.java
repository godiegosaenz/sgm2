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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "ge_requisitos_tramite", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeRequisitosTramite.findAll", query = "SELECT g FROM GeRequisitosTramite g"),
    @NamedQuery(name = "GeRequisitosTramite.findById", query = "SELECT g FROM GeRequisitosTramite g WHERE g.id = :id"),
    @NamedQuery(name = "GeRequisitosTramite.findByNombre", query = "SELECT g FROM GeRequisitosTramite g WHERE g.nombre = :nombre"),
    @NamedQuery(name = "GeRequisitosTramite.findByTieneComprobante", query = "SELECT g FROM GeRequisitosTramite g WHERE g.tieneComprobante = :tieneComprobante"),
    @NamedQuery(name = "GeRequisitosTramite.findByEstado", query = "SELECT g FROM GeRequisitosTramite g WHERE g.estado = :estado"),
    @NamedQuery(name = "GeRequisitosTramite.findByTieneArchivo", query = "SELECT g FROM GeRequisitosTramite g WHERE g.tieneArchivo = :tieneArchivo"),
    @NamedQuery(name = "GeRequisitosTramite.findByDescripcion", query = "SELECT g FROM GeRequisitosTramite g WHERE g.descripcion = :descripcion")})
public class GeRequisitosTramite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 600)
    @Column(name = "nombre", length = 600)
    private String nombre;
    @Column(name = "tiene_comprobante")
    private Boolean tieneComprobante;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;
    @Column(name = "tiene_archivo")
    private Boolean tieneArchivo;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    
    @Column(name = "string_value", length = 100)
    private String stringValue;
    @JoinTable(name = "ge_tipo_tramite_has_requisitos", joinColumns = {
        @JoinColumn(name = "requisito", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "tipo_tramite", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<GeTipoTramite> geTipoTramiteCollection;
    @JoinColumn(name = "tipo_consulta_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoConsultas tipoConsultaId;

    public GeRequisitosTramite() {
    }

    public GeRequisitosTramite(Long id) {
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

    public Boolean getTieneComprobante() {
        return tieneComprobante;
    }

    public void setTieneComprobante(Boolean tieneComprobante) {
        this.tieneComprobante = tieneComprobante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getTieneArchivo() {
        return tieneArchivo;
    }

    public void setTieneArchivo(Boolean tieneArchivo) {
        this.tieneArchivo = tieneArchivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTipoTramite> getGeTipoTramiteCollection() {
        return geTipoTramiteCollection;
    }

    public void setGeTipoTramiteCollection(Collection<GeTipoTramite> geTipoTramiteCollection) {
        this.geTipoTramiteCollection = geTipoTramiteCollection;
    }

    public GeTipoConsultas getTipoConsultaId() {
        return tipoConsultaId;
    }

    public void setTipoConsultaId(GeTipoConsultas tipoConsultaId) {
        this.tipoConsultaId = tipoConsultaId;
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
        if (!(object instanceof GeRequisitosTramite)) {
            return false;
        }
        GeRequisitosTramite other = (GeRequisitosTramite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeRequisitosTramite[ id=" + id + " ]";
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
    
}
