/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_afiliacion_camara", schema = SchemasConfig.FINANCIERO)
public class RenAfiliacionCamaraProduccion implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean estado;
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_ingreso")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "porcent_exoneracion")
    private BigDecimal porcentExoneracion;

    @OneToMany(mappedBy = "afiliacionCamara", fetch = FetchType.LAZY)
    private Collection<RenPermisosFuncionamientoLocalComercial> permisosFuncionamientoLCCollection;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RenAfiliacionCamaraProduccion other = (RenAfiliacionCamaraProduccion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Collection<RenPermisosFuncionamientoLocalComercial> getPermisosFuncionamientoLCCollection() {
        return permisosFuncionamientoLCCollection;
    }

    public void setPermisosFuncionamientoLCCollection(Collection<RenPermisosFuncionamientoLocalComercial> permisosFuncionamientoLCCollection) {
        this.permisosFuncionamientoLCCollection = permisosFuncionamientoLCCollection;
    }

    public RenAfiliacionCamaraProduccion() {
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public BigDecimal getPorcentExoneracion() {
        return porcentExoneracion;
    }

    public void setPorcentExoneracion(BigDecimal porcentExoneracion) {
        this.porcentExoneracion = porcentExoneracion;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenAfiliacionCamaraProduccion[ " + id + " ]";
    }

    
}
