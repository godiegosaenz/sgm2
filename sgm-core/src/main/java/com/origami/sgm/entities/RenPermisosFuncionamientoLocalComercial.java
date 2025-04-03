/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_permisos_funcionamiento_local", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
public class RenPermisosFuncionamientoLocalComercial implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @OneToOne(fetch = FetchType.LAZY)
    private HistoricoTramites ht;
    @JoinColumn(name = "local_comercial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalComercial localComercial;
    @JoinColumn(name = "contador", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte contador;
    @JoinColumn(name = "clase_local", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenClaseLocal claseLocal;
    @Column(name = "num_trabajadores")
    private Integer numTrabajadores;
    @JoinColumn(name = "afiliacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenAfiliacionCamaraProduccion afiliacionCamara;
    @Column(name = "tipo")
    private Integer tipo;
    @Column(name = "primera_vez")
    private Boolean primeraVez;
    @Column(name = "anio_declaracion")
    private Integer anioDeclaracion;
    @Column(name = "num_declaracion")
    private Long numDeclaracion;
    @Column(name = "fecha_emision")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaEmision;
    @Column(name = "fecha_caducidad")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaCaducidad;
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "es_publico")
    private Boolean esPublico;
    
    @Column(name = "patente")
    private Boolean patente = false;
    @Column(name = "activos")
    private Boolean activos = false;
    @Column(name = "turismo")
    private Boolean turismo = false;
    @Column(name = "tasa_habilitacion")
    private Boolean tasaHabilitacion = false;
    @Column(name = "rotulos")
    private Boolean rotulos = false;
    @Column(name = "area_bombero")
    private BigDecimal areaBombero;
    
    @OneToOne(mappedBy = "permisoFuncionamiento")
    private RenTurismo renTurismoCollection;
    
    @Column(name="mtrs_rotulo")
    private BigDecimal mtrsRotulo;
    @Column(name="tipo_rotulo")
    private String tipoRotulo;
    @OneToOne(mappedBy = "permiso")
    private RenActivosLocalComercial activosPermiso;
    @OneToOne(mappedBy = "permiso")
    private RenBalanceLocalComercial balancePermiso;
    @Column(name = "inspeccion_comisaria")
    private Boolean inspeccionComisaria = false;


    public RenPermisosFuncionamientoLocalComercial() {
    }
    
    public RenPermisosFuncionamientoLocalComercial(Long id){
        this.id = id;
    }
    
    public RenPermisosFuncionamientoLocalComercial(Long id, HistoricoTramites ht){
        this.id = id;
        this.ht = ht;
    }
            
    public RenPermisosFuncionamientoLocalComercial(Long id, HistoricoTramites ht, RenLocalComercial localComercial, CatEnte contador, RenClaseLocal claseLocal, Integer numTrabajadores, RenAfiliacionCamaraProduccion afiliacionCamara, Integer tipo, Boolean primeraVez, Integer anioDeclaracion, Long numDeclaracion, Date fechaEmision, String usuarioIngreso, Boolean esPublico, Boolean patente, Boolean activos, Boolean turismo, Boolean tasaHabilitacion, Boolean rotulos, BigDecimal areaBombero) {
        this.id = id;
        this.ht = ht;
        this.localComercial = localComercial;
        this.contador = contador;
        this.claseLocal = claseLocal;
        this.numTrabajadores = numTrabajadores;
        this.afiliacionCamara = afiliacionCamara;
        this.tipo = tipo;
        this.primeraVez = primeraVez;
        this.anioDeclaracion = anioDeclaracion;
        this.numDeclaracion = numDeclaracion;
        this.fechaEmision = fechaEmision;
        this.usuarioIngreso = usuarioIngreso;
        this.esPublico = esPublico;
        this.patente = patente;
        this.activos = activos;
        this.turismo = turismo;
        this.tasaHabilitacion = tasaHabilitacion;
        this.rotulos = rotulos;
        this.areaBombero = areaBombero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HistoricoTramites getHt() {
        return ht;
    }

    public void setHt(HistoricoTramites ht) {
        this.ht = ht;
    }

    public RenLocalComercial getLocalComercial() {
        return localComercial;
    }

    public void setLocalComercial(RenLocalComercial localComercial) {
        this.localComercial = localComercial;
    }

    public CatEnte getContador() {
        return contador;
    }

    public void setContador(CatEnte contador) {
        this.contador = contador;
    }

    public RenClaseLocal getClaseLocal() {
        return claseLocal;
    }

    public void setClaseLocal(RenClaseLocal claseLocal) {
        this.claseLocal = claseLocal;
    }

    public Integer getNumTrabajadores() {
        return numTrabajadores;
    }

    public void setNumTrabajadores(Integer numTrabajadores) {
        this.numTrabajadores = numTrabajadores;
    }

    public RenAfiliacionCamaraProduccion getAfiliacionCamara() {
        return afiliacionCamara;
    }

    public void setAfiliacionCamara(RenAfiliacionCamaraProduccion afiliacionCamara) {
        this.afiliacionCamara = afiliacionCamara;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Boolean getPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(Boolean primeraVez) {
        this.primeraVez = primeraVez;
    }

    public Integer getAnioDeclaracion() {
        return anioDeclaracion;
    }

    public void setAnioDeclaracion(Integer anioDeclaracion) {
        this.anioDeclaracion = anioDeclaracion;
    }

    public Long getNumDeclaracion() {
        return numDeclaracion;
    }

    public void setNumDeclaracion(Long numDeclaracion) {
        this.numDeclaracion = numDeclaracion;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Boolean getEsPublico() {
        return esPublico;
    }

    public void setEsPublico(Boolean esPublico) {
        this.esPublico = esPublico;
    }

    public Boolean getPatente() {
        return patente;
    }

    public void setPatente(Boolean patente) {
        this.patente = patente;
    }

    public Boolean getActivos() {
        return activos;
    }

    public void setActivos(Boolean activos) {
        this.activos = activos;
    }

    public Boolean getTurismo() {
        return turismo;
    }

    public void setTurismo(Boolean turismo) {
        this.turismo = turismo;
    }

    public Boolean getTasaHabilitacion() {
        return tasaHabilitacion;
    }

    public void setTasaHabilitacion(Boolean tasaHabilitacion) {
        this.tasaHabilitacion = tasaHabilitacion;
    }

    public Boolean getRotulos() {
        return rotulos;
    }

    public void setRotulos(Boolean rotulos) {
        this.rotulos = rotulos;
    }

    public BigDecimal getAreaBombero() {
        return areaBombero;
    }

    public void setAreaBombero(BigDecimal areaBombero) {
        this.areaBombero = areaBombero;
    }

    public RenTurismo getRenTurismoCollection() {
        return renTurismoCollection;
    }

    public void setRenTurismoCollection(RenTurismo renTurismoCollection) {
        this.renTurismoCollection = renTurismoCollection;
    }

    public BigDecimal getMtrsRotulo() {
        return mtrsRotulo;
    }

    public void setMtrsRotulo(BigDecimal mtrsRotulo) {
        this.mtrsRotulo = mtrsRotulo;
    }

    public String getTipoRotulo() {
        return tipoRotulo;
    }

    public void setTipoRotulo(String tipoRotulo) {
        this.tipoRotulo = tipoRotulo;
    }

    public RenActivosLocalComercial getActivosPermiso() {
        return activosPermiso;
    }

    public void setActivosPermiso(RenActivosLocalComercial activosPermiso) {
        this.activosPermiso = activosPermiso;
    }

    public RenBalanceLocalComercial getBalancePermiso() {
        return balancePermiso;
    }

    public void setBalancePermiso(RenBalanceLocalComercial balancePermiso) {
        this.balancePermiso = balancePermiso;
    }

    public Boolean getInspeccionComisaria() {
        return inspeccionComisaria;
    }

    public void setInspeccionComisaria(Boolean inspeccionComisaria) {
        this.inspeccionComisaria = inspeccionComisaria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HistoricoTramites)) {
            return false;
        }
        RenPermisosFuncionamientoLocalComercial other = (RenPermisosFuncionamientoLocalComercial) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }
    
    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial[ id=" + id + " ]";
    }
}
