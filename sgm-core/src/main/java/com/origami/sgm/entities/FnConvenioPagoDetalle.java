/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Dairon Freddy
 */
@Entity
@Table(name = "fn_convenio_pago_detalle", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "FnConvenioPagoDetalle.findAll", query = "SELECT f FROM FnConvenioPagoDetalle f")
    , @NamedQuery(name = "FnConvenioPagoDetalle.findById", query = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.id = :id ")
    , @NamedQuery(name = "FnConvenioPagoDetalle.findByMes", query = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.convenio.id = :convenio AND f.liquidacion.estadoLiquidacion.id = :estado ORDER BY f.mes")
    , @NamedQuery(name = "FnConvenioPagoDetalle.findByFechaMaximaPago", query = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.fechaMaximaPago = :fechaMaximaPago")
    , @NamedQuery(name = "FnConvenioPagoDetalle.findByDescripcion", query = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.descripcion = :descripcion")
    , @NamedQuery(name = "FnConvenioPagoDetalle.findByDeuda", query = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.deuda = :deuda")
    , @NamedQuery(name = "FnConvenioPagoDetalle.findByEstado", query = "SELECT f FROM FnConvenioPagoDetalle f WHERE f.estado = :estado")})
public class FnConvenioPagoDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "mes")
    private Integer mes;
    @Column(name = "fecha_maxima_pago")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMaximaPago;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "deuda")
    private BigDecimal deuda;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "convenio", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private FnConvenioPago convenio;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne
    private RenLiquidacion liquidacion;

    public FnConvenioPagoDetalle() {
    }

    public FnConvenioPagoDetalle(Long id) {
        this.id = id;
    }

    public FnConvenioPagoDetalle(Long id, BigDecimal deuda) {
        this.id = id;
        this.deuda = deuda;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Date getFechaMaximaPago() {
        return fechaMaximaPago;
    }

    public void setFechaMaximaPago(Date fechaMaximaPago) {
        this.fechaMaximaPago = fechaMaximaPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getDeuda() {
        return deuda;
    }

    public void setDeuda(BigDecimal deuda) {
        this.deuda = deuda;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public FnConvenioPago getConvenio() {
        return convenio;
    }

    public void setConvenio(FnConvenioPago convenio) {
        this.convenio = convenio;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
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
        if (!(object instanceof FnConvenioPagoDetalle)) {
            return false;
        }
        FnConvenioPagoDetalle other = (FnConvenioPagoDetalle) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnConvenioPagoDetalle[ id=" + id + " ]";
    }
    
}
