/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
 * @date 14/09/2016
 */
@Entity
@Table(name = "ren_turismo_servicios", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenTurismoServicios.findAll", query = "SELECT r FROM RenTurismoServicios r"),
    @NamedQuery(name = "RenTurismoServicios.findById", query = "SELECT r FROM RenTurismoServicios r WHERE r.id = :id"),
    @NamedQuery(name = "RenTurismoServicios.findByDescripcion", query = "SELECT r FROM RenTurismoServicios r WHERE r.descripcion = :descripcion"),
    @NamedQuery(name = "RenTurismoServicios.findByEstado", query = "SELECT r FROM RenTurismoServicios r WHERE r.estado = :estado"),
    @NamedQuery(name = "RenTurismoServicios.findByTipo", query = "SELECT r FROM RenTurismoServicios r WHERE r.tipo = :tipo"),
    @NamedQuery(name = "RenTurismoServicios.findByFechaIngreso", query = "SELECT r FROM RenTurismoServicios r WHERE r.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "RenTurismoServicios.findByUsuarioIngreso", query = "SELECT r FROM RenTurismoServicios r WHERE r.usuarioIngreso = :usuarioIngreso")})
public class RenTurismoServicios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "tipo")
    private BigInteger tipo;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Size(max = 25)
    @Column(name = "usuario_ingreso", length = 25)
    private String usuarioIngreso;
    @OneToMany(mappedBy = "tipoHabitacion")
    private Collection<RenTurismoDetalleHoteles> renTurismoDetalleHotelesCollection;
    @OneToMany(mappedBy = "transporteTerrestre")
    private Collection<RenTurismo> renTurismoCollection;
    @OneToMany(mappedBy = "servcios")
    private Collection<RenTurismo> renTurismoCollection1;
    @OneToMany(mappedBy = "especialidadServiciosAlimenticios")
    private Collection<RenTurismo> renTurismoCollection2;
    @OneToMany(mappedBy = "crucerosTuristicos")
    private Collection<RenTurismo> renTurismoCollection3;
    @OneToMany(mappedBy = "transporteMaritimo")
    private Collection<RenTurismo> renTurismoCollection4;
    @OneToMany(mappedBy = "transporteAereo")
    private Collection<RenTurismo> renTurismoCollection5;

    public RenTurismoServicios() {
    }

    public RenTurismoServicios(Long id) {
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

    public BigInteger getTipo() {
        return tipo;
    }

    public void setTipo(BigInteger tipo) {
        this.tipo = tipo;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismoDetalleHoteles> getRenTurismoDetalleHotelesCollection() {
        return renTurismoDetalleHotelesCollection;
    }

    public void setRenTurismoDetalleHotelesCollection(Collection<RenTurismoDetalleHoteles> renTurismoDetalleHotelesCollection) {
        this.renTurismoDetalleHotelesCollection = renTurismoDetalleHotelesCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismo> getRenTurismoCollection() {
        return renTurismoCollection;
    }

    public void setRenTurismoCollection(Collection<RenTurismo> renTurismoCollection) {
        this.renTurismoCollection = renTurismoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismo> getRenTurismoCollection1() {
        return renTurismoCollection1;
    }

    public void setRenTurismoCollection1(Collection<RenTurismo> renTurismoCollection1) {
        this.renTurismoCollection1 = renTurismoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismo> getRenTurismoCollection2() {
        return renTurismoCollection2;
    }

    public void setRenTurismoCollection2(Collection<RenTurismo> renTurismoCollection2) {
        this.renTurismoCollection2 = renTurismoCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismo> getRenTurismoCollection3() {
        return renTurismoCollection3;
    }

    public void setRenTurismoCollection3(Collection<RenTurismo> renTurismoCollection3) {
        this.renTurismoCollection3 = renTurismoCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismo> getRenTurismoCollection4() {
        return renTurismoCollection4;
    }

    public void setRenTurismoCollection4(Collection<RenTurismo> renTurismoCollection4) {
        this.renTurismoCollection4 = renTurismoCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismo> getRenTurismoCollection5() {
        return renTurismoCollection5;
    }

    public void setRenTurismoCollection5(Collection<RenTurismo> renTurismoCollection5) {
        this.renTurismoCollection5 = renTurismoCollection5;
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
        if (!(object instanceof RenTurismoServicios)) {
            return false;
        }
        RenTurismoServicios other = (RenTurismoServicios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenTurismoServicios[ id=" + id + " ]";
    }

}
