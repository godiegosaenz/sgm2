/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.historic.Predio;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author CarlosLoorVargas
 */
public class HistoricoPredioLazy extends BaseLazyDataModel<Predio> {

    private Long numPredio = null;
    private Boolean migrado = false;

    public HistoricoPredioLazy() {
        super(Predio.class, "id", "DESC");
    }

    public HistoricoPredioLazy(Long numPredio) {
        super(Predio.class, "id", "DESC");
        this.numPredio = numPredio;
    }

    public HistoricoPredioLazy(Long numPredio, Boolean migrado) {
        super(Predio.class, "id", "DESC");
        this.numPredio = numPredio;
        this.migrado = migrado;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (numPredio != null) {
            crit.add(Restrictions.eq("predio", numPredio));
        }
        if (migrado) {
            crit.add(Restrictions.eq("migrado", migrado));
        }
        if (filters.containsKey("usuario")) {
            crit.add(Restrictions.ilike("usuario", "%" + filters.get("usuario").toString() + "%"));
        }
        if (filters.containsKey("observacion")) {
            crit.add(Restrictions.ilike("observacion", "%" + filters.get("observacion").toString() + "%"));
        }
        if (filters.containsKey("predio")) {
            crit.add(Restrictions.eq("predio", Long.parseLong(filters.get("predio").toString())));
        }
    }

}
