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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 */
@Entity
@Table(name = "cat_clave_reordenada", schema = SchemasConfig.APP1)
@XmlRootElement
public class CatClaveReordenada implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name="predio", referencedColumnName = "id")
    @OneToOne
    private CatPredio predio;
    @Column(name = "provincia")
    private Short provincia;
    @Column(name = "canton")
    private Short canton;
    @Column(name = "parroquia")
    private Short parroquia;
    @Column(name = "zona")
    private Short zona;
    @Column(name = "sector")
    private Short sector;
    @Column(name = "mz")
    private Short mz;
    @Column(name = "solar")
    private Short solar;
    @Column(name = "bloque")
    private Short bloque;
    @Column(name = "piso")
    private Short piso;
    @Column(name = "unidad")
    private Short unidad;
    @Column(name = "clave_cat", length = 24)
    private String claveCat;

    public CatClaveReordenada() {
    }

    public CatClaveReordenada(Long id) {
        this.id = id;
    }

    public CatClaveReordenada(Long id, CatPredio predio) {
        this.id = id;
        this.predio = predio;
    }

    public CatClaveReordenada(Long id, CatPredio predio, Short provincia, Short canton, Short parroquia, Short zona, Short sector, Short mz, Short solar, Short bloque, Short piso, Short unidad, String claveCat) {
        this.id = id;
        this.predio = predio;
        this.provincia = provincia;
        this.canton = canton;
        this.parroquia = parroquia;
        this.zona = zona;
        this.sector = sector;
        this.mz = mz;
        this.solar = solar;
        this.bloque = bloque;
        this.piso = piso;
        this.unidad = unidad;
        this.claveCat = claveCat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Short getProvincia() {
        return provincia;
    }

    public void setProvincia(Short provincia) {
        this.provincia = provincia;
    }

    public Short getCanton() {
        return canton;
    }

    public void setCanton(Short canton) {
        this.canton = canton;
    }

    public Short getParroquia() {
        return parroquia;
    }

    public void setParroquia(Short parroquia) {
        this.parroquia = parroquia;
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public Short getMz() {
        return mz;
    }

    public void setMz(Short mz) {
        this.mz = mz;
    }

    public Short getSolar() {
        return solar;
    }

    public void setSolar(Short solar) {
        this.solar = solar;
    }

    public Short getBloque() {
        return bloque;
    }

    public void setBloque(Short bloque) {
        this.bloque = bloque;
    }

    public Short getPiso() {
        return piso;
    }

    public void setPiso(Short piso) {
        this.piso = piso;
    }

    public Short getUnidad() {
        return unidad;
    }

    public void setUnidad(Short unidad) {
        this.unidad = unidad;
    }

    public String getClaveCat() {
        return claveCat;
    }

    public void setClaveCat(String claveCat) {
        this.claveCat = claveCat;
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
        if (!(object instanceof CatClaveReordenada)) {
            return false;
        }
        CatClaveReordenada other = (CatClaveReordenada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatClaveReordenada[ id=" + id + " ]";
    }
    
}
