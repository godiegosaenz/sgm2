/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

//import com.origami.sgm.util.HtmlUtil;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigInteger;
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
import javax.persistence.OneToOne;
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
@Table(name = "reg_certificado",  schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"task_id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegCertificado.findAll", query = "SELECT r FROM RegCertificado r"),
    @NamedQuery(name = "RegCertificado.findById", query = "SELECT r FROM RegCertificado r WHERE r.id = :id"),
    @NamedQuery(name = "RegCertificado.findByNumTramite", query = "SELECT r FROM RegCertificado r WHERE r.numTramite = :numTramite"),
    @NamedQuery(name = "RegCertificado.findByTaskId", query = "SELECT r FROM RegCertificado r WHERE r.taskId = :taskId"),
    @NamedQuery(name = "RegCertificado.findByNumCertificado", query = "SELECT r FROM RegCertificado r WHERE r.numCertificado = :numCertificado"),
    @NamedQuery(name = "RegCertificado.findByFechaEmision", query = "SELECT r FROM RegCertificado r WHERE r.fechaEmision = :fechaEmision"),
    @NamedQuery(name = "RegCertificado.findByObservacion", query = "SELECT r FROM RegCertificado r WHERE r.observacion = :observacion"),
    @NamedQuery(name = "RegCertificado.findByCertificadoImpreso", query = "SELECT r FROM RegCertificado r WHERE r.certificadoImpreso = :certificadoImpreso")})
public class RegCertificado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "num_tramite")
    private BigInteger numTramite; // numero de seguimiento del tramite
    @Column(name = "task_id")
    private String taskId;
    @Column(name = "num_certificado")
    private BigInteger numCertificado; // numero de tramite del registro
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "certificado_impreso")
    private Boolean certificadoImpreso = false;
    @JoinColumn(name = "tipo_certificado", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RegTipoCertificado tipoCertificado;
    @JoinColumn(name = "user_creador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser userCreador;
    @Column(name = "user_edicion")
    private Long userEdicion;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @Column(name = "secuencia")
    private Integer secuencia;
    @Column(name = "beneficiario")
    private String beneficiario;
    @JoinColumn(name = "regp_certificados_inscripciones", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private RegpCertificadosInscripciones regpCertificadoInscripciones;
    @JoinColumn(name = "registrador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegRegistrador registrador;
    
    public RegCertificado() {
    }

    public RegCertificado(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(BigInteger numTramite) {
        this.numTramite = numTramite;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public BigInteger getNumCertificado() {
        return numCertificado;
    }

    public void setNumCertificado(BigInteger numCertificado) {
        this.numCertificado = numCertificado;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Boolean getCertificadoImpreso() {
        return certificadoImpreso;
    }

    public void setCertificadoImpreso(Boolean certificadoImpreso) {
        this.certificadoImpreso = certificadoImpreso;
    }

    public RegTipoCertificado getTipoCertificado() {
        return tipoCertificado;
    }

    public void setTipoCertificado(RegTipoCertificado tipoCertificado) {
        this.tipoCertificado = tipoCertificado;
    }

    public AclUser getUserCreador() {
        return userCreador;
    }

    public void setUserCreador(AclUser userCreador) {
        this.userCreador = userCreador;
    }

    public Long getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(Long userEdicion) {
        this.userEdicion = userEdicion;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public RegpCertificadosInscripciones getRegpCertificadoInscripciones() {
        return regpCertificadoInscripciones;
    }

    public void setRegpCertificadoInscripciones(RegpCertificadosInscripciones regpCertificadoInscripciones) {
        this.regpCertificadoInscripciones = regpCertificadoInscripciones;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public RegRegistrador getRegistrador() {
        return registrador;
    }

    public void setRegistrador(RegRegistrador registrador) {
        this.registrador = registrador;
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
        if (!(object instanceof RegCertificado)) {
            return false;
        }
        RegCertificado other = (RegCertificado) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegCertificado[ id=" + id + " ]";
    }
    
}
