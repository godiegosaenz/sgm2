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
@Table(name = "cat_edf_categ_prop", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatEdfCategProp.findAll", query = "SELECT c FROM CatEdfCategProp c")
    ,
    @NamedQuery(name = "CatEdfCategProp.findById", query = "SELECT c FROM CatEdfCategProp c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatEdfCategProp.findByNombre", query = "SELECT c FROM CatEdfCategProp c WHERE c.nombre = :nombre")
    ,
    @NamedQuery(name = "CatEdfCategProp.findByIsPorcentual", query = "SELECT c FROM CatEdfCategProp c WHERE c.isPorcentual = :isPorcentual")
    ,
    @NamedQuery(name = "CatEdfCategProp.findByGuiOrden", query = "SELECT c FROM CatEdfCategProp c WHERE c.guiOrden = :guiOrden")
    ,
    @NamedQuery(name = "CatEdfCategProp.findByPeso", query = "SELECT c FROM CatEdfCategProp c WHERE c.peso = :peso")})
public class CatEdfCategProp implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_porcentual", nullable = false)
    private boolean isPorcentual;
    @Basic(optional = false)
    @NotNull
    @Column(name = "gui_orden", nullable = false)
    @Expose
    private short guiOrden;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "peso", precision = 15, scale = 2)
    private BigDecimal peso;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria", fetch = FetchType.LAZY)
    @OrderBy(clause = "orden ASC")
    private Collection<CatEdfProp> catEdfPropCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "espec", fetch = FetchType.LAZY)
    private Collection<AvalDepreciacionSolar> AvalDepreciacionSolar;
    @Column(name = "tipo_estruc")
    @Expose
    private String tipoEstruc;


    @Transient
    private List<FichaModel> modelEdificaciones;

    @Transient
    private Collection<CatEdfProp> catEdfPropCollection1;

    public CatEdfCategProp() {
    }

    public CatEdfCategProp(Long id) {
        this.id = id;
    }

    public CatEdfCategProp(Long id, String nombre, boolean isPorcentual, short guiOrden) {
        this.id = id;
        this.nombre = nombre;
        this.isPorcentual = isPorcentual;
        this.guiOrden = guiOrden;
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

    public boolean getIsPorcentual() {
        return isPorcentual;
    }

    public void setIsPorcentual(boolean isPorcentual) {
        this.isPorcentual = isPorcentual;
    }

    public short getGuiOrden() {
        return guiOrden;
    }

    public void setGuiOrden(short guiOrden) {
        this.guiOrden = guiOrden;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatEdfProp> getCatEdfPropCollection() {
        return catEdfPropCollection;
    }

    public void setCatEdfPropCollection(Collection<CatEdfProp> catEdfPropCollection) {
        this.catEdfPropCollection = catEdfPropCollection;
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


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CatEdfCategProp)) {
            return false;
        }
        CatEdfCategProp other = (CatEdfCategProp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CatEdfCategProp[ id=" + id + " ]";
    }

    public Collection<AvalDepreciacionSolar> getAvalDepreciacionSolar() {
        return AvalDepreciacionSolar;
    }

    public void setAvalDepreciacionSolar(Collection<AvalDepreciacionSolar> AvalDepreciacionSolar) {
        this.AvalDepreciacionSolar = AvalDepreciacionSolar;
    }

    public Collection<CatEdfProp> getCatEdfPropCollection1() {
        return catEdfPropCollection1;
    }

    public void setCatEdfPropCollection1(Collection<CatEdfProp> catEdfPropCollection1) {
        this.catEdfPropCollection1 = catEdfPropCollection1;
    }

}
