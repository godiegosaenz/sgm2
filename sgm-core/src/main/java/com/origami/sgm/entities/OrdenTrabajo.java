/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
 import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "orden_trabajo", schema = SchemasConfig.FLOW, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"num_orden"})})
@XmlRootElement
@SequenceGenerator(name = "orden_seq", sequenceName = SchemasConfig.FLOW+".orden_seq", allocationSize = 1, schema = SchemasConfig.FLOW)
public class OrdenTrabajo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_seq")
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_orden", nullable = false)
    @Expose
    private Long numOrden;
    @Column(name = "fec_ini")
    @Temporal(TemporalType.DATE)
    @Expose
    private Date fecIni;
    @Column(name = "fec_fin")
    @Temporal(TemporalType.DATE)
    @Expose
    private Date fecFin;
    @Column(name = "sector")
    @Expose
    private String sector;
    @Column(name = "zona")
    @Expose
    private String zona;
    @Column(name = "mz")
    @Expose
    private String mz;
    @Column(name = "cdla")
    private String cdla;
    @Column(name = "solar")
    @Expose
    private String solar;
    @Column(name = "mzdiv")
    private String mzdiv;
    @Size(max = 2)
    @Column(name = "estado_ot", length = 2)
    @Expose
    private String estadoOt;
    @Column(name = "observaciones")
    @Expose
    private String observaciones;
    @Size(max = 100)
    @Column(name = "usr_cre", length = 100)
    @Expose
    private String usrCre;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date fecCre;
    @Size(max = 100)
    @Column(name = "usr_act", length = 100)
    private String usrAct;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date fecAct;
    @Column(name = "estado")
    @Expose
    private Boolean estado;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private AclUser responsable;
    @JoinColumn(name = "supervisor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private AclUser supervisor;
    @JoinColumn(name = "urbanizacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela urbanizacion;    
    @JoinColumn(name = "parroquia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatParroquia parroquia;
    @OneToMany(mappedBy = "orden", fetch = FetchType.LAZY)
    @Expose
    private Collection<OrdenDet> ordenDetCollection;

    public OrdenTrabajo() {
    }

    public OrdenTrabajo(Long id) {
        this.id = id;
    }

    public OrdenTrabajo(Long id, Long numOrden) {
        this.id = id;
        this.numOrden = numOrden;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(Long numOrden) {
        this.numOrden = numOrden;
    }

    public Date getFecIni() {
        return fecIni;
    }

    public void setFecIni(Date fecIni) {
        this.fecIni = fecIni;
    }

    public Date getFecFin() {
        return fecFin;
    }

    public void setFecFin(Date fecFin) {
        this.fecFin = fecFin;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public String getCdla() {
        return cdla;
    }

    public void setCdla(String cdla) {
        this.cdla = cdla;
    }

    public String getSolar() {
        return solar;
    }

    public String getMzdiv() {
        return mzdiv;
    }

    public void setMzdiv(String mzdiv) {
        this.mzdiv = mzdiv;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public void setSolar(String solar) {
        this.solar = solar;
    }

    public String getEstadoOt() {
        return estadoOt;
    }

    public void setEstadoOt(String estadoOt) {
        this.estadoOt = estadoOt;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getUsrAct() {
        return usrAct;
    }

    public void setUsrAct(String usrAct) {
        this.usrAct = usrAct;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public AclUser getResponsable() {
        return responsable;
    }

    public void setResponsable(AclUser responsable) {
        this.responsable = responsable;
    }

    public AclUser getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(AclUser supervisor) {
        this.supervisor = supervisor;
    }

    public CatCiudadela getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(CatCiudadela urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    @XmlTransient
    public Collection<OrdenDet> getOrdenDetCollection() {
        return ordenDetCollection;
    }

    public void setOrdenDetCollection(Collection<OrdenDet> ordenDetCollection) {
        this.ordenDetCollection = ordenDetCollection;
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
        if (!(object instanceof OrdenTrabajo)) {
            return false;
        }
        OrdenTrabajo other = (OrdenTrabajo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.censocat.entity.flow.OrdenTrabajo[ id=" + id + " ]";
    }

}
