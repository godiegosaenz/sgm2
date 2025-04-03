/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.RecActasEspeciesDet;
import com.origami.sgm.entities.RegpActosIngreso;
import com.origami.sgm.entities.RenPagoRubro;
import com.origami.sgm.entities.RenDetLiquidacion;
import com.origami.sgm.entities.RecEspecies;
import com.origami.sgm.entities.FnSolicitudTipoLiquidacionExoneracion;
import com.origami.sgm.entities.AvalDetCobroImpuestoPredios;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import com.origami.sgm.entities.database.SchemasConfig;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Joao Sanga
 */
@Entity
@Table(name = "ren_rubros_liquidacion", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "RenRubrosLiquidacion.findAll", query = "SELECT r FROM RenRubrosLiquidacion r")})
public class RenRubrosLiquidacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado")
    private Boolean estado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", scale = 2, length = 12)
    private BigDecimal valor;
    @Size(max = 25)
    @Column(name = "cuenta_presupuesto")
    private String ctaPresupuesto;
    @Size(max = 25)
    @Column(name = "cuenta_contable")
    private String ctaContable;
    @Size(max = 25)
    @Column(name = "cuenta_orden")
    private String ctaOrden;
    @Size(max = 150)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "codigo_rubro")
    private Long codigoRubro;
    @Column(name = "prioridad")
    private Long prioridad;
    @Column(name = "rubro_del_municipio")
    private Boolean rubroDelMunicipio;
    @JoinColumn(name = "tipo_liquidacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoLiquidacion tipoLiquidacion;
    @OneToMany(mappedBy = "rubro", fetch = FetchType.LAZY)
    private Collection<RenDetLiquidacion> renDetLiquidacionCollection;
    @JoinColumn(name = "tipo_valor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenTipoValor tipoValor;
    @OneToMany(mappedBy = "rubro", fetch = FetchType.LAZY)
    private Collection<RenPagoRubro> renPagoRubros;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "rubro", fetch = FetchType.LAZY)
    private RecEspecies recEspecies;

    @OneToMany(mappedBy = "rubroLiquidacion", fetch = FetchType.LAZY)
    private Collection<RegpActosIngreso> regpActosIngresoCollection;

    @OneToMany(mappedBy = "rubro", fetch = FetchType.LAZY)
    private Collection<FnSolicitudTipoLiquidacionExoneracion> rubroCollection;

    @Transient
    private BigDecimal valorTotal;
    @Transient
    private Integer cantidad;
    @Transient
    private Integer anio;
    @Transient
    private Integer mes;
    @Transient
    private RecActasEspeciesDet acta;

    @Transient
    private Boolean cobrar = false;
    @OneToMany(mappedBy = "idRubroCobrar", fetch = FetchType.LAZY)
    private List<AvalDetCobroImpuestoPredios> avalDetCobroImpuestoPredios;

    @Column(name = "function_calculation")
    private String functionCalculation;

    public RenRubrosLiquidacion() {
    }

    public RenRubrosLiquidacion(Long id) {
        this.id = id;
    }

    public RenRubrosLiquidacion(Long id, boolean estado) {
        this.id = id;
        this.estado = estado;
    }

    public RenRubrosLiquidacion(boolean estado, String descripcion, Long codigoRubro, Boolean cobrar) {
        this.estado = estado;
        this.descripcion = descripcion;
        this.codigoRubro = codigoRubro;
        this.cobrar = cobrar;
    }

    public Long getId() {
        return id;
    }

    public Collection<FnSolicitudTipoLiquidacionExoneracion> getRubroCollection() {
        return rubroCollection;
    }

    public void setRubroCollection(Collection<FnSolicitudTipoLiquidacionExoneracion> rubroCollection) {
        this.rubroCollection = rubroCollection;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getRubroDelMunicipio() {
        return rubroDelMunicipio;
    }

    public void setRubroDelMunicipio(Boolean rubroDelMunicipio) {
        this.rubroDelMunicipio = rubroDelMunicipio;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public RenTipoLiquidacion getTipoLiquidacion() {
        return tipoLiquidacion;
    }

    public void setTipoLiquidacion(RenTipoLiquidacion tipoLiquidacion) {
        this.tipoLiquidacion = tipoLiquidacion;
    }

    public RecEspecies getRecEspecies() {
        return recEspecies;
    }

    public void setRecEspecies(RecEspecies recEspecies) {
        this.recEspecies = recEspecies;
    }

    public Collection<RegpActosIngreso> getRegpActosIngresoCollection() {
        return regpActosIngresoCollection;
    }

    public void setRegpActosIngresoCollection(Collection<RegpActosIngreso> regpActosIngresoCollection) {
        this.regpActosIngresoCollection = regpActosIngresoCollection;
    }

    /*public RegpActosIngreso getRegpActosIngreso() {
    return regpActosIngreso;
    }
    public void setRegpActosIngreso(RegpActosIngreso regpActosIngreso) {
    this.regpActosIngreso = regpActosIngreso;
    }*/
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RenRubrosLiquidacion)) {
            return false;
        }
        RenRubrosLiquidacion other = (RenRubrosLiquidacion) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenRubrosLiquidacion[ id=" + id + " ]";
    }

    public Boolean getCobrar() {
        return cobrar;
    }

    public void setCobrar(Boolean cobrar) {
        this.cobrar = cobrar;
    }

    public Collection<RenDetLiquidacion> getRenDetLiquidacionCollection() {
        return renDetLiquidacionCollection;
    }

    public void setRenDetLiquidacionCollection(Collection<RenDetLiquidacion> renDetLiquidacionCollection) {
        this.renDetLiquidacionCollection = renDetLiquidacionCollection;
    }

    public String getCtaPresupuesto() {
        return ctaPresupuesto;
    }

    public void setCtaPresupuesto(String ctaPresupuesto) {
        this.ctaPresupuesto = ctaPresupuesto;
    }

    public String getCtaContable() {
        return ctaContable;
    }

    public void setCtaContable(String ctaContable) {
        this.ctaContable = ctaContable;
    }

    public String getCtaOrden() {
        return ctaOrden;
    }

    public void setCtaOrden(String ctaOrden) {
        this.ctaOrden = ctaOrden;
    }

    public RenTipoValor getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(RenTipoValor tipoValor) {
        this.tipoValor = tipoValor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCodigoRubro() {
        return codigoRubro;
    }

    public void setCodigoRubro(Long codigoRubro) {
        this.codigoRubro = codigoRubro;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Long prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public RecActasEspeciesDet getActa() {
        return acta;
    }

    public void setActa(RecActasEspeciesDet acta) {
        this.acta = acta;
    }

    public Collection<RenPagoRubro> getRenPagoRubros() {
        return renPagoRubros;
    }

    public void setRenPagoRubros(Collection<RenPagoRubro> renPagoRubros) {
        this.renPagoRubros = renPagoRubros;
    }

    public List<AvalDetCobroImpuestoPredios> getAvalDetCobroImpuestoPredios() {
        return avalDetCobroImpuestoPredios;
    }

    public void setAvalDetCobroImpuestoPredios(List<AvalDetCobroImpuestoPredios> avalDetCobroImpuestoPredios) {
        this.avalDetCobroImpuestoPredios = avalDetCobroImpuestoPredios;
    }

    public String getFunctionCalculation() {
        return functionCalculation;
    }

    public void setFunctionCalculation(String functionCalculation) {
        this.functionCalculation = functionCalculation;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }
    
}
