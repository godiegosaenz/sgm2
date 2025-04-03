/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Dairon Freddy
 */
@Entity
@Table(name = "fn_convenio_pago_observacion", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "FnConvenioPagoObservacion.findAll", query = "SELECT f FROM FnConvenioPagoObservacion f")
    , @NamedQuery(name = "FnConvenioPagoObservacion.findById", query = "SELECT f FROM FnConvenioPagoObservacion f WHERE f.id = :id")
    , @NamedQuery(name = "FnConvenioPagoObservacion.findByEstadoConvenio", query = "SELECT f FROM FnConvenioPagoObservacion f WHERE f.estadoConvenio = :estadoConvenio")
    , @NamedQuery(name = "FnConvenioPagoObservacion.findByObservacion", query = "SELECT f FROM FnConvenioPagoObservacion f WHERE f.observacion = :observacion")
    , @NamedQuery(name = "FnConvenioPagoObservacion.findByUsuarioIngreso", query = "SELECT f FROM FnConvenioPagoObservacion f WHERE f.usuarioIngreso = :usuarioIngreso")
    , @NamedQuery(name = "FnConvenioPagoObservacion.findByFechaIngreso", query = "SELECT f FROM FnConvenioPagoObservacion f WHERE f.fechaIngreso = :fechaIngreso")
    , @NamedQuery(name = "FnConvenioPagoObservacion.findByEstado", query = "SELECT f FROM FnConvenioPagoObservacion f WHERE f.estado = :estado")})
public class FnConvenioPagoObservacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado_convenio")
    private short estadoConvenio;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "convenio", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FnConvenioPago convenio;

    public FnConvenioPagoObservacion() {
    }

    public FnConvenioPagoObservacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public short getEstadoConvenio() {
        return estadoConvenio;
    }

    public void setEstadoConvenio(short estadoConvenio) {
        this.estadoConvenio = estadoConvenio;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public FnConvenioPago getConvenio() {
        return convenio;
    }

    public void setConvenio(FnConvenioPago convenio) {
        this.convenio = convenio;
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
        if (!(object instanceof FnConvenioPagoObservacion)) {
            return false;
        }
        FnConvenioPagoObservacion other = (FnConvenioPagoObservacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnConvenioPagoObservacion[ id=" + id + " ]";
    }
    
}
