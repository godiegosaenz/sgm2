/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl.entity;

import com.origami.sgm.entities.database.SchemasConfig;
import com.origami.sgm.entities.AclRol;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table; import javax.persistence.SequenceGenerator;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Fernando
 */
@Entity
@Table(name = "acl_url_has_rol", schema = SchemasConfig.APP1, 
        uniqueConstraints = @UniqueConstraint(columnNames = {"url", "rol"}))
public class AclUrlHasRol implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SchemasConfig.APPUNISEQ_ORM)  @SequenceGenerator(name = SchemasConfig.APPUNISEQ_ORM, sequenceName = SchemasConfig.APP1+"."+SchemasConfig.APPUNISEQ_DB, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @JoinColumn(name = "url", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private AclUrl url;
    
    @JoinColumn(name = "rol", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private AclRol rol;
    
    
    
    
    public AclUrlHasRol(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AclUrl getUrl() {
        return url;
    }

    public void setUrl(AclUrl url) {
        this.url = url;
    }

    public AclRol getRol() {
        return rol;
    }

    public void setRol(AclRol rol) {
        this.rol = rol;
    }
    
    
    
}
