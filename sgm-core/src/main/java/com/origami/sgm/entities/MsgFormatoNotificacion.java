/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "msg_formato_notificacion", schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MsgFormatoNotificacion.findAll", query = "SELECT m FROM MsgFormatoNotificacion m"),
    @NamedQuery(name = "MsgFormatoNotificacion.findById", query = "SELECT m FROM MsgFormatoNotificacion m WHERE m.id = :id"),
    @NamedQuery(name = "MsgFormatoNotificacion.findByHeader", query = "SELECT m FROM MsgFormatoNotificacion m WHERE m.header = :header"),
    @NamedQuery(name = "MsgFormatoNotificacion.findByFooter", query = "SELECT m FROM MsgFormatoNotificacion m WHERE m.footer = :footer"),
    @NamedQuery(name = "MsgFormatoNotificacion.findByEstado", query = "SELECT m FROM MsgFormatoNotificacion m WHERE m.estado = :estado")})
public class MsgFormatoNotificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 2000)
    @Column(name = "header", length = 2000)
    private String header;
    @Size(max = 2000)
    @Column(name = "footer", length = 2000)
    private String footer;
    @Column(name = "estado")
    private Integer estado;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MsgTipoFormatoNotificacion tipo;

    public MsgFormatoNotificacion() {
    }

    public MsgFormatoNotificacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public MsgTipoFormatoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(MsgTipoFormatoNotificacion tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof MsgFormatoNotificacion)) {
            return false;
        }
        MsgFormatoNotificacion other = (MsgFormatoNotificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.MsgFormatoNotificacion[ id=" + id + " ]";
    }
    
}
