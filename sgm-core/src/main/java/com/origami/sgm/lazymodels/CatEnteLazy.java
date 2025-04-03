/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatEnte;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Anyelo
 */
public class CatEnteLazy extends BaseLazyDataModel<CatEnte> {

    private Boolean esPersona;
    private String estado;

    public CatEnteLazy() {
        super(CatEnte.class, "id", "DESC");
    }

    public CatEnteLazy(Boolean esPersona) {
        super(CatEnte.class, "id", "DESC");
        this.esPersona = esPersona;
    }

    public CatEnteLazy(Boolean esPersona, String estado) {
        super(CatEnte.class, "id", "DESC");
        this.estado = estado;
        this.esPersona = esPersona;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        if (filters.containsKey("ciRuc")) {
            crit.add(Restrictions.ilike("ciRuc", "%" + filters.get("ciRuc").toString().trim() + "%"));
        }
        if (filters.containsKey("nombres")) {
            crit.add(Restrictions.ilike("nombres", "%" + filters.get("nombres").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("apellidos")) {
            crit.add(Restrictions.ilike("apellidos", "%" + filters.get("apellidos").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("razonSocial")) {
            crit.add(Restrictions.ilike("razonSocial", "%" + filters.get("razonSocial").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("nombreComercial")) {
            crit.add(Restrictions.ilike("nombreComercial", "%" + filters.get("nombreComercial").toString().trim().replaceAll(" ", "%") + "%"));
        }
        if (filters.containsKey("nombresCompletos")) {
            Criterion c1 = Restrictions.ilike("nombres", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c2 = Restrictions.ilike("apellidos", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c3 = Restrictions.ilike("razonSocial", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            Criterion c4 = Restrictions.ilike("nombreComercial", "%" + filters.get("nombresCompletos").toString().trim().replaceAll(" ", "%") + "%");
            crit.add(Restrictions.or(c1, c2, c3, c4));
        }
        if (esPersona != null) {
            crit.add(Restrictions.eq("esPersona", esPersona));
        }
        //if(estado != null){
            crit.add(Restrictions.eq("estado", "A"));
        //}
        if (filters.containsKey("excepcionales")) {
            crit.add(Restrictions.eq("excepcionales", Boolean.parseBoolean(filters.get("excepcionales").toString())));
        }
    }
}
