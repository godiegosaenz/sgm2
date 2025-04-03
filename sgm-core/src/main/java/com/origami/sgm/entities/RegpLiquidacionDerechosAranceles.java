/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_liquidacion_derechos_aranceles", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "RegpLiquidacionDerechosAranceles.findAll", query = "SELECT r FROM RegpLiquidacionDerechosAranceles r")})
public class RegpLiquidacionDerechosAranceles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "tasa_catastro", precision = 12, scale = 2)
    private BigDecimal tasaCatastro;
    @Column(name = "inf_adicional")
    private String infAdicional;
    @Column(name = "user_creador")
    private Long userCreador;
    @Size(max = 1)
    @Column(name = "estado_pago", length = 1)
    private String estadoPago;
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "is_registro_propiedad")
    private Boolean isRegistroPropiedad;
    @Column(name = "gastos_generales", precision = 12, scale = 2)
    private BigDecimal gastosGenerales;
    @Column(name = "descuento_porc", precision = 6, scale = 2)
    private BigDecimal descuentoPorc;
    @Column(name = "descuento_valor", precision = 12, scale = 2)
    private BigDecimal descuentoValor;
    @Column(name = "num_tramite_rp")
    private BigInteger numTramiteRp;
    @Column(name = "parentesco_solicitante")
    private String parentescoSolicitante;
    @Column(name = "cantidad_tasas_catastro")
    private BigInteger cantidadTasasCatastro;
    @Column(name = "total_pagar", precision = 14, scale = 2)
    private BigDecimal totalPagar;
    @Column(name = "valor_actos", precision = 14, scale = 2)
    private BigDecimal valorActos;
    @Column(name = "is_exoneracion")
    private Boolean isExoneracion;
    @Column(name = "inscripcion")
    private Boolean inscripcion = false;
    @Column(name = "certificado")
    private Boolean certificado = false;
    @Column(name = "numero_comprobante")
    private BigInteger numeroComprobante;
    /**
     * posibles valores del campo estado:
     * 1 - activo
     * 2 - activo e ingresado como tramite en el activiti
     * 3 - migrado de la base anterior y activo
     * 4 - migrado de la base anterior e ingresado como tramite en el activiti
     */
    @Column(name = "estado")
    private Integer estado = 1;
    @Column(name = "user_edicion")
    private Long userEdicion;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @OneToMany(mappedBy = "liquidacion", fetch = FetchType.LAZY)
    private Collection<RegpIntervinientes> regpIntervinientesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "liquidacion", fetch = FetchType.LAZY)
    private Collection<RegpEntradaSalidaDocs> regpEntradaSalidaDocsCollection;
    @JoinColumn(name = "uso_documento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private VuItems usoDocumento;
    @JoinColumn(name = "historic_tramite", referencedColumnName = "id_tramite", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private HistoricoTramites historicTramite;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "liquidacion", fetch = FetchType.LAZY)
    private Collection<RegpLiquidacionDetalles> regpLiquidacionDetallesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "liquidacion", fetch = FetchType.LAZY)
    private Collection<RegpCertificadosInscripciones> regpCertificadosInscripcionesCollection;

    @Transient
    private String nameUser;
    
    public RegpLiquidacionDerechosAranceles() {
    }

    public RegpLiquidacionDerechosAranceles(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTasaCatastro() {
        return tasaCatastro;
    }

    public void setTasaCatastro(BigDecimal tasaCatastro) {
        this.tasaCatastro = tasaCatastro;
    }

    public String getInfAdicional() {
        return infAdicional;
    }

    public void setInfAdicional(String infAdicional) {
        this.infAdicional = infAdicional;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Boolean getIsRegistroPropiedad() {
        return isRegistroPropiedad;
    }

    public void setIsRegistroPropiedad(Boolean isRegistroPropiedad) {
        this.isRegistroPropiedad = isRegistroPropiedad;
    }

    public BigDecimal getGastosGenerales() {
        return gastosGenerales;
    }

    public void setGastosGenerales(BigDecimal gastosGenerales) {
        this.gastosGenerales = gastosGenerales;
    }

    public BigDecimal getDescuentoPorc() {
        return descuentoPorc;
    }

    public void setDescuentoPorc(BigDecimal descuentoPorc) {
        this.descuentoPorc = descuentoPorc;
    }

    public BigDecimal getDescuentoValor() {
        return descuentoValor;
    }

    public void setDescuentoValor(BigDecimal descuentoValor) {
        this.descuentoValor = descuentoValor;
    }

    public BigInteger getNumTramiteRp() {
        return numTramiteRp;
    }

    public void setNumTramiteRp(BigInteger numTramiteRp) {
        this.numTramiteRp = numTramiteRp;
    }

    public String getParentescoSolicitante() {
        return parentescoSolicitante;
    }

    public void setParentescoSolicitante(String parentescoSolicitante) {
        this.parentescoSolicitante = parentescoSolicitante;
    }

    public BigInteger getCantidadTasasCatastro() {
        return cantidadTasasCatastro;
    }

    public void setCantidadTasasCatastro(BigInteger cantidadTasasCatastro) {
        this.cantidadTasasCatastro = cantidadTasasCatastro;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public Boolean getIsExoneracion() {
        return isExoneracion;
    }

    public void setIsExoneracion(Boolean isExoneracion) {
        this.isExoneracion = isExoneracion;
    }

    public Boolean getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Boolean inscripcion) {
        this.inscripcion = inscripcion;
    }

    public Boolean getCertificado() {
        return certificado;
    }

    public void setCertificado(Boolean certificado) {
        this.certificado = certificado;
    }

    public BigInteger getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(BigInteger numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public BigDecimal getValorActos() {
        return valorActos;
    }

    public void setValorActos(BigDecimal valorActos) {
        this.valorActos = valorActos;
    }

    public Long getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(Long userCreador) {
        this.userCreador = userCreador;
    }
    
    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Long getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(Long userEdicion) {
        this.userEdicion = userEdicion;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public Collection<RegpIntervinientes> getRegpIntervinientesCollection() {
        return regpIntervinientesCollection;
    }

    public void setRegpIntervinientesCollection(Collection<RegpIntervinientes> regpIntervinientesCollection) {
        this.regpIntervinientesCollection = regpIntervinientesCollection;
    }

    public Collection<RegpEntradaSalidaDocs> getRegpEntradaSalidaDocsCollection() {
        return regpEntradaSalidaDocsCollection;
    }

    public void setRegpEntradaSalidaDocsCollection(Collection<RegpEntradaSalidaDocs> regpEntradaSalidaDocsCollection) {
        this.regpEntradaSalidaDocsCollection = regpEntradaSalidaDocsCollection;
    }

    public Collection<RegpCertificadosInscripciones> getRegpCertificadosInscripcionesCollection() {
        return regpCertificadosInscripcionesCollection;
    }

    public void setRegpCertificadosInscripcionesCollection(Collection<RegpCertificadosInscripciones> regpCertificadosInscripcionesCollection) {
        this.regpCertificadosInscripcionesCollection = regpCertificadosInscripcionesCollection;
    }

    public VuItems getUsoDocumento() {
        return usoDocumento;
    }

    public void setUsoDocumento(VuItems usoDocumento) {
        this.usoDocumento = usoDocumento;
    }

    public HistoricoTramites getHistoricTramite() {
        return historicTramite;
    }

    public void setHistoricTramite(HistoricoTramites historicTramite) {
        this.historicTramite = historicTramite;
    }

    public Collection<RegpLiquidacionDetalles> getRegpLiquidacionDetallesCollection() {
        return regpLiquidacionDetallesCollection;
    }

    public void setRegpLiquidacionDetallesCollection(Collection<RegpLiquidacionDetalles> regpLiquidacionDetallesCollection) {
        this.regpLiquidacionDetallesCollection = regpLiquidacionDetallesCollection;
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
        if (!(object instanceof RegpLiquidacionDerechosAranceles)) {
            return false;
        }
        RegpLiquidacionDerechosAranceles other = (RegpLiquidacionDerechosAranceles) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpLiquidacionDerechosAranceles[ id=" + id + " ]";
    }

}
