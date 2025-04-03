/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.historic;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "novedad_valoracion_predial", schema = SchemasConfig.HISTORICO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "NovedadValoracionPredial.findAll", query = "SELECT n FROM NovedadValoracionPredial n")
    , @NamedQuery(name = "NovedadValoracionPredial.findById", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.id = :id")
    , @NamedQuery(name = "NovedadValoracionPredial.findByNumVersion", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.numVersion = :numVersion")
    , @NamedQuery(name = "NovedadValoracionPredial.findByPeriodo", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.periodo = :periodo")
    , @NamedQuery(name = "NovedadValoracionPredial.findByNumPredio", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.numPredio = :numPredio")
    , @NamedQuery(name = "NovedadValoracionPredial.findByNovedad", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.novedad = :novedad")
    , @NamedQuery(name = "NovedadValoracionPredial.findByFecCre", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.fecCre = :fecCre")
    , @NamedQuery(name = "NovedadValoracionPredial.findByEstado", query = "SELECT n FROM NovedadValoracionPredial n WHERE n.estado = :estado")})
public class NovedadValoracionPredial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_version")
    private Integer numVersion;
    @Column(name = "periodo")
    private Integer periodo;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    @Size(max = 4000)
    @Column(name = "novedad", length = 4000)
    private String novedad;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "estado")
    private Boolean estado;

    public NovedadValoracionPredial() {
    }

    public NovedadValoracionPredial(Long id) {
        this.id = id;
    }

    public NovedadValoracionPredial(Integer numVersion, Integer periodo, BigInteger numPredio, String novedad) {
        this.numVersion = numVersion;
        this.periodo = periodo;
        this.numPredio = numPredio;
        this.novedad = novedad;
        this.fecCre = new Date();
        this.estado = Boolean.TRUE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(Integer numVersion) {
        this.numVersion = numVersion;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public String getNovedad() {
        return novedad;
    }

    public void setNovedad(String novedad) {
        this.novedad = novedad;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
        if (!(object instanceof NovedadValoracionPredial)) {
            return false;
        }
        NovedadValoracionPredial other = (NovedadValoracionPredial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + id + "";
    }

}
