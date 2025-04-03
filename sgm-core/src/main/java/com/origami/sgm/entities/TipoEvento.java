/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.Agenda;
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
 * @author Origami Integrales
 */
@Entity
@Table(name = "tipo_evento", schema = SchemasConfig.AGENDA)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoEvento.findAll", query = "SELECT t FROM TipoEvento t"),
    @NamedQuery(name = "TipoEvento.findById", query = "SELECT t FROM TipoEvento t WHERE t.id = :id"),
    @NamedQuery(name = "TipoEvento.findByDescripcion", query = "SELECT t FROM TipoEvento t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "TipoEvento.findByColor", query = "SELECT t FROM TipoEvento t WHERE t.color = :color"),
    @NamedQuery(name = "TipoEvento.findByReqDoc", query = "SELECT t FROM TipoEvento t WHERE t.reqDoc = :reqDoc"),
    @NamedQuery(name = "TipoEvento.findByUrl", query = "SELECT t FROM TipoEvento t WHERE t.url = :url"),
    @NamedQuery(name = "TipoEvento.findByFecCre", query = "SELECT t FROM TipoEvento t WHERE t.fecCre = :fecCre"),
    @NamedQuery(name = "TipoEvento.findByFecAct", query = "SELECT t FROM TipoEvento t WHERE t.fecAct = :fecAct"),
    @NamedQuery(name = "TipoEvento.findByEstado", query = "SELECT t FROM TipoEvento t WHERE t.estado = :estado")})
public class TipoEvento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 200)
    @Column(name = "descripcion", length = 200)
    private String descripcion;
    @Size(max = 200)
    @Column(name = "color", length = 200)
    private String color;
    @Column(name = "req_doc")
    private Boolean reqDoc;
    @Size(max = 200)
    @Column(name = "url", length = 200)
    private String url;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "estado")
    private Boolean estado;
    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private List<Agenda> agendaList;

    public TipoEvento() {
    }

    public TipoEvento(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getReqDoc() {
        return reqDoc;
    }

    public void setReqDoc(Boolean reqDoc) {
        this.reqDoc = reqDoc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @XmlTransient
    @JsonIgnore
    public List<Agenda> getAgendaList() {
        return agendaList;
    }

    public void setAgendaList(List<Agenda> agendaList) {
        this.agendaList = agendaList;
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
        if (!(object instanceof TipoEvento)) {
            return false;
        }
        TipoEvento other = (TipoEvento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.TipoEvento[ id=" + id + " ]";
    }
    
}
