/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.GeTipoTramite;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Origami Integrales
 */
@Entity
@Table(name = "parametros_disparador", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ParametrosDisparador.findAll", query = "SELECT p FROM ParametrosDisparador p"),
    @NamedQuery(name = "ParametrosDisparador.findById", query = "SELECT p FROM ParametrosDisparador p WHERE p.id = :id"),
    @NamedQuery(name = "ParametrosDisparador.findByInterfaz", query = "SELECT p FROM ParametrosDisparador p WHERE p.interfaz = :interfaz"),
    @NamedQuery(name = "ParametrosDisparador.findByVarInterfaz", query = "SELECT p FROM ParametrosDisparador p WHERE p.varInterfaz = :varInterfaz"),
    @NamedQuery(name = "ParametrosDisparador.findByVarResp", query = "SELECT p FROM ParametrosDisparador p WHERE p.varResp = :varResp"),
    @NamedQuery(name = "ParametrosDisparador.findByEstado", query = "SELECT p FROM ParametrosDisparador p WHERE p.estado = :estado"),
    @NamedQuery(name = "ParametrosDisparador.findByFecCre", query = "SELECT p FROM ParametrosDisparador p WHERE p.fecCre = :fecCre")})
public class ParametrosDisparador implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "interfaz", nullable = false, length = 200)
    private String interfaz;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "var_interfaz", nullable = false, length = 50)
    private String varInterfaz;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "var_resp", nullable = false, length = 50)
    private String varResp;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    
    @JoinColumn(name = "tipo_tramite", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite tipoTramite;
    
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser responsable;
    @Column(name = "incial")
    private Boolean incial;

    public ParametrosDisparador() {
    }

    public ParametrosDisparador(Long id) {
        this.id = id;
    }

    public ParametrosDisparador(Long id, String interfaz, String varInterfaz, String varResp) {
        this.id = id;
        this.interfaz = interfaz;
        this.varInterfaz = varInterfaz;
        this.varResp = varResp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterfaz() {
        return interfaz;
    }

    public void setInterfaz(String interfaz) {
        this.interfaz = interfaz;
    }

    public String getVarInterfaz() {
        return varInterfaz;
    }

    public void setVarInterfaz(String varInterfaz) {
        this.varInterfaz = varInterfaz;
    }

    public String getVarResp() {
        return varResp;
    }

    public void setVarResp(String varResp) {
        this.varResp = varResp;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public AclUser getResponsable() {
        return responsable;
    }

    public void setResponsable(AclUser responsable) {
        this.responsable = responsable;
    }

    public Boolean getIncial() {
        return incial;
    }

    public void setIncial(Boolean incial) {
        this.incial = incial;
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
        if (!(object instanceof ParametrosDisparador)) {
            return false;
        }
        ParametrosDisparador other = (ParametrosDisparador) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.ParametrosDisparador[ id=" + id + " ]";
    }
    
}
