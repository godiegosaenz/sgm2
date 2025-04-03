/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_certificado_avaluo", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatCertificadoAvaluo.findAll", query = "SELECT c FROM CatCertificadoAvaluo c"),
    @NamedQuery(name = "CatCertificadoAvaluo.findById", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.id = :id"),
    @NamedQuery(name = "CatCertificadoAvaluo.findBySecuencia", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.secuencia = :secuencia"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByNumCert", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.numCert = :numCert"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByCodComprobante", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.codComprobante = :codComprobante"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByFecha", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByIdentPredial", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.identPredial = :identPredial"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByDetalle", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.detalle = :detalle"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByCodigoActual", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.codigoActual = :codigoActual"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByRegistroCatastral", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.registroCatastral = :registroCatastral"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByAvalSolar", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.avalSolar = :avalSolar"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByAvalConstruccion", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.avalConstruccion = :avalConstruccion"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByAvalPropiedad", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.avalPropiedad = :avalPropiedad"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByAvalCatastral", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.avalCatastral = :avalCatastral"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByPoligono", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.poligono = :poligono"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByDirCat", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.dirCat = :dirCat"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByUsusario", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.usuario = :usuario"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByFecAct", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.fecAct = :fecAct"),
    @NamedQuery(name = "CatCertificadoAvaluo.findByEstado", query = "SELECT c FROM CatCertificadoAvaluo c WHERE c.estado = :estado")})
