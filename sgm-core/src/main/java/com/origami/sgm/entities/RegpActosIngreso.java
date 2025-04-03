/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "regp_actos_ingreso", schema = SchemasConfig.FLOW)
@NamedQueries({
    @NamedQuery(name = "RegpActosIngreso.findAll", query = "SELECT r FROM RegpActosIngreso r")})
public class RegpActosIngreso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "nombre")
    private String nombre = "";
    @Column(name = "codigo_ordenanza")
    private String codigoOrdenanza;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 8, scale = 2)
    private BigDecimal valor = BigDecimal.valueOf(0.00);
    @Column(name = "codigo_rubro_sac")
    private Integer codigoRubroSac;
    @Column(name = "porcentaje", precision = 5, scale = 2)
    private BigDecimal porcentaje;
    @Column(name = "valor_max", precision = 8, scale = 2)
    private BigDecimal valorMax;
    @Column(name = "realiza_transferencia")
    private Boolean realizaTransferencia =  false;
    @Column(name = "sube_documento")
    private Boolean subeDocumento = false;
    @Column(name = "gastos_generales")
    private Boolean gastosGenerales = false;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "reliquidacion")
    private Boolean reliquidacion = false;

    @Column(name = "fecha_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCre;
    @Column(name = "fecha_edicion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEdicion;
    @Column(name = "user_cre")
    private String userCre;
    @Column(name = "user_edicion")
    private String userEdicion;

    @JoinColumn(name = "tipo_tarea", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpActoTipoTarea tipoTarea;
    @JoinColumn(name = "tipo_cobro", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpActoTipoCobro tipoCobro;
    @JoinColumn(name = "tipo_acto", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegpActoTipoActo tipoActo;
    
    @JoinColumn(name = "rubro_liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenRubrosLiquidacion rubroLiquidacion;

    public RegpActosIngreso() {
    }

    public RegpActosIngreso(Long id) {
        this.id = id;
    }

    public RegpActosIngreso(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoOrdenanza() {
        return codigoOrdenanza;
    }

    public void setCodigoOrdenanza(String codigoOrdenanza) {
        this.codigoOrdenanza = codigoOrdenanza;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getCodigoRubroSac() {
        return codigoRubroSac;
    }

    public void setCodigoRubroSac(Integer codigoRubroSac) {
        this.codigoRubroSac = codigoRubroSac;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public BigDecimal getValorMax() {
        return valorMax;
    }

    public void setValorMax(BigDecimal valorMax) {
        this.valorMax = valorMax;
    }

    public Boolean getRealizaTransferencia() {
        return realizaTransferencia;
    }

    public void setRealizaTransferencia(Boolean realizaTransferencia) {
        this.realizaTransferencia = realizaTransferencia;
    }

    public Boolean getSubeDocumento() {
        return subeDocumento;
    }

    public void setSubeDocumento(Boolean subeDocumento) {
        this.subeDocumento = subeDocumento;
    }

    public Boolean getGastosGenerales() {
        return gastosGenerales;
    }

    public void setGastosGenerales(Boolean gastosGenerales) {
        this.gastosGenerales = gastosGenerales;
    }

    public RegpActoTipoTarea getTipoTarea() {
        return tipoTarea;
    }

    public void setTipoTarea(RegpActoTipoTarea tipoTarea) {
        this.tipoTarea = tipoTarea;
    }

    public RegpActoTipoCobro getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(RegpActoTipoCobro tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public RegpActoTipoActo getTipoActo() {
        return tipoActo;
    }

    public void setTipoActo(RegpActoTipoActo tipoActo) {
        this.tipoActo = tipoActo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getReliquidacion() {
        return reliquidacion;
    }

    public void setReliquidacion(Boolean reliquidacion) {
        this.reliquidacion = reliquidacion;
    }

    public Date getFechaCre() {
        return fechaCre;
    }

    public void setFechaCre(Date fechaCre) {
        this.fechaCre = fechaCre;
    }

    public Date getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(Date fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getUserCre() {
        return userCre;
    }

    public void setUserCre(String userCre) {
        this.userCre = userCre;
    }

    public String getUserEdicion() {
        return userEdicion;
    }

    public void setUserEdicion(String userEdicion) {
        this.userEdicion = userEdicion;
    }

    public RenRubrosLiquidacion getRubroLiquidacion() {
        return rubroLiquidacion;
    }

    public void setRubroLiquidacion(RenRubrosLiquidacion rubroLiquidacion) {
        this.rubroLiquidacion = rubroLiquidacion;
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
        if (!(object instanceof RegpActosIngreso)) {
            return false;
        }
        RegpActosIngreso other = (RegpActosIngreso) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RegpActosIngreso[ id=" + id + " ]";
    }

}
