/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatNormasConstruccion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author supergold
 */
public class CatNormasConstruccionLazy extends BaseLazyDataModel<CatNormasConstruccion> {

    private String estado;
    
    public CatNormasConstruccionLazy(String estado) {
        super(CatNormasConstruccion.class, "id", "DESC");
        this.estado = estado;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("idCiudadela.nombre")) {
            crit.createCriteria("idCiudadela").add(Restrictions.ilike("nombre", "%" + filters.get("idCiudadela.nombre").toString().trim() + "%"));
        }
        if (filters.containsKey("tipoNorma.tipo")) {
            crit.createCriteria("tipoNorma").add(Restrictions.ilike("tipo", "%" + filters.get("tipoNorma.tipo").toString().trim() + "%"));
        }
        if (filters.containsKey("cubierta")) {
            crit.add(Restrictions.ilike("cubierta", "%" + filters.get("cubierta").toString().trim() + "%"));
        }
        if (filters.containsKey("estructura")) {
            crit.add(Restrictions.ilike("estructura", "%" + filters.get("estructura").toString().trim() + "%"));
        }
        if(estado != null){
            crit.add(Restrictions.eq("estado", estado));
        }
    }
}
