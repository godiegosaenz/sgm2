/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnExoneracionLiquidacion;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 *
 * @author Joao Sanga
 */
public class FnExoneracionTabInterLazy extends BaseLazyDataModel<FnExoneracionLiquidacion>{
    public FnExoneracionTabInterLazy(){
        super(FnExoneracionLiquidacion.class, "id", "DESC");
    }
    
    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        
        Criteria exoneracion = crit.createCriteria("exoneracion", JoinType.LEFT_OUTER_JOIN);
        Criteria liqOrig = crit.createCriteria("liquidacionOriginal", JoinType.LEFT_OUTER_JOIN);
        Criteria predio = liqOrig.createCriteria("predio", JoinType.LEFT_OUTER_JOIN);
        
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        
        if (filters.containsKey("exoneracion.numResolucionSac")) {
            exoneracion.add(Restrictions.ilike("numResolucionSac", "%" + filters.get("exoneracion.numResolucionSac").toString() +"%"));
        }
        
        if (filters.containsKey("liquidacionOriginal.predio.numPredio")) {
            predio.add(Restrictions.eq("numPredio", new BigInteger(filters.get("liquidacionOriginal.predio.numPredio").toString())));
        }
        
        if (filters.containsKey("liquidacionOriginal.anio")) {
            liqOrig.add(Restrictions.eq("anio", new Integer(filters.get("liquidacionOriginal.anio").toString())));
        }
        
        if (filters.containsKey("liquidacionOriginal.nombreComprador")) {
            liqOrig.add(Restrictions.ilike("nombreComprador", "%" + filters.get("liquidacionOriginal.nombreComprador").toString().trim() + "%"));
        }
        crit.add(Restrictions.eq("estado", Boolean.TRUE));
    }
}
