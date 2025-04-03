/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.OtrosTramites;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "requisitos_otros_tramite",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequisitosOtrosTramite.findAll", query = "SELECT r FROM RequisitosOtrosTramite r"),
    @NamedQuery(name = "RequisitosOtrosTramite.findById", query = "SELECT r FROM RequisitosOtrosTramite r WHERE r.id = :id"),
    @NamedQuery(name = "RequisitosOtrosTramite.findByNombre", query = "SELECT r FROM RequisitosOtrosTramite r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RequisitosOtrosTramite.findByTieneComprobante", query = "SELECT r FROM RequisitosOtrosTramite r WHERE r.tieneComprobante = :tieneComprobante"),
    @NamedQuery(name = "RequisitosOtrosTramite.findByTipoConsultaId", query = "SELECT r FROM RequisitosOtrosTramite r WHERE r.tipoConsultaId = :tipoConsultaId"),
    @NamedQuery(name = "RequisitosOtrosTramite.findByEstado", query = "SELECT r FROM RequisitosOtrosTramite r WHERE r.estado = :estado")})
public class RequisitosOtrosTramite implements Serializable {
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
    @Column(name = "tipo_consulta_id")
    private BigInteger tipoConsultaId;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;
    @ManyToMany(mappedBy = "requisitosOtrosTramiteCollection", fetch = FetchType.LAZY)
    private Collection<OtrosTramites> otrosTramitesCollection;

    public RequisitosOtrosTramite() {
    }

    public RequisitosOtrosTramite(Long id) {
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

    public BigInteger getTipoConsultaId() {
        return tipoConsultaId;
    }

    public void setTipoConsultaId(BigInteger tipoConsultaId) {
        this.tipoConsultaId = tipoConsultaId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<OtrosTramites> getOtrosTramitesCollection() {
        return otrosTramitesCollection;
    }

    public void setOtrosTramitesCollection(Collection<OtrosTramites> otrosTramitesCollection) {
        this.otrosTramitesCollection = otrosTramitesCollection;
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
        if (!(object instanceof RequisitosOtrosTramite)) {
            return false;
        }
        RequisitosOtrosTramite other = (RequisitosOtrosTramite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RequisitosOtrosTramite[ id=" + id + " ]";
    }
    
}
