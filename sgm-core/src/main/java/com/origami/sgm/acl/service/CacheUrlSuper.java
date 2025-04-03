/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.acl.service;

import com.origami.sgm.acl.cache.UrlRule;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Fernando
 */
abstract class CacheUrlSuper {
    
    private ConcurrentMap<String, UrlRule> rulesMap;
    
    private Boolean enabled = false;

    public CacheUrlSuper() { 
    }

    protected synchronized void initMap() {
    }

    protected ConcurrentMap<String, UrlRule> getRulesMap() {
        if (rulesMap == null) {
            initMap();
        }
        return rulesMap;
    }

    protected void setRulesMap(ConcurrentMap<String, UrlRule> rulesMap) {
        this.rulesMap = rulesMap;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    
    
}
