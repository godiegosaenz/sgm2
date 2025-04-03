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
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Angel Navarro
 * @Date 17/05/2016
 */
@Entity
@Table(name = "fn_resolucion", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FnResolucion.findAll", query = "SELECT f FROM FnResolucion f"),
    @NamedQuery(name = "FnResolucion.findById", query = "SELECT f FROM FnResolucion f WHERE f.id = :id"),
    @NamedQuery(name = "FnResolucion.findByNumeroResolucion", query = "SELECT f FROM FnResolucion f WHERE f.numeroResolucion = :numeroResolucion"),
    @NamedQuery(name = "FnResolucion.findByNumeroOficio", query = "SELECT f FROM FnResolucion f WHERE f.numeroOficio = :numeroOficio"),
    @NamedQuery(name = "FnResolucion.findByFechaResolucion", query = "SELECT f FROM FnResolucion f WHERE f.fechaResolucion = :fechaResolucion"),
    @NamedQuery(name = "FnResolucion.findByIdDocumentAlfresco", query = "SELECT f FROM FnResolucion f WHERE f.idDocumentAlfresco = :idDocumentAlfresco"),
    @NamedQuery(name = "FnResolucion.findByEstado", query = "SELECT f FROM FnResolucion f WHERE f.estado = :estado"),
    @NamedQuery(name = "FnResolucion.findByUsuarioCreacion", query = "SELECT f FROM FnResolucion f WHERE f.usuarioCreacion = :usuarioCreacion"),
    @NamedQuery(name = "FnResolucion.findByFechaIngreso", query = "SELECT f FROM FnResolucion f WHERE f.fechaIngreso = :fechaIngreso")})
public class FnResolucion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 20)
    @Column(name = "numero_resolucion", length = 20)
    private String numeroResolucion;
    @Size(max = 20)
    @Column(name = "numero_oficio", length = 20)
    private String numeroOficio;
    @Column(name = "fecha_resolucion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaResolucion;
    @Size(max = 4000)
    @Column(name = "id_document_alfresco", length = 4000)
    private String idDocumentAlfresco;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 25)
    @Column(name = "usuario_creacion", length = 25)
    private String usuarioCreacion;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @OneToMany(mappedBy = "resolucion")
    private Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection;
//    @JoinColumn(name = "solicitud_exoneracion", referencedColumnName = "id")
//    @ManyToOne
//    private FnSolicitudExoneracion solicitudExoneracion;
    

    public FnResolucion() {
    }

    public FnResolucion(Long id) {
        this.id = id;
    }

    public FnResolucion(Date fechaResolucion, Boolean estado, String usuarioCreacion, Date fechaIngreso) {
        this.fechaResolucion = fechaResolucion;
        this.estado = estado;
        this.usuarioCreacion = usuarioCreacion;
        this.fechaIngreso = fechaIngreso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroResolucion() {
        return numeroResolucion;
    }

    public void setNumeroResolucion(String numeroResolucion) {
        this.numeroResolucion = numeroResolucion;
    }

    public String getNumeroOficio() {
        return numeroOficio;
    }

    public void setNumeroOficio(String numeroOficio) {
        this.numeroOficio = numeroOficio;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getIdDocumentAlfresco() {
        return idDocumentAlfresco;
    }

    public void setIdDocumentAlfresco(String idDocumentAlfresco) {
        this.idDocumentAlfresco = idDocumentAlfresco;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    
    public Collection<FnSolicitudExoneracion> getFnSolicitudExoneracionCollection() {
        return fnSolicitudExoneracionCollection;
    }

    public void setFnSolicitudExoneracionCollection(Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection) {
        this.fnSolicitudExoneracionCollection = fnSolicitudExoneracionCollection;
    }

    
//    public FnSolicitudExoneracion getSolicitudExoneracion() {
//        return solicitudExoneracion;
//    }
//
//    public void setSolicitudExoneracion(FnSolicitudExoneracion solicitudExoneracion) {
//        this.solicitudExoneracion = solicitudExoneracion;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FnResolucion)) {
            return false;
        }
        FnResolucion other = (FnResolucion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnResolucion[ id=" + id + " ]";
    }

}
