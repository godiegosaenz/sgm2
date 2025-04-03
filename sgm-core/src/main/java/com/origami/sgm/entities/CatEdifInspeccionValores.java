/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Joao Sanga
 */

@Entity
@Table(name = "cat_edif_inspeccion_valores", schema = SchemasConfig.APP1)
public class CatEdifInspeccionValores implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "desde_num_pisos")
    private Integer desdeNumPisos;
    @Column(name = "hasta_num_pisos")
    private Integer hastaNumPisos;
    @Column(name = "estado")
    private Boolean estado;
    
    public CatEdifInspeccionValores(){
    }
    
    public CatEdifInspeccionValores(BigDecimal valor){
        this.valor = valor;
    }

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

    public Integer getDesdeNumPisos() {
        return desdeNumPisos;
    }

    public void setDesdeNumPisos(Integer desdeNumPisos) {
        this.desdeNumPisos = desdeNumPisos;
    }

    public Integer getHastaNumPisos() {
        return hastaNumPisos;
    }

    public void setHastaNumPisos(Integer hastaNumPisos) {
        this.hastaNumPisos = hastaNumPisos;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final CatEdifInspeccionValores other = (CatEdifInspeccionValores) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CatEdifInspeccionValores{" + "id=" + id + '}';
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
}
