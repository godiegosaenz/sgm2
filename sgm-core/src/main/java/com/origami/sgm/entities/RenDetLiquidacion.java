/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.MejDetRubroMejoras;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Objects;
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
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_det_liquidacion", schema = SchemasConfig.FINANCIERO)
@SequenceGenerator(name = "ren_det_liquidacion_id_seq", sequenceName = SchemasConfig.FINANCIERO + ".ren_det_liquidacion_id_seq", allocationSize = 1)
public class RenDetLiquidacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ren_det_liquidacion_id_seq")  
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "estado")
    private boolean estado = true;

    @Column(name = "cantidad")
    private Integer cantidad = 1;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "desde")
    private BigInteger desde = BigInteger.ZERO;

    @Column(name = "hasta")
    private BigInteger hasta = BigInteger.ZERO;

    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLiquidacion liquidacion;

    @Column(name = "rubro")
    private Long rubro;

    @Column(name = "valor_recaudado")
    private BigDecimal valorRecaudado = new BigDecimal("0.00");

    @Column(name = "rec_actas_especies_det")
    private Long recActasEspeciesDet;

    @OneToMany(mappedBy = "rubroMejora", fetch = FetchType.LAZY)
    @OrderBy("valor ASC")
    private Collection<MejDetRubroMejoras> mejDetRubroMejorasCollection;

    @Transient
    private String descripcion;

    @Transient
    private Boolean cobrar;

    @Transient
    private Long codigoRubro;

    public RenDetLiquidacion() {

    }

    public RenDetLiquidacion(BigDecimal valor, Long idRubro, String desc) {
        estado = true;
        this.valor = valor;
        rubro = idRubro;
        this.descripcion = desc;
    }

    public RenDetLiquidacion(BigDecimal valor, Long idRubro, String desc, Long codigoRubro) {
        estado = true;
        this.valor = valor;
        rubro = idRubro;
        this.descripcion = desc;
        this.codigoRubro = codigoRubro;
    }

    public RenDetLiquidacion(BigDecimal valor, RenLiquidacion liquidacion, Long rubro) {
        this.valor = valor;
        this.liquidacion = liquidacion;
        this.rubro = rubro;
        this.estado = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Long getRubro() {
        return rubro;
    }

    public void setRubro(Long rubro) {
        this.rubro = rubro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getCobrar() {
        return cobrar;
    }

    public void setCobrar(Boolean cobrar) {
        this.cobrar = cobrar;
    }

    public BigInteger getDesde() {
        return desde;
    }

    public void setDesde(BigInteger desde) {
        this.desde = desde;
    }

    public BigInteger getHasta() {
        return hasta;
    }

    public void setHasta(BigInteger hasta) {
        this.hasta = hasta;
    }

    public BigDecimal getValorRecaudado() {
        return valorRecaudado;
    }

    public void setValorRecaudado(BigDecimal valorRecaudado) {
        this.valorRecaudado = valorRecaudado;
    }

    public Long getRecActasEspeciesDet() {
        return recActasEspeciesDet;
    }

    public void setRecActasEspeciesDet(Long recActasEspeciesDet) {
        this.recActasEspeciesDet = recActasEspeciesDet;
    }

    public Long getCodigoRubro() {
        return codigoRubro;
    }

    public void setCodigoRubro(Long codigoRubro) {
        this.codigoRubro = codigoRubro;
    }

    public Collection<MejDetRubroMejoras> getMejDetRubroMejorasCollection() {
        return mejDetRubroMejorasCollection;
    }

    public void setMejDetRubroMejorasCollection(Collection<MejDetRubroMejoras> mejDetRubroMejorasCollection) {
        this.mejDetRubroMejorasCollection = mejDetRubroMejorasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RenDetLiquidacion other = (RenDetLiquidacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RenDetLiquidacion{" + "id=" + id + '}';
    }

}
