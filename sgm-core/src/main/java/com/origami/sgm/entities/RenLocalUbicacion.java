/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigInteger;
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

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(schema = SchemasConfig.FINANCIERO, name = "ren_local_ubicacion")
@NamedQueries({
    @NamedQuery(name = "RenLocalUbicacion.findAll", query = "SELECT r FROM RenLocalUbicacion r")})
public class RenLocalUbicacion implements Serializable {
    @Column(name = "id_sac")
    private BigInteger idSac;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 150)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "ciudadela")
    private BigInteger ciudadela;
    @Column(name = "estado")
    private Boolean estado;
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private Collection<RenLocalComercial> localComercialCollection;

    public RenLocalUbicacion() {
    }

    public RenLocalUbicacion(Long id) {
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

    public BigInteger getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(BigInteger ciudadela) {
        this.ciudadela = ciudadela;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Collection<RenLocalComercial> getLocalComercialCollection() {
        return localComercialCollection;
    }

    public void setLocalComercialCollection(Collection<RenLocalComercial> localComercialCollection) {
        this.localComercialCollection = localComercialCollection;
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
        if (!(object instanceof RenLocalUbicacion)) {
            return false;
        }
        RenLocalUbicacion other = (RenLocalUbicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenLocalUbicacion[ id=" + id + " ]";
    }

    public BigInteger getIdSac() {
        return idSac;
    }

    public void setIdSac(BigInteger idSac) {
        this.idSac = idSac;
    }
    
}
