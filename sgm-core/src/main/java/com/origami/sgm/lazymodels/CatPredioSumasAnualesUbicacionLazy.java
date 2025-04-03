/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioSumasAnualesUbicacion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Henry Pilco
 */
public class CatPredioSumasAnualesUbicacionLazy extends  BaseLazyDataModel<CatPredioSumasAnualesUbicacion>{
    
    public CatPredioSumasAnualesUbicacionLazy() {
        super(CatPredioSumasAnualesUbicacion.class, "id");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("anio")) {
            crit.add(Restrictions.eq("anio", new Long(filters.get("anio").toString().trim())));
        }
        if (filters.containsKey("ubicacion.nombre")) {
            Criteria parroquia = crit.createCriteria("ubicacion");
            parroquia.add(Restrictions.like("nombre", filters.get("ubicacion.nombre").toString().trim()));
        }
    }
}
