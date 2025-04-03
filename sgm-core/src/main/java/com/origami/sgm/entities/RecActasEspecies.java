/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "rec_actas_especies", schema = SchemasConfig.FINANCIERO)
public class RecActasEspecies implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_acta")
    private Integer numActa;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "usuario_asignado")
    private Long usuarioAsignado;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Size(max = 100)
    @Column(name = "usuario_ingreso", length = 100)
    private String usuarioIngreso;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;
    @Column(name = "tesorero")
    private Long tesorero;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acta", fetch = FetchType.LAZY)
    private Collection<RecActasEspeciesDet> recActasEspeciesDetCollection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumActa() {
        return numActa;
    }

    public void setNumActa(Integer numActa) {
        this.numActa = numActa;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Long getUsuarioAsignado() {
        return usuarioAsignado;
    }

    public void setUsuarioAsignado(Long usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getTesorero() {
        return tesorero;
    }

    public void setTesorero(Long tesorero) {
        this.tesorero = tesorero;
    }

    public Collection<RecActasEspeciesDet> getRecActasEspeciesDetCollection() {
        return recActasEspeciesDetCollection;
    }

    public void setRecActasEspeciesDetCollection(Collection<RecActasEspeciesDet> recActasEspeciesDetCollection) {
        this.recActasEspeciesDetCollection = recActasEspeciesDetCollection;
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
        if (!(object instanceof RecActasEspecies)) {
            return false;
        }
        RecActasEspecies other = (RecActasEspecies) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RecActasEspecies[ id=" + id + " ]";
    }
}
