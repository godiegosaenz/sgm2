/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatNormasConstruccion;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
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
@Table(name = "cat_solicitud_norma_construccion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findAll", query = "SELECT c FROM CatSolicitudNormaConstruccion c"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByAnioTramite", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.anioTramite = :anioTramite"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByTramite", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.tramite = :tramite"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findById", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.id = :id"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByLinderoNorteEscritura", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.linderoNorteEscritura = :linderoNorteEscritura"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByLinderoSurEscritura", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.linderoSurEscritura = :linderoSurEscritura"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByLinderoEsteEscritura", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.linderoEsteEscritura = :linderoEsteEscritura"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByLinderoOesteEscritura", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.linderoOesteEscritura = :linderoOesteEscritura"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByFechaEmision", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.fechaEmision = :fechaEmision"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByFechaCaducidad", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.fechaCaducidad = :fechaCaducidad"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByUsoSuelo", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.usoSuelo = :usoSuelo"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByTipoEdificacion", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.tipoEdificacion = :tipoEdificacion"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByCos", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.cos = :cos"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByCus", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.cus = :cus"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByNumPlantas", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.numPlantas = :numPlantas"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByAreaTotal", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.areaTotal = :areaTotal"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByNumReporte", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.numReporte = :numReporte"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findBySecuencia", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.secuencia = :secuencia"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByUrbMz", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.urbMz = :urbMz"),
    @NamedQuery(name = "CatSolicitudNormaConstruccion.findByUrbSolar", query = "SELECT c FROM CatSolicitudNormaConstruccion c WHERE c.urbSolar = :urbSolar")})
public class CatSolicitudNormaConstruccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "anio_tramite")
    private Integer anioTramite;
    @Column(name = "tramite")
    private Integer tramite;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "lindero_norte_escritura", precision = 10, scale = 2)
    private BigDecimal linderoNorteEscritura;
    @Column(name = "lindero_sur_escritura", precision = 10, scale = 2)
    private BigDecimal linderoSurEscritura;
    @Column(name = "lindero_este_escritura", precision = 10, scale = 2)
    private BigDecimal linderoEsteEscritura;
    @Column(name = "lindero_oeste_escritura", precision = 10, scale = 2)
    private BigDecimal linderoOesteEscritura;
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCaducidad;
    @Size(max = 100)
    @Column(name = "uso_suelo", length = 100)
    private String usoSuelo;
    @Size(max = 100)
    @Column(name = "tipo_edificacion", length = 100)
    private String tipoEdificacion;
    @Column(name = "cos", precision = 15, scale = 2)
    private BigDecimal cos;
    @Column(name = "cus", precision = 15, scale = 2)
    private BigDecimal cus;
    @Size(max = 50)
    @Column(name = "num_plantas", length = 50)
    private String numPlantas;
    @Column(name = "area_total", precision = 15, scale = 2)
    private BigDecimal areaTotal;
    @Column(name = "num_reporte")
    private BigInteger numReporte;
    @Column(name = "secuencia")
    private BigInteger secuencia;
    @Size(max = 40)
    @Column(name = "urb_mz", length = 40)
    private String urbMz;
    @Size(max = 40)
    @Column(name = "urb_solar", length = 40)
    private String urbSolar;
    @JoinColumn(name = "id_predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio idPredio;
    @JoinColumn(name = "norma_construccion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatNormasConstruccion normaConstruccion;
    @JoinColumn(name = "id_responsable", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte idResponsable;
    @JoinColumn(name = "id_ciudadela", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela idCiudadela;

    public CatSolicitudNormaConstruccion() {
    }

    public CatSolicitudNormaConstruccion(Long id) {
        this.id = id;
    }

    public Integer getAnioTramite() {
        return anioTramite;
    }

    public void setAnioTramite(Integer anioTramite) {
        this.anioTramite = anioTramite;
    }

    public Integer getTramite() {
        return tramite;
    }

    public void setTramite(Integer tramite) {
        this.tramite = tramite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLinderoNorteEscritura() {
        return linderoNorteEscritura;
    }

    public void setLinderoNorteEscritura(BigDecimal linderoNorteEscritura) {
        this.linderoNorteEscritura = linderoNorteEscritura;
    }

    public BigDecimal getLinderoSurEscritura() {
        return linderoSurEscritura;
    }

    public void setLinderoSurEscritura(BigDecimal linderoSurEscritura) {
        this.linderoSurEscritura = linderoSurEscritura;
    }

    public BigDecimal getLinderoEsteEscritura() {
        return linderoEsteEscritura;
    }

    public void setLinderoEsteEscritura(BigDecimal linderoEsteEscritura) {
        this.linderoEsteEscritura = linderoEsteEscritura;
    }

    public BigDecimal getLinderoOesteEscritura() {
        return linderoOesteEscritura;
    }

    public void setLinderoOesteEscritura(BigDecimal linderoOesteEscritura) {
        this.linderoOesteEscritura = linderoOesteEscritura;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getUsoSuelo() {
        return usoSuelo;
    }

    public void setUsoSuelo(String usoSuelo) {
        this.usoSuelo = usoSuelo;
    }

    public String getTipoEdificacion() {
        return tipoEdificacion;
    }

    public void setTipoEdificacion(String tipoEdificacion) {
        this.tipoEdificacion = tipoEdificacion;
    }

    public BigDecimal getCos() {
        return cos;
    }

    public void setCos(BigDecimal cos) {
        this.cos = cos;
    }

    public BigDecimal getCus() {
        return cus;
    }

    public void setCus(BigDecimal cus) {
        this.cus = cus;
    }

    public String getNumPlantas() {
        return numPlantas;
    }

    public void setNumPlantas(String numPlantas) {
        this.numPlantas = numPlantas;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigInteger getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(BigInteger numReporte) {
        this.numReporte = numReporte;
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public String getUrbMz() {
        return urbMz;
    }

    public void setUrbMz(String urbMz) {
        this.urbMz = urbMz;
    }

    public String getUrbSolar() {
        return urbSolar;
    }

    public void setUrbSolar(String urbSolar) {
        this.urbSolar = urbSolar;
    }

    public CatPredio getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(CatPredio idPredio) {
        this.idPredio = idPredio;
    }

    public CatNormasConstruccion getNormaConstruccion() {
        return normaConstruccion;
    }

    public void setNormaConstruccion(CatNormasConstruccion normaConstruccion) {
        this.normaConstruccion = normaConstruccion;
    }

    public CatEnte getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(CatEnte idResponsable) {
        this.idResponsable = idResponsable;
    }

    public CatCiudadela getIdCiudadela() {
        return idCiudadela;
    }

    public void setIdCiudadela(CatCiudadela idCiudadela) {
        this.idCiudadela = idCiudadela;
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
        if (!(object instanceof CatSolicitudNormaConstruccion)) {
            return false;
        }
        CatSolicitudNormaConstruccion other = (CatSolicitudNormaConstruccion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatSolicitudNormaConstruccion[ id=" + id + " ]";
    }
    
}
