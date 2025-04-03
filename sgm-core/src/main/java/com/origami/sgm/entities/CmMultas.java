/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 * @date 05/09/2016
 */
@Entity
@Table(name = "cm_multas", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CmMultas.findAll", query = "SELECT c FROM CmMultas c"),
    @NamedQuery(name = "CmMultas.findById", query = "SELECT c FROM CmMultas c WHERE c.id = :id"),
    @NamedQuery(name = "CmMultas.findByObservacion", query = "SELECT c FROM CmMultas c WHERE c.observacion = :observacion"),
    @NamedQuery(name = "CmMultas.findByValor", query = "SELECT c FROM CmMultas c WHERE c.valor = :valor"),
    @NamedQuery(name = "CmMultas.findByPredio", query = "SELECT c FROM CmMultas c WHERE c.predio = :predio"),
    @NamedQuery(name = "CmMultas.findByUsuarioIngreso", query = "SELECT c FROM CmMultas c WHERE c.usuarioIngreso = :usuarioIngreso"),
    @NamedQuery(name = "CmMultas.findByFechaIngreso", query = "SELECT c FROM CmMultas c WHERE c.fechaIngreso = :fechaIngreso")})
public class CmMultas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 4000)
    @Column(name = "observacion", length = 4000)
    private String observacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 19, scale = 2)
    private BigDecimal valor;
    @JoinColumn(name = "demandado", referencedColumnName = "id")
    @ManyToOne
    private CatEnte demandado;
    @JoinColumn(name = "demandante", referencedColumnName = "id")
    @ManyToOne
    private CatEnte demandante;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne
    private CatPredio predio;
    @Size(max = 25)
    @Column(name = "usuario_ingreso", length = 25)
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @JoinColumn(name = "local_comercial", referencedColumnName = "id")
    @ManyToOne
    private RenLocalComercial localComercial;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne
    private RenLiquidacion liquidacion;    
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne
    private HistoricoTramites tramite;

    public CmMultas() {
    }

    public CmMultas(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public CatEnte getDemandado() {
        return demandado;
    }

    public void setDemandado(CatEnte demandado) {
        this.demandado = demandado;
    }

    public CatEnte getDemandante() {
        return demandante;
    }

    public void setDemandante(CatEnte demandante) {
        this.demandante = demandante;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
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

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public RenLiquidacion getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RenLiquidacion liquidacion) {
        this.liquidacion = liquidacion;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
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
        if (!(object instanceof CmMultas)) {
            return false;
        }
        CmMultas other = (CmMultas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CmMultas[ id=" + id + " ]";
    }

}
