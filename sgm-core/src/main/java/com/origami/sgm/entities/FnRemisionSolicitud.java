/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.FnRemisionLiquidacion;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author origami-idea
 */
@Entity
@Table(name = "fn_solicitud_exoneraciones", schema = SchemasConfig.FINANCIERO)
public class FnRemisionSolicitud implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @Size(max = 20)
    @Column(name = "num_resolucion")
    private String numResolucion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentaje_emision")
    private BigDecimal porcentajeEmision;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne
    private CatEnte solicitante;
    @JoinColumn(name = "usuario_creacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioCreacion;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "fecha_aprobacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAprobacion;
    @Column(name = "fecha_pago_maximo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPagoMaximo;
    
    @Column(name = "recargo")
    private BigDecimal recargo;
    @Column(name = "interes")
    private BigDecimal interes;
    @Column(name = "multas")
    private BigDecimal multas;
    @Column(name = "total_remision")
    private BigDecimal totalRemision;
    @Column(name = "total_pago")
    private BigDecimal totalPago;
    @JoinColumn(name = "tramite_tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem tramiteTipo;
    @OneToMany(mappedBy = "exoneracion")
    private List<FnRemisionLiquidacion> fnRemisionLiquidacionCollection;
    @JoinColumn(name = "exoneracion_tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FnExoneracionTipo exoneracionTipo;
    
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FnEstadoExoneracion estado;
    
    
    
    public FnRemisionSolicitud() {
    }

    public FnRemisionSolicitud(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
    }

    public String getNumResolucion() {
        return numResolucion;
    }

    public void setNumResolucion(String numResolucion) {
        this.numResolucion = numResolucion;
    }

    public BigDecimal getPorcentajeEmision() {
        return porcentajeEmision;
    }

    public void setPorcentajeEmision(BigDecimal porcentajeEmision) {
        this.porcentajeEmision = porcentajeEmision;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
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

    public BigDecimal getTotalRemision() {
        return totalRemision;
    }

    public void setTotalRemision(BigDecimal totalRemision) {
        this.totalRemision = totalRemision;
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
        if (!(object instanceof FnRemisionSolicitud)) {
            return false;
        }
        FnRemisionSolicitud other = (FnRemisionSolicitud) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnSolicitudRemision[ id=" + id + " ]";
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public CtlgItem getTramiteTipo() {
        return tramiteTipo;
    }

    public void setTramiteTipo(CtlgItem tramiteTipo) {
        this.tramiteTipo = tramiteTipo;
    }

    public FnExoneracionTipo getExoneracionTipo() {
        return exoneracionTipo;
    }

    public void setExoneracionTipo(FnExoneracionTipo exoneracionTipo) {
        this.exoneracionTipo = exoneracionTipo;
    }

    public AclUser getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(AclUser usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public List<FnRemisionLiquidacion> getFnRemisionLiquidacionCollection() {
        return fnRemisionLiquidacionCollection;
    }

    public void setFnRemisionLiquidacionCollection(List<FnRemisionLiquidacion> fnRemisionLiquidacionCollection) {
        this.fnRemisionLiquidacionCollection = fnRemisionLiquidacionCollection;
    }

    public Date getFechaPagoMaximo() {
        return fechaPagoMaximo;
    }

    public void setFechaPagoMaximo(Date fechaPagoMaximo) {
        this.fechaPagoMaximo = fechaPagoMaximo;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public FnEstadoExoneracion getEstado() {
        return estado;
    }

    public void setEstado(FnEstadoExoneracion estado) {
        this.estado = estado;
    }
    
    
    
    
    
}
