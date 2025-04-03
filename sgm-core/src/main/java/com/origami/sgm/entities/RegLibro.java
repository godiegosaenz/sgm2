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
import javax.persistence.ManyToMany;
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
@Table(name = "reg_libro",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegLibro.findAll", query = "SELECT r FROM RegLibro r"),
    @NamedQuery(name = "RegLibro.findById", query = "SELECT r FROM RegLibro r WHERE r.id = :id"),
    @NamedQuery(name = "RegLibro.findByNombre", query = "SELECT r FROM RegLibro r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegLibro.findByEstado", query = "SELECT r FROM RegLibro r WHERE r.estado = :estado"),
    @NamedQuery(name = "RegLibro.findByEsMercantil", query = "SELECT r FROM RegLibro r WHERE r.esMercantil = :esMercantil"),
    @NamedQuery(name = "RegLibro.findByAnexoTresMercatilSocNombramientos", query = "SELECT r FROM RegLibro r WHERE r.anexoTresMercatilSocNombramientos = :anexoTresMercatilSocNombramientos"),
    @NamedQuery(name = "RegLibro.findByAnexoUnoRegPropiedad", query = "SELECT r FROM RegLibro r WHERE r.anexoUnoRegPropiedad = :anexoUnoRegPropiedad")})
public class RegLibro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size( max = 200)
    @Column(name = "nombre", length = 200)
    private String nombre;
    @Column(name = "fecha_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCre;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @Column(name = "user_cre")
    private String userCre;
    @Column(name = "user_edicion")
    private String userEdicion;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "es_mercantil")
    private Boolean esMercantil = false;
    @Column(name = "anexo_tres_mercatil_soc_nombramientos")
    private Boolean anexoTresMercatilSocNombramientos;
    @Column(name = "anexo_uno_reg_propiedad")
    private Boolean anexoUnoRegPropiedad;
    @Column(name = "motivada")
    private Boolean motivada = false;
    @Column(name = "estado_ficha")
    private Long estadoFicha;
    @ManyToMany(mappedBy = "regLibroCollection", fetch = FetchType.LAZY)
    private Collection<RegActo> regActoCollection;
    @OneToMany(mappedBy = "libro", fetch = FetchType.LAZY)
    private Collection<RegMovimiento> regMovimientoCollection;

    @Column(name = "nombre_carpeta")
    private String nombreCarpeta;
    
    public RegLibro() {
    }

    public RegLibro(Long id) {
        this.id = id;
    }

    public RegLibro(Long id, String nombre) {
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getEsMercantil() {
        return esMercantil;
    }

    public void setEsMercantil(Boolean esMercantil) {
        this.esMercantil = esMercantil;
    }

    public Boolean getAnexoTresMercatilSocNombramientos() {
        return anexoTresMercatilSocNombramientos;
    }

    public void setAnexoTresMercatilSocNombramientos(Boolean anexoTresMercatilSocNombramientos) {
        this.anexoTresMercatilSocNombramientos = anexoTresMercatilSocNombramientos;
    }

    public Boolean getAnexoUnoRegPropiedad() {
        return anexoUnoRegPropiedad;
    }

    public void setAnexoUnoRegPropiedad(Boolean anexoUnoRegPropiedad) {
        this.anexoUnoRegPropiedad = anexoUnoRegPropiedad;
    }

    public Date getFechaCre() {
        return fechaCre;
    }

    public void setFechaCre(Date fechaCre) {
        this.fechaCre = fechaCre;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getUserCre() {
        return userCre;
    }

    public void setUserCre(String userCre) {
        this.userCre = userCre;
    }

    public String getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(String userEdicion) {
        this.userEdicion = userEdicion;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegActo> getRegActoCollection() {
        return regActoCollection;
    }

    public void setRegActoCollection(Collection<RegActo> regActoCollection) {
        this.regActoCollection = regActoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimiento> getRegMovimientoCollection() {
        return regMovimientoCollection;
    }

    public void setRegMovimientoCollection(Collection<RegMovimiento> regMovimientoCollection) {
        this.regMovimientoCollection = regMovimientoCollection;
    }

    public Boolean getMotivada() {
        return motivada;
    }

    public void setMotivada(Boolean motivada) {
        this.motivada = motivada;
    }

    public Long getEstadoFicha() {
        return estadoFicha;
    }

    public void setEstadoFicha(Long estadoFicha) {
        this.estadoFicha = estadoFicha;
    }

    public String getNombreCarpeta() {
        return nombreCarpeta;
    }

    public void setNombreCarpeta(String nombreCarpeta) {
        this.nombreCarpeta = nombreCarpeta;
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
        if (!(object instanceof RegLibro)) {
            return false;
        }
        RegLibro other = (RegLibro) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegLibro[ id=" + id + " ]";
    }
    
}
