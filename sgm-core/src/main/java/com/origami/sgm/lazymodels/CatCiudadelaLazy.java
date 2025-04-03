/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatCiudadela;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class CatCiudadelaLazy extends BaseLazyDataModel<CatCiudadela> {

    public CatCiudadelaLazy() {
        super(CatCiudadela.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }

        if(filters.containsKey("codTipoConjunto.nombre")) {
            crit.createCriteria("codTipoConjunto").add(Restrictions.ilike("nombre", "%"+ filters.get("codTipoConjunto.nombre").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("codParroquia.descripcion")) {
            crit.createCriteria("codParroquia").add(Restrictions.ilike("descripcion", "%"+ filters.get("codParroquia.descripcion").toString().trim() +"%" ));
        }

   
    }

}
