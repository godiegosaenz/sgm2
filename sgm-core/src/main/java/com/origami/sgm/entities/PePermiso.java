/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
//import org.hibernate.annotations.FetchMode;
//import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.Where;

/**
 * Guarda el permiso de contrucción generado por el departamento de
 * edificaciones
 *
 * @author CarlosLoorVargas
 */
@Entity
//@FetchProfile(name = "tramite", fetchOverrides = {
//		   @FetchProfile.FetchOverride(entity = HistoricoTramites.class, association = "tramite", mode = FetchMode.JOIN)
//})
@Table(name = "pe_permiso", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"anio_tramite", "numero_tramite"})})
@NamedQueries({
    @NamedQuery(name = "PePermiso.findAll", query = "SELECT p FROM PePermiso p")})
public class PePermiso implements Serializable {
    

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "propietario", length = 100) 
    private String propietario; // campo no usado character varying
    @Size(max = 100)
    @Column(name = "responsable", length = 100)
    private String responsable;  // campo no usado character varying
    @Size(max = 20)
    @Column(name = "cedula_propietario", length = 20)
    private String cedulaPropietario;  // campo no usado character varying
    @Size(max = 20)
    @Column(name = "cedula_responsable_tecnico", length = 20)
    private String cedulaResponsableTecnico; // character varying
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_construccion", precision = 15, scale = 2)
    private BigDecimal areaConstruccion; // numeric(15,2)
    @Column(name = "area_implantacion", precision = 15, scale = 2)
    private BigDecimal areaImplantacion; // numeric(15,2)
    @Column(name = "area_solar", precision = 15, scale = 2)
    private BigDecimal areaSolar; // numeric(15,2)
    @Column(name = "linea_fabrica", precision = 15, scale = 2)
    private BigDecimal lineaFabrica; //numeric(15,2)
    @Column(name = "retiro_frontal", precision = 15, scale = 2)
    private BigDecimal retiroFrontal; // numeric(15,2)
    @Column(name = "retiro_posterior", precision = 15, scale = 2)
    private BigDecimal retiroPosterior; // numeric(15,2)
    @Column(name = "retiro_lateral1", precision = 15, scale = 2)
    private BigDecimal retiroLateral1; // numeric(15,2)
    @Column(name = "retiro_lateral2", precision = 15, scale = 2)
    private BigDecimal retiroLateral2; // numeric(15,2)
    @Column(name = "pisos_snb")
    private Integer pisosSnb; // Integer
    @Column(name = "pisos_bnb")
    private Integer pisosBnb; // Integer
    @Column(name = "avaluo_liquidacion", precision = 19, scale = 2)
    private BigDecimal avaluoLiquidacion; // numeric(15,2)
    @Size(max = 50)
    @Column(name = "tecnico_registro_profesional", length = 50)
    private String tecnicoRegistroProfesional; // Character Varying
    @Column(name = "impuesto", precision = 15, scale = 2)
    private BigDecimal impuesto; // numeric(15,2)
    @Column(name = "inspeccion", precision = 15, scale = 2)
    private BigDecimal inspeccion; // numeric(15,2)
    @Column(name = "revision", precision = 15, scale = 2)
    private BigDecimal revision; // numeric(15,2)
    @Column(name = "no_adeudar", precision = 15, scale = 2)
    private BigDecimal noAdeudar; // numeric(15,2)
    @Column(name = "linea_f", precision = 15, scale = 2)
    private BigDecimal lineaF; // numeric(15,2)
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "estado", length = 5)
    private String estado; // character varying
    @Size(max = 250)
    @Column(name = "observacion", length = 250)
    private String observacion; // character varying
    @Column(name = "anio_tramite")
    private Short anioTramite; //smallint
    @JoinColumn(name = "numero_tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite; // bigint
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidad;
    @Column(name = "anio_permiso")
    private Short anioPermiso; // smaillint
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIME)
    private Date fechaCreacion;
    @Size(max = 50)
    @Column(name = "calle", length = 50)
    private String calle; //character varying
    @Column(name = "altura")
    private BigDecimal altura; // numeric
    @Column(name = "area_edificaciones", precision = 15, scale = 2)
    private BigDecimal areaEdificaciones; // numeric(15,2)
    @Column(name = "area_parqueos", precision = 15, scale = 2)
    private BigDecimal areaParqueos; // numeric(15,2)
    @Column(name = "cantidad_edificaciones")
    private Integer cantidadEdificaciones; // integer
    @Column(name = "cantidad_parqueos")
    private Integer cantidadParqueos; // integer
    @Size(max = 5)
    @Column(name = "caracerrf", length = 5)
    private String caracerrf; // Character Varying
    @Size(max = 5)
    @Column(name = "caracerrl1", length = 5)
    private String caracerrl1; // Character Varying
    @Size(max = 5)
    @Column(name = "caracerrl2", length = 5)
    private String caracerrl2; // Character Varying
    @Size(max = 5)
    @Column(name = "caracerrp", length = 5)
    private String caracerrp; // Character Varying
    @Size(max = 5)
    @Column(name = "caradosrf", length = 5)
    private String caradosrf; // Character Varying
    @Size(max = 5)
    @Column(name = "caradosrl1", length = 5)
    private String caradosrl1; // Character Varying
    @Size(max = 5)
    @Column(name = "caradosrp", length = 5)
    private String caradosrp; // Character Varying
    @Size(max = 80)
    @Column(name = "desc_familiar", length = 80)
    private String descFamiliar; // Character Varying
    @Size(max = 5)
    @Column(name = "caradosrl2", length = 5)
    private String caradosrl2; // Character Varying
    @Column(name = "codigo_permiso_anterior")
    private BigInteger codigoPermisoAnterior; // bigint
    @Column(name = "idprocess")
    private BigInteger idprocess; // bigint
    @Size(max = 5)
    @Column(name = "registro_vista", length = 5)
    private String registroVista;
    @Column(name = "num_tram_ant")
    private BigInteger numTramAnt;
    @Column(name = "num_reporte")
    private BigInteger numReporte;
    @Size(max = 50)
    @Column(name = "urbmz", length = 50)
    private String urbmz;
    @Size(max = 50)
    @Column(name = "urbsolar", length = 50)
    private String urbsolar;
    @Size(max = 100)
    @Column(name = "nomb_urbanizacion_impresa", length = 100)
    private String nombUrbanizacionImpresa;
    @Column(name = "mostrar_certificado")
    private Boolean mostrarCertificado;
    @Basic(optional = false)
    @Column(name = "carta_adosamiento", nullable = false)
    private boolean cartaAdosamiento;
    @Column(name = "es_macro_lote")
    private boolean esMacroLote;
    @Size(max = 50)
    @Column(name = "usuario_modificacion", length = 50)
    private String usuarioModificacion;
    @Column(name = "fecha_modificacion")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaModificacion;
    @JoinColumn(name = "tipo_permiso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PeTipoPermiso tipoPermiso;
    @JoinColumn(name = "id_predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio idPredio;
    @JoinColumn(name = "responsable_persona", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte responsablePersona;
    @JoinColumn(name = "propietario_persona", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte propietarioPersona;
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
    @JoinColumn(name = "usuario_creador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioCreador;
    @OneToMany(mappedBy = "permiso", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection;
    @OneToMany(mappedBy = "idPermiso", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<PePermisoCabEdificacion> pePermisoCabEdificacionCollection;
    @OneToMany(mappedBy = "idPermiso", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection;
    @OneToMany(mappedBy = "pePermiso", fetch = FetchType.LAZY)
    private Collection<OtrosTramitesHasPermiso> otrosTramitesHasPermisoCollection;
    
    public PePermiso() {
    }

    public PePermiso(Long id) {
        this.id = id;
    }

    public PePermiso(Long id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getCedulaPropietario() {
        return cedulaPropietario;
    }

    public void setCedulaPropietario(String cedulaPropietario) {
        this.cedulaPropietario = cedulaPropietario;
    }

    public String getCedulaResponsableTecnico() {
        return cedulaResponsableTecnico;
    }

    public void setCedulaResponsableTecnico(String cedulaResponsableTecnico) {
        this.cedulaResponsableTecnico = cedulaResponsableTecnico;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getAreaImplantacion() {
        return areaImplantacion;
    }

    public void setAreaImplantacion(BigDecimal areaImplantacion) {
        this.areaImplantacion = areaImplantacion;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getLineaFabrica() {
        return lineaFabrica;
    }

    public void setLineaFabrica(BigDecimal lineaFabrica) {
        this.lineaFabrica = lineaFabrica;
    }

    public BigDecimal getRetiroFrontal() {
        return retiroFrontal;
    }

    public void setRetiroFrontal(BigDecimal retiroFrontal) {
        this.retiroFrontal = retiroFrontal;
    }

    public BigDecimal getRetiroPosterior() {
        return retiroPosterior;
    }

    public void setRetiroPosterior(BigDecimal retiroPosterior) {
        this.retiroPosterior = retiroPosterior;
    }

    public BigDecimal getRetiroLateral1() {
        return retiroLateral1;
    }

    public void setRetiroLateral1(BigDecimal retiroLateral1) {
        this.retiroLateral1 = retiroLateral1;
    }

    public BigDecimal getRetiroLateral2() {
        return retiroLateral2;
    }

    public void setRetiroLateral2(BigDecimal retiroLateral2) {
        this.retiroLateral2 = retiroLateral2;
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

    public BigDecimal getAvaluoLiquidacion() {
        return avaluoLiquidacion;
    }

    public void setAvaluoLiquidacion(BigDecimal avaluoLiquidacion) {
        this.avaluoLiquidacion = avaluoLiquidacion;
    }

    public String getTecnicoRegistroProfesional() {
        return tecnicoRegistroProfesional;
    }

    public void setTecnicoRegistroProfesional(String tecnicoRegistroProfesional) {
        this.tecnicoRegistroProfesional = tecnicoRegistroProfesional;
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

    public BigDecimal getLineaF() {
        return lineaF;
    }

    public void setLineaF(BigDecimal lineaF) {
        this.lineaF = lineaF;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Short getAnioTramite() {
        return anioTramite;
    }

    public void setAnioTramite(Short anioTramite) {
        this.anioTramite = anioTramite;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
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

    public Short getAnioPermiso() {
        return anioPermiso;
    }

    public void setAnioPermiso(Short anioPermiso) {
        this.anioPermiso = anioPermiso;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public BigDecimal getAreaEdificaciones() {
        return areaEdificaciones;
    }

    public void setAreaEdificaciones(BigDecimal areaEdificaciones) {
        this.areaEdificaciones = areaEdificaciones;
    }

    public BigDecimal getAreaParqueos() {
        return areaParqueos;
    }

    public void setAreaParqueos(BigDecimal areaParqueos) {
        this.areaParqueos = areaParqueos;
    }

    public Integer getCantidadEdificaciones() {
        return cantidadEdificaciones;
    }

    public void setCantidadEdificaciones(Integer cantidadEdificaciones) {
        this.cantidadEdificaciones = cantidadEdificaciones;
    }

    public Integer getCantidadParqueos() {
        return cantidadParqueos;
    }

    public void setCantidadParqueos(Integer cantidadParqueos) {
        this.cantidadParqueos = cantidadParqueos;
    }

    public String getCaracerrf() {
        return caracerrf;
    }

    public void setCaracerrf(String caracerrf) {
        this.caracerrf = caracerrf;
    }

    public String getCaracerrl1() {
        return caracerrl1;
    }

    public void setCaracerrl1(String caracerrl1) {
        this.caracerrl1 = caracerrl1;
    }

    public String getCaracerrl2() {
        return caracerrl2;
    }

    public void setCaracerrl2(String caracerrl2) {
        this.caracerrl2 = caracerrl2;
    }

    public String getCaracerrp() {
        return caracerrp;
    }

    public void setCaracerrp(String caracerrp) {
        this.caracerrp = caracerrp;
    }

    public String getCaradosrf() {
        return caradosrf;
    }

    public void setCaradosrf(String caradosrf) {
        this.caradosrf = caradosrf;
    }

    public String getCaradosrl1() {
        return caradosrl1;
    }

    public void setCaradosrl1(String caradosrl1) {
        this.caradosrl1 = caradosrl1;
    }

    public String getCaradosrp() {
        return caradosrp;
    }

    public void setCaradosrp(String caradosrp) {
        this.caradosrp = caradosrp;
    }

    public String getDescFamiliar() {
        return descFamiliar;
    }

    public void setDescFamiliar(String descFamiliar) {
        this.descFamiliar = descFamiliar;
    }

    public String getCaradosrl2() {
        return caradosrl2;
    }

    public void setCaradosrl2(String caradosrl2) {
        this.caradosrl2 = caradosrl2;
    }

    public BigInteger getCodigoPermisoAnterior() {
        return codigoPermisoAnterior;
    }

    public void setCodigoPermisoAnterior(BigInteger codigoPermisoAnterior) {
        this.codigoPermisoAnterior = codigoPermisoAnterior;
    }

    public BigInteger getIdprocess() {
        return idprocess;
    }

    public void setIdprocess(BigInteger idprocess) {
        this.idprocess = idprocess;
    }

    public String getRegistroVista() {
        return registroVista;
    }

    public void setRegistroVista(String registroVista) {
        this.registroVista = registroVista;
    }

    public BigInteger getNumTramAnt() {
        return numTramAnt;
    }

    public void setNumTramAnt(BigInteger numTramAnt) {
        this.numTramAnt = numTramAnt;
    }

    public BigInteger getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(BigInteger numReporte) {
        this.numReporte = numReporte;
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

    public String getNombUrbanizacionImpresa() {
        return nombUrbanizacionImpresa;
    }

    public void setNombUrbanizacionImpresa(String nombUrbanizacionImpresa) {
        this.nombUrbanizacionImpresa = nombUrbanizacionImpresa;
    }

    public Boolean getMostrarCertificado() {
        return mostrarCertificado;
    }

    public void setMostrarCertificado(Boolean mostrarCertificado) {
        this.mostrarCertificado = mostrarCertificado;
    }

    public PeTipoPermiso getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(PeTipoPermiso tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public CatPredio getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(CatPredio idPredio) {
        this.idPredio = idPredio;
    }

    public CatEnte getResponsablePersona() {
        return responsablePersona;
    }

    public void setResponsablePersona(CatEnte responsablePersona) {
        this.responsablePersona = responsablePersona;
    }

    public CatEnte getPropietarioPersona() {
        return propietarioPersona;
    }

    public void setPropietarioPersona(CatEnte propietarioPersona) {
        this.propietarioPersona = propietarioPersona;
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

    public boolean getCartaAdosamiento() {
        return cartaAdosamiento;
    }

    public void setCartaAdosamiento(boolean cartaAdosamiento) {
        this.cartaAdosamiento = cartaAdosamiento;
    }

    public AclUser getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(AclUser usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public boolean isEsMacroLote() {
        return esMacroLote;
    }

    public void setEsMacroLote(boolean esMacroLote) {
        this.esMacroLote = esMacroLote;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection() {
        return pePermisosAdicionalesCollection;
    }

    public void setPePermisosAdicionalesCollection(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection) {
        this.pePermisosAdicionalesCollection = pePermisosAdicionalesCollection;
    }

    public Collection<PePermisoCabEdificacion> getPePermisoCabEdificacionCollection() {
        return pePermisoCabEdificacionCollection;
    }

    public Collection<OtrosTramitesHasPermiso> getOtrosTramitesHasPermisoCollection() {
        return otrosTramitesHasPermisoCollection;
    }

    public void setOtrosTramitesHasPermisoCollection(Collection<OtrosTramitesHasPermiso> otrosTramitesHasPermisoCollection) {
        this.otrosTramitesHasPermisoCollection = otrosTramitesHasPermisoCollection;
    }

    public void setPePermisoCabEdificacionCollection(Collection<PePermisoCabEdificacion> pePermisoCabEdificacionCollection) {
        this.pePermisoCabEdificacionCollection = pePermisoCabEdificacionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection() {
        return peEstructuraEspecialCollection;
    }

    public void setPeEstructuraEspecialCollection(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection) {
        this.peEstructuraEspecialCollection = peEstructuraEspecialCollection;
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
        if (!(object instanceof PePermiso)) {
            return false;
        }
        PePermiso other = (PePermiso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PePermiso[ id=" + id + " ]";
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

}
