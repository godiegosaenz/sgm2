/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RecActasEspecies;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class RecActasEspeciesLazy extends BaseLazyDataModel<RecActasEspecies> {

    private Boolean estado;

    public RecActasEspeciesLazy() {
        super(RecActasEspecies.class, "fechaIngreso", "DESC");
    }

    public RecActasEspeciesLazy(Boolean state) {
        super(RecActasEspecies.class, "fechaIngreso", "DESC");
        this.estado = state;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("numActa")) {
            crit.add(Restrictions.eq("numActa", Integer.valueOf(filters.get("numActa").toString().trim())));
        }
        if (filters.containsKey("anio")) {
            crit.add(Restrictions.eq("anio", Integer.valueOf(filters.get("anio").toString().trim())));
        }
        if (estado != null) {
            crit.add(Restrictions.eq("estado", estado));
        }
    }

}
