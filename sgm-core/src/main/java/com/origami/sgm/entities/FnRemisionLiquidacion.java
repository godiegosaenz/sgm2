/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.origami.sgm.entities.database.SchemasConfig;
import java.math.BigDecimal;

/**
 *
 * @author origami-idea
 */
@Entity
@Table(name = "fn_exoneracion_liquidaciones", schema = SchemasConfig.FINANCIERO)
public class FnRemisionLiquidacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "usuario_aplicacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioAplicacion;
    @JoinColumn(name = "liquidacion_new", referencedColumnName = "id")
    @ManyToOne
    private RenLiquidacion liquidacion;    
    @Column(name = "fecha_aplicacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAplicacion;
    @JoinColumn(name = "exoneracion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FnRemisionSolicitud exoneracion;

    @Column(name = "recargo")
    private BigDecimal recargo;
    @Column(name = "interes")
    private BigDecimal interes;
    @Column(name = "multas")
    private BigDecimal multas;
    
    public FnRemisionLiquidacion() {
    }

    public FnRemisionLiquidacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public FnRemisionSolicitud getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnRemisionSolicitud exoneracion) {
        this.exoneracion = exoneracion;
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
        if (!(object instanceof FnRemisionLiquidacion)) {
            return false;
        }
        FnRemisionLiquidacion other = (FnRemisionLiquidacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnRemisionLiquidacion[ id=" + id + " ]";
    }

    public AclUser getUsuarioAplicacion() {
        return usuarioAplicacion;
    }

    public void setUsuarioAplicacion(AclUser usuarioAplicacion) {
        this.usuarioAplicacion = usuarioAplicacion;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public BigDecimal getRecargo() {
        return recargo;
    }

    public void setRecargo(BigDecimal recargo) {
        this.recargo = recargo;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getMultas() {
        return multas;
    }

    public void setMultas(BigDecimal multas) {
        this.multas = multas;
    }
    
    
    
    
}
