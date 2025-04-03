/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.application;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Fernando
 */
@Entity
@Table(name = "aplicacion", schema = SchemasConfig.APPLICATION)
public class ApplAplicacion implements Serializable{
    
    @Id
    @Column(name = "id", nullable = false)
    @Basic(optional = false)
    private Long id;
    
    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;
    
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    
    @Column(name = "n_version", length = 20)
    private String nVersion;
    
    @Column(name = "fec_despliegue")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fec_despliegue;
    
    @Column(name = "propietario", length = 100)
    private String propietario;
    
    @Column(name = "estado")
    private Integer estado;
    
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;

    public ApplAplicacion() {
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
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
        final ApplAplicacion other = (ApplAplicacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getnVersion() {
        return nVersion;
    }

    public void setnVersion(String nVersion) {
        this.nVersion = nVersion;
    }

    public Date getFec_despliegue() {
        return fec_despliegue;
    }

    public void setFec_despliegue(Date fec_despliegue) {
        this.fec_despliegue = fec_despliegue;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }
    
    
    
}
