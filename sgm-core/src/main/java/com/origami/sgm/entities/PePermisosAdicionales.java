/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.PeDetallePermisosAdicionales;
import com.origami.sgm.entities.PePermiso;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_permisos_adicionales",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PePermisosAdicionales.findAll", query = "SELECT p FROM PePermisosAdicionales p"),
    @NamedQuery(name = "PePermisosAdicionales.findById", query = "SELECT p FROM PePermisosAdicionales p WHERE p.id = :id"),
    @NamedQuery(name = "PePermisosAdicionales.findByNumTramite", query = "SELECT p FROM PePermisosAdicionales p WHERE p.numTramite = :numTramite"),
    @NamedQuery(name = "PePermisosAdicionales.findByFechaEmision", query = "SELECT p FROM PePermisosAdicionales p WHERE p.fechaEmision = :fechaEmision"),
    @NamedQuery(name = "PePermisosAdicionales.findByFechaCaducidad", query = "SELECT p FROM PePermisosAdicionales p WHERE p.fechaCaducidad = :fechaCaducidad"),
    @NamedQuery(name = "PePermisosAdicionales.findByNombPropietarioActual", query = "SELECT p FROM PePermisosAdicionales p WHERE p.nombPropietarioActual = :nombPropietarioActual"),
    @NamedQuery(name = "PePermisosAdicionales.findByCedidRucPropietario", query = "SELECT p FROM PePermisosAdicionales p WHERE p.cedidRucPropietario = :cedidRucPropietario"),
    @NamedQuery(name = "PePermisosAdicionales.findByNombResponsableTec", query = "SELECT p FROM PePermisosAdicionales p WHERE p.nombResponsableTec = :nombResponsableTec"),
    @NamedQuery(name = "PePermisosAdicionales.findByRegProfesional", query = "SELECT p FROM PePermisosAdicionales p WHERE p.regProfesional = :regProfesional"),
    @NamedQuery(name = "PePermisosAdicionales.findByCedulaTecnico", query = "SELECT p FROM PePermisosAdicionales p WHERE p.cedulaTecnico = :cedulaTecnico"),
    @NamedQuery(name = "PePermisosAdicionales.findBySector", query = "SELECT p FROM PePermisosAdicionales p WHERE p.sector = :sector"),
    @NamedQuery(name = "PePermisosAdicionales.findByCalle", query = "SELECT p FROM PePermisosAdicionales p WHERE p.calle = :calle"),
    @NamedQuery(name = "PePermisosAdicionales.findByAreaSolar", query = "SELECT p FROM PePermisosAdicionales p WHERE p.areaSolar = :areaSolar"),
    @NamedQuery(name = "PePermisosAdicionales.findByNumPisosSnb", query = "SELECT p FROM PePermisosAdicionales p WHERE p.numPisosSnb = :numPisosSnb"),
    @NamedQuery(name = "PePermisosAdicionales.findByNumPisosBnb", query = "SELECT p FROM PePermisosAdicionales p WHERE p.numPisosBnb = :numPisosBnb"),
    @NamedQuery(name = "PePermisosAdicionales.findByAlturaConst", query = "SELECT p FROM PePermisosAdicionales p WHERE p.alturaConst = :alturaConst"),
    @NamedQuery(name = "PePermisosAdicionales.findByTotalEdificar", query = "SELECT p FROM PePermisosAdicionales p WHERE p.totalEdificar = :totalEdificar"),
    @NamedQuery(name = "PePermisosAdicionales.findByLineaFabrica", query = "SELECT p FROM PePermisosAdicionales p WHERE p.lineaFabrica = :lineaFabrica"),
    @NamedQuery(name = "PePermisosAdicionales.findByDescripcion", query = "SELECT p FROM PePermisosAdicionales p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "PePermisosAdicionales.findByFechaIngresoTramite", query = "SELECT p FROM PePermisosAdicionales p WHERE p.fechaIngresoTramite = :fechaIngresoTramite"),
    @NamedQuery(name = "PePermisosAdicionales.findByAvaluoConstrTasas", query = "SELECT p FROM PePermisosAdicionales p WHERE p.avaluoConstrTasas = :avaluoConstrTasas"),
    @NamedQuery(name = "PePermisosAdicionales.findByImpMunicipalAreaedif", query = "SELECT p FROM PePermisosAdicionales p WHERE p.impMunicipalAreaedif = :impMunicipalAreaedif"),
    @NamedQuery(name = "PePermisosAdicionales.findByInspeccion", query = "SELECT p FROM PePermisosAdicionales p WHERE p.inspeccion = :inspeccion"),
    @NamedQuery(name = "PePermisosAdicionales.findByRevisionAprobPlanos", query = "SELECT p FROM PePermisosAdicionales p WHERE p.revisionAprobPlanos = :revisionAprobPlanos"),
    @NamedQuery(name = "PePermisosAdicionales.findByNoAdeudarMun", query = "SELECT p FROM PePermisosAdicionales p WHERE p.noAdeudarMun = :noAdeudarMun"),
    @NamedQuery(name = "PePermisosAdicionales.findByLineaFabricaCalculada", query = "SELECT p FROM PePermisosAdicionales p WHERE p.lineaFabricaCalculada = :lineaFabricaCalculada"),
    @NamedQuery(name = "PePermisosAdicionales.findByTotalPagar", query = "SELECT p FROM PePermisosAdicionales p WHERE p.totalPagar = :totalPagar"),
    @NamedQuery(name = "PePermisosAdicionales.findByNumReporte", query = "SELECT p FROM PePermisosAdicionales p WHERE p.numReporte = :numReporte"),
    @NamedQuery(name = "PePermisosAdicionales.findByAnioTramite", query = "SELECT p FROM PePermisosAdicionales p WHERE p.anioTramite = :anioTramite"),
    @NamedQuery(name = "PePermisosAdicionales.findByMostrarCertificado", query = "SELECT p FROM PePermisosAdicionales p WHERE p.mostrarCertificado = :mostrarCertificado")})
