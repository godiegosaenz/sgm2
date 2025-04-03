/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;


import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author root
 */
@Entity
@Table(name = "ren_actividad_contribuyente_telefono", schema = SchemasConfig.FINANCIERO)
public class RenActividadContribuyenteTelefono implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 50)
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "actividad_contribuyente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenActividadContribuyente actividadContribuyente;
    

    public RenActividadContribuyenteTelefono() {
    }

    public RenActividadContribuyenteTelefono(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public RenActividadContribuyente getActividadContribuyente() {
        return actividadContribuyente;
    }

    public void setActividadContribuyente(RenActividadContribuyente actividadContribuyente) {
        this.actividadContribuyente = actividadContribuyente;
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
        if (!(object instanceof RenActividadContribuyenteTelefono)) {
            return false;
        }
        RenActividadContribuyenteTelefono other = (RenActividadContribuyenteTelefono) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.ActividadContribuyenteTelefono[ id=" + id + " ]";
    }
    
}
