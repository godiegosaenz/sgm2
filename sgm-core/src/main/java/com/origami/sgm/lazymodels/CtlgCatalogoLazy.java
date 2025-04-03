/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CtlgCatalogo;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Mariuly
 */
public class CtlgCatalogoLazy extends BaseLazyDataModel<CtlgCatalogo> {

    public CtlgCatalogoLazy() {
        super(CtlgCatalogo.class, "nombre", "ASC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%"+ filters.get("nombre").toString().trim() +"%"));
        }
        if(filters.containsKey("id")){
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim() )));
        }
    }
}
