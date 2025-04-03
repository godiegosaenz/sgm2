/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.MejPagoRubroMejora;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_pago_rubro", schema = SchemasConfig.FINANCIERO)
public class RenPagoRubro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "valor")
    private BigDecimal valor;  
    @JoinColumn(name = "pago", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenPago pago;
    @JoinColumn(name = "rubro", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenRubrosLiquidacion rubro;
    @OneToMany(mappedBy = "rubroMejoraPago", fetch = FetchType.LAZY)
    @OrderBy("valor ASC")
    private Collection<MejPagoRubroMejora> mejPagoRubroMejoras;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public RenPago getPago() {
        return pago;
    }

    public void setPago(RenPago pago) {
        this.pago = pago;
    }

    public RenRubrosLiquidacion getRubro() {
        return rubro;
    }

    public void setRubro(RenRubrosLiquidacion rubro) {
        this.rubro = rubro;
    }

    public Collection<MejPagoRubroMejora> getMejPagoRubroMejoras() {
        return mejPagoRubroMejoras;
    }

    public void setMejPagoRubroMejoras(Collection<MejPagoRubroMejora> mejPagoRubroMejoras) {
        this.mejPagoRubroMejoras = mejPagoRubroMejoras;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final RenPagoRubro other = (RenPagoRubro) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
}
