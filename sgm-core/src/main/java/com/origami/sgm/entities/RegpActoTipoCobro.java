/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_acto_tipo_cobro", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "RegpActoTipoCobro.findAll", query = "SELECT r FROM RegpActoTipoCobro r")})
public class RegpActoTipoCobro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(mappedBy = "tipoCobro", fetch = FetchType.LAZY)
    private Collection<RegpActosIngreso> regpActosIngresoCollection;

    public RegpActoTipoCobro() {
    }

    public RegpActoTipoCobro(Long id) {
        this.id = id;
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

    public Collection<RegpActosIngreso> getRegpActosIngresoCollection() {
        return regpActosIngresoCollection;
    }

    public void setRegpActosIngresoCollection(Collection<RegpActosIngreso> regpActosIngresoCollection) {
        this.regpActosIngresoCollection = regpActosIngresoCollection;
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
        if (!(object instanceof RegpActoTipoCobro)) {
            return false;
        }
        RegpActoTipoCobro other = (RegpActoTipoCobro) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpActoTipoCobro[ id=" + id + " ]";
    }
    
}
