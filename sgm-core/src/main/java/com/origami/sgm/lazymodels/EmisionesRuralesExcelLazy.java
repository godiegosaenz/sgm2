/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.EmisionesRuralesExcel;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Henry Pilco
 */
public class EmisionesRuralesExcelLazy extends BaseLazyDataModel<EmisionesRuralesExcel>{

    
    
    public EmisionesRuralesExcelLazy() {
        super(EmisionesRuralesExcel.class);
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("codigoCatastral")) {
            crit.add(Restrictions.ilike("codigoCatastral", "%" + filters.get("codigoCatastral").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("apellidos")) {
            crit.add(Restrictions.ilike("apellidos", "%" + filters.get("apellidos").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("ciRuc")) {
            crit.add(Restrictions.ilike("ciRuc", "%" + filters.get("ciRuc").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("sector")) {
            crit.add(Restrictions.ilike("sector", "%" + filters.get("sector").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("parroquia")) {
            crit.add(Restrictions.ilike("parroquia", "%" + filters.get("parroquia").toString().trim().replaceAll(" ", "%") + "%"));
        }
    }
    
}
