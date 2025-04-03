/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnResolucion;
import java.math.BigDecimal;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * Lazy de FnResolucion
 *
 * @author Angel Navarro
 */
public class FnResolucionLazy extends BaseLazyDataModel<FnResolucion> {

    private Criteria ctp;
    private Long tipo;

    public FnResolucionLazy() {
        super(FnResolucion.class, "fechaIngreso", "desc");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("numeroResolucion")) {
            crit.add(Restrictions.ilike("numeroResolucion", "%" + filters.get("numeroResolucion").toString().trim().concat("%")));
        }
        if (filters.containsKey("numeroOficio")) {
            crit.add(Restrictions.ilike("numeroOficio", "%" + filters.get("numeroOficio").toString().trim().concat("%")));
        }
        
    }

}
