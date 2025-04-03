/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenRubrosLiquidacion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Joao Sanga
 */
public class RenRubrosLazy extends BaseLazyDataModel<RenRubrosLiquidacion> {

    private Boolean estado;

    public RenRubrosLazy() {
        super(RenRubrosLiquidacion.class, "id");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        crit.add(Restrictions.isNull("tipoLiquidacion"));
    }
    
}
