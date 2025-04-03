/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatCertificadoAvaluo;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "formato_reporte", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FormatoReporte.findAll", query = "SELECT f FROM FormatoReporte f")
    ,
    @NamedQuery(name = "FormatoReporte.findById", query = "SELECT f FROM FormatoReporte f WHERE f.id = :id")
    ,
    @NamedQuery(name = "FormatoReporte.findByCodigo", query = "SELECT f FROM FormatoReporte f WHERE f.codigo = :codigo")
    ,
    @NamedQuery(name = "FormatoReporte.findByFormato", query = "SELECT f FROM FormatoReporte f WHERE f.formato = :formato")
    ,
    @NamedQuery(name = "FormatoReporte.findByFecCre", query = "SELECT f FROM FormatoReporte f WHERE f.fecCre = :fecCre")
    ,
    @NamedQuery(name = "FormatoReporte.findByFecAct", query = "SELECT f FROM FormatoReporte f WHERE f.fecAct = :fecAct")
    ,
    @NamedQuery(name = "FormatoReporte.findByEstado", query = "SELECT f FROM FormatoReporte f WHERE f.estado = :estado")})
public class FormatoReporte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 4000)
    @Column(name = "formato", length = 4000)
    private String formato;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "estado")
    private Boolean estado;
    @OneToMany(mappedBy = "formato", fetch = FetchType.LAZY)
    private List<CatCertificadoAvaluo> catCertificadoAvaluoList;
    @Column(name = "reporte")
    private String reporte;

    public FormatoReporte() {
    }

    public FormatoReporte(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    @XmlTransient
    @JsonIgnore
    public List<CatCertificadoAvaluo> getCatCertificadoAvaluoList() {
        return catCertificadoAvaluoList;
    }

    public void setCatCertificadoAvaluoList(List<CatCertificadoAvaluo> catCertificadoAvaluoList) {
        this.catCertificadoAvaluoList = catCertificadoAvaluoList;
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
        if (!(object instanceof FormatoReporte)) {
            return false;
        }
        FormatoReporte other = (FormatoReporte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FormatoReporte[ id=" + id + " ]";
    }

}
