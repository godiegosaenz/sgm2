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
@Table(name = "fn_convenio_pago_archivo", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "FnConvenioPagoArchivo.findAll", query = "SELECT f FROM FnConvenioPagoArchivo f")
    , @NamedQuery(name = "FnConvenioPagoArchivo.findById", query = "SELECT f FROM FnConvenioPagoArchivo f WHERE f.id = :id")
    , @NamedQuery(name = "FnConvenioPagoArchivo.findByContentType", query = "SELECT f FROM FnConvenioPagoArchivo f WHERE f.contentType = :contentType")
    , @NamedQuery(name = "FnConvenioPagoArchivo.findByFileOid", query = "SELECT f FROM FnConvenioPagoArchivo f WHERE f.fileOid = :fileOid")
    , @NamedQuery(name = "FnConvenioPagoArchivo.findByNombre", query = "SELECT f FROM FnConvenioPagoArchivo f WHERE f.nombre = :nombre")
    , @NamedQuery(name = "FnConvenioPagoArchivo.findByDescripcion", query = "SELECT f FROM FnConvenioPagoArchivo f WHERE f.descripcion = :descripcion")
    , @NamedQuery(name = "FnConvenioPagoArchivo.findBySisEnabled", query = "SELECT f FROM FnConvenioPagoArchivo f WHERE f.sisEnabled = :sisEnabled")})
public class FnConvenioPagoArchivo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "content_type")
    private String contentType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "file_oid")
    private long fileOid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 500)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "sis_enabled")
    private Boolean sisEnabled;
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
    @JoinColumn(name = "convenio", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FnConvenioPago convenio;

    public FnConvenioPagoArchivo() {
    }

    public FnConvenioPagoArchivo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileOid() {
        return fileOid;
    }

    public void setFileOid(long fileOid) {
        this.fileOid = fileOid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getSisEnabled() {
        return sisEnabled;
    }

    public void setSisEnabled(Boolean sisEnabled) {
        this.sisEnabled = sisEnabled;
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
        if (!(object instanceof FnConvenioPagoArchivo)) {
            return false;
        }
        FnConvenioPagoArchivo other = (FnConvenioPagoArchivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnConvenioPagoArchivo[ id=" + id + " ]";
    }

}
