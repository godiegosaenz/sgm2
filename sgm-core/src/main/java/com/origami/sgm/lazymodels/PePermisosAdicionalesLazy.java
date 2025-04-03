/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.PePermisosAdicionales;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.SortOrder;

/**
 *
 * Lazy de PePermisosAdicionales
 *
 * @author Angel Navarro
 */
public class PePermisosAdicionalesLazy extends BaseLazyDataModel<PePermisosAdicionales> {

    private Criteria ctp;
    private Criteria cht;

    public PePermisosAdicionalesLazy() {
        super(PePermisosAdicionales.class, "numTramite", "DESC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        
        if (filters.containsKey("numReporte")) {
            crit.add(Restrictions.eq("numReporte", new BigInteger(filters.get("numReporte").toString().trim())));
        }
        
        if (filters.containsKey("anioTramite")) {
            crit.add(Restrictions.eq("anioTramite", new BigInteger(filters.get("anioTramite").toString().trim())));
        }

        if (filters.containsKey("numTramite")) {
            crit.add(Restrictions.eq("numTramite", new Long(filters.get("numTramite").toString().trim())));
        }        
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
                c = cht;
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
