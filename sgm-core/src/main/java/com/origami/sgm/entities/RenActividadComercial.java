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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_actividad_comercial", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenActividadComercial.findAll", query = "SELECT r FROM RenActividadComercial r")})
public class RenActividadComercial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Size(max = 20)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 25)
    @Column(name = "ciu")
    private String ciu;
    
    @ManyToMany(mappedBy = "renActividadComercialCollection", fetch = FetchType.LAZY)
    private Collection<RenLocalComercial> renLocalComercialCollection;
    @ManyToMany(mappedBy = "actividad", fetch = FetchType.LAZY)
    private Collection<RenTasaTurismo> renTasaTurismosCollection;

    public RenActividadComercial() {
    }

    public RenActividadComercial(Long id) {
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Collection<RenLocalComercial> getRenLocalComercialCollection() {
        return renLocalComercialCollection;
    }

    public void setRenLocalComercialCollection(Collection<RenLocalComercial> renLocalComercialCollection) {
        this.renLocalComercialCollection = renLocalComercialCollection;
    }

    public Collection<RenTasaTurismo> getRenTasaTurismosCollection() {
        return renTasaTurismosCollection;
    }

    public void setRenTasaTurismosCollection(Collection<RenTasaTurismo> renTasaTurismosCollection) {
        this.renTasaTurismosCollection = renTasaTurismosCollection;
    }

    public String getCiu() {
        return ciu;
    }

    public void setCiu(String ciu) {
        this.ciu = ciu;
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
        if (!(object instanceof RenActividadComercial)) {
            return false;
        }
        RenActividadComercial other = (RenActividadComercial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenActividadComercial[ id=" + id + " ]";
    }
    
}
