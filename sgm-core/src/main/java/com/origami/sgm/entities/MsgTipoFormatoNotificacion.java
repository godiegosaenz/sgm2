/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "msg_tipo_formato_notificacion", schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MsgTipoFormatoNotificacion.findAll", query = "SELECT m FROM MsgTipoFormatoNotificacion m"),
    @NamedQuery(name = "MsgTipoFormatoNotificacion.findById", query = "SELECT m FROM MsgTipoFormatoNotificacion m WHERE m.id = :id"),
    @NamedQuery(name = "MsgTipoFormatoNotificacion.findByDescripcion", query = "SELECT m FROM MsgTipoFormatoNotificacion m WHERE m.descripcion = :descripcion")})
public class MsgTipoFormatoNotificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 1000)
    @Column(name = "descripcion", length = 1000)
    private String descripcion;
    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private Collection<MsgFormatoNotificacion> msgFormatoNotificacionCollection;

    public MsgTipoFormatoNotificacion() {
    }

    public MsgTipoFormatoNotificacion(Long id) {
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

    @XmlTransient
    @JsonIgnore
    public Collection<MsgFormatoNotificacion> getMsgFormatoNotificacionCollection() {
        return msgFormatoNotificacionCollection;
    }

    public void setMsgFormatoNotificacionCollection(Collection<MsgFormatoNotificacion> msgFormatoNotificacionCollection) {
        this.msgFormatoNotificacionCollection = msgFormatoNotificacionCollection;
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
        if (!(object instanceof MsgTipoFormatoNotificacion)) {
            return false;
        }
        MsgTipoFormatoNotificacion other = (MsgTipoFormatoNotificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.MsgTipoFormatoNotificacion[ id=" + id + " ]";
    }
    
}
