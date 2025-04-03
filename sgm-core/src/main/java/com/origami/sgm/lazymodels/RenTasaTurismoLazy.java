/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenTasaTurismo;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Angel Navarro
 * @date 20/07/2016
 */
public class RenTasaTurismoLazy extends BaseLazyDataModel<RenTasaTurismo> {

    public RenTasaTurismoLazy(String defaultSorted, String defaultSortOrder) {
        super(RenTasaTurismo.class, defaultSorted, defaultSortOrder);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("tipo")) {//integer
            crit.add(Restrictions.eq("tipo", new Integer(filters.get("tipo").toString().trim())));
        }
        if (filters.containsKey("actividad.descripcion")) {
            crit.createCriteria("actividad").add(Restrictions.ilike("descripcion", "%" + filters.get("actividad.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("categoria.descripcion")) {
            crit.createCriteria("categoria").add(Restrictions.ilike("descripcion", "%" + filters.get("categoria.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("categoria")) {
            crit.add(Restrictions.eq("categoria", filters.get("categoria.descripcion")));
        }
    }

}
