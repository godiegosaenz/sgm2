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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Anyelo
 */
@Entity
@Table(name = "pub_gui_menubar",  schema = SchemasConfig.FLOW, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"identificador"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PubGuiMenubar.findAll", query = "SELECT p FROM PubGuiMenubar p"),
    @NamedQuery(name = "PubGuiMenubar.findById", query = "SELECT p FROM PubGuiMenubar p WHERE p.id = :id"),
    @NamedQuery(name = "PubGuiMenubar.findByIdentificador", query = "SELECT p FROM PubGuiMenubar p WHERE p.identificador = :identificador")})
public class PubGuiMenubar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "identificador", nullable = false, length = 255)
    private String identificador;
    @OneToMany(mappedBy = "menubar", fetch = FetchType.LAZY)
    private Collection<PubGuiMenu> pubGuiMenuCollection;
    
    @Transient
    private List<PubGuiMenu> menuListSoyMenubar_byOrden;

    public PubGuiMenubar() {
    }

    public PubGuiMenubar(Integer id) {
        this.id = id;
    }

    public PubGuiMenubar(Integer id, String identificador) {
        this.id = id;
        this.identificador = identificador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PubGuiMenu> getPubGuiMenuCollection() {
        return pubGuiMenuCollection;
    }

    public void setPubGuiMenuCollection(Collection<PubGuiMenu> pubGuiMenuCollection) {
        this.pubGuiMenuCollection = pubGuiMenuCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public List<PubGuiMenu> getMenuListSoyMenubar_byOrden() {
        return menuListSoyMenubar_byOrden;
    }

    public void setMenuListSoyMenubar_byOrden(List<PubGuiMenu> menuListSoyMenubar_byOrden) {
        this.menuListSoyMenubar_byOrden = menuListSoyMenubar_byOrden;
    }
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PubGuiMenubar)) {
            return false;
        }
        PubGuiMenubar other = (PubGuiMenubar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.origami.sgm.entities.PubGuiMenubar[ id=" + id + " ]";
    }
    
}
