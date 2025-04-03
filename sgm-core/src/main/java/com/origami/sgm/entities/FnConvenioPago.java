/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author JC
 */
@Entity
@Table(name = "fn_convenio_pago", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "FnConvenioPago.findAll", query = "SELECT f FROM FnConvenioPago f")
    , @NamedQuery(name = "FnConvenioPago.findById", query = "SELECT f FROM FnConvenioPago f WHERE f.id = :id")
    , @NamedQuery(name = "FnConvenioPago.findByDescripcion", query = "SELECT f FROM FnConvenioPago f WHERE f.descripcion = :descripcion")
    , @NamedQuery(name = "FnConvenioPago.findByFechaInicio", query = "SELECT f FROM FnConvenioPago f WHERE f.fechaInicio = :fechaInicio")
    , @NamedQuery(name = "FnConvenioPago.findByFechaPrimeraCuota", query = "SELECT f FROM FnConvenioPago f WHERE f.fechaPrimeraCuota = :fechaPrimeraCuota")
    , @NamedQuery(name = "FnConvenioPago.findByDeudaInicial", query = "SELECT f FROM FnConvenioPago f WHERE f.deudaInicial = :deudaInicial")
    , @NamedQuery(name = "FnConvenioPago.findByPorcientoInicial", query = "SELECT f FROM FnConvenioPago f WHERE f.porcientoInicial = :porcientoInicial")
    , @NamedQuery(name = "FnConvenioPago.findByValorPorcientoInicial", query = "SELECT f FROM FnConvenioPago f WHERE f.valorPorcientoInicial = :valorPorcientoInicial")
    , @NamedQuery(name = "FnConvenioPago.findByDiferenciaFinanciar", query = "SELECT f FROM FnConvenioPago f WHERE f.diferenciaFinanciar = :diferenciaFinanciar")
    , @NamedQuery(name = "FnConvenioPago.findByTasaInteresMensual", query = "SELECT f FROM FnConvenioPago f WHERE f.tasaInteresMensual = :tasaInteresMensual")
    , @NamedQuery(name = "FnConvenioPago.findByCantidadMesesDiferir", query = "SELECT f FROM FnConvenioPago f WHERE f.cantidadMesesDiferir = :cantidadMesesDiferir")
    , @NamedQuery(name = "FnConvenioPago.findByInteresCausado", query = "SELECT f FROM FnConvenioPago f WHERE f.interesCausado = :interesCausado")
    , @NamedQuery(name = "FnConvenioPago.findByDeudaDiferir", query = "SELECT f FROM FnConvenioPago f WHERE f.deudaDiferir = :deudaDiferir")
    , @NamedQuery(name = "FnConvenioPago.findByEstado", query = "SELECT f FROM FnConvenioPago f WHERE f.estado = :estado")})
