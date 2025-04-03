/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.validation.constraints.Size;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "fn_exoneracion_liquidacion", schema = SchemasConfig.FINANCIERO)
public class FnExoneracionLiquidacion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne
    private RenLiquidacion liquidacionOriginal;    
    @JoinColumn(name = "liquidacion_posterior", referencedColumnName = "id")
    @ManyToOne
    private RenLiquidacion liquidacionPosterior;
    @JoinColumn(name = "exoneracion", referencedColumnName = "id")
    @ManyToOne
    private FnSolicitudExoneracion exoneracion;
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;
    @Size(max = 250)
    @Column(name = "usuario_ingreso", length = 250)    
    private String usuarioIngreso;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "es_urbano")
    private Boolean esUrbano;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RenLiquidacion getLiquidacionOriginal() {
        return liquidacionOriginal;
    }

    public void setLiquidacionOriginal(RenLiquidacion liquidacionOriginal) {
        this.liquidacionOriginal = liquidacionOriginal;
    }

    public RenLiquidacion getLiquidacionPosterior() {
        return liquidacionPosterior;
    }

    public void setLiquidacionPosterior(RenLiquidacion liquidacionPosterior) {
        this.liquidacionPosterior = liquidacionPosterior;
    }

    public FnSolicitudExoneracion getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(FnSolicitudExoneracion exoneracion) {
        this.exoneracion = exoneracion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final FnExoneracionLiquidacion other = (FnExoneracionLiquidacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FnExoneracionLiquidacion{" + "id=" + id + '}';
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getEsUrbano() {
        return esUrbano;
    }

    public void setEsUrbano(Boolean esUrbano) {
        this.esUrbano = esUrbano;
    }
}
