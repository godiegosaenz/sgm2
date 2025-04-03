/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "pub_gui_menu",  schema = SchemasConfig.FLOW)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PubGuiMenu.findAll", query = "SELECT p FROM PubGuiMenu p"),
    @NamedQuery(name = "PubGuiMenu.findById", query = "SELECT p FROM PubGuiMenu p WHERE p.id = :id"),
    @NamedQuery(name = "PubGuiMenu.findByNombre", query = "SELECT p FROM PubGuiMenu p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "PubGuiMenu.findByNumPosicion", query = "SELECT p FROM PubGuiMenu p WHERE p.numPosicion = :numPosicion"),
    @NamedQuery(name = "PubGuiMenu.findByHrefUrl", query = "SELECT p FROM PubGuiMenu p WHERE p.hrefUrl = :hrefUrl")})
public class PubGuiMenu implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "num_posicion", nullable = false)
    private int numPosicion;
    @Size(max = 4000)
    @Column(name = "href_url", length = 4000)
    private String hrefUrl;
    @JoinColumn(name = "menubar", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PubGuiMenubar menubar;
    @JoinColumn(name = "tipo_acceso", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PubGuiMenuTipoAcceso tipoAcceso;
    @OneToMany(mappedBy = "menuPadre", fetch = FetchType.LAZY)
    private Collection<PubGuiMenu> pubGuiMenuCollection;
    @JoinColumn(name = "menu_padre", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PubGuiMenu menuPadre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "menu", fetch = FetchType.LAZY)
    private Collection<PubGuiMenuRol> pubGuiMenuRolCollection;

    @Transient
    private List<PubGuiMenu> menusHijos_byNumPosicion;

    public PubGuiMenu() {
    }

    public PubGuiMenu(Integer id) {
        this.id = id;
    }

    public PubGuiMenu(Integer id, String nombre, int numPosicion) {
        this.id = id;
        this.nombre = nombre;
        this.numPosicion = numPosicion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumPosicion() {
        return numPosicion;
    }

    public void setNumPosicion(int numPosicion) {
        this.numPosicion = numPosicion;
    }

    public String getHrefUrl() {
        return hrefUrl;
    }

    public void setHrefUrl(String hrefUrl) {
        this.hrefUrl = hrefUrl;
    }

    public PubGuiMenubar getMenubar() {
        return menubar;
    }

    public void setMenubar(PubGuiMenubar menubar) {
        this.menubar = menubar;
    }

    public PubGuiMenuTipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(PubGuiMenuTipoAcceso tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public List<PubGuiMenu> getMenusHijos_byNumPosicion() {
        return menusHijos_byNumPosicion;
    }

    public void setMenusHijos_byNumPosicion(List<PubGuiMenu> menusHijos_byNumPosicion) {
        this.menusHijos_byNumPosicion = menusHijos_byNumPosicion;
    }

    public Collection<PubGuiMenu> getPubGuiMenuCollection() {
        return pubGuiMenuCollection;
    }

    public void setPubGuiMenuCollection(Collection<PubGuiMenu> pubGuiMenuCollection) {
        this.pubGuiMenuCollection = pubGuiMenuCollection;
    }

    public PubGuiMenu getMenuPadre() {
        return menuPadre;
    }

    public void setMenuPadre(PubGuiMenu menuPadre) {
        this.menuPadre = menuPadre;
    }

    public Collection<PubGuiMenuRol> getPubGuiMenuRolCollection() {
        return pubGuiMenuRolCollection;
    }

    public void setPubGuiMenuRolCollection(Collection<PubGuiMenuRol> pubGuiMenuRolCollection) {
        this.pubGuiMenuRolCollection = pubGuiMenuRolCollection;
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
        if (!(object instanceof PubGuiMenu)) {
            return false;
        }
        PubGuiMenu other = (PubGuiMenu) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.PubGuiMenu[ id=" + id + " ]";
    }

}
