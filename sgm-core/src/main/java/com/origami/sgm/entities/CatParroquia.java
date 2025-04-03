/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatCanton;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.OrdenTrabajo;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_parroquia", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatParroquia.findAll", query = "SELECT c FROM CatParroquia c")
    ,
    @NamedQuery(name = "CatParroquia.findById", query = "SELECT c FROM CatParroquia c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatParroquia.findByCodigoParroquia", query = "SELECT c FROM CatParroquia c WHERE c.codigoParroquia = :codigoParroquia")
    ,
    @NamedQuery(name = "CatParroquia.findByDescripcion", query = "SELECT c FROM CatParroquia c WHERE c.descripcion = :descripcion")
    ,
    @NamedQuery(name = "CatParroquia.findByTipo", query = "SELECT c FROM CatParroquia c WHERE c.tipo = :tipo")})
public class CatParroquia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Column(name = "codigo_parroquia")
    private BigInteger codigoParroquia;
    @Size(max = 60)
    @Column(name = "descripcion", length = 60)
    @Expose
    private String descripcion;
    @Size(max = 3)
    @Column(name = "tipo", length = 3)
    private String tipo;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "cod_nac")
    @Expose
    private Short codNac;
    @OneToMany(mappedBy = "parroquia", fetch = FetchType.LAZY)
    private Collection<RegFicha> regFichaCollection;
    @OneToMany(mappedBy = "codParroquia", fetch = FetchType.LAZY)
    private Collection<CatCiudadela> catCiudadelaCollection;
    @JoinColumn(name = "id_canton", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCanton idCanton;
    @OneToMany(mappedBy = "parroquia", fetch = FetchType.LAZY)
    private Collection<SvSolicitudServicios> svSolicitudServiciosCollection;
    @OneToMany(mappedBy = "parroquia", fetch = FetchType.LAZY)
    private Collection<CatPredioRustico> prediosRusticosCollection;
    @OneToMany(mappedBy = "parroquia", fetch = FetchType.LAZY)
    private Collection<OrdenTrabajo> ordenTrabajoCollection;

    public CatParroquia() {
    }

    public CatParroquia(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getCodigoParroquia() {
        return codigoParroquia;
    }

    public void setCodigoParroquia(BigInteger codigoParroquia) {
        this.codigoParroquia = codigoParroquia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Short getCodNac() {
        return codNac;
    }

    public void setCodNac(Short codNac) {
        this.codNac = codNac;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegFicha> getRegFichaCollection() {
        return regFichaCollection;
    }

    public void setRegFichaCollection(Collection<RegFicha> regFichaCollection) {
        this.regFichaCollection = regFichaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatCiudadela> getCatCiudadelaCollection() {
        return catCiudadelaCollection;
    }

    public void setCatCiudadelaCollection(Collection<CatCiudadela> catCiudadelaCollection) {
        this.catCiudadelaCollection = catCiudadelaCollection;
    }

    public CatCanton getIdCanton() {
        return idCanton;
    }

    public void setIdCanton(CatCanton idCanton) {
        this.idCanton = idCanton;
    }

    public Collection<SvSolicitudServicios> getSvSolicitudServiciosCollection() {
        return svSolicitudServiciosCollection;
    }

    public void setSvSolicitudServiciosCollection(Collection<SvSolicitudServicios> svSolicitudServiciosCollection) {
        this.svSolicitudServiciosCollection = svSolicitudServiciosCollection;
    }

    public Collection<CatPredioRustico> getPrediosRusticosCollection() {
        return prediosRusticosCollection;
    }

    public void setPrediosRusticosCollection(Collection<CatPredioRustico> prediosRusticosCollection) {
        this.prediosRusticosCollection = prediosRusticosCollection;
    }

    public Collection<OrdenTrabajo> getOrdenTrabajoCollection() {
        return ordenTrabajoCollection;
    }

    public void setOrdenTrabajoCollection(Collection<OrdenTrabajo> ordenTrabajoCollection) {
        this.ordenTrabajoCollection = ordenTrabajoCollection;
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
        if (!(object instanceof CatParroquia)) {
            return false;
        }
        CatParroquia other = (CatParroquia) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatParroquia[ id=" + id + " ]";
    }

}
