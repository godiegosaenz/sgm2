/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author dfcalderio
 */
@Entity
@Table(name = "cat_escritura_propietario")
@NamedQueries({
    @NamedQuery(name = "CatEscrituraPropietario.findAll", query = "SELECT c FROM CatEscrituraPropietario c")
    , @NamedQuery(name = "CatEscrituraPropietario.findById", query = "SELECT c FROM CatEscrituraPropietario c WHERE c.id = :id")
    , @NamedQuery(name = "CatEscrituraPropietario.findByCedula", query = "SELECT c FROM CatEscrituraPropietario c WHERE c.cedula = :cedula")
    , @NamedQuery(name = "CatEscrituraPropietario.findByPorcentajePosecion", query = "SELECT c FROM CatEscrituraPropietario c WHERE c.porcentajePosecion = :porcentajePosecion")
    , @NamedQuery(name = "CatEscrituraPropietario.findByCopropietario", query = "SELECT c FROM CatEscrituraPropietario c WHERE c.copropietario = :copropietario")
    , @NamedQuery(name = "CatEscrituraPropietario.findByUsuario", query = "SELECT c FROM CatEscrituraPropietario c WHERE c.usuario = :usuario")
    , @NamedQuery(name = "CatEscrituraPropietario.findByFecha", query = "SELECT c FROM CatEscrituraPropietario c WHERE c.fecha = :fecha")})
public class CatEscrituraPropietario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM) 
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    @Expose
    private Long id;
    @Size(max = 20)
    @Column(name = "cedula")
    @Expose
    private String cedula;
    @Column(name = "porcentaje_posecion")
    @Expose
    private BigDecimal porcentajePosecion;
    @Expose
    @Column(name = "copropietario")
    private Boolean copropietario;
    @Size(max = 100)
    @Column(name = "usuario")
    @Expose
    private String usuario;
    @Expose
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Expose
    @JoinColumn(name = "ente", referencedColumnName = "id")
    @ManyToOne
    private CatEnte ente;
    @Expose
    @JoinColumn(name = "escritura", referencedColumnName = "id_escritura")
    @ManyToOne(optional = false)
    private CatEscritura escritura;
//    @Expose
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @ManyToOne
    private CatPredioPropietario propietario;

    public CatEscrituraPropietario() {
    }

    public CatEscrituraPropietario(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public BigDecimal getPorcentajePosecion() {
        return porcentajePosecion;
    }

    public void setPorcentajePosecion(BigDecimal porcentajePosecion) {
        this.porcentajePosecion = porcentajePosecion;
    }

    public Boolean getCopropietario() {
        return copropietario;
    }

    public void setCopropietario(Boolean copropietario) {
        this.copropietario = copropietario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatEscritura getEscritura() {
        return escritura;
    }

    public void setEscritura(CatEscritura escritura) {
        this.escritura = escritura;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
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
        if (!(object instanceof CatEscrituraPropietario)) {
            return false;
        }
        CatEscrituraPropietario other = (CatEscrituraPropietario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + id;
    }

}
