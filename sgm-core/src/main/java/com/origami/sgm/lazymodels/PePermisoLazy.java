/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.PePermiso;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.primefaces.model.SortOrder;

/**
 *
 * Lazy de PePermiso
 *
 * @author Angel Navarro
 */
public class PePermisoLazy extends BaseLazyDataModel<PePermiso> {

    private Criteria ctp;
    private Criteria cht;
    private Criteria cpr;
    private BigInteger idPredio;

    public PePermisoLazy() {
        super(PePermiso.class, "id", "DESC");
    }

    public PePermisoLazy(BigInteger idPredio) {
        super(PePermiso.class, "id", "DESC");
        this.idPredio = idPredio;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        ctp = crit.createCriteria("tipoPermiso");

        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("tipoPermiso.descripcion")) {
            ctp.add(Restrictions.ilike("descripcion", "%" + filters.get("tipoPermiso.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("propietario")) {
            crit.add(Restrictions.ilike("propietario", "%" + filters.get("propietario").toString().trim() + "%"));
        }
        if (filters.containsKey("anioPermiso")) {
            crit.add(Restrictions.eq("anioPermiso", new Short(filters.get("anioPermiso").toString().trim())));
        }

        if (filters.containsKey("numReporte")) {
            crit.add(Restrictions.eq("numReporte", new BigInteger(filters.get("numReporte").toString().trim())));
        }
        if (filters.containsKey("tramite.id")) {
            cht = crit.createCriteria("tramite", JoinType.LEFT_OUTER_JOIN);
            cht.add(Restrictions.eq("id", new Long(filters.get("tramite.id").toString().trim())));
        }
        if (filters.containsKey("codigoPermisoAnterior")) {
            crit.add(Restrictions.eq("codigoPermisoAnterior", new BigInteger(filters.get("codigoPermisoAnterior").toString().trim())));
        }
        if (filters.containsKey("idPredio.numPredio")) {
            cpr = crit.createCriteria("idPredio", JoinType.LEFT_OUTER_JOIN);
            cpr.add(Restrictions.eq("numPredio", new BigInteger(filters.get("idPredio.numPredio").toString().trim())));
        }
        if (idPredio != null) {
            cpr = crit.createCriteria("idPredio", JoinType.LEFT_OUTER_JOIN);
            cpr.add(Restrictions.eq("id", idPredio.longValue()));
        }
        crit.add(Restrictions.le("estado", "A"));
    }

    @Override
    public void criteriaSortSetup(Criteria crit, String field, SortOrder order) {
        Criteria c = null;
        int index;
        String entity2 = null;
        String entry;
        String field2 = null;
        if (field == null) {
            index = this.getDefaultSorted().indexOf(".");
            entry = this.getDefaultSorted();
        } else {
            index = field.indexOf(".");
            entry = field;
        }
        if (index > -1) {
            entity2 = entry.substring(0, index);
            field2 = entry.substring((index + 1), entry.length());
            if (entity2.equalsIgnoreCase("tipoPermiso")) {
                c = ctp;
            } else if (entity2.equalsIgnoreCase("tramite")) {
                if (cht != null) {
                    c = cht;
                }
            }
            if (field != null) {
                field = field2;
            }
        } else {
            if (field == null) {
                field2 = this.getDefaultSorted();
            }
            c = crit;
        }
        try {
            if (field == null) {
                if (getDefaultSortOrder().equalsIgnoreCase("ASC")) {
                    c.addOrder(Order.asc(field2));
                } else {
                    c.addOrder(Order.desc(field2));
                }
            } else {
                if (order.equals(SortOrder.ASCENDING)) {
                    c.addOrder(Order.asc(field));
                } else {
                    c.addOrder(Order.desc(field));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SvSolicitudServiciosLazy.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
