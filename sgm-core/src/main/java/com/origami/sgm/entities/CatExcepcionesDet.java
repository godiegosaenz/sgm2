/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatExcepciones;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_excepciones_det", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatExcepcionesDet.findAll", query = "SELECT c FROM CatExcepcionesDet c"),
    @NamedQuery(name = "CatExcepcionesDet.findById", query = "SELECT c FROM CatExcepcionesDet c WHERE c.id = :id"),
    @NamedQuery(name = "CatExcepcionesDet.findByDAnt", query = "SELECT c FROM CatExcepcionesDet c WHERE c.dAnt = :dAnt"),
    @NamedQuery(name = "CatExcepcionesDet.findByDAct", query = "SELECT c FROM CatExcepcionesDet c WHERE c.dAct = :dAct"),
    @NamedQuery(name = "CatExcepcionesDet.findByFecCre", query = "SELECT c FROM CatExcepcionesDet c WHERE c.fecCre = :fecCre"),
    @NamedQuery(name = "CatExcepcionesDet.findByFecAct", query = "SELECT c FROM CatExcepcionesDet c WHERE c.fecAct = :fecAct"),
    @NamedQuery(name = "CatExcepcionesDet.findByUsrCre", query = "SELECT c FROM CatExcepcionesDet c WHERE c.usrCre = :usrCre"),
    @NamedQuery(name = "CatExcepcionesDet.findByUsrAct", query = "SELECT c FROM CatExcepcionesDet c WHERE c.usrAct = :usrAct"),
    @NamedQuery(name = "CatExcepcionesDet.findByEstado", query = "SELECT c FROM CatExcepcionesDet c WHERE c.estado = :estado")})
public class CatExcepcionesDet implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 2147483647)
    @Column(name = "d_ant", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String dAnt;
    @Size(max = 2147483647)
    @Column(name = "d_act", length = 2147483647, columnDefinition = SchemasConfig.LONG_TEXT_TYPE)
    private String dAct;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "fec_act")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecAct;
    @Size(max = 20)
    @Column(name = "usr_cre", length = 20)
    private String usrCre;
    @Size(max = 20)
    @Column(name = "usr_act", length = 20)
    private String usrAct;
    @Column(name = "estado")
    private Boolean estado;
    @JoinColumn(name = "predio", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatPredio predio;
    @JoinColumn(name = "excepcion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CatExcepciones excepcion;

    public CatExcepcionesDet() {
    }

    public CatExcepcionesDet(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDAnt() {
        return dAnt;
    }

    public void setDAnt(String dAnt) {
        this.dAnt = dAnt;
    }

    public String getDAct() {
        return dAct;
    }

    public void setDAct(String dAct) {
        this.dAct = dAct;
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

    public String getUsrCre() {
        return usrCre;
    }

    public void setUsrCre(String usrCre) {
        this.usrCre = usrCre;
    }

    public String getUsrAct() {
        return usrAct;
    }

    public void setUsrAct(String usrAct) {
        this.usrAct = usrAct;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public CatPredio getPredio() {
        return predio;
    }

    public void setPredio(CatPredio predio) {
        this.predio = predio;
    }

    public CatExcepciones getExcepcion() {
        return excepcion;
    }

    public void setExcepcion(CatExcepciones excepcion) {
        this.excepcion = excepcion;
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
        if (!(object instanceof CatExcepcionesDet)) {
            return false;
        }
        CatExcepcionesDet other = (CatExcepcionesDet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatExcepcionesDet[ id=" + id + " ]";
    }
    
}
