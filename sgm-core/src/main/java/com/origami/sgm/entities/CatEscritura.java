/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.FilterDef;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_escritura", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatEscritura.findByPredio", query = "SELECT e FROM CatEscritura e INNER JOIN e.predio p WHERE p.id = :idPredio ORDER BY e.fecCre DESC ")
})
@FilterDef(name = "activosString",
        defaultCondition = "estado = 'A'")
public class CatEscritura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id_escritura", nullable = false)
    @Expose
    private Long idEscritura;
    @Column(name = "secuencia")
    @Expose
    private BigInteger secuencia;
    @Column(name = "num_ficha")
    @Expose
    private BigInteger numFicha;
    @Column(name = "notaria")
    @Expose
    private String notaria;
    @Column(name = "fec_escritura")
    @Temporal(TemporalType.DATE)
    @Expose
    private Date fecEscritura;
    @Column(name = "num_registro")
    @Expose
    private BigInteger numRegistro;
    @Column(name = "num_repertorio")
    @Expose
    private Integer numRepertorio;
    @Size(max = 20)
    @Column(name = "folio_desde", length = 20)
    @Expose
    private String folioDesde;
    @Size(max = 20)
    @Column(name = "folio_hasta", length = 20)
    @Expose
    private String folioHasta;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_solar", precision = 14, scale = 4)
    @Expose
    private BigDecimal areaSolar;
    @Column(name = "area_construccion", precision = 14, scale = 4)
    @Expose
    private BigDecimal areaConstruccion;
    @Column(name = "alicuota", precision = 12, scale = 3)
    @Expose
    private BigDecimal alicuota;

    @Column(name = "fec_inscripcion")
    @Temporal(TemporalType.DATE)
    @Expose
    private Date fecInscripcion;
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    @Expose
    private String estado;
    @Size(max = 20)
    @Column(name = "num_tramite", length = 20)
    private String numTramite;
    @Column(name = "anio")
    @Expose
    private Long anio;
    @Size(max = 255)
    @Column(name = "task_id", length = 255)
    private String taskId;
    @JoinColumn(name = "trasl_dom", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatTiposDominio traslDom;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "canton", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatCanton canton;


    @Column(name = "unidad_area")
    @Expose
    private BigInteger unidadArea;
    @JoinColumn(name = "tipo_protocolizacion", referencedColumnName = "id")
    @Expose
    @ManyToOne(fetch = FetchType.LAZY)
    private CtlgItem tipoProtocolizacion;
    @Column(name = "precio_compra")
    @Expose
    private BigDecimal precioCompra;
    @JoinColumn(name = "propietario", referencedColumnName = "id")
    @Expose
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredioPropietario propietario;
    @Column(name = "fec_cre")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Expose
    private Date fecCre;

    @Column(name = "tipo_ph")
    @Expose
    private Integer tipoPh;
    @Column(name = "resolucion")
    @Expose
    @Size(max = 255)
    private String resolucion;
    @Column(name = "fecha_resolucion")
    @Expose
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaResolucion;
    @Column(name = "cant_bloques")
    @Expose
    private Integer cantBloques;
 //   @GreaterThan
    @Column(name = "cant_alicuotas")
    @Expose
    private Integer cantAlicuotas;
//    @Expose
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "escritura")
    private List<CatEscrituraPropietario> catEscrituraPropietarioList;

    public CatEscritura() {

    }

    public CatEscritura(Long idEscritura) {
        this.idEscritura = idEscritura;
    }

    public Long getIdEscritura() {
        return idEscritura;
    }

    public void setIdEscritura(Long idEscritura) {
        this.idEscritura = idEscritura;
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public BigInteger getNumFicha() {
        return numFicha;
    }

    public void setNumFicha(BigInteger numFicha) {
        this.numFicha = numFicha;
    }

    public String getNotaria() {
        return notaria;
    }

    public void setNotaria(String notaria) {
        this.notaria = notaria;
    }

    public Date getFecEscritura() {
        return fecEscritura;
    }

    public void setFecEscritura(Date fecEscritura) {
        this.fecEscritura = fecEscritura;
    }

    public BigInteger getNumRegistro() {
        return numRegistro;
    }

    public void setNumRegistro(BigInteger numRegistro) {
        this.numRegistro = numRegistro;
    }

    public Integer getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(Integer numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public String getFolioDesde() {
        return folioDesde;
    }

    public void setFolioDesde(String folioDesde) {
        this.folioDesde = folioDesde;
    }

    public String getFolioHasta() {
        return folioHasta;
    }

    public void setFolioHasta(String folioHasta) {
        this.folioHasta = folioHasta;
    }

    public BigDecimal getAreaSolar() {
        return areaSolar;
    }

    public void setAreaSolar(BigDecimal areaSolar) {
        this.areaSolar = areaSolar;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getAlicuota() {
        return alicuota;
    }

    public void setAlicuota(BigDecimal alicuota) {
        this.alicuota = alicuota;
    }

    public Date getFecInscripcion() {
        return fecInscripcion;
    }

    public void setFecInscripcion(Date fecInscripcion) {
        this.fecInscripcion = fecInscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(String numTramite) {
        this.numTramite = numTramite;
    }

    public Long getAnio() {
        return anio;
    }

    public void setAnio(Long anio) {
        this.anio = anio;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public CatTiposDominio getTraslDom() {
        return traslDom;
    }

    public void setTraslDom(CatTiposDominio traslDom) {
        this.traslDom = traslDom;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public CtlgItem getTipoProtocolizacion() {
        return tipoProtocolizacion;
    }

    public void setTipoProtocolizacion(CtlgItem tipoProtocolizacion) {
        this.tipoProtocolizacion = tipoProtocolizacion;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public CatPredioPropietario getPropietario() {
        return propietario;
    }

    public void setPropietario(CatPredioPropietario propietario) {
        this.propietario = propietario;
    }

    public BigInteger getUnidadArea() {
        return unidadArea;
    }

    public void setUnidadArea(BigInteger unidadArea) {
        this.unidadArea = unidadArea;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Integer getTipoPh() {
        return tipoPh;
    }

    public void setTipoPh(Integer tipoPh) {
        this.tipoPh = tipoPh;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public Date getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public Integer getCantBloques() {
        return cantBloques;
    }

    public void setCantBloques(Integer cantBloques) {
        this.cantBloques = cantBloques;
    }

    public Integer getCantAlicuotas() {
        return cantAlicuotas;
    }

    public void setCantAlicuotas(Integer cantAlicuotas) {
        this.cantAlicuotas = cantAlicuotas;
    }


    public List<CatEscrituraPropietario> getCatEscrituraPropietarioList() {
        return catEscrituraPropietarioList;
    }

    public void setCatEscrituraPropietarioList(List<CatEscrituraPropietario> catEscrituraPropietarioList) {
        this.catEscrituraPropietarioList = catEscrituraPropietarioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEscritura != null ? idEscritura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CatEscritura)) {
            return false;
        }
        CatEscritura other = (CatEscritura) object;
        if ((this.idEscritura == null && other.idEscritura != null) || (this.idEscritura != null && !this.idEscritura.equals(other.idEscritura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatEscritura[ idEscritura=" + idEscritura + " ]";
    }

}
