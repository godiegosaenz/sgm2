/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "otros_tramites_has_permiso", schema = SchemasConfig.FLOW)
public class OtrosTramitesHasPermiso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "factor2")
    private BigDecimal factor2;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha_caducidad")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCaducidad;    
    @Column(name = "responsable")
    private BigInteger responsableTec;
    @JoinColumn(name = "otros_tramites", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OtrosTramites otrosTramites;    
    @JoinColumn(name = "permiso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PePermiso pePermiso;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private HistoricoTramites tramite;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public OtrosTramites getOtrosTramites() {
        return otrosTramites;
    }

    public void setOtrosTramites(OtrosTramites otrosTramites) {
        this.otrosTramites = otrosTramites;
    }

    public PePermiso getPePermiso() {
        return pePermiso;
    }

    public void setPePermiso(PePermiso pePermiso) {
        this.pePermiso = pePermiso;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public BigInteger getResponsableTec() {
        return responsableTec;
    }

    public void setResponsableTec(BigInteger responsableTec) {
        this.responsableTec = responsableTec;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final OtrosTramitesHasPermiso other = (OtrosTramitesHasPermiso) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "OtrosTramitesHasPermiso{" + "id=" + id + '}';
    }

    public BigDecimal getFactor2() {
        return factor2;
    }

    public void setFactor2(BigDecimal factor2) {
        this.factor2 = factor2;
    }
    
}
