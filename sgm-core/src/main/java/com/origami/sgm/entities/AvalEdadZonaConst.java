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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author XndySxnchez
 */
@Entity
@Table(name = "aval_edad_zona_const", schema = SchemasConfig.APP1)
public class AvalEdadZonaConst implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "zona")
    private Short zona;
    @NotNull
    @Column(name = "parroquia")
    private Short parroquia;
    @Column(name = "edad")
    private Short edad;

    public AvalEdadZonaConst() {
    }

    public AvalEdadZonaConst(Long id) {
        this.id = id;
    }

    public AvalEdadZonaConst(Long id, Short zona, Short edad) {
        this.id = id;
        this.zona = zona;
        this.edad = edad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public Short getEdad() {
        return edad;
    }

    public void setEdad(Short edad) {
        this.edad = edad;
    }

    public Short getParroquia() {
        return parroquia;
    }

    public void setParroquia(Short parroquia) {
        this.parroquia = parroquia;
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
        if (!(object instanceof AvalEdadZonaConst)) {
            return false;
        }
        AvalEdadZonaConst other = (AvalEdadZonaConst) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.AvalEdadZonaConst[ id=" + id + " ]";
    }
    
}
