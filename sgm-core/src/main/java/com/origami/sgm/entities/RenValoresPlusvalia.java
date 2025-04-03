/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.RenDesvalorizacion;
import com.origami.sgm.entities.RenDetallePlusvalia;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Angel Navarro
 * @Date 26/04/2016
 */
@Entity
@Table(name = "ren_valores_plusvalia", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenValoresPlusvalia.findAll", query = "SELECT r FROM RenValoresPlusvalia r")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findById", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.id = :id")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByDiferenciaNeta", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.diferenciaNeta = :diferenciaNeta")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByMejorasCemConst", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.mejorasCemConst = :mejorasCemConst")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByMejorasUrb", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.mejorasUrb = :mejorasUrb")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByDiferenciaNeta2", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.diferenciaNeta2 = :diferenciaNeta2")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByRebajaGen", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.rebajaGen = :rebajaGen")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByBaseDesvalorizacion", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.baseDesvalorizacion = :baseDesvalorizacion")
    ,
    @NamedQuery(name = "RenValoresPlusvalia.findByDesvalorizacionMonet", query = "SELECT r FROM RenValoresPlusvalia r WHERE r.desvalorizacionMonet = :desvalorizacionMonet")})
public class RenValoresPlusvalia implements Serializable {

    @OneToMany(mappedBy = "valoresPlusvalia")
    private Collection<RenDetallePlusvalia> renDetallePlusvaliaCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "diferencia_neta")
    private BigDecimal diferenciaNeta = BigDecimal.ZERO;
    @Column(name = "mejoras_cem_const")
    private BigDecimal mejorasCemConst = BigDecimal.ZERO;
    @Column(name = "mejoras_urb")
    private BigDecimal mejorasUrb = BigDecimal.ZERO;
    @Column(name = "diferencia_neta2")
    private BigDecimal diferenciaNeta2 = BigDecimal.ZERO;
    @Column(name = "rebaja_gen")
    private BigDecimal rebajaGen = BigDecimal.ZERO;
    @Column(name = "base_desvalorizacion")
    private BigDecimal baseDesvalorizacion = BigDecimal.ZERO;
    @Column(name = "desvalorizacion_monet")
    private BigDecimal desvalorizacionMonet = BigDecimal.ZERO;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @OneToOne(optional = false)
    private RenLiquidacion liquidacion;
    @Column(name = "utilidad_imponib")
    private BigDecimal utilidadImponib = BigDecimal.ZERO;
    @Column(name = "porcentaje_rebaja")
    private Integer porcentajeRebaja;
    @JoinColumn(name = "desvalorizacion", referencedColumnName = "id")
    @ManyToOne
    private RenDesvalorizacion desvalorizacion;
    @Column(name = "vigencia")
    private Integer vigencia = 3;

    @Column(name = "fecha_escrita_ant")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEscritaAnt;
    @Column(name = "fecha_escrita_act")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEscritaAct;

    public RenValoresPlusvalia() {
    }

    public RenValoresPlusvalia(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDiferenciaNeta() {
        return diferenciaNeta;
    }

    public void setDiferenciaNeta(BigDecimal diferenciaNeta) {
        this.diferenciaNeta = diferenciaNeta;
    }

    public BigDecimal getMejorasCemConst() {
        return mejorasCemConst;
    }

    public void setMejorasCemConst(BigDecimal mejorasCemConst) {
        this.mejorasCemConst = mejorasCemConst;
    }

    public BigDecimal getMejorasUrb() {
        return mejorasUrb;
    }

    public void setMejorasUrb(BigDecimal mejorasUrb) {
        this.mejorasUrb = mejorasUrb;
    }

    public BigDecimal getDiferenciaNeta2() {
        return diferenciaNeta2;
    }

    public void setDiferenciaNeta2(BigDecimal diferenciaNeta2) {
        this.diferenciaNeta2 = diferenciaNeta2;
    }

    public BigDecimal getRebajaGen() {
        return rebajaGen;
    }

    public void setRebajaGen(BigDecimal rebajaGen) {
        this.rebajaGen = rebajaGen;
    }

    public BigDecimal getBaseDesvalorizacion() {
        return baseDesvalorizacion;
    }

    public void setBaseDesvalorizacion(BigDecimal baseDesvalorizacion) {
        this.baseDesvalorizacion = baseDesvalorizacion;
    }

    public BigDecimal getDesvalorizacionMonet() {
        return desvalorizacionMonet;
    }

    public void setDesvalorizacionMonet(BigDecimal desvalorizacionMonet) {
        this.desvalorizacionMonet = desvalorizacionMonet;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public RenDesvalorizacion getDesvalorizacion() {
        return desvalorizacion;
    }

    public void setDesvalorizacion(RenDesvalorizacion desvalorizacion) {
        this.desvalorizacion = desvalorizacion;
    }

    public BigDecimal getUtilidadImponib() {
        return utilidadImponib;
    }

    public void setUtilidadImponib(BigDecimal utilidadImponib) {
        this.utilidadImponib = utilidadImponib;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Integer getPorcentajeRebaja() {
        return porcentajeRebaja;
    }

    public void setPorcentajeRebaja(Integer porcentajeRebaja) {
        this.porcentajeRebaja = porcentajeRebaja;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RenValoresPlusvalia)) {
            return false;
        }
        RenValoresPlusvalia other = (RenValoresPlusvalia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenValoresPlusvalia[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RenDetallePlusvalia> getRenDetallePlusvaliaCollection() {
        return renDetallePlusvaliaCollection;
    }

    public void setRenDetallePlusvaliaCollection(Collection<RenDetallePlusvalia> renDetallePlusvaliaCollection) {
        this.renDetallePlusvaliaCollection = renDetallePlusvaliaCollection;
    }

    public Integer getVigencia() {
        return vigencia;
    }

    public void setVigencia(Integer vigencia) {
        this.vigencia = vigencia;
    }

    public Date getFechaEscritaAnt() {
        return fechaEscritaAnt;
    }

    public void setFechaEscritaAnt(Date fechaEscritaAnt) {
        this.fechaEscritaAnt = fechaEscritaAnt;
    }

    public Date getFechaEscritaAct() {
        return fechaEscritaAct;
    }

    public void setFechaEscritaAct(Date fechaEscritaAct) {
        this.fechaEscritaAct = fechaEscritaAct;
    }

}
