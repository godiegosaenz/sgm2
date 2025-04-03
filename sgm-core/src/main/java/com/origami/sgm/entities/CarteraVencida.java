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
@Table(name = "cartera_vencida", schema = "dbo")
public class CarteraVencida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 18)
    @Column(name = "pre_codigocatastral")
    private String preCodigocatastral;
    @Size(max = 13)
    @Column(name = "carve_ruc")
    private String carveRuc;
    @Size(max = 10)
    @Column(name = "carve_ci")
    private String carveCi;
    @Size(max = 100)
    @Column(name = "carve_nombres")
    private String carveNombres;
    @Size(max = 100)
    @Column(name = "carve_calle")
    private String carveCalle;
    @Size(max = 20)
    @Column(name = "carve_numero")
    private String carveNumero;
    @Size(max = 150)
    @Column(name = "carve_direccpropietario")
    private String carveDireccpropietario;
    @Column(name = "carve_valtotalterrpredio")
    private Long carveValtotalterrpredio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "carve_valtotaledifpredio")
    private BigDecimal carveValtotaledifpredio;
    @Column(name = "carve_valcultivos")
    private BigDecimal carveValcultivos;
    @Column(name = "carve_valforestales")
    private BigDecimal carveValforestales;
    @Column(name = "carve_valobrasinter")
    private BigDecimal carveValobrasinter;
    @Column(name = "carve_valotrasinver")
    private BigDecimal carveValotrasinver;
    @Column(name = "carve_valcomerpredio")
    private BigDecimal carveValcomerpredio;
    @Column(name = "carve_rebajahipotec")
    private BigDecimal carveRebajahipotec;
    @Column(name = "carve_baseimponible")
    private BigDecimal carveBaseimponible;
    @Column(name = "carve_rebajageneral")
    private BigDecimal carveRebajageneral;
    @Column(name = "carve_ipu")
    private BigDecimal carveIpu;
    @Column(name = "carve_magisterio")
    private BigDecimal carveMagisterio;
    @Column(name = "carve_educacionelemental")
    private BigDecimal carveEducacionelemental;
    @Column(name = "carve_medicinarural")
    private BigDecimal carveMedicinarural;
    @Column(name = "carve_estableceducativos")
    private BigDecimal carveEstableceducativos;
    @Column(name = "carve_solnoedif")
    private BigDecimal carveSolnoedif;
    @Column(name = "carve_constobsoleta")
    private BigDecimal carveConstobsoleta;
    @Column(name = "carve_snerecargo")
    private BigDecimal carveSnerecargo;
    @Column(name = "carve_viviendarural")
    private BigDecimal carveViviendarural;
    @Column(name = "carve_fechaemision")
    @Temporal(TemporalType.TIMESTAMP)
    private Date carveFechaemision;
    @Size(max = 18)
    @Column(name = "carve_titulogral")
    private String carveTitulogral;
    @Column(name = "carve_tasaadministrativa")
    private BigDecimal carveTasaadministrativa;
    @Column(name = "carve_otrosadicionales")
    private BigDecimal carveOtrosadicionales;
    @Column(name = "carve_recoleccionbasura")
    private BigDecimal carveRecoleccionbasura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "carve_bomberos")
    private BigDecimal carveBomberos;
    @Column(name = "carve_valoremitido")
    private BigDecimal carveValoremitido;
    @Column(name = "carve_valortcobrado")
    private BigDecimal carveValortcobrado;
    @Size(max = 15)
    @Column(name = "carve_numtitulo")
    private String carveNumtitulo;
    @Column(name = "carve_interes")
    private BigDecimal carveInteres;
    @Column(name = "carve_fecharecaudacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date carveFecharecaudacion;
    @Size(max = 1)
    @Column(name = "carve_estado")
    private String carveEstado;
    @Size(max = 250)
    @Column(name = "carve_observaciones")
    private String carveObservaciones;
    @Size(max = 15)
    @Column(name = "usu_usuario")
    private String usuUsuario;
    @Column(name = "carve_valor1")
    private BigDecimal carveValor1;
    @Column(name = "carve_valor2")
    private BigDecimal carveValor2;
    @Column(name = "carve_recargo")
    private BigDecimal carveRecargo;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    public CarteraVencida() {
    }

    public CarteraVencida(Long id) {
        this.id = id;
    }

    public CarteraVencida(Long id, BigDecimal carveBomberos) {
        this.id = id;
        this.carveBomberos = carveBomberos;
    }

    public String getPreCodigocatastral() {
        return preCodigocatastral;
    }

    public void setPreCodigocatastral(String preCodigocatastral) {
        this.preCodigocatastral = preCodigocatastral;
    }

    public String getCarveRuc() {
        return carveRuc;
    }

    public void setCarveRuc(String carveRuc) {
        this.carveRuc = carveRuc;
    }

    public String getCarveCi() {
        return carveCi;
    }

    public void setCarveCi(String carveCi) {
        this.carveCi = carveCi;
    }

    public String getCarveNombres() {
        return carveNombres;
    }

    public void setCarveNombres(String carveNombres) {
        this.carveNombres = carveNombres;
    }

    public String getCarveCalle() {
        return carveCalle;
    }

    public void setCarveCalle(String carveCalle) {
        this.carveCalle = carveCalle;
    }

    public String getCarveNumero() {
        return carveNumero;
    }

    public void setCarveNumero(String carveNumero) {
        this.carveNumero = carveNumero;
    }

    public String getCarveDireccpropietario() {
        return carveDireccpropietario;
    }

    public void setCarveDireccpropietario(String carveDireccpropietario) {
        this.carveDireccpropietario = carveDireccpropietario;
    }

    public Long getCarveValtotalterrpredio() {
        return carveValtotalterrpredio;
    }

    public void setCarveValtotalterrpredio(Long carveValtotalterrpredio) {
        this.carveValtotalterrpredio = carveValtotalterrpredio;
    }

    public BigDecimal getCarveValtotaledifpredio() {
        return carveValtotaledifpredio;
    }

    public void setCarveValtotaledifpredio(BigDecimal carveValtotaledifpredio) {
        this.carveValtotaledifpredio = carveValtotaledifpredio;
    }

    public BigDecimal getCarveValcultivos() {
        return carveValcultivos;
    }

    public void setCarveValcultivos(BigDecimal carveValcultivos) {
        this.carveValcultivos = carveValcultivos;
    }

    public BigDecimal getCarveValforestales() {
        return carveValforestales;
    }

    public void setCarveValforestales(BigDecimal carveValforestales) {
        this.carveValforestales = carveValforestales;
    }

    public BigDecimal getCarveValobrasinter() {
        return carveValobrasinter;
    }

    public void setCarveValobrasinter(BigDecimal carveValobrasinter) {
        this.carveValobrasinter = carveValobrasinter;
    }

    public BigDecimal getCarveValotrasinver() {
        return carveValotrasinver;
    }

    public void setCarveValotrasinver(BigDecimal carveValotrasinver) {
        this.carveValotrasinver = carveValotrasinver;
    }

    public BigDecimal getCarveValcomerpredio() {
        return carveValcomerpredio;
    }

    public void setCarveValcomerpredio(BigDecimal carveValcomerpredio) {
        this.carveValcomerpredio = carveValcomerpredio;
    }

    public BigDecimal getCarveRebajahipotec() {
        return carveRebajahipotec;
    }

    public void setCarveRebajahipotec(BigDecimal carveRebajahipotec) {
        this.carveRebajahipotec = carveRebajahipotec;
    }

    public BigDecimal getCarveBaseimponible() {
        return carveBaseimponible;
    }

    public void setCarveBaseimponible(BigDecimal carveBaseimponible) {
        this.carveBaseimponible = carveBaseimponible;
    }

    public BigDecimal getCarveRebajageneral() {
        return carveRebajageneral;
    }

    public void setCarveRebajageneral(BigDecimal carveRebajageneral) {
        this.carveRebajageneral = carveRebajageneral;
    }

    public BigDecimal getCarveIpu() {
        return carveIpu;
    }

    public void setCarveIpu(BigDecimal carveIpu) {
        this.carveIpu = carveIpu;
    }

    public BigDecimal getCarveMagisterio() {
        return carveMagisterio;
    }

    public void setCarveMagisterio(BigDecimal carveMagisterio) {
        this.carveMagisterio = carveMagisterio;
    }

    public BigDecimal getCarveEducacionelemental() {
        return carveEducacionelemental;
    }

    public void setCarveEducacionelemental(BigDecimal carveEducacionelemental) {
        this.carveEducacionelemental = carveEducacionelemental;
    }

    public BigDecimal getCarveMedicinarural() {
        return carveMedicinarural;
    }

    public void setCarveMedicinarural(BigDecimal carveMedicinarural) {
        this.carveMedicinarural = carveMedicinarural;
    }

    public BigDecimal getCarveEstableceducativos() {
        return carveEstableceducativos;
    }

    public void setCarveEstableceducativos(BigDecimal carveEstableceducativos) {
        this.carveEstableceducativos = carveEstableceducativos;
    }

    public BigDecimal getCarveSolnoedif() {
        return carveSolnoedif;
    }

    public void setCarveSolnoedif(BigDecimal carveSolnoedif) {
        this.carveSolnoedif = carveSolnoedif;
    }

    public BigDecimal getCarveConstobsoleta() {
        return carveConstobsoleta;
    }

    public void setCarveConstobsoleta(BigDecimal carveConstobsoleta) {
        this.carveConstobsoleta = carveConstobsoleta;
    }

    public BigDecimal getCarveSnerecargo() {
        return carveSnerecargo;
    }

    public void setCarveSnerecargo(BigDecimal carveSnerecargo) {
        this.carveSnerecargo = carveSnerecargo;
    }

    public BigDecimal getCarveViviendarural() {
        return carveViviendarural;
    }

    public void setCarveViviendarural(BigDecimal carveViviendarural) {
        this.carveViviendarural = carveViviendarural;
    }

    public Date getCarveFechaemision() {
        return carveFechaemision;
    }

    public void setCarveFechaemision(Date carveFechaemision) {
        this.carveFechaemision = carveFechaemision;
    }

    public String getCarveTitulogral() {
        return carveTitulogral;
    }

    public void setCarveTitulogral(String carveTitulogral) {
        this.carveTitulogral = carveTitulogral;
    }

    public BigDecimal getCarveTasaadministrativa() {
        return carveTasaadministrativa;
    }

    public void setCarveTasaadministrativa(BigDecimal carveTasaadministrativa) {
        this.carveTasaadministrativa = carveTasaadministrativa;
    }

    public BigDecimal getCarveOtrosadicionales() {
        return carveOtrosadicionales;
    }

    public void setCarveOtrosadicionales(BigDecimal carveOtrosadicionales) {
        this.carveOtrosadicionales = carveOtrosadicionales;
    }

    public BigDecimal getCarveRecoleccionbasura() {
        return carveRecoleccionbasura;
    }

    public void setCarveRecoleccionbasura(BigDecimal carveRecoleccionbasura) {
        this.carveRecoleccionbasura = carveRecoleccionbasura;
    }

    public BigDecimal getCarveBomberos() {
        return carveBomberos;
    }

    public void setCarveBomberos(BigDecimal carveBomberos) {
        this.carveBomberos = carveBomberos;
    }

    public BigDecimal getCarveValoremitido() {
        return carveValoremitido;
    }

    public void setCarveValoremitido(BigDecimal carveValoremitido) {
        this.carveValoremitido = carveValoremitido;
    }

    public BigDecimal getCarveValortcobrado() {
        return carveValortcobrado;
    }

    public void setCarveValortcobrado(BigDecimal carveValortcobrado) {
        this.carveValortcobrado = carveValortcobrado;
    }

    public String getCarveNumtitulo() {
        return carveNumtitulo;
    }

    public void setCarveNumtitulo(String carveNumtitulo) {
        this.carveNumtitulo = carveNumtitulo;
    }

    public BigDecimal getCarveInteres() {
        return carveInteres;
    }

    public void setCarveInteres(BigDecimal carveInteres) {
        this.carveInteres = carveInteres;
    }

    public Date getCarveFecharecaudacion() {
        return carveFecharecaudacion;
    }

    public void setCarveFecharecaudacion(Date carveFecharecaudacion) {
        this.carveFecharecaudacion = carveFecharecaudacion;
    }

    public String getCarveEstado() {
        return carveEstado;
    }

    public void setCarveEstado(String carveEstado) {
        this.carveEstado = carveEstado;
    }

    public String getCarveObservaciones() {
        return carveObservaciones;
    }

    public void setCarveObservaciones(String carveObservaciones) {
        this.carveObservaciones = carveObservaciones;
    }

    public String getUsuUsuario() {
        return usuUsuario;
    }

    public void setUsuUsuario(String usuUsuario) {
        this.usuUsuario = usuUsuario;
    }

    public BigDecimal getCarveValor1() {
        return carveValor1;
    }

    public void setCarveValor1(BigDecimal carveValor1) {
        this.carveValor1 = carveValor1;
    }

    public BigDecimal getCarveValor2() {
        return carveValor2;
    }

    public void setCarveValor2(BigDecimal carveValor2) {
        this.carveValor2 = carveValor2;
    }

    public BigDecimal getCarveRecargo() {
        return carveRecargo;
    }

    public void setCarveRecargo(BigDecimal carveRecargo) {
        this.carveRecargo = carveRecargo;
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
        if (!(object instanceof CarteraVencida)) {
            return false;
        }
        CarteraVencida other = (CarteraVencida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.ame.CarteraVencida[ id=" + id + " ]";
    }
    
}
