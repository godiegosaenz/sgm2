/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEdificacionPisosDet;
import com.origami.sgm.entities.CatCategoriasConstruccion;
import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.predio.models.NivelModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Where;

/**
 *
 * @author CarlosLoorVargas,
 */
@Entity
@Table(name = "cat_predio_edificacion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatPredioEdificacion.findAll", query = "SELECT c FROM CatPredioEdificacion c")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findById", query = "SELECT c FROM CatPredioEdificacion c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByNoEdificacion", query = "SELECT c FROM CatPredioEdificacion c WHERE c.noEdificacion = :noEdificacion")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByNumPisos", query = "SELECT c FROM CatPredioEdificacion c WHERE c.numPisos = :numPisos")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByAreaConsCenso", query = "SELECT c FROM CatPredioEdificacion c INNER JOIN c.predio p WHERE p.id = :idPredio")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByAreaConsPermiso", query = "SELECT c FROM CatPredioEdificacion c WHERE c.areaConsPermiso = :areaConsPermiso")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByAreaConsLosa", query = "SELECT c FROM CatPredioEdificacion c WHERE c.areaConsLosa = :areaConsLosa")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByEnConstruccionPorc", query = "SELECT c FROM CatPredioEdificacion c WHERE c.enConstruccionPorc = :enConstruccionPorc")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByAnioCons", query = "SELECT c FROM CatPredioEdificacion c WHERE c.anioCons = :anioCons")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByEstaRentado", query = "SELECT c FROM CatPredioEdificacion c WHERE c.estaRentado = :estaRentado")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByInstalacionesElectricas", query = "SELECT c FROM CatPredioEdificacion c WHERE c.instalacionesElectricas = :instalacionesElectricas")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByNumPisosnew", query = "SELECT c FROM CatPredioEdificacion c WHERE c.numPisosnew = :numPisosnew")
    ,
    @NamedQuery(name = "CatPredioEdificacion.findByVidautil", query = "SELECT c FROM CatPredioEdificacion c WHERE c.vidautil = :vidautil")})

