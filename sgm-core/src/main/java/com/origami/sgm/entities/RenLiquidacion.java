/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.FnConvenioPago;
import com.origami.sgm.entities.EmisionesRuralesExcel;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatCategoriasPredio;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Where;
import com.origami.sgm.entities.UtilsEnts;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_liquidacion", schema = SchemasConfig.FINANCIERO)
@SequenceGenerator(name = "ren_liquidacion_id_seq", sequenceName = SchemasConfig.FINANCIERO + ".ren_liquidacion_id_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "RenLiquidacion.findAll", query = "SELECT r FROM RenLiquidacion r")
    ,
    @NamedQuery(name = "RenLiquidacion.findPagoPredio", query = "SELECT r FROM RenLiquidacion r INNER JOIN r.tipoLiquidacion t INNER JOIN r.renPagoCollection p WHERE t.codigoTituloReporte = :codigoTitulo AND r.predio = :predio AND r.anio BETWEEN :anioInicio AND :anioFin")
    ,
    @NamedQuery(name = "RenLiquidacion.findPagoPredioRustico", query = "SELECT r FROM RenLiquidacion r INNER JOIN r.tipoLiquidacion t INNER JOIN r.renPagoCollection p WHERE t.codigoTituloReporte = = :codigoTitulo AND r.predio = :predio AND r.anio BETWEEN :anioInicio AND :anioFin")})
