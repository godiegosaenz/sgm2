/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "mat_formula_tramite", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MatFormulaTramite.findAll", query = "SELECT m FROM MatFormulaTramite m"),
    @NamedQuery(name = "MatFormulaTramite.findById", query = "SELECT m FROM MatFormulaTramite m WHERE m.id = :id"),
    @NamedQuery(name = "MatFormulaTramite.findByNombre", query = "SELECT m FROM MatFormulaTramite m WHERE m.nombre = :nombre"),
    @NamedQuery(name = "MatFormulaTramite.findByFormula", query = "SELECT m FROM MatFormulaTramite m WHERE m.formula = :formula"),
    @NamedQuery(name = "MatFormulaTramite.findByNVersion", query = "SELECT m FROM MatFormulaTramite m WHERE m.nVersion = :nVersion"),
    @NamedQuery(name = "MatFormulaTramite.findByFecCre", query = "SELECT m FROM MatFormulaTramite m WHERE m.fecCre = :fecCre"),
    @NamedQuery(name = "MatFormulaTramite.findByEstado", query = "SELECT m FROM MatFormulaTramite m WHERE m.estado = :estado")})
public class MatFormulaTramite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "formula", nullable = false, length = 4000)
    private String formula;
    @Column(name = "n_version")
    private Integer nVersion;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "estado")
    private Boolean estado=true;
    @Column(name = "prefijo", length = 10)
    private String prefijo;
    @JoinColumn(name = "tipo_tramite", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private GeTipoTramite tipoTramite;

    public MatFormulaTramite() {
    }

    public MatFormulaTramite(Long id) {
        this.id = id;
    }

    public MatFormulaTramite(Long id, String nombre, String formula) {
        this.id = id;
        this.nombre = nombre;
        this.formula = formula;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getnVersion() {
        return nVersion;
    }

    public void setnVersion(Integer nVersion) {
        this.nVersion = nVersion;
    }
    

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Integer getNVersion() {
        return nVersion;
    }

    public void setNVersion(Integer nVersion) {
        this.nVersion = nVersion;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
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

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
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
        if (!(object instanceof MatFormulaTramite)) {
            return false;
        }
        MatFormulaTramite other = (MatFormulaTramite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.MatFormulaTramite[ id=" + id + " ]";
    }
    
}
