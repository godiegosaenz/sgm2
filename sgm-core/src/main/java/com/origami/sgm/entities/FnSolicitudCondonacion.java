/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import org.hibernate.annotations.Where;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "fn_solicitud_de_condonacion", schema = SchemasConfig.FINANCIERO)
public class FnSolicitudCondonacion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne
    private CatPredio predio;
    
    @JoinColumn(name = "predio_rustico", referencedColumnName = "id")
    @ManyToOne
    private CatPredioRustico predioRustico;
    
    @JoinColumn(name = "tipo_liquidacion", referencedColumnName = "id")
    @ManyToOne
    private RenTipoLiquidacion tipoLiquidacion;
    
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne
    private CatEnte solicitante;
    
    @OneToMany(mappedBy = "solCondonacion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<RenSolicitudesLiquidacion> renSolicitudesLiquidacionCollection;
    
    @Column(name = "dias_plazo")
    private Integer diasPlazo;
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne
    private FnEstadoExoneracion estado;
    @Column(name = "numero")
    private Long numero;
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaIngreso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public Integer getDiasPlazo() {
        return diasPlazo;
    }

    public void setDiasPlazo(Integer diasPlazo) {
        this.diasPlazo = diasPlazo;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Collection<RenSolicitudesLiquidacion> getRenSolicitudesLiquidacionCollection() {
        return renSolicitudesLiquidacionCollection;
    }

    public void setRenSolicitudesLiquidacionCollection(Collection<RenSolicitudesLiquidacion> renSolicitudesLiquidacionCollection) {
        this.renSolicitudesLiquidacionCollection = renSolicitudesLiquidacionCollection;
    }

    public CatPredioRustico getPredioRustico() {
        return predioRustico;
    }

    public void setPredioRustico(CatPredioRustico predioRustico) {
        this.predioRustico = predioRustico;
    }

    public FnEstadoExoneracion getEstado() {
        return estado;
    }

    public void setEstado(FnEstadoExoneracion estado) {
        this.estado = estado;
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
        final FnSolicitudCondonacion other = (FnSolicitudCondonacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FnSolicitudCondonacion{" + "id=" + id + '}';
    }
    
}
