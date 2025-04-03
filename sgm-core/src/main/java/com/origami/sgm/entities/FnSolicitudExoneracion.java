/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import com.origami.sgm.entities.FnExoneracionTipo;
import com.origami.sgm.entities.FnExoneracionLiquidacion;
import com.origami.sgm.entities.FnEstadoExoneracion;
import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatEnte;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Where;

/**
 *
 * @author Angel Navarro
 * @Date 17/05/2016
 */
@Entity
@Table(name = "fn_solicitud_exoneracion", schema = SchemasConfig.FINANCIERO)
@SequenceGenerator(name = "fn_solexon_seq", sequenceName = SchemasConfig.FINANCIERO + ".fn_solexon_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FnSolicitudExoneracion.findAll", query = "SELECT f FROM FnSolicitudExoneracion f"),
    @NamedQuery(name = "FnSolicitudExoneracion.findById", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.id = :id"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByTramite", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.tramite = :tramite"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByPredio", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.predio = :predio"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByAnioInicio", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.anioInicio = :anioInicio"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByAnioFin", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.anioFin = :anioFin"),
    @NamedQuery(name = "FnSolicitudExoneracion.findBySemestreInicio", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.semestreInicio = :semestreInicio"),
    @NamedQuery(name = "FnSolicitudExoneracion.findBySemestreFin", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.semestreFin = :semestreFin"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByValor", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.valor = :valor"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByUsuarioCreacion", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.usuarioCreacion = :usuarioCreacion"),
    @NamedQuery(name = "FnSolicitudExoneracion.findByFechaIngreso", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.fechaIngreso = :fechaIngreso"),
    @NamedQuery(name = "FnSolicitudExoneracion.findBySolicitante", query = "SELECT f FROM FnSolicitudExoneracion f WHERE f.solicitante = :solicitante"),
    @NamedQuery(name = "FnSolicitudExoneracion.findAllds", query = "SELECT s FROM FnSolicitudExoneracion s LEFT JOIN s.tramite ht LEFT JOIN ht.historicoTramiteDetCollection htd WHERE htd.predio = :predio AND s.exoneracionTipo = :exoneracionTipo AND s.anioFin = :anioFin"),
    @NamedQuery(name = "FnSolicitudExoneracion.findSolicitudRust", query = "SELECT s FROM FnSolicitudExoneracion s LEFT JOIN s.tramite ht LEFT JOIN ht.historicoTramiteDetCollection htd WHERE htd.predioRustico = :predioRustico AND s.exoneracionTipo = :exoneracionTipo AND s.estado.id = :estado AND s.anioFin BETWEEN :anioInicio AND :anioFin AND s.anioInicio BETWEEN :anioInicio AND :anioFin")})
public class FnSolicitudExoneracion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fn_solexon_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "tramite", referencedColumnName = "id_tramite")
    @ManyToOne
    private HistoricoTramites tramite;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "predio_rustico", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredioRustico predioRustico;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @Column(name = "semestre_inicio")
    private Integer semestreInicio;
    @Column(name = "semestre_fin")
    private Integer semestreFin;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 19, scale = 2)
    private BigDecimal valor;
    @Column(name = "valor_ip", precision = 10, scale = 2)
    private BigDecimal valorIP;
    @Size(max = 25)
    @Column(name = "usuario_creacion", length = 25)
    private String usuarioCreacion;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    @Column(name = "fecha_aprobacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAprobacion;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne
    private CatEnte solicitante;
    @JoinColumn(name = "tipo_valor", referencedColumnName = "id")
    @ManyToOne
    private RenTipoValor tipoValor;
    @JoinColumn(name = "resolucion", referencedColumnName = "id")
    @ManyToOne
    private FnResolucion resolucion;
    @JoinColumn(name = "exoneracion_tipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FnExoneracionTipo exoneracionTipo;
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private FnEstadoExoneracion estado;
    @Column(name = "solicitud_grupal")
    private Boolean solicitudGrupal;
//    @OneToMany(mappedBy = "solicitudExoneracion")
//    private List<FnResolucion> fnResolucions;
    @OneToMany(mappedBy = "exoneracion")
    private Collection<FnExoneracionLiquidacion> exoneracionLiquidacionCollection;
    
    @OneToMany(mappedBy = "solExoneracion", fetch = FetchType.LAZY)
    @Where(clause = "estado")
    private Collection<RenSolicitudesLiquidacion> renSolicitudesLiquidacionCollection;    
    
    @Column(name = "num_resolucion_sac")
    private String numResolucionSac;
    
    @OneToMany(mappedBy = "solicitudExoneracion", fetch = FetchType.LAZY)
    private Collection<FnSolicitudTipoLiquidacionExoneracion> solicitudExoneracionCollection;
    @OneToMany(mappedBy = "solicitudExoneracion", fetch = FetchType.LAZY)
    private Collection<FnSolicitudExoneracionPredios> solicitudExoneracionCollection1;
    
    public FnSolicitudExoneracion() {
    }

    public FnSolicitudExoneracion(Long id) {
        this.id = id;
    }

    public Collection<FnSolicitudTipoLiquidacionExoneracion> getSolicitudExoneracionCollection() {
        return solicitudExoneracionCollection;
    }

    public void setSolicitudExoneracionCollection(Collection<FnSolicitudTipoLiquidacionExoneracion> solicitudExoneracionCollection) {
        this.solicitudExoneracionCollection = solicitudExoneracionCollection;
    }

    public Collection<FnSolicitudExoneracionPredios> getSolicitudExoneracionCollection1() {
        return solicitudExoneracionCollection1;
    }

    public void setSolicitudExoneracionCollection1(Collection<FnSolicitudExoneracionPredios> solicitudExoneracionCollection1) {
        this.solicitudExoneracionCollection1 = solicitudExoneracionCollection1;
    }

    public FnSolicitudExoneracion(Integer anioInicio, Integer anioFin, Integer semestreInicio, RenTipoValor tipoValor) {
        this.anioInicio = anioInicio;
        this.anioFin = anioFin;
        this.semestreInicio = semestreInicio;
        this.tipoValor = tipoValor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
    }

    public Integer getSemestreInicio() {
        return semestreInicio;
    }

    public void setSemestreInicio(Integer semestreInicio) {
        this.semestreInicio = semestreInicio;
    }

    public Integer getSemestreFin() {
        return semestreFin;
    }

    public void setSemestreFin(Integer semestreFin) {
        this.semestreFin = semestreFin;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public CatEnte getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(CatEnte solicitante) {
        this.solicitante = solicitante;
    }

    public RenTipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(RenTipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }

    public FnResolucion getResolucion() {
        return resolucion;
    }

    public void setResolucion(FnResolucion resolucion) {
        this.resolucion = resolucion;
    }

    public FnExoneracionTipo getExoneracionTipo() {
        return exoneracionTipo;
    }

    public void setExoneracionTipo(FnExoneracionTipo exoneracionTipo) {
        this.exoneracionTipo = exoneracionTipo;
    }

    public FnEstadoExoneracion getEstado() {
        return estado;
    }

    public void setEstado(FnEstadoExoneracion estado) {
        this.estado = estado;
    }

    public Boolean getSolicitudGrupal() {
        return solicitudGrupal;
    }

    public void setSolicitudGrupal(Boolean solicitudGrupal) {
        this.solicitudGrupal = solicitudGrupal;
    }

//    public List<FnResolucion> getFnResolucions() {
//        return fnResolucions;
//    }
//
//    public void setFnResolucions(List<FnResolucion> fnResolucions) {
//        this.fnResolucions = fnResolucions;
//    }

    public Collection<RenSolicitudesLiquidacion> getRenSolicitudesLiquidacionCollection() {
        return renSolicitudesLiquidacionCollection;
    }

    public void setRenSolicitudesLiquidacionCollection(Collection<RenSolicitudesLiquidacion> renSolicitudesLiquidacionCollection) {
        this.renSolicitudesLiquidacionCollection = renSolicitudesLiquidacionCollection;
    }

    public String getNumResolucionSac() {
        return numResolucionSac;
    }

    public void setNumResolucionSac(String numResolucionSac) {
        this.numResolucionSac = numResolucionSac;
    }

    public BigDecimal getValorIP() {
        return valorIP;
    }

    public void setValorIP(BigDecimal valorIP) {
        this.valorIP = valorIP;
    }

    public CatPredioRustico getPredioRustico() {
        return predioRustico;
    }

    public void setPredioRustico(CatPredioRustico predioRustico) {
        this.predioRustico = predioRustico;
    }

    public Date getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(Date fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public Collection<FnExoneracionLiquidacion> getExoneracionLiquidacionCollection() {
        return exoneracionLiquidacionCollection;
    }

    public void setExoneracionLiquidacionCollection(Collection<FnExoneracionLiquidacion> exoneracionLiquidacionCollection) {
        this.exoneracionLiquidacionCollection = exoneracionLiquidacionCollection;
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
        if (!(object instanceof FnSolicitudExoneracion)) {
            return false;
        }
        FnSolicitudExoneracion other = (FnSolicitudExoneracion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnSolicitudExoneracion[ id=" + id + " ]";
    }

}
