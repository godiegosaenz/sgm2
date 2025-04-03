/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEdfProp;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_detalle_inspeccion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeDetalleInspeccion.findAll", query = "SELECT p FROM PeDetalleInspeccion p"),
    @NamedQuery(name = "PeDetalleInspeccion.findById", query = "SELECT p FROM PeDetalleInspeccion p WHERE p.id = :id"),
    @NamedQuery(name = "PeDetalleInspeccion.findByArea", query = "SELECT p FROM PeDetalleInspeccion p WHERE p.area = :area"),
    @NamedQuery(name = "PeDetalleInspeccion.findByPorcentaje", query = "SELECT p FROM PeDetalleInspeccion p WHERE p.porcentaje = :porcentaje")})
public class PeDetalleInspeccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area", precision = 15, scale = 2)
    private BigDecimal area;
    @Column(name = "porcentaje", precision = 15, scale = 2)
    private BigDecimal porcentaje;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "id_inspeccion_cab_edificacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeInspeccionCabEdificacion edificacion;
    @JoinColumn(name = "id_cat_edf_prop", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatEdfProp caracteristica;

    public PeDetalleInspeccion() {
        
    }

    public PeDetalleInspeccion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public PeInspeccionCabEdificacion getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(PeInspeccionCabEdificacion edificacion) {
        this.edificacion = edificacion;
    }

    public CatEdfProp getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(CatEdfProp caracteristica) {
        this.caracteristica = caracteristica;
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
        if (!(object instanceof PeDetalleInspeccion)) {
            return false;
        }
        PeDetalleInspeccion other = (PeDetalleInspeccion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeDetalleInspeccion[ id=" + id + " ]";
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
}
