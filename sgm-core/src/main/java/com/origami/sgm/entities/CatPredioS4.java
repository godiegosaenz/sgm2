/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_predio_s4", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"predio"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioS4.findAll", query = "SELECT c FROM CatPredioS4 c")
    ,
    @NamedQuery(name = "CatPredioS4.findById", query = "SELECT c FROM CatPredioS4 c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPredioS4.findByIdPredio", query = "SELECT c FROM CatPredioS4 c WHERE c.predio.id = :idPredio")
    ,
    @NamedQuery(name = "CatPredioS4.findByFrente1", query = "SELECT c FROM CatPredioS4 c WHERE c.frente1 = :frente1")
    ,
    @NamedQuery(name = "CatPredioS4.findByFrente2", query = "SELECT c FROM CatPredioS4 c WHERE c.frente2 = :frente2")
    ,
    @NamedQuery(name = "CatPredioS4.findByFrente3", query = "SELECT c FROM CatPredioS4 c WHERE c.frente3 = :frente3")
    ,
    @NamedQuery(name = "CatPredioS4.findByFrente4", query = "SELECT c FROM CatPredioS4 c WHERE c.frente4 = :frente4")
    ,
    @NamedQuery(name = "CatPredioS4.findByFrenteTotal", query = "SELECT c FROM CatPredioS4 c WHERE c.frenteTotal = :frenteTotal")
    ,
    @NamedQuery(name = "CatPredioS4.findByFondo1", query = "SELECT c FROM CatPredioS4 c WHERE c.fondo1 = :fondo1")
    ,
    @NamedQuery(name = "CatPredioS4.findByFondo2", query = "SELECT c FROM CatPredioS4 c WHERE c.fondo2 = :fondo2")
    ,
    @NamedQuery(name = "CatPredioS4.findByAreaCalculada", query = "SELECT c FROM CatPredioS4 c WHERE c.areaCalculada = :areaCalculada")
    ,
    @NamedQuery(name = "CatPredioS4.findByNumHombres", query = "SELECT c FROM CatPredioS4 c WHERE c.numHombres = :numHombres")
    ,
    @NamedQuery(name = "CatPredioS4.findByNumMujeres", query = "SELECT c FROM CatPredioS4 c WHERE c.numMujeres = :numMujeres")
    ,
    @NamedQuery(name = "CatPredioS4.findByNumAdultos", query = "SELECT c FROM CatPredioS4 c WHERE c.numAdultos = :numAdultos")
    ,
    @NamedQuery(name = "CatPredioS4.findByNumNinos", query = "SELECT c FROM CatPredioS4 c WHERE c.numNinos = :numNinos")})
