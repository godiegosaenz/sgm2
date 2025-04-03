/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnSolicitudExoneracion;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
public class FnSolicitudExoneracionLazy extends BaseLazyDataModel<FnSolicitudExoneracion> {

    private Criteria cts;
    private Criteria ctp;
    private Criteria ctpr;

    public FnSolicitudExoneracionLazy() {
        super(FnSolicitudExoneracion.class, "fechaIngreso", "DESC");
    }

    public FnSolicitudExoneracionLazy(String defaultSorted) {
        super(FnSolicitudExoneracion.class, defaultSorted);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {

        Criteria solicitante = crit.createCriteria("solicitante", JoinType.LEFT_OUTER_JOIN);
        Criteria tramite = crit.createCriteria("tramite", JoinType.LEFT_OUTER_JOIN);

        if (filters.containsKey("id")) {
            if (Utils.validateNumberPattern(filters.get("id").toString())) {
                crit.add(Restrictions.eq("id", Long.valueOf(filters.get("id").toString())));
            }
        }
        if (filters.containsKey("anioInicio")) {
            if (Utils.validateNumberPattern(filters.get("anioInicio").toString())) {
                crit.add(Restrictions.eq("anioInicio", Integer.valueOf(filters.get("anioInicio").toString())));
            }
        }
        if (filters.containsKey("tramite.id")) {
            if (Utils.validateNumberPattern(filters.get("tramite.id").toString())) {
                tramite.add(Restrictions.eq("id", Long.valueOf(filters.get("tramite.id").toString())));
            }
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
