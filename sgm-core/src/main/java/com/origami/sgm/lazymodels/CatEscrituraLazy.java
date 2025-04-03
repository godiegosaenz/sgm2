/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatEscritura;
import com.origami.sgm.entities.CatPredio;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author arome
 */
public class CatEscrituraLazy extends BaseLazyDataModel<CatEscritura> {

    private CatPredio predio;

    public CatEscrituraLazy(CatPredio predio) {
        super(CatEscritura.class);
        this.predio = predio;
    }


    public CatEscrituraLazy(){
        //super(CatEscritura.class, "fecInscripcion", "NOT NULL");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (predio != null) {
            crit.add(Restrictions.eq("predio", predio));
        }
        crit.add(Restrictions.isNotNull("fecInscripcion"));
    }
}
