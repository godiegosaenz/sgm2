/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl.entity;

import com.origami.sgm.entities.database.SchemasConfig;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 *
 * @author Fernando
 */
@Entity
@Table(name = "acl_url", schema = SchemasConfig.APP1)
//@GenericGenerator(name=SchemasConfig.APPUNISEQ_ORM, strategy = "sequence",
//    parameters = {
//        @Parameter(name="sequence", value = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB) })
public class AclUrl implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    //@SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Basic(optional = false)
    @Column(name = "url_", nullable = false, length = 2000, unique = true)
    private String url;
    
    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL)
    private Collection<AclUrlHasRol> urlHasRolColl;
    
    @Basic(optional = false)
    @Column(name = "sis_enabled", nullable = false)
    private Boolean sisEnabled = true;
    
    
    
    public AclUrl(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Collection<AclUrlHasRol> getUrlHasRolColl() {
        return urlHasRolColl;
    }

    public void setUrlHasRolColl(Collection<AclUrlHasRol> urlHasRolColl) {
        this.urlHasRolColl = urlHasRolColl;
    }

    public Boolean getSisEnabled() {
        return sisEnabled;
    }

    public void setSisEnabled(Boolean sisEnabled) {
        this.sisEnabled = sisEnabled;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AclUrl other = (AclUrl) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
