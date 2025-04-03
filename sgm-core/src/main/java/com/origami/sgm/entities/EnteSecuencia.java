/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 */
@Entity
@Table(name = "ente_secuencia", schema = SchemasConfig.SECUENCIAS)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EnteSecuencia.findAll", query = "SELECT e FROM EnteSecuencia e"),
    @NamedQuery(name = "EnteSecuencia.findById", query = "SELECT e FROM EnteSecuencia e WHERE e.id = :id"),
    @NamedQuery(name = "EnteSecuencia.findByAnio", query = "SELECT e FROM EnteSecuencia e WHERE e.anio = :anio"),
    @NamedQuery(name = "EnteSecuencia.findBySecuencia", query = "SELECT e FROM EnteSecuencia e WHERE e.secuencia = :secuencia")})
public class EnteSecuencia implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "anio")
    private BigInteger anio;
    @Column(name = "secuencia")
    private BigInteger secuencia;

    public EnteSecuencia() {
    }

    public EnteSecuencia(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getAnio() {
        return anio;
    }

    public void setAnio(BigInteger anio) {
        this.anio = anio;
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
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
        if (!(object instanceof EnteSecuencia)) {
            return false;
        }
        EnteSecuencia other = (EnteSecuencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.EnteSecuencia[ id=" + id + " ]";
    }
    
}
