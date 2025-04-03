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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Origami Integrales
 */
@Entity
@Table(name = "disparador_tramites", schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DisparadorTramites.findAll", query = "SELECT d FROM DisparadorTramites d"),
    @NamedQuery(name = "DisparadorTramites.findById", query = "SELECT d FROM DisparadorTramites d WHERE d.id = :id"),
    @NamedQuery(name = "DisparadorTramites.findByDescripcion", query = "SELECT d FROM DisparadorTramites d WHERE d.descripcion = :descripcion"),
    @NamedQuery(name = "DisparadorTramites.findByArchivoBpm", query = "SELECT d FROM DisparadorTramites d WHERE d.archivoBpm = :archivoBpm"),
    @NamedQuery(name = "DisparadorTramites.findByEstado", query = "SELECT d FROM DisparadorTramites d WHERE d.estado = :estado"),
    @NamedQuery(name = "DisparadorTramites.findByFecCre", query = "SELECT d FROM DisparadorTramites d WHERE d.fecCre = :fecCre")})
public class DisparadorTramites implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "archivo_bpm", nullable = false, length = 500)
    private String archivoBpm;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "fec_cre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecCre;
    @Column(name = "carpeta", nullable = false, length = 100)
    private String carpeta;
    @OneToMany(mappedBy = "disparador", fetch = FetchType.LAZY)
    private Collection<GeTipoTramite> geTipoTramiteCollection;

    public DisparadorTramites() {
    }

    public DisparadorTramites(Long id) {
        this.id = id;
    }

    public DisparadorTramites(Long id, String descripcion, String archivoBpm) {
        this.id = id;
        this.descripcion = descripcion;
        this.archivoBpm = archivoBpm;
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

    public String getArchivoBpm() {
        return archivoBpm;
    }

    public void setArchivoBpm(String archivoBpm) {
        this.archivoBpm = archivoBpm;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Date getFecCre() {
        return fecCre;
    }

    public void setFecCre(Date fecCre) {
        this.fecCre = fecCre;
    }

    public String getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(String carpeta) {
        this.carpeta = carpeta;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<GeTipoTramite> getGeTipoTramiteCollection() {
        return geTipoTramiteCollection;
    }

    public void setGeTipoTramiteCollection(Collection<GeTipoTramite> geTipoTramiteCollection) {
        this.geTipoTramiteCollection = geTipoTramiteCollection;
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
        if (!(object instanceof DisparadorTramites)) {
            return false;
        }
        DisparadorTramites other = (DisparadorTramites) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.DisparadorTramites[ id=" + id + " ]";
    }

}
