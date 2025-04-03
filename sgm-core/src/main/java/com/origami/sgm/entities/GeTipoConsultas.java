/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_tipo_consultas", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeTipoConsultas.findAll", query = "SELECT g FROM GeTipoConsultas g"),
    @NamedQuery(name = "GeTipoConsultas.findById", query = "SELECT g FROM GeTipoConsultas g WHERE g.id = :id"),
    @NamedQuery(name = "GeTipoConsultas.findByConsultaPor", query = "SELECT g FROM GeTipoConsultas g WHERE g.consultaPor = :consultaPor")})
public class GeTipoConsultas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 50)
    @Column(name = "consulta_por", length = 50)
    private String consultaPor;
    @OneToMany(mappedBy = "tipoConsultaId", fetch = FetchType.LAZY)
    private Collection<GeRequisitosTramite> geRequisitosTramiteCollection;

    public GeTipoConsultas() {
    }

    public GeTipoConsultas(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConsultaPor() {
        return consultaPor;
    }

    public void setConsultaPor(String consultaPor) {
        this.consultaPor = consultaPor;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeRequisitosTramite> getGeRequisitosTramiteCollection() {
        return geRequisitosTramiteCollection;
    }

    public void setGeRequisitosTramiteCollection(Collection<GeRequisitosTramite> geRequisitosTramiteCollection) {
        this.geRequisitosTramiteCollection = geRequisitosTramiteCollection;
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
        if (!(object instanceof GeTipoConsultas)) {
            return false;
        }
        GeTipoConsultas other = (GeTipoConsultas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeTipoConsultas[ id=" + id + " ]";
    }
    
}
