/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Origami Integrales
 */
@Entity
@Table(name = "cat_ubicacion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatUbicacion.findAll", query = "SELECT c FROM CatUbicacion c"),
    @NamedQuery(name = "CatUbicacion.findById", query = "SELECT c FROM CatUbicacion c WHERE c.id = :id"),
    @NamedQuery(name = "CatUbicacion.findByNombre", query = "SELECT c FROM CatUbicacion c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "CatUbicacion.findByEstado", query = "SELECT c FROM CatUbicacion c WHERE c.estado = :estado"),
    @NamedQuery(name = "CatUbicacion.findByFechaIngreso", query = "SELECT c FROM CatUbicacion c WHERE c.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "CatUbicacion.findByFechaModificacion", query = "SELECT c FROM CatUbicacion c WHERE c.fechaModificacion = :fechaModificacion")})
public class CatUbicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 60)
    @Column(name = "nombre", length = 60)
    private String nombre;
    @Column(name = "estado")
    private Boolean estado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private Collection<CatCiudadela> catCiudadelaCollection;
    @JoinColumn(name = "usuario_modificacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioModificacion;
    @JoinColumn(name = "usuario_ingreso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AclUser usuarioIngreso;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parroquia", nullable = false)
    private Short parroquia;
    @Basic(optional = false)
    @NotNull
    @Column(name = "zona", nullable = false)
    private Short zona;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sector", nullable = false)
    private Short sector;
    @Column(name = "solar", nullable = false)
    private Short solar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mz", nullable = false)
    private Short mz;
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY)
    private List<MejObraUbicacion> mejObraUbicacions;
    @Column(name = "predio")
    private Long predio;
    @Column(name = "frentista")
    private Boolean frentista = Boolean.FALSE;
    @Column(name = "avaluos_totales")
    private BigDecimal avaluoTotales;        
    @Transient
    private BigDecimal avaluoMunicipal;
    @Transient
    private BigDecimal porcentajeAvaluo;
    @Transient
    private BigDecimal totalCem;
    @Transient
    private BigDecimal totalCemAnio;
    
    
//    @Transient
//    private  
            
    public CatUbicacion() {
    }

    public CatUbicacion(Long id) {
        this.id = id;
    }

    public CatUbicacion(Long id, Date fechaIngreso) {
        this.id = id;
        this.fechaIngreso = fechaIngreso;
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

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatCiudadela> getCatCiudadelaCollection() {
        return catCiudadelaCollection;
    }

    public void setCatCiudadelaCollection(Collection<CatCiudadela> catCiudadelaCollection) {
        this.catCiudadelaCollection = catCiudadelaCollection;
    }

    public AclUser getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(AclUser usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public AclUser getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(AclUser usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Short getParroquia() {
        return parroquia;
    }

    public void setParroquia(Short parroquia) {
        this.parroquia = parroquia;
    }

    public Short getZona() {
        return zona;
    }

    public void setZona(Short zona) {
        this.zona = zona;
    }

    public Short getSector() {
        return sector;
    }

    public void setSector(Short sector) {
        this.sector = sector;
    }

    public Short getSolar() {
        return solar;
    }

    public void setSolar(Short solar) {
        this.solar = solar;
    }

    public Short getMz() {
        return mz;
    }

    public void setMz(Short mz) {
        this.mz = mz;
    }

    public List<MejObraUbicacion> getMejObraUbicacions() {
        return mejObraUbicacions;
    }

    public void setMejObraUbicacions(List<MejObraUbicacion> mejObraUbicacions) {
        this.mejObraUbicacions = mejObraUbicacions;
    }
    
    public Boolean getFrentista() {
        return frentista;
    }

    public void setFrentista(Boolean frentista) {
        this.frentista = frentista;
    }

    public BigDecimal getPorcentajeAvaluo() {
        return porcentajeAvaluo;
    }

    public void setPorcentajeAvaluo(BigDecimal porcentajeAvaluo) {
        this.porcentajeAvaluo = porcentajeAvaluo;
    }

    public BigDecimal getTotalCem() {
        return totalCem;
    }

    public void setTotalCem(BigDecimal totalCem) {
        this.totalCem = totalCem;
    }

    public BigDecimal getTotalCemAnio() {
        return totalCemAnio;
    }

    public void setTotalCemAnio(BigDecimal totalCemAnio) {
        this.totalCemAnio = totalCemAnio;
    }

    public Long getPredio() {
        return predio;
    }

    public void setPredio(Long predio) {
        this.predio = predio;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public BigDecimal getAvaluoTotales() {
        return avaluoTotales;
    }

    public void setAvaluoTotales(BigDecimal avaluoTotales) {
        this.avaluoTotales = avaluoTotales;
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
        if (!(object instanceof CatUbicacion)) {
            return false;
        }
        CatUbicacion other = (CatUbicacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatUbicacion[ id=" + id + " ]";
    }
    
}
