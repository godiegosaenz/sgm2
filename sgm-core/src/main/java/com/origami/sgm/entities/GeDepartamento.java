/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.AclRol;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ge_departamento", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeDepartamento.findAll", query = "SELECT g FROM GeDepartamento g")
    ,
    @NamedQuery(name = "GeDepartamento.findById", query = "SELECT g FROM GeDepartamento g WHERE g.id = :id"),
    @NamedQuery(name = "GeDepartamento.findByNombre", query = "SELECT g FROM GeDepartamento g WHERE g.nombre = :nombre"),
    @NamedQuery(name = "GeDepartamento.findByEstado", query = "SELECT g FROM GeDepartamento g WHERE g.estado = :estado"),
    @NamedQuery(name = "GeDepartamento.findByDireccion", query = "SELECT g FROM GeDepartamento g WHERE g.direccion = :direccion"),
    @NamedQuery(name = "GeDepartamento.findByPadre", query = "SELECT g FROM GeDepartamento g WHERE g.padre = :padre")})
public class GeDepartamento implements Serializable {
    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    private Collection<GeTipoProcesos> geTipoProcesosCollection;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado")
    private boolean estado;
    @Column(name = "padre")
    private BigInteger padre;
//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "estado", nullable = false)
//    private boolean estado;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "nombre", length = 100)
    private String nombre;
    @Column(name = "direccion")
    private Boolean direccion = true;
//    @Column(name = "padre")
//    private Long padre;
    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<AclRol> aclRolCollection;
    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    private Collection<SvSolicitudDepartamento> svSolicitudDepartamentoCollection;
    @OneToMany(mappedBy = "departamento", fetch = FetchType.LAZY)
    @OrderBy("descripcion ASC")
    @Where(clause = "estado")
    private Collection<GeTipoTramite> geTipoTramiteCollection;

    public GeDepartamento() {
    }

    public GeDepartamento(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getDireccion() {
        return direccion;
    }

    public void setDireccion(Boolean direccion) {
        this.direccion = direccion;
    }


    @XmlTransient
    @JsonIgnore
    public Collection<AclRol> getAclRolCollection() {
        return aclRolCollection;
    }

    public void setAclRolCollection(Collection<AclRol> aclRolCollection) {
        this.aclRolCollection = aclRolCollection;
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
        if (!(object instanceof GeDepartamento)) {
            return false;
        }
        GeDepartamento other = (GeDepartamento) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return nombre;
    }

    public BigInteger getPadre() {
        return padre;
    }

    public void setPadre(BigInteger padre) {
        this.padre = padre;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Collection<SvSolicitudDepartamento> getSvSolicitudDepartamentoCollection() {
        return svSolicitudDepartamentoCollection;
    }

    public void setSvSolicitudDepartamentoCollection(Collection<SvSolicitudDepartamento> svSolicitudDepartamentoCollection) {
        this.svSolicitudDepartamentoCollection = svSolicitudDepartamentoCollection;
    }

    public Collection<GeTipoTramite> getGeTipoTramiteCollection() {
        return geTipoTramiteCollection;
    }

    public void setGeTipoTramiteCollection(Collection<GeTipoTramite> geTipoTramiteCollection) {
        this.geTipoTramiteCollection = geTipoTramiteCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTipoProcesos> getGeTipoProcesosCollection() {
        return geTipoProcesosCollection;
    }

    public void setGeTipoProcesosCollection(Collection<GeTipoProcesos> geTipoProcesosCollection) {
        this.geTipoProcesosCollection = geTipoProcesosCollection;
    }

}
