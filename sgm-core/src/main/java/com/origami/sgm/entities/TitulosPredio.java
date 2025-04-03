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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author root
 */
@Entity
@Table(name = "titulos_predio", schema = "dbo")
public class TitulosPredio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 18)
    @Column(name = "pre_codigocatastral")
    private String preCodigocatastral;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "titpr_numtitulo")
    private String titprNumtitulo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "titpr_valtotalterrpredio")
    private BigDecimal titprValtotalterrpredio;
    @Column(name = "titpr_valtotaledifpredio")
    private BigDecimal titprValtotaledifpredio;
    @Column(name = "titpr_valcultivos")
    private BigDecimal titprValcultivos;
    @Column(name = "titpr_valforestales")
    private BigDecimal titprValforestales;
    @Column(name = "titpr_valobrasinter")
    private BigDecimal titprValobrasinter;
    @Column(name = "titpr_valotrasinver")
    private BigDecimal titprValotrasinver;
    @Column(name = "titpr_valcomerpredio")
    private BigDecimal titprValcomerpredio;
    @Column(name = "titpr_ipu")
    private BigDecimal titprIpu;
    @Column(name = "titpr_solnoedif")
    private BigDecimal titprSolnoedif;
    @Column(name = "titpr_bomberos")
    private BigDecimal titprBomberos;
    @Column(name = "titpr_fechaemision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date titprFechaemision;
    @Column(name = "titpr_valoremitido")
    private BigDecimal titprValoremitido;
    @Column(name = "titpr_descuento")
    private BigDecimal titprDescuento;
    @Column(name = "titpr_recargo")
    private BigDecimal titprRecargo;
    @Column(name = "titpr_interes")
    private BigDecimal titprInteres;
    @Column(name = "titpr_valortcobrado")
    private BigDecimal titprValortcobrado;
    @Size(max = 1)
    @Column(name = "titpr_estado")
    private String titprEstado;
    @Column(name = "titpr_fecharecaudacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date titprFecharecaudacion;
    @Column(name = "titpr_tasaadministrativa")
    private BigDecimal titprTasaadministrativa;
    @Column(name = "titpr_rebajahipotec")
    private BigDecimal titprRebajahipotec;
    @Column(name = "titpr_baseimponible")
    private BigDecimal titprBaseimponible;
    @Column(name = "titpr_constobsoleta")
    private BigDecimal titprConstobsoleta;
    @Size(max = 18)
    @Column(name = "titpr_titulogral")
    private String titprTitulogral;
    @Column(name = "titpr_snerecargo")
    private BigDecimal titprSnerecargo;
    @Size(max = 250)
    @Column(name = "titpr_observaciones")
    private String titprObservaciones;
    @Size(max = 15)
    @Column(name = "usu_usuario")
    private String usuUsuario;
    @Size(max = 13)
    @Column(name = "titpr_ruc_ci")
    private String titprRucCi;
    @Size(max = 100)
    @Column(name = "titpr_direccioncont")
    private String titprDireccioncont;
    @Size(max = 138)
    @Column(name = "titpr_nombres")
    private String titprNombres;
    @Size(max = 2)
    @Column(name = "titpr_propietario")
    private String titprPropietario;
    @Size(max = 6)
    @Column(name = "titpr_tipo")
    private String titprTipo;
    @Column(name = "titpr_valor1")
    private BigDecimal titprValor1;
    @Column(name = "titpr_valor2")
    private BigDecimal titprValor2;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    public TitulosPredio() {
    }

    public TitulosPredio(Long id) {
        this.id = id;
    }

    public TitulosPredio(Long id, String titprNumtitulo) {
        this.id = id;
        this.titprNumtitulo = titprNumtitulo;
    }

    public String getPreCodigocatastral() {
        return preCodigocatastral;
    }

    public void setPreCodigocatastral(String preCodigocatastral) {
        this.preCodigocatastral = preCodigocatastral;
    }

    public String getTitprNumtitulo() {
        return titprNumtitulo;
    }

    public void setTitprNumtitulo(String titprNumtitulo) {
        this.titprNumtitulo = titprNumtitulo;
    }

    public BigDecimal getTitprValtotalterrpredio() {
        return titprValtotalterrpredio;
    }

    public void setTitprValtotalterrpredio(BigDecimal titprValtotalterrpredio) {
        this.titprValtotalterrpredio = titprValtotalterrpredio;
    }

    public BigDecimal getTitprValtotaledifpredio() {
        return titprValtotaledifpredio;
    }

    public void setTitprValtotaledifpredio(BigDecimal titprValtotaledifpredio) {
        this.titprValtotaledifpredio = titprValtotaledifpredio;
    }

    public BigDecimal getTitprValcultivos() {
        return titprValcultivos;
    }

    public void setTitprValcultivos(BigDecimal titprValcultivos) {
        this.titprValcultivos = titprValcultivos;
    }

    public BigDecimal getTitprValforestales() {
        return titprValforestales;
    }

    public void setTitprValforestales(BigDecimal titprValforestales) {
        this.titprValforestales = titprValforestales;
    }

    public BigDecimal getTitprValobrasinter() {
        return titprValobrasinter;
    }

    public void setTitprValobrasinter(BigDecimal titprValobrasinter) {
        this.titprValobrasinter = titprValobrasinter;
    }

    public BigDecimal getTitprValotrasinver() {
        return titprValotrasinver;
    }

    public void setTitprValotrasinver(BigDecimal titprValotrasinver) {
        this.titprValotrasinver = titprValotrasinver;
    }

    public BigDecimal getTitprValcomerpredio() {
        return titprValcomerpredio;
    }

    public void setTitprValcomerpredio(BigDecimal titprValcomerpredio) {
        this.titprValcomerpredio = titprValcomerpredio;
    }

    public BigDecimal getTitprIpu() {
        return titprIpu;
    }

    public void setTitprIpu(BigDecimal titprIpu) {
        this.titprIpu = titprIpu;
    }

    public BigDecimal getTitprSolnoedif() {
        return titprSolnoedif;
    }

    public void setTitprSolnoedif(BigDecimal titprSolnoedif) {
        this.titprSolnoedif = titprSolnoedif;
    }

    public BigDecimal getTitprBomberos() {
        return titprBomberos;
    }

    public void setTitprBomberos(BigDecimal titprBomberos) {
        this.titprBomberos = titprBomberos;
    }

    public Date getTitprFechaemision() {
        return titprFechaemision;
    }

    public void setTitprFechaemision(Date titprFechaemision) {
        this.titprFechaemision = titprFechaemision;
    }

    public BigDecimal getTitprValoremitido() {
        return titprValoremitido;
    }

    public void setTitprValoremitido(BigDecimal titprValoremitido) {
        this.titprValoremitido = titprValoremitido;
    }

    public BigDecimal getTitprDescuento() {
        return titprDescuento;
    }

    public void setTitprDescuento(BigDecimal titprDescuento) {
        this.titprDescuento = titprDescuento;
    }

    public BigDecimal getTitprRecargo() {
        return titprRecargo;
    }

    public void setTitprRecargo(BigDecimal titprRecargo) {
        this.titprRecargo = titprRecargo;
    }

    public BigDecimal getTitprInteres() {
        return titprInteres;
    }

    public void setTitprInteres(BigDecimal titprInteres) {
        this.titprInteres = titprInteres;
    }

    public BigDecimal getTitprValortcobrado() {
        return titprValortcobrado;
    }

    public void setTitprValortcobrado(BigDecimal titprValortcobrado) {
        this.titprValortcobrado = titprValortcobrado;
    }

    public String getTitprEstado() {
        return titprEstado;
    }

    public void setTitprEstado(String titprEstado) {
        this.titprEstado = titprEstado;
    }

    public Date getTitprFecharecaudacion() {
        return titprFecharecaudacion;
    }

    public void setTitprFecharecaudacion(Date titprFecharecaudacion) {
        this.titprFecharecaudacion = titprFecharecaudacion;
    }

    public BigDecimal getTitprTasaadministrativa() {
        return titprTasaadministrativa;
    }

    public void setTitprTasaadministrativa(BigDecimal titprTasaadministrativa) {
        this.titprTasaadministrativa = titprTasaadministrativa;
    }

    public BigDecimal getTitprRebajahipotec() {
        return titprRebajahipotec;
    }

    public void setTitprRebajahipotec(BigDecimal titprRebajahipotec) {
        this.titprRebajahipotec = titprRebajahipotec;
    }

    public BigDecimal getTitprBaseimponible() {
        return titprBaseimponible;
    }

    public void setTitprBaseimponible(BigDecimal titprBaseimponible) {
        this.titprBaseimponible = titprBaseimponible;
    }

    public BigDecimal getTitprConstobsoleta() {
        return titprConstobsoleta;
    }

    public void setTitprConstobsoleta(BigDecimal titprConstobsoleta) {
        this.titprConstobsoleta = titprConstobsoleta;
    }

    public String getTitprTitulogral() {
        return titprTitulogral;
    }

    public void setTitprTitulogral(String titprTitulogral) {
        this.titprTitulogral = titprTitulogral;
    }

    public BigDecimal getTitprSnerecargo() {
        return titprSnerecargo;
    }

    public void setTitprSnerecargo(BigDecimal titprSnerecargo) {
        this.titprSnerecargo = titprSnerecargo;
    }

    public String getTitprObservaciones() {
        return titprObservaciones;
    }

    public void setTitprObservaciones(String titprObservaciones) {
        this.titprObservaciones = titprObservaciones;
    }

    public String getUsuUsuario() {
        return usuUsuario;
    }

    public void setUsuUsuario(String usuUsuario) {
        this.usuUsuario = usuUsuario;
    }

    public String getTitprRucCi() {
        return titprRucCi;
    }

    public void setTitprRucCi(String titprRucCi) {
        this.titprRucCi = titprRucCi;
    }

    public String getTitprDireccioncont() {
        return titprDireccioncont;
    }

    public void setTitprDireccioncont(String titprDireccioncont) {
        this.titprDireccioncont = titprDireccioncont;
    }

    public String getTitprNombres() {
        return titprNombres;
    }

    public void setTitprNombres(String titprNombres) {
        this.titprNombres = titprNombres;
    }

    public String getTitprPropietario() {
        return titprPropietario;
    }

    public void setTitprPropietario(String titprPropietario) {
        this.titprPropietario = titprPropietario;
    }

    public String getTitprTipo() {
        return titprTipo;
    }

    public void setTitprTipo(String titprTipo) {
        this.titprTipo = titprTipo;
    }

    public BigDecimal getTitprValor1() {
        return titprValor1;
    }

    public void setTitprValor1(BigDecimal titprValor1) {
        this.titprValor1 = titprValor1;
    }

    public BigDecimal getTitprValor2() {
        return titprValor2;
    }

    public void setTitprValor2(BigDecimal titprValor2) {
        this.titprValor2 = titprValor2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof TitulosPredio)) {
            return false;
        }
        TitulosPredio other = (TitulosPredio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.ame.TitulosPredio[ id=" + id + " ]";
    }
    
}
