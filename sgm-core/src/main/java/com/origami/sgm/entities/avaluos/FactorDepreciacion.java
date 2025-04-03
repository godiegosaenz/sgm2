/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.avaluos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;  import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "factor_depreciacion", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FactorDepreciacion.findAll", query = "SELECT f FROM FactorDepreciacion f")
    , @NamedQuery(name = "FactorDepreciacion.findByFechaModificacion", query = "SELECT f FROM FactorDepreciacion f WHERE f.fechaModificacion = :fechaModificacion")
    , @NamedQuery(name = "FactorDepreciacion.findByUsuarioModificacion", query = "SELECT f FROM FactorDepreciacion f WHERE f.usuarioModificacion = :usuarioModificacion")
    , @NamedQuery(name = "FactorDepreciacion.findByFechaIngreso", query = "SELECT f FROM FactorDepreciacion f WHERE f.fechaIngreso = :fechaIngreso")
    , @NamedQuery(name = "FactorDepreciacion.findByUsuarioIngreso", query = "SELECT f FROM FactorDepreciacion f WHERE f.usuarioIngreso = :usuarioIngreso")
    , @NamedQuery(name = "FactorDepreciacion.findByValor", query = "SELECT f FROM FactorDepreciacion f WHERE f.valor = :valor")
    , @NamedQuery(name = "FactorDepreciacion.findByAnio", query = "SELECT f FROM FactorDepreciacion f WHERE f.anio = :anio")})
public class FactorDepreciacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "fecha_modificacion")
    @Temporal(TemporalType.DATE)
    private Date fechaModificacion;
    @Size(max = 20)
    @Column(name = "usuario_modificacion", length = 20)
    private String usuarioModificacion;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Size(max = 20)
    @Column(name = "usuario_ingreso", length = 20)
    private String usuarioIngreso;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 10, scale = 4)
    private BigDecimal valor;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio", nullable = false)
    private Integer anio;

    public FactorDepreciacion() {
    }

    public FactorDepreciacion(Integer anio) {
        this.anio = anio;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(String usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (anio != null ? anio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FactorDepreciacion)) {
            return false;
        }
        FactorDepreciacion other = (FactorDepreciacion) object;
        if ((this.anio == null && other.anio != null) || (this.anio != null && !this.anio.equals(other.anio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.avaluos.FactorDepreciacion[ anio=" + anio + " ]";
    }
    
}
