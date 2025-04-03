/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import java.util.List;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author supergold
 */
@Entity
@Table(name = "sv_solicitud_servicios", schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SvSolicitudServicios.findAll", query = "SELECT s FROM SvSolicitudServicios s"),
    @NamedQuery(name = "SvSolicitudServicios.findById", query = "SELECT s FROM SvSolicitudServicios s WHERE s.id = :id"),
    @NamedQuery(name = "SvSolicitudServicios.findByDireccion", query = "SELECT s FROM SvSolicitudServicios s WHERE s.representante = :representante"),
    @NamedQuery(name = "SvSolicitudServicios.findByFechaInconveniente", query = "SELECT s FROM SvSolicitudServicios s WHERE s.fechaInconveniente = :fechaInconveniente"),
    @NamedQuery(name = "SvSolicitudServicios.findByDescripcionInconveniente", query = "SELECT s FROM SvSolicitudServicios s WHERE s.descripcionInconveniente = :descripcionInconveniente"),
    @NamedQuery(name = "SvSolicitudServicios.findBySolicitante", query = "SELECT s FROM SvSolicitudServicios s WHERE s.solicitante = :solicitante"),
    @NamedQuery(name = "SvSolicitudServicios.findByNotificacionDep", query = "SELECT s FROM SvSolicitudServicios s WHERE s.notificacionDep = :notificacionDep"),
    @NamedQuery(name = "SvSolicitudServicios.findByParroquia", query = "SELECT s FROM SvSolicitudServicios s WHERE s.parroquia = :parroquia"),
    @NamedQuery(name = "SvSolicitudServicios.findByCdla", query = "SELECT s FROM SvSolicitudServicios s WHERE s.cdla = :cdla"),
    @NamedQuery(name = "SvSolicitudServicios.findBySolicitudInterna", query = "SELECT s FROM SvSolicitudServicios s WHERE s.solicitudInterna = :solicitudInterna"),
    @NamedQuery(name = "SvSolicitudServicios.findByEnteSolicitante", query = "SELECT s FROM SvSolicitudServicios s WHERE s.enteSolicitante = :enteSolicitante"),
    @NamedQuery(name = "SvSolicitudServicios.findByInforme", query = "SELECT s FROM SvSolicitudServicios s WHERE s.informe = :informe"),
    @NamedQuery(name = "SvSolicitudServicios.findByFechaCreacion", query = "SELECT s FROM SvSolicitudServicios s WHERE s.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "SvSolicitudServicios.findByAsignado", query = "SELECT s FROM SvSolicitudServicios s WHERE s.asignado = :asignado"),
    @NamedQuery(name = "SvSolicitudServicios.findByAsignados", query = "SELECT s FROM SvSolicitudServicios s WHERE s.asignados = :asignados"),
    @NamedQuery(name = "SvSolicitudServicios.findByStatus", query = "SELECT s FROM SvSolicitudServicios s WHERE s.status = :status"),
    @NamedQuery(name = "SvSolicitudServicios.findByTramite", query = "SELECT s FROM SvSolicitudServicios s WHERE s.tramite = :tramite")})
