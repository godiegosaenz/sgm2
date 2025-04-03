/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.CatEnte;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Angel Navarro
 * @date 14/09/2016
 */
@Entity
@Table(name = "ren_turismo", schema = SchemasConfig.FINANCIERO, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"permiso_funcionamiento"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenTurismo.findAll", query = "SELECT r FROM RenTurismo r"),
    @NamedQuery(name = "RenTurismo.findById", query = "SELECT r FROM RenTurismo r WHERE r.id = :id"),
    @NamedQuery(name = "RenTurismo.findByFecha", query = "SELECT r FROM RenTurismo r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "RenTurismo.findByRepesentanteLegal", query = "SELECT r FROM RenTurismo r WHERE r.repesentanteLegal = :repesentanteLegal"),
    @NamedQuery(name = "RenTurismo.findByPropietario", query = "SELECT r FROM RenTurismo r WHERE r.propietario = :propietario"),
    @NamedQuery(name = "RenTurismo.findByEsMatriz", query = "SELECT r FROM RenTurismo r WHERE r.esMatriz = :esMatriz"),
    @NamedQuery(name = "RenTurismo.findByEsPropio", query = "SELECT r FROM RenTurismo r WHERE r.esPropio = :esPropio"),
    @NamedQuery(name = "RenTurismo.findByClasificacionServicio", query = "SELECT r FROM RenTurismo r WHERE r.clasificacionServicio = :clasificacionServicio"),
    @NamedQuery(name = "RenTurismo.findByTieneLicencia", query = "SELECT r FROM RenTurismo r WHERE r.tieneLicencia = :tieneLicencia"),
    @NamedQuery(name = "RenTurismo.findByNumMesas", query = "SELECT r FROM RenTurismo r WHERE r.numMesas = :numMesas"),
    @NamedQuery(name = "RenTurismo.findByNumSillas", query = "SELECT r FROM RenTurismo r WHERE r.numSillas = :numSillas"),
    @NamedQuery(name = "RenTurismo.findByNumBanios", query = "SELECT r FROM RenTurismo r WHERE r.numBanios = :numBanios"),
    @NamedQuery(name = "RenTurismo.findByNumVehiculos", query = "SELECT r FROM RenTurismo r WHERE r.numVehiculos = :numVehiculos"),
    @NamedQuery(name = "RenTurismo.findByNumPlazas", query = "SELECT r FROM RenTurismo r WHERE r.numPlazas = :numPlazas"),
    @NamedQuery(name = "RenTurismo.findByFechaIngreso", query = "SELECT r FROM RenTurismo r WHERE r.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "RenTurismo.findByUsuarioIngreso", query = "SELECT r FROM RenTurismo r WHERE r.usuarioIngreso = :usuarioIngreso")})
public class RenTurismo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "repesentante_legal", referencedColumnName = "id")
    @ManyToOne
    private CatEnte repesentanteLegal;
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @ManyToOne
    private CatEnte propietario;
    @Column(name = "es_matriz")
    private Boolean esMatriz;
    @Column(name = "es_propio")
    private Boolean esPropio;
    @Size(max = 4000)
    @Column(name = "clasificacion_servicio", length = 4000)
    private String clasificacionServicio;
    @Column(name = "tiene_licencia")
    private Boolean tieneLicencia = false;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "num_mesas")
    private Integer numMesas;
    @Column(name = "num_sillas")
    private Integer numSillas;
    @Column(name = "num_banios")
    private Integer numBanios;
    @Column(name = "num_vehiculos")
    private Integer numVehiculos;
    @Column(name = "num_plazas")
    private Integer numPlazas;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Size(max = 25)
    @Column(name = "usuario_ingreso", length = 25)
    private String usuarioIngreso;
    @OneToMany(mappedBy = "turismo")
    private Collection<RenTurismoDetalleHoteles> renTurismoDetalleHotelesCollection;
    @JoinColumn(name = "transporte_terrestre", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios transporteTerrestre;
    @JoinColumn(name = "servcios", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios servcios;
    @JoinColumn(name = "especialidad_servicios_alimenticios", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios especialidadServiciosAlimenticios;
    @JoinColumn(name = "cruceros_turisticos", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios crucerosTuristicos;
    @JoinColumn(name = "transporte_maritimo", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios transporteMaritimo;
    @JoinColumn(name = "transporte_aereo", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios transporteAereo;
    @JoinColumn(name = "permiso_funcionamiento", referencedColumnName = "id")
    @OneToOne
    private RenPermisosFuncionamientoLocalComercial permisoFuncionamiento;

    public RenTurismo() {
    }

    public RenTurismo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CatEnte getRepesentanteLegal() {
        return repesentanteLegal;
    }

    public void setRepesentanteLegal(CatEnte repesentanteLegal) {
        this.repesentanteLegal = repesentanteLegal;
    }

    public CatEnte getPropietario() {
        return propietario;
    }

    public void setPropietario(CatEnte propietario) {
        this.propietario = propietario;
    }

    public Boolean getEsMatriz() {
        return esMatriz;
    }

    public void setEsMatriz(Boolean esMatriz) {
        this.esMatriz = esMatriz;
    }

    public Boolean getEsPropio() {
        return esPropio;
    }

    public void setEsPropio(Boolean esPropio) {
        this.esPropio = esPropio;
    }

    public String getClasificacionServicio() {
        return clasificacionServicio;
    }

    public void setClasificacionServicio(String clasificacionServicio) {
        this.clasificacionServicio = clasificacionServicio;
    }

    public Boolean getTieneLicencia() {
        return tieneLicencia;
    }

    public void setTieneLicencia(Boolean tieneLicencia) {
        this.tieneLicencia = tieneLicencia;
    }

    public Integer getNumMesas() {
        return numMesas;
    }

    public void setNumMesas(Integer numMesas) {
        this.numMesas = numMesas;
    }

    public Integer getNumSillas() {
        return numSillas;
    }

    public void setNumSillas(Integer numSillas) {
        this.numSillas = numSillas;
    }

    public Integer getNumBanios() {
        return numBanios;
    }

    public void setNumBanios(Integer numBanios) {
        this.numBanios = numBanios;
    }

    public Integer getNumVehiculos() {
        return numVehiculos;
    }

    public void setNumVehiculos(Integer numVehiculos) {
        this.numVehiculos = numVehiculos;
    }

    public Integer getNumPlazas() {
        return numPlazas;
    }

    public void setNumPlazas(Integer numPlazas) {
        this.numPlazas = numPlazas;
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

    @XmlTransient
    @JsonIgnore
    public Collection<RenTurismoDetalleHoteles> getRenTurismoDetalleHotelesCollection() {
        return renTurismoDetalleHotelesCollection;
    }

    public void setRenTurismoDetalleHotelesCollection(Collection<RenTurismoDetalleHoteles> renTurismoDetalleHotelesCollection) {
        this.renTurismoDetalleHotelesCollection = renTurismoDetalleHotelesCollection;
    }

    public RenTurismoServicios getTransporteTerrestre() {
        return transporteTerrestre;
    }

    public void setTransporteTerrestre(RenTurismoServicios transporteTerrestre) {
        this.transporteTerrestre = transporteTerrestre;
    }

    public RenTurismoServicios getServcios() {
        return servcios;
    }

    public void setServcios(RenTurismoServicios servcios) {
        this.servcios = servcios;
    }

    public RenTurismoServicios getEspecialidadServiciosAlimenticios() {
        return especialidadServiciosAlimenticios;
    }

    public void setEspecialidadServiciosAlimenticios(RenTurismoServicios especialidadServiciosAlimenticios) {
        this.especialidadServiciosAlimenticios = especialidadServiciosAlimenticios;
    }

    public RenTurismoServicios getCrucerosTuristicos() {
        return crucerosTuristicos;
    }

    public void setCrucerosTuristicos(RenTurismoServicios crucerosTuristicos) {
        this.crucerosTuristicos = crucerosTuristicos;
    }

    public RenTurismoServicios getTransporteMaritimo() {
        return transporteMaritimo;
    }

    public void setTransporteMaritimo(RenTurismoServicios transporteMaritimo) {
        this.transporteMaritimo = transporteMaritimo;
    }

    public RenTurismoServicios getTransporteAereo() {
        return transporteAereo;
    }

    public void setTransporteAereo(RenTurismoServicios transporteAereo) {
        this.transporteAereo = transporteAereo;
    }

    public RenPermisosFuncionamientoLocalComercial getPermisoFuncionamiento() {
        return permisoFuncionamiento;
    }

    public void setPermisoFuncionamiento(RenPermisosFuncionamientoLocalComercial permisoFuncionamiento) {
        this.permisoFuncionamiento = permisoFuncionamiento;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
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
        if (!(object instanceof RenTurismo)) {
            return false;
        }
        RenTurismo other = (RenTurismo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenTurismo[ id=" + id + " ]";
    }

}
