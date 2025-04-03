/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.MatFormulaTramite;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class MatFormulaTramiteLazy extends BaseLazyDataModel<MatFormulaTramite> {

    public MatFormulaTramiteLazy() {
        super(MatFormulaTramite.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        
        if (filters.containsKey("nVersion")) {
            crit.add(Restrictions.eq("nVersion", new Integer(filters.get("nVersion").toString().trim())));
        }
        
        /*
        if (filters.containsKey("tipoTramite.descripcion")) {
            crit.createCriteria("tipoTramite").add(Restrictions.eq("descripcion", "%" + filters.get("tipoTramite.descripcion").toString().trim() + "%"));
        }
                */
        
        if(filters.containsKey("tipoTramite.descripcion")) {
            crit.createCriteria("tipoTramite").add(Restrictions.ilike("descripcion", "%"+ filters.get("tipoTramite.descripcion").toString().trim() +"%" ));
        }


    }

}
