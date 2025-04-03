/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.AclUser;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_movimiento", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"task_id"})})
public class RegMovimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_repertorio")
    private Integer numRepertorio;
    @Column(name = "num_inscripcion")
    private Integer numInscripcion;
    @Column(name = "fecha_inscripcion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInscripcion;
    @Column(name = "indice")
    private Integer indice;
    @Column(name = "folio_inicio")
    private Integer folioInicio;
    @Column(name = "folio_fin")
    private Integer folioFin;
    @Column(name = "fecha_cort")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCort;
    @Column(name = "registro")
    private Integer registro;
    @Size(max = 3)
    @Column(name = "estado", length = 3)
    private String estado;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
//    @Column(name = "usuario")
//    private Integer usuario;
    @Column(name = "fecha_correc")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCorrec;
    @Column(name = "usuario_correc")
    private Integer usuarioCorrec;
    @Column(name = "observacion")
    private String observacion;
    @Size(max = 10)
    @Column(name = "num_tomo", length = 10)
    private String numTomo;
    @Column(name = "fecha_oto")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaOto;
    @Column(name = "impraz")
    private Boolean impraz;
    @Size(max = 1)
    @Column(name = "estado_inf", length = 1)
    private String estadoInf;
    @Column(name = "permod")
    private Boolean permod;
    @Column(name = "ord_jud")
    private Boolean ordJud = false;
    @Column(name = "anocon")
    private Integer anocon;
    @Column(name = "fecha_resolucion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaResolucion;
    @Size(max = 2147483647)
    @Column(name = "escrit_juic_prov_resolucion", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String escritJuicProvResolucion;
    @Column(name = "fecha_repertorio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRepertorio;
    @Column(name = "num_tramite")
    private Long numTramite;
    @Size(max = 64)
    @Column(name = "task_id", length = 64)
    private String taskId;
    @Column(name = "razon_impresa")
    private Boolean razonImpresa;
    @Column(name = "inscripcion_impresa")
    private Boolean inscripcionImpresa;
    @Size(max = 2147483647)
    @Column(name = "lind_escr_norte", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String lindEscrNorte;
    @Column(name = "lind_escr_norte_con", precision = 10, scale = 2)
    private BigDecimal lindEscrNorteCon;
    @Size(max = 2147483647)
    @Column(name = "lind_escr_este", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String lindEscrEste;
    @Column(name = "lind_escr_este_con", precision = 10, scale = 2)
    private BigDecimal lindEscrEsteCon;
    @Size(max = 2147483647)
    @Column(name = "lind_escr_sur", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String lindEscrSur;
    @Column(name = "lind_escr_sur_con", precision = 10, scale = 2)
    private BigDecimal lindEscrSurCon;
    @Size(max = 2147483647)
    @Column(name = "lind_escr_oeste", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String lindEscrOeste;
    @Column(name = "lind_escr_oeste_con", precision = 10, scale = 2)
    private BigDecimal lindEscrOesteCon;
    @Column(name = "area_total_escr", precision = 10, scale = 2)
    private BigDecimal areaTotalEscr;
    @Column(name = "alicuota", precision = 12, scale = 6)
    private BigDecimal alicuota;
    @Column(name = "num_pagina_razon")
    private Integer numPaginaRazon;
    @Column(name = "num_pagina_inscripcion")
    private Integer numPaginaInscripcion;
    @Column(name = "num_paginas_contabilizada")
    private Integer numPaginasContabilizada;
    @Column(name = "folio_anterior")
    private Integer folioAnterior;
    @Column(name = "es_negativa")
    private Boolean esNegativa = false;
    @Column(name = "mostrar_relacion")
    private Boolean mostrarRelacion;
    @Column(name = "is_mercantil")
    private Boolean isMercantil = false;
    @Column(name = "transferencia_dominio")
    private Boolean transferenciaDominio;
    @Size(max = 255)
    @Column(name = "valor_uuid", length = 255)
    private String valorUuid;
    @Column(name = "anexo_negativa")
    private String anexoNegativa;
    @Column(name = "obs_cancela")
    private String obsCancela;

    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY)
    private Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection;
    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY)
    private Collection<RegMovimientoCapital> regMovimientoCapitalCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movimientoReff", fetch = FetchType.LAZY)
    private Collection<RegMovimientoReferencia> regMovimientoReferenciaCollection;
    @JoinColumn(name = "libro", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegLibro libro;
    @JoinColumn(name = "ente_judicial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegEnteJudiciales enteJudicial;
    @JoinColumn(name = "asoc_camara", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegAsocCamaras asocCamara;
    @JoinColumn(name = "acto", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegActo acto;
    @JoinColumn(name = "codigo_can", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCanton codigoCan;
    @JoinColumn(name = "user_creador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser userCreador;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movimiento", fetch = FetchType.LAZY)
    private Collection<RegMovimientoFicha> regMovimientoFichaCollection;
    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY)
    private Collection<RegMovimientoSocios> regMovimientoSociosCollection;
    @OneToMany(mappedBy = "movimiento", fetch = FetchType.LAZY)
    private Collection<RegMovimientoCliente> regMovimientoClienteCollection;
    @JoinColumn(name = "regp_certificados_inscripciones", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private RegpCertificadosInscripciones regpCertificadoInscripcion;
    @JoinColumn(name = "registrador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegRegistrador registrador;
    
    @Transient
    private String observacionEliminacion;

    public RegMovimiento() {
    }

    public RegMovimiento(Long id) {
        this.id = id;
    }

    public RegMovimiento(Long id, Integer numRepertorio) {
        this.id = id;
        this.numRepertorio = numRepertorio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(Integer numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public Integer getNumInscripcion() {
        return numInscripcion;
    }

    public void setNumInscripcion(Integer numInscripcion) {
        this.numInscripcion = numInscripcion;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public Integer getFolioInicio() {
        return folioInicio;
    }

    public void setFolioInicio(Integer folioInicio) {
        this.folioInicio = folioInicio;
    }

    public Integer getFolioFin() {
        return folioFin;
    }

    public void setFolioFin(Integer folioFin) {
        this.folioFin = folioFin;
    }

    public Date getFechaCort() {
        return fechaCort;
    }

    public void setFechaCort(Date fechaCort) {
        this.fechaCort = fechaCort;
    }

    public Integer getRegistro() {
        return registro;
    }

    public void setRegistro(Integer registro) {
        this.registro = registro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

//    public Integer getUsuario() {
//        return usuario;
//    }
//
//    public void setUsuario(Integer usuario) {
//        this.usuario = usuario;
//    }
    public Date getFechaCorrec() {
        return fechaCorrec;
    }

    public void setFechaCorrec(Date fechaCorrec) {
        this.fechaCorrec = fechaCorrec;
    }

    public Integer getUsuarioCorrec() {
        return usuarioCorrec;
    }

    public void setUsuarioCorrec(Integer usuarioCorrec) {
        this.usuarioCorrec = usuarioCorrec;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getNumTomo() {
        return numTomo;
    }

    public void setNumTomo(String numTomo) {
        this.numTomo = numTomo;
    }

    public Date getFechaOto() {
        return fechaOto;
    }

    public void setFechaOto(Date fechaOto) {
        this.fechaOto = fechaOto;
    }

    public Boolean getImpraz() {
        return impraz;
    }

    public void setImpraz(Boolean impraz) {
        this.impraz = impraz;
    }

    public String getEstadoInf() {
        return estadoInf;
    }

    public void setEstadoInf(String estadoInf) {
        this.estadoInf = estadoInf;
    }

    public Boolean getPermod() {
        return permod;
    }

    public void setPermod(Boolean permod) {
        this.permod = permod;
    }

    public Boolean getOrdJud() {
        return ordJud;
    }

    public void setOrdJud(Boolean ordJud) {
        this.ordJud = ordJud;
    }

    public Integer getAnocon() {
        return anocon;
    }

    public void setAnocon(Integer anocon) {
        this.anocon = anocon;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public String getEscritJuicProvResolucion() {
        return escritJuicProvResolucion;
    }

    public void setEscritJuicProvResolucion(String escritJuicProvResolucion) {
        this.escritJuicProvResolucion = escritJuicProvResolucion;
    }

    public Date getFechaRepertorio() {
        return fechaRepertorio;
    }

    public void setFechaRepertorio(Date fechaRepertorio) {
        this.fechaRepertorio = fechaRepertorio;
    }

    public Long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(Long numTramite) {
        this.numTramite = numTramite;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getRazonImpresa() {
        return razonImpresa;
    }

    public void setRazonImpresa(Boolean razonImpresa) {
        this.razonImpresa = razonImpresa;
    }

    public Boolean getInscripcionImpresa() {
        return inscripcionImpresa;
    }

    public void setInscripcionImpresa(Boolean inscripcionImpresa) {
        this.inscripcionImpresa = inscripcionImpresa;
    }

    public String getLindEscrNorte() {
        return lindEscrNorte;
    }

    public void setLindEscrNorte(String lindEscrNorte) {
        this.lindEscrNorte = lindEscrNorte;
    }

    public BigDecimal getLindEscrNorteCon() {
        return lindEscrNorteCon;
    }

    public void setLindEscrNorteCon(BigDecimal lindEscrNorteCon) {
        this.lindEscrNorteCon = lindEscrNorteCon;
    }

    public String getLindEscrEste() {
        return lindEscrEste;
    }

    public void setLindEscrEste(String lindEscrEste) {
        this.lindEscrEste = lindEscrEste;
    }

    public BigDecimal getLindEscrEsteCon() {
        return lindEscrEsteCon;
    }

    public void setLindEscrEsteCon(BigDecimal lindEscrEsteCon) {
        this.lindEscrEsteCon = lindEscrEsteCon;
    }

    public String getLindEscrSur() {
        return lindEscrSur;
    }

    public void setLindEscrSur(String lindEscrSur) {
        this.lindEscrSur = lindEscrSur;
    }

    public BigDecimal getLindEscrSurCon() {
        return lindEscrSurCon;
    }

    public void setLindEscrSurCon(BigDecimal lindEscrSurCon) {
        this.lindEscrSurCon = lindEscrSurCon;
    }

    public String getLindEscrOeste() {
        return lindEscrOeste;
    }

    public void setLindEscrOeste(String lindEscrOeste) {
        this.lindEscrOeste = lindEscrOeste;
    }

    public BigDecimal getLindEscrOesteCon() {
        return lindEscrOesteCon;
    }

    public void setLindEscrOesteCon(BigDecimal lindEscrOesteCon) {
        this.lindEscrOesteCon = lindEscrOesteCon;
    }

    public BigDecimal getAreaTotalEscr() {
        return areaTotalEscr;
    }

    public void setAreaTotalEscr(BigDecimal areaTotalEscr) {
        this.areaTotalEscr = areaTotalEscr;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

    public Integer getNumPaginaRazon() {
        return numPaginaRazon;
    }

    public void setNumPaginaRazon(Integer numPaginaRazon) {
        this.numPaginaRazon = numPaginaRazon;
    }

    public Integer getNumPaginaInscripcion() {
        return numPaginaInscripcion;
    }

    public void setNumPaginaInscripcion(Integer numPaginaInscripcion) {
        this.numPaginaInscripcion = numPaginaInscripcion;
    }

    public Integer getNumPaginasContabilizada() {
        return numPaginasContabilizada;
    }

    public void setNumPaginasContabilizada(Integer numPaginasContabilizada) {
        this.numPaginasContabilizada = numPaginasContabilizada;
    }

    public Integer getFolioAnterior() {
        return folioAnterior;
    }

    public void setFolioAnterior(Integer folioAnterior) {
        this.folioAnterior = folioAnterior;
    }

    public Boolean getEsNegativa() {
        return esNegativa;
    }

    public void setEsNegativa(Boolean esNegativa) {
        this.esNegativa = esNegativa;
    }

    public Boolean getMostrarRelacion() {
        return mostrarRelacion;
    }

    public void setMostrarRelacion(Boolean mostrarRelacion) {
        this.mostrarRelacion = mostrarRelacion;
    }

    public Boolean getIsMercantil() {
        return isMercantil;
    }

    public void setIsMercantil(Boolean isMercantil) {
        this.isMercantil = isMercantil;
    }

    public String getValorUuid() {
        return valorUuid;
    }

    public void setValorUuid(String valorUuid) {
        this.valorUuid = valorUuid;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoRepresentante> getRegMovimientoRepresentanteCollection() {
        return regMovimientoRepresentanteCollection;
    }

    public void setRegMovimientoRepresentanteCollection(Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection) {
        this.regMovimientoRepresentanteCollection = regMovimientoRepresentanteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoCapital> getRegMovimientoCapitalCollection() {
        return regMovimientoCapitalCollection;
    }

    public void setRegMovimientoCapitalCollection(Collection<RegMovimientoCapital> regMovimientoCapitalCollection) {
        this.regMovimientoCapitalCollection = regMovimientoCapitalCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoReferencia> getRegMovimientoReferenciaCollection() {
        return regMovimientoReferenciaCollection;
    }

    public void setRegMovimientoReferenciaCollection(Collection<RegMovimientoReferencia> regMovimientoReferenciaCollection) {
        this.regMovimientoReferenciaCollection = regMovimientoReferenciaCollection;
    }

    public RegLibro getLibro() {
        return libro;
    }

    public void setLibro(RegLibro libro) {
        this.libro = libro;
    }

    public RegEnteJudiciales getEnteJudicial() {
        return enteJudicial;
    }

    public void setEnteJudicial(RegEnteJudiciales enteJudicial) {
        this.enteJudicial = enteJudicial;
    }

    public RegAsocCamaras getAsocCamara() {
        return asocCamara;
    }

    public void setAsocCamara(RegAsocCamaras asocCamara) {
        this.asocCamara = asocCamara;
    }

    public RegActo getActo() {
        return acto;
    }

    public void setActo(RegActo acto) {
        this.acto = acto;
    }

    public CatCanton getCodigoCan() {
        return codigoCan;
    }

    public void setCodigoCan(CatCanton codigoCan) {
        this.codigoCan = codigoCan;
    }

    public AclUser getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(AclUser userCreador) {
        this.userCreador = userCreador;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoFicha> getRegMovimientoFichaCollection() {
        return regMovimientoFichaCollection;
    }

    public void setRegMovimientoFichaCollection(Collection<RegMovimientoFicha> regMovimientoFichaCollection) {
        this.regMovimientoFichaCollection = regMovimientoFichaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoSocios> getRegMovimientoSociosCollection() {
        return regMovimientoSociosCollection;
    }

    public void setRegMovimientoSociosCollection(Collection<RegMovimientoSocios> regMovimientoSociosCollection) {
        this.regMovimientoSociosCollection = regMovimientoSociosCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoCliente> getRegMovimientoClienteCollection() {
        return regMovimientoClienteCollection;
    }

    public void setRegMovimientoClienteCollection(Collection<RegMovimientoCliente> regMovimientoClienteCollection) {
        this.regMovimientoClienteCollection = regMovimientoClienteCollection;
    }

    public RegpCertificadosInscripciones getRegpCertificadoInscripcion() {
        return regpCertificadoInscripcion;
    }

    public void setRegpCertificadoInscripcion(RegpCertificadosInscripciones regpCertificadoInscripcion) {
        this.regpCertificadoInscripcion = regpCertificadoInscripcion;
    }

    public String getObservacionEliminacion() {
        return observacionEliminacion;
    }

    public void setObservacionEliminacion(String observacionEliminacion) {
        this.observacionEliminacion = observacionEliminacion;
    }

    public String getAnexoNegativa() {
        return anexoNegativa;
    }

    public void setAnexoNegativa(String anexoNegativa) {
        this.anexoNegativa = anexoNegativa;
    }

    public String getObsCancela() {
        return obsCancela;
    }

    public void setObsCancela(String obsCancela) {
        this.obsCancela = obsCancela;
    }

    public RegRegistrador getRegistrador() {
        return registrador;
    }

    public void setRegistrador(RegRegistrador registrador) {
        this.registrador = registrador;
    }

    public Boolean getTransferenciaDominio() {
        return transferenciaDominio;
    }

    public void setTransferenciaDominio(Boolean transferenciaDominio) {
        this.transferenciaDominio = transferenciaDominio;
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
        if (!(object instanceof RegMovimiento)) {
            return false;
        }
        RegMovimiento other = (RegMovimiento) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegMovimiento[ id=" + id + " ]";
    }

}
