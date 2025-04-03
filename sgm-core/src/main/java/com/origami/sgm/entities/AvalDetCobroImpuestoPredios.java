/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author andysanchez
 */
@Entity
@Table(name = "aval_det_cobro_impuesto_predios", schema = SchemasConfig.APP1)
@NamedQueries({
    @NamedQuery(name = "AvalDetCobroImpuestoPredios.findAll", query = "SELECT a FROM AvalDetCobroImpuestoPredios a")})
public class AvalDetCobroImpuestoPredios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "id_rubro_cobrar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenRubrosLiquidacion idRubroCobrar;
    @JoinColumn(name = "id_aval_impuesto_predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AvalImpuestoPredios idAvalImpuestoPredio;

    public AvalDetCobroImpuestoPredios() {
    }

    public AvalDetCobroImpuestoPredios(Long id) {
        this.id = id;
    }

    public AvalDetCobroImpuestoPredios(Long id, RenRubrosLiquidacion idRubroCobrar, AvalImpuestoPredios idAvalImpuestoPredio) {
        this.id = id;
        this.idRubroCobrar = idRubroCobrar;
        this.idAvalImpuestoPredio = idAvalImpuestoPredio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RenRubrosLiquidacion getIdRubroCobrar() {
        return idRubroCobrar;
    }

    public void setIdRubroCobrar(RenRubrosLiquidacion idRubroCobrar) {
        this.idRubroCobrar = idRubroCobrar;
    }

    public AvalImpuestoPredios getIdAvalImpuestoPredio() {
        return idAvalImpuestoPredio;
    }

    public void setIdAvalImpuestoPredio(AvalImpuestoPredios idAvalImpuestoPredio) {
        this.idAvalImpuestoPredio = idAvalImpuestoPredio;
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
        if (!(object instanceof AvalDetCobroImpuestoPredios)) {
            return false;
        }
        AvalDetCobroImpuestoPredios other = (AvalDetCobroImpuestoPredios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.AvalDetCobroImpuestoPredios[ id=" + id + " ]";
    }
    
}
