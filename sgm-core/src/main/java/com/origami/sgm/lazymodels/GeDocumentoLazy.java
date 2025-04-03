/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.GeDocumentos;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Mariuly
 */
public class GeDocumentoLazy extends BaseLazyDataModel<GeDocumentos> {

    private Long raiz;
    
    public GeDocumentoLazy() {
        super(GeDocumentos.class, "nombre", "ASC");
    }
    
    public GeDocumentoLazy(Long id) {
        super(GeDocumentos.class, "fechaCreacion", "DESC");
        this.raiz = id;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.eq("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        if (filters.containsKey("identificacion")) {
            crit.add(Restrictions.eq("identificacion", "%" + filters.get("identificacion").toString().trim() + "%"));
        }
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.eq("descripcion", "%" + filters.get("contentType").toString().trim() + "%"));
        }
        if (raiz != null) {
            crit.add(Restrictions.eq("raiz", BigInteger.valueOf(raiz)));
        }
    }

}
