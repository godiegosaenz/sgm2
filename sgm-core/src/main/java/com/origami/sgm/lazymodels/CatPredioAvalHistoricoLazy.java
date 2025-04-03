/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioAvalHistorico;
import java.util.Date;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author XndySxnchez :v
 */
public class CatPredioAvalHistoricoLazy extends BaseLazyDataModel<CatPredioAvalHistorico> {

    public CatPredioAvalHistoricoLazy() {
        super(CatPredioAvalHistorico.class, "id");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        crit.add(Restrictions.eq("anioInicio", Utils.getAnio(new Date())));
        crit.add(Restrictions.eq("anioFin", Utils.getAnio(new Date())));
    }
    
    
    
    
}
