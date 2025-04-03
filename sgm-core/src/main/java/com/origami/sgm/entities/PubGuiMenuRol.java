/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "pub_gui_menu_rol",  schema = SchemasConfig.FLOW, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"menu", "rol"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PubGuiMenuRol.findAll", query = "SELECT p FROM PubGuiMenuRol p"),
    @NamedQuery(name = "PubGuiMenuRol.findById", query = "SELECT p FROM PubGuiMenuRol p WHERE p.id = :id"),
    @NamedQuery(name = "PubGuiMenuRol.findByRol", query = "SELECT p FROM PubGuiMenuRol p WHERE p.rol = :rol"),
    @NamedQuery(name = "PubGuiMenuRol.findByEsDirector", query = "SELECT p FROM PubGuiMenuRol p WHERE p.esDirector = :esDirector")})
public class PubGuiMenuRol implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rol", nullable = false)
    private Long rol;
    @Column(name = "es_director")
    private Boolean esDirector;
    @JoinColumn(name = "menu", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PubGuiMenu menu;

    public PubGuiMenuRol() {
    }

    public PubGuiMenuRol(Long id) {
        this.id = id;
    }

    public PubGuiMenuRol(Long id, Long rol) {
        this.id = id;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRol() {
        return rol;
    }

    public void setRol(Long rol) {
        this.rol = rol;
    }

    public Boolean getEsDirector() {
        return esDirector;
    }

    public void setEsDirector(Boolean esDirector) {
        this.esDirector = esDirector;
    }

    public PubGuiMenu getMenu() {
        return menu;
    }

    public void setMenu(PubGuiMenu menu) {
        this.menu = menu;
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
        if (!(object instanceof PubGuiMenuRol)) {
            return false;
        }
        PubGuiMenuRol other = (PubGuiMenuRol) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.PubGuiMenuRol[ id=" + id + " ]";
    }
    
}
