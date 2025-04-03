/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.AclUser;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_categorias_construccion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatCategoriasConstruccion.findAll", query = "SELECT c FROM CatCategoriasConstruccion c"),
    @NamedQuery(name = "CatCategoriasConstruccion.findById", query = "SELECT c FROM CatCategoriasConstruccion c WHERE c.id = :id"),
    @NamedQuery(name = "CatCategoriasConstruccion.findByCodigoCategoria", query = "SELECT c FROM CatCategoriasConstruccion c WHERE c.codigoCategoria = :codigoCategoria"),
    @NamedQuery(name = "CatCategoriasConstruccion.findByDescripcion", query = "SELECT c FROM CatCategoriasConstruccion c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "CatCategoriasConstruccion.findByValorMt2", query = "SELECT c FROM CatCategoriasConstruccion c WHERE c.valorMt2 = :valorMt2"),
    @NamedQuery(name = "CatCategoriasConstruccion.findByFechaIngreso", query = "SELECT c FROM CatCategoriasConstruccion c WHERE c.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "CatCategoriasConstruccion.findByVidautil", query = "SELECT c FROM CatCategoriasConstruccion c WHERE c.vidautil = :vidautil")})
public class CatCategoriasConstruccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Column(name = "codigo_categoria")
    @Expose
    private Integer codigoCategoria;
    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    @Expose
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_mt2", precision = 15, scale = 2)
    @Expose
    private BigDecimal valorMt2;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "vidautil")
    @Expose
    private Integer vidautil;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuario;
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private Collection<CatCategoriasConstruccionValores> catCategoriasConstruccionValoresCollection;
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private Collection<CatPredioEdificacion> catPredioEdificacionCollection;

    public CatCategoriasConstruccion() {
    }

    public CatCategoriasConstruccion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(Integer codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getValorMt2() {
        return valorMt2;
    }

    public void setValorMt2(BigDecimal valorMt2) {
        this.valorMt2 = valorMt2;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getVidautil() {
        return vidautil;
    }

    public void setVidautil(Integer vidautil) {
        this.vidautil = vidautil;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatCategoriasConstruccionValores> getCatCategoriasConstruccionValoresCollection() {
        return catCategoriasConstruccionValoresCollection;
    }

    public void setCatCategoriasConstruccionValoresCollection(Collection<CatCategoriasConstruccionValores> catCategoriasConstruccionValoresCollection) {
        this.catCategoriasConstruccionValoresCollection = catCategoriasConstruccionValoresCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioEdificacion> getCatPredioEdificacionCollection() {
        return catPredioEdificacionCollection;
    }

    public void setCatPredioEdificacionCollection(Collection<CatPredioEdificacion> catPredioEdificacionCollection) {
        this.catPredioEdificacionCollection = catPredioEdificacionCollection;
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
        if (!(object instanceof CatCategoriasConstruccion)) {
            return false;
        }
        CatCategoriasConstruccion other = (CatCategoriasConstruccion) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "" + id + "";
    }
    
}
