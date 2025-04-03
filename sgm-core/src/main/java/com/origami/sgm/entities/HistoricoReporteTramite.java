/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "historico_reporte_tramite", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistoricoReporteTramite.findAll", query = "SELECT h FROM HistoricoReporteTramite h"),
    @NamedQuery(name = "HistoricoReporteTramite.findById", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.id = :id"),
    @NamedQuery(name = "HistoricoReporteTramite.findByTramite", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.tramite = :tramite"),
    @NamedQuery(name = "HistoricoReporteTramite.findBySecuencia", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.secuencia = :secuencia"),
    @NamedQuery(name = "HistoricoReporteTramite.findByNombreTarea", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.nombreTarea = :nombreTarea"),
    @NamedQuery(name = "HistoricoReporteTramite.findByUrl", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.url = :url"),
    @NamedQuery(name = "HistoricoReporteTramite.findByProceso", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.proceso = :proceso"),
    @NamedQuery(name = "HistoricoReporteTramite.findByCodValidacion", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.codValidacion = :codValidacion"),
    @NamedQuery(name = "HistoricoReporteTramite.findByFecCre", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.fecCre = :fecCre"),
    @NamedQuery(name = "HistoricoReporteTramite.findByEstado", query = "SELECT h FROM HistoricoReporteTramite h WHERE h.estado = :estado")})
public class HistoricoReporteTramite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;
    @Column(name = "secuencia")
    private BigInteger secuencia;
    @Size(max = 100)
    @Column(name = "nombre_tarea", length = 100)
    private String nombreTarea;
    @Size(max = 1000)
    @Column(name = "url", length = 1000)
    private String url;
    @Size(max = 50)
    @Column(name = "proceso", length = 50)
    private String proceso;
    @Size(max = 50)
    @Column(name = "cod_validacion", length = 50)
    private String codValidacion;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 50)
    @Column(name = "nombre_reporte", length = 100)
    private String nombreReporte;

    public HistoricoReporteTramite() {
    }

    public HistoricoReporteTramite(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getCodValidacion() {
        return codValidacion;
    }

    public void setCodValidacion(String codValidacion) {
        this.codValidacion = codValidacion;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
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
        if (!(object instanceof HistoricoReporteTramite)) {
            return false;
        }
        HistoricoReporteTramite other = (HistoricoReporteTramite) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "" + id + "";
    }

}
