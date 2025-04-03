/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.AclUser;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author origami-idea
 */
public class AclUsersLazy extends BaseLazyDataModel<AclUser> {

    public AclUsersLazy() {
        super(AclUser.class, "usuario");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        Criteria c = crit.createCriteria("ente");
        if (filters.containsKey("usuario")) {
            crit.add(Restrictions.ilike("usuario", "%" + filters.get("usuario").toString().trim() + "%"));
        }
        if (filters.containsKey("ente.nombres")) {
            c.add(Restrictions.ilike("nombres", "%" + filters.get("ente.nombres").toString().trim() + "%"));
        }
        if (filters.containsKey("ente.apellidos")) {
            c.add(Restrictions.ilike("apellidos", "%" + filters.get("ente.apellidos").toString().trim() + "%"));
        }
    }

}
