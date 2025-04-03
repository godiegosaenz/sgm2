/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import org.hibernate.annotations.Where;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_local_tipo_accesorio", schema = SchemasConfig.FINANCIERO)
public class RenLocalTipoAccesorio implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Size(max = 150)
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado")
    private Boolean estado;
    
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;
    
    @Size(max = 50)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    
    @OneToMany(mappedBy = "tipoAccesorio", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<RenLocalCantidadAccesorios> cantidadAccesoriosCollection;

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

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Collection<RenLocalCantidadAccesorios> getCantidadAccesoriosCollection() {
        return cantidadAccesoriosCollection;
    }

    public void setCantidadAccesoriosCollection(Collection<RenLocalCantidadAccesorios> cantidadAccesoriosCollection) {
        this.cantidadAccesoriosCollection = cantidadAccesoriosCollection;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final RenLocalTipoAccesorio other = (RenLocalTipoAccesorio) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RenLocalTipoAccesorio{" + "id=" + id + '}';
    }
    
}
