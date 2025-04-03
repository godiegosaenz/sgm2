/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "orden_det", schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrdenDet.findAll", query = "SELECT o FROM OrdenDet o")
    ,
    @NamedQuery(name = "OrdenDet.findById", query = "SELECT o FROM OrdenDet o WHERE o.id = :id")
    ,
    @NamedQuery(name = "OrdenDet.findByPredio", query = "SELECT o FROM OrdenDet o WHERE o.predio = :predio")
    ,
    @NamedQuery(name = "OrdenDet.findByFecha", query = "SELECT o FROM OrdenDet o WHERE o.fecha = :fecha")
    ,
    @NamedQuery(name = "OrdenDet.findByObservaciones", query = "SELECT o FROM OrdenDet o WHERE o.observaciones = :observaciones")
    ,
    @NamedQuery(name = "OrdenDet.findByEstadoDet", query = "SELECT o FROM OrdenDet o WHERE o.estadoDet = :estadoDet")
    ,
    @NamedQuery(name = "OrdenDet.findByFecCre", query = "SELECT o FROM OrdenDet o WHERE o.fecCre = :fecCre")
    ,
    @NamedQuery(name = "OrdenDet.findByUsrAct", query = "SELECT o FROM OrdenDet o WHERE o.usrAct = :usrAct")
    ,
    @NamedQuery(name = "OrdenDet.findByFecAct", query = "SELECT o FROM OrdenDet o WHERE o.fecAct = :fecAct")
    ,
    @NamedQuery(name = "OrdenDet.findByEstado", query = "SELECT o FROM OrdenDet o WHERE o.estado = :estado")})
@SequenceGenerator(name = "det_orden_seq", sequenceName = SchemasConfig.FLOW+".det_orden_seq", allocationSize = 1, schema = SchemasConfig.FLOW)
public class OrdenDet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "det_orden_seq")
    @Expose
    private Long id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    @Expose
    private Date fecha;
    @Column(name = "observaciones")
    @Expose
    private String observaciones;
    @Size(max = 1)
    @Column(name = "estado_det", length = 1)
    @Expose
    private String estadoDet;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    private Date fecCre;
    @Size(max = 100)
    @Column(name = "usr_act", length = 100)
    private String usrAct;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "nuevo")
    private Boolean nuevo;
    @Column(name = "num_predio")
    @Expose
    private long numPredio;
    @Size(max = 2147483647)
    @Column(name = "dato_ref", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    @Expose
    private String datoRef;
    @Column(name = "dato_act", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    @Expose
    private String datoAct;
    @Column(name = "dato_fin", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String datoFin;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "orden", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private OrdenTrabajo orden;
    @Column(name = "uuid", length = 100)
    @Expose
    private String uuid;
    @OneToMany(mappedBy = "predio")
    private List<FotoPredio> fotosPredio;
    @Column(name = "problemas")
    private Boolean problemas;
    @Column(name = "tipo_predio")
    private String tipoPredio;
    @JoinColumn(name = "informante", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte informante;

    public OrdenDet() {
    }

    public OrdenDet(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstadoDet() {
        return estadoDet;
    }

    public void setEstadoDet(String estadoDet) {
        this.estadoDet = estadoDet;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getUsrAct() {
        return usrAct;
    }

    public void setUsrAct(String usrAct) {
        this.usrAct = usrAct;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public OrdenTrabajo getOrden() {
        return orden;
    }

    public void setOrden(OrdenTrabajo orden) {
        this.orden = orden;
    }

    public long getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(long numPredio) {
        this.numPredio = numPredio;
    }

    public String getDatoRef() {
        return datoRef;
    }

    public void setDatoRef(String datoRef) {
        this.datoRef = datoRef;
    }

    public String getDatoAct() {
        return datoAct;
    }

    public void setDatoAct(String datoAct) {
        this.datoAct = datoAct;
    }

    public String getDatoFin() {
        return datoFin;
    }

    public void setDatoFin(String datoFin) {
        this.datoFin = datoFin;
    }

    public Boolean getNuevo() {
        return nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        this.nuevo = nuevo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<FotoPredio> getFotosPredio() {
        return fotosPredio;
    }

    public void setFotosPredio(List<FotoPredio> fotosPredio) {
        this.fotosPredio = fotosPredio;
    }

    public Boolean getProblemas() {
        return problemas;
    }

    public void setProblemas(Boolean problemas) {
        this.problemas = problemas;
    }

    public String getTipoPredio() {
        return tipoPredio;
    }

    public void setTipoPredio(String tipoPredio) {
        this.tipoPredio = tipoPredio;
    }

    public CatEnte getInformante() {
        return informante;
    }

    public void setInformante(CatEnte informante) {
        this.informante = informante;
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
        if (!(object instanceof OrdenDet)) {
            return false;
        }
        OrdenDet other = (OrdenDet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + id + "";
    }

}
