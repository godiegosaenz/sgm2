/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "parametros_valoracion_predio", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"prefijo"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ParametrosValoracionPredio.findAll", query = "SELECT p FROM ParametrosValoracionPredio p"),
    @NamedQuery(name = "ParametrosValoracionPredio.findById", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.id = :id"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByPrefijo", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.prefijo = :prefijo"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByMaxAreaSolar", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.maxAreaSolar = :maxAreaSolar"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByMinAreaConst", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.minAreaConst = :minAreaConst"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByCoefGeoMax", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.coefGeoMax = :coefGeoMax"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByCoefGeoMin", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.coefGeoMin = :coefGeoMin"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactAreaMin", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factAreaMin = :factAreaMin"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactAreaMax", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factAreaMax = :factAreaMax"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactFrenteMin", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factFrenteMin = :factFrenteMin"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactFrenteMax", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factFrenteMax = :factFrenteMax"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFrenteTipoDivMin", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.frenteTipoDivMin = :frenteTipoDivMin"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFrenteTipoDivMax", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.frenteTipoDivMax = :frenteTipoDivMax"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactProfMin", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factProfMin = :factProfMin"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactProfMax", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factProfMax = :factProfMax"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactTotalMin", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factTotalMin = :factTotalMin"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFactTotalMax", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.factTotalMax = :factTotalMax"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByMaxValAreaCub", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.maxValAreaCub = :maxValAreaCub"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFecCre", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.fecCre = :fecCre"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByFecAct", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.fecAct = :fecAct"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByUsrCre", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.usrCre = :usrCre"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByUsrAct", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.usrAct = :usrAct"),
    @NamedQuery(name = "ParametrosValoracionPredio.findByEstado", query = "SELECT p FROM ParametrosValoracionPredio p WHERE p.estado = :estado")})
public class ParametrosValoracionPredio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 10)
    @Column(name = "prefijo", length = 10)
    private String prefijo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "max_area_solar", precision = 10, scale = 2)
    private BigDecimal maxAreaSolar;
    @Column(name = "min_area_const", precision = 10, scale = 2)
    private BigDecimal minAreaConst;
    @Column(name = "coef_geo_max", precision = 10, scale = 2)
    private BigDecimal coefGeoMax;
    @Column(name = "coef_geo_min", precision = 10, scale = 2)
    private BigDecimal coefGeoMin;
    @Column(name = "fact_area_min", precision = 10, scale = 2)
    private BigDecimal factAreaMin;
    @Column(name = "fact_area_max", precision = 10, scale = 2)
    private BigDecimal factAreaMax;
    @Column(name = "fact_frente_min", precision = 10, scale = 2)
    private BigDecimal factFrenteMin;
    @Column(name = "fact_frente_max", precision = 10, scale = 2)
    private BigDecimal factFrenteMax;
    @Column(name = "frente_tipo_div_min", precision = 10, scale = 2)
    private BigDecimal frenteTipoDivMin;
    @Column(name = "frente_tipo_div_max", precision = 10, scale = 2)
    private BigDecimal frenteTipoDivMax;
    @Column(name = "fact_prof_min", precision = 10, scale = 2)
    private BigDecimal factProfMin;
    @Column(name = "fact_prof_max", precision = 10, scale = 2)
    private BigDecimal factProfMax;
    @Column(name = "fact_total_min", precision = 10, scale = 2)
    private BigDecimal factTotalMin;
    @Column(name = "fact_total_max", precision = 10, scale = 2)
    private BigDecimal factTotalMax;
    @Column(name = "max_val_area_cub", precision = 10, scale = 2)
    private BigDecimal maxValAreaCub;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Size(max = 50)
    @Column(name = "usr_cre", length = 50)
    private String usrCre;
    @Size(max = 50)
    @Column(name = "usr_act", length = 50)
    private String usrAct;
    @Column(name = "estado")
    private Boolean estado;

    public ParametrosValoracionPredio() {
    }

    public ParametrosValoracionPredio(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public BigDecimal getMaxAreaSolar() {
        return maxAreaSolar;
    }

    public void setMaxAreaSolar(BigDecimal maxAreaSolar) {
        this.maxAreaSolar = maxAreaSolar;
    }

    public BigDecimal getMinAreaConst() {
        return minAreaConst;
    }

    public void setMinAreaConst(BigDecimal minAreaConst) {
        this.minAreaConst = minAreaConst;
    }

    public BigDecimal getCoefGeoMax() {
        return coefGeoMax;
    }

    public void setCoefGeoMax(BigDecimal coefGeoMax) {
        this.coefGeoMax = coefGeoMax;
    }

    public BigDecimal getCoefGeoMin() {
        return coefGeoMin;
    }

    public void setCoefGeoMin(BigDecimal coefGeoMin) {
        this.coefGeoMin = coefGeoMin;
    }

    public BigDecimal getFactAreaMin() {
        return factAreaMin;
    }

    public void setFactAreaMin(BigDecimal factAreaMin) {
        this.factAreaMin = factAreaMin;
    }

    public BigDecimal getFactAreaMax() {
        return factAreaMax;
    }

    public void setFactAreaMax(BigDecimal factAreaMax) {
        this.factAreaMax = factAreaMax;
    }

    public BigDecimal getFactFrenteMin() {
        return factFrenteMin;
    }

    public void setFactFrenteMin(BigDecimal factFrenteMin) {
        this.factFrenteMin = factFrenteMin;
    }

    public BigDecimal getFactFrenteMax() {
        return factFrenteMax;
    }

    public void setFactFrenteMax(BigDecimal factFrenteMax) {
        this.factFrenteMax = factFrenteMax;
    }

    public BigDecimal getFrenteTipoDivMin() {
        return frenteTipoDivMin;
    }

    public void setFrenteTipoDivMin(BigDecimal frenteTipoDivMin) {
        this.frenteTipoDivMin = frenteTipoDivMin;
    }

    public BigDecimal getFrenteTipoDivMax() {
        return frenteTipoDivMax;
    }

    public void setFrenteTipoDivMax(BigDecimal frenteTipoDivMax) {
        this.frenteTipoDivMax = frenteTipoDivMax;
    }

    public BigDecimal getFactProfMin() {
        return factProfMin;
    }

    public void setFactProfMin(BigDecimal factProfMin) {
        this.factProfMin = factProfMin;
    }

    public BigDecimal getFactProfMax() {
        return factProfMax;
    }

    public void setFactProfMax(BigDecimal factProfMax) {
        this.factProfMax = factProfMax;
    }

    public BigDecimal getFactTotalMin() {
        return factTotalMin;
    }

    public void setFactTotalMin(BigDecimal factTotalMin) {
        this.factTotalMin = factTotalMin;
    }

    public BigDecimal getFactTotalMax() {
        return factTotalMax;
    }

    public void setFactTotalMax(BigDecimal factTotalMax) {
        this.factTotalMax = factTotalMax;
    }

    public BigDecimal getMaxValAreaCub() {
        return maxValAreaCub;
    }

    public void setMaxValAreaCub(BigDecimal maxValAreaCub) {
        this.maxValAreaCub = maxValAreaCub;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public String getUsrAct() {
        return usrAct;
    }

    public void setUsrAct(String usrAct) {
        this.usrAct = usrAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
        if (!(object instanceof ParametrosValoracionPredio)) {
            return false;
        }
        ParametrosValoracionPredio other = (ParametrosValoracionPredio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.ParametrosValoracionPredio[ id=" + id + " ]";
    }
    
}
