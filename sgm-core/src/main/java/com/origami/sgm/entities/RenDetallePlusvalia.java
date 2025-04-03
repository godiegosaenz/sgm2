/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.origami.sgm.entities;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Angel Navarro
 * @Date 07/07/2016
 */
@Entity
@Table(name = "ren_detalle_plusvalia", schema = SchemasConfig.FINANCIERO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RenDetallePlusvalia.findAll", query = "SELECT r FROM RenDetallePlusvalia r"),
    @NamedQuery(name = "RenDetallePlusvalia.findById", query = "SELECT r FROM RenDetallePlusvalia r WHERE r.id = :id"),
    @NamedQuery(name = "RenDetallePlusvalia.findByPrediosAsociados", query = "SELECT r FROM RenDetallePlusvalia r WHERE r.prediosAsociados = :prediosAsociados")})
public class RenDetallePlusvalia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @JoinColumn(name = "predios_asociados", referencedColumnName = "id")
    @ManyToOne
    private CatPredio prediosAsociados;
    @JoinColumn(name = "valores_plusvalia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RenValoresPlusvalia valoresPlusvalia;

    public RenDetallePlusvalia() {
    }

    public RenDetallePlusvalia(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatPredio getPrediosAsociados() {
        return prediosAsociados;
    }

    public void setPrediosAsociados(CatPredio prediosAsociados) {
        this.prediosAsociados = prediosAsociados;
    }

    public RenValoresPlusvalia getValoresPlusvalia() {
        return valoresPlusvalia;
    }

    public void setValoresPlusvalia(RenValoresPlusvalia valoresPlusvalia) {
        this.valoresPlusvalia = valoresPlusvalia;
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
        if (!(object instanceof RenDetallePlusvalia)) {
            return false;
        }
        RenDetallePlusvalia other = (RenDetallePlusvalia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.RenDetallePlusvalia[ id=" + id + " ]";
    }

}
