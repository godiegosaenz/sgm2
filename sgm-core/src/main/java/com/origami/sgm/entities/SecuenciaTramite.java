/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.GeTipoTramite;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author supergold
 */
@Entity
@Table(name = "secuencia_tramite", schema = SchemasConfig.SECUENCIAS)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SecuenciaTramite.findAll", query = "SELECT s FROM SecuenciaTramite s"),
    @NamedQuery(name = "SecuenciaTramite.findById", query = "SELECT s FROM SecuenciaTramite s WHERE s.id = :id"),
    @NamedQuery(name = "SecuenciaTramite.findByAnio", query = "SELECT s FROM SecuenciaTramite s WHERE s.anio = :anio"),
    @NamedQuery(name = "SecuenciaTramite.findBySecuencia", query = "SELECT s FROM SecuenciaTramite s WHERE s.secuencia = :secuencia"),
    @NamedQuery(name = "SecuenciaTramite.findByFechaActualizacion", query = "SELECT s FROM SecuenciaTramite s WHERE s.fechaActualizacion = :fechaActualizacion")})
public class SecuenciaTramite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio", nullable = false)
    private Integer anio;
    @Basic(optional = false)
    @JoinColumn(name = "tramite_departamento", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite tramiteDepartamento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "secuencia", nullable = false)
    private Long secuencia;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;

    public SecuenciaTramite() {
    }

    public SecuenciaTramite(Long id) {
        this.id = id;
    }

    public SecuenciaTramite(Long id, Integer anio, Long secuencia, Date fechaActualizacion) {
        this.id = id;
        this.anio = anio;
        this.secuencia = secuencia;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public GeTipoTramite getTramiteDepartamento() {
        return tramiteDepartamento;
    }

    public void setTramiteDepartamento(GeTipoTramite tramiteDepartamento) {
        this.tramiteDepartamento = tramiteDepartamento;
    }

    public Long getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Long secuencia) {
        this.secuencia = secuencia;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
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
        if (!(object instanceof SecuenciaTramite)) {
            return false;
        }
        SecuenciaTramite other = (SecuenciaTramite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.SecuenciaTramite[ id=" + id + " ]";
    }
    
}
