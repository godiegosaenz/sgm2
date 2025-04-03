/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "pe_estructura_especial", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeEstructuraEspecial.findAll", query = "SELECT p FROM PeEstructuraEspecial p"),
    @NamedQuery(name = "PeEstructuraEspecial.findById", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.id = :id"),
    @NamedQuery(name = "PeEstructuraEspecial.findByAnioTramite", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.anioTramite = :anioTramite"),
    @NamedQuery(name = "PeEstructuraEspecial.findByNumeroTramite", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.numeroTramite = :numeroTramite"),
    @NamedQuery(name = "PeEstructuraEspecial.findByFechaEmision", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.fechaEmision = :fechaEmision"),
    @NamedQuery(name = "PeEstructuraEspecial.findByFechaCaducidad", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.fechaCaducidad = :fechaCaducidad"),
    @NamedQuery(name = "PeEstructuraEspecial.findByPropietario", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.propietario = :propietario"),
    @NamedQuery(name = "PeEstructuraEspecial.findByCedulaRucPropietario", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.cedulaRucPropietario = :cedulaRucPropietario"),
    @NamedQuery(name = "PeEstructuraEspecial.findByResponsableTecnico", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.responsableTecnico = :responsableTecnico"),
    @NamedQuery(name = "PeEstructuraEspecial.findByRegistroProfesional", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.registroProfesional = :registroProfesional"),
    @NamedQuery(name = "PeEstructuraEspecial.findByCedulaRucTecnico", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.cedulaRucTecnico = :cedulaRucTecnico"),
    @NamedQuery(name = "PeEstructuraEspecial.findBySector", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.sector = :sector"),
    @NamedQuery(name = "PeEstructuraEspecial.findByUrbanizacion", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.urbanizacion = :urbanizacion"),
    @NamedQuery(name = "PeEstructuraEspecial.findByCalle", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.calle = :calle"),
    @NamedQuery(name = "PeEstructuraEspecial.findByDescripcion", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "PeEstructuraEspecial.findByObservacion", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.observacion = :observacion"),
    @NamedQuery(name = "PeEstructuraEspecial.findByPisosSnb", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.pisosSnb = :pisosSnb"),
    @NamedQuery(name = "PeEstructuraEspecial.findByPisosBnb", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.pisosBnb = :pisosBnb"),
    @NamedQuery(name = "PeEstructuraEspecial.findByAlturaConstruccion", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.alturaConstruccion = :alturaConstruccion"),
    @NamedQuery(name = "PeEstructuraEspecial.findByAreaSolar", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.areaSolar = :areaSolar"),
    @NamedQuery(name = "PeEstructuraEspecial.findByLote", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.lote = :lote"),
    @NamedQuery(name = "PeEstructuraEspecial.findByFechaIngresoTramite", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.fechaIngresoTramite = :fechaIngresoTramite"),
    @NamedQuery(name = "PeEstructuraEspecial.findByImpuesto", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.impuesto = :impuesto"),
    @NamedQuery(name = "PeEstructuraEspecial.findByInspeccion", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.inspeccion = :inspeccion"),
    @NamedQuery(name = "PeEstructuraEspecial.findByRevision", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.revision = :revision"),
    @NamedQuery(name = "PeEstructuraEspecial.findByNoAdeudar", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.noAdeudar = :noAdeudar"),
    @NamedQuery(name = "PeEstructuraEspecial.findByLineaFabrica", query = "SELECT p FROM PeEstructuraEspecial p WHERE p.lineaFabrica = :lineaFabrica")})
public class PeEstructuraEspecial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "anio_tramite")
    private Integer anioTramite;
    @Column(name = "numero_tramite")
    private Integer numeroTramite;
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidad;
    @Size(max = 100)
    @Column(name = "propietario", length = 100)
    private String propietario;
    @Size(max = 20)
    @Column(name = "cedula_ruc_propietario", length = 20)
    private String cedulaRucPropietario;
    @Size(max = 100)
    @Column(name = "responsable_tecnico", length = 100)
    private String responsableTecnico;
    @Size(max = 50)
    @Column(name = "registro_profesional", length = 50)
    private String registroProfesional;
    @Size(max = 20)
    @Column(name = "cedula_ruc_tecnico", length = 20)
    private String cedulaRucTecnico;
    @Size(max = 50)
    @Column(name = "sector", length = 50)
    private String sector;
    @Size(max = 100)
    @Column(name = "urbanizacion", length = 100)
    private String urbanizacion;
    @Size(max = 50)
    @Column(name = "calle", length = 50)
    private String calle;
    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    @Size(max = 100)
    @Column(name = "observacion", length = 100)
    private String observacion;
    @Column(name = "pisos_snb")
    private Integer pisosSnb;
    @Column(name = "pisos_bnb")
    private Integer pisosBnb;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "altura_construccion", precision = 18, scale = 4)
    private BigDecimal alturaConstruccion;
    @Column(name = "area_solar", precision = 15, scale = 2)
    private BigDecimal areaSolar;
    @Size(max = 50)
    @Column(name = "lote", length = 50)
    private String lote;
    @Column(name = "fecha_ingreso_tramite")
    @Temporal(TemporalType.DATE)
    private Date fechaIngresoTramite;
    @Column(name = "impuesto", precision = 15, scale = 2)
    private BigDecimal impuesto;
    @Column(name = "inspeccion", precision = 15, scale = 2)
    private BigDecimal inspeccion;
    @Column(name = "revision", precision = 15, scale = 2)
    private BigDecimal revision;
    @Column(name = "no_adeudar", precision = 15, scale = 2)
    private BigDecimal noAdeudar;
    @Column(name = "linea_fabrica", precision = 15, scale = 2)
    private BigDecimal lineaFabrica;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idEstructuraEspecial", fetch = FetchType.LAZY)
    private Collection<PeDetalleEstructuraEspecial> peDetalleEstructuraEspecialCollection;
    @JoinColumn(name = "id_permiso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PePermiso idPermiso;
    @JoinColumn(name = "id_predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio idPredio;
    @JoinColumn(name = "propietario_persona", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte propietarioPersona;
    @JoinColumn(name = "responsable_persona", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte responsablePersona;
    @JoinColumn(name = "prop_cubierta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propCubierta;
    @JoinColumn(name = "prop_plantaalta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propPlantaalta;
    @JoinColumn(name = "prop_paredes", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propParedes;
    @JoinColumn(name = "prop_instalaciones", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propInstalaciones;
    @JoinColumn(name = "prop_estructura", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propEstructura;
    @JoinColumn(name = "prop_plantabaja", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propPlantabaja;

    public PeEstructuraEspecial() {
    }

    public PeEstructuraEspecial(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnioTramite() {
        return anioTramite;
    }

    public void setAnioTramite(Integer anioTramite) {
        this.anioTramite = anioTramite;
    }

    public Integer getNumeroTramite() {
        return numeroTramite;
    }

    public void setNumeroTramite(Integer numeroTramite) {
        this.numeroTramite = numeroTramite;
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

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getCedulaRucPropietario() {
        return cedulaRucPropietario;
    }

    public void setCedulaRucPropietario(String cedulaRucPropietario) {
        this.cedulaRucPropietario = cedulaRucPropietario;
    }

    public String getResponsableTecnico() {
        return responsableTecnico;
    }

    public void setResponsableTecnico(String responsableTecnico) {
        this.responsableTecnico = responsableTecnico;
    }

    public String getRegistroProfesional() {
        return registroProfesional;
    }

    public void setRegistroProfesional(String registroProfesional) {
        this.registroProfesional = registroProfesional;
    }

    public String getCedulaRucTecnico() {
        return cedulaRucTecnico;
    }

    public void setCedulaRucTecnico(String cedulaRucTecnico) {
        this.cedulaRucTecnico = cedulaRucTecnico;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Integer getPisosSnb() {
        return pisosSnb;
    }

    public void setPisosSnb(Integer pisosSnb) {
        this.pisosSnb = pisosSnb;
    }

    public Integer getPisosBnb() {
        return pisosBnb;
    }

    public void setPisosBnb(Integer pisosBnb) {
        this.pisosBnb = pisosBnb;
    }

    public BigDecimal getAlturaConstruccion() {
        return alturaConstruccion;
    }

    public void setAlturaConstruccion(BigDecimal alturaConstruccion) {
        this.alturaConstruccion = alturaConstruccion;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Date getFechaIngresoTramite() {
        return fechaIngresoTramite;
    }

    public void setFechaIngresoTramite(Date fechaIngresoTramite) {
        this.fechaIngresoTramite = fechaIngresoTramite;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(BigDecimal inspeccion) {
        this.inspeccion = inspeccion;
    }

    public BigDecimal getRevision() {
        return revision;
    }

    public void setRevision(BigDecimal revision) {
        this.revision = revision;
    }

    public BigDecimal getNoAdeudar() {
        return noAdeudar;
    }

    public void setNoAdeudar(BigDecimal noAdeudar) {
        this.noAdeudar = noAdeudar;
    }

    public BigDecimal getLineaFabrica() {
        return lineaFabrica;
    }

    public void setLineaFabrica(BigDecimal lineaFabrica) {
        this.lineaFabrica = lineaFabrica;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeDetalleEstructuraEspecial> getPeDetalleEstructuraEspecialCollection() {
        return peDetalleEstructuraEspecialCollection;
    }

    public void setPeDetalleEstructuraEspecialCollection(Collection<PeDetalleEstructuraEspecial> peDetalleEstructuraEspecialCollection) {
        this.peDetalleEstructuraEspecialCollection = peDetalleEstructuraEspecialCollection;
    }

    public PePermiso getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(PePermiso idPermiso) {
        this.idPermiso = idPermiso;
    }

    public CatPredio getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(CatPredio idPredio) {
        this.idPredio = idPredio;
    }

    public CatEnte getPropietarioPersona() {
        return propietarioPersona;
    }

    public void setPropietarioPersona(CatEnte propietarioPersona) {
        this.propietarioPersona = propietarioPersona;
    }

    public CatEnte getResponsablePersona() {
        return responsablePersona;
    }

    public void setResponsablePersona(CatEnte responsablePersona) {
        this.responsablePersona = responsablePersona;
    }

    public CatEdfProp getPropCubierta() {
        return propCubierta;
    }

    public void setPropCubierta(CatEdfProp propCubierta) {
        this.propCubierta = propCubierta;
    }

    public CatEdfProp getPropPlantaalta() {
        return propPlantaalta;
    }

    public void setPropPlantaalta(CatEdfProp propPlantaalta) {
        this.propPlantaalta = propPlantaalta;
    }

    public CatEdfProp getPropParedes() {
        return propParedes;
    }

    public void setPropParedes(CatEdfProp propParedes) {
        this.propParedes = propParedes;
    }

    public CatEdfProp getPropInstalaciones() {
        return propInstalaciones;
    }

    public void setPropInstalaciones(CatEdfProp propInstalaciones) {
        this.propInstalaciones = propInstalaciones;
    }

    public CatEdfProp getPropEstructura() {
        return propEstructura;
    }

    public void setPropEstructura(CatEdfProp propEstructura) {
        this.propEstructura = propEstructura;
    }

    public CatEdfProp getPropPlantabaja() {
        return propPlantabaja;
    }

    public void setPropPlantabaja(CatEdfProp propPlantabaja) {
        this.propPlantabaja = propPlantabaja;
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
        if (!(object instanceof PeEstructuraEspecial)) {
            return false;
        }
        PeEstructuraEspecial other = (PeEstructuraEspecial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeEstructuraEspecial[ id=" + id + " ]";
    }
    
}
