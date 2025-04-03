/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.services.ejbs;

import com.origami.sgm.acl.service.AclCache;
import com.origami.transactionalcore.entitymanager.Entitymanager;
import com.origami.sgm.util.EjbUtil;

/**
 *
 * @author Fernando
 */
public abstract class SisEjb {
    
    public static Entitymanager aclService(){
        return (Entitymanager) EjbUtil.getEjb("manager");
    }
    
    public static AclCache aclCacheServ(){
        return (AclCache) EjbUtil.getEjb("AclCacheServ");
    }
    
}
