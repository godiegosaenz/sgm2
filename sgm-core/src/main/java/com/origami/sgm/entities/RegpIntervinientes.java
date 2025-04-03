/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
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
 * @author Anyelo
 */
@Entity
@Table(name = "regp_intervinientes",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegpIntervinientes.findAll", query = "SELECT r FROM RegpIntervinientes r"),
    @NamedQuery(name = "RegpIntervinientes.findById", query = "SELECT r FROM RegpIntervinientes r WHERE r.id = :id"),
    @NamedQuery(name = "RegpIntervinientes.findByEnte", query = "SELECT r FROM RegpIntervinientes r WHERE r.ente = :ente"),
    @NamedQuery(name = "RegpIntervinientes.findByPapel", query = "SELECT r FROM RegpIntervinientes r WHERE r.papel = :papel"),
    @NamedQuery(name = "RegpIntervinientes.findByEsBeneficiario", query = "SELECT r FROM RegpIntervinientes r WHERE r.esBeneficiario = :esBeneficiario")})
public class RegpIntervinientes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpLiquidacionDerechosAranceles liquidacion;
    @JoinColumn(name = "papel", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegCatPapel papel;
    @JoinColumn(name = "ente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte ente;
    @Column(name = "es_beneficiario")
    private Boolean esBeneficiario = false;
    @Column(name = "nombres")
    private String nombres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEsBeneficiario() {
        return esBeneficiario;
    }

    public void setEsBeneficiario(Boolean esBeneficiario) {
        this.esBeneficiario = esBeneficiario;
    }

    public RegpLiquidacionDerechosAranceles getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(RegpLiquidacionDerechosAranceles liquidacion) {
        this.liquidacion = liquidacion;
    }

    public RegCatPapel getPapel() {
        return papel;
    }

    public void setPapel(RegCatPapel papel) {
        this.papel = papel;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
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
        if (!(object instanceof RegpIntervinientes)) {
            return false;
        }
        RegpIntervinientes other = (RegpIntervinientes) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpIntervinientes[ id=" + id + " ]";
    }
    
}
