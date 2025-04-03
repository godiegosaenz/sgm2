/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CtlgItem;
import com.origami.sgm.entities.CatParroquia;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatCiudadela;
import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatEscrituraRural;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Contiene el datos de la ficha
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_ficha", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cat_escritura_rural"}),
    @UniqueConstraint(columnNames = {"predio"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegFicha.findAll", query = "SELECT r FROM RegFicha r"),
    @NamedQuery(name = "RegFicha.findById", query = "SELECT r FROM RegFicha r WHERE r.id = :id"),
    @NamedQuery(name = "RegFicha.findByNumFicha", query = "SELECT r FROM RegFicha r WHERE r.numFicha = :numFicha"),
    @NamedQuery(name = "RegFicha.findByCodigoPredial", query = "SELECT r FROM RegFicha r WHERE r.codigoPredial = :codigoPredial"),
    @NamedQuery(name = "RegFicha.findByFechaApe", query = "SELECT r FROM RegFicha r WHERE r.fechaApe = :fechaApe"),
    @NamedQuery(name = "RegFicha.findByLinderos", query = "SELECT r FROM RegFicha r WHERE r.linderos = :linderos"),
    @NamedQuery(name = "RegFicha.findByTipoPredio", query = "SELECT r FROM RegFicha r WHERE r.tipoPredio = :tipoPredio"),
    @NamedQuery(name = "RegFicha.findByTipoDep", query = "SELECT r FROM RegFicha r WHERE r.tipoDep = :tipoDep"),
    @NamedQuery(name = "RegFicha.findByStatus", query = "SELECT r FROM RegFicha r WHERE r.status = :status"),
    @NamedQuery(name = "RegFicha.findByFechaMovimiento", query = "SELECT r FROM RegFicha r WHERE r.fechaMovimiento = :fechaMovimiento"),
    @NamedQuery(name = "RegFicha.findByDetFuncionario", query = "SELECT r FROM RegFicha r WHERE r.detFuncionario = :detFuncionario"),
    @NamedQuery(name = "RegFicha.findByLibro", query = "SELECT r FROM RegFicha r WHERE r.libro = :libro"),
    @NamedQuery(name = "RegFicha.findByNumInscripcion", query = "SELECT r FROM RegFicha r WHERE r.numInscripcion = :numInscripcion"),
    @NamedQuery(name = "RegFicha.findByFechaInscripcion", query = "SELECT r FROM RegFicha r WHERE r.fechaInscripcion = :fechaInscripcion"),
    @NamedQuery(name = "RegFicha.findByNumRepertorio", query = "SELECT r FROM RegFicha r WHERE r.numRepertorio = :numRepertorio"),
    @NamedQuery(name = "RegFicha.findByObservacion", query = "SELECT r FROM RegFicha r WHERE r.observacion = :observacion"),
    @NamedQuery(name = "RegFicha.findByFichaMatriz", query = "SELECT r FROM RegFicha r WHERE r.fichaMatriz = :fichaMatriz")})
