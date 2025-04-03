/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_ente_interviniente",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegEnteInterviniente.findAll", query = "SELECT r FROM RegEnteInterviniente r"),
    @NamedQuery(name = "RegEnteInterviniente.findById", query = "SELECT r FROM RegEnteInterviniente r WHERE r.id = :id"),
    @NamedQuery(name = "RegEnteInterviniente.findByCedRuc", query = "SELECT r FROM RegEnteInterviniente r WHERE r.cedRuc = :cedRuc"),
    @NamedQuery(name = "RegEnteInterviniente.findBySecuencia", query = "SELECT r FROM RegEnteInterviniente r WHERE r.secuencia = :secuencia"),
    @NamedQuery(name = "RegEnteInterviniente.findByNombre", query = "SELECT r FROM RegEnteInterviniente r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegEnteInterviniente.findByTipoInterv", query = "SELECT r FROM RegEnteInterviniente r WHERE r.tipoInterv = :tipoInterv"),
    @NamedQuery(name = "RegEnteInterviniente.findByProcedencia", query = "SELECT r FROM RegEnteInterviniente r WHERE r.procedencia = :procedencia")})
public class RegEnteInterviniente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "ced_ruc")
    private String cedRuc;
    @Column(name = "secuencia")
    private Integer secuencia;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "tipo_interv")
    private String tipoInterv;
    @Column(name = "procedencia")
    private String procedencia;
    
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "usuario")
    private String usuario;
    
    @OneToMany(mappedBy = "enteInterv", fetch = FetchType.LAZY)
    private Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection;
    @OneToMany(mappedBy = "enteInterv", fetch = FetchType.LAZY)
    private Collection<RegMovimientoSocios> regMovimientoSociosCollection;
    @OneToMany(mappedBy = "enteInterv", fetch = FetchType.LAZY)
    private Collection<RegMovimientoCliente> regMovimientoClienteCollection;
    @OneToMany(mappedBy = "interviniente", fetch = FetchType.LAZY)
    private Collection<RegFichaPropietarios> regFichaPropietariosCollection;

    @Transient
    private String apellidos;
    
    @Transient
    private String tipo;
    
    public RegEnteInterviniente() {
    }

    public RegEnteInterviniente(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedRuc() {
        return cedRuc;
    }

    public void setCedRuc(String cedRuc) {
        this.cedRuc = cedRuc;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoInterv() {
        return tipoInterv;
    }

    public void setTipoInterv(String tipoInterv) {
        this.tipoInterv = tipoInterv;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Collection<RegMovimientoRepresentante> getRegMovimientoRepresentanteCollection() {
        return regMovimientoRepresentanteCollection;
    }

    public void setRegMovimientoRepresentanteCollection(Collection<RegMovimientoRepresentante> regMovimientoRepresentanteCollection) {
        this.regMovimientoRepresentanteCollection = regMovimientoRepresentanteCollection;
    }

    public Collection<RegMovimientoSocios> getRegMovimientoSociosCollection() {
        return regMovimientoSociosCollection;
    }

    public void setRegMovimientoSociosCollection(Collection<RegMovimientoSocios> regMovimientoSociosCollection) {
        this.regMovimientoSociosCollection = regMovimientoSociosCollection;
    }

    public Collection<RegMovimientoCliente> getRegMovimientoClienteCollection() {
        return regMovimientoClienteCollection;
    }

    public void setRegMovimientoClienteCollection(Collection<RegMovimientoCliente> regMovimientoClienteCollection) {
        this.regMovimientoClienteCollection = regMovimientoClienteCollection;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Collection<RegFichaPropietarios> getRegFichaPropietariosCollection() {
        return regFichaPropietariosCollection;
    }

    public void setRegFichaPropietariosCollection(Collection<RegFichaPropietarios> regFichaPropietariosCollection) {
        this.regFichaPropietariosCollection = regFichaPropietariosCollection;
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
        if (!(object instanceof RegEnteInterviniente)) {
            return false;
        }
        RegEnteInterviniente other = (RegEnteInterviniente) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegEnteInterviniente[ id=" + id + " ]";
    }
    
}
