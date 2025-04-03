/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.models.ModelMap;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 * @Date 22/06/2016
 */
@Entity
@Table(name = "cat_predio_rustico", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioRustico.findAll", query = "SELECT c FROM CatPredioRustico c"),
    @NamedQuery(name = "CatPredioRustico.findById", query = "SELECT c FROM CatPredioRustico c WHERE c.id = :id"),
    @NamedQuery(name = "CatPredioRustico.findByRegCatastral", query = "SELECT c FROM CatPredioRustico c WHERE c.regCatastral = :regCatastral"),
    @NamedQuery(name = "CatPredioRustico.findByIdPredial", query = "SELECT c FROM CatPredioRustico c WHERE c.idPredial = :idPredial"),
    @NamedQuery(name = "CatPredioRustico.findByPoligono", query = "SELECT c FROM CatPredioRustico c WHERE c.poligono = :poligono"),
    @NamedQuery(name = "CatPredioRustico.findByFecha", query = "SELECT c FROM CatPredioRustico c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "CatPredioRustico.findByNombrePredio", query = "SELECT c FROM CatPredioRustico c WHERE c.nombrePredio = :nombrePredio"),
    @NamedQuery(name = "CatPredioRustico.findBySuperficie", query = "SELECT c FROM CatPredioRustico c WHERE c.superficie = :superficie"),
    @NamedQuery(name = "CatPredioRustico.findByAvaluoCatastral", query = "SELECT c FROM CatPredioRustico c WHERE c.avaluoCatastral = :avaluoCatastral"),
    @NamedQuery(name = "CatPredioRustico.findByTipo", query = "SELECT c FROM CatPredioRustico c WHERE c.tipo = :tipo"),
    @NamedQuery(name = "CatPredioRustico.findByInfluencia", query = "SELECT c FROM CatPredioRustico c WHERE c.influencia = :influencia"),
    @NamedQuery(name = "CatPredioRustico.findBySitio", query = "SELECT c FROM CatPredioRustico c WHERE c.sitio = :sitio"),
    @NamedQuery(name = "CatPredioRustico.findByEstado", query = "SELECT c FROM CatPredioRustico c WHERE c.estado = :estado"),
    @NamedQuery(name = "CatPredioRustico.findByRebaja", query = "SELECT c FROM CatPredioRustico c WHERE c.rebaja = :rebaja"),
    @NamedQuery(name = "CatPredioRustico.findByUsuarioIngreso", query = "SELECT c FROM CatPredioRustico c WHERE c.usuarioIngreso = :usuarioIngreso"),
    @NamedQuery(name = "CatPredioRustico.findByFechaIngreso", query = "SELECT c FROM CatPredioRustico c WHERE c.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "CatPredioRustico.findByNumPredioRustico", query = "SELECT c FROM CatPredioRustico c WHERE c.numPredioRustico = :numPredioRustico"),
    @NamedQuery(name = "CatPredioRustico.findByDestino", query = "SELECT c FROM CatPredioRustico c WHERE c.destino = :destino"),
    @NamedQuery(name = "CatPredioRustico.findByTenencia", query = "SELECT c FROM CatPredioRustico c WHERE c.tenencia = :tenencia")})
public class CatPredioRustico extends ModelMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 20)
    @Column(name = "reg_catastral", length = 20)
    private String regCatastral;
    @Size(max = 20)
    @Column(name = "id_predial", length = 20)
    private String idPredial;
   
    
    @JoinColumn(name = "parroquia", referencedColumnName = "id")
    @ManyToOne
    private CatParroquia parroquia;
    
    @Column(name = "poligono")
    private Integer poligono;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 100)
    @Column(name = "nombre_predio", length = 100)
    private String nombrePredio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "superficie", precision = 9, scale = 4)
    private BigDecimal superficie;
    @Column(name = "avaluo_catastral", precision = 10, scale = 2)
    private BigDecimal avaluoCatastral;
    @Column(name = "tipo")
    private BigInteger tipo;
    @Column(name = "influencia")
    private BigInteger influencia;
    @Size(max = 100)
    @Column(name = "sitio", length = 100)
    private String sitio;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "rebaja", precision = 9, scale = 2)
    private BigDecimal rebaja;
    @Size(max = 50)
    @Column(name = "usuario_ingreso", length = 50)
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "num_predio_rustico")
    private BigInteger numPredioRustico;
    @Size(max = 5)
    @Column(name = "destino", length = 5)
    private String destino;
    @Size(max = 5)
    @Column(name = "tenencia", length = 5)
    private String tenencia;
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte propietario;
    @OneToMany(mappedBy = "predioRustico")
    private List<HistoricoTramiteDet> historicoTramiteDets;
    @OneToMany(mappedBy = "predioRustico")
    private List<RenLiquidacion> renLiquidacions;
    @OneToMany(mappedBy = "predioRustico", fetch = FetchType.LAZY)
    private Collection<FnSolicitudCondonacion> solicitudCondonacionCollection;
    @OneToMany(mappedBy = "predioRustico", fetch = FetchType.LAZY)
    private Collection<FnSolicitudExoneracion> solicitudExoneracionCollection;
    @OneToMany(mappedBy = "predioRural", fetch = FetchType.LAZY)
    private Collection<FnSolicitudExoneracionPredios> predioRuralCollection;
    
    public CatPredioRustico() {
    }

    public CatPredioRustico(Long id) {
        this.id = id;
    }

    public Collection<FnSolicitudExoneracionPredios> getPredioRuralCollection() {
        return predioRuralCollection;
    }

    public void setPredioRuralCollection(Collection<FnSolicitudExoneracionPredios> predioRuralCollection) {
        this.predioRuralCollection = predioRuralCollection;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegCatastral() {
        return regCatastral;
    }

    public void setRegCatastral(String regCatastral) {
        this.regCatastral = regCatastral;
    }

    public String getIdPredial() {
        return idPredial;
    }

    public void setIdPredial(String idPredial) {
        this.idPredial = idPredial;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public Integer getPoligono() {
        return poligono;
    }

    public void setPoligono(Integer poligono) {
        this.poligono = poligono;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombrePredio() {
        return nombrePredio;
    }

    public void setNombrePredio(String nombrePredio) {
        this.nombrePredio = nombrePredio;
    }

    public BigDecimal getSuperficie() {
        return superficie;
    }

    public void setSuperficie(BigDecimal superficie) {
        this.superficie = superficie;
    }

    public BigDecimal getAvaluoCatastral() {
        return avaluoCatastral;
    }

    public void setAvaluoCatastral(BigDecimal avaluoCatastral) {
        this.avaluoCatastral = avaluoCatastral;
    }

    public BigInteger getTipo() {
        return tipo;
    }

    public void setTipo(BigInteger tipo) {
        this.tipo = tipo;
    }

    public BigInteger getInfluencia() {
        return influencia;
    }

    public void setInfluencia(BigInteger influencia) {
        this.influencia = influencia;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public BigDecimal getRebaja() {
        return rebaja;
    }

    public void setRebaja(BigDecimal rebaja) {
        this.rebaja = rebaja;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public BigInteger getNumPredioRustico() {
        return numPredioRustico;
    }

    public void setNumPredioRustico(BigInteger numPredioRustico) {
        this.numPredioRustico = numPredioRustico;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTenencia() {
        return tenencia;
    }

    public void setTenencia(String tenencia) {
        this.tenencia = tenencia;
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }

    
    public List<HistoricoTramiteDet> getHistoricoTramiteDets() {
        return historicoTramiteDets;
    }

    public void setHistoricoTramiteDets(List<HistoricoTramiteDet> historicoTramiteDets) {
        this.historicoTramiteDets = historicoTramiteDets;
    }

    public List<RenLiquidacion> getRenLiquidacions() {
        return renLiquidacions;
    }

    public void setRenLiquidacions(List<RenLiquidacion> renLiquidacions) {
        this.renLiquidacions = renLiquidacions;
    }

    public Collection<FnSolicitudCondonacion> getSolicitudCondonacionCollection() {
        return solicitudCondonacionCollection;
    }

    public void setSolicitudCondonacionCollection(Collection<FnSolicitudCondonacion> solicitudCondonacionCollection) {
        this.solicitudCondonacionCollection = solicitudCondonacionCollection;
    }

    public Collection<FnSolicitudExoneracion> getSolicitudExoneracionCollection() {
        return solicitudExoneracionCollection;
    }

    public void setSolicitudExoneracionCollection(Collection<FnSolicitudExoneracion> solicitudExoneracionCollection) {
        this.solicitudExoneracionCollection = solicitudExoneracionCollection;
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
        if (!(object instanceof CatPredioRustico)) {
            return false;
        }
        CatPredioRustico other = (CatPredioRustico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatPredioRustico[ id=" + id + " ]";
    }

    
    
}
