/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_tareas_dinardap", schema = SchemasConfig.FLOW)
public class RegpTareasDinardap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 500)
    @Column(name = "institucion", length = 500)
    private String institucion;
    @Size(max = 500)
    @Column(name = "solicitante", length = 500)
    private String solicitante;
    @Size(max = 500)
    @Column(name = "numero_solicitud", length = 500)
    private String numeroSolicitud;
    @Size(max = 500)
    @Column(name = "documento", length = 500)
    private String documento;
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;
    @Size(max = 100)
    @Column(name = "usuario", length = 100)
    private String usuario;
    @Column(name = "realizado")
    private Boolean realizado = false;
    @Column(name = "estado")
    private Boolean estado = true;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tareaDinardap", fetch = FetchType.LAZY)
    private Collection<RegpCertificadosInscripciones> regpCertificadosInscripciones;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "regpTareasDinardap", fetch = FetchType.LAZY)
    private Collection<RegpTareasDinardapDocs> regpTareasDinardapDocs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getNumeroSolicitud() {
        return numeroSolicitud;
    }

    public void setNumeroSolicitud(String numeroSolicitud) {
        this.numeroSolicitud = numeroSolicitud;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Collection<RegpCertificadosInscripciones> getRegpCertificadosInscripciones() {
        return regpCertificadosInscripciones;
    }

    public void setRegpCertificadosInscripciones(Collection<RegpCertificadosInscripciones> regpCertificadosInscripciones) {
        this.regpCertificadosInscripciones = regpCertificadosInscripciones;
    }

    public Collection<RegpTareasDinardapDocs> getRegpTareasDinardapDocs() {
        return regpTareasDinardapDocs;
    }

    public void setRegpTareasDinardapDocs(Collection<RegpTareasDinardapDocs> regpTareasDinardapDocs) {
        this.regpTareasDinardapDocs = regpTareasDinardapDocs;
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
        if (!(object instanceof RegpTareasDinardap)) {
            return false;
        }
        RegpTareasDinardap other = (RegpTareasDinardap) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpTareasDinardap[ id=" + id + " ]";
    }
}
