/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Where;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "coa_juicio", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "CoaJuicio.findAll", query = "SELECT c FROM CoaJuicio c")})
public class CoaJuicio implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "numero_juicio")
    private Integer numeroJuicio;
    @Column(name = "anio_juicio")
    private Integer anioJuicio;
    @Column(name = "fecha_juicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaJuicio;
    
    @JoinColumn(name = "abogado_juicio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CoaAbogado abogadoJuicio;
    @JoinColumn(name = "estado_juicio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CoaEstadoJuicio estadoJuicio;
    
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "usuario_ingreso", length = 100)
    private String usuarioIngreso;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @Column(name = "usuario_edicion", length = 100)
    private String usuarioEdicion;
    @Column(name = "estado")
    private Boolean estado=Boolean.TRUE;
    @Column(name = "observacion")
    private String observacion;
    @OneToMany(mappedBy = "juicio", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<CoaJuicioPredio> coaJuicioPredioCollection;
    @Column(name = "total_deuda")
    private BigDecimal totalDeuda;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @OneToOne(fetch = FetchType.LAZY)
    private HistoricoTramites tramite;
    
    @JoinColumn(name = "usuario_asignado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser usuarioAsignado;
    

    public CoaJuicio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroJuicio() {
        return numeroJuicio;
    }

    public void setNumeroJuicio(Integer numeroJuicio) {
        this.numeroJuicio = numeroJuicio;
    }

    public Integer getAnioJuicio() {
        return anioJuicio;
    }

    public void setAnioJuicio(Integer anioJuicio) {
        this.anioJuicio = anioJuicio;
    }

    public Date getFechaJuicio() {
        return fechaJuicio;
    }

    public void setFechaJuicio(Date fechaJuicio) {
        this.fechaJuicio = fechaJuicio;
    }

    public CoaAbogado getAbogadoJuicio() {
        return abogadoJuicio;
    }

    public void setAbogadoJuicio(CoaAbogado abogadoJuicio) {
        this.abogadoJuicio = abogadoJuicio;
    }

    public CoaEstadoJuicio getEstadoJuicio() {
        return estadoJuicio;
    }

    public void setEstadoJuicio(CoaEstadoJuicio estadoJuicio) {
        this.estadoJuicio = estadoJuicio;
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

    public Collection<CoaJuicioPredio> getCoaJuicioPredioCollection() {
        return coaJuicioPredioCollection;
    }

    public void setCoaJuicioPredioCollection(Collection<CoaJuicioPredio> coaJuicioPredioCollection) {
        this.coaJuicioPredioCollection = coaJuicioPredioCollection;
    }

    public BigDecimal getTotalDeuda() {
        return totalDeuda;
    }

    public void setTotalDeuda(BigDecimal totalDeuda) {
        this.totalDeuda = totalDeuda;
    }

    public HistoricoTramites getTramite() {
        return tramite;
    }

    public void setTramite(HistoricoTramites tramite) {
        this.tramite = tramite;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getUsuarioEdicion() {
        return usuarioEdicion;
    }

    public void setUsuarioEdicion(String usuarioEdicion) {
        this.usuarioEdicion = usuarioEdicion;
    }

    public AclUser getUsuarioAsignado() {
        return usuarioAsignado;
    }

    public void setUsuarioAsignado(AclUser usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
        if (!(object instanceof CoaJuicio)) {
            return false;
        }
        CoaJuicio other = (CoaJuicio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CoaJuicio[ id=" + id + " ]";
    }
}
