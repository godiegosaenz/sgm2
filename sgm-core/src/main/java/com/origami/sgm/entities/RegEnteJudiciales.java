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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "reg_ente_judiciales",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegEnteJudiciales.findAll", query = "SELECT r FROM RegEnteJudiciales r"),
    @NamedQuery(name = "RegEnteJudiciales.findById", query = "SELECT r FROM RegEnteJudiciales r WHERE r.id = :id"),
    @NamedQuery(name = "RegEnteJudiciales.findByNombre", query = "SELECT r FROM RegEnteJudiciales r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegEnteJudiciales.findByAbreviatura", query = "SELECT r FROM RegEnteJudiciales r WHERE r.abreviatura = :abreviatura"),
    //@NamedQuery(name = "RegEnteJudiciales.findByAtajo", query = "SELECT r FROM RegEnteJudiciales r WHERE r.atajo = :atajo"),
    @NamedQuery(name = "RegEnteJudiciales.findByEstado", query = "SELECT r FROM RegEnteJudiciales r WHERE r.estado = :estado")})
public class RegEnteJudiciales implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "abreviatura")
    private String abreviatura;
//    @Size(max = 20)
//    @Column(name = "atajo", length = 20)
//    private String atajo;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @Column(name = "user_creacion")
    private String userCreacion;
    @Column(name = "user_edicion")
    private String userEdicion;
    
//    @ManyToMany(mappedBy = "regEnteJudicialesCollection", fetch = FetchType.LAZY)
//    private Collection<RegActo> regActoCollection;
//    
//    @OneToMany(mappedBy = "entJudicial", fetch = FetchType.LAZY)
//    private Collection<RegCatEnlaceEntesClie> regCatEnlaceEntesClieCollection;
//        
//    @OneToMany(mappedBy = "enteJudicial", fetch = FetchType.LAZY)
//    private Collection<RegMovimiento> regMovimientoCollection;

    public RegEnteJudiciales() {
    }

    public RegEnteJudiciales(Long id) {
        this.id = id;
    }

    public RegEnteJudiciales(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

//    public String getAtajo() {
//        return atajo;
//    }
//
//    public void setAtajo(String atajo) {
//        this.atajo = atajo;
//    }

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

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getUserCreacion() {
        return userCreacion;
    }

    public void setUserCreacion(String userCreacion) {
        this.userCreacion = userCreacion;
    }

    public String getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(String userEdicion) {
        this.userEdicion = userEdicion;
    }

//    public Collection<RegMovimiento> getRegMovimientoCollection() {
//        return regMovimientoCollection;
//    }
//
//    public void setRegMovimientoCollection(Collection<RegMovimiento> regMovimientoCollection) {
//        this.regMovimientoCollection = regMovimientoCollection;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegEnteJudiciales)) {
            return false;
        }
        RegEnteJudiciales other = (RegEnteJudiciales) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegEnteJudiciales[ id=" + id + " ]";
    }
    
}
