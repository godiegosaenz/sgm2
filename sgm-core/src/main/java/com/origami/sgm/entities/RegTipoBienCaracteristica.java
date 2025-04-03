/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author CarlosLoorVargas
 */
@Entity
@Table(name = "reg_tipo_bien_caracteristica",  schema = SchemasConfig.APP1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegTipoBienCaracteristica.findAll", query = "SELECT r FROM RegTipoBienCaracteristica r"),
    @NamedQuery(name = "RegTipoBienCaracteristica.findById", query = "SELECT r FROM RegTipoBienCaracteristica r WHERE r.id = :id"),
    @NamedQuery(name = "RegTipoBienCaracteristica.findByNombre", query = "SELECT r FROM RegTipoBienCaracteristica r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "RegTipoBienCaracteristica.findByEstado", query = "SELECT r FROM RegTipoBienCaracteristica r WHERE r.estado = :estado"),
    @NamedQuery(name = "RegTipoBienCaracteristica.findByIsMain", query = "SELECT r FROM RegTipoBienCaracteristica r WHERE r.isMain = :isMain")})
public class RegTipoBienCaracteristica implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 255)
    @Column(name = "nombre", length = 255)
    private String nombre;
    @Column(name = "estado")
    private Boolean estado;
    @Column(name = "is_main")
    private Boolean isMain;
    @OneToMany(mappedBy = "caracteristica", fetch = FetchType.LAZY)
    private Collection<RegFichaBien> regFichaBienCollection;
    @JoinColumn(name = "tipo_bien", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RegTipoBien tipoBien;

    public RegTipoBienCaracteristica() {
    }

    public RegTipoBienCaracteristica(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<RegFichaBien> getRegFichaBienCollection() {
        return regFichaBienCollection;
    }

    public void setRegFichaBienCollection(Collection<RegFichaBien> regFichaBienCollection) {
        this.regFichaBienCollection = regFichaBienCollection;
    }

    public RegTipoBien getTipoBien() {
        return tipoBien;
    }

    public void setTipoBien(RegTipoBien tipoBien) {
        this.tipoBien = tipoBien;
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
        if (!(object instanceof RegTipoBienCaracteristica)) {
            return false;
        }
        RegTipoBienCaracteristica other = (RegTipoBienCaracteristica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gob.samborondon.entities.RegTipoBienCaracteristica[ id=" + id + " ]";
    }
    
}
