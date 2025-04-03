/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author origami
 */
@Entity
@Table(name = "mej_pago_rubro_mejora", schema = SchemasConfig.MEJORAS)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MejPagoRubroMejora.findAll", query = "SELECT m FROM MejPagoRubroMejora m")})
public class MejPagoRubroMejora implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "rubro_mejora_pago", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenPagoRubro rubroMejoraPago;
    @JoinColumn(name = "ubicacion_obra", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MejValoresObraUbicacion ubicacionObra;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;

    public MejPagoRubroMejora() {
    }

    public MejPagoRubroMejora(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RenPagoRubro getRubroMejoraPago() {
        return rubroMejoraPago;
    }

    public void setRubroMejoraPago(RenPagoRubro rubroMejoraPago) {
        this.rubroMejoraPago = rubroMejoraPago;
    }

    public MejValoresObraUbicacion getUbicacionObra() {
        return ubicacionObra;
    }

    public void setUbicacionObra(MejValoresObraUbicacion ubicacionObra) {
        this.ubicacionObra = ubicacionObra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        if (!(object instanceof MejPagoRubroMejora)) {
            return false;
        }
        MejPagoRubroMejora other = (MejPagoRubroMejora) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.MejPagoRubroMejora[ id=" + id + " ]";
    }
    
}
