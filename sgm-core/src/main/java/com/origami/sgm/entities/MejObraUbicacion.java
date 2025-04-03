/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatUbicacion;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.List;
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
import javax.validation.constraints.NotNull;

/**
 *
 * @author andysanchez
 */
@Entity
@Table(name = "mej_obra_ubicacion", schema = SchemasConfig.MEJORAS)
@NamedQueries({
    @NamedQuery(name = "MejObraUbicacion.findAll", query = "SELECT m FROM MejObraUbicacion m")})
public class MejObraUbicacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "id_mejora", referencedColumnName = "id") 
    @ManyToOne(fetch = FetchType.LAZY)
    private MejObra mejora;
    @JoinColumn(name = "ubicacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatUbicacion ubicacion;

    public MejObraUbicacion() {
    }

    public MejObraUbicacion(Long id) {
        this.id = id;
    }

    public MejObraUbicacion(Long id, MejObra mejora, CatUbicacion ubicacion) {
        this.id = id;
        this.mejora = mejora;
        this.ubicacion = ubicacion;
    }

    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MejObra getMejora() {
        return mejora;
    }

    public void setMejora(MejObra mejora) {
        this.mejora = mejora;
    }

    public CatUbicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(CatUbicacion ubicacion) {
        this.ubicacion = ubicacion;
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
        if (!(object instanceof MejObraUbicacion)) {
            return false;
        }
        MejObraUbicacion other = (MejObraUbicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.MejObraUbicacion[ id=" + id + " ]";
    }
    
}
