/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.lazymodels;

import com.origami.sgm.entities.CatPredioPropietario;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author CarlosLoorVargas
 */
public class CatPredioPropietarioLazy extends BaseLazyDataModel<CatPredioPropietario> {

    private Boolean estado = false;
    private static final Logger log = Logger.getLogger(CatPredioPropietarioLazy.class.getName());

    public CatPredioPropietarioLazy(Boolean estado) {
        super(CatPredioPropietario.class);
        this.estado = estado;
    }

    @Override
    public void criteriaFilterSetup(Criteria crit, Map<String, Object> filters) throws Exception {
        try {
            if (filters.containsKey("predio.numPredio")) {
                if (NumberUtils.isNumber(filters.get("predio.numPredio").toString())) {
                    crit.createCriteria("predio").add(Restrictions.eq("numPredio", new BigInteger(filters.get("numPredio").toString().trim())));
                }
            }
            if (filters.containsKey("ente.ciRuc")) {
                crit.createCriteria("ente").add(Restrictions.ilike("ciRuc", "%" + filters.get("ente.ciRuc").toString().trim() + "%"));
            }
            if (filters.containsKey("ente.nombres")) {
                crit.createCriteria("ente").add(Restrictions.ilike("nombres", "%" + filters.get("ente.nombres").toString().trim() + "%"));
            }
            if (filters.containsKey("ente.apellidos")) {
                crit.createCriteria("ente").add(Restrictions.ilike("apellidos", "%" + filters.get("ente.apellidos").toString().trim() + "%"));
            }
            if (filters.containsKey("ente.razonSocial")) {
                crit.createCriteria("ente").add(Restrictions.ilike("razonSocial", "%" + filters.get("ente.razonSocial").toString().trim() + "%"));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
        }
    }

}