public class SvSolicitudServicios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 500)
    @Column(name = "representante", length = 500)
    private String representante;
    @Column(name = "fecha_inconveniente")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInconveniente;
    @Size(max = 500)
    @Column(name = "descripcion_inconveniente", length = 500)
    private String descripcionInconveniente;
    @Column(name = "solicitante")
    private BigInteger solicitante;
    @Size(max = 500)
    @Column(name = "notificacion_dep", length = 500)
    private String notificacionDep;
    @Column(name = "solicitud_interna")
    private Boolean solicitudInterna;
    @Column(name = "informe")
    private String informe;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Column(name = "asignado")
    private Boolean asignado;
    @Size(max = 250)
    @Column(name = "asignados", length = 250)
    private String asignados;
    @Size(max = 30)
    @Column(name = "status", length = 30)
    private String status;
    @Column(name = "archivar")
    private Boolean archivar = false;
    @Column(name = "notificar")
    private Boolean notificar = true;
    @Column(name = "notificacion")
    private String notificacion;
    @JoinColumn(name = "tipo_servicio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private VuItems tipoServicio;
    @JoinColumn(name = "lugar_audiencia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private VuItems lugarAudiencia;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;
    @JoinColumn(name = "ente_solicitante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte enteSolicitante;
    @JoinColumn(name = "parroquia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatParroquia parroquia;
    @JoinColumn(name = "cdla", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela cdla;
    @Column(name = "nota_guia")
    private String notaGuia;
    
    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private Collection<SvSolicitudDepartamento> svSolicitudDepartamentoCollection;
    @OneToMany(mappedBy = "servicios", fetch = FetchType.LAZY)
    private List<SvSolicitudServiciosPredios> svSolicitudServiciosPredios;
    

    @Column(name = "fecha_inspeccion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInspeccion;
    
    public SvSolicitudServicios() {
    }

    public SvSolicitudServicios(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public Date getFechaInconveniente() {
        return fechaInconveniente;
    }

    public void setFechaInconveniente(Date fechaInconveniente) {
        this.fechaInconveniente = fechaInconveniente;
    }

    public String getDescripcionInconveniente() {
        return descripcionInconveniente;
    }

    public void setDescripcionInconveniente(String descripcionInconveniente) {
        this.descripcionInconveniente = descripcionInconveniente;
    }

    public BigInteger getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(BigInteger solicitante) {
        this.solicitante = solicitante;
    }

    public String getNotificacionDep() {
        return notificacionDep;
    }

    public void setNotificacionDep(String notificacionDep) {
        this.notificacionDep = notificacionDep;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public CatCiudadela getCdla() {
        return cdla;
    }

    public void setCdla(CatCiudadela cdla) {
        this.cdla = cdla;
    }

    public Boolean getSolicitudInterna() {
        return solicitudInterna;
    }

    public void setSolicitudInterna(Boolean solicitudInterna) {
        this.solicitudInterna = solicitudInterna;
    }

    public CatEnte getEnteSolicitante() {
        return enteSolicitante;
    }

    public void setEnteSolicitante(CatEnte enteSolicitante) {
        this.enteSolicitante = enteSolicitante;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getAsignado() {
        return asignado;
    }

    public void setAsignado(Boolean asignado) {
        this.asignado = asignado;
    }

    public String getAsignados() {
        return asignados;
    }

    public void setAsignados(String asignados) {
        this.asignados = asignados;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VuItems getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(VuItems tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public VuItems getLugarAudiencia() {
        return lugarAudiencia;
    }

    public void setLugarAudiencia(VuItems lugarAudiencia) {
        this.lugarAudiencia = lugarAudiencia;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Boolean getArchivar() {
        return archivar;
    }

    public void setArchivar(Boolean archivar) {
        this.archivar = archivar;
    }

    public Boolean getNotificar() {
        return notificar;
    }

    public void setNotificar(Boolean notificar) {
        this.notificar = notificar;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public String getNotaGuia() {
        return notaGuia;
    }

    public void setNotaGuia(String notaGuia) {
        this.notaGuia = notaGuia;
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
        if (!(object instanceof SvSolicitudServicios)) {
            return false;
        }
        SvSolicitudServicios other = (SvSolicitudServicios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.SvSolicitudServicios[ id=" + id + " ]";
    }

    public Collection<SvSolicitudDepartamento> getSvSolicitudDepartamentoCollection() {
        return svSolicitudDepartamentoCollection;
    }

    public void setSvSolicitudDepartamentoCollection(Collection<SvSolicitudDepartamento> SvSolicitudDepartamentoCollection) {
        this.svSolicitudDepartamentoCollection = SvSolicitudDepartamentoCollection;
    }

    public List<SvSolicitudServiciosPredios> getSvSolicitudServiciosPredios() {
        return svSolicitudServiciosPredios;
    }

    public void setSvSolicitudServiciosPredios(List<SvSolicitudServiciosPredios> svSolicitudServiciosPredios) {
        this.svSolicitudServiciosPredios = svSolicitudServiciosPredios;
    }

    public Date getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(Date fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    
    
    
}
