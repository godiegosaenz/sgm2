/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.CatPredio;
import java.io.Serializable;
import java.math.BigDecimal;
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
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "proceso_fusion_predios",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProcesoFusionPredios.findAll", query = "SELECT p FROM ProcesoFusionPredios p"),
    @NamedQuery(name = "ProcesoFusionPredios.findById", query = "SELECT p FROM ProcesoFusionPredios p WHERE p.id = :id"),
    @NamedQuery(name = "ProcesoFusionPredios.findByAvaluoSolar", query = "SELECT p FROM ProcesoFusionPredios p WHERE p.avaluoSolar = :avaluoSolar"),
    @NamedQuery(name = "ProcesoFusionPredios.findByAvaluoConstruccion", query = "SELECT p FROM ProcesoFusionPredios p WHERE p.avaluoConstruccion = :avaluoConstruccion"),
    @NamedQuery(name = "ProcesoFusionPredios.findByAvaluoEdificacion", query = "SELECT p FROM ProcesoFusionPredios p WHERE p.avaluoEdificacion = :avaluoEdificacion")})
public class ProcesoFusionPredios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "avaluo_solar", precision = 12, scale = 4)
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_construccion", precision = 12, scale = 4)
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_edificacion", precision = 12, scale = 4)
    private BigDecimal avaluoEdificacion;
    @JoinColumn(name = "reporte_proceso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramiteDet htd;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private HistoricoTramites tramite;

    public ProcesoFusionPredios() {
    }

    public ProcesoFusionPredios(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    public BigDecimal getAvaluoEdificacion() {
        return avaluoEdificacion;
    }

    public void setAvaluoEdificacion(BigDecimal avaluoEdificacion) {
        this.avaluoEdificacion = avaluoEdificacion;
    }

    public HistoricoTramiteDet getHtd() {
        return htd;
    }

    public void setHtd(HistoricoTramiteDet htd) {
        this.htd = htd;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
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
        if (!(object instanceof ProcesoFusionPredios)) {
            return false;
        }
        ProcesoFusionPredios other = (ProcesoFusionPredios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.ProcesoFusionPredios[ id=" + id + " ]";
    }
    
}
