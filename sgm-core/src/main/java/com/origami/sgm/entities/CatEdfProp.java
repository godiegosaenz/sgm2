/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.predio.models.FichaModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.OrderBy;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_edf_prop", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatEdfProp.findAll", query = "SELECT c FROM CatEdfProp c")
    ,
    @NamedQuery(name = "CatEdfProp.findById", query = "SELECT c FROM CatEdfProp c WHERE c.id = :id")

    ,
    @NamedQuery(name = "CatEdfProp.findByNombre", query = "SELECT c FROM CatEdfProp c WHERE c.nombre = :nombre")
    ,
    @NamedQuery(name = "CatEdfProp.findByPeso", query = "SELECT c FROM CatEdfProp c WHERE c.peso = :peso")})
public class CatEdfProp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "nombre", nullable = false, length = 60)
    @Expose
    private String nombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "peso", precision = 15, scale = 2)
    private BigDecimal peso;
    @Column(name = "orden")
    @Expose
    private BigInteger orden;
    @Column(name = "tipo_estruc")
    @Expose
    private String tipoEstruc;

  
    @Transient
    private List<FichaModel> modelEdificaciones;

    @OneToMany(mappedBy = "propCubierta", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection;
    @OneToMany(mappedBy = "propPlantaalta", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection1;
    @OneToMany(mappedBy = "propParedes", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection2;
    @OneToMany(mappedBy = "propInstalaciones", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection3;
    @OneToMany(mappedBy = "propEstructura", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection4;
    @OneToMany(mappedBy = "propPlantabaja", fetch = FetchType.LAZY)
    private Collection<PePermiso> pePermisoCollection5;
    @Expose
    @JoinColumn(name = "categoria", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @OrderBy(clause = "gui_orden ASC")
    private CatEdfCategProp categoria;
    @OneToMany(mappedBy = "instalaciones", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection;
    @OneToMany(mappedBy = "plantaAlta", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection1;
    @OneToMany(mappedBy = "paredes", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection2;
    @OneToMany(mappedBy = "plantaBaja", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection3;
    @OneToMany(mappedBy = "cubierta", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection4;
    @OneToMany(mappedBy = "estructura", fetch = FetchType.LAZY)
    private Collection<PePermisosAdicionales> pePermisosAdicionalesCollection5;
    @OneToMany(mappedBy = "propCubierta", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection;
    @OneToMany(mappedBy = "propPlantaalta", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection1;
    @OneToMany(mappedBy = "propParedes", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection2;
    @OneToMany(mappedBy = "propInstalaciones", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection3;
    @OneToMany(mappedBy = "propEstructura", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection4;
    @OneToMany(mappedBy = "propPlantabaja", fetch = FetchType.LAZY)
    private Collection<PeEstructuraEspecial> peEstructuraEspecialCollection5;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "caracteristica", fetch = FetchType.LAZY)
    private Collection<PeDetalleInspeccion> peDetalleInspeccionCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prop", fetch = FetchType.LAZY)
    private Collection<CatPredioEdificacionProp> catPredioEdificacionPropCollection;
    @OneToMany(mappedBy = "propCubierta", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection;
    @OneToMany(mappedBy = "propPlantaAlta", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection1;
    @OneToMany(mappedBy = "propPlantaBaja", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection2;
    @OneToMany(mappedBy = "propParedes", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection3;
    @OneToMany(mappedBy = "propInstalaciones", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection4;
    @OneToMany(mappedBy = "propEstructura", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection5;

//    private Long idCategoria

    public CatEdfProp() {
    }

    public CatEdfProp(Long id) {
        this.id = id;
    }

    public CatEdfProp(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection() {
        return pePermisoCollection;
    }

    public void setPePermisoCollection(Collection<PePermiso> pePermisoCollection) {
        this.pePermisoCollection = pePermisoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection1() {
        return pePermisoCollection1;
    }

    public void setPePermisoCollection1(Collection<PePermiso> pePermisoCollection1) {
        this.pePermisoCollection1 = pePermisoCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection2() {
        return pePermisoCollection2;
    }

    public void setPePermisoCollection2(Collection<PePermiso> pePermisoCollection2) {
        this.pePermisoCollection2 = pePermisoCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection3() {
        return pePermisoCollection3;
    }

    public void setPePermisoCollection3(Collection<PePermiso> pePermisoCollection3) {
        this.pePermisoCollection3 = pePermisoCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection4() {
        return pePermisoCollection4;
    }

    public void setPePermisoCollection4(Collection<PePermiso> pePermisoCollection4) {
        this.pePermisoCollection4 = pePermisoCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermiso> getPePermisoCollection5() {
        return pePermisoCollection5;
    }

    public void setPePermisoCollection5(Collection<PePermiso> pePermisoCollection5) {
        this.pePermisoCollection5 = pePermisoCollection5;
    }

    public CatEdfCategProp getCategoria() {
        return categoria;
    }

    public void setCategoria(CatEdfCategProp categoria) {
        this.categoria = categoria;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection() {
        return pePermisosAdicionalesCollection;
    }

    public void setPePermisosAdicionalesCollection(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection) {
        this.pePermisosAdicionalesCollection = pePermisosAdicionalesCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection1() {
        return pePermisosAdicionalesCollection1;
    }

    public void setPePermisosAdicionalesCollection1(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection1) {
        this.pePermisosAdicionalesCollection1 = pePermisosAdicionalesCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection2() {
        return pePermisosAdicionalesCollection2;
    }

    public void setPePermisosAdicionalesCollection2(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection2) {
        this.pePermisosAdicionalesCollection2 = pePermisosAdicionalesCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection3() {
        return pePermisosAdicionalesCollection3;
    }

    public void setPePermisosAdicionalesCollection3(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection3) {
        this.pePermisosAdicionalesCollection3 = pePermisosAdicionalesCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection4() {
        return pePermisosAdicionalesCollection4;
    }

    public void setPePermisosAdicionalesCollection4(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection4) {
        this.pePermisosAdicionalesCollection4 = pePermisosAdicionalesCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PePermisosAdicionales> getPePermisosAdicionalesCollection5() {
        return pePermisosAdicionalesCollection5;
    }

    public void setPePermisosAdicionalesCollection5(Collection<PePermisosAdicionales> pePermisosAdicionalesCollection5) {
        this.pePermisosAdicionalesCollection5 = pePermisosAdicionalesCollection5;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection() {
        return peEstructuraEspecialCollection;
    }

    public void setPeEstructuraEspecialCollection(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection) {
        this.peEstructuraEspecialCollection = peEstructuraEspecialCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection1() {
        return peEstructuraEspecialCollection1;
    }

    public void setPeEstructuraEspecialCollection1(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection1) {
        this.peEstructuraEspecialCollection1 = peEstructuraEspecialCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection2() {
        return peEstructuraEspecialCollection2;
    }

    public void setPeEstructuraEspecialCollection2(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection2) {
        this.peEstructuraEspecialCollection2 = peEstructuraEspecialCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection3() {
        return peEstructuraEspecialCollection3;
    }

    public void setPeEstructuraEspecialCollection3(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection3) {
        this.peEstructuraEspecialCollection3 = peEstructuraEspecialCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection4() {
        return peEstructuraEspecialCollection4;
    }

    public void setPeEstructuraEspecialCollection4(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection4) {
        this.peEstructuraEspecialCollection4 = peEstructuraEspecialCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeEstructuraEspecial> getPeEstructuraEspecialCollection5() {
        return peEstructuraEspecialCollection5;
    }

    public void setPeEstructuraEspecialCollection5(Collection<PeEstructuraEspecial> peEstructuraEspecialCollection5) {
        this.peEstructuraEspecialCollection5 = peEstructuraEspecialCollection5;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeDetalleInspeccion> getPeDetalleInspeccionCollection() {
        return peDetalleInspeccionCollection;
    }

    public void setPeDetalleInspeccionCollection(Collection<PeDetalleInspeccion> peDetalleInspeccionCollection) {
        this.peDetalleInspeccionCollection = peDetalleInspeccionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioEdificacionProp> getCatPredioEdificacionPropCollection() {
        return catPredioEdificacionPropCollection;
    }

    public void setCatPredioEdificacionPropCollection(Collection<CatPredioEdificacionProp> catPredioEdificacionPropCollection) {
        this.catPredioEdificacionPropCollection = catPredioEdificacionPropCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection() {
        return peInspeccionFinalCollection;
    }

    public void setPeInspeccionFinalCollection(Collection<PeInspeccionFinal> peInspeccionFinalCollection) {
        this.peInspeccionFinalCollection = peInspeccionFinalCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection1() {
        return peInspeccionFinalCollection1;
    }

    public void setPeInspeccionFinalCollection1(Collection<PeInspeccionFinal> peInspeccionFinalCollection1) {
        this.peInspeccionFinalCollection1 = peInspeccionFinalCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection2() {
        return peInspeccionFinalCollection2;
    }

    public void setPeInspeccionFinalCollection2(Collection<PeInspeccionFinal> peInspeccionFinalCollection2) {
        this.peInspeccionFinalCollection2 = peInspeccionFinalCollection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection3() {
        return peInspeccionFinalCollection3;
    }

    public void setPeInspeccionFinalCollection3(Collection<PeInspeccionFinal> peInspeccionFinalCollection3) {
        this.peInspeccionFinalCollection3 = peInspeccionFinalCollection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection4() {
        return peInspeccionFinalCollection4;
    }

    public void setPeInspeccionFinalCollection4(Collection<PeInspeccionFinal> peInspeccionFinalCollection4) {
        this.peInspeccionFinalCollection4 = peInspeccionFinalCollection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection5() {
        return peInspeccionFinalCollection5;
    }

    public void setPeInspeccionFinalCollection5(Collection<PeInspeccionFinal> peInspeccionFinalCollection5) {
        this.peInspeccionFinalCollection5 = peInspeccionFinalCollection5;
    }

    public BigInteger getOrden() {
        return orden;
    }

    public void setOrden(BigInteger orden) {
        this.orden = orden;
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
        if (!(object instanceof CatEdfProp)) {
            return false;
        }
        CatEdfProp other = (CatEdfProp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatEdfProp[ id=" + id + " ]";
    }

    public List<FichaModel> getModelEdificaciones() {
        return modelEdificaciones;
    }

    public void setModelEdificaciones(List<FichaModel> modelEdificaciones) {
        this.modelEdificaciones = modelEdificaciones;
    }

    public String getTipoEstruc() {
        return tipoEstruc;
    }

    public void setTipoEstruc(String tipoEstruc) {
        this.tipoEstruc = tipoEstruc;
    }

}