public class PePermisosAdicionales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_tramite", nullable = false)
    private long numTramite;
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidad;
    @Size(max = 100)
    @Column(name = "nomb_propietario_actual", length = 100)
    private String nombPropietarioActual;
    @Size(max = 15)
    @Column(name = "cedid_ruc_propietario", length = 15)
    private String cedidRucPropietario;
    @Size(max = 100)
    @Column(name = "nomb_responsable_tec", length = 100)
    private String nombResponsableTec;
    @Size(max = 15)
    @Column(name = "reg_profesional", length = 15)
    private String regProfesional;
    @Size(max = 15)
    @Column(name = "cedula_tecnico", length = 15)
    private String cedulaTecnico;
    @Size(max = 60)
    @Column(name = "sector", length = 60)
    private String sector;
    @Size(max = 60)
    @Column(name = "calle", length = 60)
    private String calle;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_solar", precision = 15, scale = 2)
    private BigDecimal areaSolar;
    @Column(name = "num_pisos_snb")
    private Integer numPisosSnb;
    @Column(name = "num_pisos_bnb")
    private Integer numPisosBnb;
    @Column(name = "altura_const", precision = 15, scale = 2)
    private BigDecimal alturaConst;
    @Column(name = "total_edificar", precision = 15, scale = 2)
    private BigDecimal totalEdificar;
    @Column(name = "linea_fabrica", precision = 8, scale = 2)
    private BigDecimal lineaFabrica;
    @Size(max = 500)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_ingreso_tramite")
    @Temporal(TemporalType.DATE)
    private Date fechaIngresoTramite;
    @Column(name = "avaluo_constr_tasas", precision = 19, scale = 2)
    private BigDecimal avaluoConstrTasas;
    @Column(name = "imp_municipal_areaedif", precision = 19, scale = 2)
    private BigDecimal impMunicipalAreaedif;
    @Column(name = "inspeccion", precision = 15, scale = 2)
    private BigDecimal inspeccion;
    @Column(name = "revision_aprob_planos", precision = 15, scale = 2)
    private BigDecimal revisionAprobPlanos;
    @Column(name = "no_adeudar_mun", precision = 15, scale = 2)
    private BigDecimal noAdeudarMun;
    @Column(name = "linea_fabrica_calculada", precision = 15, scale = 2)
    private BigDecimal lineaFabricaCalculada;
    @Column(name = "total_pagar", precision = 15, scale = 2)
    private BigDecimal totalPagar;
    @Column(name = "num_reporte")
    private BigInteger numReporte;
    @Column(name = "anio_tramite")
    private BigInteger anioTramite;
    @Column(name = "mostrar_certificado")
    private Boolean mostrarCertificado;
    @Column(name = "carta_adosamiento", nullable = false)
    private Boolean cartaAdosamiento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPermisosAdicionales", fetch = FetchType.LAZY)
    @Where(clause="estado")
    private Collection<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesCollection;
    @JoinColumn(name = "tipo_permiso_adicional", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeTipoPermisoAdicionales tipoPermisoAdicional;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuario;
    @JoinColumn(name = "urbanizacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela urbanizacion;
    @JoinColumn(name = "instalaciones", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp instalaciones;
    @JoinColumn(name = "planta_alta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp plantaAlta;
    @JoinColumn(name = "paredes", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp paredes;
    @JoinColumn(name = "planta_baja", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp plantaBaja;
    @JoinColumn(name = "cubierta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp cubierta;
    @JoinColumn(name = "estructura", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp estructura;
    @JoinColumn(name = "resp_tecn", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte respTecn;
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte propietario;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "permiso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PePermiso permiso;
    
    @Size(max = 10)
    @Column(name = "urbmz", length = 10)
    private String urbmz;
    
    @Size(max = 10)
    @Column(name = "urbsolar", length = 10)
    private String urbsolar;

    public PePermisosAdicionales() {
    }

    public PePermisosAdicionales(Long id) {
        this.id = id;
    }

    public PePermisosAdicionales(Long id, long numTramite) {
        this.id = id;
        this.numTramite = numTramite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(long numTramite) {
        this.numTramite = numTramite;
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

    public String getNombPropietarioActual() {
        return nombPropietarioActual;
    }

    public void setNombPropietarioActual(String nombPropietarioActual) {
        this.nombPropietarioActual = nombPropietarioActual;
    }

    public String getCedidRucPropietario() {
        return cedidRucPropietario;
    }

    public void setCedidRucPropietario(String cedidRucPropietario) {
        this.cedidRucPropietario = cedidRucPropietario;
    }

    public String getNombResponsableTec() {
        return nombResponsableTec;
    }

    public void setNombResponsableTec(String nombResponsableTec) {
        this.nombResponsableTec = nombResponsableTec;
    }

    public String getRegProfesional() {
        return regProfesional;
    }

    public void setRegProfesional(String regProfesional) {
        this.regProfesional = regProfesional;
    }

    public String getCedulaTecnico() {
        return cedulaTecnico;
    }

    public void setCedulaTecnico(String cedulaTecnico) {
        this.cedulaTecnico = cedulaTecnico;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public Integer getNumPisosSnb() {
        return numPisosSnb;
    }

    public void setNumPisosSnb(Integer numPisosSnb) {
        this.numPisosSnb = numPisosSnb;
    }

    public Integer getNumPisosBnb() {
        return numPisosBnb;
    }

    public void setNumPisosBnb(Integer numPisosBnb) {
        this.numPisosBnb = numPisosBnb;
    }

    public BigDecimal getAlturaConst() {
        return alturaConst;
    }

    public void setAlturaConst(BigDecimal alturaConst) {
        this.alturaConst = alturaConst;
    }

    public BigDecimal getTotalEdificar() {
        return totalEdificar;
    }

    public void setTotalEdificar(BigDecimal totalEdificar) {
        this.totalEdificar = totalEdificar;
    }

    public BigDecimal getLineaFabrica() {
        return lineaFabrica;
    }

    public void setLineaFabrica(BigDecimal lineaFabrica) {
        this.lineaFabrica = lineaFabrica;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaIngresoTramite() {
        return fechaIngresoTramite;
    }

    public void setFechaIngresoTramite(Date fechaIngresoTramite) {
        this.fechaIngresoTramite = fechaIngresoTramite;
    }

    public BigDecimal getAvaluoConstrTasas() {
        return avaluoConstrTasas;
    }

    public void setAvaluoConstrTasas(BigDecimal avaluoConstrTasas) {
        this.avaluoConstrTasas = avaluoConstrTasas;
    }

    public BigDecimal getImpMunicipalAreaedif() {
        return impMunicipalAreaedif;
    }

    public void setImpMunicipalAreaedif(BigDecimal impMunicipalAreaedif) {
        this.impMunicipalAreaedif = impMunicipalAreaedif;
    }

    public BigDecimal getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(BigDecimal inspeccion) {
        this.inspeccion = inspeccion;
    }

    public BigDecimal getRevisionAprobPlanos() {
        return revisionAprobPlanos;
    }

    public void setRevisionAprobPlanos(BigDecimal revisionAprobPlanos) {
        this.revisionAprobPlanos = revisionAprobPlanos;
    }

    public BigDecimal getNoAdeudarMun() {
        return noAdeudarMun;
    }

    public void setNoAdeudarMun(BigDecimal noAdeudarMun) {
        this.noAdeudarMun = noAdeudarMun;
    }

    public BigDecimal getLineaFabricaCalculada() {
        return lineaFabricaCalculada;
    }

    public void setLineaFabricaCalculada(BigDecimal lineaFabricaCalculada) {
        this.lineaFabricaCalculada = lineaFabricaCalculada;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public BigInteger getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(BigInteger numReporte) {
        this.numReporte = numReporte;
    }

    public BigInteger getAnioTramite() {
        return anioTramite;
    }

    public void setAnioTramite(BigInteger anioTramite) {
        this.anioTramite = anioTramite;
    }

    public Boolean getMostrarCertificado() {
        return mostrarCertificado;
    }

    public void setMostrarCertificado(Boolean mostrarCertificado) {
        this.mostrarCertificado = mostrarCertificado;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeDetallePermisosAdicionales> getPeDetallePermisosAdicionalesCollection() {
        return peDetallePermisosAdicionalesCollection;
    }

    public void setPeDetallePermisosAdicionalesCollection(Collection<PeDetallePermisosAdicionales> peDetallePermisosAdicionalesCollection) {
        this.peDetallePermisosAdicionalesCollection = peDetallePermisosAdicionalesCollection;
    }

    public PeTipoPermisoAdicionales getTipoPermisoAdicional() {
        return tipoPermisoAdicional;
    }

    public void setTipoPermisoAdicional(PeTipoPermisoAdicionales tipoPermisoAdicional) {
        this.tipoPermisoAdicional = tipoPermisoAdicional;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public CatCiudadela getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(CatCiudadela urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public CatEdfProp getInstalaciones() {
        return instalaciones;
    }

    public void setInstalaciones(CatEdfProp instalaciones) {
        this.instalaciones = instalaciones;
    }

    public CatEdfProp getPlantaAlta() {
        return plantaAlta;
    }

    public void setPlantaAlta(CatEdfProp plantaAlta) {
        this.plantaAlta = plantaAlta;
    }

    public CatEdfProp getParedes() {
        return paredes;
    }

    public void setParedes(CatEdfProp paredes) {
        this.paredes = paredes;
    }

    public CatEdfProp getPlantaBaja() {
        return plantaBaja;
    }

    public void setPlantaBaja(CatEdfProp plantaBaja) {
        this.plantaBaja = plantaBaja;
    }

    public CatEdfProp getCubierta() {
        return cubierta;
    }

    public void setCubierta(CatEdfProp cubierta) {
        this.cubierta = cubierta;
    }

    public CatEdfProp getEstructura() {
        return estructura;
    }

    public void setEstructura(CatEdfProp estructura) {
        this.estructura = estructura;
    }

    public CatEnte getRespTecn() {
        return respTecn;
    }

    public void setRespTecn(CatEnte respTecn) {
        this.respTecn = respTecn;
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public PePermiso getPermiso() {
        return permiso;
    }

    public void setPermiso(PePermiso permiso) {
        this.permiso = permiso;
    }

    public Boolean getCartaAdosamiento() {
        return cartaAdosamiento;
    }

    public void setCartaAdosamiento(Boolean cartaAdosamiento) {
        this.cartaAdosamiento = cartaAdosamiento;
    }

    public String getUrbmz() {
        return urbmz;
    }

    public void setUrbmz(String urbmz) {
        this.urbmz = urbmz;
    }

    public String getUrbsolar() {
        return urbsolar;
    }

    public void setUrbsolar(String urbsolar) {
        this.urbsolar = urbsolar;
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
        if (!(object instanceof PePermisosAdicionales)) {
            return false;
        }
        PePermisosAdicionales other = (PePermisosAdicionales) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PePermisosAdicionales[ id=" + id + " ]";
    }
    
}
