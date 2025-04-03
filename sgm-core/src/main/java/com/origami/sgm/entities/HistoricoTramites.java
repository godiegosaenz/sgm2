/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CoaJuicio;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.Agenda;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Where;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "historico_tramites", schema = SchemasConfig.FLOW, uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistoricoTramites.findAll", query = "SELECT h FROM HistoricoTramites h"),
    @NamedQuery(name = "HistoricoTramites.findById", query = "SELECT h FROM HistoricoTramites h WHERE h.id = :id"),
    @NamedQuery(name = "HistoricoTramites.findByIdTramite", query = "SELECT h FROM HistoricoTramites h WHERE h.idTramite = :idTramite"),
    @NamedQuery(name = "HistoricoTramites.findByIdProceso", query = "SELECT h FROM HistoricoTramites h WHERE h.idProceso = :idProceso"),
    @NamedQuery(name = "HistoricoTramites.findByNumPredio", query = "SELECT h FROM HistoricoTramites h WHERE h.numPredio = :numPredio"),
    @NamedQuery(name = "HistoricoTramites.findByEstado", query = "SELECT h FROM HistoricoTramites h WHERE h.estado = :estado"),
    @NamedQuery(name = "HistoricoTramites.findByFecha", query = "SELECT h FROM HistoricoTramites h WHERE h.fecha = :fecha"),
    @NamedQuery(name = "HistoricoTramites.findByUrbanizacion", query = "SELECT h FROM HistoricoTramites h WHERE h.urbanizacion = :urbanizacion"),
    @NamedQuery(name = "HistoricoTramites.findByNombrePropietario", query = "SELECT h FROM HistoricoTramites h WHERE h.nombrePropietario = :nombrePropietario"),
    @NamedQuery(name = "HistoricoTramites.findByTipoTramiteNombre", query = "SELECT h FROM HistoricoTramites h WHERE h.tipoTramiteNombre = :tipoTramiteNombre"),
    @NamedQuery(name = "HistoricoTramites.findByCorreos", query = "SELECT h FROM HistoricoTramites h WHERE h.correos = :correos"),
    @NamedQuery(name = "HistoricoTramites.findByTelefonos", query = "SELECT h FROM HistoricoTramites h WHERE h.telefonos = :telefonos"),
    @NamedQuery(name = "HistoricoTramites.findByNumLiquidacion", query = "SELECT h FROM HistoricoTramites h WHERE h.numLiquidacion = :numLiquidacion"),
    @NamedQuery(name = "HistoricoTramites.findByValorLiquidacion", query = "SELECT h FROM HistoricoTramites h WHERE h.valorLiquidacion = :valorLiquidacion"),
    @NamedQuery(name = "HistoricoTramites.findByObservacion", query = "SELECT h FROM HistoricoTramites h WHERE h.observacion = :observacion"),
    @NamedQuery(name = "HistoricoTramites.findByUserCreador", query = "SELECT h FROM HistoricoTramites h WHERE h.userCreador = :userCreador"),
    @NamedQuery(name = "HistoricoTramites.findByNumTramiteXDepartamento", query = "SELECT h FROM HistoricoTramites h WHERE h.numTramiteXDepartamento = :numTramiteXDepartamento")})
