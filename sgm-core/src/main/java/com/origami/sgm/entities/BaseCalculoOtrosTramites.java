/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "base_calculo_otros_tramites", schema = SchemasConfig.FLOW)
public class BaseCalculoOtrosTramites implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;    
    @Column(name = "valor_base")
    private Double valorBase;
    @OneToMany(mappedBy = "baseCalculo", fetch = FetchType.LAZY)
    private Collection<OtrosTramites> otrosTramitesCollection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValorBase() {
        return valorBase;
    }

    public void setValorBase(Double valorBase) {
        this.valorBase = valorBase;
    }

    public Collection<OtrosTramites> getOtrosTramitesCollection() {
        return otrosTramitesCollection;
    }

    public void setOtrosTramitesCollection(Collection<OtrosTramites> otrosTramitesCollection) {
        this.otrosTramitesCollection = otrosTramitesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.id);
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
        final BaseCalculoOtrosTramites other = (BaseCalculoOtrosTramites) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BaseCalculoOtrosTramites{" + "id=" + id + '}';
    }
    
}
