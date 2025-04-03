/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "agenda", schema = SchemasConfig.AGENDA)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Agenda.findAll", query = "SELECT a FROM Agenda a"),
    @NamedQuery(name = "Agenda.findById", query = "SELECT a FROM Agenda a WHERE a.id = :id"),
    @NamedQuery(name = "Agenda.findByIdProceso", query = "SELECT a FROM Agenda a WHERE a.idProceso = :idProceso"),
    @NamedQuery(name = "Agenda.findByPrioridad", query = "SELECT a FROM Agenda a WHERE a.prioridad = :prioridad"),
    @NamedQuery(name = "Agenda.findByDescripcion", query = "SELECT a FROM Agenda a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Agenda.findByFInicio", query = "SELECT a FROM Agenda a WHERE a.fInicio = :fInicio"),
    @NamedQuery(name = "Agenda.findByFFin", query = "SELECT a FROM Agenda a WHERE a.fFin = :fFin"),
    @NamedQuery(name = "Agenda.findByObservacion", query = "SELECT a FROM Agenda a WHERE a.observacion = :observacion"),
    @NamedQuery(name = "Agenda.findByFecCre", query = "SELECT a FROM Agenda a WHERE a.fecCre = :fecCre"),
    @NamedQuery(name = "Agenda.findByFecAct", query = "SELECT a FROM Agenda a WHERE a.fecAct = :fecAct"),
    @NamedQuery(name = "Agenda.findByFinalizado", query = "SELECT a FROM Agenda a WHERE a.finalizado = :finalizado"),
    @NamedQuery(name = "Agenda.findByEstado", query = "SELECT a FROM Agenda a WHERE a.estado = :estado")})
public class Agenda implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 50)
    @Column(name = "id_proceso", length = 50)
    private String idProceso;
    @Column(name = "prioridad")
    private Integer prioridad;
    @Size(max = 200)
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    @Column(name = "f_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fInicio;
    @Column(name = "f_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fFin;
    @Size(max = 500)
    @Column(name = "observacion", length = 500)
    private String observacion;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "finalizado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalizado;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "lectura")
    private Boolean lectura;
    @Column(name = "contestacion")
    private Boolean contestacion;
    @Column(name = "confirmacion")
    private Boolean confirmacion;
    @Column(name = "color")
    private String color;
    @Column(name = "lugar")
    private String lugar;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte responsable;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoEvento tipo;
    @OneToMany(mappedBy = "agenda", fetch = FetchType.LAZY)
    private List<TipoAviso> tipoAvisoList;
    @OneToMany(mappedBy = "agenda", fetch = FetchType.LAZY)
    private List<AgendaDet> agendaDetList;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;

    public Agenda() {
    }

    public Agenda(Long id) {
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

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFInicio() {
        return fInicio;
    }

    public void setFInicio(Date fInicio) {
        this.fInicio = fInicio;
    }

    public Date getFFin() {
        return fFin;
    }

    public void setFFin(Date fFin) {
        this.fFin = fFin;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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

    public Date getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Date finalizado) {
        this.finalizado = finalizado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getLectura() {
        return lectura;
    }

    public void setLectura(Boolean lectura) {
        this.lectura = lectura;
    }

    public Boolean getContestacion() {
        return contestacion;
    }

    public void setContestacion(Boolean contestacion) {
        this.contestacion = contestacion;
    }

    public Boolean getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(Boolean confirmacion) {
        this.confirmacion = confirmacion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
    
    public TipoEvento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public CatEnte getResponsable() {
        return responsable;
    }

    public void setResponsable(CatEnte responsable) {
        this.responsable = responsable;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }
    
    @XmlTransient
    @JsonIgnore
    public List<TipoAviso> getTipoAvisoList() {
        return tipoAvisoList;
    }

    public void setTipoAvisoList(List<TipoAviso> tipoAvisoList) {
        this.tipoAvisoList = tipoAvisoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<AgendaDet> getAgendaDetList() {
        return agendaDetList;
    }

    public void setAgendaDetList(List<AgendaDet> agendaDetList) {
        this.agendaDetList = agendaDetList;
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
        if (!(object instanceof Agenda)) {
            return false;
        }
        Agenda other = (Agenda) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.Agenda[ id=" + id + " ]";
    }
    
}
