/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.GeTipoTramite;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author MauricioGuzmán
 */
public class GeTipoTramiteLazy extends BaseLazyDataModel<GeTipoTramite> {

    public GeTipoTramiteLazy() {
        super(GeTipoTramite.class);
    }


    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        
        if (filters.containsKey("carpeta")) {
            crit.add(Restrictions.ilike("carpeta", "%" + filters.get("carpeta").toString().trim() + "%"));
        }
        
        if(filters.containsKey("rol.nombre")) {
            crit.createCriteria("rol").add(Restrictions.ilike("nombre", "%"+ filters.get("rol.nombre").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("categoria.nombre")) {
            crit.createCriteria("categoria").add(Restrictions.ilike("nombre", "%"+ filters.get("categoria.nombre").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("tipoProceso.descripcion")) {
            crit.createCriteria("tipoProceso").add(Restrictions.ilike("descripcion", "%"+ filters.get("tipoProceso.descripcion").toString().trim() +"%" ));
        }
        
        if(filters.containsKey("disparador.descripcion")) {
            crit.createCriteria("disparador").add(Restrictions.ilike("descripcion", "%"+ filters.get("disparador.descripcion").toString().trim() +"%" ));
        }
   
    }

}
