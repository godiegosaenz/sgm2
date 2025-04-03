/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatTiposDominio;
import com.origami.sgm.entities.CatCanton;
import com.origami.sgm.entities.AclUser;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_escritura",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegEscritura.findAll", query = "SELECT r FROM RegEscritura r"),
    @NamedQuery(name = "RegEscritura.findById", query = "SELECT r FROM RegEscritura r WHERE r.id = :id"),
    @NamedQuery(name = "RegEscritura.findByNotaria", query = "SELECT r FROM RegEscritura r WHERE r.notaria = :notaria"),
    @NamedQuery(name = "RegEscritura.findByFecEscritura", query = "SELECT r FROM RegEscritura r WHERE r.fecEscritura = :fecEscritura"),
    @NamedQuery(name = "RegEscritura.findByFecInscripcion", query = "SELECT r FROM RegEscritura r WHERE r.fecInscripcion = :fecInscripcion"),
    @NamedQuery(name = "RegEscritura.findByNumRepertorio", query = "SELECT r FROM RegEscritura r WHERE r.numRepertorio = :numRepertorio"),
    @NamedQuery(name = "RegEscritura.findByNumRegistro", query = "SELECT r FROM RegEscritura r WHERE r.numRegistro = :numRegistro"),
    @NamedQuery(name = "RegEscritura.findByFolioDesde", query = "SELECT r FROM RegEscritura r WHERE r.folioDesde = :folioDesde"),
    @NamedQuery(name = "RegEscritura.findByFolioHasta", query = "SELECT r FROM RegEscritura r WHERE r.folioHasta = :folioHasta")})
public class RegEscritura implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "notaria")
    private BigInteger notaria;
    @Column(name = "fec_escritura")
    @Temporal(TemporalType.DATE)
    private Date fecEscritura;
    @Column(name = "fec_inscripcion")
    @Temporal(TemporalType.DATE)
    private Date fecInscripcion;
    @Column(name = "num_repertorio")
    private BigInteger numRepertorio;
    @Column(name = "num_registro")
    private BigInteger numRegistro;
    @Size(max = 20)
    @Column(name = "folio_desde", length = 20)
    private String folioDesde;
    @Size(max = 20)
    @Column(name = "folio_hasta", length = 20)
    private String folioHasta;
    @JoinColumn(name = "trasldom", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatTiposDominio trasldom;
    @JoinColumn(name = "canton", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCanton canton;
    @JoinColumn(name = "user_creacion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AclUser userCreacion;
    @OneToMany(mappedBy = "escrituraReg", fetch = FetchType.LAZY)
    private Collection<RegPredioUrbano> regPredioUrbanoCollection;
    @OneToMany(mappedBy = "escrituraReg", fetch = FetchType.LAZY)
    private Collection<RegEnteRegescritura> regEnteRegescrituraCollection;

    public RegEscritura() {
    }

    public RegEscritura(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getNotaria() {
        return notaria;
    }

    public void setNotaria(BigInteger notaria) {
        this.notaria = notaria;
    }

    public Date getFecEscritura() {
        return fecEscritura;
    }

    public void setFecEscritura(Date fecEscritura) {
        this.fecEscritura = fecEscritura;
    }

    public Date getFecInscripcion() {
        return fecInscripcion;
    }

    public void setFecInscripcion(Date fecInscripcion) {
        this.fecInscripcion = fecInscripcion;
    }

    public BigInteger getNumRepertorio() {
        return numRepertorio;
    }

    public void setNumRepertorio(BigInteger numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    public BigInteger getNumRegistro() {
        return numRegistro;
    }

    public void setNumRegistro(BigInteger numRegistro) {
        this.numRegistro = numRegistro;
    }

    public String getFolioDesde() {
        return folioDesde;
    }

    public void setFolioDesde(String folioDesde) {
        this.folioDesde = folioDesde;
    }

    public String getFolioHasta() {
        return folioHasta;
    }

    public void setFolioHasta(String folioHasta) {
        this.folioHasta = folioHasta;
    }

    public CatTiposDominio getTrasldom() {
        return trasldom;
    }

    public void setTrasldom(CatTiposDominio trasldom) {
        this.trasldom = trasldom;
    }

    public CatCanton getCanton() {
        return canton;
    }

    public void setCanton(CatCanton canton) {
        this.canton = canton;
    }

    public AclUser getUserCreacion() {
        return userCreacion;
    }

    public void setUserCreacion(AclUser userCreacion) {
        this.userCreacion = userCreacion;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegPredioUrbano> getRegPredioUrbanoCollection() {
        return regPredioUrbanoCollection;
    }

    public void setRegPredioUrbanoCollection(Collection<RegPredioUrbano> regPredioUrbanoCollection) {
        this.regPredioUrbanoCollection = regPredioUrbanoCollection;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegEnteRegescritura> getRegEnteRegescrituraCollection() {
        return regEnteRegescrituraCollection;
    }

    public void setRegEnteRegescrituraCollection(Collection<RegEnteRegescritura> regEnteRegescrituraCollection) {
        this.regEnteRegescrituraCollection = regEnteRegescrituraCollection;
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
        if (!(object instanceof RegEscritura)) {
            return false;
        }
        RegEscritura other = (RegEscritura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegEscritura[ id=" + id + " ]";
    }
    
}
