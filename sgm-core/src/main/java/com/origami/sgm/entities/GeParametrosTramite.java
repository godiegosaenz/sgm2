/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_parametros_tramite", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeParametrosTramite.findAll", query = "SELECT g FROM GeParametrosTramite g"),
    @NamedQuery(name = "GeParametrosTramite.findById", query = "SELECT g FROM GeParametrosTramite g WHERE g.id = :id"),
    @NamedQuery(name = "GeParametrosTramite.findByParametro", query = "SELECT g FROM GeParametrosTramite g WHERE g.parametro = :parametro"),
    @NamedQuery(name = "GeParametrosTramite.findByValor", query = "SELECT g FROM GeParametrosTramite g WHERE g.valor = :valor"),
    @NamedQuery(name = "GeParametrosTramite.findByDescripcion", query = "SELECT g FROM GeParametrosTramite g WHERE g.descripcion = :descripcion")})
public class GeParametrosTramite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 255)
    @Column(name = "parametro", length = 255)
    private String parametro;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 12, scale = 2)
    private BigDecimal valor;
    @Size(max = 255)
    @Column(name = "descripcion", length = 255)
    private String descripcion;
    @JoinColumn(name = "tramite_tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite tramiteTipo;

    public GeParametrosTramite() {
    }

    public GeParametrosTramite(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
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

    public GeTipoTramite getTramiteTipo() {
        return tramiteTipo;
    }

    public void setTramiteTipo(GeTipoTramite tramiteTipo) {
        this.tramiteTipo = tramiteTipo;
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
        if (!(object instanceof GeParametrosTramite)) {
            return false;
        }
        GeParametrosTramite other = (GeParametrosTramite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.GeParametrosTramite[ id=" + id + " ]";
    }
    
}
