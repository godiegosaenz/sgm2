/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.DisparadorTramites;
import com.origami.sgm.entities.CatExcepciones;
import com.origami.sgm.entities.AclRol;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_tipo_tramite", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeTipoTramite.findAll", query = "SELECT g FROM GeTipoTramite g"),
    @NamedQuery(name = "GeTipoTramite.findById", query = "SELECT g FROM GeTipoTramite g WHERE g.id = :id"),
    @NamedQuery(name = "GeTipoTramite.findByDescripcion", query = "SELECT g FROM GeTipoTramite g WHERE g.descripcion = :descripcion"),
    @NamedQuery(name = "GeTipoTramite.findByDatosPredio", query = "SELECT g FROM GeTipoTramite g WHERE g.datosPredio = :datosPredio"),
    @NamedQuery(name = "GeTipoTramite.findByActivitykey", query = "SELECT g FROM GeTipoTramite g WHERE g.activitykey = :activitykey"),
    @NamedQuery(name = "GeTipoTramite.findByCarpeta", query = "SELECT g FROM GeTipoTramite g WHERE g.carpeta = :carpeta"),
    @NamedQuery(name = "GeTipoTramite.findByUserDireccion", query = "SELECT g FROM GeTipoTramite g WHERE g.userDireccion = :userDireccion"),
    @NamedQuery(name = "GeTipoTramite.findByFormaCalculo", query = "SELECT g FROM GeTipoTramite g WHERE g.formaCalculo = :formaCalculo"),
    @NamedQuery(name = "GeTipoTramite.findByDescripcionTramite", query = "SELECT g FROM GeTipoTramite g WHERE g.descripcionTramite = :descripcionTramite"),
    @NamedQuery(name = "GeTipoTramite.findByEstado", query = "SELECT g FROM GeTipoTramite g WHERE g.estado = :estado"),
    @NamedQuery(name = "GeTipoTramite.findByDocumentIdAlfresco", query = "SELECT g FROM GeTipoTramite g WHERE g.documentIdAlfresco = :documentIdAlfresco"),
    @NamedQuery(name = "GeTipoTramite.findByUrlEnlaceVideo", query = "SELECT g FROM GeTipoTramite g WHERE g.urlEnlaceVideo = :urlEnlaceVideo"),
    @NamedQuery(name = "GeTipoTramite.findByPaginaSolicitada", query = "SELECT g FROM GeTipoTramite g WHERE g.paginaSolicitada = :paginaSolicitada"),
    @NamedQuery(name = "GeTipoTramite.findByArchivoBpmn", query = "SELECT g FROM GeTipoTramite g WHERE g.archivoBpmn = :archivoBpmn"),
    @NamedQuery(name = "GeTipoTramite.findByDocumentoFichaTecnica", query = "SELECT g FROM GeTipoTramite g WHERE g.documentoFichaTecnica = :documentoFichaTecnica"),
    @NamedQuery(name = "GeTipoTramite.findByAbreviatura", query = "SELECT g FROM GeTipoTramite g WHERE g.abreviatura = :abreviatura"),
    @NamedQuery(name = "GeTipoTramite.findByMailFrom", query = "SELECT g FROM GeTipoTramite g WHERE g.mailFrom = :mailFrom"),
    @NamedQuery(name = "GeTipoTramite.findByMailFooter", query = "SELECT g FROM GeTipoTramite g WHERE g.mailFooter = :mailFooter"),
    @NamedQuery(name = "GeTipoTramite.findByTieneDigitalizacion", query = "SELECT g FROM GeTipoTramite g WHERE g.tieneDigitalizacion = :tieneDigitalizacion"),
    @NamedQuery(name = "GeTipoTramite.findByMailDepartamentoResponsable", query = "SELECT g FROM GeTipoTramite g WHERE g.mailDepartamentoResponsable = :mailDepartamentoResponsable")})
