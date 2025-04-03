/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegLibro;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Supergol
 */
public class RegLibroLazy extends BaseLazyDataModel<RegLibro>{
    
    public RegLibroLazy() {
        super(RegLibro.class);
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
      
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }

        if (filters.containsKey("tipo")) {
            crit.add(Restrictions.ilike("tipo", "%" + filters.get("tipo").toString().trim() + "%"));
        }
    }
    
}
