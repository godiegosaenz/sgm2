/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.avaluos.SectorValorizacion;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * Lazy de SectorValorizacion
 *
 * @author Angel Navarro
 */
public class SectorValorizacionLazy extends BaseLazyDataModel<SectorValorizacion> {

    private Criteria ctp;
    private Long tipo;

    public SectorValorizacionLazy() {
        super(SectorValorizacion.class, "sector", "desc");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("sector")) {
            crit.add(Restrictions.eq("sector", new Long(filters.get("sector").toString().trim())));
        }
        if (filters.containsKey("valorM2")) {
            crit.add(Restrictions.eq("valorM2", new BigDecimal(filters.get("valorM2").toString().trim())));
        }
        if (filters.containsKey("detalle")) {
            crit.add(Restrictions.ilike("detalle", "%" + filters.get("valorM2").toString().trim() + "%"));
        }
        if (filters.containsKey("servicios")) {
            crit.add(Restrictions.eq("servicios", new BigInteger(filters.get("servicios").toString().trim())));
        }
    }

}
