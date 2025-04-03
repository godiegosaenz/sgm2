/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatEnte;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "cat_transferencia_detalle")
@NamedQueries({
    @NamedQuery(name = "CatTransferenciaDetalle.findAll", query = "SELECT c FROM CatTransferenciaDetalle c")})
public class CatTransferenciaDetalle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "actual")
    private Boolean actual;
    @Column(name = "observacion")
    private String observacion;
    // AQUI SE GUARDAN LOS PROPIETARIOS QUE SE VAN A ELIMINAR POR LA TRANSFERNCIA DE DOMINIO
    // LOS PROPIETARIOS NUEVOS SON LOS ACTUALES PARA CADA PREDIO
    @JoinColumn(name = "ente", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatEnte ente;
    @JoinColumn(name = "transferencia", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatTransferenciaDominio transferencia;

    public CatTransferenciaDetalle() {
    }

    public CatTransferenciaDetalle(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActual() {
        return actual;
    }

    public void setActual(Boolean actual) {
        this.actual = actual;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public CatTransferenciaDominio getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(CatTransferenciaDominio transferencia) {
        this.transferencia = transferencia;
    }

    public CatEnte getEnte() {
        return ente;
    }

    public void setEnte(CatEnte ente) {
        this.ente = ente;
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
        if (!(object instanceof CatTransferenciaDetalle)) {
            return false;
        }
        CatTransferenciaDetalle other = (CatTransferenciaDetalle) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatTransferenciaDetalle[ id=" + id + " ]";
    }
    
}
