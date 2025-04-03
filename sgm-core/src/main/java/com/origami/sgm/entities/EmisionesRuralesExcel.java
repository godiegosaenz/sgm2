/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
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
import javax.validation.constraints.Size;

/**
 *
 * @author origami
 */
@Entity
@Table(name = "emisiones_rurales_excel", schema = SchemasConfig.APP1)
@NamedQueries({
    @NamedQuery(name = "EmisionesRuralesExcel.findAll", query = "SELECT e FROM EmisionesRuralesExcel e")})
public class EmisionesRuralesExcel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 200)
    @Column(name = "codigo_catastral")
    private String codigoCatastral;
    @Size(max = 200)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 200)
    @Column(name = "apellidos")
    private String apellidos;
    @Size(max = 20)
    @Column(name = "ci_ruc")
    private String ciRuc;
    @Size(max = 100)
    @Column(name = "tipo_persona")
    private String tipoPersona;
    @Size(max = 200)
    @Column(name = "utilidad_publica")
    private String utilidadPublica;
    @Size(max = 200)
    @Column(name = "nombre_predio")
    private String nombrePredio;
    @Size(max = 200)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 100)
    @Column(name = "sector")
    private String sector;
    @Size(max = 100)
    @Column(name = "parroquia")
    private String parroquia;
    @Column(name = "cod_parroquia")
    private BigInteger codParroquia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "avaluo_terreno")
    private BigDecimal avaluoTerreno;
    @Column(name = "avaluo_costruccion")
    private BigDecimal avaluoCostruccion;
    @Column(name = "avaluo_mejoras")
    private BigDecimal avaluoMejoras;
    @Column(name = "avaluo_total")
    private BigDecimal avaluoTotal;
    @Column(name = "fecha_avaluo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAvaluo;
    @Column(name = "area_terreno")
    private BigDecimal areaTerreno;
    @Column(name = "area_construccion")
    private BigDecimal areaConstruccion;
    @Column(name = "banda")
    private BigDecimal banda;
    @Column(name = "impuesto_predial")
    private BigDecimal impuestoPredial;
    @Column(name = "tasa_mantenimiento")
    private BigDecimal tasaMantenimiento;
    @Column(name = "emision")
    private BigDecimal emision;
    @Column(name = "bomberos")
    private BigDecimal bomberos;
    @Column(name = "total")
    private BigDecimal total;
    @Size(max = 200)
    @Column(name = "codigo_catastral_anterior")
    private String codigoCatastralAnterior;
    @OneToMany(mappedBy = "ruralExcel", fetch = FetchType.LAZY)
    private Collection<RenLiquidacion> renLiquidacionCollection;

    public EmisionesRuralesExcel() {
    }

    public EmisionesRuralesExcel(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoCatastral() {
        return codigoCatastral;
    }

    public void setCodigoCatastral(String codigoCatastral) {
        this.codigoCatastral = codigoCatastral;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCiRuc() {
        return ciRuc;
    }

    public void setCiRuc(String ciRuc) {
        this.ciRuc = ciRuc;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getUtilidadPublica() {
        return utilidadPublica;
    }

    public void setUtilidadPublica(String utilidadPublica) {
        this.utilidadPublica = utilidadPublica;
    }

    public String getNombrePredio() {
        return nombrePredio;
    }

    public void setNombrePredio(String nombrePredio) {
        this.nombrePredio = nombrePredio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getParroquia() {
        return parroquia;
    }

    public void setParroquia(String parroquia) {
        this.parroquia = parroquia;
    }

    public BigInteger getCodParroquia() {
        return codParroquia;
    }

    public void setCodParroquia(BigInteger codParroquia) {
        this.codParroquia = codParroquia;
    }

    public BigDecimal getAvaluoTerreno() {
        return avaluoTerreno;
    }

    public void setAvaluoTerreno(BigDecimal avaluoTerreno) {
        this.avaluoTerreno = avaluoTerreno;
    }

    public BigDecimal getAvaluoCostruccion() {
        return avaluoCostruccion;
    }

    public void setAvaluoCostruccion(BigDecimal avaluoCostruccion) {
        this.avaluoCostruccion = avaluoCostruccion;
    }

    public BigDecimal getAvaluoMejoras() {
        return avaluoMejoras;
    }

    public void setAvaluoMejoras(BigDecimal avaluoMejoras) {
        this.avaluoMejoras = avaluoMejoras;
    }

    public BigDecimal getAvaluoTotal() {
        return avaluoTotal;
    }

    public void setAvaluoTotal(BigDecimal avaluoTotal) {
        this.avaluoTotal = avaluoTotal;
    }

    public Date getFechaAvaluo() {
        return fechaAvaluo;
    }

    public void setFechaAvaluo(Date fechaAvaluo) {
        this.fechaAvaluo = fechaAvaluo;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public BigDecimal getAreaConstruccion() {
        return areaConstruccion;
    }

    public void setAreaConstruccion(BigDecimal areaConstruccion) {
        this.areaConstruccion = areaConstruccion;
    }

    public BigDecimal getBanda() {
        return banda;
    }

    public void setBanda(BigDecimal banda) {
        this.banda = banda;
    }

    public BigDecimal getImpuestoPredial() {
        return impuestoPredial;
    }

    public void setImpuestoPredial(BigDecimal impuestoPredial) {
        this.impuestoPredial = impuestoPredial;
    }

    public BigDecimal getTasaMantenimiento() {
        return tasaMantenimiento;
    }

    public void setTasaMantenimiento(BigDecimal tasaMantenimiento) {
        this.tasaMantenimiento = tasaMantenimiento;
    }

    public BigDecimal getEmision() {
        return emision;
    }

    public void setEmision(BigDecimal emision) {
        this.emision = emision;
    }

    public BigDecimal getBomberos() {
        return bomberos;
    }

    public void setBomberos(BigDecimal bomberos) {
        this.bomberos = bomberos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCodigoCatastralAnterior() {
        return codigoCatastralAnterior;
    }

    public void setCodigoCatastralAnterior(String codigoCatastralAnterior) {
        this.codigoCatastralAnterior = codigoCatastralAnterior;
    }

    public Collection<RenLiquidacion> getRenLiquidacionCollection() {
        return renLiquidacionCollection;
    }

    public void setRenLiquidacionCollection(Collection<RenLiquidacion> renLiquidacionCollection) {
        this.renLiquidacionCollection = renLiquidacionCollection;
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
        if (!(object instanceof EmisionesRuralesExcel)) {
            return false;
        }
        EmisionesRuralesExcel other = (EmisionesRuralesExcel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.EmisionesRuralesExcel[ id=" + id + " ]";
    }
    
}
