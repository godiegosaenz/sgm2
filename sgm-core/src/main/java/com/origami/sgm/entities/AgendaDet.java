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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "agenda_det", schema = SchemasConfig.AGENDA)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AgendaDet.findAll", query = "SELECT a FROM AgendaDet a"),
    @NamedQuery(name = "AgendaDet.findById", query = "SELECT a FROM AgendaDet a WHERE a.id = :id"),
    @NamedQuery(name = "AgendaDet.findByInvolucrado", query = "SELECT a FROM AgendaDet a WHERE a.involucrado = :involucrado"),
    @NamedQuery(name = "AgendaDet.findByIdProceso", query = "SELECT a FROM AgendaDet a WHERE a.idProceso = :idProceso"),
    @NamedQuery(name = "AgendaDet.findByDetalle", query = "SELECT a FROM AgendaDet a WHERE a.detalle = :detalle"),
    @NamedQuery(name = "AgendaDet.findByAsistencia", query = "SELECT a FROM AgendaDet a WHERE a.asistencia = :asistencia"),
    @NamedQuery(name = "AgendaDet.findByFecAtencion", query = "SELECT a FROM AgendaDet a WHERE a.fecAtencion = :fecAtencion"),
    @NamedQuery(name = "AgendaDet.findByFecCre", query = "SELECT a FROM AgendaDet a WHERE a.fecCre = :fecCre"),
    @NamedQuery(name = "AgendaDet.findByFecAct", query = "SELECT a FROM AgendaDet a WHERE a.fecAct = :fecAct"),
    @NamedQuery(name = "AgendaDet.findByEstado", query = "SELECT a FROM AgendaDet a WHERE a.estado = :estado")})
public class AgendaDet implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 50)
    @Column(name = "id_proceso", length = 50)
    private String idProceso;
    @Size(max = 4000)
    @Column(name = "detalle", length = 4000)
    private String detalle;
    @Column(name = "asistencia")
    private Boolean asistencia;
    @Column(name = "fec_atencion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAtencion;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "agenda", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Agenda agenda;
    @JoinColumn(name = "involucrado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte involucrado;
    @Transient
    private String usuario;

    public AgendaDet() {
    }

    public AgendaDet(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Boolean asistencia) {
        this.asistencia = asistencia;
    }

    public Date getFecAtencion() {
        return fecAtencion;
    }

    public void setFecAtencion(Date fecAtencion) {
        this.fecAtencion = fecAtencion;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public CatEnte getInvolucrado() {
        return involucrado;
    }

    public void setInvolucrado(CatEnte involucrado) {
        this.involucrado = involucrado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof AgendaDet)) {
            return false;
        }
        AgendaDet other = (AgendaDet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.AgendaDet[ id=" + id + " ]";
    }
    
}
