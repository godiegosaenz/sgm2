/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.avaluos;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.CatPredio;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "sector_valorizacion", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sector"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SectorValorizacion.findAll", query = "SELECT s FROM SectorValorizacion s")
    , @NamedQuery(name = "SectorValorizacion.findById", query = "SELECT s FROM SectorValorizacion s WHERE s.id = :id")
    , @NamedQuery(name = "SectorValorizacion.findBySector", query = "SELECT s FROM SectorValorizacion s WHERE s.sector = :sector")
    , @NamedQuery(name = "SectorValorizacion.findByDetalle", query = "SELECT s FROM SectorValorizacion s WHERE s.detalle = :detalle")
    , @NamedQuery(name = "SectorValorizacion.findByValorM2", query = "SELECT s FROM SectorValorizacion s WHERE s.valorM2 = :valorM2")
    , @NamedQuery(name = "SectorValorizacion.findByFecCre", query = "SELECT s FROM SectorValorizacion s WHERE s.fecCre = :fecCre")
    , @NamedQuery(name = "SectorValorizacion.findByFecAct", query = "SELECT s FROM SectorValorizacion s WHERE s.fecAct = :fecAct")
    , @NamedQuery(name = "SectorValorizacion.findByUsrCre", query = "SELECT s FROM SectorValorizacion s WHERE s.usrCre = :usrCre")
    , @NamedQuery(name = "SectorValorizacion.findByUsrAct", query = "SELECT s FROM SectorValorizacion s WHERE s.usrAct = :usrAct")
    , @NamedQuery(name = "SectorValorizacion.findByEstado", query = "SELECT s FROM SectorValorizacion s WHERE s.estado = :estado")})
public class SectorValorizacion implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Column(name = "uso", length = 500)
    @Expose
    private String uso;
    @Column(name = "servicios")
    @Expose
    private Integer servicios;
    @Size(max = 500)
    @Column(name = "detalle", length = 500)
    @Expose
    private String detalle;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_m2", precision = 10, scale = 4)
    @Expose
    private BigDecimal valorM2;
    @Column(name = "fact_act", precision = 14, scale = 4)
    @Expose
    private BigDecimal facAct;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Size(max = 100)
    @Column(name = "usr_cre", length = 100)
    private String usrCre;
    @Size(max = 100)
    @Column(name = "usr_act", length = 100)
    private String usrAct;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "sector")
    @Expose
    private BigInteger sector;
    @Column(name = "cobro_bomberos")
    @Expose
    private Boolean cobroBomberos;
    @OneToMany(mappedBy = "subsector", fetch = FetchType.LAZY)
    private Collection<CatPredio> subsectorCollection;

    public SectorValorizacion() {
    }

    public SectorValorizacion(Long id) {
        this.id = id;
    }

    public SectorValorizacion(Long id, BigInteger sector) {
        this.id = id;
        this.sector = sector;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public BigDecimal getValorM2() {
        return valorM2;
    }

    public void setValorM2(BigDecimal valorM2) {
        this.valorM2 = valorM2;
    }

    public BigDecimal getFacAct() {
        return facAct;
    }

    public void setFacAct(BigDecimal facAct) {
        this.facAct = facAct;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public String getUsrAct() {
        return usrAct;
    }

    public void setUsrAct(String usrAct) {
        this.usrAct = usrAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Collection<CatPredio> getSubsectorCollection() {
        return subsectorCollection;
    }

    public void setSubsectorCollection(Collection<CatPredio> subsectorCollection) {
        this.subsectorCollection = subsectorCollection;
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
        if (!(object instanceof SectorValorizacion)) {
            return false;
        }
        SectorValorizacion other = (SectorValorizacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.avaluos.SectorValorizacion[ id=" + id + " ]";
    }

    public BigInteger getSector() {
        return sector;
    }

    public void setSector(BigInteger sector) {
        this.sector = sector;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public Integer getServicios() {
        return servicios;
    }

    public void setServicios(Integer servicios) {
        this.servicios = servicios;
    }

    public Boolean  getCobroBomberos() {
        return cobroBomberos;
    }

    public void setCobroBomberos(Boolean  cobroBomberos) {
        this.cobroBomberos = cobroBomberos;
    }

    
}
