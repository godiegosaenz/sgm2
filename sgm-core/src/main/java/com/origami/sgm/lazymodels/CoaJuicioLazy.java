/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CoaJuicio;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 *
 * @author Anyelo
 */
public class CoaJuicioLazy extends BaseLazyDataModel<CoaJuicio>{
    
    private Criteria tramite;

    public CoaJuicioLazy() {
        super(CoaJuicio.class, "id", "DESC");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("numeroJuicio")) {
            crit.add(Restrictions.eq("numeroJuicio", new Integer(filters.get("numeroJuicio").toString().trim())));
        }
        if (filters.containsKey("anioJuicio")) {
            crit.add(Restrictions.eq("anioJuicio", new Integer(filters.get("anioJuicio").toString().trim())));
        }
        if (filters.containsKey("tramite.id")) {
            tramite = crit.createCriteria("tramite", JoinType.LEFT_OUTER_JOIN);
            tramite.add(Restrictions.eq("id", new Long(filters.get("tramite.id").toString().trim())));
        }
    }
    
}
