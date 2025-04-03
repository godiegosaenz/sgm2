/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenDesvalorizacion;
import java.math.BigDecimal;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * Lazy de RenDesvalorizacion
 *
 * @author Angel Navarro
 */
public class RenDesvalorizacionLazy extends BaseLazyDataModel<RenDesvalorizacion> {

    private Criteria ctp;
    private Long tipo;

    public RenDesvalorizacionLazy() {
        super(RenDesvalorizacion.class, "anio", "desc");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("anio")) {
            crit.add(Restrictions.eq("anio", new Integer(filters.get("anio").toString().trim())));
        }
        if (filters.containsKey("valor")) {
            ctp.add(Restrictions.ilike("valor", new BigDecimal(filters.get("valor").toString().trim())));
        }
    }

}
