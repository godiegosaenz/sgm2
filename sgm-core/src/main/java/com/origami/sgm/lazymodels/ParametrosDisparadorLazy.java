/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.ParametrosDisparador;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class ParametrosDisparadorLazy extends BaseLazyDataModel<ParametrosDisparador> {

    public ParametrosDisparadorLazy() {
        super(ParametrosDisparador.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("varResp")) {
            crit.add(Restrictions.ilike("varResp", "%" + filters.get("varResp").toString().trim() + "%"));
        }
        
        if(filters.containsKey("tipoTramite.descripcion")) {
            crit.createCriteria("tipoTramite").add(Restrictions.ilike("descripcion", "%"+ filters.get("tipoTramite.descripcion").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("responsable.usuario")) {
            crit.createCriteria("responsable").add(Restrictions.ilike("usuario", "%"+ filters.get("responsable.usuario").toString().trim() +"%" ));
        }
        
        /*
        
        tipoTramite.descripcion

        if(filters.containsKey("codTipoConjunto.nombre")) {
            crit.createCriteria("codTipoConjunto").add(Restrictions.ilike("nombre", "%"+ filters.get("codTipoConjunto.nombre").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("codParroquia.descripcion")) {
            crit.createCriteria("codParroquia").add(Restrictions.ilike("descripcion", "%"+ filters.get("codParroquia.descripcion").toString().trim() +"%" ));
        }
                */

   
    }

}
