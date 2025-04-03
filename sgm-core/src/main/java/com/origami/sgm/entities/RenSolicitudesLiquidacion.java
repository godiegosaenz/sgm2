/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.FnSolicitudCondonacion;
import com.origami.sgm.entities.FnSolicitudExoneracion;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_solicitudes_liquidacion", schema = SchemasConfig.FINANCIERO)
public class RenSolicitudesLiquidacion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "sol_exoneracion", referencedColumnName = "id")
    @ManyToOne
    private FnSolicitudExoneracion solExoneracion;
    @JoinColumn(name = "sol_condonacion", referencedColumnName = "id")
    @ManyToOne
    private FnSolicitudCondonacion solCondonacion;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne
    private RenLiquidacion liquidacion;
    @Column(name = "estado")
    private Boolean estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FnSolicitudExoneracion getSolExoneracion() {
        return solExoneracion;
    }

    public void setSolExoneracion(FnSolicitudExoneracion solExoneracion) {
        this.solExoneracion = solExoneracion;
    }

    public FnSolicitudCondonacion getSolCondonacion() {
        return solCondonacion;
    }

    public void setSolCondonacion(FnSolicitudCondonacion solCondonacion) {
        this.solCondonacion = solCondonacion;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final RenSolicitudesLiquidacion other = (RenSolicitudesLiquidacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RenSolicitudesLiquidacion{" + "id=" + id + '}';
    }
    
    
}
