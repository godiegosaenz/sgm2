/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatCanton;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_predio_s6", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"predio"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioS6.findByIdPredio", query = "SELECT c FROM CatPredioS6 c WHERE c.predio.id = :idPredio")})
public class CatPredioS6 implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Column(name = "tiene_agua_potable")
    @Expose
    private Boolean tieneAguaPotable = Boolean.FALSE;
    @Column(name = "num_medidores_agua")
    @Expose
    private Short numMedidoresAgua;
    @Column(name = "tiene_alcantarillado")
    @Expose
    private Boolean tieneAlcantarillado;
    @Column(name = "alcantarillado_pluvial")
    @Expose
    private Boolean alcantarilladoPluvial;
    @Column(name = "tiene_electricidad")
    @Expose
    private Boolean tieneElectricidad = Boolean.FALSE;

    @Column(name = "num_med_elect")
    @Expose
    private Short numMedElect;
    @Column(name = "tiene_internet")
    @Expose
    private Boolean tieneInternet;
    @Column(name = "tiene_telf_fijo")
    @Expose
    private Boolean tieneTelfFijo;
    @Column(name = "cobertura_celular")
    @Expose
    private Boolean coberturaCelular;
    @Size(max = 255)
    @Column(name = "telf_fijo", length = 255)
    @Expose
    private String telfFijo;
    @Column(name = "tiene_aceras")
    @Expose
    private Boolean tieneAceras;
    @Column(name = "alumbrado")
    @Expose
    private Boolean alumbrado;
    @Column(name = "aseo_calles")
    @Expose
    private Boolean aseoCalles;
    @Column(name = "tv_pag")
    @Expose
    private Boolean tvPag;
    @Column(name = "medidor_ee")
    @Expose
    private Boolean medidorEe;
    @Column(name = "tpublico")
    @Expose
    private Boolean tpublico;
    @Column(name = "tiene_bordillo")
    @Expose
    private Boolean tieneBordillo;
    @Column(name = "tiene_cunetas")
    @Expose
    private Boolean tieneCunetas;

    @Column(name = "recoleccion_basura")
    @Expose
    private Boolean recoleccionbasura = Boolean.FALSE;
    @Column(name = "telefonia_satelital")
    @Expose
    private Boolean telefoniasatelital;
    @Column(name = "alarmas_comunitarias")
    @Expose
    private Boolean alarmas_comunitarias;

    @Column(name = "notaria")
    @Size(max = 250)
    @Expose
    private String notaria;
    @Column(name = "fecha_escritura")
    @Temporal(TemporalType.DATE)
    private Date fechaEscritura;
    @Column(name = "fecha_inscripcion")
    @Temporal(TemporalType.DATE)
    private Date fechaInscripcion;
    @Column(name = "num_registro")
    @Expose
    private BigInteger numRegistro;
    @Column(name = "num_repertorio")
    private Integer numRepertorio;
    @Column(name = "folio_desde")
    @Expose
    private Integer folioDesde;
    @Column(name = "folio_hasta")
    @Expose
    private Integer folioHasta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_solar", precision = 14, scale = 4)
    @Expose
    private BigDecimal areaSolar;
    @Column(name = "area_cons", precision = 14, scale = 4)
    @Expose
    private BigDecimal areaCons;
    @Column(name = "alicuota", precision = 12, scale = 3)
    @Expose
    private BigDecimal alicuota;
    @Size(max = 45)
    @Column(name = "lind_lev_norte", length = 45)
    private String lindLevNorte;
    @Column(name = "lind_lev_norte_con", precision = 10, scale = 2)
    private BigDecimal lindLevNorteCon;
    @Size(max = 45)
    @Column(name = "lind_lev_sur", length = 45)
    @Expose
    private String lindLevSur;
    @Column(name = "lind_lev_sur_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindLevSurCon;
    @Size(max = 45)
    @Column(name = "lind_lev_este", length = 45)
    @Expose
    private String lindLevEste;
    @Column(name = "lind_lev_este_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindLevEsteCon;
    @Size(max = 45)
    @Column(name = "lind_lev_oeste", length = 45)
    private String lindLevOeste;
    @Column(name = "lind_lev_oeste_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindLevOesteCon;
    @Size(max = 45)
    @Column(name = "lind_esc_norte", length = 45)
    @Expose
    private String lindEscNorte;
    @Column(name = "lind_esc_norte_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindEscNorteCon;
    @Size(max = 45)
    @Column(name = "lind_esc_sur", length = 45)
    @Expose
    private String lindEscSur;
    @Column(name = "lind_esc_sur_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindEscSurCon;
    @Size(max = 45)
    @Column(name = "lind_esc_este", length = 45)
    @Expose
    private String lindEscEste;
    @Column(name = "lind_esc_este_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindEscEsteCon;
    @Size(max = 45)
    @Column(name = "lind_esc_oeste", length = 45)
    @Expose
    private String lindEscOeste;
    @Column(name = "lind_esc_oeste_con", precision = 10, scale = 2)
    @Expose
    private BigDecimal lindEscOesteCon;
    @Size(max = 40)
    @Column(name = "folio_desde_cad", length = 40)
    private String folioDesdeCad;
    @Size(max = 40)
    @Column(name = "folio_hasta_cad", length = 40)
    private String folioHastaCad;
    @JoinTable(name = "cat_predio_s6_has_vias", joinColumns = {
        @JoinColumn(name = "predio_s6", updatable = true, referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "ctlg_item", updatable = true, referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    @Expose
    private Collection<CtlgItem> ctlgItemCollection;
    @JoinTable(name = "cat_predio_s6_has_instalacion_especial", joinColumns = {
        @JoinColumn(name = "predio_s6", updatable = true, referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "ctlg_item", updatable = true, referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    @Expose
    private Collection<CtlgItem> ctlgItemCollectionInstalacionEspecial;
    @JoinColumn(name = "abast_agua_proviene", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem abastAgua;
    @JoinColumn(name = "abas_agua_recibe", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem abastAguaRecibe;
    @JoinColumn(name = "recol_basura", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem recolBasura;
    @JoinColumn(name = "evac_aguas_serv", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem evacAguasServ;
    @JoinColumn(name = "abaste_electrico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem abasteElectrico;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "canton", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCanton canton;
    @Column(name = "medidor_agua", length = 45)
    @Expose
    private String medidorAgua;
    @Column(name = "medidor_electrico", length = 45)
    @Expose
    private String medidorElectrico;
    
    public CatPredioS6() {
    }

    public CatPredioS6(Long id) {
        this.id = id;
    }

    public CatPredioS6(Long id, Boolean tieneAceras, Boolean tieneBordillo, Boolean tieneCunetas) {
        this.id = id;
        this.tieneAceras = tieneAceras;
        this.tieneBordillo = tieneBordillo;
        this.tieneCunetas = tieneCunetas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getTieneAguaPotable() {
        return tieneAguaPotable;
    }

    public void setTieneAguaPotable(Boolean tieneAguaPotable) {
        this.tieneAguaPotable = tieneAguaPotable;
    }

    public Short getNumMedidoresAgua() {
        return numMedidoresAgua;
    }

    public void setNumMedidoresAgua(Short numMedidoresAgua) {
        this.numMedidoresAgua = numMedidoresAgua;
    }

    public Boolean getTieneAlcantarillado() {
        return tieneAlcantarillado;
    }

    public void setTieneAlcantarillado(Boolean tieneAlcantarillado) {
        this.tieneAlcantarillado = tieneAlcantarillado;
    }

    public Boolean getAlcantarilladoPluvial() {
        return alcantarilladoPluvial;
    }

    public void setAlcantarilladoPluvial(Boolean alcantarilladoPluvial) {
        this.alcantarilladoPluvial = alcantarilladoPluvial;
    }

    public Boolean getTieneElectricidad() {
        return tieneElectricidad;
    }

    public void setTieneElectricidad(Boolean tieneElectricidad) {
        this.tieneElectricidad = tieneElectricidad;
    }

    public Short getNumMedElect() {
        return numMedElect;
    }

    public void setNumMedElect(Short numMedElect) {
        this.numMedElect = numMedElect;
    }

    public Boolean getTieneInternet() {
        return tieneInternet;
    }

    public void setTieneInternet(Boolean tieneInternet) {
        this.tieneInternet = tieneInternet;
    }

    public Boolean getTieneTelfFijo() {
        return tieneTelfFijo;
    }

    public void setTieneTelfFijo(Boolean tieneTelfFijo) {
        this.tieneTelfFijo = tieneTelfFijo;
    }

    public String getTelfFijo() {
        return telfFijo;
    }

    public void setTelfFijo(String telfFijo) {
        this.telfFijo = telfFijo;
    }

    public Boolean getTieneAceras() {
        return tieneAceras;
    }

    public void setTieneAceras(Boolean tieneAceras) {
        this.tieneAceras = tieneAceras;
    }

    public Boolean getAlumbrado() {
        return alumbrado;
    }

    public void setAlumbrado(Boolean alumbrado) {
        this.alumbrado = alumbrado;
    }

    public Boolean getAseoCalles() {
        return aseoCalles;
    }

    public void setAseoCalles(Boolean aseoCalles) {
        this.aseoCalles = aseoCalles;
    }

    public Boolean getTvPag() {
        return tvPag;
    }

    public void setTvPag(Boolean tvPag) {
        this.tvPag = tvPag;
    }

    public Boolean getMedidorEe() {
        return medidorEe;
    }

    public void setMedidorEe(Boolean medidorEe) {
        this.medidorEe = medidorEe;
    }

    public Boolean getTpublico() {
        return tpublico;
    }

    public void setTpublico(Boolean tpublico) {
        this.tpublico = tpublico;
    }

    public Boolean getTieneBordillo() {
        return tieneBordillo;
    }

    public void setTieneBordillo(Boolean tieneBordillo) {
        this.tieneBordillo = tieneBordillo;
    }

    public Boolean getTieneCunetas() {
        return tieneCunetas;
    }

    public void setTieneCunetas(Boolean tieneCunetas) {
        this.tieneCunetas = tieneCunetas;
    }

    public Boolean getRecoleccionbasura() {
        return recoleccionbasura;
    }

    public void setRecoleccionbasura(Boolean recoleccionbasura) {
        this.recoleccionbasura = recoleccionbasura;
    }

    public Boolean getTelefoniasatelital() {
        return telefoniasatelital;
    }

    public void setTelefoniasatelital(Boolean telefoniasatelital) {
        this.telefoniasatelital = telefoniasatelital;
    }

    public Boolean getAlarmas_comunitarias() {
        return alarmas_comunitarias;
    }

    public void setAlarmas_comunitarias(Boolean alarmas_comunitarias) {
        this.alarmas_comunitarias = alarmas_comunitarias;
    }

    public String getNotaria() {
        return notaria;
    }

    public void setNotaria(String notaria) {
        this.notaria = notaria;
    }

    public Date getFechaEscritura() {
        return fechaEscritura;
    }

    public void setFechaEscritura(Date fechaEscritura) {
        this.fechaEscritura = fechaEscritura;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public BigInteger getNumRegistro() {
        return numRegistro;
    }

    public void setNumRegistro(BigInteger numRegistro) {
        this.numRegistro = numRegistro;
    }

    public Integer getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(Integer numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public Integer getFolioDesde() {
        return folioDesde;
    }

    public void setFolioDesde(Integer folioDesde) {
        this.folioDesde = folioDesde;
    }

    public Integer getFolioHasta() {
        return folioHasta;
    }

    public void setFolioHasta(Integer folioHasta) {
        this.folioHasta = folioHasta;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAreaCons() {
        return areaCons;
    }

    public void setAreaCons(BigDecimal areaCons) {
        this.areaCons = areaCons;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

    public String getLindLevNorte() {
        return lindLevNorte;
    }

    public void setLindLevNorte(String lindLevNorte) {
        this.lindLevNorte = lindLevNorte;
    }

    public BigDecimal getLindLevNorteCon() {
        return lindLevNorteCon;
    }

    public void setLindLevNorteCon(BigDecimal lindLevNorteCon) {
        this.lindLevNorteCon = lindLevNorteCon;
    }

    public String getLindLevSur() {
        return lindLevSur;
    }

    public void setLindLevSur(String lindLevSur) {
        this.lindLevSur = lindLevSur;
    }

    public BigDecimal getLindLevSurCon() {
        return lindLevSurCon;
    }

    public void setLindLevSurCon(BigDecimal lindLevSurCon) {
        this.lindLevSurCon = lindLevSurCon;
    }

    public String getLindLevEste() {
        return lindLevEste;
    }

    public void setLindLevEste(String lindLevEste) {
        this.lindLevEste = lindLevEste;
    }

    public BigDecimal getLindLevEsteCon() {
        return lindLevEsteCon;
    }

    public void setLindLevEsteCon(BigDecimal lindLevEsteCon) {
        this.lindLevEsteCon = lindLevEsteCon;
    }

    public String getLindLevOeste() {
        return lindLevOeste;
    }

    public void setLindLevOeste(String lindLevOeste) {
        this.lindLevOeste = lindLevOeste;
    }

    public BigDecimal getLindLevOesteCon() {
        return lindLevOesteCon;
    }

    public void setLindLevOesteCon(BigDecimal lindLevOesteCon) {
        this.lindLevOesteCon = lindLevOesteCon;
    }

    public String getLindEscNorte() {
        return lindEscNorte;
    }

    public void setLindEscNorte(String lindEscNorte) {
        this.lindEscNorte = lindEscNorte;
    }

    public BigDecimal getLindEscNorteCon() {
        return lindEscNorteCon;
    }

    public void setLindEscNorteCon(BigDecimal lindEscNorteCon) {
        this.lindEscNorteCon = lindEscNorteCon;
    }

    public String getLindEscSur() {
        return lindEscSur;
    }

    public void setLindEscSur(String lindEscSur) {
        this.lindEscSur = lindEscSur;
    }

    public BigDecimal getLindEscSurCon() {
        return lindEscSurCon;
    }

    public void setLindEscSurCon(BigDecimal lindEscSurCon) {
        this.lindEscSurCon = lindEscSurCon;
    }

    public String getLindEscEste() {
        return lindEscEste;
    }

    public void setLindEscEste(String lindEscEste) {
        this.lindEscEste = lindEscEste;
    }

    public BigDecimal getLindEscEsteCon() {
        return lindEscEsteCon;
    }

    public void setLindEscEsteCon(BigDecimal lindEscEsteCon) {
        this.lindEscEsteCon = lindEscEsteCon;
    }

    public String getLindEscOeste() {
        return lindEscOeste;
    }

    public void setLindEscOeste(String lindEscOeste) {
        this.lindEscOeste = lindEscOeste;
    }

    public BigDecimal getLindEscOesteCon() {
        return lindEscOesteCon;
    }

    public void setLindEscOesteCon(BigDecimal lindEscOesteCon) {
        this.lindEscOesteCon = lindEscOesteCon;
    }

    public String getFolioDesdeCad() {
        return folioDesdeCad;
    }

    public void setFolioDesdeCad(String folioDesdeCad) {
        this.folioDesdeCad = folioDesdeCad;
    }

    public String getFolioHastaCad() {
        return folioHastaCad;
    }

    public void setFolioHastaCad(String folioHastaCad) {
        this.folioHastaCad = folioHastaCad;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CtlgItem> getCtlgItemCollection() {
        return ctlgItemCollection;
    }

    public void setCtlgItemCollection(Collection<CtlgItem> ctlgItemCollection) {
        this.ctlgItemCollection = ctlgItemCollection;
    }

    public CtlgItem getAbastAgua() {
        return abastAgua;
    }

    public void setAbastAgua(CtlgItem abastAgua) {
        this.abastAgua = abastAgua;
    }

    public CtlgItem getEvacAguasServ() {
        return evacAguasServ;
    }

    public void setEvacAguasServ(CtlgItem evacAguasServ) {
        this.evacAguasServ = evacAguasServ;
    }

    public CtlgItem getAbasteElectrico() {
        return abasteElectrico;
    }

    public void setAbasteElectrico(CtlgItem abasteElectrico) {
        this.abasteElectrico = abasteElectrico;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public String getMedidorAgua() {
        return medidorAgua;
    }

    public void setMedidorAgua(String medidorAgua) {
        this.medidorAgua = medidorAgua;
    }

    public String getMedidorElectrico() {
        return medidorElectrico;
    }

    public void setMedidorElectrico(String medidorElectrico) {
        this.medidorElectrico = medidorElectrico;
    }

    public CtlgItem getAbastAguaRecibe() {
        return abastAguaRecibe;
    }

    public void setAbastAguaRecibe(CtlgItem abastAguaRecibe) {
        this.abastAguaRecibe = abastAguaRecibe;
    }

    public CtlgItem getRecolBasura() {
        return recolBasura;
    }

    public void setRecolBasura(CtlgItem recolBasura) {
        this.recolBasura = recolBasura;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CtlgItem> getCtlgItemCollectionInstalacionEspecial() {
        return ctlgItemCollectionInstalacionEspecial;
    }

    public void setCtlgItemCollectionInstalacionEspecial(Collection<CtlgItem> ctlgItemCollectionInstalacionEspecial) {
        this.ctlgItemCollectionInstalacionEspecial = ctlgItemCollectionInstalacionEspecial;
    }

    public Boolean getCoberturaCelular() {
        return coberturaCelular;
    }

    public void setCoberturaCelular(Boolean coberturaCelular) {
        this.coberturaCelular = coberturaCelular;
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
        if (!(object instanceof CatPredioS6)) {
            return false;
        }
        CatPredioS6 other = (CatPredioS6) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPredioS6[ id=" + id + " ]";
    }

}
