/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RegEnteInterviniente;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Angel Navarro
 */
public class RegEnteIntervinienteLazy extends BaseLazyDataModel<RegEnteInterviniente> {

    private String cadena;
    private String consulta;
    private int tipoConsulta = 0;

    public RegEnteIntervinienteLazy() {
        super(RegEnteInterviniente.class);
    }

    public RegEnteIntervinienteLazy(String cadena) {
        super(RegEnteInterviniente.class);
        this.cadena = cadena;
    }

    public RegEnteIntervinienteLazy(String consulta, int tipoConsulta) {
        super(RegEnteInterviniente.class);
        this.consulta = consulta;
        this.tipoConsulta = tipoConsulta;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        if (filters.containsKey("cedRuc")) {
            crit.add(Restrictions.ilike("cedRuc", "%" + filters.get("cedRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("nombre")) {
            crit.add(Restrictions.ilike("nombre", "%" + filters.get("nombre").toString().trim() + "%"));
        }
        if (tipoConsulta == 1) {
            crit.add(Restrictions.ilike("nombre", "%" + consulta.trim() + "%"));
        } else if (tipoConsulta == 2) {
            crit.add(Restrictions.ilike("cedRuc", "%" + consulta.trim() + "%"));
        }

        if (cadena != null) {
            crit.add(Restrictions.or(Restrictions.ilike("cedRuc", "%" + cadena.trim() + "%"), Restrictions.ilike("nombre", "%" + cadena.trim() + "%")));
        }

    }

}
