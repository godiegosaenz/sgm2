/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities.historic;

import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.GeDocumentos;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
  import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
 import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
@Table(name = "predio", schema = SchemasConfig.HISTORICO)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Predio.findAll", query = "SELECT p FROM Predio p"),
    @NamedQuery(name = "Predio.findById", query = "SELECT p FROM Predio p WHERE p.id = :id"),
    @NamedQuery(name = "Predio.findByPredio", query = "SELECT p FROM Predio p WHERE p.predio = :predio"),
    @NamedQuery(name = "Predio.findByFichaAnt", query = "SELECT p FROM Predio p WHERE p.fichaAnt = :fichaAnt"),
    @NamedQuery(name = "Predio.findByFichaAct", query = "SELECT p FROM Predio p WHERE p.fichaAct = :fichaAct"),
    @NamedQuery(name = "Predio.findByFecCre", query = "SELECT p FROM Predio p WHERE p.fecCre = :fecCre"),
    @NamedQuery(name = "Predio.findByFecAct", query = "SELECT p FROM Predio p WHERE p.fecAct = :fecAct"),
    @NamedQuery(name = "Predio.findByUsuario", query = "SELECT p FROM Predio p WHERE p.usuario = :usuario"),
    @NamedQuery(name = "Predio.findByObservacion", query = "SELECT p FROM Predio p WHERE p.observacion = :observacion")})
public class Predio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "predio", nullable = false)
    private long predio;
    @Column(name = "ficha_ant", columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String fichaAnt;
    @Column(name = "ficha_act", columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String fichaAct;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Size(max = 100)
    @Column(name = "usuario", length = 100)
    private String usuario;
    @Column(name = "observacion")
    private String observacion;
    @Size(max = 2147483647)
    @Column(name = "ficha_edificacion_ant", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String fichaEdificacionAnt;
    @Size(max = 2147483647)
    @Column(name = "ficha_edificacion_act", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String fichaEdificacionAct;
    @Size(max = 2147483647)
    @Column(name = "ficha_model_ant", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String fichaModelAnt;
    @Size(max = 2147483647)
    @Column(name = "ficha_model_act", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String fichaModelAct;
    @Column(name = "migrado")
    private Boolean migrado;
    @JoinColumn(name = "ge_documento", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GeDocumentos geDocumento;
    
    @Column(name = "cambios", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String cambios;
    
    
    public Predio() {
    }

    public Predio(Long id) {
        this.id = id;
    }

    public Predio(Long id, long predio) {
        this.id = id;
        this.predio = predio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPredio() {
        return predio;
    }

    public void setPredio(long predio) {
        this.predio = predio;
    }

    public String getFichaAnt() {
        return fichaAnt;
    }

    public void setFichaAnt(String fichaAnt) {
        this.fichaAnt = fichaAnt;
    }

    public String getFichaAct() {
        return fichaAct;
    }

    public void setFichaAct(String fichaAct) {
        this.fichaAct = fichaAct;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public Date getFecAct() {
        return fecAct;
    }

    public void setFecAct(Date fecAct) {
        this.fecAct = fecAct;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFichaEdificacionAnt() {
        return fichaEdificacionAnt;
    }

    public void setFichaEdificacionAnt(String fichaEdificacionAnt) {
        this.fichaEdificacionAnt = fichaEdificacionAnt;
    }

    public String getFichaEdificacionAct() {
        return fichaEdificacionAct;
    }

    public void setFichaEdificacionAct(String fichaEdificacionAct) {
        this.fichaEdificacionAct = fichaEdificacionAct;
    }

    public Boolean getMigrado() {
        return migrado;
    }

    public void setMigrado(Boolean migrado) {
        this.migrado = migrado;
    }

    public String getFichaModelAnt() {
        return fichaModelAnt;
    }

    public void setFichaModelAnt(String fichaModelAnt) {
        this.fichaModelAnt = fichaModelAnt;
    }

    public String getFichaModelAct() {
        return fichaModelAct;
    }

    public void setFichaModelAct(String fichaModelAct) {
        this.fichaModelAct = fichaModelAct;
    }

    public GeDocumentos getGeDocumento() {
        return geDocumento;
    }

    public void setGeDocumento(GeDocumentos geDocumento) {
        this.geDocumento = geDocumento;
    }

    public String getCambios() {
        return cambios;
    }

    public void setCambios(String cambios) {
        this.cambios = cambios;
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
        if (!(object instanceof Predio)) {
            return false;
        }
        Predio other = (Predio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.historic.Predio[ id=" + id + " ]";
    }

}