public class FnConvenioPago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "memo_detalle")
    private String memoDetalle;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    @Column(name = "fecha_primera_cuota")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPrimeraCuota;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "deuda_inicial")
    private BigDecimal deudaInicial;
    @Column(name = "porciento_inicial")
    private BigDecimal porcientoInicial;
    @Column(name = "valor_porciento_inicial")
    private BigDecimal valorPorcientoInicial;
    @Column(name = "diferencia_financiar")
    private BigDecimal diferenciaFinanciar;
    @Column(name = "tasa_interes_mensual")
    private BigDecimal tasaInteresMensual;
    @Column(name = "cantidad_meses_diferir")
    private Integer cantidadMesesDiferir;
    @Column(name = "interes_causado")
    private BigDecimal interesCausado;
    @Column(name = "deuda_diferir")
    private BigDecimal deudaDiferir;
    @Column(name = "estado")
    private Short estado;
    @Column(name = "usuario_ingreso", length = 100)
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "convenio")
    private List<FnConvenioPagoDetalle> cuotasConvenio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "convenio")
    private List<FnConvenioPagoArchivo> archivos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "convenio")
    private List<FnConvenioPagoObservacion> observaciones;
    @JoinColumn(name = "pago_final", referencedColumnName = "id")
    @ManyToOne
    private RenPago pagoFinal;
    @JoinColumn(name = "pago_inicial", referencedColumnName = "id")
    @ManyToOne
    private RenPago pagoInicial;
    @JoinColumn(name = "contribuyente", referencedColumnName = "id")
    @ManyToOne
    private CatEnte contribuyente;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne
    private CatPredio predio;

    public FnConvenioPago() {
        this.estado = (short) 0;
        this.porcientoInicial = BigDecimal.valueOf(20);
        this.cantidadMesesDiferir = 6;
    }

    public FnConvenioPago(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaPrimeraCuota() {
        return fechaPrimeraCuota;
    }

    public void setFechaPrimeraCuota(Date fechaPrimeraCuota) {
        this.fechaPrimeraCuota = fechaPrimeraCuota;
    }

    public BigDecimal getDeudaInicial() {
        return deudaInicial;
    }

    public void setDeudaInicial(BigDecimal deudaInicial) {
        this.deudaInicial = deudaInicial;
    }

    public BigDecimal getPorcientoInicial() {
        return porcientoInicial;
    }

    public void setPorcientoInicial(BigDecimal porcientoInicial) {
        this.porcientoInicial = porcientoInicial;
    }

    public BigDecimal getValorPorcientoInicial() {
        return valorPorcientoInicial;
    }

    public void setValorPorcientoInicial(BigDecimal valorPorcientoInicial) {
        this.valorPorcientoInicial = valorPorcientoInicial;
    }

    public BigDecimal getDiferenciaFinanciar() {
        return diferenciaFinanciar;
    }

    public void setDiferenciaFinanciar(BigDecimal diferenciaFinanciar) {
        this.diferenciaFinanciar = diferenciaFinanciar;
    }

    public BigDecimal getTasaInteresMensual() {
        return tasaInteresMensual;
    }

    public void setTasaInteresMensual(BigDecimal tasaInteresMensual) {
        this.tasaInteresMensual = tasaInteresMensual;
    }

    public Integer getCantidadMesesDiferir() {
        return cantidadMesesDiferir;
    }

    public void setCantidadMesesDiferir(Integer cantidadMesesDiferir) {
        this.cantidadMesesDiferir = cantidadMesesDiferir;
    }

    public BigDecimal getInteresCausado() {
        return interesCausado;
    }

    public void setInteresCausado(BigDecimal interesCausado) {
        this.interesCausado = interesCausado;
    }

    public BigDecimal getDeudaDiferir() {
        return deudaDiferir;
    }

    public void setDeudaDiferir(BigDecimal deudaDiferir) {
        this.deudaDiferir = deudaDiferir;
    }

    /*
     0 - PREELABORACION DEL CONVENIO.
     1 - ACTUALIZACION DE CUOTAS O PORCENTAJE INICIAL PARA PAGO INICIAL.
     2 - PENDIENTE DE ACTIVACION POR COBRO ABONO INICIAL.
     3 - APROBADO
     4 - NO APROBADO
     5 - CANCELADO
     6 - COMPLETADO
     */
    public Short getEstado() {
        return estado;
    }

    public String getObservacion() {

        switch (this.estado) {
            case 0:
                return "PREELABORACION DEL CONVENIO.";
            case 1:
                return "ACTUALIZACION DE CUOTAS O PORCENTAJE INICIAL PARA PAGO INICIAL.";
            case 2:
                return "PENDIENTE DE ACTIVACION POR COBRO ABONO INICIAL.";
            case 3:
                return "APROBADO.";
            case 4:
                return "NO APROBADO.";
            case 5:
                return "CANCELADO.";
            default:
                return "COMPLETADO.";
        }
    }

    public void setEstado(Short estado) {
        this.estado = estado;
    }

    public List<FnConvenioPagoDetalle> getCuotasConvenio() {
        return cuotasConvenio;
    }

    public void setCuotasConvenio(List<FnConvenioPagoDetalle> cuotasConvenio) {
        this.cuotasConvenio = cuotasConvenio;
    }

    public RenPago getPagoFinal() {
        return pagoFinal;
    }

    public void setPagoFinal(RenPago pagoFinal) {
        this.pagoFinal = pagoFinal;
    }

    public RenPago getPagoInicial() {
        return pagoInicial;
    }

    public void setPagoInicial(RenPago pagoInicial) {
        this.pagoInicial = pagoInicial;
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

    public CatEnte getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(CatEnte contribuyente) {
        this.contribuyente = contribuyente;
    }

    public List<FnConvenioPagoArchivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<FnConvenioPagoArchivo> archivos) {
        this.archivos = archivos;
    }

    public List<FnConvenioPagoObservacion> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(List<FnConvenioPagoObservacion> observaciones) {
        this.observaciones = observaciones;
    }

    public String getMemoDetalle() {
        return memoDetalle;
    }

    public void setMemoDetalle(String memoDetalle) {
        this.memoDetalle = memoDetalle;
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
        if (!(object instanceof FnConvenioPago)) {
            return false;
        }
        FnConvenioPago other = (FnConvenioPago) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnConvenioPago[ id=" + id + " ]";
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }


}
