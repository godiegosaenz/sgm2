/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author XndySxnchez
 */
@Entity
@Table(name = "aval_coeficientes_suelo", schema = SchemasConfig.APP1)
@XmlRootElement
public class AvalCoeficientesSuelo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "valor_coeficiente", precision = 19, scale = 4)
    private BigDecimal valorCoeficiente;

    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @JoinColumn(name = "categoria_construccion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp categoriaConstruccion;
    @JoinColumn(name = "categoria_solar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem categoriaSolar;
    //estas columnas haace referencia a los rangos factoriales inferior y superior
    @Column(name = "valor_coef_inferior", precision = 19, scale = 4)
    private BigDecimal valorCoefInferior;
    @Column(name = "valor_coef_superior", precision = 19, scale = 4)
    private BigDecimal valorCoefSuperior;

    public AvalCoeficientesSuelo() {
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

    public AvalCoeficientesSuelo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorCoeficiente() {
        return valorCoeficiente;
    }

    public void setValorCoeficiente(BigDecimal valorCoeficiente) {
        this.valorCoeficiente = valorCoeficiente;
    }

    public CatEdfProp getCategoriaConstruccion() {
        return categoriaConstruccion;
    }

    public void setCategoriaConstruccion(CatEdfProp categoriaConstruccion) {
        this.categoriaConstruccion = categoriaConstruccion;
    }

    public CtlgItem getCategoriaSolar() {
        return categoriaSolar;
    }

    public void setCategoriaSolar(CtlgItem categoriaSolar) {
        this.categoriaSolar = categoriaSolar;
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
        if (!(object instanceof AvalCoeficientesSuelo)) {
            return false;
        }
        AvalCoeficientesSuelo other = (AvalCoeficientesSuelo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public BigDecimal getValorCoefInferior() {
        return valorCoefInferior;
    }

    public void setValorCoefInferior(BigDecimal valorCoefInferior) {
        this.valorCoefInferior = valorCoefInferior;
    }

    public BigDecimal getValorCoefSuperior() {
        return valorCoefSuperior;
    }

    public void setValorCoefSuperior(BigDecimal valorCoefSuperior) {
        this.valorCoefSuperior = valorCoefSuperior;
    }

    @Override
    public String toString() {
        return "AvalCoeficientesSuelo{" + "id=" + id + ", valorCoeficiente=" + valorCoeficiente + ", anioInicio=" + anioInicio + ", anioFin=" + anioFin + ", categoriaConstruccion=" + categoriaConstruccion + ", categoriaSolar=" + categoriaSolar + ", valorCoefInferior=" + valorCoefInferior + ", valorCoefSuperior=" + valorCoefSuperior + '}';
    }

}
