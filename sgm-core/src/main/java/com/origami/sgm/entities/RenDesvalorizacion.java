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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Angel Navarro
 * @Date 26/04/2016
 */
@Entity
@Table(name = "ren_desvalorizacion", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenDesvalorizacion.findAll", query = "SELECT r FROM RenDesvalorizacion r"),
    @NamedQuery(name = "RenDesvalorizacion.findById", query = "SELECT r FROM RenDesvalorizacion r WHERE r.id = :id"),
    @NamedQuery(name = "RenDesvalorizacion.findByAnio", query = "SELECT r FROM RenDesvalorizacion r WHERE r.anio = :anio"),
    @NamedQuery(name = "RenDesvalorizacion.findByValor", query = "SELECT r FROM RenDesvalorizacion r WHERE r.valor = :valor"),
    @NamedQuery(name = "RenDesvalorizacion.findByUsuarioCreac", query = "SELECT r FROM RenDesvalorizacion r WHERE r.usuarioCreac = :usuarioCreac"),
    @NamedQuery(name = "RenDesvalorizacion.findByFechaCreac", query = "SELECT r FROM RenDesvalorizacion r WHERE r.fechaCreac = :fechaCreac")})
public class RenDesvalorizacion implements Serializable {

    @Column(name = "porcentaje_rebaja")
    private Integer porcentajeRebaja;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio")
    private int anio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 20)
    @Column(name = "usuario_creac")
    private String usuarioCreac;
    @Column(name = "fecha_creac")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreac;
    @Column(name = "estado")
    private Boolean estado;
    @OneToMany(mappedBy = "desvalorizacion")
    private Collection<RenValoresPlusvalia> renValoresPlusvaliaCollection;

    public RenDesvalorizacion() {
    }

    public RenDesvalorizacion(Long id) {
        this.id = id;
    }

    public RenDesvalorizacion(Long id, int anio) {
        this.id = id;
        this.anio = anio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUsuarioCreac() {
        return usuarioCreac;
    }

    public void setUsuarioCreac(String usuarioCreac) {
        this.usuarioCreac = usuarioCreac;
    }

    public Date getFechaCreac() {
        return fechaCreac;
    }

    public void setFechaCreac(Date fechaCreac) {
        this.fechaCreac = fechaCreac;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Collection<RenValoresPlusvalia> getRenValoresPlusvaliaCollection() {
        return renValoresPlusvaliaCollection;
    }

    public void setRenValoresPlusvaliaCollection(Collection<RenValoresPlusvalia> renValoresPlusvaliaCollection) {
        this.renValoresPlusvaliaCollection = renValoresPlusvaliaCollection;
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
        if (!(object instanceof RenDesvalorizacion)) {
            return false;
        }
        RenDesvalorizacion other = (RenDesvalorizacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenDesvalorizacion[ id=" + id + " ]";
    }

    public Integer getPorcentajeRebaja() {
        return porcentajeRebaja;
    }

    public void setPorcentajeRebaja(Integer porcentajeRebaja) {
        this.porcentajeRebaja = porcentajeRebaja;
    }

}
