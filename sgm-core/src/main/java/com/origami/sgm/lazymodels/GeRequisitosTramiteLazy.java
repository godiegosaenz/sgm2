/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.GeRequisitosTramite;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class GeRequisitosTramiteLazy extends BaseLazyDataModel<GeRequisitosTramite> {

    public GeRequisitosTramiteLazy() {
        super(GeRequisitosTramite.class);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        
        //GeTipoConsultas tipoConsultaId
        if(filters.containsKey("tipoConsultaId.consultaPor")) {
            crit.createCriteria("tipoConsultaId").add(Restrictions.ilike("consultaPor", "%"+ filters.get("tipoConsultaId.consultaPor").toString().trim() +"%" ));
        }


    }

}
