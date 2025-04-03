/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_resolucion_consejo", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatResolucionConsejo.findAll", query = "SELECT c FROM CatResolucionConsejo c"),
    @NamedQuery(name = "CatResolucionConsejo.findById", query = "SELECT c FROM CatResolucionConsejo c WHERE c.id = :id"),
    @NamedQuery(name = "CatResolucionConsejo.findByNumSessionCon", query = "SELECT c FROM CatResolucionConsejo c WHERE c.numSessionCon = :numSessionCon"),
    @NamedQuery(name = "CatResolucionConsejo.findByNumTramite", query = "SELECT c FROM CatResolucionConsejo c WHERE c.numTramite = :numTramite"),
    @NamedQuery(name = "CatResolucionConsejo.findByFecSession", query = "SELECT c FROM CatResolucionConsejo c WHERE c.fecSession = :fecSession"),
    @NamedQuery(name = "CatResolucionConsejo.findByNumResolucion", query = "SELECT c FROM CatResolucionConsejo c WHERE c.numResolucion = :numResolucion"),
    @NamedQuery(name = "CatResolucionConsejo.findByObservacion", query = "SELECT c FROM CatResolucionConsejo c WHERE c.observacion = :observacion")})
public class CatResolucionConsejo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_session_con")
    private BigInteger numSessionCon;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "num_tramite", nullable = false, length = 15)
    private String numTramite;
    @Column(name = "fec_session")
    @Temporal(TemporalType.DATE)
    private Date fecSession;
    @Column(name = "num_resolucion")
    private BigInteger numResolucion;
    @Size(max = 4000)
    @Column(name = "observacion", length = 4000)
    private String observacion;

    public CatResolucionConsejo() {
    }

    public CatResolucionConsejo(Long id) {
        this.id = id;
    }

    public CatResolucionConsejo(Long id, String numTramite) {
        this.id = id;
        this.numTramite = numTramite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getNumSessionCon() {
        return numSessionCon;
    }

    public void setNumSessionCon(BigInteger numSessionCon) {
        this.numSessionCon = numSessionCon;
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public Date getFecSession() {
        return fecSession;
    }

    public void setFecSession(Date fecSession) {
        this.fecSession = fecSession;
    }

    public BigInteger getNumResolucion() {
        return numResolucion;
    }

    public void setNumResolucion(BigInteger numResolucion) {
        this.numResolucion = numResolucion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
        if (!(object instanceof CatResolucionConsejo)) {
            return false;
        }
        CatResolucionConsejo other = (CatResolucionConsejo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatResolucionConsejo[ id=" + id + " ]";
    }
    
}
