/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author OrigamiSolutions
 */
@Entity
@Table(name = "cat_predio_obra_interna", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioObraInterna.findAll", query = "SELECT c FROM CatPredioObraInterna c")
    ,
    @NamedQuery(name = "CatPredioObraInterna.findById", query = "SELECT c FROM CatPredioObraInterna c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPredioObraInterna.findByModificado", query = "SELECT c FROM CatPredioObraInterna c WHERE c.modificado = :modificado")
    ,
    @NamedQuery(name = "CatPredioObraInterna.findByEstado", query = "SELECT c FROM CatPredioObraInterna c WHERE c.estado = :estado")})
public class CatPredioObraInterna implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;

    @Size(max = 20)
    @Column(name = "modificado", length = 20)
    @Expose
    private String modificado;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    @Expose
    private String estado = "A";
    @Column(name = "usuario", length = 100)
    private String usuario;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;
    @Column(name = "area", precision = 12, scale = 2)
    @Expose
    private BigDecimal area;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem tipo;
    @JoinColumn(name = "material", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem material;
    @Column(name = "cantidad")
    @Expose
    private BigDecimal cantidad;
    @JoinColumn(name = "conservacion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @Expose
    private CtlgItem conservacion;
    @JoinColumn(name = "edad", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem edad;
    @Column(name = "observaciones", length = 5000)
    @Expose
    private String observaciones;
    @Column(name = "altura")
    @Expose
    private BigDecimal altura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CtlgItem getTipo() {
        return tipo;
    }

    public void setTipo(CtlgItem tipo) {
        this.tipo = tipo;
    }

    public CtlgItem getMaterial() {
        return material;
    }

    public void setMaterial(CtlgItem material) {
        this.material = material;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public CtlgItem getConservacion() {
        return conservacion;
    }

    public void setConservacion(CtlgItem conservacion) {
        this.conservacion = conservacion;
    }

    public CtlgItem getEdad() {
        return edad;
    }

    public void setEdad(CtlgItem edad) {
        this.edad = edad;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
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
        if (!(object instanceof CatPredioObraInterna)) {
            return false;
        }
        CatPredioObraInterna other = (CatPredioObraInterna) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatPredioObraInterna[ id=" + id + " ]";
    }

}