public class RenLiquidacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ren_liquidacion_id_seq")
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "num_comprobante")
    private BigInteger numComprobante;
    @Column(name = "num_liquidacion")
    private BigInteger numLiquidacion;
    @Size(max = 25)
    @Column(name = "id_liquidacion")
    private String idLiquidacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_pago")
    private BigDecimal totalPago;
    @Column(name = "banda_impositiva")
    private BigDecimal bandaImpositiva;
    @Column(name = "saldo")
    private BigDecimal saldo;
    @Size(max = 20)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Size(max = 20)
    @Column(name = "num_reporte")
    private String numReporte;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso = new Date();
    @JoinColumn(name = "comprador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte comprador;
    @JoinColumn(name = "vendedor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte vendedor;

    @Column(name = "codigo_local")
    @Size(max = 40)
    private String codigoLocal;
    @Column(name = "costo_adq")
    private BigDecimal costoAdq;
    @Column(name = "cuantia")
    private BigDecimal cuantia;
    @Column(name = "fecha_contrato_ant")
    @Temporal(TemporalType.DATE)
    private Date fechaContratoAnt;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "predio_historico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predioHistorico;
    @OneToMany(mappedBy = "liquidacion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    @OrderBy("num_comprobante ASC")
    private Collection<RenPago> renPagoCollection;

    @JoinColumn(name = "categoria_predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCategoriasPredio categoriaPredio;

    @JoinColumn(name = "tipo_liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoLiquidacion tipoLiquidacion;

    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;

    @JoinColumn(name = "local_comercial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalComercial localComercial;

    @JoinColumn(name = "estado_liquidacion", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private RenEstadoLiquidacion estadoLiquidacion;

    @OneToMany(mappedBy = "liquidacion", fetch = FetchType.LAZY)
    @OrderBy("valor ASC")
    private Collection<RenDetLiquidacion> renDetLiquidacionCollection;

    @OneToMany(mappedBy = "liquidacion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<RenSolicitudesLiquidacion> renSolicitudesLiquidacionCollection;

    @Column(name = "observacion")
    private String observacion;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "valor_comercial")
    private BigDecimal valorComercial;
    @Column(name = "valor_catastral")
    private BigDecimal valorCatastral;
    @Column(name = "valor_hipoteca")
    private BigDecimal valorHipoteca;
    @Column(name = "valor_nominal")
    private BigDecimal valorNominal;
    @Column(name = "valor_mora")
    private BigDecimal valorMora;
    @Column(name = "total_adicionales")
    private BigDecimal totalAdicionales;
    @Column(name = "otros")
    private BigDecimal otros;
    @Column(name = "valor_compra")
    private BigDecimal valorCompra;
    @Column(name = "valor_venta")
    private BigDecimal valorVenta;
    @Column(name = "valor_exoneracion")
    private BigDecimal valorExoneracion;
    @Column(name = "valor_mejoras")
    private BigDecimal valorMejoras;
    @Column(name = "area_total")
    private BigDecimal areaTotal;
    @Column(name = "patrimonio")
    private BigDecimal patrimonio;

    @Column(name = "avaluo_construccion")
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_solar")
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_municipal")
    private BigDecimal avaluoMunicipal;

    @JoinColumn(name = "predio_rustico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredioRustico predioRustico;

    @Column(name = "exoneracion_descripcion")
    private String exoneracionDescripcion;

    @Transient
    private BigDecimal valorCoactiva = new BigDecimal("0.00");
    @Transient
    private BigDecimal descuento = new BigDecimal("0.00");
    @Transient
    private BigDecimal recargo = new BigDecimal("0.00");
    @Transient
    private BigDecimal interes = new BigDecimal("0.00");
    @Transient
    private BigDecimal pagoFinal;

    /* estado_coactiva :
     1 : NO COACTIVA / 2 : EN COACTIVA / 3 : COACTIVA PAGADA */
    @Column(name = "estado_coactiva")
    private Integer estadoCoactiva;

    @Column(name = "coactiva")
    private Boolean coactiva;
    @Column(name = "bombero")
    private Boolean bombero = false;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "liquidacion")
    private RenValoresPlusvalia renValoresPlusvalia;
    @OneToMany(mappedBy = "liquidacion")
    private Collection<CmMultas> cmMultasCollection;
    @Column(name = "nombre_comprador")
    private String nombreComprador;
    @Column(name = "nombre_comprador_historic")
    private String nombreCompradorHistoric;
    @Column(name = "exonerado")
    private Boolean estaExonerado;
    @OneToMany(mappedBy = "liquidacionOriginal")
    private Collection<FnExoneracionLiquidacion> exoneracionLiquidacionCollection;
    @OneToMany(mappedBy = "liquidacionPosterior")
    private Collection<FnExoneracionLiquidacion> exoneracionLiquidacionPosteriorCollection;

    @JoinColumn(name = "rural_excel", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private EmisionesRuralesExcel ruralExcel;

    @Column(name = "clave_ame")
    private String claveAME;

    @JoinColumn(name = "convenio_pago", referencedColumnName = "id")
    @ManyToOne
    private FnConvenioPago convenioPago;

    @Size(max = 200)
    @Column(name = "usuario_valida")
    private String usuarioValida;
    @Column(name = "mes")
    private Integer mes;

    public RenLiquidacion() {
    }

    public RenLiquidacion(Long id) {
        this.id = id;
    }

    public RenLiquidacion(Long id, Date fechaIngreso) {
        this.id = id;
        this.fechaIngreso = fechaIngreso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public BigInteger getNumComprobante() {
        return numComprobante;
    }

    public void setNumComprobante(BigInteger numComprobante) {
        this.numComprobante = numComprobante;
    }

    public BigInteger getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(BigInteger numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }

    public String getIdLiquidacion() {
        return idLiquidacion;
    }

    public void setIdLiquidacion(String idLiquidacion) {
        this.idLiquidacion = idLiquidacion;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public BigDecimal getBandaImpositiva() {
        return bandaImpositiva;
    }

    public void setBandaImpositiva(BigDecimal bandaImpositiva) {
        this.bandaImpositiva = bandaImpositiva;
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

    public Collection<RenPago> getRenPagoCollection() {
        return renPagoCollection;
    }

    public void setRenPagoCollection(Collection<RenPago> renPagoCollection) {
        this.renPagoCollection = renPagoCollection;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public RenEstadoLiquidacion getEstadoLiquidacion() {
        return estadoLiquidacion;
    }

    public void setEstadoLiquidacion(RenEstadoLiquidacion estadoLiquidacion) {
        this.estadoLiquidacion = estadoLiquidacion;
    }

    public CatCategoriasPredio getCategoriaPredio() {
        return categoriaPredio;
    }

    public void setCategoriaPredio(CatCategoriasPredio categoriaPredio) {
        this.categoriaPredio = categoriaPredio;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public void calcularPago() {
        this.pagoFinal = this.saldo.add(this.recargo).subtract(this.descuento).add(this.interes);
        if (estadoCoactiva != null && estadoCoactiva == 2) {
            this.valorCoactiva = UtilsEnts.bigdecimalTo2Decimals(this.pagoFinal.multiply(new BigDecimal("0.1")));
        }
    }

    public void calcularPagoConCoactiva() {
        this.pagoFinal = this.saldo.add(this.recargo).subtract(this.descuento).add(this.interes);
        if (this.estadoCoactiva != null && this.estadoCoactiva == 2) {
            this.valorCoactiva = UtilsEnts.bigdecimalTo2Decimals(this.pagoFinal.multiply(new BigDecimal("0.1")));
            this.pagoFinal = this.pagoFinal.add(this.valorCoactiva);
        }
    }

    public Boolean calculoMinimoPago(BigDecimal valorTotal) {
        BigDecimal minimoPago = this.recargo.add(this.interes);
        if (this.estadoCoactiva != null && this.estadoCoactiva == 2) {
            minimoPago = minimoPago.add(UtilsEnts.bigdecimalTo2Decimals(minimoPago.multiply(new BigDecimal("0.1"))));
        }
        System.out.println("// minimo pago: " + minimoPago);
        return valorTotal.compareTo(minimoPago) <= 0;
    }

    public CatEnte getComprador() {
        return comprador;
    }

    public void setComprador(CatEnte comprador) {
        this.comprador = comprador;
    }

    public CatEnte getVendedor() {
        return vendedor;
    }

    public void setVendedor(CatEnte vendedor) {
        this.vendedor = vendedor;
    }

    public BigDecimal getCostoAdq() {
        return costoAdq;
    }

    public void setCostoAdq(BigDecimal costoAdq) {
        this.costoAdq = costoAdq;
    }

    public BigDecimal getCuantia() {
        return cuantia;
    }

    public void setCuantia(BigDecimal cuantia) {
        this.cuantia = cuantia;
    }

    public Date getFechaContratoAnt() {
        return fechaContratoAnt;
    }

    public void setFechaContratoAnt(Date fechaContratoAnt) {
        this.fechaContratoAnt = fechaContratoAnt;
    }

    public String getCodigoLocal() {
        return codigoLocal;
    }

    public void setCodigoLocal(String codigoLocal) {
        this.codigoLocal = codigoLocal;
    }

    public Collection<RenDetLiquidacion> getRenDetLiquidacionCollection() {
        return renDetLiquidacionCollection;
    }

    public void setRenDetLiquidacionCollection(Collection<RenDetLiquidacion> renDetLiquidacionCollection) {
        this.renDetLiquidacionCollection = renDetLiquidacionCollection;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Boolean getCoactiva() {
        return coactiva;
    }

    public void setCoactiva(Boolean coactiva) {
        this.coactiva = coactiva;
    }

    public RenValoresPlusvalia getRenValoresPlusvalia() {
        return renValoresPlusvalia;
    }

    public void setRenValoresPlusvalia(RenValoresPlusvalia renValoresPlusvalia) {
        this.renValoresPlusvalia = renValoresPlusvalia;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(String numReporte) {
        this.numReporte = numReporte;
    }

    public BigDecimal getValorComercial() {
        return valorComercial;
    }

    public void setValorComercial(BigDecimal valorComercial) {
        this.valorComercial = valorComercial;
    }

    public BigDecimal getValorCatastral() {
        return valorCatastral;
    }

    public void setValorCatastral(BigDecimal valorCatastral) {
        this.valorCatastral = valorCatastral;
    }

    public BigDecimal getValorHipoteca() {
        return valorHipoteca;
    }

    public void setValorHipoteca(BigDecimal valorHipoteca) {
        this.valorHipoteca = valorHipoteca;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(BigDecimal valorNominal) {
        this.valorNominal = valorNominal;
    }

    public BigDecimal getValorMora() {
        return valorMora;
    }

    public void setValorMora(BigDecimal valorMora) {
        this.valorMora = valorMora;
    }

    public BigDecimal getTotalAdicionales() {
        return totalAdicionales;
    }

    public void setTotalAdicionales(BigDecimal totalAdicionales) {
        this.totalAdicionales = totalAdicionales;
    }

    public BigDecimal getOtros() {
        return otros;
    }

    public void setOtros(BigDecimal otros) {
        this.otros = otros;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getValorVenta() {
        return valorVenta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valorVenta = valorVenta;
    }

    public BigDecimal getValorMejoras() {
        return valorMejoras;
    }

    public void setValorMejoras(BigDecimal valorMejoras) {
        this.valorMejoras = valorMejoras;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(BigDecimal patrimonio) {
        this.patrimonio = patrimonio;
    }

    public Boolean getBombero() {
        return bombero;
    }

    public void setBombero(Boolean bombero) {
        this.bombero = bombero;
    }

    public CatPredioRustico getPredioRustico() {
        return predioRustico;
    }

    public void setPredioRustico(CatPredioRustico predioRustico) {
        this.predioRustico = predioRustico;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
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

    public BigDecimal getPagoFinal() {
        return pagoFinal;
    }

    public void setPagoFinal(BigDecimal pagoFinal) {
        this.pagoFinal = pagoFinal;
    }

    public BigDecimal getValorCoactiva() {
        return valorCoactiva;
    }

    public void setValorCoactiva(BigDecimal valorCoactiva) {
        this.valorCoactiva = valorCoactiva;
    }

    public Integer getEstadoCoactiva() {
        return estadoCoactiva;
    }

    public void setEstadoCoactiva(Integer estadoCoactiva) {
        this.estadoCoactiva = estadoCoactiva;
    }

    public Collection<RenSolicitudesLiquidacion> getRenSolicitudesLiquidacionCollection() {
        return renSolicitudesLiquidacionCollection;
    }

    public void setRenSolicitudesLiquidacionCollection(Collection<RenSolicitudesLiquidacion> renSolicitudesLiquidacionCollection) {
        this.renSolicitudesLiquidacionCollection = renSolicitudesLiquidacionCollection;
    }

    public Collection<CmMultas> getCmMultasCollection() {
        return cmMultasCollection;
    }

    public void setCmMultasCollection(Collection<CmMultas> cmMultasCollection) {
        this.cmMultasCollection = cmMultasCollection;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    public Collection<FnExoneracionLiquidacion> getExoneracionLiquidacionCollection() {
        return exoneracionLiquidacionCollection;
    }

    public void setExoneracionLiquidacionCollection(Collection<FnExoneracionLiquidacion> exoneracionLiquidacionCollection) {
        this.exoneracionLiquidacionCollection = exoneracionLiquidacionCollection;
    }

    public Collection<FnExoneracionLiquidacion> getExoneracionLiquidacionPosteriorCollection() {
        return exoneracionLiquidacionPosteriorCollection;
    }

    public void setExoneracionLiquidacionPosteriorCollection(Collection<FnExoneracionLiquidacion> exoneracionLiquidacionPosteriorCollection) {
        this.exoneracionLiquidacionPosteriorCollection = exoneracionLiquidacionPosteriorCollection;
    }

    public Boolean getEstaExonerado() {
        return estaExonerado;
    }

    public void setEstaExonerado(Boolean estaExonerado) {
        this.estaExonerado = estaExonerado;
    }

    public EmisionesRuralesExcel getRuralExcel() {
        return ruralExcel;
    }

    public void setRuralExcel(EmisionesRuralesExcel ruralExcel) {
        this.ruralExcel = ruralExcel;
    }

    public String getExoneracionDescripcion() {
        return exoneracionDescripcion;
    }

    public void setExoneracionDescripcion(String exoneracionDescripcion) {
        this.exoneracionDescripcion = exoneracionDescripcion;
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
        if (!(object instanceof RenLiquidacion)) {
            return false;
        }
        RenLiquidacion other = (RenLiquidacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenLiquidacion[ id=" + id + " ]";
    }

    public String getNombreCompradorHistoric() {
        return nombreCompradorHistoric;
    }

    public void setNombreCompradorHistoric(String nombreCompradorHistoric) {
        this.nombreCompradorHistoric = nombreCompradorHistoric;
    }

    public CatPredio getPredioHistorico() {
        return predioHistorico;
    }

    public void setPredioHistorico(CatPredio predioHistorico) {
        this.predioHistorico = predioHistorico;
    }

    public BigDecimal getValorExoneracion() {
        return valorExoneracion;
    }

    public void setValorExoneracion(BigDecimal valorExoneracion) {
        this.valorExoneracion = valorExoneracion;
    }

    public String getClaveAME() {
        return claveAME;
    }

    public void setClaveAME(String claveAME) {
        this.claveAME = claveAME;
    }

    public FnConvenioPago getConvenioPago() {
        return convenioPago;
    }

    public void setConvenioPago(FnConvenioPago convenioPago) {
        this.convenioPago = convenioPago;
    }

    public String getUsuarioValida() {
        return usuarioValida;
    }

    public void setUsuarioValida(String usuarioValida) {
        this.usuarioValida = usuarioValida;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    
    
}
