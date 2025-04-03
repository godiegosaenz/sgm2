/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegistroProfesionalTecnico;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class RegistroProfesionalTecnicoLazy extends BaseLazyDataModel<RegistroProfesionalTecnico> {

    public RegistroProfesionalTecnicoLazy() {
        super(RegistroProfesionalTecnico.class);
    }


    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("codigoTecnico")) {
            crit.add(Restrictions.ilike("codigoTecnico", "%" + filters.get("codigoTecnico").toString().trim() + "%"));
        }
        
        if(filters.containsKey("ente.ciRuc")) {
            crit.createCriteria("ente").add(Restrictions.ilike("ciRuc", "%"+ filters.get("ente.ciRuc").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("ente.nombres")) {
            crit.createCriteria("ente").add(Restrictions.ilike("nombres", "%"+ filters.get("ente.nombres").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("ente.apellidos")) {
            crit.createCriteria("ente").add(Restrictions.ilike("apellidos", "%"+ filters.get("ente.apellidos").toString().trim() +"%" ));
        }
   
    }

}
