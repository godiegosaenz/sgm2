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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "reg_acto", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegActo.findAll", query = "SELECT r FROM RegActo r"),
    @NamedQuery(name = "RegActo.findById", query = "SELECT r FROM RegActo r WHERE r.id = :id"),
    @NamedQuery(name = "RegActo.findByNombre", query = "SELECT r FROM RegActo r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegActo.findByAbreviatura", query = "SELECT r FROM RegActo r WHERE r.abreviatura = :abreviatura"),
    @NamedQuery(name = "RegActo.findByEstado", query = "SELECT r FROM RegActo r WHERE r.estado = :estado"),
    @NamedQuery(name = "RegActo.findByAnexoUnoRegPropiedad", query = "SELECT r FROM RegActo r WHERE r.anexoUnoRegPropiedad = :anexoUnoRegPropiedad"),
    @NamedQuery(name = "RegActo.findByAnexoDosMercantilContrato", query = "SELECT r FROM RegActo r WHERE r.anexoDosMercantilContrato = :anexoDosMercantilContrato"),
    @NamedQuery(name = "RegActo.findByAnexoTresMercatilSocNombramientos", query = "SELECT r FROM RegActo r WHERE r.anexoTresMercatilSocNombramientos = :anexoTresMercatilSocNombramientos")})
public class RegActo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "abreviatura")
    private String abreviatura;
    @Column(name = "estado")
    private Boolean estado = true;

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

    @Column(name = "anexo_uno_reg_propiedad")
    private Boolean anexoUnoRegPropiedad;
    @Column(name = "anexo_dos_mercantil_contrato")
    private Boolean anexoDosMercantilContrato;
    @Column(name = "anexo_tres_mercatil_soc_nombramientos")
    private Boolean anexoTresMercatilSocNombramientos;

    @JoinTable(name = "reg_acto_has_papel", joinColumns = {
        @JoinColumn(name = "acto", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "papel", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<RegCatPapel> regCatPapelCollection;
    @JoinTable(name = "reg_actos_has_libros", joinColumns = {
        @JoinColumn(name = "acto", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "libro", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<RegLibro> regLibroCollection;

    public RegActo() {
    }

    public RegActo(Long id) {
        this.id = id;
    }

    public RegActo(Long id, String nombre) {
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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

    public Boolean getAnexoUnoRegPropiedad() {
        return anexoUnoRegPropiedad;
    }

    public void setAnexoUnoRegPropiedad(Boolean anexoUnoRegPropiedad) {
        this.anexoUnoRegPropiedad = anexoUnoRegPropiedad;
    }

    public Boolean getAnexoDosMercantilContrato() {
        return anexoDosMercantilContrato;
    }

    public void setAnexoDosMercantilContrato(Boolean anexoDosMercantilContrato) {
        this.anexoDosMercantilContrato = anexoDosMercantilContrato;
    }

    public Boolean getAnexoTresMercatilSocNombramientos() {
        return anexoTresMercatilSocNombramientos;
    }

    public void setAnexoTresMercatilSocNombramientos(Boolean anexoTresMercatilSocNombramientos) {
        this.anexoTresMercatilSocNombramientos = anexoTresMercatilSocNombramientos;
    }

    public Collection<RegCatPapel> getRegCatPapelCollection() {
        return regCatPapelCollection;
    }

    public void setRegCatPapelCollection(Collection<RegCatPapel> regCatPapelCollection) {
        this.regCatPapelCollection = regCatPapelCollection;
    }

    public Collection<RegLibro> getRegLibroCollection() {
        return regLibroCollection;
    }

    public void setRegLibroCollection(Collection<RegLibro> regLibroCollection) {
        this.regLibroCollection = regLibroCollection;
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
        if (!(object instanceof RegActo)) {
            return false;
        }
        RegActo other = (RegActo) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegActo[ id=" + id + " ]";
    }

}