public class CatCertificadoAvaluo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @Column(name = "secuencia", nullable = false)
    private long secuencia;
    @Column(name = "num_cert")
    private BigInteger numCert;
    @Column(name = "cod_comprobante")
    private BigInteger codComprobante;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "ident_predial")
    private BigInteger identPredial;
    @Size(max = 4000)
    @Column(name = "detalle", length = 4000)
    private String detalle;
    @Size(max = 50)
    @Column(name = "codigo_actual", length = 50)
    private String codigoActual;
    @Size(max = 50)
    @Column(name = "codigo_anterior", length = 50)
    private String codigoAnterior;
    @Size(max = 50)
    @Column(name = "alicuota", length = 50)
    private String alicuota;
    @Column(name = "registro_catastral")
    private BigInteger registroCatastral;
    @Column(name = "area_solar", precision = 15, scale = 2)
    private BigDecimal areaSolar;
    @Column(name = "area_construccion", precision = 15, scale = 2)
    private BigDecimal areaConstruccion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "aval_solar", precision = 15, scale = 2)
    private BigDecimal avalSolar;
    @Column(name = "aval_construccion", precision = 15, scale = 2)
    private BigDecimal avalConstruccion;
    @Column(name = "aval_propiedad", precision = 15, scale = 2)
    private BigDecimal avalPropiedad;
    @Column(name = "aval_catastral", precision = 15, scale = 2)
    private BigDecimal avalCatastral;
    @Column(name = "poligono")
    private BigInteger poligono;
    @Size(max = 200)
    @Column(name = "dir_cat", length = 200)
    private String dirCat;
    @Size(max = 50)
    @Column(name = "usuario", length = 50)
    private String usuario;
    @Column(name = "firma_dir", length = 500)
    private String firmaDir;
    @Size(max = 1000)
    @Column(name = "codigo", length = 1000)
    private String codigo;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 25)
    @Column(name = "identificacion", length = 25)
    private String identificacion;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "formato", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FormatoReporte formato;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte solicitante;
    @Column(name = "valor_m2")
    private BigDecimal valorM2;
    @Column(name = "area_desmembrada")
    private BigDecimal areaDesmembrada = BigDecimal.ZERO;
    @Column(name = "avaluo_x_accion")
    private BigDecimal avaluoPorAccion = BigDecimal.ZERO;
    @Column(name = "numero_acciones")
    private Integer numeroAccciones = 0;
    @Column(name = "total_acciones")
    private Integer totalAccciones = 0;
    @Column(name = "porcentaje_aval_division")
    private BigDecimal porcentajeAvalDivision;
    //LOS TIPOS SON: POR ACCIONES  - NORMAL  - DESMEMBRACION, ENTIENDASE POR NORMAL UN CERTIFICADO DE AVALUO 
    //SIN NINGUN TIPO DDE ALTERACION EN EL AVALUO (N - D - P) - (normal - desemembracion - porcentual)
    @Column(name = "tipo")
    private String tipo;

    public BigDecimal getAreaDesmembrada() {
        return areaDesmembrada;
    }

    public void setAreaDesmembrada(BigDecimal areaDesmembrada) {
        this.areaDesmembrada = areaDesmembrada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getAvaluoPorAccion() {
        return avaluoPorAccion;
    }

    public void setAvaluoPorAccion(BigDecimal avaluoPorAccion) {
        this.avaluoPorAccion = avaluoPorAccion;
    }

    public BigDecimal getPorcentajeAvalDivision() {
        return porcentajeAvalDivision;
    }

    public void setPorcentajeAvalDivision(BigDecimal porcentajeAvalDivision) {
        this.porcentajeAvalDivision = porcentajeAvalDivision;
    }

    public Integer getNumeroAccciones() {
        return numeroAccciones;
    }

    public void setNumeroAccciones(Integer numeroAccciones) {
        this.numeroAccciones = numeroAccciones;
    }

    public CatCertificadoAvaluo() {
    }

    public CatCertificadoAvaluo(Long id) {
        this.id = id;
    }

    public CatCertificadoAvaluo(Long id, long secuencia) {
        this.id = id;
        this.secuencia = secuencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(long secuencia) {
        this.secuencia = secuencia;
    }

    public BigInteger getNumCert() {
        return numCert;
    }

    public void setNumCert(BigInteger numCert) {
        this.numCert = numCert;
    }

    public BigInteger getCodComprobante() {
        return codComprobante;
    }

    public void setCodComprobante(BigInteger codComprobante) {
        this.codComprobante = codComprobante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigInteger getIdentPredial() {
        return identPredial;
    }

    public void setIdentPredial(BigInteger identPredial) {
        this.identPredial = identPredial;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getCodigoActual() {
        return codigoActual;
    }

    public void setCodigoActual(String codigoActual) {
        this.codigoActual = codigoActual;
    }

    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    public String getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(String alicuota) {
        this.alicuota = alicuota;
    }

    public BigInteger getRegistroCatastral() {
        return registroCatastral;
    }

    public void setRegistroCatastral(BigInteger registroCatastral) {
        this.registroCatastral = registroCatastral;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAvalSolar() {
        return avalSolar;
    }

    public void setAvalSolar(BigDecimal avalSolar) {
        this.avalSolar = avalSolar;
    }

    public BigDecimal getAvalConstruccion() {
        return avalConstruccion;
    }

    public void setAvalConstruccion(BigDecimal avalConstruccion) {
        this.avalConstruccion = avalConstruccion;
    }

    public BigDecimal getAvalPropiedad() {
        return avalPropiedad;
    }

    public void setAvalPropiedad(BigDecimal avalPropiedad) {
        this.avalPropiedad = avalPropiedad;
    }

    public BigDecimal getAvalCatastral() {
        return avalCatastral;
    }

    public void setAvalCatastral(BigDecimal avalCatastral) {
        this.avalCatastral = avalCatastral;
    }

    public BigInteger getPoligono() {
        return poligono;
    }

    public void setPoligono(BigInteger poligono) {
        this.poligono = poligono;
    }

    public String getDirCat() {
        return dirCat;
    }

    public void setDirCat(String dirCat) {
        this.dirCat = dirCat;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFirmaDir() {
        return firmaDir;
    }

    public void setFirmaDir(String firmaDir) {
        this.firmaDir = firmaDir;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public FormatoReporte getFormato() {
        return formato;
    }

    public void setFormato(FormatoReporte formato) {
        this.formato = formato;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
        if (!(object instanceof CatCertificadoAvaluo)) {
            return false;
        }
        CatCertificadoAvaluo other = (CatCertificadoAvaluo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public Integer getTotalAccciones() {
        return totalAccciones;
    }

    public void setTotalAccciones(Integer totalAccciones) {
        this.totalAccciones = totalAccciones;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatCertificadoAvaluo[ id=" + id + " ]";
    }

}
