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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "rec_actas_especies_det", schema = SchemasConfig.FINANCIERO)
public class RecActasEspeciesDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "cantidad")
    private Integer cantidad;
    @Column(name = "valor_uni", precision = 12, scale = 2)
    private BigDecimal valorUni;
    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal;
    @Column(name = "desde")
    private Long desde;
    @Column(name = "hasta")
    private Long hasta;
    @Column(name = "disponibles")
    private Integer disponibles;
    @Column(name = "ultimo_vendido")
    private Long ultimoVendido = 0L;
    @Column(name = "estado")
    private String estado = "A";
    
    @JoinColumn(name = "especie", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RecEspecies especie;
    
    @JoinColumn(name = "acta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RecActasEspecies acta;
    
    @Transient
    private Long desdeTemp;
    @Transient
    private Long hastaTemp;
    @Transient
    private Integer disponiblesTemp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorUni() {
        return valorUni;
    }

    public void setValorUni(BigDecimal valorUni) {
        this.valorUni = valorUni;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getDesde() {
        return desde;
    }

    public void setDesde(Long desde) {
        this.desde = desde;
    }

    public Long getHasta() {
        return hasta;
    }

    public void setHasta(Long hasta) {
        this.hasta = hasta;
    }

    public Integer getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(Integer disponibles) {
        this.disponibles = disponibles;
    }

    public Long getUltimoVendido() {
        return ultimoVendido;
    }

    public void setUltimoVendido(Long ultimoVendido) {
        this.ultimoVendido = ultimoVendido;
    }

    public RecEspecies getEspecie() {
        return especie;
    }

    public void setEspecie(RecEspecies especie) {
        this.especie = especie;
    }

    public RecActasEspecies getActa() {
        return acta;
    }

    public void setActa(RecActasEspecies acta) {
        this.acta = acta;
    }

    public Long getDesdeTemp() {
        return desdeTemp;
    }

    public void setDesdeTemp(Long desdeTemp) {
        this.desdeTemp = desdeTemp;
    }

    public Long getHastaTemp() {
        return hastaTemp;
    }

    public void setHastaTemp(Long hastaTemp) {
        this.hastaTemp = hastaTemp;
    }

    public Integer getDisponiblesTemp() {
        return disponiblesTemp;
    }

    public void setDisponiblesTemp(Integer disponiblesTemp) {
        this.disponiblesTemp = disponiblesTemp;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
        if (!(object instanceof RecActasEspeciesDet)) {
            return false;
        }
        RecActasEspeciesDet other = (RecActasEspeciesDet) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RecActasEspeciesDet[ id=" + id + " ]";
    }
}
