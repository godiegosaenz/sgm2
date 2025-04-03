/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
import com.origami.sgm.entities.CatCanton;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_movimiento_cliente", schema = SchemasConfig.APP1)
public class RegMovimientoCliente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "cedula")
    private String cedula;
    @Column(name = "nombres")
    private String nombres;
    @Size(max = 2147483647)
    @Column(name = "estado")
    private String estado;
    @JoinColumn(name = "movimiento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegMovimiento movimiento;
    @JoinColumn(name = "ente_interv", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegEnteInterviniente enteInterv;
    @JoinColumn(name = "papel", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegCatPapel papel;
    @JoinColumn(name = "ente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte ente;
    @JoinColumn(name = "domicilio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatCanton domicilio;

    @Transient
    private Boolean sociosSeleccionados = false;
    @Transient
    private Boolean tieneCapital = false;
    @Transient
    private Boolean representanteSeleccionados = false;
    @Transient
    private Boolean propietario = false;

    public RegMovimientoCliente() {
    }

    public RegMovimientoCliente(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public RegEnteInterviniente getEnteInterv() {
        return enteInterv;
    }

    public void setEnteInterv(RegEnteInterviniente enteInterv) {
        this.enteInterv = enteInterv;
    }

    public RegCatPapel getPapel() {
        return papel;
    }

    public void setPapel(RegCatPapel papel) {
        this.papel = papel;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
    }

    public CatCanton getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(CatCanton domicilio) {
        this.domicilio = domicilio;
    }

    public Boolean getSociosSeleccionados() {
        return sociosSeleccionados;
    }

    public void setSociosSeleccionados(Boolean sociosSeleccionados) {
        this.sociosSeleccionados = sociosSeleccionados;
    }

    public Boolean getTieneCapital() {
        return tieneCapital;
    }

    public void setTieneCapital(Boolean tieneCapital) {
        this.tieneCapital = tieneCapital;
    }

    public Boolean getRepresentanteSeleccionados() {
        return representanteSeleccionados;
    }

    public void setRepresentanteSeleccionados(Boolean representanteSeleccionados) {
        this.representanteSeleccionados = representanteSeleccionados;
    }

    public Boolean getPropietario() {
        return propietario;
    }

    public void setPropietario(Boolean propietario) {
        this.propietario = propietario;
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
        if (!(object instanceof RegMovimientoCliente)) {
            return false;
        }
        RegMovimientoCliente other = (RegMovimientoCliente) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegMovimientoCliente[ id=" + id + " ]";
    }

}
