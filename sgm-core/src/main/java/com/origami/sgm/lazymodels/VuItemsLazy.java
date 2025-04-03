/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.VuCatalogo;
import com.origami.sgm.entities.VuItems;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author origami
 */
public class VuItemsLazy extends BaseLazyDataModel<VuItems>{
    private VuCatalogo catalogo;
    
    public VuItemsLazy() {
        super(VuItems.class,"nombre","ASC");
    }

    public VuItemsLazy(VuCatalogo catalogo) {
        super(VuItems.class,"nombre","ASC");
        this.catalogo = catalogo;
    }
    
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        
        if(catalogo!=null){
            crit.add(Restrictions.eq("catalogo", catalogo));
        }
        
    }
}
