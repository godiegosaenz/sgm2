/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioPropietario;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Joao Sanga
 */
public class PropietariosLazy extends BaseLazyDataModel<CatPredioPropietario> {

    private Criteria cht;
    private Boolean isDistinct = Boolean.FALSE;

    public PropietariosLazy() {
        super(CatPredioPropietario.class, "id", "ASC");
    }

    public PropietariosLazy(Boolean distinct) {
        super(CatPredioPropietario.class);
        this.isDistinct = distinct;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        cht = crit.createCriteria("ente");
        System.out.println("isDistinct" + isDistinct);
        if (isDistinct) {
            System.out.println("holi");
           crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            //crit.setProjection(Projections.distinct(Projections.property("ente")));
        }
        
        crit.createCriteria("predio").add(Restrictions.eq("estado", "A"));
        crit.add(Restrictions.eq("estado", "A"));

        if (filters.containsKey("ente.ciRuc")) {
            cht.add(Restrictions.ilike("ciRuc", "%" + filters.get("ente.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("ente.nombres")) {
            cht.add(Restrictions.ilike("nombres", "%" + filters.get("ente.nombres").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("ente.apellidos")) {
            cht.add(Restrictions.ilike("apellidos", "%" + filters.get("ente.apellidos").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("ente.razonSocial")) {
            cht.add(Restrictions.ilike("razonSocial", "%" + filters.get("ente.razonSocial").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("ente.nombreComercial")) {
            cht.add(Restrictions.ilike("nombreComercial", "%" + filters.get("ente.nombreComercial").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("ente.nombresCompletos")) {
            Criterion c1 = Restrictions.ilike("nombres", "%" + filters.get("ente.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c2 = Restrictions.ilike("apellidos", "%" + filters.get("ente.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c3 = Restrictions.ilike("razonSocial", "%" + filters.get("ente.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c4 = Restrictions.ilike("nombreComercial", "%" + filters.get("ente.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            crit.add(Restrictions.or(c1, c2, c3, c4));
        }
        if (filters.containsKey("predio.numPredio")) {
            crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(filters.get("predio.numPredio").toString())));
        }
    }

    @Override
    public void criteriaSortSetup(Criteria crit, String field, SortOrder order) {
        try {
            if (field == null) {
                crit.addOrder(("ASC".equalsIgnoreCase("ASC")) ? Order.asc("id") : Order.desc("id"));
//                if (this.defaultSortOrder2 != null && this.defaultSortOrder2 != null) {
//                    crit.addOrder((defaultSortOrder2.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted2) : Order.desc(defaultSorted2));
//                }
//                if (this.defaultSortOrder3 != null && this.defaultSortOrder3 != null) {
//                    crit.addOrder((defaultSortOrder3.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted3) : Order.desc(defaultSorted3));
//                }
//                if (this.defaultSortOrder4 != null && this.defaultSortOrder4 != null) {
//                    crit.addOrder((defaultSortOrder4.equalsIgnoreCase("ASC")) ? Order.asc(defaultSorted4) : Order.desc(defaultSorted4));
//                }
            } else {
                if (field.equals("ente.apellidos")) {
                    if (order.equals(SortOrder.ASCENDING)) {
                        cht.addOrder(Order.asc("apellidos"));
                    } else {
                        cht.addOrder(Order.desc("apellidos"));
                    }
                }
                if (field.equals("ente.ciRuc")) {
                    if (order.equals(SortOrder.ASCENDING)) {
                        cht.addOrder(Order.asc("ciRuc"));
                    } else {
                        cht.addOrder(Order.desc("ciRuc"));
                    }
                }
                if (field.equals("ente.nombres")) {
                    if (order.equals(SortOrder.ASCENDING)) {
                        cht.addOrder(Order.asc("nombres"));
                    } else {
                        cht.addOrder(Order.desc("nombres"));
                    }
                }
                if (field.equals("ente.razonSocial")) {
                    if (order.equals(SortOrder.ASCENDING)) {
                        cht.addOrder(Order.asc("razonSocial"));
                    } else {
                        cht.addOrder(Order.desc("razonSocial"));
                    }
                }

            }
        } catch (Exception e) {
            Logger.getLogger(BaseLazyDataModel.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Boolean getIsDistinct() {
        return isDistinct;
    }

    public void setIsDistinct(Boolean isDistinct) {
        this.isDistinct = isDistinct;
    }

}
