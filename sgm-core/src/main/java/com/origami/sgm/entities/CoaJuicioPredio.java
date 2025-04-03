/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "coa_juicio_predio", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "CoaJuicioPredio.findAll", query = "SELECT c FROM CoaJuicioPredio c")})
public class CoaJuicioPredio implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @JoinColumn(name = "juicio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CoaJuicio juicio;
    @JoinColumn(name = "abogado_juicio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CoaAbogado abogadoJuicio;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    
    @Column(name = "anio_deuda")
    private Integer anioDeuda;
    @Column(name = "valor_deuda")
    private BigDecimal valorDeuda;
    
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private RenLiquidacion liquidacion;
    
    @Column(name = "estado")
    private Boolean estado=Boolean.TRUE;
    @Column(name = "observacion")
    private String observacion;
    
    @Transient
    private Integer anioDesde;
    @Transient
    private Integer anioHasta;

    public CoaJuicioPredio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CoaJuicio getJuicio() {
        return juicio;
    }

    public void setJuicio(CoaJuicio juicio) {
        this.juicio = juicio;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Integer getAnioDeuda() {
        return anioDeuda;
    }

    public void setAnioDeuda(Integer anioDeuda) {
        this.anioDeuda = anioDeuda;
    }

    public BigDecimal getValorDeuda() {
        return valorDeuda;
    }

    public void setValorDeuda(BigDecimal valorDeuda) {
        this.valorDeuda = valorDeuda;
    }

    public Integer getAnioDesde() {
        return anioDesde;
    }

    public void setAnioDesde(Integer anioDesde) {
        this.anioDesde = anioDesde;
    }

    public Integer getAnioHasta() {
        return anioHasta;
    }

    public void setAnioHasta(Integer anioHasta) {
        this.anioHasta = anioHasta;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public CoaAbogado getAbogadoJuicio() {
        return abogadoJuicio;
    }

    public void setAbogadoJuicio(CoaAbogado abogadoJuicio) {
        this.abogadoJuicio = abogadoJuicio;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
        if (!(object instanceof CoaJuicioPredio)) {
            return false;
        }
        CoaJuicioPredio other = (CoaJuicioPredio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CoaJuicioPredio[ id=" + id + " ]";
    }
    
}
