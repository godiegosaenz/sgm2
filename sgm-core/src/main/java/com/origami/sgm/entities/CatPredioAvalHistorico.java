/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author XndySxnchez :v
 */
@Entity
@Table(name = "cat_predio_aval_historico", schema = SchemasConfig.APP1)
@XmlRootElement
@SequenceGenerator(name = "cat_predio_aval_historico_id_seq", sequenceName = SchemasConfig.APP1 + ".aval_historico_id_seq", allocationSize = 1)
public class CatPredioAvalHistorico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_predio_aval_historico_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "avaluo_solar", precision = 19, scale = 5)
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_construccion", precision = 19, scale = 5)
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_cultivo", precision = 19, scale = 5)
    private BigDecimal avaluoCultivo;
    @Column(name = "avaluo_municipal", precision = 19, scale = 5)
    private BigDecimal avaluoMunicipal;
    @Column(name = "suma_coeficientes", precision = 19, scale = 4)
    private BigDecimal sumaCoeficientes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @Column(name = "predio_json")
    private String predio_json;
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "predio_edificacion_json")
    private String predioEdificacionJson;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne
    private CatPredio predio;
    @Column(name = "migrada")
    private Boolean migrada;
    @Column(name = "valor_base_m2")
    private BigDecimal valorBaseM2;
    @OneToMany(mappedBy = "predioAvalHistorico", fetch = FetchType.LAZY)
    private List<CatPredioAvalDetHist> catPredioAvalDetHists;

    public CatPredioAvalHistorico() {
    }

    public CatPredioAvalHistorico(Long id) {
        this.id = id;
    }

    public CatPredioAvalHistorico(Long id, Date fechaActualizacion) {
        this.id = id;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getAvaluoCultivo() {
        return avaluoCultivo;
    }

    public void setAvaluoCultivo(BigDecimal avaluoCultivo) {
        this.avaluoCultivo = avaluoCultivo;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public String getPredio_json() {
        return predio_json;
    }

    public void setPredio_json(String predio_json) {
        this.predio_json = predio_json;
    }

    public String getPredioEdificacionJson() {
        return predioEdificacionJson;
    }

    public void setPredioEdificacionJson(String predioEdificacionJson) {
        this.predioEdificacionJson = predioEdificacionJson;
    }

    public Boolean getMigrada() {
        return migrada;
    }

    public void setMigrada(Boolean migrada) {
        this.migrada = migrada;
    }

    public BigDecimal getSumaCoeficientes() {
        return sumaCoeficientes;
    }

    public void setSumaCoeficientes(BigDecimal sumaCoeficientes) {
        this.sumaCoeficientes = sumaCoeficientes;
    }

    public BigDecimal getValorBaseM2() {
        return valorBaseM2;
    }

    public void setValorBaseM2(BigDecimal valorBaseM2) {
        this.valorBaseM2 = valorBaseM2;
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
        if (!(object instanceof CatPredioAvalHistorico)) {
            return false;
        }
        CatPredioAvalHistorico other = (CatPredioAvalHistorico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatPredioAvalHistorico[ id=" + id + " ]";
    }

    public List<CatPredioAvalDetHist> getCatPredioAvalDetHists() {
        return catPredioAvalDetHists;
    }

    public void setCatPredioAvalDetHists(List<CatPredioAvalDetHist> catPredioAvalDetHists) {
        this.catPredioAvalDetHists = catPredioAvalDetHists;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

}
