/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.HistoricoTramites;
import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEdfProp;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Where;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "pe_inspeccion_final", schema = SchemasConfig.APP1)
@XmlRootElement
public class PeInspeccionFinal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @Column(name = "desc_edificacion", length = 100)
    private String descEdificacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_const", precision = 15, scale = 2)
    private BigDecimal areaConst;
    @Column(name = "area_solar", precision = 15, scale = 2)
    private BigDecimal areaSolar;
    @Column(name = "retiro_frontal", precision = 15, scale = 2)
    private BigDecimal retiroFrontal;
    @Column(name = "retiro_posterior", precision = 15, scale = 2)
    private BigDecimal retiroPosterior;
    @Column(name = "retiro_lateral1", precision = 15, scale = 2)
    private BigDecimal retiroLateral1;
    @Column(name = "retiro_lateral2", precision = 15, scale = 2)
    private BigDecimal retiroLateral2;
    @Column(name = "altura")
    private BigDecimal altura;
    @Column(name = "pisos_sbn")
    private Integer pisosSbn;
    @Column(name = "pisos_bnb")
    private Integer pisosBnb;
    @Column(name = "cant_edificaciones")
    private BigDecimal cantEdificaciones;
    @Column(name = "area_edificaciones")
    private BigDecimal areaEdificaciones;
    @Column(name = "cant_parqueos")
    private BigInteger cantParqueos;
    @Column(name = "area_parqueos")
    private BigDecimal areaParqueos;
    @Column(name = "evaluo_liquidacion", precision = 19, scale = 2)
    private BigDecimal evaluoLiquidacion;
    @Column(name = "impuesto", precision = 15, scale = 2)
    private BigDecimal impuesto;
    @Column(name = "inspeccion", precision = 15, scale = 2)
    private BigDecimal inspeccion;
    @Column(name = "revicion", precision = 15, scale = 2)
    private BigDecimal revicion;
    @Column(name = "no_adeudar", precision = 15, scale = 2)
    private BigDecimal noAdeudar;
    @Column(name = "avaluo_inspeccion", precision = 19, scale = 2)
    private BigDecimal avaluoInspeccion;
    @Column(name = "area_edificada", precision = 15, scale = 2)
    private BigDecimal areaEdificada;
    @Size(max = 500)
    @Column(name = "observacion", length = 500)
    private String observacion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "estado", nullable = false, length = 10)
    private String estado;
    @Column(name = "numero_tramite_inspeccion")
    private BigInteger numeroTramiteInspeccion;
    @Size(max = 100)
    @Column(name = "calle", length = 100)
    private String calle;
    @Column(name = "num_permiso_construc")
    private BigInteger numPermisoConstruc;
    @Column(name = "anio_permiso_construc")
    private BigInteger anioPermisoConstruc;
    @Column(name = "anio_inspeccion")
    private BigInteger anioInspeccion;
    @Column(name = "num_reporte")
    private BigInteger numReporte;
    @Column(name = "mostrar_certificado")
    private Boolean mostrarCertificado;
    
    @Column(name = "fecha_inspeccion")
    private Date fechaInspeccion;
    
    @Column(name = "fecha_ingreso")
    private Date fechaIngreso;
    @Column(name = "usuario_ingreso", length = 50)
    private String usuarioIngreso;
    
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte solicitante;
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte propietario;
    @JoinColumn(name = "resp_tecnico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte respTecnico;
    @JoinColumn(name = "prop_planta_baja", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propPlantaBaja;
    @JoinColumn(name = "prop_planta_alta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propPlantaAlta;
    @JoinColumn(name = "prop_cubierta", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propCubierta;
    @JoinColumn(name = "prop_paredes", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propParedes;
    @JoinColumn(name = "prop_instalaciones", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propInstalaciones;
    @JoinColumn(name = "prop_estructura", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEdfProp propEstructura;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuario;
    
    
    @OneToMany(mappedBy = "idInspeccion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<PeInspeccionCabEdificacion> peInspeccionCabEdificacionCollection;
    
    @OneToMany(mappedBy = "inspeccion", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFotos> fotos;
    
    @JoinTable(name = "pe_inspeccion_has_detalle", joinColumns = {
        @JoinColumn(name = "inspeccion", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "detalle", referencedColumnName = "id", nullable = false)})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<CtlgItem> detalleList;
    
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private HistoricoTramites tramite;

    public PeInspeccionFinal() {
    }

    public PeInspeccionFinal(Long id) {
        this.id = id;
    }

    public PeInspeccionFinal(Long id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescEdificacion() {
        return descEdificacion;
    }

    public void setDescEdificacion(String descEdificacion) {
        this.descEdificacion = descEdificacion;
    }

    public BigDecimal getAreaConst() {
        return areaConst;
    }

    public void setAreaConst(BigDecimal areaConst) {
        this.areaConst = areaConst;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getRetiroFrontal() {
        return retiroFrontal;
    }

    public void setRetiroFrontal(BigDecimal retiroFrontal) {
        this.retiroFrontal = retiroFrontal;
    }

    public BigDecimal getRetiroPosterior() {
        return retiroPosterior;
    }

    public void setRetiroPosterior(BigDecimal retiroPosterior) {
        this.retiroPosterior = retiroPosterior;
    }

    public BigDecimal getRetiroLateral1() {
        return retiroLateral1;
    }

    public void setRetiroLateral1(BigDecimal retiroLateral1) {
        this.retiroLateral1 = retiroLateral1;
    }

    public BigDecimal getRetiroLateral2() {
        return retiroLateral2;
    }

    public void setRetiroLateral2(BigDecimal retiroLateral2) {
        this.retiroLateral2 = retiroLateral2;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public Integer getPisosSbn() {
        return pisosSbn;
    }

    public void setPisosSbn(Integer pisosSbn) {
        this.pisosSbn = pisosSbn;
    }

    public Integer getPisosBnb() {
        return pisosBnb;
    }

    public void setPisosBnb(Integer pisosBnb) {
        this.pisosBnb = pisosBnb;
    }

    public BigDecimal getCantEdificaciones() {
        return cantEdificaciones;
    }

    public void setCantEdificaciones(BigDecimal cantEdificaciones) {
        this.cantEdificaciones = cantEdificaciones;
    }

    public BigDecimal getAreaEdificaciones() {
        return areaEdificaciones;
    }

    public void setAreaEdificaciones(BigDecimal areaEdificaciones) {
        this.areaEdificaciones = areaEdificaciones;
    }

    public BigInteger getCantParqueos() {
        return cantParqueos;
    }

    public void setCantParqueos(BigInteger cantParqueos) {
        this.cantParqueos = cantParqueos;
    }

    public BigDecimal getAreaParqueos() {
        return areaParqueos;
    }

    public void setAreaParqueos(BigDecimal areaParqueos) {
        this.areaParqueos = areaParqueos;
    }

    public BigDecimal getEvaluoLiquidacion() {
        return evaluoLiquidacion;
    }

    public void setEvaluoLiquidacion(BigDecimal evaluoLiquidacion) {
        this.evaluoLiquidacion = evaluoLiquidacion;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getInspeccion() {
        return inspeccion;
    }

    public void setInspeccion(BigDecimal inspeccion) {
        this.inspeccion = inspeccion;
    }

    public BigDecimal getRevicion() {
        return revicion;
    }

    public void setRevicion(BigDecimal revicion) {
        this.revicion = revicion;
    }

    public BigDecimal getNoAdeudar() {
        return noAdeudar;
    }

    public void setNoAdeudar(BigDecimal noAdeudar) {
        this.noAdeudar = noAdeudar;
    }

    public BigDecimal getAreaEdificada() {
        return areaEdificada;
    }

    public void setAreaEdificada(BigDecimal areaEdificada) {
        this.areaEdificada = areaEdificada;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigInteger getNumeroTramiteInspeccion() {
        return numeroTramiteInspeccion;
    }

    public void setNumeroTramiteInspeccion(BigInteger numeroTramiteInspeccion) {
        this.numeroTramiteInspeccion = numeroTramiteInspeccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public BigInteger getNumPermisoConstruc() {
        return numPermisoConstruc;
    }

    public void setNumPermisoConstruc(BigInteger numPermisoConstruc) {
        this.numPermisoConstruc = numPermisoConstruc;
    }

    public BigInteger getAnioPermisoConstruc() {
        return anioPermisoConstruc;
    }

    public void setAnioPermisoConstruc(BigInteger anioPermisoConstruc) {
        this.anioPermisoConstruc = anioPermisoConstruc;
    }

    public BigInteger getAnioInspeccion() {
        return anioInspeccion;
    }

    public void setAnioInspeccion(BigInteger anioInspeccion) {
        this.anioInspeccion = anioInspeccion;
    }

    public BigInteger getNumReporte() {
        return numReporte;
    }

    public void setNumReporte(BigInteger numReporte) {
        this.numReporte = numReporte;
    }

    public Boolean getMostrarCertificado() {
        return mostrarCertificado;
    }

    public void setMostrarCertificado(Boolean mostrarCertificado) {
        this.mostrarCertificado = mostrarCertificado;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public CatEnte getRespTecnico() {
        return respTecnico;
    }

    public void setRespTecnico(CatEnte respTecnico) {
        this.respTecnico = respTecnico;
    }

    public CatEdfProp getPropPlantaBaja() {
        return propPlantaBaja;
    }

    public void setPropPlantaBaja(CatEdfProp propPlantaBaja) {
        this.propPlantaBaja = propPlantaBaja;
    }

    public CatEdfProp getPropPlantaAlta() {
        return propPlantaAlta;
    }

    public void setPropPlantaAlta(CatEdfProp propPlantaAlta) {
        this.propPlantaAlta = propPlantaAlta;
    }

    public CatEdfProp getPropCubierta() {
        return propCubierta;
    }

    public void setPropCubierta(CatEdfProp propCubierta) {
        this.propCubierta = propCubierta;
    }

    public CatEdfProp getPropParedes() {
        return propParedes;
    }

    public void setPropParedes(CatEdfProp propParedes) {
        this.propParedes = propParedes;
    }

    public CatEdfProp getPropInstalaciones() {
        return propInstalaciones;
    }

    public void setPropInstalaciones(CatEdfProp propInstalaciones) {
        this.propInstalaciones = propInstalaciones;
    }

    public CatEdfProp getPropEstructura() {
        return propEstructura;
    }

    public void setPropEstructura(CatEdfProp propEstructura) {
        this.propEstructura = propEstructura;
    }

    public AclUser getUsuario() {
        return usuario;
    }

    public void setUsuario(AclUser usuario) {
        this.usuario = usuario;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
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
        if (!(object instanceof PeInspeccionFinal)) {
            return false;
        }
        PeInspeccionFinal other = (PeInspeccionFinal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.PeInspeccionFinal[ id=" + id + " ]";
    }

    public Collection<CtlgItem> getDetalleList() {
        return detalleList;
    }

    public void setDetalleList(Collection<CtlgItem> detalleList) {
        this.detalleList = detalleList;
    }

    public Collection<PeInspeccionCabEdificacion> getPeInspeccionCabEdificacionCollection() {
        return peInspeccionCabEdificacionCollection;
    }

    public void setPeInspeccionCabEdificacionCollection(Collection<PeInspeccionCabEdificacion> peInspeccionCabEdificacionCollection) {
        this.peInspeccionCabEdificacionCollection = peInspeccionCabEdificacionCollection;
    }

    public Date getFechaInspeccion() {
        return fechaInspeccion;
    }

    public void setFechaInspeccion(Date fechaInspeccion) {
        this.fechaInspeccion = fechaInspeccion;
    }

    public Collection<PeInspeccionFotos> getFotos() {
        return fotos;
    }

    public void setFotos(Collection<PeInspeccionFotos> fotos) {
        this.fotos = fotos;
    }

    public BigDecimal getAvaluoInspeccion() {
        return avaluoInspeccion;
    }

    public void setAvaluoInspeccion(BigDecimal avaluoInspeccion) {
        this.avaluoInspeccion = avaluoInspeccion;
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }
    
}
