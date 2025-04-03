/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.google.gson.annotations.Expose;
import com.origami.sgm.entities.UtilsEnts;
import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
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
@Table(name = "cat_canton", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatCanton.findAll", query = "SELECT c FROM CatCanton c")
    ,
    @NamedQuery(name = "CatCanton.findById", query = "SELECT c FROM CatCanton c WHERE c.id = :id")
    ,
    @NamedQuery(name = "CatCanton.findByNombre", query = "SELECT c FROM CatCanton c WHERE c.nombre = :nombre")
    ,
    @NamedQuery(name = "CatCanton.findByCodigoNacional", query = "SELECT c FROM CatCanton c WHERE c.codigoNacional = :codigoNacional")
    ,
    @NamedQuery(name = "CatCanton.findByIdCantonRegistro", query = "SELECT c FROM CatCanton c WHERE c.idCantonRegistro = :idCantonRegistro")})
public class CatCanton implements Serializable {

    @Column(name = "id_sire")
    private BigInteger idSire;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)
    @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1 + "." + SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    @Expose
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "nombre", nullable = false, length = 80)
    @Expose
    private String nombre;
    @Column(name = "estado")
    private Boolean estado = true;
    @Column(name = "codigo_nacional")
    @Expose
    private BigInteger codigoNacional;
    @Column(name = "id_canton_registro")
    private BigInteger idCantonRegistro;
    @Column(name = "cod_nac")
    @Expose
    private Short codNac;
    @OneToMany(mappedBy = "canton", fetch = FetchType.LAZY)
    private Collection<CatEscritura> catEscrituraCollection;
    @OneToMany(mappedBy = "canton", fetch = FetchType.LAZY)
    private Collection<CatPredioS6> catPredioS6Collection;
    @JoinColumn(name = "id_provincia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatProvincia idProvincia;
    @OneToMany(mappedBy = "codigoCan", fetch = FetchType.LAZY)
    private Collection<RegMovimiento> regMovimientoCollection;
    @OneToMany(mappedBy = "canton", fetch = FetchType.LAZY)
    private Collection<RegEscritura> regEscrituraCollection;
    @OneToMany(mappedBy = "idCanton", fetch = FetchType.LAZY)
    @Expose
    private Collection<CatParroquia> catParroquiaCollection;

    public CatCanton() {
    }

    public CatCanton(Long id) {
        this.id = id;
    }

    public CatCanton(Long id, String nombre) {
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public BigInteger getCodigoNacional() {
        return codigoNacional;
    }

    public void setCodigoNacional(BigInteger codigoNacional) {
        this.codigoNacional = codigoNacional;
    }

    public BigInteger getIdCantonRegistro() {
        return idCantonRegistro;
    }

    public void setIdCantonRegistro(BigInteger idCantonRegistro) {
        this.idCantonRegistro = idCantonRegistro;
    }

    public Short getCodNac() {
        return codNac;
    }

    public void setCodNac(Short codNac) {
        this.codNac = codNac;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatEscritura> getCatEscrituraCollection() {
        return catEscrituraCollection;
    }

    public void setCatEscrituraCollection(Collection<CatEscritura> catEscrituraCollection) {
        this.catEscrituraCollection = catEscrituraCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatPredioS6> getCatPredioS6Collection() {
        return catPredioS6Collection;
    }

    public void setCatPredioS6Collection(Collection<CatPredioS6> catPredioS6Collection) {
        this.catPredioS6Collection = catPredioS6Collection;
    }

    public CatProvincia getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(CatProvincia idProvincia) {
        this.idProvincia = idProvincia;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegMovimiento> getRegMovimientoCollection() {
        return regMovimientoCollection;
    }

    public void setRegMovimientoCollection(Collection<RegMovimiento> regMovimientoCollection) {
        this.regMovimientoCollection = regMovimientoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegEscritura> getRegEscrituraCollection() {
        return regEscrituraCollection;
    }

    public void setRegEscrituraCollection(Collection<RegEscritura> regEscrituraCollection) {
        this.regEscrituraCollection = regEscrituraCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatParroquia> getCatParroquiaCollection() {
        return catParroquiaCollection;
    }

    public void setCatParroquiaCollection(Collection<CatParroquia> catParroquiaCollection) {
        this.catParroquiaCollection = catParroquiaCollection;
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
        if (!(object instanceof CatCanton)) {
            return false;
        }
        CatCanton other = (CatCanton) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatCanton[ id=" + id + " ]";
    }

    public BigInteger getIdSire() {
        return idSire;
    }

    public void setIdSire(BigInteger idSire) {
        this.idSire = idSire;
    }

    public String cantoncodNac() {
        if (idProvincia != null && this.codNac != null) {
            String retornar =  UtilsEnts.completarCadenaConCeros(idProvincia.getCodNac().toString(), 2)
                    +  UtilsEnts.completarCadenaConCeros(this.codNac.toString(), 2);

            return retornar;
        } else if (idProvincia != null && this.codNac == null) {
            return UtilsEnts.completarCadenaConCeros(idProvincia.getCodNac().toString(), 2) + "00";
        }
        return "0000";
    }
}
