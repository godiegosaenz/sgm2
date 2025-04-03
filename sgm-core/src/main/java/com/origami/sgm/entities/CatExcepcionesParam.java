/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import com.origami.sgm.entities.CatExcepciones;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity; import com.origami.sgm.entities.database.SchemasConfig;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_excepciones_param", schema = SchemasConfig.APP1, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"prefijo"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatExcepcionesParam.findAll", query = "SELECT c FROM CatExcepcionesParam c"),
    @NamedQuery(name = "CatExcepcionesParam.findById", query = "SELECT c FROM CatExcepcionesParam c WHERE c.id = :id"),
    @NamedQuery(name = "CatExcepcionesParam.findByDescripcion", query = "SELECT c FROM CatExcepcionesParam c WHERE c.descripcion = :descripcion"),
    @NamedQuery(name = "CatExcepcionesParam.findByPrefijo", query = "SELECT c FROM CatExcepcionesParam c WHERE c.prefijo = :prefijo"),
    @NamedQuery(name = "CatExcepcionesParam.findByTipoTramPref", query = "SELECT c FROM CatExcepcionesParam c WHERE c.tipoTramPref = :tipoTramPref"),
    @NamedQuery(name = "CatExcepcionesParam.findByFecCre", query = "SELECT c FROM CatExcepcionesParam c WHERE c.fecCre = :fecCre"),
    @NamedQuery(name = "CatExcepcionesParam.findByFecAct", query = "SELECT c FROM CatExcepcionesParam c WHERE c.fecAct = :fecAct"),
    @NamedQuery(name = "CatExcepcionesParam.findByUsrCre", query = "SELECT c FROM CatExcepcionesParam c WHERE c.usrCre = :usrCre"),
    @NamedQuery(name = "CatExcepcionesParam.findByUsrAct", query = "SELECT c FROM CatExcepcionesParam c WHERE c.usrAct = :usrAct"),
    @NamedQuery(name = "CatExcepcionesParam.findByEstado", query = "SELECT c FROM CatExcepcionesParam c WHERE c.estado = :estado")})
public class CatExcepcionesParam implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 4000)
    @Column(name = "descripcion", length = 4000)
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "prefijo", nullable = false, length = 12)
    private String prefijo;
    @Size(max = 200)
    @Column(name = "tipo_tram_pref", length = 200)
    private String tipoTramPref;
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
    @OneToMany(mappedBy = "parametro", fetch = FetchType.LAZY)
    private Collection<CatExcepciones> catExcepcionesCollection;

    public CatExcepcionesParam() {
    }

    public CatExcepcionesParam(Long id) {
        this.id = id;
    }

    public CatExcepcionesParam(Long id, String prefijo) {
        this.id = id;
        this.prefijo = prefijo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getTipoTramPref() {
        return tipoTramPref;
    }

    public void setTipoTramPref(String tipoTramPref) {
        this.tipoTramPref = tipoTramPref;
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

    @XmlTransient
    @JsonIgnore
    public Collection<CatExcepciones> getCatExcepcionesCollection() {
        return catExcepcionesCollection;
    }

    public void setCatExcepcionesCollection(Collection<CatExcepciones> catExcepcionesCollection) {
        this.catExcepcionesCollection = catExcepcionesCollection;
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
        if (!(object instanceof CatExcepcionesParam)) {
            return false;
        }
        CatExcepcionesParam other = (CatExcepcionesParam) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatExcepcionesParam[ id=" + id + " ]";
    }
    
}
