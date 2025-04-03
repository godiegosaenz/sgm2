/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.historic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "comparativo_emision", schema = SchemasConfig.HISTORICO)
@XmlRootElement
public class ComparativoEmision implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "id_predio")
    private BigInteger idPredio;
    @Column(name = "num_predio")
    private BigInteger numPredio;
    @Column(name = "liquidacion")
    private BigInteger liquidacion;
    @Column(name = "periodo")
    private Integer periodo;
    @Column(name = "num_version")
    private Integer numVersion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_total", precision = 14, scale = 4)
    private BigDecimal areaTotal;
    @Column(name = "avaluo_solar", precision = 14, scale = 4)
    private BigDecimal avaluoSolar;
    @Column(name = "avaluo_construccion", precision = 14, scale = 4)
    private BigDecimal avaluoConstruccion;
    @Column(name = "avaluo_municipal", precision = 14, scale = 4)
    private BigDecimal avaluoMunicipal;
    @Column(name = "ip_liq", precision = 14, scale = 4)
    private BigDecimal ipLiq;
    @Column(name = "descuento_liq", precision = 14, scale = 4)
    private BigDecimal descuentoLiq;
    @Column(name = "mejoras_liq", precision = 14, scale = 4)
    private BigDecimal mejorasLiq;
    @Column(name = "sol_nedif_liq", precision = 14, scale = 4)
    private BigDecimal solNedifLiq;
    @Column(name = "emision_liq", precision = 14, scale = 4)
    private BigDecimal emisionLiq;
    @Column(name = "bomberos_liq", precision = 14, scale = 4)
    private BigDecimal bomberosLiq;
    @Column(name = "tasa_mant_liq", precision = 14, scale = 4)
    private BigDecimal tasaMantLiq;
    @Column(name = "area_calc", precision = 14, scale = 4)
    private BigDecimal areaCalc;
    @Column(name = "aval_sol_calc", precision = 14, scale = 4)
    private BigDecimal avalSolCalc;
    @Column(name = "aval_edif_calc", precision = 14, scale = 4)
    private BigDecimal avalEdifCalc;
    @Column(name = "aval_mun_calc", precision = 14, scale = 4)
    private BigDecimal avalMunCalc;
    @Column(name = "ip_calc", precision = 14, scale = 4)
    private BigDecimal ipCalc;
    @Column(name = "emision_calc", precision = 14, scale = 4)
    private BigDecimal emisionCalc;
    @Column(name = "sol_nedif_calc", precision = 14, scale = 4)
    private BigDecimal solNedifCalc;
    @Column(name = "bomberos_calc", precision = 14, scale = 4)
    private BigDecimal bomberosCalc;
    @Column(name = "tasa_mant_calc", precision = 14, scale = 4)
    private BigDecimal tasaMantCalc;
    @Size(max = 50)
    @Column(name = "usr_cre", length = 50)
    private String usrCre;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "fec_pago")
    private Date fecPago;
    @Column(name = "resolucion")
    private String resolucion;
    @Column(name = "contribuyente")
    private String contribuyente;
    @Column(name = "aprob_catastro")
    private Boolean aprobCatastro;
    @Column(name = "aprob_financiero")
    private Boolean aprobFinanciero;
    @Column(name = "aprob_rentas")
    private Boolean aprobRentas;
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "fec_financiero")
    private Date fecFinanciero;
    @Column(name = "fec_rentas")
    private Date fecRentas;
    @Column(name = "firma_catastro")
    private String firmaCatastro;
    @Column(name = "director")
    private String director;

    public ComparativoEmision() {
    }

    public ComparativoEmision(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getIdPredio() {
        return idPredio;
    }

    public void setIdPredio(BigInteger idPredio) {
        this.idPredio = idPredio;
    }

    public BigInteger getNumPredio() {
        return numPredio;
    }

    public void setNumPredio(BigInteger numPredio) {
        this.numPredio = numPredio;
    }

    public BigInteger getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(BigInteger liquidacion) {
        this.liquidacion = liquidacion;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(Integer numVersion) {
        this.numVersion = numVersion;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getAvaluoSolar() {
        return avaluoSolar;
    }

    public void setAvaluoSolar(BigDecimal avaluoSolar) {
        this.avaluoSolar = avaluoSolar;
    }

    public BigDecimal getAvaluoConstruccion() {
        return avaluoConstruccion;
    }

    public void setAvaluoConstruccion(BigDecimal avaluoConstruccion) {
        this.avaluoConstruccion = avaluoConstruccion;
    }

    public BigDecimal getAvaluoMunicipal() {
        return avaluoMunicipal;
    }

    public void setAvaluoMunicipal(BigDecimal avaluoMunicipal) {
        this.avaluoMunicipal = avaluoMunicipal;
    }

    public BigDecimal getIpLiq() {
        return ipLiq;
    }

    public void setIpLiq(BigDecimal ipLiq) {
        this.ipLiq = ipLiq;
    }

    public BigDecimal getDescuentoLiq() {
        return descuentoLiq;
    }

    public void setDescuentoLiq(BigDecimal descuentoLiq) {
        this.descuentoLiq = descuentoLiq;
    }

    public BigDecimal getMejorasLiq() {
        return mejorasLiq;
    }

    public void setMejorasLiq(BigDecimal mejorasLiq) {
        this.mejorasLiq = mejorasLiq;
    }

    public BigDecimal getSolNedifLiq() {
        return solNedifLiq;
    }

    public void setSolNedifLiq(BigDecimal solNedifLiq) {
        this.solNedifLiq = solNedifLiq;
    }

    public BigDecimal getEmisionLiq() {
        return emisionLiq;
    }

    public void setEmisionLiq(BigDecimal emisionLiq) {
        this.emisionLiq = emisionLiq;
    }

    public BigDecimal getBomberosLiq() {
        return bomberosLiq;
    }

    public void setBomberosLiq(BigDecimal bomberosLiq) {
        this.bomberosLiq = bomberosLiq;
    }

    public BigDecimal getTasaMantLiq() {
        return tasaMantLiq;
    }

    public void setTasaMantLiq(BigDecimal tasaMantLiq) {
        this.tasaMantLiq = tasaMantLiq;
    }

    public BigDecimal getAreaCalc() {
        return areaCalc;
    }

    public void setAreaCalc(BigDecimal areaCalc) {
        this.areaCalc = areaCalc;
    }

    public BigDecimal getAvalSolCalc() {
        return avalSolCalc;
    }

    public void setAvalSolCalc(BigDecimal avalSolCalc) {
        this.avalSolCalc = avalSolCalc;
    }

    public BigDecimal getAvalEdifCalc() {
        return avalEdifCalc;
    }

    public void setAvalEdifCalc(BigDecimal avalEdifCalc) {
        this.avalEdifCalc = avalEdifCalc;
    }

    public BigDecimal getAvalMunCalc() {
        return avalMunCalc;
    }

    public void setAvalMunCalc(BigDecimal avalMunCalc) {
        this.avalMunCalc = avalMunCalc;
    }

    public BigDecimal getIpCalc() {
        return ipCalc;
    }

    public void setIpCalc(BigDecimal ipCalc) {
        this.ipCalc = ipCalc;
    }

    public BigDecimal getEmisionCalc() {
        return emisionCalc;
    }

    public void setEmisionCalc(BigDecimal emisionCalc) {
        this.emisionCalc = emisionCalc;
    }

    public BigDecimal getSolNedifCalc() {
        return solNedifCalc;
    }

    public void setSolNedifCalc(BigDecimal solNedifCalc) {
        this.solNedifCalc = solNedifCalc;
    }

    public BigDecimal getBomberosCalc() {
        return bomberosCalc;
    }

    public void setBomberosCalc(BigDecimal bomberosCalc) {
        this.bomberosCalc = bomberosCalc;
    }

    public BigDecimal getTasaMantCalc() {
        return tasaMantCalc;
    }

    public void setTasaMantCalc(BigDecimal tasaMantCalc) {
        this.tasaMantCalc = tasaMantCalc;
    }

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFecPago() {
        return fecPago;
    }

    public void setFecPago(Date fecPago) {
        this.fecPago = fecPago;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    public Boolean getAprobCatastro() {
        return aprobCatastro;
    }

    public void setAprobCatastro(Boolean aprobCatastro) {
        this.aprobCatastro = aprobCatastro;
    }

    public Boolean getAprobFinanciero() {
        return aprobFinanciero;
    }

    public void setAprobFinanciero(Boolean aprobFinanciero) {
        this.aprobFinanciero = aprobFinanciero;
    }

    public Boolean getAprobRentas() {
        return aprobRentas;
    }

    public void setAprobRentas(Boolean aprobRentas) {
        this.aprobRentas = aprobRentas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFecFinanciero() {
        return fecFinanciero;
    }

    public void setFecFinanciero(Date fecFinanciero) {
        this.fecFinanciero = fecFinanciero;
    }

    public Date getFecRentas() {
        return fecRentas;
    }

    public void setFecRentas(Date fecRentas) {
        this.fecRentas = fecRentas;
    }

    public String getFirmaCatastro() {
        return firmaCatastro;
    }

    public void setFirmaCatastro(String firmaCatastro) {
        this.firmaCatastro = firmaCatastro;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
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
        if (!(object instanceof ComparativoEmision)) {
            return false;
        }
        ComparativoEmision other = (ComparativoEmision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.historic.ComparativoEmision[ id=" + id + " ]";
    }

}
