/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
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

/**
 *
 * @author Angel Navarro
 * @date 26/09/2016
 */
@Entity
@Table(name = "observaciones_local", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "ObservacionesLocal.findAll", query = "SELECT o FROM ObservacionesLocal o")})
public class ObservacionesLocal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "local_comercial", referencedColumnName = "id")
    @ManyToOne
    private RenLocalComercial localComercial;
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "user_cre")
    private String userCre;
    @Column(name = "tarea")
    private String tarea;
    @Column(name = "estado")
    private Boolean estado;

    public ObservacionesLocal() {
    }

    public ObservacionesLocal(Long id) {
        this.id = id;
    }

    public ObservacionesLocal(RenLocalComercial localComercial, String observacion, String userCre, String tarea) {
        this.localComercial = localComercial;
        this.observacion = observacion;
        this.fecCre = new Date();
        this.userCre = userCre;
        this.tarea = tarea;
        this.estado = true;
    }

    public ObservacionesLocal(RenLocalComercial localComercial, String observacion, Date fecCre, String userCre, String tarea, Boolean estado) {
        this.localComercial = localComercial;
        this.observacion = observacion;
        this.fecCre = fecCre;
        this.userCre = userCre;
        this.tarea = tarea;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
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

    public String getUserCre() {
        return userCre;
    }

    public void setUserCre(String userCre) {
        this.userCre = userCre;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObservacionesLocal)) {
            return false;
        }
        ObservacionesLocal other = (ObservacionesLocal) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.ObservacionesLocal[ id=" + id + " ]";
    }

}
