/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenActividadComercial;
import com.origami.sgm.entities.RenLocalUbicacion;
import java.util.Collection;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Angel Navarro
 */
public class RenActividadLocalLazy extends BaseLazyDataModel<RenActividadComercial> {

    private Collection<RenActividadComercial> actividades;

    public RenActividadLocalLazy() {
        super(RenActividadComercial.class, "descripcion", "ASC");
    }

    public RenActividadLocalLazy(Collection<RenActividadComercial> renActividadComercialCollection) {
        super(RenActividadComercial.class, "descripcion", "ASC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        crit.add(Restrictions.eq("estado", Boolean.TRUE));    
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        if (actividades != null) {
//            crit.add
        }
    }

}
