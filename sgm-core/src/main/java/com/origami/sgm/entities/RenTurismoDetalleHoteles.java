/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 * @date 14/09/2016
 */
@Entity
@Table(name = "ren_turismo_detalle_hoteles", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenTurismoDetalleHoteles.findAll", query = "SELECT r FROM RenTurismoDetalleHoteles r"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findById", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.id = :id"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByCbp", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.cbp = :cbp"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByBc", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.bc = :bc"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByTotal", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.total = :total"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByCamas", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.camas = :camas"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByPlazas", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.plazas = :plazas"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByTvc", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.tvc = :tvc"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByNev", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.nev = :nev"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByAire", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.aire = :aire"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByTelf", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.telf = :telf"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findBySecad", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.secad = :secad"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByMusica", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.musica = :musica"),
    @NamedQuery(name = "RenTurismoDetalleHoteles.findByEstado", query = "SELECT r FROM RenTurismoDetalleHoteles r WHERE r.estado = :estado")})
public class RenTurismoDetalleHoteles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "cbp")
    private Integer cbp;
    @Column(name = "bc")
    private Integer bc;
    @Column(name = "total")
    private Integer total;
    @Column(name = "camas")
    private Integer camas;
    @Column(name = "plazas")
    private Integer plazas;
    @Column(name = "tvc")
    private Integer tvc;
    @Column(name = "nev")
    private Integer nev;
    @Column(name = "aire")
    private Integer aire;
    @Column(name = "telf")
    private Integer telf;
    @Column(name = "secad")
    private Integer secad;
    @Column(name = "musica")
    private Integer musica;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "tipo_habitacion", referencedColumnName = "id")
    @ManyToOne
    private RenTurismoServicios tipoHabitacion;
    @JoinColumn(name = "turismo", referencedColumnName = "id")
    @ManyToOne
    private RenTurismo turismo;

    public RenTurismoDetalleHoteles() {
    }

    public RenTurismoDetalleHoteles(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCbp() {
        return cbp;
    }

    public void setCbp(Integer cbp) {
        this.cbp = cbp;
    }

    public Integer getBc() {
        return bc;
    }

    public void setBc(Integer bc) {
        this.bc = bc;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCamas() {
        return camas;
    }

    public void setCamas(Integer camas) {
        this.camas = camas;
    }

    public Integer getPlazas() {
        return plazas;
    }

    public void setPlazas(Integer plazas) {
        this.plazas = plazas;
    }

    public Integer getTvc() {
        return tvc;
    }

    public void setTvc(Integer tvc) {
        this.tvc = tvc;
    }

    public Integer getNev() {
        return nev;
    }

    public void setNev(Integer nev) {
        this.nev = nev;
    }

    public Integer getAire() {
        return aire;
    }

    public void setAire(Integer aire) {
        this.aire = aire;
    }

    public Integer getTelf() {
        return telf;
    }

    public void setTelf(Integer telf) {
        this.telf = telf;
    }

    public Integer getSecad() {
        return secad;
    }

    public void setSecad(Integer secad) {
        this.secad = secad;
    }

    public Integer getMusica() {
        return musica;
    }

    public void setMusica(Integer musica) {
        this.musica = musica;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public RenTurismoServicios getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(RenTurismoServicios tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public RenTurismo getTurismo() {
        return turismo;
    }

    public void setTurismo(RenTurismo turismo) {
        this.turismo = turismo;
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
        if (!(object instanceof RenTurismoDetalleHoteles)) {
            return false;
        }
        RenTurismoDetalleHoteles other = (RenTurismoDetalleHoteles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenTurismoDetalleHoteles[ id=" + id + " ]";
    }

}
