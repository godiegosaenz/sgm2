/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.application;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Fernando
 */
@Entity
@Table(name = "actividad_economica", schema = SchemasConfig.APPLICATION)
public class ApplActividadEconomica implements Serializable{
    
    @Id
    @Column(name = "id", nullable = false)
    @Basic(optional = false)
    private Long id;
    
    @OneToMany(mappedBy = "actEcon")
    private Collection<ApplEmpresa> empresas;
    
    @Basic(optional = false)
    @Column(name = "descripcion", length = 100, nullable = false)
    private String descripcion;
    
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre = new Date();
    
    @Column(name = "estado")
    private Integer estado;

    public ApplActividadEconomica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<ApplEmpresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(Collection<ApplEmpresa> empresas) {
        this.empresas = empresas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApplActividadEconomica other = (ApplActividadEconomica) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
}
