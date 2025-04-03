/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegCatPapel;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Adrian
 */
public class RegPapelLazy extends BaseLazyDataModel<RegCatPapel> {

    public RegPapelLazy() {
        super(RegCatPapel.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("papel")) {
            crit.add(Restrictions.ilike("papel", "%" + filters.get("papel").toString().trim() + "%"));
        }
        if (filters.containsKey("abreviatura")) {
            crit.add(Restrictions.ilike("abreviatura", "%" + filters.get("abreviatura").toString().trim() + "%"));
        }
    }
}
