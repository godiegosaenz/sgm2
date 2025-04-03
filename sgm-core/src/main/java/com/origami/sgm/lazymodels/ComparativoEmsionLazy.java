/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.historic.ComparativoEmision;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author HenryPilco
 */
public class ComparativoEmsionLazy extends BaseLazyDataModel<ComparativoEmision>{

    public ComparativoEmsionLazy() {
        super(ComparativoEmision.class, "id", "DESC");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("numPredio")) {
            crit.add(Restrictions.eq("numPredio", new BigInteger(filters.get("numPredio").toString().trim())));
        }
        crit.add(Restrictions.eq("aprobCatastro",Boolean.TRUE));
    }
}
