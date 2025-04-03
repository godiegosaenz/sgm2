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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_escritura_rural", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"registro_catastral", "identificacion_predial"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatEscrituraRural.findAll", query = "SELECT c FROM CatEscrituraRural c"),
    @NamedQuery(name = "CatEscrituraRural.findById", query = "SELECT c FROM CatEscrituraRural c WHERE c.id = :id"),
    @NamedQuery(name = "CatEscrituraRural.findByRegistroCatastral", query = "SELECT c FROM CatEscrituraRural c WHERE c.registroCatastral = :registroCatastral"),
    @NamedQuery(name = "CatEscrituraRural.findByIdentificacionPredial", query = "SELECT c FROM CatEscrituraRural c WHERE c.identificacionPredial = :identificacionPredial"),
    @NamedQuery(name = "CatEscrituraRural.findByAreaSolar", query = "SELECT c FROM CatEscrituraRural c WHERE c.areaSolar = :areaSolar"),
    @NamedQuery(name = "CatEscrituraRural.findByAlicuota", query = "SELECT c FROM CatEscrituraRural c WHERE c.alicuota = :alicuota"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrNorte", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrNorte = :lindEscrNorte"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrNorteCon", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrNorteCon = :lindEscrNorteCon"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrEste", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrEste = :lindEscrEste"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrEsteCon", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrEsteCon = :lindEscrEsteCon"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrSur", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrSur = :lindEscrSur"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrSurCon", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrSurCon = :lindEscrSurCon"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrOeste", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrOeste = :lindEscrOeste"),
    @NamedQuery(name = "CatEscrituraRural.findByLindEscrOesteCon", query = "SELECT c FROM CatEscrituraRural c WHERE c.lindEscrOesteCon = :lindEscrOesteCon"),
    @NamedQuery(name = "CatEscrituraRural.findByPropiedadHorizontal", query = "SELECT c FROM CatEscrituraRural c WHERE c.propiedadHorizontal = :propiedadHorizontal"),
    @NamedQuery(name = "CatEscrituraRural.findByNombrePredio", query = "SELECT c FROM CatEscrituraRural c WHERE c.nombrePredio = :nombrePredio"),
    @NamedQuery(name = "CatEscrituraRural.findByLugarPredio", query = "SELECT c FROM CatEscrituraRural c WHERE c.lugarPredio = :lugarPredio")})
public class CatEscrituraRural implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "registro_catastral", nullable = false)
    private Long registroCatastral;
    @Basic(optional = false)
    @NotNull
    @Column(name = "identificacion_predial", nullable = false)
    private Long identificacionPredial;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_solar", precision = 14, scale = 2)
    private BigDecimal areaSolar;
    @Column(name = "alicuota", precision = 5, scale = 2)
    private BigDecimal alicuota;
    @Size(max = 50)
    @Column(name = "lind_escr_norte", length = 50)
    private String lindEscrNorte;
    @Column(name = "lind_escr_norte_con", precision = 14, scale = 2)
    private BigDecimal lindEscrNorteCon;
    @Size(max = 50)
    @Column(name = "lind_escr_este", length = 50)
    private String lindEscrEste;
    @Column(name = "lind_escr_este_con", precision = 14, scale = 2)
    private BigDecimal lindEscrEsteCon;
    @Size(max = 50)
    @Column(name = "lind_escr_sur", length = 50)
    private String lindEscrSur;
    @Column(name = "lind_escr_sur_con", precision = 14, scale = 2)
    private BigDecimal lindEscrSurCon;
    @Size(max = 50)
    @Column(name = "lind_escr_oeste", length = 50)
    private String lindEscrOeste;
    @Column(name = "lind_escr_oeste_con", precision = 14, scale = 2)
    private BigDecimal lindEscrOesteCon;
    @Column(name = "propiedad_horizontal")
    private Boolean propiedadHorizontal;
    @Size(max = 255)
    @Column(name = "nombre_predio", length = 255)
    private String nombrePredio;
    @Size(max = 255)
    @Column(name = "lugar_predio", length = 255)
    private String lugarPredio;
    @OneToOne(mappedBy = "catEscrituraRural", fetch = FetchType.LAZY)
    private RegFicha regFicha;

    @Size(max = 100)
    @Column(name = "user_creador", length = 100)
    private String userCreador;
    @Column(name = "fecha_creador")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreador;
    @Size(max = 100)
    @Column(name = "user_edicion", length = 100)
    private String userEdicion;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    
    public CatEscrituraRural() {
    }

    public CatEscrituraRural(Long id) {
        this.id = id;
    }

    public CatEscrituraRural(Long id, Long registroCatastral, Long identificacionPredial) {
        this.id = id;
        this.registroCatastral = registroCatastral;
        this.identificacionPredial = identificacionPredial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegistroCatastral() {
        return registroCatastral;
    }

    public void setRegistroCatastral(Long registroCatastral) {
        this.registroCatastral = registroCatastral;
    }

    public Long getIdentificacionPredial() {
        return identificacionPredial;
    }

    public void setIdentificacionPredial(Long identificacionPredial) {
        this.identificacionPredial = identificacionPredial;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
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

    public Boolean getPropiedadHorizontal() {
        return propiedadHorizontal;
    }

    public void setPropiedadHorizontal(Boolean propiedadHorizontal) {
        this.propiedadHorizontal = propiedadHorizontal;
    }

    public String getNombrePredio() {
        return nombrePredio;
    }

    public void setNombrePredio(String nombrePredio) {
        this.nombrePredio = nombrePredio;
    }

    public String getLugarPredio() {
        return lugarPredio;
    }

    public void setLugarPredio(String lugarPredio) {
        this.lugarPredio = lugarPredio;
    }

    public RegFicha getRegFicha() {
        return regFicha;
    }

    public void setRegFicha(RegFicha regFicha) {
        this.regFicha = regFicha;
    }

    public String getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(String userCreador) {
        this.userCreador = userCreador;
    }

    public Date getFechaCreador() {
        return fechaCreador;
    }

    public void setFechaCreador(Date fechaCreador) {
        this.fechaCreador = fechaCreador;
    }

    public String getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(String userEdicion) {
        this.userEdicion = userEdicion;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
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
        if (!(object instanceof CatEscrituraRural)) {
            return false;
        }
        CatEscrituraRural other = (CatEscrituraRural) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatEscrituraRural[ id=" + id + " ]";
    }
    
}
