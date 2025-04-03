/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author root
 */
@Entity
@Table(name = "claves_anteriores_revision", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "ClavesAnterioresRevision.findAll", query = "SELECT c FROM ClavesAnterioresRevision c")})
public class ClavesAnterioresRevision implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 18)
    @Column(name = "clave")
    private String clave = "132250"; 
    @Column(name = "nombres_ame")
    private String nombresAme; 
    @Column(name = "revisada")
    private Boolean revisada;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;

    public ClavesAnterioresRevision() {
    }

    public ClavesAnterioresRevision(Long id) {
        this.id = id;
    }

    public ClavesAnterioresRevision(Long id, String clave) {
        this.id = id;
        this.clave = clave;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Boolean getRevisada() {
        return revisada;
    }

    public void setRevisada(Boolean revisada) {
        this.revisada = revisada;
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
        if (!(object instanceof ClavesAnterioresRevision)) {
            return false;
        }
        ClavesAnterioresRevision other = (ClavesAnterioresRevision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    
    @Override
    public String toString() {
        return "com.origami.sgm.ame.ClavesAnterioresRevision[ id=" + id + " ]";
    }

    public String getNombresAme() {
        return nombresAme;
    }

    public void setNombresAme(String nombresAme) {
        this.nombresAme = nombresAme;
    }
    
    
    
}
