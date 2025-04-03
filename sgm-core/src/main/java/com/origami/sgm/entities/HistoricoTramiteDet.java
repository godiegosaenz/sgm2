/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "historico_tramite_det", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "HistoricoTramiteDet.findAll", query = "SELECT h FROM HistoricoTramiteDet h")})
public class HistoricoTramiteDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "estado")
    private Boolean estado = Boolean.FALSE;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;
    @Column(name = "avaluo_solar", precision = 19, scale = 4)
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_construccion", precision = 19, scale = 4)
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_edificacion", precision = 19, scale = 4)
    private BigDecimal avaluoEdificacion;
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    @Column(name = "total", precision = 19, scale = 2)
    private BigDecimal total;
    @JoinColumn(name = "firma", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PeFirma firma;
    @Column(name = "area_edificacion", precision = 18, scale = 4)
    private BigDecimal areaEdificacion;
    @Column(name = "base_calculo", precision = 8, scale = 3)
    private BigDecimal baseCalculo;
    @Column(name = "num_tasa")
    private BigInteger numTasa;
    @Column(name = "num_pisos_sobre_bord")
    private BigInteger numPisosSobreBord;
    @Column(name = "num_pisos_bajo_bord")
    private BigInteger numPisosBajoBord;
    @Column(name = "pres_obra", precision = 14, scale = 4)
    private BigDecimal presObra;
    @Column(name = "num_planos")
    private BigInteger numPlanos;
    @Column(name = "area_exedente", precision = 10, scale = 4)
    private BigDecimal areaExedente;
    @Column(name = "avaluo_propiedad", precision = 19, scale = 4)
    private BigDecimal avaluoPropiedad;
    @Column(name = "responsable", length = 200)
    private String responsable;
    @Column(name = "carta_adosamiento", nullable = false)
    private boolean cartaAdosamiento;
    @OneToMany(mappedBy = "htd", fetch = FetchType.LAZY)
    private Collection<ProcesoFusionPredios> procesoFusionPrediosCollection;

    @Column(name = "valor_x_metro", precision = 18, scale = 4)
    private BigDecimal valorXMetro;

    @Column(name = "depreciacion", precision = 18, scale = 4)
    private BigDecimal depreciacion;

    @JoinColumn(name = "predio_rustico", referencedColumnName = "id")
    @ManyToOne
    private CatPredioRustico predioRustico;

    @Column(name = "to_json", columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String json;

    @Transient
    private List<Observaciones> observaciones;

    public HistoricoTramiteDet() {
        this.estado = Boolean.FALSE;
    }

    public HistoricoTramiteDet(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    /**
     *
     * @return
     */
    public BigDecimal getAvaluoEdificacion() {
        return avaluoEdificacion;
    }

    public void setAvaluoEdificacion(BigDecimal avaluoEdificacion) {
        this.avaluoEdificacion = avaluoEdificacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public PeFirma getFirma() {
        return firma;
    }

    public void setFirma(PeFirma firma) {
        this.firma = firma;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    /**
     * PRESICION DE 12,4
     *
     * @return
     */
    public BigDecimal getAreaEdificacion() {
        return areaEdificacion;
    }

    public void setAreaEdificacion(BigDecimal areaEdificacion) {
        this.areaEdificacion = areaEdificacion;
    }

    public BigDecimal getAvaluoPropiedad() {
        return avaluoPropiedad;
    }

    public void setAvaluoPropiedad(BigDecimal avaluoPropiedad) {
        this.avaluoPropiedad = avaluoPropiedad;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public boolean isCartaAdosamiento() {
        return cartaAdosamiento;
    }

    public void setCartaAdosamiento(boolean cartaAdosamiento) {
        this.cartaAdosamiento = cartaAdosamiento;
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
        if (!(object instanceof HistoricoTramiteDet)) {
            return false;
        }
        HistoricoTramiteDet other = (HistoricoTramiteDet) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.HistoricoTramiteDet[ id=" + id + " ]";
    }

    public BigInteger getNumTasa() {
        return numTasa;
    }

    public void setNumTasa(BigInteger numTasa) {
        this.numTasa = numTasa;
    }

    public BigInteger getNumPisosSobreBord() {
        return numPisosSobreBord;
    }

    public void setNumPisosSobreBord(BigInteger numPisosSobreBord) {
        this.numPisosSobreBord = numPisosSobreBord;
    }

    public BigInteger getNumPisosBajoBord() {
        return numPisosBajoBord;
    }

    public void setNumPisosBajoBord(BigInteger numPisosBajoBord) {
        this.numPisosBajoBord = numPisosBajoBord;
    }

    public BigDecimal getPresObra() {
        return presObra;
    }

    public void setPresObra(BigDecimal presObra) {
        this.presObra = presObra;
    }

    public BigInteger getNumPlanos() {
        return numPlanos;
    }

    public void setNumPlanos(BigInteger numPlanos) {
        this.numPlanos = numPlanos;
    }

    public BigDecimal getAreaExedente() {
        return areaExedente;
    }

    public void setAreaExedente(BigDecimal areaExedente) {
        this.areaExedente = areaExedente;
    }

    public Collection<ProcesoFusionPredios> getProcesoFusionPrediosCollection() {
        return procesoFusionPrediosCollection;
    }

    public void setProcesoFusionPrediosCollection(Collection<ProcesoFusionPredios> procesoFusionPrediosCollection) {
        this.procesoFusionPrediosCollection = procesoFusionPrediosCollection;
    }

    public BigDecimal getValorXMetro() {
        return valorXMetro;
    }

    public void setValorXMetro(BigDecimal valorXMetro) {
        this.valorXMetro = valorXMetro;
    }

    public BigDecimal getDepreciacion() {
        return depreciacion;
    }

    public void setDepreciacion(BigDecimal depreciacion) {
        this.depreciacion = depreciacion;
    }

    public CatPredioRustico getPredioRustico() {
        return predioRustico;
    }

    public void setPredioRustico(CatPredioRustico predioRustico) {
        this.predioRustico = predioRustico;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<Observaciones> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(List<Observaciones> observaciones) {
        this.observaciones = observaciones;
    }

}
