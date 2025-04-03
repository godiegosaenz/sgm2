/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CtlgCatalogo;
import com.origami.sgm.entities.CatPredioS4;
import com.origami.sgm.entities.CatPredioS12;
import com.origami.sgm.entities.CatPredioS6;
import com.origami.sgm.entities.CatPredio;
import com.origami.sgm.entities.CatPredioEdificacion;
import com.origami.sgm.entities.CatPredioPropietario;
import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatEnte;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "ctlg_item", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"catalogo", "codename"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CtlgItem.findAll", query = "SELECT c FROM CtlgItem c")
    ,
    @NamedQuery(name = "CtlgItem.findById", query = "SELECT c FROM CtlgItem c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CtlgItem.findByValor", query = "SELECT c FROM CtlgItem c WHERE c.valor = :valor")
    ,
    @NamedQuery(name = "CtlgItem.findByCodename", query = "SELECT c FROM CtlgItem c WHERE c.codename = :codename")})
@SequenceGenerator(name = "ctlg_item_id_seq", sequenceName = SchemasConfig.APP1 + ".ctlg_item_id_seq", allocationSize = 1)
public class CtlgItem implements Serializable {

    private static final long serialVersionUID = 8799656478674716638L;

    @Column(name = "referencia")
    private BigInteger referencia;
    @Column(name = "padre")
    @Expose
    private BigInteger padre;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ctlg_item_id_seq")
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull

