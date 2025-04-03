/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
@Table(name = "cat_ente_socios", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatEnteSocios.findAll", query = "SELECT c FROM CatEnteSocios c"),
    @NamedQuery(name = "CatEnteSocios.findById", query = "SELECT c FROM CatEnteSocios c WHERE c.id = :id"),
    @NamedQuery(name = "CatEnteSocios.findByEnte", query = "SELECT c FROM CatEnteSocios c WHERE c.ente = :ente"),
    @NamedQuery(name = "CatEnteSocios.findByParticipacionPorc", query = "SELECT c FROM CatEnteSocios c WHERE c.participacionPorc = :participacionPorc")})
public class CatEnteSocios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "ente")
    private BigInteger ente;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "participacion_porc", precision = 8, scale = 2)
    private BigDecimal participacionPorc;
    @JoinColumn(name = "ente_socios", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte enteSocios;

    public CatEnteSocios() {
    }

    public CatEnteSocios(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getEnte() {
        return ente;
    }

    public void setEnte(BigInteger ente) {
        this.ente = ente;
    }

    public BigDecimal getParticipacionPorc() {
        return participacionPorc;
    }

    public void setParticipacionPorc(BigDecimal participacionPorc) {
        this.participacionPorc = participacionPorc;
    }

    public CatEnte getEnteSocios() {
        return enteSocios;
    }

    public void setEnteSocios(CatEnte enteSocios) {
        this.enteSocios = enteSocios;
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
        if (!(object instanceof CatEnteSocios)) {
            return false;
        }
        CatEnteSocios other = (CatEnteSocios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatEnteSocios[ id=" + id + " ]";
    }
    
}
