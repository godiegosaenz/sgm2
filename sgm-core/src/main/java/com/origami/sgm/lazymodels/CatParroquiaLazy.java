/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatParroquia;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class CatParroquiaLazy extends BaseLazyDataModel<CatParroquia> {

    public CatParroquiaLazy() {
        super(CatParroquia.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        
        if(filters.containsKey("idCanton.nombre")) {
            crit.createCriteria("idCanton").add(Restrictions.ilike("nombre", "%"+ filters.get("idCanton.nombre").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("idCanton.idProvincia.descripcion")) {
            crit.createCriteria("idCanton").createCriteria("idProvincia").add(Restrictions.ilike("descripcion", "%"+ filters.get("idCanton.idProvincia.descripcion").toString().trim() +"%" ));
        }
   
    }

}
