/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.FnExoneracionClase;
import com.origami.sgm.entities.RenPermisosFuncionamientoLocalComercial;
import com.origami.sgm.entities.RenTasaTurismo;
import com.origami.sgm.entities.RenTurismoServicios;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import util.Utils;

/**
 *
 * @author Angel Navarro
 * @Date 17/05/2016
 */
public class RenTurismoServiciosLazy extends BaseLazyDataModel<RenTurismoServicios> {

    public RenTurismoServiciosLazy() {
        super(RenTurismoServicios.class);
    }

    public RenTurismoServiciosLazy(String defaultSorted) {
        super(RenTurismoServicios.class, defaultSorted);
    }

    public RenTurismoServiciosLazy(String descripcion, String asc) {
        super(RenTurismoServicios.class, descripcion, asc);
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
         if (filters.containsKey("id")) {
            if (Utils.validateNumberPattern(filters.get("id").toString())) {
                crit.add(Restrictions.eq("id", Long.valueOf(filters.get("id").toString())));
            }
        }
        if (filters.containsKey("descripcion")) {
            crit.add(Restrictions.ilike("descripcion", "%".concat(filters.get("descripcion").toString()).concat("%")));
        }
        if (filters.containsKey("estado")) {
            crit.add(Restrictions.eq("estado", Boolean.valueOf(filters.get("estado").toString())));
        }
        if (filters.containsKey("tipo")) {
            if (Utils.validateNumberPattern(filters.get("tipo").toString()))
                crit.add(Restrictions.eq("tipo", new BigInteger(filters.get("tipo").toString())));
        }
    }
}
