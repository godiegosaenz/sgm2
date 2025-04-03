/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;


import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author root
 */
@Entity
@Table(name = "ren_actividad_contribuyente", schema = SchemasConfig.FINANCIERO)
public class RenActividadContribuyente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 150)
    @Column(name = "nombre_local")
    private String nombreLocal;
    @Column(name = "predio")
    private BigInteger predio;
    @JoinColumn(name = "contribuyente", referencedColumnName = "id")
    @OneToOne(optional = false,  fetch = FetchType.LAZY)
    private CatEnte contribuyente;
    @JoinColumn(name = "representante_legal", referencedColumnName = "id")
    @OneToOne(optional = false,  fetch = FetchType.LAZY)
    private CatEnte representanteLegal;
    @JoinColumn(name = "contador", referencedColumnName = "id")
    @OneToOne(optional = false,  fetch = FetchType.LAZY)
    private CatEnte contador;
    @Size(max = 250)
    @Column(name = "razon_social")
    private String razonSocial;
    @Column(name = "calificacion_artesanal")
    private Boolean calificacionArtesanal;
    @Column(name = "numero_sucursales")
    private BigInteger numeroSucursales;
    @Column(name = "lleva_contabilidad")
    private Boolean llevaContabilidad;
    @Size(max = 20)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "es_propio")
    private Boolean esPropio;
    @Size(max = 150)
    @Column(name = "pagina_web")
    private String paginaWeb;
    
    @Column(name = "direccion_actividades")
    private String direccionActividades;
    @Column(name = "inicio_actividad")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioActividades;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "actividadContribuyente", fetch = FetchType.LAZY)
    private List<RenActividadContribuyenteTelefono> actividadContribuyenteTelefonos = new ArrayList<>();

    public RenActividadContribuyente() {
    }

    public RenActividadContribuyente(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreLocal() {
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public BigInteger getPredio() {
        return predio;
    }

    public void setPredio(BigInteger predio) {
        this.predio = predio;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Boolean getCalificacionArtesanal() {
        return calificacionArtesanal;
    }

    public void setCalificacionArtesanal(Boolean calificacionArtesanal) {
        this.calificacionArtesanal = calificacionArtesanal;
    }

    public BigInteger getNumeroSucursales() {
        return numeroSucursales;
    }

    public void setNumeroSucursales(BigInteger numeroSucursales) {
        this.numeroSucursales = numeroSucursales;
    }

    public Boolean getLlevaContabilidad() {
        return llevaContabilidad;
    }

    public void setLlevaContabilidad(Boolean llevaContabilidad) {
        this.llevaContabilidad = llevaContabilidad;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getEsPropio() {
        return esPropio;
    }

    public void setEsPropio(Boolean esPropio) {
        this.esPropio = esPropio;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public CatEnte getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(CatEnte contribuyente) {
        this.contribuyente = contribuyente;
    }

    public CatEnte getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(CatEnte representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public CatEnte getContador() {
        return contador;
    }

    public void setContador(CatEnte contador) {
        this.contador = contador;
    }

    public String getDireccionActividades() {
        return direccionActividades;
    }

    public void setDireccionActividades(String direccionActividades) {
        this.direccionActividades = direccionActividades;
    }

    public Date getInicioActividades() {
        return inicioActividades;
    }

    public void setInicioActividades(Date inicioActividades) {
        this.inicioActividades = inicioActividades;
    }

    public List<RenActividadContribuyenteTelefono> getActividadContribuyenteTelefonos() {
        return actividadContribuyenteTelefonos;
    }

    public void setActividadContribuyenteTelefonos(List<RenActividadContribuyenteTelefono> actividadContribuyenteTelefonos) {
        this.actividadContribuyenteTelefonos = actividadContribuyenteTelefonos;
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
        if (!(object instanceof RenActividadContribuyente)) {
            return false;
        }
        RenActividadContribuyente other = (RenActividadContribuyente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenDeclaracionContribuyente[ id=" + id + " ]";
    }
    
}
