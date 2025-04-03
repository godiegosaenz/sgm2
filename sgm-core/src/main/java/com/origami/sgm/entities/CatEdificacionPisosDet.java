/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.SequenceGenerator;
 import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ing. Carlos Loor
 */
@Entity
@Table(name = "cat_edificacion_pisos_det", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatEdificacionPisosDet.findAll", query = "SELECT c FROM CatEdificacionPisosDet c")
    , @NamedQuery(name = "CatEdificacionPisosDet.findById", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.id = :id")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByPiso", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.piso = :piso")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByArea", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.area = :area")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByAnio", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.anio = :anio")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByEstructura", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.estructura = :estructura")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByAcabados", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.acabados = :acabados")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByFecCre", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.fecCre = :fecCre")
    , @NamedQuery(name = "CatEdificacionPisosDet.findByEstado", query = "SELECT c FROM CatEdificacionPisosDet c WHERE c.estado = :estado")})
public class CatEdificacionPisosDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Expose
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "piso", nullable = false)
    @Expose
    private int piso;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area", precision = 14, scale = 4)
    @Expose
    private BigDecimal area = BigDecimal.ZERO;
    @Column(name = "anio")
    @Expose
    private Integer anio;
    @Column(name = "estructura")
    @Expose
    private Boolean estructura;
    @Column(name = "acabados")
    @Expose
    private Boolean acabados;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date fecCre;
    @Column(name = "estado")
    @Expose
    private String estado;
    @JoinColumn(name = "edificacion", referencedColumnName = "id")
    @ManyToOne( fetch = FetchType.LAZY)
    private CatPredioEdificacion edificacion;
    @JoinColumn(name = "nivel", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem nivel;


    public CatEdificacionPisosDet() {
    }

    public CatEdificacionPisosDet(Long id) {
        this.id = id;
    }

    public CatEdificacionPisosDet(Long id, int piso) {
        this.id = id;
        this.piso = piso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Boolean getEstructura() {
        return estructura;
    }

    public void setEstructura(Boolean estructura) {
        this.estructura = estructura;
    }

    public Boolean getAcabados() {
        return acabados;
    }

    public void setAcabados(Boolean acabados) {
        this.acabados = acabados;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public CatPredioEdificacion getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(CatPredioEdificacion edificacion) {
        this.edificacion = edificacion;
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
        if (!(object instanceof CatEdificacionPisosDet)) {
            return false;
        }
        CatEdificacionPisosDet other = (CatEdificacionPisosDet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatEdificacionPisosDet[ id=" + id + " ]";
    }

    public CtlgItem getNivel() {
        return nivel;
    }

    public void setNivel(CtlgItem nivel) {
        this.nivel = nivel;
    }
    
}
