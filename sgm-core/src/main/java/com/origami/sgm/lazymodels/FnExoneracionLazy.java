/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnExoneracionClase;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 17/05/2016
 */
public class FnExoneracionLazy extends BaseLazyDataModel<FnExoneracionClase> {
    
    public FnExoneracionLazy() {
        super(FnExoneracionClase.class);
    }
    
    public FnExoneracionLazy(String defaultSorted) {
        super(FnExoneracionClase.class, defaultSorted);
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            if (Utils.validateNumberPattern(filters.get("id").toString())) {
                crit.add(Restrictions.eq("id", Long.valueOf(filters.get("id").toString())));
            }
        }
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%".concat(filters.get("descripcion").toString()).concat("%")));
        }
    }
}
