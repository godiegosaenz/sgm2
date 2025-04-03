/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenLocalUbicacion;
import java.math.BigDecimal;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
public class UbicacionLocalLazy extends BaseLazyDataModel<RenLocalUbicacion> {


    public UbicacionLocalLazy() {
        super(RenLocalUbicacion.class, "id");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.eq("descripcion", Boolean.valueOf(filters.get("descripcion").toString())));
        }        
        crit.add(Restrictions.eq("estado", true));
    }
    
}
