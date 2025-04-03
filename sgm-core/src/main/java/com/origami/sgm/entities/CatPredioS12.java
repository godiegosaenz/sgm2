/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_predio_s12", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"predio"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioS12.findAll", query = "SELECT c FROM CatPredioS12 c")
    ,
    @NamedQuery(name = "CatPredioS12.findById", query = "SELECT c FROM CatPredioS12 c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPredioS12.findByNumPermisoConstruccion", query = "SELECT c FROM CatPredioS12 c WHERE c.numPermisoConstruccion = :numPermisoConstruccion")
    ,
    @NamedQuery(name = "CatPredioS12.findByFechaPermiso", query = "SELECT c FROM CatPredioS12 c WHERE c.fechaPermiso = :fechaPermiso")
    ,
    @NamedQuery(name = "CatPredioS12.findByResponsablePermiso", query = "SELECT c FROM CatPredioS12 c WHERE c.responsablePermiso = :responsablePermiso")
    ,
    @NamedQuery(name = "CatPredioS12.findByNumInspeccionFinal", query = "SELECT c FROM CatPredioS12 c WHERE c.numInspeccionFinal = :numInspeccionFinal")
    ,
    @NamedQuery(name = "CatPredioS12.findByFechaInspeccionFinal", query = "SELECT c FROM CatPredioS12 c WHERE c.fechaInspeccionFinal = :fechaInspeccionFinal")
    ,
    @NamedQuery(name = "CatPredioS12.findByAreaConsInspeccion", query = "SELECT c FROM CatPredioS12 c WHERE c.areaConsInspeccion = :areaConsInspeccion")
    ,
    @NamedQuery(name = "CatPredioS12.findByObservaciones", query = "SELECT c FROM CatPredioS12 c WHERE c.observaciones = :observaciones")})
public class CatPredioS12 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Size(max = 45)
    @Column(name = "num_permiso_construccion", length = 45)
    @Expose
    private String numPermisoConstruccion;
    @Column(name = "fecha_permiso")
    @Temporal(TemporalType.DATE)
    private Date fechaPermiso;
    @Size(max = 255)
    @Column(name = "responsable_permiso", length = 255)
    @Expose
    private String responsablePermiso;
    @Size(max = 45)
    @Column(name = "num_inspeccion_final", length = 45)
    @Expose
    private String numInspeccionFinal;
    @Column(name = "fecha_inspeccion_final")
    @Temporal(TemporalType.DATE)
    private Date fechaInspeccionFinal;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_cons_inspeccion", precision = 15, scale = 4)
    @Expose
    private BigDecimal areaConsInspeccion;
    @Size(max = 4000)
    @Column(name = "observaciones", length = 4000)
    @Expose
    private String observaciones;
    @JoinTable(name = "cat_predio_s12_has_usos", joinColumns = {
        @JoinColumn(name = "predio_s12", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "uso", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<CtlgItem> usosList;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;

    @Transient
    private Boolean nextStep = false;

    public CatPredioS12() {
    }

    public CatPredioS12(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumPermisoConstruccion() {
        return numPermisoConstruccion;
    }

    public void setNumPermisoConstruccion(String numPermisoConstruccion) {
        this.numPermisoConstruccion = numPermisoConstruccion;
    }

    public Date getFechaPermiso() {
        return fechaPermiso;
    }

    public void setFechaPermiso(Date fechaPermiso) {
        this.fechaPermiso = fechaPermiso;
    }

    public String getResponsablePermiso() {
        return responsablePermiso;
    }

    public void setResponsablePermiso(String responsablePermiso) {
        this.responsablePermiso = responsablePermiso;
    }

    public String getNumInspeccionFinal() {
        return numInspeccionFinal;
    }

    public void setNumInspeccionFinal(String numInspeccionFinal) {
        this.numInspeccionFinal = numInspeccionFinal;
    }

    public Date getFechaInspeccionFinal() {
        return fechaInspeccionFinal;
    }

    public void setFechaInspeccionFinal(Date fechaInspeccionFinal) {
        this.fechaInspeccionFinal = fechaInspeccionFinal;
    }

    public BigDecimal getAreaConsInspeccion() {
        return areaConsInspeccion;
    }

    public void setAreaConsInspeccion(BigDecimal areaConsInspeccion) {
        this.areaConsInspeccion = areaConsInspeccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CtlgItem> getUsosList() {
        return usosList;
    }

    public void setUsosList(Collection<CtlgItem> usosList) {
        this.usosList = usosList;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
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
        if (!(object instanceof CatPredioS12)) {
            return false;
        }
        CatPredioS12 other = (CatPredioS12) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPredioS12[ id=" + id + " ]";
    }

    public Boolean getNextStep() {
        return nextStep;
    }

    public void setNextStep(Boolean nextStep) {
        this.nextStep = nextStep;
    }

}
