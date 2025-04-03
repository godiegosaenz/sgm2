/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import com.origami.sgm.entities.AclUser;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.RegActo;
import com.origami.sgm.entities.RegAsocCamaras;
import com.origami.sgm.entities.RegEnteJudiciales;
import com.origami.sgm.entities.RegLibro;
import com.origami.sgm.entities.RegMovimientoCapital;
import com.origami.sgm.entities.RegMovimientoCliente;
import com.origami.sgm.entities.RegMovimientoFicha;
import com.origami.sgm.entities.RegMovimientoReferencia;
import com.origami.sgm.entities.RegMovimientoRepresentante;
import com.origami.sgm.entities.RegMovimientoSocios;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * @author Angel Navarro
 */
public class RegMovimientoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private long numRepertorio;
    private BigInteger numInscripcion;
    private Date fechaInscripcion;
    private BigInteger indice;
    private BigInteger folioInicio;
    private BigInteger folioFin;
    private Date fechaCort;
    private BigInteger registro;
    private String estado;
    private Date fechaIngreso;
    private BigInteger usuario;
    private Date fechaCorrec;
    private BigInteger usuarioCorrec;
    private String observacion;
    private String numTomo;
    private Date fechaOto;
    private Boolean impraz;
    private String estadoInf;
    private Boolean permod;
    private Boolean ordJud;
    private BigInteger anocon;
    private Date fechaResolucion;
    private String escritJuicProvResolucion;
    private Date fechaRepertorio;
    private BigInteger numTramite;
    private String taskId;
    private Boolean razonImpresa;
    private Boolean inscripcionImpresa;
    private String lindEscrNorte;
    private BigDecimal lindEscrNorteCon;
    private String lindEscrEste;
    private BigDecimal lindEscrEsteCon;
    private String lindEscrSur;
    private BigDecimal lindEscrSurCon;
    private String lindEscrOeste;
    private BigDecimal lindEscrOesteCon;
    private BigDecimal areaTotalEscr;
    private BigDecimal alicuota;
    private BigInteger numPaginaRazon;
    private BigInteger numPaginaInscripcion;
    private BigInteger numPaginasContabilizada;
    private BigInteger folioAnterior;
    private Boolean esNegativa;
    private Boolean mostrarRelacion;
    private Boolean isMercantil;
    private String valorUuid;
    private RegLibro libro;
    private RegEnteJudiciales enteJudicial;
    private RegAsocCamaras asocCamara;
    private RegActo acto;
    private CatCanton codigoCan;
    private AclUser userCreador;
    private Collection<RegMovimientoFicha> regMovimientoFichaCollection;
    private Collection<RegMovimientoSocios> regMovimientoSociosCollection;
    private Collection<RegMovimientoCliente> regMovimientoClienteCollection;
    private Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection;
    private Collection<RegMovimientoCapital> regMovimientoCapitalCollection;
    private Collection<RegMovimientoReferencia> regMovimientoReferenciaCollection;

    public RegMovimientoModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(long numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public BigInteger getNumInscripcion() {
        return numInscripcion;
    }

    public void setNumInscripcion(BigInteger numInscripcion) {
        this.numInscripcion = numInscripcion;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public BigInteger getIndice() {
        return indice;
    }

    public void setIndice(BigInteger indice) {
        this.indice = indice;
    }

    public BigInteger getFolioInicio() {
        return folioInicio;
    }

    public void setFolioInicio(BigInteger folioInicio) {
        this.folioInicio = folioInicio;
    }

    public BigInteger getFolioFin() {
        return folioFin;
    }

    public void setFolioFin(BigInteger folioFin) {
        this.folioFin = folioFin;
    }

    public Date getFechaCort() {
        return fechaCort;
    }

    public void setFechaCort(Date fechaCort) {
        this.fechaCort = fechaCort;
    }

    public BigInteger getRegistro() {
        return registro;
    }

    public void setRegistro(BigInteger registro) {
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

    public BigInteger getUsuario() {
        return usuario;
    }

    public void setUsuario(BigInteger usuario) {
        this.usuario = usuario;
    }

    public Date getFechaCorrec() {
        return fechaCorrec;
    }

    public void setFechaCorrec(Date fechaCorrec) {
        this.fechaCorrec = fechaCorrec;
    }

    public BigInteger getUsuarioCorrec() {
        return usuarioCorrec;
    }

    public void setUsuarioCorrec(BigInteger usuarioCorrec) {
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

    public BigInteger getAnocon() {
        return anocon;
    }

    public void setAnocon(BigInteger anocon) {
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

    public BigInteger getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(BigInteger numTramite) {
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

    public BigInteger getNumPaginaRazon() {
        return numPaginaRazon;
    }

    public void setNumPaginaRazon(BigInteger numPaginaRazon) {
        this.numPaginaRazon = numPaginaRazon;
    }

    public BigInteger getNumPaginaInscripcion() {
        return numPaginaInscripcion;
    }

    public void setNumPaginaInscripcion(BigInteger numPaginaInscripcion) {
        this.numPaginaInscripcion = numPaginaInscripcion;
    }

    public BigInteger getNumPaginasContabilizada() {
        return numPaginasContabilizada;
    }

    public void setNumPaginasContabilizada(BigInteger numPaginasContabilizada) {
        this.numPaginasContabilizada = numPaginasContabilizada;
    }

    public BigInteger getFolioAnterior() {
        return folioAnterior;
    }

    public void setFolioAnterior(BigInteger folioAnterior) {
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

    public Collection<RegMovimientoRepresentante> getRegMovimientoRepresentanteCollection() {
        return regMovimientoRepresentanteCollection;
    }

    public void setRegMovimientoRepresentanteCollection(Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection) {
        this.regMovimientoRepresentanteCollection = regMovimientoRepresentanteCollection;
    }

    public Collection<RegMovimientoCapital> getRegMovimientoCapitalCollection() {
        return regMovimientoCapitalCollection;
    }

    public void setRegMovimientoCapitalCollection(Collection<RegMovimientoCapital> regMovimientoCapitalCollection) {
        this.regMovimientoCapitalCollection = regMovimientoCapitalCollection;
    }

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

}
