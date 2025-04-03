/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "tipo_departamento",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoDepartamento.findAll", query = "SELECT t FROM TipoDepartamento t"),
    @NamedQuery(name = "TipoDepartamento.findById", query = "SELECT t FROM TipoDepartamento t WHERE t.id = :id"),
    @NamedQuery(name = "TipoDepartamento.findByNomDepartamento", query = "SELECT t FROM TipoDepartamento t WHERE t.nomDepartamento = :nomDepartamento"),
    @NamedQuery(name = "TipoDepartamento.findByRolId", query = "SELECT t FROM TipoDepartamento t WHERE t.rolId = :rolId"),
    @NamedQuery(name = "TipoDepartamento.findByEstado", query = "SELECT t FROM TipoDepartamento t WHERE t.estado = :estado"),
    @NamedQuery(name = "TipoDepartamento.findByNomDirector", query = "SELECT t FROM TipoDepartamento t WHERE t.nomDirector = :nomDirector")})
public class TipoDepartamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "nom_departamento", length = 100)
    private String nomDepartamento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rol_id", nullable = false)
    private long rolId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "estado", nullable = false, length = 1)
    private String estado;
    @Size(max = 50)
    @Column(name = "nom_director", length = 50)
    private String nomDirector;

    public TipoDepartamento() {
    }

    public TipoDepartamento(Long id) {
        this.id = id;
    }

    public TipoDepartamento(Long id, long rolId, String estado) {
        this.id = id;
        this.rolId = rolId;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomDepartamento() {
        return nomDepartamento;
    }

    public void setNomDepartamento(String nomDepartamento) {
        this.nomDepartamento = nomDepartamento;
    }

    public long getRolId() {
        return rolId;
    }

    public void setRolId(long rolId) {
        this.rolId = rolId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNomDirector() {
        return nomDirector;
    }

    public void setNomDirector(String nomDirector) {
        this.nomDirector = nomDirector;
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
        if (!(object instanceof TipoDepartamento)) {
            return false;
        }
        TipoDepartamento other = (TipoDepartamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.TipoDepartamento[ id=" + id + " ]";
    }
    
}
