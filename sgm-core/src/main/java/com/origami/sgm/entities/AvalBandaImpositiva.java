/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author OrigamiV2
 */
@Entity
@Table(name = "aval_banda_impositiva", schema = SchemasConfig.APP1)

public class AvalBandaImpositiva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    @Column(name = "multiplo_impuesto_predial", precision = 19, scale = 5)
    private BigDecimal multiploImpuestoPredial;
    @Column(name = "hasta_us", precision = 19, scale = 5)
    private BigDecimal hastaUs;
    @Column(name = "desde_us", precision = 19, scale = 5)
    private BigDecimal desdeUs;
    @Column(name = "fraccion_basica", precision = 19, scale = 5)
    private Double fracionBasica;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @Column(name = "estado", length = 1)
    private String estado;
    @Column(name = "predeterminada")
    private Boolean predeterminada = Boolean.FALSE;
    @OneToMany(mappedBy = "bandaImpositiva")
    private List<AvalImpuestoPredios> avalImpuestoPredioss;

    public AvalBandaImpositiva() {
    }

    public AvalBandaImpositiva(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMultiploImpuestoPredial() {
        return multiploImpuestoPredial;
    }

    public void setMultiploImpuestoPredial(BigDecimal multiploImpuestoPredial) {
        this.multiploImpuestoPredial = multiploImpuestoPredial;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getPredeterminada() {
        return predeterminada;
    }

    public void setPredeterminada(Boolean predeterminada) {
        this.predeterminada = predeterminada;
    }

    public List<AvalImpuestoPredios> getAvalImpuestoPredioss() {
        return avalImpuestoPredioss;
    }

    public void setAvalImpuestoPredioss(List<AvalImpuestoPredios> avalImpuestoPredioss) {
        this.avalImpuestoPredioss = avalImpuestoPredioss;
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
        if (!(object instanceof AvalBandaImpositiva)) {
            return false;
        }
        AvalBandaImpositiva other = (AvalBandaImpositiva) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public BigDecimal getHastaUs() {
        return hastaUs;
    }

    public void setHastaUs(BigDecimal hastaUs) {
        this.hastaUs = hastaUs;
    }

    public BigDecimal getDesdeUs() {
        return desdeUs;
    }

    public void setDesdeUs(BigDecimal desdeUs) {
        this.desdeUs = desdeUs;
    }

    public Double getFracionBasica() {
        return fracionBasica;
    }

    public void setFracionBasica(Double fracionBasica) {
        this.fracionBasica = fracionBasica;
    }

    @Override
    public String toString() {
        return "AvalBandaImpositiva{" + "id=" + id + ", multiploImpuestoPredial=" + multiploImpuestoPredial + ", hastaUs=" + hastaUs + ", desdeUs=" + desdeUs + ", fracionBasica=" + fracionBasica + ", anioInicio=" + anioInicio + ", anioFin=" + anioFin + ", estado=" + estado + ", predeterminada=" + predeterminada + ", avalImpuestoPredioss=" + avalImpuestoPredioss + '}';
    }
    
    

}
