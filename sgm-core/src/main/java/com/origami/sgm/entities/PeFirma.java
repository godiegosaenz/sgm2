/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramiteDet;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
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
@Table(name = "pe_firma", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeFirma.findAll", query = "SELECT p FROM PeFirma p"),
    @NamedQuery(name = "PeFirma.findById", query = "SELECT p FROM PeFirma p WHERE p.id = :id"),
    @NamedQuery(name = "PeFirma.findByCargo", query = "SELECT p FROM PeFirma p WHERE p.cargo = :cargo"),
    @NamedQuery(name = "PeFirma.findByDepartamento", query = "SELECT p FROM PeFirma p WHERE p.departamento = :departamento"),
    @NamedQuery(name = "PeFirma.findByNomCompleto", query = "SELECT p FROM PeFirma p WHERE p.nomCompleto = :nomCompleto"),
    @NamedQuery(name = "PeFirma.findByEstado", query = "SELECT p FROM PeFirma p WHERE p.estado = :estado")})
public class PeFirma implements Serializable {
    @OneToMany(mappedBy = "firma", fetch = FetchType.LAZY)
    private Collection<AclUser> aclUserCollection;
    @OneToMany(mappedBy = "firma", fetch = FetchType.LAZY)
    private Collection<CatEnte> catEnteCollection;
    @Column(name = "img_bytes")
    private Serializable imgBytes;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "cargo", length = 100)
    private String cargo;
    @Size(max = 100)
    @Column(name = "departamento", length = 100)
    private String departamento;
    @Size(max = 150)
    @Column(name = "nom_completo", length = 150)
    private String nomCompleto;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firma", fetch = FetchType.LAZY)
    private Collection<ProcesoReporte> procesoReporteCollection;
    @JoinColumn(name = "proceso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ReProcesos proceso;
    @OneToMany(mappedBy = "firma", fetch = FetchType.LAZY)
    private Collection<HistoricoTramiteDet> htdList;
    @OneToOne(mappedBy = "firma", fetch = FetchType.LAZY)
    private AclUser aclUser;

    public PeFirma() {
    }

    public PeFirma(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNomCompleto() {
        return nomCompleto;
    }

    public void setNomCompleto(String nomCompleto) {
        this.nomCompleto = nomCompleto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Collection<HistoricoTramiteDet> getHtdList() {
        return htdList;
    }

    public void setHtdList(Collection<HistoricoTramiteDet> htdList) {
        this.htdList = htdList;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<ProcesoReporte> getProcesoReporteCollection() {
        return procesoReporteCollection;
    }

    public void setProcesoReporteCollection(Collection<ProcesoReporte> procesoReporteCollection) {
        this.procesoReporteCollection = procesoReporteCollection;
    }

    public ReProcesos getProceso() {
        return proceso;
    }

    public void setProceso(ReProcesos proceso) {
        this.proceso = proceso;
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
        if (!(object instanceof PeFirma)) {
            return false;
        }
        PeFirma other = (PeFirma) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.PeFirma[ id=" + id + " ]";
    }

    public Serializable getImgBytes() {
        return imgBytes;
    }

    public void setImgBytes(Serializable imgBytes) {
        this.imgBytes = imgBytes;
    }

    public AclUser getAclUser() {
        return aclUser;
    }

    public void setAclUser(AclUser aclUser) {
        this.aclUser = aclUser;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<AclUser> getAclUserCollection() {
        return aclUserCollection;
    }

    public void setAclUserCollection(Collection<AclUser> aclUserCollection) {
        this.aclUserCollection = aclUserCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatEnte> getCatEnteCollection() {
        return catEnteCollection;
    }

    public void setCatEnteCollection(Collection<CatEnte> catEnteCollection) {
        this.catEnteCollection = catEnteCollection;
    }

}
