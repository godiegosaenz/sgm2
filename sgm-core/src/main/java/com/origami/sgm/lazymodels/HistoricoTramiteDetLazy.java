/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.HistoricoTramiteDet;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.SortOrder;

/**
 *
 * Lazy de PePermiso
 *
 * @author Angel Navarro
 */
public class HistoricoTramiteDetLazy extends BaseLazyDataModel<HistoricoTramiteDet> {

    private Criteria ctt;
    private Criteria cht;
    private Criteria cothp;
    private Long idTipoTramite;

    public HistoricoTramiteDetLazy() {
        super(HistoricoTramiteDet.class, "tramite.id", "DESC");
    }
    
    public HistoricoTramiteDetLazy(Long tipoTramite) {
        super(HistoricoTramiteDet.class, "tramite.id", "DESC");
        this.idTipoTramite = tipoTramite;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        cht = crit.createCriteria("tramite");
        ctt = cht.createCriteria("tipoTramite");
        
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("fecCre")) {
            crit.add(Restrictions.ilike("fecCre", "%" + filters.get("fecCre").toString().trim() + "%"));
        }
        if (filters.containsKey("tramite.tipoTramite.descripcion")) {
            ctt.add(Restrictions.ilike("descripcion", "%" + filters.get("descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("tramite.id")) {
            cht.add(Restrictions.eq("id", new Long(filters.get("tramite.id").toString().trim())));
        }
        if(idTipoTramite == 14L){
            ArrayList ids = new ArrayList();
            ids.add(14L);
            ids.add(43L);
            ids.add(44L);
            cothp = cht.createCriteria("otrosTramitesHasPermiso");
            cothp.add(Restrictions.isNotNull("id"));            
            ctt.add(Restrictions.in("id", ids));
        }else{
            ctt.add(Restrictions.eq("id", idTipoTramite));
        }
        cht.add(Restrictions.isNotNull("id"));
        crit.add(Restrictions.eq("estado", true));
        
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
            if (entity2.equalsIgnoreCase("tramite")) {
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
