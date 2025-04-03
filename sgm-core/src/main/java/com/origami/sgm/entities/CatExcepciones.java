/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "cat_excepciones", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CatExcepciones.findAll", query = "SELECT c FROM CatExcepciones c"),
    @NamedQuery(name = "CatExcepciones.findById", query = "SELECT c FROM CatExcepciones c WHERE c.id = :id"),
    @NamedQuery(name = "CatExcepciones.findByDetalle", query = "SELECT c FROM CatExcepciones c WHERE c.detalle = :detalle"),
    @NamedQuery(name = "CatExcepciones.findByFecCre", query = "SELECT c FROM CatExcepciones c WHERE c.fecCre = :fecCre"),
    @NamedQuery(name = "CatExcepciones.findByFecAct", query = "SELECT c FROM CatExcepciones c WHERE c.fecAct = :fecAct"),
    @NamedQuery(name = "CatExcepciones.findByUsrCre", query = "SELECT c FROM CatExcepciones c WHERE c.usrCre = :usrCre"),
    @NamedQuery(name = "CatExcepciones.findByUsrAct", query = "SELECT c FROM CatExcepciones c WHERE c.usrAct = :usrAct"),
    @NamedQuery(name = "CatExcepciones.findByEstado", query = "SELECT c FROM CatExcepciones c WHERE c.estado = :estado")})
public class CatExcepciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 4000)
    @Column(name = "detalle", length = 4000)
    private String detalle;
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
    @JoinColumn(name = "tipo_tramite", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GeTipoTramite tipoTramite;
    @JoinColumn(name = "parametro", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatExcepcionesParam parametro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "excepcion", fetch = FetchType.LAZY)
    private Collection<CatExcepcionesDet> catExcepcionesDetCollection;

    public CatExcepciones() {
    }

    public CatExcepciones(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
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

    public GeTipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(GeTipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public CatExcepcionesParam getParametro() {
        return parametro;
    }

    public void setParametro(CatExcepcionesParam parametro) {
        this.parametro = parametro;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<CatExcepcionesDet> getCatExcepcionesDetCollection() {
        return catExcepcionesDetCollection;
    }

    public void setCatExcepcionesDetCollection(Collection<CatExcepcionesDet> catExcepcionesDetCollection) {
        this.catExcepcionesDetCollection = catExcepcionesDetCollection;
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
        if (!(object instanceof CatExcepciones)) {
            return false;
        }
        CatExcepciones other = (CatExcepciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.CatExcepciones[ id=" + id + " ]";
    }
    
}
