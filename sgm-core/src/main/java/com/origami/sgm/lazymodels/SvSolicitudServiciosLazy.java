/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.SvSolicitudServicios;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Angel Navarro
 */
public class SvSolicitudServiciosLazy extends BaseLazyDataModel<SvSolicitudServicios> {

    private Criteria cth;

    public SvSolicitudServiciosLazy() {
        super(SvSolicitudServicios.class, "tramite.id", "DESC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        cth = crit.createCriteria("tramite");
        if (filters.containsKey("tramite.idTramite")) {
            cth.add(Restrictions.eq("idTramite", new Long(filters.get("tramite.idTramite").toString().trim())));
        }

        if (filters.containsKey("tramite.id")) {
            cth.add(Restrictions.eq("id", new Long(filters.get("tramite.id").toString().trim())));
        }

        if (filters.containsKey("descripcionInconveniente")) {
            crit.add(Restrictions.ilike("descripcionInconveniente", "%" + ((String) filters.get("descripcionInconveniente")).trim() + "%"));
        }
        cth.add(Restrictions.ne("estado", "inactivo"));
    }

    @Override
    public void criteriaSortSetup(Criteria crit, String field, SortOrder order) {
        Criteria c;
        try {
            if (field == null) {
                int index = this.getDefaultSorted().indexOf(".");
                if (index > -1) {
                    String entity2 = this.getDefaultSorted().substring(0, index);
                    String field2 = this.getDefaultSorted().substring((index + 1), this.getDefaultSorted().length());
//                    c = crit.setFetchMode(entity2, FetchMode.JOIN);
                    if (cth != null) {
                        if (getDefaultSortOrder().equalsIgnoreCase("ASC")) {
                            cth.addOrder(Order.asc(field2));
                        } else {
                            cth.addOrder(Order.desc(field2));
                        }
                    }
                }
            } else {
                if (order.equals(SortOrder.ASCENDING)) {
                    crit.addOrder(Order.asc(field));
                } else {
                    crit.addOrder(Order.desc(field));
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SvSolicitudServiciosLazy.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
