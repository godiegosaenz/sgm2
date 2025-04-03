/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.historic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "valoracion_predial", schema = SchemasConfig.HISTORICO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ValoracionPredial.findAll", query = "SELECT v FROM ValoracionPredial v")
    , @NamedQuery(name = "ValoracionPredial.findById", query = "SELECT v FROM ValoracionPredial v WHERE v.id = :id")
    , @NamedQuery(name = "ValoracionPredial.findByNumVersion", query = "SELECT v FROM ValoracionPredial v WHERE v.numVersion = :numVersion")
    , @NamedQuery(name = "ValoracionPredial.findByPeriodo", query = "SELECT v FROM ValoracionPredial v WHERE v.periodo = :periodo")
    , @NamedQuery(name = "ValoracionPredial.findByNumPredio", query = "SELECT v FROM ValoracionPredial v WHERE v.numPredio = :numPredio")
    , @NamedQuery(name = "ValoracionPredial.findByClaveMz", query = "SELECT v FROM ValoracionPredial v WHERE v.claveMz = :claveMz")
    , @NamedQuery(name = "ValoracionPredial.findByCodigoCatastral", query = "SELECT v FROM ValoracionPredial v WHERE v.codigoCatastral = :codigoCatastral")
    , @NamedQuery(name = "ValoracionPredial.findByMatriz", query = "SELECT v FROM ValoracionPredial v WHERE v.matriz = :matriz")
    , @NamedQuery(name = "ValoracionPredial.findByNumeroMatriz", query = "SELECT v FROM ValoracionPredial v WHERE v.numeroMatriz = :numeroMatriz")
    , @NamedQuery(name = "ValoracionPredial.findByAreaSolar", query = "SELECT v FROM ValoracionPredial v WHERE v.areaSolar = :areaSolar")
    , @NamedQuery(name = "ValoracionPredial.findByAreaSolarTipo", query = "SELECT v FROM ValoracionPredial v WHERE v.areaSolarTipo = :areaSolarTipo")
    , @NamedQuery(name = "ValoracionPredial.findByFrenteSolar", query = "SELECT v FROM ValoracionPredial v WHERE v.frenteSolar = :frenteSolar")
    , @NamedQuery(name = "ValoracionPredial.findByFrenteSolarTipo", query = "SELECT v FROM ValoracionPredial v WHERE v.frenteSolarTipo = :frenteSolarTipo")
    , @NamedQuery(name = "ValoracionPredial.findByCantServicios", query = "SELECT v FROM ValoracionPredial v WHERE v.cantServicios = :cantServicios")
    , @NamedQuery(name = "ValoracionPredial.findBySubsector", query = "SELECT v FROM ValoracionPredial v WHERE v.subsector = :subsector")
    , @NamedQuery(name = "ValoracionPredial.findByValorM2Subsector", query = "SELECT v FROM ValoracionPredial v WHERE v.valorM2Subsector = :valorM2Subsector")
    , @NamedQuery(name = "ValoracionPredial.findByAlfa", query = "SELECT v FROM ValoracionPredial v WHERE v.alfa = :alfa")
    , @NamedQuery(name = "ValoracionPredial.findByBeta", query = "SELECT v FROM ValoracionPredial v WHERE v.beta = :beta")
    , @NamedQuery(name = "ValoracionPredial.findByY", query = "SELECT v FROM ValoracionPredial v WHERE v.y = :y")
    , @NamedQuery(name = "ValoracionPredial.findByFactExt", query = "SELECT v FROM ValoracionPredial v WHERE v.factExt = :factExt")
    , @NamedQuery(name = "ValoracionPredial.findByFactFrente", query = "SELECT v FROM ValoracionPredial v WHERE v.factFrente = :factFrente")
    , @NamedQuery(name = "ValoracionPredial.findByFactGeometrico", query = "SELECT v FROM ValoracionPredial v WHERE v.factGeometrico = :factGeometrico")
    , @NamedQuery(name = "ValoracionPredial.findByFactServicio", query = "SELECT v FROM ValoracionPredial v WHERE v.factServicio = :factServicio")
    , @NamedQuery(name = "ValoracionPredial.findByFactCorrelacion", query = "SELECT v FROM ValoracionPredial v WHERE v.factCorrelacion = :factCorrelacion")
    , @NamedQuery(name = "ValoracionPredial.findByBandaImpositiva", query = "SELECT v FROM ValoracionPredial v WHERE v.bandaImpositiva = :bandaImpositiva")
    , @NamedQuery(name = "ValoracionPredial.findByIpAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.ipAnt = :ipAnt")
    , @NamedQuery(name = "ValoracionPredial.findByIpAct", query = "SELECT v FROM ValoracionPredial v WHERE v.ipAct = :ipAct")
    , @NamedQuery(name = "ValoracionPredial.findByRmjAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.rmjAnt = :rmjAnt")
    , @NamedQuery(name = "ValoracionPredial.findByRmjAct", query = "SELECT v FROM ValoracionPredial v WHERE v.rmjAct = :rmjAct")
    , @NamedQuery(name = "ValoracionPredial.findBySneAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.sneAnt = :sneAnt")
    , @NamedQuery(name = "ValoracionPredial.findBySneAct", query = "SELECT v FROM ValoracionPredial v WHERE v.sneAct = :sneAct")
    , @NamedQuery(name = "ValoracionPredial.findByRbAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.rbAnt = :rbAnt")
    , @NamedQuery(name = "ValoracionPredial.findByRbAct", query = "SELECT v FROM ValoracionPredial v WHERE v.rbAct = :rbAct")
    , @NamedQuery(name = "ValoracionPredial.findByEmisionAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.emisionAnt = :emisionAnt")
    , @NamedQuery(name = "ValoracionPredial.findByEmisionAct", query = "SELECT v FROM ValoracionPredial v WHERE v.emisionAct = :emisionAct")
    , @NamedQuery(name = "ValoracionPredial.findByAvsolAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.avsolAnt = :avsolAnt")
    , @NamedQuery(name = "ValoracionPredial.findByAvedifAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.avedifAnt = :avedifAnt")
    , @NamedQuery(name = "ValoracionPredial.findByAvmunAnt", query = "SELECT v FROM ValoracionPredial v WHERE v.avmunAnt = :avmunAnt")
    , @NamedQuery(name = "ValoracionPredial.findByTasaMant", query = "SELECT v FROM ValoracionPredial v WHERE v.tasaMant = :tasaMant")
    , @NamedQuery(name = "ValoracionPredial.findByAlicuota", query = "SELECT v FROM ValoracionPredial v WHERE v.alicuota = :alicuota")
    , @NamedQuery(name = "ValoracionPredial.findByAvaluoSolar", query = "SELECT v FROM ValoracionPredial v WHERE v.avaluoSolar = :avaluoSolar")
    , @NamedQuery(name = "ValoracionPredial.findByAvaluoEdificacion", query = "SELECT v FROM ValoracionPredial v WHERE v.avaluoEdificacion = :avaluoEdificacion")
    , @NamedQuery(name = "ValoracionPredial.findByAvaluoMunicipal", query = "SELECT v FROM ValoracionPredial v WHERE v.avaluoMunicipal = :avaluoMunicipal")
    , @NamedQuery(name = "ValoracionPredial.findByAvaluoMunicipalMatriz", query = "SELECT v FROM ValoracionPredial v WHERE v.avaluoMunicipalMatriz = :avaluoMunicipalMatriz")
    , @NamedQuery(name = "ValoracionPredial.findByFecCre", query = "SELECT v FROM ValoracionPredial v WHERE v.fecCre = :fecCre")
    , @NamedQuery(name = "ValoracionPredial.findByUsrCre", query = "SELECT v FROM ValoracionPredial v WHERE v.usrCre = :usrCre")})
