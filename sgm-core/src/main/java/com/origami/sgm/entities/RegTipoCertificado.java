/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_tipo_certificado", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre_certificado"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegTipoCertificado.findAll", query = "SELECT r FROM RegTipoCertificado r"),
    @NamedQuery(name = "RegTipoCertificado.findById", query = "SELECT r FROM RegTipoCertificado r WHERE r.id = :id"),
    @NamedQuery(name = "RegTipoCertificado.findByNombreCertificado", query = "SELECT r FROM RegTipoCertificado r WHERE r.nombreCertificado = :nombreCertificado"),
    @NamedQuery(name = "RegTipoCertificado.findByEstado", query = "SELECT r FROM RegTipoCertificado r WHERE r.estado = :estado"),
    @NamedQuery(name = "RegTipoCertificado.findByPlantilla", query = "SELECT r FROM RegTipoCertificado r WHERE r.plantilla = :plantilla")})
public class RegTipoCertificado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "nombre_certificado")
    private String nombreCertificado;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "plantilla")
    private String plantilla;
    @Column(name = "user_creador")
    private String userCreador;
    @Column(name = "user_modificacion")
    private String userModificacion;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
    @Column(name = "tipo_plantilla")
    private Integer tipoPlantilla = 0;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoCertificado", fetch = FetchType.LAZY)
    private Collection<RegCertificado> regCertificadoCollection;

    public RegTipoCertificado() {
    }

    public RegTipoCertificado(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCertificado() {
        return nombreCertificado;
    }

    public void setNombreCertificado(String nombreCertificado) {
        this.nombreCertificado = nombreCertificado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public String getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(String userCreador) {
        this.userCreador = userCreador;
    }

    public String getUserModificacion() {
        return userModificacion;
    }

    public void setUserModificacion(String userModificacion) {
        this.userModificacion = userModificacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getTipoPlantilla() {
        return tipoPlantilla;
    }

    public void setTipoPlantilla(Integer tipoPlantilla) {
        this.tipoPlantilla = tipoPlantilla;
    }

    public Collection<RegCertificado> getRegCertificadoCollection() {
        return regCertificadoCollection;
    }

    public void setRegCertificadoCollection(Collection<RegCertificado> regCertificadoCollection) {
        this.regCertificadoCollection = regCertificadoCollection;
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
        if (!(object instanceof RegTipoCertificado)) {
            return false;
        }
        RegTipoCertificado other = (RegTipoCertificado) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegTipoCertificado[ id=" + id + " ]";
    }

}
