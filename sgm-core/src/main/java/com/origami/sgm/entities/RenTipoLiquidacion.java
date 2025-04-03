/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.RenLiquidacion;
import com.origami.sgm.entities.FnSolicitudTipoLiquidacionExoneracion;
import com.origami.sgm.entities.FnSolicitudCondonacion;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Where;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_tipo_liquidacion", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenTipoTransaccion.findAll", query = "SELECT r FROM RenTipoLiquidacion r")})
public class RenTipoLiquidacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 20)
    @Column(name = "cta_transaccion")
    private String ctaTransaccion;

    @Column(name = "nombre_titulo")
    private String nombreTitulo;
    @Size(max = 5)
    @Column(name = "prefijo")
    private String prefijo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado")
    private boolean estado;
    @Size(max = 20)
    @Column(name = "usuario_ingreso")
    private String usuarioIngreso;

    @Size(max = 500)
    @Column(name = "nombre_transaccion")
    private String nombreTransaccion;
    @Column(name = "transaccion_padre")
    private Long transaccionPadre;

    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;

    @Column(name = "codigo_titulo_reporte")
    private Long codigoTituloReporte;

    @Column(name = "mostrar_transaccion")
    private Boolean mostrarTransaccion;

    @Size(max = 500)
    @Column(name = "nombre_reporte")
    private String nombreReporte;

    @JoinColumn(name = "tipo_transaccion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoTransaccion tipoTransaccion;

    @OneToMany(mappedBy = "tipoLiquidacion", fetch = FetchType.LAZY)
    private Collection<RenLiquidacion> renLiquidacionCollection;

    @OneToMany(mappedBy = "tipoLiquidacion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    @OrderBy("prioridad ASC, descripcion ASC")
    private Collection<RenRubrosLiquidacion> renRubrosLiquidacionCollection;

    @OneToMany(mappedBy = "tipoLiquidacion", fetch = FetchType.LAZY)
    private Collection<RenSecuenciaNumLiquidicacion> renNumLiquidacionCollection;

    @OneToMany(mappedBy = "tipoLiquidacion", fetch = FetchType.LAZY)
    private Collection<FnSolicitudCondonacion> solicitudCondonacionCollection;
    @Column(name = "permite_anulacion")
    private Boolean permiteAnulacion;

    @Column(name = "permite_exoneracion")
    private Boolean permiteExoneracion = Boolean.FALSE;

    @OneToMany(mappedBy = "tipoLiquidacion", fetch = FetchType.LAZY)
    private Collection<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionCollection;

    @Transient
    private Boolean tomado = false;

    public RenTipoLiquidacion() {
    }

    public RenTipoLiquidacion(Long id) {
        this.id = id;
    }

    public RenTipoLiquidacion(Long id, boolean estado, Date fechaIngreso) {
        this.id = id;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
    }

    public Collection<FnSolicitudTipoLiquidacionExoneracion> getTipoLiquidacionCollection() {
        return tipoLiquidacionCollection;
    }

    public void setTipoLiquidacionCollection(Collection<FnSolicitudTipoLiquidacionExoneracion> tipoLiquidacionCollection) {
        this.tipoLiquidacionCollection = tipoLiquidacionCollection;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCtaTransaccion() {
        return ctaTransaccion;
    }

    public void setCtaTransaccion(String ctaTransaccion) {
        this.ctaTransaccion = ctaTransaccion;
    }

    public String getNombreTitulo() {
        return nombreTitulo;
    }

    public void setNombreTitulo(String nombreTitulo) {
        this.nombreTitulo = nombreTitulo;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
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

    public Collection<RenLiquidacion> getRenLiquidacionCollection() {
        return renLiquidacionCollection;
    }

    public void setRenLiquidacionCollection(Collection<RenLiquidacion> renLiquidacionCollection) {
        this.renLiquidacionCollection = renLiquidacionCollection;
    }

    public Collection<RenRubrosLiquidacion> getRenRubrosLiquidacionCollection() {
        return renRubrosLiquidacionCollection;
    }

    public void setRenRubrosLiquidacionCollection(Collection<RenRubrosLiquidacion> renRubrosLiquidacionCollection) {
        this.renRubrosLiquidacionCollection = renRubrosLiquidacionCollection;
    }

    public String getNombreTransaccion() {
        return nombreTransaccion;
    }

    public void setNombreTransaccion(String nombreTransaccion) {
        this.nombreTransaccion = nombreTransaccion;
    }

    public Long getTransaccionPadre() {
        return transaccionPadre;
    }

    public void setTransaccionPadre(Long transaccionPadre) {
        this.transaccionPadre = transaccionPadre;
    }

    public RenTipoTransaccion getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(RenTipoTransaccion tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public Boolean getTomado() {
        return tomado;
    }

    public void setTomado(Boolean tomado) {
        this.tomado = tomado;
    }

    public Collection<FnSolicitudCondonacion> getSolicitudCondonacionCollection() {
        return solicitudCondonacionCollection;
    }

    public void setSolicitudCondonacionCollection(Collection<FnSolicitudCondonacion> solicitudCondonacionCollection) {
        this.solicitudCondonacionCollection = solicitudCondonacionCollection;
    }

    public Collection<RenSecuenciaNumLiquidicacion> getRenNumLiquidacionCollection() {
        return renNumLiquidacionCollection;
    }

    public void setRenNumLiquidacionCollection(Collection<RenSecuenciaNumLiquidicacion> renNumLiquidacionCollection) {
        this.renNumLiquidacionCollection = renNumLiquidacionCollection;
    }

    public Boolean getMostrarTransaccion() {
        return mostrarTransaccion;
    }

    public void setMostrarTransaccion(Boolean mostrarTransaccion) {
        this.mostrarTransaccion = mostrarTransaccion;
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
        if (!(object instanceof RenTipoLiquidacion)) {
            return false;
        }
        RenTipoLiquidacion other = (RenTipoLiquidacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenTipoTransaccion[ id=" + id + " ]";
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public Long getCodigoTituloReporte() {
        return codigoTituloReporte;
    }

    public void setCodigoTituloReporte(Long codigoTituloReporte) {
        this.codigoTituloReporte = codigoTituloReporte;
    }

    public Boolean getPermiteAnulacion() {
        return permiteAnulacion;
    }

    public void setPermiteAnulacion(Boolean permiteAnulacion) {
        this.permiteAnulacion = permiteAnulacion;
    }

    public Boolean getPermiteExoneracion() {
        return permiteExoneracion;
    }

    public void setPermiteExoneracion(Boolean permiteExoneracion) {
        this.permiteExoneracion = permiteExoneracion;
    }
    
}