public class ValoracionPredial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_version", nullable = false)
    private long numVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "periodo", nullable = false)
    private int periodo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_predio", nullable = false)
    private long numPredio;
    @Column(name = "id_predio")
    private Long idPredio;
    @Size(max = 50)
    @Column(name = "clave_mz", length = 50)
    private String claveMz;
    @Size(max = 50)
    @Column(name = "codigo_catastral", length = 50)
    private String codigoCatastral;
    @Column(name = "matriz")
    private Boolean matriz;
    @Column(name = "numero_matriz")
    private BigInteger numeroMatriz;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_solar", precision = 14, scale = 4)
    private BigDecimal areaSolar;
    @Column(name = "area_construccion", precision = 14, scale = 4)
    private BigDecimal areaConstruccion;
    @Column(name = "area_solar_tipo", precision = 14, scale = 4)
    private BigDecimal areaSolarTipo;
    @Column(name = "frente_solar", precision = 14, scale = 4)
    private BigDecimal frenteSolar;
    @Column(name = "frente_solar_tipo", precision = 14, scale = 4)
    private BigDecimal frenteSolarTipo;
    @Column(name = "cant_servicios")
    private Integer cantServicios;
    @Column(name = "subsector")
    private Integer subsector;
    @Column(name = "valor_m2_subsector", precision = 14, scale = 4)
    private BigDecimal valorM2Subsector;
    @Column(name = "alfa", precision = 14, scale = 4)
    private BigDecimal alfa;
    @Column(name = "beta", precision = 14, scale = 4)
    private BigDecimal beta;
    @Column(name = "y", precision = 14, scale = 4)
    private BigDecimal y;
    @Column(name = "fact_ext", precision = 14, scale = 4)
    private BigDecimal factExt;
    @Column(name = "fact_frente", precision = 14, scale = 4)
    private BigDecimal factFrente;
    @Column(name = "fact_geometrico", precision = 14, scale = 4)
    private BigDecimal factGeometrico;
    @Column(name = "fact_servicio", precision = 14, scale = 4)
    private BigDecimal factServicio;
    @Column(name = "fact_correlacion", precision = 14, scale = 4)
    private BigDecimal factCorrelacion;
    @Column(name = "banda_impositiva", precision = 14, scale = 4)
    private BigDecimal bandaImpositiva;
    @Column(name = "ip_ant", precision = 14, scale = 4)
    private BigDecimal ipAnt;
    @Column(name = "ip_act", precision = 14, scale = 4)
    private BigDecimal ipAct;
    @Column(name = "rmj_ant", precision = 14, scale = 4)
    private BigDecimal rmjAnt;
    @Column(name = "rmj_act", precision = 14, scale = 4)
    private BigDecimal rmjAct;
    @Column(name = "sne_ant", precision = 14, scale = 4)
    private BigDecimal sneAnt;
    @Column(name = "sne_act", precision = 14, scale = 4)
    private BigDecimal sneAct;
    @Column(name = "rb_ant", precision = 14, scale = 4)
    private BigDecimal rbAnt;
    @Column(name = "rb_act", precision = 14, scale = 4)
    private BigDecimal rbAct;
    @Column(name = "emision_ant", precision = 14, scale = 4)
    private BigDecimal emisionAnt;
    @Column(name = "emision_act", precision = 14, scale = 4)
    private BigDecimal emisionAct;
    @Column(name = "avsol_ant", precision = 14, scale = 4)
    private BigDecimal avsolAnt;
    @Column(name = "avedif_ant", precision = 14, scale = 4)
    private BigDecimal avedifAnt;
    @Column(name = "avmun_ant", precision = 14, scale = 4)
    private BigDecimal avmunAnt;
    @Column(name = "tasa_mant", precision = 14, scale = 4)
    private BigDecimal tasaMant;
    @Column(name = "alicuota", precision = 14, scale = 4)
    private BigDecimal alicuota;
    @Column(name = "avaluo_solar", precision = 14, scale = 4)
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_edificacion", precision = 14, scale = 4)
    private BigDecimal avaluoEdificacion;
    @Column(name = "avaluo_municipal", precision = 14, scale = 4)
    private BigDecimal avaluoMunicipal;
    @Column(name = "avaluo_municipal_matriz", precision = 14, scale = 4)
    private BigDecimal avaluoMunicipalMatriz;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Size(max = 50)
    @Column(name = "usr_cre", length = 50)
    private String usrCre;
    @Column(name = "contribuyente", length = 500)
    private String contribuyente;
    @Column(name = "liquidacion", nullable = false)
    private Long liquidacion;

    public ValoracionPredial() {
    }

    public ValoracionPredial(Long id) {
        this.id = id;
    }

    public ValoracionPredial(Long id, long numVersion, int periodo, long numPredio) {
        this.id = id;
        this.numVersion = numVersion;
        this.periodo = periodo;
        this.numPredio = numPredio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(long numVersion) {
        this.numVersion = numVersion;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(long numPredio) {
        this.numPredio = numPredio;
    }

    public String getClaveMz() {
        return claveMz;
    }

    public void setClaveMz(String claveMz) {
        this.claveMz = claveMz;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public Boolean getMatriz() {
        return matriz;
    }

    public void setMatriz(Boolean matriz) {
        this.matriz = matriz;
    }

    public BigInteger getNumeroMatriz() {
        return numeroMatriz;
    }

    public void setNumeroMatriz(BigInteger numeroMatriz) {
        this.numeroMatriz = numeroMatriz;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getAreaSolarTipo() {
        return areaSolarTipo;
    }

    public void setAreaSolarTipo(BigDecimal areaSolarTipo) {
        this.areaSolarTipo = areaSolarTipo;
    }

    public BigDecimal getFrenteSolar() {
        return frenteSolar;
    }

    public void setFrenteSolar(BigDecimal frenteSolar) {
        this.frenteSolar = frenteSolar;
    }

    public BigDecimal getFrenteSolarTipo() {
        return frenteSolarTipo;
    }

    public void setFrenteSolarTipo(BigDecimal frenteSolarTipo) {
        this.frenteSolarTipo = frenteSolarTipo;
    }

    public Integer getCantServicios() {
        return cantServicios;
    }

    public void setCantServicios(Integer cantServicios) {
        this.cantServicios = cantServicios;
    }

    public Integer getSubsector() {
        return subsector;
    }

    public void setSubsector(Integer subsector) {
        this.subsector = subsector;
    }

    public BigDecimal getValorM2Subsector() {
        return valorM2Subsector;
    }

    public void setValorM2Subsector(BigDecimal valorM2Subsector) {
        this.valorM2Subsector = valorM2Subsector;
    }

    public BigDecimal getAlfa() {
        return alfa;
    }

    public void setAlfa(BigDecimal alfa) {
        this.alfa = alfa;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public BigDecimal getFactExt() {
        return factExt;
    }

    public void setFactExt(BigDecimal factExt) {
        this.factExt = factExt;
    }

    public BigDecimal getFactFrente() {
        return factFrente;
    }

    public void setFactFrente(BigDecimal factFrente) {
        this.factFrente = factFrente;
    }

    public BigDecimal getFactGeometrico() {
        return factGeometrico;
    }

    public void setFactGeometrico(BigDecimal factGeometrico) {
        this.factGeometrico = factGeometrico;
    }

    public BigDecimal getFactServicio() {
        return factServicio;
    }

    public void setFactServicio(BigDecimal factServicio) {
        this.factServicio = factServicio;
    }

    public BigDecimal getFactCorrelacion() {
        return factCorrelacion;
    }

    public void setFactCorrelacion(BigDecimal factCorrelacion) {
        this.factCorrelacion = factCorrelacion;
    }

    public BigDecimal getBandaImpositiva() {
        return bandaImpositiva;
    }

    public void setBandaImpositiva(BigDecimal bandaImpositiva) {
        this.bandaImpositiva = bandaImpositiva;
    }

    public BigDecimal getIpAnt() {
        return ipAnt;
    }

    public void setIpAnt(BigDecimal ipAnt) {
        this.ipAnt = ipAnt;
    }

    public BigDecimal getIpAct() {
        return ipAct;
    }

    public void setIpAct(BigDecimal ipAct) {
        this.ipAct = ipAct;
    }

    public BigDecimal getRmjAnt() {
        return rmjAnt;
    }

    public void setRmjAnt(BigDecimal rmjAnt) {
        this.rmjAnt = rmjAnt;
    }

    public BigDecimal getRmjAct() {
        return rmjAct;
    }

    public void setRmjAct(BigDecimal rmjAct) {
        this.rmjAct = rmjAct;
    }

    public BigDecimal getSneAnt() {
        return sneAnt;
    }

    public void setSneAnt(BigDecimal sneAnt) {
        this.sneAnt = sneAnt;
    }

    public BigDecimal getSneAct() {
        return sneAct;
    }

    public void setSneAct(BigDecimal sneAct) {
        this.sneAct = sneAct;
    }

    public BigDecimal getRbAnt() {
        return rbAnt;
    }

    public void setRbAnt(BigDecimal rbAnt) {
        this.rbAnt = rbAnt;
    }

    public BigDecimal getRbAct() {
        return rbAct;
    }

    public void setRbAct(BigDecimal rbAct) {
        this.rbAct = rbAct;
    }

    public BigDecimal getEmisionAnt() {
        return emisionAnt;
    }

    public void setEmisionAnt(BigDecimal emisionAnt) {
        this.emisionAnt = emisionAnt;
    }

    public BigDecimal getEmisionAct() {
        return emisionAct;
    }

    public void setEmisionAct(BigDecimal emisionAct) {
        this.emisionAct = emisionAct;
    }

    public BigDecimal getAvsolAnt() {
        return avsolAnt;
    }

    public void setAvsolAnt(BigDecimal avsolAnt) {
        this.avsolAnt = avsolAnt;
    }

    public BigDecimal getAvedifAnt() {
        return avedifAnt;
    }

    public void setAvedifAnt(BigDecimal avedifAnt) {
        this.avedifAnt = avedifAnt;
    }

    public BigDecimal getAvmunAnt() {
        return avmunAnt;
    }

    public void setAvmunAnt(BigDecimal avmunAnt) {
        this.avmunAnt = avmunAnt;
    }

    public BigDecimal getTasaMant() {
        return tasaMant;
    }

    public void setTasaMant(BigDecimal tasaMant) {
        this.tasaMant = tasaMant;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoEdificacion() {
        return avaluoEdificacion;
    }

    public void setAvaluoEdificacion(BigDecimal avaluoEdificacion) {
        this.avaluoEdificacion = avaluoEdificacion;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public BigDecimal getAvaluoMunicipalMatriz() {
        return avaluoMunicipalMatriz;
    }

    public void setAvaluoMunicipalMatriz(BigDecimal avaluoMunicipalMatriz) {
        this.avaluoMunicipalMatriz = avaluoMunicipalMatriz;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public Long getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(Long idPredio) {
        this.idPredio = idPredio;
    }

    public String getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    public Long getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(Long liquidacion) {
        this.liquidacion = liquidacion;
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
        if (!(object instanceof ValoracionPredial)) {
            return false;
        }
        ValoracionPredial other = (ValoracionPredial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + id + "";
    }

}
