/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.avaluos;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "rangos_depreciacion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RangosDepreciacion.findAll", query = "SELECT r FROM RangosDepreciacion r")
    , @NamedQuery(name = "RangosDepreciacion.findById", query = "SELECT r FROM RangosDepreciacion r WHERE r.id = :id")
    , @NamedQuery(name = "RangosDepreciacion.findByVidautildesde", query = "SELECT r FROM RangosDepreciacion r WHERE r.vidautildesde = :vidautildesde")
    , @NamedQuery(name = "RangosDepreciacion.findByVidautilhasta", query = "SELECT r FROM RangosDepreciacion r WHERE r.vidautilhasta = :vidautilhasta")
    , @NamedQuery(name = "RangosDepreciacion.findByAnios", query = "SELECT r FROM RangosDepreciacion r WHERE r.anios = :anios")
    , @NamedQuery(name = "RangosDepreciacion.findByNuevo", query = "SELECT r FROM RangosDepreciacion r WHERE r.nuevo = :nuevo")
    , @NamedQuery(name = "RangosDepreciacion.findByBueno", query = "SELECT r FROM RangosDepreciacion r WHERE r.bueno = :bueno")
    , @NamedQuery(name = "RangosDepreciacion.findByRegular", query = "SELECT r FROM RangosDepreciacion r WHERE r.regular = :regular")
    , @NamedQuery(name = "RangosDepreciacion.findByMalo", query = "SELECT r FROM RangosDepreciacion r WHERE r.malo = :malo")
    , @NamedQuery(name = "RangosDepreciacion.findByObsoleto", query = "SELECT r FROM RangosDepreciacion r WHERE r.obsoleto = :obsoleto")})
public class RangosDepreciacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "vidautildesde")
    private Integer vidautildesde;
    @Column(name = "vidautilhasta")
    private Integer vidautilhasta;
    @Column(name = "anios")
    private Integer anios;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "nuevo", precision = 12, scale = 4)
    private BigDecimal nuevo;
    @Column(name = "bueno", precision = 12, scale = 4)
    private BigDecimal bueno;
    @Column(name = "regular", precision = 12, scale = 4)
    private BigDecimal regular;
    @Column(name = "malo", precision = 12, scale = 4)
    private BigDecimal malo;
    @Column(name = "obsoleto", precision = 12, scale = 4)
    private BigDecimal obsoleto;

    public RangosDepreciacion() {
    }

    public RangosDepreciacion(Long id) {
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
        if (!(object instanceof RangosDepreciacion)) {
            return false;
        }
        RangosDepreciacion other = (RangosDepreciacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.avaluos.RangosDepreciacion[ id=" + id + " ]";
    }
    
}
