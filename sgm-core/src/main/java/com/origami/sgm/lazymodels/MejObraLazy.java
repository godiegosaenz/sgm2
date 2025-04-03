/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.MejObra;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author origami
 */
public class MejObraLazy extends BaseLazyDataModel<MejObra>{

    public MejObraLazy() {
        super(MejObra.class, "id", "DESC");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("anio")) {
            crit.add(Restrictions.eq("anio", new Long(filters.get("anio").toString().trim())));
        }
        if (filters.containsKey("tipoObra.descripcion")) {
            Criteria tipo = crit.createCriteria("tipoObra");
            tipo.add(Restrictions.ilike("descripcion", "%" + filters.get("tipoObra.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("ubicacion.nombre")) {
            Criteria parroquia = crit.createCriteria("ubicacion");
            parroquia.add(Restrictions.like("nombre", filters.get("ubicacion.nombre").toString().trim()));
        }
    }
    
}
