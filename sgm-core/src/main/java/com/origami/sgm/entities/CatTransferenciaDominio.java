/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatPredio;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "cat_transferencia_dominio")
@NamedQueries({
    @NamedQuery(name = "CatTransferenciaDominio.findAll", query = "SELECT c FROM CatTransferenciaDominio c")})
public class CatTransferenciaDominio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "movimiento", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RegMovimiento movimiento;
    @JoinColumn(name = "predio", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatPredio predio;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transferencia", fetch = FetchType.LAZY)
    private Collection<CatTransferenciaDetalle> catTransferenciaDetalleCollection;

    @Column(name = "usuario")
    private String usuario;
    @Column(name = "task_id")
    private String taskId;
    @Column(name = "num_tramite")
    private Long numTramite;
    @JoinColumn(name = "tarea_registro", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private RegpCertificadosInscripciones tareaRegistro;
    
    public CatTransferenciaDominio() {
    }

    public CatTransferenciaDominio(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public RegMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(RegMovimiento movimiento) {
        this.movimiento = movimiento;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getNumTramite() {
        return numTramite;
    }

    public void setNumTramite(Long numTramite) {
        this.numTramite = numTramite;
    }

    public RegpCertificadosInscripciones getTareaRegistro() {
        return tareaRegistro;
    }

    public void setTareaRegistro(RegpCertificadosInscripciones tareaRegistro) {
        this.tareaRegistro = tareaRegistro;
    }

    public Collection<CatTransferenciaDetalle> getCatTransferenciaDetalleCollection() {
        return catTransferenciaDetalleCollection;
    }

    public void setCatTransferenciaDetalleCollection(Collection<CatTransferenciaDetalle> catTransferenciaDetalleCollection) {
        this.catTransferenciaDetalleCollection = catTransferenciaDetalleCollection;
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
        if (!(object instanceof CatTransferenciaDominio)) {
            return false;
        }
        CatTransferenciaDominio other = (CatTransferenciaDominio) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatTransferenciaDominio[ id=" + id + " ]";
    }
    
}
