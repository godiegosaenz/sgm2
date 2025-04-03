/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_local_cantidad_accesorios", schema = SchemasConfig.FINANCIERO)
public class RenLocalCantidadAccesorios implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "cantidad")
    private Integer cantidad;
    
    @Column(name = "estado")
    private Boolean estado;
    
    @Column(name = "anio")
    private Integer anio;
    
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;
    
    @Size(max = 50)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    
    @JoinColumn(name = "local_comercial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalComercial localComercial;
    
    @JoinColumn(name = "tipo_accesorio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalTipoAccesorio tipoAccesorio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
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

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public RenLocalTipoAccesorio getTipoAccesorio() {
        return tipoAccesorio;
    }

    public void setTipoAccesorio(RenLocalTipoAccesorio tipoAccesorio) {
        this.tipoAccesorio = tipoAccesorio;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final RenLocalCantidadAccesorios other = (RenLocalCantidadAccesorios) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RenLocalCantidadAccesorios{" + "id=" + id + '}';
    }
    
}
