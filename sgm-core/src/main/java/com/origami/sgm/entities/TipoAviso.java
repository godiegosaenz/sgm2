/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.Agenda;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "tipo_aviso", schema = SchemasConfig.AGENDA)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoAviso.findAll", query = "SELECT t FROM TipoAviso t"),
    @NamedQuery(name = "TipoAviso.findById", query = "SELECT t FROM TipoAviso t WHERE t.id = :id"),
    @NamedQuery(name = "TipoAviso.findByAlerta", query = "SELECT t FROM TipoAviso t WHERE t.alerta = :alerta"),
    @NamedQuery(name = "TipoAviso.findByNotificacion", query = "SELECT t FROM TipoAviso t WHERE t.notificacion = :notificacion"),
    @NamedQuery(name = "TipoAviso.findByHora", query = "SELECT t FROM TipoAviso t WHERE t.hora = :hora"),
    @NamedQuery(name = "TipoAviso.findByIntervalo", query = "SELECT t FROM TipoAviso t WHERE t.intervalo = :intervalo"),
    @NamedQuery(name = "TipoAviso.findByDia", query = "SELECT t FROM TipoAviso t WHERE t.dia = :dia"),
    @NamedQuery(name = "TipoAviso.findByMes", query = "SELECT t FROM TipoAviso t WHERE t.mes = :mes"),
    @NamedQuery(name = "TipoAviso.findByFecCre", query = "SELECT t FROM TipoAviso t WHERE t.fecCre = :fecCre"),
    @NamedQuery(name = "TipoAviso.findByFecAct", query = "SELECT t FROM TipoAviso t WHERE t.fecAct = :fecAct"),
    @NamedQuery(name = "TipoAviso.findByEstado", query = "SELECT t FROM TipoAviso t WHERE t.estado = :estado")})
public class TipoAviso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "alerta")
    private Boolean alerta;
    @Column(name = "notificacion")
    private Boolean notificacion;
    @Column(name = "hora")
    private Boolean hora;
    @Column(name = "intervalo")
    private Integer intervalo;
    @Column(name = "dia")
    private Boolean dia;
    @Column(name = "mes")
    private Boolean mes;
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

    public TipoAviso() {
    }

    public TipoAviso(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAlerta() {
        return alerta;
    }

    public void setAlerta(Boolean alerta) {
        this.alerta = alerta;
    }

    public Boolean getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Boolean notificacion) {
        this.notificacion = notificacion;
    }

    public Boolean getHora() {
        return hora;
    }

    public void setHora(Boolean hora) {
        this.hora = hora;
    }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public Boolean getDia() {
        return dia;
    }

    public void setDia(Boolean dia) {
        this.dia = dia;
    }

    public Boolean getMes() {
        return mes;
    }

    public void setMes(Boolean mes) {
        this.mes = mes;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoAviso)) {
            return false;
        }
        TipoAviso other = (TipoAviso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.TipoAviso[ id=" + id + " ]";
    }
    
}
