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

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "coa_estado_juicio", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "CoaEstadoJuicio.findAll", query = "SELECT c FROM CoaEstadoJuicio c")})
public class CoaEstadoJuicio implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "secuencia")
    private Integer secuencia;
    
    @OneToMany(mappedBy = "estadoJuicio", fetch = FetchType.LAZY)
    private Collection<CoaJuicio> coaJuicioCollection;

    public CoaEstadoJuicio() {
    }

    public CoaEstadoJuicio(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
    }

    public Collection<CoaJuicio> getCoaJuicioCollection() {
        return coaJuicioCollection;
    }

    public void setCoaJuicioCollection(Collection<CoaJuicio> coaJuicioCollection) {
        this.coaJuicioCollection = coaJuicioCollection;
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
        if (!(object instanceof CoaEstadoJuicio)) {
            return false;
        }
        CoaEstadoJuicio other = (CoaEstadoJuicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CoaEstadoJuicio[ id=" + id + " ]";
    }
    
}
