/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnSolicitudCondonacion;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 *
 * @author Joao Sanga
 */
public class FnSolicitudCondonacionLazy extends BaseLazyDataModel<FnSolicitudCondonacion> {

    private Criteria ctp;
    private Long tipo;

    public FnSolicitudCondonacionLazy() {
        super(FnSolicitudCondonacion.class, "fechaIngreso", "desc");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        
        Criteria solicitante = crit.createCriteria("solicitante", JoinType.LEFT_OUTER_JOIN);
        Criteria cpr = crit.createCriteria("predio", JoinType.LEFT_OUTER_JOIN);
        
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("estado.id")) {
            crit.add(Restrictions.eq("estado.id", new Long(filters.get("estado.id").toString().trim())));
        }
        if (filters.containsKey("predio.numPredio")) {
            cpr.add(Restrictions.eq("numPredio", BigInteger.valueOf(new Long(filters.get("predio.numPredio").toString().trim()))));
        }
        if (filters.containsKey("solicitante.ciRuc")) {
            solicitante.add(Restrictions.ilike("ciRuc", "%" + filters.get("solicitante.ciRuc").toString() + "%"));
        }
        if (filters.containsKey("solicitante.nombreCompleto")) {
            Criterion apellidos = Restrictions.ilike("apellidos", "%" + filters.get("solicitante.nombreCompleto").toString() + "%");
            Criterion razonSocial = Restrictions.ilike("razonSocial", "%" + filters.get("solicitante.nombreCompleto").toString() + "%");
            LogicalExpression orExp = Restrictions.or(apellidos, razonSocial);
            solicitante.add(orExp);
        }
    }
    
}
