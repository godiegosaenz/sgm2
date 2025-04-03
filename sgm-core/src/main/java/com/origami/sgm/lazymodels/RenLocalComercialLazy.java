/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.RenLocalComercial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Joao Sanga
 */
public class RenLocalComercialLazy extends BaseLazyDataModel<RenLocalComercial> {

    private Boolean estado;
    private Criteria cht;

    public RenLocalComercialLazy() {
        super(RenLocalComercial.class, "id");
        //this.estado = true;
    }

    public RenLocalComercialLazy(Boolean estado) {
        super(RenLocalComercial.class, "id");
        this.estado = estado;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        //cht = crit.createCriteria("propietario");

        if (filters.containsKey("nombreLocal")) {
            crit.add(Restrictions.ilike("nombreLocal", "%" + filters.get("nombreLocal").toString().trim() + "%"));
        }
        if (filters.containsKey("numLocal")) {
            crit.add(Restrictions.ilike("numLocal", "%" + filters.get("numLocal").toString().trim() + "%"));
        }
        if (filters.containsKey("area")) {
            if (Utils.validateNumberPattern(filters.get("area").toString().trim())) {
                crit.add(Restrictions.eq("area", new BigDecimal(filters.get("area").toString().trim())));
            }
        }
        if (filters.containsKey("razonSocial.razonSocial")) {
            crit.createCriteria("razonSocial").add(Restrictions.ilike("razonSocial", "%" + filters.get("razonSocial.razonSocial").toString().trim() + "%"));
        }
//        if (filters.containsKey("propietario.nombresCompletos")) {
//            Criterion c1 = Restrictions.ilike("nombres", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
//            Criterion c2 = Restrictions.ilike("apellidos", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
//            Criterion c3 = Restrictions.ilike("razonSocial", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
//            Criterion c4 = Restrictions.ilike("nombreComercial", "%" + filters.get("propietario.nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
//            //cht.add(Restrictions.or(c1, c2, c3, c4));
//        }
        if (filters.containsKey("razonSocial.ciRuc")) {
            crit.createCriteria("razonSocial").add(Restrictions.ilike("ciRuc", "%" + filters.get("razonSocial.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("propietario.ciRuc")) {
            crit.createCriteria("propietario").add(Restrictions.ilike("ciRuc", "%" + filters.get("propietario.ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("ubicacion")) {
            crit.add(Restrictions.eq("ubicacion", filters.get("ubicacion")));
        }
        if (filters.containsKey("ubicacion.descripcion")) {
            crit.createCriteria("ubicacion").add(Restrictions.ilike("descripcion", "%" + filters.get("ubicacion.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("categoria.descripcion")) {
            crit.createCriteria("categoria").add(Restrictions.ilike("descripcion", "%" + filters.get("categoria.descripcion").toString().trim() + "%"));
        }
        if (filters.containsKey("categoria")) {
            crit.add(Restrictions.eq("categoria", filters.get("categoria")));
        }
        if (filters.containsKey("turismo")) {
            crit.add(Restrictions.eq("turismo", Boolean.valueOf(filters.get("turismo").toString())));
        }
        if (filters.containsKey("estadoLocalComercial")) {
            crit.add(Restrictions.eq("estadoLocalComercial", new BigInteger(filters.get("estadoLocalComercial").toString())));
        }
//        crit.add(Restrictions.and(predicates));

        if (estado != null) {
            if (estado) {
                crit.add(Restrictions.eq("estado", estado));
            }
        }

    }

}
