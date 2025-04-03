/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CoaAbogado;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author origami
 */
public class CoaAbogadosLazy extends BaseLazyDataModel<CoaAbogado>{

    public CoaAbogadosLazy() {
        super(CoaAbogado.class, "id", "DESC");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("detalle")) {
            crit.add(Restrictions.ilike("detalle", "%" + filters.get("detalle").toString().trim() + "%"));
        }
    }
    
}
