/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl.cache;

import com.origami.sgm.entities.AclRol;
import com.origami.sgm.services.ejbs.SisEjb;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Fernando
 */
public class UrlRule {
    
    private Long id;
    private String url;
    
    private ConcurrentMap<Long,Rol> rolesMap;
    
    
    
    protected synchronized void initRolesMap(){
        if(this.getRolesMap()==null){
            ConcurrentHashMap<Long, Rol> map1 = new ConcurrentHashMap<>();
            List<AclRol> rolesList = SisEjb.aclCacheServ().getRoles_url(id);
            for (AclRol cdRol : rolesList) {
                Rol rol1 = new Rol();
                rol1.setId(cdRol.getId());
                rol1.setNombre(cdRol.getNombre());
                // add to map
                map1.put(rol1.getId(), rol1);
            }
            this.setRolesMap(map1);
        }
    }
    
    public void clear(){
        this.setRolesMap(null);
    }
    
    public UrlRule(){
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

    public ConcurrentMap<Long, Rol> getRolesMap() {
        if(rolesMap==null) initRolesMap();
        return getRolesMap();
    }

    public void setRolesMap(ConcurrentMap<Long, Rol> rolesMap) {
        this.rolesMap = rolesMap;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.url);
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
        final UrlRule other = (UrlRule) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}
