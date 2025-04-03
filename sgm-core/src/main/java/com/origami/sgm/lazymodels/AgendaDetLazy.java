/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.AgendaDet;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author CarlosLoorVargas
 */
public class AgendaDetLazy extends BaseLazyDataModel<AgendaDet> {

    private Criteria tipo, invol, agenda,tramite;
    private Long involucrado;

    public AgendaDetLazy() {
        super(AgendaDet.class, "id");
    }

    public AgendaDetLazy(Long involucrado) {
        super(AgendaDet.class, "id");
        this.involucrado = involucrado;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        invol = crit.createCriteria("involucrado");
        agenda = crit.createCriteria("agenda");
        tramite = crit.createCriteria("agenda.tramite");
        
        /*if (filters.containsKey("agenda.tipo.descripcion")) {
            agenda.add(Restrictions.ilike("tipo.descripcion", "%" + filters.get("tipo.descripcion").toString().trim() + "%"));
        }*/
        if (filters.containsKey("agenda.descripcion")) {
            agenda.add(Restrictions.ilike("descripcion", "%" + filters.get("agenda.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("involucrado.ciRuc")) {
            invol.add(Restrictions.ilike("ciRuc", "%" + filters.get("involucrado.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("involucrado.nombres")) {
            invol.add(Restrictions.ilike("nombres", "%" + filters.get("involucrado.nombres").toString().trim() + "%"));
        }
        if (filters.containsKey("involucrado.apellidos")) {
            invol.add(Restrictions.ilike("apellidos", "%" + filters.get("involucrado.apellidos").toString().trim() + "%"));
        }
        if(filters.containsKey("agenda.tramite.nombrePropietario")) {
            tramite.add(Restrictions.ilike("nombrePropietario", "%" + filters.get("tramite.nombrePropietario").toString().trim() + "%"));
        }
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("detalle")) {
            crit.add(Restrictions.ilike("detalle", "%" + filters.get("detalle").toString().trim() + "%"));
        }
        if (involucrado != null) {
            invol.add(Restrictions.eq("id", involucrado));
        }
    }

}
