/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenTipoTransaccion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Joao Sanga
 */
public class TipoTransaccionLazy extends BaseLazyDataModel<RenTipoTransaccion>{

    public TipoTransaccionLazy() {
        super(RenTipoTransaccion.class);
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        //ctp = crit.createCriteria("tipo", JoinType.LEFT_OUTER_JOIN);
        
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        crit.add(Restrictions.eq("estado", true));
    }
}
