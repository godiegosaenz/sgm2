/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.PeFirma;
import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "proceso_reporte",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProcesoReporte.findAll", query = "SELECT p FROM ProcesoReporte p"),
    @NamedQuery(name = "ProcesoReporte.findById", query = "SELECT p FROM ProcesoReporte p WHERE p.id = :id"),
    @NamedQuery(name = "ProcesoReporte.findByNumLiqTramTasas", query = "SELECT p FROM ProcesoReporte p WHERE p.numLiqTramTasas = :numLiqTramTasas"),
    @NamedQuery(name = "ProcesoReporte.findByNumTramite", query = "SELECT p FROM ProcesoReporte p WHERE p.numTramite = :numTramite"),
    @NamedQuery(name = "ProcesoReporte.findByNumPisoSobreNivelBord", query = "SELECT p FROM ProcesoReporte p WHERE p.numPisoSobreNivelBord = :numPisoSobreNivelBord"),
    @NamedQuery(name = "ProcesoReporte.findByNumPisoBajoNivelBord", query = "SELECT p FROM ProcesoReporte p WHERE p.numPisoBajoNivelBord = :numPisoBajoNivelBord"),
    @NamedQuery(name = "ProcesoReporte.findByAreaConstruccion", query = "SELECT p FROM ProcesoReporte p WHERE p.areaConstruccion = :areaConstruccion"),
    @NamedQuery(name = "ProcesoReporte.findByAreaSolar", query = "SELECT p FROM ProcesoReporte p WHERE p.areaSolar = :areaSolar"),
    @NamedQuery(name = "ProcesoReporte.findByAvaluoConstruccion", query = "SELECT p FROM ProcesoReporte p WHERE p.avaluoConstruccion = :avaluoConstruccion"),
    @NamedQuery(name = "ProcesoReporte.findByAvaluoSolar", query = "SELECT p FROM ProcesoReporte p WHERE p.avaluoSolar = :avaluoSolar"),
    @NamedQuery(name = "ProcesoReporte.findByAvaluoPropiedad", query = "SELECT p FROM ProcesoReporte p WHERE p.avaluoPropiedad = :avaluoPropiedad"),
    @NamedQuery(name = "ProcesoReporte.findByBaseCalcu1", query = "SELECT p FROM ProcesoReporte p WHERE p.baseCalcu1 = :baseCalcu1"),
    @NamedQuery(name = "ProcesoReporte.findByBaseCalcu2", query = "SELECT p FROM ProcesoReporte p WHERE p.baseCalcu2 = :baseCalcu2"),
    @NamedQuery(name = "ProcesoReporte.findByFechaImpresion", query = "SELECT p FROM ProcesoReporte p WHERE p.fechaImpresion = :fechaImpresion"),
    @NamedQuery(name = "ProcesoReporte.findByTotalPagar", query = "SELECT p FROM ProcesoReporte p WHERE p.totalPagar = :totalPagar"),
    @NamedQuery(name = "ProcesoReporte.findByAreaEdificacion", query = "SELECT p FROM ProcesoReporte p WHERE p.areaEdificacion = :areaEdificacion"),
    @NamedQuery(name = "ProcesoReporte.findByDescripcionEdificacion", query = "SELECT p FROM ProcesoReporte p WHERE p.descripcionEdificacion = :descripcionEdificacion"),
    @NamedQuery(name = "ProcesoReporte.findByResponsableRealizadoPor", query = "SELECT p FROM ProcesoReporte p WHERE p.responsableRealizadoPor = :responsableRealizadoPor"),
    @NamedQuery(name = "ProcesoReporte.findByCalle", query = "SELECT p FROM ProcesoReporte p WHERE p.calle = :calle"),
    @NamedQuery(name = "ProcesoReporte.findByNumPredio", query = "SELECT p FROM ProcesoReporte p WHERE p.numPredio = :numPredio"),
    @NamedQuery(name = "ProcesoReporte.findByAnioTramite", query = "SELECT p FROM ProcesoReporte p WHERE p.anioTramite = :anioTramite"),
    @NamedQuery(name = "ProcesoReporte.findByProcessid", query = "SELECT p FROM ProcesoReporte p WHERE p.processid = :processid"),
    @NamedQuery(name = "ProcesoReporte.findByNumReporte", query = "SELECT p FROM ProcesoReporte p WHERE p.numReporte = :numReporte"),
    @NamedQuery(name = "ProcesoReporte.findByNumPlanosIngresados", query = "SELECT p FROM ProcesoReporte p WHERE p.numPlanosIngresados = :numPlanosIngresados"),
    @NamedQuery(name = "ProcesoReporte.findByMetrosLinealesLineaf", query = "SELECT p FROM ProcesoReporte p WHERE p.metrosLinealesLineaf = :metrosLinealesLineaf"),
    @NamedQuery(name = "ProcesoReporte.findByPresupuestoObra", query = "SELECT p FROM ProcesoReporte p WHERE p.presupuestoObra = :presupuestoObra"),
    @NamedQuery(name = "ProcesoReporte.findByFiscalizacion", query = "SELECT p FROM ProcesoReporte p WHERE p.fiscalizacion = :fiscalizacion"),
    @NamedQuery(name = "ProcesoReporte.findByValorUno", query = "SELECT p FROM ProcesoReporte p WHERE p.valorUno = :valorUno"),
    @NamedQuery(name = "ProcesoReporte.findByPorEstudioProyecto", query = "SELECT p FROM ProcesoReporte p WHERE p.porEstudioProyecto = :porEstudioProyecto"),
    @NamedQuery(name = "ProcesoReporte.findByAreaExcedente", query = "SELECT p FROM ProcesoReporte p WHERE p.areaExcedente = :areaExcedente")})
