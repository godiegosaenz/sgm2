/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 */
@Entity
@Table(name = "finan_prestamo_predio", schema = SchemasConfig.APP1)
@XmlRootElement
public class FinanPrestamoPredio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Expose
    private Long id;
    @Column(name = "monto_prestamo")
    private BigDecimal montoPrestamo;
    @Size(max = 2)
    @Column(name = "plazo_prestamo")
    @Expose
    private String plazoPrestamo;
    @Column(name = "fecha_consecion_prestamo")
    @Expose
    @Temporal(TemporalType.DATE)
    private Date fechaConsecionPrestamo;
    @Column(name = "porcentaje_exon_prestamo")
    @Expose
    private Integer porcentajeExonPrestamo;
    @Size(max = 12)
    @Column(name = "numero_tramite")
    @Expose
    private String numeroTramite;
    @JoinColumn(name = "estado_prestamo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem estadoPrestamo;
    @Column(name = "comentario_estado_prestamo")
    @Expose
    private String comentarioEstadoPrestamo;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date fechaIngreso;
    @Basic(optional = false)
    @Column(name = "codigo_prestamo", columnDefinition = "")
    @Expose
    private Long codigoPrestamo;
    @Column(name = "monto_inicial_prestamo")
    @Expose
    private BigDecimal montoInicialPrestamo;
    @Column(name = "tasa_interes_prestamo")
    @Expose
    private BigDecimal tasaInteresPrestamo;
    @Column(name = "saldo_2013")
    @Expose
    private BigDecimal saldo2013;
    @JoinColumn(name="predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name="cod_finan_prestamo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem codFinanPrestamo;
    @JoinColumn(name="modalidad_prestamo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem modalidadPrestamo;

    public FinanPrestamoPredio() {
    }

    public FinanPrestamoPredio(Long id) {
        this.id = id;
    }

    public FinanPrestamoPredio(Long id, Long codigoPrestamo) {
        this.id = id;
        this.codigoPrestamo = codigoPrestamo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontoPrestamo() {
        return montoPrestamo;
    }

    public void setMontoPrestamo(BigDecimal montoPrestamo) {
        this.montoPrestamo = montoPrestamo;
    }

    public String getPlazoPrestamo() {
        return plazoPrestamo;
    }

    public void setPlazoPrestamo(String plazoPrestamo) {
        this.plazoPrestamo = plazoPrestamo;
    }

    public Date getFechaConsecionPrestamo() {
        return fechaConsecionPrestamo;
    }

    public void setFechaConsecionPrestamo(Date fechaConsecionPrestamo) {
        this.fechaConsecionPrestamo = fechaConsecionPrestamo;
    }

    public Integer getPorcentajeExonPrestamo() {
        return porcentajeExonPrestamo;
    }

    public void setPorcentajeExonPrestamo(Integer porcentajeExonPrestamo) {
        this.porcentajeExonPrestamo = porcentajeExonPrestamo;
    }

    public String getNumeroTramite() {
        return numeroTramite;
    }

    public void setNumeroTramite(String numeroTramite) {
        this.numeroTramite = numeroTramite;
    }

    public CtlgItem getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(CtlgItem estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }

    public String getComentarioEstadoPrestamo() {
        return comentarioEstadoPrestamo;
    }

    public void setComentarioEstadoPrestamo(String comentarioEstadoPrestamo) {
        this.comentarioEstadoPrestamo = comentarioEstadoPrestamo;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Long getCodigoPrestamo() {
        return codigoPrestamo;
    }

    public void setCodigoPrestamo(Long codigoPrestamo) {
        this.codigoPrestamo = codigoPrestamo;
    }

    public BigDecimal getMontoInicialPrestamo() {
        return montoInicialPrestamo;
    }

    public void setMontoInicialPrestamo(BigDecimal montoInicialPrestamo) {
        this.montoInicialPrestamo = montoInicialPrestamo;
    }

    public BigDecimal getTasaInteresPrestamo() {
        return tasaInteresPrestamo;
    }

    public void setTasaInteresPrestamo(BigDecimal tasaInteresPrestamo) {
        this.tasaInteresPrestamo = tasaInteresPrestamo;
    }

    public BigDecimal getSaldo2013() {
        return saldo2013;
    }

    public void setSaldo2013(BigDecimal saldo2013) {
        this.saldo2013 = saldo2013;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CtlgItem getCodFinanPrestamo() {
        return codFinanPrestamo;
    }

    public void setCodFinanPrestamo(CtlgItem codFinanPrestamo) {
        this.codFinanPrestamo = codFinanPrestamo;
    }

    public CtlgItem getModalidadPrestamo() {
        return modalidadPrestamo;
    }

    public void setModalidadPrestamo(CtlgItem modalidadPrestamo) {
        this.modalidadPrestamo = modalidadPrestamo;
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
        if (!(object instanceof FinanPrestamoPredio)) {
            return false;
        }
        FinanPrestamoPredio other = (FinanPrestamoPredio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FinanPrestamoPredio[ id=" + id + " ]";
    }
    
}
