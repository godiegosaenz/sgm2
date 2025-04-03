/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegpTareasDinardap;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class RegpTareasDinardapLazy extends BaseLazyDataModel<RegpTareasDinardap> {

    private Boolean estado;
    
    public RegpTareasDinardapLazy() {
        super(RegpTareasDinardap.class, "fecha", "DESC");
    }
    
    public RegpTareasDinardapLazy(Boolean estado) {
        super(RegpTareasDinardap.class, "fecha", "DESC");
        this.estado = estado;
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("institucion")) {
            crit.add(Restrictions.ilike("institucion", "%" + filters.get("institucion").toString().trim() + "%"));
        }
        if (filters.containsKey("solicitante")) {
            crit.add(Restrictions.ilike("solicitante", "%" + filters.get("solicitante").toString().trim() + "%"));
        }
        if (filters.containsKey("numeroSolicitud")) {
            crit.add(Restrictions.ilike("numeroSolicitud", "%" + filters.get("numeroSolicitud").toString().trim() + "%"));
        }
        if(estado != null){
            crit.add(Restrictions.eq("estado", estado));
        }
    }
    
}
