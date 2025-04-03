/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author OrigamiV2
 */
@Entity
@Table(name = "cat_predio_fusion_division",  schema = SchemasConfig.APP1)
@XmlRootElement
public class CatPredioFusionDivision implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name="estado")
    private Boolean estado;
    
    @JoinColumn(name = "predio_raiz", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predioRaiz;
    @JoinColumn(name = "predio_resultante", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predioResultante;
    
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose(serialize = true, deserialize = false)
    private Date fechaIngreso;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipo;

    public CatPredioFusionDivision() {
    }

    public CatPredioFusionDivision(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public CatPredio getPredioRaiz() {
        return predioRaiz;
    }

    public void setPredioRaiz(CatPredio predioRaiz) {
        this.predioRaiz = predioRaiz;
    }

    public CatPredio getPredioResultante() {
        return predioResultante;
    }

    public void setPredioResultante(CatPredio predioResultante) {
        this.predioResultante = predioResultante;
    }

    public CtlgItem getTipo() {
        return tipo;
    }

    public void setTipo(CtlgItem tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof CatPredioFusionDivision)) {
            return false;
        }
        CatPredioFusionDivision other = (CatPredioFusionDivision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatPredioFusionDivision[ id=" + id + " ]";
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }    
    
}