public class ProcesoReporte implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 20)
    @Column(name = "num_liq_tram_tasas", length = 20)
    private String numLiqTramTasas;
    @Size(max = 50)
    @Column(name = "num_tramite", length = 50)
    private String numTramite;
    @Column(name = "num_piso_sobre_nivel_bord")
    private BigInteger numPisoSobreNivelBord;
    @Column(name = "num_piso_bajo_nivel_bord")
    private BigInteger numPisoBajoNivelBord;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_construccion", precision = 12, scale = 4)
    private BigDecimal areaConstruccion;
    @Column(name = "area_solar", precision = 12, scale = 4)
    private BigDecimal areaSolar;
    @Column(name = "avaluo_construccion", precision = 12, scale = 4)
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_solar", precision = 12, scale = 4)
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_propiedad", precision = 12, scale = 4)
    private BigDecimal avaluoPropiedad;
    @Column(name = "base_calcu1", precision = 8, scale = 3)
    private BigDecimal baseCalcu1;
    @Column(name = "base_calcu2", precision = 8, scale = 2)
    private BigDecimal baseCalcu2;
    @Column(name = "fecha_impresion")
    @Temporal(TemporalType.DATE)
    private Date fechaImpresion;
    @Column(name = "total_pagar", precision = 14, scale = 2)
    private BigDecimal totalPagar;
    @Column(name = "area_edificacion", precision = 12, scale = 4)
    private BigDecimal areaEdificacion;
    @Size(max = 2147483647)
    @Column(name = "descripcion_edificacion", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String descripcionEdificacion;
    @Size(max = 100)
    @Column(name = "responsable_realizado_por", length = 100)
    private String responsableRealizadoPor;
    @Size(max = 50)
    @Column(name = "calle", length = 50)
    private String calle;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    @Column(name = "anio_tramite")
    private BigInteger anioTramite;
    @Size(max = 50)
    @Column(name = "processid", length = 50)
    private String processid;
    @Column(name = "num_reporte")
    private BigInteger numReporte;
    @Column(name = "num_planos_ingresados")
    private BigInteger numPlanosIngresados;
    @Column(name = "metros_lineales_lineaf", precision = 14, scale = 2)
    private BigDecimal metrosLinealesLineaf;
    @Column(name = "presupuesto_obra", precision = 14, scale = 2)
    private BigDecimal presupuestoObra;
    @Column(name = "fiscalizacion", precision = 12, scale = 2)
    private BigDecimal fiscalizacion;
    @Column(name = "valor_uno", precision = 12, scale = 2)
    private BigDecimal valorUno;
    @Column(name = "por_estudio_proyecto", precision = 12, scale = 2)
    private BigDecimal porEstudioProyecto;
    @Column(name = "area_excedente", precision = 12, scale = 2)
    private BigDecimal areaExcedente;
    @JoinColumn(name = "firma", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PeFirma firma;
    @JoinColumn(name = "tipo_tramite", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private GeTipoTramite tipoTramite;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "nom_solicitante", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatEnte nomSolicitante;
    @JoinColumn(name = "usuario_creador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioCreador;

    public ProcesoReporte() {
    }

    public ProcesoReporte(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumLiqTramTasas() {
        return numLiqTramTasas;
    }

    public void setNumLiqTramTasas(String numLiqTramTasas) {
        this.numLiqTramTasas = numLiqTramTasas;
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public BigInteger getNumPisoSobreNivelBord() {
        return numPisoSobreNivelBord;
    }

    public void setNumPisoSobreNivelBord(BigInteger numPisoSobreNivelBord) {
        this.numPisoSobreNivelBord = numPisoSobreNivelBord;
    }

    public BigInteger getNumPisoBajoNivelBord() {
        return numPisoBajoNivelBord;
    }

    public void setNumPisoBajoNivelBord(BigInteger numPisoBajoNivelBord) {
        this.numPisoBajoNivelBord = numPisoBajoNivelBord;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
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

    public BigDecimal getAvaluoPropiedad() {
        return avaluoPropiedad;
    }

    public void setAvaluoPropiedad(BigDecimal avaluoPropiedad) {
        this.avaluoPropiedad = avaluoPropiedad;
    }

    public BigDecimal getBaseCalcu1() {
        return baseCalcu1;
    }

    public void setBaseCalcu1(BigDecimal baseCalcu1) {
        this.baseCalcu1 = baseCalcu1;
    }

    public BigDecimal getBaseCalcu2() {
        return baseCalcu2;
    }

    public void setBaseCalcu2(BigDecimal baseCalcu2) {
        this.baseCalcu2 = baseCalcu2;
    }

    public Date getFechaImpresion() {
        return fechaImpresion;
    }

    public void setFechaImpresion(Date fechaImpresion) {
        this.fechaImpresion = fechaImpresion;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public BigDecimal getAreaEdificacion() {
        return areaEdificacion;
    }

    public void setAreaEdificacion(BigDecimal areaEdificacion) {
        this.areaEdificacion = areaEdificacion;
    }

    public String getDescripcionEdificacion() {
        return descripcionEdificacion;
    }

    public void setDescripcionEdificacion(String descripcionEdificacion) {
        this.descripcionEdificacion = descripcionEdificacion;
    }

    public String getResponsableRealizadoPor() {
        return responsableRealizadoPor;
    }

    public void setResponsableRealizadoPor(String responsableRealizadoPor) {
        this.responsableRealizadoPor = responsableRealizadoPor;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public BigInteger getAnioTramite() {
        return anioTramite;
    }

    public void setAnioTramite(BigInteger anioTramite) {
        this.anioTramite = anioTramite;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public BigInteger getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(BigInteger numReporte) {
        this.numReporte = numReporte;
    }

    public BigInteger getNumPlanosIngresados() {
        return numPlanosIngresados;
    }

    public void setNumPlanosIngresados(BigInteger numPlanosIngresados) {
        this.numPlanosIngresados = numPlanosIngresados;
    }

    public BigDecimal getMetrosLinealesLineaf() {
        return metrosLinealesLineaf;
    }

    public void setMetrosLinealesLineaf(BigDecimal metrosLinealesLineaf) {
        this.metrosLinealesLineaf = metrosLinealesLineaf;
    }

    public BigDecimal getPresupuestoObra() {
        return presupuestoObra;
    }

    public void setPresupuestoObra(BigDecimal presupuestoObra) {
        this.presupuestoObra = presupuestoObra;
    }

    public BigDecimal getFiscalizacion() {
        return fiscalizacion;
    }

    public void setFiscalizacion(BigDecimal fiscalizacion) {
        this.fiscalizacion = fiscalizacion;
    }

    public BigDecimal getValorUno() {
        return valorUno;
    }

    public void setValorUno(BigDecimal valorUno) {
        this.valorUno = valorUno;
    }

    public BigDecimal getPorEstudioProyecto() {
        return porEstudioProyecto;
    }

    public void setPorEstudioProyecto(BigDecimal porEstudioProyecto) {
        this.porEstudioProyecto = porEstudioProyecto;
    }

    public BigDecimal getAreaExcedente() {
        return areaExcedente;
    }

    public void setAreaExcedente(BigDecimal areaExcedente) {
        this.areaExcedente = areaExcedente;
    }

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatEnte getNomSolicitante() {
        return nomSolicitante;
    }

    public void setNomSolicitante(CatEnte nomSolicitante) {
        this.nomSolicitante = nomSolicitante;
    }

    public AclUser getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(AclUser usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
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
        if (!(object instanceof ProcesoReporte)) {
            return false;
        }
        ProcesoReporte other = (ProcesoReporte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.ProcesoReporte[ id=" + id + " ]";
    }
    
}
