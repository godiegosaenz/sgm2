/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatConfig;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Mariuly
 */
public class CatConfigLazy extends BaseLazyDataModel<CatConfig> {

    public CatConfigLazy() {
        super(CatConfig.class, "nombre", "ASC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        if (filters.containsKey("valor")) {
            crit.add(Restrictions.ilike("valor",  "%" + filters.get("valor").toString().trim() + "%"));
        }

    }

}