public class HistoricoTramites implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id_tramite", nullable = false)
    private Long idTramite;
    //@Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 100)
    @Column(name = "id_proceso", length = 100)
    private String idProceso;
    @Size(max = 100)
    @Column(name = "id_proceso_temp", length = 100)
    private String idProcesoTemp;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    @Size(max = 15)
    @Column(name = "estado", length = 15)
    private String estado;
    @Column(name = "liquidacion_aprobada")
    private Boolean liquidacionAprobada;
    @JoinColumn(name = "tipo_tramite", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite tipoTramite;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "urbanizacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela urbanizacion;
    @Size(max = 250)
    @Column(name = "nombre_propietario", length = 250)
    private String nombrePropietario;
    @Size(max = 20)
    @Column(name = "mz", length = 20)
    private String mz;
    @Size(max = 20)
    @Column(name = "solar", length = 20)
    private String solar;
    @Size(max = 200)
    @Column(name = "tipo_tramite_nombre", length = 200)
    private String tipoTramiteNombre;
    @Size(max = 300)
    @Column(name = "correos", length = 300)
    private String correos;
    @Size(max = 300)
    @Column(name = "telefonos", length = 300)
    private String telefonos;
    @Column(name = "num_liquidacion")
    private BigInteger numLiquidacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_liquidacion", precision = 19, scale = 2)
    private BigDecimal valorLiquidacion;
    @Size(max = 4000)
    @Column(name = "observacion", length = 4000)
    private String observacion;
    @Column(name = "user_creador")
    private Long userCreador;
    @Column(name = "num_tramite_x_departamento")
    private BigInteger numTramiteXDepartamento;
    @Column(name = "carpeta_rep")
    private String carpetaRep;
    @JoinColumn(name = "sub_tipo_tramite", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OtrosTramites subTipoTramite;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte solicitante;
    @Column(name = "correccion")
    private Long correccion=0L;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "historicTramite", fetch = FetchType.LAZY)
    private RegpLiquidacionDerechosAranceles regpLiquidacionDerechosAranceles;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "ht", fetch = FetchType.LAZY)
    private RenPermisosFuncionamientoLocalComercial permisoDeFuncionamientoLC;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<HistoricoTramiteDet> historicoTramiteDetCollection;
    @OneToMany(mappedBy = "idTramite", fetch = FetchType.LAZY)
    @OrderBy("id DESC")
    private Collection<Observaciones> observacionesCollection;
    @Transient
    private String fechaCorrecta;
    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<ProcesoFusionPredios> procesoFusionPrediosCollection;
    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<HistoricoReporteTramite> historicoReporteTramiteCollection;
    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tramite", fetch = FetchType.LAZY)
    private PeInspeccionFinal peInspeccionFinal;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tramite", fetch = FetchType.LAZY)
    private OtrosTramitesHasPermiso otrosTramitesHasPermiso;
    
    @OneToMany(mappedBy = "tramite")
    private Collection<CmMultas> MultasCollection;
    
    @OneToMany(mappedBy = "tramite")
    private Collection<HistoricoArchivo> historicoArchivo;

    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<SvSolicitudServicios> svSolicitudServiciosCollection;
    
    @OneToMany(mappedBy = "tramite")
    private Collection<Agenda> agendaCollection;
    
    @OneToMany(mappedBy = "tramite")
    private Collection<Resolucion> resolucionCollection;
    
    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<RenLiquidacion> renLiquidacionCollection;
    @OneToMany(mappedBy = "tramite")
    private List<FnSolicitudExoneracion> fnSolicitudExoneracions;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tramite", fetch = FetchType.LAZY)
    private CoaJuicio coaJuicio;
    
    public HistoricoTramites() {
    }

    public List<FnSolicitudExoneracion> getFnSolicitudExoneracions() {
        return fnSolicitudExoneracions;
    }

    public void setFnSolicitudExoneracions(List<FnSolicitudExoneracion> fnSolicitudExoneracions) {
        this.fnSolicitudExoneracions = fnSolicitudExoneracions;
    }

    public HistoricoTramites(Long idTramite) {
        this.idTramite = idTramite;
    }
    
    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public String getIdProcesoTemp() {
        return idProcesoTemp;
    }

    public void setIdProcesoTemp(String idProcesoTemp) {
        this.idProcesoTemp = idProcesoTemp;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CatCiudadela getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(CatCiudadela urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public String getSolar() {
        return solar;
    }

    public void setSolar(String solar) {
        this.solar = solar;
    }

    public String getTipoTramiteNombre() {
        return tipoTramiteNombre;
    }

    public void setTipoTramiteNombre(String tipoTramiteNombre) {
        this.tipoTramiteNombre = tipoTramiteNombre;
    }

    public String getCorreos() {
        return correos;
    }

    public void setCorreos(String correos) {
        this.correos = correos;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public BigInteger getNumLiquidacion() {
        return numLiquidacion;
    }

    public void setNumLiquidacion(BigInteger numLiquidacion) {
        this.numLiquidacion = numLiquidacion;
    }

    public BigDecimal getValorLiquidacion() {
        return valorLiquidacion;
    }

    public void setValorLiquidacion(BigDecimal valorLiquidacion) {
        this.valorLiquidacion = valorLiquidacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Long getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(Long userCreador) {
        this.userCreador = userCreador;
    }

    public BigInteger getNumTramiteXDepartamento() {
        return numTramiteXDepartamento;
    }

    public void setNumTramiteXDepartamento(BigInteger numTramiteXDepartamento) {
        this.numTramiteXDepartamento = numTramiteXDepartamento;
    }

    public String getCarpetaRep() {
        return carpetaRep;
    }

    public void setCarpetaRep(String carpetaRep) {
        this.carpetaRep = carpetaRep;
    }

    public OtrosTramites getSubTipoTramite() {
        return subTipoTramite;
    }

    public void setSubTipoTramite(OtrosTramites subTipoTramite) {
        this.subTipoTramite = subTipoTramite;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public RegpLiquidacionDerechosAranceles getRegpLiquidacionDerechosAranceles() {
        return regpLiquidacionDerechosAranceles;
    }

    public void setRegpLiquidacionDerechosAranceles(RegpLiquidacionDerechosAranceles regpLiquidacionDerechosAranceles) {
        this.regpLiquidacionDerechosAranceles = regpLiquidacionDerechosAranceles;
    }

    public String getFechaCorrecta() {
        return fechaCorrecta;
    }

    public void setFechaCorrecta(String fechaCorrecta) {
        this.fechaCorrecta = fechaCorrecta;
    }

    public Collection<ProcesoFusionPredios> getProcesoFusionPrediosCollection() {
        return procesoFusionPrediosCollection;
    }

    public void setProcesoFusionPrediosCollection(Collection<ProcesoFusionPredios> procesoFusionPrediosCollection) {
        this.procesoFusionPrediosCollection = procesoFusionPrediosCollection;
    }

    public Collection<HistoricoReporteTramite> getHistoricoReporteTramiteCollection() {
        return historicoReporteTramiteCollection;
    }

    public void setHistoricoReporteTramiteCollection(Collection<HistoricoReporteTramite> historicoReporteTramiteCollection) {
        this.historicoReporteTramiteCollection = historicoReporteTramiteCollection;
    }

    public OtrosTramitesHasPermiso getOtrosTramitesHasPermiso() {
        return otrosTramitesHasPermiso;
    }

    public void setOtrosTramitesHasPermiso(OtrosTramitesHasPermiso otrosTramitesHasPermiso) {
        this.otrosTramitesHasPermiso = otrosTramitesHasPermiso;
    }
    

    public Collection<Observaciones> getObservacionesCollection() {
        return observacionesCollection;
    }

    public void setObservacionesCollection(Collection<Observaciones> observacionesCollection) {
        this.observacionesCollection = observacionesCollection;
    }

    public Collection<HistoricoTramiteDet> getHistoricoTramiteDetCollection() {
        return historicoTramiteDetCollection;
    }

    public void setHistoricoTramiteDetCollection(Collection<HistoricoTramiteDet> historicoTramiteDetCollection) {
        this.historicoTramiteDetCollection = historicoTramiteDetCollection;
    }

    public Collection<PePermiso> getPePermisoCollection() {
        return pePermisoCollection;
    }

    public void setPePermisoCollection(Collection<PePermiso> pePermisoCollection) {
        this.pePermisoCollection = pePermisoCollection;
    }

    public Collection<HistoricoArchivo> getHistoricoArchivo() {
        return historicoArchivo;
    }

    public void setHistoricoArchivo(Collection<HistoricoArchivo> historicoArchivo) {
        this.historicoArchivo = historicoArchivo;
    }

    public PeInspeccionFinal getPeInspeccionFinal() {
        return peInspeccionFinal;
    }

    public void setPeInspeccionFinal(PeInspeccionFinal peInspeccionFinal) {
        this.peInspeccionFinal = peInspeccionFinal;
    }

    public Collection<SvSolicitudServicios> getSvSolicitudServiciosCollection() {
        return svSolicitudServiciosCollection;
    }

    public void setSvSolicitudServiciosCollection(Collection<SvSolicitudServicios> svSolicitudServiciosCollection) {
        this.svSolicitudServiciosCollection = svSolicitudServiciosCollection;
    }

    public Collection<Agenda> getAgendaCollection() {
        return agendaCollection;
    }

    public void setAgendaCollection(Collection<Agenda> agendaCollection) {
        this.agendaCollection = agendaCollection;
    }

    public Long getCorreccion() {
        return correccion;
    }

    public void setCorreccion(Long correccion) {
        this.correccion = correccion;
    }

    public CoaJuicio getCoaJuicio() {
        return coaJuicio;
    }

    public void setCoaJuicio(CoaJuicio coaJuicio) {
        this.coaJuicio = coaJuicio;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTramite != null ? idTramite.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HistoricoTramites)) {
            return false;
        }
        HistoricoTramites other = (HistoricoTramites) object;
        return !((this.idTramite == null && other.idTramite != null) || (this.idTramite != null && !this.idTramite.equals(other.idTramite)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.HistoricoTramites[ id=" + idTramite + " ]";
    }

    public Collection<Resolucion> getResolucionCollection() {
        return resolucionCollection;
    }

    public void setResolucionCollection(Collection<Resolucion> resolucionCollection) {
        this.resolucionCollection = resolucionCollection;
    }

    public Collection<RenLiquidacion> getRenLiquidacionCollection() {
        return renLiquidacionCollection;
    }

    public void setRenLiquidacionCollection(Collection<RenLiquidacion> renLiquidacionCollection) {
        this.renLiquidacionCollection = renLiquidacionCollection;
    }

    public RenPermisosFuncionamientoLocalComercial getPermisoDeFuncionamientoLC() {
        return permisoDeFuncionamientoLC;
    }

    public void setPermisoDeFuncionamientoLC(RenPermisosFuncionamientoLocalComercial permisoDeFuncionamientoLC) {
        this.permisoDeFuncionamientoLC = permisoDeFuncionamientoLC;
    }

    public Collection<CmMultas> getMultasCollection() {
        return MultasCollection;
    }

    public void setMultasCollection(Collection<CmMultas> MultasCollection) {
        this.MultasCollection = MultasCollection;
    }

    public Boolean getLiquidacionAprobada() {
        return liquidacionAprobada;
    }

    public void setLiquidacionAprobada(Boolean liquidacionAprobada) {
        this.liquidacionAprobada = liquidacionAprobada;
    }

}