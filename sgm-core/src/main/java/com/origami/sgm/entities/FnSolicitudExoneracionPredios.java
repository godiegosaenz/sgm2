/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredioRustico;
import com.origami.sgm.entities.CatPredio;
import java.io.Serializable;
import java.math.BigInteger;
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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author HenryPilco
 */
@Entity
@Table(name = "fn_solicitud_exoneracion_predios", schema = SchemasConfig.FINANCIERO)
@NamedQueries({
    @NamedQuery(name = "FnSolicitudExoneracionPredios.findAll", query = "SELECT f FROM FnSolicitudExoneracionPredios f")})
public class FnSolicitudExoneracionPredios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "solicitud_exoneracion", referencedColumnName = "id")
    @ManyToOne
    private FnSolicitudExoneracion solicitudExoneracion;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "predio_rural", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredioRustico predioRural;
//    @JoinColumn(name = "predio_rural_2017", referencedColumnName = "id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private EmisionesRuralesExcel predioRural2017;

    public FnSolicitudExoneracionPredios() {
    }

    public FnSolicitudExoneracionPredios(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FnSolicitudExoneracion getSolicitudExoneracion() {
        return solicitudExoneracion;
    }

    public void setSolicitudExoneracion(FnSolicitudExoneracion solicitudExoneracion) {
        this.solicitudExoneracion = solicitudExoneracion;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatPredioRustico getPredioRural() {
        return predioRural;
    }

    public void setPredioRural(CatPredioRustico predioRural) {
        this.predioRural = predioRural;
    }

//    public EmisionesRuralesExcel getPredioRural2017() {
//        return predioRural2017;
//    }
//
//    public void setPredioRural2017(EmisionesRuralesExcel predioRural2017) {
//        this.predioRural2017 = predioRural2017;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FnSolicitudExoneracionPredios)) {
            return false;
        }
        FnSolicitudExoneracionPredios other = (FnSolicitudExoneracionPredios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.FnSolicitudExoneracionPredios[ id=" + id + " ]";
    }
    
}
