/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author XndySxnchez :v
 */
@Entity
@Table(name = "aval_impuesto_predios", schema = SchemasConfig.APP1) 
@XmlRootElement
public class AvalImpuestoPredios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parroquia", nullable = false)
    private short parroquia;
    @Basic(optional = false)
    @NotNull
    @Column(name = "zona", nullable = false)
    private short zona;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sector", nullable = false)
    private short sector;
    @Column(name = "solar", nullable = false)
    private short solar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mz", nullable = false)
    private short mz;
    @Column(name = "anio_inicio")
    private Integer anioInicio;
    @Column(name = "anio_fin")
    private Integer anioFin;
    @JoinColumn(name = "banda_impositiva", referencedColumnName = "id")
    @ManyToOne
    private AvalBandaImpositiva bandaImpositiva;
    @Column(name = "estado")
    private String estado;
    @OneToMany(mappedBy = "idAvalImpuestoPredio", fetch = FetchType.LAZY)
    private List<AvalDetCobroImpuestoPredios> avalDetCobroImpuestoPredios;
    

    public AvalImpuestoPredios() {
    }

    public AvalImpuestoPredios(Long id) {
        this.id = id;
    }

    public AvalImpuestoPredios(Long id, short parroquia, short zona, short sector, short mz) {
        this.id = id;
        this.parroquia = parroquia;
        this.zona = zona;
        this.sector = sector;
        this.mz = mz;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public short getParroquia() {
        return parroquia;
    }

    public void setParroquia(short parroquia) {
        this.parroquia = parroquia;
    }

    public short getZona() {
        return zona;
    }

    public void setZona(short zona) {
        this.zona = zona;
    }

    public short getSector() {
        return sector;
    }

    public void setSector(short sector) {
        this.sector = sector;
    }

    public short getMz() {
        return mz;
    }

    public void setMz(short mz) {
        this.mz = mz;
    }

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
    }

    public AvalBandaImpositiva getBandaImpositiva() {
        return bandaImpositiva;
    }

    public void setBandaImpositiva(AvalBandaImpositiva bandaImpositiva) {
        this.bandaImpositiva = bandaImpositiva;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public short getSolar() {
        return solar;
    }

    public void setSolar(short solar) {
        this.solar = solar;
    }

    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AvalImpuestoPredios)) {
            return false;
        }
        AvalImpuestoPredios other = (AvalImpuestoPredios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AvalImpuestoPredios{" + "id=" + id + ", parroquia=" + parroquia + ", zona=" + zona + ", sector=" + sector + ", mz=" + mz + ", anioInicio=" + anioInicio + ", anioFin=" + anioFin + ", bandaImpositiva=" + bandaImpositiva + '}';
    }

    public List<AvalDetCobroImpuestoPredios> getAvalDetCobroImpuestoPredios() {
        return avalDetCobroImpuestoPredios;
    }

    public void setAvalDetCobroImpuestoPredios(List<AvalDetCobroImpuestoPredios> avalDetCobroImpuestoPredios) {
        this.avalDetCobroImpuestoPredios = avalDetCobroImpuestoPredios;
    }

}