public class GeTipoTramite implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "mostrar_estadistica", nullable = false)
    private boolean mostrarEstadistica;
    @Column(name = "ren_tipo_liquidacion")
    private BigInteger renTipoLiquidacion;
    @OneToMany(mappedBy = "tipoTramite", fetch = FetchType.LAZY)
    private Collection<CatExcepciones> catExcepcionesCollection;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    @Column(name = "datos_predio")
    private Boolean datosPredio;
    @Size(max = 255)
    @Column(name = "activitykey", length = 255)
    private String activitykey;
    @Size(max = 100)
    @Column(name = "carpeta", length = 100)
    private String carpeta;
    @Size(max = 50)
    @Column(name = "user_direccion", length = 50)
    private String userDireccion;
    @Size(max = 4000)
    @Column(name = "forma_calculo", length = 4000)
    private String formaCalculo;
    @Size(max = 4000)
    @Column(name = "descripcion_tramite", length = 4000)
    private String descripcionTramite;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 255)
    @Column(name = "document_id_alfresco", length = 255)
    private String documentIdAlfresco;
    @Size(max = 255)
    @Column(name = "url_enlace_video", length = 255)
    private String urlEnlaceVideo;
    @Size(max = 250)
    @Column(name = "pagina_solicitada", length = 250)
    private String paginaSolicitada;
    @Size(max = 100)
    @Column(name = "archivo_bpmn", length = 100)
    private String archivoBpmn;
    @Size(max = 255)
    @Column(name = "documento_ficha_tecnica", length = 255)
    private String documentoFichaTecnica;
    @Size(max = 50)
    @Column(name = "abreviatura", length = 50)
    private String abreviatura;
    @Size(max = 40)
    @Column(name = "mail_from", length = 40)
    private String mailFrom;
    @Size(max = 2147483647)
    @Column(name = "mail_footer", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String mailFooter;
    @Column(name = "tiene_digitalizacion")
    private Boolean tieneDigitalizacion;
    @Column(name = "asignacion_manual")
    private Boolean asignacionManual;
    @Size(max = 60)
    @Column(name = "mail_departamento_responsable", length = 60)
    private String mailDepartamentoResponsable;
    @Column(name = "facelet_inicial", length = 200)
    private String faceletInicial;
    @Column(name = "flag_one")
    private Boolean flagOne; // SE LO USA PARA CUANDO ESTE LISTO RECAUDACIONES EN LA BASE SE CAMBIA A TRUE
    @Column(name = "llega_catastro")
    private Boolean llegaCatastro;
    @Column(name = "tipo_req_ws")
    private Long tipoReqWs;
    @Size(max = 100)
    @Column(name = "id_proceso_temp", length = 100)
    private String idProcesoTemp;
    @Column(name = "roles_perm", length = 200)
    private String rolesPerm;
    @Column(name = "tiene_valija")
    private Boolean tieneValija;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoTramite", fetch = FetchType.LAZY)
    private Collection<MatFormulaTramite> matFormulaTramiteCollection;

    @OneToMany(mappedBy = "tipoTramite", fetch = FetchType.LAZY)
    private Collection<ParametrosDisparador> parametrosDisparadorCollection;

    @JoinColumn(name = "disparador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private DisparadorTramites disparador;

    @JoinColumn(name = "departamento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeDepartamento departamento;

    @JoinColumn(name = "tipo_proceso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoProcesos tipoProceso;
    private static final long serialVersionUID = 1L;

    @ManyToMany(mappedBy = "geTipoTramiteCollection", fetch = FetchType.LAZY)
    private Collection<GeRequisitosTramite> geRequisitosTramiteCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoTramite", fetch = FetchType.LAZY)
    private Collection<ProcesoReporte> procesoReporteCollection;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeCategoriasOnlinea categoria;
    @JoinColumn(name = "rol", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclRol rol;
    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<GeTramiteBaseLegal> geTramiteBaseLegalCollection;
    @OneToMany(mappedBy = "idTramite", fetch = FetchType.LAZY)
    private Collection<GeTiposSolicitudesServicio> geTiposSolicitudesServicioCollection;
    @OneToMany(mappedBy = "tramiteTipo", fetch = FetchType.LAZY)
    private Collection<GeParametrosTramite> geParametrosTramiteCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoTramite")
    private Collection<GeRequisitosTipoTramite> geRequisitosTipoTramiteCollection;
    @OneToMany(mappedBy = "tramite", fetch = FetchType.LAZY)
    private Collection<GeTareaUsuario> geTareaUsuarioCollection;
    @OneToMany(mappedBy = "tipoTramite", fetch = FetchType.LAZY)
    private Collection<HistoricoTramites> historicoTramitesCollection;
    @OneToMany(mappedBy = "tramiteDepartamento", fetch = FetchType.LAZY)
    private Collection<SecuenciaTramite> secuenciaTramitesCollection;

    public GeTipoTramite() {
    }

    public GeTipoTramite(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getDatosPredio() {
        return datosPredio;
    }

    public void setDatosPredio(Boolean datosPredio) {
        this.datosPredio = datosPredio;
    }

    public String getActivitykey() {
        return activitykey;
    }

    public void setActivitykey(String activitykey) {
        this.activitykey = activitykey;
    }

    public String getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(String carpeta) {
        this.carpeta = carpeta;
    }

    public String getUserDireccion() {
        return userDireccion;
    }

    public void setUserDireccion(String userDireccion) {
        this.userDireccion = userDireccion;
    }

    public String getFormaCalculo() {
        return formaCalculo;
    }

    public void setFormaCalculo(String formaCalculo) {
        this.formaCalculo = formaCalculo;
    }

    public String getDescripcionTramite() {
        return descripcionTramite;
    }

    public void setDescripcionTramite(String descripcionTramite) {
        this.descripcionTramite = descripcionTramite;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getDocumentIdAlfresco() {
        return documentIdAlfresco;
    }

    public void setDocumentIdAlfresco(String documentIdAlfresco) {
        this.documentIdAlfresco = documentIdAlfresco;
    }

    public String getUrlEnlaceVideo() {
        return urlEnlaceVideo;
    }

    public void setUrlEnlaceVideo(String urlEnlaceVideo) {
        this.urlEnlaceVideo = urlEnlaceVideo;
    }

    public String getPaginaSolicitada() {
        return paginaSolicitada;
    }

    public void setPaginaSolicitada(String paginaSolicitada) {
        this.paginaSolicitada = paginaSolicitada;
    }

    public String getArchivoBpmn() {
        return archivoBpmn;
    }

    public void setArchivoBpmn(String archivoBpmn) {
        this.archivoBpmn = archivoBpmn;
    }

    public String getDocumentoFichaTecnica() {
        return documentoFichaTecnica;
    }

    public void setDocumentoFichaTecnica(String documentoFichaTecnica) {
        this.documentoFichaTecnica = documentoFichaTecnica;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailFooter() {
        return mailFooter;
    }

    public void setMailFooter(String mailFooter) {
        this.mailFooter = mailFooter;
    }

    public Boolean getTieneDigitalizacion() {
        return tieneDigitalizacion;
    }

    public void setTieneDigitalizacion(Boolean tieneDigitalizacion) {
        this.tieneDigitalizacion = tieneDigitalizacion;
    }

    public String getMailDepartamentoResponsable() {
        return mailDepartamentoResponsable;
    }

    public void setMailDepartamentoResponsable(String mailDepartamentoResponsable) {
        this.mailDepartamentoResponsable = mailDepartamentoResponsable;
    }

    public String getFaceletInicial() {
        return faceletInicial;
    }

    public void setFaceletInicial(String faceletInicial) {
        this.faceletInicial = faceletInicial;
    }

    public Boolean getFlagOne() {
        return flagOne;
    }

    public void setFlagOne(Boolean flagOne) {
        this.flagOne = flagOne;
    }

    public Boolean getLlegaCatastro() {
        return llegaCatastro;
    }

    public void setLlegaCatastro(Boolean llegaCatastro) {
        this.llegaCatastro = llegaCatastro;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeRequisitosTramite> getGeRequisitosTramiteCollection() {
        return geRequisitosTramiteCollection;
    }

    public void setGeRequisitosTramiteCollection(Collection<GeRequisitosTramite> geRequisitosTramiteCollection) {
        this.geRequisitosTramiteCollection = geRequisitosTramiteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ProcesoReporte> getProcesoReporteCollection() {
        return procesoReporteCollection;
    }

    public void setProcesoReporteCollection(Collection<ProcesoReporte> procesoReporteCollection) {
        this.procesoReporteCollection = procesoReporteCollection;
    }

    public GeCategoriasOnlinea getCategoria() {
        return categoria;
    }

    public void setCategoria(GeCategoriasOnlinea categoria) {
        this.categoria = categoria;
    }

    public AclRol getRol() {
        return rol;
    }

    public void setRol(AclRol rol) {
        this.rol = rol;
    }

    public Boolean getAsignacionManual() {
        return asignacionManual;
    }

    public void setAsignacionManual(Boolean asignacionManual) {
        this.asignacionManual = asignacionManual;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTramiteBaseLegal> getGeTramiteBaseLegalCollection() {
        return geTramiteBaseLegalCollection;
    }

    public void setGeTramiteBaseLegalCollection(Collection<GeTramiteBaseLegal> geTramiteBaseLegalCollection) {
        this.geTramiteBaseLegalCollection = geTramiteBaseLegalCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTiposSolicitudesServicio> getGeTiposSolicitudesServicioCollection() {
        return geTiposSolicitudesServicioCollection;
    }

    public void setGeTiposSolicitudesServicioCollection(Collection<GeTiposSolicitudesServicio> geTiposSolicitudesServicioCollection) {
        this.geTiposSolicitudesServicioCollection = geTiposSolicitudesServicioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeParametrosTramite> getGeParametrosTramiteCollection() {
        return geParametrosTramiteCollection;
    }

    public void setGeParametrosTramiteCollection(Collection<GeParametrosTramite> geParametrosTramiteCollection) {
        this.geParametrosTramiteCollection = geParametrosTramiteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeRequisitosTipoTramite> getGeRequisitosTipoTramiteCollection() {
        return geRequisitosTipoTramiteCollection;
    }

    public void setGeRequisitosTipoTramiteCollection(Collection<GeRequisitosTipoTramite> geRequisitosTipoTramiteCollection) {
        this.geRequisitosTipoTramiteCollection = geRequisitosTipoTramiteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTareaUsuario> getGeTareaUsuarioCollection() {
        return geTareaUsuarioCollection;
    }

    public void setGeTareaUsuarioCollection(Collection<GeTareaUsuario> geTareaUsuarioCollection) {
        this.geTareaUsuarioCollection = geTareaUsuarioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<HistoricoTramites> getHistoricoTramitesCollection() {
        return historicoTramitesCollection;
    }

    public void setHistoricoTramitesCollection(Collection<HistoricoTramites> historicoTramitesCollection) {
        this.historicoTramitesCollection = historicoTramitesCollection;
    }

    public GeTipoProcesos getTipoProceso() {
        return tipoProceso;
    }

    public void setTipoProceso(GeTipoProcesos tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ParametrosDisparador> getParametrosDisparadorCollection() {
        return parametrosDisparadorCollection;
    }

    public void setParametrosDisparadorCollection(Collection<ParametrosDisparador> parametrosDisparadorCollection) {
        this.parametrosDisparadorCollection = parametrosDisparadorCollection;
    }

    public DisparadorTramites getDisparador() {
        return disparador;
    }

    public void setDisparador(DisparadorTramites disparador) {
        this.disparador = disparador;
    }

    public String getIdProcesoTemp() {
        return idProcesoTemp;
    }

    public void setIdProcesoTemp(String idProcesoTemp) {
        this.idProcesoTemp = idProcesoTemp;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<MatFormulaTramite> getMatFormulaTramiteCollection() {
        return matFormulaTramiteCollection;
    }

    public void setMatFormulaTramiteCollection(Collection<MatFormulaTramite> matFormulaTramiteCollection) {
        this.matFormulaTramiteCollection = matFormulaTramiteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SecuenciaTramite> getSecuenciaTramitesCollection() {
        return secuenciaTramitesCollection;
    }

    public void setSecuenciaTramitesCollection(Collection<SecuenciaTramite> secuenciaTramitesCollection) {
        this.secuenciaTramitesCollection = secuenciaTramitesCollection;
    }

    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public Boolean getMostrarEstadistica() {
        return mostrarEstadistica;
    }

    public void setMostrarEstadistica(Boolean mostrarEstadistica) {
        this.mostrarEstadistica = mostrarEstadistica;
    }

    public BigInteger getRenTipoLiquidacion() {
        return renTipoLiquidacion;
    }

    public void setRenTipoLiquidacion(BigInteger renTipoLiquidacion) {
        this.renTipoLiquidacion = renTipoLiquidacion;
    }

    public String getRolesPerm() {
        return rolesPerm;
    }

    public void setRolesPerm(String rolesPerm) {
        this.rolesPerm = rolesPerm;
    }

    public Boolean getTieneValija() {
        return tieneValija;
    }

    public void setTieneValija(Boolean tieneValija) {
        this.tieneValija = tieneValija;
    }

    public Long getTipoReqWs() {
        return tipoReqWs;
    }

    public void setTipoReqWs(Long tipoReqWs) {
        this.tipoReqWs = tipoReqWs;
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
        if (!(object instanceof GeTipoTramite)) {
            return false;
        }
        GeTipoTramite other = (GeTipoTramite) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "" + id + "";
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatExcepciones> getCatExcepcionesCollection() {
        return catExcepcionesCollection;
    }

    public void setCatExcepcionesCollection(Collection<CatExcepciones> catExcepcionesCollection) {
        this.catExcepcionesCollection = catExcepcionesCollection;
    }

}
