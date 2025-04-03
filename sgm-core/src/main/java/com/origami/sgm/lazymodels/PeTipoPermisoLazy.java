/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.PeTipoPermiso;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class PeTipoPermisoLazy extends BaseLazyDataModel<PeTipoPermiso> {

    public PeTipoPermisoLazy() {
        super(PeTipoPermiso.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        
        if (filters.containsKey("codigo")) {
            crit.add(Restrictions.ilike("codigo", "%" + filters.get("codigo").toString().trim() + "%"));
        }
   
    }

}
