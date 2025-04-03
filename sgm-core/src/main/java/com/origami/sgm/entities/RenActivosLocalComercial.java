/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_activos_local_comercial", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenActivosLocalComercial.findAll", query = "SELECT r FROM RenActivosLocalComercial r")})
public class RenActivosLocalComercial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "num_liquidacion")
    private BigInteger numLiquidacion;
    @Column(name = "anio_balance")
    private Integer anioBalance;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "activo_total")
    private BigDecimal activoTotal = BigDecimal.ZERO;
    @Column(name = "activo_contingente")
    private BigDecimal activoContingente  = BigDecimal.ZERO;
    @Column(name = "pasivo_total")
    private BigDecimal pasivoTotal = BigDecimal.ZERO;
    @Column(name = "pasivo_contingente")
    private BigDecimal pasivoContingente = BigDecimal.ZERO;
    @Column(name = "porcentaje_ingreso")
    private BigDecimal porcentajeIngreso = BigDecimal.ZERO;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 20)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    
    @JoinColumn(name = "local_comercial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalComercial localComercial;
    @JoinColumn(name = "permiso", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private RenPermisosFuncionamientoLocalComercial permiso;

    public RenActivosLocalComercial() {
    }

    public RenActivosLocalComercial(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(BigInteger numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }

    public Integer getAnioBalance() {
        return anioBalance;
    }

    public void setAnioBalance(Integer anioBalance) {
        this.anioBalance = anioBalance;
    }

    public BigDecimal getActivoTotal() {
        return activoTotal;
    }

    public void setActivoTotal(BigDecimal activoTotal) {
        this.activoTotal = activoTotal;
    }

    public BigDecimal getActivoContingente() {
        return activoContingente;
    }

    public void setActivoContingente(BigDecimal activoContingente) {
        this.activoContingente = activoContingente;
    }

    public BigDecimal getPasivoTotal() {
        return pasivoTotal;
    }

    public void setPasivoTotal(BigDecimal pasivoTotal) {
        this.pasivoTotal = pasivoTotal;
    }

    public BigDecimal getPorcentajeIngreso() {
        return porcentajeIngreso;
    }

    public void setPorcentajeIngreso(BigDecimal porcentajeIngreso) {
        this.porcentajeIngreso = porcentajeIngreso;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public RenPermisosFuncionamientoLocalComercial getPermiso() {
        return permiso;
    }

    public void setPermiso(RenPermisosFuncionamientoLocalComercial permiso) {
        this.permiso = permiso;
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
        if (!(object instanceof RenActivosLocalComercial)) {
            return false;
        }
        RenActivosLocalComercial other = (RenActivosLocalComercial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenActivosLocalComercial[ id=" + id + " ]";
    }

    public BigDecimal getPasivoContingente() {
        return pasivoContingente;
    }

    public void setPasivoContingente(BigDecimal pasivoContingente) {
        this.pasivoContingente = pasivoContingente;
    }
    
}
