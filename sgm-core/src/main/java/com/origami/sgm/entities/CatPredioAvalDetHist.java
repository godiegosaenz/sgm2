/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author andysanchez
 */
@Entity
@Table(name = "cat_predio_aval_det_hist", schema = SchemasConfig.APP1)
@SequenceGenerator(name = "cat_predio_aval_det_hist_seq", 
        sequenceName = SchemasConfig.APP1 + ".cat_predio_aval_det_hist_seq", allocationSize = 1)
public class CatPredioAvalDetHist implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_predio_aval_det_hist_seq")
    @Column(name = "id")
    private BigDecimal id;
    @Column(name = "area_construccion", precision = 19, scale = 4)
    private BigDecimal areaConstruccion;
    @Column(name = "area_solar", precision = 19, scale = 4)
    private BigDecimal areaSolar;
    @Column(name = "depreciacion", precision = 19, scale = 4)
    private BigDecimal depreciacion;
    @Column(name = "suma_coeficientes_solar", precision = 19, scale = 4)
    private BigDecimal sumaCoeficientesSolar;
    @Column(name = "suma_coeficientes_construccion", precision = 19, scale = 4)
    private BigDecimal sumaCoeficientesConstruccion;
    @JoinColumn(name = "estado_conservacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem estadoConservacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "no_edificacion", nullable = false)
    private Short noEdificacion;
    @Column(name = "num_pisos")
    private Short numPisos;
    
    @JoinColumn(name = "predio_aval_historico", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredioAvalHistorico predioAvalHistorico;

    public CatPredioAvalDetHist() {
    }

    public CatPredioAvalDetHist(BigDecimal id) {
        this.id = id;
    }

    

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getDepreciacion() {
        return depreciacion;
    }

    public void setDepreciacion(BigDecimal depreciacion) {
        this.depreciacion = depreciacion;
    }

    public BigDecimal getSumaCoeficientesSolar() {
        return sumaCoeficientesSolar;
    }

    public void setSumaCoeficientesSolar(BigDecimal sumaCoeficientesSolar) {
        this.sumaCoeficientesSolar = sumaCoeficientesSolar;
    }

    public BigDecimal getSumaCoeficientesConstruccion() {
        return sumaCoeficientesConstruccion;
    }

    public void setSumaCoeficientesConstruccion(BigDecimal sumaCoeficientesConstruccion) {
        this.sumaCoeficientesConstruccion = sumaCoeficientesConstruccion;
    }

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public CatPredioAvalHistorico getPredioAvalHistorico() {
        return predioAvalHistorico;
    }

    public void setPredioAvalHistorico(CatPredioAvalHistorico predioAvalHistorico) {
        this.predioAvalHistorico = predioAvalHistorico;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CatPredioAvalDetHist)) {
            return false;
        }
        CatPredioAvalDetHist other = (CatPredioAvalDetHist) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatPredioAvalDetHist[ id=" + id + " ]";
    }

    public CtlgItem getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(CtlgItem estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }

    public Short getNoEdificacion() {
        return noEdificacion;
    }

    public void setNoEdificacion(Short noEdificacion) {
        this.noEdificacion = noEdificacion;
    }

    public Short getNumPisos() {
        return numPisos;
    }

    public void setNumPisos(Short numPisos) {
        this.numPisos = numPisos;
    }
    
    
    
}
