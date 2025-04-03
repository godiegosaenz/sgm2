/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl.service;

import com.origami.sgm.acl.AccessLevelRequest;
import com.origami.sgm.acl.RespuestaAcceso;
import com.origami.sgm.acl.cache.UrlRule;
import com.origami.sgm.acl.entity.AclUrl;
import com.origami.sgm.entities.AclRol;
import com.origami.sgm.services.ejbs.HibernateEjbInterceptor;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import util.HiberUtil;

/**
 *
 * @author Fernando
 */
@Singleton(name="AclCacheServ")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Interceptors(value = {HibernateEjbInterceptor.class})
public class AclCacheServ extends CacheUrlSuper implements AclCache {
    
    @Override
    public Boolean checkAccess(String url, Long idUser, List<Long> rolesIds){
        // obtener regla del cache hashmap
        UrlRule rule = this.getRulesMap().get(url);
        // si no existe regla para esta url es pública, dar acceso libre:
        if(rule == null) {
            return true;
        }
        // si existe regla, comprobar:
        else {
            // si es url libre de roles y es usuario logueado, aceptar acceso
            if(rule.getRolesMap().isEmpty() && idUser!=null) return true;
            // comprobar si algun rol hace match:
            if(rolesIds!=null){
                for(Long cdRolId : rolesIds){
                    if(rule.getRolesMap().containsKey(cdRolId)) return true;
                }
            }
        }
        
        return false;
    }
    
    public AclCacheServ(){
    }
        
    @Override
    public void clear() {
        this.setRulesMap(null);
    }
    
    @Override
    protected synchronized void initMap() {
        if (getRulesMap() != null) {
            return;
        }
        ConcurrentMap<String, UrlRule> urlMap = new ConcurrentHashMap<>();
        List<AclUrl> urls = this.listUrls();
        for (AclUrl cdUrl : urls) {
            UrlRule rule = new UrlRule();
            rule.setId(cdUrl.getId());
            rule.setUrl(cdUrl.getUrl());
            // add to map
            urlMap.put(cdUrl.getUrl(), rule);
        }
        // set as cache object:
        this.setRulesMap(urlMap);
    }
    
    protected List<AclUrl> listUrls(){
        Session sess = HiberUtil.getSession();
        
        Criteria crit = sess.createCriteria(AclUrl.class);
        crit.add(Restrictions.eq("sisEnabled", true));
        
        return crit.list();
    }
    
    /**************************************************************************/
    /******************************** ACL ROL *********************************/
    
    @Override
    public List<AclRol> getRoles_url(AclUrl urlEnt) {
        Session sess = HiberUtil.getSession();

        Criteria crit = sess.createCriteria(AclRol.class);
        crit.createAlias("urlHasRolColl", "uhr1");
        crit.add(Restrictions.eq("uhr1.url", urlEnt));

        return crit.list();
    }

    @Override
    public List<AclRol> getRoles_url(Long idUrl) {
        Session sess = HiberUtil.getSession();
        AclUrl urlEnt = (AclUrl) sess.load(AclUrl.class, idUrl);
        return this.getRoles_url(urlEnt);
    }

    @Override
    public RespuestaAcceso checkAccessLevel(AccessLevelRequest alreq) {
        RespuestaAcceso respuesta = new RespuestaAcceso();
        // si el acl esta deshabilitado, dar acceso
        if (!Boolean.TRUE.equals(getEnabled())) {
            // acceso libre:
            respuesta.setAccessGranted(true);
            return respuesta;
        }
        // si es Super User, dar accesos:
        if (alreq.getEsSuperUser()) {
            respuesta.setAccessGranted(true);
            return respuesta;
        }
        // consulta cache-transaccional:
        Boolean ok = checkAccess(alreq.getUrlAcceso(), alreq.getIdUser(), alreq.getRolesIds());
        respuesta.setAccessGranted(ok);
        return respuesta;
    }
    
}
