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
import org.hibernate.annotations.FilterDef;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_detalle_permiso", schema = SchemasConfig.APP1)
@XmlRootElement
@FilterDef(name = "activos", 
    defaultCondition = "estado = " + SchemasConfig.FILTER_ESTADO)
@NamedQueries({
    @NamedQuery(name = "PeDetallePermiso.findAll", query = "SELECT p FROM PeDetallePermiso p"),
    @NamedQuery(name = "PeDetallePermiso.findById", query = "SELECT p FROM PeDetallePermiso p WHERE p.id = :id"),
    @NamedQuery(name = "PeDetallePermiso.findByArea", query = "SELECT p FROM PeDetallePermiso p WHERE p.area = :area"),
    @NamedQuery(name = "PeDetallePermiso.findByPorcentaje", query = "SELECT p FROM PeDetallePermiso p WHERE p.porcentaje = :porcentaje")})
public class PeDetallePermiso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area", precision = 15, scale = 2)
    private BigDecimal area;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "porcentaje", precision = 10, scale = 2)
    private BigDecimal porcentaje;
    @JoinColumn(name = "id_permiso_cab_edificacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PePermisoCabEdificacion idPermisoEdificacion;
    @JoinColumn(name = "id_cat_edf_prop", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatEdfProp idCatEdfProp;

    public PeDetallePermiso() {
    }

    public PeDetallePermiso(Long id) {
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

    public PePermisoCabEdificacion getIdPermisoEdificacion() {
        return idPermisoEdificacion;
    }

    public void setIdPermisoEdificacion(PePermisoCabEdificacion idPermisoEdificacion) {
        this.idPermisoEdificacion = idPermisoEdificacion;
    }

    public CatEdfProp getIdCatEdfProp() {
        return idCatEdfProp;
    }

    public void setIdCatEdfProp(CatEdfProp idCatEdfProp) {
        this.idCatEdfProp = idCatEdfProp;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
        if (!(object instanceof PeDetallePermiso)) {
            return false;
        }
        PeDetallePermiso other = (PeDetallePermiso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeDetallePermiso[ id=" + id + " ]";
    }
    
}
