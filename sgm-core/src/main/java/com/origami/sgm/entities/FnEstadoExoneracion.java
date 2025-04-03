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
@Table(name = "fn_estado_exoneracion", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FnEstadoExoneracion.findAll", query = "SELECT f FROM FnEstadoExoneracion f"),
    @NamedQuery(name = "FnEstadoExoneracion.findById", query = "SELECT f FROM FnEstadoExoneracion f WHERE f.id = :id"),
    @NamedQuery(name = "FnEstadoExoneracion.findByDescripcion", query = "SELECT f FROM FnEstadoExoneracion f WHERE f.descripcion = :descripcion"),
    @NamedQuery(name = "FnEstadoExoneracion.findByEstado", query = "SELECT f FROM FnEstadoExoneracion f WHERE f.estado = :estado"),
    @NamedQuery(name = "FnEstadoExoneracion.findByFechaIngreso", query = "SELECT f FROM FnEstadoExoneracion f WHERE f.fechaIngreso = :fechaIngreso")})
public class FnEstadoExoneracion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 25)
    @Column(name = "descripcion", length = 25)
    private String descripcion;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @OneToMany(mappedBy = "estado")
    private Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection;
    
    @OneToMany(mappedBy = "estado")
    private Collection<FnSolicitudCondonacion> fnSolicitudCondonacionCollection;
    
    //@OneToMany(mappedBy = "estado")
    //private Collection<FnSolicitudCondonacion> fnSolicitudCondonacionCollection;
    

    public FnEstadoExoneracion() {
    }

    
    
    public FnEstadoExoneracion(Long id) {
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<FnSolicitudExoneracion> getFnSolicitudExoneracionCollection() {
        return fnSolicitudExoneracionCollection;
    }

    public void setFnSolicitudExoneracionCollection(Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection) {
        this.fnSolicitudExoneracionCollection = fnSolicitudExoneracionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /*public Collection<FnSolicitudCondonacion> getFnSolicitudCondonacionCollection() {
        return fnSolicitudCondonacionCollection;
    }

    public void setFnSolicitudCondonacionCollection(Collection<FnSolicitudCondonacion> fnSolicitudCondonacionCollection) {
        this.fnSolicitudCondonacionCollection = fnSolicitudCondonacionCollection;
    }*/

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FnEstadoExoneracion)) {
            return false;
        }
        FnEstadoExoneracion other = (FnEstadoExoneracion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnEstadoExoneracion[ id=" + id + " ]";
    }

    public Collection<FnSolicitudCondonacion> getFnSolicitudCondonacionCollection() {
        return fnSolicitudCondonacionCollection;
    }

    public void setFnSolicitudCondonacionCollection(Collection<FnSolicitudCondonacion> fnSolicitudCondonacionCollection) {
        this.fnSolicitudCondonacionCollection = fnSolicitudCondonacionCollection;
    }

}
