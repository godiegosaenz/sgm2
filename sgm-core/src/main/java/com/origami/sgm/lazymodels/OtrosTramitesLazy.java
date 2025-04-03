/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.OtrosTramites;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author CarlosLoorVargas
 */
public class OtrosTramitesLazy extends BaseLazyDataModel<OtrosTramites> {

    public OtrosTramitesLazy() {
        super(OtrosTramites.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            if (filters.containsKey("tipoTramite")) {
                crit.add(Restrictions.ilike("tipoTramite", "%" + new String(((String) filters.get("tipoTramite")).trim()) + "%"));
            }
        } catch (Exception e) {
            Logger.getLogger(OtrosTramitesLazy.class.getName()).log(Level.SEVERE, null, e.getMessage());
        }
    }

}
