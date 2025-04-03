/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigInteger;
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
import javax.validation.constraints.Size;

/**
 *
 * @author origami
 */
@Entity
@Table(name = "solicitud_correccion_predio", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "SolicitudCorreccionPredio.findAll", query = "SELECT s FROM SolicitudCorreccionPredio s"),
    @NamedQuery(name = "SolicitudCorreccionPredio.findById", query = "SELECT s FROM SolicitudCorreccionPredio s WHERE s.id = :id")})
public class SolicitudCorreccionPredio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @Column(name = "fecha_solicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSolicitud;
    
    @Basic(optional = false)
    @NotNull
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;
    
    @Basic(optional = false)
    @NotNull
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser solicitante;
    
    @Basic(optional = false)
    @NotNull
    @JoinColumn(name = "director_catastro", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser directorCatastro;
    
    @JoinColumn(name = "tecnico_catastro", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser tecnicoCatastro;
    
    @Column(name = "accion")
    private BigInteger accion;
    @Size(max = 2147483647)
    @Column(name = "detalle_solicitud")
    private String detalleSolicitud;
    @Size(max = 2147483647)
    @Column(name = "detalle_informe")
    private String detalleInforme;
    @Column(name = "fecha_informe")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInforme;
    @Column(name = "fecha_asignacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAsignacion;

    public SolicitudCorreccionPredio() {
    }

    public SolicitudCorreccionPredio(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public AclUser getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(AclUser solicitante) {
        this.solicitante = solicitante;
    }

    public AclUser getDirectorCatastro() {
        return directorCatastro;
    }

    public void setDirectorCatastro(AclUser directorCatastro) {
        this.directorCatastro = directorCatastro;
    }

    public AclUser getTecnicoCatastro() {
        return tecnicoCatastro;
    }

    public void setTecnicoCatastro(AclUser tecnicoCatastro) {
        this.tecnicoCatastro = tecnicoCatastro;
    }

    public BigInteger getAccion() {
        return accion;
    }

    public void setAccion(BigInteger accion) {
        this.accion = accion;
    }

    public String getDetalleSolicitud() {
        return detalleSolicitud;
    }

    public void setDetalleSolicitud(String detalleSolicitud) {
        this.detalleSolicitud = detalleSolicitud;
    }

    public String getDetalleInforme() {
        return detalleInforme;
    }

    public void setDetalleInforme(String detalleInforme) {
        this.detalleInforme = detalleInforme;
    }

    public Date getFechaInforme() {
        return fechaInforme;
    }

    public void setFechaInforme(Date fechaInforme) {
        this.fechaInforme = fechaInforme;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
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
        if (!(object instanceof SolicitudCorreccionPredio)) {
            return false;
        }
        SolicitudCorreccionPredio other = (SolicitudCorreccionPredio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.SolicitudCorreccionPredio[ id=" + id + " ]";
    }
    
}
