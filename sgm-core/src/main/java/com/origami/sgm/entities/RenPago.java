/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_pago", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenPago.findAll", query = "SELECT r FROM RenPago r")})
@SequenceGenerator(name = "ren_pago_id_seq", sequenceName = SchemasConfig.FINANCIERO + ".ren_pago_id_seq", allocationSize = 1)
public class RenPago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ren_pago_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_pago")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPago;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;    
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado")
    private boolean estado;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLiquidacion liquidacion;
    @Column(name = "num_comprobante")
    private Long numComprobante;
    @Column(name = "observacion")
    private String observacion;
    @JoinColumn(name = "cajero", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser cajero;
    
    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    private Collection<RenPagoDetalle> renPagoDetalles;
    
    @OneToMany(mappedBy = "pago", fetch = FetchType.LAZY)
    private Collection<RenPagoRubro> renPagoRubros;
    
    @Column(name = "descuento")
    private BigDecimal descuento = new BigDecimal("0.00"); 
    @Column(name = "recargo")
    private BigDecimal recargo = new BigDecimal("0.00"); 
    @Column(name = "interes")
    private BigDecimal interes = new BigDecimal("0.00"); 
    @Column(name = "nombre_contribuyente")
    private String nombreContribuyente;    
    @JoinColumn(name = "contribuyente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte contribuyente;
    @Column(name = "fecha_anulacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAnulacion;

    public RenPago() {
    }

    public RenPago(Long id) {
        this.id = id;
    }

    public RenPago(Long id, Date fechaPago, boolean estado) {
        this.id = id;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Long getNumComprobante() {
        return numComprobante;
    }

    public void setNumComprobante(Long numComprobante) {
        this.numComprobante = numComprobante;
    }

    public AclUser getCajero() {
        return cajero;
    }

    public void setCajero(AclUser cajero) {
        this.cajero = cajero;
    }

    public Collection<RenPagoDetalle> getRenPagoDetalles() {
        return renPagoDetalles;
    }

    public void setRenPagoDetalles(Collection<RenPagoDetalle> renPagoDetalles) {
        this.renPagoDetalles = renPagoDetalles;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Collection<RenPagoRubro> getRenPagoRubros() {
        return renPagoRubros;
    }

    public void setRenPagoRubros(Collection<RenPagoRubro> renPagoRubros) {
        this.renPagoRubros = renPagoRubros;
    }

    public String getNombreContribuyente() {
        return nombreContribuyente;
    }

    public void setNombreContribuyente(String nombreContribuyente) {
        this.nombreContribuyente = nombreContribuyente;
    }

    public CatEnte getContribuyente() {
        return contribuyente;
    }

    public Date getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(Date fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public void setContribuyente(CatEnte contribuyente) {
        this.contribuyente = contribuyente;
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
        if (!(object instanceof RenPago)) {
            return false;
        }
        RenPago other = (RenPago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenPago[ id=" + id + " ]";
    }
    
    
}