public class CatPredioS4 implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "frente1", precision = 10, scale = 2)
    @Expose
    private BigDecimal frente1;
    @Column(name = "frente2", precision = 10, scale = 2)
    @Expose
    private BigDecimal frente2;
    @Column(name = "frente3", precision = 10, scale = 2)
    @Expose
    private BigDecimal frente3;
    @Column(name = "frente4", precision = 10, scale = 2)
    @Expose
    private BigDecimal frente4;
    @Column(name = "superficie", precision = 10, scale = 2)
    @Expose
    private BigDecimal superfice;
    @Column(name = "frente_total", precision = 10, scale = 2)
    @Expose
    private BigDecimal frenteTotal;
    @Column(name = "fondo1", precision = 10, scale = 2)
    @Expose
    private BigDecimal fondo1;
    @Column(name = "fondo2", precision = 10, scale = 2)
    @Expose
    private BigDecimal fondo2;
    @Column(name = "area_calculada", precision = 15, scale = 2)
    @Expose
    private BigDecimal areaCalculada;
    @Column(name = "num_hombres")
    @Expose
    private Short numHombres;
    @Column(name = "num_mujeres")
    @Expose
    private Short numMujeres;
    @Column(name = "num_adultos")
    @Expose
    private Short numAdultos;
    @Column(name = "num_ninos")
    @Expose
    private Short numNinos;
    @Column(name = "area_grafica_lote", precision = 10, scale = 3)
    @Expose
    private BigDecimal areaGraficaLote;
    @JoinTable(name = "cat_predio_s4_has_accesibilidad", joinColumns = {
        @JoinColumn(name = "predio_s4", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "accesibilidad_ctlg", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<CtlgItem> accesibilidadList;
    @JoinColumn(name = "topografia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem topografia;
    @JoinColumn(name = "tipo_suelo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipoSuelo;
    @JoinColumn(name = "loc_manzana", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem locManzana;
    @JoinColumn(name = "cerramiento_ctlg", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem cerramientoCtlg;
    @JoinColumn(name = "estado_solar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem estadoSolar;
    @JoinColumn(name = "tipo_obra_mejora", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipoObraMejora;
    @JoinColumn(name = "material_mejora", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem materialMejora;
    @JoinColumn(name = "estado_mejora", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem estadoMejora;
    @Column(name = "area_mejora", precision = 10, scale = 6)
    @Expose
    private BigDecimal areaMejora;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;

    @JoinColumn(name = "cobertura_predominante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem coberturaPredominante;
    @JoinColumn(name = "ecosistema_relevante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem ecosistemaRelevante;
    @JoinColumn(name = "riesgo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem riesgo;
    @JoinColumn(name = "erosion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem erosion;
    @JoinColumn(name = "drenaje", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem drenaje;

    @Column(name = "tiene_hipoteca")
    @Expose
    private Boolean tieneHipoteca;
    @Column(name = "inst_financiera_hip")
    @Expose
    private String instFinancieraHip;
    @Column(name = "lote_en_conflicto")
    @Expose
    private Boolean loteEnConflicto;
    @Column(name = "opbserv_lote_en_conflicto")
    @Expose
    private String opbservLoteEnConflicto;
    @Column(name = "tiene_permiso_const")
    @Expose
    private Boolean tienePermisoConst;
    @Column(name = "tiene_adosamiento")
    @Expose
    private Boolean tieneAdosamiento;
    @Column(name = "tiene_retiros")
    @Expose
    private Boolean tieneRetiros;
    @JoinColumn(name = "afectacion_lote", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem afectacionLote;


    public CatPredioS4() {
    }

    public CatPredioS4(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getFrente1() {
        return frente1;
    }

    public void setFrente1(BigDecimal frente1) {
        this.frente1 = frente1;
    }

    public BigDecimal getFrente2() {
        return frente2;
    }

    public void setFrente2(BigDecimal frente2) {
        this.frente2 = frente2;
    }

    public BigDecimal getFrente3() {
        return frente3;
    }

    public void setFrente3(BigDecimal frente3) {
        this.frente3 = frente3;
    }

    public BigDecimal getFrente4() {
        return frente4;
    }

    public void setFrente4(BigDecimal frente4) {
        this.frente4 = frente4;
    }

    public BigDecimal getFrenteTotal() {
        return frenteTotal;
    }

    public void setFrenteTotal(BigDecimal frenteTotal) {
        this.frenteTotal = frenteTotal;
    }

    public BigDecimal getFondo1() {
        return fondo1;
    }

    public void setFondo1(BigDecimal fondo1) {
        this.fondo1 = fondo1;
    }

    public BigDecimal getFondo2() {
        return fondo2;
    }

    public void setFondo2(BigDecimal fondo2) {
        this.fondo2 = fondo2;
    }

    public BigDecimal getAreaCalculada() {
        return areaCalculada;
    }

    public void setAreaCalculada(BigDecimal areaCalculada) {
        this.areaCalculada = areaCalculada;
    }

    public Short getNumHombres() {
        return numHombres;
    }

    public void setNumHombres(Short numHombres) {
        this.numHombres = numHombres;
    }

    public Short getNumMujeres() {
        return numMujeres;
    }

    public void setNumMujeres(Short numMujeres) {
        this.numMujeres = numMujeres;
    }

    public Short getNumAdultos() {
        return numAdultos;
    }

    public void setNumAdultos(Short numAdultos) {
        this.numAdultos = numAdultos;
    }

    public Short getNumNinos() {
        return numNinos;
    }

    public void setNumNinos(Short numNinos) {
        this.numNinos = numNinos;
    }

    public CtlgItem getTopografia() {
        return topografia;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CtlgItem> getAccesibilidadList() {
        return accesibilidadList;
    }

    public void setAccesibilidadList(Collection<CtlgItem> accesibilidadList) {
        this.accesibilidadList = accesibilidadList;
    }

    public void setTopografia(CtlgItem topografia) {
        this.topografia = topografia;
    }

    public CtlgItem getTipoSuelo() {
        return tipoSuelo;
    }

    public void setTipoSuelo(CtlgItem tipoSuelo) {
        this.tipoSuelo = tipoSuelo;
    }

    public CtlgItem getLocManzana() {
        return locManzana;
    }

    public void setLocManzana(CtlgItem locManzana) {
        this.locManzana = locManzana;
    }

    public CtlgItem getCerramientoCtlg() {
        return cerramientoCtlg;
    }

    public void setCerramientoCtlg(CtlgItem cerramientoCtlg) {
        this.cerramientoCtlg = cerramientoCtlg;
    }

    public CtlgItem getEstadoSolar() {
        return estadoSolar;
    }

    public void setEstadoSolar(CtlgItem estadoSolar) {
        this.estadoSolar = estadoSolar;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CtlgItem getCoberturaPredominante() {
        return coberturaPredominante;
    }

    public void setCoberturaPredominante(CtlgItem coberturaPredominante) {
        this.coberturaPredominante = coberturaPredominante;
    }

    public CtlgItem getEcosistemaRelevante() {
        return ecosistemaRelevante;
    }

    public void setEcosistemaRelevante(CtlgItem ecosistemaRelevante) {
        this.ecosistemaRelevante = ecosistemaRelevante;
    }

    public CtlgItem getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(CtlgItem riesgo) {
        this.riesgo = riesgo;
    }

    public CtlgItem getErosion() {
        return erosion;
    }

    public void setErosion(CtlgItem erosion) {
        this.erosion = erosion;
    }

    public CtlgItem getDrenaje() {
        return drenaje;
    }

    public void setDrenaje(CtlgItem drenaje) {
        this.drenaje = drenaje;
    }

    public BigDecimal getAreaGraficaLote() {
        return areaGraficaLote;
    }

    public void setAreaGraficaLote(BigDecimal areaGraficaLote) {
        this.areaGraficaLote = areaGraficaLote;
    }

    public CtlgItem getTipoObraMejora() {
        return tipoObraMejora;
    }

    public void setTipoObraMejora(CtlgItem tipoObraMejora) {
        this.tipoObraMejora = tipoObraMejora;
    }

    public CtlgItem getMaterialMejora() {
        return materialMejora;
    }

    public void setMaterialMejora(CtlgItem materialMejora) {
        this.materialMejora = materialMejora;
    }

    public CtlgItem getEstadoMejora() {
        return estadoMejora;
    }

    public void setEstadoMejora(CtlgItem estadoMejora) {
        this.estadoMejora = estadoMejora;
    }

    public BigDecimal getAreaMejora() {
        return areaMejora;
    }

    public void setAreaMejora(BigDecimal areaMejora) {
        this.areaMejora = areaMejora;
    }

    public Boolean getTieneHipoteca() {
        return tieneHipoteca;
    }

    public void setTieneHipoteca(Boolean tieneHipoteca) {
        this.tieneHipoteca = tieneHipoteca;
    }

    public String getInstFinancieraHip() {
        return instFinancieraHip;
    }

    public void setInstFinancieraHip(String instFinancieraHip) {
        this.instFinancieraHip = instFinancieraHip;
    }

    public Boolean getLoteEnConflicto() {
        return loteEnConflicto;
    }

    public void setLoteEnConflicto(Boolean loteEnConflicto) {
        this.loteEnConflicto = loteEnConflicto;
    }

    public String getOpbservLoteEnConflicto() {
        return opbservLoteEnConflicto;
    }

    public void setOpbservLoteEnConflicto(String opbservLoteEnConflicto) {
        this.opbservLoteEnConflicto = opbservLoteEnConflicto;
    }

    public Boolean getTienePermisoConst() {
        return tienePermisoConst;
    }

    public void setTienePermisoConst(Boolean tienePermisoConst) {
        this.tienePermisoConst = tienePermisoConst;
    }

    public Boolean getTieneAdosamiento() {
        return tieneAdosamiento;
    }

    public void setTieneAdosamiento(Boolean tieneAdosamiento) {
        this.tieneAdosamiento = tieneAdosamiento;
    }

    public Boolean getTieneRetiros() {
        return tieneRetiros;
    }

    public void setTieneRetiros(Boolean tieneRetiros) {
        this.tieneRetiros = tieneRetiros;
    }

    public CtlgItem getAfectacionLote() {
        return afectacionLote;
    }

    public void setAfectacionLote(CtlgItem afectacionLote) {
        this.afectacionLote = afectacionLote;
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
        if (!(object instanceof CatPredioS4)) {
            return false;
        }
        CatPredioS4 other = (CatPredioS4) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPredioS4[ id=" + id + " ]";
    }

    public BigDecimal getSuperfice() {
        return superfice;
    }

    public void setSuperfice(BigDecimal superfice) {
        this.superfice = superfice;
    }


    
}
