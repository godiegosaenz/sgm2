/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_tabla_depreciacion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatTablaDepreciacion.findAll", query = "SELECT c FROM CatTablaDepreciacion c"),
    @NamedQuery(name = "CatTablaDepreciacion.findById", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.id = :id"),
    @NamedQuery(name = "CatTablaDepreciacion.findByVidautildesde", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.vidautildesde = :vidautildesde"),
    @NamedQuery(name = "CatTablaDepreciacion.findByVidautilhasta", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.vidautilhasta = :vidautilhasta"),
    @NamedQuery(name = "CatTablaDepreciacion.findByAnios", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.anios = :anios"),
    @NamedQuery(name = "CatTablaDepreciacion.findByNuevo", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.nuevo = :nuevo"),
    @NamedQuery(name = "CatTablaDepreciacion.findByBueno", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.bueno = :bueno"),
    @NamedQuery(name = "CatTablaDepreciacion.findByRegular", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.regular = :regular"),
    @NamedQuery(name = "CatTablaDepreciacion.findByMalo", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.malo = :malo"),
    @NamedQuery(name = "CatTablaDepreciacion.findByObsoleto", query = "SELECT c FROM CatTablaDepreciacion c WHERE c.obsoleto = :obsoleto")})
public class CatTablaDepreciacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "vidautildesde")
    private Integer vidautildesde;
    @Column(name = "vidautilhasta")
    private Integer vidautilhasta;
    @Column(name = "anios")
    private Integer anios;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "nuevo", precision = 10, scale = 4)
    private BigDecimal nuevo;
    @Column(name = "bueno", precision = 10, scale = 4)
    private BigDecimal bueno;
    @Column(name = "regular", precision = 10, scale = 4)
    private BigDecimal regular;
    @Column(name = "malo", precision = 10, scale = 4)
    private BigDecimal malo;
    @Column(name = "obsoleto", precision = 10, scale = 4)
    private BigDecimal obsoleto;

    public CatTablaDepreciacion() {
    }

    public CatTablaDepreciacion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVidautildesde() {
        return vidautildesde;
    }

    public void setVidautildesde(Integer vidautildesde) {
        this.vidautildesde = vidautildesde;
    }

    public Integer getVidautilhasta() {
        return vidautilhasta;
    }

    public void setVidautilhasta(Integer vidautilhasta) {
        this.vidautilhasta = vidautilhasta;
    }

    public Integer getAnios() {
        return anios;
    }

    public void setAnios(Integer anios) {
        this.anios = anios;
    }

    public BigDecimal getNuevo() {
        return nuevo;
    }

    public void setNuevo(BigDecimal nuevo) {
        this.nuevo = nuevo;
    }

    public BigDecimal getBueno() {
        return bueno;
    }

    public void setBueno(BigDecimal bueno) {
        this.bueno = bueno;
    }

    public BigDecimal getRegular() {
        return regular;
    }

    public void setRegular(BigDecimal regular) {
        this.regular = regular;
    }

    public BigDecimal getMalo() {
        return malo;
    }

    public void setMalo(BigDecimal malo) {
        this.malo = malo;
    }

    public BigDecimal getObsoleto() {
        return obsoleto;
    }

    public void setObsoleto(BigDecimal obsoleto) {
        this.obsoleto = obsoleto;
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
        if (!(object instanceof CatTablaDepreciacion)) {
            return false;
        }
        CatTablaDepreciacion other = (CatTablaDepreciacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.CatTablaDepreciacion[ id=" + id + " ]";
    }
    
}
