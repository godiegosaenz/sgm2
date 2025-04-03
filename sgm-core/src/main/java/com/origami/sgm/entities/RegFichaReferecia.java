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
import javax.persistence.FetchType;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_ficha_referecia",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegFichaReferecia.findAll", query = "SELECT r FROM RegFichaReferecia r"),
    @NamedQuery(name = "RegFichaReferecia.findById", query = "SELECT r FROM RegFichaReferecia r WHERE r.id = :id"),
    @NamedQuery(name = "RegFichaReferecia.findByTipoFicha", query = "SELECT r FROM RegFichaReferecia r WHERE r.tipoFicha = :tipoFicha"),
    @NamedQuery(name = "RegFichaReferecia.findByNumFicha", query = "SELECT r FROM RegFichaReferecia r WHERE r.numFicha = :numFicha"),
    @NamedQuery(name = "RegFichaReferecia.findByLibro", query = "SELECT r FROM RegFichaReferecia r WHERE r.libro = :libro"),
    @NamedQuery(name = "RegFichaReferecia.findByNumRepertorio", query = "SELECT r FROM RegFichaReferecia r WHERE r.numRepertorio = :numRepertorio"),
    @NamedQuery(name = "RegFichaReferecia.findByFechaInscripcion", query = "SELECT r FROM RegFichaReferecia r WHERE r.fechaInscripcion = :fechaInscripcion"),
    @NamedQuery(name = "RegFichaReferecia.findByNumInscripcion", query = "SELECT r FROM RegFichaReferecia r WHERE r.numInscripcion = :numInscripcion"),
    @NamedQuery(name = "RegFichaReferecia.findByIndice", query = "SELECT r FROM RegFichaReferecia r WHERE r.indice = :indice"),
    @NamedQuery(name = "RegFichaReferecia.findByNumTit", query = "SELECT r FROM RegFichaReferecia r WHERE r.numTit = :numTit")})
public class RegFichaReferecia implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "tipo_ficha")
    private BigInteger tipoFicha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_ficha", nullable = false)
    private long numFicha;
    @Column(name = "libro")
    private BigInteger libro;
    @Column(name = "num_repertorio")
    private BigInteger numRepertorio;
    @Column(name = "fecha_inscripcion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInscripcion;
    @Column(name = "num_inscripcion")
    private BigInteger numInscripcion;
    @Column(name = "indice")
    private BigInteger indice;
    @Column(name = "num_tit")
    private BigInteger numTit;
    @JoinColumn(name = "ficha", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RegFicha ficha;

    public RegFichaReferecia() {
    }

    public RegFichaReferecia(Long id) {
        this.id = id;
    }

    public RegFichaReferecia(Long id, long numFicha) {
        this.id = id;
        this.numFicha = numFicha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getTipoFicha() {
        return tipoFicha;
    }

    public void setTipoFicha(BigInteger tipoFicha) {
        this.tipoFicha = tipoFicha;
    }

    public long getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(long numFicha) {
        this.numFicha = numFicha;
    }

    public BigInteger getLibro() {
        return libro;
    }

    public void setLibro(BigInteger libro) {
        this.libro = libro;
    }

    public BigInteger getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(BigInteger numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public BigInteger getNumInscripcion() {
        return numInscripcion;
    }

    public void setNumInscripcion(BigInteger numInscripcion) {
        this.numInscripcion = numInscripcion;
    }

    public BigInteger getIndice() {
        return indice;
    }

    public void setIndice(BigInteger indice) {
        this.indice = indice;
    }

    public BigInteger getNumTit() {
        return numTit;
    }

    public void setNumTit(BigInteger numTit) {
        this.numTit = numTit;
    }

    public RegFicha getFicha() {
        return ficha;
    }

    public void setFicha(RegFicha ficha) {
        this.ficha = ficha;
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
        if (!(object instanceof RegFichaReferecia)) {
            return false;
        }
        RegFichaReferecia other = (RegFichaReferecia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegFichaReferecia[ id=" + id + " ]";
    }
    
}
