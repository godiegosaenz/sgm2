/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.AclRol;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author origami-idea
 */
public class AclRolesLazy extends BaseLazyDataModel<AclRol> {
    
    public AclRolesLazy() {
        super(AclRol.class, "nombre");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        
        if (filters.containsKey("departamento.nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("departamento.nombre").toString().trim() + "%"));
        }
    }
}