    @Column(name = "valor", nullable = false)
    @Expose(serialize = true, deserialize = true)
    private String valor;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "codename", nullable = false, length = 40)
    @Expose
    private String codename;
    @Column(name = "factor", precision = 10, scale = 4)
    private BigDecimal factor;
    @Column(name = "rango_desde", precision = 19, scale = 5)
    private BigDecimal rangoDesde;
    @Column(name = "rango_hasta", precision = 19, scale = 5)
    private BigDecimal rangoHasta;
    @Column(name = "orden")
    @Expose
    private Integer orden;
    @Column(name = "hijo")
    @Expose
    private BigInteger hijo;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @ManyToMany(mappedBy = "detalleList", fetch = FetchType.LAZY)
    private Collection<PeInspeccionFinal> peInspeccionFinalCollection;

    @ManyToMany(mappedBy = "ctlgItemCollection", fetch = FetchType.LAZY)
    private Collection<CatPredioS6> catPredioS6Collection;

    @ManyToMany(mappedBy = "ctlgItemCollectionInstalacionEspecial", fetch = FetchType.LAZY)
    private Collection<CatPredioS6> catPredioS6CollectionInstalaciones;

    @ManyToMany(mappedBy = "accesibilidadList", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection;
    @ManyToMany(mappedBy = "usosList", fetch = FetchType.LAZY)
    private Collection<CatPredioS12> catPredioS12Collection;
    @JoinColumn(name = "catalogo", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CtlgCatalogo catalogo;
    @OneToMany(mappedBy = "topografia", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection1;
    @OneToMany(mappedBy = "tipoSuelo", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection2;
    @OneToMany(mappedBy = "locManzana", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection3;
    @OneToMany(mappedBy = "cerramientoCtlg", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection4;
    @OneToMany(mappedBy = "estadoSolar", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection5;

    @OneToMany(mappedBy = "coberturaPredominante", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection6;
    @OneToMany(mappedBy = "ecosistemaRelevante", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection7;
    @OneToMany(mappedBy = "riesgo", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection8;
    @OneToMany(mappedBy = "erosion", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection9;
    @OneToMany(mappedBy = "drenaje", fetch = FetchType.LAZY)
    private Collection<CatPredioS4> catPredioS4Collection10;

    @OneToMany(mappedBy = "abastAgua", fetch = FetchType.LAZY)
    private Collection<CatPredioS6> catPredioS6Collection1;
    @OneToMany(mappedBy = "evacAguasServ", fetch = FetchType.LAZY)
    private Collection<CatPredioS6> catPredioS6Collection3;
    @OneToMany(mappedBy = "abasteElectrico", fetch = FetchType.LAZY)
    private Collection<CatPredioS6> catPredioS6Collection4;
    @OneToMany(mappedBy = "topografiaSolar", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<CatPredio> topografiaSolarCollection;

    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<CatPredioPropietario> catPredioPropietarioCollection;

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    private Collection<RegFicha> regFichaCollection;
    @OneToMany(mappedBy = "estadoConservacion", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<CatPredioEdificacion> catPredioEdificacionCollection;
    @OneToMany(mappedBy = "usoSolar", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<CatPredio> catPredioCollection4;
    @OneToMany(mappedBy = "prototipo", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<CatPredioEdificacion> prototipoCollection;
    @OneToMany(mappedBy = "constructividad", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<CatPredio> catPredioconstructividadCollection4;
    @OneToMany(mappedBy = "discapacidad")
    private List<CatEnte> catEntesDiscapacidad1;

    @OneToMany(mappedBy = "estadoCivil", fetch = FetchType.LAZY)
    private List<CatEnte> estadoCivilList;
    @OneToMany(mappedBy = "tipoDocumento", fetch = FetchType.LAZY)
    private List<CatEnte> tipoDocumentoList;
    @OneToMany(mappedBy = "clasificacionSuelo", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CatPredio> clasificacionSueloList;
    @OneToMany(mappedBy = "afectacionLote", fetch = FetchType.LAZY)
    private List<CatPredioS4> afectacionLoteCollection;
    @OneToMany(mappedBy = "tipoPoseedor", fetch = FetchType.LAZY)
    private List<CatPredio> tipoPoseedorCollection;
    @OneToMany(mappedBy = "tipoProtocolizacion", fetch = FetchType.LAZY)
    private List<CatEscritura> tipoProtocolizacionCollection;

    public CtlgItem() {
    }

    public CtlgItem(Long id) {
        this.id = id;
    }

    public CtlgItem(Long id, String valor, String codename) {
        this.id = id;
        this.valor = valor;
        this.codename = codename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS6> getCatPredioS6Collection() {
        return catPredioS6Collection;
    }

    public void setCatPredioS6Collection(Collection<CatPredioS6> catPredioS6Collection) {
        this.catPredioS6Collection = catPredioS6Collection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection() {
        return catPredioS4Collection;
    }

    public void setCatPredioS4Collection(Collection<CatPredioS4> catPredioS4Collection) {
        this.catPredioS4Collection = catPredioS4Collection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS12> getCatPredioS12Collection() {
        return catPredioS12Collection;
    }

    public void setCatPredioS12Collection(Collection<CatPredioS12> catPredioS12Collection) {
        this.catPredioS12Collection = catPredioS12Collection;
    }

    public CtlgCatalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(CtlgCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection1() {
        return catPredioS4Collection1;
    }

    public void setCatPredioS4Collection1(Collection<CatPredioS4> catPredioS4Collection1) {
        this.catPredioS4Collection1 = catPredioS4Collection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection2() {
        return catPredioS4Collection2;
    }

    public void setCatPredioS4Collection2(Collection<CatPredioS4> catPredioS4Collection2) {
        this.catPredioS4Collection2 = catPredioS4Collection2;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection3() {
        return catPredioS4Collection3;
    }

    public void setCatPredioS4Collection3(Collection<CatPredioS4> catPredioS4Collection3) {
        this.catPredioS4Collection3 = catPredioS4Collection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection4() {
        return catPredioS4Collection4;
    }

    public void setCatPredioS4Collection4(Collection<CatPredioS4> catPredioS4Collection4) {
        this.catPredioS4Collection4 = catPredioS4Collection4;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection5() {
        return catPredioS4Collection5;
    }

    public void setCatPredioS4Collection5(Collection<CatPredioS4> catPredioS4Collection5) {
        this.catPredioS4Collection5 = catPredioS4Collection5;
    }
//holguer

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection6() {
        return catPredioS4Collection6;
    }

    public void setCatPredioS4Collection6(Collection<CatPredioS4> catPredioS4Collection6) {
        this.catPredioS4Collection6 = catPredioS4Collection6;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection7() {
        return catPredioS4Collection7;
    }

    public void setCatPredioS4Collection7(Collection<CatPredioS4> catPredioS4Collection7) {
        this.catPredioS4Collection7 = catPredioS4Collection7;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection8() {
        return catPredioS4Collection8;
    }

    public void setCatPredioS4Collection8(Collection<CatPredioS4> catPredioS4Collection8) {
        this.catPredioS4Collection8 = catPredioS4Collection8;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection9() {
        return catPredioS4Collection9;
    }

    public void setCatPredioS4Collection9(Collection<CatPredioS4> catPredioS4Collection9) {
        this.catPredioS4Collection9 = catPredioS4Collection9;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS4> getCatPredioS4Collection10() {
        return catPredioS4Collection10;
    }

    public void setCatPredioS4Collection10(Collection<CatPredioS4> catPredioS4Collection10) {
        this.catPredioS4Collection10 = catPredioS4Collection10;
    }

//        holguer
    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS6> getCatPredioS6Collection1() {
        return catPredioS6Collection1;
    }

    public void setCatPredioS6Collection1(Collection<CatPredioS6> catPredioS6Collection1) {
        this.catPredioS6Collection1 = catPredioS6Collection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS6> getCatPredioS6Collection3() {
        return catPredioS6Collection3;
    }

    public void setCatPredioS6Collection3(Collection<CatPredioS6> catPredioS6Collection3) {
        this.catPredioS6Collection3 = catPredioS6Collection3;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS6> getCatPredioS6Collection4() {
        return catPredioS6Collection4;
    }

    public void setCatPredioS6Collection4(Collection<CatPredioS6> catPredioS6Collection4) {
        this.catPredioS6Collection4 = catPredioS6Collection4;
    }

    public Collection<CatPredioPropietario> getCatPredioPropietarioCollection() {
        return catPredioPropietarioCollection;
    }

    public void setCatPredioPropietarioCollection(Collection<CatPredioPropietario> catPredioPropietarioCollection) {
        this.catPredioPropietarioCollection = catPredioPropietarioCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegFicha> getRegFichaCollection() {
        return regFichaCollection;
    }

    public void setRegFichaCollection(Collection<RegFicha> regFichaCollection) {
        this.regFichaCollection = regFichaCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioEdificacion> getCatPredioEdificacionCollection() {
        return catPredioEdificacionCollection;
    }

    public void setCatPredioEdificacionCollection(Collection<CatPredioEdificacion> catPredioEdificacionCollection) {
        this.catPredioEdificacionCollection = catPredioEdificacionCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS6> getCatPredioS6CollectionInstalaciones() {
        return catPredioS6CollectionInstalaciones;
    }

    public void setCatPredioS6CollectionInstalaciones(Collection<CatPredioS6> catPredioS6CollectionInstalaciones) {
        this.catPredioS6CollectionInstalaciones = catPredioS6CollectionInstalaciones;
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
        if (!(object instanceof CtlgItem)) {
            return false;
        }
        CtlgItem other = (CtlgItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return orden != null ? orden.toString() : "";
    }

    public Collection<PeInspeccionFinal> getPeInspeccionFinalCollection() {
        return peInspeccionFinalCollection;
    }

    public void setPeInspeccionFinalCollection(Collection<PeInspeccionFinal> peInspeccionFinalCollection) {
        this.peInspeccionFinalCollection = peInspeccionFinalCollection;
    }

    public Collection<CatPredio> getCatPredioCollection4() {
        return catPredioCollection4;
    }

    public void setCatPredioCollection4(Collection<CatPredio> catPredioCollection4) {
        this.catPredioCollection4 = catPredioCollection4;
    }

    public Collection<CatPredioEdificacion> getPrototipoCollection() {
        return prototipoCollection;
    }

    public void setPrototipoCollection(Collection<CatPredioEdificacion> prototipoCollection) {
        this.prototipoCollection = prototipoCollection;
    }

    public Collection<CatPredio> getCatPredioconstructividadCollection4() {
        return catPredioconstructividadCollection4;
    }

    public void setCatPredioconstructividadCollection4(Collection<CatPredio> catPredioconstructividadCollection4) {
        this.catPredioconstructividadCollection4 = catPredioconstructividadCollection4;
    }

    public BigInteger getReferencia() {
        return referencia;
    }

    public void setReferencia(BigInteger referencia) {
        this.referencia = referencia;
    }

    public BigInteger getPadre() {
        return padre;
    }

    public void setPadre(BigInteger padre) {
        this.padre = padre;
    }

    public Collection<CatPredio> getTopografiaSolarCollection() {
        return topografiaSolarCollection;
    }

    public void setTopografiaSolarCollection(Collection<CatPredio> topografiaSolarCollection) {
        this.topografiaSolarCollection = topografiaSolarCollection;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public List<CatEnte> getCatEntesDiscapacidad1() {
        return catEntesDiscapacidad1;
    }

    public void setCatEntesDiscapacidad1(List<CatEnte> catEntesDiscapacidad1) {
        this.catEntesDiscapacidad1 = catEntesDiscapacidad1;
    }

    public List<CatEnte> getEstadoCivilList() {
        return estadoCivilList;
    }

    public void setEstadoCivilList(List<CatEnte> estadoCivilList) {
        this.estadoCivilList = estadoCivilList;
    }

    public List<CatEnte> getTipoDocumentoList() {
        return tipoDocumentoList;
    }

    public void setTipoDocumentoList(List<CatEnte> tipoDocumentoList) {
        this.tipoDocumentoList = tipoDocumentoList;
    }

    public List<CatPredio> getClasificacionSueloList() {
        return clasificacionSueloList;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public void setClasificacionSueloList(List<CatPredio> clasificacionSueloList) {
        this.clasificacionSueloList = clasificacionSueloList;
    }

    public List<CatPredioS4> getAfectacionLoteCollection() {
        return afectacionLoteCollection;
    }

    public void setAfectacionLoteCollection(List<CatPredioS4> afectacionLoteCollection) {
        this.afectacionLoteCollection = afectacionLoteCollection;
    }

    public List<CatPredio> getTipoPoseedorCollection() {
        return tipoPoseedorCollection;
    }

    public void setTipoPoseedorCollection(List<CatPredio> tipoPoseedorCollection) {
        this.tipoPoseedorCollection = tipoPoseedorCollection;
    }

    public List<CatEscritura> getTipoProtocolizacionCollection() {
        return tipoProtocolizacionCollection;
    }

    public void setTipoProtocolizacionCollection(List<CatEscritura> tipoProtocolizacionCollection) {
        this.tipoProtocolizacionCollection = tipoProtocolizacionCollection;
    }

    public BigDecimal getRangoDesde() {
        return rangoDesde;
    }

    public void setRangoDesde(BigDecimal rangoDesde) {
        this.rangoDesde = rangoDesde;
    }

    public BigDecimal getRangoHasta() {
        return rangoHasta;
    }

    public void setRangoHasta(BigDecimal rangoHasta) {
        this.rangoHasta = rangoHasta;
    }

    public BigInteger getHijo() {
        return hijo;
    }

    public void setHijo(BigInteger hijo) {
        this.hijo = hijo;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

}
