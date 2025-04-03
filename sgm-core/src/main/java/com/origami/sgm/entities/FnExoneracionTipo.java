/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import com.origami.sgm.entities.CatCategoriasPredio;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
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
 * @author Angel Navarro
 * @Date 17/05/2016
 */
@Entity
@Table(name = "fn_exoneracion_tipo", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FnExoneracionTipo.findAll", query = "SELECT f FROM FnExoneracionTipo f"),
    @NamedQuery(name = "FnExoneracionTipo.findById", query = "SELECT f FROM FnExoneracionTipo f WHERE f.id = :id"),
    @NamedQuery(name = "FnExoneracionTipo.findByDescripcion", query = "SELECT f FROM FnExoneracionTipo f WHERE f.descripcion = :descripcion"),
    @NamedQuery(name = "FnExoneracionTipo.findByReglamento", query = "SELECT f FROM FnExoneracionTipo f WHERE f.reglamento = :reglamento"),
    @NamedQuery(name = "FnExoneracionTipo.findByEstado", query = "SELECT f FROM FnExoneracionTipo f WHERE f.estado = :estado"),
    @NamedQuery(name = "FnExoneracionTipo.findByUsuarioCreacion", query = "SELECT f FROM FnExoneracionTipo f WHERE f.usuarioCreacion = :usuarioCreacion"),
    @NamedQuery(name = "FnExoneracionTipo.findByFechaIngreso", query = "SELECT f FROM FnExoneracionTipo f WHERE f.fechaIngreso = :fechaIngreso")})
public class FnExoneracionTipo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 250)
    @Column(name = "descripcion", length = 250)
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "reglamento", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String reglamento;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 25)
    @Column(name = "usuario_creacion", length = 25)
    private String usuarioCreacion;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    
    @JoinColumn(name = "categoria_predio", referencedColumnName = "id")
    @ManyToOne
    private CatCategoriasPredio categoriaPredio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exoneracionTipo")
    private Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection;
    @JoinColumn(name = "exoneracion_clase", referencedColumnName = "id")
    @ManyToOne
    private FnExoneracionClase exoneracionClase;
    @Column(name ="aplica")
    private String aplica;
    @Column(name = "valida_remuneracion")
    private Boolean validaRemuneracion=Boolean.TRUE;

    @Column(name ="orden")
    private Integer orden;
    
    public FnExoneracionTipo() {
    }

    public FnExoneracionTipo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReglamento() {
        return reglamento;
    }

    public void setReglamento(String reglamento) {
        this.reglamento = reglamento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public CatCategoriasPredio getCategoriaPredio() {
        return categoriaPredio;
    }

    public void setCategoriaPredio(CatCategoriasPredio categoriaPredio) {
        this.categoriaPredio = categoriaPredio;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<FnSolicitudExoneracion> getFnSolicitudExoneracionCollection() {
        return fnSolicitudExoneracionCollection;
    }

    public void setFnSolicitudExoneracionCollection(Collection<FnSolicitudExoneracion> fnSolicitudExoneracionCollection) {
        this.fnSolicitudExoneracionCollection = fnSolicitudExoneracionCollection;
    }

    public FnExoneracionClase getExoneracionClase() {
        return exoneracionClase;
    }

    public void setExoneracionClase(FnExoneracionClase exoneracionClase) {
        this.exoneracionClase = exoneracionClase;
    }

    public String getAplica() {
        return aplica;
    }

    public void setAplica(String aplica) {
        this.aplica = aplica;
    }

    public Boolean getValidaRemuneracion() {
        return validaRemuneracion;
    }

    public void setValidaRemuneracion(Boolean validaRemuneracion) {
        this.validaRemuneracion = validaRemuneracion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
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
        if (!(object instanceof FnExoneracionTipo)) {
            return false;
        }
        FnExoneracionTipo other = (FnExoneracionTipo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnExoneracionTipo[ id=" + id + " ]";
    }

}
