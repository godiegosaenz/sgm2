/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 17/05/2016
 */
public class RenPermisoFuncionamientoLazy extends BaseLazyDataModel<RenPermisosFuncionamientoLocalComercial> {

    public RenPermisoFuncionamientoLazy() {
        super(RenPermisosFuncionamientoLocalComercial.class, "id", "DESC");
    }

    public RenPermisoFuncionamientoLazy(String defaultSorted) {
        super(RenPermisosFuncionamientoLocalComercial.class, defaultSorted);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        Criteria ht = crit.createCriteria("ht");
        Criteria localComercial =crit.createCriteria("localComercial");
        if (filters.containsKey("id")) {
            if (Utils.validateNumberPattern(filters.get("id").toString())) {
                crit.add(Restrictions.eq("id", Long.valueOf(filters.get("id").toString())));
            }
        }
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%".concat(filters.get("descripcion").toString()).concat("%")));
        }
        if (filters.containsKey("ht.id")) {
            if (Utils.validateNumberPattern(filters.get("ht.id").toString())) 
                ht.add(Restrictions.eq("id", Long.valueOf(filters.get("ht.id").toString())));
        }
        if (filters.containsKey("ht.nombrePropietario")) {
            ht.add(Restrictions.ilike("nombrePropietario", "%".concat(filters.get("ht.nombrePropietario").toString()).concat("%")));
        }
        if (filters.containsKey("localComercial.nombreLocal")) {
            localComercial.add(Restrictions.ilike("nombreLocal", "%".concat(filters.get("localComercial.nombreLocal").toString()).concat("%")));
        }
    }
}
