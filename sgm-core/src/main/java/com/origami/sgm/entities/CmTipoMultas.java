/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
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
 * @author Angel Navarro
 * @date 05/09/2016
 */
@Entity
@Table(name = "cm_tipo_multas", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CmTipoMultas.findAll", query = "SELECT c FROM CmTipoMultas c"),
    @NamedQuery(name = "CmTipoMultas.findById", query = "SELECT c FROM CmTipoMultas c WHERE c.id = :id"),
    @NamedQuery(name = "CmTipoMultas.findByDescripcion", query = "SELECT c FROM CmTipoMultas c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "CmTipoMultas.findByValor", query = "SELECT c FROM CmTipoMultas c WHERE c.valor = :valor"),
    @NamedQuery(name = "CmTipoMultas.findByUsuarioIngreso", query = "SELECT c FROM CmTipoMultas c WHERE c.usuarioIngreso = :usuarioIngreso"),
    @NamedQuery(name = "CmTipoMultas.findByFechaIngreso", query = "SELECT c FROM CmTipoMultas c WHERE c.fechaIngreso = :fechaIngreso")})
public class CmTipoMultas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor", precision = 19, scale = 2)
    private BigDecimal valor;
    @Size(max = 25)
    @Column(name = "usuario_ingreso", length = 25)
    private String usuarioIngreso;
    @Column(name = "fecha_ingreso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaIngreso;
    
    @JoinColumn(name = "clase_multa", referencedColumnName = "id")
    @ManyToOne
    private CmClaseMultas claseMulta;
    @Column(name = "es_porcentaje")
    private Boolean esPorcentaje;
    
    public CmTipoMultas() {
    }

    public CmTipoMultas(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUsuarioIngreso() {
        return usuarioIngreso;
    }

    public void setUsuarioIngreso(String usuarioIngreso) {
        this.usuarioIngreso = usuarioIngreso;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public CmClaseMultas getClaseMulta() {
        return claseMulta;
    }

    public void setClaseMulta(CmClaseMultas claseMulta) {
        this.claseMulta = claseMulta;
    }
    
    public Boolean getEsPorcentaje() {
        return esPorcentaje;
    }

    public void setEsPorcentaje(Boolean esPorcentaje) {
        this.esPorcentaje = esPorcentaje;
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
        if (!(object instanceof CmTipoMultas)) {
            return false;
        }
        CmTipoMultas other = (CmTipoMultas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CmTipoMultas[ id=" + id + " ]";
    }

}
