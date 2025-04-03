/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEdfProp;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_predio_edificacion_prop", schema = SchemasConfig.APP1
// , uniqueConstraints = {@UniqueConstraint(columnNames = {"edificacion", "prop"})}
)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioEdificacionProp.findAll", query = "SELECT c FROM CatPredioEdificacionProp c WHERE c.edificacion.id = :idEdificacion ")
    ,
    @NamedQuery(name = "CatPredioEdificacionProp.findById", query = "SELECT c FROM CatPredioEdificacionProp c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPredioEdificacionProp.findByPorcentaje", query = "SELECT c FROM CatPredioEdificacionProp c WHERE c.porcentaje = :porcentaje")})
public class CatPredioEdificacionProp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "porcentaje", precision = 8, scale = 2)
    private BigDecimal porcentaje;
    @JoinColumn(name = "edificacion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredioEdificacion edificacion;

    @JoinColumn(name = "prop", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Expose
    private CatEdfProp prop;

    @Basic(optional = false)
    @Column(name = "estado", nullable = false)
    @Expose
    @Where(clause = "estado = " + SchemasConfig.FILTER_ESTADO)
    private Boolean estado;
    @Column(name = "fecha", nullable = false)
    @Expose
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "usuario", nullable = false)
    @Expose
    private String usuario;

    @Transient
    @Expose
    private Long idCategoria;

    public CatPredioEdificacionProp() {
    }

    public CatPredioEdificacionProp(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public CatPredioEdificacion getEdificacion() {
        return edificacion;
    }

    public void setEdificacion(CatPredioEdificacion edificacion) {
        this.edificacion = edificacion;
    }

    public CatEdfProp getProp() {
        return prop;
    }

    public void setProp(CatEdfProp prop) {
        this.prop = prop;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof CatPredioEdificacionProp)) {
            return false;
        }
        CatPredioEdificacionProp other = (CatPredioEdificacionProp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPredioEdificacionProp[ id=" + id + " ]";
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

}
