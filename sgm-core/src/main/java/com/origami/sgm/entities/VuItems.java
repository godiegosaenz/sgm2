/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.RegpLiquidacionDerechosAranceles;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "vu_items",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VuItems.findAll", query = "SELECT v FROM VuItems v"),
    @NamedQuery(name = "VuItems.findById", query = "SELECT v FROM VuItems v WHERE v.id = :id"),
    @NamedQuery(name = "VuItems.findByPadreItem", query = "SELECT v FROM VuItems v WHERE v.padreItem = :padreItem"),
    @NamedQuery(name = "VuItems.findByNombre", query = "SELECT v FROM VuItems v WHERE v.nombre = :nombre"),
    @NamedQuery(name = "VuItems.findByClasificacion", query = "SELECT v FROM VuItems v WHERE v.clasificacion = :clasificacion"),
    @NamedQuery(name = "VuItems.findByCodigoCiuu", query = "SELECT v FROM VuItems v WHERE v.codigoCiuu = :codigoCiuu"),
    @NamedQuery(name = "VuItems.findByValorBombero", query = "SELECT v FROM VuItems v WHERE v.valorBombero = :valorBombero")})
public class VuItems implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "padre_item")
    private BigInteger padreItem;
    @Size(max = 200)
    @Column(name = "nombre", length = 200)
    private String nombre;
    @Size(max = 1)
    @Column(name = "clasificacion", length = 1)
    private String clasificacion;
    @Size(max = 10)
    @Column(name = "codigo_ciuu", length = 10)
    private String codigoCiuu;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor_bombero", precision = 6, scale = 2)
    private BigDecimal valorBombero;
    @OneToMany(mappedBy = "tipoServicio", fetch = FetchType.LAZY)
    private Collection<SvSolicitudServicios> svSolicitudServiciosCollection;
    @OneToMany(mappedBy = "lugarAudiencia", fetch = FetchType.LAZY)
    private Collection<SvSolicitudServicios> svSolicitudServiciosCollection1;
    @OneToMany(mappedBy = "usoDocumento", fetch = FetchType.LAZY)
    private Collection<RegpLiquidacionDerechosAranceles> regpLiquidacionDerechosArancelesCollection;
    @JoinColumn(name = "catalogo", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private VuCatalogo catalogo;

    public VuItems() {
    }

    public VuItems(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getPadreItem() {
        return padreItem;
    }

    public void setPadreItem(BigInteger padreItem) {
        this.padreItem = padreItem;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getCodigoCiuu() {
        return codigoCiuu;
    }

    public void setCodigoCiuu(String codigoCiuu) {
        this.codigoCiuu = codigoCiuu;
    }

    public BigDecimal getValorBombero() {
        return valorBombero;
    }

    public void setValorBombero(BigDecimal valorBombero) {
        this.valorBombero = valorBombero;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SvSolicitudServicios> getSvSolicitudServiciosCollection() {
        return svSolicitudServiciosCollection;
    }

    public void setSvSolicitudServiciosCollection(Collection<SvSolicitudServicios> svSolicitudServiciosCollection) {
        this.svSolicitudServiciosCollection = svSolicitudServiciosCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<SvSolicitudServicios> getSvSolicitudServiciosCollection1() {
        return svSolicitudServiciosCollection1;
    }

    public void setSvSolicitudServiciosCollection1(Collection<SvSolicitudServicios> svSolicitudServiciosCollection1) {
        this.svSolicitudServiciosCollection1 = svSolicitudServiciosCollection1;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegpLiquidacionDerechosAranceles> getRegpLiquidacionDerechosArancelesCollection() {
        return regpLiquidacionDerechosArancelesCollection;
    }

    public void setRegpLiquidacionDerechosArancelesCollection(Collection<RegpLiquidacionDerechosAranceles> regpLiquidacionDerechosArancelesCollection) {
        this.regpLiquidacionDerechosArancelesCollection = regpLiquidacionDerechosArancelesCollection;
    }

    public VuCatalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(VuCatalogo catalogo) {
        this.catalogo = catalogo;
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
        if (!(object instanceof VuItems)) {
            return false;
        }
        VuItems other = (VuItems) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.VuItems[ id=" + id + " ]";
    }
    
}
