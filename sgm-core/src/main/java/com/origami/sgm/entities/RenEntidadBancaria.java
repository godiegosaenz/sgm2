/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_entidad_bancaria", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenEntidadBancaria.findAll", query = "SELECT r FROM RenEntidadBancaria r")})
public class RenEntidadBancaria implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 150)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "entidad_bancaria_padre")
    private BigInteger entidadBancariaPadre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado")
    private boolean estado;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoEntidadBancaria tipo;
    @OneToMany(mappedBy = "banco", fetch = FetchType.LAZY)
    private Collection<RenPagoDetalle> renPagoDetalleCollection;

    public RenEntidadBancaria() {
    }

    public RenEntidadBancaria(Long id) {
        this.id = id;
    }

    public RenEntidadBancaria(Long id, Date fechaIngreso, boolean estado) {
        this.id = id;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
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

    public BigInteger getEntidadBancariaPadre() {
        return entidadBancariaPadre;
    }

    public void setEntidadBancariaPadre(BigInteger entidadBancariaPadre) {
        this.entidadBancariaPadre = entidadBancariaPadre;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public RenTipoEntidadBancaria getTipo() {
        return tipo;
    }

    public void setTipo(RenTipoEntidadBancaria tipo) {
        this.tipo = tipo;
    }

    public Collection<RenPagoDetalle> getRenPagoDetalleCollection() {
        return renPagoDetalleCollection;
    }

    public void setRenPagoDetalleCollection(Collection<RenPagoDetalle> renPagoDetalleCollection) {
        this.renPagoDetalleCollection = renPagoDetalleCollection;
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
        if (!(object instanceof RenEntidadBancaria)) {
            return false;
        }
        RenEntidadBancaria other = (RenEntidadBancaria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenEntidadBancaria[ id=" + id + " ]";
    }
    
}
