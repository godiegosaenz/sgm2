/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.GeDepartamento;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "sv_solicitud_departamento",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SvSolicitudDepartamento.findAll", query = "SELECT s FROM SvSolicitudDepartamento s"),
    @NamedQuery(name = "SvSolicitudDepartamento.findById", query = "SELECT s FROM SvSolicitudDepartamento s WHERE s.id = :id"),
    @NamedQuery(name = "SvSolicitudDepartamento.findByDepartamento", query = "SELECT s FROM SvSolicitudDepartamento s WHERE s.departamento = :departamento")})
public class SvSolicitudDepartamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    /*comunicado text,
  informe text,
  archivo boolean DEFAULT true,
  accion bigint,*/
    @Column(name = "comunicado")
    private String comunicado;
    @Column(name = "informe")
    private String informe;
    @Column(name = "archivo")
    private Boolean archivo = true;
    @Column(name = "accion")
    private Long accion;
    @Column(name = "validar")
    private Boolean validar = true;
    @JoinColumn(name = "departamento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeDepartamento departamento;
    @JoinColumn(name = "solicitud", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SvSolicitudServicios solicitud;
    @Column(name = "estado")
    private Boolean estado = true;
    @JoinColumn(name = "director", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser director;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AclUser responsable;
    @Column(name = "padre")
    private Long padre;
    @Column(name = "fecha_finalizado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinalizado;
    @Column(name = "fecha_accion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAccion;
    @Column(name = "informe_departamento")
    private String informeDepartamento;

    public SvSolicitudDepartamento() {
    }

    public SvSolicitudDepartamento(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComunicado() {
        return comunicado;
    }

    public void setComunicado(String comunicado) {
        this.comunicado = comunicado;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public Boolean getArchivo() {
        return archivo;
    }

    public void setArchivo(Boolean archivo) {
        this.archivo = archivo;
    }

    public Long getAccion() {
        return accion;
    }

    public void setAccion(Long accion) {
        this.accion = accion;
    }

    public Boolean getValidar() {
        return validar;
    }

    public void setValidar(Boolean validar) {
        this.validar = validar;
    }
    
    public GeDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(GeDepartamento departamento) {
        this.departamento = departamento;
    }

    public SvSolicitudServicios getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(SvSolicitudServicios solicitud) {
        this.solicitud = solicitud;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public AclUser getDirector() {
        return director;
    }

    public void setDirector(AclUser director) {
        this.director = director;
    }

    public AclUser getResponsable() {
        return responsable;
    }

    public void setResponsable(AclUser responsable) {
        this.responsable = responsable;
    }

    public Long getPadre() {
        return padre;
    }

    public void setPadre(Long padre) {
        this.padre = padre;
    }    

    public Date getFechaFinalizado() {
        return fechaFinalizado;
    }

    public void setFechaFinalizado(Date fechaFinalizado) {
        this.fechaFinalizado = fechaFinalizado;
    }

    public Date getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(Date fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public String getInformeDepartamento() {
        return informeDepartamento;
    }

    public void setInformeDepartamento(String informeDepartamento) {
        this.informeDepartamento = informeDepartamento;
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
        if (!(object instanceof SvSolicitudDepartamento)) {
            return false;
        }
        SvSolicitudDepartamento other = (SvSolicitudDepartamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.SvSolicitudDepartamento[ id=" + id + " ]";
    }
    
}
