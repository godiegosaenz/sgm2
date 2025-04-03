/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatSolicitudNormaConstruccion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author CarlosLoorVargas
 */
public class CatSolicitudNormaConstruccionLazy extends BaseLazyDataModel<CatSolicitudNormaConstruccion>{

    public CatSolicitudNormaConstruccionLazy() {
        super(CatSolicitudNormaConstruccion.class,"id","DESC");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("tramite")) {
            crit.add(Restrictions.eq("tramite", new Integer(filters.get("tramite").toString().trim())));
        }
    }
}
