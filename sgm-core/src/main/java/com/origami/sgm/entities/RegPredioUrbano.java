/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_predio_urbano",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegPredioUrbano.findAll", query = "SELECT r FROM RegPredioUrbano r"),
    @NamedQuery(name = "RegPredioUrbano.findById", query = "SELECT r FROM RegPredioUrbano r WHERE r.id = :id"),
    @NamedQuery(name = "RegPredioUrbano.findByPredioId", query = "SELECT r FROM RegPredioUrbano r WHERE r.predioId = :predioId"),
    @NamedQuery(name = "RegPredioUrbano.findByNumPredio", query = "SELECT r FROM RegPredioUrbano r WHERE r.numPredio = :numPredio"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosNorteCon", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosNorteCon = :linderosNorteCon"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosNorte", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosNorte = :linderosNorte"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosSur", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosSur = :linderosSur"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosSurCon", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosSurCon = :linderosSurCon"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosEste", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosEste = :linderosEste"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosEsteCon", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosEsteCon = :linderosEsteCon"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosOeste", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosOeste = :linderosOeste"),
    @NamedQuery(name = "RegPredioUrbano.findByLinderosOesteCon", query = "SELECT r FROM RegPredioUrbano r WHERE r.linderosOesteCon = :linderosOesteCon"),
    @NamedQuery(name = "RegPredioUrbano.findByNumFichaResgistroProp", query = "SELECT r FROM RegPredioUrbano r WHERE r.numFichaResgistroProp = :numFichaResgistroProp"),
    @NamedQuery(name = "RegPredioUrbano.findByAreaSolar", query = "SELECT r FROM RegPredioUrbano r WHERE r.areaSolar = :areaSolar"),
    @NamedQuery(name = "RegPredioUrbano.findByAreaConstruc", query = "SELECT r FROM RegPredioUrbano r WHERE r.areaConstruc = :areaConstruc"),
    @NamedQuery(name = "RegPredioUrbano.findByAlicuota", query = "SELECT r FROM RegPredioUrbano r WHERE r.alicuota = :alicuota"),
    @NamedQuery(name = "RegPredioUrbano.findByEsPropiedadHorizontal", query = "SELECT r FROM RegPredioUrbano r WHERE r.esPropiedadHorizontal = :esPropiedadHorizontal")})
public class RegPredioUrbano implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "predio_id", nullable = false)
    private long predioId;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "linderos_norte_con", precision = 10, scale = 2)
    private BigDecimal linderosNorteCon;
    @Size(max = 50)
    @Column(name = "linderos_norte", length = 50)
    private String linderosNorte;
    @Size(max = 50)
    @Column(name = "linderos_sur", length = 50)
    private String linderosSur;
    @Column(name = "linderos_sur_con", precision = 10, scale = 2)
    private BigDecimal linderosSurCon;
    @Size(max = 50)
    @Column(name = "linderos_este", length = 50)
    private String linderosEste;
    @Column(name = "linderos_este_con", precision = 10, scale = 2)
    private BigDecimal linderosEsteCon;
    @Size(max = 50)
    @Column(name = "linderos_oeste", length = 50)
    private String linderosOeste;
    @Column(name = "linderos_oeste_con", precision = 10, scale = 2)
    private BigDecimal linderosOesteCon;
    @Column(name = "num_ficha_resgistro_prop")
    private BigInteger numFichaResgistroProp;
    @Column(name = "area_solar", precision = 12, scale = 2)
    private BigDecimal areaSolar;
    @Column(name = "area_construc", precision = 12, scale = 2)
    private BigDecimal areaConstruc;
    @Column(name = "alicuota", precision = 12, scale = 2)
    private BigDecimal alicuota;
    @Column(name = "es_propiedad_horizontal")
    private Boolean esPropiedadHorizontal;
    @JoinColumn(name = "escritura_reg", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegEscritura escrituraReg;

    public RegPredioUrbano() {
    }

    public RegPredioUrbano(Long id) {
        this.id = id;
    }

    public RegPredioUrbano(Long id, long predioId) {
        this.id = id;
        this.predioId = predioId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPredioId() {
        return predioId;
    }

    public void setPredioId(long predioId) {
        this.predioId = predioId;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public BigDecimal getLinderosNorteCon() {
        return linderosNorteCon;
    }

    public void setLinderosNorteCon(BigDecimal linderosNorteCon) {
        this.linderosNorteCon = linderosNorteCon;
    }

    public String getLinderosNorte() {
        return linderosNorte;
    }

    public void setLinderosNorte(String linderosNorte) {
        this.linderosNorte = linderosNorte;
    }

    public String getLinderosSur() {
        return linderosSur;
    }

    public void setLinderosSur(String linderosSur) {
        this.linderosSur = linderosSur;
    }

    public BigDecimal getLinderosSurCon() {
        return linderosSurCon;
    }

    public void setLinderosSurCon(BigDecimal linderosSurCon) {
        this.linderosSurCon = linderosSurCon;
    }

    public String getLinderosEste() {
        return linderosEste;
    }

    public void setLinderosEste(String linderosEste) {
        this.linderosEste = linderosEste;
    }

    public BigDecimal getLinderosEsteCon() {
        return linderosEsteCon;
    }

    public void setLinderosEsteCon(BigDecimal linderosEsteCon) {
        this.linderosEsteCon = linderosEsteCon;
    }

    public String getLinderosOeste() {
        return linderosOeste;
    }

    public void setLinderosOeste(String linderosOeste) {
        this.linderosOeste = linderosOeste;
    }

    public BigDecimal getLinderosOesteCon() {
        return linderosOesteCon;
    }

    public void setLinderosOesteCon(BigDecimal linderosOesteCon) {
        this.linderosOesteCon = linderosOesteCon;
    }

    public BigInteger getNumFichaResgistroProp() {
        return numFichaResgistroProp;
    }

    public void setNumFichaResgistroProp(BigInteger numFichaResgistroProp) {
        this.numFichaResgistroProp = numFichaResgistroProp;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAreaConstruc() {
        return areaConstruc;
    }

    public void setAreaConstruc(BigDecimal areaConstruc) {
        this.areaConstruc = areaConstruc;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

    public Boolean getEsPropiedadHorizontal() {
        return esPropiedadHorizontal;
    }

    public void setEsPropiedadHorizontal(Boolean esPropiedadHorizontal) {
        this.esPropiedadHorizontal = esPropiedadHorizontal;
    }

    public RegEscritura getEscrituraReg() {
        return escrituraReg;
    }

    public void setEscrituraReg(RegEscritura escrituraReg) {
        this.escrituraReg = escrituraReg;
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
        if (!(object instanceof RegPredioUrbano)) {
            return false;
        }
        RegPredioUrbano other = (RegPredioUrbano) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegPredioUrbano[ id=" + id + " ]";
    }
    
}
