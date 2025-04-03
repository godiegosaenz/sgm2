/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CtlgDescuentoEmision;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Mariuly
 */
public class CtlgDescuentoEmisionLazy extends BaseLazyDataModel<CtlgDescuentoEmision> {

    public CtlgDescuentoEmisionLazy() {

        super(CtlgDescuentoEmision.class, "numMes", "ASC");

    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        if (filters.containsKey("numMes")) {
            crit.add(Restrictions.eq("numMes", new BigInteger(filters.get("numMes").toString().trim())));
        }
        if (filters.containsKey("numQuincena")) {
            crit.add(Restrictions.eq("numQuincena", new BigInteger(filters.get("numQuincena").toString().trim())));
        }
        if (filters.containsKey("porcentaje")) {
            crit.add(Restrictions.eq("porcentaje", new BigDecimal(filters.get("porcentaje").toString().trim())));
        }

    }

}
