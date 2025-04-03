/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 * @date 26/07/2016
 */
@Entity
@Table(name = "parte_recaudaciones", schema = SchemasConfig.TEMPORAL)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ParteRecaudaciones.findAll", query = "SELECT p FROM ParteRecaudaciones p"),
    @NamedQuery(name = "ParteRecaudaciones.findByCodigo", query = "SELECT p FROM ParteRecaudaciones p WHERE p.codigo = :codigo"),
    @NamedQuery(name = "ParteRecaudaciones.findByDescripcion", query = "SELECT p FROM ParteRecaudaciones p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "ParteRecaudaciones.findByCtaTransaccion", query = "SELECT p FROM ParteRecaudaciones p WHERE p.ctaTransaccion = :ctaTransaccion"),
    @NamedQuery(name = "ParteRecaudaciones.findById", query = "SELECT p FROM ParteRecaudaciones p WHERE p.id = :id"),
    @NamedQuery(name = "ParteRecaudaciones.findByOrden", query = "SELECT p FROM ParteRecaudaciones p WHERE p.orden = :orden"),
    @NamedQuery(name = "ParteRecaudaciones.findByPadre", query = "SELECT p FROM ParteRecaudaciones p WHERE p.padre = :padre")})
public class ParteRecaudaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 50)
    @Column(name = "codigo", length = 50)
    private String codigo;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    @Size(max = 4000)
    @Column(name = "cta_transaccion", length = 4000)
    private String ctaTransaccion;
    @Basic(optional = false)
    @Column(name = "orden", nullable = false)
    private int orden;
    @Column(name = "padre")
    private Integer padre;
    @Column(name = "tipo")
    private Integer tipo;
    @Column(name = "anio_anterior")
    private Boolean anioAnterior;
    
    @Transient
    private BigDecimal valor = BigDecimal.ZERO;

    public ParteRecaudaciones() {
    }

    public ParteRecaudaciones(Long id) {
        this.id = id;
    }

    public ParteRecaudaciones(Long id, int orden) {
        this.id = id;
        this.orden = orden;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCtaTransaccion() {
        return ctaTransaccion;
    }

    public void setCtaTransaccion(String ctaTransaccion) {
        this.ctaTransaccion = ctaTransaccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public Integer getPadre() {
        return padre;
    }

    public void setPadre(Integer padre) {
        this.padre = padre;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Boolean getAnioAnterior() {
        return anioAnterior;
    }

    public void setAnioAnterior(Boolean anioAnterior) {
        this.anioAnterior = anioAnterior;
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
//        if (!(object instanceof ParteRecaudaciones)) {
//            return false;
//        }
//        ParteRecaudaciones other = (ParteRecaudaciones) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
        if ((object instanceof ParteRecaudaciones)) {
            ParteRecaudaciones other = (ParteRecaudaciones) object;
            if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
                return false;
            }
        } else if ((object instanceof com.origami.sgm.entities.models.ParteRecaudaciones)) {
            com.origami.sgm.entities.models.ParteRecaudaciones other = (com.origami.sgm.entities.models.ParteRecaudaciones) object;
            if ((this.id == null && other.getId() != null) || (this.id != null && !(this.id == other.getId().longValue()))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.ParteRecaudaciones[ id=" + id + " ]";
    }

}