public class CatPredioEdificacion implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "no_edificacion", nullable = false)
    @Expose
    private Short noEdificacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado", nullable = false)
    @Expose
    private String estado;
    @Size(max = 3)
    @Column(name = "tipo_losa", length = 3)
    @Expose
    private String tipoLosa;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Column(name = "num_pisos")
    @Expose
    private Short numPisos;
    @JoinColumn(name = "estado_conservacion", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem estadoConservacion;
    @JoinColumn(name = "condicion_fisica", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem condicionFisica;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "area_cons_censo", precision = 15, scale = 4)
    @Expose
    private BigDecimal areaConsCenso;
    @Column(name = "area_cons_permiso", precision = 15, scale = 4)
    @Expose
    private BigDecimal areaConsPermiso;
    @Column(name = "area_cons_losa", precision = 15, scale = 4)
    @Expose
    private BigDecimal areaConsLosa;
    @Column(name = "en_construccion_porc")
    @Expose
    private Short enConstruccionPorc;
    @Column(name = "anio_cons")
    @Expose
    private Integer anioCons;
    @Column(name = "anio_restaura")
    @Expose
    private Integer anioRestaura;

    @Column(name = "esta_rentado")
    @Expose
    private Boolean estaRentado;
    @Column(name = "instalaciones_electricas")
    @Expose
    private Short instalacionesElectricas;
    @Size(max = 20)
    @Column(name = "num_pisosnew", length = 20)
    @Expose
    private String numPisosnew;
    @Column(name = "vidautil")
    @Expose
    private Integer vidautil;
    @JoinColumn(name = "valor_cultural", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem valorCultural;
    @JoinColumn(name = "uso_constructivo_piso", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem usoConstructivoPiso;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "categoria", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CatCategoriasConstruccion categoria;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "edificacion", fetch = FetchType.LAZY)
    @Expose
    @Where(clause = "estado = " + SchemasConfig.FILTER_ESTADO)
    @javax.persistence.OrderBy("prop")
    private List<CatPredioEdificacionProp> catPredioEdificacionPropCollection = new ArrayList<>();
    @OneToMany(mappedBy = "edificacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Expose
    @Where(clause = "estado = 'A'")
    private Collection<CatEdificacionPisosDet> catEdificacionPisosDetCollection;


    @Transient
    @Expose
    private String cabecera;
    @Column(name = "factor_depreciacion", precision = 12, scale = 4)
    @Expose
    private BigDecimal factorDepreciacion;
    @JoinColumn(name = "prototipo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @Expose
    private CtlgItem prototipo;

    @Transient
    private CtlgItem tipoPrototipo;

    @Column(name = "area_bloque", precision = 15, scale = 4)
    @Expose
    private BigDecimal areaBloque;
    @Column(name = "observaciones")
    @Expose
    private String observaciones;
    @Column(name = "modificado")
    @Expose
    private String modificado;
    @Column(name = "usuario")
    @Expose
    private String usuario;
    @Column(name = "edad_construccion")
    @Expose
    private Short edadConstruccion;

    @Transient
    private List<NivelModel> niveles;
    @Transient

    private int cantAlicuotas;

    @Expose
    @Transient
    private Long idDetalleTramite;
    @Expose
    @Transient
    private List<String> fotos;


    public CatPredioEdificacion() {
        niveles = new ArrayList<>();
    }

    public CatPredioEdificacion(Long id) {
        this.id = id;
    }

    public CatPredioEdificacion(Long id, Short noEdificacion) {
        this.id = id;
        this.noEdificacion = noEdificacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getNoEdificacion() {
        return noEdificacion;
    }

    public void setNoEdificacion(Short noEdificacion) {
        this.noEdificacion = noEdificacion;
    }

    public Short getNumPisos() {
        return numPisos;
    }

    public void setNumPisos(Short numPisos) {
        this.numPisos = numPisos;
    }

    public BigDecimal getAreaConsCenso() {
        return areaConsCenso;
    }

    public void setAreaConsCenso(BigDecimal areaConsCenso) {
        this.areaConsCenso = areaConsCenso;
    }

    public BigDecimal getAreaConsPermiso() {
        return areaConsPermiso;
    }

    public void setAreaConsPermiso(BigDecimal areaConsPermiso) {
        this.areaConsPermiso = areaConsPermiso;
    }

    public BigDecimal getAreaConsLosa() {
        return areaConsLosa;
    }

    public void setAreaConsLosa(BigDecimal areaConsLosa) {
        this.areaConsLosa = areaConsLosa;
    }

    public Short getEnConstruccionPorc() {
        return enConstruccionPorc;
    }

    public void setEnConstruccionPorc(Short enConstruccionPorc) {
        this.enConstruccionPorc = enConstruccionPorc;
    }

    public Integer getAnioCons() {
        return anioCons;
    }

    public void setAnioCons(Integer anioCons) {
        this.anioCons = anioCons;
    }

    public Boolean getEstaRentado() {
        return estaRentado;
    }

    public void setEstaRentado(Boolean estaRentado) {
        this.estaRentado = estaRentado;
    }

    public Short getInstalacionesElectricas() {
        return instalacionesElectricas;
    }

    public void setInstalacionesElectricas(Short instalacionesElectricas) {
        this.instalacionesElectricas = instalacionesElectricas;
    }

    public String getNumPisosnew() {
        return numPisosnew;
    }

    public void setNumPisosnew(String numPisosnew) {
        this.numPisosnew = numPisosnew;
    }

    public Integer getVidautil() {
        return vidautil;
    }

    public void setVidautil(Integer vidautil) {
        this.vidautil = vidautil;
    }

    public CtlgItem getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(CtlgItem estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatCategoriasConstruccion getCategoria() {
        return categoria;
    }

    public void setCategoria(CatCategoriasConstruccion categoria) {
        this.categoria = categoria;
    }

    public List<CatPredioEdificacionProp> getCatPredioEdificacionPropCollection() {
        return catPredioEdificacionPropCollection;
    }

    public void setCatPredioEdificacionPropCollection(List<CatPredioEdificacionProp> catPredioEdificacionPropCollection) {
        this.catPredioEdificacionPropCollection = catPredioEdificacionPropCollection;
    }

    public String getCabecera() {
        return cabecera;
    }

    public void setCabecera(String cabecera) {
        this.cabecera = cabecera;
    }

    public BigDecimal getFactorDepreciacion() {
        return factorDepreciacion;
    }

    public void setFactorDepreciacion(BigDecimal factorDepreciacion) {
        this.factorDepreciacion = factorDepreciacion;
    }

    public CtlgItem getPrototipo() {
        return prototipo;
    }

    public void setPrototipo(CtlgItem prototipo) {
        this.prototipo = prototipo;
    }

    public CtlgItem getTipoPrototipo() {
        return tipoPrototipo;
    }

    public void setTipoPrototipo(CtlgItem tipoPrototipo) {
        this.tipoPrototipo = tipoPrototipo;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
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
        if (!(object instanceof CatPredioEdificacion)) {
            return false;
        }
        CatPredioEdificacion other = (CatPredioEdificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatPredioEdificacion[ id=" + id + " ]";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoLosa() {
        return tipoLosa;
    }

    public void setTipoLosa(String tipoLosa) {
        this.tipoLosa = tipoLosa;
    }

    public CtlgItem getValorCultural() {
        return valorCultural;
    }

    public void setValorCultural(CtlgItem valorCultural) {
        this.valorCultural = valorCultural;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatEdificacionPisosDet> getCatEdificacionPisosDetCollection() {
        return catEdificacionPisosDetCollection;
    }

    public void setCatEdificacionPisosDetCollection(Collection<CatEdificacionPisosDet> catEdificacionPisosDetCollection) {
        this.catEdificacionPisosDetCollection = catEdificacionPisosDetCollection;
    }

    public CtlgItem getUsoConstructivoPiso() {
        return usoConstructivoPiso;
    }

    public void setUsoConstructivoPiso(CtlgItem usoConstructivoPiso) {
        this.usoConstructivoPiso = usoConstructivoPiso;
    }

    public List<NivelModel> getNiveles() {
        return niveles;
    }

    public void setNiveles(List<NivelModel> niveles) {
        this.niveles = niveles;
    }

    public int getCantAlicuotas() {
        return cantAlicuotas;
    }

    public void setCantAlicuotas(int cantAlicuotas) {
        this.cantAlicuotas = cantAlicuotas;
    }

    public BigDecimal getAreaBloque() {
        return areaBloque;
    }

    public void setAreaBloque(BigDecimal areaBloque) {
        this.areaBloque = areaBloque;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Short getEdadConstruccion() {
        return edadConstruccion;
    }

    public void setEdadConstruccion(Short edadConstruccion) {
        this.edadConstruccion = edadConstruccion;
    }

    public Integer getAnioRestaura() {
        return anioRestaura;
    }

    public void setAnioRestaura(Integer anioRestaura) {
        this.anioRestaura = anioRestaura;
    }

    public CtlgItem getCondicionFisica() {
        return condicionFisica;
    }

    public void setCondicionFisica(CtlgItem condicionFisica) {
        this.condicionFisica = condicionFisica;
    }


    public Long getIdDetalleTramite() {
        return idDetalleTramite;
    }

    public void setIdDetalleTramite(Long idDetalleTramite) {
        this.idDetalleTramite = idDetalleTramite;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

}
