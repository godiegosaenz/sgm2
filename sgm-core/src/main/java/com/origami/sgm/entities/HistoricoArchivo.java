/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "historico_archivo", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistoricoArchivo.findAll", query = "SELECT h FROM HistoricoArchivo h"),
    @NamedQuery(name = "HistoricoArchivo.findById", query = "SELECT h FROM HistoricoArchivo h WHERE h.id = :id"),
    @NamedQuery(name = "HistoricoArchivo.findByProcessInstance", query = "SELECT h FROM HistoricoArchivo h WHERE h.processInstance = :processInstance"),
    @NamedQuery(name = "HistoricoArchivo.findByTaskId", query = "SELECT h FROM HistoricoArchivo h WHERE h.taskId = :taskId"),
    @NamedQuery(name = "HistoricoArchivo.findByCarpetaContenedora", query = "SELECT h FROM HistoricoArchivo h WHERE h.carpetaContenedora = :carpetaContenedora"),
    @NamedQuery(name = "HistoricoArchivo.findByEstado", query = "SELECT h FROM HistoricoArchivo h WHERE h.estado = :estado"),
    @NamedQuery(name = "HistoricoArchivo.findByFechaCreacion", query = "SELECT h FROM HistoricoArchivo h WHERE h.fechaCreacion = :fechaCreacion"),
    @NamedQuery(name = "HistoricoArchivo.findByTramite", query = "SELECT h FROM HistoricoArchivo h WHERE h.tramite = :tramite")})
public class HistoricoArchivo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "process_instance", length = 100)
    private String processInstance;
    @Size(max = 100)
    @Column(name = "task_id", length = 100)
    private String taskId;
    @Size(max = 1000)
    @Column(name = "carpeta_contenedora", length = 1000)
    private String carpetaContenedora;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Column(name = "id_archivo")
    private String idArchivo;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;

    public HistoricoArchivo() {
    }

    public HistoricoArchivo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(String processInstance) {
        this.processInstance = processInstance;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCarpetaContenedora() {
        return carpetaContenedora;
    }

    public void setCarpetaContenedora(String carpetaContenedora) {
        this.carpetaContenedora = carpetaContenedora;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public String getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(String idArchivo) {
        this.idArchivo = idArchivo;
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
        if (!(object instanceof HistoricoArchivo)) {
            return false;
        }
        HistoricoArchivo other = (HistoricoArchivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.HistoricoArchivo[ id=" + id + " ]";
    }
    
}