public class RegFicha implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_ficha", nullable = false)
    private long numFicha;
    @Size(max = 100)
    @Column(name = "codigo_predial", length = 100)
    private String codigoPredial;
    @Column(name = "fecha_ape")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaApe;
    @Size(max = 2147483647)
    @Column(name = "linderos", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String linderos;
    @Size(max = 1)
    @Column(name = "tipo_predio", length = 1)
    private String tipoPredio;
    @Column(name = "tipo_dep")
    private Boolean tipoDep;
    @Size(max = 5)
    @Column(name = "status", length = 5)
    private String status;
    @Column(name = "fecha_movimiento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMovimiento;
    @Size(max = 4000)
    @Column(name = "det_funcionario", length = 4000)
    private String detFuncionario;
    @Column(name = "libro")
    private BigInteger libro;
    @Column(name = "num_inscripcion")
    private BigInteger numInscripcion;
    @Column(name = "fecha_inscripcion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInscripcion;
    @Column(name = "num_repertorio")
    private BigInteger numRepertorio;
    @Size(max = 4000)
    @Column(name = "observacion", length = 4000)
    private String observacion;
    @Column(name = "ficha_matriz")
    private BigInteger fichaMatriz;

    @Size(max = 100)
    @Column(name = "user_edicion", length = 100)
    private String userEdicion;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @Column(name = "area_escritura", precision = 12, scale = 2)
    private BigDecimal areaEscritura;//
    @Column(name = "alicuota_escritura", precision = 12, scale = 3)
    private BigDecimal alicuotaEscritura;
    @Column(name = "propiedad_horizontal")
    private Boolean propiedadHorizontal;
    @OneToMany(mappedBy = "ficha", fetch = FetchType.LAZY)
    private Collection<RegFichaBien> regFichaBienCollection;
    @JoinColumn(name = "tipo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RegTipoFicha tipo;
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem estado;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "parroquia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatParroquia parroquia;
    @JoinColumn(name = "cat_escritura_rural", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private CatEscrituraRural catEscrituraRural;
    @JoinColumn(name = "ente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte ente;
    @JoinColumn(name = "ciudadela", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCiudadela ciudadela;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ficha", fetch = FetchType.LAZY)
    private Collection<RegMovimientoFicha> regMovimientoFichaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ficha", fetch = FetchType.LAZY)
    private Collection<RegFichaReferecia> regFichaRefereciaCollection;

    @OneToMany(mappedBy = "ficha", fetch = FetchType.LAZY)
    private Collection<RegFichaPropietarios> regFichaPropietariosCollection;
    
    @Transient
    private String tipoPredioTemp;
    @Transient
    private String descripcionTemp;

    public String getTipoPredioTemp() {
        return tipoPredioTemp;
    }

    public void setTipoPredioTemp(String tipoPredioTemp) {
        this.tipoPredioTemp = tipoPredioTemp;
    }

    public String getDescripcionTemp() {
        return descripcionTemp;
    }

    public void setDescripcionTemp(String descripcionTemp) {
        this.descripcionTemp = descripcionTemp;
    }

    public RegFicha() {
    }

    public RegFicha(Long id) {
        this.id = id;
    }

    public RegFicha(Long id, long numFicha) {
        this.id = id;
        this.numFicha = numFicha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(long numFicha) {
        this.numFicha = numFicha;
    }

    public String getCodigoPredial() {
        return codigoPredial;
    }

    public void setCodigoPredial(String codigoPredial) {
        this.codigoPredial = codigoPredial;
    }

    public Date getFechaApe() {
        return fechaApe;
    }

    public void setFechaApe(Date fechaApe) {
        this.fechaApe = fechaApe;
    }

    public String getLinderos() {
        return linderos;
    }

    public void setLinderos(String linderos) {
        this.linderos = linderos;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public Boolean getTipoDep() {
        return tipoDep;
    }

    public void setTipoDep(Boolean tipoDep) {
        this.tipoDep = tipoDep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getDetFuncionario() {
        return detFuncionario;
    }

    public void setDetFuncionario(String detFuncionario) {
        this.detFuncionario = detFuncionario;
    }

    public BigInteger getLibro() {
        return libro;
    }

    public void setLibro(BigInteger libro) {
        this.libro = libro;
    }

    public BigInteger getNumInscripcion() {
        return numInscripcion;
    }

    public void setNumInscripcion(BigInteger numInscripcion) {
        this.numInscripcion = numInscripcion;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public BigInteger getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(BigInteger numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigInteger getFichaMatriz() {
        return fichaMatriz;
    }

    public void setFichaMatriz(BigInteger fichaMatriz) {
        this.fichaMatriz = fichaMatriz;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegFichaBien> getRegFichaBienCollection() {
        return regFichaBienCollection;
    }

    public void setRegFichaBienCollection(Collection<RegFichaBien> regFichaBienCollection) {
        this.regFichaBienCollection = regFichaBienCollection;
    }

    public RegTipoFicha getTipo() {
        return tipo;
    }

    public void setTipo(RegTipoFicha tipo) {
        this.tipo = tipo;
    }

    public CtlgItem getEstado() {
        return estado;
    }

    public void setEstado(CtlgItem estado) {
        this.estado = estado;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatParroquia getParroquia() {
        return parroquia;
    }

    public void setParroquia(CatParroquia parroquia) {
        this.parroquia = parroquia;
    }

    public CatEscrituraRural getCatEscrituraRural() {
        return catEscrituraRural;
    }

    public void setCatEscrituraRural(CatEscrituraRural catEscrituraRural) {
        this.catEscrituraRural = catEscrituraRural;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatCiudadela getCiudadela() {
        return ciudadela;
    }

    public void setCiudadela(CatCiudadela ciudadela) {
        this.ciudadela = ciudadela;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimientoFicha> getRegMovimientoFichaCollection() {
        return regMovimientoFichaCollection;
    }

    public void setRegMovimientoFichaCollection(Collection<RegMovimientoFicha> regMovimientoFichaCollection) {
        this.regMovimientoFichaCollection = regMovimientoFichaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegFichaReferecia> getRegFichaRefereciaCollection() {
        return regFichaRefereciaCollection;
    }

    public void setRegFichaRefereciaCollection(Collection<RegFichaReferecia> regFichaRefereciaCollection) {
        this.regFichaRefereciaCollection = regFichaRefereciaCollection;
    }

    public String getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(String userEdicion) {
        this.userEdicion = userEdicion;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public BigDecimal getAreaEscritura() {
        return areaEscritura;
    }

    public void setAreaEscritura(BigDecimal areaEscritura) {
        this.areaEscritura = areaEscritura;
    }

    public BigDecimal getAlicuotaEscritura() {
        return alicuotaEscritura;
    }

    public void setAlicuotaEscritura(BigDecimal alicuotaEscritura) {
        this.alicuotaEscritura = alicuotaEscritura;
    }

    public Boolean getPropiedadHorizontal() {
        return propiedadHorizontal;
    }

    public void setPropiedadHorizontal(Boolean propiedadHorizontal) {
        this.propiedadHorizontal = propiedadHorizontal;
    }

    public Collection<RegFichaPropietarios> getRegFichaPropietariosCollection() {
        return regFichaPropietariosCollection;
    }

    public void setRegFichaPropietariosCollection(Collection<RegFichaPropietarios> regFichaPropietariosCollection) {
        this.regFichaPropietariosCollection = regFichaPropietariosCollection;
    }

    public String getObsvEstado(CtlgItem item) {
        String valor = null;
//        switch (item.getCodename()) {
//            case "activo":
//                valor = "Estado de la Ficha: ACTIVO";
//                break;
//            case "inactivo":
//                valor = "Estado de la Ficha: INACTIVO";
//                break;
//            case "prohibicion":
//                valor = "ESTA FICHA TIENE PROHIBICION YA INSCRITA";
//                break;
//            case "negativa":
//                valor = "ESTA FICHA TIENE NEGATIVA YA INSCRITA";
//                break;
//            case "embargo":
//                valor = "ESTA FICHA TIENE EMBARGO YA INSCRITA";
//                break;
//            case "np":
//                valor = "LA FICHA TIENE NEGATIVA Y PROHIBICION YA INSCRITA";
//                break;
//            default:
//                valor = "";
//                break;
//        }
        return valor;
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
        if (!(object instanceof RegFicha)) {
            return false;
        }
        RegFicha other = (RegFicha) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegFicha[ id=" + id + " ]";
    }

}
