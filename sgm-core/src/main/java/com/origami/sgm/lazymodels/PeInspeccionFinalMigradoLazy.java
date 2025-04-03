/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.PeInspeccionFinal;
import java.math.BigInteger;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Joao Sanga
 */
public class PeInspeccionFinalMigradoLazy extends BaseLazyDataModel<PeInspeccionFinal> {

    public PeInspeccionFinalMigradoLazy() {
        super(PeInspeccionFinal.class, "id", "DESC");
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        Criteria ct = crit.createCriteria("tramite");
        if (filters.containsKey("id")) {
            crit.add(Restrictions.eq("id", new Long(filters.get("id").toString().trim())));
        }
        
        if (filters.containsKey("anioInspeccion")) {
            crit.add(Restrictions.eq("anioInspeccion", new BigInteger(filters.get("anioInspeccion").toString().trim())));
        }

        if (filters.containsKey("numReporte")) {
            crit.add(Restrictions.eq("numReporte", new BigInteger(filters.get("numReporte").toString().trim())));
        }
        ct.add(Restrictions.eq("observacion", "Trámite migrado"));

        if(filters.containsKey("tramite.idTramite")){
            ct.add(Restrictions.eq("id", new Long(filters.get("tramite.idTramite").toString().trim())));
        }

        if (filters.containsKey("tramite.id")) {
            ct.add(Restrictions.eq("id", new Long(filters.get("tramite.id").toString().trim())));
        }

        if (filters.containsKey("tramite.estado")) {
            ct.add(Restrictions.ilike("estado", "%" + filters.get("tramite.estado").toString().trim() + "%"));
        }
        //crit.add(Restrictions.le("estado", "A"));
    }
    
}
