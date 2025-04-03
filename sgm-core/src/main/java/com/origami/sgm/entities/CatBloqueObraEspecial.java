/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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

@Entity
@Table(name = "cat_bloque_obra_especial", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({    
    @NamedQuery(name = "CatBloqueObraEspecial.findAll", query = "SELECT c FROM CatBloqueObraEspecial c"),
    @NamedQuery(name = "CatBloqueObraEspecial.findById", query = "SELECT c FROM CatBloqueObraEspecial c WHERE c.id = :id"),    
    @NamedQuery(name = "CatBloqueObraEspecial.findByModificado", query = "SELECT c FROM CatBloqueObraEspecial c WHERE c.modificado = :modificado"),
    @NamedQuery(name = "CatBloqueObraEspecial.findByEstado", query = "SELECT c FROM CatBloqueObraEspecial c WHERE c.estado = :estado")})   
public class CatBloqueObraEspecial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;    
    @Size(max = 20)
    @Column(name = "modificado", length = 20)
    private String modificado;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado = "A";       
    @Column(name = "usuario", length = 100)
    private String usuario;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
      
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem tipo;
    @JoinColumn(name = "orden", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem orden;
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    @Column(name = "valor_unitario")
    private BigDecimal valorUnitario;
    @JoinColumn(name = "conservacion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CtlgItem conservacion;
    @JoinColumn(name = "edad", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem edad;

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

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CtlgItem getTipo() {
        return tipo;
    }

    public void setTipo(CtlgItem tipo) {
        this.tipo = tipo;
    }

    public CtlgItem getOrden() {
        return orden;
    }

    public void setOrden(CtlgItem orden) {
        this.orden = orden;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
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
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CatBloqueObraEspecial)) {
            return false;
        }
        CatBloqueObraEspecial other = (CatBloqueObraEspecial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatBloqueObraEspecial[ id=" + id + " ]";
    }
    
}
