/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.ObservacionesLocal;
import com.origami.sgm.entities.CertificadoExoneracionLocalActivos;
import com.origami.sgm.entities.CmMultas;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_local_comercial", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenLocalComercial.findAll", query = "SELECT r FROM RenLocalComercial r"),
    @NamedQuery(name = "RenLocalComercial.findAllByLiquidacion", query = "SELECT r FROM RenLocalComercial r INNER JOIN r.renLiquidacionCollection l WHERE l.id = :liquidacion")})
public class RenLocalComercial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 150)
    @Column(name = "nombre_local")
    private String nombreLocal;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    
    @Size(max = 50)
    @Column(name = "num_local")
    private String numLocal;
    
    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area")
    private BigDecimal area;
    @Column(name = "turismo")
    private Boolean turismo;
    @Column(name = "contabilidad")
    private Boolean contabilidad = false;
    @Size(max = 20)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "estado")
    private Boolean estado;
    
    @Column(name = "estado_local")
    private BigInteger estadoLocalComercial;
    
    @JoinTable(name = "ren_actividad_por_local", schema = SchemasConfig.FINANCIERO, joinColumns = {
        @JoinColumn(name = "local_comercial", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "actividad", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<RenActividadComercial> renActividadComercialCollection;
    @OneToMany(mappedBy = "localComercial", fetch = FetchType.LAZY)
    private Collection<RenLiquidacion> renLiquidacionCollection;
    @OneToMany(mappedBy = "localComercial", fetch = FetchType.LAZY)
    private Collection<RenPermisosFuncionamientoLocalComercial> permisosFuncionamientoLCCollection;
    @OneToMany(mappedBy = "localComercial", fetch = FetchType.LAZY)
    private Collection<CertificadoExoneracionLocalActivos> certificadosExoLocalActivosCollection;
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte propietario;
    @JoinColumn(name = "tipo_local", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoLocalComercial tipoLocal;
    @OneToMany(mappedBy = "localComercial", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    @OrderBy(clause = "anio_balance DESC")
    private Collection<RenActivosLocalComercial> activosLocalComercialCollection;
    @OneToMany(mappedBy = "localComercial", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    @OrderBy(clause = "anio_balance DESC")
    private Collection<RenBalanceLocalComercial> balanceLocalComercialCollection;
    
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalCategoria categoria;
    
    @JoinColumn(name = "ubicacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenLocalUbicacion ubicacion;
    
    @JoinColumn(name = "razon_social", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte razonSocial;
    @OneToMany(mappedBy = "localComercial", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<RenLocalCantidadAccesorios> cantidadAccesoriosCollection;
    @Column(name = "inicio_actividad")
    @Temporal(TemporalType.DATE)
    private Date inicioActividad;
    @OneToMany(mappedBy = "localComercial")
    private Collection<CmMultas> cmMultasCollection;
    @Column(name = "matriz")
    private Boolean matriz;
    @OneToMany(mappedBy = "localComercial")
    private List<ObservacionesLocal> observacionesLocalsCollections;
    
    public RenLocalComercial() {
    }

    public RenLocalComercial(Long id) {
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

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public Collection<RenLiquidacion> getRenLiquidacionCollection() {
        return renLiquidacionCollection;
    }

    public void setRenLiquidacionCollection(Collection<RenLiquidacion> renLiquidacionCollection) {
        this.renLiquidacionCollection = renLiquidacionCollection;
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }

    public String getNumLocal() {
        return numLocal;
    }

    public void setNumLocal(String numLocal) {
        this.numLocal = numLocal;
    }

    public RenTipoLocalComercial getTipoLocal() {
        return tipoLocal;
    }

    public void setTipoLocal(RenTipoLocalComercial tipoLocal) {
        this.tipoLocal = tipoLocal;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public Boolean getTurismo() {
        return turismo;
    }

    public void setTurismo(Boolean turismo) {
        this.turismo = turismo;
    }

    public Boolean getContabilidad() {
        return contabilidad;
    }

    public void setContabilidad(Boolean contabilidad) {
        this.contabilidad = contabilidad;
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

    public Collection<RenActividadComercial> getRenActividadComercialCollection() {
        return renActividadComercialCollection;
    }

    public void setRenActividadComercialCollection(Collection<RenActividadComercial> renActividadComercialCollection) {
        this.renActividadComercialCollection = renActividadComercialCollection;
    }

    public Collection<RenActivosLocalComercial> getActivosLocalComercialCollection() {
        return activosLocalComercialCollection;
    }

    public void setActivosLocalComercialCollection(Collection<RenActivosLocalComercial> activosLocalComercialCollection) {
        this.activosLocalComercialCollection = activosLocalComercialCollection;
    }


    public RenLocalCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(RenLocalCategoria categoria) {
        this.categoria = categoria;
    }

    public RenLocalUbicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(RenLocalUbicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Collection<RenBalanceLocalComercial> getBalanceLocalComercialCollection() {
        return balanceLocalComercialCollection;
    }

    public void setBalanceLocalComercialCollection(Collection<RenBalanceLocalComercial> balanceLocalComercialCollection) {
        this.balanceLocalComercialCollection = balanceLocalComercialCollection;
    }

    public CatEnte getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(CatEnte razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Collection<RenLocalCantidadAccesorios> getCantidadAccesoriosCollection() {
        return cantidadAccesoriosCollection;
    }

    public void setCantidadAccesoriosCollection(Collection<RenLocalCantidadAccesorios> cantidadAccesoriosCollection) {
        this.cantidadAccesoriosCollection = cantidadAccesoriosCollection;
    }

    public Date getInicioActividad() {
        return inicioActividad;
    }

    public void setInicioActividad(Date inicioActividad) {
        this.inicioActividad = inicioActividad;
    }

    public Collection<CmMultas> getCmMultasCollection() {
        return cmMultasCollection;
    }

    public void setCmMultasCollection(Collection<CmMultas> cmMultasCollection) {
        this.cmMultasCollection = cmMultasCollection;
    }

    public BigInteger getEstadoLocalComercial() {
        return estadoLocalComercial;
    }

    public void setEstadoLocalComercial(BigInteger estadoLocalComercial) {
        this.estadoLocalComercial = estadoLocalComercial;
    }

    public void setMatriz(Boolean matriz) {
        this.matriz = matriz;
    }

    public Boolean getMatriz() {
        return matriz;
    }

    public Collection<CertificadoExoneracionLocalActivos> getCertificadosExoLocalActivosCollection() {
        return certificadosExoLocalActivosCollection;
    }

    public void setCertificadosExoLocalActivosCollection(Collection<CertificadoExoneracionLocalActivos> certificadosExoLocalActivosCollection) {
        this.certificadosExoLocalActivosCollection = certificadosExoLocalActivosCollection;
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
        if (!(object instanceof RenLocalComercial)) {
            return false;
        }
        RenLocalComercial other = (RenLocalComercial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenLocalComercial[ id=" + id + " ]";
    }

    public Collection<RenPermisosFuncionamientoLocalComercial> getPermisosFuncionamientoLCCollection() {
        return permisosFuncionamientoLCCollection;
    }

    public void setPermisosFuncionamientoLCCollection(Collection<RenPermisosFuncionamientoLocalComercial> permisosFuncionamientoLCCollection) {
        this.permisosFuncionamientoLCCollection = permisosFuncionamientoLCCollection;
    }

    public List<ObservacionesLocal> getObservacionesLocalsCollections() {
        return observacionesLocalsCollections;
    }

    public void setObservacionesLocalsCollections(List<ObservacionesLocal> observacionesLocalsCollections) {
        this.observacionesLocalsCollections = observacionesLocalsCollections;
    }
    
    public String getActividades(){
        if(renActividadComercialCollection == null)
            return "";
        String s = "";
        Integer i = 0;
        
        for(RenActividadComercial temp : renActividadComercialCollection){
            i++;
            if(i!=renActividadComercialCollection.size())
                s = s + temp.getDescripcion()+ "; ";
            else
                s = s + temp.getDescripcion();
        }        
        return s;
    }
}
